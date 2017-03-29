package com.bbfos.hbecher.geodiff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.element.Status;
import com.github.filosganga.geogson.model.Feature;

/**
 * Contains the result of the computation, providing methods to filter elements bu their status.
 */
public class Delta
{
	/**
	 * The GeoDiff status property.
	 */
	private static final String PROPERTY = "geodiff-type";

	private final List<Element> elements;

	public Delta(List<Element> elements)
	{
		this.elements = elements;
	}

	private List<Element> filter(Status status)
	{
		return elements.stream().filter(element -> element.getStatus() == status).collect(Collectors.toList());
	}

	public List<Element> getAdditions()
	{
		return filter(Status.ADDITION);
	}

	public List<Element> getDeletions()
	{
		return filter(Status.DELETION);
	}

	public List<Element> getModifications()
	{
		return filter(Status.MODIFICATION);
	}

	public List<Element> getIdentical()
	{
		return filter(Status.IDENTICAL);
	}

	public List<Element> getAll()
	{
		return Collections.unmodifiableList(elements);
	}

	/**
	 * Returns the decorated features.
	 *
	 * @return The decorated features.
	 */
	public List<Feature> getFeatures()
	{
		return elements.stream().map(element -> element.toGeoJsonElement().getFeature().withProperty(PROPERTY, element.getStatus().asJsonElement())).collect(Collectors.toList());
	}
}
