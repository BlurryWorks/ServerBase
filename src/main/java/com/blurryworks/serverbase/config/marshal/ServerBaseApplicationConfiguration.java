package com.blurryworks.serverbase.config.marshal;

import java.util.List;

import com.blurryworks.serverbase.ApplicationBlueprint;


/**
 * ServerBase Configuration of an application to load into the Server, loaded out of the JSON. 
 * 
 */
public class ServerBaseApplicationConfiguration
{
	
	public List<ConnectorConfiguration> connectors;
	/**
	 * Used to locate configuration file if the class requires a configuration
	 */
	public String name;
	
	/**
	 * The class to invoke, must extend {@link ApplicationBlueprint}
	 */
	public String load;

}
