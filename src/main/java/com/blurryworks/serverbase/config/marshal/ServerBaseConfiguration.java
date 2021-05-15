package com.blurryworks.serverbase.config.marshal;

import java.util.List;

/**
 * Typically loaded from a JSON file/stream on ServerBase boot 
 */
public class ServerBaseConfiguration {

	
	public ServerBaseApplicationConfiguration application;
	
	public List<LoggingConfiguration> logging;
	
	
	
	public ServerBaseApplicationConfiguration getApplication()
	{
		return application;
	}
	
	public List<LoggingConfiguration> getLogging()
	{
		return logging;
	}
	
	

}
