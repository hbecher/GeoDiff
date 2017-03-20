package com.bbfos.hbecher.geodiff.elements;

/**
 * Represents a coordinate in the geographic coordinate system.
 */
public class Coordinate
{
	/**
	 * The longitide
	 */
	private final double lon;
	/**
	 * The latitude
	 */
	private final double lat;
	/**
	 * The elevation (optional, {@code 0} if not specified)
	 */
	private final double elev;

	/**
	 * Creates a new {@code Coordinate} with the specified longitude and latitude.<br />
	 * The elevation is set to {@code 0}.
	 *
	 * @param lon the longitude
	 * @param lat the latitude
	 * @see Coordinate#Coordinate(double, double, double)
	 */
	public Coordinate(double lon, double lat)
	{
		this(lon, lat, 0.0D);
	}

	/**
	 * Creates a new {@code Coordinate} with the specified longitude, latitude and elevation.<br />
	 *
	 * @param lon  the longitude
	 * @param lat  the latitude
	 * @param elev the elevation
	 * @see Coordinate#Coordinate(double, double)
	 */
	public Coordinate(double lon, double lat, double elev)
	{
		this.lon = lon;
		this.lat = lat;
		this.elev = elev;
	}

	/**
	 * Returns the longitude of this coordinate.
	 *
	 * @return The longitude
	 */
	public double getLongitude()
	{
		return lon;
	}

	/**
	 * Returns the latitude of this coordinate.
	 *
	 * @return The latitude
	 */
	public double getLatitude()
	{
		return lat;
	}

	/**
	 * Returns the elevation of this coordinate.
	 *
	 * @return The elevation
	 */
	public double getElevation()
	{
		return elev;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}

		if(!(o instanceof Coordinate))
		{
			return false;
		}

		Coordinate that = (Coordinate) o;

		return Double.compare(that.lon, lon) == 0 && Double.compare(that.lat, lat) == 0 && Double.compare(that.elev, elev) == 0;
	}

	@Override
	public int hashCode()
	{
		long temp = Double.doubleToLongBits(lon);
		int result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lat);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(elev);
		result = 31 * result + (int) (temp ^ (temp >>> 32));

		return result;
	}

	@Override
	public String toString()
	{
		return "Coordinate{lon=" + getLongitude() + ", lat=" + getLatitude() + ", elev=" + getElevation() + '}';
	}
}
