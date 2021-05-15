package com.blurryworks.serverbase;

import org.eclipse.jetty.server.session.SessionDataStoreFactory;

import com.blurryworks.serverbase.dispatch.APIDispatch;
import com.blurryworks.serverbase.dispatch.Routable;
import com.blurryworks.serverbase.managementinterface.ServerManager;


/**
 * An {@link Application} is a Network Server and Application code
 * 
 * A single ServerBase Application
 * 
 * Responsible for attaching {@link APIDispatch} and other functionality to a Jetty
 * http server. Can be bound to multiple network ports.
 * 
 * 
 * TODO Consider splitting abstract Application from a wrapper solution that moves system level operations out of this abstract class.
 * 
 * 
 */

public abstract class Application
{
	String name = null;


	ServerManager serverManager = null;
	SessionDataStoreFactory jettySessionDataStoreFactory = null;
	
	
	/**
	 * Returns the {@link ServerManager} that is the parent to this
	 * {@link Application}
	 * 
	 * @return ServerManager that the application is running under
	 */
	final public ServerManager getServerManager()
	{
		return serverManager;
	}
	
	
	public String getName()
	{
		return name;
	}

		
	

	final public void init(String applicationName, ServerManager serverManager)
	{
		this.name = applicationName;
		this.serverManager = serverManager;
		
	}
	

	
	public void addDispatch(Routable routable)
	{
		routable.initializeRoutable(getServerManager(), this);
		getServerManager().addRoute(routable.getRoute());
	}
	
	/**
	 * May only be called from inside {@link #setup()}, otherwise the change will not be applied.
	 * @param sessionDataStoreFactory A factory for Jetty to use to store sessions.  By default no long term or clustered storage of sessions is done.
	 */
	public void setJettySessionDataStoreFactory(SessionDataStoreFactory sessionDataStoreFactory)
	{
		this.jettySessionDataStoreFactory = sessionDataStoreFactory;
	}
	
	public SessionDataStoreFactory getJettySessionDataStoreFactory()
	{
		return jettySessionDataStoreFactory;
	}
	
	
	

	
	/** 
	 * Called during start of the application
	 * Called before Jetty is started 
	 * 
	 * @see APIDispatch
	 * 
	 * @throws Exception When a failure happens that should prevent the Application from starting 
	 */
	public abstract void setup() throws Exception;
	
	
	/**
	 * Called once during configuration lifecycle shutdown.
	 * Use this to release any resources that need to be shutdown.
	 */
	public abstract void shutdown();
	
	

	
	

}
