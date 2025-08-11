package com.database_backup.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.database_backup.entity.customer.CustomerEntity;
import com.database_backup.repository.CustomerRepository;
import com.database_backup.util.GoogleDriveUtil;

@Component
public class DatabaseBackUpScheduler {

	@Autowired
	CustomerRepository customerRepository;

	private static final Logger logger = LoggerFactory.getLogger(DatabaseBackUpScheduler.class);

	@Scheduled(cron = "0 0 9,13,17,22 ? * MON-SAT")
	public void sendStatusUpdate() throws Exception {
		logger.debug("DatabaseBackUpScheduler :: sendStatusUpdate :: Entered");

		List<CustomerEntity> customers = customerRepository.getAllActiveCustomers();
		for (CustomerEntity customer : customers) {
			String dbName = "hurecom_" + customer.getTenant();
			String dbUser = "root";
			String dbPass = "root";
			String backupPath = "C:\\AutoBackUp\\";
			String backupFileName = dbName + "_" + LocalDate.now() + ".sql";
			String zipFileName = dbName + "_" + LocalDate.now() + ".zip";

			String sqlPath = backupPath + backupFileName;
			String zipPath = backupPath + zipFileName;

			try {
				ProcessBuilder builder = new ProcessBuilder(
						"C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump", "-u" + dbUser, "-p" + dbPass,
						dbName, "--result-file=" + backupPath + backupFileName);
				builder.start().waitFor();

				// Compress backup file
				zipDirectory(sqlPath, zipPath);

				GoogleDriveUtil.uploadFileToDrive(zipPath, zipFileName);
				logger.debug("Backup File sent successfully for tenant :" + customer.getTenant());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

//	 Method to zip a directory
	public static void zipDirectory(String sourcePath, String zipPath) throws IOException {
		logger.debug("DatabaseBackUpScheduler :: zipDirectory :: Entered");

		File file = new File(sourcePath);
		try (FileOutputStream fos = new FileOutputStream(zipPath); ZipOutputStream zos = new ZipOutputStream(fos)) {
			// for (File file : sourceFolder.listFiles()) {
			if (file.isFile()) {
				ZipEntry zipEntry = new ZipEntry(file.getName());
				zos.putNextEntry(zipEntry);
				try (FileInputStream fis = new FileInputStream(file)) {
					byte[] buffer = new byte[1024];
					int length;
					while ((length = fis.read(buffer)) > 0) {
						zos.write(buffer, 0, length);
					}
				}
				zos.closeEntry();
			}
			// }
		}
		logger.debug("DatabaseBackUpScheduler :: zipDirectory :: Exited");
	}

//	@Scheduled(cron = "* */2 * * * *")
//	public void deleteFiles() {
//		try {
//			GoogleDriveUtil.deleteOldFiles();
//		} catch (Exception e) {
//			logger.error("DatabaseBackUpScheduler :: deleteFiles ::" + e.getMessage());
//		}
//	}

}
