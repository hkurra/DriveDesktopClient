package cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import Global.SharedInstances;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Revision;
import com.google.api.services.drive.model.RevisionList;

public class gDriveFileRevisions {
	
	private HashMap<String, List<Revision>> mAllFileRevision;
	
	public void cacheAllFileRevision() {
		List<File> allFile =  gDriveFiles.getAllFiles();
		mAllFileRevision.clear();
		try {
			for (File file : allFile) {
				
				mAllFileRevision.put(file.getId(), getFileRevision(file.getId()));
			}
		}
		catch(Exception e) {
			mAllFileRevision.clear();
		}
	}
	
	private List<Revision> getFileRevision(String fileID)throws Exception {
		
		  try {
	      RevisionList revisions = SharedInstances.DRIVE.revisions().list(fileID).execute();
	      return revisions.getItems();
	    } 
		  catch (IOException e) {
	      throw e;
	    }
	}
	
	public void updateFileRevision(String fileID) {
		
		try {
			mAllFileRevision.put(fileID, getFileRevision(fileID));
		}
		catch (Exception e) {
			
		}
	}

}

