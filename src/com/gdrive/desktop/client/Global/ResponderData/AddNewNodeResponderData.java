package com.gdrive.desktop.client.Global.ResponderData;

import com.gdrive.desktop.client.Global.ServiceManager.serviceType;

public class AddNewNodeResponderData implements IBaseResponderData {

	@Override
	public serviceType getResponderServiceType() {
		return serviceType.ADD_NEW_NODE_SERVICE_ID;
	}

}
