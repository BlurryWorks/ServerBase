package com.blurryworks.serverbase;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.serverbase.life.LifeCycleEvent;
import com.blurryworks.serverbase.life.LifeCycleListener;

/**
 * Waits until all observed threads have exited before exiting
 */
public class ServerShutdownObserver extends Thread implements LifeCycleListener
{

	Logger log = LoggerFactory.getLogger(this.getClass());

	ConcurrentLinkedQueue<Thread> observedThreads = new ConcurrentLinkedQueue<>();
	ConcurrentLinkedQueue<Server> observedJettyServers = new ConcurrentLinkedQueue<>();
	Boolean stop = false;

	public ServerShutdownObserver()
	{

	}

	public void addObservation(Thread t)
	{
		this.observedThreads.add(t);
	}
	
	public void addObservation(Server s)
	{
		this.observedJettyServers.add(s);
	}

	
	private synchronized Boolean isStop()
	{
		return stop;
	}
	
	public synchronized void setStop()
	{
		this.stop = true;
		this.notifyAll();
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				wait();
				if(isStop())
					break;
			} catch (InterruptedException e2)
			{
				log.trace("Interrupted waiting until shutdown notification",e2);
			}
		}
		
		
		while(!observedJettyServers.isEmpty())
		{
			Server underObservation = observedJettyServers.poll();
			try
			{
				underObservation.join();
			}
			catch (InterruptedException e)
			{
				log.error("Observer waiting on an Observerd Jetty Server Join interrupted.",e);
			}
		}

		while (!observedThreads.isEmpty())
		{
			Thread underObservation = observedThreads.poll();

			try
			{
				underObservation.join();
			} catch (InterruptedException e)
			{
				log.error("Observer waiting on an Observerd Thread Join interrupted.", e);
			}
		}
	}

	@Override
	public void receiveFragmentServerLifeCycleEvent(LifeCycleEvent event)
	{
		if(event == LifeCycleEvent.ServerStopping)
			setStop();
	}
}
