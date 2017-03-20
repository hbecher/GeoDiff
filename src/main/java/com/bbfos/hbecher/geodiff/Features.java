package com.bbfos.hbecher.geodiff;

import java.util.List;
import java.util.stream.Collectors;

import com.bbfos.hbecher.geodiff.elements.Element;
import com.github.filosganga.geogson.model.Feature;

/**
 * This is a holder whose main goal is to provide the method to extract the decorated features of an {@code Element} list.
 */
public class Features
{
	/**
	 * The GeoDiff state property.
	 */
	private static final String PROPERTY = "geodiff-type";
	private final List<Element> elements;

	public Features(List<Element> elements)
	{
		this.elements = elements;
	}

	/**
	 * Returns the decorated features.
	 *
	 * @return The decorated features.
	 */
	public List<Feature> getFeatures()
	{
		return elements.stream().map(element -> element.toGeoJsonElement().getFeature().withProperty(PROPERTY, element.getState().asJsonElement())).collect(Collectors.toList());
	}
}
