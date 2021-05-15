package com.blurryworks.serverbase.dispatch;

import org.eclipse.jetty.server.Handler;

import com.blurryworks.serverbase.Application;
import com.blurryworks.serverbase.managementinterface.ServerManager;

/**
 * ServerBase Dispatch that can be mapped in a ServerBase context.  
 * Jetty {@link Handler} is foundational to ServerBase dispatch components. 
 */
public interface Routable extends Handler
{
	
	public Route getRoute();
	
	public void initializeRoutable(ServerManager manager, Application application);

}
