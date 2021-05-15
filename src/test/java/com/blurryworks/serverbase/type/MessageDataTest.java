package com.blurryworks.serverbase.type;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

public class MessageDataTest
{
	
	@Test
	void testGetDataAsObject() throws Exception
	{
		MessageData md = new MessageData();
		SimpleMap data = new SimpleMap();
		data.put("field1", 1);
		data.put("field2", 2);
		
		md.data = data;
		
		assertEquals(data, md.getDataAsObject(SimpleMap.class));
		
		DataTestObj expectedObj = new DataTestObj();
		expectedObj.field1 = 1;
		expectedObj.field2 = "2";
		md = new MessageData();
		md.data = expectedObj;
		
		assertTrue(EqualsBuilder.reflectionEquals(expectedObj, md.data));	
	}

}
