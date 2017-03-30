package com.bbfos.hbecher.geodiff;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.List;

import com.bbfos.hbecher.geodiff.element.Status;
import com.bbfos.hbecher.geodiff.geojson.GeoJsonParser;
import com.bbfos.hbecher.geodiff.metadata.Metadata;
import com.bbfos.hbecher.geodiff.metadata.MetadataParser;
import com.bbfos.hbecher.geodiff.parser.ParseException;
import com.bbfos.hbecher.geodiff.parser.ParsedElements;
import com.bbfos.hbecher.geodiff.parser.Parser;
import joptsimple.*;

/**
 * The main class of the program.
 */
public class Main
{
	public static void main(String[] args)
	{
		// jopt simple magic
		OptionParser optionParser = new OptionParser();

		AbstractOptionSpec<Void> help = optionParser.accepts("help", "Shows this help message").forHelp();
		ArgumentAcceptingOptionSpec<String> format = optionParser.accepts("format", "Currently unused, will be used to explicitly specify the format of the input files").withRequiredArg();
		ArgumentAcceptingOptionSpec<String> metadata = optionParser.accepts("metadata", "Some additional information for the parser (if a custom identifier is used, it can be specified here using 'id' - please note that an uid is required for the program to work)").withRequiredArg().describedAs("META");
		ArgumentAcceptingOptionSpec<File> output = optionParser.accepts("output", "The output file (prints to the console if not specified)").withRequiredArg().ofType(File.class);
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
			List<String> leftOverArgs = optionSet.valuesOf(nonOption);
			int leftOverSize = leftOverArgs.size();

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
				String fileA = leftOverArgs.get(0), fileB = leftOverArgs.get(1);
				Metadata meta;

				if(optionSet.has(metadata))
				{
					MetadataParser metadataParser = new MetadataParser();

					try
					{
						meta = metadataParser.parse(optionSet.valueOf(metadata));
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

				// we would specify what format we are reading
				Parser parser = new GeoJsonParser(fileA, fileB, meta);
				//Parser parser = new CsvParser(fileA, fileB, meta);
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
				Delta delta = geoDiff.delta();
				FeatureCollectionWrapper result = new FeatureCollectionWrapper(delta, EnumSet.allOf(Status.class));

				if(optionSet.has(output))
				{
					result.writeTo(optionSet.valueOf(output));
				}
				else
				{
					result.writeTo(System.out);
				}
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

		System.out.println("Usage: java -jar GeoDiff.jar [OPTIONS] <file A> <file B>");

		try(PrintWriter writer = new PrintWriter(System.out))
		{
			optionParser.printHelpOn(writer);
		}
		catch(IOException ignored)
		{
		}
	}
}
