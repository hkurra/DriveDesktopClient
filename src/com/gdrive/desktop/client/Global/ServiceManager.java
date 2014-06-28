package com.gdrive.desktop.client.Global;



import java.util.ArrayList;

import com.gdrive.desktop.client.Global.ResponderData.IBaseResponderData;
import com.gdrive.desktop.client.Global.ServiceRegistry;

public class ServiceManager {
	
	public static enum serviceType {
	      BEFORE_UPLOAD_SERVICE_ID,
	      AFTER_UPLOAD_SERVICE_ID,
	      UPDATE_FILE_CACHE_SERVICE_ID,
	      AFTER_FILE_DELETE_SERVICE_ID
	  };
	
	public static void registerResponder(IResponder responder, serviceType service ) {
		ServiceRegistry.AddToRegistry(responder, service);
	}
	
	public static void registerResponders(IResponder responder, ArrayList<serviceType> services ) {
		
		for (serviceType service : services) {
			ServiceRegistry.AddToRegistry(responder, service);
		}
	}
	
	public static void ExecuteResponders(serviceType service, IBaseResponderData responderData) {
		if(ServiceRegistry.getServiceRegistry().containsKey(service)) {
			ArrayList<IResponder> responder = ServiceRegistry.getServiceRegistry().get(service);
			
			for(IResponder s : responder) {
				s.Update(responderData);
		}
		}
	}

}
