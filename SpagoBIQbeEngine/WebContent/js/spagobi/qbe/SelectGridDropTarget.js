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
  * - name (mail)
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.SelectGridDropTarget = function(selectGridPanel, config) {
	
	var c = Ext.apply({
		ddGroup    : 'gridDDGroup',
		copy       : false
	}, config || {});
	

	this.targetPanel = selectGridPanel;
	this.targetGrid = this.targetPanel.grid; 
	this.targetElement = this.targetGrid.getView().el.dom.childNodes[0].childNodes[1];
	
	// constructor
    Sbi.qbe.SelectGridDropTarget.superclass.constructor.call(this, this.targetElement, c);
};

Ext.extend(Sbi.qbe.SelectGridDropTarget, Ext.dd.DropTarget, {
    
    services: null
    , targetPanel: null
    , targetGrid: null
    , targetElement: null
   
    , notifyOver : function(ddSource, e, data){
    	return (ddSource.grid &&  ddSource.grid.type === 'filtergrid')? this.dropNotAllowed : this.dropAllowed;
    }
		
	, notifyDrop : function(ddSource, e, data){
    	
		// the row index on which the tree node has been dropped on
		var rowIndex;

		if(this.targetGrid.targetRow) {
			rowIndex = this.targetGrid.getView().findRowIndex( this.targetGrid.targetRow );
		}

		if(rowIndex == undefined || rowIndex === false) {
			// append the new row
			rowIndex = undefined;
		}  
		
		var sourceObject;
      	if(ddSource.tree) {
        	this.notifyDropFromDatamartStructureTree(ddSource, e, data, rowIndex);
      	} else if(ddSource.grid &&  ddSource.grid.type === 'selectgrid') {
        	this.notifyDropFromSelectGrid(ddSource, e, data, rowIndex);
      	} else if(ddSource.grid &&  ddSource.grid.type === 'filtergrid') {
        	this.notifyDropFromFilterGrid(ddSource, e, data);
      	} else if(ddSource.grid &&  (ddSource.grid.type === 'parametersgrid' || ddSource.grid.type === 'documentparametersgrid')) {
			Ext.Msg.show({
				   title:'Drop target not allowed',
				   msg: 'Parameters cannot be dropped here!',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
      	} else {
        	alert('Source object: unknown');
      	}        
	}

	// =====================================================================================
	// from TREE
	// =====================================================================================
	, notifyDropFromDatamartStructureTree: function(ddSource, e, data, rowIndex) {
		//alert('Source object: tree');


        var node;  // the node dragged from tree to grid
        var field;        
        var nodeType;
        
		node = ddSource.dragData.node; 				
		nodeType = node.attributes.type || node.attributes.attributes.type;

        if(nodeType == 'field') {
        	field = {
        		id: node.id , 
            	entity: node.attributes.attributes.entity , 
            	field: node.attributes.attributes.field,
            	alias: node.attributes.attributes.field,
            	longDescription: node.attributes.attributes.longDescription
          	};
        
        	this.targetPanel.addField(field, rowIndex);
        	
        } else if(nodeType == 'calculatedField'){
        	
        	field = {
            	id: node.id , 
                entity: node.attributes.attributes.entity , 
                field: node.attributes.attributes.field,
                alias: node.attributes.attributes.field  
             };
            
            this.targetPanel.addField(field, rowIndex);
            
            // TODO: drop also all  the correlated fields. Snippet from method onAddNodeToSelect of QueryBuilderPanel
            /*
            var seeds =  Sbi.qbe.CalculatedFieldWizard.getUsedItemSeeds('dmFields', node.attributes.attributes.formState.expression);
 	    		for(var i = 0; i < seeds.length; i++) {
 	    			var n = node.parentNode.findChildBy(function(childNode) {
 	    				return childNode.id === seeds[i];
 	    			});
 	    			
 	    			if(n) {
 	    				this.onAddNodeToSelect(n, {visible:false});
 	    				//this.dataMartStructurePanel.fireEvent('click', this.dataMartStructurePanel, n);
 	    			} else {
 	    				alert('node  [' + seeds + '] not contained in entity [' + node.parentNode.text + ']');
 	    			}
 	    			
 	    			
 	    		}
             */
        } else if(nodeType == 'inLineCalculatedField'){

        	field = {
        			id: node.attributes.attributes.formState,
        			type: 'inLineCalculatedField',
        			entity: node.parentNode.text, 
        			field: node.text,
        			alias: node.text,
        			longDescription: null
		    };

            this.targetPanel.addField(field, rowIndex);
            
        } else if(nodeType == 'entity'){
			
			for(var i = 0; i < node.attributes.children.length; i++) {
				if(node.attributes.children[i].attributes.type != 'field') continue;
				field = {
      				id: node.attributes.children[i].id , 
        			entity: node.attributes.children[i].attributes.entity , 
        			field: node.attributes.children[i].attributes.field,
                	alias: node.attributes.children[i].attributes.field,
                	longDescription: node.attributes.children[i].attributes.longDescription
      			};				
				this.targetPanel.addField(field, rowIndex);
			}
			
        } else {
        	Ext.Msg.show({
				   title:'Drop target not allowed',
				   msg: 'Node of type [' + nodeType + '] cannot be dropped here',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});
        }

        this.targetGrid.getView().refresh();
	} 

	//=====================================================================================
	// from SELECT GRID (self)
	// ====================================================================================
    , notifyDropFromSelectGrid: function(ddSource, e, data, rowIndex) {
    	//alert('Source object: select-grid');
        var sm = this.targetGrid.getSelectionModel();
        var ds = this.targetGrid.getStore();
        var rows = sm.getSelections();
        
        rows = rows.sort(function(r1, r2) {
        	var row1 = ds.getById(r1.id);
            var row2 = ds.getById(r2.id);
            return ds.indexOf(r2) - ds.indexOf(r1);
         });
         if(rowIndex == undefined) {
            rows = rows.reverse();
         }
           
         for (i = 0; i < rows.length; i++) {
         	var rowData=ds.getById(rows[i].id);
            if(!this.copy) {
            	ds.remove(ds.getById(rows[i].id));
                if(rowIndex != undefined) {
                  ds.insert(rowIndex, rowData);
                } else {
                  ds.add(rowData);
                }
            }
         }
         
         this.targetGrid.getView().refresh();
      } // notifyDropFromSelectGrid
      
      , notifyDropFromFilterGrid: function(ddSource, e, data) {
      	//alert('Source object: filter-grid');
      } // notifyDropFromFilterGrid  	
});