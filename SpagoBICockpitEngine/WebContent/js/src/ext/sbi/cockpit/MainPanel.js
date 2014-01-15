/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit");

/**
 * Class: Sbi.cockpit.MainPanel
 * Main GUI of SpagoBIGeoReportEngine
 */
Sbi.cockpit.MainPanel = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	var defaultSettings = {
	
	};
		
	if(Sbi.settings && Sbi.settings.cockpit && Sbi.settings.cockpit.mainpanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.cockpit.mainpanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.initServices();
	//this.initStore();
	
	this.init();
	
	c = Ext.apply(c, {
         //layout   : 'fit',
         hideBorders: true,
         items    : [this.widgetContainer]
	});

	// constructor
	Sbi.cockpit.MainPanel.superclass.constructor.call(this, c);
};

/**
 * @class Sbi.cockpit.MainPanel
 * @extends Ext.Panel
 * 
 * ...
 */
Ext.extend(Sbi.cockpit.MainPanel, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
    services: null
    
    , msgPanel: null
   
    

    
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
	
	}
	
	, validate: function (successHandler, failureHandler, scope) {
		Sbi.trace("[MainPanel.validate]: IN");
		
		var template = this.getAnalysisState();
		var templeteStr = Ext.util.JSON.encode(template);
		Sbi.trace("[MainPanel.validate]: template = " + templeteStr);
		
		Sbi.trace("[MainPanel.validate]: OUT");
		return templeteStr;
	}
	
	, getAnalysisState: function () {
		Sbi.trace("[MainPanel.getAnalysisState]: IN");
		
		var analysisState = {};
		
		Sbi.trace("[MainPanel.getAnalysisState]: OUT");
		return analysisState;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // accessor methods
	// -----------------------------------------------------------------------------------------------------------------

	// ...
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	// ...

	//-----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - xxx: ...
	 *    - yyy: ...
	 *    - zzz: ...
	 *    
	 */
	, initServices: function() {
		this.services = this.services || new Array();	
		
		var params = {
			
		};
		
//		this.services['GetTargetDataset'] = this.services['GetTargetDataset'] || Sbi.config.serviceRegistry.getServiceUrl({
//			serviceName: 'GetTargetDataset'
//			, baseParams: params
//		});

	}
	
	
	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.initToolbar();
		this.initWidgetContainer();
	}
	
	, initToolbar: function() {
		this.tbar = new Ext.Toolbar({
			//renderTo: document.body,
		    //width: 300,
		    //height: 100,
		    items: [
		        '->', // same as {xtype: 'tbfill'}, // Ext.Toolbar.Fill
		        {
		        	text: 'Add widget'
		        	, handler: this.addWidget
		        	, scope: this
		        }
		    ]
		});
	}
	
	, addWidget: function() {
		var dummyWidget = new Sbi.cockpit.widgets.DummyWidget();
		dummyWidget.setParentContainer(null);
		this.widgetContainer.addWidget(dummyWidget, {
			x : 0
	    	, y: 0
			, width : 0.5
    		, height : 0.5
		});
	}
	
	, initWidgetContainer: function() { 
		Sbi.trace("[MainPanel.initWidgetContainer]: IN");
		var dummyWidget = new Sbi.cockpit.widgets.DummyWidget({msg: "paperino"});
		Sbi.trace("[MainPanel.initWidgetContainer]: dummy widget succesfully created");
		
		this.widgetContainer = new Sbi.cockpit.runtime.WidgetContainer({
			items: [dummyWidget]
		});
		Sbi.trace("[MainPanel.initWidgetContainer]: widget panel succesfully created");
		
		Sbi.trace("[MainPanel.initWidgetContainer]: IN");

//		this.msgPanel = new Ext.Panel({
//			html : 'Ciao, sono il nuovo motore cockpit'
//		});	
		Sbi.trace("[MainPanel.initWidgetContainer]: OUT");
	}
});