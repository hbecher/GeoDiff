package com.bbfos.hbecher.geodiff.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.filosganga.geogson.model.*;
import com.github.filosganga.geogson.model.positions.LinearPositions;
import com.github.filosganga.geogson.model.positions.Positions;
import com.github.filosganga.geogson.model.positions.SinglePosition;

public class Utils
{
	private Utils()
	{
	}

	public static <T> List<T> collect(Iterable<T> iterable)
	{
		List<T> collected = new ArrayList<>();

		for(T t : iterable)
		{
			collected.add(t);
		}

		return collected;
	}

	public static <T> List<Integer> matching(List<T> list, T that)
	{
		return IntStream.range(0, list.size()).filter(i -> list.get(i).equals(that)).boxed().collect(Collectors.toList());
	}

	public static boolean equalsGeometry(Geometry<? extends Positions> g1, Geometry<? extends Positions> g2)
	{
		if(g1.size() != g2.size())
		{
			return false;
		}

		Geometry.Type type = g1.type();

		if(type != g2.type())
		{
			return false;
		}

		switch(type)
		{
			case POLYGON:
			{
				Polygon p1 = (Polygon) g1, p2 = (Polygon) g2;

				return equalsCyclical(p1.perimeter(), p2.perimeter()) && equalsIgnoreOrder(collect(p1.holes()), collect(p2.holes()));
			}

			case LINEAR_RING:
			{
				LinearRing lr1 = (LinearRing) g1, lr2 = (LinearRing) g2;

				return equalsCyclical(lr1, lr2);
			}

			case LINE_STRING:
			{
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
				LinearPositions lp1 = mp1.positions(), lp2 = mp2.positions();

				return equalsCyclical(lp1, lp2);
			}

			case MULTI_POLYGON:
			{
				MultiPolygon mp1 = (MultiPolygon) g1, mp2 = (MultiPolygon) g2;

				return equalsIgnoreOrder(collect(mp1.polygons()), collect(mp2.polygons()));
			}

			case MULTI_LINE_STRING:
			{
				MultiLineString mls1 = (MultiLineString) g1, mls2 = (MultiLineString) g2;

				return equalsIgnoreOrder(collect(mls1.lineStrings()), collect(mls2.lineStrings()));
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
