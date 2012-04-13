/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
 * - Andrea Gioia (andrea.gioia@eng.it)
 */

Ext.ns("Sbi.qbe");

Sbi.qbe.QbePanel = function(config) {

	var c = Ext.apply({
		// set default values here
		displayQueryBuilderPanel: true
		, displayFormBuilderPanel: false
		, displayWorksheetDesignerPanel: true
		, displayWorksheetPreviewPanel: true
	}, config || {});

	this.services = new Array();
	var params = {};
	this.services['getFirstQuery'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_FIRST_QUERY_ACTION'
			, baseParams: params
	});
	this.services['saveAnalysisState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SAVE_ANALYSIS_STATE_ACTION'
			, baseParams: params
	});
	this.services['getWorkSheetState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_WORKSHEET_PREVIEW_ACTION'
			, baseParams: params
	});
	this.services['setWorkSheetState'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SET_WORKSHEET_DEFINITION_ACTION'
			, baseParams: params
	});
	this.services['executeWorksheetStartAction'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'START_WORKSHEET_FROM_QBE_ACTION'
			, baseParams: params
	});

	this.addEvents();

	if (c.initialQueriesCatalogue) {
		this.setInitialQueriesCatalogue(c.initialQueriesCatalogue);
	}

	this.queryEditorPanel = null;
	this.queryResultPanel = new Sbi.widgets.DataStorePanel(Ext.apply(c, {
		id : 'DataStorePanel'
	}));
	this.worksheetDesignerPanel = null;

	var items = [];

	if (c.displayQueryBuilderPanel) {
		this.queryEditorPanel = new Sbi.qbe.QueryBuilderPanel(Ext.apply(c, {
			id : 'QueryBuilderPanel'
		}));
		items.push(this.queryEditorPanel);
	}

	items.push(this.queryResultPanel);

	if (c.displayWorksheetDesignerPanel) {

		var worksheetDesignerConfig = c.worksheet || {};
		this.worksheetDesignerPanel = new Sbi.worksheet.designer.WorksheetDesignerPanel(Ext.apply(worksheetDesignerConfig, {
			id : 'WorksheetDesignerPanel'
		}));

		var worksheetEngineInitialized = worksheetDesignerConfig.engineInitialized;
		if (worksheetEngineInitialized === undefined || worksheetEngineInitialized == false) {
			// if the worksheet engine is not initialized, we need to initialize it on first activation of worksheet designer tab
			this.worksheetDesignerPanel.on('activate', function(){
				if(!this.notFirstTimeDesignPanelOpened){
					this.notFirstTimeDesignPanelOpened = true;
					Ext.Ajax.request({
						url: this.services['executeWorksheetStartAction'],
						params: {},
						scope: this,
						failure: Sbi.exception.ExceptionHandler.handleFailure
					});
				}
			}, this);
		}
		
		items.push(this.worksheetDesignerPanel);
	}
	


	if (c.displayWorksheetPreviewPanel) {
		this.worksheetPreviewPanel = new Sbi.worksheet.runtime.WorkSheetPreviewPage({
			id : 'WorkSheetPreviewPage',
			closable: false
		});

		this.worksheetPreviewPanel.on('activate', function() {
			
			this.worksheetDesignerPanel.validate(
					function(){
						this.setWorksheetState(this.refreshWorksheetPreview, Sbi.exception.ExceptionHandler.handleFailure, this);
					}
					, this.worksheetDesignerPanel.showValidationErrors
					, this);
			
		}, this);

		items.push(this.worksheetPreviewPanel);
	}

	if (c.displayFormBuilderPanel && c.formbuilder !== undefined && c.formbuilder.template !== undefined) {
		this.formBuilderPage = new Sbi.formbuilder.FormPanel({template: c.formbuilder.template});
		items.push(this.formBuilderPage);
	}

	if (!c.displayQueryBuilderPanel) {
		// if user is a read-only user, do not instantiate and show the QueryBuilderPanel
		// and execute first query on catalog
		this.loadFirstQuery();
	}

	this.tabs = new Ext.TabPanel({
		border: false,
		activeTab: config.isFromCross?1:0,
				items: items
	});

	if (this.queryEditorPanel != null) {
		this.queryEditorPanel.on('execute', function(editorPanel, query){
			this.checkPromptableFilters(query);
		}, this);
		this.queryEditorPanel.on('save', function(meta){
			this.saveQuery(meta);
		}, this);
		this.tabs.on('tabchange', function () {
			var anActiveTab = this.tabs.getActiveTab();
						
			/*
			 * work-around: forcing the layout recalculation on west/center/est region panels on tab change
			 * TODO: try to remove it when upgrading Ext library
			 */
			if (anActiveTab.centerRegionPanel !== undefined) {
				anActiveTab.centerRegionPanel.doLayout();
			}
			if (anActiveTab.westRegionPanel !== undefined) {
				anActiveTab.westRegionPanel.doLayout();
			}
			if (anActiveTab.eastRegionPanel !== undefined) {
				anActiveTab.eastRegionPanel.doLayout();
			}

			if(config.isFromCross) {
				if(anActiveTab.selectGridPanel != null && anActiveTab.selectGridPanel.dropTarget === null) {
					anActiveTab.selectGridPanel.dropTarget = new Sbi.qbe.SelectGridDropTarget(anActiveTab.selectGridPanel);
				}

				if(anActiveTab.filterGridPanel != null && anActiveTab.filterGridPanel.dropTarget === null) {
					anActiveTab.filterGridPanel.dropTarget = new Sbi.qbe.FilterGridDropTarget(anActiveTab.filterGridPanel);
				}

				if(anActiveTab.havingGridPanel != null && anActiveTab.havingGridPanel.dropTarget === null) {
					anActiveTab.havingGridPanel.dropTarget = new Sbi.qbe.HavingGridDropTarget(anActiveTab.havingGridPanel);
				}

				if(anActiveTab.filtersTemplatePanel != null && anActiveTab.filtersTemplatePanel.staticOpenFiltersEditorPanel != null
						&& anActiveTab.filtersTemplatePanel.staticOpenFiltersEditorPanel.dropTarget === null) {
					anActiveTab.filtersTemplatePanel.staticOpenFiltersEditorPanel.dropTarget = 
						new Sbi.formbuilder.StaticOpenFiltersEditorPanelDropTarget(anActiveTab.filtersTemplatePanel.staticOpenFiltersEditorPanel);
				}
			}
		}, this);
	}

	c = Ext.apply(c, {
		layout: 'fit',
		autoScroll: true, 
		margins:'0 4 4 0',
		items: [this.tabs] 
	});

	// constructor
	Sbi.qbe.QbePanel.superclass.constructor.call(this, c);

	//alert('isFromCross: ' + config.isFromCross);
	if(config.isFromCross) {
		this.loadFirstQuery();
	}
};

