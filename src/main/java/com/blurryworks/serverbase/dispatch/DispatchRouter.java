package com.blurryworks.serverbase.dispatch;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.blurryworks.serverbase.State;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandlerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DispatchRouter extends AbstractHandlerContainer
{
	
	List<Route> routes = new CopyOnWriteArrayList<>();
	
		
	Logger log = LoggerFactory.getLogger(this.getClass());
	Server server = null;
	State state = State.Stopped;

	
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		Routed match = null;
		for(Route r : routes)
		{
			match = r.route(target);
			if(match != null)
			{



				match.handler.handle(match.subpath, baseRequest, request, response);
				return;
			}
		}
		
		response.setStatus(BlurryResponseStatusCodes.NotFound.getHttpStatusCode());
		baseRequest.setHandled(true);
	}

	public void addRoute(Route r)
	{
		Handler[] oldHandlers = getHandlers();
		r.getHandler().setServer(getServer());
		routes.add(r);
		updateBeans(oldHandlers, getHandlers());
	}

	/**
	 * Setup for 
	 * 
	 */
	@Override
	public Handler[] getHandlers()
	{
		return routes.stream().map(r -> r.getHandler()).toArray(Handler[]::new);		
	}

}
