/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit");

/**
 * @class Sbi.cockpit.MainPanel
 * @extends Ext.Panel
 * 
 * The main panel of SpagoBI's cockpit engine.
 */

/**
 * @cfg {Object} config The configuration object passed to the constructor
 */
Sbi.cockpit.MainPanel = function(config) {
	
	this.validateConfigObject(config);
	this.adjustConfigObject(config);
	
	
	// init properties...
	var defaultSettings = {
			hideBorders: true
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.core', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	this.initServices();
	this.init();
	
	c = Ext.apply(c, {
		id: "mainPanel",
		bodyCls : "mainPanel",
        items    : [this.widgetContainer]
	});

	// constructor
	Sbi.cockpit.MainPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.cockpit.MainPanel, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
    services: null
    
    /**
     * @property {Sbi.cockpit.core.WidgetContainer} widgetContainer
     * The container that manage the layout off all the widget contained in this cockpit
     */
    , widgetContainer: null
    
    /**
	 * @property {Ext.Window} widgetEditorWizard
	 * The wizard that manages the single widget definition
	 */
	, associationEditorWizard: null
    
    , msgPanel: null
    
    // TODO remove from global
    , saved: null
   

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
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method
	 * @deprecated
	 * 
	 * Returns the analysis state of this engine encoded as string. This method is usually called
	 * by parent container to generate the template to store in SpagoBI database when the document is saved
	 * by the user.
	 * 
	 * Replaced by #validateAnalysisState
	 */
	, validate: function (successHandler, failureHandler, scope) {
		Sbi.trace("[MainPanel.validate]: IN");
		
		var templeteStr = this.getTemplate();
		Sbi.trace("[MainPanel.validate]: template = " + templeteStr);
		
		Sbi.trace("[MainPanel.validate]: OUT");
		return templeteStr;
	}
	
	/**
	 * @method 
	 * Returns the cockpit current template that is equal to the current analysisState
	 * encoded as string
	 * 
	 * @return {String} The current template
	 */
	, getTemplate: function() {
		var template = this.getAnalysisState();
		var templeteStr = Ext.JSON.encode(template);
		return templeteStr;
	}
	
	/**
	 * @method 
	 * Convert the template received as argument into a JSON object and the use it to set the current
	 * analysis state of the cockpit.
	 * 
	 * @param {String} template The template
	 */
	, setTemplate: function(template) {
		Sbi.trace("[MainPanel.setTemplate]: IN");
		if(Ext.isString(template)) {
			var analysisState = Ext.JSON.decode(template);
			this.setAnalysisState(analysisState);
		} else {
			Sbi.trace("[MainPanel.setTemplate]: Input parameter [template] is not of type [string]");
		}
		Sbi.trace("[MainPanel.setTemplate]: OUT");
	}
	
	/**
	 * @method
	 * 
	 * Returns weather the current analysis state is valid or not. Some engine during editing phase can
	 * allow inconsistent states. This method is usually called to deciede if the document can be saved or
	 * not. 
	 */
	, isValidAnalysisState: function() {
		// in cockpit engine all possible editing states are valid
		return true;
	}
	
	, validateAnalysisState: function(successHandler, failureHandler, scope) {
		var returnState = true;
		var analysisState =  this.getAnalysisState();
		
		successHandler = successHandler || function(){return true;};
		failureHandler = failureHandler || function(){return true;};
		
		if(this.isValidAnalysisState()) {
			if( successHandler.call(scope || this, analysisState) === false) {
				returnState = false;
			}
		} else { // impossible to go into this branch because the cockpit is allways valid :)
			// get the list of validation error messages
			var validationErrors = [];
			validationErrors.push("Error 1 caused by problem A");
			validationErrors.push("Error 2 caused by problem B");
			if( failureHandler.call(scope || this, analysisState, validationErrors) === false) {
				returnState = false;
			}
		}
		
		if(returnState) {
			return analysisState;
		} else {
			return null;
		}
	}
	
	/**
	 * @method
	 * 
	 * Returns the current analysis state. For the cockpit engine it is equal to #widgetContainer configuration
	 * and Sbi.storeManager configuration
	 * 
	 * @return {Object} The analysis state.
	 */
	, getAnalysisState: function () {	
		var analysisState = {};
		
		analysisState.widgetsConf = this.widgetContainer.getConfiguration();
		analysisState.storesConf = Sbi.storeManager.getConfiguration();
		analysisState.associationsConf = Sbi.storeManager.getAssociationsConfiguration();
		
		return analysisState;
	}
	
	, resetAnalysisState: function() {
		this.widgetContainer.resetConfiguration();
		Sbi.storeManager.resetConfiguration();
		Sbi.storeManager.resetAssociations();
	}
	
	/**
	 * @method
	 */
	, setAnalysisState: function(analysisState) {
		Sbi.trace("[MainPanel.setAnalysisState]: IN");
		Sbi.storeManager.setConfiguration(analysisState.storesConf);
		Sbi.storeManager.setAssociationsConfiguration(analysisState.associationsConf);
		this.widgetContainer.setConfiguration(analysisState.widgetsConf);
		Sbi.trace("[MainPanel.setAnalysisState]: OUT");
	}
	
	, isDocumentSaved: function() {
		
		if(Sbi.isNotValorized(this.documentSaved)) {
			this.documentSaved = !Ext.isEmpty(Sbi.config.docLabel);
		}
		
		return this.documentSaved ;
	}
	
	, isDocumentNotSaved: function() {
		return !this.isDocumentSaved();
	}
	
	, closeDocument : function() {
		Sbi.trace("[MainPanel.closeDocument]: IN");
		
		var url = Sbi.config.contextName + '/servlet/AdapterHTTP?ACTION_NAME=CREATE_DOCUMENT_START_ACTION&LIGHT_NAVIGATOR_RESET_INSERT=TRUE';
		
		Sbi.trace("[MainPanel.closeDocument]: go back to [" + Sbi.config.environment + "]");
		
		if (Sbi.config.environment == "MYANALYSIS") {
			sendMessage({newUrl:url},'closeDocument');	
		} else if (Sbi.config.environment == "DOCBROWSER") {
			sendMessage({},'closeDocument');
		} else {
			window.location = url;
		}
			   
		Sbi.trace("[MainPanel.closeDocument]: IN");   
	}
	
	, showSaveDocumentWin: function() {
		this.showSaveDocumentWindow(false);
	}
	
	, showSaveDocumentAsWin: function() {
		this.showSaveDocumentWindow(true);
	}
	
	, showSaveDocumentWindow: function(insert){
		Sbi.trace("[MainPanel.showSaveDocumentWindow]: IN");
		if(this.saveWindow != null){		
			this.saveWindow.close();
			this.saveWindow.destroy();
		}

		var template = this.getTemplate();
	
		
		var documentWindowsParams = {				
			'OBJECT_TYPE': 'DOCUMENT_COMPOSITE',
			'OBJECT_TEMPLATE': template,
			'typeid': 'COCKPIT'
		};
		
		var formState = {};
		formState.visibility = true; //default for insertion
		formState.OBJECT_FUNCTIONALITIES  = Sbi.config.docFunctionalities;
		
		if (insert === true) {
			formState.docLabel = 'cockpit__' + Math.floor((Math.random()*1000000000)+1); 
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE';
			Sbi.trace("[MainPanel.showSaveDocumentWindow]: Document [" + formState.docLabel + "] will be created");
		} else {
			formState.docLabel = Sbi.config.docLabel;
			formState.docName = Sbi.config.docName;
			formState.docDescr = Sbi.config.docDescription;
			formState.visibility = Sbi.config.docIsVisible;
			formState.isPublic = Sbi.config.docIsPublic;
			documentWindowsParams.MESSAGE_DET= 'MODIFY_COCKPIT';
			Sbi.trace("[MainPanel.showSaveDocumentWindow]: Document [" + formState.docLabel + "] will be updated");
		}
		documentWindowsParams.formState = formState;
		documentWindowsParams.isInsert = insert;
		documentWindowsParams.fromMyAnalysis = Sbi.config.fromMyAnalysis;
		
		this.saveWindow = new Sbi.widgets.SaveDocumentWindow(documentWindowsParams);
		
		this.saveWindow.on('savedocument', this.onSaveDocument, this);
		//this.saveWindow.on('closeDocument', this.returnToMyAnalysis, this);
		
		this.saveWindow.show();		

		Sbi.trace("[MainPanel.showSaveDocumentWindow]: OUT");
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	// utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, onAddWidget: function() {
		// add an empty widget in the default region of the container
		this.widgetContainer.addWidget();
	}
	
	, onShowAssociationEditorWizard: function(){
		var config = {};
		config.storeList = Sbi.storeManager.getStoreIds();
		config.state = this.getAnalysisState();
//		if(this.associationEditorWizard === null) {    		
    		Sbi.trace("[MainPanel.showAssociationEditorWizard]: instatiating the editor");    		
    		this.associationEditorWizard = Ext.create('Sbi.data.AssociationEditorWizard',config);
    		this.associationEditorWizard.on("submit", this.onAssociationEditorWizardSubmit, this);
    		this.associationEditorWizard.on("cancel", this.onAssociationEditorWizardCancel, this);
//    		this.associationEditorWizard.on("apply", this.onAssociationEditorWizardApply, this);    		
	    	Sbi.trace("[MainPanel.showAssociationEditorWizard]: editor succesfully instantiated");
//    	}
				
		this.associationEditorWizard.show();
	}
	
	, onAssociationEditorWizardCancel: function(wizard) {
		Sbi.trace("[MainPanel.onAssociationEditorWizardCancel]: IN");
//		this.associationEditorWizard.hide();
		this.associationEditorWizard.close();
		Sbi.trace("[MainPanel.onAssociationEditorWizardCancel]: OUT");
	}
	
	, onAssociationEditorWizardSubmit: function(wizard) {
		Sbi.trace("[MainPanel.onAssociationEditorWizardSubmit]: IN");
		var wizardState = wizard.getWizardState();
		if (wizardState.associationsList != null && wizardState.associationsList !== undefined){
			Sbi.storeManager.resetAssociations(); //reset old associations
			Sbi.storeManager.setAssociations(wizardState.associationsList);
			Sbi.trace("[MainPanel.onAssociationEditorWizardSubmit]: setted relation group [" + Sbi.toSource(wizardState.associationsList) + "] succesfully added to store manager");
		}
//		this.associationEditorWizard.hide();
		this.associationEditorWizard.close();
		Sbi.trace("[MainPanel.onAssociationEditorWizardSubmit]: OUT");
	}
	
	, onShowSaveDocumentWindow: function() {
		this.showSaveDocumentWin();
	}
	
	, onShowSaveDocumentAsWindow: function() {
		this.showSaveDocumentAsWin();
	}
	
	, onSaveDocument: function(win, closeDocument, params) {	
		Sbi.trace("[MainPanel.onSaveDocument]: IN");
		this.documentSaved = true;
		
		// show save button (the button that allow to perform save as)
		var itemEl = Ext.get('save');
		if(itemEl && itemEl !== null) {
			itemEl.hidden = false;
		}	
		
		Sbi.trace("[MainPanel.onSaveDocument]: Input parameter [closeDocument] is equal to [" + closeDocument + "]");
		if(closeDocument === true) {
			this.closeDocument();
		}
		Sbi.trace("[MainPanel.onSaveDocument]: OUT");
	}
	
	, onDebug: function() {
		this.cockpitConfigurationTest();
	}
	
	//-----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------

	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component.
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
	
		this.tbar = new Ext.Toolbar({
		    items: [
		        '->', // same as {xtype: 'tbfill'}, // Ext.Toolbar.Fill
		        {
		        	text: LN('sbi.cockpit.mainpanel.btn.associations')
		        	, handler: this.onShowAssociationEditorWizard
		        	, scope: this
		        },{
		        	text: LN('sbi.cockpit.mainpanel.btn.addWidget')
		        	, handler: this.onAddWidget
		        	, scope: this
		        },	new Ext.Button({
		        			id: 'save'
		        		   , iconCls: 'icon-save' 
		 				   , tooltip: 'Save'
		 				   , scope: this
		 				   , handler:  this.onShowSaveDocumentWindow
		 				   , hidden: this.isDocumentNotSaved()
		 		 }), new Ext.Button({
		 			 		id: 'saveAs'
		 			   	   , iconCls: 'icon-saveas' 
		 				   , tooltip: 'Save As'
		 				   , scope: this
		 				   , handler:  this.onShowSaveDocumentAsWindow
		 		 }), new Ext.Button({
	 			 		id: 'debug'
			 	   	   , text: 'Debug'
			 	       , scope: this
			 		   , handler:  this.onDebug
			 	 })
		    ]
		});
	}		
	
	, initWidgetContainer: function() { 
		Sbi.trace("[MainPanel.initWidgetContainer]: IN");

		var conf = {};
		if(Sbi.isValorized(this.analysisState)) {
			conf = this.analysisState.widgetsConf;
		}
		this.widgetContainer = new Sbi.cockpit.core.WidgetContainer(conf);
		delete this.analysisState;

		Sbi.trace("[MainPanel.initWidgetContainer]: widget panel succesfully created");
		
		Sbi.trace("[MainPanel.initWidgetContainer]: OUT");
	}
	
	
	//-----------------------------------------------------------------------------------------------------------------
	// test methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, containerConfigurationTest: function() {
		var conf = this.widgetContainer.getConfiguration();
		this.widgetContainer.resetConfiguration();
		Sbi.trace("[MainPanel.containerConfigurationTest]: Configuration succesfully reset");
		alert("Configuration succesfully reset");
		this.widgetContainer.setConfiguration(conf);
		alert("Configuration succesfully set: " + this.widgetContainer.getWidgetsCount());
		Sbi.trace("[MainPanel.containerConfigurationTest]: Configuration succesfully set: " + this.widgetContainer.getWidgetsCount());
	}
	
	, cockpitConfigurationTest: function() {
		var template = this.getTemplate();
		Sbi.trace("[MainPanel.cockpitConfigurationTest]: Current configuration saved [" + template + "]");
		this.resetAnalysisState();
		Sbi.trace("[MainPanel.cockpitConfigurationTest]: Configuration succesfully reset");
		alert("Configuration succesfully reset");
		this.setTemplate(template);
		alert("Configuration succesfully set: " + this.widgetContainer.getWidgetsCount());
		Sbi.trace("[MainPanel.cockpitConfigurationTest]: Configuration succesfully set: " + this.widgetContainer.getWidgetsCount());
	}	
});