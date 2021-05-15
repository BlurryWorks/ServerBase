package com.blurryworks.serverbase.dispatch;

import com.blurryworks.serverbase.type.MessageData;

public class MessageResponse extends MessageData
{
	
	public static final String FIELD_STATUS = "status";
	public static final String FIELD_RAW = "raw";
	
	
	
	public void setRawResponse(byte[] response, String contentType)
	{
		meta.put(FIELD_RAW, true);
		this.setMIMEType(contentType);
		this.data = response;
	}
	
	public boolean isRawResponse()
	{
		return (boolean) meta.getOrDefault(FIELD_RAW, false);
	}
	
	
	public void setStatus(BlurryResponseStatusCodes responseStatus)
	{
		this.meta.put(FIELD_STATUS, responseStatus);
	}
	
	public void setStatus(BlurryResponseStatusCodes responseStatus, String statusDescription)
	{
		this.setStatus(responseStatus);
		if(this.isDataMap())
		{
			getDataAsMap().put("statusDescription", statusDescription);
		}
	}
	
	public BlurryResponseStatusCodes getStatus()
	{		
		return (BlurryResponseStatusCodes) this.meta.get(FIELD_STATUS);
	}


}
