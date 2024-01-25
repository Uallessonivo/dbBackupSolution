package com.project.dbbackupsolution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DbBackupSolutionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbBackupSolutionApplication.class, args);
	}
 
}
