/**
 * Copy file into drive
 * TODO check MIME TYPE and extension of new file 
 * 
 */
package com.gdrive.desktop.client.FileOperation;

import java.io.IOException;



import com.gdrive.desktop.client.Global.DriveDesktopClient;
import com.gdrive.desktop.client.cache.GDriveFiles;
import com.google.api.services.drive.model.File;

/**
 * @author harsh
 *
 */
public class CopyCommand extends ICommand {

	/**
	 * file ID of file to be copied
	 */
	private String mOrigionFileId;
	
	/**
	 * Drive file (for setting metadata)
	 */
	private File mCopiedFile;
	
	/**
	 * @param originFileId
	 */
	public CopyCommand(String originFileId, File driveFile)
	{
		mOrigionFileId = originFileId;
		mCopiedFile = driveFile;
	}
	/* (non-Javadoc)
	 * @see FileOperation.gCommand#IsExecutable()
	 */
	@Override
	public Boolean IsExecutable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see FileOperation.gCommand#PreExecute()
	 */
	@Override
	protected int PreExecute() {
		
		String defaultTitle =  GDriveFiles.searchFileID(mOrigionFileId, false, false).getTitle();
	
		if (mCopiedFile == null) {
			mCopiedFile = new File();
			mCopiedFile.setTitle(defaultTitle);
		}
		else if (mCopiedFile.getTitle() == null) {
			mCopiedFile.setTitle(defaultTitle);
		}
		
		else if(mCopiedFile.getTitle() != null) {
			//TODO Append file extension in this case if it is not there  
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see FileOperation.gCommand#Execute()
	 */
	@Override
	protected int Execute() throws Exception {
	    try {
	      Result = DriveDesktopClient.DRIVE.files().copy(mOrigionFileId, mCopiedFile).execute();
	    } 
	    catch (IOException e) {
	      System.out.println( DriveDesktopClient.MY_RESOURCE.getString("GENERELIZED_ERROR")+ e);
	      throw e;
	    }
		return 0;
	}

	/* 
	 * @see FileOperation.gCommand#PostExecute()
	 */
	@Override
	protected int PostExecute() {
		return 0;
	}

}
