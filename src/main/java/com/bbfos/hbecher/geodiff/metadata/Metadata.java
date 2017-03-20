package com.bbfos.hbecher.geodiff.metadata;

import java.util.Map;

/**
 * An object that holds the parsed metadata arguments passed via the {@code --metadata} option.<br />
 * The metadata should always contain the {@link com.bbfos.hbecher.geodiff.elements.Identifier unique identifier}.
 */
public class Metadata
{
	/**
	 * The unique identifier key in the metadata.
	 */
	private static final String ID = "id";
	private final Map<String, String> metadata;

	/**
	 * Creates a {@code Metadata} by parsing the given {@code String}.<br />
	 * {@code} metadata has to be non-null and non-empty.
	 *
	 * @param metadata the metadata string
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
	 * Returns the unique identifier specified by the metadata.<br />
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
