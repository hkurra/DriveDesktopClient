/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Global;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
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
}
