var markerOptions = function(c) {
    return {
        "radius": 8,
        "fillColor": c,
        "color": "#000",
        "weight": 1,
        "opacity": 1,
        "fillOpacity": 1
    };
};

var geojsonStyle = {
    style: function(feature) {
        switch(feature.geometry.type) {
            case "LineString":
                return {color: feature.properties.stroke};
            case "Polygon":
                return {color: feature.properties.fill};
            default:
                return {};
        }
    },
    pointToLayer: function (feature, latlng) {
        return L.circleMarker(latlng, markerOptions(feature.properties["marker-color"]));
    },
    onEachFeature: function (feature, layer) {
        var id = feature.properties.id;
        var desc = feature.properties.desc;
        layer.bindPopup((id ? id : "Pas d\'identifiant") + "<br />" + (desc ? desc : "Description indisponible"));
    }
};

var toGeoJson = function(string)
{
    return JSON.parse(string);
};

var toLGeoJson = function(geojson)
{
	return L.geoJson(geojson, geojsonStyle);
};

var map = null;
var emptyGeoJson = toGeoJson("{\"type\":\"FeatureCollection\",\"features\":[]}");
var emptyLGeoJson = toLGeoJson(emptyGeoJson);
var inputA = emptyLGeoJson;
var inputB = emptyLGeoJson;
var delta = emptyLGeoJson;
var layer = L.control.layers(null, {
	"Données n°1": inputA,
	"Données n°2": inputB,
	"Différences": delta
});

var show = function()
{
	var fileInput = document.getElementById("file");

	if(fileInput.files)
	{
        var reader = new FileReader();

        reader.addEventListener("load", function() {
            showGeoJson(reader.result);
        });

        reader.readAsText(fileInput.files[0]);
    }
};

// A, B, D -> Json Objects
var update = function(A, B, D)
{
	map.removeLayer(inputA);
	map.removeLayer(inputB);
	map.removeLayer(delta);
	layer.removeLayer(inputA);
	layer.removeLayer(inputB);
	layer.removeLayer(delta);

	delta = L.geoJson(D, geojsonStyle);

    layer.addOverlay(inputA, "Données n°1");
	layer.addOverlay(inputB, "Données n°2");
	layer.addOverlay(delta, "Différences");
};

var showGeoJson = function(input)
{
    if(input)
    {
        try
        {
            var D = toGeoJson(input);
        }
        catch(err)
        {
            alert("Erreur : syntaxe invalide !\n" + err);

            return;
        }

        update(emptyGeoJson, emptyGeoJson, D);
    }
};