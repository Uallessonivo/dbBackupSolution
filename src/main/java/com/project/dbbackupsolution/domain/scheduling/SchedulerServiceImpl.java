package com.project.dbbackupsolution.domain.scheduling;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.dbbackupsolution.domain.services.FileService;
import com.project.dbbackupsolution.domain.services.StorageService;

@Service
public class SchedulerServiceImpl implements SchedulerService {
    private final StorageService storageService;
    private final FileService fileService;

    public SchedulerServiceImpl(StorageService storageService, FileService fileService) {
        this.storageService = storageService;
        this.fileService = fileService;
    }

    @Override
    @Scheduled(cron = "#{cronExpression}")
    public void schedulerFileMove(String sourcePath, String destinationPath, String cronExpression) {
        storageService.moveFile(cronExpression, destinationPath);
    }

    @Override
    @Scheduled(cron = "#{cronExpression}")
    public void schedulerFileCopy(String sourcePath, String destinationPath, String cronExpression) {
        storageService.copyFile(cronExpression, destinationPath);
    }

    @Override
    @Scheduled(cron = "#{cronExpression}")
    public void schedulerFilesUpload(List<String> sourcePath, List<String> fileExtension, String cronExpression) {
        for (String path : sourcePath) {
            for (String extension : fileExtension) {
                fileService.sendFileToStorage(path, extension);
            }
        }
    }

    @Override
    @Scheduled(cron = "#{cronExpression}")
    public void schedulerDeleteOldFiles(int numberOfDays, String cronExpression) {
        storageService.deleteOldFiles(numberOfDays);
    }

    @Override
    @Scheduled(cron = "#{cronExpression}")
    public void schedulerDeleteAllBySuffix(String fileExtension, String cronExpression) {
        storageService.deleteAllFilesBySuffix(fileExtension);
    }
}
