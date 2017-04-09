// the unique identifier
// null <=> GeoJSON standard (feature.id)
// empty or /\s+/ <=> all properties (feature.properties)
// <id> <=> feature.properties.<id>
// <prop1>,<prop2>,... <=> [feature.properties.<prop1>, feature.properties.<prop2>, ...]
var uid = null;

const geoProperty = "geodiff-type"; // the GeoDiff property

//--------------------------------//
// the data manipulation module
//--------------------------------//

// encapsule les features dans une FeatureCollection
const toGeoJson = function(features)
{
	return {
		"type": "FeatureCollection",
		"features": features
	};
};

// renvoie un marqueur circulaire de couleur c
const markerOptions = function(c)
{
	return {
		"radius": 6,
		"fillColor": c,
		"color": "#000",
		"weight": 1,
		"opacity": 1,
		"fillOpacity": 0.5
	};
};

// extrait les propriétés de feature, séparées par un retour à la ligne
// utilisé dans l'affichage de l'infobulle
const extractProperties = function(feature)
{
	var properties = Object.keys(feature.properties);
	var props = "";

	for(const prop of properties)
	{
		props += "<br>" + prop + ": " + feature.properties[prop];
	}

	return props;
};

// retourne la couleur suivant la propriété GeoDiff
const getColor = function(feature)
{
	var c = diffs[feature.properties[geoProperty]];

	return (c ? c : diffs.undef).color;
};

// le style à appliquer lors de la transformation en objet Leaflet
const geojsonStyle = {
	style: function(feature)
	{
		return {
			"color": getColor(feature)
		};
	},
	pointToLayer: function(feature, latlng)
	{
		return L.circleMarker(latlng, markerOptions(getColor(feature)));
	},
	onEachFeature: function(feature, layer)
	{
		let id = uid ? feature.properties[uid] : feature.id;
		let props = extractProperties(feature);

		layer.bindPopup((id ? id : "Pas d'identifiant") + (props ? "<br>" + props : ""));
	}
};

// retourne l'objet Leaflet correspondant à l'objet JSON geojson
const toLGeoJson = function(geojson)
{
	return L.geoJSON(geojson, geojsonStyle);
};

var map = null; // la map
const emptyGeoJson = toGeoJson([]); // objet GeoJSON vide
const emptyLGeoJson = toLGeoJson(emptyGeoJson); // objet Leaflet GeoJSON vide

// les différences
// contient pour chaque type de différence le nom, l'objet JSON, l'objet Leaflet associé et sa couleur
const diffs = {
	"add": {
		"name": "Additions",
		"features": [],
		"leaflet": emptyLGeoJson,
		"color": "#0f0"
	},
	"del": {
		"name": "Suppressions",
		"features": [],
		"leaflet": emptyLGeoJson,
		"color": "#f00"
	},
	"old": {
		"name": "Anciennes versions",
		"features": [],
		"leaflet": emptyLGeoJson,
		"color": "#ff7f00"
	},
	"new": {
		"name": "Nouvelles versions",
		"features": [],
		"leaflet": emptyLGeoJson,
		"color": "#ff0"
	},
	"mod": {
		"name": "Modifications",
		"features": [],
		"leaflet": emptyLGeoJson,
		"color": "#ff7f00"
	},
	"id": {
		"name": "Identiques",
		"features": [],
		"leaflet": emptyLGeoJson,
		"color": "#00f"
	},
	"undef": {
		"name": "Indéfinis",
		"features": [],
		"leaflet": emptyLGeoJson,
		"color": "#f0f"
	}
};

// les couches Leaflet
var layer = L.control.layers(null, {});

// extrait les différences contenues dans delta
// et les distribue suivant leur type dans diffs
const extractDiffs = function(delta)
{
	for(const key of Object.keys(diffs))
	{
		diffs[key].features = [];
	}

	for(const key of Object.keys(delta))
	{
		let feature = delta[key];

		diffs[feature.properties[geoProperty] ? feature.properties[geoProperty] : "undef"].features.push(feature);
	}
};

// met à jour les différences
const update = function(delta)
{
	map.removeControl(layer);

	for(const key of Object.keys(diffs))
	{
		map.removeLayer(diffs[key].leaflet);
		layer.removeLayer(diffs[key].leaflet);
	}

	extractDiffs(delta);

	var readLayer = false;

	for(const key of Object.keys(diffs))
	{
		if(diffs[key].features.length)
		{
			diffs[key].leaflet = toLGeoJson(diffs[key].features);

			layer.addOverlay(diffs[key].leaflet, diffs[key].name);

			readLayer = true;
		}
		else
		{
			diffs[key].leaflet = emptyLGeoJson;
		}
	}

	if(readLayer)
	{
		map.addControl(layer);
	}
};

//--------------------------------//
// the file downloader
//--------------------------------//

const download = function()
{
	var form = document.getElementById("data-form");
	var submit = false;

	for(const key of Object.keys(diffs))
	{
		if(diffs[key].features.length)
		{
			document.getElementById("data-" + key).value = encodeURIComponent(JSON.stringify(toGeoJson(diffs[key].features)));

			submit = true;
		}
	}

	if(submit)
	{
		form.submit();
	}
}

