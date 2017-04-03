package com.bbfos.hbecher.geodiff.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.metadata.Metadata;
import com.bbfos.hbecher.geodiff.parser.ParseException;
import com.bbfos.hbecher.geodiff.parser.Parser;

/**
 * The CSV parser.<br><br>
 * This parser is only compatible with Points.<br>
 * Three properties are required to parse CSV elements, extracted from the metadata:
 * <ul>
 * <li>the uid ("id")</li>
 * <li>the longitude key ("lon")</li>
 * <li>the latitude key ("lat")</li>
 * </ul>
 */
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

		id = metadata.getId();
		lonId = metadata.getMetadata("lon");
		latId = metadata.getMetadata("lat");

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

			String[] properties = getProperties(line);
			CsvDescriptor descriptor = new CsvDescriptor(properties, id, lonId, latId);
			int numOfProps = properties.length, count = 1;
			elements = new ArrayList<>();

			while((line = reader.readLine()) != null)
			{
				properties = getProperties(line);

				if(properties.length != numOfProps)
				{
					throw new ParseException("At line " + count + ": received " + properties.length + " properties, expected " + numOfProps);
				}

				CsvElement element = new CsvElement(properties, descriptor);

				elements.add(element);
				count++;
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
