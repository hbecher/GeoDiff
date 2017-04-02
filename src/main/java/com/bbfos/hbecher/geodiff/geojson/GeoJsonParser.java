package com.bbfos.hbecher.geodiff.geojson;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.metadata.Metadata;
import com.bbfos.hbecher.geodiff.parser.ParseException;
import com.bbfos.hbecher.geodiff.parser.Parser;
import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.github.filosganga.geogson.model.FeatureCollection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * The GeoJSON parser.<br><br>
 * At the moment, it only support input files representing a FeatureCollection (not individual Features).
 */
public class GeoJsonParser extends Parser
{
	public static final Gson GSON = new GsonBuilder().registerTypeAdapterFactory(new GeometryAdapterFactory()).create();
	private final String[] id;

	public GeoJsonParser(String fileA, String fileB, Metadata metadata)
	{
		super(fileA, fileB, metadata);

		this.id = metadata == null ? null : getIdentifiers(metadata.getId());
	}

	private static String[] getIdentifiers(String id)
	{
		return id == null ? null : id.isEmpty() ? new String[0] : id.split(",");
	}

	private static List<Element> toElements(FeatureCollection fc, String[] id)
	{
		return fc.features().stream().map(feature -> new GeoJsonElement(feature, id)).collect(Collectors.toList());
	}

	@Override
	public List<Element> parseFile(String file) throws ParseException
	{
		FeatureCollection featuresCol;

		try(JsonReader reader = GSON.newJsonReader(new FileReader(file)))
		{
			featuresCol = GSON.fromJson(reader, FeatureCollection.class);
		}
		catch(IOException | JsonIOException | JsonSyntaxException e)
		{
			throw new ParseException("An exception occurred while parsing file " + file, e);
		}

		return toElements(featuresCol, id);
	}
}
