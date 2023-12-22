package com.project.dbbackupsolution.domain.scheduling;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Override
    public void schedulerFileMove(String sourcePath, String destinationPath, String cronExpression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'schedulerFileMove'");
    }

    @Override
    public void schedulerFileCopy(String sourcePath, String destinationPath, String cronExpression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'schedulerFileCopy'");
    }

    @Override
    public void schedulerFilesUpload(List<String> sourcePath, List<String> fileExtension, String cronExpression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'schedulerFilesUpload'");
    }
}
