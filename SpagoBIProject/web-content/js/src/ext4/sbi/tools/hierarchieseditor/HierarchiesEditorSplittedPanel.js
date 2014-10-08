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
    fields: ['text', 'canDropOnFirst', 'canDropOnSecond','leafId']
})


Ext.define('Sbi.tools.hierarchieseditor.HierarchiesEditorSplittedPanel', {
    extend: 'Sbi.widgets.compositepannel.SplittedPanel'

    ,config: {
    	
    	
    }

	, constructor: function(config) {
		thisPanel = this;
		
		Ext.tip.QuickTipManager.init();
		
		this.initServices();		
		this.createAutomaticHierarchiesPanel();
		this.createCustomHierarchiesPanel();	
		
		//Main Objects **************************************
	   
		this.mainTitle = LN('sbi.hierarchies.editor');
		
		this.leftPanel =  Ext.create('Ext.panel.Panel', {
		    bodyPadding: 5,  
			title: LN('sbi.hierarchies.automatic'),
			items: [this.automaticHierarchiesComboPanel]
		});
		
		this.rightPanel =  Ext.create('Ext.panel.Panel', {
		    bodyPadding: 5,  	
			title: LN('sbi.hierarchies.custom'),
			items: [this.newCustomHierarchyPanel,this.customHierarchiesGrid]
		});
		
		//***************************************************
   	  	
		this.callParent(arguments);

	}

	
	/******************************
	 * Initializations
	 *******************************/
	
	, createAutomaticHierarchiesPanel: function(){
		this.hierarchiesStore;
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
				select:{
		               fn:function(combo, value) {
		            	   //populate hierarchies combo
		            	   this.comboHierarchies.setDisabled(false);
		            	   this.comboHierarchies.clearValue();
		            	   var dimensionName = value[0].get('DIMENSION_NM');
		            	   this.hierarchiesStore = this.createHierarchiesComboStore(dimensionName);
		            	   this.comboHierarchies.bindStore(this.hierarchiesStore);
		            	   //delete existing trees rendered
		            	   if ((Ext.getCmp('automaticTreePanel') != null) & (Ext.getCmp('automaticTreePanel') != undefined)){
			            	   this.leftPanel.remove(Ext.getCmp('automaticTreePanel'));
		            	   }
		            	   if ((Ext.getCmp('customTreePanel') != null) & (Ext.getCmp('customTreePanel') != undefined)){
			             	  	this.rightPanel.remove(Ext.getCmp('customTreePanel'));
		            	   }

		            	   //populate customHierarchies grid 
		            	   this.customHierarchiesGridStore = this.createCustomHierarchiesGridStore(dimensionName);
		            	   this.customHierarchiesGrid.reconfigure(this.customHierarchiesGridStore);
		            	   this.customHierarchiesGrid.setTitle("Custom Hierarchies for: "+dimensionName);
		               }
		           }
		        ,scope:this   
			}
		});
		
		this.comboHierarchies = new Ext.form.ComboBox({
			id: 'hierarchiesCombo',
			fieldLabel: LN('sbi.hierarchies.hierarchies'),
	        queryMode: 'local',
	        displayField : 'HIERARCHY_NM',
			valueField :  'HIERARCHY_NM',
			width : 300,
			typeAhead : true,
			triggerAction : 'all',
			editable : false,
			style:'padding:5px',
			disabled: true,
			listeners: {
				select:{
		               fn:function(combo, value) {
		            	  var hierarchy = value[0].get('HIERARCHY_NM');
		            	  var dimension = this.comboDimensions.getValue();
		            	  this.automaticHierarchiesTreeStore = this.createAutomaticHierarchyTreeStore(dimension, hierarchy);
		            	  this.leftPanel.remove(Ext.getCmp('automaticTreePanel'));
		            	  var myTreePanel = this.createTreePanel(this.automaticHierarchiesTreeStore);
		            	  this.leftPanel.add(myTreePanel);
		            	  myTreePanel.expandAll();

		               }
		           }
		        ,scope:this  
			}
		});
		
		
		this.automaticHierarchiesComboPanel =  Ext.create('Ext.panel.Panel', {
	        bodyStyle:'padding:20px',
	        height: 185,
			items:[this.comboDimensions,this.comboHierarchies]
		});		
	}
	
	//----------------------------------------------------
	
	, createCustomHierarchiesPanel: function(){
		//New Custom Hierarchy Panel
		
	    this.newCustomHierarchyTypeStore = new Ext.data.Store({
	        fields: ['type', 'name'],
	        data: [{
	            "type": "SEMIMANUAL",
	            "name": "Semi-Manual"
	        }, {
	            "type": "MANUAL",
	            "name": "Manual"
	        }]
	    });
		
		this.newCustomHierarchyTypeCombo = new Ext.form.ComboBox({
	        fieldLabel: 'New Hierarchy Type',
	        store: this.newCustomHierarchyTypeStore,
	        queryMode: 'local',
	        displayField : 'name',
	        valueField: 'type',
	        labelWidth: 130,
			width : 300,
			typeAhead : true,
			triggerAction : 'all',
			editable : false,
	    });
		//select first value by default
		this.newCustomHierarchyTypeCombo.select(this.newCustomHierarchyTypeCombo.getStore().getAt(0));
		
		this.newCustomHierarchyButton = new Ext.Button({
	        text: 'Create new Hierarchy',
            margin: '0 0 0 10',
	        listeners: {
	            click: function() {
	            	this.createCustomHierarchyEmptyPanel();
	            	this.disableGUIElements();
	            	this.saveCustomHierarchyButton.setDisabled(false);
	            	this.cancelCustomHierarchyButton.setDisabled(false);
	            }
				,scope:this 
	        }
	    })
		
		
		this.saveCustomHierarchyButton = new Ext.Button({
	        text: 'Save',
            margin: '0 0 0 10',
            disabled: true,
	        listeners: {
	            click: function() {
	            	this.saveCustomHierarchy();
	            	this.enableGUIElements();
	            	this.saveCustomHierarchyButton.setDisabled(true);
	            	this.cancelCustomHierarchyButton.setDisabled(true);
	            }
				,scope:this 
	        }
	    })
		
		this.cancelCustomHierarchyButton = new Ext.Button({
	        text: 'Cancel',
            margin: '0 0 0 10',
            disabled: true,
	        listeners: {
	            click: function() {
	            	this.cancelCustomHierarchy();
	            	this.enableGUIElements();
	            	this.saveCustomHierarchyButton.setDisabled(true);
	            	this.cancelCustomHierarchyButton.setDisabled(true);	            	
	            }
				,scope:this 
	        }
	    })
		
		this.newCustomHierarchyPanel =  Ext.create('Ext.panel.Panel', {
	        bodyStyle:'padding:3px',
			layout:'table',
			layoutConfig: {
		        columns: 5
		    },
			items:[this.newCustomHierarchyTypeCombo,this.newCustomHierarchyButton,this.saveCustomHierarchyButton,this.cancelCustomHierarchyButton]
		});	
		
		
		
		//Custom Hierarchies Grid
		
		//empty store only for initialization
		this.customHierarchiesGridStore = new Ext.data.Store({
	        storeId: 'customHierarchiesStore',
	        fields: ['HIERARCHY_NM', 'HIERARCHY_TP']

	    });
		
		this.customHierarchiesGrid = new Ext.grid.Panel( {
	        title: 'Custom Hierarchies',
	        store: Ext.data.StoreManager.lookup('customHierarchiesStore'),
	        columns: [{
	            header: 'Name',
	            dataIndex: 'HIERARCHY_NM',
	            flex: 1
	        }, {
	            header: 'Type',
	            dataIndex: 'HIERARCHY_TP',
	            width: 100
	        }, {
				//SHOW TREE BUTTON
	        	menuDisabled: true,
				sortable: false,
				xtype: 'actioncolumn',
				width: 20,
				columnType: "decorated",
				items: [{
					tooltip: 'Show Hierarchy tree',
					iconCls   : 'button-detail',  
					handler: function(grid, rowIndex, colIndex) {
						var selectedRecord =  grid.store.getAt(rowIndex);
						thisPanel.onShowCustomHierarchyTree(selectedRecord);
					}
				}]
			}
	        , {
				//DELETE BUTTON
	        	menuDisabled: true,
				sortable: false,
				xtype: 'actioncolumn',
				width: 20,
				columnType: "decorated",
				items: [{
					tooltip: 'Delete custom Hierarchy',
					iconCls   : 'button-remove',  
					handler: function(grid, rowIndex, colIndex) {								
						var selectedRecord =  grid.store.getAt(rowIndex);
						alert("TODO: to implement");
						//thisPanel.onDeleteSchedulation(selectedRecord);
					}
				}]
			}
	        ],
	        height: 150,
	        width: '100%',
	    })		
	}
	
	//----------------------------------------
	
	, createCustomHierarchiesGridStore: function(dimension){
		var baseParams = {}
		baseParams.dimension = dimension;
		
		
		this.services["getCustomHierarchies"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/getCustomHierarchies',
			baseParams: baseParams
		});
		
		var gridStore = new Ext.data.Store({
	        storeId: 'customHierarchiesStore',
	        fields: ['HIERARCHY_NM', 'HIERARCHY_TP'],
	        proxy: {
	            type: 'ajax',
	            url: this.services["getCustomHierarchies"],
	            reader: {
	                type: 'json'
	            }
	        }
	        
	    });
		
		gridStore.load();
		
		return gridStore;
	}
	
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
	
	, createHierarchiesComboStore: function(dimension){
		Ext.define("HierarchiesModel", {
    		extend: 'Ext.data.Model',
            fields: ["HIERARCHY_NM"]
    	});
		
		var baseParams = {}
		baseParams.dimension = dimension;
		
		this.services["getHierarchiesOfDimension"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/hierarchiesOfDimension',
			baseParams: baseParams
		});
		
		var hierarchiesStore=  Ext.create('Ext.data.Store',{
    		model: "HierarchiesModel",
    		proxy: {
    			type: 'ajax',
    			url:  this.services['getHierarchiesOfDimension'],
    			reader: {
    				type:"json"
    			}
    		}
    	});
		hierarchiesStore.load();
    	
    	return hierarchiesStore;		
	}
	
	, createAutomaticHierarchyTreeStore: function(dimension, hierarchy){
		var baseParams = {}
		baseParams.dimension = dimension;
		baseParams.hierarchy = hierarchy;

		
		this.services["getAutomaticHierarchyTree"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/getAutomaticHierarchyTree',
			baseParams: baseParams
		});

		var automaticHierarchyTreeStore = new Ext.data.TreeStore({
			model:'Item',
			proxy: {
				type: 'ajax',
				url: this.services["getAutomaticHierarchyTree"],
				reader: {
					type: 'json'
				}
			}
			,autoload:true
		});
		return automaticHierarchyTreeStore;
		
	}	
	
	, createCustomHierarchyTreeStore: function(dimension, hierarchy){
		var baseParams = {}
		baseParams.dimension = dimension;
		baseParams.hierarchy = hierarchy;

		
		this.services["getCustomHierarchyTree"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/getCustomHierarchyTree',
			baseParams: baseParams
		});

		var customHierarchyTreeStore = new Ext.data.TreeStore({
			model:'Item',
			proxy: {
				type: 'ajax',
				url: this.services["getCustomHierarchyTree"],
				reader: {
					type: 'json'
				}
			}
			,autoload:true
			
		});
		return customHierarchyTreeStore;
		
	}	

	/**************************************
	 * Private methods
	 **************************************/
	
	, disableGUIElements: function(){
		//Disable some GUI elements when editing a custom hierarchy to prevent inconsistency
		this.comboHierarchies.setDisabled(true);
		this.comboDimensions.setDisabled(true);
		this.newCustomHierarchyTypeCombo.setDisabled(true);
		this.newCustomHierarchyButton.setDisabled(true);
	}
	
	, enableGUIElements: function(){
		//Enable some GUI elements after custom hierarchy save or cancel
		this.comboHierarchies.setDisabled(false);
		this.comboDimensions.setDisabled(false);
		this.newCustomHierarchyTypeCombo.setDisabled(false);
		this.newCustomHierarchyButton.setDisabled(false);

	}
	
	, saveCustomHierarchy: function(){		
		this.customHierarchyName = new Ext.form.Text({
			name: 'name',
	        fieldLabel: 'Name',
	        labelWidth: 130,
			width : 300,
	        allowBlank: false
		});
		
	    this.scopeComboStore = new Ext.data.Store({
	        fields: ['type', 'name'],
	        data: [{
	            "type": "ALL",
	            "name": "All"
	        }, {
	            "type": "OWNER",
	            "name": "Owner"
	        }, {
	            "type": "POWER",
	            "name": "Power"
	        }]
	    });
		
		this.scopeCombo = new Ext.form.ComboBox({
	        fieldLabel: 'Hierarchy Scope',
	        store: this.scopeComboStore,
	        queryMode: 'local',
	        displayField : 'name',
	        valueField: 'type',
	        labelWidth: 130,
			width : 300,
			typeAhead : true,
			triggerAction : 'all',
			editable : false,
	    });
		//select first value by default
		this.scopeCombo.select(this.scopeCombo.getStore().getAt(0));

		var win = new Ext.Window(
			    {
			        layout: 'fit',
			        width: 500,
			        height: 300,
			        modal: true,
			        closeAction: 'destroy',
			        title:'Save Custom Hierarchy',
			        items: new Ext.Panel(
			        {
						
						bodyStyle:'padding:20px',
			        	items: [this.customHierarchyName,this.scopeCombo,]
			        }),
			        buttons:[
			                 {
			                	 text:'OK',
			                	 handler:function() {
			                		 var customTreePanel = Ext.getCmp('customTreePanel');
			                		 var myStore = customTreePanel.getStore();        	
			                		 var rootNode =  myStore.getRootNode();
			                		 var myJson= this.getJson(rootNode);
			                		 
			                		 var params = {};
			                		 params.root = Ext.encode(myJson);
			                		 params.name = this.customHierarchyName.getValue();
			                		 params.scope = this.scopeCombo.getValue();
			                		 
			                		 //Call ajax function
			                			Ext.Ajax.request({
			                				url: this.services["saveCustomHierarchy"],
			                				params: params,			
			                				success : function(response, options) {				
			                					if(response !== undefined  && response.responseText !== undefined && response.statusText=="OK") {
			                						if(response.responseText!=null && response.responseText!=undefined){
			                							if(response.responseText.indexOf("error.mesage.description")>=0){
			                								
			                								Sbi.exception.ExceptionHandler.handleFailure(response);
			                							}else{			
			                								alert("saved");
			                							}
			                						}
			                					} else {
			                						Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
			                					}
			                				},
			                				scope: this,
			                				failure: Sbi.exception.ExceptionHandler.handleFailure      
			                			});
			                		 
			                		 
			                		 
			                		 console.log(Ext.encode(myJson));
			                		 win.close();
			                	 }
			                 	 ,scope:this 
			                 },
			                 {
			                	 text:'Cancel',
			                	 handler:function() {
			                		 win.close();
			                	 }
			                 }
			      ]
			    });
		win.show();
	}
	
	, cancelCustomHierarchy: function(){
		alert("TODO: cancel current hierarchy editing process");
		if ((Ext.getCmp('customTreePanel') != null) & (Ext.getCmp('customTreePanel') != undefined)){
			this.rightPanel.remove(Ext.getCmp('customTreePanel'));
		}
		if ((Ext.getCmp('customTreePanelTemp') != null) & (Ext.getCmp('customTreePanelTemp') != undefined)){
			this.rightPanel.remove(Ext.getCmp('customTreePanelTemp'));
		}

		//Reload current Automatic Hierarchy selected
		var hierarchy = this.comboHierarchies.getValue();
		var dimension = this.comboDimensions.getValue();
		this.automaticHierarchiesTreeStore = this.createAutomaticHierarchyTreeStore(dimension, hierarchy);
		this.leftPanel.remove(Ext.getCmp('automaticTreePanel'));
		var myTreePanel = this.createTreePanel(this.automaticHierarchiesTreeStore);
		this.leftPanel.add(myTreePanel);
		myTreePanel.expandAll();

	}
	
	, createCustomHierarchyEmptyPanel: function(){
		   
		   if ((Ext.getCmp('customTreePanel') != null) & (Ext.getCmp('customTreePanel') != undefined)){
        	  	this.rightPanel.remove(Ext.getCmp('customTreePanel'));
		   }
		   
		   if ((Ext.getCmp('customTreePanelTemp') != null) & (Ext.getCmp('customTreePanelTemp') != undefined)){
			   this.rightPanel.remove(Ext.getCmp('customTreePanelTemp'));
		   }

		   //TreePanel initialized as simple panel for tree creation
		   this.treePanelRight = Ext.create('Ext.panel.Panel', {
			   id: 'customTreePanelTemp',
			   layout: {
				   type: 'vbox',
				   align: 'center',
				   pack: 'center'
			   },
			   frame: false,
			   border:true,
			   height: 200,

			   items: [
			           {
			        	   xtype: 'label',
			        	   html: '<b>Drag here the root of the new hierarchy</b>'
			           }
			   ]
			   ,listeners: {
				   'afterrender': function () {
					   var customTreePanelTempDropTarget = new Ext.dd.DropTarget(thisPanel.treePanelRight.getEl(), {
						   ddGroup    : 'DDhierarchiesTrees',
						   copy       : false,
						   notifyDrop : function(ddSource, e, data){
							   console.log('drop');
							   var droppedNode = data.records[0];
							   if (!droppedNode.isLeaf()){
								   var store = new Ext.data.TreeStore();
								   var nodeClone = thisPanel.cloneNode(droppedNode); 

								   store.setRootNode(nodeClone);
								   var customTreePanel = thisPanel.createCustomTreePanel(store,true);
								   thisPanel.rightPanel.add(customTreePanel);	
								   //remove nodes from the original tree source (avoid duplicates)
								   data.records[0].remove(); 
								   Ext.getCmp('customTreePanelTemp').setVisible(false);
								   return true;
							   } else {
								   Ext.Msg.alert('Wrong Action', 'Cannot use a leaf node as a new root');
							   }

							   return false;

						   }
					   }); 

				   }
			   }           
		   });		


		   
		   this.rightPanel.add(this.treePanelRight);
	}
	 
	, onShowCustomHierarchyTree: function(selectedRecord){
		var hierarchyName = selectedRecord.get('HIERARCHY_NM');
		var dimensionName = this.comboDimensions.getValue();
		
		var customTreeStore = this.createCustomHierarchyTreeStore(dimensionName,hierarchyName)
  	  	this.rightPanel.remove(Ext.getCmp('customTreePanel'));
		var customTreePanel = this.createCustomTreePanel(customTreeStore,false);
		this.rightPanel.add(customTreePanel);
		customTreePanel.expandAll();

	}
	
	, createCustomTreePanel: function(store,rootVisible){
		return new Ext.tree.Panel({
	        id: 'customTreePanel',
	        layout: 'fit',
	        store: store,
	        rootVisible: rootVisible,
	        frame: false,
	        border:false,
	        bodyStyle: {border:0},
	        bodyStyle:'padding:20px',
	        viewConfig: {
	         plugins: {
	               ptype: 'treeviewdragdrop',
	               ddGroup: 'DDhierarchiesTrees',
	               enableDrag: true,
	               enableDrop: true
	         }
	        ,listeners: {           	
	        	//listeners for drag & drop management           	
	        	viewready: function (tree) {                   
	        		tree.plugins[0].dropZone.notifyDrop = function(dd, e, data){
	        			var customHierarchyType = thisPanel.newCustomHierarchyTypeCombo.getValue();
	        			var isLeafNode = data.records[0].isLeaf();
	        			if ((customHierarchyType != "SEMIMANUAL") || (!isLeafNode) ){
	        				if(this.lastOverNode){
	        					this.onNodeOut(this.lastOverNode, dd, e, data);
	        					this.lastOverNode = null;
	        				}
	        				var n = this.getTargetFromEvent(e);
	        				return n ?
	        						this.onNodeDrop(n, dd, e, data) :
	        							this.onContainerDrop(dd, e, data);
	        			} else {
	        				Ext.Msg.alert('Wrong Action', 'Cannot drop a leaf node');
	        				return false;
	        			}
	        		}	
	        	},
	        	beforedrop: function(node, data, overModel, dropPosition, dropFunction, options) {
	        		alert("BeforeDrop!");
	        	}/*
                , beforedrop: {
	                fn: this.onBeforeDropRightTree,

	                scope: this
	            }*/
	        }	        
	        }
		});
			
	}
	
	, createTreePanel: function(store){
		var automaticTree = new Ext.tree.Panel({
	        id: 'automaticTreePanel',
	        layout: 'fit',
	        store: store,
	        rootVisible: false,
	        frame: false,
	        border:false,
	        bodyStyle: {border:0},
	        bodyStyle:'padding:20px',
	        viewConfig: {
	            plugins: {
	               ptype: 'treeviewdragdrop',
	               ddGroup: 'DDhierarchiesTrees',
	               enableDrag: true,
	               enableDrop: false,
	               copy: false
	            }
	        }
	    });
		return automaticTree;
			
	}
	
	//clone a Ext.data.NodeInterface with deep copy
	, cloneNode: function(node) {
		var result = node.copy(),
		len = node.childNodes ? node.childNodes.length : 0,
				i;
		// Move child nodes across to the copy if required
		for (i = 0; i < len; i++)
			result.appendChild(this.cloneNode(node.childNodes[i]));
		return result;
	}
	
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
	
	/***********************************
	 * REST services for Ajax calls
	 ***********************************/
	, initServices : function(baseParams) {
		this.services = [];
		
		if(baseParams == undefined){
			baseParams ={};
		}
		
		this.services["getDimensions"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/dimensions',
			baseParams: baseParams
		});
		
		this.services["getHierarchiesOfDimension"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/hierarchiesOfDimension',
			baseParams: baseParams //must specify a dimension parameter
		});
		
		this.services["getAutomaticHierarchyTree"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/getAutomaticHierarchyTree',
			baseParams: baseParams //must specify a dimension and hierarchy parameters
		});
		
		this.services["getCustomHierarchies"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/getCustomHierarchies',
			baseParams: baseParams //must specify a dimension parameter
		});
		
		this.services["getCustomHierarchyTree"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/getCustomHierarchyTree',
			baseParams: baseParams //must specify a dimension and hierarchy parameters
		});
		
		this.services["saveCustomHierarchy"]= Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName: 'hierarchies/saveCustomHierarchy',
			baseParams: baseParams //must specify a dimension parameter
		});
		
		
	}	


	



	
});

