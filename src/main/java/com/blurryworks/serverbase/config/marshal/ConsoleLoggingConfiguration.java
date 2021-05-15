package com.blurryworks.serverbase.config.marshal;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.writers.ConsoleWriter;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("console")
public class ConsoleLoggingConfiguration extends LoggingConfiguration
{

	@Override
	public void applyConfiguration(Configurator configurator)
	{
		configurator.addWriter(new ConsoleWriter(), this.level.getTinylogLevel());

	}

}
