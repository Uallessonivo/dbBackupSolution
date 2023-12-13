package com.project.dbbackupsolution.configuration;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class LoadPaths {
    @Value("${BACKUP_PATH}")
    private String BACKUP_PATH;
    @Value("${RESTORE_PATH}")
    private String RESTORE_PATH;
    @Value("${LOG_PATH}")
    private String LOG_PATH;
}
