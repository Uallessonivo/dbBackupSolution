package com.project.dbbackupsolution.domain.scheduling;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.project.dbbackupsolution.domain.models.SchedulerModel;
import com.project.dbbackupsolution.domain.models.TaskType;
import com.project.dbbackupsolution.infrastructure.EmailNotification;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.project.dbbackupsolution.domain.services.FileService;
import com.project.dbbackupsolution.domain.services.StorageService;

@Service
public class SchedulerService {
    private final StorageService storageService;
    private final FileService fileService;
    private final TaskScheduler taskScheduler;
    private final SchedulerManager schedulerManager;
    private final EmailNotification emailNotification;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerService.class);
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public SchedulerService(StorageService storageService, FileService fileService, TaskScheduler taskScheduler,
                            SchedulerManager schedulerManager, EmailNotification emailNotification) {
        this.storageService = storageService;
        this.fileService = fileService;
        this.taskScheduler = taskScheduler;
        this.schedulerManager = schedulerManager;
        this.emailNotification = emailNotification;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing scheduler service");
        List<SchedulerModel> tasks = schedulerManager.getSavedSchedulerModels();
        if (tasks != null && !tasks.isEmpty()) {
            for (SchedulerModel task : tasks) {
                scheduledTask(task);
            }
        }
        LOGGER.info("Scheduler service initialized successfully");
    }

    private ScheduledFuture<?> scheduledTask(SchedulerModel task) {
        Runnable runnableTask = createRunnableTask(task);
        CronTrigger cronTrigger = new CronTrigger(task.getCronExpression());
        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(runnableTask, cronTrigger);
        scheduledTasks.put(String.valueOf(task.getTaskType()), scheduledTask);
        LOGGER.info("Scheduled task: {}", task.getTaskType());
        return scheduledTask;
    }

    private Runnable createRunnableTask(SchedulerModel task) {
        LOGGER.info("Creating runnable task: {}", task.getTaskType());
        Runnable runnableTask = switch (task.getTaskType()) {
            case TaskType.FILE_MOVE -> () -> {
                try {
                    storageService.moveFile(task.getSourcePath(), task.getDestinationPath());
                    emailNotification.sendEmail("File Move Scheduler", "File moved successfully");
                } catch (Exception e) {
                    emailNotification.sendEmail("File Move Scheduler", "Error moving file: " + e.getMessage());
                }
            };
            case TaskType.FILE_COPY -> () -> {
                try {
                    storageService.copyFile(task.getSourcePath(), task.getDestinationPath());
                    emailNotification.sendEmail("File Copy Scheduler", "File copied successfully");
                } catch (Exception e) {
                    emailNotification.sendEmail("File Copy Scheduler", "Error copying file: " + e.getMessage());
                }
            };
            case TaskType.FILES_UPLOAD -> () -> {
                try {
                    for (String path : task.getSourcePaths()) {
                        for (String extension : task.getFileExtensions()) {
                            fileService.sendFileToStorage(path, extension);
                        }
                    }
                    emailNotification.sendEmail("File Upload Scheduler", "Files uploaded successfully");
                } catch (Exception e) {
                    emailNotification.sendEmail("File Upload Scheduler", "Error uploading files: " + e.getMessage());
                }
            };
            case TaskType.DELETE_OLD_FILES -> () -> {
                try {
                    storageService.deleteOldFiles(task.getNumberOfDays());
                    emailNotification.sendEmail("Delete Old Files Scheduler", "Old files deleted successfully");
                } catch (Exception e) {
                    emailNotification.sendEmail("Delete Old Files Scheduler", "Error deleting old files: " + e.getMessage());
                }
            };
            case TaskType.DELETE_ALL_BY_EXTENSION -> () -> {
                try {
                    storageService.deleteAllFilesByExtension(task.getFileExtension());
                    emailNotification.sendEmail("Delete All Files By Extension Scheduler", "Files deleted successfully");
                } catch (Exception e) {
                    emailNotification.sendEmail("Delete All Files By Extension Scheduler", "Error deleting files: " + e.getMessage());
                }

            };
            case TaskType.DELETE_ALL_OLD_BY_EXTENSION -> () -> {
                try {
                    storageService.deleteAllOldFilesByExtension(task.getNumberOfDays(), task.getFileExtension());
                    emailNotification.sendEmail("Delete All Old Files By Extension Scheduler", "Old files deleted successfully");
                } catch (Exception e) {
                    emailNotification.sendEmail("Delete All Old Files By Extension Scheduler", "Error deleting old files: " + e.getMessage());
                }
            };
            default -> throw new IllegalStateException("Unexpected value: " + task.getTaskType());
        };
        LOGGER.info("Runnable task created successfully: {}", task.getTaskType());
        return runnableTask;
    }

    public void rescheduleTask(SchedulerModel task) {
        cancelAndRemoveScheduledTask(String.valueOf(task.getTaskType()));
        scheduledTasks.put(String.valueOf(task.getTaskType()), scheduledTask(task));
        LOGGER.info("Rescheduled task: {}", task.getTaskType());
    }

    public void updateSchedulesTasks(SchedulerModel task) {
        rescheduleTask(task);
        LOGGER.info("Updated task: {}", task.getTaskType());
    }

    private void cancelAndRemoveScheduledTask(String taskType) {
        ScheduledFuture<?> oldScheduledTask = scheduledTasks.remove(taskType);
        if (oldScheduledTask != null) {
            oldScheduledTask.cancel(true);
            LOGGER.info("Canceled task: {}", taskType);
        }
    }
}
