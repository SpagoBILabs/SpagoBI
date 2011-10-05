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

Sbi.qbe.DataMartStructurePanel = function(config) {
	
	var defaultSettings = {
		title: 'Datamart 1'
		, border:false
		, autoScroll: true
		, containerScroll: true
		, rootNodeText: 'Datamart'
		, ddGroup: 'gridDDGroup'
		, type: 'datamartstructuretree'
		, preloadTree: true
		, baseParams: {}
		, enableTreeContextMenu: false
  	};
	if (Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.dataMartStructurePanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.dataMartStructurePanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
	var params = c.title !== undefined ? {'datamartName': c.title} : {};
	
	this.services = this.services || new Array();	
	this.services['loadTree'] = this.services['loadTree'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_TREE_ACTION'
		, baseParams: params
	});

	this.services['getParameters'] = this.services['getParameters'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETERS_ACTION'
		, baseParams: params
	});
	
	this.services['getAttributes'] = this.services['getAttributes'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_ATTRIBUTES_ACTION'
		, baseParams: params
	});
	
	this.services['addCalculatedField'] = this.services['addCalculatedField'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'ADD_CALCULATED_FIELD_ACTION'
		, baseParams: params
	});
	
	this.services['modifyCalculatedField'] = this.services['modifyCalculatedField'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MODIFY_CALCULATED_FIELD_ACTION'
		, baseParams: params
	});
	
	this.services['deleteCalculatedField'] = this.services['deleteCalculatedField'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'DELETE_CALCULATED_FIELD_ACTION'
		, baseParams: params
	});
	
	
	this.addEvents('load', 'addnodetoselect');
	
	this.initTree(c.treeConfig || {});
		
	Ext.apply(c, {
		layout: 'fit'
		, items: [this.tree]
	});	
	
	// constructor
	Sbi.qbe.DataMartStructurePanel.superclass.constructor.call(this, c);
    
    
};

