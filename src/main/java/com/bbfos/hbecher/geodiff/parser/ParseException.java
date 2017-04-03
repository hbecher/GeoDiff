package com.bbfos.hbecher.geodiff.parser;

/**
 * Thrown when a exception occurs while parsing.
 */
public class ParseException extends Exception
{
	public ParseException()
	{
		super();
	}

	public ParseException(String message)
	{
		super(message);
	}

	public ParseException(Throwable cause)
	{
		super(cause);
	}

	public ParseException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
