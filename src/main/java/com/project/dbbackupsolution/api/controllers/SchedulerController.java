package com.project.dbbackupsolution.api.controllers;

import com.project.dbbackupsolution.domain.scheduling.SchedulerManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dbbackupsolution.domain.scheduling.SchedulerService;

@RestController
@RequestMapping("/api/scheduler")
public class SchedulerController {
    private final SchedulerService schedulerService;
    private final SchedulerManager schedulerManager;

    public SchedulerController(SchedulerService schedulerService, SchedulerManager schedulerManager) {
        this.schedulerService = schedulerService;
        this.schedulerManager = schedulerManager;
    }
}
