/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Global;

import Authorization.UserAutorization;

import cache.gDriveFiles;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsh
 */
public class SharedInstances
{

    /**
     *
     */
    public static String CLIENT_ID;
    /**
     *
     */
    public static String CLIENT_SECRET;
    /**
     *
     */
    public static String APPLICATION_NAME;
    /**
     *
     */
    public static String REDIRECT_URI;
    /**
     * Global instance of resource bundle
     */
    public static final ResourceBundle myResources;
    /**
     * Global instance of directory path which store user credential
     */
    public static final java.io.File DATA_STORE_DIR;
    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to
     * make it a single globally shared instance across your application.
     */
    public static FileDataStoreFactory dataStoreFactory;
    /**
     * Global instance of the HTTP transport.
     */
    public static HttpTransport httpTransport;
    /**
     * Global instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY;
    
    /**
     * Global instance of the UserCredential on which you are operating
     */
	public static Credential mCredential;
	
    /**
     * Global instance of the User_Authorization.
     */
	private static UserAutorization mUserAutorization;
    /**
     * Global instance of the Google Drive on which you are operating
     */
	public static Drive mDrive;

	/**
	 * folder MIME type string constant
	 */
	public static final String folderMIMEtype = "application/vnd.google-apps.folder";
	
	/**
	 * document MIME type string constant **google docs**
	 */
	public static final String documentMIMEtype = "application/vnd.google-apps.document";
	
	/**
	 * audio MIME type string constant
	 */
	public static final String audioMIMEtype = "application/vnd.google-apps.audio";
	/**
	 * folder MIME type string constant **google drawing**
	 */
	public static final String drawingMIMEtype = "application/vnd.google-apps.drawing";
	/**
	 * folder MIME type string constant **google file**
	 */
	public static final String filerMIMEtype = "application/vnd.google-apps.file";
	/**
	 * form MIME type string constant **google form**
	 */
	public static final String formMIMEtype = "application/vnd.google-apps.form";
	/**
	 * photo MIME type string constant
	 */
	public static final String photoMIMEtype = "application/vnd.google-apps.photo";
	/**
	 * presentation MIME type string constant   **Google Slides**
	 */
	public static final String presentationMIMEtype = "application/vnd.google-apps.presentation";
	/**
	 * unknown MIME type string constant
	 */
	public static final String unknownMIMEtype = "application/vnd.google-apps.unknown";
	/**
	 * video MIME type string constant
	 */
	public static final String videoMIMEtype = "application/vnd.google-apps.video";
	/**
	 * spreadsheet MIME type string constant **google sheet**
	 */
	public static final String spreadSheetMIMEtype = "application/vnd.google-apps.spreadsheet";
	
	
    static {
        REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
        myResources = java.util.ResourceBundle.getBundle("resources/Bundle");
        JSON_FACTORY = JacksonFactory.getDefaultInstance();
        DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/drive_sample");
        try {
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(SharedInstances.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SharedInstances.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   public static void setUpGDrive() {
		mUserAutorization = new UserAutorization();
		mCredential = mUserAutorization.authorize();
		mDrive = new Drive.Builder(httpTransport, JSON_FACTORY, mCredential).setApplicationName(APPLICATION_NAME).build();
		gDriveFiles.CacheAllFiles();
    }
    
    static void changeUser() {
    	DATA_STORE_DIR.delete();
    	setUpGDrive();
   }
    
}
