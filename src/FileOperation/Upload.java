/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FileOperation;

import Authorization.UserAutorization;
import Global.SharedInstances;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.drive.Drive;

/**
 *
 * @author harsh
 */
public class Upload
{

    UserAutorization userAutorization = new UserAutorization();
    Credential credential = userAutorization.authorize();
    Drive drive =
            new Drive.Builder(SharedInstances.httpTransport, SharedInstances.JSON_FACTORY, credential).setApplicationName(
            "jj").build();
}
