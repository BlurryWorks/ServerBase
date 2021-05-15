package com.blurryworks.serverbase.dispatch;

import java.net.URL;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

import com.blurryworks.serverbase.Application;
import com.blurryworks.serverbase.managementinterface.ServerManager;

public class ResourceDispatch extends ResourceHandler implements Routable
{

	SimpleRoute route;

	public ResourceDispatch(String path)
	{
		super();

		this.route = new SimpleRoute(path, this);

		this.setDirAllowed(false);
		this.setDirectoriesListed(false);
		this.setRedirectWelcome(false);
		this.setPathInfoOnly(true);
		setResourceBase(path);
	}

	/**
	 * @see #setBaseResource(Resource)
	 * @param resourceURL
	 *            Path to the file or Java Resource to load resources from
	 */
	public void setBaseResource(URL resourceURL)
	{
		setBaseResource(Resource.newResource(resourceURL));
	}

	@Override
	public Route getRoute()
	{
		return route;
	}
	
	@Override
	public void initializeRoutable(ServerManager manager, Application application)
	{
		
	}
}
