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
  * - Andrea Gioia (adrea.gioia@eng.it)
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.QueryCataloguePanel = function(config) {
	var c = Ext.apply({
		// set default values here
	}, config || {});
	
	this.services = new Array();
	var params = {};
	this.services['getCatalogue'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_CATALOGUE_ACTION'
		, baseParams: params
	});
	
	this.services['setCatalogue'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'SET_CATALOGUE_ACTION'
		, baseParams: params
	});
	
	this.services['validateCatalogue'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'VALIDATE_CATALOGUE_ACTION'
		, baseParams: params
	});
	
	this.services['addQuery'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'ADD_QUERY_ACTION'
		, baseParams: params
	});
	
	this.services['deleteQueries'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'DELETE_QUERIES_ACTION'
		, baseParams: params
	});
	
	
	this.addEvents('beforeselect');
	
	this.initTree(c);
	
	Ext.apply(c, {
		layout: 'fit'
		, border:false
		, autoScroll: true
		, containerScroll: true
		, items: [this.tree]
	});
	
	
	// constructor
	Sbi.qbe.QueryCataloguePanel.superclass.constructor.call(this, c);
    
    
};

Ext.extend(Sbi.qbe.QueryCataloguePanel, Ext.Panel, {
    
	services: null
	, treeSelectionModel: null
	, treeLoader: null
	, rootNode: null
	, tree: null
	, type: 'querycataloguetree'
	
	// public methods
	
	, load: function() {
		this.treeLoader.load(this.rootNode, function(){});
	}

	, commit: function(callback, scope) {
		
		var params = {
				catalogue: Ext.util.JSON.encode(this.getQueries())
		};

		Ext.Ajax.request({
		    url: this.services['setCatalogue'],
		    success: callback,
		    failure: Sbi.exception.ExceptionHandler.handleFailure,	
		    scope: scope,
		    params: params
		});   
	}
	
	, validate: function(callback, scope) {
		var params = {};
		Ext.Ajax.request({
		    url: this.services['validateCatalogue'],
		    success: callback,
		    failure: Sbi.exception.ExceptionHandler.handleFailure,	
		    scope: scope,
		    params: params
		});   
	}
	
	, addQuery: function(query) {
		var queryItem;
		if(query) queryItem = {query: query};
		this.addQueryItem(queryItem);
	}
	
	, insertQuery: function(parentQuery) {
		var parentQueryItem;
		if(parentQuery) parentQueryItem = {query: parentQuery};
		this.insertQueryItem(parentQueryItem);
	}

	, setQuery: function(queryItemId, query) {
		var oldQuery;
		var item = this.getQueryItemById(queryItemId);
		if(item) {
			oldQuery = item.query;
			item.query = query;
		}
		
		return oldQuery;
	}
	
	, getQueries: function() {
		var queries = [];
    	if( this.rootNode.childNodes && this.rootNode.childNodes.length > 0 ) {
			for(var i = 0; i < this.rootNode.childNodes.length; i++) {
				queries.push( this.getQueryById(this.rootNode.childNodes[i].id) );
			}
		}
    	
    	return queries;
	}
	
	, getQueryById: function(queryId) {
		var query;
		var queryNode = this.tree.getNodeById(queryId);
		
		if(queryNode) {
			
			query = queryNode.props.query;
			query.name = queryNode.text;
			query.subqueries = [];
			if( queryNode.childNodes && queryNode.childNodes.length > 0 ) {
				for(var i = 0; i < queryNode.childNodes.length; i++) {
					var subquery = this.getQueryById( queryNode.childNodes[i].id );
					query.subqueries.push( subquery );
				}
			}
		}
		
		return query;
	}
	
	, getParentQuery: function(queryId) {
		var query = null;
		var queryNode = this.tree.getNodeById(queryId);
		if(queryNode) {
			var parentQueryNode = queryNode.parentNode;
			if(parentQueryNode && parentQueryNode.id !== this.rootNode.id) {
				query = this.getQueryById(parentQueryNode.id);
			}
		}
		return query;
	}
	
	, getSelectedQuery: function() {
		var queryItem = this.getSelectedQueryItem();
		return queryItem? queryItem.query: undefined;
	}
	
	
	, deleteQueries: function(queries) {
		this.deleteQueryItems(queries);
	}
	
	
	// PRIVATE:  item level
	
	, getQueryItems: function() {
		var queryItems = [];
    	if( this.rootNode.childNodes && this.rootNode.childNodes.length > 0 ) {
			for(var i = 0; i < this.rootNode.childNodes.length; i++) {
				queryItems.push( this.getQueryItemById(this.rootNode.childNodes[i].id) );
			}
		}
    	
    	return queryItems;
	}

	, getQueryItemById: function(queryId) {
		var queryItem;
		var queryNode = this.tree.getNodeById(queryId);
		
		if(queryNode) {
			
			queryItem = queryNode.props;
			queryItem.subqueries = [];
			if( queryNode.childNodes && queryNode.childNodes.length > 0 ) {
				for(var i = 0; i < queryNode.childNodes.length; i++) {
					var subquery = this.getQueryItemById( queryNode.childNodes[i].id );
					queryItem.subqueries.push( subquery );
				}
			}
		}
		
		return queryItem;
	}
	
	, getSelectedQueryItem: function() {
		var queryNode = this.tree.getSelectionModel().getSelectedNode();
		return queryNode? queryNode.props: undefined;
	}
	
	, addQueryItem: function(queryItem) {
		this.insertQueryItem(this.rootNode.id, queryItem);
	}
	
	

	, insertQueryItem: function(parentQueryItem, queryItem) {
		var nodeId = (typeof parentQueryItem === 'string')? parentQueryItem: parentQueryItem.query.id;
		var parentQueryNode = this.tree.getNodeById(nodeId);
		 
		if(!queryItem) {
			this.createQueryNode(this.insertQueryNode.createDelegate(this, [parentQueryNode], 0), this);
		} else {
			 var queryNode = {
			    id: queryItem.query.id
			   	, text: queryItem.query.id
			   	, leaf: true
			   	, props: {
			 		query: queryItem.query
			 		, iconCls: 'icon-query'
			    }
			 };
			 this.insertQueryNode(parentQueryNode, queryNode);			 
		}
	}
	
	, deleteQueryItems: function(queries) {
		this.deleteQueryNodes(queries);
	}
	
	
	
	// PRIVATE:  node level
	
	, addQueryNode: function(queryNode) {
		this.insertQueryNode(this.rootNode, queryNode);
	}
	
	, insertQueryNode: function(parentQueryNode, queryNode) {
		if(!queryNode) {
			this.createQueryNode(this.insertQueryNode.createDelegate(this, [parentQueryNode], 0), this);
		} else {			
			parentQueryNode.leaf = false;					
			parentQueryNode.appendChild( queryNode );
			parentQueryNode.expand();
			queryNode.select();		
			
			var te = this.treeEditor;
			var edit = function(){
                te.editNode = queryNode;
                te.startEdit(queryNode.ui.textNode);
            };
			setTimeout(edit, 10);
		}
	}
	
	, createQueryNode: function(callback, scope) {
		Ext.Ajax.request({
		   	url: this.services['addQuery'],
		   	success: function(response, options) {
    			if(response !== undefined && response.responseText !== undefined) {
					var content = Ext.util.JSON.decode( response.responseText );
					var queryNode = new Ext.tree.TreeNode(content);
					queryNode.props = content.attributes;
					callback.call(scope, queryNode);
				} else {
			      	Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
			    }     					
   			},
   			failure: Sbi.exception.ExceptionHandler.handleFailure,
   			scope: this
		});	   
	}
	
	, deleteQueryNodes: function(queries, callback, scope) {
		var p;
    	if(queries) {
    		if( !(queries instanceof Array) ) {
    			queries = [queries];
    		}
    		
    		for(var i = 0, p = []; i < queries.length; i++) {
    			var query = queries[i];
    			if(typeof query === 'string') {
    				p.push( query );
    			} else if(typeof query === 'object') {
    				p.push( query.id || query.query.id );
    			} else {
    				alert('Invalid type [' + (typeof query) + '] for object query in function [deleteQueries]');
    			}
    		}
    		// don't let to erase the root query
    		if(p == 'q1'){
				Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.qbe.queryeditor.eastregion.tools.wanringEraseRoot'), 'Warning');
    			return;	
    		}
    		
			Ext.Ajax.request({
			   	url: this.services['deleteQueries'],
			   	params: {queries: Ext.util.JSON.encode(p)},
			
			   	success: function(response, options) {
			   		var q = Ext.util.JSON.decode( options.params.queries );
			   		for(var i = 0; i < q.length; i++) {
			   			var node = this.tree.getNodeById(q[i]);
			   			node.remove();
			   		}
			   		
			   		if(callback) callback.call(scope, q);
	   			},
	   			failure: Sbi.exception.ExceptionHandler.handleFailure,
	   			scope: this
			});	
    	}
	}
		

	// private methods
	
	, initTree: function(config) {
		
		this.treeLoader = new Ext.tree.TreeLoader({
	        dataUrl: this.services['getCatalogue']
	    });
		// redefine createnode function in order to disable node expansion on dblclick
		this.treeLoader.createNode = function(attr){
	        // apply baseAttrs, nice idea Corey!
	        if(this.baseAttrs){
	            Ext.applyIf(attr, this.baseAttrs);
	        }
	        if(this.applyLoader !== false){
	            attr.loader = this;
	        }
	        if(typeof attr.uiProvider == 'string'){
	           attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
	        }
	        
	     
	        var resultNode;
	        if(attr.leaf) {
	        	resultNode = new Ext.tree.TreeNode(attr);
	        	resultNode.props = attr.attributes;
	        } else {
	        	resultNode = new Ext.tree.AsyncTreeNode(attr);
	        	resultNode.props = attr.attributes;
	        	//resultNode.attributes = attr.attributes;
	        }
	        
	        resultNode.getUI().onDblClick = function(e){
	            e.preventDefault();
	            if(this.disabled){
	                return;
	            }
	            if(this.checkbox){
	                this.toggleCheck();
	            }
	            
	            this.fireEvent("dblclick", this.node, e);
	        };
	        
	        return resultNode;
	    };
	    
	   
		
		this.treeSelectionModel = new Ext.tree.DefaultSelectionModel({
			init : function(tree){
		        this.tree = tree;
		        tree.on("dblclick", this.onNodeDbClick, this);
	    	},
	    
	    	onNodeDbClick : function(node, e){
	    		this.select(node);
	    	}
		});
				
		this.rootNode = new Ext.tree.AsyncTreeNode({
	        text		: 'Queries',
	        iconCls		: 'database',
	        expanded	: true,
	        draggable	: false
	    });
		
		this.tree = new Ext.tree.TreePanel({
	        collapsible: false,
	        
	        enableDD: true,	        
	        ddGroup: 'gridDDGroup',
	        dropConfig: {
				ddGroup: 'gridDDGroup',
				// avoid in tree drop
				isValidDropPoint : function(n, pt, dd, e, data){
					return false;
				}      
	      	},
	      	
	      	dragConfig: {
	      		// if dragConfig in set the ddGroup is taken from there and not from the tree
	      		// so if not defined there the defaut one will be used : 'treeDD'
	      		ddGroup: 'gridDDGroup', 
	      		onInitDrag : function(e){
		            var data = this.dragData;
		            // when start a new drag we do not want to select the dragged node
		            //this.tree.getSelectionModel().select(data.node);
		            this.tree.eventModel.disable();
		            this.proxy.update("");
		            data.node.ui.appendDDGhost(this.proxy.ghost.dom);
		            this.tree.fireEvent("startdrag", this.tree, data.node, e);
	      		}
	      		
	      		, beforeInvalidDrop : function(e, id){
	      	        // when a drop fails we do not want to select the dragged node
	      	        //var sm = this.tree.getSelectionModel();
	      	        //sm.clearSelections();
	      	        //sm.select(this.dragData.node);
	      	    }
	      	}, 
	      	
	      	
	        animCollapse     : true,
	        collapseFirst	 : false,
	        border           : false,
	        autoScroll       : true,
	        containerScroll  : true,
	        animate          : false,
	        trackMouseOver 	 : true,
	        useArrows 		 : true,
	        selModel		 : this.treeSelectionModel,
	        loader           : this.treeLoader,
	        root 			 : this.rootNode
	    });	
		
		// defines the tree sorting
		new Ext.tree.TreeSorter(this.tree, {
		    folderSort: true
		    , dir: 'asc'
		    , property: 'id'
		});
		
		this.tree.type = this.type;
		
		/*
		this.tree.on('startdrag', function(tree, node, e) {
			alert(tree.dragZone.ddGroup);
		}, this);
		*/
		
		// add an inline editor for the nodes
	    this.treeEditor = new Ext.tree.TreeEditor(this.tree, {/* fieldconfig here */ }, {
	        allowBlank:false,
	        blankText:'A name is required',
	        selectOnFocus:true
	    });
	    // we do not want editing to start after node clicking
	    //this.treeEditor.beforeNodeClick = Ext.emptyFn;

		
		this.tree.getSelectionModel().on('beforeselect', this.onSelect, this);
		this.treeLoader.on('load', this.onLoad, this);
	}
	
	, onLoad: function(loader, node, response) {
		node.expandChildNodes();
		     
		if( node.childNodes && node.childNodes.length > 0 ) {
			this.tree.getSelectionModel().select( node.childNodes[0] );
		}
	}
	
	, onSelect: function(sm, newnode, oldnode) {
		var allowSelection = true;
		
		if(newnode.id !== this.rootNode.id) {
			var oldquery = oldnode?  oldnode.props.query: undefined;
			var b = this.fireEvent('beforeselect', this, newnode.props.query, oldquery);
			if(b === false) allowSelection = b;
		} else {
			allowSelection = false;
		}
		
		return allowSelection;
	}
	
});