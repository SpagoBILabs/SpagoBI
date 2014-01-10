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
     * @property {Sbi.cockpit.widgets.WidgetContainer} parentContainer
     * The WidgetContainer object that contains this widget
     */
    parentContainer: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    , setParentContainer: function(c) {	
		this.parentContainer = c;	
	}

	, getStore: function(storeiId) {
		var store;
		
		if(this.parentContainer) {
			var sm = this.parentContainer.getStoreManager();
			if(sm) {
				store = sm.getStore(storeiId);
			} else {
				alert("getStore: storeManager not defined");
			}
		} else {
			alert("getStore: container not defined");
		}	
		return store;
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