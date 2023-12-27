package com.project.dbbackupsolution.configuration;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoadPaths {
    private final String BACKUP_PATH;
    private final String RESTORE_PATH;

    public LoadPaths() {
        BACKUP_PATH = System.getenv("SERVER_STORAGE_BACKUP_PATH");
        RESTORE_PATH = System.getenv("SERVER_STORAGE_RESTORE_PATH");
    }
}
