package com.project.dbbackupsolution.domain.scheduling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dbbackupsolution.domain.exceptions.DomainException;
import com.project.dbbackupsolution.domain.models.SchedulerModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SchedulerManagerImpl implements SchedulerManager {
    @Override
    public void saveSchedulerModel(SchedulerModel schedulerEntity) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("scheduler.json");

        try {
            List<SchedulerModel> tasks;
            if (file.exists()) {
                tasks = mapper.readValue(file, new TypeReference<>(){});
                tasks.removeIf(task -> task.getTaskType().equals(schedulerEntity.getTaskType()));
            } else {
                tasks = new ArrayList<>();
            }
            tasks.add(schedulerEntity);
            mapper.writeValue(file, tasks);
        } catch (Exception e) {
            throw new DomainException("Error while saving scheduler model", e);
        }
    }

    @Override
    public List<SchedulerModel> getSavedSchedulerModels() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("scheduler.json");
        List<SchedulerModel> tasks = new ArrayList<>();

        if (file.exists()) {
            try {
                tasks = mapper.readValue(file, new TypeReference<>() {});
            } catch (Exception e) {
                throw new DomainException("Error while getting scheduler models", e);
            }
        }

        return tasks;
    }
}
