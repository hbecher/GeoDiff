package com.bbfos.hbecher.geodiff.element;

import static com.bbfos.hbecher.geodiff.util.Utils.*;

import com.github.filosganga.geogson.model.*;
import com.github.filosganga.geogson.model.positions.LinearPositions;
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
		if(geometry.size() != that.geometry.size())
		{
			return false;
		}

		Geometry.Type type = geometry.type();

		if(type != that.geometry.type())
		{
			return false;
		}

		switch(type)
		{
			case POLYGON:
			{
				Polygon p1 = (Polygon) geometry, p2 = (Polygon) that.geometry;

				return equalsCyclical(p1.perimeter(), p2.perimeter()) && equalsIgnoreOrder(collect(p1.holes()), collect(p2.holes()));
			}

			case LINEAR_RING:
			{
				LinearRing lr1 = (LinearRing) geometry, lr2 = (LinearRing) that.geometry;

				return equalsCyclical(lr1, lr2);
			}

			case LINE_STRING:
			{
				LineString ls1 = (LineString) geometry, ls2 = (LineString) that.geometry;

				if(ls1.isClosed() && ls2.isClosed())
				{
					return equalsCyclical(ls1.positions(), ls2.positions());
				}

				break;
			}

			case MULTI_POINT:
			{
				MultiPoint mp1 = (MultiPoint) geometry, mp2 = (MultiPoint) that.geometry;
				LinearPositions lp1 = mp1.positions(), lp2 = mp2.positions();

				return equalsCyclical(lp1, lp2);
			}

			case MULTI_POLYGON:
			{
				MultiPolygon mp1 = (MultiPolygon) geometry, mp2 = (MultiPolygon) that.geometry;

				return equalsIgnoreOrder(collect(mp1.polygons()), collect(mp2.polygons()));
			}

			case MULTI_LINE_STRING:
			{
				MultiLineString mls1 = (MultiLineString) geometry, mls2 = (MultiLineString) that.geometry;

				return equalsIgnoreOrder(collect(mls1.lineStrings()), collect(mls2.lineStrings()));
			}
		}

		return geometry.equals(that.geometry);
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
