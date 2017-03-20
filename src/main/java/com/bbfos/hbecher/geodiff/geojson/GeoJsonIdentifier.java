package com.bbfos.hbecher.geodiff.geojson;

import com.bbfos.hbecher.geodiff.elements.Identifier;

public class GeoJsonIdentifier extends Identifier
{
	private final String id;

	public GeoJsonIdentifier(String id)
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
