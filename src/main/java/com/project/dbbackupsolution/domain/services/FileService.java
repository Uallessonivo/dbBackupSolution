package com.project.dbbackupsolution.domain.services;

public interface FileService {
    void sendFileToStorage(String path, String fileExtension);
}
