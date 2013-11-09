/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */
 
  
Ext.define('Sbi.selfservice.SelfServiceExecutionIFrame', {
	extend: 'Sbi.widgets.EditorIFramePanelContainer'

		
	, modelName: null
	, datasetLabel: null

	
	, init: function(config){
		this.callParent(arguments);
		if( Sbi.settings && Sbi.settings 
			&& Sbi.settings.mydata && Sbi.settings.mydata.toolbar 
			&& Sbi.settings.mydata && Sbi.settings.mydata.toolbar.hide === true) {
			Sbi.debug("[SelfServiceExecutionIFrame.init]: Toolbar not visible");
		} else {
			this.initToolbar(config);
		}
		
	}


	, initToolbar : function (config) {

		this.tbar  = Ext.create('Ext.toolbar.Toolbar');
		this.tbar.add('->');
		// passed by JSP userDocumentBrowserCreateDoc.jsp 
		if(config.hideExtraSaveButton != undefined && config.hideExtraSaveButton == true){
				// if in creation detail page in user browser do not use this save button
		} else{
			this.tbar.add({
				iconCls : 'icon-saveas' 
				, tooltip: LN('sbi.execution.executionpage.toolbar.saveas')
				, scope : this
			    , handler : this.saveHandler
			});
		}
		
	}
	
	, saveHandler : function() {
		var theWindow = this.iframe.getWin();
		Sbi.debug('[SelfServiceExecutionIFrame.saveWorksheet]: got window');
		
		if (theWindow.qbe != null) {
			this.saveQbe();
		} else if (theWindow.workSheetPanel != null) {
			var template = theWindow.workSheetPanel.validate();
			this.saveWorksheet(template);
		} else if (theWindow.geoReportPanel != null){
			var template = theWindow.geoReportPanel.validate();
			this.saveGeoReport(template);
		} else {
			alert("Impossible to save document of type [unknown]");
		}
		
	}
	
	, saveGeoReport : function(template) {
		
    	if (template == null) {
    		alert("Impossible to get template");
    		return;
    	}
    	
    	Sbi.debug('[SelfServiceExecutionIFrame.saveGeoReport]: ' + template);
    	
		var documentWindowsParams = {
				'OBJECT_TYPE': 'MAP',
				'OBJECT_TEMPLATE': template,
				'model_name': this.modelName,
				'typeid': 'GEOREPORT' 
		};

		if(this.datasetLabel!=null){
			documentWindowsParams.dataset_label= this.datasetLabel;
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE_FROM_DATASET';
		} else if(this.modelName!=null){
			documentWindowsParams.model_name= this.modelName;
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE_FROM_MODEL';
		}
		
		this.win_saveDoc = Ext.create("Sbi.execution.SaveDocumentWindowExt4", documentWindowsParams);
		this.win_saveDoc.show();
    
    }
	
	,
	saveQbe : function () {
		//try {
			// May be we have to save a new dataset or a worksheet document
			var qbeWindow = this.iframe.getWin();
			var qbePanel = qbeWindow.qbe;
			var anActiveTab = qbePanel.tabs.getActiveTab();
			var activeTabId = anActiveTab.getId();
			var isBuildingWorksheet = (activeTabId === 'WorksheetPanel');
			if (isBuildingWorksheet) {
				// save worksheet as document
				var template = qbePanel.validate();
				this.saveWorksheet(template);
			} else {
				// save query as new dataset
				
				this.openQbeSaveDataSetWizard();
				
//				var queryDefinition = this.getQbeQueryDefinition();
//				var saveDatasetWindow = Ext.create("Sbi.selfservice.SaveDatasetWindow", { queryDefinition : queryDefinition } );
//				saveDatasetWindow.on('save', function(theWindow, formState) { theWindow.close(); }, this);
//				saveDatasetWindow.show();
				
			}
		//} catch (err) {
		//	alert('Sorry, cannot perform operation.');
		//	throw err;
		//}
	}
	
	, saveWorksheet : function(template) {
	
    	if (template == null) {
    		alert("Impossible to get template");
    		return;
    	}
    	
    	var templateJSON = Ext.JSON.decode(template);
		var wkDefinition = templateJSON.OBJECT_WK_DEFINITION;
		var worksheetQuery = templateJSON.OBJECT_QUERY;
		var documentWindowsParams = {
				'OBJECT_TYPE': 'WORKSHEET',
				'template': wkDefinition,
				'OBJECT_WK_DEFINITION': wkDefinition,
				'OBJECT_QUERY': worksheetQuery,
				'model_name': this.modelName,
				'typeid': 'WORKSHEET' 
		};

		if(this.datasetLabel!=null){
			documentWindowsParams.dataset_label= this.datasetLabel;
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE_FROM_DATASET';
		}else if(this.modelName!=null){
			documentWindowsParams.model_name= this.modelName;
			documentWindowsParams.MESSAGE_DET= 'DOC_SAVE_FROM_MODEL';
		}
		
		this.win_saveDoc = Ext.create("Sbi.execution.SaveDocumentWindowExt4",documentWindowsParams);
		this.win_saveDoc.show();
    
    }
	
//	,
//	getQbeQueryDefinition : function () {
//		Sbi.debug('[SelfServiceExecutionIFrame.getQbeQueryDefinition]: IN');
//		var qbeWindow = this.iframe.getWin();
//		Sbi.debug('[SelfServiceExecutionIFrame.getQbeQueryDefinition]: got window');
//		var qbePanel = qbeWindow.qbe;
//		Sbi.debug('[SelfServiceExecutionIFrame.getQbeQueryDefinition]: got qbe panel object');
//		var queries = qbePanel.getQueriesCatalogue();
//		Sbi.debug('[SelfServiceExecutionIFrame.getQbeQueryDefinition]: got queries');
//		var toReturn = {};
//		toReturn.queries = queries;
//		toReturn.sourceDatasetLabel = this.datasetLabel;
//		return toReturn;
//	}
	
	,
	openQbeSaveDataSetWizard : function () {
		Sbi.debug('[SelfServiceExecutionIFrame.getQbeQueryDefinition]: IN');
		var qbeWindow = this.iframe.getWin();
		Sbi.debug('[SelfServiceExecutionIFrame.getQbeQueryDefinition]: got window');
		var qbePanel = qbeWindow.qbe;
		Sbi.debug('[SelfServiceExecutionIFrame.getQbeQueryDefinition]: got qbe panel object');
		qbePanel.openSaveDataSetWizard();
	}	
	
});