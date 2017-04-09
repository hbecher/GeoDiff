function ArgumentError(message)
{
	this.name = "ArgumentError";
	this.message = message;
	this.stack = (new Error()).stack;
}

ArgumentError.prototype = new Error;

class ArrayIterator
{
	constructor(array)
	{
		this.array = array;
		this.length = array.length;
		this.pos = 0;
	}

	next()
	{
		if(this.pos >= this.length)
		{
			throw new RangeError("End of iteration reached");
		}

		return this.array[this.pos++];
	}

	hasNext()
	{
		return this.pos < this.length;
	}
}

class ShiftedIterator extends ArrayIterator
{
	constructor(array, start)
	{
		super(array);

		if(start < 0)
		{
			throw new ArgumentError("start < 0");
		}

		if(start >= array.length)
		{
			throw new ArgumentError("start >= array.length");
		}

		this.start = start;
	}

	mod(pos, start, length)
	{
		var result = (pos + start) % length;

		if(result < 0)
		{
			result += length;
		}

		return result;
	}

	next()
	{
		if(this.pos >= this.length)
		{
			throw new RangeError("End of iteration reached");
		}

		return this.array[this.mod(this.pos++, this.start, this.length)];
	}
}
