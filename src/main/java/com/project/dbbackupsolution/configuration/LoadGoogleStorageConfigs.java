package com.project.dbbackupsolution.configuration;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoadGoogleStorageConfigs {
    private String projectId;
    private String bucketName;
    private String credentials;
    private final String backupPath = "backup/";

    public LoadGoogleStorageConfigs() {
        this.projectId = System.getenv("GOOGLE_STORAGE_PROJECT_ID");
        this.bucketName = System.getenv("GOOGLE_STORAGE_BUCKET_NAME");
        this.credentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
    }
}
