/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **/

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
 * - Giulio gavardi (giulio.gavardi@eng.it)
 */

Ext.ns("Sbi.browser.mexport");

Sbi.browser.mexport.MassiveExportWizard = function(config) {

	var defaultSettings = {
			title: LN('sbi.browser.mexport.massiveExportWizard.title')
			, layout: 'fit'
			, width: 800
			, height: 300           	
			, closable: true
			, constrain: true
			, hasBuddy: false
			, resizable: true
	};
	if (Sbi.settings && Sbi.settings.browser 
			&& Sbi.settings.browser.mexport && Sbi.settings.browser.mexport.massiveExportWizard) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.browser.mexport.massiveExportWizard);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);

	this.services = this.services || new Array();

	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null, TYPE: 'WORKSHEET'};
	this.services['StartMassiveExportExecutionProcessAction'] = this.services['StartMassiveExportExecutionProcessAction'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'START_MASSIVE_EXPORT_EXECUTION_PROCESS_ACTION'
		, baseParams: new Object()
	});	
	this.services['getAnalyticalDriversFromDocsInFolderAction'] = this.services['getAnalyticalDriversFromDocsInFolderAction'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_ANALYTICAL_DRIVER_FROM_DOCS_IN_FOLDER_ACTION'
			, baseParams: new Object()
	});	
	this.services['startMassiveExportThreadAction'] = this.services['startMassiveExportThreadAction'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'START_MASSIVE_EXPORT_THREAD_ACTION'
		, baseParams: new Object()
	});
	
	//this.addEvents();
	this.initMainPanel(c);	
	c = Ext.apply(c, {
		layout: 'fit'
		, items: [this.mainPanel]	
	});

	// constructor
	Sbi.browser.mexport.MassiveExportWizard.superclass.constructor.call(this, c);
	
	//this.addEvents();
};

