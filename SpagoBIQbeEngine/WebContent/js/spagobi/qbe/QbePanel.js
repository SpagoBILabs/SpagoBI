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
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.QbePanel = function(config) {
	
	var c = Ext.apply({
		// set default values here
		displayQueryBuilderPanel: true
		, displayFormBuilderPanel: false
		, displayCrosstabDesignerPanel: true
		, displayCrosstabPreviewPanel: true
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
	
	this.addEvents();
	
	this.queryEditorPanel = null;
	this.queryResultPanel = new Sbi.widgets.DataStorePanel(c);
	this.crosstabDesignerPanel = null;
	
	var items = [];
	
	if (c.displayQueryBuilderPanel) {
		this.queryEditorPanel = new Sbi.qbe.QueryBuilderPanel(c);
		items.push(this.queryEditorPanel);
	}
	
	items.push(this.queryResultPanel);
	
	if (c.displayCrosstabDesignerPanel) {
		this.crosstabDesignerPanel = new Sbi.crosstab.CrosstabDesignerPanel(c.crosstab);
		items.push(this.crosstabDesignerPanel);
		this.crosstabDesignerPanel.centerRegionPanel.on('preview', this.showCrosstabPreview, this);
	}

	if (c.displayCrosstabPreviewPanel) {
		this.crosstabPreviewPanel = new Sbi.crosstab.CrosstabPreviewPanel(c.crosstab);
		items.push(this.crosstabPreviewPanel);
		// if user is not a power user, show crosstab on first tab render event
		if (!c.displayCrosstabDesignerPanel) {
			this.crosstabPreviewPanel.on('render', function() {
				this.showCrosstabPreview(null, c.crosstab.crosstabTemplate);
			}, this);
		}
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
    , crosstabDesignerPanel: null
    , crosstabPreviewPanel: null
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
	
	, getQueriesCatalogue: function () {
		var toReturn = {};
		toReturn.catalogue = {};
		toReturn.catalogue.queries = this.getQueries();
		toReturn.version = Sbi.config.qbeEngineAnalysisStateVersion;
		return toReturn;
	}
    
    // private methods
	, loadFirstQuery: function() {
		Ext.Ajax.request({
	        url: this.services['getFirstQuery'],
	        params: {},
	        success : function(response, opts) {
  	  			try {
  	  				var firstQuery = Ext.util.JSON.decode( response.responseText );
  	  				this.checkPromptableFilters(firstQuery);
  	  			} catch (err) {
  	  				Sbi.exception.ExceptionHandler.handleFailure();
  	  			}
	        },
	        scope: this,
			failure: Sbi.exception.ExceptionHandler.handleFailure      
		});
	}
	
	// check if there are some promptable filters before starting query execution
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
  	
  	, showCrosstabPreview: function(crosstabDefinitionPanel, crosstabDefinition) {
  		this.tabs.activate(this.crosstabPreviewPanel);
  		this.crosstabPreviewPanel.load(crosstabDefinition);
  	}
  	
  	, saveQuery: function(meta) {
    	this.save(meta, function(response, options) {
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
  	
	, save: function(meta, callback, scope) {
		
		var crosstabDefinition =  this.crosstabDesignerPanel.getCrosstabDefinition();
		var crosstabCalculatedFields =  this.crosstabPreviewPanel.getCalculatedFields();
		
		crosstabDefinition.calculatedFields = crosstabCalculatedFields;
		
		var params = Ext.apply({
			crosstabDefinition: Ext.util.JSON.encode(crosstabDefinition)
		}, meta);
		
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
		
		var crosstabData = this.crosstabPreviewPanel.serializeCrossTab();
		var crosstabDataEncoded = Ext.util.JSON.encode(crosstabData);
		return crosstabDataEncoded;
		
	}
  	
	,
	getParameters: function () {
		this.queryEditorPanel.getParameters();
	}
	
	,
	setParameters: function (parameters) {
		this.queryEditorPanel.setParameters(parameters);
	}
	
	,
	setQueriesCatalogue: function (queriesCatalogue) {
		this.queryEditorPanel.setQueriesCatalogue(queriesCatalogue);
	}
	
});