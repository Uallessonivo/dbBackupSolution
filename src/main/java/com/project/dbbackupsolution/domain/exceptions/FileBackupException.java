package com.project.dbbackupsolution.domain.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class FileBackupException extends RuntimeException {
    private final List<String> failedFiles;
    private final String failedFile;

    public FileBackupException(String message, List<String> failedFiles) {
        super(message);
        this.failedFiles = failedFiles;
        this.failedFile = null;
    }

    public FileBackupException(String message, String failedFile) {
        super(message);
        this.failedFile = failedFile;
        this.failedFiles = null;
    }
}
