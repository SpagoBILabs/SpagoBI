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
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageGoals = function(config, ref) { 
	
	//debug options
	this.selectedGrantId = '2';
	this.kpiTreeRoot ={
		nodeType : 'async',
		text : 'root',
		modelId : '4',
		id:  '4'
	}

	var paramsOUChildList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "OU_CHILDS_LIST"};
	this.configurationObject = {};
	this.configurationObject.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_OUS_ACTION'
			, baseParams: paramsOUChildList
	});	
	
	var paramsGoalChildList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "GOAL_NODE_CHILD"};
	this.configurationObject.manageGoalTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_GOALS_ACTION'
			, baseParams: paramsGoalChildList
	});	
	
	var paramsGoal = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "GRANT_DEF"};
	this.configurationObject.manageGoalService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_OUS_ACTION'
			, baseParams: paramsGoal
	});	
	
	
	
	
	
	this.config = config;
	this.addEvents();
	var thisPanel = this;
	this.initChildrens(config);
	
	var tbSave = new Ext.Toolbar( {
		buttonAlign : 'right',
		items : [ 
		         new Ext.Toolbar.Button( {
		        	 text : LN('sbi.generic.update'),
		        	 iconCls : 'icon-save',
		        	 handler : this.save,
		        	 width : 30,
		        	 scope : thisPanel
		         })
		         ]
	});

	var c = {
			id: 'goalPanel',
			tbar: tbSave,
			title: 'Goal Definition',
			layout: 'border',
			border: false,
			items: [
			        {
			        	id: 'OU',
			        	title: 'OU',
			        	region: 'west',
			        	width: 275,
			        	collapseMode:'mini',
			        	autoScroll: true,
			        	split: true,
			        	layout: 'fit',
			        	items: [this.ouTree]
			        },
			        {
			        	id: 'goalPanel',	  
			        	region: 'center',
			        	width: 300,
			        	split: true,
			        	collapseMode:'mini',
			        	autoScroll: true,
			        	layout: 'fit',
			        	items: [this.goalPanel]
			        }
			        ]
	};
    Sbi.kpi.ManageGoals.superclass.constructor.call(this, c);	
	
};

