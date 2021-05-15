package com.blurryworks.serverbase.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.serverbase.PathConfiguration;
import com.blurryworks.serverbase.config.marshal.ServerBaseConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigurationManager
{

	Logger log = LoggerFactory.getLogger(this.getClass());

	

	PathConfiguration pathConfiguration = null;	

	public PathConfiguration getPathConfiguration()
	{
		return pathConfiguration;
	}

	public File getConfigurationFile()
	{
		return pathConfiguration.getConfigurationFile();
	}

	public File getConfigurationPath()
	{
		return pathConfiguration.getConfiguraitonPathAsFile();
	}

	public ServerBaseConfiguration loadConfiguration(PathConfiguration config) throws Exception
	{
		
		this.pathConfiguration = config;
		File operationsPathFile = config.getOperationPathAsFile();

		if (!operationsPathFile.exists() || !operationsPathFile.isDirectory())
		{
			throw new FileNotFoundException("ServerBase Operation Directory doesn't exist or is not a directory: "
					+ operationsPathFile.getAbsolutePath());
		}

		File configurationPath = config.getConfiguraitonPathAsFile();

		if (!configurationPath.exists() || !configurationPath.isDirectory())
		{
			throw new FileNotFoundException("ServerBase Configuration Directory doesn't exist or is not a directory: "
					+ operationsPathFile.getAbsolutePath());
		}

		File configurationFile = config.getConfigurationFile();

		if (!configurationFile.exists())
		{
			throw new FileNotFoundException(
					"ServerBase Configuration file does not exist: " + configurationFile.getAbsolutePath());
		}		

		
		ObjectMapper mapper = new ObjectMapper();
		
		log.info("ServerBase Loading Configuration");
		
		try
		{
			return mapper.readValue(configurationFile, ServerBaseConfiguration.class);
		}
		catch (IOException e)
		{
			throw new Exception("Error parsing ServerBase configuration file: " + configurationFile.getAbsolutePath(),
					e);
		}		
		
	}


}
