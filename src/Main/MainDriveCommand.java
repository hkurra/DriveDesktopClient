/*
 *This project would work like as library in future so this class is just for testing this library feature
 *
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

import cache.gDriveFile;
import FileOperation.UploadCommand;
import FileOperation.gCommand;
import Global.SharedInstances;


public class MainDriveCommand {


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
        assert(args.length == 3 || !args[0].isEmpty() || !args[1].isEmpty() || !args[2].isEmpty()):"Invalid argument";
        
        SharedInstances.CLIENT_ID = args[0];
        SharedInstances.CLIENT_SECRET = args[1];
        SharedInstances.APPLICATION_NAME = args[2];
  
        SharedInstances.setUpGDrive();
        if (SharedInstances.mCredential != null){
        	System.out.println(SharedInstances.myResources.getString("SUCCESS_AUTHORIZATION_MSSG"));
        }
        System.out.println("***************************************************");
       
        
        //sample code
        /* 
         * Uploading file in root directory of drive
         */
//        gDriveFile fileMetadata = new gDriveFile("F:/CheckGDrive/check.txt", null);
//        gCommand gCmd = new UploadCommand(fileMetadata);
//        gCmd.DoExcute();
        
        /*
         * uploading folder in root directory of drive
         */
//        gDriveFile fileMetadata1 = new gDriveFile("folder34", null);
//        gCommand gCmd1 = new UploadCommand(fileMetadata1);
//        gCmd1.DoExcute();
        
        /*
         * for uploading file/folder in any directory add parent id in second parameter of upload command
         */
        
       
        
        
        
        
        
        
        
        
        
        
        
        
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
