package com.gdrive.desktop.client.Global.ResponderData;

import com.gdrive.desktop.client.FileOperation.UploadCommand;
import com.gdrive.desktop.client.Global.ServiceManager;

public class BeforeFileUploadResponderData implements IBaseResponderData
{
	public BeforeFileUploadResponderData(UploadCommand UploadCommandData) {
		mUploadCommandData = UploadCommandData;
	}

	/**
	 * uploadFileCommandData 
	 */
	public UploadCommand mUploadCommandData;;
	/**
	 * AFTER_UPLOAD_SERVICE_ID
	 */
	@Override
	public com.gdrive.desktop.client.Global.ServiceManager.serviceType getResponderServiceType() {
		return ServiceManager.serviceType.BEFORE_UPLOAD_SERVICE_ID;
	}
	

}
