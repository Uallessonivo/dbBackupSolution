package com.project.dbbackupsolution.domain.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface StorageService {
    void moveFile(String fileName, String destinationPath);
    void copyFile(String fileName, String destinationPath);
    void uploadFile(MultipartFile file);
    void sendFile(File file, String fileExtension);
    void backupFiles(MultipartFile[] files);
    void deleteFile(String fileName);
    void deleteOldFiles(int numberOfDays);
    void deleteAllFilesByExtension(String fileExtension);
    void deleteAllOldFilesByExtension(int numberOfDays, String fileExtension);
    ByteArrayResource downloadFile(String fileName);
    List<String> listOfFiles();
}
