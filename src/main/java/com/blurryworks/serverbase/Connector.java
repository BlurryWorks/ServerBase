package com.blurryworks.serverbase;

import com.blurryworks.serverbase.config.marshal.ConnectorConfiguration;

public class Connector
{
	String host;
	Integer port;
	
	public Connector() {
		this.host = "*";
	}
	
	public Connector(String host, Integer port)
	{
		setHost(host);
		setPort(port);
	}
	
	
	public Connector(ConnectorConfiguration connectorConfig)
	{
		setHost(connectorConfig.getHost());
		setPort(connectorConfig.getPort());
	}

	public String getHost()
	{
		return host;
	}
	
	public boolean allHosts()
	{
		return host.equals("*");
	}
	
	public Integer getPort()
	{
		return port;
	}
	
	public void setHost(String host)
	{
		if(host == null || host.isEmpty())
			this.host = "*";
		else
			this.host = host;
	}
	
	public void setPort(Integer port)
	{
		this.port = port;
	}
	
	@Override
	public String toString()
	{
		return host + ":" + port;
	}
	

}
