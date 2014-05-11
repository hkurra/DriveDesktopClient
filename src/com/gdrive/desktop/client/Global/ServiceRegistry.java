package com.gdrive.desktop.client.Global;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class ServiceRegistry {
	
	static 
	{
		mServiceRegistry = new HashMap<ServiceManager.serviceType, ArrayList<IResponder>>();
	}
	  
	  private static HashMap<ServiceManager.serviceType, ArrayList<IResponder>> mServiceRegistry;
	  
	  public static void AddToRegistry(IResponder responder, ServiceManager.serviceType service) {
		  
		if(getServiceRegistry().containsKey(service)) {
			getServiceRegistry().get(service).add(responder);
		}
		else {
			ArrayList<IResponder> newResponder = new ArrayList<IResponder>();
			newResponder.add(responder);
			
			getServiceRegistry().put(service, newResponder);
		}
	}
	/**
	 * @return the mServiceRegistry
	 */
	public static HashMap<ServiceManager.serviceType, ArrayList<IResponder>> getServiceRegistry() {
		return mServiceRegistry;
	}
	  
	  
}
