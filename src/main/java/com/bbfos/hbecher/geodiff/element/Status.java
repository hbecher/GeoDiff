package com.bbfos.hbecher.geodiff.element;

import com.google.gson.JsonPrimitive;

/**
 * Represents the status of an element after computing the differences.
 */
public enum Status
{
	ADDITION("add"), DELETION("del"), IDENTICAL("id"), MODIFICATION("mod"), UNDEFINED("undef");

	private final String name;

	Status(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the property name of this {@code Status}.
	 *
	 * @return The property name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Returns the status as a Json object.
	 *
	 * @return The Json object.
	 */
	public JsonPrimitive toJson()
	{
		return new JsonPrimitive(name);
	}
}
