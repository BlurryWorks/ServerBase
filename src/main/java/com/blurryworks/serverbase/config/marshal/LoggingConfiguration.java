package com.blurryworks.serverbase.config.marshal;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * {@code 
 * {}
 * 
 * }
 */
@JsonTypeInfo(include = As.PROPERTY, use = Id.NAME, property = "type")
@JsonSubTypes({
	@Type(value = ConsoleLoggingConfiguration.class)
})
public abstract class LoggingConfiguration
{
	
	public enum LogLevel
	{
		error(Level.ERROR),
		warning(Level.WARNING),
		info(Level.INFO),
		debug(Level.DEBUG),
		trace(Level.TRACE);
		
		Level tinylogLevel;
		private LogLevel(Level tinylogLevel)
		{			
			this.tinylogLevel = tinylogLevel;
		}
		
		public Level getTinylogLevel()
		{
			return tinylogLevel;
		}
	}
	
	
	
	public LogLevel level;
	
	
	public abstract void applyConfiguration(Configurator configurator);

}
