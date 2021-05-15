package com.blurryworks.serverbase;

import java.lang.reflect.ParameterizedType;

/**
 * This class is specified in the setup of the ServerBase configuration.
 * Provides class meta data information to ServerBase
 * 
 * TODO This should be collapsed into {@link Application} as they provide overlapping concepts.
 *
 * @param <A> The Application entry description that should be loaded
 * @param <C> The configuration class that will be loaded by ServerBase from JSON
 */
public abstract class ApplicationBlueprint<A extends Application,C>
{	
	
	/**
	 * If the application wants to load configuration from disk, the class to load the data should be returned.
	 * The configuration file should JSON formated and be the applicationName.json
	 * 
	 * Once the configuration file is loaded into the provided class, {@link #setApplicationConfiguration(Application, Object)} is called
	 * 
	 *
	 * @return The configuration class that should be used to marshal the application json configuration.
	 */
	@SuppressWarnings("unchecked")
	public Class<C> getConfigurationClass()
	{		
		return  (Class<C>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];		
	}
	
	@SuppressWarnings("unchecked")
	public Class<A> getApplicationClass()
	{
		return  (Class<A>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	/**
	 * Once the application configuration is loaded off disk into the desired object this is called to store local configuration.
	 * A separate call {@link Application#setup()} is where application startup should be managed.
	 * 
	 * @param application The constructed application to apply the configuration too.
	 * @param configuration The configuration to apply
	 */
	public abstract void setApplicationConfiguration(A application, C configuration);
	
	


}
