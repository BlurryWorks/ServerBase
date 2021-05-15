package com.blurryworks.serverbase.log;

import java.util.List;

import org.pmw.tinylog.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.serverbase.config.marshal.LoggingConfiguration;

/**
 * SLF4J is routed to tinylog
 */
public class ServerBaseLogManager
{

	public void apply(List<LoggingConfiguration> loggingConfigurations)
	{
		Logger log = LoggerFactory.getLogger(this.getClass());
		
		log.info("ServerBase Log Configuration Update: Started" );
		
		Configurator logConfig  = Configurator.defaultConfig();
		
		if(loggingConfigurations != null)
		{	
			logConfig.removeAllWriters();
			logConfig.resetCustomLevels();
			
			for(LoggingConfiguration logger : loggingConfigurations)
			{				
				logger.applyConfiguration(logConfig);				
			}
		}
		
		logConfig.activate();
		
		log.info("ServerBase Log Configuration Update: Finished" );
		
		
	}
	

}
