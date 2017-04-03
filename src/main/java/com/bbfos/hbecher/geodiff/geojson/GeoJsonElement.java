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

/**
 * Represents a GeoJSON element.
 *
 * @see #GeoJsonElement(Feature, String[])
 */
public class GeoJsonElement extends Element
{
	private final Feature feature;
	private final GeoJsonIdentifier id;
	private final Type type;
	private final Coordinates coordinates;

	/**
	 * Convenient constructor that uses a {@code null} identifier,
	 * corresponding to the identifier defined by the GeoJSON standard.
	 *
	 * @param feature the feature
	 */
	public GeoJsonElement(Feature feature)
	{
		this(feature, null);
	}

	/**
	 * Creates a {@code GeoJsonElement} from the given {@code Feature} identified by the given properties.<br>
	 * Depending on the value of {@code idKeys} the uid will be one of the following:
	 * <ul>
	 * <li>if {@code idKeys == null}, then the standard GeoJSON uid will be used</li>
	 * <li>if {@code idKeys.length == 0}, then all properties will be used as the uid</li>
	 * <li>if {@code idKeys.length > 0}, then the given properties will be used as the uid</li>
	 * </ul>
	 * A uid is considered invalid if
	 * <ul>
	 * <li>the Feature doesn't have the standard GeoJSON "id" key</li>
	 * <li>at least one of the given properties doesn't exist</li>
	 * </ul>
	 *
	 * @param feature the feature
	 * @param idKeys  a list of properties to use as an identifier
	 * @throws IllegalArgumentException if the id given is invalid
	 */
	public GeoJsonElement(Feature feature, String[] idKeys)
	{
		JsonElement uid;

		if(idKeys == null)
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

			if(idKeys.length == 0)
			{
				for(Map.Entry<String, JsonElement> entry : feature.properties().entrySet())
				{
					obj.add(entry.getKey(), entry.getValue());
				}
			}
			else
			{
				for(String property : idKeys)
				{
					JsonElement e = feature.properties().get(property);

					if(e == null)
					{
						throw new IllegalArgumentException("Feature has no property '" + property + "'");
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
