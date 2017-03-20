package com.bbfos.hbecher.geodiff.geojson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bbfos.hbecher.geodiff.elements.Coordinate;
import com.bbfos.hbecher.geodiff.elements.Element;
import com.bbfos.hbecher.geodiff.elements.Identifier;
import com.bbfos.hbecher.geodiff.elements.Type;
import com.github.filosganga.geogson.model.Coordinates;
import com.github.filosganga.geogson.model.Feature;
import com.github.filosganga.geogson.model.positions.Positions;
import com.github.filosganga.geogson.model.positions.SinglePosition;
import com.google.common.base.Optional;
import com.google.gson.JsonElement;

public class GeoJsonElement extends Element
{
	private final Feature feature;
	private final Identifier id;
	private final Type type;
	private final List<Coordinate> coordinates = new ArrayList<>();

	public GeoJsonElement(Feature feature)
	{
		this(feature, null);
	}

	// propertyId != null ==> use feature.properties.<propertyId> ad uid
	// otherwise use standard GeoJSON uid feature.id
	public GeoJsonElement(Feature feature, String propertyId)
	{
		String uid;

		if(propertyId == null)
		{
			Optional<String> optId = feature.id();

			if(optId.isPresent())
			{
				uid = optId.get();
			}
			else
			{
				throw new IllegalArgumentException("Feature has no valid id");
			}
		}
		else if(propertyId.isEmpty())
		{
			throw new IllegalArgumentException("Received empty uid");
		}
		else
		{
			JsonElement element = feature.properties().get(propertyId);

			if(element == null)
			{
				throw new IllegalArgumentException("Feature has no valid id");
			}

			uid = element.getAsString();
		}

		this.feature = feature;
		this.id = new GeoJsonIdentifier(uid);
		this.type = GeoJsonUtils.getType(feature.geometry().type());

		fillCoordinates(coordinates, feature.geometry().positions());
	}

	private static void fillCoordinates(List<Coordinate> coordinates, Positions positions)
	{
		if(positions != null)
		{
			if(positions instanceof SinglePosition)
			{
				Coordinates c = ((SinglePosition) positions).coordinates();

				coordinates.add(new Coordinate(c.getLon(), c.getLat()));
			}
			else
			{
				for(Positions child : positions.children())
				{
					fillCoordinates(coordinates, child);
				}
			}
		}
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
	public List<Coordinate> getCoordinates()
	{
		return Collections.unmodifiableList(coordinates);
	}

	@Override
	public GeoJsonElement toGeoJsonElement()
	{
		return this;
	}
}
