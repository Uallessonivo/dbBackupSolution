package com.project.dbbackupsolution.domain.scheduling;

import com.project.dbbackupsolution.domain.models.SchedulerModel;

import java.util.List;

public interface SchedulerManager {
    void saveSchedulerModel(SchedulerModel schedulerEntity);
    List<SchedulerModel> getSavedSchedulerModels();
}
