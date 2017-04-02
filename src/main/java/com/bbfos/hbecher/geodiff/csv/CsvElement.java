package com.bbfos.hbecher.geodiff.csv;

import com.bbfos.hbecher.geodiff.element.Coordinates;
import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.element.Type;
import com.bbfos.hbecher.geodiff.geojson.GeoJsonElement;
import com.github.filosganga.geogson.model.Feature;
import com.github.filosganga.geogson.model.Point;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Work in progress.
 */
public class CsvElement extends Element
{
	private final String[] properties;
	private final CsvDescriptor descriptor;
	private final CsvIdentifier id;
	private final Coordinates point;

	public CsvElement(String[] properties, CsvDescriptor descriptor)
	{
		this.properties = properties;
		this.descriptor = descriptor;
		this.id = new CsvIdentifier(descriptor, properties);

		double lon, lat;

		try
		{
			lon = Double.parseDouble(properties[descriptor.getLonId()]);
			lat = Double.parseDouble(properties[descriptor.getLatId()]);
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Invalid point coordinates");
		}

		point = new Coordinates(Point.from(lon, lat));
	}

	@Override
	public CsvIdentifier getId()
	{
		return id;
	}

	@Override
	public Type getType()
	{
		return Type.POINT;
	}

	@Override
	public Coordinates getCoordinates()
	{
		return point;
	}

	@Override
	public GeoJsonElement toGeoJsonElement()
	{
		ImmutableMap.Builder<String, JsonElement> builder = ImmutableMap.builder();

		for(int i = 0; i < properties.length; i++)
		{
			builder.put(descriptor.getProperty(i), new JsonPrimitive(properties[i]));
		}

		return new GeoJsonElement(new Feature(point.getGeometry(), builder.build(), Optional.of(descriptor.getIdProperty())));
	}
}
