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

import Authorization.*;
import FileOperation.UploadCommand;
import FileOperation.gCommand;
import Global.SharedInstances;
import com.google.api.client.auth.oauth2.Credential;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

public class MainDriveCommand {


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
        gCommand gCmd = new UploadCommand("F:/ghk/reaction.txt");
        gCmd.DoExcute();
//        do{
//        	System.out.println("Command Availabele are");
//        	
//        	
//        	  JSONArray a = (JSONArray) parser.parse(new FileReader("resources/client_Secret"));
//
//        	  for (Object o : a)
//        	  {
//        	    JSONObject person = (JSONObject) o;
//
//        	    String name = (String) person.get("name");
//        	    System.out.println(name);
//
//        	    String city = (String) person.get("city");
//        	    System.out.println(city);
//
//        	    String job = (String) person.get("job");
//        	    System.out.println(job);
//
//        	    JSONArray cars = (JSONArray) jsonObject.get("cars");
//
//        	    for (Object c : cars)
//        	    {
//        	      System.out.println(c+"");
//        	    }
//        	  }
//        	
//        	
//        }while(true);
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
