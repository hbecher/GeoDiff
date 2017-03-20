package com.bbfos.hbecher.geodiff;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.bbfos.hbecher.geodiff.geojson.GeoJsonParser;
import com.github.filosganga.geogson.model.Feature;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

/**
 * Represents the final result, to be written to the output file.
 */
public class Result
{
	/**
	 * The type of GeoJSON object we are writing.
	 */
	private final String type = "FeatureCollection";
	private final List<Feature> features;

	public Result(Features features)
	{
		this.features = features.getFeatures();
	}

	public void writeFile(File output)
	{
		Gson gson = GeoJsonParser.GSON;

		try(JsonWriter writer = gson.newJsonWriter(new FileWriter(output)))
		{
			gson.toJson(this, new TypeToken<Result>()
			{
			}.getType(), writer);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
