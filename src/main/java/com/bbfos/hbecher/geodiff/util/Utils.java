package com.bbfos.hbecher.geodiff.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.bbfos.hbecher.geodiff.element.Element;
import com.github.filosganga.geogson.model.Feature;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;

/**
 * This class contains various utility methods used throughout the program.
 */
public class Utils
{
	/**
	 * The GeoDiff status property.
	 */
	public static final String GEODIFF_PROPERTY = "geodiff-type";

	private Utils()
	{
	}

	/**
	 * Converts the {@code Element} to its corresponding {@code Feature},
	 * adds the GeoDiff status property {@value GEODIFF_PROPERTY} to the feature's properties
	 * and returns the new feature.<br>
	 * This method checks if the property has already been added beforehand and replaces it,
	 * otherwise it would throw an exception (due to the implementation).
	 *
	 * @param element the element to decorate
	 * @return The corresponding decorated feature
	 */
	private static Feature decorate(Element element)
	{
		Feature feature = element.toGeoJsonElement().getFeature();

		return feature.properties().containsKey(GEODIFF_PROPERTY) ? new Feature(feature.geometry(), ImmutableMap.<String, JsonElement>builder().putAll(feature.properties().entrySet().stream().filter(entry -> !entry.getKey().equals(GEODIFF_PROPERTY)).collect(Collectors.toList())).put(GEODIFF_PROPERTY, element.getStatus().toJson()).build(), feature.id()) : feature.withProperty(GEODIFF_PROPERTY, element.getStatus().toJson());
	}

	/**
	 * Returns the decorated features.
	 *
	 * @param elements the list of elements
	 * @return The decorated features.
	 */
	public static List<Feature> toFeatures(List<Element> elements)
	{
		return elements.stream().map(Utils::decorate).collect(Collectors.toList());
	}

	/**
	 * Returns a {@link List} containig all the elements returned in order by the {@link Iterable}.
	 *
	 * @param iterable the iterable
	 * @param <T>      the type of elements
	 * @return A list containing the elements of the iterable.
	 */
	public static <T> List<T> collect(Iterable<T> iterable)
	{
		List<T> collected = new ArrayList<>();

		for(T t : iterable)
		{
			collected.add(t);
		}

		return collected;
	}

	/**
	 * Returns a list containing all the elements from {@code list} that are equal to {@code that}, i.e all elements that satisfy
	 * <pre>element.equals(that) == true</pre>
	 *
	 * @param list the list
	 * @param that the element to match
	 * @param <T>  the type of elements
	 * @return The list of all matching elements.
	 */
	public static <T> List<Integer> matching(List<T> list, T that)
	{
		return IntStream.range(0, list.size()).filter(i -> list.get(i).equals(that)).boxed().collect(Collectors.toList());
	}
}
