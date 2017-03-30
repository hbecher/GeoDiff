package com.bbfos.hbecher.geodiff.element;

import static com.bbfos.hbecher.geodiff.util.Utils.equalsGeometry;

import com.github.filosganga.geogson.model.Geometry;
import com.github.filosganga.geogson.model.positions.Positions;

/**
 * Represents the coordinates of an {@link Element}.
 */
public class Coordinates
{
	private final Geometry<? extends Positions> geometry;

	public Coordinates(Geometry<? extends Positions> geometry)
	{
		this.geometry = geometry;
	}

	public Geometry<? extends Positions> getGeometry()
	{
		return geometry;
	}

	public boolean equals(Coordinates that)
	{
		return equalsGeometry(geometry, that.geometry);
	}

	@Override
	public boolean equals(Object o)
	{
		return this == o || o instanceof Coordinates && equals((Coordinates) o);
	}

	@Override
	public int hashCode()
	{
		return geometry.hashCode();
	}

	@Override
	public String toString()
	{
		return geometry.toString();
	}
}
