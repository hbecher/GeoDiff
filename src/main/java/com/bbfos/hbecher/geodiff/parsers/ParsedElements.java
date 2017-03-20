package com.bbfos.hbecher.geodiff.parsers;

import java.util.List;

import com.bbfos.hbecher.geodiff.elements.Element;

/**
 * This is a container holding the {@link Element} lists.<br />
 * It is used only to pass the parsed elements from the parser to {@link com.bbfos.hbecher.geodiff.GeoDiff GeoDiff}.
 */
public class ParsedElements
{
	/**
	 * The first and second {@link Element} lists used by the program.
	 */
	private final List<Element> elementsA, elementsB;

	public ParsedElements(List<Element> elementsA, List<Element> elementsB)
	{
		this.elementsA = elementsA;
		this.elementsB = elementsB;
	}

	public List<Element> getElementsA()
	{
		return elementsA;
	}

	public List<Element> getElementsB()
	{
		return elementsB;
	}
}
