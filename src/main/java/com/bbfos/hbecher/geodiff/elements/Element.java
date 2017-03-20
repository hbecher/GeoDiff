package com.bbfos.hbecher.geodiff.elements;

import java.util.List;
import java.util.Objects;

import com.bbfos.hbecher.geodiff.geojson.GeoJsonElement;
import com.bbfos.hbecher.geodiff.states.State;

/**
 * Represents an element.
 */
public abstract class Element
{
	private State state = State.INACTION;

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
	 * Returns the list of {@link Coordinate coordinates} of this element.
	 *
	 * @return The type
	 * @see Coordinate
	 */
	public abstract List<Coordinate> getCoordinates();

	/**
	 * Converts this element to a {@link GeoJsonElement}.
	 *
	 * @return The GeoJsonElement representation of this element
	 * @see GeoJsonElement
	 */
	public abstract GeoJsonElement toGeoJsonElement();

	/**
	 * Returns the state of this element.
	 *
	 * @return The state.
	 */
	public State getState()
	{
		return state;
	}

	/**
	 * Sets the state of this element.
	 *
	 * @param state The new state.
	 */
	public void setState(State state)
	{
		this.state = state == null ? State.INACTION : state;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == this)
		{
			return true;
		}

		if(!(o instanceof Element))
		{
			return false;
		}

		Element that = (Element) o;

		return getId().equals(that.getId()) && getType().equals(that.getType()) && getCoordinates().equals(that.getCoordinates());
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
