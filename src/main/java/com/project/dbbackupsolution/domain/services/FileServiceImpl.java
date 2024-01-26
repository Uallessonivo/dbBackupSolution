package com.project.dbbackupsolution.domain.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
    private final StorageService storageService;

    public FileServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void sendFileToStorage(String path, String fileExtension) {
        File folder = new File(path);

        if (!folder.exists()) {
            LOGGER.error("The path does not exist: {}", path);
            throw new IllegalArgumentException("The path does not exist");
        }

        File[] listOfFiles = folder.listFiles();
        List<File> filesWithDesiredExtension = new ArrayList<>();

        if (listOfFiles == null || listOfFiles.length == 0) {
            LOGGER.error("The path does not contain any files: {}", path);
            throw new IllegalArgumentException("The path does not contain any files");
        }

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(fileExtension)) {
                LOGGER.info("File with desired extension found: {}", file.getName());
                filesWithDesiredExtension.add(file);
            }
        }

        File compressedFiles = compressFiles(filesWithDesiredExtension);
        storageService.sendFile(compressedFiles, fileExtension);

        if (!compressedFiles.delete()) {
            LOGGER.error("Failed to delete compressed files");
            throw new RuntimeException("Failed to delete compressed files");
        }
    }

    private File compressFiles(List<File> files) {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();

        String zipFileName = "backup-" + month + "-" + year + ".zip";
        try (FileOutputStream fos = new FileOutputStream(zipFileName);
                ZipOutputStream zipOS = new ZipOutputStream(fos)) {

            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOS.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) != -1) {
                        zipOS.write(buffer, 0, len);
                    }

                    zipOS.closeEntry();
                } catch (IOException e) {
                    LOGGER.error("Failed to add file to zip", e);
                    throw new RuntimeException("Failed to add file to zip", e);
                }
                LOGGER.info("File added to zip: {}", file.getName());
            }
            LOGGER.info("Files compressed successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to compress files", e);
            throw new RuntimeException("Failed to compress files", e);
        }

        return new File(zipFileName);
    }
}
