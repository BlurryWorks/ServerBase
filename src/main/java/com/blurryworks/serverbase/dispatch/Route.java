package com.blurryworks.serverbase.dispatch;

import org.eclipse.jetty.server.Handler;

/**
 * Only a single {@link Handler} can be contained in a single route
 */
public interface Route
{	
	/** 
	 * 
	 * 
	 * @param path The incoming request path
	 * @return An object describing which handler to execute and what the new target is that should be passed to that handler.  Return null if there is no match.
	 */
	public Routed route(String path);
	
	public Handler getHandler();
	
	
}
