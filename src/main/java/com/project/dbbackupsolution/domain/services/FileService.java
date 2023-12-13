package com.project.dbbackupsolution.domain.services;

public interface FileService {
    void extractFiles(String path, String fileExtension);
    void extractMetadata(String path, String fileExtension);
    void compressFiles(String path, String fileExtension);
}
