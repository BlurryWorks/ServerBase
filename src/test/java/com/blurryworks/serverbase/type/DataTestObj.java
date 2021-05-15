package com.blurryworks.serverbase.type;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class DataTestObj
{
	public int field1;
	public String field2;
	
	
	@Override
	public boolean equals(Object obj)
	{
		return EqualsBuilder.reflectionEquals(this, obj);	
	}
}