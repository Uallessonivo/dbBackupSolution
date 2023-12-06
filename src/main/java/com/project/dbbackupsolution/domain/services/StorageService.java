package com.project.dbbackupsolution.domain.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    void uploadFile(MultipartFile file) throws IOException;
    boolean deleteFile(String fileName);
    ByteArrayResource downloadFile(String fileName);
    List<String> listOfFiles();
}
