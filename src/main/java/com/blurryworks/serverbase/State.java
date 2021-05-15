package com.blurryworks.serverbase;

import com.blurryworks.serverbase.life.LifeCycleEvent;

public enum State
{
	Stopped(), 
	Starting(LifeCycleEvent.ServerStarting), 
	Started(), 
	Stopping(LifeCycleEvent.ServerStopping);
	
	
	LifeCycleEvent lifeCycleEvent = null;
	
	private State()
	{
	}
	
	private State(LifeCycleEvent lifeCycleEvent)
	{
		this.lifeCycleEvent = lifeCycleEvent;
	}

	public boolean isStopped()
	{
		return this == Stopped;
	}

	public boolean isStopping()
	{
		return this == Stopping;
	}

	public boolean isStarting()
	{
		return this == State.Starting;
	}

	public boolean isStarted()
	{
		return this == State.Started;
	}

	public boolean isStoppedOrStopping()
	{
		switch (this)
		{
		case Stopping:
		case Stopped:
			return true;
		default:
			return false;
		}
	}

	public LifeCycleEvent getLifeCycleEvent()
	{
		return this.lifeCycleEvent;
	}

}
