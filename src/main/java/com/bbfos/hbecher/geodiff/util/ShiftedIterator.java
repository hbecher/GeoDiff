package com.bbfos.hbecher.geodiff.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ShiftedIterator<T> implements Iterator<T>
{
	private final int index;
	private List<T> list;
	private int pos = 0, lastRet = -1;

	public ShiftedIterator(List<T> list, int index)
	{
		this.list = list;
		this.index = index;
	}

	@Override
	public boolean hasNext()
	{
		return pos < list.size();
	}

	@Override
	public T next()
	{
		int size = list.size();
		T next = pos < size ? list.get((pos + index) % size) : null;

		lastRet = -1;
		pos++;

		return next;
	}

	@Override
	public void remove()
	{
		if(pos >= list.size())
		{
			throw new IllegalStateException("End of iteration reached");
		}

		if(lastRet != -1)
		{
			throw new IllegalStateException("Element already removed");
		}

		list.remove(pos);

		lastRet = pos;
	}

	@Override
	public void forEachRemaining(Consumer<? super T> action)
	{
		int size = list.size();

		if(pos < size)
		{
			list.subList(pos, size).forEach(action);
		}
	}
}
