package com.project.dbbackupsolution.configuration;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoadPaths {
    @Value("${server.storage.backup-path}")
    private String BACKUP_PATH;
    @Value("${server.storage.restore-path}")
    private String RESTORE_PATH;
}
