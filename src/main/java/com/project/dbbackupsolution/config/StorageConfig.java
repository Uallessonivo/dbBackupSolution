package com.project.dbbackupsolution.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Getter
@Configuration
public class StorageConfig {
    @Value("${google.storage.project-id}")
    private String projectId;
    @Value("${google.storage.bucket-name}")
    private String bucketName;
    @Value("${google.storage.credentials}")
    private String credentials;
}
