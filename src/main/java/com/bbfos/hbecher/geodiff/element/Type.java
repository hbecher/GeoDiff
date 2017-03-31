package com.bbfos.hbecher.geodiff.element;

import java.util.Arrays;

import com.github.filosganga.geogson.model.Geometry;

/**
 * Represents the name of an {@link Element}.<br>
 * Supported types are:
 * <ul>
 * <li>{@code Point}</li>
 * <li>{@code LineString}</li>
 * <li>{@code Polygon}</li>
 * <li>{@code MultiPoint}</li>
 * <li>{@code MultiLineString}</li>
 * <li>{@code MultiPolygon}</li>
 * <li>{@code GeometryCollection}</li>
 * </ul>
 * The {@code Unknown} name is used if name is not recognized.
 */
public enum Type
{
	POINT("Point"), LINE_STRING("LineString"), POLYGON("Polygon"), MULTI_POINT("MultiPoint"), MULTI_LINE_STRING("MultiLineString"), MULTI_POLYGON("MultiPolygon"), GEOMETRY_COLLECTION("GeometryCollection"), UNKNOWN("Unknown");

	private final String name;

	Type(String name)
	{
		this.name = name;
	}

	public static Type byName(String name)
	{
		return Arrays.stream(values()).filter(type -> type.name.equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public static Type from(Geometry.Type type)
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

			case LINEAR_RING:
			case LINE_STRING:
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

	public String getName()
	{
		return name;
	}
}
