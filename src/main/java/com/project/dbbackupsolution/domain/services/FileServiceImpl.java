package com.project.dbbackupsolution.domain.services;

import com.project.dbbackupsolution.configuration.LoadPaths;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    private final LoadPaths loadPaths;

    public FileServiceImpl(LoadPaths loadPaths) {
        this.loadPaths = loadPaths;
    }

    @Override
    public List<File> extractFiles(String path, String fileExtension) {
        return null;
    }

    private void extractMetadata(String path, String fileExtension) {

    }

    private void compressFiles(String path, String fileExtension) {

    }
}
