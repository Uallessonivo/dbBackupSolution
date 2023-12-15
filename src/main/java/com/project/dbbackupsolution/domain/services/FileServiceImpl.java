package com.project.dbbackupsolution.domain.services;

import com.project.dbbackupsolution.configuration.LoadPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {
    private final LoadPaths loadPaths;

    @Autowired
    public FileServiceImpl(LoadPaths loadPaths) {
        this.loadPaths = loadPaths;
    }

    @Override
    public List<File> extractFiles(String path, String fileExtension) {
        File folder = new File(path);

        if (!folder.exists()) {
            throw new IllegalArgumentException("The path does not exist");
        }

        File[] listOfFiles = folder.listFiles();
        List<File> filesWithDesiredExtension = new ArrayList<>();

        if (listOfFiles == null) {
            throw new IllegalArgumentException("The path does not contain any files");
        }

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(fileExtension)) {
                filesWithDesiredExtension.add(file);
            }
        }

        return filesWithDesiredExtension;
    }

    private void extractMetadata(String path, String fileExtension) {

    }

    private File compressFile(File file) {
        return null;
    }
}
