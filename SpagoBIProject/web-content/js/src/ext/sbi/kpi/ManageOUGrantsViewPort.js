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

Sbi.kpi.ManageOUGrantsViewPort = function(config) { 
	var paramsResList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINST_RESOURCE_LIST"};
	var paramsResSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINST_RESOURCE_SAVE"};

	var conf = config;
	this.resListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsResList
	});	
	this.resSaveService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsResSave
	});	
	this.resourcesStore = new Ext.data.JsonStore({
		autoLoad: false    	  
		, root: 'rows'
			, url: this.resListService	
			, fields: ['resourceId', 'resourceName', 'resourceCode', 'resourceType', 'modelInstId']

	});
	var paramsTree = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_COPY_MODEL"};
	var paramsSaveRoot = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_SAVE_ROOT"};

	this.modelTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsTree
	});	
	this.saveRootService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsSaveRoot
	});
	//DRAW center element
	this.ManageOUGrants = new Sbi.kpi.ManageOUGrants(conf, this);
	this.ManageOUGrants.addEvents('changeOU_KPI');
	this.ManageOUGrants.addEvents('saved');
	this.ManageOUGrants.on('changeOU_KPI',function(kpi,ou){this.displayTree(kpi,ou);},this);


	this.manageOUGrantsGrid = new Sbi.kpi.ManageOUGrantsGrid(conf, this);

	this.ManageOUGrants.on('saved',function(){this.manageOUGrantsGrid.mainElementsStore.reload(); },this);

	conf.readonlyStrict = true;
	conf.dropToItem = 'kpinameField';

	this.initPanels();

	var c = Ext.apply({}, config || {}, this.viewport);

	Sbi.kpi.ManageOUGrantsViewPort.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.kpi.ManageOUGrantsViewPort, Ext.Viewport, {
	ManageOUGrants: null,
	manageOUGrantsGrid: null,
	resourcesTab : null,
	centerTabbedPanel: null,
	viewport: null,
	lastRecSelected: null

	,initPanels : function() {
		
		this.manageOUGrantsGrid.addListener('rowclick', this.sendSelectedItem, this);	
		this.manageOUGrantsGrid.addListener('copytree', this.copyModelTree,  this);	

		this.tabs = new Ext.Panel({
			title: LN('sbi.grants.panelTitle')
			, id : 'modeinstTab'
			, layout: 'fit'
			, autoScroll: true
			, items: [this.ManageOUGrants]
			, itemId: 'modInstTab'
			, scope: this
		});

		this.viewport = {
				layout: 'border'
					, height:560
					, autoScroll: true
					, items: [
					          {
					        	  id: 'modelInstancesList00',
					        	  region: 'west',
					        	  width: 275,
					        	  height:560,
					        	  collapseMode:'mini',
					        	  autoScroll: true,
					        	  split: true,
					        	  layout: 'fit',
					        	  items:[this.manageOUGrantsGrid]
					          },
					          {
					        	  id: 'main00',	  
					        	  region: 'center',
					        	  width: 300,
					        	  height:560,
					        	  split: true,
					        	  collapseMode:'mini',
					        	  autoScroll: true,
					        	  layout: 'fit',
					        	  items: [this.tabs]
					          }
					          ]
		};
		
		this.ManageOUGrants.setDisabled(true);
	}

	,sendSelectedItem: function(grid, rowIndex, e){
		
		this.ManageOUGrants.setDisabled(false);
		var rec = this.manageOUGrantsGrid.rowselModel.getSelected();
		this.ManageOUGrants.detailFieldLabel.setValue(rec.data.label);
		this.ManageOUGrants.detailFieldName.setValue(rec.data.name);
		this.ManageOUGrants.detailFieldDescr.setValue(rec.data.description);
		this.ManageOUGrants.detailFieldFrom.setRawValue(rec.data.startdate);
		this.ManageOUGrants.detailFieldTo.setRawValue(rec.data.enddate);
		this.ManageOUGrants.detailFieldOUHierarchy.setValue(rec.data.hierarchy.id);
		this.ManageOUGrants.detailFieldOUHierarchy.setRawValue(rec.data.hierarchy.name);
		this.ManageOUGrants.detailFieldKpiHierarchy.setValue(rec.data.modelinstance.modelInstId);
		this.ManageOUGrants.detailFieldKpiHierarchy.setRawValue(rec.data.modelinstance.modelText);
		this.ManageOUGrants.selectedGrantId = rec.data.id;
		this.ManageOUGrants.loadTrees();
	}
	
	, displayTree: function(kpi, ou){
		var newOURoot = this.displayOuTree(ou);
		var newKpiRoot = this.displayKpiTree(kpi);
		this.ManageOUGrants.treePanel.doLayout();

		
//		if(ou.modelinstancenodes == undefined 
//			|| ou.modelinstancenodes == null 
//			|| ou.modelinstancenodes.length == 0){
//			//add all model inst nodes to ou root
//			this.ManageOUGrants.checkForRoot(newOURoot, newKpiRoot);
//		}
	}
	
	, displayKpiTree: function(rec){
		this.ManageOUGrants.rootNodeRightText = rec.text;
		this.ManageOUGrants.rootNodeRightId = rec.modelInstId;
		var newroot = this.ManageOUGrants.createKPIRootNodeByRec(rec);
		this.ManageOUGrants.rightTree.setRootNode(newroot);
		return newroot;
	}
	
	, displayOuTree: function(rec){
		this.ManageOUGrants.rootNodeLeftText = rec.label;
		this.ManageOUGrants.rootNodeLeftId = rec.id;
		var newroot2 = this.ManageOUGrants.createRootNodeByRec(rec);
		this.ManageOUGrants.leftTree.setRootNode(newroot2);
		return newroot2;
	}

});
