package com.project.dbbackupsolution.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.project.dbbackupsolution.configuration.LoadGoogleStorageConfigs;
import com.project.dbbackupsolution.domain.exceptions.DomainException;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

@Component
public class GoogleStorage {
    private final LoadGoogleStorageConfigs storageConfig;

    public GoogleStorage(LoadGoogleStorageConfigs storageConfig) {
        this.storageConfig = storageConfig;
    }

    public Storage getStorage() {
        try {
            String credentialsPath = storageConfig.getCredentialsPath();
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

            return StorageOptions.newBuilder()
                    .setProjectId(storageConfig.getProjectId())
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new DomainException("Error while reading credentials file");
        }
    }
}
