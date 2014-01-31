/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 
Ext.ns("Sbi.cockpit.runtime");

Sbi.cockpit.runtime.WidgetContainerComponent = function(config) {
	
	this.adjustConfigObject(config);
	this.validateConfigObject(config);
	
	// init properties...
	var defaultSettings = {
		title : config.widget? 'Widget [' + config.widget.id + ']': 'Widget'
	    , bodyBorder: true
	    , frame: true
	    , shadow: false
	    , plain : true
	    , constrain: true
	    , layout : 'fit'
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.runtime.WidgetContainerComponent', defaultSettings);
	
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	// init events...
	this.addEvents('performaction', 'move', 'resize');
	
	this.initServices();
	this.init();
	
	if(this.widget) {
		this.items = [this.widget];
	} else {
		this.html = "Please configure the widget";
	}
	
	// constructor
	Sbi.cockpit.runtime.WidgetContainerComponent.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.cockpit.runtime.WidgetContainerComponent
 * @extends Ext.Window
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.runtime.WidgetContainerComponent, Ext.Window, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
	services: null
   
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
		return config;
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
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, setWidget: function(widget) {
		// sostituisce il vecchio widget embeddato con quello ricevuto come argomento
	}
	
	, getWidget: function() {
		return this.widget;
	}
	
	, setWidgetConfiguration: function(widget) {
		// se è un tipo di widget diverso da quello attualmente embeddato lo crea e lo sostiruisce al vecchio
		// se è dello stesso tipo chiama il metodo setConfiguration sul vecchi senza ricrearne uno nuovo
	}
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	, onShowWidgetConfiguration: function(widget) {
		this.fireEvent('performaction', this, widget, 'showConfiguration');
    } 
    
    , onShowWidgetEditor: function(widget) {
    	this.fireEvent('performaction', this, widget, 'showEditor');
    } 
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - none
	 */
	, initServices: function() {
//		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
//		
//		this.services = this.services || new Array();
//		
//		this.services['exampleService'] = this.services['exampleService'] || Sbi.config.serviceRegistry.getServiceUrl({
//			serviceName: 'EXAMPLE_ACTION'
//			, baseParams: params
//		});	
	}


	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.tools =  [{
    		id:'gear',
    		handler: function(event, button, win, tc){
    			this.onShowWidgetEditor(win.widget);
        	},
    		scope: this
    	}, {
        	id:'help',
            handler: function(event, button, win, tc){
            	this.onShowWidgetConfiguration(win.widget);
            },
    		scope: this
        }, {
        	id:'refresh',
     	   	handler: function(){
     	   		Ext.Msg.alert('Message', 'The REFRESH tool was clicked.');
     	    },
    		scope: this
        }];
	}
	
	// =================================================================================================================
	// EVENTS
	// =================================================================================================================
	
//	, this.addEvents(
//		/**
//	     * @event performaction
//	     * Fired when the user trigger the execution of a specific action doing something on this widget 
//	     * @param {Sbi.xxx.Xxxx} this
//	     * @param {Ext.Toolbar} the contained widget
//	     * @param {Sring} action
//	     */
//		'performaction'
//	);	
});