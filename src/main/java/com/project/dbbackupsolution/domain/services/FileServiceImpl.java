package com.project.dbbackupsolution.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final StorageService storageService;

    @Autowired
    public FileServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void sendFileToStorage(String path, String fileExtension) {
        File folder = new File(path);

        if (!folder.exists()) {
            throw new IllegalArgumentException("The path does not exist");
        }

        File[] listOfFiles = folder.listFiles();
        List<File> filesWithDesiredExtension = new ArrayList<>();

        if (listOfFiles == null || listOfFiles.length == 0) {
            throw new IllegalArgumentException("The path does not contain any files");
        }

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(fileExtension)) {
                filesWithDesiredExtension.add(file);
            }
        }

        File compressedFiles = compressFiles(filesWithDesiredExtension);
        storageService.sendFile(compressedFiles, fileExtension);

        if (!compressedFiles.delete()) {
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
                    throw new RuntimeException("Failed to add file to zip", e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress files", e);
        }

        return new File(zipFileName);
    }
}
