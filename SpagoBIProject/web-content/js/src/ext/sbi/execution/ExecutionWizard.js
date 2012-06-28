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

Ext.ns("Sbi.execution");

Sbi.execution.ExecutionWizard = function(config, doc) {
	
	this.baseConfig = config;
	this.document = doc;
	
	// always declare exploited services first!
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
	this.services = new Array();
	this.services['startExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'START_EXECUTION_PROCESS_ACTION'
		, baseParams: params
	});
	
	this.services['getParametersForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETERS_FOR_EXECUTION_ACTION'
		, baseParams: params
	});
	
	this.services['showSendToForm'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SHOW_SEND_TO_FORM'
		, baseParams: params
	});
	
	this.services['saveIntoPersonalFolder'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SAVE_PERSONAL_FOLDER'
		, baseParams: params
	});
	
	this.addEvents('executionfailure', 'beforetoolbarinit', 'documentexecutionpageinit');
	
	this.isFromCross = config.isFromCross || false;
	 
	// propagate preferences to role selection page
	var roleSelectionPageConfig = Ext.applyIf({}, config.preferences);
	this.roleSelectionPage = new Sbi.execution.RoleSelectionPage(roleSelectionPageConfig, this.document);
	this.roleSelectionPage.maskOnRender = true;
	
	// propagate preferences to parameters selection page
	var parametersSelectionPageConfig = Ext.applyIf({isFromCross: this.isFromCross}, config.preferences);
	this.parametersSelectionPage =  new Sbi.execution.ParametersSelectionPage(parametersSelectionPageConfig || {}, this.document);
	this.parametersSelectionPage.maskOnRender = true;

	// 20100505: set if coming from tree or list of documents
	if (config.preferences){
		if(config.preferences.fromDocTreeOrList){
			if(config.preferences.fromDocTreeOrList == true){			
				//this.documentExecutionPage.callFromTreeListDoc = true;
				this.parametersSelectionPage.callFromTreeListDoc = true;
				this.roleSelectionPage.callFromTreeListDoc = true;
			}
		}
	}
	
	/*
	this.errorPage = new Ext.Panel({
		layout: 'fit'
		, html: LN('sbi.execution.error')
	});
	*/
	
	this.activePageNumber = 0;
	
	this.roleSelectionPage.on('movenextrequest', this.moveToNextPage, this);
	this.roleSelectionPage.on('beforetoolbarinit', function(page, toolbar){
		this.fireEvent('beforetoolbarinit', toolbar);
	}, this);

	// 20100505
	this.roleSelectionPage.on('backToAdmin', this.backToAdmin, this);
	this.parametersSelectionPage.on('backToAdmin', this.backToAdmin, this);
		
	this.parametersSelectionPage.on('moveprevrequest', this.moveToPreviousPage, this);
	this.parametersSelectionPage.on('movenextrequest', this.moveToNextPage, this);
	this.parametersSelectionPage.on('beforetoolbarinit', function(page, toolbar){
		this.fireEvent('beforetoolbarinit', toolbar);
	}, this);
	
	var c = Ext.apply({}, config, {
		layout:'card',
		hideMode: !Ext.isIE ? 'nosize' : 'display',
		activeItem: this.activePanel || 0, // index or id
		//tbar: this.tb,
		items: [
		 this.roleSelectionPage
		 , this.parametersSelectionPage
		 //, this.documentExecutionPage
		 //, this.errorPage 
		]		        
	});
	
	// constructor
    Sbi.execution.ExecutionWizard.superclass.constructor.call(this, c);
    
    this.roleSelectionPage.addListener('synchronize', this.onRolesForExecutionLoaded, this);
    this.roleSelectionPage.addListener('synchronizeexception', this.onRolesForExecutionLoadException, this);
    
    /*
    if(config.document) {
    	this.execute( config.document );
    }
    */
    	
};

