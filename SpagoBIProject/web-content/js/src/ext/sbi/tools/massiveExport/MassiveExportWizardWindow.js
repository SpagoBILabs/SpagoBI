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

Ext.ns("Sbi.tools.massiveExport");

Sbi.tools.massiveExport.MassiveExportWizardWindow = function(config) {

	var defaultSettings = {
			title: LN('Sbi.tools.massiveExport.MassiveExportWizardWindow.title')
			, layout: 'fit'
			, width: 800
			, height: 300           	
			, closable: true
			, constrain: true
			, hasBuddy: false
			, resizable: true
	};

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	


	this.services = new Array();

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
	
	this.addEvents();

	c = Ext.apply(c, {
		layout: 'fit'
		,  html:'swswsws'
	});

	// constructor
	Sbi.tools.massiveExport.MassiveExportWizardWindow.superclass.constructor.call(this, c);
	
	this.initMainPanel(c);
	
	this.add(this.mainPanel);
	
	this.addEvents();
	

	

};

Ext.extend(Sbi.tools.massiveExport.MassiveExportWizardWindow, Ext.Window, {

	services: null
    , mainPanel: null
    , optionsPanel: null
    , parametersPanel : null
    , functId: null
    , functCd: null
	, executionInstances: null
    , btnPrev: null
	, btnNext: null
	 , btnFinish: null
    
    , initMainPanel: function(c) {
			var config = {functId: this.functId};
	
			var navHandler = function(page){
				if(this.mainPanel !== null){
					var curr = this.mainPanel.layout.activeItem;
				if(page == 1){ // PARAMETER PANEL CASE
						// clear the fields in case you are coming to panel for the second time
						this.parametersPanel.clear();

						this.mainPanel.layout.setActiveItem(1);
						this.btnNext.disable();
						this.btnPrev.enable();
						this.btnFinish.enable();
				
						// create ExecutionInstances and  get parameters 
						var selRole = this.mainPanel.getComponent(0).getSelectedRole();		
							var pars = {
									selectedRole : selRole
									, functId : this.functId
									, type : 'WORKSHEET'						
							}				
						this.createExecutionInstances(pars);
		


			} else{
				//back
				this.mainPanel.layout.setActiveItem(0);
				//this.firstCalculatedFiledPanel.detailsFormPanel.syncSize()();
				this.mainP
				this.btnPrev.disabled = true;
				this.btnNext.disabled = false;
				this.btnFinish.disabled = true;
				
				this.btnPrev.disable();
				this.btnNext.enable();
				this.btnFinish.disable();
			}
		}
	};
	
	this.btnPrev = new Ext.Button({
        id: 'move-prev',
        text: LN('Sbi.tools.massiveExport.MassiveExportWizardWindow.back'),
        handler: navHandler.createDelegate(this, [-1]),
        disabled : true
	});
	
	this.btnNext = new Ext.Button({
        id: 'move-next',
        text: LN('Sbi.tools.massiveExport.MassiveExportWizardWindow.next'),
        handler: navHandler.createDelegate(this, [1])
	});
	
	this.btnFinish = new Ext.Button({
        id: 'finish',
        text: LN('Sbi.tools.massiveExport.MassiveExportWizardWindow.finish'),
        disabled: false,
        scope: this,
        disabled: true,
        handler: function(){
			  this.finishButtonPressed();
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
		});
	

	
//	config = {
//			services : ser	
//		};
	
	this.optionsPanel = new Sbi.tools.massiveExport.MassiveExportOptionsPanel(config);
	this.optionsPanel.on('noDocsEvent', 
			function() {
				this.btnNext.disable();
				this.btnFinish.disable();
			}
		, this
	);
	
	
	var firstPage = this.firstCalculatedFiledPanel;
	
	var ser = new Array();
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'
				   , SBI_EXECUTION_ID: null
				   
	};
	ser['getParametersForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_ANALYTICAL_DRIVER_FROM_DOCS_IN_FOLDER_ACTION'
		, baseParams: params
	});

	config = {
		services : ser	
		, contest : 'massiveExport'
	};
	
	this.parametersPanel = new Sbi.execution.ParametersPanel(config);

	var wizardPages = [this.optionsPanel, this.parametersPanel ];

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
		  items: wizardPages
	});
	this.mainPanel.doLayout();
	
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
      		  		for(p in this.parametersPanel.fields){
      		  			var field = this.parametersPanel.fields[p];
      		  			field.enable();
      		  		}
      		  		this.btnFinish.enable();
      				pars = Ext.apply(pars, this.executionInstances);
      				this.parametersPanel.loadParametersForExecution(pars);
      			} 
      		} else {
      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
      		}
	  	}
	  	else{
	  	//clear preceding store if error happened
	  		for(p in this.parametersPanel.fields){
	  			var field = this.parametersPanel.fields[p];
	  			field.disable();
	  		}
	  		this.btnFinish.disable();
	  	}
      },
        scope: this,
		failure: Sbi.exception.ExceptionHandler.handleFailure      
   });
}	
, finishButtonPressed: function() {
	// get all values
	var state = this.getParametersPanelFormState();
	var jsonState = Sbi.commons.JSON.encode( state );
	// call the action
	var selRole = this.mainPanel.getComponent(0).getSelectedRole();
	var splittingFiltersB = this.mainPanel.getComponent(0).isCycleOnFilterSelected();
	var pars = {
    		selectedRole : selRole
         	, functId : this.functId
         	, type : 'WORKSHEET'
         	, splittingFilter : splittingFiltersB
         	, parameterValues : jsonState
	}	
	
	// Start amssive export
	Ext.Ajax.request({
        url: this.services['startMassiveExportThreadAction'],
       
        params: pars,
       
        success : function(response, options){
        },
        scope: this,
		failure: Sbi.exception.ExceptionHandler.handleFailure      
   });
	
}
/**
 * returns the value selecte dof the parameters in parametersPanel,
 * and for each also the objparameterId (for label rinomination: name  => nameB)
 */
, getParametersPanelFormState: function() {
	var state;
	//to avoid synchronization problem
	
	state = {};
	for(p in this.parametersPanel.fields) {
		var field = this.parametersPanel.fields[p];
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
			//state[field.name + '_objParameterIds']=field.objParameterIds;
	return state;
}

});