Ext.extend(Sbi.browser.mexport.MassiveExportWizard, Ext.Window, {

	services: null
    , mainPanel: null
    , btnPrev: null
	, btnNext: null
	, btnFinish: null
	, activePageNumber: null
	, pages: null
	
    , optionsPage: null
    , parametersPage: null
    , OPTIONS_PAGE_NUMBER: null
	, PARAMETERS_PAGE_NUMBER: null
    
    , functId: null
    , functCd: null
	, executionInstances: null
    
	// ----------------------------------------------------------------------------------------
	// public methods
	// ----------------------------------------------------------------------------------------
	
	/**
	 * @returns whether this wizard could be finished without further user interaction. 
	 * Typically, this is used by the wizard to enable or disable the Finish button.
	 */
	, canFinish: function() {
		//return this.activePageNumber == this.PARAMETERS_PAGE_NUMBER;
		return this.activePageNumber == this.pages.length - 1;
	}
    
	/**
	 * Called by the wizard when the Finish button is pressed.
	 */
	, performFinish: function() {
		// get all values
		var state = this.getParametersPanelFormState();
		var jsonState = Sbi.commons.JSON.encode( state );
		var selRole = this.optionsPage.getSelectedRole();
		var splittingFiltersB = this.optionsPage.isCycleOnFilterSelected();
		var params = {
	    	selectedRole : selRole
	       	, functId : this.functId
	       	, type : 'WORKSHEET'
	       	, splittingFilter : splittingFiltersB
	       	, parameterValues : jsonState
		};
		
		// Start mamssive export
		Ext.Ajax.request({
		        url: this.services['startMassiveExportThreadAction']
		        , params: params
		        , success : function(response, options){}
				, failure: Sbi.exception.ExceptionHandler.handleFailure      
				, scope: this
		});
		
		var messageBox = Ext.MessageBox.show({
				title: 'Status',
				msg: LN('Export thread started for worksheet in functionality '+this.functCd+"; check Progress Panel on the left to know progress"),
				modal: true,
				buttons: Ext.MessageBox.OK,
				width:500,
				icon: Ext.MessageBox.INFO,
				animEl: 'root-menu'        			
		});
		this.close();
	}
	
    // ----------------------------------------------------------------------------------------
	// private methods
	// ----------------------------------------------------------------------------------------
	
    , initMainPanel: function(c) {
    	
    	this.initButtons();
		this.initPages()
		
	
		this.mainPanel = new Ext.Panel({  
			layout: 'card',  
			activeItem: 0,  
			scope: this,
			height: 420,
			autoWidth: true,
			resizable: true,
			defaults: {border:false},  
			bbar: [
			       this.btnPrev,
			       '->', // greedy spacer so that the buttons are aligned to each side
			       this.btnNext,
			       this.btnFinish
			], 
			items: this.pages
		});
		
		this.mainPanel.doLayout();
		
		this.activePageNumber = 0;
    }

    , initButtons: function() {
    	this.btnPrev = new Ext.Button({
	        text: LN('sbi.browser.mexport.massiveExportWizard.button.back')
	        , handler: this.moveToPreviousPage
	        , scope: this
	        , disabled : true
		});
	
		this.btnNext = new Ext.Button({
	        text: LN('sbi.browser.mexport.massiveExportWizard.button.next')
	        , handler: this.moveToNextPage
	        , scope: this
	        , bdisabled : false
		});
	
		this.btnFinish = new Ext.Button({
	        text: LN('sbi.browser.mexport.massiveExportWizard.button.finish')
	        , handler: this.performFinish
	        , scope: this
	        , disabled: true
		});
    }
    
	, initPages: function() {
		this.pages = [];
		this.initOptionsPage();
		this.initParametersPage();
	}
	
	, initOptionsPage: function() {
		var config = {functId: this.functId};
		this.optionsPage = new Sbi.browser.mexport.MassiveExportWizardOptionsPage(config);
		this.optionsPage.on('noDocsEvent', 
				function() {
					this.btnNext.disable();
					this.btnFinish.disable();
				}
			, this
		);
		
		this.OPTIONS_PAGE_NUMBER = this.pages.length;
		this.pages.push(this.optionsPage);
	}
	
	, initParametersPage: function() {
		var ser = new Array();
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'
					   , SBI_EXECUTION_ID: null
					   
		};
		ser['getParametersForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_ANALYTICAL_DRIVER_FROM_DOCS_IN_FOLDER_ACTION'
			, baseParams: params
		});
	
		var config = {
			services : ser	
			, contest : 'massiveExport'
			, drawHelpMessage : false	
		};
		
		this.parametersPage = new Sbi.execution.ParametersPanel(config);
		
		this.PARAMETERS_PAGE_NUMBER = this.pages.length;
		this.pages.push(this.parametersPage);
	}

	, createExecutionInstances: function(pars) {
		pars = Ext.apply(pars, {modality: 'CREATE_EXEC_CONTEST_ID_MODALITY'});
		Ext.Ajax.request({
	        url: this.services['StartMassiveExportExecutionProcessAction'],
	       
	        params: pars,
	        
	        //callback : function(options , success, response){
	        success : function(response, options) {
	        if(response !== undefined) {   
	      		if(response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if(content !== undefined) {
	      				this.executionInstances = {
	      						SBI_EXECUTION_ID: content.execContextId
	      					};
	      		  		for(p in this.parametersPage.fields){
	      		  			var field = this.parametersPage.fields[p];
	      		  			field.enable();
	      		  		}
	      		  		this.btnFinish.enable();
	      				pars = Ext.apply(pars, this.executionInstances);
	      				this.parametersPage.loadParametersForExecution(pars);
	      			} 
	      		} else {
	      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
	      		}
		  	}
		  	else{
		  	//clear preceding store if error happened
		  		for(p in this.parametersPage.fields){
		  			var field = this.parametersPage.fields[p];
		  			field.disable();
		  		}
		  		this.btnFinish.disable();
		  	}
	      },
	        scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure      
	   });
	}	
	
	/**
	 * returns the value selected of the parameters in parametersPanel,
	 * and for each also the objparameterId (for label rinomination: name  => nameB)
	 */
	, getParametersPanelFormState: function() {
		var state;
		//to avoid synchronization problem
		
		state = {};
		for(p in this.parametersPage.fields) {
			var field = this.parametersPage.fields[p];
			var value = field.getValue();
			state[field.name] = value;
			var rawValue = field.getRawValue();
			if(value == "" && rawValue != ""){
				state[field.name] = rawValue;
			}
			
			// add objParsId information if present (massive export case)
			if(field.objParameterIds){
				for(pr=0;pr < field.objParameterIds.length;pr++){
					val = field.objParameterIds[pr];
					state[val+ '_objParameterId']=field.name;
				}
			}
		}
		return state;
	}
	
	, moveToPreviousPage: function() {
		this.moveToPage(this.activePageNumber - 1);
	}
	
	, moveToNextPage: function() {
		this.moveToPage(this.activePageNumber + 1);
	}

	, moveToPage: function(page) {
		
		var curr = this.mainPanel.layout.activeItem;
		if(page == this.PARAMETERS_PAGE_NUMBER){ 
			
			// clear the fields in case you are coming to panel for the second time
			this.parametersPage.clear();

			this.mainPanel.layout.setActiveItem(this.PARAMETERS_PAGE_NUMBER);
			this.activePageNumber = this.PARAMETERS_PAGE_NUMBER;

			// create ExecutionInstances and  get parameters 
			var selectedRole = this.optionsPage.getSelectedRole();		
			var params = {
				selectedRole : selectedRole
				, functId : this.functId
				, type : 'WORKSHEET'						
			}				
			this.createExecutionInstances(params);
			
		} else if(page == this.OPTIONS_PAGE_NUMBER) { 
				this.mainPanel.layout.setActiveItem(this.OPTIONS_PAGE_NUMBER);
				this.activePageNumber = this.OPTIONS_PAGE_NUMBER;
		} else {
			alert('page [' + page + '] not available in wizard');
		}
		
		
		if(page === 0) {
			this.btnPrev.disable();
		} else {
			this.btnPrev.enable();
		}
		
		if(page === (this.pages.length -1)) {
			this.btnNext.disable();
		} else {
			this.btnNext.enable();
		}
				
		if(this.canFinish() === true) {
			this.btnFinish.enable();
		} else {
			this.btnFinish.disable();
		}
	}

});