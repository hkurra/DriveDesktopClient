package com.gdrive.desktop.client.Global.ResponderData;

import com.gdrive.desktop.client.FileOperation.UploadCommand;
import com.gdrive.desktop.client.Global.ServiceManager;
import com.gdrive.desktop.client.Global.ServiceManager.serviceType;
import com.google.api.services.drive.model.File;

public class AfterFileUploadRespoderData implements IBaseResponderData {
	public AfterFileUploadRespoderData(UploadCommand UploadCommandData, File Uplodedfile) {
		mUploadCommandData = UploadCommandData;
		mFile  = Uplodedfile;
	}
	
	/**
	 * Input file 
	 */
	public UploadCommand mUploadCommandData;;
	
	/**
	 * uploaded File
	 */
	public File mFile;

	@Override
	public serviceType getResponderServiceType() {
		return ServiceManager.serviceType.AFTER_UPLOAD_SERVICE_ID;
	}
		
}
