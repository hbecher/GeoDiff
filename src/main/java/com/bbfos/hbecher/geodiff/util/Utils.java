package com.bbfos.hbecher.geodiff.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.element.Status;
import com.github.filosganga.geogson.model.*;
import com.github.filosganga.geogson.model.positions.LinearPositions;
import com.github.filosganga.geogson.model.positions.Positions;
import com.github.filosganga.geogson.model.positions.SinglePosition;

/**
 * This class contains various utility methods used throughout the program.
 */
public class Utils
{
	/**
	 * The GeoDiff status property.
	 */
	public static final String GEODIFF_PROPERTY = "geodiff-type";

	private Utils()
	{
	}

	/**
	 * Returns the decorated features.
	 *
	 * @return The decorated features.
	 */
	public static List<Feature> toFeatures(List<Element> elements)
	{
		return elements.stream().map(element -> element.toGeoJsonElement().getFeature().withProperty(GEODIFF_PROPERTY, element.getStatus().toJson())).collect(Collectors.toList());
	}

	public static EnumSet<Status> getFilters(boolean add, boolean del, boolean mod, boolean id)
	{
		EnumSet<Status> statuses = EnumSet.noneOf(Status.class);

		if(add)
		{
			statuses.add(Status.ADDITION);
		}

		if(del)
		{
			statuses.add(Status.DELETION);
		}

		if(mod)
		{
			statuses.add(Status.MODIFICATION);
		}

		if(id)
		{
			statuses.add(Status.IDENTICAL);
		}

		return statuses;
	}

	/**
	 * Returns a {@link List} containig all the elements returned in order by the {@link Iterable}.
	 *
	 * @param iterable the iterable
	 * @param <T>      the type of elements
	 * @return A list containing the elements of the iterable.
	 */
	public static <T> List<T> collect(Iterable<T> iterable)
	{
		List<T> collected = new ArrayList<>();

		for(T t : iterable)
		{
			collected.add(t);
		}

		return collected;
	}

	/**
	 * Returns a list containing all the elements from {@code list} that are equal to {@code that}, i.e all elements that satisfy
	 * <code>element.equals(that) == true</code>
	 *
	 * @param list the list
	 * @param that the element to match
	 * @param <T>  the type of elements
	 * @return The list of all matching elements.
	 */
	public static <T> List<Integer> matching(List<T> list, T that)
	{
		return IntStream.range(0, list.size()).filter(i -> list.get(i).equals(that)).boxed().collect(Collectors.toList());
	}

	/**
	 * Returns {@code true} if both {@link Geometry Geometries} are equivalent.
	 * The definition of equivalent depends on the type of geometry.
	 *
	 * @param g1 the first {@code Geometry}
	 * @param g2 the second {@code Geometry}
	 * @return {@code true} if both geometries are equal.
	 */
	public static boolean equalsGeometry(Geometry<? extends Positions> g1, Geometry<? extends Positions> g2)
	{
		if(g1.type() != g2.type() || g1.size() != g2.size())
		{
			// not the same type, different number of coordinates
			return false;
		}

		switch(g1.type())
		{
			case POLYGON:
			{
				// two polygons are equivalent <==> their perimeters describe the same coordinates up to a shift and
				// the same goes for their holes, but they don't need to be in the same order.
				Polygon p1 = (Polygon) g1, p2 = (Polygon) g2;

				return equalsCyclical(p1.perimeter(), p2.perimeter()) && equalsOrder(collect(p1.holes()), collect(p2.holes()));
			}

			case LINEAR_RING:
			{
				// two linear rings are equivalent <==> they describe the same coordinates up to a shift
				LinearRing lr1 = (LinearRing) g1, lr2 = (LinearRing) g2;

				return equalsCyclical(lr1, lr2);
			}

			case LINE_STRING:
			{
				// two line strings are equivalent <==> they describe the same coordinates up to a shift if they are closed,
				// otherwise they have the same coordinates in order.
				LineString ls1 = (LineString) g1, ls2 = (LineString) g2;

				if(ls1.isClosed() && ls2.isClosed())
				{
					return equalsCyclical(ls1.positions(), ls2.positions());
				}

				break;
			}

			case MULTI_POINT:
			{
				MultiPoint mp1 = (MultiPoint) g1, mp2 = (MultiPoint) g2;

				return equalsCyclical(mp1, mp2) || equalsOrder(collect(mp1.points()), collect(mp2.points()));
			}

			case MULTI_POLYGON:
			{
				MultiPolygon mp1 = (MultiPolygon) g1, mp2 = (MultiPolygon) g2;

				return equalsOrder(collect(mp1.polygons()), collect(mp2.polygons()));
			}

			case MULTI_LINE_STRING:
			{
				MultiLineString mls1 = (MultiLineString) g1, mls2 = (MultiLineString) g2;

				return equalsOrder(collect(mls1.lineStrings()), collect(mls2.lineStrings()));
			}
		}

		return g1.equals(g2);
	}

	private static <T extends Geometry<? extends Positions>> boolean hasCounterpart(List<T> list, T toTest, boolean[] visited)
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(!visited[i] && equalsGeometry(list.get(i), toTest))
			{
				visited[i] = true;

				return true;
			}
		}

		return false;
	}

	public static <T extends Geometry<? extends Positions>> boolean equalsIgnoreOrder(List<T> l1, List<T> l2)
	{
		if(l1 == l2)
		{
			return true;
		}

		if(l1.size() != l2.size())
		{
			return false;
		}

		boolean[] visited = new boolean[l1.size()];

		for(T t : l1)
		{
			if(!hasCounterpart(l2, t, visited))
			{
				return false;
			}
		}

		for(boolean b : visited)
		{
			if(!b)
			{
				return false;
			}
		}

		return true;
	}

	public static <T extends Geometry<? extends Positions>> boolean equalsOrder(List<T> l1, List<T> l2)
	{
		return l1 == l2 || l1.size() == l2.size() && IntStream.range(0, l1.size()).allMatch(i -> equalsGeometry(l1.get(i), l2.get(i)));
	}

	public static boolean equalsCyclical(LinearGeometry l1, LinearGeometry l2)
	{
		return l1 == l2 || l1.size() == l2.size() && (l1.size() == 0 || equalsCyclical(l1.positions(), l2.positions()));
	}

	public static boolean equalsCyclical(LinearPositions lp1, LinearPositions lp2)
	{
		if(lp1 == lp2)
		{
			return true;
		}

		if(lp1.isClosed() && lp2.isClosed())
		{
			List<SinglePosition> sps1 = collect(lp1.children()), sps2 = collect(lp2.children());
			List<Integer> startingPoints = matching(sps2, sps1.get(0));

			for(Integer start : startingPoints)
			{
				Iterator<SinglePosition> i1 = sps1.iterator();
				ShiftedIterator<SinglePosition> i2 = new ShiftedIterator<>(sps2, start);
				boolean equals = true;

				while(i1.hasNext() && i2.hasNext()) // just in case
				{
					SinglePosition sp1 = i1.next(), sp2 = i2.next();

					if(!sp1.equals(sp2))
					{
						equals = false;

						break;
					}
				}

				if(equals)
				{
					return true;
				}
			}
		}

		return false;
	}
}
