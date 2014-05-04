/**
 * 
 */
package com.gdrive.desktop.client.FileOperation;


import com.gdrive.desktop.client.Global.SharedInstances;
import com.google.api.services.drive.model.File;

/**
 * @author harsh
 * 
 * <p>Command To delete/Trash File from Google drive</p>
 *
 */
public class DeleteCommand extends gCommand {

	/**
	 * Fileid of file to be delete
	 */
	private String mFileID = null;
	
	/**
	 * file to be delete
	 */
	private File mFile = null;
	
	/**
	 * Weather to trash file or not 
	 */
	private Boolean mTrash = null;
	/**
	 * Revision id of file to delete
	 */
	private String mRevisionID = null;
	
	private boolean mDeleteRevision = false;
	/**
	 * @param file
	 * @param trash
	 */
	public DeleteCommand(File file, Boolean trash) {
		mFile = file;
		mTrash = trash;
	}
	/**
	 * 
	 * @param fileID
	 * @param trash
	 */
	public DeleteCommand(String fileID, Boolean trash) {
		mFileID  = fileID;
		mTrash = trash;
	}
	
	/**
	 * Delete File Revision
	 * 
	 * @param fileID
	 * @param revisionID
	 */
	public DeleteCommand (String fileID, String revisionID) {
		mFileID = fileID;
		mRevisionID = revisionID;
	}
	/*
	 * @see FileOperation.gCommand#IsExecutable()
	 */
	@Override
	public Boolean IsExecutable() {
		boolean isExecutable = true;
		if (mFileID == null && mFile == null && mFile.getId() == null && (mRevisionID == null && mFileID == null)) {
			isExecutable = false; 
		}
		return isExecutable;
	}

	/* 
	 * @see FileOperation.gCommand#PreExecute()
	 */
	@Override
	protected int PreExecute() {
		if (mFileID == null && mFile != null) {
			mFileID = mFile.getId();	
		}
		if (mTrash == null) {
			mTrash = false;
		}
		if (mRevisionID != null) {
			mDeleteRevision = true;
		}
		return 0;
	}

	/* 
	 * @see FileOperation.gCommand#Execute()
	 */
	@Override
	protected int Execute() throws Exception {
		
		if (mTrash) {
			Result = SharedInstances.DRIVE.files().trash(mFileID).execute();
		}
		
		else if(mDeleteRevision) {
			Result = SharedInstances.DRIVE.revisions().delete(mFileID, mRevisionID).execute();
		}
		else  {
			Result = SharedInstances.DRIVE.files().delete(mFileID).execute();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see FileOperation.gCommand#PostExecute()
	 */
	@Override
	protected int PostExecute() {
		// TODO Auto-generated method stub
		return 0;
	}

}
