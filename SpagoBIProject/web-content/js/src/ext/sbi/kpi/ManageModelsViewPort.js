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
 * Authors - Chiara Chiarelli
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageModelsViewPort = function(config) { 
	
	var conf = config;

	//DRAW center element
	this.manageModels = new Sbi.kpi.ManageModels(conf, this);

	//DRAW west element
    this.modelsGrid = new Sbi.kpi.ManageModelsGrid(conf, this.manageModels);
   //DRAW east element
    this.manageKpis = new Sbi.kpi.ManageKpis(conf);
	
	var viewport = {
		layout: 'border'
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
	           items:[this.modelsGrid]
	          },
		    {
		       region: 'center',
		       width: 300,
		       height:560,
		       split: true,
		       collapseMode:'mini',
		       autoScroll: true,
		       layout: 'fit',
		       items: [this.manageModels]
		    }, {
		        region: 'east',
		        split: true,
		        width: 900,
		        height:540,
		        collapsed:true,
		        collapseMode:'mini',
		        autoScroll: true,
		        layout: 'fit',
		        items:[this.manageKpis]
		    }
		]
		

	};
	
	
	var c = Ext.apply({}, config || {}, viewport);
	
	this.initPanels();

	Sbi.kpi.ManageModelsViewPort.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.kpi.ManageModelsViewPort, Ext.Viewport, {
	modelsGrid: null,
	manageModels: null,
	manageKpis: null,
	lastRecSelected: null
	
	,initPanels : function() {

		this.manageKpis.addListener('render', this.configureDD, this);
		this.modelsGrid.addListener('rowclick', this.sendSelectedItem, this);

	}
	, displayTree: function(rec){
		this.manageModels.rootNodeText = rec.get('code')+ " - "+rec.get('name');
		this.manageModels.rootNodeId = rec.get('modelId');
		var newroot = this.manageModels.createRootNodeByRec(rec);
		this.manageModels.mainTree.setRootNode(newroot);
		
		this.manageModels.mainTree.getSelectionModel().select(newroot);
		this.manageModels.mainTree.doLayout();
	}

	,sendSelectedItem: function(grid, rowIndex, e){
		var rec = grid.getSelectionModel().getSelected();


		//if unsaved changes
		if(this.manageModels.nodesToSave.length > 0){
			//if there are modification on current selection
			Ext.MessageBox.confirm(
					LN('sbi.generic.pleaseConfirm'),
					LN('sbi.generic.confirmChangeNode'),            
		            function(btn, text) {

		                if (btn=='yes') {

		                	this.manageModels.cleanAllUnsavedNodes();	        			
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

	, configureDD: function() {
	  	  /****
		  * Setup Drop Targets
		  ***/

		  var nodeTreePanelDropTarget = new Ext.tree.TreeDropZone(this.manageModels.mainTree, {
		    ddGroup  : 'grid2treeAndDetail',
		    dropAllowed : true,
		    overClass: 'over',
		    scope: this,
		    initialConfig: this.manageModels
		  });

		  // This will make sure we only drop to the view container
		  var fieldDropTargetEl =  this.manageModels.detailFieldKpi.getEl().dom; 
		  var formPanelDropTarget = new Ext.dd.DropTarget(fieldDropTargetEl, {
			    ddGroup  : 'grid2treeAndDetail',
			    overClass: 'over',
			    scope: this,
			    initialConfig: this.manageModels,
			    notifyEnter : function(ddSource, e, data) {
			      //Add some flare to invite drop.
			      Ext.fly(Ext.getCmp('model-detailFieldKpi').getEl()).frame("00AE00");

			    },
			    notifyDrop  : function(ddSource, e, data){
	  
			      // Reference the record (single selection) for readability
			      var selectedRecord = ddSource.dragData.selections[0];

			      // Load the record into the form field
			      Ext.getCmp('model-detailFieldKpi').setValue(selectedRecord.get('name')); 

			      var node = this.initialConfig.mainTree.getSelectionModel().getSelectedNode() ;

			      if(node !== undefined && node != null){
			    	  var nodesList = this.initialConfig.nodesToSave;
			    	  
			    	  //if the node is already present in the list
			    	  var exists = nodesList.indexOf(node);
			    	  if(exists == -1){
						  var size = nodesList.length;
						  this.initialConfig.nodesToSave[size] = node;
						  node.attributes.toSave = true;
			    	  }
			    	  
				      node.attributes.kpi = selectedRecord.get('name');
				      node.attributes.kpiId = selectedRecord.get('id');
			      }
			      Ext.fly(this.getEl()).frame("ff0000");
			      return(true);
			    }
			  }, this);
	}
});
