package com.bbfos.hbecher.geodiff.metadata;

import java.util.HashMap;
import java.util.Map;

import com.bbfos.hbecher.geodiff.parsers.ParseException;

public final class MetadataParser
{
	/**
	 * The characters used to delimit the entries and key-value pairs
	 */
	private static final char ENTRY_SEPARATOR = ';', PAIR_SEPARATOR = '=';
	/**
	 * The escape character.
	 */
	private static final char ESCAPE = '\\';

	/**
	 * Parses the metadata and puts each key-value pair in the map.
	 *
	 * @param metadata the raw metadata string
	 */
	public Metadata parse(String metadata) throws ParseException
	{
		if(metadata == null)
		{
			return null;
		}

		if(metadata.isEmpty())
		{
			throw new ParseException("Metadata can't be empty");
		}

		Map<String, String> metamap = new HashMap<>();
		// add ENTRY_SEPARATOR char to the end of metadata if absent, so  there is no extra step at the end of the for-loop
		char[] chars = (metadata.endsWith(String.valueOf(ENTRY_SEPARATOR)) ? metadata : metadata + ENTRY_SEPARATOR).toCharArray();
		StringBuilder key = new StringBuilder(), value = new StringBuilder(); // the key and value containers
		boolean keyRead = false; // if the key has been read
		boolean escape = false; // if the next character should be escaped

		for(char c : chars)
		{
			if(escape)
			{
				(keyRead ? value : key).append(c);

				escape = false;
			}
			else
			{
				switch(c)
				{
					case ESCAPE:
					{
						escape = true;

						break;
					}

					case PAIR_SEPARATOR:
					{
						keyRead = true;

						break;
					}

					case ENTRY_SEPARATOR:
					{
						if(key.length() == 0)
						{
							throw new ParseException("Invalid metadata: empty key");
						}

						// empty values are tolerated (flags for example)

						metamap.put(key.toString(), value.toString());

						// or just create new StringBuilders
						key.delete(0, key.length());
						value.delete(0, value.length());

						keyRead = false;

						break;
					}

					default:
					{
						(keyRead ? value : key).append(c);
					}
				}
			}
		}

		return new Metadata(metamap);
	}
}