Ext.extend(Sbi.qbe.QbePanel, Ext.Panel, {

	services: null
	, queryResultPanel: null
	, queryEditorPanel: null
	, worksheetDesignerPanel: null
	, worksheetPreviewPanel: null
	, initialQueriesCatalogue: null // used as a queries repository variable when the queryEditorPanel is not displayed
	, tabs: null
	, query: null


	// public methods

	, setQuery: function(q) {
	query = q;
	this.queryEditorPanel.setQuery(q);
}

, getSQLQuery: function(callbackFn, scope) {
	this.queryEditorPanel.getSQLQuery(callbackFn, scope);
}

, getQueries: function() {
	return this.queryEditorPanel.getQueries();
}

//private methods
, loadFirstQuery: function() {
	Ext.Ajax.request({
		url: this.services['getFirstQuery'],
		params: {},
		success : function(response, opts) {
			try {
				var firstQuery = Ext.util.JSON.decode( response.responseText );
				if (!Sbi.user.isPowerUser && firstQuery.fields.length == 0) {
					// user is NOT a power user and the first query is empty: most likely the user is executing 
					// a Qbe document, not a saved query
		        	Ext.MessageBox.show({
		           		title: LN('sbi.qbe.qbepanel.emptyquerytitle')
		           		, msg: LN('sbi.qbe.qbepanel.emptyquerymessage')
		           		, buttons: Ext.MessageBox.OK
		           		, icon: Ext.MessageBox.WARNING
		           		, modal: false
		       		});
		        	return;
				}
				this.checkPromptableFilters(firstQuery);
			} catch (err) {
				Sbi.exception.ExceptionHandler.handleFailure();
			}
		},
		scope: this,
		failure: Sbi.exception.ExceptionHandler.handleFailure      
	});
}

//check if there are some promptable filters before starting query execution
, checkPromptableFilters: function(query) {
	var freeFilters = this.getPromptableFilters(query);
	if (freeFilters.length > 0) {
		var freeConditionsWindow = new Sbi.qbe.FreeConditionsWindow({
			freeFilters: freeFilters
		});
		freeConditionsWindow.on('apply', function (formState) {
			// make last values persistent on filter grid panel
			if (this.queryEditorPanel != null) {
				this.queryEditorPanel.filterGridPanel.setPromptableFiltersLastValues(formState);
				this.queryEditorPanel.havingGridPanel.setPromptableFiltersLastValues(formState);
			}
			this.executeQuery(query, formState);
		}, this);
		freeConditionsWindow.on('savedefaults', function (formState) {
			// make default values persistent on filter grid panel
			if (this.queryEditorPanel != null) {
				this.queryEditorPanel.filterGridPanel.setPromptableFiltersDefaultValues(formState);
				this.queryEditorPanel.havingGridPanel.setPromptableFiltersDefaultValues(formState);
			}
		}, this);
		freeConditionsWindow.show();
	} else {
		this.executeQuery(query);
	}
}

, executeQuery: function(query, promptableFilters) {
	this.tabs.activate(this.queryResultPanel);
	this.queryResultPanel.execQuery(query, promptableFilters);
}

, getPromptableFilters : function(query) {
	var filters = [];
	if (query.filters != null && query.filters.length > 0) {
		for(i = 0; i < query.filters.length; i++) {
			var filter =  query.filters[i];
			if (filter.promptable) {
				filters.push(filter);
			}
		}
	}
	if (query.havings != null && query.havings.length > 0) {
		for(i = 0; i < query.havings.length; i++) {
			var filter = query.havings[i];
			if (filter.promptable) {
				filters.push(filter);
			}
		}
	}
	return filters;
}

, saveQuery: function(meta) {
	this.saveAnalysisState(meta, function(response, options) {
		// for old gui
		try {
			var content = Ext.util.JSON.decode( response.responseText );
			content.text = content.text || "";
			parent.loadSubObject(window.name, content.text);
		} catch (ex) {}

		// for new gui
		// build a JSON object containing message and ID of the saved  object
		try {
			// get the id of the subobject just inserted, decode string, need to call metadata window
			var responseJSON = Ext.util.JSON.decode( response.responseText )
			var id = responseJSON.text;
			var msgToSend = 'Sub Object Saved!!';

			//sendMessage({'id': id, 'meta' : meta.metadata, 'msg': msgToSend},'subobjectsaved');
			//alert('id '+id+' message '+msgToSend);
			sendMessage({'id': id, 'msg': msgToSend},'subobjectsaved');
		} catch (ex) {}
		// show only if not showing metadata windows
		/*if( meta.metadata == false ){
			Ext.Msg.show({
				   title:LN('sbi.qbe.queryeditor.querysaved'),
				   msg: LN('sbi.qbe.queryeditor.querysavedsucc'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.INFO
			});
		}*/
	}, this);
}

, saveAnalysisState: function(meta, callback, scope) {

	var params = Ext.apply({}, meta);

	var doSave = function() {
		Ext.Ajax.request({
			url: this.services['saveAnalysisState'],
			success: callback,
			failure: Sbi.exception.ExceptionHandler.handleFailure,	
			scope: scope,
			params: params
		});  
	};

	this.queryEditorPanel.queryCataloguePanel.commit(function() {
		if(Sbi.config.queryValidation.isEnabled) {
			this.queryEditorPanel.queryCataloguePanel.validate(doSave, this);
		} else {
			doSave();
		}

	}, this);		
}

/*
 * This method is invoked by Sbi.execution.DocumentExecutionPage on SpagoBI core!!!
 * See SpagoBI/js/src/ext/sbi/execution/DocumentExecutionPage.js, retrieveQbeCrosstabData method
 */
, getCrosstabDataEncoded: function () {

	var crosstabData = this.worksheetPreviewPanel.serializeCrossTab(); // TODO manage crosstab export
	var crosstabDataEncoded = Ext.util.JSON.encode(crosstabData);
	return crosstabDataEncoded;

}

,
getParameters: function () {
	return this.queryEditorPanel.getParameters();
}

,
setParameters: function (parameters) {
	this.queryEditorPanel.setParameters(parameters);
}

,
getInitialQueriesCatalogue: function () {
	return this.initialQueriesCatalogue;
}

,
setInitialQueriesCatalogue: function (initialQueriesCatalogue) {
	this.initialQueriesCatalogue = initialQueriesCatalogue;
}

, 
getQueriesCatalogue: function () {
	if (this.queryEditorPanel == null) {
		// query designer panel not displayed, returns the initial catalogue
		return this.getInitialQueriesCatalogue();
	} else {
		// query designer panel displayed
		var toReturn = {};
		toReturn.catalogue = {};
		toReturn.catalogue.queries = this.getQueries();
		toReturn.version = Sbi.config.queryVersion;
		return toReturn;
	}
}

,
setQueriesCatalogue: function (queriesCatalogue) {
	if (this.queryEditorPanel != null) {
		this.queryEditorPanel.setQueriesCatalogue(queriesCatalogue);
	} else {
		alert('Query builder panel not instantiated, you cannot invoke setQueriesCatalogue method');
	}
}

,
setWorksheetState : function (successFn, failureFn, scope) {
	
	var worksheetDefinition = this.worksheetDesignerPanel.getWorksheetDefinition();
	this.addSheetAdditionalData(worksheetDefinition,this.worksheetDesignerPanel.worksheetTemplate);
	var params = {
			'worksheetdefinition':  Ext.encode(worksheetDefinition)
	};
	Ext.Ajax.request({
		url: this.services['setWorkSheetState'],
		success: successFn,
		failure: failureFn,
		scope: scope,
		params: params
	});   
}

,
refreshWorksheetPreview : function () {
	this.worksheetPreviewPanel.getFrame().setSrc(this.services['getWorkSheetState']);
}

, validate : function () {
	return 	this.worksheetDesignerPanel.validate(this.getWorksheetTemplateAsString, this.worksheetDesignerPanel.showValidationErrors, this );	
}

, getWorksheetTemplateAsString : function () {

	// check validation before retrieving template
//	var errorArray = this.worksheetDesignerPanel.validate();
	//	if(errorArray && errorArray.length>0){
	//		this.worksheetDesignerPanel.showValidationErrors(errorArray);		
	//		return;
	//	}

	var queriesCatalogue = this.getQueriesCatalogue();
	var worksheetDefinition = null;
	if (this.worksheetDesignerPanel.rendered === true) {
		// get the current worksheet designer state
		worksheetDefinition = this.worksheetDesignerPanel.getWorksheetDefinition();
	} else {
		// get the initial worksheet template
		worksheetDefinition = this.worksheetDesignerPanel.worksheetTemplate;
	}
	this.addAdditionalData(worksheetDefinition);
	var template = Ext.util.JSON.encode({
		'OBJECT_WK_DEFINITION' : worksheetDefinition,
		'OBJECT_QUERY' : queriesCatalogue
	});
	return template;
}

, addAdditionalData : function(sheetTemplate){

	if(this.worksheetPreviewPanel.rendered === true){
		var additionalData = this.worksheetPreviewPanel.getFrame().getWindow().workSheetPanel.getAdditionalData();
		if(additionalData!=undefined && additionalData!=null){
			var sheets = sheetTemplate.sheets;
			for(var i=0; i<sheets.length; i++){
				if(additionalData[i]!=undefined && additionalData[i]!=null && additionalData[i].data!=undefined && additionalData[i].data!=null && !Ext.isEmpty(additionalData[i].data) && sheets[i].content.crosstabDefinition!=undefined && sheets[i].content.crosstabDefinition!=null){
					if(additionalData[i].data!=undefined && additionalData[i].data!=null && !Ext.isEmpty(additionalData[i].data) && sheets[i].content.crosstabDefinition!=undefined && sheets[i].content.crosstabDefinition!=null){
						sheets[i].content.crosstabDefinition.calculatedFields =additionalData[i].data.crosstabDefinition.calculatedFields;
						sheets[i].content.crosstabDefinition.additionalData =additionalData[i].data.crosstabDefinition.additionalData;
					}
				}
			}	
		}
	}else{
		this.addSheetAdditionalData(sheetTemplate,this.worksheetDesignerPanel.worksheetTemplate);
	}
}


, addSheetAdditionalData: function(designerTemplate, documentTemplate){
	if(designerTemplate!=undefined && designerTemplate!=null && documentTemplate!=undefined && documentTemplate!=null){
		var designerTemplateSheets = designerTemplate.sheets;
		var documentTemplateSheets = documentTemplate.sheets;
		if(documentTemplateSheets!=undefined && documentTemplateSheets!=null && designerTemplateSheets!=undefined && designerTemplateSheets!=null){
			for(var i=0; i<designerTemplateSheets.length; i++){
				for(var y=0; y<documentTemplateSheets.length; y++){
					if(designerTemplateSheets[i].name == documentTemplateSheets[y].name){
						if(designerTemplateSheets[i].content.crosstabDefinition!=undefined && designerTemplateSheets[i].content.crosstabDefinition!=null && documentTemplateSheets[y].content.crosstabDefinition!=undefined && documentTemplateSheets[y].content.crosstabDefinition!=null){
							designerTemplateSheets[i].content.crosstabDefinition.calculatedFields =documentTemplateSheets[y].content.crosstabDefinition.calculatedFields;
							designerTemplateSheets[i].content.crosstabDefinition.additionalData =documentTemplateSheets[y].content.crosstabDefinition.additionalData;
						}
						break;
					}
				}	
			}	
		}
	}
}

, exportContent: function(mimeType){

	if(this.isWorksheetPageActive()){
		if( this.worksheetPreviewPanel!=undefined && this.worksheetPreviewPanel!=null && 
				this.worksheetPreviewPanel.getFrame()!=undefined && this.worksheetPreviewPanel.getFrame()!=null &&
				this.worksheetPreviewPanel.getFrame().getWindow()!=undefined && this.worksheetPreviewPanel.getFrame().getWindow()!=null &&
				this.worksheetPreviewPanel.getFrame().getWindow().workSheetPanel!=undefined && this.worksheetPreviewPanel.getFrame().getWindow().workSheetPanel!=null){
					this.worksheetPreviewPanel.getFrame().getWindow().workSheetPanel.un('contentexported', this.sendMessageToParentFrame,this);	
					this.worksheetPreviewPanel.getFrame().getWindow().workSheetPanel.on('contentexported', this.sendMessageToParentFrame,this);	
					this.worksheetPreviewPanel.getFrame().getWindow().workSheetPanel.exportContent(mimeType, true);
					
			}
	}else{
		sendMessage({}, 'worksheetexporttaberror');
	}
}

, sendMessageToParentFrame: function(){
	sendMessage({}, 'contentexported');
}

, isWorksheetPageActive: function(){
	return this.tabs.getActiveTab().id=='WorkSheetPreviewPage';
}

});