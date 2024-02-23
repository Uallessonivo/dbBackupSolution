package com.project.dbbackupsolution.domain.services;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.project.dbbackupsolution.configuration.LoadGoogleStorageConfigs;
import com.project.dbbackupsolution.domain.exceptions.DomainException;
import com.project.dbbackupsolution.domain.exceptions.FileException;
import com.project.dbbackupsolution.infrastructure.GoogleStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StorageServiceImpl implements StorageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageServiceImpl.class);
    private final LoadGoogleStorageConfigs storageConfig;
    private final Storage storage;

    public StorageServiceImpl(LoadGoogleStorageConfigs storageConfig, GoogleStorage googleStorage) {
        this.storageConfig = storageConfig;
        this.storage = googleStorage.getStorage();
    }

    @Override
    public void moveFile(String fileName, String destinationPath) {
        LOGGER.info("Moving file {} to {}", fileName, destinationPath);

        try {
            Blob file = copyObjectFromBucket(fileName, destinationPath);
            file.delete();
            LOGGER.info("File {} moved to {} successfully", fileName, destinationPath);
        } catch (Exception e) {
            LOGGER.error("Error while moving file ", e);
            throw new DomainException("Error while moving file ", e.getCause());
        }
    }

    @Override
    public void copyFile(String fileName, String destinationPath) {
        LOGGER.info("Copying file {} to {}", fileName, destinationPath);

        try {
            copyObjectFromBucket(fileName, destinationPath);
            LOGGER.info("File {} copied to {} successfully", fileName, destinationPath);
        } catch (Exception e) {
            LOGGER.error("Error while copying file ", e);
            throw new DomainException("Error while copying file ", e.getCause());
        }
    }

    @Override
    public void uploadFile(MultipartFile file) {
        LOGGER.info("Uploading file {}", file.getOriginalFilename());

        try {
            String fullPath = storageConfig.getBackupPath() + file.getOriginalFilename();
            BlobId blobId = BlobId.of(storageConfig.getBucketName(), fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());
            LOGGER.info("File {} uploaded successfully", file.getOriginalFilename());
        } catch (Exception e) {
            LOGGER.error("Error while uploading file ", e);
            throw new FileException("Error occurred while backing up file: ", file.getOriginalFilename());
        }
    }

    @Override
    public void sendFile(File file, String fileExtension) {
        if (file == null) {
            throw new IllegalArgumentException("File or filename cannot be null");
        }

        LOGGER.info("Sending file {}", file.getName());

        try {
            String fileName = file.getName();
            String fullPath = String.format("%s/%s/%s", fileExtension, getCurrentMonthAndYear(), fileName);
            BlobId blobId = BlobId.of(storageConfig.getBucketName(), fullPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

            byte[] bytesArray = new byte[(int) file.length()];
            try (FileInputStream fis = new FileInputStream(file)) {
                int bytesRead = fis.read(bytesArray);
                if (bytesRead != file.length()) {
                    throw new FileException("Failed to read the entire file: ", fileName);
                }
            }

            storage.create(blobInfo, bytesArray);
            LOGGER.info("File {} sent successfully", file.getName());
        } catch (Exception e) {
            LOGGER.error("Error while sending file ", e);
            throw new FileException("Error occurred while sending file: ", file.getName());
        }
    }

    @Override
    public void sendFiles(List<File> files, String fileExtension) {
        if (files == null) {
            throw new IllegalArgumentException("File or filename cannot be null");
        }

        LOGGER.info("Sending files with desired extension: {}", fileExtension);

        for (File file : files) {
            try {
                String fileName = file.getName();
                String fullPath = String.format("%s/%s/%s", fileExtension, getCurrentMonthAndYear(), fileName);
                BlobId blobId = BlobId.of(storageConfig.getBucketName(), fullPath);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

                byte[] bytesArray = new byte[(int) file.length()];
                try (FileInputStream fis = new FileInputStream(file)) {
                    int bytesRead = fis.read(bytesArray);
                    if (bytesRead != file.length()) {
                        throw new FileException("Failed to read the entire files: ", fileName);
                    }
                }

                storage.create(blobInfo, bytesArray);
                LOGGER.info("File {} sent successfully", file.getName());
            } catch (Exception e) {
                LOGGER.error("Error while sending files ", e);
                throw new FileException("Error occurred while sending files: ", file.getName());
            }
        }
    }

    @Override
    public void backupFiles(MultipartFile[] files) {
        LOGGER.info("Backing up {} files", files.length);
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String fileName = Objects.requireNonNull(file.getOriginalFilename(), "Filename cannot be null");
                String extension = getFileExtension(fileName);
                String fullPath = String.format("%s/%s/%s", extension, getCurrentMonthAndYear(), fileName);
                BlobId blobId = BlobId.of(storageConfig.getBucketName(), fullPath);
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                        .setContentType(file.getContentType())
                        .build();

                storage.create(blobInfo, file.getBytes());
            } catch (Exception e) {
                failedFiles.add(file.getOriginalFilename());
            }
            LOGGER.info("File {} backed up successfully", file.getOriginalFilename());
        }

        if (!failedFiles.isEmpty()) {
            LOGGER.error("Error while backing up files: {}", failedFiles);
            throw new FileException("Error occurred while backing up some files", failedFiles);
        }

        LOGGER.info("All files backed up successfully");
    }

    @Override
    public void deleteFile(String fileName) {
        LOGGER.info("Deleting file {}", fileName);

        try {
            Blob blob = storage.get(storageConfig.getBucketName(), fileName);
            blob.delete();
            LOGGER.info("File {} deleted successfully", fileName);
        } catch (Exception e) {
            LOGGER.error("Error while deleting file ", e);
            throw new FileException("Error occurred while deleting file: ", fileName);
        }
    }

    @Override
    public void deleteOldFiles(int numberOfDays) {
        LOGGER.info("Deleting files older than {} days", numberOfDays);

        LocalDate currentDate = LocalDate.now();
        LocalDate daysAgo = currentDate.minusDays(numberOfDays);
        List<String> failedFiles = new ArrayList<>();

        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            LocalDate blobCreationDate = blob.getCreateTimeOffsetDateTime().toLocalDate();

            if (blobCreationDate.isBefore(daysAgo)) {
                try {
                    blob.delete();
                    LOGGER.info("File {} deleted successfully", blob.getName());
                } catch (Exception e) {
                    LOGGER.error("Error while deleting file ", e);
                    failedFiles.add(blob.getName());
                }
            }
        }

        if (!failedFiles.isEmpty()) {
            LOGGER.error("Error while deleting files: {}", failedFiles);
            throw new FileException("Error occurred while deleting some files", failedFiles);
        }

        LOGGER.info("All files older than {} days deleted successfully", numberOfDays);
    }

    @Override
    public void deleteAllFilesByExtension(String fileExtension) {
        LOGGER.info("Deleting all files with extension {}", fileExtension);

        List<String> failedFiles = new ArrayList<>();
        if (!fileExtension.startsWith(".")) {
            fileExtension = "." + fileExtension;
        }

        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            String fileName = blob.getName();
            if (fileName.endsWith(fileExtension)) {
                try {
                    blob.delete();
                    LOGGER.info("File {} deleted successfully", fileName);
                } catch (Exception e) {
                    failedFiles.add(fileName);
                    LOGGER.error("Error while deleting file ", e);
                }
            }
        }

        if (!failedFiles.isEmpty()) {
            LOGGER.error("Error while deleting files: {}", failedFiles);
            throw new FileException("Error occurred while deleting some files", failedFiles);
        }

        LOGGER.info("All files with extension {} deleted successfully", fileExtension);
    }

    @Override
    public void deleteAllOldFilesByExtension(int numberOfDays, String fileExtension) {
        LOGGER.info("Deleting all files with extension {} older than {} days", fileExtension, numberOfDays);

        LocalDate currentDate = LocalDate.now();
        LocalDate daysAgo = currentDate.minusDays(numberOfDays);

        List<String> failedFiles = new ArrayList<>();
        if (!fileExtension.startsWith(".")) {
            fileExtension = "." + fileExtension;
        }

        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            String fileName = blob.getName();
            LocalDate blobCreationDate = blob.getCreateTimeOffsetDateTime().toLocalDate();

            if (fileName.endsWith(fileExtension) && blobCreationDate.isBefore(daysAgo)) {
                try {
                    blob.delete();
                    LOGGER.info("File {} deleted successfully", fileName);
                } catch (Exception e) {
                    LOGGER.error("Error while deleting file ", e);
                    failedFiles.add(fileName);
                }
            }
        }

        if (!failedFiles.isEmpty()) {
            LOGGER.error("Error while deleting files: {}", failedFiles);
            throw new FileException("Error occurred while deleting some files", failedFiles);
        }

        LOGGER.info("All files with extension {} older than {} days deleted successfully", fileExtension, numberOfDays);
    }

    @Override
    public ByteArrayResource downloadFile(String fileName) {
        LOGGER.info("Downloading file {}", fileName);

        try {
            Blob file = searchObject(fileName);
            byte[] content = file.getContent();
            LOGGER.info("File {} downloaded successfully", fileName);
            return new ByteArrayResource(content);
        } catch (Exception e) {
            LOGGER.error("Error while downloading file ", e);
            throw new DomainException("Error occurred while downloading file: " + fileName);
        }
    }

    @Override
    public List<String> listOfFiles() {
        LOGGER.info("Listing all files");

        List<String> list = new ArrayList<>();
        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            list.add(blob.getName());
        }

        LOGGER.info("Listed {} files", list.size());

        return list;
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    private Blob searchObject(String fileName) {
        Blob object = null;
        Page<Blob> blobs = storage.list(storageConfig.getBucketName());
        for (Blob blob : blobs.iterateAll()) {
            if (blob.getName().endsWith(fileName)) {
                object = blob;
                break;
            }
        }

        if (object == null) {
            throw new DomainException("File not found");
        }

        return object;
    }

    private Blob copyObjectFromBucket(String fileName, String destinationPath) {
        Blob file = searchObject(fileName);

        String destination = String.format("%s/%s", destinationPath, fileName);
        BlobId source = BlobId.of(storageConfig.getBucketName(), file.getName());
        BlobId target = BlobId.of(storageConfig.getBucketName(), destination);

        storage.copy(Storage.CopyRequest.newBuilder()
                .setSource(source)
                .setTarget(target)
                .build());

        return file;
    }

    private String getCurrentMonthAndYear() {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();
        return String.format("%d%d", month, year);
    }
}
