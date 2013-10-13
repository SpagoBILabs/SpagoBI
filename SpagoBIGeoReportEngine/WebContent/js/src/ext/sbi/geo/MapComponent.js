/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.geo");

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
Sbi.geo.MapComponent = function(config) {
    Ext.apply(this, config);
    this.contentEl = this.map.div;

    // Set the map container height and width to avoid css 
    // bug in standard mode. 
    // See https://trac.mapfish.org/trac/mapfish/ticket/85
    var content = Ext.get(this.contentEl);
    content.setStyle('width', '100%');
    content.setStyle('height', '100%');
    
    
    Sbi.geo.MapComponent.superclass.constructor.call(this);
};

Ext.extend(Sbi.geo.MapComponent, Ext.Panel, {
    /**
     * Property: map
     * {OpenLayers.Map}  
     */
    map: null,
    
    mask: null,

    initComponent: function() {
    	Sbi.geo.MapComponent.superclass.initComponent.apply(this, arguments);
        this.on("bodyresize", this.map.updateSize, this.map);
    },

	mask: function() {
		this.mask = new Ext.LoadMask(Ext.get(this.contentEl), {msg:"Please wait..."});
        this.mask.show();
	},
    
    unmask: function() {
    	this.mask.hide();
    }
});



Ext.reg('mapcomponent', Sbi.geo.MapComponent);
