package com.blurryworks.serverbase.protocolinterface;

import com.blurryworks.serverbase.dispatch.MessageRequest;
import com.blurryworks.serverbase.dispatch.MessageResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HttpServletServerContentTypeHandler
{
	public String getSupportedProtocol();
	
	public void parseRequest(HttpServletRequest request,MessageRequest blurryRequest,Class<?> inputObjectclass) throws Exception;
	
	public void encodeResponse(HttpServletResponse response, MessageResponse blurryResponse) throws Exception;	
	
	
}
