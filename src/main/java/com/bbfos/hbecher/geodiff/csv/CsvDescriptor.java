package com.bbfos.hbecher.geodiff.csv;

/**
 * This class holds the descriptor of a CSV file (i.e the first line).<br><br>
 * For a descriptor to be valid, it needs the unique identifier, longitude and latitude keys.<br>
 * These are specified using the metadata.
 */
class CsvDescriptor
{
	private final String[] properties;
	private final int id, lonId, latId;

	/**
	 * The constructor.
	 *
	 * @param properties the properties describing the file
	 * @param id         the property for uid
	 * @param lonId      the property for longitude
	 * @param latId      the property for latitude
	 * @throws IllegalArgumentException if one of the given properties id invalid (i.e not present in {@code properties})
	 */
	public CsvDescriptor(String[] properties, String id, String lonId, String latId)
	{
		this.properties = properties;
		this.id = getIndex(id);
		this.lonId = getIndex(lonId);
		this.latId = getIndex(latId);
	}

	public int getId()
	{
		return id;
	}

	public int getLonId()
	{
		return lonId;
	}

	public int getLatId()
	{
		return latId;
	}

	public String getIdProperty()
	{
		return getProperty(id);
	}

	public String getProperty(int index)
	{
		return properties[index];
	}

	/**
	 * Returns the index of the property.
	 *
	 * @param property the property
	 * @return Its index.
	 * @throws IllegalArgumentException if the given property is not recognized
	 */
	private int getIndex(String property)
	{
		for(int i = 0; i < properties.length; i++)
		{
			if(properties[i].equals(property))
			{
				return i;
			}
		}

		throw new IllegalArgumentException("Invalid CSV descriptor");
	}

	/**
	 * Returns the number of properties in this descriptor.
	 *
	 * @return The number of properties.
	 */
	public int getNumOfProperties()
	{
		return properties.length;
	}
}
