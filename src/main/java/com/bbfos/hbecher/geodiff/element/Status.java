package com.bbfos.hbecher.geodiff.element;

import java.util.Arrays;

import com.google.gson.JsonPrimitive;

/**
 * Represents the status of an element after computing the differences.
 */
public enum Status
{
	ADDITION("add"), DELETION("del"), OLD_VERSION("old"), NEW_VERSION("new"), /**
 * Kept for backward compatibility
 */@Deprecated MODIFICATION("mod"), IDENTICAL("id"), UNDEFINED("undef");

	private final String name;

	Status(String name)
	{
		this.name = name;
	}

	public static Status byName(String name)
	{
		return Arrays.stream(values()).filter(status -> status.name.equalsIgnoreCase(name)).findFirst().orElse(null);
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
