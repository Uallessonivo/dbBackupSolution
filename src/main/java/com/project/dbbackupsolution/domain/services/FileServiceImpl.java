package com.project.dbbackupsolution.domain.services;

import com.project.dbbackupsolution.configuration.LoadPaths;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
    private final LoadPaths loadPaths;

    public FileServiceImpl(LoadPaths loadPaths) {
        this.loadPaths = loadPaths;
    }

    @Override
    public void extractFiles(String path, String fileExtension) {

    }

    @Override
    public void extractMetadata(String path, String fileExtension) {

    }

    @Override
    public void compressFiles(String path, String fileExtension) {

    }
}
