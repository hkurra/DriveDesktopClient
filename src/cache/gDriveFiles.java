package cache;
/**
 * maintain caching of files of User drive 
 * 
 * @author harsh
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Global.SharedInstances;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ParentReference;

public class gDriveFiles {
	/**
	 * cache copy of drive files 
	 */
	private static List<File> mAllFiles;
	/**
	 * directory structure of Files of Drive
	 */
	private static List<HashMap<String, Object>> mDirectoryStructure;
	
	/**
	 * static block
	 */
	static 
	{
		setAllFiles(new ArrayList<File>());	
		setDirectoryStructure(new ArrayList<HashMap<String, Object>>());
	}
	/**
	 * do caching of drive fileS 
	 * Must Call this method before any file operation and validate current cache 
	 */
	public static void CacheAllFiles() {
		try {
			Drive.Files.List list = SharedInstances.mDrive.files().list();
			 FileList fileList = list.execute();
			 getAllFiles().addAll(fileList.getItems());
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			//(TODO need testing)in case of failure or refresh we have to clear this structure otherwise it lead to wrong information 
			//so its your responsibility to cache old copy of structure in case of failure of refreshing
			//this caching always maintain fresh structure until you refresh 
			getAllFiles().clear();
			getDirectoryStructure().clear();
		}
		CreateDirectoryStructure();
	}
	
	/**
	 * replicate and cache google Drive current directory structure 
	 * must call this to refresh UI
	 */
	//TODO 9/feb/14  write now i know only this way to implement may be some API already there 
	//TODO NEED TO HANDLE CASE OF FILE UNDER MULTIPLE FOLDER AND FILE UNDER TRASH FLDER
	private static void CreateDirectoryStructure() {
		for (int index = 0;index < getAllFiles().size(); index++) {
			List<ParentReference> parentList = getAllFiles().get(index).getParents();
			
			//if it is file 
			if (getAllFiles().get(index).getFileExtension() != null) {
				//if it is a file under default(MyDrive) directory
				if (parentList.get(0).getIsRoot()) {
					getDirectoryStructure().add(FileProcessing(getAllFiles().get(index)));
				}
			}
			//if it is folder
			else if (getAllFiles().get(index).getFileExtension() == null) {
				HashMap<String, Object> folderDict = FolderProcessing(getAllFiles().get(index));
				getDirectoryStructure().add(folderDict);
			}
		}
	}

	/**
	 * DO folder Processing imply find out its child hierarchy and return as map structure
	 * 
	 * @param driveFileRef
	 * @return
	 */
	/**
	 * @param driveFileRef
	 * @return
	 */
	private static HashMap<String, Object> FolderProcessing(File driveFileRef) {
		
		HashMap<String, Object> fileDict = new HashMap<String, Object>();
		fileDict.put("SELF", driveFileRef);
		fileDict.put("FILE_ID", driveFileRef.getId());
		fileDict.put("IS_FOLDER", new Boolean(true));
		
		
		List<HashMap<String, Object>> childList = new ArrayList<HashMap<String, Object>>();
	    Drive.Children.List request = null;
		try {
			request = SharedInstances.mDrive.children().list(driveFileRef.getId());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    do {
	      try {
	    	  ChildList children = request.execute();
	        for (ChildReference child : children.getItems()) {
	          System.out.println("File Id: " + child.getId());
	          //TODO its a costlier call so replace it with something else because at this point we already have this child reference in allfilereference 
	          File childFileRef = SharedInstances.mDrive.files().get(child.getId()).execute();
	          
	          //if it is file
	          if (childFileRef.getFileExtension() != null) {
	        	  childList.add(FileProcessing(childFileRef)) ;
	          }
	          
	          //if it is folder
	          else if (childFileRef.getFileExtension() == null) {
	        	  childList.add(FolderProcessing(childFileRef)) ;
	          }
	        }
	        request.setPageToken(children.getNextPageToken());
	      } catch (IOException e) {
	        System.out.println("An error occurred: " + e);
	        request.setPageToken(null);
	      }
	    } while (request.getPageToken() != null &&
	             request.getPageToken().length() > 0);
	    
	    if (childList.size() == 0) childList = null;
	    
	    fileDict.put("CHILD", childList);
		return fileDict;
	}
	
	
	/**
	 * Do file Processing 
	 * @param driveFileRef
	 * @return HashMap<String, Object>
	 */
	private static HashMap<String, Object> FileProcessing(File driveFileRef) {
		
		HashMap<String, Object> fileDict = new HashMap<String, Object>();
		fileDict.put("SELF", driveFileRef);
		fileDict.put("CHILD", null);
		fileDict.put("FILE_ID", driveFileRef.getId());
		fileDict.put("IS_FOLDER", new Boolean(false));
		
		return fileDict;
	}

	/**
	 * @param mAllFiles the mAllFiles to set
	 */
	public static void setAllFiles(List<File> mAllFiles) {
		gDriveFiles.mAllFiles = mAllFiles;
	}

	/**
	 * @return the mAllFiles
	 */
	public static List<File> getAllFiles() {
		return mAllFiles;
	}

	/**
	 * @param mDirectoryStructure the mDirectoryStructure to set
	 */
	public static void setDirectoryStructure(List<HashMap<String, Object>> mDirectoryStructure) {
		gDriveFiles.mDirectoryStructure = mDirectoryStructure;
	}

	/**
	 * @return the mDirectoryStructure
	 */
	public static List<HashMap<String, Object>> getDirectoryStructure() {
		return mDirectoryStructure;
	}

}
