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
		this.crosstabPreviewPanel = new Sbi.crosstab.CrosstabPreviewPanel();
		items.push(this.crosstabPreviewPanel);
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
		var params = Ext.apply({
			crosstabDefinition: Ext.util.JSON.encode(this.crosstabDesignerPanel.getCrosstabDefinition())
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
	
	, getCrosstabDataEncoded: function () {
		return Ext.util.JSON.encode({data:[["2.0", "5.52", "27.0", "74.52", "20.0", "55.2", "25.0", "69.0", "30.0", "82.8", "22.0", "60.72", "22.0", "60.72", "4.0", "11.04", "23.0", "63.48", "29.0", "80.04", "19.0", "52.44", "NA", "NA", "NA", "NA"], ["7.0", "18.22", "24.0", "76.3", "48.0", "113.46", "29.0", "80.78", "37.0", "88.36", "34.0", "79.08", "38.0", "79.78", "NA", "NA", "33.0", "87.66", "27.0", "69.1", "35.0", "122.0", "6.0", "18.56", "14.0", "52.92"], ["6.0", "6.76", "106.0", "104.58", "64.0", "67.9", "64.0", "70.6", "59.0", "57.7", "65.0", "76.72", "62.0", "65.2", "1.0", "1.2", "63.0", "70.44", "79.0", "80.72", "121.0", "134.36", "12.0", "10.98", "36.0", "31.62"], ["10.0", "21.4", "63.0", "126.09", "44.0", "92.96", "61.0", "129.13", "74.0", "154.79", "73.0", "149.71", "59.0", "113.63", "8.0", "17.57", "46.0", "89.35", "57.0", "132.54", "100.0", "217.66", "7.0", "17.83", "23.0", "53.03"], ["4.0", "3.04", "58.0", "66.14", "68.0", "64.24", "80.0", "77.93", "65.0", "82.02", "138.0", "155.71", "87.0", "127.93", "NA", "NA", "51.0", "49.16", "80.0", "94.02", "109.0", "166.47", "NA", "NA", "23.0", "36.45"], ["7.0", "20.79", "73.0", "165.2", "36.0", "86.82", "54.0", "126.53", "55.0", "147.82", "90.0", "189.83", "85.0", "176.76", "5.0", "9.93", "113.0", "316.43", "40.0", "96.78", "93.0", "223.58", "4.0", "3.67", "22.0", "57.51"], ["31.0", "63.85", "224.0", "464.09", "316.0", "674.82", "365.0", "694.2", "344.0", "666.53", "486.0", "963.52", "374.0", "774.39", "32.0", "71.65", "352.0", "677.79", "407.0", "746.63", "467.0", "952.74", "35.0", "65.48", "166.0", "314.92"]], columns:{node_key:"rootC", node_childs:[{node_key:"USA", node_childs:[{node_key:"Bellingham", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Beverly Hills", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Bremerton", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Los Angeles", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Portland", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Salem", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"San Diego", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"San Francisco", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Seattle", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Spokane", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Tacoma", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Walla Walla", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}, {node_key:"Yakima", node_childs:[{node_key:"Unit Sales"}, {node_key:"Store Sales"}]}]}]}, config:{measureson:"columns"}, rows:{node_key:"rootR", node_childs:[{node_key:"ADJ"}, {node_key:"James Bay"}, {node_key:"Jardon"}, {node_key:"Jeffers"}, {node_key:"Johnson"}, {node_key:"Jumbo"}, {node_key:"Just Right"}]}});
		//return Ext.util.JSON.encode([["2.0", "5.52"], ["27.0", "74.52"]]);
	}
  	
});