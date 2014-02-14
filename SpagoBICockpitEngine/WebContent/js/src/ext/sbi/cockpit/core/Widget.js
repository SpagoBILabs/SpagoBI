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

Ext.ns("Sbi.cockpit.core");

Sbi.cockpit.core.Widget = function(config) {	
		
	Sbi.trace("[Widget]: IN");
	// init properties...
	var defaultSettings = {
		border: false
		, bodyBorder: false
		, hideBorders: true
		, frame: false
		, defaultMsg: ' '
	};

	var settings = Sbi.getObjectSettings('Sbi.cockpit.core.Widget', defaultSettings);
	
	var c = Ext.apply(settings, config || {});
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
	Sbi.cockpit.core.Widget.superclass.constructor.call(this, c);
	
	Sbi.trace("[Widget]: OUT");
};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.core.Widget, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Sbi.cockpit.core.WidgetPanel} parentContainer
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
    	Sbi.trace("[Widget.getConfiguration]: IN");
    	var config = {};
		config.dataset = this.selectedDatasetLabel;
		config.wtype = this.wtype;
    	config.custom = this.getCustomConfiguration();
    	
    	config.layout = this.getRegion();
    	config.style = this.getStyleConfiguration();
    	config.common = this.getCommonConfiguration();
		
    	Sbi.trace("[Widget.getConfiguration]: OUT");
    	
    	return config;
	}

	, getCustomConfiguration: function() {
		Sbi.trace("[Widget.getCustomConfiguration]: IN");
		var config = {};
		Sbi.trace("[Widget.getCustomConfiguration]: OUT");
		return config;
	}
	
	, getStyleConfiguration: function() {
		var config = {};
		return config;
	}

	, getCommonConfiguration: function(){
		var config = {};
		
		var datasets = [];
		var sm = this.getWidgetManager().getStoreManager();
		for (var i=0; i < sm.length; i++){
			var s = sm.get(i);
			datasets.push(s.datasetLabel);
		}
		
		config.datasets = datasets;
		return config;
	}
	
    , getParentComponent: function() {	
    	Sbi.trace("[Widget.getParentComponent]: IN");
    	Sbi.trace("[Widget.getParentComponent]: OUT");
		return this.parentComponent;	
	}

    , setParentComponent: function(component) {
    	Sbi.trace("[Widget.setParentComponent]: IN");
		this.parentComponent = component;	
		Sbi.trace("[Widget.setParentComponent]: Parent container of widget [" + this.id +  "] is [" + (component?component.id:"null") + "]");
		Sbi.trace("[Widget.setParentComponent]: OUT");
	}
    
    , getParentContainer: function() {	
    	Sbi.trace("[Widget.getParentContainer]: IN");
		var container = null;
		
		var component = this.getParentComponent();
			
		if(Sbi.isValorized(component)) {
			Sbi.trace("[Widget.getParentContainer]: widget [" + this.id +  "] is bound to component [" + component.id + "]");
			container = component.getParentContainer();
		} else {
			Sbi.warn("[Widget.getParentContainer]: widget [" + this.id + "] is not bound to any component");
		}
		Sbi.trace("[Widget.getParentContainer]: OUT");
    	return container;	
	}
    
    , isBoundToAContainer: function() {
    	Sbi.trace("[Widget.isBoundToAContainer]: IN");
    	var isBound = false;
    	var container = this.getParentContainer();
    	if(container != null) {
    		isBound = true;
    		Sbi.trace("[Widget.getParentContainer]: widget [" + this.id +  "] is bound to container [" + container.id + "]");
    	}
    	Sbi.trace("[Widget.isBoundToAContainer]: OUT");
    	return isBound;
    }
    
    , getWidgetManager: function() {
    	var widgetManager = null;
    	
    	Sbi.trace("[Widget.getWidgetManager]: IN");
    	
    	if(this.isBoundToAContainer() === true) {
    		widgetManager = this.getParentContainer().getWidgetManager();
    		if(widgetManager === null) {
    			Sbi.error("[Widget.getWidgetManager]: Widget [" + this.toString() + "] is bound to a widget container but it is not possible to retrive from it a valid widget manager");
    		}
    	} else {
    		Sbi.warn("[Widget.getWidgetManager]: It's not possble to retrieve widget manager of widget [" + this.toString() + "] because it is not bound to any widget container");
    	}
    	
    	Sbi.trace("[Widget.getWidgetManager]: OUT");
    	
    	return widgetManager;
    }

    , getRegion: function() {
    	var r = null;
    	var h =   this.getHeight();
    	var w =  this.getWidth();
    	var p = this.getPosition();
    	
    	r =  {
	   			  x : p[0]
	   	    	, y: p[1]
	   			, width : w
	       		, height : h
	    	};
    	
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
		Sbi.cockpit.core.Widget.superclass.onRender.call(this, ct, position);	
	}
	
	
	
	// =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
    
	
});