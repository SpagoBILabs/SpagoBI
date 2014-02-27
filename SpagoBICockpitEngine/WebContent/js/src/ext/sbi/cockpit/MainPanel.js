/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit");

/**
 * Class: Sbi.cockpit.MainPanel
 * Main GUI of SpagoBICockpitEngine
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
	this.init();
	
	c = Ext.apply(c, {
         //layout   : 'fit',
         hideBorders: true,
         items    : [this.widgetContainer]
	});

	// constructor
	Sbi.cockpit.MainPanel.superclass.constructor.call(this, c);
	 
	this.addEvents("returnToMyAnalysis");
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
    
    , isInsert: null
    
//    , fromMyAnalysis: false
   

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
		
		var analysisState = this.widgetContainer.getConfiguration();
		
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
		this.isInsert = (Sbi.config.docLabel === '')?true:false;
		
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
		        },	new Ext.Toolbar.Button({
		        			id: 'save'
		        		   , iconCls: 'icon-save' 
		 				   , tooltip: 'Save'
		 				   , scope: this
		 				   , handler:  this.showSaveWin
		 				   , hidden: this.isInsert
		 		 }), new Ext.Toolbar.Button({
		 			 		id: 'saveAs'
		 			   	   , iconCls: 'icon-saveas' 
		 				   , tooltip: 'Save As'
		 				   , scope: this
		 				   , handler:  this.showSaveWinAs
		 		 }), new Ext.Toolbar.Button({
	 			 		id: 'saveAs'
			 	   	   , text: 'Debug'
			 	       , scope: this
			 		   , handler:  this.debug
			 	 })
		    ]
		});
	}
	
	, debug: function() {
		var conf = this.widgetContainer.getConfiguration();
		this.widgetContainer.resetConfiguration();
		Sbi.trace("[MainPanel.debug]: Configuration succesfully reset");
		alert("Configuration succesfully reset");
		this.widgetContainer.setConfiguration(conf);
		alert("Configuration succesfully set: " + this.widgetContainer.getWidgetsCount());
		Sbi.trace("[MainPanel.debug]: Configuration succesfully set: " + this.widgetContainer.getWidgetsCount());
	}
	
	, addWidget: function() {
		// add an empty widget in the default region of the container
		this.widgetContainer.addWidget();
	}
		
	
	, initWidgetContainer: function() { 
		Sbi.trace("[MainPanel.initWidgetContainer]: IN");

		this.widgetContainer = new Sbi.cockpit.core.WidgetContainer(this.template);

		Sbi.trace("[MainPanel.initWidgetContainer]: widget panel succesfully created");
		
		Sbi.trace("[MainPanel.initWidgetContainer]: OUT");
	}
	
	
	, showSaveWin: function() {
		this.isInsert = false;
		this.showSaveWindow();
	}
	
	, showSaveWinAs: function() {
		this.isInsert = true;
		this.showSaveWindow();
	}
	, showSaveWindow: function(){
		Sbi.trace("[MainPanel.showSaveWindow]: IN");
		if(this.saveWindow != null){		
			this.saveWindow.close();
			this.saveWindow.destroy();
		}

		var template = this.getAnalysisState();
		var templeteStr = Ext.util.JSON.encode(template);
		
		var documentWindowsParams = {				
			'OBJECT_TYPE': 'DOCUMENT_COMPOSITE',
			'OBJECT_TEMPLATE': templeteStr,
			'typeid': 'COCKPIT'
		};
		
		var formState = {};
		formState.visibility = true; //default for insertion
		formState.OBJECT_FUNCTIONALITIES  = Sbi.config.docFunctionalities;
		
		if (this.isInsert){
			formState.docLabel = 'cockpit__' + Math.floor((Math.random()*1000000000)+1); 
			if (Sbi.config.docDatasetLabel) {
				documentWindowsParams.dataset_label= Sbi.config.docDatasetLabel;
			}
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE';
		} else {
			formState.docLabel = Sbi.config.docLabel;
			formState.docName = Sbi.config.docName;
			formState.docDescr = Sbi.config.docDescription;
			formState.visibility = Sbi.config.docIsVisible;
			formState.isPublic = Sbi.config.docIsPublic;
			documentWindowsParams.MESSAGE_DET= 'MODIFY_COCKPIT';	
		}
		documentWindowsParams.formState = formState;
		documentWindowsParams.isInsert = this.isInsert;
		documentWindowsParams.fromMyAnalysis = Sbi.config.fromMyAnalysis;
		
		this.saveWindow = new Sbi.widgets.SaveDocumentWindow(documentWindowsParams);
		this.saveWindow.addListener('syncronizePanel', this.onSyncronizePanel, this);
		this.saveWindow.addListener('closeDocument', this.returnToMyAnalysis, this);
		this.saveWindow.show();		

		Sbi.trace("[MainPanel.showSaveWindow]: OUT");
	}
	
	, onSyncronizePanel: function(p) {		
		//after insert redefines the buttons toolbar
		if (this.isInsert == true && p.docLabel.value !== undefined && p.docLabel.value !== null && 
				p.docLabel.value !==""){
			this.isInsert = false;
			
			var itemEl = Ext.get('save');
			if(itemEl && itemEl !== null) {
				itemEl.hidden = false;
			}	
			Sbi.config.docLabel = p.docLabel.value;
		}
	}
	
	, returnToMyAnalysis : function() {
	   var url = Sbi.config.contextName + '/servlet/AdapterHTTP?ACTION_NAME=CREATE_DOCUMENT_START_ACTION&LIGHT_NAVIGATOR_RESET_INSERT=TRUE';
	   if (Sbi.config.environment == "MYANALYSIS")
		   sendMessage({newUrl:url},'closeDocument');		  
	   else if (Sbi.config.environment == "DOCBROWSER")
		   sendMessage({},'closeDocument');
	   else
		   window.location = url;
	   
	}
	
});