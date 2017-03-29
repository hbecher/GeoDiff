package com.bbfos.hbecher.geodiff.geojson;

import com.bbfos.hbecher.geodiff.element.Identifier;
import com.google.gson.JsonElement;

public class GeoJsonIdentifier extends Identifier
{
	private final JsonElement id;

	public GeoJsonIdentifier(JsonElement id)
	{
		this.id = id;
	}

	@Override
	public JsonElement getId()
	{
		return id;
	}

	@Override
	public String asString()
	{
		return id.getAsString();
	}
}
