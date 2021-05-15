package com.blurryworks.serverbase.managementinterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.serverbase.ServerShutdownObserver;
import com.blurryworks.fragment.validator.ValidationProcessor;
import com.blurryworks.serverbase.Application;
import com.blurryworks.serverbase.ApplicationBlueprint;
import com.blurryworks.serverbase.Connector;
import com.blurryworks.serverbase.PathConfiguration;
import com.blurryworks.serverbase.State;
import com.blurryworks.serverbase.config.ConfigurationManager;
import com.blurryworks.serverbase.config.marshal.ConnectorConfiguration;
import com.blurryworks.serverbase.config.marshal.ServerBaseApplicationConfiguration;
import com.blurryworks.serverbase.config.marshal.ServerBaseConfiguration;
import com.blurryworks.serverbase.dispatch.APIDispatch;
import com.blurryworks.serverbase.dispatch.DispatchRouter;
import com.blurryworks.serverbase.dispatch.MessageProcessor;
import com.blurryworks.serverbase.dispatch.Route;
import com.blurryworks.serverbase.life.LifeCycleManager;
import com.blurryworks.serverbase.log.ServerBaseLogManager;
import com.blurryworks.serverbase.managementinterface.ShutdownReason.Cause;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * General Interface for management of the entire Server and all running
 * {@link Application}s.
 *
 */
public class ServerManager
{

	Logger log = LoggerFactory.getLogger(this.getClass());

	ServerBaseConfiguration serverConfiguration;
	ServerBaseLogManager serverBaseLogManager = new ServerBaseLogManager();
	ConfigurationManager configManager = new ConfigurationManager();
	LifeCycleManager lifeCycleManager = new LifeCycleManager();
	ServerShutdownObserver serverObserver = new ServerShutdownObserver();
	private ShutdownReason shutdownReason = null;
	private volatile State state = State.Starting;

	Application application = null;

	ValidationProcessor validationProcessor = new ValidationProcessor();

//	private List<Handler> dispatchers = new LinkedList<>();
	Server jetty;
	List<Connector> connectors;
	
	DispatchRouter dispatchRouterHandler;
	
	
	public class ShutdownThread extends Thread
	{
		ServerManager serverManger;
		Integer delay;
		ShutdownReason reason;
		
		public ShutdownThread(ServerManager serverManager, ShutdownReason reason, Integer delay)
		{
			this.serverManger = serverManager;
			this.delay = delay;
			this.reason = reason;
		}
		
		@Override
		public void run()
		{
			log.info(reason.toString());
			if (delay > 0)
			{
				log.info("Delaying ServerBase Shutdown by " + delay + " milliseconds");
				try
				{
					Thread.sleep(delay);
				}
				catch (InterruptedException e)
				{
					log.error("Delay on shutdown was interrupted", e);
				}
				
				
			}

			log.info("Starting ServerBase Shutdown");
			
			serverManger.stop();
		
		}
	}

	public ServerManager()
	{
		SystemShutdownHook shutdownHook = new SystemShutdownHook(this);

		Runtime.getRuntime().addShutdownHook(shutdownHook);

		lifeCycleManager.registerLifeCycleListener(serverObserver);
		lifeCycleManager.registerLifeCycleListener(shutdownHook);
	}

	public ServerShutdownObserver getServerObserver()
	{
		return serverObserver;
	}

	private void loadConfiguration(PathConfiguration pathConfig)
	{
		try
		{
			serverConfiguration = configManager.loadConfiguration(pathConfig);

			if (serverConfiguration == null)
			{
				throw new Exception("Failed to load server configuration, no application specified: "
						+ configManager.getConfigurationFile().getAbsolutePath());
			}

			serverBaseLogManager.apply(serverConfiguration.getLogging());
			loadApplication();
		}
		catch (Exception e)
		{
			log.error("Failure while loading configuration.  Shutting down system", e);
			this.shutdown(new ShutdownReason(Cause.SystemFailure, e));
		}
	}

	public PathConfiguration getPathConfiguration()
	{
		return configManager.getPathConfiguration();
	}

	private void loadApplication() throws Exception
	{
		try
		{
			setApplication(serverConfiguration.getApplication(), this.configManager.getConfigurationPath());

		}
		catch (Exception e)
		{
			throw new Exception("Failed to load server configuration while loading application: "
					+ serverConfiguration.getApplication().name + " Class: "
					+ serverConfiguration.getApplication().load, e);
		}

	}

