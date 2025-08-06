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

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleDriveUtil {

	private static final Logger logger = LoggerFactory.getLogger(GoogleDriveUtil.class);

	private static final String APPLICATION_NAME = "Drive Backup App";
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String CREDENTIAL_LOCATION = "C:\\credentials\\service-account-key.json";

	private static Drive getDriveService() throws Exception {
		logger.debug("GoogleDriveUtil :: getDriveService :: Entered");

		GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(CREDENTIAL_LOCATION))
				.createScoped(Collections.singleton(DriveScopes.DRIVE));

		logger.debug("GoogleDriveUtil :: getDriveService :: Exited");

		return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	public static void uploadFileToDrive(String filePath, String fileName) {
		logger.debug("GoogleDriveUtil :: uploadFileToDrive :: Entered");
		String folderId = "1WXul-mxVSy5jMJPZ51qZtiinfuSfWRDL";

		try {
			Drive service = getDriveService();

			File fileMetadata = new File();
			fileMetadata.setName(fileName);
			fileMetadata.setParents(Collections.singletonList(folderId));

			java.io.File filePathObj = new java.io.File(filePath);
			FileContent mediaContent = new FileContent("files/zip", filePathObj);

			File uploadedFile = service.files().create(fileMetadata, mediaContent).setSupportsAllDrives(true)
					.setFields("id").execute();

			logger.debug("GoogleDriveUtil :: uploadFileToDrive :: Exited" + uploadedFile.getId());

		} catch (Exception e) {
			logger.error("GoogleDriveUtil :: uploadFileToDrive :: Error" + e);
		}
	}

}
