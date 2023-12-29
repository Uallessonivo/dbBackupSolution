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
    private final String backupPath = "uploads/";

    public LoadGoogleStorageConfigs() {
        Dotenv dotenv = Dotenv.load();
        this.projectId = dotenv.get("GOOGLE_STORAGE_PROJECT_ID");
        this.bucketName = dotenv.get("GOOGLE_STORAGE_BUCKET_NAME");
        this.credentials = dotenv.get("GOOGLE_APPLICATION_CREDENTIALS");
    }
}
