package com.project.dbbackupsolution.domain.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    void uploadFile(MultipartFile file) throws IOException;
    void backupFiles(MultipartFile[] files) throws IOException;
    void deleteFile(String fileName);
    void deleteOldFiles(int numberOfDays);
    void deleteAllFilesBySuffix(String suffix);
    ByteArrayResource downloadFile(String fileName);
    List<String> listOfFiles();
}