Ext.extend(Sbi.qbe.DataMartStructurePanel, Ext.Panel, {
    
	services: null
	, treeLoader: null
	, rootNode: null
	, preloadTree: true
	, tree: null
	, type: null
	, pressedNode: null
	, calculatedFieldWizard : null
	, inLineCalculatedFieldWizard : null
	, menu: null
	, CALCULATED_FIELD: 'calculatedField'
	, IN_LINE_CALCULATED_FIELD: 'inLineCalculatedField'
	
	// --------------------------------------------------------------------------------
	// public methods
	// --------------------------------------------------------------------------------
	
	, load: function(params) {
		if(params) {
			this.treeLoader.baseParams = params;
		}
		this.tree.setRootNode(this.createRootNode());
	}

	, expandAll: function() {
		this.tree.expandAll();
	}
	
	, collapseAll: function() {
		this.tree.collapseAll();
	}
	
	, removeCalculatedField:  function(fieldNode) {
		var nodeType;
		nodeType = fieldNode.attributes.type || fieldNode.attributes.attributes.type;
		if(nodeType === 'calculatedField' || nodeType === 'inLineCalculatedField') {
			
			var entityId = fieldNode.parentNode.id;
			var formState;
			if( fieldNode.attributes.attributes!=null){
				formState = fieldNode.attributes.attributes.formState;
			}else{
				formState = fieldNode.attributes.formState;
			}
    		var f = {
    			alias: formState.alias
    			, type: formState.type
    			, calculationDescriptor: formState
    		};
    		
    		var params = {
    			entityId: entityId,
    			nodeId: fieldNode.id,
    			field: Ext.util.JSON.encode(f)
    		}
    		
			Ext.Ajax.request({
				url:  this.services['deleteCalculatedField'],
				success: function(response, options, a) {
					var node = this.tree.getNodeById(options.params.nodeId);
					node.unselect();
		            Ext.fly(node.ui.elNode).ghost('l', {
		                callback: node.remove, scope: node, duration: .4
		            });
       			},
       			scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure,	
				params: params
        	}); 
		} else {
			Ext.Msg.show({
				   title:'Invalid operation',
				   msg: 'Node of type [' + nodeType + '] cannot be deleted',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});
		}
	}
	
	, editField: function(fieldNode) {
		var nodeType;
		nodeType = fieldNode.attributes.type || fieldNode.attributes.attributes.type;
		if(nodeType == 'calculatedField') {
			
			if(this.calculatedFieldWizard === null) {
				this.initCalculatedFieldWizards();
			}
			
			var parentEtityNode = fieldNode.parentNode;
			var fields = new Array();
			for(var i = 0; i < parentEtityNode.attributes.children.length; i++) {
				var child = parentEtityNode.attributes.children[i];
				var childType = child.attributes.type || child.attributes.attributes.type;
				if(childType === 'field') {
					var field = {
						uniqueName: child.id,
						alias: child.text,
						text: child.attributes.field, 
						qtip: child.attributes.entity + ' : ' + child.attributes.field, 
						type: 'field', 
						value: 'dmFields[\'' + child.id + '\']'
					};	
					fields.push(field);
				}
			}
			this.calculatedFieldWizard.validationService.params = {fields: Ext.util.JSON.encode(fields)};
		
			this.calculatedFieldWizard.setExpItems('fields', fields);
			
			this.calculatedFieldWizard.setTargetNode(fieldNode);
			this.calculatedFieldWizard.show();
		} else 	if(nodeType == 'inLineCalculatedField') {
			
			if(this.inLineCalculatedFieldWizard === null) {
				this.initCalculatedFieldWizards();
			}			
			
			var parentEtityNode = fieldNode.parentNode;
			var fields = new Array();
			for(var i = 0; i < parentEtityNode.attributes.children.length; i++) {
				var child = parentEtityNode.attributes.children[i];
				var childType = child.attributes.type || child.attributes.attributes.type;

			
				if(childType === 'field') {
					var field = {
						uniqueName: child.id,
						alias: child.text,
						text: child.attributes.field, 
						qtip: child.attributes.entity + ' : ' + child.attributes.field, 
						type: 'field', 
						value: 'dmFields[\'' + child.id + '\']'
					};	
					fields.push(field);
				}
			}
			this.inLineCalculatedFieldWizard.validationService.params = {fields: Ext.util.JSON.encode(fields)};
			this.inLineCalculatedFieldWizard.setExpItems('fields', fields);
			this.inLineCalculatedFieldWizard.setTargetNode(fieldNode);
			this.inLineCalculatedFieldWizard.show();
	
			
		} else{
			Ext.Msg.show({
				   title:'Invalid operation',
				   msg: 'Node of type [' + nodeType + '] cannot be edited',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});
		}
	}
	
	, addCalculatedField: function(entityNode) {
		if(entityNode==null || entityNode==undefined){
			entityNode = this.pressedNode;
		}
			
		var selectNode;
		this.pressedNode=entityNode;
		if(!entityNode) return;
		var type = entityNode.attributes.type || entityNode.attributes.attributes.type;
		var text = entityNode.text || entityNode.attributes.text;
		
		if(type === 'entity') {
			
			if(this.calculatedFieldWizard === null) {
				this.initCalculatedFieldWizards();
			}
			
			var fields = new Array();
			for(var i = 0; i < entityNode.attributes.children.length; i++) {
				var child = entityNode.attributes.children[i];
				var childType = child.attributes.type || child.attributes.attributes.type;
				if(childType === 'field') {
					var field = {
						uniqueName: child.id,
						alias: child.text,
						text: child.attributes.field, 
						qtip: child.attributes.entity + ' : ' + child.attributes.field, 
						type: 'field', 
						value: 'dmFields[\'' + child.id + '\']'
					};	
					fields.push(field);
				}
			}
			this.calculatedFieldWizard.validationService.params = {fields: Ext.util.JSON.encode(fields)};
			this.calculatedFieldWizard.setExpItems('fields', fields);
			this.calculatedFieldWizard.setTargetNode(entityNode);
			this.calculatedFieldWizard.show();
		
		} else {
			Ext.Msg.show({
				   title:'Invalid operation',
				   msg: 'Impossible to add calculated field to a node of type [' + type + ']',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});		
		}	
	}
	
	, addInLineCalculatedField: function(entityNode) {
		if(entityNode==null || entityNode==undefined){
			entityNode = this.pressedNode;
		}
		this.pressedNode=entityNode
		
		var selectNode;
		
		if(!entityNode) return;
		var type = entityNode.attributes.type || entityNode.attributes.attributes.type;
		var text = entityNode.text || entityNode.attributes.text;
		
		if(type === 'entity') {
			
			if(this.inLineCalculatedFieldWizard === null) {
				this.initCalculatedFieldWizards();
			}
			
			var fields = new Array();
			for(var i = 0; i < entityNode.attributes.children.length; i++) {
				var child = entityNode.attributes.children[i];
				var childType = child.attributes.type || child.attributes.attributes.type;
				if(childType === 'field') {
					var field = {
						uniqueName: child.id,
						alias: child.text,
						text: child.attributes.field, 
						qtip: child.attributes.entity + ' : ' + child.attributes.field, 
						type: 'field', 
						value: 'dmFields[\'' + child.attributes.field + '\']'
					};	
					fields.push(field);
				}
			}
			
			this.inLineCalculatedFieldWizard.show();
			this.inLineCalculatedFieldWizard.validationService.params = {fields: Ext.util.JSON.encode(fields)};
			this.inLineCalculatedFieldWizard.setExpItems('fields', fields);
			this.inLineCalculatedFieldWizard.setTargetNode(entityNode);
			this.inLineCalculatedFieldWizard.show();
		
		} else {
			Ext.Msg.show({
				   title:'Invalid operation',
				   msg: 'Impossible to add calculated field to a node of type [' + type + ']',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});		
		}	
		
	}
	
	// --------------------------------------------------------------------------------
	// private methods
	// --------------------------------------------------------------------------------
	
	, createRootNode: function() {		
		var node = new Ext.tree.AsyncTreeNode({
	        text		: this.rootNodeText,
	        iconCls		: 'database',
	        expanded	: true,
	        draggable	: false
	    });
		return node;
	}
	
	, initTree: function(config) {
		
		this.treeLoader = new Ext.tree.TreeLoader({
	        baseParams: this.baseParams || {},
	        dataUrl: this.services['loadTree']
	    });
		this.treeLoader.on('load', this.oonLoad, this);
		this.treeLoader.on('loadexception', this.oonLoadException, this);
		
		this.rootNode = this.createRootNode();
		
		this.tree = new Ext.tree.TreePanel({
	        collapsible: false,
	        
	        enableDD: true,
	        ddGroup: this.ddGroup,
	        dropConfig: {
				isValidDropPoint : function(n, pt, dd, e, data){
					return false;
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
	        loader           : this.treeLoader,
	        preloadTree		 : this.preloadTree,
	        root 			 : this.rootNode
	    });	
		
		this.tree.type = this.type;
		
		this.tree.on('click', function(node) {
			var nodeType = node.attributes.type || node.attributes.attributes.type;
			if(nodeType !== 'entity') {
				this.fireEvent('addnodetoselect', this, node);
			}
		}, this);
		if(this.enableTreeContextMenu) {
			this.tree.on('contextmenu', this.onContextMenu, this);
		}
	}

	, initCalculatedFieldWizards: function() {
		
		var fields = new Array();
		
		var parametersLoader = new Ext.tree.TreeLoader({
	        baseParams: {},
	        dataUrl: this.services['getParameters']
	    });
		
		var attributesLoader = new Ext.tree.TreeLoader({
	        baseParams: {},
	        dataUrl: this.services['getAttributes']
	    });
		
		var crossFn = Ext.util.Format.htmlEncode("String label = 'bestByRegion';") + '<br>' + 
			Ext.util.Format.htmlEncode("String text= fields['salesRegion'];") + '<br>' + 
			Ext.util.Format.htmlEncode("String params= 'region=5';") + '<br>' + 
			Ext.util.Format.htmlEncode("String subobject;") + '<p>' + 
			Ext.util.Format.htmlEncode("String result = '';") + '<p>' + 
			Ext.util.Format.htmlEncode("result +='<a href=\"#\" onclick=\"javascript:sendMessage({';") + '<br>' + 
			Ext.util.Format.htmlEncode("result +='\\'label\\':\\'' + label + '\\'';") + '<br>' + 
			Ext.util.Format.htmlEncode("result +=', parameters:\\'' + params + '\\'';") + '<br>' + 
			Ext.util.Format.htmlEncode("result +=', windowName: this.name';") + '<br>' + 
			Ext.util.Format.htmlEncode("if(subobject != null) result +=', subobject:\\'' + subobject +'\\'';") + '<br>' + 
			Ext.util.Format.htmlEncode("result += '},\\'crossnavigation\\')\"';") + '<br>' + 
			Ext.util.Format.htmlEncode("result += '>' + text + '</a>';") + '<p>' + 
			Ext.util.Format.htmlEncode("return result;");
		
		var functions = [
		    {
			    text: 'link'
			    , qtip: 'create a link to external web page'
			    , type: 'function'
			    , value: Ext.util.Format.htmlEncode('\'<a href="${URL}">\' + ${LABEL} + \'</a>\'')
		    }, {
			    text: 'image' , 
			    qtip: 'include an external image'
			    , type: 'function'
			    , value: Ext.util.Format.htmlEncode('\'<img src="${URL}"></img>\'')
		    }, {
			    text: 'cross-navigation'
			    , qtip: 'create a cross navigation link'
			    , type: 'function'
			    , value: crossFn
		    }
		];
		
		var aggregationFunctions = [
		                            
     		    {
     			    text: 'SUM'
     			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.sum')
     			    , type: 'function'
    			    , value: Ext.util.Format.htmlEncode('SUM(op1)')
    			    , alias: Ext.util.Format.htmlEncode('SUM(op1)')
     		    }, {
     			    text: 'MIN'  
     			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.min')
     			    , type: 'function'
     			    , value: Ext.util.Format.htmlEncode('MIN(op1)')
     			    , alias: Ext.util.Format.htmlEncode('MIN(op1)')
     		    }, {
     			    text: 'MAX' 
     			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.max')
     			    , type: 'function'
     			    , value: Ext.util.Format.htmlEncode('MAX(op1)')
     			    , alias: Ext.util.Format.htmlEncode('MAX(op1)')
     		    }, {
     			    text: 'COUNT'
     			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.count')
     			    , type: 'function'
     	 			, value: Ext.util.Format.htmlEncode('COUNT(op1)')
     	 			, alias: Ext.util.Format.htmlEncode('COUNT(op1)')
     		    }, {
     			    text: 'COUNT_DISTINCT'
     			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct')
     			    , type: 'function'
     	 			, value: Ext.util.Format.htmlEncode('COUNT(DISTINCT op1)')
     	 			, alias: Ext.util.Format.htmlEncode('COUNT(DISTINCT op1)')
     		    }, {
    			    text: 'AVG'
    				, qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.avg')
    				, type: 'function'
    				, value: Ext.util.Format.htmlEncode('AVG(op1)')
    				, alias: Ext.util.Format.htmlEncode('AVG(op1)')
    				}
     		];
		
		var functionsForInline = [
      		    {
      			    text: '+'
      			    , qtip: 'binary sum function'
      			    , type: 'function'
      			    , value: Ext.util.Format.htmlEncode('op1 + op2')
      			    , alias: Ext.util.Format.htmlEncode('op1 + op2')
      		    }, {
      			    text: '-' 
      			    , qtip: 'binary difference function'
      			    , type: 'function'
      			    , value: Ext.util.Format.htmlEncode('op1 - op2')
      			    , alias: Ext.util.Format.htmlEncode('op1 - op2')
      		    }, {
      			    text: '*'
      			    , qtip: 'binary multiplication function'
      			    , type: 'function'
      			    , value: Ext.util.Format.htmlEncode('op1 * op2')
      			    , alias: Ext.util.Format.htmlEncode('op1 * op2')
      		    }, {
      			    text: '/'
      				, qtip: 'binary division function'
      				, type: 'function'
      				, value: Ext.util.Format.htmlEncode('op1 / op2')
      				, alias: Ext.util.Format.htmlEncode('op1 / op2')
      			}, {
      			    text: '||'
      				, qtip: 'pipe'
      				, type: 'function'
      				, value: Ext.util.Format.htmlEncode('op1 || op2')
      				, alias: Ext.util.Format.htmlEncode('op1 || op2')
      			}
      		];		
		
		
		//this.treeLoader.on('load', this.oonLoad, this);
		//this.treeLoader.on('loadexception', this.oonLoadException, this);
		
		this.calculatedFieldWizard = new Sbi.qbe.CalculatedFieldWizard({
    		title: 'Calculated Field Wizard',
     		expItemGroups: [
       		    {name:'fields', text: 'Fields'}, 
       		    {name:'parameters', text: 'Parameters', loader: parametersLoader}, 
       		    {name:'attributes', text: 'Attributes', loader: attributesLoader},
       		    {name:'functions', text: 'Functions'}
       		],
       		fields: fields,
       		functions: functions,
       		expertMode: true,
          	scopeComboBoxData :[
           	    ['STRING','String', 'If the expression script returns a plain text string'],
           	    ['HTML', 'Html', 'If the expression script returns a valid html fragment'],
           	    ['NUMBER', 'Number', 'If the expression script returns a number']
           	],
    		validationService: {
				serviceName: 'VALIDATE_EXPRESSION_ACTION'
				, baseParams: {contextType: 'datamart'}
				, params: null
			}
    	});
		
		this.inLineCalculatedFieldWizard = new Sbi.qbe.CalculatedFieldWizard({
    		title: 'Calculated Field Wizard',
    		expItemGroups: [
    		    {name:'fields', text: 'Fields'}, 
    		    {name:'functions', text: 'Functions'},
    		    {name:'aggregationFunctions', text: 'Aggregation Functions'}
    		],
    		fields: fields,
    		functions: functionsForInline,
    		aggregationFunctions: aggregationFunctions,
    		expertMode: false,
        	scopeComboBoxData :[
        	    ['STRING','String', 'If the expression script returns a plain text string'],
        	    ['NUMBER', 'Number', 'If the expression script returns a number']
        	],
        	validationService: {
				serviceName: 'VALIDATE_EXPRESSION_ACTION'
				, baseParams: {contextType: 'datamart'}
				, params: null
			}
    	});
     	
     	this.calculatedFieldWizard.on('expert', function(){
     		if(this.calculatedFieldWizard!=null){
     			var alias = this.calculatedFieldWizard.inputFields.alias.getValue();
     		}
     		this.initCalculatedFieldWizards();
     		this.addInLineCalculatedField(this.pressedNode);
     		this.inLineCalculatedFieldWizard.setCFAlias(alias);
     		this.inLineCalculatedFieldWizard.show();
     	}, this);
 
     	this.inLineCalculatedFieldWizard.on('notexpert', function(){
     		if(this.inLineCalculatedFieldWizard!=null){
     			var alias = this.inLineCalculatedFieldWizard.inputFields.alias.getValue();
     		}
     		this.initCalculatedFieldWizards();
     		this.addCalculatedField(this.pressedNode);
   			this.calculatedFieldWizard.setCFAlias(alias);
   			this.calculatedFieldWizard.show();
     	}, this);
	
    	this.inLineCalculatedFieldWizard.on('apply', function(win, formState, targetNode){
    		
    		var nodeType;
    		nodeType = targetNode.attributes.type || targetNode.attributes.attributes.type;
    		
    		var entityId = (nodeType == this.IN_LINE_CALCULATED_FIELD)? targetNode.parentNode.id: targetNode.id;
    		var f = {
    			alias: formState.alias
    			, id: formState
    			, filedType: this.IN_LINE_CALCULATED_FIELD
    			, type: formState.type
    			, calculationDescriptor: formState
    		};
    		
    		var params = {
    			entityId: entityId,
    			field: Ext.util.JSON.encode(f)
    		}
    		
    		Ext.Ajax.request({
				url:  this.services['addCalculatedField'],
				success: function(response, options) {
       				//alert('saved');
       			},
       			scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure,	
				params: params
        	}); 
    		
    		
    		if(nodeType == this.IN_LINE_CALCULATED_FIELD) {
    			targetNode.text = formState.alias;
    			targetNode.attributes.attributes.formState = formState;
    		} else if (nodeType == 'entity') {
    			var node = new Ext.tree.TreeNode({
        			text: formState.alias,
        			leaf: true,
        			type: this.IN_LINE_CALCULATED_FIELD, 
        			longDescription: formState.expression,
        			formState: formState, 
        			iconCls: 'calculation',
        			attributes:{
            			text: formState.alias,
            			leaf: true,
            			type: this.IN_LINE_CALCULATED_FIELD, 
            			longDescription: formState.expression,
            			formState: formState, 
            			iconCls: 'calculation'}
            		
        		});

    			
    			if (!targetNode.isExpanded()) {
        			targetNode.expand(false, true, function() {targetNode.appendChild( node );});
        		} else {
        			targetNode.appendChild( node );
        		}
    		} else {
    			Ext.Msg.show({
					   title:'Invalid operation',
					   msg: 'Node of type [' + nodeType + '] cannot be modified',
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.ERROR
				});
    		}
    	}, this);
	
		
    	this.calculatedFieldWizard.on('apply', function(win, formState, targetNode){
    		
    		var nodeType;
    		nodeType = targetNode.attributes.type || targetNode.attributes.attributes.type;
    		
    		var entityId = (nodeType == this.CALCULATED_FIELD)? targetNode.parentNode.id: targetNode.id;
    		var f = {
    			alias: formState.alias
    			, id: formState
    			, type: formState.type
    			, filedType: this.CALCULATED_FIELD
    			, calculationDescriptor: formState
    		};
    		var params = {
    			entityId: entityId,
    			field: Ext.util.JSON.encode(f)
    		}
    		
    		Ext.Ajax.request({
				url:  this.services['addCalculatedField'],
				success: function(response, options) {
       				//alert('saved');
       			},
       			scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure,	
				params: params
        	}); 
    		
    		
    		if(nodeType == this.CALCULATED_FIELD) {
    			targetNode.text = formState.alias;
    			targetNode.attributes.attributes.formState = formState;
    		} else if (nodeType == 'entity') {
    			var node = new Ext.tree.TreeNode({
        			text: formState.alias,
        			leaf: true,
        			type: this.CALCULATED_FIELD, 
        			longDescription: formState.expression,
        			formState: formState, 
        			iconCls: 'calculation',
        			attributes:{
	        			text: formState.alias,
	        			leaf: true,
	        			type: this.CALCULATED_FIELD, 
	        			longDescription: formState.expression,
	        			formState: formState, 
	        			iconCls: 'calculation'}
        		});

    			
    			if (!targetNode.isExpanded()) {
        			targetNode.expand(false, true, function() {targetNode.appendChild( node );});
        		} else {
        			targetNode.appendChild( node );
        		}
    		} else {
    			Ext.Msg.show({
					   title:'Invalid operation',
					   msg: 'Node of type [' + nodeType + '] cannot be modified',
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.ERROR
				});
    		}
    			
    		
    		
    	}, this);
	}
	
	, initMenu: function() {
		 this.menu = new Ext.menu.Menu({
             id:'feeds-ctx',
             items: [
             // ACID operations on nodes
             '-',{
            	 text:'Add Calculated Field',
                 iconCls:'add',
                 handler:function(){
            	   	this.addInLineCalculatedField(this.ctxNode);	         	 	
	             },
                 scope: this
             },{
            	 text:'Remove Calculated Field',
                 iconCls:'remove',
                 handler:  function() {
	            	 this.ctxNode.ui.removeClass('x-node-ctx');
	            	 this.removeCalculatedField(this.ctxNode);
	            	 this.ctxNode = null;
                 },
                 scope: this
             },{
            	 text:'Edit Field',
                 iconCls:'edit',
                 handler:function(){
	         	 	this.editField(this.ctxNode);
	             },
                 scope: this
             },{
            	 text:'Insert Slot',
                 iconCls:'slot',
                 handler:function(){
	         	 	alert("slot");
	             },
                 scope: this
             }]
         });
		 
		 for(var i = 0; i < this.actions.length; i++) {
			
			 var item = new Ext.menu.Item({
				 text: this.actions[i].text,
	             iconCls: this.actions[i].iconCls,
	             handler:  this.executeAction.createDelegate(this, [i]),
	             scope: this
			 });
			 //item.action = this.actions[i];
			 this.menu.insert(i, item);
		 }
		 
		 
		 
         this.menu.on('hide', function(){
             if(this.ctxNode){
                 this.ctxNode.ui.removeClass('x-node-ctx');
                 this.ctxNode = null;
             }
         }, this);
	}
	
	, executeAction: function(actionIndex) {
		this.actions[actionIndex].handler.call(this.actions[actionIndex].scope, this.ctxNode);
	}
	
	, onContextMenu : function(node, e){
		if(this.menu != null){
			this.menu.destroy();
		}

		this.initMenu();// create context menu on first right click
        
        if(this.ctxNode){
            this.ctxNode.ui.removeClass('x-node-ctx');
            this.ctxNode = null;
        }
        
        this.ctxNode = node;
        this.ctxNode.ui.addClass('x-node-ctx');
        this.menu.showAt(e.getXY());
        
        /*
        if(node.isLeaf()){
            this.ctxNode = node;
            this.ctxNode.ui.addClass('x-node-ctx');
            this.menu.items.get('load').setDisabled(node.isSelected());
            this.menu.showAt(e.getXY());
        }
        */
    }
	
	, oonLoad: function(treeLoader, node, response) {
		this.rootNode = this.tree.root;
		this.fireEvent('load', this, treeLoader, node, response);
	}
	
	, oonLoadException: function(treeLoader, node, response) {
		Sbi.exception.ExceptionHandler.handleFailure(response, treeLoader.baseParams || {});
	}
});