package com.bbfos.hbecher.geodiff.geojson;

import java.util.Objects;

import com.bbfos.hbecher.geodiff.element.Identifier;
import com.google.gson.JsonElement;

/**
 * Represents the unique identifier of a {@link GeoJsonElement}.<br><br>
 * <cite>If a Feature has a commonly used identifier, that identifier
 * SHOULD be included as a member of the Feature object with the name
 * "id", and the value of this member is either a JSON string or
 * number.</cite><br>
 * From RFC 7946, §3.2. on the GeoJSON Format<br><br>
 * There are two ways to identify a {@code GeoJsonElement}:
 * <ul>
 * <li>by the "id" key at its root (cf. RFC 7946)</li>
 * <li>by its properties</li>
 * </ul>
 * The GeoJSON standard defines the "id" key as what should uniquely identify each Feature,
 * however its presence is not required.
 *
 * @see GeoJsonElement
 */
public class GeoJsonIdentifier extends Identifier
{
	private final JsonElement id;

	public GeoJsonIdentifier(JsonElement id)
	{
		this.id = Objects.requireNonNull(id);
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
