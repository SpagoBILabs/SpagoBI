/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.core");

Sbi.cockpit.core.WidgetManager = function(config) {
	
	// init properties...
	var defaultSettings = {
		// set default values here
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.core', defaultSettings);
	
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);

	this.init();
	
	// constructor
	Sbi.cockpit.core.WidgetManager.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.cockpit.core.WidgetManager
 * @extends Ext.util.Observable
 * 
 *  It handles:
 *  - widgets lifecycle management: register, unregister, lookup
 *  - shared resources: through env
 *  - intra-widgets comunications: sendMessage (asyncronous: point to point or broadcast)
 */

/**  
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.core.WidgetManager, Ext.util.Observable, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Ext.util.MixedCollection} widgets
     * The collection of widgets managed by this container
     */
    widgets: null
    
    /**
     * @property {Ext.util.MixedCollection} storeManager
     * The collection of stores managed by this container
     */
    , storeManager: null
    
    /**
     * @property {Object} env
     * This container environment
     * WARNINGS: not used at the moment
     */
    , env: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , init: function() {
    	  
    	if(!this.storeManager) {
    		//alert("Store manager not defined");
    		this.storeManager = new Ext.util.MixedCollection();
//	    	var testStore = new Ext.data.JsonStore({
//		        fields:['name', 'visits', 'views'],
//		        data: [
//		            {name:'Jul 07', visits: 245000, views: 3000000},
//		            {name:'Aug 07', visits: 240000, views: 3500000},
//		            {name:'Sep 07', visits: 355000, views: 4000000},
//		            {name:'Oct 07', visits: 375000, views: 4200000},
//		            {name:'Nov 07', visits: 490000, views: 4500000},
//		            {name:'Dec 07', visits: 495000, views: 5800000},
//		            {name:'Jan 08', visits: 520000, views: 6000000},
//		            {name:'Feb 08', visits: 620000, views: 7500000}
//		        ]
//		    });
//	    	this.storeManager.add('testStore', testStore);
    	}
    	
    	this.widgets = new Ext.util.MixedCollection();
    	this.widgets.on('add', this.onWidgetAdd, this);
    	this.widgets.on('remove', this.onWidgetRemove, this);
	}

    // -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

        
    , register: function(w) {
    	if(Ext.isArray(w) === false) {
    		w = [w];
    	}
		this.widgets.addAll(w);
	}

	, unregister: function(w) {
		this.remove(w);
	}
	
	, lookup: function(w) {
		this.widgets.get(w);
	}
	
	, getWidgets: function() {
		return this.widgets;
	}
	
	, getStoreManager: function() {
		return this.storeManager;
	}
	
	, getUsedStoreLabels: function() {
		var storeLabels = [];
		for(var l in this.storeManager.map) {
			storeLabels.push(l);
		}
		return storeLabels;
	}
	
	, addStore: function(s) {
		if (s != undefined && s.dsLabel !== undefined){
			var newStore = new  Ext.data.JsonStore({
				 datasetLabel: s.dsLabel
//				, autoLoad: false
//				, refreshTime: c[i].refreshTime
//				, limitSS: this.limitSS
//				, memoryPagination: c[i].memoryPagination || false 
			});
			this.storeManager.add(newStore.datasetLabel, newStore);								
		}
	}

	, getStoreByLabel: function(s) {
		var toReturn = null;
		if (s != undefined){
			toReturn = this.storeManager.get(s);								
		}
		return toReturn;
	}
	
	, removeStore: function(s) {
		if (s != undefined){
			this.storeManager.remove(this.storeManager.get(s));								
		}
	}

	, existsStore: function(s) {
		var toReturn = false;
		if (s != undefined){
			if (this.storeManager.get(s) !== null && this.storeManager.get(s) !== undefined)
					toReturn = true;
		}
		return toReturn;
	}	
	
	, getWidgetUsedByStore: function(s){
		var toReturn = new Ext.util.MixedCollection();
		if (s != undefined){
			for(var i=0; i < this.widgets.getCount(); i++){
				var w = this.widgets.item(i);
				if (w.dataset !== undefined && w.dataset == s  ){
					toReturn.add(w);
				}
			}
		}
		return toReturn;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
    
    , onWidgetAdd: function(index, widget, key) {
    	//widget.setParentContainer(this);
    }
    
    , onWidgetRemove: function(widget, key) {
    	//widget.setParentContainer(null);
    }
    
    // =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
});


Sbi.cockpit.core.registry = {}; 

Sbi.cockpit.core.WidgetManager.regsterWidget = function(wtype, wdescriptor) {
	Sbi.cockpit.core.registry[wtype] = wdescriptor;
};

Sbi.regsterWidget = Sbi.cockpit.core.WidgetManager.regsterWidget;

Sbi.cockpit.core.WidgetManager.getWidget = function(wtype, wconf) {
	
	var wdescriptor = Sbi.cockpit.core.registry[wtype];
	
	if(wdescriptor !== undefined) {
		return new wdescriptor.runtimeClass(wconf); 
	} else {
		alert("Widget of type [" + wtype +"] not supprted");
	}
};

Sbi.cockpit.core.WidgetManager.getWidgetDesigner = function(wtype, wconf) {
	
	var wdescriptor = Sbi.cockpit.core.registry[wtype];
	
	if(wdescriptor !== undefined) {
		return new wdescriptor.designerClass(wconf); 
	} else {
		alert("Widget of type [" + wtype +"] not supprted");
	}
};