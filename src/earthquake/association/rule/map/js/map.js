var map = L.map('map').setView([-0.789275, 113.92132700000002], 4.5);
var geojson;

L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors',
    maxZoom: 18
}).addTo(map);

function getColor(d) {
    return d > 30 ? '#800026' :
            d > 25 ? '#BD0026' :
            d > 20 ? '#E31A1C' :
            d > 15 ? '#FC4E2A' :
            d > 10 ? '#FD8D3C' :
            d > 5 ? '#FEB24C' :
            d > 0 ? '#FED976' :
            '#FFEDA0';
}

function style(feature) {
    return {
        fillColor: getColor(feature.properties.OBJECTID),
        weight: 2,
        opacity: 1,
        color: 'white',
        dashArray: '3',
        fillOpacity: 0.7
    };
}

function highlightFeature(e) {
    var layer = e.target;

    layer.setStyle({
        weight: 5,
        color: '#484848',
        dashArray: '',
        fillOpacity: 0.7
    });

    if (!L.Browser.ie && !L.Browser.opera && !L.Browser.edge) {
        layer.bringToFront();
    }
}

function resetHighlight(e) {
    geojson.resetStyle(e.target);
}

function zoomToFeature(e) {
    var layer = e.target;
    window.eqMap.tryIncrementalTable(layer.feature.properties.A1N_2013);
    geojson.bindPopup(layer.feature.properties.A1N_2013);
}

function onEachFeature(feature, layer) {
    layer.on({
        mouseover: highlightFeature,
        mouseout: resetHighlight,
        click: zoomToFeature
    });
}
function association(prov1, prov2) {
    var newProv1 = prov1.split(",");
    var newProv2 = prov2.split(",");

    geojson = L.geoJson(provincesData, {
        style: function (feature) {
            for (var i = 0; i < newProv1.length; i++) {
                if (feature.properties.A1N_2013 === newProv1[i].toUpperCase()) {
                    return {
                        stroke: false,
                        fillColor: 'orange',
                        fillOpacity: 1
                    };
                }
            }

            for (var i = 0; i < newProv2.length; i++) {
                if (feature.properties.A1N_2013 === newProv2[i].toUpperCase()) {
                    return {
                        stroke: false,
                        fillColor: 'red',
                        fillOpacity: 1
                    };
                }
            }

            return {
                stroke: false,
                fillOpacity: 0
            };
        },
        onEachFeature: onEachFeature
    }).addTo(layerGroup);

}
function associationRisk(prov, color) {
    geojson = L.geoJson(provincesData, {
        style: function (feature) {
            if (feature.properties.A1N_2013 === prov.toUpperCase()) {
                return {
                    stroke: false,
                    fillColor: color,
                    fillOpacity: 1
                };

            }
            return {
                stroke: false,
                fillOpacity: 0
            };
        },
        onEachFeature: onEachFeature

    }).addTo(layerGroup);

}

//map.fitBounds(geojson.getBounds());

var layerGroup = L.layerGroup().addTo(map);
var legend = L.control({position: 'bottomright'});

function clearMarker() {
    layerGroup.clearLayers();
}

function visualize(id, lat, lang, depth, mag) {
    var circleMarker = L.circleMarker([lat, lang], {
        stroke: false,
        fillColor: "red",
        fillOpacity: getDepth(depth),
        radius: mag * 1.1
    }).on('click', function (ev) {
        window.eqMap.showInfoEarthquake(id);
    }).addTo(layerGroup);
}
function createLegend(k) {
    legend.onAdd = function (map) {
        var div = L.DomUtil.create('div', 'info legend'),
                grades = [],
                labels = [];

        // loop through our density intervals and generate a label with a colored square for each interval
        if (k < 100) {
            for (var i = 0; i < k; i++) {
                div.innerHTML +=
                        '<i style="background:' + fillColor(i) + '"></i> ' +
                        "Cluster " + (i + 1) + '  <br>';
            }
        } else if (k == 100) {
            div.innerHTML +=
                    '<i style="background:' + "red" + '"></i> ' +
                    "Earthquake " + '  <br>';
        } else if (k == 200) {
            div.innerHTML +=
                    '<i style="background:' + "orange" + '"></i> ' +
                    "Antecendent " + '  <br>' +
                    '<i style="background:' + "red" + '"></i> ' +
                    "Decendent " + '  <br>';
        } else if (k == 300) {
            div.innerHTML +=
                    '<i style="background:' + "red" + '"></i> ' +
                    "Major " + '  <br>' +
                    '<i style="background:' + "orange" + '"></i> ' +
                    "Strong " + '  <br>' +
                    '<i style="background:' + "yellow" + '"></i> ' +
                    "Moderate " + '  <br>'+
                    '<i style="background:' + "green" + '"></i> ' +
                    "Small " + '  <br>'+
                    '<i style="background:' + "LightSlateGray" + '"></i> ' +
                    "Minor " + '  <br>';
        }
        return div;
    };
    legend.addTo(map);
}
function visualizeCluster(id, lat, lang, depth, mag, cluster) {
    var circleMarker = L.circleMarker([lat, lang], {
        stroke: false,
        fillColor: fillColor(cluster),
        fillOpacity: getDepth(depth),
        radius: mag * 1.1
    }).on('click', function (ev) {
        window.eqMap.showInfoEqCluster(id);
    }).addTo(layerGroup);
}

function fillColor(c) {
    switch (c) {
        case 0:
            return ("#D50000");
            break;
        case 1:
            return ("#AA00FF");
            break;
        case 2:
            return ("#1A237E");
            break;
        case 3:
            return ("#01579B");
            break;
        case 4:
            return ("#004D40");
            break;
        case 5:
            return ("#33691E");
            break;
        case 6:
            break;
        case 7:
            return ("#F57F17");
            break;
        case 8:
            return ("#BF360C");
            break;
        case 9:
            return ("#3E2723");
            break;
        default:
            return ("black");
            break;
    }
}

function getDepth(d) {
    return d > 700 ? 0.15 :
            d > 500 ? 0.2 :
            d > 300 ? 0.3 :
            d > 150 ? 0.4 :
            d > 70 ? 0.6 :
            d > 35 ? 0.8 :
            d > 0 ? 1 :
            0;
}