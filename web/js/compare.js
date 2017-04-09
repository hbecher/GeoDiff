//--------------------------------//
// the delta computation module
//--------------------------------//

function NullError(message)
{
	this.name = "NullError";
	this.message = message;
	this.stack = (new Error()).stack;
}

NullError.prototype = new Error;

const Status = {
	"ADD": "add",
	"DEL": "del",
	"OLD": "old",
	"NEW": "new",
	"ID": "id",
	"UNDEF": "undef"
}

class Identifier
{
	constructor(uid)
	{
		if(uid == null)
		{
			throw new NullError();
		}

		this.uid = uid;
	}

	equals(that)
	{
		if(that === this)
		{
			return true;
		}

		var thatUid = that.getRaw();
		var type = typeof this.uid;

		if(typeof thatUid !== type)
		{
			return false;
		}

		if(type === "object")
		{
			let keys = Object.keys(this.uid);

			if(keys.length === Object.keys(thatUid).length)
			{
				for(const key of keys)
				{
					if(this.uid[key] !== thatUid[key])
					{
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	getRaw()
	{
		return this.uid;
	}
}

class Element
{
	constructor(feature, idKeys)
	{
		var id;

		if(idKeys == null)
		{
			if(feature.id)
			{
				id = feature.id;
			}
			else
			{
				throw new NullError("Feature has no valid id");
			}
		}
		else
		{
			var obj = {};

			if(idKeys.length === 0)
			{
				for(const key of Object.keys(feature.properties))
				{
					obj[key] = feature.properties[key];
				}
			}
			else
			{
				for(const key of idKeys)
				{
					let prop = feature.properties[key];

					if(prop == null)
					{
						throw new NullError("Feature has no property " + key);
					}

					obj[key] = prop;
				}
			}

			id = obj;
		}

		this.feature = feature;
		this.id = new Identifier(id);
		this.status = Status.UNDEF;
	}

	equals(that)
	{
		// CompareGeojsonGeometry(this.getCoordinates(), that.getCoordinates(), this.getType())
		return that === this || this.getType() === that.getType() && this.getId().equals(that.getId()) && equalsGeometry(this.getCoordinates(), that.getCoordinates(), this.getType());
	}

	getType()
	{
		return this.feature.geometry.type;
	}

	getId()
	{
		return this.id;
	}

	getCoordinates()
	{
		return this.feature.geometry.coordinates;
	}

	getStatus()
	{
		return this.status;
	}

	setStatus(status)
	{
		this.status = status;
	}

	decorate()
	{
		this.feature.properties[geoProperty] = this.status;

		return this.feature;
	}
}

function toElements(features)
{
	var elements = [];
	var idKeys = uid == null ? null : /^\s*$/.test(uid) ? [] : uid.split(/,/);

	for(const feature of features)
	{
		elements.push(new Element(feature, idKeys));
	}

	return elements;
}

function counterpart(elements, toTest, visited)
{
	var id = toTest.getId();

	for(var i = 0; i < elements.length; i++)
	{
		if(!visited[i] && elements[i].getId().equals(id))
		{
			visited[i] = true;

			return elements[i];
		}
	}

	return null;
}

function compare(featuresA, featuresB) // two Feature[], returns decorated Feature[]
{
	var elementsA = toElements(featuresA), elementsB = toElements(featuresB);

	var result = [];
	var visited = []; // visited.length will be elementsB.length

	for(let i = 0; i < elementsB.length; i++) // init to false
	{
		visited[i] = false;
	}

	for(const element of elementsA) // for each element of A
	{
		let e = counterpart(elementsB, element, visited); // we search for its counterpart in B
		let status = e == null ? Status.DEL : element.equals(e) ? Status.ID : Status.OLD; // compute status

		element.setStatus(status);

		result.push(element.decorate());

		if(status === Status.OLD) // if altered
		{
			e.setStatus(Status.NEW); // add the new version

			result.push(e.decorate());
		}
	}

	for(var i = 0; i < visited.length; i++) // add the non visited as new
	{
		if(!visited[i])
		{
			let element = elementsB[i];

			element.setStatus(Status.ADD);
			result.push(element.decorate());
		}
	}

	return result;
}
