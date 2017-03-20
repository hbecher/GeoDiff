package com.bbfos.hbecher.geodiff.geojson;

import com.bbfos.hbecher.geodiff.elements.Type;
import com.github.filosganga.geogson.model.Geometry;

public class GeoJsonUtils
{
	public static Type getType(Geometry.Type type)
	{
		switch(type)
		{
			case POINT:
			{
				return Type.POINT;
			}

			case MULTI_POINT:
			{
				return Type.MULTI_POINT;
			}

			case LINE_STRING:
			{
				return Type.LINE_STRING;
			}

			case LINEAR_RING:
			{
				return Type.LINE_STRING;
			}

			case MULTI_LINE_STRING:
			{
				return Type.MULTI_LINE_STRING;
			}

			case POLYGON:
			{
				return Type.POLYGON;
			}

			case MULTI_POLYGON:
			{
				return Type.MULTI_POLYGON;
			}

			case GEOMETRY_COLLECTION:
			{
				return Type.GEOMETRY_COLLECTION;
			}

			default:
			{
				return Type.UNKNOWN;
			}
		}
	}
}
