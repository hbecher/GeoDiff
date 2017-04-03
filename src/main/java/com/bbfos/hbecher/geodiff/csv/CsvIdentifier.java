package com.bbfos.hbecher.geodiff.csv;

import com.bbfos.hbecher.geodiff.element.Identifier;

/**
 * Represents the unique identifier of a {@link CsvElement}.
 *
 * @see CsvElement
 */
public class CsvIdentifier extends Identifier
{
	private final String id;

	/**
	 * Creates a CSV identifier by extracting the uid property of {@code properties} using the given {@code descriptor}.
	 *
	 * @param descriptor the {@code CsvDescriptor}
	 * @param properties the properties
	 */
	public CsvIdentifier(CsvDescriptor descriptor, String[] properties)
	{
		this(properties[descriptor.getId()]);
	}

	public CsvIdentifier(String id)
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
