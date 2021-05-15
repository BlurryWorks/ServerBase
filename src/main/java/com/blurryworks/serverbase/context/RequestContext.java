package com.blurryworks.serverbase.context;

import com.blurryworks.serverbase.Application;
import com.blurryworks.serverbase.dispatch.APIDispatch;
import com.blurryworks.serverbase.managementinterface.ServerManager;

public class RequestContext
{
		
	private Application application = null;
	private APIDispatch dispatcher = null;
	private ServerManager serverManager = null;

	
	public RequestContext(Application applicaiton, APIDispatch dispatch, ServerManager serverManager)
	{
		
		this.application = applicaiton;
		this.dispatcher = dispatch;	
		this.serverManager = serverManager;
		
		
	}
	
	public APIDispatch getDispatcher()
	{
		return dispatcher;
	}
	
	public ServerManager getServerManager()
	{
		return serverManager;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public <SA extends Application> SA getApplication()
	{
		return (SA)application;
	}
	
	
	
}
