package cache;



import com.google.api.services.drive.model.File;


/**
 * @author harsh
 * 
 *this class going to store File metadata(as like com.google.api.services.drive.model.File) with some extra 
 *metadata need locally before uplaoding or downloading .this class should be a child class of com.google.api.services.drive.model.File; but 
 *as above file is final so have to go this way 
 */
public class gDriveFile  {
	
private String mFilePath;

private File mDFile;

private Boolean mDelateFile = false;

private Boolean mFolder;

public gDriveFile(String filePath, File driveFile)
{
	if (driveFile != null) setDFile(driveFile);
	if (filePath != null) setFilePath(filePath);
	if (!filePath.isEmpty() && !new java.io.File(filePath).exists()){
		setFolder(true);
	}
}

{
	setFilePath(new String());
	setFolder(false);
	setDFile(new File());
	setDelateFile(false);
}

/**
 * @param mFilePath the mFilePath to set
 */
public void setFilePath(String mFilePath) {
	this.mFilePath = mFilePath;
}

/**
 * @return the mFilePath
 */
public String getFilePath() {
	return mFilePath;
}

/**
 * @param mDFile the mDFile to set
 */
public void setDFile(File mDFile) {
	this.mDFile = mDFile;
}

/**
 * @return the mDFile
 */
public File getDFile() {
	return mDFile;
}


/**
 * @param mFolder the mFolder to set
 */
public void setFolder(Boolean mFolder) {
	this.mFolder = mFolder;
}


/**
 * @return the mFolder
 */
public Boolean isFolder() {
	return mFolder;
}


/**
 * @param mDelateFile the mDelateFile to set
 */
public void setDelateFile(Boolean mDelateFile) {
	this.mDelateFile = mDelateFile;
}


/**
 * @return the mDelateFile
 */
public Boolean getDelateFile() {
	return mDelateFile;
}

}
