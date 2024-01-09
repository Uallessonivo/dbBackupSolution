package com.project.dbbackupsolution.domain.scheduling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dbbackupsolution.domain.exceptions.DomainException;
import com.project.dbbackupsolution.domain.models.SchedulerModel;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class SchedulerManager {
    public void saveSchedulerModel(SchedulerModel schedulerEntity) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("scheduler.json");
        try {
            List<SchedulerModel> tasks;
            if (file.exists() && file.length() > 0) {
                tasks = mapper.readValue(file, new TypeReference<>() {
                });
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

    public List<SchedulerModel> getSavedSchedulerModels() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("scheduler.json");

        if (file.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(file, new TypeReference<List<SchedulerModel>>() {
            });
        } catch (Exception e) {
            throw new DomainException("Error while getting scheduler models", e);
        }
    }
}
