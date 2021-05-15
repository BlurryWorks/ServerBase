package com.blurryworks.serverbase.dispatch;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.fragment.validator.ValidationProcessor;
import com.blurryworks.fragment.validator.ValidationResult;
import com.blurryworks.fragment.validator.ValidationResults;
import com.blurryworks.fragment.validator.engine.ResultStatus;
import com.blurryworks.serverbase.Application;
import com.blurryworks.serverbase.context.RequestContext;
import com.blurryworks.serverbase.filter.RequestFilter;
import com.blurryworks.serverbase.filter.RequestFilters;
import com.blurryworks.serverbase.filter.ResponseFilter;
import com.blurryworks.serverbase.filter.ResponseFilters;
import com.blurryworks.serverbase.filter.implementation.RequestFilterBase;
import com.blurryworks.serverbase.filter.implementation.ResponseFilterBase;
import com.blurryworks.serverbase.managementinterface.ServerManager;

public class APIDispatch extends AbstractHandler implements Routable
{
	public static final String DEFAULT_BASE_URI = "/api/";

	Logger log = LoggerFactory.getLogger(this.getClass());
	
	Route route;

	RequestContext context = null;
	ValidationProcessor validationProcessor = null;

	ParserStack parser = new ParserStack();
	HashMap<String, Class<? extends MessageProcessor>> dispatchMap = new HashMap<String, Class<? extends MessageProcessor>>();

	boolean provideJSONSchema = false;

	
	/**
	 * Defaults the path to {@value #DEFAULT_BASE_URI}
	 */
	public APIDispatch()
	{
		this.route = new SimpleRoute(DEFAULT_BASE_URI, this);
	}
	
	public APIDispatch(String path)
	{
		this.route = new SimpleRoute(path, this);	
	}
		

	/**
	 * 
	 * @param path Path to bind too
	 * @param handler MessageProcessor to process incoming requests on path
	 * 
	 * @throws NullPointerException If any passed parameters are null 
	 */
	public void addDispatch(String path, Class<? extends MessageProcessor> handler)
	{
		if(path == null)
			throw new NullPointerException();
		else if(handler == null)
			log.error("API Dispatch: Provider handler for path: " + path + " is null.  This route will not be created");
		else
			this.dispatchMap.put(path, handler);
	}

	public void setProvideJSONSchema(boolean provideJSONSchema)
	{
		this.provideJSONSchema = provideJSONSchema;
	}

	public HashMap<String, Class<? extends MessageProcessor>> getDispatchMap()
	{
		return dispatchMap;
	}

	final void dispatch(String message, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws Exception
	{

		MessageRequest blurryRequest;
		MessageResponse blurryResponse = new MessageResponse();

		Class<? extends MessageProcessor> processorClazz = dispatchMap.get(message);
		Constructor<? extends MessageProcessor> constructor = processorClazz.getConstructor();
		MessageProcessor processor = constructor.newInstance();

		Class<?> inputObjClass = processor.getInputClass();

		blurryRequest = parser.parseHttpRequest(message, baseRequest, request, inputObjClass);

		ValidationResults validationResults = null;

		if (inputObjClass != null)
		{
			if(blurryRequest.getContentLength() == 0)
			{
				validationResults = new ValidationResults();
				validationResults.add(new ValidationResult("Missing Post Data", ResultStatus.Failure));
			}
			else
			{
				// PERCEPTION-31
				validationResults = validationProcessor.validate(blurryRequest.data);
			}
		}

		if (validationResults != null && !validationResults.isEmpty())
		{
			blurryResponse.setStatus(BlurryResponseStatusCodes.MalformedRequest);
			blurryResponse.data = validationResults;
		}
		else
		{
			processor.setJettyRequest(baseRequest);
			processor.setContext(context);
			processor.setRequest(blurryRequest);
			processor.setResponse(blurryResponse);
			

			RequestFilters requestFilters = processorClazz.getAnnotation(RequestFilters.class);
			ResponseFilters responseFilters = processorClazz.getAnnotation(ResponseFilters.class);

			boolean process = true;

			// Magic Happens
			if (requestFilters != null)
				for (RequestFilter filter : requestFilters.value())
				{
					RequestFilterBase filterImpl = filter.value().getConstructor().newInstance();

					process = filterImpl.filter(context,blurryRequest,blurryResponse);
					if (!process)
						break;
				}

			if (process)
			{
				processor.run();

				if (responseFilters != null)
				{
					for (ResponseFilter filter : responseFilters.value())
					{						
						ResponseFilterBase filterImply = filter.value().getConstructor().newInstance();
						filterImply.filter(context,blurryResponse);
					}
				}
			}
		}

		parser.encodeHTTPBlurryResponse(blurryResponse, response);

	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{

	
		switch (baseRequest.getMethod())
		{
		case "POST":
			log.debug("Dispatching " + target);
			baseRequest.setHandled(true);
			try
			{

				if (dispatchMap.containsKey(target))
				{
					this.dispatch(target, baseRequest, request, response);
				}
				else
				{
					response.setStatus(BlurryResponseStatusCodes.NotFound.getHttpStatusCode());
				}
			}
			catch (Exception e)
			{
				log.error("Dispatch Error", e);
				response.setStatus(BlurryResponseStatusCodes.ServerError.getHttpStatusCode());
			}
			break;
		default:
			log.trace("Non-POST method request on API Dispatcher");
			response.setStatus(BlurryResponseStatusCodes.MethodNotAllowed.getHttpStatusCode());			
			break;
		}
		
		baseRequest.setHandled(true);
	
	}

	



	@Override
	public Route getRoute()
	{
		return route;
	}
	
	@Override
	public void initializeRoutable(ServerManager manager, Application application)
	{
		context = new RequestContext(application, this, manager);
		validationProcessor = manager.getValidationProcessor();		
	}

}
