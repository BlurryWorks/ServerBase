package com.blurryworks.serverbase.config.marshal;

public class ConnectorConfiguration
{
	
	public String host;
	public Integer port;
	
	
	public String getHost()
	{
		return host;
	}
	
	public Integer getPort()
	{
		return port;
	}
	
	
	@Override
	public String toString()
	{
		
		return host + ":" + port;
	
	}
	

}
