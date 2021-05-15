package com.blurryworks.serverbase.dispatch.protocol;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.blurryworks.serverbase.dispatch.BlurryResponseStatusCodes;
import com.blurryworks.serverbase.dispatch.MessageRequest;
import com.blurryworks.serverbase.dispatch.MessageResponse;
import com.blurryworks.serverbase.protocolinterface.HttpServletServerContentTypeHandler;

public class Http implements HttpServletServerContentTypeHandler
{
	
	@Override
	public String getSupportedProtocol()
	{
		return "http";
	}

	@Override
	public void parseRequest(HttpServletRequest request,
			MessageRequest blurryRequest,
			Class<?> inputObjectClass)
	{
		blurryRequest.setURI(request.getRequestURL().toString());
		blurryRequest.setHTTPMethod(request.getMethod());
		
		if(request.getContentType() != null)
			blurryRequest.setMIMEType(request.getContentType());
		blurryRequest.setContentLength(request.getContentLengthLong());
	}
	
	@Override
	public void encodeResponse(HttpServletResponse response, MessageResponse blurryResponse)
	{		
		response.setContentType(blurryResponse.getMIMEType());
		
		BlurryResponseStatusCodes responseStatus = blurryResponse.getStatus();
		if(responseStatus == null)
		{
			responseStatus = BlurryResponseStatusCodes.Success;
		}
		
		response.setStatus(responseStatus.getHttpStatusCode());
		
		//Default Headers
		response.addHeader("X-Frame-Options", "deny");
		response.addHeader("X-XSS-Protection", "1; mode=block");
		response.addHeader("X-Content-Type-Options", "nosniff");
		response.addHeader("Referrer-Policy", "no-referrer");
		
	}

}
