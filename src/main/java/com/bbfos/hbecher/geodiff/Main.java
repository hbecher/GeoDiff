package com.bbfos.hbecher.geodiff;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.bbfos.hbecher.geodiff.csv.CsvParser;
import com.bbfos.hbecher.geodiff.metadata.Metadata;
import com.bbfos.hbecher.geodiff.metadata.MetadataParser;
import com.bbfos.hbecher.geodiff.parsers.ParseException;
import com.bbfos.hbecher.geodiff.parsers.ParsedElements;
import com.bbfos.hbecher.geodiff.parsers.Parser;
import joptsimple.*;

/**
 * The main class of the program.
 */
public class Main
{
	public static void main(String[] args)
	{
		OptionParser optionParser = new OptionParser();

		optionParser.allowsUnrecognizedOptions();

		AbstractOptionSpec<Void> help = optionParser.accepts("help").forHelp();
		ArgumentAcceptingOptionSpec<String> format = optionParser.accepts("format", "Currently unused, will be used to explicitly specify the format of the input files").withRequiredArg();
		ArgumentAcceptingOptionSpec<String> metadata = optionParser.accepts("metadata", "Some additional information for the parser (if a custom identifier is used, it can be specified here using 'id' - please note that an uid is required for the program to work)").withRequiredArg().describedAs("META");
		ArgumentAcceptingOptionSpec<File> output = optionParser.accepts("output", "The output file").withRequiredArg().ofType(File.class).defaultsTo(new File("geodiff.geojson"));
		NonOptionArgumentSpec<String> nonOption = optionParser.nonOptions();

		OptionSet optionSet;

		try
		{
			optionSet = optionParser.parse(args);
		}
		catch(OptionException e)
		{
			showHelp("Error: " + e.getMessage(), optionParser);

			return;
		}

		if(optionSet.has(help))
		{
			showHelp(null, optionParser);
		}
		else
		{
			List<String> leftoverArgs = optionSet.valuesOf(nonOption);
			int leftOverSize = leftoverArgs.size();

			if(leftOverSize < 2)
			{
				showHelp("Not enough arguments", optionParser);
			}
			else if(leftOverSize > 2)
			{
				showHelp("Too many arguments", optionParser);
			}
			else
			{
				String fileA = leftoverArgs.get(0), fileB = leftoverArgs.get(1);
				Metadata meta;

				if(optionSet.has(metadata))
				{
					MetadataParser metadataParser = new MetadataParser();

					try
					{
						meta = metadataParser.parse(metadata.value(optionSet));
					}
					catch(ParseException e)
					{
						showHelp(e.getMessage(), optionParser);

						return;
					}
				}
				else
				{
					meta = null;
				}

				// The program will (probably?) be able to infer the format in the future
				// Parser parser = new GeoJsonParser(fileA, fileB, metadata.value(optionSet));
				Parser parser = new CsvParser(fileA, fileB, meta);
				ParsedElements parsedElements;

				try
				{
					parsedElements = parser.parse();
				}
				catch(ParseException e)
				{
					throw new RuntimeException(e);
				}

				GeoDiff geoDiff = new GeoDiff(parsedElements);
				Features delta = geoDiff.delta();

				Result result = new Result(delta);

				result.writeFile(output.value(optionSet));
			}
		}
	}

	/**
	 * Shows help to the user.
	 *
	 * @param errorMsg     an additional error message
	 * @param optionParser the {@code OptionParser}
	 */
	private static void showHelp(String errorMsg, OptionParser optionParser)
	{
		if(errorMsg != null)
		{
			System.out.println(errorMsg);
		}

		try(PrintWriter writer = new PrintWriter(System.out))
		{
			optionParser.printHelpOn(writer);
		}
		catch(IOException ignored)
		{
		}
	}
}
