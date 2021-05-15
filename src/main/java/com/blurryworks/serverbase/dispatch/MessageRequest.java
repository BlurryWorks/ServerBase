package com.blurryworks.serverbase.dispatch;

import java.net.URI;
import java.util.Optional;

import com.blurryworks.serverbase.type.MessageData;

public class MessageRequest extends MessageData
{
	public static final String FIELD_URI = "uri";
	public static final String FIELD_TARGET = "target";	
	public static final String FIELD_HTTP_METHOD = "http.method";
	public static final String FIELD_CONTENT_LENGTH = "contentLength";

	public String getTarget()
	{
		return meta.getString(FIELD_TARGET);
	}	

	public URI getURI()
	{
		return URI.create(meta.getString(FIELD_URI));
	}
	
	public void setURI(String uri)
	{
		meta.put(FIELD_URI, uri);
	}

	public String getPath()
	{
		return getURI().getPath();
	}
	
	public String getHTTPMethod()
	{
		return meta.getAs(FIELD_HTTP_METHOD);
	}
	
	public void setHTTPMethod(String method)
	{
		meta.put(FIELD_HTTP_METHOD, method);
	}
	
	
	public void setContentLength(Long contentLength)
	{
		meta.put(FIELD_CONTENT_LENGTH, Optional.ofNullable(contentLength).orElse((long) 0));
	}
	
	public Long getContentLength()
	{
		return meta.getAs(FIELD_CONTENT_LENGTH);
	}
}
