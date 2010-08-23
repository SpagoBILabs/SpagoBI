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
	
	var conf = config;

	//DRAW center element
	this.manageModelInstances = new Sbi.kpi.ManageModelInstances(conf, this);

	//DRAW west element
    this.modelInstancesGrid = new Sbi.kpi.ManageModelInstancesGrid(conf, this.manageModelInstances);
   //DRAW east element
    this.manageModelsTree = new Sbi.kpi.ManageModelsTree(conf);
	
	var viewport = {
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
		       items: [this.manageModelInstances]
		    }, {
		        region: 'east',
		        split: true,
		        width: 400,
		        height:560,
		        collapsed:true,
		        collapseMode:'mini',
		        autoScroll: true,
		        items:[this.manageModelsTree]
		    }
		]
		

	};
	
	
	var c = Ext.apply({}, config || {}, viewport);
	
	this.initPanels();

	Sbi.kpi.ManageModelInstancesViewPort.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.kpi.ManageModelInstancesViewPort, Ext.Viewport, {
	manageModelInstances: null,
	modelInstancesGrid: null,
	manageModelsTree: null,
	lastRecSelected: null

	,initPanels : function() {
		this.modelInstancesGrid.addListener('rowclick', this.sendSelectedItem, this);	
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
			if(rec != this.lastRecSelected){
				this.lastRecSelected = rec;
			}
		}
	
	}
	, displayTree: function(rec){
		this.manageModelInstances.rootNodeText = rec.get('name');
		this.manageModelInstances.rootNodeId = rec.get('modelInstId');
		var newroot = this.manageModelInstances.createRootNodeByRec(rec);
		this.manageModelInstances.mainTree.setRootNode(newroot);
		
		this.manageModelInstances.mainTree.getSelectionModel().select(newroot);
		this.manageModelInstances.mainTree.doLayout();
	}
});
