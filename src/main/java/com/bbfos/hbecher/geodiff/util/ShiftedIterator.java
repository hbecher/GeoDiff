package com.bbfos.hbecher.geodiff.util;

import java.util.*;
import java.util.function.Consumer;

/**
 * An iterator that traverses a virtually left-shifted list.
 * <br><br>
 * For example, if we consider the list {@code l = [1, 5, 9, 7, 3, 0]}, a shifted iterator over {@code l} starting at index {@code 4}
 * would iterate over {@code [7, 3, 0, 1, 5, 9]}.<br>
 * This is especially useful when testing if two lists describe the same cycle.
 * <br><br>
 * The {@link Iterator#remove()} operation is supported.
 * <br><br>
 * <b>This iterator is not thread safe, nor does it detect concurrent modifications.</b>
 *
 * @param <T> the type of elements returned by this iterator
 * @see #ShiftedIterator(List, int)
 */
public class ShiftedIterator<T> implements Iterator<T>
{
	private List<T> list;
	private int start, pos = -1, expectedSize, lastHashCode;
	private boolean removed = false;

	/**
	 * Creates a {@code ShiftedIterator} with the start index set at {@code 0}.<br>
	 * This is equivalent to a regular {@link Iterator}.
	 *
	 * @param list the list to iterate over
	 * @see #ShiftedIterator(List, int)
	 */
	public ShiftedIterator(List<T> list)
	{
		this(list, 0);
	}

	/**
	 * Creates a {@code ShiftedIterator} with the specified start index.
	 * The start index must lie between {@code 0} (inclusive) and the size of the list (exclusive).
	 *
	 * @param list  the list to iterate over
	 * @param start the start index
	 * @see #ShiftedIterator(List)
	 */
	public ShiftedIterator(List<T> list, int start)
	{
		Objects.requireNonNull(list);

		if(start < 0)
		{
			throw new IllegalArgumentException("start < 0");
		}

		if(start >= list.size())
		{
			throw new IllegalArgumentException("start >= list.size()");
		}

		this.list = list;
		this.start = start;
		expectedSize = list.size();
		lastHashCode = list.hashCode();
	}

	/**
	 * Returns {@link Math#floorMod(int, int) Math.floorMod}{@code (pos + start, size)}
	 *
	 * @param pos   the current position within {@code 0} and {@code size - 1}
	 * @param start the start of the iteration
	 * @param size  the size of the list
	 * @return {@code Math.floorMod(pos + start, size)}
	 */
	private static int mod(int pos, int start, int size)
	{
		return Math.floorMod(pos + start, size);
	}

	@Override
	public boolean hasNext()
	{
		return pos < list.size() - 1;
	}

	@Override
	public T next() throws NoSuchElementException
	{
		int size = list.size();

		if(size != expectedSize || list.hashCode() != lastHashCode)
		{
			throw new ConcurrentModificationException();
		}

		if(pos >= size)
		{
			throw new NoSuchElementException();
		}

		removed = false;

		return list.get(mod(++pos, start, size));
	}

	/**
	 * @throws IllegalStateException if the last element returned by this iterator has already been removed
	 *                               or if the end of iteration has been reached
	 */
	@Override
	public void remove()
	{
		int size = list.size();

		if(size != expectedSize)
		{
			throw new ConcurrentModificationException();
		}

		int hashCode = list.hashCode();

		if(hashCode != lastHashCode)
		{
			throw new ConcurrentModificationException();
		}

		if(pos >= size)
		{
			throw new IllegalStateException("End of iteration reached");
		}

		if(removed)
		{
			throw new IllegalStateException("Element already removed");
		}

		list.remove(mod(pos--, start, size));

		if(pos + start >= list.size())
		{
			start--;
		}

		expectedSize--;
		lastHashCode = hashCode;
		removed = true;
	}

	@Override
	public void forEachRemaining(Consumer<? super T> action)
	{
		Objects.requireNonNull(action);

		while(hasNext())
		{
			action.accept(next());
		}
	}
}
