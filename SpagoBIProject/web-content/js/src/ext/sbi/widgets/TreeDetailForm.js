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

Ext.ns("Sbi.widgets");

Sbi.widgets.TreeDetailForm = function(config) {

	var conf = config.configurationObject;
	this.services = new Array();
	this.services['manageTreeService'] = conf.manageTreeService;
	this.services['saveTreeService'] = conf.saveTreeService;
	this.services['deleteTreeService'] = conf.deleteTreeService;
	
	this.tabItems = conf.tabItems;

	this.treeTitle = conf.treeTitle;

	this.initWidget();
	this.initContextMenu();
	

	var c = Ext.apply( {}, config, this.gridForm);

	Sbi.widgets.TreeDetailForm.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.widgets.TreeDetailForm, Ext.FormPanel, {

	gridForm : null,
	tabs : null,
	tabItems : null,
	treeLoader : null,
	rootNode : null,
	rootNodeId : null,
	preloadTree : true,
	rootNodeText : null,
	treeTitle : null,
	menu : null,
	
	nodesToSave : new Array(),
	selectedNodeToEdit : null,
	
	initContextMenu : function() {

		this.menu = new Ext.menu.Menu( {
			id : 'actions',
			items : [
			// ACID operations on nodes
					'-', {
						text : 'Add Model Node',
						iconCls : 'icon-add',
						handler : function() {
							this.addNewItem(this.ctxNode);
						},
						scope : this
					}, {
						text : 'Remove Model Node',
						iconCls : 'icon-remove',
						handler : function() {
							this.deleteItem(this.ctxNode);
						},
						scope : this
					} ]
		});

	},
	initWidget : function() {

		this.tbSave = new Ext.Toolbar( {
			buttonAlign : 'right',
			items : [ new Ext.Toolbar.Button( {
				text : LN('sbi.generic.update'),
				iconCls : 'icon-save',
				handler : this.save,
				width : 30,
				id : 'save-btn',
				scope : this
			}) ]
		});

		this.tabs = new Ext.TabPanel( {
			enableTabScroll : true,
			id : 'tab-panel1',
			activeTab : 0,
			columnWidth : 0.6,
			autoScroll : true,
			width : 450,
			height : 490,
			itemId : 'tabs',
			items : this.tabItems
		});

		this.mainTree = new Ext.tree.TreePanel( {
			id:'model-maintree',
			title : this.treeTitle,
			width : 250,
			height : 230,
			userArrows : true,
			animate : true,
			autoScroll : true,			
			loader: new Ext.tree.TreeLoader({
				dataUrl: this.services['manageTreeService'],
		        createNode: function(attr) {
					//alert(Ext.util.JSON.encode(attr));

		            if (attr.modelId) {
		                attr.id = attr.modelId;
		            }

		    		if (attr.kpi !== undefined && attr.kpi != null
		    				&& attr.kpi != '') {
		    			attr.iconCls = 'has-kpi';
		    		}
		    		if (attr.error !== undefined && attr.error != false) {
		    			attr.cls = 'has-error';
		    		}
		            return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
		        }

			}),

			preloadTree : this.preloadTree,
			enableDD : true,
            enableDrop: true,
            ddAppendOnly: false ,
			scope : this,
			shadow : true,
			tbar : this.tbSave,
			root : {
				nodeType : 'async',
				text : this.rootNodeText,
				modelId : this.rootNodeId,
				id:  this.rootNodeId
			}
		   ,listeners:{
			   // create nodes based on data from grid
			   beforenodedrop:{
			   	   fn:function(e) {

					   // e.data.selections is the array of selected records
					   if(Ext.isArray(e.data.selections)) {					    
						   // reset cancel flag
						   e.cancel = false;						    
						   // setup dropNode (it can be array of nodes)
						   e.dropNode = [];
						   var r;
						   for(var i = 0; i < e.data.selections.length; i++) {
						    
							   // get record from selectons
							   r = e.data.selections[i];
							  /*    * tree - The TreePanel
								    * target - The node being targeted for the drop
								    * data - The drag data from the drag source
								    * point - The point of the drop - append, above or below
								    * source - The drag source
								    * rawEvent - Raw mouse event
								    * dropNode - Drop node(s) provided by the source OR you can supply node(s) to be inserted by setting them on this object.
								    * cancel - Set this to true to cancel the drop.
								    * dropStatus - If the default drop action is cancelled but the drop is valid, setting this to true will prevent the animated 'repair' from appearing.
								*/  
							   e.target.allowChildren = true;
							   var parent = e.target;
							   if(e.target.attributes.modelId == null || e.target.attributes.modelId === undefined){
								   //drop forbidden!
								   alert("Parent undefined: drop forbidden");
								   return false;
							   }
							   var idxNodeType = this.typesStore.find('domainCd', 'MODEL_NODE');			
							   var recDomain = this.typesStore.getAt(idxNodeType);	
							   var newNode = this.mainTree.getLoader().createNode({
								   kpi: r.get('name')
								   , kpiId: r.get('id')
								   , text: r.get('name')
								   , parentId: e.target.attributes.modelId
								   , type: recDomain.get('typeCd')
								   , typeId: recDomain.get('typeId')
								   , typeDescr: recDomain.get('typeDs')
								   , leaf: false
							   });
							   
							   // create node from record data
							   e.dropNode.push(newNode);
							   
						   }
					    
						   // we want Ext to complete the drop, thus return true
						   return true;
					   }
			    
				   // if we get here the drop is automatically cancelled by Ext
				   }//end fn
				   , scope:this
		   		}
		   }
		});
		
		this.mainTree.on('contextmenu', this.onContextMenu, this);

		/*
		 * Here is where we create the Form
		 */
		this.gridForm = new Ext.FormPanel( {
			id : 'model-detail-formpan',
			frame : true,
			autoScroll : true,
			labelAlign : 'left',
			title : this.panelTitle,
			width : 600,
			height : 550,
			layout : 'border',
			layoutConfig : {
				animate : true,
				activeOnTop : false

			},
			trackResetOnLoad : true,
			items : [ {
				region : 'west',
				collapseMode : 'mini',
				layout : 'fit',
				width : 300,
				items : this.mainTree
			}, {
				border : false,
				frame : false,
				collapseMode : 'mini',
				split : true,
				region : 'center',
				layout : 'fit',
				items : this.tabs
			} ]
		});
		this.setListeners();

	},
	save : function() {
		alert("Overridden");
		
	},
	createNewRootNode: function() {
		var node = new Ext.tree.AsyncTreeNode({
	        text		: '... - ...',
	        expanded	: false,
	        leaf		: false,	        
	        draggable	: false
	    });
		return node;
	},

	fillDetail : function(sel, node) {
		alert("override");
	},
	renderTree : function(tree) {
		alert("override");
	},

	editNode : function(field, newVal, oldVal) {
		alert("override");
	},

	addNewItem : function(parent) {
		var idxNodeType = this.typesStore.find('domainCd', 'MODEL_NODE');			
		var recDomain = this.typesStore.getAt(idxNodeType);	
		
		if (parent === undefined || parent == null) {
			alert("Select parent node");
			return;
		} else {
			parent.leaf = false;
		}
		var parentId = parent.attributes.modelId;

		//if parent is newly created --> confirm
		if(parentId == null || parentId == undefined){
			Ext.MessageBox.confirm(
				LN('sbi.generic.pleaseConfirm'),
				'Per inserire il nodo bisogna prima salvare il nodo padre. Effettuare salvataggio?',            
	            function(btn, text) {
	                if (btn=='yes') {
	                	//save parent
	                	
	                	this.mainTree.getLoader().load(parent);
	                	var newparentId = parent.attributes.modelId;

	        	    	//then create child node
	        			var node = new Ext.tree.TreeNode( {
	        				text : '... - ...',
	        				leaf : false,
	        				parentId: newparentId,
	        				type: recDomain.get('typeCd'),
	        				typeId: recDomain.get('typeId'),
	        				typeDescr: recDomain.get('typeDs'),
	        				toSave :false,
	        				allowDrag : false
	        			});
	        			//save parent
	                	this.saveParentNode(parent, node);
	                	
	        			this.mainTree.render();
	        			if (!parent.isExpanded()) {
	        				parent.expand(false, /*no anim*/false);
	        			}
	        			this.mainTree.render();
	        			parent.appendChild(node);
	        	
	        			this.mainTree.getSelectionModel().select(node);
	        			
	                }else{
	                	//exit
	                	return;
	                }
	            },
	            this
			);
			return null;
		}else{

	    	//then create child node
			var node = new Ext.tree.TreeNode( {
				text : '... - ...',
				leaf : false,
				parentId: parentId,
				toSave :false,
				type: recDomain.get('typeCd'),
				typeId: recDomain.get('typeId'),
				typeDescr: recDomain.get('typeDs'),
				allowDrag : false
			});
			this.mainTree.render();
			if (!parent.isExpanded()) {
				parent.expand(false, /*no anim*/false);
			}
			this.mainTree.render();
			parent.appendChild(node);
	
			this.selectedNodeToEdit = node;
			this.mainTree.getSelectionModel().select(node);
			return node;
		}

	},
	deleteItem : function(node) {
		
		if (node === undefined || node == null) {
			alert("Select node to delete");
			return;
		}
		
		Ext.MessageBox.confirm(
				LN('sbi.generic.pleaseConfirm'),
				LN('sbi.generic.confirmDelete'),            
	            function(btn, text) {
	                if (btn=='yes') {
	                	if (node != null) {	
							Ext.Ajax.request({
					            url: this.services['deleteTreeService'],
					            params: {'modelId': node.attributes.modelId},
					            method: 'GET',
					            success: function(response, options) {
									if (response !== undefined) {
										this.mainTree.getSelectionModel().clearSelections(false);
										node.remove();
										//this.mainTree.doLayout();
									} else {
										Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.deletingItemError'), LN('sbi.generic.serviceError'));
									}
					            },
					            failure: function() {
					                Ext.MessageBox.show({
					                    title: LN('sbi.generic.error'),
					                    msg: LN('sbi.generic.deletingItemError'),
					                    width: 150,
					                    buttons: Ext.MessageBox.OK
					               });
					            }
					            ,scope: this
				
							});
						} else {
							Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.generic.error.msg'),LN('sbi.generic.warning'));
						}
	                }
	            },
	            this
			);

		
	},
	onContextMenu : function(node, e) {
		if (this.menu == null) { // create context menu on first right click
				this.initContextMenu();
		}
		
		if (this.ctxNode && this.ctxNode.ui) {
			this.ctxNode.ui.removeClass('x-node-ctx');
			this.ctxNode = null;
		}
		
		this.ctxNode = node;
		this.ctxNode.ui.addClass('x-node-ctx');
		this.menu.showAt(e.getXY());
	
	}

});

