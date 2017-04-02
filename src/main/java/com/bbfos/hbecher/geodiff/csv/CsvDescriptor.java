package com.bbfos.hbecher.geodiff.csv;

/**
 * Work in progress.
 */
class CsvDescriptor
{
	private final String[] properties;
	private final int id, lonId, latId;

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

	public int getNumOfProperties()
	{
		return properties.length;
	}
}
