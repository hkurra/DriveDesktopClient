/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdrive.desktop.client.FileOperation;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.nio.file.Files;



import com.gdrive.desktop.client.Global.SharedInstances;
import com.gdrive.desktop.client.cache.gDriveFile;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;


/**
 * 
 * @author harsh
 */


public class UploadCommand extends gCommand {

	
	public static enum UploadOperation {
	    NEW_REVISION,
	    PATCH,
	    TOUCH,
	    NEW_UPLOAD
	};
	/**
	 * parent directory id where given file will uploaded  
	 */
	private String mParentID = null;
	
	/**
	 * MIME type of file to be upload
	 * no need to set as this library automatically get this from file 
	 */
	private String mMimeType = null; 
	
	/**
	 *  description for file  
	 */
	private String mDescription = null; 
	
	/**
	 *  title of file 
	 */
	private String mTitle = null; 
		
	/**
	 * delete file after uploading 
	 */
	private boolean mDeleteFile = false;

	/**
	 * uploading file is directory
	 */
	private boolean mFolder = true;
	
	/**
	 * local variant of Drive file class (for setting metadata)
	 */
	private gDriveFile mGDriveFile = null;
	
	/**
	 * represent local disk file 
	 */
	private java.io.File mLocalDiskFile;
	
	/**
	 * metadata of file uploaded by this command
	 */
	private File mUploadedFile;
	
	/**
	 * Weather to update some file 
	 */
	private UploadOperation mUploadOperation;

	
	/**
	 * constructor for uploading file/folder at root of drive
	 * 
	 * @param gDrivefile
	 */
	public UploadCommand(gDriveFile gDrivefile, UploadOperation isUpdatefile) {
			mUploadOperation = isUpdatefile;
			Init(gDrivefile);	
	}
	/**
	 * constructor for uploading file in specified parent directory
	 * 
	 * @param gDrivefile
	 * @param parentID
	 */
	public UploadCommand(gDriveFile gDrivefile, String parentID) {
		Init(gDrivefile);
		mParentID = parentID;
		mUploadOperation = UploadOperation.NEW_UPLOAD;
	}
	
	/**
	 * Initialize Upload Command
	 * @param gDrivefile
	 */
	private void Init(gDriveFile gDrivefile) {
		mGDriveFile = gDrivefile;
		mDeleteFile = mGDriveFile.getDelateFile();
		mFolder = mGDriveFile.isFolder();
		mMimeType = mGDriveFile.getDFile().getMimeType();
		mTitle = mGDriveFile.getDFile().getTitle();
		mDescription = mGDriveFile.getDFile().getDescription();
		mLocalDiskFile = new java.io.File(mGDriveFile.getFilePath());
	}
	
	
	public int Execute() throws Exception {
	   try {
		   //assert(mGDriveFile == null):"illigal file path";
		    File fileMetadata = new File();
		    // File's metadata.
		    fileMetadata.setTitle(mTitle);
		    fileMetadata.setDescription(mDescription);
		    fileMetadata.setMimeType(mMimeType);
		    // Set the parent folder.
		    if (mParentID != null && mParentID.length() > 0) {
		    	fileMetadata.setParents(Arrays.asList(new ParentReference().setId(mParentID)));
		    }
		   	FileContent fileContent = new  FileContent(mMimeType, mLocalDiskFile);
		    
		   	switch(mUploadOperation) {
		   	case NEW_REVISION: 
		   		mUploadedFile = SharedInstances.DRIVE.files().update(mGDriveFile.getDFile().getId(), fileMetadata, fileContent).execute();
		   		break;
		   	
		   	case NEW_UPLOAD:
		   		Drive.Files.Insert insert = mFolder ? SharedInstances.DRIVE.files().insert(fileMetadata) : SharedInstances.DRIVE.files().insert(fileMetadata, fileContent);
			    //MediaHttpUploader uploader = insert.getMediaHttpUploader();
			    mUploadedFile = insert.execute();
			    break;
			    
		   	case PATCH:
		   		mUploadedFile = SharedInstances.DRIVE.files().patch(mGDriveFile.getDFile().getId(), fileMetadata).execute();
		   		break;
		   		
		   	case TOUCH:
		   		mUploadedFile = SharedInstances.DRIVE.files().touch(mGDriveFile.getDFile().getId()).execute();
		   		break;
		   	
		   	}
		    //uploader.setDirectUploadEnabled(useDirectUpload);
		    //uploader.setProgressListener(new FileUploadProgressListener());
	   }
	   catch (FileNotFoundException e) {
		   e.printStackTrace();
		   throw e;
	   }
	   catch (IOException e) {
		   System.out.println(SharedInstances.MY_RESOURCE.getString("I/O_ERROR"));
		   e.printStackTrace();
		   throw e;
	   }
	   catch (Exception ex) {
		   throw ex;
	   }
    return 0;
   }

	public int PreExecute() {

		if (mLocalDiskFile.isDirectory() || mFolder) {
			mMimeType = SharedInstances.FOLDER_MIME_TYPE;
			mFolder = true;
		}
		//determine MIME type
		
		//not set by you 
		if (mMimeType == null) {
			try {
				mMimeType = Files.probeContentType(((java.io.File)mLocalDiskFile).toPath());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			//not determine by us
			if (mMimeType == null) {
				mMimeType = SharedInstances.BINARY_FILE_MIME_TYPE;
			}
		}
		
		//Determine title of file/folder
		if (mTitle == null) {
			mTitle = mLocalDiskFile.getName();
		}
	 
		return 0;
	}

	public int PostExecute() {
		
		System.out.print("file "+mUploadedFile.getTitle()+" with id \""+mUploadedFile.getId()+"\" is susseccfully uploded");
		if (mDeleteFile) {
			try {
				mLocalDiskFile.delete();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}

	public Boolean IsExecutable() {
		boolean isExecutable = false;
		if (UploadOperation.NEW_REVISION == mUploadOperation) {
			String fileID = mGDriveFile.getDFile().getId();
			if (fileID != null) {
				isExecutable = !fileID.isEmpty();
			}
		}
		return isExecutable;
	}
}
