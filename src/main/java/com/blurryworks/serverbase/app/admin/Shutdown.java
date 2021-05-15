package com.blurryworks.serverbase.app.admin;

import com.blurryworks.serverbase.dispatch.MessageProcessor;
import com.blurryworks.serverbase.managementinterface.ShutdownReason;
import com.blurryworks.serverbase.managementinterface.ShutdownReason.Cause;

/**
 * Delays shutdown request by 1 second to allow for the response to this request to be processed.
 */
public class Shutdown extends MessageProcessor
{
	@Override
	public void process() throws Exception
	{		
		this.context.getServerManager().shutdown(1000,new ShutdownReason(Cause.ApplicationRequest));
		this.response.getDataAsMap().put("Status", "Shutting down");
	}
}
