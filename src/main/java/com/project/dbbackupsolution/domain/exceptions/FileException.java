package com.project.dbbackupsolution.domain.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class FileException extends RuntimeException {
    private final List<String> failedFiles;
    private final String failedFile;

    public FileException(String message, List<String> failedFiles) {
        super(message);
        this.failedFiles = failedFiles;
        this.failedFile = null;
    }

    public FileException(String message, String failedFile) {
        super(message);
        this.failedFile = failedFile;
        this.failedFiles = null;
    }
}