//--------------------------------//
// the choice picker and resetter
//--------------------------------//

const select = function(choice)
{
	var choices = document.getElementsByName("choice");
	var tabs = document.getElementsByName("tab");

	for(let e of choices)
	{
		e.disabled = true;
	}

	for(let e of tabs)
	{
		e.hidden = true;
	}

	document.getElementById(choice + "-div").hidden = false;
};

const reset = function()
{
	uid = null;

	map.removeControl(layer);

	for(const key of Object.keys(diffs))
	{
		map.removeLayer(diffs[key].leaflet);
		layer.removeLayer(diffs[key].leaflet);

		diffs[key].features = [];
		diffs[key].leaflet = emptyLGeoJson;
	}

	document.getElementById('calc-choice').disabled = false;
	document.getElementById('show-choice').disabled = false;
	document.getElementById('calc-div').hidden = true;
	document.getElementById('show-div').hidden = true;
	document.getElementById("uid_field").disabled = false;
	document.getElementById("uid_field").value = "";
	document.getElementById("buttonOK").disabled = false;
	document.getElementById("div-file").hidden = true;
	document.getElementById("fileA").value = "";
	document.getElementById("fileB").value = "";
	document.getElementById("calc").disabled = true;
	document.getElementById("download").hidden = true;
	document.getElementById("fileDiff").value = "";
	document.getElementById("show").disabled = true;
};

//--------------------------------//
// the input parser module - part 1
//--------------------------------//

// appelé lors d'un clic sur le bouton d'affichage
const show = function()
{
	var fileInput = document.getElementById("fileDiff");

	if(fileInput.files.length)
	{
		let reader = new FileReader();

		reader.onload = function(event)
		{
			if(this.result)
			{
				try
				{
					var delta = JSON.parse(this.result);
				}
				catch(err)
				{
					alert("Erreur : syntaxe JSON invalide !\n" + err);

					return;
				}

				update(delta.features);
			}
		};

		reader.readAsText(fileInput.files[0]);
	}
};

//--------------------------------//
// the input parser module - part 2
//--------------------------------//

const showIdHelp = function()
{
	alert("Syntaxe de l'identifiant unique :\n- vide : standard GeoJSON (feature.id)\n- espaces : toutes les propriétés (feature.properties)\n- <id> : la propriété <id> (feature.properties.<id>)\n- <prop1>,<prop2>,... : les propriétés spécifiées, séparées par des virgules");
};

//--------------------------------//
//variables globale ! 
//amelioration possible : refactoring de certain nom,separer les implementaions ... 
const id_table= new Array();//verification unicite des id 
id_table[0] = new Array(); // contiendra les suppressions
id_table[1] = new Array(); 
const myObject = new Array();
myObject[3]=new Array();//contiendra les modifications
myObject[4]=new Array();//contiendra les ajouts
//myObject[0] contiendra les supressions  
//myObject[1] contiendra fichier 1
//myObject[2] contiendra fichier 2
var sup; //debug voir l'element supprime de myobject[1]
var v = 0 ; //garde le nb de supression DEBUG 
//--------------------------------//

/*
recupere le nom de la propriete contenant un identifiant et demasque le choix des fichier d'entree 
*/
const getID = function()
{
	var field = document.getElementById("uid_field");
	//12:27:05,131 L’utilisation de « getPreventDefault() » est obsolète. Utiliser « defaultPrevented » à la place. 
	uid = field.value;
	field.disabled = true;
	document.getElementById("buttonOK").disabled = true;
	document.getElementById("div-file").hidden=false; //demasquage pour choisir fichier 
	//console.log("id:",uid);
	//return uid;
}

