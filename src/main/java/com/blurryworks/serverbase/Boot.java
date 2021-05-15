package com.blurryworks.serverbase;

import java.time.Duration;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blurryworks.fragment.validator.ValidationResults;
import com.blurryworks.fragment.validator.engine.ResultStatus;
import com.blurryworks.serverbase.managementinterface.ServerManager;

public class Boot implements Runnable
{
	static final String operation_path_cmd_option = "o";
	static final String configuration_path_cmd_option = "c";
	
	Logger log = LoggerFactory.getLogger(this.getClass());

	ServerManager serverManager = null;
	PathConfiguration bootConfig;

	private static void printHelp(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();

		String header = "";

		String footer = "";

		formatter.printHelp("server", header, options, footer, true);
		// TODO Need to package application and update this

	}

	Boot(PathConfiguration config)
	{
		this.bootConfig = config;
	}

	@Override
	public void run()
	{
		long startTime = System.nanoTime();
		log.info("ServerBase Starting");

		serverManager = new ServerManager();

		try
		{
			serverManager.start(bootConfig);
			serverManager.join();

		}
		catch (Exception e)
		{
			log.error("ServerBase failed to start", e);
		}
		finally
		{
			long endTime = System.nanoTime();
			String status = null;

			if (serverManager.getState().isStoppedOrStopping())
			{
				status = "failed to start in";
			}
			else
			{
				status = "started in";
			}

			log.info("ServerBase " + status + ": " + Duration.ofNanos(endTime - startTime).toString().substring(2)
					.replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase());

		}
	}
	
	
	public static void main(String[] args)
	{
		Options options = new Options();

		Option configPath = Option.builder(configuration_path_cmd_option).longOpt("configPath").desc("Configuration path. The path to configuration files.  Default is the subfolder config in the operation path.")
				.hasArg().argName("path").build();
		Option help = Option.builder("h").longOpt("help").desc("Display Help").build();
		Option operationPath = Option.builder(operation_path_cmd_option).longOpt("operationPath").desc("Operation path. Assign the working path of the server.  Defaults to working directory.  ")
				.hasArg().argName("path").build();

		
		options.addOption(configPath);
		options.addOption(operationPath);
		options.addOption(help);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try
		{
			cmd = parser.parse(options, args);
		}
		catch (ParseException e)
		{
			System.out.println("Command Line Parsing failed.");
			printHelp(options);
			return;
		}

		if (cmd.hasOption("h"))
		{
			printHelp(options);
		}
		else
		{
			PathConfiguration config = new PathConfiguration(cmd);
			ValidationResults vr = config.validate();
			
			if(vr.getStatus() == ResultStatus.Success)
			{
				Boot system = new Boot(config);
				system.run();
			}
			else
			{
				System.out.println(vr.toString());
			}
		}
	}
}
