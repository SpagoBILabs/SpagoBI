/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * WidgetContainer
  * 
  * handle:
  *  - widgets lifecycle management: register, unregister, lookup
  *  - shared resources: through env
  *  - intra-widgets comunications: sendMessage (asyncronous: point to point or broadcast)
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

Ext.ns("Sbi.console");

Sbi.console.WidgetContainer = function(config) {
	
		var defaultSettings = {
			
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.widgetContainer) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.widgetContainer);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		
		Ext.apply(this, c);

		this.init();
	

		// constructor
		Sbi.console.WidgetContainer.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.console.WidgetContainer, Ext.util.Observable, {
    
    widgets: null
    , env: null
    , storeManager: null
    
   
    //  -- public methods ---------------------------------------------------------
        
    , register: function(w) {
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
	
	
    
    //  -- private methods ---------------------------------------------------------
    
    , init: function() {
  
    	if(!this.storeManager) {
    		alert("Store manager not defined");
    		this.storeManager = new Ext.util.MixedCollection();
	    	var testStore = new Ext.data.JsonStore({
		        fields:['name', 'visits', 'views'],
		        data: [
		            {name:'Jul 07', visits: 245000, views: 3000000},
		            {name:'Aug 07', visits: 240000, views: 3500000},
		            {name:'Sep 07', visits: 355000, views: 4000000},
		            {name:'Oct 07', visits: 375000, views: 4200000},
		            {name:'Nov 07', visits: 490000, views: 4500000},
		            {name:'Dec 07', visits: 495000, views: 5800000},
		            {name:'Jan 08', visits: 520000, views: 6000000},
		            {name:'Feb 08', visits: 620000, views: 7500000}
		        ]
		    });
	    	this.storeManager.add('testStore', testStore);
    	}
    	
    	this.widgets = new Ext.util.MixedCollection();
    	this.widgets.on('add', this.onWidgetAdd, this);
    	this.widgets.on('remove', this.onWidgetRemove, this);
	}
    
    , onWidgetAdd: function(index, widget, key) {
    	widget.setParentContainer(this);
    }
    
    , onWidgetRemove: function(widget, key) {
    	widget.setParentContainer(null);
    }
});