package com.database_backup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatabaseBackupApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatabaseBackupApplication.class, args);
	}

}
