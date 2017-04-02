package com.bbfos.hbecher.geodiff.parser;

import java.util.List;

import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.metadata.Metadata;

/**
 * This class defines the basic behavior of a parser.
 */
public abstract class Parser
{
	protected final Metadata metadata;
	private final String fileA, fileB;

	/**
	 * Base constructor.
	 *
	 * @param fileA    the first file
	 * @param fileB    the second file
	 * @param metadata additional data for the parser (such as the unique identifier)
	 */
	public Parser(String fileA, String fileB, Metadata metadata)
	{
		if(fileA == null || fileB == null)
		{
			throw new NullPointerException();
		}

		this.fileA = fileA;
		this.fileB = fileB;
		this.metadata = metadata;
	}

	/**
	 * Parses the given files.
	 *
	 * @return the parsed elements
	 * @throws ParseException if an exception occurs while parsing one of the files
	 */
	public final ParsedElements parse() throws ParseException
	{
		List<Element> parsedA = parseFile(fileA), parsedB = parseFile(fileB);

		return new ParsedElements(parsedA, parsedB);
	}

	/**
	 * Parses one file.
	 *
	 * @param file the file
	 * @return the list of parsed elements from this file
	 * @throws ParseException if an exception occurs while parsing
	 */
	public abstract List<Element> parseFile(String file) throws ParseException;
}
