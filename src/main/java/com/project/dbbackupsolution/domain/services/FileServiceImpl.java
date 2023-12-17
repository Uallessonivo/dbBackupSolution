package com.project.dbbackupsolution.domain.services;

import com.project.dbbackupsolution.configuration.LoadPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
                File compressedFile = compressFile(file);
                filesWithDesiredExtension.add(compressedFile);
            }
        }

        return filesWithDesiredExtension;
    }

    private void extractMetadata(String path, String fileExtension) {

    }

    private File compressFile(File file) {
        String filePath = file.getAbsolutePath();

        if (filePath.endsWith(".zip") || filePath.endsWith(".rar") || filePath.endsWith(".7z") || filePath.endsWith(".gz")) {
            return file;
        }

        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(filePath + ".zip");
             ZipOutputStream zipOS = new ZipOutputStream(fos)) {

            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOS.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int len;
            while ((len=fis.read(buffer)) != -1) {
                zipOS.write(buffer, 0, len);
            }

            zipOS.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress file", e);
        }

        file.delete();

        return new File(filePath + ".zip");
    }
}
