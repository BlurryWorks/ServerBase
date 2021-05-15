package com.blurryworks.serverbase;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Optional;
import org.apache.commons.cli.CommandLine;

import com.blurryworks.fragment.validator.ValidationResult;
import com.blurryworks.fragment.validator.ValidationResults;
import com.blurryworks.fragment.validator.engine.ResultStatus;

public class PathConfiguration
{

	public static final String CONFIG_FOLDER = "config";
	public static final String CONFIG_FILE = "config.json";
	
	Optional<Path> operationPath;
	Optional<Path> configuraitonPath;

	public PathConfiguration(CommandLine cl)
	{
		
		configuraitonPath = Optional.ofNullable(cl.getOptionValue(Boot.configuration_path_cmd_option)).map(path -> FileSystems.getDefault().getPath(path));
		operationPath = Optional.ofNullable(cl.getOptionValue(Boot.operation_path_cmd_option)).map(path -> FileSystems.getDefault().getPath(path));
		
	}
	
	
	public void setConfiguraitonBase(Path configuraitonBase)
	{
		this.configuraitonPath = Optional.ofNullable(configuraitonBase);
	}
	
	public void setConfiguraitonBase(Optional<Path> configuraitonBase)
	{
		this.configuraitonPath = configuraitonBase;
	}
	
	public void setOperationBase(Path operationBase)
	{
		this.operationPath = Optional.ofNullable(operationBase);
	}
	
	public void setOperationBase(Optional<Path> operationBase)
	{
		this.operationPath = operationBase;
	}

	public ValidationResults validate()
	{
		ValidationResults results = new ValidationResults();

		operationPath.ifPresent(f -> {
			if (!f.toFile().exists())
				results.add(new ValidationResult("Operation Path does not exist: " + f,
						ResultStatus.Failure));
		});
		
		configuraitonPath.ifPresent(f-> {
			if (!f.toFile().exists())
				results.add(new ValidationResult("Configuration Path does not exist: " + f,
						ResultStatus.Failure));
		});

		return results;

	}
	
	public Path getOperationPath()
	{
		return operationPath.orElseGet(() -> FileSystems.getDefault().getPath("."));
				
	}
	
	public File getOperationPathAsFile()
	{
		return getOperationPath().toFile();
	}
	
	public Path getConfigurationPath()
	{
		return configuraitonPath.orElseGet(() -> getOperationPath().resolve(CONFIG_FOLDER) );
	}
	
	public File getConfiguraitonPathAsFile()
	{
		return configuraitonPath.orElseGet(() -> getOperationPath()).toFile();
	}
	
	public File getConfigurationFile()
	{
		return getConfigurationPath().resolve(CONFIG_FILE).toFile();
	}
}