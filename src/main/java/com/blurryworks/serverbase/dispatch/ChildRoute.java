package com.blurryworks.serverbase.dispatch;

import org.eclipse.jetty.server.Handler;

public class ChildRoute implements Route
{
	Route child;
	
	Handler parent;
	
	public ChildRoute(Handler parent, Route child)
	{
		this.parent = parent;
		this.child = child;
	}

	@Override
	public Routed route(String path)
	{
		return child.route(path);
	}

	@Override
	public Handler getHandler()
	{
		return parent;
	}

}
