package com.bbfos.hbecher.geodiff.util;

import static com.bbfos.hbecher.geodiff.util.Utils.collect;
import static com.bbfos.hbecher.geodiff.util.Utils.matching;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.github.filosganga.geogson.model.*;
import com.github.filosganga.geogson.model.positions.LinearPositions;
import com.github.filosganga.geogson.model.positions.Positions;
import com.github.filosganga.geogson.model.positions.SinglePosition;

/**
 * This class contains the various methods used to determine geometry equivalence.
 */
public class Equals
{
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
		if(g1 == g2)
		{
			return true;
		}

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
				LinearPositions pos1 = p1.perimeter().positions(), pos2 = p2.perimeter().positions();
				List<SinglePosition> sp1 = collect(pos1.children()).subList(0, pos1.size() - 1), sp2 = collect(pos2.children()).subList(0, pos2.size() - 1);

				return equalsCyclical(sp1, sp2) && equalsOrder(collect(p1.holes()), collect(p2.holes()));
			}

			case LINEAR_RING:
			{
				// two linear rings are equivalent <==> they describe the same coordinates up to a shift
				LinearRing lr1 = (LinearRing) g1, lr2 = (LinearRing) g2;
				LinearPositions pos1 = lr1.positions(), pos2 = lr2.positions();
				List<SinglePosition> sp1 = collect(pos1.children()).subList(0, pos1.size() - 1), sp2 = collect(pos2.children()).subList(0, pos2.size() - 1);

				return equalsCyclical(sp1, sp2);
			}

			case LINE_STRING:
			{
				// two line strings are equivalent <==> they describe the same coordinates up to a shift if they are closed,
				// otherwise they have the same coordinates in order.
				LineString ls1 = (LineString) g1, ls2 = (LineString) g2;

				if(ls1.isClosed() && ls2.isClosed())
				{
					LinearPositions pos1 = ls1.positions(), pos2 = ls2.positions();
					List<SinglePosition> sp1 = collect(pos1.children()).subList(0, pos1.size() - 1), sp2 = collect(pos2.children()).subList(0, pos2.size() - 1);

					return equalsCyclical(sp1, sp2);
				}

				break;
			}

			case MULTI_POINT:
			{
				MultiPoint mp1 = (MultiPoint) g1, mp2 = (MultiPoint) g2;

				return equalsCyclical(collect(mp1.positions().children()), collect(mp2.positions().children())) || equalsOrder(collect(mp1.points()), collect(mp2.points()));
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

	private static <T extends Geometry<? extends Positions>> boolean equalsIgnoreOrder(List<T> l1, List<T> l2)
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

	private static <T extends Geometry<? extends Positions>> boolean equalsOrder(List<T> l1, List<T> l2)
	{
		return l1 == l2 || l1.size() == l2.size() && IntStream.range(0, l1.size()).allMatch(i -> equalsGeometry(l1.get(i), l2.get(i)));
	}

	private static boolean equalsCyclical(List<SinglePosition> lp1, List<SinglePosition> lp2)
	{
		if(lp1 == lp2)
		{
			return true;
		}

		if(lp1.size() != lp2.size())
		{
			return false;
		}

		List<Integer> startingPoints = matching(lp2, lp1.get(0));

		for(Integer start : startingPoints)
		{
			Iterator<SinglePosition> i1 = lp1.iterator();
			ShiftedIterator<SinglePosition> i2 = new ShiftedIterator<>(lp2, start);
			boolean equals = true;

			while(i1.hasNext()) // lp1.size() == lp2.size()
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

		return false;
	}
}
