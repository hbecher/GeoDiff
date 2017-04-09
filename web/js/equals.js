const Type = {
	"POINT": "Point",
	"LINE_STRING": "LineString",
	"LINEAR_RING": "LinearRing",
	"POLYGON": "Polygon",
	"MULTI_POINT": "MultiPoint",
	"MULTI_LINE_STRING": "MultiLineString",
	"MULTI_POLYGON": "MultiPolygon"
};

const equalsGeometry = function(c1, c2, type)
{
	if(c1 === c2)
	{
		return true;
	}

	switch(type)
	{
		case Type.POINT:
		{
			return c1[0] === c2[0] && c1[1] === c2[1] && c1[3] === c2[3]; 
		}

		case Type.POLYGON:
		{
			return equalsGeometry(c1[0], c2[0], Type.LINEAR_RING) && equalsOrder(c1.slice(1, c1.length), c2.slice(1, c2.length), Type.LINEAR_RING);
		}

		case Type.LINE_STRING:
		{
			if(!equalsGeometry(c1[0], c1[c1.length - 1], Type.POINT) || !equalsGeometry(c2[0], c2[c2.length - 1], Type.POINT))
			{
				return equalsOrder(c1, c2, Type.POINT);
			}
		}

		case Type.LINEAR_RING:
		{
			return equalsCyclical(c1.slice(0, c1.length - 1), c2.slice(0, c2.length - 1));
		}

		case Type.MULTI_POINT:
		{
			return equalsCyclical(c1, c2);
		}

		case Type.MULTI_POLYGON:
		{
			return equalsOrder(c1[0], c2[0], Type.POLYGON);
		}

		case Type.MULTI_LINE_STRING:
		{
			return equalsOrder(c1[0], c2[0], Type.LINE_STRING);
		}
	}

	return false;
}

const hasCounterpart = function(list, toTest, visited, type)
{
	for(let i = 0; i < list.length; i++)
	{
		if(!visited[i] && equalsGeometry(list[i], toTest, type))
		{
			visited[i] = true;

			return true;
		}
	}

	return false;
}

const equalsIgnoreOrder = function(l1, l2, type)
{
	if(l1 === l2)
	{
		return true;
	}

	if(l1.length !== l2.length)
	{
		return false;
	}

	var visited = [];

	for(let i = 0; i < l1.length; i++)
	{
		visited[i] = false;
	}

	for(const t of l1)
	{
		if(!hasCounterpart(l2, t, visited, type))
		{
			return false;
		}
	}

	for(const b of visited)
	{
		if(!b)
		{
			return false;
		}
	}

	return true;
}

const equalsOrder = function(l1, l2, type)
{
	if(l1 === l2)
	{
		return true;
	}

	if(l1.length !== l2.length)
	{
		return false;
	}

	for(let i = 0; i < l1.length; i++)
	{
		if(!equalsGeometry(l1[i], l2[i], type))
		{
			return false;
		}
	}

	return true;
}

const matching = function(list, that)
{
	var result = [];

	for(let i = 0; i < list.length; i++)
	{
		if(equalsGeometry(list[i], that, Type.POINT))
		{
			result.push(i);
		}
	}

	return result;
}

const equalsCyclical = function(lp1, lp2)
{
	if(lp1 === lp2)
	{
		return true;
	}

	if(lp1.length !== lp2.length)
	{
		return false;
	}

	var startingPoints = matching(lp1, lp2[0]);

	for(const start of startingPoints)
	{
		let i1 = new ArrayIterator(lp1);
		let i2 = new ShiftedIterator(lp2, start);
		let equals = true;

		while(i1.hasNext()) // lp1.length === lp2.length
		{
			let sp1 = i1.next(), sp2 = i2.next();

			if(!equalsGeometry(sp1, sp2, Type.POINT))
			{
				equals = false;

				break;
			}
		}

		if(equals)
		{
			return true;
		}
	}

	return false;
}
