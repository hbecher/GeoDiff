package com.bbfos.hbecher.geodiff.geojson;

import java.util.Map;

import com.bbfos.hbecher.geodiff.element.Coordinates;
import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.element.Identifier;
import com.bbfos.hbecher.geodiff.element.Type;
import com.github.filosganga.geogson.model.Feature;
import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GeoJsonElement extends Element
{
	private final Feature feature;
	private final Identifier id;
	private final Type type;
	private final Coordinates coordinates;

	public GeoJsonElement(Feature feature)
	{
		this(feature, null);
	}

	// propertyId != null ==> use feature.properties.<propertyId> ad uid
	// otherwise use standard GeoJSON uid feature.id
	public GeoJsonElement(Feature feature, String propertyId)
	{
		JsonElement uid;

		if(propertyId == null)
		{
			Optional<String> optId = feature.id();

			if(optId.isPresent())
			{
				uid = new JsonPrimitive(optId.get());
			}
			else
			{
				throw new IllegalArgumentException("Feature has no valid id");
			}
		}
		else if(propertyId.isEmpty())
		{
			JsonObject obj = new JsonObject();

			for(Map.Entry<String, JsonElement> entry : feature.properties().entrySet())
			{
				obj.add(entry.getKey(), entry.getValue());
			}

			uid = obj;
		}
		else
		{
			uid = feature.properties().get(propertyId);

			if(uid == null)
			{
				throw new IllegalArgumentException("Feature has no valid id");
			}
		}

		this.feature = feature;
		this.id = new GeoJsonIdentifier(uid);
		this.type = Type.from(feature.geometry().type());

		this.coordinates = new Coordinates(feature.geometry());
	}

	public Feature getFeature()
	{
		return feature;
	}

	@Override
	public Identifier getId()
	{
		return id;
	}

	@Override
	public Type getType()
	{
		return type;
	}

	@Override
	public Coordinates getCoordinates()
	{
		return coordinates;
	}

	@Override
	public GeoJsonElement toGeoJsonElement()
	{
		return this;
	}
}
