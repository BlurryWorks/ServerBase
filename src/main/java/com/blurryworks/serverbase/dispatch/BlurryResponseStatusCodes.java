package com.blurryworks.serverbase.dispatch;

public enum BlurryResponseStatusCodes
{
	
	Success(200),
	MalformedRequest(400),
	NotAuthenticated(401),
	NotAuthorized(403),
	NotFound(404),
	MethodNotAllowed(405),
	ServerError(500);
	
	
	private int httpStatusCode;
	private BlurryResponseStatusCodes(int httpStatusCode)
	{
		this.httpStatusCode = httpStatusCode;
	}
	
	public int getHttpStatusCode()
	{
		return httpStatusCode;
	}

}
