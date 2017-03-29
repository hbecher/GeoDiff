package com.bbfos.hbecher.geodiff;

import static com.bbfos.hbecher.geodiff.geojson.GeoJsonParser.GSON;

import java.io.*;
import java.util.List;

import com.github.filosganga.geogson.model.Feature;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

/**
 * Represents the final result, to be written to the output file.
 */
public class FeatureCollectionWrapper
{
	/**
	 * The type of GeoJSON object we are writing.
	 */
	private final String type = "FeatureCollection"; // we force the type to be written
	private final List<Feature> features;

	public FeatureCollectionWrapper(Delta delta)
	{
		this.features = delta.getFeatures();
	}

	public void write(Writer output)
	{
		try(JsonWriter writer = GSON.newJsonWriter(output))
		{
			GSON.toJson(this, new TypeToken<FeatureCollectionWrapper>()
			{
			}.getType(), writer);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void print(Object output)
	{
		if(output == null)
		{
			throw new NullPointerException();
		}

		Writer writer;

		if(output instanceof Writer)
		{
			writer = (Writer) output;
		}
		else if(output instanceof OutputStream)
		{
			writer = new OutputStreamWriter((OutputStream) output);
		}
		else if(output instanceof File)
		{
			File file = (File) output;

			try
			{
				writer = new FileWriter(file);
			}
			catch(IOException e)
			{
				throw new RuntimeException("Could not write to file " + file.getAbsolutePath(), e);
			}
		}
		else
		{
			throw new IllegalArgumentException("Unrecognized output type, received object of class " + output.getClass());
		}

		write(writer);
	}
}
