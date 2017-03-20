package com.bbfos.hbecher.geodiff.elements;

/**
 * Represents the type of an {@link Element}.<br />
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
 * The {@code Unknown} type is used if type is not recognized.
 */
public enum Type
{
	POINT("Point"), LINE_STRING("LineString"), POLYGON("Polygon"), MULTI_POINT("MultiPoint"), MULTI_LINE_STRING("MultiLineString"), MULTI_POLYGON("MultiPolygon"), GEOMETRY_COLLECTION("GeometryCollection"), UNKNOWN("-");

	private final String type;

	Type(String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return "Type{type=" + type + "}";
	}
}
