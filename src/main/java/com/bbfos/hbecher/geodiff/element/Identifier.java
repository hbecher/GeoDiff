package com.bbfos.hbecher.geodiff.element;

/**
 * Represents the unique identifier of an {@link Element}.<br><br>
 * A unique identifier is required for the program to work,
 * as it needs a way to match each Feature from one set to one of the other.
 *
 * @see com.bbfos.hbecher.geodiff.geojson.GeoJsonIdentifier GeoJsonIdentifier
 * @see com.bbfos.hbecher.geodiff.csv.CsvIdentifier CsvIdentifier
 */
public abstract class Identifier
{
	/**
	 * Returns the object that is used as the identifier.
	 *
	 * @return The identifier object.
	 */
	public abstract Object getId();

	/**
	 * Returns the textual representation of the object used as the identifier.
	 *
	 * @return The textual representation of the identifier object.
	 */
	public String asString()
	{
		return getId().toString();
	}

	@Override
	public boolean equals(Object o)
	{
		return o == this || o instanceof Identifier && getId().equals(((Identifier) o).getId());
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{" + "id=" + asString() + "}";
	}
}
