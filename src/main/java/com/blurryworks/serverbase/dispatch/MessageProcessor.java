package com.blurryworks.serverbase.dispatch;

import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.serverbase.context.RequestContext;

/**
 * 
 */
public abstract class MessageProcessor implements Runnable
{

	protected Request jettyRequest;
	protected MessageRequest request;
	protected MessageResponse response;
	protected RequestContext context;
	protected Logger log = LoggerFactory.getLogger(this.getClass());

	public void setRequest(MessageRequest request)
	{
		this.request = request;
	}
	
	public void setJettyRequest(Request jettyRequest)
	{
		this.jettyRequest = jettyRequest;
	}

	/**
	 * The default implementation of this is a non-operation. This is only called if
	 * {@link #getInputClass()} returns a non-null response Prior to this method
	 * being called, the validation logic on the Bean is run
	 * 
	 * @param obj
	 *            The mapped bean of input data based on {@link #getInputClass()}
	 */
	public void setInputObject(Object obj)
	{

	}

	public void setResponse(MessageResponse response)
	{
		this.response = response;
	}
	
	public final void run()
	{
		try
		{
			process();
		} catch (Exception e)
		{
			response.setStatus(BlurryResponseStatusCodes.ServerError);
			log.error("Failure while processing request",e);
		}

	}

	public abstract void process() throws Exception;

	/**
	 * @return The default returns null. Override and return not null for this
	 *         function to transform input, run validation and call
	 *         {@link #setInputObject(Object)}
	 */
	public Class<?> getInputClass()
	{
		return null;
	}

	public Class<?> getOutputClass()
	{
		return null;
	}

	public void setContext(RequestContext context)
	{
		this.context = context;
	}
	
	
}
