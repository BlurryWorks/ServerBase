package com.blurryworks.serverbase.dispatch;

import org.eclipse.jetty.server.Handler;

public class SimpleRoute implements Route
{
	Handler handler;
	String prefix;
	
	public SimpleRoute(String path, Handler handler)
	{
		this.prefix = path;
		this.handler = handler;
	}

	@Override
	public Routed route(String path)
	{
		return path.startsWith(prefix) ? new Routed(prefix,path.substring(prefix.length()),handler) : null;
	}

	@Override
	public Handler getHandler()
	{
		return handler;
	}


	
}
