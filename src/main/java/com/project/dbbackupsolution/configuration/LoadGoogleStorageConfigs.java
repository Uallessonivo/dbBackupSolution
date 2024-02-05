package com.project.dbbackupsolution.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoadGoogleStorageConfigs {
    @Value("${GOOGLE_STORAGE_PROJECT_ID}")
    private String projectId;
    @Value("${GOOGLE_STORAGE_BUCKET_NAME}")
    private String bucketName;
    @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
    private String credentialsPath;
    @Value("${GOOGLE_APPLICATION_BACKUP_PATH}")
    private String backupPath;
}
