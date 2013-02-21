/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.ns("Sbi.execution");

Sbi.execution.InfoPage = function(config) {
	
	// init properties...
	var defaultSettings = {
		// set default values here
	};

	if (Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.infopage) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.execution.infopage);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
	// init events...
	this.addEvents();
	this.initServices();
	this.init();
	
	// constructor
    Sbi.execution.InfoPage.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.execution.InfoPage
 * @extends Ext.Panel
 * 
 * bla bla bla bla bla ...
 */

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.execution.InfoPage, Ext.Panel, {
    
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
		this.html = 'Descrizione doc (lavori in corso) ...';
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	// =================================================================================================================
	// EVENTS
	// =================================================================================================================
	
	//this.addEvents(
	/**
     * @event eventone
     * Fired when ...
     * @param {Sbi.execution.InfoPage} this
     * @param {Ext.Toolbar} ...
     */
	//'eventone'
	/**
     * @event eventtwo
     * Fired before ...
     * @param {Sbi.execution.InfoPage} this
     * @param {Object} ...
     */
	//'eventtwo'
	//);	
});