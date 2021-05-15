package com.blurryworks.serverbase.dispatch;

import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.serverbase.dispatch.protocol.Http;
import com.blurryworks.serverbase.dispatch.protocol.Json;
import com.blurryworks.serverbase.type.MessageData;

public class ParserStack
{
	Http httpParse = new Http();
	Json jsonParse = new Json();
	
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	public MessageRequest parseHttpRequest(String message, Request baseRequest,
			HttpServletRequest request,Class<?> inputObject) throws Exception
	{
		MessageRequest req = new MessageRequest();
		
		req.meta.put(MessageData.FIELD_MESSAGE, message);	
				
		httpParse.parseRequest(request, req, null);
		
		if(req.getMIMEType() != null && HttpMethod.POST.is(req.getHTTPMethod()))
		{
			String mimeType = req.getMIMEType(jsonParse.getSupportedProtocol()); 
			switch(mimeType)
			{
				case "application/json":
					jsonParse.parseRequest(request, req, inputObject);
					break;
				default:
					log.info("Unsupported mime type: " + mimeType);
					break;
			}
		}
		return req;
	}
	
	
	
	public void encodeHTTPBlurryResponse(MessageResponse blurryResponse, HttpServletResponse response) throws Exception
	{
		
		httpParse.encodeResponse(response, blurryResponse);
		
		if(blurryResponse.isRawResponse())
		{
			OutputStream stream = response.getOutputStream();
			stream.write((byte[]) blurryResponse.data);
			stream.close();		
		}
		else
		{	
			String contentType = blurryResponse.getMIMEType(jsonParse.getSupportedProtocol()); //Default to JSON
			

			response.setContentType(contentType);
			
			
			switch(contentType)
			{
				case "application/json":
					jsonParse.encodeResponse(response, blurryResponse);					
					break;
					
			}	
			
			response.flushBuffer();
			
			
			
			
		}
		
	}
	
	

}
