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
		ArgumentAcceptingOptionSpec<String> filters = optionParser.accepts("filters", "The list of statuses to keep, separated by commas - possible values: 'add' (additions), 'del' (deletions), 'old' (old versions), 'new' (new versions), 'mod' (all previous ones), 'id' (unchanged) and 'undef' (the ones that couldn't be processed, for some reason)").withRequiredArg().describedAs("FILTERS");
		ArgumentAcceptingOptionSpec<String> metadata = optionParser.accepts("metadata", "Some additional information for the parser").withRequiredArg().describedAs("META");
		ArgumentAcceptingOptionSpec<File> output = optionParser.accepts("output", "The output file (prints to the console if not specified)").withRequiredArg().ofType(File.class).describedAs("OUTPUT");
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
				EnumSet<Status> statuses;

				if(optionSet.has(filters))
				{
					statuses = EnumSet.noneOf(Status.class);

					for(String filter : optionSet.valueOf(filters).split(","))
					{
						Status status = Status.byName(filter);

						if(status == null)
						{
							showHelp("Received invalid filter: " + filter, optionParser);

							return;
						}

						statuses.add(status);
					}
				}
				else
				{
					statuses = EnumSet.allOf(Status.class);
				}

				Metadata meta;

				if(optionSet.has(metadata))
				{
					try
					{
						MetadataParser parser = new MetadataParser();

						meta = parser.parse(optionSet.valueOf(metadata));
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

				// we would specify what format we are reading in future versions
				// if more formats are supported
				String fileA = leftOverArgs.get(0), fileB = leftOverArgs.get(1);
				//Parser parser = new CsvParser(fileA, fileB, meta);
				ParsedElements parsedElements;

				try
				{
					GeoJsonParser parser = new GeoJsonParser(fileA, fileB, meta);

					parsedElements = parser.parse();
				}
				catch(ParseException e)
				{
					System.err.println(e.getMessage());

					return;
				}

				GeoDiff geoDiff = new GeoDiff(parsedElements);
				Delta delta = geoDiff.delta();

				FeatureCollectionWrapper result = new FeatureCollectionWrapper(delta, statuses);

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

		System.out.println("Usage: java -jar GeoDiff.jar [OPTIONS...] <file A> <file B>");

		try(PrintWriter writer = new PrintWriter(System.out))
		{
			optionParser.printHelpOn(writer);
		}
		catch(IOException ignored)
		{
		}
	}
}
