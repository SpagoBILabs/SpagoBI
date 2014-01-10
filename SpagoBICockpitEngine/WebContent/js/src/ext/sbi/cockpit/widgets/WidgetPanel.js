/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * WidgetPanel
  * 
  * handle layout of widgets (maybe also d&d)
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

Sbi.cockpit.widgets.WidgetPanel = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	var defaultSettings = {
		layout:'whiteboard'
		, layoutConfig: {
			tableAttrs: {
				style: {width: '100%', height:'100%'}
			}
        }
	};
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.widgetPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.widgetPanel);
	}	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
		
	// constructor
	Sbi.cockpit.widgets.WidgetPanel.superclass.constructor.call(this, c);	
};

/**
 * @class Sbi.cockpit.widgets.WidgetPanel
 * @extends Ext.util.Observable
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.WidgetPanel, Sbi.cockpit.widgets.Widget, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Sbi.cockpit.widgets.WidgetContainer} widgetContainer
     * The container that manages the all the widgets rendered within this panel
     */
	widgetContainer: null
	 
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
		
	/**
	 * @method 
	 * 
	 * Controls that the configuration object passed in to the class constructor contains all the compulsory properties. 
	 * If it is not the case an exception is thrown. Use it when there are properties necessary for the object
	 * construction for whom is not possible to find out a valid default value.
	 * 
	 * @param {Object} the configuration object passed in to the class constructor
	 * 
	 * @return {Object} the config object received as input
	 */
	, validateConfigObject: function(config) {

	}

	/**
	 * @method 
	 * 
	 * Modify the configuration object passed in to the class constructor adding/removing properties. Use it for example to 
	 * rename a property or to filter out not necessary properties.
	 * 
	 * @param {Object} the configuration object passed in to the class constructor
	 * 
	 * @return {Object} the modified version config object received as input
	 * 
	 */
	, adjustConfigObject: function(config) {
		if(config.widgetContainer === undefined) {
			config.widgetContainer = new Sbi.cockpit.widgets.WidgetContainer({storeManager: config.storeManager});
			if(config.storeManager) {
				delete config.storeManager;
			}
		}
			
		if(config.items !== undefined) {
			config.widgetContainer.register(config.items);
			delete config.items;
		}	
	}
    
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , addWidget: function(widget, position) {	
    	Sbi.trace("[WidgetPanel.addWidget]: IN");
		this.widgetContainer.register(widget);	
		Sbi.trace("[WidgetPanel.addWidget]: OUT");
	}
    
    // -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , onRender: function(ct, position) {	
    	Sbi.trace("[WidgetPanel.onRender]: IN");
    	
		Sbi.cockpit.widgets.WidgetPanel.superclass.onRender.call(this, ct, position);
	
		this.clearContent();
		this.renderContent();
		Sbi.trace("[WidgetPanel.onRender]: OUT");
	}
     
    , clearContent: function() {
//    	 this.items.each( function(item) {
// 			this.items.remove(item);
// 	        item.destroy();           
// 	    }, this); 
    }
    
    , renderContent: function() {
    	var widgets = this.widgetContainer.getWidgets();
		widgets.each(function(widget, index, length) {
			this.renderWidget(widget);
		}, this);	
    }
    
    , renderWidget: function(widget) {
    	
    	var vpSize = Ext.getBody().getViewSize();
    	alert(vpSize.height);
    	
    	var winConf = {
    		title : 'Widget'
    		
    		, bodyBorder: true
    		, frame: true
    		, shadow: false
    		, plain : true
    		
    		, constrain: true
    		
    		, width : vpSize.width * 0.5
    		, height : vpSize.height * 0.5
    		, x : '50%'
    		, y: '50%'
    		
    		,layout : 'fit'
    		, items : [widget]
    		, tools  :  [{
    			id:'gear',
    	        handler: function(){
    	            Ext.Msg.alert('Message', 'The EDIT tool was clicked.');
    	        }
    		}, {
        		id:'help',
        	    handler: function(){
        	    	Ext.Msg.alert('Message', 'The CONFIG tool was clicked.');
        	    }
        	}, {
        		id:'refresh',
     	       	handler: function(){
     	           Ext.Msg.alert('Message', 'The REFRESH tool was clicked.');
     	       	}
        	}]
    	};
    	var win = new Ext.Window(winConf);
    	
    	this.windows = new Array();
    	this.windows.push(win);
    	win.show();
    	
    	//this.add(widget);
    }

}); 