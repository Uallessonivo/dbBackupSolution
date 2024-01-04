package com.project.dbbackupsolution.domain.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerModel {
    private int numberOfDays;
    private String fileExtension;
    private List<String> fileExtensions;
    private String sourcePath;
    private List<String> sourcePaths;
    private String destinationPath;
    private String cronExpression;
    private TaskType taskType;

    @JsonCreator
    public SchedulerModel(@JsonProperty("numberOfDays") int numberOfDays,
                          @JsonProperty("fileExtension") String fileExtension,
                          @JsonProperty("fileExtensions") List<String> fileExtensions,
                          @JsonProperty("sourcePath") String sourcePath,
                          @JsonProperty("sourcePaths") List<String> sourcePaths,
                          @JsonProperty("destinationPath") String destinationPath,
                          @JsonProperty("cronExpression") String cronExpression,
                          @JsonProperty("taskType") String taskType) {
        this.numberOfDays = numberOfDays;
        this.fileExtension = fileExtension;
        this.fileExtensions = fileExtensions;
        this.sourcePath = sourcePath;
        this.sourcePaths = sourcePaths;
        this.destinationPath = destinationPath;
        this.cronExpression = cronExpression;
        this.taskType = TaskType.valueOf(taskType.toUpperCase().replace(" ", "_"));
    }
}