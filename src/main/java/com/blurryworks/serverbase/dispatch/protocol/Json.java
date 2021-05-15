package com.blurryworks.serverbase.dispatch.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.blurryworks.serverbase.dispatch.MessageRequest;
import com.blurryworks.serverbase.dispatch.MessageResponse;
import com.blurryworks.serverbase.protocol.JSONResonableDefaults;
import com.blurryworks.serverbase.protocolinterface.HttpServletServerContentTypeHandler;
import com.blurryworks.serverbase.type.SimpleMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class Json implements HttpServletServerContentTypeHandler
{

	private static ObjectMapper jsonMapper = new ObjectMapper();

	static
	{
		JSONResonableDefaults.applyDefaults(jsonMapper);
	}

	static public ObjectReader getReader(Class<?> mapTo)
	{
		return jsonMapper.readerFor(mapTo);
	}

	static public ObjectMapper getMapper()
	{
		return jsonMapper;
	}

	@Override
	public void parseRequest(HttpServletRequest request, MessageRequest blurryRequest, Class<?> inputObjectClass)
			throws IOException
	{

		try (BufferedReader stream = request.getReader())
		{

			ObjectReader jsonReader;

			if (inputObjectClass != null)
			{
				jsonReader = jsonMapper.readerFor(inputObjectClass);
				blurryRequest.data = jsonReader.readValue(stream);
			}
			else
			{
				jsonReader = jsonMapper.readerFor(SimpleMap.class);
				blurryRequest.data = jsonReader.readValue(stream);
			}
		}
	}

	@Override
	public void encodeResponse(HttpServletResponse response, MessageResponse blurryResponse) throws Exception
	{
		OutputStream stream = response.getOutputStream();
		jsonMapper.writeValue(stream, blurryResponse.data);
	}

	@Override
	public String getSupportedProtocol()
	{
		return "application/json";
	}

}
