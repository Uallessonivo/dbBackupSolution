package com.project.dbbackupsolution.domain.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.jetbrains.annotations.NotNull;

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
    private boolean useDateOnPath;
    private String dateFormat;
    private String timeZone;
    private String baseFilePath;
    private String finalFilePath;

    @JsonCreator
    public SchedulerModel(@JsonProperty("numberOfDays") int numberOfDays,
                          @JsonProperty("fileExtension") String fileExtension,
                          @JsonProperty("fileExtensions") List<String> fileExtensions,
                          @JsonProperty("sourcePath") String sourcePath,
                          @JsonProperty("sourcePaths") List<String> sourcePaths,
                          @JsonProperty("destinationPath") String destinationPath,
                          @JsonProperty("cronExpression") String cronExpression,
                          @NotNull @JsonProperty("taskType") String taskType,
                          @JsonProperty("useDateOnPath") boolean useDateOnPath,
                          @JsonProperty("dateFormat") String dateFormat,
                          @JsonProperty("timeZone") String timeZone,
                          @JsonProperty("baseFilePath") String baseFilePath,
                          @JsonProperty("finalFilePath") String finalFilePath) {
        this.numberOfDays = numberOfDays;
        this.fileExtension = fileExtension;
        this.fileExtensions = fileExtensions;
        this.sourcePath = sourcePath;
        this.sourcePaths = sourcePaths;
        this.destinationPath = destinationPath;
        this.cronExpression = cronExpression;
        this.taskType = TaskType.valueOf(taskType.toUpperCase());
        this.useDateOnPath = useDateOnPath;
        this.dateFormat = dateFormat;
        this.timeZone = timeZone;
        this.baseFilePath = baseFilePath;
        this.finalFilePath = finalFilePath;
    }
}