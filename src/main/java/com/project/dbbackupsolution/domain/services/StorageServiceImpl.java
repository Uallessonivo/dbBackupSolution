package com.project.dbbackupsolution.domain.services;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.project.dbbackupsolution.config.StorageConfig;
import com.project.dbbackupsolution.domain.exceptions.DomainException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageServiceImpl implements StorageService {
    private final StorageConfig storageConfig;
    private final Storage storage;

    @Autowired
    public StorageServiceImpl(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;

        try (InputStream serviceAccountStream = new ClassPathResource("gcp_account_file.json").getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream)
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");

            this.storage = StorageOptions.newBuilder()
                    .setProjectId(storageConfig.getProjectId())
                    .setCredentials(credentials)
                    .build()
                    .getService();
        } catch (IOException e) {
            throw new DomainException("Error while reading credentials file");
        }
    }

    @Override
    public void uploadFile(MultipartFile file) throws IOException {
        String fullPath = storageConfig.getBackupPath() + file.getOriginalFilename();
        BlobId blobId = BlobId.of(storageConfig.getBucketName(), fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getBytes());
    }

    @Override
    public boolean deleteFile(String fileName) {
        Blob blob = storage.get(storageConfig.getBucketName(), fileName);
        return blob.delete();
    }

    @Override
    public ByteArrayResource downloadFile(String fileName) {
        Blob blob = storage.get(storageConfig.getBucketName(), fileName);
        return new ByteArrayResource(blob.getContent());
    }

    @Override
    public List<String> listOfFiles() {
        List<String> list = new ArrayList<>();
        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            list.add(blob.getName());
        }
        return list;
    }
}
