package com.project.dbbackupsolution.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class LoadGoogleStorageConfigs {
    @Value("${google.storage.project-id}")
    private String projectId;
    @Value("${google.storage.bucket-name}")
    private String bucketName;
    @Value("${google.storage.credentials}")
    private String credentials;
    private final String backupPath = "backup/";
}
