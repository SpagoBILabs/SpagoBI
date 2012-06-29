/*
* SpagoBI, the Open Source Business Intelligence suite
* 
* Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This file is part of MapFish Client, Copyright (C) 2007 Camptocamp 
* This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the “Incompatible With Secondary Licenses” notice, according to the ExtJS Open Source License Exception for Development, version 1.03, January 23rd, 2012 http://www.sencha.com/legal/open-source-faq/open-source-license-exception-for-development/ 
* If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. 
* This file is an extension to Ext JS Library that is distributed under the terms of the GNU GPL v3 license. For any information, visit: http://www.sencha.com/license.
* 
* The original copyright notice of this file follows.*/
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

Ext.namespace('mapfish.widgets');

/**
 * Class: mapfish.widgets.MapComponent
 *
 * A map container in order to be able to insert a map into a complex layout
 * Its main interest is to update the map size when the container is resized
 *
 * Simple example usage:
 * > var mapcomponent = new mapfish.widgets.MapComponent({map: map});
 *
 * Inherits from:
 * - {Ext.Panel}
 */

/*
 * Constructor: mapfish.widgets.MapComponent
 * Create a new MapComponent.
 *
 * Parameters:
 * config - {Object} The config object
 */
mapfish.widgets.MapComponent = function(config) {
    Ext.apply(this, config);
    this.contentEl = this.map.div;

    // Set the map container height and width to avoid css 
    // bug in standard mode. 
    // See https://trac.mapfish.org/trac/mapfish/ticket/85
    var content = Ext.get(this.contentEl);
    content.setStyle('width', '100%');
    content.setStyle('height', '100%');
    
    mapfish.widgets.MapComponent.superclass.constructor.call(this);
};

Ext.extend(mapfish.widgets.MapComponent, Ext.Panel, {
    /**
     * Property: map
     * {OpenLayers.Map}  
     */
    map: null,

    initComponent: function() {
        mapfish.widgets.MapComponent.superclass.initComponent.apply(this, arguments);
        this.on("bodyresize", this.map.updateSize, this.map);
    }
});
Ext.reg('mapcomponent', mapfish.widgets.MapComponent);
