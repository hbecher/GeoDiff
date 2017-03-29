package com.bbfos.hbecher.geodiff.element;

import java.util.Objects;

import com.bbfos.hbecher.geodiff.geojson.GeoJsonElement;

/**
 * Represents an element of any type (that can be GeoJSON, CSV, etc.).<br>
 * This is basically an accessor to all properties needed for the computation.
 */
public abstract class Element
{
	private Status status = Status.UNDEFINED; // undefined before computation

	/**
	 * Returns the {@link Identifier unique identifier} of this element.
	 *
	 * @return The unique identifier
	 * @see Identifier
	 */
	public abstract Identifier getId();

	/**
	 * Returns the {@link Type type} of this element.
	 *
	 * @return The type
	 * @see Type
	 */
	public abstract Type getType();

	/**
	 * Returns the {@link Coordinates list of coordinates} of this element.
	 *
	 * @return The type
	 * @see Coordinates
	 */
	public abstract Coordinates getCoordinates();

	/**
	 * Converts this element to a {@link GeoJsonElement}.
	 *
	 * @return The GeoJsonElement representation of this element
	 * @see GeoJsonElement
	 */
	public abstract GeoJsonElement toGeoJsonElement();

	/**
	 * Returns the status of this element.
	 *
	 * @return The status.
	 */
	public Status getStatus()
	{
		return status;
	}

	/**
	 * Sets the status of this element.
	 *
	 * @param status The new status.
	 */
	public void setStatus(Status status)
	{
		this.status = status == null ? Status.UNDEFINED : status;
	}

	/**
	 * Two elements are considered equal (or equivalent) if
	 * <ul>
	 * <li>they have the same {@link Identifier}</li>
	 * <li>they are of the same {@link Type}</li>
	 * <li>their {@link Coordinates} are equivalent (it depends on their type)</li>
	 * </ul>
	 *
	 * @param that the other {@link Element}
	 * @return {@code true} if they are equivalent, {@code false} otherwise.
	 */
	public boolean equals(Element that)
	{
		return that == this || getId().equals(that.getId()) && getType().equals(that.getType()) && getCoordinates().equals(that.getCoordinates());
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Element && equals((Element) o);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getId(), getType());
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{" + "id=" + getId() + ", type=" + getType() + "}";
	}
}
