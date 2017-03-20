package com.bbfos.hbecher.geodiff.parsers;

import java.util.List;

import com.bbfos.hbecher.geodiff.elements.Element;
import com.bbfos.hbecher.geodiff.metadata.Metadata;

public abstract class Parser
{
	protected final Metadata metadata;
	private final String fileA, fileB;

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

	public final ParsedElements parse() throws ParseException
	{
		List<Element> parsedA = parseFile(fileA), parsedB = parseFile(fileB);

		return new ParsedElements(parsedA, parsedB);
	}

	public abstract List<Element> parseFile(String file) throws ParseException;
}
