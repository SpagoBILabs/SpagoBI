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
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageModels = function(config, ref) { 
	var paramsList = {MESSAGE_DET: "MODEL_NODES_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODEL_NODES_SAVE"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODEL_NODE_DELETE"};
	
	this.configurationObject = {};
	
	this.configurationObject.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODELS_ACTION'
		, baseParams: paramsList
	});	
	this.configurationObject.saveTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODELS_ACTION'
		, baseParams: paramsSave
	});
	this.configurationObject.deleteTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODELS_ACTION'
		, baseParams: paramsDel
	});
	//reference to viewport container
	this.referencedCmp = ref;
	this.initConfigObject();
	config.configurationObject = this.configurationObject;

	var c = Ext.apply({}, config || {}, {});

	Sbi.kpi.ManageModels.superclass.constructor.call(this, c);	 	
	
}

Ext.extend(Sbi.kpi.ManageModels, Sbi.widgets.TreeDetailForm, {
	
	configurationObject: null
	, gridForm:null
	, mainElementsStore:null
	, root:null
	, referencedCmp : null

	,initConfigObject: function(){

		this.configurationObject.treeTitle = LN('sbi.models.listTitle');;
	
		this.configurationObject.panelTitle = LN('sbi.models.panelTitle');
		this.configurationObject.listTitle = LN('sbi.models.listTitle');
		
		this.initTabItems();
    }

	,initTabItems: function(){
		//Store of the combobox
 	    this.typesStore = new Ext.data.SimpleStore({
 	        fields: ['typeId', 'typeCd', 'typeDs', 'domainCd'],
 	        data: config.nodeTypesCd,
 	        autoLoad: false
 	    });
		/*DETAIL FIELDS*/

	 	   this.detailFieldName = new Ext.form.TextField({
	          	 maxLength:100,
	        	 minLength:1,
	        	 regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.generic.name'),
	             allowBlank: false,
	             //validationEvent:true,
	             name: 'name'
	         });
	 			  
	 	   this.detailFieldCode = new Ext.form.TextField({
	          	 maxLength:45,
	        	 minLength:1,
	        	 regex : new RegExp("^([A-Za-z0-9_])+$", "g"),
	        	 regexText : LN('sbi.roles.alfanumericString2'),
	             fieldLabel:LN('sbi.generic.code'),
	             allowBlank: false,
	             //validationEvent:true,
	             name: 'code'
	         });  
	 		   
	 	   this.detailFieldDescr = new Ext.form.TextArea({
	          	 maxLength:400,
	       	     width : 250,
	             height : 80,
	        	 regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.generic.descr'),
	             //validationEvent:true,
	             name: 'description'
	         });
	 	 		   
	 	   this.detailFieldLabel = new Ext.form.TextField({
	        	 minLength:1,
	        	 regex : new RegExp("^([A-Za-z0-9_])+$", "g"),
	        	 regexText : LN('sbi.roles.alfanumericString2'),
	             fieldLabel:LN('sbi.generic.label'),
	             allowBlank: false,
	             //validationEvent:true,
	             name: 'label'
	         });	  
	 	 	 			  
	 	   this.detailFieldKpi = new Ext.form.TextField({
	 		   	 itemId: 'model-detailFieldKpi',
	 		   	 id: 'model-detailFieldKpi',
	        	 minLength:1,
	        	 regex : new RegExp("^([A-Za-z0-9_])+$", "g"),
	        	 regexText : LN('sbi.roles.alfanumericString2'),
	             fieldLabel: LN('sbi.generic.kpi'),
	             allowBlank: false,
	             readOnly: true,
	             name: 'kpi'
	         });	 
	 		   
	 	   this.detailFieldNodeType =  new Ext.form.ComboBox({
	        	  name: 'typeCd',
	              store: this.typesStore,
	              fieldLabel: LN('sbi.generic.nodetype'),
	              displayField: 'typeCd',   // what the user sees in the popup
	              valueField: 'typeId',        // what is passed to the 'change' event
	              typeAhead: true,
	              forceSelection: true,
	              mode: 'local',
	              triggerAction: 'all',
	              selectOnFocus: true,
	              editable: false,
	              allowBlank: false
	              //,validationEvent:true
	          });
	 	  this.detailFieldTypeDescr = new Ext.form.DisplayField({
	          	 maxLength:400,
	       	     width : 250,
	             height : 80,
	        	 regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             //validationEvent:true,
	             readOnly: true,
	             name: 'typeDescr'
	         });
	 	   /*END*/
	 	  
	 	  this.configurationObject.tabItems = [{
		        title: LN('sbi.generic.details')
		        , itemId: 'detail'
		        , width: 430
		        , items: {
			   		 id: 'items-detail-models',   	
		 		   	 itemId: 'items-detail1',   	              
		 		   	 columnWidth: 0.4,
		             xtype: 'fieldset',
		             labelWidth: 90,
		             defaults: {width: 140, border:false},    
		             defaultType: 'textfield',
		             autoHeight: true,
		             autoScroll  : true,
		             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
		             border: false,
		             style: {
		                 //"margin-left": "10px", 
		                 "background-color": "#f1f1f1",
		                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
		             },
		             items: [this.detailFieldLabel, this.detailFieldCode, this.detailFieldName,  this.detailFieldDescr,
		                      this.detailFieldKpi, this.detailFieldNodeType, this.detailFieldTypeDescr]
		    	}
		    }];

	}
	

    //OVERRIDING save method
	,save : function() {
    	var jsonStr = '[';

		Ext.each(this.nodesToSave, function(node, index) {
			if(node instanceof Ext.tree.TreeNode){
				//alert(node.attributes.name);
				jsonStr += Ext.util.JSON.encode(node.attributes);
				jsonStr +=',';
			}
		});

		jsonStr += ']';
		
		var params = {
			nodes : jsonStr
		};

		Ext.Ajax.request( {
			url : this.services['saveTreeService'],
			success : function(response, options) {
				if(response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if(content !== undefined && content !== null){
	      				var hasErrors = false;
	      				for (var key in content) {
		      				  var value = content[key];
		      				  var nodeSel = Ext.getCmp('model-maintree').getNodeById(key);
		      				  //response returns key = guiid, value = 'KO' if operation fails, or modelId if operation succeded
		      				  if(value  == 'KO'){
		      					  hasErrors= true;
		 		      			  ///contains error gui ids      						  
	      						  nodeSel.attributes.error = true;
	      						  Ext.fly(nodeSel.getUI().getEl()).applyStyles('{ border: 1px solid red; font-weight: bold; font-style: italic; color: #cd2020; text-decoration: underline; }');
		      				  }else{
		      					  nodeSel.attributes.error = false; 
		      					  nodeSel.attributes.modelId = value; 
		      					  Ext.fly(nodeSel.getUI().getEl()).applyStyles('{ border: 0; font-weight: normal; font-style: normal; text-decoration: none; }');
		      					  this.fireEvent('parentsave-complete', nodeSel);
		      				  }
		      				
		      		    }
	      				
	      				if(hasErrors){
	      					alert(LN('sbi.generic.savingItemError'));
	      					
	      				}else{
	      					///success no errors!
	      					this.cleanAllUnsavedNodes();
	      					alert(LN('sbi.generic.resultMsg'));
		      				this.referencedCmp.modelsGrid.mainElementsStore.load();
	      				}
	      			}else{
	      				alert(LN('sbi.generic.savingItemError'));
	      			}
				}else{
      				this.cleanAllUnsavedNodes();
      				alert(LN('sbi.generic.resultMsg'));
      				this.referencedCmp.modelsGrid.mainElementsStore.load();
				}
      			this.mainTree.doLayout();
      			this.referencedCmp.modelsGrid.getView().refresh();
				this.referencedCmp.modelsGrid.doLayout();
				
				
				
      			return;
			},
			scope : this,
			failure : function(response) {
				if(response.responseText !== undefined) {
					alert("Error");
				}
			},
			params : params
		});
		
    }
	,saveParentNode : function(parent, child) {
		var jsonStr = '[';
    	jsonStr +=  Ext.util.JSON.encode(parent.attributes)
    	jsonStr += ']';

		var params = {
			nodes : jsonStr
		};

		Ext.Ajax.request( {
			url : this.services['saveTreeService'],
			success : function(response, options) {
				if(response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if(content !== undefined && content !== null){
	      				var hasErrors = false;
	      				for (var key in content) {
		      				  var value = content[key];
		      				  var nodeSel = Ext.getCmp('model-maintree').getNodeById(key);
		      				  //response returns key = guiid, value = 'KO' if operation fails, or modelId if operation succeded
		      				  if(value  == 'KO'){
		      					  hasErrors= true;
		 		      			  ///contains error gui ids      						  
	      						  nodeSel.attributes.error = true;
	      						  Ext.fly(nodeSel.getUI().getEl()).applyStyles('{ border: 1px solid red; font-weight: bold; font-style: italic; color: #cd2020; text-decoration: underline; }');
		      				  }else{
		      					  nodeSel.attributes.error = false; 
		      					  nodeSel.attributes.modelId = value; 
		      					  Ext.fly(nodeSel.getUI().getEl()).applyStyles('{ border: 0; font-weight: normal; font-style: normal; text-decoration: none; }');
		      					  
		      					  //completes child node instanciation
			  	        		  this.selectedNodeToEdit = child;
				        		  //this.mainTree.getSelectionModel().select(child);

			  	        		  child.attributes.parentId = parent.attributes.modelId;
			        			  var size = this.nodesToSave.length;
			        			  this.nodesToSave[size] = child;
	      				      }
		      				
		      		    }
	      				
	      				if(hasErrors){
	      					alert(LN('sbi.generic.savingItemError'));
	      					
	      				}else{
	      					///success no errors!
	      					this.cleanAllUnsavedNodes();
	      					alert(LN('sbi.generic.resultMsg'));
		      				this.referencedCmp.modelsGrid.mainElementsStore.load();
	      				}
	      			}else{
	      				alert(LN('sbi.generic.savingItemError'));
	      			}
				}else{
      				this.cleanAllUnsavedNodes();
      				alert(LN('sbi.generic.resultMsg'));
      				this.referencedCmp.modelsGrid.mainElementsStore.load();
				}
      			this.mainTree.doLayout();
      			this.referencedCmp.modelsGrid.getView().refresh();
				this.referencedCmp.modelsGrid.doLayout();
				
				
				
      			return;
			},
			scope : this,
			failure : function(response) {
				if(response.responseText !== undefined) {
					alert("Error");
				}
			},
			params : params
		});
		
    }
	,editNode : function(field, newVal, oldVal) {
		var node = this.selectedNodeToEdit;
		if (node !== undefined) {
			var val = node.text;
			var aPosition = val.indexOf(" - ");
			var name = "";
			var code = "";
			if (aPosition !== undefined && aPosition != -1) {
				name = val.substr(aPosition + 3);
				code = val.substr(0, aPosition);
				if (field.getName() == 'name') {
					name = newVal;
				} else if (field.getName() == 'code') {
					code = newVal;
				}
			}
			var text = code + " - " + name;
			node.setText(text);
			node.attributes.toSave = true;

			node.attributes.name = name;
			node.attributes.code = code;

			if(this.referencedCmp.modelsGrid.emptyRecord != null){
				this.referencedCmp.modelsGrid.emptyRecord.set('name', name);
				this.referencedCmp.modelsGrid.emptyRecord.set('code', code);
				
				this.referencedCmp.modelsGrid.getView().refresh();
				this.referencedCmp.modelsGrid.doLayout();
			}
		}
	}
	, editNodeAttribute: function(field, newVal, oldVal) {
		var node = this.selectedNodeToEdit;
		if (node !== undefined && node !== null) {
			node.attributes.toSave = true;
			var fName = field.name;
			if(fName == 'description'){
				node.attributes.description = newVal;
			}else if(fName == 'label'){
				node.attributes.label = newVal;
			}
		}
	}
	, setDomainType: function(field, rec, index) {
		var node = this.selectedNodeToEdit;		
		if (node !== undefined && node !== null) {
			node.attributes.toSave = true;
			node.attributes.typeId = rec.data.typeId;
			node.attributes.type = rec.data.typeCd;//unused server side		

			this.detailFieldTypeDescr.setValue(rec.data.typeDs);			
		}
	}
	,fillDetail : function(sel, node) {
		if(node !== undefined && node != null){
			var val = node.text;
			if (val != null && val !== undefined) {
				var aPosition = val.indexOf(" - ");
	
				var name = node.attributes.name;
				var code = node.attributes.code;
				if (aPosition !== undefined && aPosition != -1) {
					name = val.substr(aPosition + 3);
					code = val.substr(0, aPosition)
				}
	
				this.detailFieldDescr.setValue(node.attributes.description);			
				this.detailFieldLabel.setValue(node.attributes.label);
				this.detailFieldKpi.setValue(node.attributes.kpi);
				this.detailFieldNodeType.setValue(node.attributes.type);
	
				this.detailFieldName.setValue(name);
				this.detailFieldCode.setValue(code);
				
				this.detailFieldTypeDescr.setValue(node.attributes.typeDescr);
			}
		}
	}
	,renderTree : function(tree) {
		tree.getLoader().nodeParameter = 'modelId';
		tree.getRootNode().expand(false, /*no anim*/false);
	}
	,selectNode : function(field) {
		
		/*utility to store node that has been edited*/
		this.selectedNodeToEdit = this.mainTree.getSelectionModel().getSelectedNode();
		
		if(this.selectedNodeToEdit.attributes.toSave === undefined || this.selectedNodeToEdit.attributes.toSave == false){
			var size = this.nodesToSave.length;
			this.nodesToSave[size] = this.selectedNodeToEdit;
		}//else skip because already taken
	}
	,setListeners : function() {
			this.mainTree.getSelectionModel().addListener('selectionchange',
					this.fillDetail, this);
			this.mainTree.addListener('render', this.renderTree, this);

			/* form fields editing */
			this.detailFieldName.addListener('focus', this.selectNode, this);
			this.detailFieldName.addListener('change', this.editNode, this);

			this.detailFieldCode.addListener('focus', this.selectNode, this);
			this.detailFieldCode.addListener('change', this.editNode, this);

			this.detailFieldDescr.addListener('focus', this.selectNode, this);
			this.detailFieldDescr.addListener('change', this.editNodeAttribute, this);

			this.detailFieldLabel.addListener('focus', this.selectNode, this);
			this.detailFieldLabel.addListener('change', this.editNodeAttribute, this);
			
			this.detailFieldNodeType.addListener('focus', this.selectNode, this);
			this.detailFieldNodeType.addListener('select', this.setDomainType, this);

	},	
	createRootNodeByRec: function(rec) {
			var iconClass = '';
			var cssClass = '';
			if (rec.get('kpi') !== undefined && rec.get('kpi') != null
					&& rec.get('kpi') != '') {
				iconClass = 'has-kpi';
			}
			if (rec.get('error') !== undefined && rec.get('error') != false) {
				cssClass = 'has-error';
			}
			var node = new Ext.tree.AsyncTreeNode({
		        text		: this.rootNodeText,
		        expanded	: true,
		        leaf		: false,
				modelId 	: this.rootNodeId,
				id			: this.rootNodeId,
				label		: rec.get('label'),
				type		: rec.get('type'),
				typeId		: rec.get('typeId'),
				description	: rec.get('description'),
				typeDescr	: rec.get('typeDescr'),
				kpi			: rec.get('kpi'),
				kpiId		: rec.get('kpiId'),
				code		: rec.get('code'),
				name		: rec.get('name'),
				iconCls		: iconClass,
				cls			: cssClass,
		        draggable	: false
		    });
			return node;
	}
	, cleanAllUnsavedNodes: function() {
		
		Ext.each(this.nodesToSave, function(node, index) {
			node.attributes.toSave = false;  
					
		});
		this.nodesToSave = new Array();
	}
});
