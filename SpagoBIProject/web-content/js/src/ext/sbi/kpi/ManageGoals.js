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

	var paramsOUChildList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "OU_CHILDS_LIST"};
	this.configurationObject = {};
	this.configurationObject.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_OUS_ACTION'
			, baseParams: paramsOUChildList
	});	
	this.config = config;
	this.addEvents();
	var thisPanel = this;
	this.initChildrens(config);

	var c = {
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
		this.ouTree.getSelectionModel().addListener('selectionchange', this.updateGoalPanel, this);
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
			dataUrl: thisPanel.configurationObject.manageTreeService,
			createNode: function(attr) {
				
				if (attr.nodeId) {
					attr.id = attr.nodeId;
				}
				if (attr.ou!=null) {
					attr.text = attr.ou.name;
					attr.qtip = attr.ou.name;
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
//		this.goalDetailsFormPanelName = new Ext.form.TextField(	{
//			fieldLabel: 'Name',
//			name: 'name',
//			allowBlank:false
//		});
		
		this.goalDetailsFormPanel = new Ext.FormPanel({
			border: false,
			labelWidth: 75, 
			bodyStyle:'padding:5px 5px 0',
			items:[	this.goalDetailsFormPanelGoal]
		});
		
//		var goalDetailsFormPanelNameForm = new Ext.FormPanel({
//			border: false,
//			labelWidth: 75,
//			height: 55,
//			items:[	this.goalDetailsFormPanelName]
//		});
		
//		this.goalDetailsFormPanel = new Ext.Panel({
//			layout:'table',
//		    layoutConfig: {
//		        columns: 2
//		    },
//			border: false,
//	        bodyStyle:'padding:5px 5px 0',
//	        //items: [goalDetailsFormPanelGoalForm, goalDetailsFormPanelNameForm]
//		    items: [goalDetailsFormPanelGoalForm]
//	    });
	}
	
	,initGoalDetailsKpiPanel: function(conf){

		conf.rootNodeText = 'root';
		conf.rootNodeId = '4';
		conf.checkbox= true;
		this.selectedGrantId = '2';
		this.selectedOUNode = '1';//this.ouTree.modelsTree.getSelectionModel().getSelectedNode().id;//'1';

		
		var paramsOUChildList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "KPI_ACTIVE_CHILDS_LIST"};
		conf.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MANAGE_OUS_ACTION'
				, baseParams: paramsOUChildList
		});	

		conf.treeLoaderBaseParameters = {'grantId': this.selectedGrantId, 'ouNodeId': this.selectedOUNode}
		this.goalDetailskpiPanel=new Sbi.widgets.ModelsInstanceTree(conf, {});
		//this.goalDetailskpiPanel.renderTree(this.goalDetailskpiPanel.modelsTree);
		this.goalDetailskpiPanel.doLayout();
		this.doLayout();
	}
	
	,updateGoalPanel: function(sel, node){
		var conf=this.config;
		conf.rootNodeText = 'root';
		conf.rootNodeId = '4';
		conf.checkbox= true;
		this.selectedGrantId = '2';
		this.selectedOUNode = ''+node.id;

		conf.treeLoaderBaseParameters = {'grantId': this.selectedGrantId, 'ouNodeId': this.selectedOUNode}
		
		var treeroot = {
			nodeType : 'async',
			text : 'root',
			modelId : '4',
			id:  '4'
		}
		
		this.goalDetailskpiPanel.loader.baseParams = conf.treeLoaderBaseParameters;
		this.goalDetailskpiPanel.setRootNode(treeroot);
		this.goalDetailskpiPanel.getRootNode().expand(false, /*no anim*/false);


	}
});


