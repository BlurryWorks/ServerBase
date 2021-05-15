package com.blurryworks.serverbase.managementinterface;

public class ShutdownReason {
	public enum Cause {
		ApplicationRequest,
		/** 
		 * Signal sent to the PID, such as SIGHUP
		 */
		ExternalProcessRequest,
		/**
		 * Unrecoverable failure
		 */
		SystemFailure
		
	}
	
	
	public ShutdownReason(Cause cause)
	{	
		this.cause = cause;
	}
	
	public ShutdownReason(Cause cause, Exception exception)
	{
		this(cause);
		this.exception = exception;
	}
	
	
	Cause cause;
	Exception exception = null;
	
	
	public void setCause(Cause cause)
	{
		this.cause = cause;
	}
	
	public void setException(Exception exception)
	{
		this.exception = exception;
	}
	
	public Cause getCause()
	{
		return cause;
	}
	
	public Exception getException()
	{
		return exception;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder("Shutdown triggered, cause: ");
		result.append(this.getCause());
		if(exception != null)
		{
			result.append(" exception: ");
			result.append(exception);
		}
		return result.toString();	
	}
	
	
	
}
