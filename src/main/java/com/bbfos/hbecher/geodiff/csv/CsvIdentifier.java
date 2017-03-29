package com.bbfos.hbecher.geodiff.csv;

import com.bbfos.hbecher.geodiff.element.Identifier;

public class CsvIdentifier extends Identifier
{
	private final String id;

	public CsvIdentifier(CsvDescriptor descriptor, String[] properties)
	{
		this(properties[descriptor.getId()]);
	}

	public CsvIdentifier(String id)
	{
		this.id = id;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String asString()
	{
		return id;
	}
}
