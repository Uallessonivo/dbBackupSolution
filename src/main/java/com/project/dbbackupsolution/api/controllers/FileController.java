package com.project.dbbackupsolution.api.controllers;

import com.project.dbbackupsolution.api.request.FileRequest;
import com.project.dbbackupsolution.domain.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendFiles(@RequestBody FileRequest fileRequest) {
        try {
            fileService.sendFileToStorage(fileRequest.getPath(), fileRequest.getFileExtension());
            return ResponseEntity.ok("Files sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
