/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

/**
 *
 * @author harsh
 */
/**
 *
 * @Include File
 */
import java.io.IOException;

import Authorization.*;
import Global.SharedInstances;
import com.google.api.client.auth.oauth2.Credential;

public class MainDriveCommand {


    public static void main(String[] args) throws IOException {
        
        if (args.length < 3 || args[0].isEmpty() || args[1].isEmpty() || args[2].isEmpty()) {
            System.out.println("Invalid argument");
            System.exit(1);
        }
        SharedInstances.CLIENT_ID = args[0];
        SharedInstances.CLIENT_SECRET = args[1];
        SharedInstances.APPLICATION_NAME = args[2];
  
        UserAutorization userAutorization = new UserAutorization();
        Credential credential = userAutorization.authorize();
        /*HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();

        String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        System.out.println("Please open the following URL in your browser then type the authorization code:");
        System.out.println("  " + url);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();

        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
        GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);

        //Create a new authorized API client
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();

        //Insert a file  
        File body = new File();
        body.setTitle("My document");
        body.setDescription("A test document");
        body.setMimeType("text/plain");

        java.io.File fileContent = new java.io.File("douc7ument.txt");
        FileContent mediaContent = new FileContent("text/plain", fileContent);

        File file = service.files().insert(body, mediaContent).execute();
        System.out.println("File ID: " + file.getId()); */
    }
}
