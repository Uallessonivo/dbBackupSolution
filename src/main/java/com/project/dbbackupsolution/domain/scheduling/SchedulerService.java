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
                    String emailBody = String.format("File moved successfully from %s to %s", task.getSourcePath(), task.getDestinationPath());
                    emailNotification.sendEmail("File Move Scheduler", emailBody);
                } catch (Exception e) {
                    String emailBody = String.format("Error moving file from %s to %s: %s", task.getSourcePath(), task.getDestinationPath(), e.getMessage());
                    emailNotification.sendEmail("File Move Scheduler", emailBody);
                }
            };
            case TaskType.FILE_COPY -> () -> {
                try {
                    storageService.copyFile(task.getSourcePath(), task.getDestinationPath());
                    String emailBody = String.format("File copied successfully from %s to %s", task.getSourcePath(), task.getDestinationPath());
                    emailNotification.sendEmail("File Copy Scheduler", emailBody);
                } catch (Exception e) {
                    String emailBody = String.format("Error copying file from %s to %s: %s", task.getSourcePath(), task.getDestinationPath(), e.getMessage());
                    emailNotification.sendEmail("File Copy Scheduler", emailBody);
                }
            };
            case TaskType.FILES_UPLOAD -> () -> {
                try {
                    for (String path : task.getSourcePaths()) {
                        for (String extension : task.getFileExtensions()) {
                            fileService.sendFileToStorage(path, extension);
                        }
                    }
                    String emailBody = String.format("Files uploaded successfully from %s", task.getSourcePaths());
                    emailNotification.sendEmail("File Upload Scheduler", emailBody);
                } catch (Exception e) {
                    String emailBody = String.format("Error uploading files from %s: %s", task.getSourcePaths(), e.getMessage());
                    emailNotification.sendEmail("File Upload Scheduler", emailBody);
                }
            };
            case TaskType.DELETE_OLD_FILES -> () -> {
                try {
                    storageService.deleteOldFiles(task.getNumberOfDays());
                    String emailBody = String.format("Old files with %d days deleted successfully", task.getNumberOfDays());
                    emailNotification.sendEmail("Delete Old Files Scheduler", emailBody);
                } catch (Exception e) {
                    String emailBody = String.format("Error deleting old files: %s", e.getMessage());
                    emailNotification.sendEmail("Delete Old Files Scheduler", emailBody);
                }
            };
            case TaskType.DELETE_ALL_BY_EXTENSION -> () -> {
                try {
                    storageService.deleteAllFilesByExtension(task.getFileExtension());
                    String emailBody = String.format("Files with extension %s deleted successfully", task.getFileExtension());
                    emailNotification.sendEmail("Delete All Files By Extension Scheduler", emailBody);
                } catch (Exception e) {
                    String emailBody = String.format("Error deleting files: %s", e.getMessage());
                    emailNotification.sendEmail("Delete All Files By Extension Scheduler", emailBody);
                }

            };
            case TaskType.DELETE_ALL_OLD_BY_EXTENSION -> () -> {
                try {
                    storageService.deleteAllOldFilesByExtension(task.getNumberOfDays(), task.getFileExtension());
                    String emailBody = String.format("Old files with %d days and extension %s deleted successfully", task.getNumberOfDays(), task.getFileExtension());
                    emailNotification.sendEmail("Delete All Old Files By Extension Scheduler", emailBody);
                } catch (Exception e) {
                    String emailBody = String.format("Error deleting old files: %s", e.getMessage());
                    emailNotification.sendEmail("Delete All Old Files By Extension Scheduler", emailBody);
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
