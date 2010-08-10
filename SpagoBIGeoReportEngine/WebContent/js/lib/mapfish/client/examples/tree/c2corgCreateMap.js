/*
 * Copyright (C) 2007-2008  Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */


function c2corgCreateMap(mapDiv) {

    var options = {
        projection: "EPSG:4326",
        controls: [new OpenLayers.Control.MouseDefaults()],
        'numZoomLevels': 20
    };

    var map = new OpenLayers.Map(mapDiv , options);

    var wms = new OpenLayers.Layer.WMS("OpenLayers WMS", 
        "http://labs.metacarta.com/wms/vmap0",
        {layers: 'basic'},
        {isBaseLayer: true}
    );
    wms.setVisibility(false);
    map.addLayer(wms);

    var twms = new OpenLayers.Layer.WMS("World Map", 
        "http://world.freemap.in/cgi-bin/mapserv?", 
        {
            map: '/www/freemap.in/world/map/factbooktrans.map',
            transparent: true,
            layers: 'factbook',
            format: 'image/png'
        }
    );
    twms.setVisibility(false);
    map.addLayer(twms);

    c2cwmsLayers = ['parkings', 'summits', 'refuges', 'sites'];
    c2cwms = new OpenLayers.Layer.WMS("C2C Objects",
        "http://demo.mapfish.org/mapfishsample/1.0/wms?",
        {
            singleTile: true,
            layers: c2cwmsLayers,
            format: 'image/png',
            transparent: true
        }
    );
    c2cwms.setVisibility(false);
    map.addLayer(c2cwms);

    map.setCenter(new OpenLayers.LonLat(5,45), 6);
    map.addControl(new OpenLayers.Control.PanZoomBar());

    return map;
}
