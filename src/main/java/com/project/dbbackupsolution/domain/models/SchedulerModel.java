package com.project.dbbackupsolution.domain.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SchedulerModel {
    private int numberOfDays;
    private String fileExtension;
    private List<String> fileExtensions;
    private String sourcePath;
    private List<String> sourcePaths;
    private String destinationPath;
    private String cronExpression;
    private String taskType;
}