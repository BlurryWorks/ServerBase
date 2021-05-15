package com.blurryworks.serverbase.protocol;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JSONResonableDefaults
{
	
	/**
	 * Adds in various modules 
	 * <ul><li>{@link JavaTimeModule}</li>
	 * <li>{@link Jdk8Module}</li>
	 * 
	 * <li>Disables {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES} </li>
	 * <li>Disables {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS}</li>
	 * </ul>
	 * 
	 * @param mapper JSON Mapper to apply the defaults too
	 */
	public static final void applyDefaults(ObjectMapper mapper)
	{
		mapper.registerModule(new JavaTimeModule());
		mapper.registerModule(new Jdk8Module());
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);		
	}

}
