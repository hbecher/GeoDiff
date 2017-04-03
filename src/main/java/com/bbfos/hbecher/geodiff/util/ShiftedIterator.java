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
	private final List<T> list, ref;
	private int start, pos = -1;
	private boolean removed, checkForCoMod;

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
		ref = new ArrayList<T>(list); // a reference list for detecting concurrent modifications
		this.start = start;
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

	/**
	 * Checks if a concurrent modification has been made.
	 *
	 * @param list the list to check
	 * @param ref  the reference list
	 * @param <T>  the type of elements of the lists
	 * @throws ConcurrentModificationException if a concurrent modification has been detected
	 */
	private static <T> void checkConcurrentModification(List<T> list, List<T> ref) throws ConcurrentModificationException
	{
		int size = list.size();

		if(size != ref.size())
		{
			throw new ConcurrentModificationException();
		}

		for(int i = 0; i < size; i++)
		{
			if(list.get(i) != ref.get(i))
			{
				throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * Controls additional concurrent modification checking (slower on large lists).
	 *
	 * @param checkForCoMod whether to check or not
	 */
	public void checkForCoMod(boolean checkForCoMod)
	{
		this.checkForCoMod = checkForCoMod;
	}

	@Override
	public boolean hasNext()
	{
		return pos < list.size() - 1;
	}

	@Override
	public T next() throws NoSuchElementException
	{
		if(checkForCoMod)
		{
			checkConcurrentModification(list, ref);
		}

		int size = list.size();

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
		if(checkForCoMod)
		{
			checkConcurrentModification(list, ref);
		}

		int size = list.size();

		if(pos >= size)
		{
			throw new IllegalStateException("End of iteration reached");
		}

		if(removed)
		{
			throw new IllegalStateException("Element already removed");
		}

		int index = mod(pos--, start, size);

		list.remove(index);
		ref.remove(index);

		if(pos + start >= list.size())
		{
			start--;
		}

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
