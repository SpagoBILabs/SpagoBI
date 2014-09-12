/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Marco Cortella (marco.cortella@eng.it)
 */

//TODO: DA SPOSTARE IN FILE SEPARATO
Ext.define('Item', {
    extend: 'Ext.data.Model',
    fields: ['text', 'canDropOnFirst', 'canDropOnSecond','']
})


Ext.define('Sbi.tools.hierarchieseditor.HierarchiesEditorSplittedPanel', {
    extend: 'Sbi.widgets.compositepannel.SplittedPanel'

    ,config: {
    	
    	
    }

	, constructor: function(config) {
		this.initServices();
		
		//*******************
		//Automatic Hierarchies Combos
		
		this.dimensionsStore = this.createDimensionsStore();
		
		this.comboDimensions = new Ext.form.ComboBox({
			id: 'dimensionsCombo',
			fieldLabel: LN('sbi.hierarchies.dimensions'),
			store :this.dimensionsStore,
			displayField : 'DIMENSION_NM',
			valueField :  'DIMENSION_NM',
			width : 300,
			typeAhead : true, forceSelection : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true, 
			editable : false,
			style:'padding:5px',
			listeners: {
				//TODO
			}
		});
		
		this.comboHierarchies = new Ext.form.ComboBox({
			id: 'hierarchiesCombo',
			fieldLabel: LN('sbi.hierarchies.hierarchies'),
			store : [],
			displayField : 'HIERARCHY_NM',
			valueField :  'HIERARCHY_NM',
			width : 300,
			typeAhead : true, forceSelection : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true, 
			editable : false,
			style:'padding:5px',
			listeners: {
				//TODO
			}
		});
		
		
		this.automaticHierarchiesComboPanel =  Ext.create('Ext.panel.Panel', {
			items:[this.comboDimensions,this.comboHierarchies]
		});
		
		
		//*******************
		//Trees
		this.store1 = new Ext.data.TreeStore({
	        model: 'Item',
	        root: {
	            text: 'Root 1',
	            expanded: true,
	            children: [{
	                text: 'Child 1',
	                id: 'Child 1',
	                canDropOnFirst: true,
	                canDropOnSecond: true,
	                leaf: true
	            }, {
	                text: 'Child 2',
	                id: 'Child 2',
	                canDropOnFirst: true,
	                canDropOnSecond: false,
	                leaf: true
	            }, {
	                text: 'Child 3',
	                id: 'Child 3',	                
	                canDropOnFirst: false,
	                canDropOnSecond: true,
	                leaf: true
	            }, {
	                text: 'Child 4',
	                id: 'Child 4',	                	                
	                canDropOnFirst: false,
	                canDropOnSecond: false,
	                leaf: true
	            },
	            {
	                text: 'Folder 5',
	                id: 'Folder 5',	                
	                canDropOnFirst: false,
	                canDropOnSecond: false,
	                children: [{
		                text: 'Child 51',
		                id: 'Child 51',		                
		                leaf: true
		            }, {
		                text: 'Child 52',
		                id: 'Child 52',
		                leaf: true
		            }]
	            }]
	        },
	        proxy: {
	            type: 'memory',
	            reader: {
	              type: 'json'
	            }
	          }
	    });
		
	   this.store2 = new Ext.data.TreeStore({
	        model: 'Item',
	        root: {
	            text: 'Root 2',
	            expanded: true,
	            children: [{
	                text: 'Folder 1',
	                id: 'Folder 1',	                
	                children: [],
	                expanded: true
	            }, {
	                text: 'Folder 2',
	                id: 'Folder 2',	                
	                children: [],
	                expanded: true
	            }]
	        },
	        proxy: {
	            type: 'memory',
	            reader: {
	              type: 'json'
	            }
	          }
	   
	    });
	    
	   this.treePanelLeft = Ext.create('Ext.tree.Panel', {
	        id: 'firstTreePanel',
	        layout: 'fit',
	        store: this.store1,
	        rootVisible: true,
	        frame: false,
	        border:false,
	        bodyStyle: {border:0},
            viewConfig: {
                plugins: {
                   ptype: 'treeviewdragdrop',
                   ddGroup: 'DDhierarchiesTrees',
                   enableDrag: true,
                   enableDrop: false
                }
            }

	    });
	   
	   this.treePanelRight = Ext.create('Ext.tree.Panel', {
	        id: 'secondTreePanel',
	        layout: 'fit',
	        store: this.store2,
	        rootVisible: true,
	        frame: false,
	        border:false,
	        bodyStyle: {border:0},
            viewConfig: {
               plugins: {
                  ptype: 'treeviewdragdrop',
                  ddGroup: 'DDhierarchiesTrees',
                  enableDrag: true,
                  enableDrop: true
               }
              ,listeners: {
                nodedragover: function(targetNode, position, dragData){
                	alert("on drag over");
                    var rec = dragData.records[0],
                        isFirst = targetNode.isFirst(),
                        canDropFirst = rec.get('canDropOnFirst'),
                        canDropSecond = rec.get('canDropOnSecond');
                        
                    return isFirst ? canDropFirst : canDropSecond;
                }
	            , beforedrop: {
	                fn: this.onBeforeDropRightTree,
	                scope: this
	            }
            }	        
           }
	    });
		
		
		//**********************
		
		
		//Main Objects **************************************
		this.mainTitle = 'Hierarchies Editor';
		
		this.leftPanel =  Ext.create('Ext.panel.Panel', {
		    bodyPadding: 5,  
			title: 'Gerarchie Automatiche',
			items: [this.automaticHierarchiesComboPanel,this.treePanelLeft]
		});
		
		this.rightPanel =  Ext.create('Ext.panel.Panel', {
		    bodyPadding: 5,  	
			title: 'Gerarchie Custom',
			items: [this.treePanelRight]
		});
		//***************************************************
		
		this.callParent(arguments);

	}
	
	//Initializations
	, createDimensionsStore: function(){
		Ext.define("DimensionsModel", {
    		extend: 'Ext.data.Model',
            fields: ["DIMENSION_NM","DIMENSION_DS"]
    	});
    	
    	var dimensionsStore=  Ext.create('Ext.data.Store',{
    		model: "DimensionsModel",
    		proxy: {
    			type: 'ajax',
    			url:  this.services['getDimensions'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
    	dimensionsStore.load();
    	
    	return dimensionsStore;
	}
	
	
	//REST services for Ajax calls
	,initServices : function(baseParams) {
		this.services = [];
		
		if(baseParams == undefined){
			baseParams ={};
		}
		
		this.services["getDimensions"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/dimensions',
			baseParams: baseParams
		});
		
	}	
	
	//Private methods
	
	, onBeforeDropRightTree: function(node, data, overModel, dropPosition, dropFunction, options) {
		//alert("before drop");
        //data.copy = true; //to copy node and not moving from source
        
        var isFirstTarget = overModel.data.isFirst;
        

        
        var nodeTargetName = node.textContent;
        
        var rec = data.records[0];
        var isLeaf = rec.get('leaf');
        var canDropFirst = rec.get('canDropOnFirst');
        var canDropSecond = rec.get('canDropOnSecond');
        
        var mystore = this.store2;
        /*
        if (isLeaf){
        	return false;
        }
        */
        /*
        if (isFirstTarget){
        	node = canDropFirst;
        } else {
        	alert ("Cannot drop this node here!");
        	return false;
        }
        */
        
        
        //test visita albero partendo dalla radice
        
        var rootNode =  mystore.getRootNode();
        /*
        var me = this;
        var hash = {};
        rootNode.cascadeBy(function(node) {
           // if (fn.call(me, node)) {
              if (node.data.parentId == 'root') {
                hash[node.data.id] = node.copy(null, true);
                hash[node.data.id].childNodes = [];
              }
              else if (hash[node.data.parentId]) {
                hash[node.data.parentId].appendChild(node.data);
              }
           // }
          });
        
        alert(hash);
        */
        var myJson= this.getJson(rootNode);
   }
	//Transform the Tree structure in a JSON form that can be converted to a string
	//node is the rootNode
	,getJson: function(node) {
		// Should deep copy so we don't affect the tree
		var json = node.data;

		json.children = [];
		for (var i=0; i < node.childNodes.length; i++) {
			json.children.push( this.getJson(node.childNodes[i]) );
		}
		return json;
	}


	



	
});

