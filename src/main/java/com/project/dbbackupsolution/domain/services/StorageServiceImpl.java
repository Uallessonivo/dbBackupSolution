package com.project.dbbackupsolution.domain.services;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.project.dbbackupsolution.configuration.LoadGoogleStorageConfigs;
import com.project.dbbackupsolution.domain.exceptions.DomainException;
import com.project.dbbackupsolution.domain.exceptions.FileBackupException;
import com.project.dbbackupsolution.infrastructure.GoogleStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StorageServiceImpl implements StorageService {
    private final LoadGoogleStorageConfigs storageConfig;
    private final Storage storage;

    @Autowired
    public StorageServiceImpl(LoadGoogleStorageConfigs storageConfig, GoogleStorage googleStorage) {
        this.storageConfig = storageConfig;
        this.storage = googleStorage.getStorage();
    }

    @Override
    public void uploadFile(MultipartFile file) {
        try {
            String fullPath = storageConfig.getBackupPath() + file.getOriginalFilename();
            BlobId blobId = BlobId.of(storageConfig.getBucketName(), fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());
        } catch (Exception e) {
            throw new FileBackupException("Error occurred while backing up file: ", file.getOriginalFilename());
        }
    }

    @Override
    public void backupFiles(MultipartFile[] files) {
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String fileName = Objects.requireNonNull(file.getOriginalFilename(), "Filename cannot be null");
                String extension = getFileExtension(fileName);

                LocalDate currentDate = LocalDate.now();
                int month = currentDate.getMonthValue();
                int year = currentDate.getYear();

                String fullPath = String.format("%s/%d%d/%s", extension, month, year, fileName);
                BlobId blobId = BlobId.of(storageConfig.getBucketName(), fullPath);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(file.getContentType())
                        .build();

                storage.create(blobInfo, file.getBytes());
            } catch (Exception e) {
                failedFiles.add(file.getOriginalFilename());
            }
        }

        if (!failedFiles.isEmpty()) {
            throw new FileBackupException("Error occurred while backing up some files", failedFiles);
        }
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    @Override
    public void deleteFile(String fileName) {
        try {
            Blob blob = storage.get(storageConfig.getBucketName(), fileName);
            blob.delete();
        } catch (Exception e) {
            throw new DomainException("Error occurred while deleting file: " + fileName);
        }
    }

    @Override
    public void deleteOldFiles(int numberOfDays) {
        LocalDate currentDate = LocalDate.now();
        LocalDate daysAgo = currentDate.minusDays(numberOfDays);
        List<String> failedFiles = new ArrayList<>();

        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            LocalDate blobCreationDate = blob.getCreateTimeOffsetDateTime().toLocalDate();

            if (blobCreationDate.isBefore(daysAgo)) {
                try {
                    blob.delete();
                } catch (Exception e) {
                    failedFiles.add(blob.getName());
                }
            }
        }

        if (!failedFiles.isEmpty()) {
            throw new FileBackupException("Error occurred while deleting some files", failedFiles);
        }
    }

    @Override
    public void deleteAllFilesBySuffix(String suffix) {
        List<String> failedFiles = new ArrayList<>();
        if (!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }

        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            String fileName = blob.getName();
            if (fileName.endsWith(suffix)) {
                try {
                    blob.delete();
                } catch (Exception e) {
                    failedFiles.add(fileName);
                }
            }
        }

        if (!failedFiles.isEmpty()) {
            throw new FileBackupException("Error occurred while deleting some files", failedFiles);
        }
    }

    @Override
    public ByteArrayResource downloadFile(String fileName) {
        try {
            Blob blob = storage.get(storageConfig.getBucketName(), fileName);
            byte[] content = blob.getContent();
            return new ByteArrayResource(content);
        } catch (Exception e) {
            throw new DomainException("Error occurred while downloading file: " + fileName);
        }
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
