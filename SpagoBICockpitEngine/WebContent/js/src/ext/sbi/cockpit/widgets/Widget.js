/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.cockpit.widgets");

Sbi.cockpit.widgets.Widget = function(config) {	
		
	Sbi.trace("[Widget]: IN");
	// init properties...
	var defaultSettings = {
		border: false
		, bodyBorder: false
		, hideBorders: true
		, frame: false
		, defaultMsg: ' '
	};

	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.widget) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.widget);
	}
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
			
	this.msgPanel = new Ext.Panel({
		html: this.defaultMsg
	});
	c = Ext.apply(c, { 
		border: false
		, bodyBorder: false
		, hideBorders: true
		, frame: false
	   	, items: [this.msgPanel]
	});
		
	// constructor
	Sbi.cockpit.widgets.Widget.superclass.constructor.call(this, c);
	
	Sbi.trace("[Widget]: OUT");
};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.Widget, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Sbi.cockpit.widgets.WidgetPanel} parentContainer
     * The WidgetPanel object that contains this widget
     */
    parentContainer: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
   
    , getConfiguration: function() {
    	var config = {};
    	config.custom = this.getCustomConfiguration();
    	config.layout = this.getRegion();
    	config.style = this.getStyleConfiguration();
		return config;
	}

	, getCustomConfiguration: function() {
		var config = {};
		return config;
	}
	
	, getStyleConfiguration: function() {
		var config = {};
		return config;
	}

    , getParentContainer: function(c) {	
		return this.parentContainer;	
	}

    , setParentContainer: function(c) {	
		this.parentContainer = c;	
	}
    
    , isBoundToAContainer: function() {
    	return this.parentContainer != null;
    }
    
    , getWidgetManager: function() {
    	var wm = null;
    	
    	Sbi.trace("[Widget.getWidgetManager]: IN");
    	
    	if(this.isBoundToAContainer() === true) {
    		wm = this.parentContainer.getWidgetManager();
    		if(wm === null) {
    			Sbi.error("[Widget.getWidgetManager]: Widget [" + this.toString() + "] is bound to a widget container but it is not possible to retrive from it a valid widget manager");
    		}
    	} else {
    		Sbi.warn("[Widget.getWidgetManager]: It's not possble to retrieve widget manager of widget [" + this.toString() + "] because it is not bound to any widget container");
    	}
    	
    	Sbi.trace("[Widget.getWidgetManager]: OUT");
    	
    	return wm;
    }

    , getRegion: function() {
    	var r = null;
    	
    	if(this.isBoundToAContainer() === true) {
    		r = this.getParentContainer().getWidgetRegion(this);
    		if(r === null) {
    			Sbi.warn("[Widget.getWidgetManager]: Widget [" + this.toString() + "] is bound to a widget container but it is not possible to retrive the region it occupies");
    		}
    	}
    	
    	return r;
    }
    
	, getStore: function(storeiId) {
		var store;
		
		if(this.getWidgetManager) {
			var wm = this.getWidgetManager();
			if(wm) {
				store = wm.getStore(storeiId);
			} else {
				alert("getStore: storeManager not defined");
			}
		} else {
			alert("getStore: container not defined");
		}	
		return store;
	}
	
	, toString: function() {
		return this.id;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	, onRender: function(ct, position) {	
		Sbi.cockpit.widgets.Widget.superclass.onRender.call(this, ct, position);	
	}
	
	// =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
    
	
});