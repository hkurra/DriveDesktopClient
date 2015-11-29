package com.gdrive.desktop.client.Global;

import com.gdrive.desktop.client.Authorization.UserAutorization;
import com.gdrive.desktop.client.cache.GDriveFileRevisions;
import com.gdrive.desktop.client.cache.GDriveFiles;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry Pass to use this Library
 * 
 * @see DriveDesktopClient.setUpGDrive()
 * 
 * @author harsh
 */
public class DriveDesktopClient {

	/**
	 * client ID of your project, if not available than obtained on from google
	 * developer console
	 */
	public static String CLIENT_ID;

	/**
	 * client secret of your project, if not available than obtained on from
	 * google developer console
	 */
	public static String CLIENT_SECRET;

	/**
	 * your Application name
	 */
	public static String APPLICATION_NAME;

	/**
	 * NA
	 */
	public static String REDIRECT_URI;

	/**
	 * yet not implemented proxy related setting
	 */
	public static String ProxyHostname;
	public static String ProxyPost;
	public static String Password;
	public static String UserName;

	/**
	 * Information about the current user along with Drive API settings
	 */
	public static About ABOUT;

	/**
	 * Global instance of resource bundle
	 */
	public static final ResourceBundle MY_RESOURCE;

	/**
	 * Global instance of directory path which store user credential
	 */
	public static final java.io.File DATA_STORE_DIR;

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to
	 * make it a single globally shared instance across your application.
	 */
	public static FileDataStoreFactory DATA_STORE_FACOTRY;

	/**
	 * Global instance of the HTTP transport.
	 */
	public static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the JSON factory.
	 */
	public static final JsonFactory JSON_FACTORY;

	/**
	 * Global instance of the UserCredential on which you are operating
	 */
	public static Credential CREDENTIAL;

	/**
	 * Global instance of the User_Authorization.
	 */
	private static UserAutorization USER_AUTHORIZATION;

	/**
	 * Global instance of the Google Drive on which you are operating
	 */
	public static Drive DRIVE;

	/**
	 * Scope for which you want Authorization default Drive scope is there in
	 * list
	 */
	public static List<String> SCOPES;
	

	/**
	 * Scope for which you want Authorization default Drive scope is there in
	 * list
	 */
	public static Boolean NEED_MEMORY_CACHING = true;


	/**
	 * folder MIME type string constant
	 */
	public static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";

	/**
	 * document MIME type string constant **google docs**
	 */
	public static final String DOCUMENT_MIME_TYPE = "application/vnd.google-apps.document";

	/**
	 * audio MIME type string constant
	 */
	public static final String AUDIO_MIME_TYPE = "application/vnd.google-apps.audio";

	/**
	 * folder MIME type string constant **google drawing**
	 */
	public static final String DRAWING_MIME_TYPE = "application/vnd.google-apps.drawing";

	/**
	 * folder MIME type string constant **google file**
	 */
	public static final String FILE_MIME_TYPE = "application/vnd.google-apps.file";

	/**
	 * form MIME type string constant **google form**
	 */
	public static final String FORM_MIME_TYPE = "application/vnd.google-apps.form";

	/**
	 * photo MIME type string constant
	 */
	public static final String PHOTO_MIME_TYPE = "application/vnd.google-apps.photo";

	/**
	 * presentation MIME type string constant **Google Slides**
	 */
	public static final String PRESENTATION_MIME_TYPE = "application/vnd.google-apps.presentation";

	/**
	 * unknown MIME type string constant
	 */
	public static final String UNKNOWN_MIME_TYPE = "application/vnd.google-apps.unknown";

	/**
	 * video MIME type string constant
	 */
	public static final String VIDEO_MIME_TYPE = "application/vnd.google-apps.video";

	/**
	 * spreadsheet MIME type string constant **google sheet**
	 */
	public static final String SPREADSHEET_MIME_TYPE = "application/vnd.google-apps.spreadsheet";

	/**
	 * A MIME attachment with the content type "application/octet-stream" is a
	 * binary file. Typically, it will be an application or a document that must
	 * be opened in an particular application
	 */
	public static final String BINARY_FILE_MIME_TYPE = "application/octet-stream";
	
	/**
	 * Google App script type
	 */
	public static final String GOOGLE_APP_SCRIPT = "application/vnd.google-apps.script";

	static {
		REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
		MY_RESOURCE = java.util.ResourceBundle.getBundle("Bundle");
		JSON_FACTORY = JacksonFactory.getDefaultInstance();
		DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
				MY_RESOURCE.getString("USER_PATH"));
		try {
			DATA_STORE_FACOTRY = new FileDataStoreFactory(DATA_STORE_DIR);
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (GeneralSecurityException ex) {
			Logger.getLogger(DriveDesktopClient.class.getName()).log(
					Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(DriveDesktopClient.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		SCOPES = new ArrayList<String>();
		SCOPES.add(DriveScopes.DRIVE);
	}

	/**
	 * <p>
	 * first step to set up this library for accessing google drive
	 * </p>
	 * <p>
	 * call it just after setting your Client code and secret code on
	 * SharedInstance
	 * </p>
	 * <p>
	 * eg: DriveDesktopClient.CLIENT_ID = "YOUR CLIENT ID";
	 * DriveDesktopClient.CLIENT_SECRET = "YOUR CLIENT SECRET";
	 * DriveDesktopClient.APPLICATION_NAME = "YOUR APPLICATION NAME"; if
	 * (DriveDesktopClient.setUpGDrive()){ //Set Up your Application }
	 * </p>
	 * 
	 * @return
	 */
	public static Boolean setUpGDrive() {
		Boolean isGdriveSetUped = false;
		do {
			USER_AUTHORIZATION = new UserAutorization();
			CREDENTIAL = USER_AUTHORIZATION.authorize();
			DRIVE = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, CREDENTIAL)
					.setApplicationName(APPLICATION_NAME).build();
			isGdriveSetUped = (DRIVE != null);
			try {
				ABOUT = DRIVE.about().get().execute();					
				if (NEED_MEMORY_CACHING) {					
					GDriveFiles.CacheAllFiles();
					GDriveFileRevisions.cacheAllFileRevision();
				}
			} catch (TokenResponseException tokenException) {
				Logger.getLogger(DriveDesktopClient.class.getName()).log(
						Level.SEVERE, null, tokenException);
				isGdriveSetUped = false;
				break;
				// TODO Should retry after it
			} catch (IOException e) {
				isGdriveSetUped = false;
				Logger.getLogger(DriveDesktopClient.class.getName()).log(
						Level.SEVERE, null, e);
				break;
			} catch (Exception e) {
				isGdriveSetUped = false;
				Logger.getLogger(DriveDesktopClient.class.getName()).log(
						Level.SEVERE, null, e);
				break;
			}
		} while (false);
		return isGdriveSetUped;
	}

	/**
	 * <p>
	 * use to change user
	 * </p>
	 * TODO currently not working
	 */
	public static void changeUser() {
		try {
			DATA_STORE_DIR.delete();
			setUpGDrive();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
