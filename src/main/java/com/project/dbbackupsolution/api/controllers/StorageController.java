package com.project.dbbackupsolution.api.controllers;

import com.project.dbbackupsolution.domain.services.StorageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class StorageController {
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<List<String>> listOfFiles() {
        try {
            List<String> files = storageService.listOfFiles();
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) {
        try {
            storageService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/backup")
    public ResponseEntity<String> backupFiles(@RequestParam MultipartFile[] files) {
        try {
            storageService.backupFiles(files);
            return ResponseEntity.ok("Files backed up successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam String fileName) {
        try {
            storageService.deleteFile(fileName);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-old-files")
    public ResponseEntity<String> deleteOldFile(@RequestParam int numberOfDays) {
        try {
            storageService.deleteOldFiles(numberOfDays);
            return ResponseEntity.ok("Files deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete-by-suffix")
    public ResponseEntity<String> deleteAllFileBySuffix(@RequestParam String suffix) {
        try {
            storageService.deleteAllFilesByExtension(suffix);
            return ResponseEntity.ok("Files deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String fileName) {
        try {
            ByteArrayResource resource = storageService.downloadFile(fileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("move-file")
    public ResponseEntity<String> moveFile(@RequestParam String fileName, @RequestParam String destination) {
        try {
            storageService.moveFile(fileName, destination);
            return ResponseEntity.ok("File moved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("copy-file")
    public ResponseEntity<String> copyFile(@RequestParam String fileName, @RequestParam String destination) {
        try {
            storageService.copyFile(fileName, destination);
            return ResponseEntity.ok("File copied successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
