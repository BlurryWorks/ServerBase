package com.blurryworks.serverbase.filter.implementation;

import com.blurryworks.serverbase.context.RequestContext;
import com.blurryworks.serverbase.dispatch.MessageRequest;
import com.blurryworks.serverbase.dispatch.MessageResponse;

public abstract class RequestFilterBase
{
	
	public abstract boolean filter(RequestContext context, MessageRequest request, MessageResponse response);

}
