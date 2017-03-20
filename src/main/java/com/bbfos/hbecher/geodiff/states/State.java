package com.bbfos.hbecher.geodiff.states;

import com.google.gson.JsonPrimitive;

/**
 * Represents the state of an element.
 */
public enum State
{
	ADDITION("add"), DELETION("del"), INACTION("id"), MODIFICATION("mod");

	private final String name;

	State(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the property name of this {@code State}.
	 *
	 * @return The property name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the state as a Json object.
	 *
	 * @return The Json object.
	 */
	public JsonPrimitive asJsonElement()
	{
		return new JsonPrimitive(name);
	}

	@Override
	public String toString()
	{
		return "State{name=" + name + "}";
	}
}
