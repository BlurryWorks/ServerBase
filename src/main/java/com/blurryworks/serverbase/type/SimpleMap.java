package com.blurryworks.serverbase.type;

import java.util.HashMap;

import com.blurryworks.serverbase.dispatch.protocol.Json;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO See if there is another option here
 */
public class SimpleMap extends HashMap<String, Object>
{	

	private static final long serialVersionUID = 7181664129317374040L;

	public String getString(String key)
	{
		return (String) get(key);		
	}
	
	public String getStringOrDefault(String key,String defaultValue)
	{
		return (String) this.getOrDefault(key, defaultValue);
		
	}
	
	public <T> T asObject(Class<T> clazz) throws Exception 
	{
		ObjectMapper mapper = Json.getMapper();
		try
		{
			return mapper.convertValue(this, clazz);
		}
		catch (IllegalArgumentException e) {
			throw new Exception("Failed to convert to desired object.",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAs(String key)
	{
		return (T) get(key);
	}
	
	public void setAsObject(Object object) 
	{
		this.clear();
		this.mergeObject(object);
	}
	
	public void mergeObject(Object object)
	{
		ObjectMapper mapper = new ObjectMapper();
		this.putAll(mapper.convertValue(object, this.getClass()));
	}

	
	
	
	

}
