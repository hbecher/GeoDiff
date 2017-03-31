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

	// propsIds != null && propsIds.length > 0 ==> use feature.properties.[propsIds] ad uid
	// propsIds != null && propsIds.length == 0 ==> use all properties as uid
	// otherwise use standard GeoJSON uid feature.id
	public GeoJsonElement(Feature feature, String[] propsIds)
	{
		JsonElement uid;

		if(propsIds == null)
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
		else
		{
			JsonObject obj = new JsonObject();

			if(propsIds.length == 0)
			{
				for(Map.Entry<String, JsonElement> entry : feature.properties().entrySet())
				{
					obj.add(entry.getKey(), entry.getValue());
				}
			}
			else
			{
				for(String property : propsIds)
				{
					JsonElement e = feature.properties().get(property);

					if(e == null)
					{
						throw new IllegalArgumentException("Feature has no property " + property);
					}

					obj.add(property, e);
				}
			}

			uid = obj;
		}

		this.feature = feature;
		id = new GeoJsonIdentifier(uid);
		type = Type.from(feature.geometry().type());
		coordinates = new Coordinates(feature.geometry());
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
