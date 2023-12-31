package com.project.dbbackupsolution.api.controllers;

import com.project.dbbackupsolution.domain.models.SchedulerModel;
import com.project.dbbackupsolution.domain.scheduling.SchedulerManager;
import com.project.dbbackupsolution.domain.scheduling.SchedulerService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> saveSchedulerModel(@RequestBody SchedulerModel schedulerModel) {
        try {
            schedulerManager.saveSchedulerModel(schedulerModel);
            schedulerService.updateSchedulesTasks(schedulerModel);
            return ResponseEntity.ok("Scheduler model saved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reschedule")
    public ResponseEntity<String> rescheduleSchedulerModel(@RequestBody SchedulerModel schedulerModel) {
        try {
            schedulerService.rescheduleTask(schedulerModel);
            return ResponseEntity.ok("Scheduler model rescheduled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
