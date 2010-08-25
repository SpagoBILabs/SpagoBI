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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageModelInstancesViewPort = function(config) { 
	var paramsResList = {MESSAGE_DET: "MODELINST_RESOURCE_LIST"};
	

	var conf = config;
	this.resListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
		, baseParams: paramsResList
	});	
    
	this.resourcesStore = new Ext.data.JsonStore({
    	autoLoad: false    	  
    	, root: 'rows'
		, url: this.resListService	
		, fields: ['resourceId', 'resourceName', 'resourceCode', 'resourceType']

	});
	//DRAW center element
	this.manageModelInstances = new Sbi.kpi.ManageModelInstances(conf, this);

	//DRAW west element
    this.modelInstancesGrid = new Sbi.kpi.ManageModelInstancesGrid(conf, this);
   //DRAW east element
    this.manageModelsTree = new Sbi.kpi.ManageModelsTree(conf, this.modelInstancesGrid);
    
    this.resourcesTab = new Ext.Panel({
        title: LN('sbi.modelinstances.resourcesTab')
	        , id : 'resourcesTab'
	        , layout: 'fit'
	        , autoScroll: true
	        , items: []
	        , itemId: 'resourcesTab'
	        , scope: this
	});
    this.dispalyResourcesGridPanel();
    this.initPanels();
    
    
	var c = Ext.apply({}, config || {}, this.viewport);
	
	Sbi.kpi.ManageModelInstancesViewPort.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.kpi.ManageModelInstancesViewPort, Ext.Viewport, {
	manageModelInstances: null,
	modelInstancesGrid: null,
	manageModelsTree: null,
	resourcesTab : null,
	centerTabbedPanel: null,
	viewport: null,
	lastRecSelected: null

	,initPanels : function() {
		this.modelInstancesGrid.addListener('rowclick', this.sendSelectedItem, this);	
		
		this.manageModelsTree.addListener('render', this.configureDD, this);

		this.modelInstancesTreeTab = new Ext.Panel({
	        title: LN('sbi.modelinstances.treeTitle')
		        , id : 'modeinstTab'
		        , layout: 'fit'
		        , autoScroll: true
		        , items: [this.manageModelInstances]
		        , itemId: 'modInstTab'
		        , scope: this
		});
		this.tabs = new Ext.TabPanel({
	           enableTabScroll : true
	           , activeTab : 0
	           , autoScroll : true
	           //NB: Important trick: to render all content tabs on page load
	           , deferredRender: false
	           , width: 450
	           , height: 450
	           , itemId: 'tabs'
			   , items: [this.modelInstancesTreeTab, this.resourcesTab]

			});
		this.viewport = {
				layout: 'border'
				, id: 'model-viewport'
				, height:560
				, autoScroll: true
				, items: [
			         {
			           region: 'west',
			           width: 275,
			           height:560,
			           collapseMode:'mini',
			           autoScroll: true,
			           split: true,
			           layout: 'fit',
			           items:[this.modelInstancesGrid]
			          },
				    {
				       region: 'center',
				       width: 300,
				       height:560,
				       split: true,
				       collapseMode:'mini',
				       autoScroll: true,
				       layout: 'fit',
				       items: [this.tabs]
				    }, {
				        region: 'east',
				        split: true,
				        width: 400,
				        height:560,
				        collapsed:false,
				        collapseMode:'mini',
				        autoScroll: true,
				        items:[this.manageModelsTree]
				    }
				]
				

			};
		
	}

	,sendSelectedItem: function(grid, rowIndex, e){
		var rec = grid.getSelectionModel().getSelected();
	
		//if unsaved changes
		if(this.manageModelInstances.nodesToSave.length > 0){
			//if there are modification on current selection
			Ext.MessageBox.confirm(
					LN('sbi.generic.pleaseConfirm'),
					LN('sbi.generic.confirmChangeNode'),            
		            function(btn, text) {
	
		                if (btn=='yes') {
	
		                	this.manageModelInstances.cleanAllUnsavedNodes();	        			
		        			this.displayTree(rec);
			        		if(rec != this.lastRecSelected){
			        			this.lastRecSelected = rec;
			        		}
	
		                }else{
		                	grid.getSelectionModel().selectRecords([this.lastRecSelected]);
		                	
		                }
	
		            },
		            this
				);
		}else{
			this.displayTree(rec);
			this.displaySourceModelDetail(rec);
			this.dispalyResourcesGridPanel(rec);
			if(rec != this.lastRecSelected){
				this.lastRecSelected = rec;
			}
		}
	
	}
	, displayTree: function(rec){
		this.manageModelInstances.rootNodeText = rec.get('name');
		this.manageModelInstances.rootNodeId = rec.get('modelInstId');
		
		//main instances tree - center
		var newroot = this.manageModelInstances.createRootNodeByRec(rec);
		this.manageModelInstances.mainTree.setRootNode(newroot);
		
		this.manageModelInstances.mainTree.getSelectionModel().select(newroot);
		this.manageModelInstances.mainTree.doLayout();

		//model tree - left modelId
		this.manageModelsTree.rootNodeText = rec.get('modelText');
		this.manageModelsTree.rootNodeId = rec.get('modelId');

		var newroot2 = this.manageModelsTree.createRootNodeByRec(rec);
		this.manageModelsTree.modelsTree.setRootNode(newroot2);
		
		this.manageModelsTree.modelsTree.getSelectionModel().select(newroot2);
		this.manageModelsTree.modelsTree.doLayout();

	}
	, displaySourceModelDetail: function(rec) {
		this.manageModelInstances.srcModelName.setValue(rec.get('modelName'));
		this.manageModelInstances.srcModelCode.setValue(rec.get('modelCode'));
		this.manageModelInstances.srcModelDescr.setValue(rec.get('modelDescr'));
		this.manageModelInstances.srcModelType.setValue(rec.get('modelType'));
		this.manageModelInstances.srcModelTypeDescr.setValue(rec.get('modelTypeDescr'));
	}
	, dispalyResourcesGridPanel : function(rec) {
		if(rec !== undefined && rec != null){
			var params = {
	        	modelInstId : rec.data.modelInstId
	        }
	        
	        Ext.Ajax.request({
	            url: this.resListService,
	            params: params,
	            method: 'GET',
	            success: function(response, options) {
					if (response !== undefined) {			
			      		if(response.responseText !== undefined) {

			      			var content = Ext.util.JSON.decode( response.responseText );
			      			this.resourcesStore.load();
			      		}
					}
	            }
	            ,scope: this
	        });	
		}
    	this.smResources = new Ext.grid.CheckboxSelectionModel( {header: ' ',singleSelect: false, scope:this, dataIndex: 'resourceId'} );
		
        this.cmResources = new Ext.grid.ColumnModel([
	         {header: LN('sbi.generic.name'), width: 45, sortable: true, dataIndex: 'resourceName'},
	         {header: LN('sbi.generic.code'), width: 65, sortable: true, dataIndex: 'resourceCode'}
	         ,{header: LN('sbi.generic.type'), width: 65, sortable: true, dataIndex: 'resourceType'}
	         ,this.smResources
	    ]);

		this.resourcesGrid = new Ext.grid.GridPanel({
			store: this.resourcesStore 
			, id: 'resources-grid-checks'
   	     	, cm: this.cmResources
   	     	, sm: this.smResources
   	     	, frame: false
   	     	, border:false  
   	     	, collapsible:false
   	     	, loadMask: true
   	     	, viewConfig: {
   	        	forceFit:true
   	        	, enableRowBody:true
   	        	, showPreview:true
   	     	}
			, scope: this
		});
		this.resourcesGrid.superclass.constructor.call(this);
		
		Ext.getCmp("resources-grid-checks").on('recToSelect', function(id, index){		
			Ext.getCmp("resources-grid-checks").selModel.selectRow(index,true);
		});
		this.resourcesTab.add(this.resourcesGrid);
		this.resourcesGrid.doLayout();
	}
	, configureDD: function() {
		  var nodeTreePanelDropTarget = new Ext.tree.TreeDropZone(Ext.getCmp('model-maintree'), {
		    ddGroup  : 'tree2tree',
		    dropAllowed : true,
		    overClass: 'over',
		    copy: true,
		    scope: this,
		    initialConfig: this.manageModelsTree
		  });

	}
});
