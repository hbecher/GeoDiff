package com.bbfos.hbecher.geodiff;

import static com.bbfos.hbecher.geodiff.geojson.GeoJsonParser.GSON;
import static com.bbfos.hbecher.geodiff.util.Utils.toFeatures;

import java.io.*;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.bbfos.hbecher.geodiff.element.Status;
import com.github.filosganga.geogson.model.Feature;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

/**
 * Encapsulates the final result so it is written with the type FeatureCollection.
 */
public class FeatureCollectionWrapper
{
	/**
	 * The type of GeoJSON object we are writing.<br>
	 * It is a member of the class so that the type is written by the JsonWriter.
	 */
	private final String type = "FeatureCollection";
	private final List<Feature> features;

	public FeatureCollectionWrapper(Delta delta, EnumSet<Status> statuses)
	{
		this.features = Collections.unmodifiableList(toFeatures(delta.filter(statuses)));
	}

	public List<Feature> getFeatures()
	{
		return features;
	}

	private void write(Writer output)
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

	public void writeTo(Writer writer)
	{
		write(writer);
	}

	public void writeTo(OutputStream stream)
	{
		try(Writer writer = new OutputStreamWriter(stream))
		{
			write(writer);
		}
		catch(IOException ignored)
		{
		}
	}

	public void writeTo(File file)
	{
		try(Writer writer = new FileWriter(file))
		{
			write(writer);
		}
		catch(FileNotFoundException e)
		{
			throw new RuntimeException("Invalid file path: " + file.getAbsolutePath(), e);
		}
		catch(IOException ignored)
		{
		}
	}
}
