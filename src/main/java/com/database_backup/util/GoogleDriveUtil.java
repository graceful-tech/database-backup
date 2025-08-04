package com.database_backup.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleDriveUtil {

	private static final Logger logger = LoggerFactory.getLogger(GoogleDriveUtil.class);

	private static final String APPLICATION_NAME = "Drive Backup App";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private static Drive getDriveService() throws Exception {
		logger.debug("GoogleDriveUtil :: getDriveService :: Entered");

		// Load service account key
		InputStream in = GoogleDriveUtil.class.getClassLoader().getResourceAsStream("service-account-key.json");
		if (in == null) {
			throw new RuntimeException("service-account-key.json not found in resources folder");
		}

		GoogleCredential credential = GoogleCredential.fromStream(in)
				.createScoped(Collections.singleton(DriveScopes.DRIVE));

		logger.debug("GoogleDriveUtil :: getDriveService :: Exited");

		return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	public static void uploadFileToDrive(String filePath, String fileName) throws Exception {
		String folderId = "1WXul-mxVSy5jMJPZ51qZtiinfuSfWRDL";

		Drive service = getDriveService();

		File fileMetadata = new File();
		fileMetadata.setName(fileName);

		fileMetadata.setParents(Collections.singletonList(folderId));

		java.io.File filePathObj = new java.io.File(filePath);
		FileContent mediaContent = new FileContent("files/zip", filePathObj);

		File uploadedFile = service.files().create(fileMetadata, mediaContent).setSupportsAllDrives(true)
				.setFields("id").execute();

		logger.info("✅ File uploaded with ID: " + uploadedFile.getId());
	}
}
