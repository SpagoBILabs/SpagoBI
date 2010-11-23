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
Ext.ns("Sbi.widgets");

Sbi.widgets.ConfigurableTree = function(config) { 
	
	this.treeLoader = config.treeLoader;
	
    var c =  {
    	border: false,
		autoWidth : true,
		height : 300,
		layout: 'fit',
		userArrows : true,
		animate : true,
		autoScroll : true,		
        style: {
            "border":"none"
        },
		loader: this.treeLoader,

		preloadTree : this.preloadTree,
		enableDD : true,
        enableDrop: false,
        enableDrag: true,
        ddAppendOnly: false ,
        ddGroup  : 'tree2tree',
		scope : this,
		shadow : true,
		root : {
			nodeType : 'async',
			text : '1',
			id:  '1'
		}
	};
    
    Sbi.widgets.ConfigurableTree.superclass.constructor.call(this, c || config);	
    
    this.initWidget();
	
};

Ext.extend(Sbi.widgets.ConfigurableTree, Ext.tree.TreePanel, {
	 treeLoader: null
	
	,initWidget: function(){
		this.initContextMenu();
		this.addListeners();
	}
	
	,addListeners: function(){
		//add the editor
		var field = new Ext.form.TextField();
		var treeEditor = new Ext.tree.TreeEditor(this,field);
		this.on('contextmenu', this.onContextMenu, this);
	}
	
	, addNewItem : function(parent) {
		alert(parent.id);
		if (parent === undefined || parent == null) {
			alert(LN('sbi.models.DDNoParentMsg'));
			return;
		} else {
			parent.leaf = false;
		}
		
		var parentId = parent.attributes.nodeId;

		//if parent is newly created --> confirm
		if(parentId == null || parentId == undefined){
			Ext.MessageBox.confirm(
				LN('sbi.generic.pleaseConfirm'),
				LN('sbi.models.confirmSaveParent'),            
	            function(btn, text) {
	                if (btn=='yes') {
	                	//save parent
	                	
	                	//this.mainTree.getLoader().load(parent);
	                	//var newparentId = parent.attributes.modelId;

	        	    	//then create child node
	        			var node = new Ext.tree.TreeNode( {
	        				text : '...',
	        				leaf : true,
	        				parentId: parentId,
	        				toSave :false,
	        				editable: true
	        			});
	        			//save parent
	                	//this.saveParentNode(parent, node);
	                	
//	        			this.mainTree.render();
//	        			if (!parent.isExpanded()) {
//	        				parent.expand(false, /*no anim*/false);
//	        			}
//	        			this.mainTree.render();
	                	
	        			parent.appendChild(node);
	        	
//	        			this.mainTree.getSelectionModel().select(node);
	        			
	                }else{
	                	//exit
	                	return;
	                }
	            },
	            this
			);
			return null;
		}

	}	
	
	, initContextMenu : function() {

		this.menu = new Ext.menu.Menu( {
			items : [
			// ACID operations on nodes
					'-', {
						text : LN('sbi.models.addNode'),
						iconCls : 'icon-add',
						handler : function() {
							this.addNewItem(this.ctxNode);
						},
						scope : this
					}, {
						text : LN('sbi.models.remodeNode'),
						iconCls : 'icon-remove',
						handler : function() {
							alert('reove');
						},
						scope : this
					} ]
		});

	}
	
	,onContextMenu : function(node, e) {
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


