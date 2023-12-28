package com.project.dbbackupsolution.domain.scheduling;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.project.dbbackupsolution.domain.models.SchedulerModel;
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
    private ScheduledFuture<?> scheduledTask;
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
        for (SchedulerModel task : tasks) {
            scheduledTask(task);
        }
    }

    private ScheduledFuture<?> scheduledTask(SchedulerModel task) {
        Runnable runnableTask = createRunnableTask(task);
        CronTrigger cronTrigger = new CronTrigger(task.getCronExpression());
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(runnableTask, cronTrigger);
        scheduledTasks.put(task.getTaskType(), scheduledTask);
        return scheduledTask;
    }

    private Runnable createRunnableTask(SchedulerModel task) {
        return switch (task.getTaskType()) {
            case "FileMove" -> () -> storageService.moveFile(task.getSourcePath(), task.getDestinationPath());
            case "FileCopy" -> () -> storageService.copyFile(task.getSourcePath(), task.getDestinationPath());
            case "FilesUpload" -> () -> {
                for (String path : task.getSourcePaths()) {
                    for (String extension : task.getFileExtensions()) {
                        fileService.sendFileToStorage(path, extension);
                    }
                }
            };
            case "DeleteOldFiles" -> () -> storageService.deleteOldFiles(task.getNumberOfDays());
            case "DeleteAllByExtension" -> () -> storageService.deleteAllFilesByExtension(task.getFileExtension());
            default -> throw new IllegalStateException("Unexpected value: " + task.getTaskType());
        };
    }

    private void cancelScheduledTask(String taskType) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(taskType);
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTasks.remove(taskType);
        }
    }

    public void rescheduleTask(SchedulerModel task) {
        cancelScheduledTask(task.getTaskType());
        task.setCronExpression(task.getCronExpression());
        scheduledTasks.put(task.getTaskType(), scheduledTask(task));
    }
}
