package com.project.dbbackupsolution.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerRequest {
    private String taskId;
    private String cronExpression;
    private String taskName;
    private String taskDescription;
}
