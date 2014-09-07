/**
 * 
 */
package com.gdrive.desktop.client.FileOperation;



import com.gdrive.desktop.client.Global.ServiceManager;
import com.gdrive.desktop.client.Global.DriveDesktopClient;
import com.gdrive.desktop.client.Global.ResponderData.AfterFileDeleteResponderData;
import com.gdrive.desktop.client.cache.GDriveFiles;
import com.gdrive.desktop.client.cache.TreeNodeInfo;
import com.google.api.services.drive.model.File;

/**
 * @author harsh
 * 
 * <p>Command To delete/Trash File from Google drive</p>
 *
 */
public class DeleteCommand extends ICommand {

	{
		mCommandType = "DELETE";
	}
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
	
	private boolean mIsTrashed = false;
	
	private boolean mIsFolder;
	/**
	 * @param file
	 * @param trash
	 */
	public DeleteCommand(File file, Boolean trash) {
		mFile = file;
		mFileID = file.getId();
		mTrash = trash;
	}
	/**
	 * 
	 * @param fileID
	 * @param trash
	 */
	public DeleteCommand(String fileID, Boolean trash) {
		mFileID  = fileID;
		mFile = GDriveFiles.searchFileID(fileID, false, false);
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
		mFile = GDriveFiles.searchFileID(fileID, false, false);
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
		mIsFolder = (Boolean)GDriveFiles.getFileTreeNodeInfo(mFileID).get(GDriveFiles.IS_FOLDER_KEY);
		
		if (mTrash == null) {
			mTrash = false;
		}
		mIsTrashed = mFile.getExplicitlyTrashed() != null && mFile.getExplicitlyTrashed();
	
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
		do {
		try {
		if (mTrash && !mIsTrashed) {
			mResult = DriveDesktopClient.DRIVE.files().trash(mFileID).execute();
			TreeNodeInfo ParentNode = GDriveFiles.getFileParentTreeNodeInfo(mFileID);
			ParentNode.deleteChildren(GDriveFiles.getFileTreeNodeInfo(mFileID));
			GDriveFiles.getTrashedDirectoryStructure().add(mIsFolder ? 
					GDriveFiles.FolderProcessing(mFile, GDriveFiles.getTrashedRootNode()) : 
						GDriveFiles.FileProcessing(mFile, GDriveFiles.getTrashedRootNode()));
		}
		
		else if(mDeleteRevision) {
			mResult = DriveDesktopClient.DRIVE.revisions().delete(mFileID, mRevisionID).execute();
		}
		else if (!mTrash)  {
			mResult = DriveDesktopClient.DRIVE.files().delete(mFileID).execute();
			TreeNodeInfo ParentNode = GDriveFiles.getFileParentTreeNodeInfo(mFileID);
			ParentNode.deleteChildren(GDriveFiles.getFileTreeNodeInfo(mFileID));
		}
		}
		catch (Exception e) {
			throw e;
		}
		}while(false);
		return 0;
	}

	/* (non-Javadoc)
	 * @see FileOperation.gCommand#PostExecute()
	 */
	@Override
	protected int PostExecute() {
		
	    ServiceManager.ExecuteResponders(
	    	      ServiceManager.serviceType.AFTER_FILE_DELETE_SERVICE_ID, 
	    	      new AfterFileDeleteResponderData(this, this.mFile));
		
		return 0;
	}
}
