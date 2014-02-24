/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.core");

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
     * @property {Sbi.data.StoreManager} storeManager
     * The object that manage the dataset used by the widget in this container
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
    		this.storeManager = new Sbi.data.StoreManager();
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