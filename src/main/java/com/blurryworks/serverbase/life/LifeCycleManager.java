package com.blurryworks.serverbase.life;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LifeCycleManager
{
	List<LifeCycleListener> lifeCycleListeners = Collections.synchronizedList(new LinkedList<>());

	public void registerLifeCycleListener(LifeCycleListener listener)
	{
		lifeCycleListeners.add(listener);
	}

	public void deregisterLifeCycleListener(LifeCycleListener listener)
	{

		lifeCycleListeners.remove(listener);
	}

	public void event(LifeCycleEvent event)
	{
		if(event == null) return;
		
		synchronized (lifeCycleListeners)
		{
			lifeCycleListeners.stream().forEach(l -> l.receiveFragmentServerLifeCycleEvent(event));
		}

	}

}
