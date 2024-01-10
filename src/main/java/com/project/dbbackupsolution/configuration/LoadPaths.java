package com.project.dbbackupsolution.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoadPaths {
    private final String BACKUP_PATH;
    private final String RESTORE_PATH;

    public LoadPaths() {
        Dotenv dotenv = Dotenv.load();
        BACKUP_PATH = dotenv.get("SERVER_STORAGE_BACKUP_PATH");
        RESTORE_PATH = dotenv.get("SERVER_STORAGE_RESTORE_PATH");

        if (BACKUP_PATH == null || BACKUP_PATH.isEmpty()) {
            throw new RuntimeException("BACKUP_PATH not found in .env file");
        }

        if (RESTORE_PATH == null || RESTORE_PATH.isEmpty()) {
            throw new RuntimeException("RESTORE_PATH not found in .env file");
        }
    }
}
