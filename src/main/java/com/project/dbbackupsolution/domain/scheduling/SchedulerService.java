package com.project.dbbackupsolution.domain.scheduling;

import java.util.List;

public interface SchedulerService {
    void schedulerFileMove(String sourcePath, String destinationPath, String cronExpression);

    void schedulerFileCopy(String sourcePath, String destinationPath, String cronExpression);

    void schedulerFilesUpload(List<String> sourcePath, List<String> fileExtension, String cronExpression);
}
