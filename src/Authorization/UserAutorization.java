/*
 * Used for Authenticate App ID and Authorize user Credential.
 */
package Authorization;

import Global.SharedInstances;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.drive.DriveScopes;
import java.io.IOException;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harsh
 */
public class UserAutorization
{

    /*
     * constructor 
     */
    public UserAutorization() {
    }

    /**
     *
     * @return
     */
    public Credential authorize() {

        // load client secrets(App Client ID & secret Code)

        Credential credential = null;



        try {
            // set up authorization code flow .for more info see documentation
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(SharedInstances.httpTransport, SharedInstances.JSON_FACTORY, SharedInstances.CLIENT_ID, SharedInstances.CLIENT_SECRET,
                    Collections.singleton(DriveScopes.DRIVE_FILE))
                    .setDataStoreFactory(SharedInstances.dataStoreFactory).build();

            // authorize user credential read from folder(set by us in user folder) else ask for authorization from user on browser and store its credential in local folder
            credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException ex) {
            Logger.getLogger(UserAutorization.class.getName()).log(Level.SEVERE, null, ex);
        }



        return credential;
    }
}
