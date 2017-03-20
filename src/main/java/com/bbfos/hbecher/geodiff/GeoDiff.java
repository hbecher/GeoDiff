package com.bbfos.hbecher.geodiff;

import java.util.ArrayList;
import java.util.List;

import com.bbfos.hbecher.geodiff.elements.Element;
import com.bbfos.hbecher.geodiff.elements.Identifier;
import com.bbfos.hbecher.geodiff.parsers.ParsedElements;
import com.bbfos.hbecher.geodiff.states.State;

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
		if(elementsA == null || elementsB == null)
		{
			throw new NullPointerException();
		}

		this.elementsA = elementsA;
		this.elementsB = elementsB;
	}

	/**
	 * Returns the index of {@code toTest} within {@code elements} if not already visited, {@code -1} otherwise.
	 *
	 * @param elements the elements
	 * @param toTest   the {@code Element} to test
	 * @param visited  the {@code boolean} array keeping track of already visited elements
	 * @return The index of {@code toTest} within {@code elements} or {@code -1} if already visited.
	 */
	private int index(List<Element> elements, Element toTest, boolean[] visited)
	{
		Identifier id = toTest.getId();

		for(int i = 0; i < elements.size(); i++)
		{
			if(!visited[i] && elements.get(i).getId().equals(id))
			{
				visited[i] = true;

				return i;
			}
		}

		return -1;
	}

	/**
	 * Computes and returns the delta of the element sets wrapped in a {@link Features} object.
	 *
	 * @return The delta
	 */
	public Features delta()
	{
		boolean[] visited = new boolean[elementsB.size()];
		List<Element> result = new ArrayList<>();

		for(Element element : elementsA)
		{
			int k = index(elementsB, element, visited);

			element.setState(k == -1 ? State.DELETION : element.equals(elementsB.get(k)) ? State.INACTION : State.MODIFICATION);

			result.add(element);
		}

		for(int i = 0; i < visited.length; i++)
		{
			if(!visited[i])
			{
				Element element = elementsB.get(i);

				element.setState(State.ADDITION);
				result.add(element);
			}
		}

		return new Features(result);
	}
}
