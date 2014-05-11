/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gdrive.desktop.client.FileOperation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.nio.file.Files;

import com.gdrive.desktop.client.Global.ServiceManager;
import com.gdrive.desktop.client.Global.SharedInstances;
import com.gdrive.desktop.client.Global.ResponderData.AfterFileUploadRespoderData;
import com.gdrive.desktop.client.Global.ResponderData.BeforeFileUploadResponderData;
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
		NEW_REVISION, PATCH, TOUCH, NEW_UPLOAD
	};

	/**
	 * parent directory id where given file will uploaded
	 */
	private String mParentID = null;

	/**
	 * MIME type of file to be upload no need to set as this library
	 * automatically get this from file
	 */
	private String mMimeType = null;

	/**
	 * description for file
	 */
	private String mDescription = null;

	/**
	 * title of file
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
		setUploadOperation(isUpdatefile);
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
		setUploadOperation(UploadOperation.NEW_UPLOAD);
	}

	/**
	 * Initialize Upload Command
	 * 
	 * @param gDrivefile
	 */
	private void Init(gDriveFile gDrivefile) {
		setGDriveFile(gDrivefile);
		setDeleteFile(getGDriveFile().getDelateFile());
		setFolder(getGDriveFile().isFolder());
		setMimeType(getGDriveFile().getDFile().getMimeType());
		setTitle(getGDriveFile().getDFile().getTitle());
		setFileDescription(getGDriveFile().getDFile().getDescription());
		setLocalDiskFile(new java.io.File(getGDriveFile().getFilePath()));
	}

	public int Execute() throws Exception {
		try {
			// assert(mGDriveFile == null):"illigal file path";
			File fileMetadata = new File();
			// File's metadata.
			fileMetadata.setTitle(getTitle());
			fileMetadata.setDescription(getFileDescription());
			fileMetadata.setMimeType(getMimeType());
			// Set the parent folder.
			if (mParentID != null && mParentID.length() > 0) {
				fileMetadata.setParents(Arrays.asList(new ParentReference()
						.setId(mParentID)));
			}
			FileContent fileContent = new FileContent(getMimeType(), getLocalDiskFile());

			switch (getUploadOperation()) {
			case NEW_REVISION:
				setUploadedFile(SharedInstances.DRIVE
						.files()
						.update(getGDriveFile().getDFile().getId(), fileMetadata,
								fileContent).execute());
				break;

			case NEW_UPLOAD:
				Drive.Files.Insert insert = isFolder() ? SharedInstances.DRIVE
						.files().insert(fileMetadata) : SharedInstances.DRIVE
						.files().insert(fileMetadata, fileContent);
				// MediaHttpUploader uploader = insert.getMediaHttpUploader();
				setUploadedFile(insert.execute());
				break;

			case PATCH:
				setUploadedFile(SharedInstances.DRIVE.files()
						.patch(getGDriveFile().getDFile().getId(), fileMetadata)
						.execute());
				break;

			case TOUCH:
				setUploadedFile(SharedInstances.DRIVE.files()
						.touch(getGDriveFile().getDFile().getId()).execute());
				break;

			}
			// uploader.setDirectUploadEnabled(useDirectUpload);
			// uploader.setProgressListener(new FileUploadProgressListener());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			System.out.println(SharedInstances.MY_RESOURCE
					.getString("I/O_ERROR"));
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			throw ex;
		}
		return 0;
	}

	public int PreExecute() {

		if (getLocalDiskFile().isDirectory() || isFolder()) {
			setMimeType(SharedInstances.FOLDER_MIME_TYPE);
			setFolder(true);
		}
		// determine MIME type

		// not set by you
		if (getMimeType() == null) {
			try {
				setMimeType(Files
						.probeContentType(((java.io.File) getLocalDiskFile())
								.toPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			// not determine by us
			if (getMimeType() == null) {
				setMimeType(SharedInstances.BINARY_FILE_MIME_TYPE);
			}
		}

		// Determine title of file/folder
		if (getTitle() == null) {
			setTitle(getLocalDiskFile().getName());
		}
		ServiceManager.ExecuteResponders(
				ServiceManager.serviceType.BEFORE_UPLOAD_SERVICE_ID,
				new BeforeFileUploadResponderData(this));
		return 0;
	}

	public int PostExecute() {

		ServiceManager.ExecuteResponders(
				ServiceManager.serviceType.AFTER_UPLOAD_SERVICE_ID,
				new AfterFileUploadRespoderData(this, mUploadedFile));
		if (isDeleteFile()) {
			try {
				getLocalDiskFile().delete();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}

	public Boolean IsExecutable() {
		boolean isExecutable = false;
		if (UploadOperation.NEW_REVISION == getUploadOperation()) {
			String fileID = getGDriveFile().getDFile().getId();
			if (fileID != null) {
				isExecutable = !fileID.isEmpty();
			}
		}
		return isExecutable;
	}

	/**
	 * @param mMimeType the mMimeType to set
	 */
	public void setMimeType(String mMimeType) {
		this.mMimeType = mMimeType;
	}

	/**
	 * @return the mMimeType
	 */
	public String getMimeType() {
		return mMimeType;
	}

	/**
	 * @param mDescription the mDescription to set
	 */
	public void setFileDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	/**
	 * @return the mDescription
	 */
	public String getFileDescription() {
		return mDescription;
	}

	/**
	 * @param mTitle the mTitle to set
	 */
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	/**
	 * @return the mTitle
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param mDeleteFile the mDeleteFile to set
	 */
	public void setDeleteFile(boolean mDeleteFile) {
		this.mDeleteFile = mDeleteFile;
	}

	/**
	 * @return the mDeleteFile
	 */
	public boolean isDeleteFile() {
		return mDeleteFile;
	}

	/**
	 * @param mFolder the mFolder to set
	 */
	public void setFolder(boolean mFolder) {
		this.mFolder = mFolder;
	}

	/**
	 * @return the mFolder
	 */
	public boolean isFolder() {
		return mFolder;
	}

	/**
	 * @param mGDriveFile the mGDriveFile to set
	 */
	public void setGDriveFile(gDriveFile mGDriveFile) {
		this.mGDriveFile = mGDriveFile;
	}

	/**
	 * @return the mGDriveFile
	 */
	public gDriveFile getGDriveFile() {
		return mGDriveFile;
	}

	/**
	 * @param mLocalDiskFile the mLocalDiskFile to set
	 */
	public void setLocalDiskFile(java.io.File mLocalDiskFile) {
		this.mLocalDiskFile = mLocalDiskFile;
	}

	/**
	 * @return the mLocalDiskFile
	 */
	public java.io.File getLocalDiskFile() {
		return mLocalDiskFile;
	}

	/**
	 * @param mUploadedFile the mUploadedFile to set
	 */
	public void setUploadedFile(File mUploadedFile) {
		this.mUploadedFile = mUploadedFile;
	}

	/**
	 * @return the mUploadedFile
	 */
	public File getUploadedFile() {
		return mUploadedFile;
	}

	/**
	 * @param mUploadOperation the mUploadOperation to set
	 */
	public void setUploadOperation(UploadOperation mUploadOperation) {
		this.mUploadOperation = mUploadOperation;
	}

	/**
	 * @return the mUploadOperation
	 */
	public UploadOperation getUploadOperation() {
		return mUploadOperation;
	}
}
