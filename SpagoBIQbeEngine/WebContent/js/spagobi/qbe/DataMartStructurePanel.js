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
	, slotWizard : null
	, menu: null
	
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
		if(nodeType === Sbi.settings.qbe.constants.NODE_TYPE_CALCULATED_FIELD 
		|| nodeType === Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD) {
			
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
				   title:LN('sbi.qbe.bands.wizard.invalid.operation'),
				   msg: LN('sbi.qbe.bands.wizard.invalid.operation.delete.msg'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});
		}
	}
	
	, editField: function(fieldNode) {
		var nodeType;
		nodeType = fieldNode.attributes.type || fieldNode.attributes.attributes.type;
		//edit slot forbidden
		if(fieldNode.attributes.attributes.formState.slots !== undefined || fieldNode.attributes.attributes.formState.slots !== null){
			Ext.Msg.show({
				   title:'Invalid operation',
				   msg: 'Range cannot be edited. Use Edit Range menu function.',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});
			return;
		}
		
		if(nodeType == Sbi.settings.qbe.constants.NODE_TYPE_CALCULATED_FIELD) {
			
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
			this.calculatedFieldWizard.mainPanel.validationService.params = {fields: Ext.util.JSON.encode(fields)};
		
			this.calculatedFieldWizard.setExpItems('fields', fields);
			
			this.calculatedFieldWizard.setTargetNode(fieldNode);
			this.calculatedFieldWizard.show();
		} else 	if(nodeType == Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD) {
			
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
			this.inLineCalculatedFieldWizard.mainPanel.validationService.params = {fields: Ext.util.JSON.encode(fields)};
			this.inLineCalculatedFieldWizard.setExpItems('fields', fields);
			this.inLineCalculatedFieldWizard.setTargetNode(fieldNode);
			this.inLineCalculatedFieldWizard.show();
	
			
		} else{
			Ext.Msg.show({
				   title:LN('sbi.qbe.bands.wizard.invalid.operation'),
				   msg: LN('sbi.qbe.bands.wizard.invalid.operation.edit.msg'),
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
		
		if(type === Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {
			
			if(this.calculatedFieldWizard === null) {
				this.initCalculatedFieldWizards();
			}
			
			var fields = new Array();
			for(var i = 0; i < entityNode.attributes.children.length; i++) {
				var child = entityNode.attributes.children[i];
				var childType = child.attributes.type || child.attributes.attributes.type;
				if(childType === Sbi.settings.qbe.constants.NODE_TYPE_SIMPLE_FIELD) {
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
			this.calculatedFieldWizard.mainPanel.validationService.params = {fields: Ext.util.JSON.encode(fields)};
			this.calculatedFieldWizard.setExpItems('fields', fields);
			this.calculatedFieldWizard.setTargetNode(entityNode);
			this.calculatedFieldWizard.show();
		
		} else {
			Ext.Msg.show({
				   title:LN('sbi.qbe.bands.wizard.invalid.operation'),
				   msg: LN('sbi.qbe.calculatedFields.add.error'),
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
		
		if(type === Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {
			
			if(this.inLineCalculatedFieldWizard === null) {
				this.initCalculatedFieldWizards();
			}
			
			var fields = new Array();
			for(var i = 0; i < entityNode.attributes.children.length; i++) {
				var child = entityNode.attributes.children[i];
				var childType = child.attributes.type || child.attributes.attributes.type;
				if(childType === Sbi.settings.qbe.constants.NODE_TYPE_SIMPLE_FIELD) {
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
			this.inLineCalculatedFieldWizard.mainPanel.validationService.params = {fields: Ext.util.JSON.encode(fields)};
			this.inLineCalculatedFieldWizard.setExpItems('fields', fields);
			this.inLineCalculatedFieldWizard.setTargetNode(entityNode);
			this.inLineCalculatedFieldWizard.show();
		
		} else {
			Ext.Msg.show({
				   title:LN('sbi.qbe.bands.wizard.invalid.operation'),
				   msg: LN('sbi.qbe.calculatedFields.add.error'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});		
		}	
		
	}
	, addSlot: function(entityNode) {
		if(entityNode==null || entityNode==undefined){
			entityNode = this.pressedNode;
		}
		this.pressedNode=entityNode
		
		var selectNode;
		
		if(!entityNode) return;
		var type = entityNode.attributes.type || entityNode.attributes.attributes.type;
		var text = entityNode.text || entityNode.attributes.text;
		
		if(type === Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {

			var dateFunctions = [
     		    {
     	            text: 'GG_between_dates'
     	            , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.ggbetweendates')
     	            , type: 'function'
     	            , value: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
     	            , alias: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
     	            , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
     	            		   , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
     		    
     	         }, {
     		         text: 'MM_between_dates'
     		         , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.mmbetweendates')
     			     , type: 'function'
     			     , value: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
     			     , alias: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
     			     , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
     	            		    , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
     			 },{
     				 text: 'AA_between_dates'
     			      , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.aabetweendates')
     			      , type: 'function'
     			      , value: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
     			      , alias: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
     			      , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
     	            		    , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
     			 }, {
     			     text: 'GG_up_today'
     			     , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.gguptoday')
     				 , type: 'function'
     				 , value: Ext.util.Format.htmlEncode('GG_up_today(op1)')
     				 , alias: Ext.util.Format.htmlEncode('GG_up_today(op1)')
     				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
     			 }, {
     			     text: 'MM_up_today'
     				 , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.mmuptoday')
     				 , type: 'function'
     				 , value: Ext.util.Format.htmlEncode('MM_up_today(op1)')
     				 , alias: Ext.util.Format.htmlEncode('MM_up_today(op1)')
     				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
     			  }, {
     				 text: 'AA_up_today'
     				 , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.aauptoday')
     				 , type: 'function'
     				 , value: Ext.util.Format.htmlEncode('AA_up_today(op1)')
     				 , alias: Ext.util.Format.htmlEncode('AA_up_today(op1)')
     				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
     			   }
     	    ];
			
			var functionsForInline = [
			    {
				    text: '+'
				    , qtip: LN('sbi.qbe.selectgridpanel.func.sum.tip')
				    , type: 'function'
				    , value: Ext.util.Format.htmlEncode('+')
				    , alias: Ext.util.Format.htmlEncode('+')
			    }, {
				    text: '-' 
				    , qtip: LN('sbi.qbe.selectgridpanel.func.difference.tip')
				    , type: 'function'
				    , value: Ext.util.Format.htmlEncode('-')
				    , alias: Ext.util.Format.htmlEncode('-')
			    }, {
				    text: '*'
				    ,qtip: LN('sbi.qbe.selectgridpanel.func.multiplication.tip')
				    , type: 'function'
				    , value: Ext.util.Format.htmlEncode('*')
				    , alias: Ext.util.Format.htmlEncode('*')
			    }, {
				    text: '/'
					,qtip: LN('sbi.qbe.selectgridpanel.func.division.tip')
					, type: 'function'
					, value: Ext.util.Format.htmlEncode('/')
					, alias: Ext.util.Format.htmlEncode('/')
				}, {
				    text: '||'
					,qtip: LN('sbi.qbe.selectgridpanel.func.pipe.tip')
					, type: 'function'
					, value: Ext.util.Format.htmlEncode('||')
					, alias: Ext.util.Format.htmlEncode('||')
				}, {
				    text: '('
	    				,qtip: LN('sbi.qbe.selectgridpanel.func.openpar.tip')
	    				, type: 'function'
	    				, value: Ext.util.Format.htmlEncode('(')
	    				, alias: Ext.util.Format.htmlEncode('(')
	    		}, {
				    text: ')'
	    				,qtip: LN('sbi.qbe.selectgridpanel.func.closepar.tip')
	    				, type: 'function'
	    				, value: Ext.util.Format.htmlEncode(')')
	    				, alias: Ext.util.Format.htmlEncode(')')
	        	}
			];		
			
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
			//creates a window
			this.slotWizard = new Sbi.qbe.SlotWizard( { 
				title: LN('sbi.qbe.bands.title'),
				startFromFirstPage: true,
		    	expItemGroups: [
	    		    {name:'fields', text: LN('sbi.qbe.calculatedFields.fields')}, 
	    		    {name:'functions', text: LN('sbi.qbe.calculatedFields.functions')},  		    
	    		    {name:'dateFunctions', text: LN('sbi.qbe.calculatedFields.datefunctions')}
	    		],
	    		fields: new Array(),
	    		functions: functionsForInline,
	    		dateFunctions: dateFunctions,
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
	    	
			

			var slotCalculatedFieldsPanel = this.slotWizard.getCalculatedFiledPanel();
			if(slotCalculatedFieldsPanel.mainPanel !== undefined && slotCalculatedFieldsPanel.mainPanel != null){
				slotCalculatedFieldsPanel.mainPanel.validationService.params = {fields: Ext.util.JSON.encode(fields)};
			}
			slotCalculatedFieldsPanel.setExpItems('fields', fields);
			slotCalculatedFieldsPanel.setTargetNode(entityNode);
		
			
			this.slotWizard.mainPanel.doLayout();
			this.slotWizard.show();

		
		} else if(type === Sbi.settings.qbe.constants.NODE_TYPE_SIMPLE_FIELD){
			//creates a window
			this.slotWizard = new Sbi.qbe.SlotWizard( { 
				title: LN('sbi.qbe.bands.title'),
				fieldForSlot: entityNode,
				startFromFirstPage: false
			});
			
			this.slotWizard.mainPanel.doLayout();
			this.slotWizard.show();
		}else {
			Ext.Msg.show({
				   title: LN('sbi.qbe.bands.wizard.invalid.definition'),
				   msg: LN('sbi.qbe.bands.wizard.invalid.definition.msg'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});		
		}	
		this.slotWizard.on('apply', function(win, formState, targetNode){
    		
    		var nodeType;
    		nodeType = targetNode.attributes.type || targetNode.attributes.attributes.type;
    		
    		
    		
    		var entityId = (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD)? targetNode.parentNode.id: targetNode.id;
    		var f = {
    			alias: formState.alias
    			, id: formState
    			, filedType: Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD
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
       				//('saved');
       			},
       			scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure,	
				params: params
        	}); 
    		
    		
    		if(nodeType == Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD) {
    			targetNode.text = formState.alias;
    			targetNode.attributes.attributes.formState = formState;
    		} else if (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {
    			var node = new Ext.tree.TreeNode({
        			text: formState.alias,
        			leaf: true,
        			type: Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD, 
        			longDescription: formState.expression,
        			formState: formState, 
        			iconCls: 'calculation',
        			attributes:{
            			text: formState.alias,
            			leaf: true,
            			type: Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD, 
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
					   title: LN('sbi.qbe.bands.wizard.invalid.operation'),
					   msg: LN('sbi.qbe.bands.wizard.invalid.operation.msg'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.ERROR
				});
    		}
    		win.close();
    	}, this);
	}
	, editSlot: function(entityNode) {
		if(entityNode==null || entityNode==undefined){
			entityNode = this.pressedNode;
		}
		this.pressedNode=entityNode
		
		var selectNode;
		
		if(!entityNode) return;
		var type = entityNode.attributes.type || entityNode.attributes.attributes.type;
		var text = entityNode.text || entityNode.attributes.text;
		
		if(type === Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {			
			Ext.Msg.show({
			   title: LN('sbi.qbe.bands.wizard.invalid.operation'),
			   msg:  LN('sbi.qbe.bands.wizard.invalid.node'),
			   buttons: Ext.Msg.OK,
			   icon: Ext.MessageBox.ERROR
			});		
		} else if(type === Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD){
			var dateFunctions = [
      		    {
      	            text: 'GG_between_dates'
      	            , qtip: LN('da implementare')
      	            , type: 'function'
      	            , value: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
      	            , alias: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
      	            , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
      	            		   , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
      		    
      	         }, {
      		         text: 'MM_between_dates'
      		         , qtip: LN('da implementare')
      			     , type: 'function'
      			     , value: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
      			     , alias: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
      			     , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
      	            		    , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
      			 },{
      				 text: 'AA_between_dates'
      			      , qtip: LN('da implementare')
      			      , type: 'function'
      			      , value: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
      			      , alias: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
      			      , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
      	            		    , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
      			 }, {
      			     text: 'GG_up_today'
      			     , qtip: LN('da implementare')
      				 , type: 'function'
      				 , value: Ext.util.Format.htmlEncode('GG_up_today(op1)')
      				 , alias: Ext.util.Format.htmlEncode('GG_up_today(op1)')
      				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
      			 }, {
      			     text: 'MM_up_today'
      				 , qtip: LN('da implementare')
      				 , type: 'function'
      				 , value: Ext.util.Format.htmlEncode('MM_up_today(op1)')
      				 , alias: Ext.util.Format.htmlEncode('MM_up_today(op1)')
      				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
      			  }, {
      				 text: 'AA_up_today'
      				 , qtip: LN('da implementare')
      				 , type: 'function'
      				 , value: Ext.util.Format.htmlEncode('AA_up_today(op1)')
      				 , alias: Ext.util.Format.htmlEncode('AA_up_today(op1)')
      				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
      			   }
      	    ];
 			var fields = new Array();
 			var fieldNode = entityNode.parentNode;
 			for(var i = 0; i < fieldNode.childNodes.length; i++) {
 				var child = fieldNode.childNodes[i];
 				var childType = child.attributes.type || child.attributes.attributes.type;
 				if(childType === 'field') {
 					var field = {
 						uniqueName: child.id,
 						alias: child.text,
 						text: child.attributes.attributes.field, 
 						qtip: child.attributes.attributes.entity + ' : ' + child.attributes.attributes.field, 
 						type: 'field', 
 						value: 'dmFields[\'' + child.attributes.attributes.field + '\']'
 					};	
 					fields.push(field);
 				}
 			}
 			//creates a window
 			this.slotWizard = new Sbi.qbe.SlotWizard( { 
 				title: LN('sbi.qbe.bands.title'),
				fieldForSlot: entityNode,
				modality: 'edit',
				startFromFirstPage: false,
 		    	expItemGroups: [
 	    		    {name:'fields', text: 'Fields'}, 
 	    		    {name:'dateFunctions', text: 'Date Functions'}
 	    		],
 	    		fields: new Array(),
 	    		dateFunctions: dateFunctions,
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
    	

			this.slotWizard.on('apply', function(win, formState, targetNode){
	    		
	    		var nodeType;
	    		nodeType = targetNode.attributes.type || targetNode.attributes.attributes.type;
	    		
	    		var entityId = (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD)? targetNode.parentNode.id: targetNode.id;
	    		var f = {
	    			alias: formState.alias
	    			, id: formState
	    			, filedType: Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD
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
	    		
	    		
	    		if(nodeType == Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD) {
	    			targetNode.text = formState.alias;
	    			targetNode.attributes.attributes.formState = formState;
	    		} else if (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {
	    			var node = new Ext.tree.TreeNode({
	        			text: formState.alias,
	        			leaf: true,
	        			type: Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD, 
	        			longDescription: formState.expression,
	        			formState: formState, 
	        			iconCls: 'calculation',
	        			attributes:{
	            			text: formState.alias,
	            			leaf: true,
	            			type: Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD, 
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
						   title:LN('sbi.qbe.bands.wizard.invalid.operation'),
						   msg: LN('sbi.qbe.bands.wizard.invalid.operation.msg'),
						   buttons: Ext.Msg.OK,
						   icon: Ext.MessageBox.ERROR
					});
	    		}
	    		win.close();
	    	}, this);
			
 			if(this.slotWizard.mainPanel.validationService !== undefined){
 				this.slotWizard.mainPanel.validationService.params = {fields: Ext.util.JSON.encode(fields)}; 		
 			}
			this.slotWizard.setExpItems('fields', fields);
						
			this.slotWizard.setTargetNode(entityNode);
			this.slotWizard.mainPanel.doLayout();
			//this.slotWizard.mainPanel.firstCalculatedFiledPanel.inputFields['type'].syncSize();
			this.slotWizard.show(); 	

		}else {
			Ext.Msg.show({
				   title:LN('sbi.qbe.bands.wizard.invalid.operation'),
				   msg: LN('sbi.qbe.bands.wizard.invalid.node'),
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
			    , qtip: LN('sbi.qbe.selectgridpanel.func.link.tip')
			    , type: 'function'
			    , value: Ext.util.Format.htmlEncode('\'<a href="${URL}">\' + ${LABEL} + \'</a>\'')
		    }, {
			    text: 'image' , 
			    qtip: LN('sbi.qbe.selectgridpanel.func.image.tip')
			    , type: 'function'
			    , value: Ext.util.Format.htmlEncode('\'<img src="${URL}"></img>\'')
		    }, {
			    text: 'cross-navigation'
			    , qtip: LN('sbi.qbe.selectgridpanel.func.cross.tip')
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
			    , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpSum')}]
 		    }, {
 			    text: 'MIN'  
 			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.min')
 			    , type: 'function'
 			    , value: Ext.util.Format.htmlEncode('MIN(op1)')
 			    , alias: Ext.util.Format.htmlEncode('MIN(op1)')
 			    , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMin')}]
 		    }, {
 			    text: 'MAX' 
 			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.max')
 			    , type: 'function'
 			    , value: Ext.util.Format.htmlEncode('MAX(op1)')
 			    , alias: Ext.util.Format.htmlEncode('MAX(op1)')
 			    , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpMax')}]
 		    }, {
 			    text: 'COUNT'
 			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.count')
 			    , type: 'function'
 	 			, value: Ext.util.Format.htmlEncode('COUNT(op1)')
 	 			, alias: Ext.util.Format.htmlEncode('COUNT(op1)')
 	 		    , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCount')}]
 		    }, {
 			    text: 'COUNT_DISTINCT'
 			    , qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct')
 			    , type: 'function'
 	 			, value: Ext.util.Format.htmlEncode('COUNT(DISTINCT op1)')
 	 			, alias: Ext.util.Format.htmlEncode('COUNT(DISTINCT op1)')
 	 			, operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpCountDist')}]
 		    }, {
			    text: 'AVG'
				, qtip: LN('sbi.qbe.selectgridpanel.aggfunc.desc.avg')
				, type: 'function'
				, value: Ext.util.Format.htmlEncode('AVG(op1)')
				, alias: Ext.util.Format.htmlEncode('AVG(op1)')
				, operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpAVG')}]
			}
 		];
		
		var dateFunctions = [
		    {
	            text: 'GG_between_dates'
	            , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.ggbetweendates')
	            , type: 'function'
	            , value: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
	            , alias: Ext.util.Format.htmlEncode('GG_between_dates(op1,op2)')
	            , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
	            		   , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
		    
	         }, {
		         text: 'MM_between_dates'
		         , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.mmbetweendates')
			     , type: 'function'
			     , value: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
			     , alias: Ext.util.Format.htmlEncode('MM_between_dates(op1,op2)')
			     , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
	            		    , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
			 },{
				 text: 'AA_between_dates'
			      , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.aabetweendates')
			      , type: 'function'
			      , value: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
			      , alias: Ext.util.Format.htmlEncode('AA_between_dates(op1,op2)')
			      , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate1')}
	            		    , {label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate2')}]
			 }, {
			     text: 'GG_up_today'
			     , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.gguptoday')
				 , type: 'function'
				 , value: Ext.util.Format.htmlEncode('GG_up_today(op1)')
				 , alias: Ext.util.Format.htmlEncode('GG_up_today(op1)')
				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
			 }, {
			     text: 'MM_up_today'
				 , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.mmuptoday')
				 , type: 'function'
				 , value: Ext.util.Format.htmlEncode('MM_up_today(op1)')
				 , alias: Ext.util.Format.htmlEncode('MM_up_today(op1)')
				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
			  }, {
				 text: 'AA_up_today'
				 , qtip: LN('sbi.qbe.selectgridpanel.datefunc.desc.aauptoday')
				 , type: 'function'
				 , value: Ext.util.Format.htmlEncode('AA_up_today(op1)')
				 , alias: Ext.util.Format.htmlEncode('AA_up_today(op1)')
				 , operands: [{label: LN('sbi.qbe.selectgridpanel.aggfunc.desc.labelOpDate')}]
			   }
	    ];
		
		var functionsForInline = [
  		    {
  			    text: '+'
  			    , qtip: LN('sbi.qbe.selectgridpanel.func.sum.tip')
  			    , type: 'function'
  			    , value: Ext.util.Format.htmlEncode('+')
  			    , alias: Ext.util.Format.htmlEncode('+')
  		    }, {
  			    text: '-' 
  			    , qtip: LN('sbi.qbe.selectgridpanel.func.difference.tip')
  			    , type: 'function'
  			    , value: Ext.util.Format.htmlEncode('-')
  			    , alias: Ext.util.Format.htmlEncode('-')
  		    }, {
  			    text: '*'
  			    ,qtip: LN('sbi.qbe.selectgridpanel.func.multiplication.tip')
  			    , type: 'function'
  			    , value: Ext.util.Format.htmlEncode('*')
  			    , alias: Ext.util.Format.htmlEncode('*')
  		    }, {
  			    text: '/'
  				,qtip: LN('sbi.qbe.selectgridpanel.func.division.tip')
  				, type: 'function'
  				, value: Ext.util.Format.htmlEncode('/')
  				, alias: Ext.util.Format.htmlEncode('/')
  			}, {
  			    text: '||'
  				,qtip: LN('sbi.qbe.selectgridpanel.func.pipe.tip')
  				, type: 'function'
  				, value: Ext.util.Format.htmlEncode('||')
  				, alias: Ext.util.Format.htmlEncode('||')
  			}, {
  			    text: '('
      				,qtip: LN('sbi.qbe.selectgridpanel.func.openpar.tip')
      				, type: 'function'
      				, value: Ext.util.Format.htmlEncode('(')
      				, alias: Ext.util.Format.htmlEncode('(')
      		}, {
  			    text: ')'
      				,qtip: LN('sbi.qbe.selectgridpanel.func.closepar.tip')
      				, type: 'function'
      				, value: Ext.util.Format.htmlEncode(')')
      				, alias: Ext.util.Format.htmlEncode(')')
          	}
  		];				
		
		this.calculatedFieldWizard = new Sbi.qbe.CalculatedFieldWizard({
    		title: LN('sbi.qbe.calculatedFields.title'),
     		expItemGroups: [
       		    {name:'fields', text: LN('sbi.qbe.calculatedFields.fields')}, 
       		    {name:'parameters', text: LN('sbi.qbe.calculatedFields.parameters'), loader: parametersLoader}, 
       		    {name:'attributes', text: LN('sbi.qbe.calculatedFields.attributes'), loader: attributesLoader},
       		    {name:'functions', text: LN('sbi.qbe.calculatedFields.functions')},
       		    {name:'dateFunctions', text: LN('sbi.qbe.calculatedFields.datefunctions')}
       		],
       		fields: fields,
       		functions: functions,
       		dateFunctions: dateFunctions,
       		expertMode: true,
          	scopeComboBoxData :[
           	    ['STRING','String', LN('sbi.qbe.calculatedFields.string.type')],
           	    ['HTML', 'Html', LN('sbi.qbe.calculatedFields.html.type')],
           	    ['NUMBER', 'Number', LN('sbi.qbe.calculatedFields.num.type')]
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
    		    {name:'fields', text: LN('sbi.qbe.calculatedFields.fields')}, 
    		    {name:'functions', text: LN('sbi.qbe.calculatedFields.functions')},
    		    {name:'aggregationFunctions', text: LN('sbi.qbe.calculatedFields.aggrfunctions')},
    		    {name:'dateFunctions', text: LN('sbi.qbe.calculatedFields.datefunctions')}
    		],
    		fields: fields,
    		functions: functionsForInline,
    		aggregationFunctions: aggregationFunctions,
    		dateFunctions: dateFunctions,
    		expertMode: false,
        	scopeComboBoxData :[
        	    ['STRING','String',  LN('sbi.qbe.calculatedFields.string.type')],
        	    ['NUMBER', 'Number', LN('sbi.qbe.calculatedFields.num.type')]
        	],
        	validationService: {
				serviceName: 'VALIDATE_EXPRESSION_ACTION'
				, baseParams: {contextType: 'datamart'}
				, params: null
			}
    	});
     	
     	this.calculatedFieldWizard.mainPanel.on('expert', function(){

     		if(this.calculatedFieldWizard!=null){
     			var alias = this.calculatedFieldWizard.mainPanel.inputFields.alias.getValue();
     		}
     		this.initCalculatedFieldWizards();
     		this.addInLineCalculatedField(this.pressedNode);
     		this.inLineCalculatedFieldWizard.mainPanel.setCFAlias(alias);
     		this.inLineCalculatedFieldWizard.show();
     	}, this);
 
     	this.inLineCalculatedFieldWizard.mainPanel.on('notexpert', function(){

     		if(this.inLineCalculatedFieldWizard!=null){
     			var alias = this.inLineCalculatedFieldWizard.mainPanel.inputFields.alias.getValue();
     		}
     		this.initCalculatedFieldWizards();
     		this.addCalculatedField(this.pressedNode);
   			this.calculatedFieldWizard.mainPanel.setCFAlias(alias);
   			this.calculatedFieldWizard.show();
     	}, this);
	
    	this.inLineCalculatedFieldWizard.on('apply', this.onAddInlineCalculatedField, this);
    	this.calculatedFieldWizard.on('apply', this.onAddCalculatedField, this);
	}
	
	, onAddInlineCalculatedField : function(win, formState, targetNode, fieldType){
		
		var nodeType;
		nodeType = targetNode.attributes.type || targetNode.attributes.attributes.type;
		
		var entityId = (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD)? targetNode.parentNode.id: targetNode.id;
		var f = {
			alias: formState.alias
			, id: formState
			, filedType: fieldType
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
		
		
		if(nodeType == Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD) {
			targetNode.text = formState.alias;
			targetNode.attributes.attributes.formState = formState;
			
		} else if (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {
			
			var node = new Ext.tree.TreeNode({
    			text: formState.alias,
    			leaf: true,
    			type: fieldType, 
    			longDescription: formState.expression,
    			formState: formState, 
    			iconCls: 'calculation',
    			attributes:{
        			text: formState.alias,
        			leaf: true,
        			type: Sbi.settings.qbe.constants.NODE_TYPE_INLINE_CALCULATED_FIELD, 
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
				   title: LN('sbi.qbe.bands.wizard.invalid.operation'),
				   msg: LN('sbi.qbe.bands.wizard.invalid.operation.msg'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});
		}
	}
	
	, onAddCalculatedField : function(win, formState, targetNode, fieldType){
		
		var nodeType;
		nodeType = targetNode.attributes.type || targetNode.attributes.attributes.type;
		
		var entityId = (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_CALCULATED_FIELD)? targetNode.parentNode.id: targetNode.id;
		var f = {
			alias: formState.alias
			, id: formState
			, type: formState.type
			, filedType: fieldType
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
		

    	
		if(nodeType == Sbi.settings.qbe.constants.NODE_TYPE_CALCULATED_FIELD) {
			targetNode.text = formState.alias;
			targetNode.attributes.attributes.formState = formState;
		} else if (nodeType == Sbi.settings.qbe.constants.NODE_TYPE_ENTITY) {
			var node = new Ext.tree.TreeNode({
    			text: formState.alias,
    			leaf: true,
    			type: fieldType, 
    			longDescription: formState.expression,
    			formState: formState, 
    			iconCls: 'calculation',
    			attributes:{
        			text: formState.alias,
        			leaf: true,
        			type: Sbi.settings.qbe.constants.NODE_TYPE_CALCULATED_FIELD, 
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
				   title:LN('sbi.qbe.bands.wizard.invalid.operation'),
				   msg: LN('sbi.qbe.bands.wizard.invalid.operation.msg'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
			});
		}
	}
	
	, initMenu: function() {
		 this.menu = new Ext.menu.Menu({
             id:'feeds-ctx',
             items: [
             // ACID operations on nodes
             '-',{
            	 text:LN('sbi.qbe.calculatedFields.add'),
                 iconCls:'add',
                 handler:function(){
            	   	this.addInLineCalculatedField(this.ctxNode);	         	 	
	             },
                 scope: this
             },{
            	 text:LN('sbi.qbe.calculatedFields.edit'),
                 iconCls:'edit',
                 handler:function(){
	         	 	this.editField(this.ctxNode);
	             },
                 scope: this
             },{
            	 text: LN('sbi.qbe.menu.bands.add'),
                 iconCls:'slot',
                 handler:function(){
            		this.addSlot(this.ctxNode);	
	             },
                 scope: this
             },{
            	 text: LN('sbi.qbe.menu.bands.edit'),
                 iconCls:'slot',
                 handler:function(){
            		this.editSlot(this.ctxNode);	
	             },
                 scope: this
             },{
            	 text:LN('sbi.qbe.calculatedFields.remove'),
                 iconCls:'remove',
                 handler:  function() {
	            	 this.ctxNode.ui.removeClass('x-node-ctx');
	            	 this.removeCalculatedField(this.ctxNode);
	            	 this.ctxNode = null;
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
    }
	
	, oonLoad: function(treeLoader, node, response) {
		this.rootNode = this.tree.root;
		this.fireEvent('load', this, treeLoader, node, response);
	}
	
	, oonLoadException: function(treeLoader, node, response) {
		Sbi.exception.ExceptionHandler.handleFailure(response, treeLoader.baseParams || {});
	}
});