Ext.extend(Sbi.execution.ExecutionWizard, Ext.Panel, {
    
	services: null
	, baseConfig: null
    , executionInstance: null
    , isFromCross: null
    
    , prevActivePageNumber: null
    , activePageNumber: null
    
    , roleSelectionPage: null
    , parametersSelectionPage: null
    , documentExecutionPage: null 
    //, errorPage: null 
    
    , ROLE_SELECTION_PAGE_NUMBER: 0 
	, PARAMETER_SELECTION_PAGE_NUMBER: 1 
	, EXECUTION_PAGE_NUMBER: 2 
	//, ERROR_PAGE_NUMBER: 3 
    
	, initDocumentExecutionPage: function() {
		var documentExecutionPageConfig = Ext.applyIf({maskOnRender: true}, this.baseConfig.preferences);
		// preferences for shortcuts ARE NOT PROPAGATED to execution page (since panels on ShortcutPanel are instantiated twice, this may generate conflicts)
		if (documentExecutionPageConfig !== undefined) {
			delete documentExecutionPageConfig.subobject;
			delete documentExecutionPageConfig.snapshot;
		}
		if (this.parametersSelectionPage.isParameterPanelReadyForExecution === true) {
			documentExecutionPageConfig.hideParametersPanel = true; 
		}
		
		this.documentExecutionPage = new Sbi.execution.DocumentExecutionPage(documentExecutionPageConfig || {}, this.document);
		
		// 20100505
		this.documentExecutionPage.on('backToAdmin', this.backToAdmin, this);
		
		this.documentExecutionPage.on('moveprevrequest', this.moveToPreviousPage, this);
		this.documentExecutionPage.on('beforetoolbarinit', function(page, toolbar){
			this.fireEvent('beforetoolbarinit', toolbar);
		}, this);
		
	    this.documentExecutionPage.addListener('beforerefresh', function(){ this.prevActivePageNumber = this.EXECUTION_PAGE_NUMBER; }, this);
	    this.documentExecutionPage.addListener('loadurlfailure', this.onLoadUrlFailure, this);
		
		// 20100505: set if coming from tree or list of documents
		if (this.baseConfig.preferences && this.baseConfig.preferences.fromDocTreeOrList 
				&& this.baseConfig.preferences.fromDocTreeOrList == true){
					this.documentExecutionPage.callFromTreeListDoc = true;
					this.documentExecutionPage.toolbar.callFromTreeListDoc = true;
		}
		
		this.fireEvent('documentexecutionpageinit');
	}
	
	
    // public methods
    
    // toolbar
    , moveToPage: function(pageNumber) {
		
		this.prevActivePageNumber = this.activePageNumber;
		this.activePageNumber = pageNumber;
	
		// up-hill ->
		if(this.prevActivePageNumber == this.ROLE_SELECTION_PAGE_NUMBER && this.activePageNumber == this.PARAMETER_SELECTION_PAGE_NUMBER) {
			this.roleSelectionPage.loadingMask.hide();
			this.startExecution();
		}
		if(this.prevActivePageNumber == this.PARAMETER_SELECTION_PAGE_NUMBER && this.activePageNumber == this.EXECUTION_PAGE_NUMBER) {
			// save parameters into session
			Sbi.execution.SessionParametersManager.saveStateObject(this.parametersSelectionPage.parametersPanel);
			Sbi.execution.SessionParametersManager.updateMementoObject(this.parametersSelectionPage.parametersPanel);
			
			// init document execution page, in case it was not initialized yet
    		if (this.documentExecutionPage == null) {
    			this.initDocumentExecutionPage();
    			this.add(this.documentExecutionPage);
    			this.documentExecutionPage.on('render', function() {
    		    	// load execution url
    				this.loadUrlForExecution();
    			}, this);
    		} else {
    			this.documentExecutionPage.southPanel.collapse();
    			this.documentExecutionPage.northPanel.collapse();
    	    	// load execution url
    			this.loadUrlForExecution();
    		}
    		
		}
		if(this.prevActivePageNumber == this.EXECUTION_PAGE_NUMBER && this.activePageNumber == this.EXECUTION_PAGE_NUMBER) { // todo: handle refresh properly
			this.documentExecutionPage.southPanel.collapse();
			this.documentExecutionPage.northPanel.collapse();
			this.loadUrlForExecution();
		}
		
		// down-hill <-
		if(this.prevActivePageNumber == this.EXECUTION_PAGE_NUMBER && this.activePageNumber == this.PARAMETER_SELECTION_PAGE_NUMBER) {
			delete this.executionInstance.SBI_SUBOBJECT_ID;
			delete this.executionInstance.SBI_SNAPSHOT_ID;
			Sbi.execution.SessionParametersManager.restoreMementoObject(this.parametersSelectionPage.parametersPanel);
			// force synchronization, since subobject, snapshots, viewpoints may have been deleted, or a new subobject may have been created
			this.parametersSelectionPage.shortcutsPanel.synchronize(this.executionInstance);
		}
		
		this.getLayout().setActiveItem( this.activePageNumber );
	}

    , moveToPreviousPage: function() {
    	this.moveToPage( this.activePageNumber-1 );
	}
    
    , moveToNextPage: function() {
    	this.moveToPage( this.activePageNumber+1 );
	}
	
	// 20100505
	, backToAdmin: function(){
		// build url to go back one page
		var serviceRegistry = Sbi.config.serviceRegistry;
		
		var urlToCall = Sbi.config.serviceRegistry.getBaseUrlStr({
			//isAbsolute :  true
		});		

		urlToCall = urlToCall+'?LIGHT_NAVIGATOR_BACK_TO=1';		
		
		//alert(urlStr);
		window.location=urlToCall;
    }
    // execution
    , execute : function() {
    	this.roleSelectionPage.loadingMask.show();
		if(!this.document || (!this.document.id && !this.document.label) ) {
			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.execution.error.nodocid'), 'Intenal Error');
		}
		
		this.executionInstance = {}
		if(this.document.id) this.executionInstance.OBJECT_ID = this.document.id;
		if(this.document.label) this.executionInstance.OBJECT_LABEL = this.document.label;
		this.executionInstance.document = this.document;
		this.executionInstance.isFromCross = this.isFromCross;
		
		this.loadRolesForExecution();
	}

	, loadRolesForExecution: function() {
		this.roleSelectionPage.synchronize( this.executionInstance );
	}
	
	, loadUrlForExecution: function() {
		var formState = this.parametersSelectionPage.parametersPanel.getFormState();
		this.executionInstance.PARAMETERS = Sbi.commons.JSON.encode( formState );
		this.documentExecutionPage.parametersPanel.on(
				'synchronize',
				this.updateParametersFormState,
				this
		);
		
		this.documentExecutionPage.synchronize( this.executionInstance );
	}
	
	// update parameters form state when moving from parameters page to execution page
	, updateParametersFormState: function () {
		var formState = this.parametersSelectionPage.parametersPanel.getFormState();
		this.documentExecutionPage.parametersPanel.setFormState(formState);
		// removes listener
		this.documentExecutionPage.parametersPanel.un(
				'synchronize',
				this.updateParametersFormState,
				this
		);
	}
	
	, onRolesForExecutionLoaded: function(form, store, records, options) {
		var rolesNo = store.getCount();
		if(rolesNo === 0) {
			alert(LN('sbi.execution.error.novalidrole'));
		} else if(rolesNo === 1) {
			var role = store.getRange()[0];
			form.roleComboBox.setValue(role.data.name); 
			this.executionInstance.isPossibleToComeBackToRolePage = false;
			this.moveToNextPage();
		} else {
			this.roleSelectionPage.loadingMask.hide();
		}
	}
	
	, onRolesForExecutionLoadException: function(form, store) {
		this.moveToPage(3);
		this.roleSelectionPage.loadingMask.hide();
	}
	
	
	, startExecution: function() {
		var role = this.roleSelectionPage.getSelectedRole();
		this.executionInstance.ROLE = role;
		
		Ext.Ajax.request({
	          url: this.services['startExecutionService'],
	          params: this.executionInstance,
	          callback : function(options , success, response){
	    	  	if(success && response !== undefined) {   
		      		if(response.responseText !== undefined) {
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if(content !== undefined) {
		      				this.onExecutionStarted(content.execContextId);
		      				
		      			} 
		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
	    	  	}
	          },
	          scope: this,
	  		  failure: Sbi.exception.ExceptionHandler.handleFailure      
	     });
	}
	
	, onExecutionStarted: function( execContextId ) {
		this.executionInstance.SBI_EXECUTION_ID = execContextId;
		this.parametersSelectionPage.synchronize(this.executionInstance);
	}
	
	, onLoadUrlFailure: function ( errors ) {
		var messageBox = Ext.MessageBox.show({
				title: 'Error',
				msg: errors,
				modal: false,
				buttons: Ext.MessageBox.OK,
				width:300,
				icon: Ext.MessageBox.ERROR,
				animEl: 'root-menu'        			
		});
		if(this.prevActivePageNumber !== this.EXECUTION_PAGE_NUMBER){
			this.moveToPage( this.prevActivePageNumber ); 
		}		
	}
	
});
