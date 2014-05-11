
package Main;

/**
 *
 * @author harsh
 * 
 * <p>This project would work like as library in future so this class is just for testing this library feature</p>
 */
/**
 *
 * @Include File
 */
import java.io.IOException;
import java.util.List;

import com.gdrive.desktop.client.FileOperation.CopyCommand;
import com.gdrive.desktop.client.FileOperation.UploadCommand;
import com.gdrive.desktop.client.FileOperation.gCommand;
import com.gdrive.desktop.client.FileOperation.UploadCommand.UploadOperation;
import com.gdrive.desktop.client.Global.SharedInstances;
import com.gdrive.desktop.client.cache.gDriveFile;
import com.gdrive.desktop.client.cache.gDriveFileRevisions;
import com.gdrive.desktop.client.cache.gDriveFiles;
import com.google.api.client.googleapis.media.MediaHttpUploader.UploadState;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Revision;



public class MainDriveCommand {


    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        
       // assert(args.length == 3 || !args[0].isEmpty() || !args[1].isEmpty() || !args[2].isEmpty()):"Invalid argument";
        
//        SharedInstances.CLIENT_ID = args[0];
//        SharedInstances.CLIENT_SECRET = args[1];
//        SharedInstances.APPLICATION_NAME = args[2];
        
        SharedInstances.CLIENT_ID = "882577327525.apps.googleusercontent.com";
        SharedInstances.CLIENT_SECRET = "06DR_ebCYtPCpGo4RhCjD4_2";
        SharedInstances.APPLICATION_NAME = "First_UI _testing";
  
        
        if (SharedInstances.setUpGDrive()){
        	System.out.println(SharedInstances.MY_RESOURCE.getString("SUCCESS_AUTHORIZATION_MSSG"));
        }
        System.out.println("***************************************************");
       
        
        //sample code
        /* 
         * Uploading file in root directory of drive
         */
//        gDriveFile fileMetadata = new gDriveFile("F:/CheckGDrive/check.txt", null);
//        gCommand gCmd = new UploadCommand(fileMetadata, UploadOperation.NEW_UPLOAD);
//        gCmd.DoExcute();
        
        /*
         * uploading folder in root directory of drive
         */
//        gDriveFile fileMetadata1 = new gDriveFile("folder34", null);
//        gCommand gCmd1 = new UploadCommand(fileMetadata1, UploadOperation.NEW_UPLOAD);
//        gCmd1.DoExcute();
        
        
        /* 
         * Updating file 
         */
//        File searchFile = gDriveFiles.searchFileID("0Bx5XsDaBgWSKV1pEdnpVdnVFZ3M", false, false);
//        searchFile.setTitle("updatedFile");
//        gDriveFile fileMetadata = new gDriveFile("F:/CheckGDrive/check2.pdf", searchFile );
//        gCommand gCmd = new UploadCommand(fileMetadata, UploadOperation.NEW_REVISION);
//        gCmd.DoExcute();
        
        /*
         * testing 
         */
//        List<Revision>  fileRevisions = gDriveFileRevisions.searchFileRevisionsByID("0Bx5XsDaBgWSKV1pEdnpVdnVFZ3M", false, true);
//        for (Revision fileRevision: fileRevisions) {
//        	if (fileRevision.getOriginalFilename().equals("hgh.indd")) {
//        		fileRevision.setLastModifyingUserName("mai hun don");
//        		fileRevision.setOriginalFilename("dddfdsfgdfg");
//        		if (fileRevision.getPinned() == true ) {
//        			fileRevision.setPinned(false);
//        		}
//        		SharedInstances.DRIVE.revisions().patch("0Bx5XsDaBgWSKV1pEdnpVdnVFZ3M", fileRevision.getId(), fileRevision).execute();
//        	}
//        }
//        try {
//            // First retrieve the revision from the API.
//            Revision revision = service.revisions().get(
//                fileId, revisionId).execute();
//            revision.setPinned(true);
//            return service.revisions().update(
//                fileId, revisionId, revision).execute();
//          } catch (IOException e) {
//            System.out.println("An error occurred: " + e);
//          }
        
        /*
         * for uploading file/folder in any directory add parent id in second parameter of upload command
         */
        
        /*
         * Copy file/Folder with different name
         */
//       	gCommand gCmd1 = new CopyCommand("0Bx5XsDaBgWSKV1pEdnpVdnVFZ3M", new File().setTitle("Copy of blahh"));
//      	gCmd1.DoExcute();
        
        /*
         * Copy file/Folder with same name
         */
//       	gCommand gCmd1 = new CopyCommand("0Bx5XsDaBgWSKV1pEdnpVdnVFZ3M", null);
//      	gCmd1.DoExcute();
        
        /*
         * get user information
         */
//       System.out.println("USER_NAME = " + SharedInstances.ABOUT.getName());
//       System.out.println("USER = " + SharedInstances.ABOUT.getUser());
//       System.out.println("ROOT_FOLDER_ID = " + SharedInstances.ABOUT.getRootFolderId());
//       System.out.println("USED_MEMORY = " + SharedInstances.ABOUT.getQuotaBytesUsed()/1000000 + "MB");
        
        
        
        
        
        
        
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
