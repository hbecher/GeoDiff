package com.bbfos.hbecher.geodiff.metadata;

import java.util.Map;

/**
 * An object that holds the parsed metadata arguments passed via the {@code --metadata} option.<br><br>
 * The metadata gives information to the parsers, such as what {@link com.bbfos.hbecher.geodiff.element.Identifier uniquely identifies} each element.
 * At the moment, it is only used to give a custom identifier for GeoJSON elements
 * (if they don't have the default one, such as one or more of their properties) and describe the fields of CSV files.<br>
 * The uid can be accessed using the {@link #getId()} method, provided it was specified. The name of the uid key is {@value ID}.<br><br>
 */
public class Metadata
{
	/**
	 * The unique identifier key.
	 */
	private static final String ID = "id";
	private final Map<String, String> metadata;

	/**
	 * The constructor.
	 *
	 * @param metadata the metadata mapping
	 */
	Metadata(Map<String, String> metadata)
	{
		this.metadata = metadata;
	}

	/**
	 * Gets the metadata value corresponding to the key.
	 *
	 * @param key the key
	 * @return The metadata value, or {@code null} if not any.
	 */
	public String getMetadata(String key)
	{
		return metadata.get(key);
	}

	/**
	 * Checks if the key maps to a metadata value.
	 *
	 * @param key the key
	 * @return {@code true} if the key maps to a metadata value, {@code false} otherwise.
	 */
	public boolean hasMetadata(String key)
	{
		return metadata.containsKey(key);
	}

	/**
	 * Returns the unique identifier specified by the metadata.<br>
	 * May be null. You can use {@link Metadata#hasId()} to check if the uid is present.
	 *
	 * @return The unique identifier.
	 */
	public String getId()
	{
		return getMetadata(ID);
	}

	/**
	 * Returns {@code true} if th metadata contains a mapping for the uid, {@code false} otherwise.
	 *
	 * @return {@code true} if the metadata contains a uid.
	 */
	public boolean hasId()
	{
		return hasMetadata(ID);
	}
}
