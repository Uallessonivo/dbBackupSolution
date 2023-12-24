package com.project.dbbackupsolution.domain.scheduling;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.project.dbbackupsolution.domain.services.FileService;
import com.project.dbbackupsolution.domain.services.StorageService;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    private final StorageService storageService;
    private final FileService fileService;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;

    public SchedulerServiceImpl(StorageService storageService, FileService fileService, TaskScheduler taskScheduler) {
        this.storageService = storageService;
        this.fileService = fileService;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void schedulerFileMove(String sourcePath, String destinationPath, String cronExpression) {
        cancelTask();
        Runnable task = () -> storageService.moveFile(sourcePath, destinationPath);
        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        taskScheduler.schedule(task, cronTrigger);
    }

    @Override
    public void schedulerFileCopy(String sourcePath, String destinationPath, String cronExpression) {
        cancelTask();
        Runnable task = () -> storageService.copyFile(sourcePath, destinationPath);
        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        taskScheduler.schedule(task, cronTrigger);
    }

    @Override
    public void schedulerFilesUpload(List<String> sourcePath, List<String> fileExtension, String cronExpression) {
        cancelTask();

        Runnable task = () -> {
            for (String path : sourcePath) {
                for (String extension : fileExtension) {
                    fileService.sendFileToStorage(path, extension);
                }
            }
        };

        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        taskScheduler.schedule(task, cronTrigger);
    }

    @Override
    public void schedulerDeleteOldFiles(int numberOfDays, String cronExpression) {
        cancelTask();
        Runnable task = () -> storageService.deleteOldFiles(numberOfDays);
        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        taskScheduler.schedule(task, cronTrigger);
    }

    @Override
    public void schedulerDeleteAllByExtension(String fileExtension, String cronExpression) {
        cancelTask();
        Runnable task = () -> storageService.deleteAllFilesByExtension(fileExtension);
        CronTrigger cronTrigger = new CronTrigger(cronExpression);
        taskScheduler.schedule(task, cronTrigger);
    }

    private void cancelTask() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }
}
