package com.bbfos.hbecher.geodiff.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bbfos.hbecher.geodiff.elements.Element;
import com.bbfos.hbecher.geodiff.metadata.Metadata;
import com.bbfos.hbecher.geodiff.parsers.ParseException;
import com.bbfos.hbecher.geodiff.parsers.Parser;

public class CsvParser extends Parser
{
	private final String id, lonId, latId;

	public CsvParser(String fileA, String fileB, Metadata metadata)
	{
		super(fileA, fileB, metadata);

		if(metadata == null)
		{
			throw new NullPointerException("Metadata is required for the uid, longitude and latitude descriptors");
		}

		id = this.metadata.getId();
		lonId = this.metadata.getMetadata("lon");
		latId = this.metadata.getMetadata("lat");

		if(id == null || lonId == null || latId == null)
		{
			throw new IllegalArgumentException("Invalid metadata");
		}
	}

	@Override
	public List<Element> parseFile(String file) throws ParseException
	{
		List<CsvElement> elements;

		try(BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			String line = reader.readLine();

			if(line == null)
			{
				throw new ParseException("Invalid CSV file");
			}

			CsvDescriptor descriptor = new CsvDescriptor(getProperties(line), id, lonId, latId);
			elements = new ArrayList<>();

			while((line = reader.readLine()) != null)
			{
				CsvElement element = new CsvElement(getProperties(line), descriptor);

				elements.add(element);
			}
		}
		catch(IOException e)
		{
			throw new ParseException(e);
		}

		return elements.stream().map(CsvElement::toGeoJsonElement).collect(Collectors.toList());
	}

	private String[] getProperties(String line)
	{
		List<String> properties = new ArrayList<>();
		char[] chars = line.toCharArray();
		StringBuilder word = new StringBuilder();
		boolean escape = false;

		for(char c : chars)
		{
			switch(c)
			{
				case '"':
				{
					escape = !escape;

					break;
				}

				case ',':
				{
					if(escape)
					{
						word.append(c);
					}
					else
					{
						properties.add(word.toString());

						word = new StringBuilder();
					}

					break;
				}

				default:
				{
					word.append(c);
				}
			}
		}

		if(word.length() > 0)
		{
			properties.add(word.toString());
		}

		return properties.toArray(new String[properties.size()]);
	}
}
