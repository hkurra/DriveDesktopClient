package com.gdrive.desktop.client.Global.ResponderData;

import com.gdrive.desktop.client.FileOperation.UploadCommand;
import com.gdrive.desktop.client.Global.ServiceManager;
import com.gdrive.desktop.client.Global.ServiceManager.serviceType;
import com.google.api.services.drive.model.File;

public class AfterFileUploadRespoderData implements IBaseResponderData {
	public AfterFileUploadRespoderData(UploadCommand UploadCommandData, File Uplodedfile) {
		setUploadCommandData(UploadCommandData);
		setFile(Uplodedfile);
	}
	
	/**
	 * uploaded Command 
	 */
	private UploadCommand mUploadCommandData;;
	
	/**
	 * uploaded File
	 */
	private File mFile;

	@Override
	public serviceType getResponderServiceType() {
		return ServiceManager.serviceType.AFTER_UPLOAD_SERVICE_ID;
	}

	/**
	 * @param mUploadCommandData the mUploadCommandData to set
	 */
	public void setUploadCommandData(UploadCommand mUploadCommandData) {
		this.mUploadCommandData = mUploadCommandData;
	}

	/**
	 * @return the mUploadCommandData
	 */
	public UploadCommand getUploadCommandData() {
		return mUploadCommandData;
	}

	/**
	 * @param mFile the mFile to set
	 */
	public void setFile(File mFile) {
		this.mFile = mFile;
	}

	/**
	 * @return the mFile
	 */
	public File getFile() {
		return mFile;
	}
		
}
