package com.project.dbbackupsolution.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoadGoogleStorageConfigs {
    private final String projectId;
    private final String bucketName;
    private final String credentials;
    private final String backupPath;

    public LoadGoogleStorageConfigs() {
        Dotenv dotenv = Dotenv.load();
        this.projectId = dotenv.get("GOOGLE_STORAGE_PROJECT_ID");
        this.bucketName = dotenv.get("GOOGLE_STORAGE_BUCKET_NAME");
        this.credentials = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS");
        this.backupPath = dotenv.get("GOOGLE_APPLICATION_BACKUP_PATH");

        if (this.projectId == null || this.projectId.isEmpty()) {
            throw new RuntimeException("GOOGLE_STORAGE_PROJECT_ID not found in .env file");
        }

        if (this.bucketName == null || this.bucketName.isEmpty()) {
            throw new RuntimeException("GOOGLE_STORAGE_BUCKET_NAME not found in .env file");
        }

        if (this.credentials == null || this.credentials.isEmpty()) {
            throw new RuntimeException("GOOGLE_APPLICATION_CREDENTIALS not found in .env file");
        }

        if (this.backupPath == null || this.backupPath.isEmpty()) {
            throw new RuntimeException("GOOGLE_APPLICATION_BACKUP_PATH not found in .env file");
        }
    }
}