	/**
	 * Loads server configuration(s) and starts them
	 * 
	 * @param pathConfig File paths that determine where the server configuration is stored.
	 * @throws Exception
	 *             If any {@link Application} can not be loaded for any reason.
	 *             Any resources opened by ServerBase are safely closed before
	 *             this is thrown.
	 * 
	 */
	synchronized public void start(PathConfiguration pathConfig) throws Exception
	{
		setState(State.Starting);
		loadConfiguration(pathConfig);
		setState(State.Started);
	}

	/**
	 * Blocks until all server threads exit
	 */
	public void join()
	{
		if (serverObserver != null)
		{
			try
			{
				serverObserver.join();
			}
			catch (InterruptedException e)
			{
				log.error("ServerObserver Interrupt on Join", e);
			}
		}
	}

	/**
	 * Initiates Shutdown of the entire system This request is non-blocking
	 * 
	 * @param reason What is triggering the shutdown
	 */
	public void shutdown(ShutdownReason reason)
	{
		this.shutdown(0, reason);
	}

	/**
	 * Initiates Shutdown of the entire system This request is non-blocking
	 * 
	 * @param delay Delay in milliseconds to delay the shutdown
	 * @param reason What caused the shutdown to be initiated
	 */
	synchronized public void shutdown(int delay, ShutdownReason reason)
	{

		if (getState().isStoppedOrStopping())
		{
			log.trace("ServerBase shutdown already in progress");
			return;
		}

		setState(State.Stopping);
		this.shutdownReason = reason;
		
		new ShutdownThread(this,reason,delay).start();

	}

	/**
	 * Returns the current state of the ServerBase system.
	 * 
	 * @return Current status of the ServerBase system
	 */
	public State getState()
	{
		return state;
	}

	protected synchronized void setState(State state)
	{
		this.state = state;
		this.lifeCycleManager.event(state.getLifeCycleEvent());
	}

	public File getServerBaseConfigurationPath()
	{
		return this.configManager.getConfigurationPath();
	}

	final public void setupJettyConnectors(List<Connector> connectors)
	{

		this.connectors = connectors;
		HttpConfiguration httpConfig = new HttpConfiguration();
		
		ContextHandler baseHandler = new ContextHandler("/");	
		SessionHandler s = new SessionHandler();
		s.setSessionCookie("id");
		
		this.dispatchRouterHandler = new DispatchRouter();
		s.setHandler(dispatchRouterHandler);
		baseHandler.setHandler(s);

		httpConfig.setSendServerVersion(false);
		HttpConnectionFactory httpFactory = new HttpConnectionFactory(httpConfig);

		QueuedThreadPool threadPool = new QueuedThreadPool();

		jetty = new org.eclipse.jetty.server.Server(threadPool);

		jetty.setStopTimeout(30000);

		for (Connector c : connectors)
		{
			ServerConnector jettyConnector = new ServerConnector(jetty, httpFactory);

			String host;

			if (c.allHosts())
			{
				host = null;
			}
			else
			{
				host = c.getHost();
			}

			jettyConnector.setHost(host);
			jettyConnector.setPort(c.getPort());

			jetty.addConnector(jettyConnector);
		}
		
		
		jetty.setHandler(baseHandler);

	}

	final public void addRoute(Route route)
	{
		dispatchRouterHandler.addRoute(route);
	}

	/**
	 * Print to the Log what connectors are configured.
	 */
	final public void logConnectors()
	{
		if (log.isInfoEnabled())
		{
			StringBuilder serverinfo = new StringBuilder();

			serverinfo.append("ServerBase Application " + application.getName() + " starting on ");
			for (Connector c : connectors)
			{
				serverinfo.append(System.lineSeparator() + "   " + c.getHost() + ":" + c.getPort());
			}

			log.info(serverinfo.toString());
		}

	}
	
	final public synchronized void stop()
	{
		try
		{
			stopJetty();
		}
		catch (Exception e1)
		{
			log.error("Error while shutting down Jetty", e1);
		}
		
		try
		{					
			stopApplication();
		}
		catch (Exception e)
		{
			log.error("Error while shutting down application: " + application.getName(), e);
		}
		finally
		{
			application = null;
		}

		try
		{
			serverObserver.join();
		}
		catch (InterruptedException e)
		{
			log.trace("ServerManager shutdown thread interrupted while waiting for ServerObserver", e);
		}

		setState(com.blurryworks.serverbase.State.Stopped);

		log.info("Finished ServerBase Shutdown");
		
	}

	final public synchronized void stopJetty() throws Exception
	{
		log.info("ServerBase Jetty: Stopping");
		jetty.stop();
		log.info("ServerBase Jetty: Stopped");
	}

	/**
	 * Triggers {@link Application#shutdown()} 
	 *  
	 * @throws Exception Any application triggered exceptions are passed up 
	 */
	final public synchronized void stopApplication() throws Exception
	{

		log.info("ServerBase Application: " + application.getName() + " stopping");		

		application.shutdown();
		log.info("ServerBase Application: " + application.getName() + " stopped");
	}

//	final public List<Handler> getDispatchers()
//	{
//		return dispatchers;
//	}

