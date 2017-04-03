package com.bbfos.hbecher.geodiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.element.Identifier;
import com.bbfos.hbecher.geodiff.element.Status;
import com.bbfos.hbecher.geodiff.parser.ParsedElements;

/**
 * This is the module that computes the delta between two given data sets.
 */
public class GeoDiff
{
	private final List<Element> elementsA, elementsB;

	/**
	 * Convenient constructor.
	 *
	 * @param parsed The result of the parsing.
	 */
	public GeoDiff(ParsedElements parsed)
	{
		this(parsed.getElementsA(), parsed.getElementsB());
	}

	/**
	 * Main constructor.
	 *
	 * @param elementsA the first list of elements (A)
	 * @param elementsB the second list of elements (B)
	 */
	public GeoDiff(List<Element> elementsA, List<Element> elementsB)
	{
		this.elementsA = Objects.requireNonNull(elementsA);
		this.elementsB = Objects.requireNonNull(elementsB);
	}

	/**
	 * Returns the {@code Element} with the same {@code Identifier} as {@code toTest} within {@code elements} if not already visited, {@code null} otherwise.
	 *
	 * @param elements the elements
	 * @param toTest   the {@code Element} to com.bbfos.hbecher.geodiff.test
	 * @param visited  the {@code boolean} array keeping track of already visited elements
	 * @return The {@code Element} counterpart of {@code toTest} within {@code elements} or {@code null} if already visited.
	 */
	private Element counterpart(List<Element> elements, Element toTest, boolean[] visited)
	{
		Identifier id = toTest.getId();

		for(int i = 0; i < elements.size(); i++)
		{
			if(!visited[i] && elements.get(i).getId().equals(id))
			{
				visited[i] = true;

				return elements.get(i);
			}
		}

		return null;
	}

	/**
	 * Computes and returns the delta of the element sets.
	 *
	 * @return The delta
	 * @see Delta
	 */
	public Delta delta()
	{
		boolean[] visited = new boolean[elementsB.size()];
		List<Element> result = new ArrayList<>();

		for(Element element : elementsA)
		{
			Element e = counterpart(elementsB, element, visited);
			Status status = e == null ? Status.DELETION : element.equals(e) ? Status.IDENTICAL : Status.OLD_VERSION;

			element.setStatus(status);

			result.add(element);

			if(status == Status.OLD_VERSION)
			{
				e.setStatus(Status.NEW_VERSION);

				result.add(e);
			}
		}

		for(int i = 0; i < visited.length; i++)
		{
			if(!visited[i])
			{
				Element element = elementsB.get(i);

				element.setStatus(Status.ADDITION);
				result.add(element);
			}
		}

		return new Delta(result);
	}
}
