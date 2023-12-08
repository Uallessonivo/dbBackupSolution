package com.project.dbbackupsolution.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.project.dbbackupsolution.configuration.LoadGoogleStorageConfigs;
import com.project.dbbackupsolution.domain.exceptions.DomainException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class GoogleStorage {
    private final LoadGoogleStorageConfigs storageConfig;

    public GoogleStorage(LoadGoogleStorageConfigs storageConfig) {
        this.storageConfig = storageConfig;
    }

    public Storage getStorage() {
        try (InputStream serviceAccountStream = new ClassPathResource("gcp_account_file.json").getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");

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