	public List<Connector> getConnectors()
	{
		return connectors;
	}	

	@SuppressWarnings("unchecked")
	public <A extends Application> A getApplication()
	{
		if (application != null)
			return (A) application;
		return null;
	}

	public <A extends Application, C> void setApplication(ServerBaseApplicationConfiguration configuration,
			File baseConfigurationPath) throws Exception
	{

		log.info("ServerBase Application: " + configuration.name + ": Loading");

		List<Connector> connections = new LinkedList<>();
		for (ConnectorConfiguration connectorConfig : configuration.connectors)
		{
			Connector connector = new Connector(connectorConfig);
			log.info("ServerBase Application: " + configuration.name + ": Binding Port: " + connector);
			connections.add(connector);
		}

		try
		{
			Class<?> clazz = Class.forName(configuration.load);
			@SuppressWarnings("unchecked")
			ApplicationBlueprint<A, C> applicationConfiguration = (ApplicationBlueprint<A, C>) clazz.getConstructor()
					.newInstance();

			Class<A> appClass = applicationConfiguration.getApplicationClass();

			if (appClass == null)
			{
				throw new Exception("Null application class provided for \"" + configuration.name + "\"");
			}

			A app = appClass.getConstructor().newInstance();

			app.init(configuration.name, this);
			Class<C> appConfigclass = applicationConfiguration.getConfigurationClass();

			if (appConfigclass != null)
			{
				File appConfigFile = new File(baseConfigurationPath, configuration.name + ".json");
				if (!appConfigFile.exists())
					throw new FileNotFoundException();
				ObjectMapper mapper = new ObjectMapper();
				C loadedConfiguration = mapper.readValue(appConfigFile, appConfigclass);
				applicationConfiguration.setApplicationConfiguration(app, loadedConfiguration);
			}

			launchApplication(connections, configuration.name, app);

		}
		catch (FileNotFoundException fnfe)
		{
			throw new Exception("Failed to load configuration file as requested for: " + configuration.name, fnfe);
		}
		catch (ClassNotFoundException e)
		{
			throw new Exception("Application Configuration class not found application \"" + configuration.name
					+ "\" ClassName: " + configuration.load, e);
		}
		catch (ClassCastException e)
		{
			throw new Exception("Application Configuration class (" + configuration.name + ") does not extend "
					+ ApplicationBlueprint.class.getCanonicalName(), e);
		}

	}
	
	

	// public void addApplication(Connector connection, String serverName,
	// Application application,
	// ServerManager serverManager) throws Exception
	// {
	// List<Connector> connections = new LinkedList<>();
	// connections.add(connection);
	// launchApplication(connections, serverName, application, serverManager);
	// }

	public void buildValidationCache(Application application)
	{
		log.info("Start: Building Validation Cache for " + application.getName());
		for (Handler h : dispatchRouterHandler.getHandlers())
		{
			if (h instanceof APIDispatch)
			{
				for (Class<? extends MessageProcessor> mpc : ((APIDispatch) h).getDispatchMap().values())
				{
					try
					{
						MessageProcessor mp = mpc.getConstructor().newInstance();
						Class<?> classToCache = mp.getInputClass();
						validationProcessor.cacheClassValidation(classToCache);
					}
					catch (Exception e)
					{
						log.error("Unable to build validation cache for " + mpc.getName(), e);
					}
				}
			}
		}
		log.info("Finished: Building Validation Cache for " + application.getName());
	}

	/**
	 * As long as the Server is {@link State#Started} or {@link State#Starting}
	 * then the application is loaded and started in the web server.
	 * 
	 * @param connections
	 *            What network ports the application should be hooked into
	 * @param applicationName
	 *            String name for the application
	 * @param application
	 *            An instance of the application to map and setup
	 * @throws Exception Any failure to configure/start the application/jetty will be passed up
	 */
	public synchronized void launchApplication(List<Connector> connections, String applicationName,
			Application application) throws Exception
	{
		this.application = application;

		setupJettyConnectors(connections);
		application.setup();
		buildValidationCache(application);
		logConnectors();
		
		if(application.getJettySessionDataStoreFactory() != null)
			jetty.addBean(application.getJettySessionDataStoreFactory(),true);
		startJetty();
	}

	private void startJetty() throws Exception
	{
		jetty.start();
	}

	public ValidationProcessor getValidationProcessor()
	{
		return validationProcessor;
	}
	
	public ShutdownReason getShutdownReason()
	{
		return shutdownReason;
	}	

}
