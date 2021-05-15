package com.blurryworks.serverbase.type;

public class MessageData
{
	
	public static final String FIELD_MIME_TYPE = "mime-type";
	public static final String FIELD_MESSAGE = "message";
	
	public Meta meta = new Meta();
	public Object data = new SimpleMap();

	@SuppressWarnings("unchecked")
	public <T> T getDataAsObject(Class<T> clazz) 
	{
		if(clazz.isInstance(data))
		{
			return (T) data;
		}
		else return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getDataAsObject()
	{
		return (T) data;
	}
	
	public SimpleMap getDataAsMap()
	{		
		return getDataAsObject(SimpleMap.class);
	}
	
	
	
	public boolean isDataMap()
	{
		return SimpleMap.class.isInstance(data);
	}
	
	public void setMIMEType(String MIMEType)
	{
		meta.put(FIELD_MIME_TYPE, MIMEType);
		
	}
	
	public String getMIMEType()
	{
		return meta.getString(FIELD_MIME_TYPE);
	}
	
	public String getMIMEType(String defaultValue)
	{
		return meta.getStringOrDefault(FIELD_MIME_TYPE,defaultValue);
	}
	
	public void setMessage(String message)
	{
		meta.put(FIELD_MESSAGE, message);
	}
	
	public String getMessage()
	{
		return meta.getString(FIELD_MESSAGE);
	}
	
	
	
	
}
