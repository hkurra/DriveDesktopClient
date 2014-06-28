package com.gdrive.desktop.client.Global.ResponderData;

import com.gdrive.desktop.client.FileOperation.DeleteCommand;
import com.gdrive.desktop.client.Global.ServiceManager;
import com.gdrive.desktop.client.Global.ServiceManager.serviceType;
import com.google.api.services.drive.model.File;

public class AfterFileDeleteResponderData implements IBaseResponderData {

	
	private File mFile;
	
	private DeleteCommand mDeleteCommandData;

	public AfterFileDeleteResponderData(DeleteCommand DeleteCommandData, File Uplodedfile) {
		mDeleteCommandData = DeleteCommandData;
		mFile  = Uplodedfile;
	}
	
	@Override
	public serviceType getResponderServiceType() {
		return ServiceManager.serviceType.AFTER_FILE_DELETE_SERVICE_ID;
	}

}
