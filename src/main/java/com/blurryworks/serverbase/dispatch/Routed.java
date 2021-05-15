package com.blurryworks.serverbase.dispatch;

import org.eclipse.jetty.server.Handler;

public class Routed
{
	
	public Handler handler;
	public String subpath;
	public String prefix;
	
	public Routed(String prefix, String subpath, Handler handler)
	{
		this.prefix = prefix;
		this.handler = handler;
		setSubpath(subpath);
		
		
	}
	
	public void setSubpath(String subpath)
	{
		if(subpath.length() == 0)
			this.subpath = "/";
		else
			this.subpath = subpath;
	}

}
