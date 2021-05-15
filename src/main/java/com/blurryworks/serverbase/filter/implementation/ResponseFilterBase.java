package com.blurryworks.serverbase.filter.implementation;

import com.blurryworks.serverbase.context.RequestContext;
import com.blurryworks.serverbase.dispatch.MessageResponse;

public abstract class ResponseFilterBase
{
	
	
	public abstract void filter(RequestContext context, MessageResponse response); 

}
