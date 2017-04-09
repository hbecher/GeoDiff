package com.bbfos.hbecher.geodiff;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.bbfos.hbecher.geodiff.element.Element;
import com.bbfos.hbecher.geodiff.element.Status;

/**
 * Contains the result of the computation, providing methods to filter elements by their {@link Status}.
 */
public class Delta
{
	private final List<Element> elements;

	public Delta(List<Element> elements)
	{
		this.elements = elements;
	}

	/**
	 * Returns the list of elements whose status is one of {@code statuses}.
	 *
	 * @param statuses the desired statuses
	 * @return The filtered list
	 */
	public List<Element> filter(EnumSet<Status> statuses)
	{
		return statuses.isEmpty() ? new ArrayList<>() : elements.stream().filter(element -> statuses.contains(element.getStatus())).collect(Collectors.toList());
	}

	public List<Element> getAdditions()
	{
		return filter(EnumSet.of(Status.ADDITION));
	}

	public List<Element> getDeletions()
	{
		return filter(EnumSet.of(Status.DELETION));
	}

	public List<Element> getOldVersions()
	{
		return filter(EnumSet.of(Status.OLD_VERSION));
	}

	public List<Element> getNewVersions()
	{
		return filter(EnumSet.of(Status.NEW_VERSION));
	}

	public List<Element> getModifications()
	{
		return filter(EnumSet.of(Status.ADDITION, Status.DELETION, Status.OLD_VERSION, Status.NEW_VERSION));
	}

	public List<Element> getIdentical()
	{
		return filter(EnumSet.of(Status.IDENTICAL));
	}

	public List<Element> getAll()
	{
		return new ArrayList<>(elements);
	}
}