//--------------------------------//
const parse = function(string, i)
{
	//console.log("id:",uid);
	myObject[i] = JSON.parse(string);//parseur json
	var numero_de_ligne = 0;
	//console.log("debut",myObject);
	//verification des id 
	for (var k = 0 ; k < myObject[i].features.length ; k = k + 1 ){//pour chaque id
		if (id_table[i-1][myObject[i].features[k].properties[uid]] !== undefined){//si l'id existe deja 
			console.log("erreur id non unique : " + id_table[i-1][myObject[i].features[k].properties[uid]]);
			return -1;
		}
		else{
			id_table[i-1][myObject[i].features[k].properties[uid]] = k;//ajout de l'id et de son indice dans la table
			//console.log("id_table",i,myObject[i].features[k].properties[uid],id_table[i-1][myObject[i].features[k].properties[uid]]);
		}                    
	}

	if (i === 1){//on copie le premier fichier dans myobjet[0] il est deja present dans myobject[1]
		//myObject[0] contiendra les suppression on l'initalise avec tout le contenu du 1 er fichier 
		//et on le reduira petit a petit dans la suite
		myObject[0] = $.extend(true,{},myObject[1]);//prends de la place memoire 
		//ajout marqueur de deletion  dans les propriete 
		for (var l = 0 ; l < myObject[0].features.length ; l = l + 1 ){
		myObject[0].features[l].properties[geoProperty]="del";
		}
	}
	else{
		for (var j = 0 ; j < myObject[i].features.length ; j = j + 1 ){ //parcours du i eme fichier i = 2 
			if (myObject[i].features[j].properties[uid] in id_table[0]){//si l'id du feature j du deuxieme fichier existe dans le premier
				//on recupere l'index correspondant dans le premier fichier 
				numero_de_ligne = id_table[0][myObject[i].features[j].properties[uid]];
				//console.log(" j ",j,"numero de ligne",numero_de_ligne);
				//DEBUG
				//console.log("tableau avant sup = ",myObject[0].features);
				//console.log("k-v:",k-v);
				//console.log("v",v);
				//FIN DEBUG
				sup = myObject[0].features.splice(numero_de_ligne-v,1);//ce n'est pas une suppression donc on l'enleve du fichier qui contiendra les suppression
				v=v+1;//on update le nombre de supression
				//DEBUG
				//console.log("sup = ",sup);
				//console.log("tableau apres sup = ",myObject[0].features);
				//console.log("tableau non modif fin = ",myObject[1].features);
				// console.log("taille du fichier des supp",myObject[0].features.length);   
				//FIN DEBUG
				//si la longueur et le type sont identique
				if (myObject[i].features[j].geometry.coordinates.length === myObject[1].features[numero_de_ligne].geometry.coordinates.length && myObject[i].features[j].geometry.type === myObject[1].features[numero_de_ligne].geometry.type){
					
					//attention actuelement il y a deux version de la fonction de comparaison pas completement satisfaisante
					//Celle utilise est celle qui tient compte de l'ordre dans tous les type
					//les cas cycliques ne sont pas traités
					//si au moins une coordonnee differente au sens de la fonction de comparaison CompareGeojonGeometry => modif 
					if (!(CompareGeojsonGeometry( myObject[i].features[j].geometry.coordinates, 
					myObject[1].features[numero_de_ligne].geometry.coordinates,
					myObject[i].features[j].geometry.type))){
						myObject[3].push(myObject[i].features[j]);//si les coordonnees sont differente:
						// on ajoute cette ligne dans le tableau des modification
						//ajout marqueur de modif dans les propriete
						myObject[3][myObject[3].length -1].properties[geoProperty]="mod";
					}
				}
				//sinon c'est une modif (type ou longueur differente )
				else{
					myObject[3].push(myObject[i].features[j]);//si les coordonnees sont differente:
					// on ajoute cette ligne dans le tableau des modification
					//ajout marqueur de modif dans les propriete 
					myObject[3][myObject[3].length -1].properties[geoProperty]="mod";
				}
			}else {//si l'id ne correspond a aucun du premier fichier
				//console.log("ajout");
				myObject[4].push(myObject[i].features[j]);//on la place dand le tableau des ajout
				//ajout marqueur d'ajout dans les propriete 
				myObject[4][myObject[4].length -1].properties[geoProperty]="add"; 
			}  
		}
		//DEBUG
		//console.log("fichier 1:",myObject[1]);
		//console.log("fichier 2:",myObject[2]);
		//console.log("supression",myObject[0].features);
		//console.log("modif",myObject[3]);
		//console.log("ajout",myObject[4]);

		var delta = {
			type: "FeatureCollection",
			features: []
		};

		Array.prototype.push.apply(delta.features, myObject[0].features);
		Array.prototype.push.apply(delta.features, myObject[3]);
		Array.prototype.push.apply(delta.features, myObject[4]);
		
		id_table[0] = new Array();
		id_table[1] = new Array(); 
		myObject[0] = new Array();
		myObject[1] = new Array();
		myObject[2] = new Array();
		myObject[3]=new Array();
		myObject[4]=new Array();
		
		// compléter diffs
		update(delta);

		document.getElementById("download").hidden=false; //demasquage pour telecharger le resultat zip
		//le zip contiendra 4 fichier 1 avec toutes les differences 3 autre selon le type de differences format geojson
	}

	return 0;
};
//--------------------------------//

const compareFiles = function(stringA, stringB)
{
	try
	{
		var jsonA = JSON.parse(stringA), jsonB = JSON.parse(stringB);
	}
	catch(err)
	{
		alert("Erreur : syntaxe JSON invalide !\n" + err);

		return;
	}

	var delta = compare(jsonA.features, jsonB.features);

	update(delta);

	document.getElementById("download").hidden = false;
}

const parseFiles = function()
{
	var fileInputA = document.getElementById("fileA"), fileInputB = document.getElementById("fileB");

	let readerA = new FileReader(), readerB = new FileReader();

	readerA.onload = function(event)
	{
		readerB.readAsText(fileInputB.files[0]);
	};

	readerB.onload = function(event)
	{
		compareFiles(readerA.result, readerB.result);
	};

	readerA.readAsText(fileInputA.files[0]);
};

const checkFileInputs = function()
{
	var fileInputA = document.getElementById("fileA");
	var fileInputB = document.getElementById("fileB");
	var calcButton = document.getElementById("calc");

	calcButton.disabled = !fileInputA.files.length || !fileInputB.files.length;
};
