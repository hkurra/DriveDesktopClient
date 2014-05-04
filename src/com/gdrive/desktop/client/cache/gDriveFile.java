package com.gdrive.desktop.client.cache;

import com.google.api.services.drive.model.File;

/**
 * 
 * <p>
 * This class going to store File metadata(as like
 * com.google.api.services.drive.model.File) with some extra metadata need
 * locally before uplaoding or downloading .this class should be a child class
 * of com.google.api.services.drive.model.File; but as above file is final so
 * have to go this way
 * </p>
 * 
 * 
 * @author harsh
 */
public class gDriveFile {

	private String mFilePath;

	private File mDFile;

	private Boolean mDelateFile = false;

	private Boolean mFolder;

	{
		setFilePath(new String());
		setFolder(false);
		setDFile(new File());
		setDelateFile(false);
	}

	/**
	 * <p>
	 * for file @param filePath is file's absolute path
	 * </p>
	 * <p>
	 * for folder @param filePath is folder's name
	 * </p>
	 * 
	 * @param filePath
	 * @param driveFile
	 */
	public gDriveFile(final String filePath, final File driveFile) {
		if (driveFile != null)
			setDFile(driveFile);
		if (filePath != null)
			setFilePath(filePath);
		// TODO have to test this line
		if (!filePath.isEmpty() && !new java.io.File(filePath).exists()) {
			setFolder(true);
		}
	}

	/**
	 * @return the mDelateFile
	 */
	public Boolean getDelateFile() {
		return mDelateFile;
	}

	/**
	 * @return the mDFile
	 */
	public File getDFile() {
		return mDFile;
	}

	/**
	 * @return the mFilePath
	 */
	public String getFilePath() {
		return mFilePath;
	}

	/**
	 * @return the mFolder
	 */
	public Boolean isFolder() {
		return mFolder;
	}

	/**
	 * @param mDelateFile
	 *            the mDelateFile to set
	 */
	public void setDelateFile(final Boolean mDelateFile) {
		this.mDelateFile = mDelateFile;
	}

	/**
	 * @param mDFile
	 *            the mDFile to set
	 */
	public void setDFile(final File mDFile) {
		this.mDFile = mDFile;
	}

	/**
	 * @param mFilePath
	 *            the mFilePath to set
	 */
	public void setFilePath(final String mFilePath) {
		this.mFilePath = mFilePath;
	}

	/**
	 * @param mFolder
	 *            the mFolder to set
	 */
	public void setFolder(final Boolean mFolder) {
		this.mFolder = mFolder;
	}

}
