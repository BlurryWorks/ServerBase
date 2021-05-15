package com.blurryworks.serverbase.managementinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.serverbase.life.LifeCycleEvent;
import com.blurryworks.serverbase.life.LifeCycleListener;
import com.blurryworks.serverbase.managementinterface.ShutdownReason.Cause;

public class SystemShutdownHook extends Thread implements LifeCycleListener
{

	Logger log = LoggerFactory.getLogger(this.getClass());
	ServerManager serverManager = null;
	Boolean shutdownInProgress = false;
	

	public SystemShutdownHook(ServerManager serverManager)
	{
		this.serverManager = serverManager;
	}

	/**
	 * This thread should not be started until JVM exit.  
	 */
	@Override
	public synchronized void run()
	{
	
		if(!shutdownInProgress)
		{			
			log.error("Unexpected external shutdown of JVM, attempting graceful exit");
			serverManager.shutdown(new ShutdownReason(Cause.ExternalProcessRequest));
			serverManager.join();
		}		


	}

	@Override
	public synchronized void receiveFragmentServerLifeCycleEvent(LifeCycleEvent event)
	{
		if(event == LifeCycleEvent.ServerStopping)
			this.shutdownInProgress = true;
	}

}
