package com.project.dbbackupsolution.domain.scheduling;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.project.dbbackupsolution.domain.models.SchedulerModel;
import com.project.dbbackupsolution.domain.models.TaskType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.project.dbbackupsolution.domain.services.FileService;
import com.project.dbbackupsolution.domain.services.StorageService;

import javax.annotation.PostConstruct;

@Service
public class SchedulerService {
    private final StorageService storageService;
    private final FileService fileService;
    private final TaskScheduler taskScheduler;
    private final SchedulerManager schedulerManager;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public SchedulerService(StorageService storageService, FileService fileService, TaskScheduler taskScheduler, SchedulerManager schedulerManager) {
        this.storageService = storageService;
        this.fileService = fileService;
        this.taskScheduler = taskScheduler;
        this.schedulerManager = schedulerManager;
    }

    @PostConstruct
    public void init() {
        List<SchedulerModel> tasks = schedulerManager.getSavedSchedulerModels();
        if  (tasks != null && !tasks.isEmpty()) {
            for (SchedulerModel task : tasks) {
                System.out.println("SchedulerService.init: " + task);
                scheduledTask(task);
            }
        }
    }

    private ScheduledFuture<?> scheduledTask(SchedulerModel task) {
        Runnable runnableTask = createRunnableTask(task);
        CronTrigger cronTrigger = new CronTrigger(task.getCronExpression());
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(runnableTask, cronTrigger);
        scheduledTasks.put(String.valueOf(task.getTaskType()), scheduledTask);
        System.out.println("SchedulerService.scheduledTask: " + scheduledTask + " " + task.getTaskType());
        return scheduledTask;
    }

    private Runnable createRunnableTask(SchedulerModel task) {
        return switch (task.getTaskType()) {
            case TaskType.FILE_MOVE -> () -> storageService.moveFile(task.getSourcePath(), task.getDestinationPath());
            case TaskType.FILE_COPY -> () -> storageService.copyFile(task.getSourcePath(), task.getDestinationPath());
            case TaskType.FILES_UPLOAD -> () -> {
                for (String path : task.getSourcePaths()) {
                    for (String extension : task.getFileExtensions()) {
                        fileService.sendFileToStorage(path, extension);
                    }
                }
            };
            case TaskType.DELETE_OLD_FILES -> () -> storageService.deleteOldFiles(task.getNumberOfDays());
            case TaskType.DELETE_ALL_BY_EXTENSION -> () -> storageService.deleteAllFilesByExtension(task.getFileExtension());
            default -> throw new IllegalStateException("Unexpected value: " + task.getTaskType());
        };
    }

    public void rescheduleTask(SchedulerModel task) {
        cancelAndRemoveScheduledTask(String.valueOf(task.getTaskType()));
        scheduledTasks.put(String.valueOf(task.getTaskType()), scheduledTask(task));
    }

    public void updateSchedulesTasks(SchedulerModel task) {
        rescheduleTask(task);
    }

    private void cancelAndRemoveScheduledTask(String taskType) {
        ScheduledFuture<?> oldScheduledTask = scheduledTasks.remove(taskType);
        if (oldScheduledTask != null) {
            oldScheduledTask.cancel(true);
        }
    }
}
