package com.project.dbbackupsolution.api.controllers;

import com.project.dbbackupsolution.domain.models.SchedulerModel;
import com.project.dbbackupsolution.domain.scheduling.SchedulerManager;
import com.project.dbbackupsolution.domain.scheduling.SchedulerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {
    private final SchedulerService schedulerService;
    private final SchedulerManager schedulerManager;

    public SchedulerController(SchedulerService schedulerService, SchedulerManager schedulerManager) {
        this.schedulerService = schedulerService;
        this.schedulerManager = schedulerManager;
    }

    @PostMapping("/save")
    public void saveSchedulerModel(@RequestBody SchedulerModel schedulerModel) {
        schedulerManager.saveSchedulerModel(schedulerModel);
        schedulerService.updateSchedulesTasks(schedulerModel);
    }

    @PostMapping("/reschedule")
    public void rescheduleSchedulerModel(@RequestBody SchedulerModel schedulerModel) {
        schedulerService.rescheduleTask(schedulerModel);
    }
}
