/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

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
				      Ext.fly(node.getUI().getIconEl() ).replaceClass('', 'has-kpi');
			      }
			      Ext.fly(this.getEl()).frame("ff0000");
			      return(true);
			    }
			  }, this);
	}
});