Ext.extend(Sbi.kpi.ManageGoals, Ext.Panel, {
	ouTree: null
	,goalPanel: null
	,goalTreePanel: null
	,goalDetailsPanel: null
	,goalDetailsFormPanel: null
	,goalDetailsFormPanelGoal: null
	,goalDetailsFormPanelName: null
	,goalDetailskpiPanel: null
	,kpiTreeRoot: null
	
	,initChildrens: function(conf){
		this.initOUPanel(conf);
		this.initGoalPanel(conf);
	}
	

	,initOUPanel: function(conf){
		conf.rootNodeText = 'root';
		conf.rootNodeId = '1';
		this.ouTree =new Sbi.kpi.ManageGoalsOUTree(conf, {});
		//this.ouTree.renderTree(this.ouTree);
		this.ouTree.doLayout();
	//	this.doLayout();
		this.ouTree.on('afterLayout',this.selectOUPanelRoot, this);
		this.ouTree.getSelectionModel().addListener('selectionchange', this.updateGoalDetailsKpi, this);
	}
	
	, selectOUPanelRoot: function(tree){
		tree.getSelectionModel().select(tree.getRootNode());
		tree.un('afterLayout',this.selectOUPanelRoot, this);
	}

	,initGoalPanel: function(conf){
		this.initGoalDetailsPanel(conf);
		this.initGoalTreePanel();
		this.goalPanel = new Ext.Panel({
    		layout: 'border',
    		autoHeight: true,
    		border: false,
			items: [
			          {
			        	  id: 'goalPanelTree',
			        	  title: 'Goal',
			        	  region: 'north',
			        	  height: 150,
			        	  collapseMode:'mini',
			        	  autoScroll: true,
			        	  split: true,
			        	  layout: 'fit',
			        	  items: [this.goalTreePanel]
			          },
			          {
			        	  id: 'goalPanelDetails',
			        	  title: 'Details',
			        	  region: 'center',
			        	  split: true,
			        	  collapseMode:'mini',
			        	  autoScroll: true,
			        	  layout: 'fit',
			        	  items: [this.goalDetailsPanel]
			          }
			          ]
    	});
	}
	
	,initGoalTreePanel: function(){
		var c= {};
		var thisPanel = this;
		var treeLoader =new Ext.tree.TreeLoader({
			nodeParameter: 'nodeId',
			dataUrl: thisPanel.configurationObject.manageGoalTreeService,
			createNode: function(attr) {
				
				if (attr.nodeId) {
					attr.id = attr.nodeId;
				}
				if (attr.ou!=null) {
					attr.text = attr.name;
					attr.qtip = attr.label;
				}
	
				var node = Ext.tree.TreeLoader.prototype.createNode.call(this, attr);

				return node;
			}
		}); 
		
		c.treeLoader = treeLoader;
		this.goalTreePanel = new Sbi.widgets.ConfigurableTree(c);
	}
	
	,initGoalDetailsPanel: function(conf){
		this.initGoalDetailsFormPanel();
		this.initGoalDetailsKpiPanel(conf);
		
		this.goalDetailsPanel = new Ext.Panel({
    		layout: 'border',
    		border: false,
			items: [
			          {
			        	  id: 'goalPanelDetailsGoal',
			        	  region: 'north',
			        	  height:60,
			        	  collapseMode:'mini',
			        	  //split: true,
			        	  layout: 'fit',
			        	  items: [this.goalDetailsFormPanel]
			          },
			          {
			        	  id: 'goalPanelDetailsKPI',
			        	  region: 'center',
			        	  split: true,
			        	  collapseMode:'mini',
			        	  autoScroll: true,
			        	  layout:'anchor',
			        	  items: [this.goalDetailskpiPanel]
			          }
			          ]
    	});
	}
	
	,initGoalDetailsFormPanel: function(){
		
		this.goalDetailsFormPanelGoal = new Ext.form.TextArea(	{
			fieldLabel: 'Goal',
			name: 'goal',
			style: 'width: 100%;',
			height: 50,
			allowBlank:false
		});
		
		this.goalDetailsFormPanel = new Ext.FormPanel({
			border: false,
			labelWidth: 75, 
			bodyStyle:'padding:5px 5px 0',
			items:[	this.goalDetailsFormPanelGoal]
		});
		
	}
	
	,initGoalDetailsKpiPanel: function(conf){

		this.selectedOUNode = '1';//this.ouTree.modelsTree.getSelectionModel().getSelectedNode().id;//'1';
		
		var paramsOUChildList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "KPI_ACTIVE_CHILDS_LIST"};
		
		conf.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MANAGE_OUS_ACTION'
				, baseParams: paramsOUChildList
		});	

		conf.treeLoaderBaseParameters = {'grantId': this.selectedGrantId, 'ouNodeId': this.selectedOUNode}
		conf.rootNode = this.kpiTreeRoot;
		this.goalDetailskpiPanel= new Sbi.widgets.ModelInstanceTree.createGoalModelInstanceTree(conf);

		this.goalDetailskpiPanel.doLayout();
		this.doLayout();
	}
	
	,updateGoalDetailsKpi: function(sel, node){
		var conf=this.config;

		conf.checkbox= true;

		this.selectedOUNode = ''+node.id;

		conf.treeLoaderBaseParameters = {'grantId': this.selectedGrantId, 'ouNodeId': this.selectedOUNode}
				
		this.goalDetailskpiPanel.loader.baseParams = conf.treeLoaderBaseParameters;
		this.goalDetailskpiPanel.setRootNode(this.kpiTreeRoot);
		this.goalDetailskpiPanel.getRootNode().expand(false, /*no anim*/false);
	}
	
	,updateGoalDetailsKpiRoot: function(root){
		var conf=this.config;
		conf.checkbox= true;
		this.kpiTreeRoot = root;
		this.goalDetailskpiPanel.setRootNode(root);
		this.goalDetailskpiPanel.getRootNode().expand(false, /*no anim*/false);
	}
	
	,updatePanel: function(grant){
		this.selectedGrantId = grant;
		var thisPanel = this;
		
		Ext.Ajax.request({
			url: this.configurationObject.manageGoalService,
			params: {'grantId': grant},
			method: 'POST',
			success: function(response, options) {
				if (response !== undefined && response.responseText!== undefined) {
					var kpiInstRoot = Ext.util.JSON.decode( response.responseText ).modelinstance;
					alert(kpiInstRoot.modelCode+' - '+kpiInstRoot.name+ attrKpiCode);
		    		var attrKpiCode = '';
		    		if(kpiInstRoot.kpiCode !== undefined){
		    			attrKpiCode = ' - '+kpiInstRoot.kpiCode;
		    		}
					var root = {
							nodeType : 'async',
							text : kpiInstRoot.modelCode+' - '+kpiInstRoot.name+ attrKpiCode,
							modelId : kpiInstRoot.modelInstId,
							id:  kpiInstRoot.modelInstId
						}
						
					thisPanel.updateGoalDetailsKpiRoot(root);
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.savingItemError'), LN('sbi.generic.serviceError'));
				}
			},
			failure: function() {
				Ext.MessageBox.show({
					title: LN('sbi.generic.error'),
					msg: LN('sbi.generic.savingItemError'),
					width: 150,
					buttons: Ext.MessageBox.OK
				});
				
			}
			,scope: this
	
		});
	}
	
	, save: function(){
		alert(this.visitGoalTree(this.goalDetailskpiPanel.getRootNode()).toSource());
	}
	
	, visitGoalTree: function(node){
		
		var array = new Array();
		
		if(node.getUI().isChecked()){
			var serializedNode = {};
			var cols = this.goalDetailskpiPanel.columns;
			for(var i=0; i<cols.length; i++){
				var element = document.getElementById(cols[i].columnId+node.columnValues);
				if(element==null){
					serializedNode[cols[i].columnId]= '';
				}else{
					serializedNode[cols[i].columnId] = element.value;
				}
			}
			array.push(serializedNode);
		}
		for(var i=0; i<node.childNodes.length; i++){
			array = array.concat(this.visitGoalTree(node.childNodes[i]));
		}
		return array;
	}
});


