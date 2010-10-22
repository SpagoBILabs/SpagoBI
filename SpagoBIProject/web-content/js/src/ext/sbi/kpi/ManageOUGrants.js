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
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageOUGrants = function(config, ref) { 
	this.showModelUuid = config.showModelUuid;

	var paramsNodeList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_NODES_LIST"};
	var paramsNode = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_NODE_DETAILS"};
	var paramsList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_LIST"};

	var paramsOUList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "OU_LIST"};
	var paramsOUChildList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "OU_CHILDS_LIST"};
	var paramsOURoot = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "OU_HIERARCHY_ROOT"};
	var paramsOUInsert = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "OU_GRANT_INSERT"};

	this.configurationObject = {};

	this.configurationObject.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsNodeList
	});	

	this.configurationObject.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsList
	});	

	this.configurationObject.manageNodeListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
			, baseParams: paramsNode
	});	

	this.configurationObject.manageOUListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_OUS_ACTION'
			, baseParams: paramsOUList
	});	

	this.configurationObject.manageOURootService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_OUS_ACTION'
			, baseParams: paramsOURoot
	});	

	this.configurationObject.manageOUChildService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_OUS_ACTION'
			, baseParams: paramsOUChildList
	});	

	this.configurationObject.saveGrantService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_OUS_ACTION'
			, baseParams: paramsOUInsert
	});	



	this.initConfigObject();

	//the default values 2 checks of the south panel
	this.hideOULeafs = true;//hide the leaf ous
	this.hideNotActivatedOUS = false;//hide the ous without grants


	config.configurationObject = this.configurationObject;
	config.toolsMenuItems= this.getToolsMenu();

	var c = Ext.apply({}, config || {}, {});

	Sbi.kpi.ManageOUGrants.superclass.constructor.call(this, c);	 	

	//after the 2 trees have been displayed we select the root node of the ou tree
	this.treePanel.on('afterlayout', function(){		
		this.leftTree.getSelectionModel().select(this.leftTree.getRootNode());
		this.updateKpisCheck(this.leftTree.getRootNode());},
		this);

};

Ext.extend(Sbi.kpi.ManageOUGrants, Sbi.widgets.KpiTreeOuTreePanel, {

	configurationObject: null
	, gridForm:null
	, mainElementsStore:null
	, root:null
	, droppedSubtreeToSave: new Array()
	, kpitreeLoader : null
	, outreeLoader : null
	, newRootNode: null
	, existingRootNode: null
	, hideNotActivatedOUS: null
	, hideOULeafs: null
	, detailFieldFrom: null
	, ouHierarchy: null
	, kpiModelInstanceRoot: null
	, selectedGrantId: null
	, detailsForm: null
	
	,initConfigObject: function(){
		this.initTabItems();
	}
	
	,initTabItems: function(){
		var thisPanel = this;
	
		//loader for the tree of the kpis
		this.kpitreeLoader =new Ext.tree.TreeLoader({
			dataUrl: this.configurationObject.manageTreeService,
			createNode: function(attr) {
	
				if (attr.modelInstId) {
					attr.id = attr.modelInstId;
				}
	
				if (attr.kpiInstId !== undefined && attr.kpiInstId !== null
						&& attr.kpiInstId != '') {
					attr.iconCls = 'has-kpi';
				}
				if (attr.error !== undefined && attr.error !== false) {
					attr.cls = 'has-error';
				}
				var attrKpiCode = '';
				if(attr.kpiCode !== undefined){
					attrKpiCode = ' - '+attr.kpiCode;
				}
				attr.qtip = attr.modelCode+' - '+attr.name+ attrKpiCode;
	
				attr.checked = false;
	
				var node = Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
				thisPanel.addKPINodeListeners(node);
				return node;
			}
	
		});
	
		//loader for the tree of the ous
		this.outreeLoader =new Ext.tree.TreeLoader({
			dataUrl: thisPanel.configurationObject.manageOUChildService,
			baseParams : {'grantId': thisPanel.selectedGrantId},
			createNode: function(attr) {
				if (attr.nodeId) {
					attr.id = attr.nodeId;
				}
				if (attr.ou!=null) {
					attr.text = attr.ou.label;
					attr.qtip = attr.ou.name;
				}
				//attr.iconCls = 'ou';
				if(thisPanel.hideNotActivatedOUS && (attr.modelinstancenodes==null || attr.modelinstancenodes.length==0) ){
					return;
				}
	
				var node = Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
	
				if(node.isLeaf() && thisPanel.hideOULeafs){//for hide the leafs
					return;
				}

				attr.modelinstancenodesOfThisSession = new Array();
				
				node.on('click',thisPanel.updateKpisCheck,thisPanel);
				node.on('append', function( tree, thisNode, childNode,index ) {
					if(childNode.attributes.modelinstancenodes==null || childNode.attributes.modelinstancenodes.length==0){
						childNode.attributes.modelinstancenodes = thisNode.attributes.modelinstancenodesOfThisSession.slice(0);//copy the array
						childNode.attributes.modelinstancenodesOfThisSession = thisNode.attributes.modelinstancenodesOfThisSession.slice(0);//copy the array
					}
				},this);

				
				return node;
			}
		});  
	
		//fileds of the detail panel
		this.detailFieldLabel = new Ext.form.TextField({
			minLength:1,
			fieldLabel:LN('sbi.generic.label'),
			allowBlank: false,
			//validationEvent:true,
			name: 'label'
		});	  
	
		this.detailFieldName = new Ext.form.TextField({
			maxLength:100,
			minLength:1,
			fieldLabel: LN('sbi.generic.name'),
			allowBlank: false,
			//validationEvent:true,
			name: 'name'
		});
	
	
		this.detailFieldDescr = new Ext.form.TextArea({
			maxLength:400,
			width : 250,
			height : 80,
			fieldLabel: LN('sbi.generic.descr'),
			//validationEvent:true,
			name: 'description',
			allowBlank: false
		});
	
		this.detailFieldFrom = new Ext.form.DateField({
			id: 'from',
			name: 'from',
			fieldLabel: LN('sbi.generic.from'),
			format: 'd/m/Y',
			allowBlank: false
		});
	
		this.detailFieldTo = new Ext.form.DateField({
			id: 'to',
			name: 'to',
			fieldLabel: LN('sbi.generic.to'),
			format: 'd/m/Y',
			allowBlank: false
		});
	
		var baseConfig = {drawFilterToolbar:false}; 
	
		var kpiInstStore = new Ext.data.JsonStore({
			root: 'rows',
			url: this.configurationObject.manageListService,
			fields: ['modelText','kpiCode','text','modelCode','kpiId','modelInstId','label','leaf','modelId','modelName','modelTypeDescr','modelType','kpiName','description','name','kpiInstId','modelDescr','kpiInstSaveHistory']
		});
	
		var OUStore = new Ext.data.JsonStore({
			url: this.configurationObject.manageOUListService,
			fields: ['id','label','name','description']
		});
	
		this.detailFieldOUHierarchy = new Sbi.widgets.LookupField(Ext.apply( baseConfig, {
			name: 'name',
			valueField: 'id',
			displayField: 'label',
			descriptionField: 'description',
			fieldLabel: LN('sbi.grants.ouhierarchy'),
			store: OUStore,
			singleSelect: true,
			allowBlank: false,
			cm: new Ext.grid.ColumnModel([
			                              new Ext.grid.RowNumberer(),
			                              {   header: LN('sbi.generic.label'),
			                            	  dataIndex: 'label',
			                            	  width: 75
			                              },
			                              {   header: LN('sbi.generic.name'),
			                            	  dataIndex: 'name',
			                            	  width: 75
			                              },
			                              {   header: LN('sbi.generic.descr'),
			                            	  dataIndex: 'description',
			                            	  width: 75
			                              }
			                              ])
		}));
	
		var kpiInstStore = new Ext.data.JsonStore({
			root: 'rows',
			url: this.configurationObject.manageListService,
			fields: ['modelText','kpiCode','text','modelCode','kpiId','modelInstId','label','leaf','modelId','modelName','modelTypeDescr','modelType','kpiName','description','name','kpiInstId','modelDescr','kpiInstSaveHistory']
		});
	
		this.detailFieldKpiHierarchy = new Sbi.widgets.LookupField(Ext.apply( baseConfig, {
			name: 'name',
			valueField: 'modelInstId',
			displayField: 'modelText',
			descriptionField: 'modelText',
			fieldLabel: LN('sbi.grants.kpihierarchy'),
			store: kpiInstStore,
			singleSelect: true,
			allowBlank: false,
			cm: new Ext.grid.ColumnModel([
			                              new Ext.grid.RowNumberer(),
	
			                              {   header: LN('sbi.generic.name'),
			                            	  dataIndex: 'name',
			                            	  width: 75
			                              },
			                              {   header: LN('sbi.modelinstances.code'),
			                            	  dataIndex: 'modelCode',
			                            	  width: 75
			                              }
			                              ])
		}));
	
		var tbSave2 = new Ext.Toolbar( {
			buttonAlign : 'right',
			items : [ 
			         new Ext.Toolbar.Button({ 
			        	 text: LN('sbi.grants.loadtrees'),
			        	 iconCls : 'icon-execute',
			        	 handler: function(){this.loadTrees(); this.setActiveTab(1);},
			        	 width : 30,
			        	 scope: thisPanel
			         }),
			         new Ext.Toolbar.Button( {
			        	 text : LN('sbi.generic.update'),
			        	 iconCls : 'icon-save',
			        	 handler : this.save,
			        	 width : 30,
			        	 scope : thisPanel
			         })
			         ]
		});
	
		
		this.detailsForm = new Ext.FormPanel({
       	 title: LN('sbi.generic.details')
    	 , itemId: 'detail'
    	 ,tbar: tbSave2
    	 , width: 430
    	 , items: [{
    		 id: 'items-detail',   	
    			 itemId: 'items-detail',               
    			 columnWidth: 2,
    			 xtype: 'fieldset',
    			 labelWidth: 150,
    			 defaults: {width: 200, border:false},    
    			 defaultType: 'textfield',
    			 autoHeight: true,
    			 autoScroll  : true,
    			 bodyStyle: Ext.isIE ? 'padding:15 0 5px 10px;' : 'padding:10px 15px;',
    					 border: false,
    					 style: {
    						 "margin-left": "10px", 
    						 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
    					 },
    					 items: [this.detailFieldLabel, this.detailFieldName,  this.detailFieldDescr,
    					         this.detailFieldFrom, this.detailFieldTo, this.detailFieldOUHierarchy, 
    					         this.detailFieldKpiHierarchy]
    		 }]});
		
		//the detail panel
		this.configurationObject.tabItems = [this.detailsForm];  
	}
	
	//add the listeners to the kpi nodes
	, addKPINodeListeners: function(node){
		node.on('expand', this.getSelectedOUAndUpdateKpisCheck,this);
		/*
		 * When the user change the check of a kpi node
		 * we add/remove the id of the kpi from the list
		 * of the grants of the selected ou node
		 * */
		node.on('checkchange', function(node, checked){
			node.suspendEvents(false);
	
			//change to the selected node and all it's child the kpi model instance list
			var subTreeNodes = this.getSubTreeNodes(this.leftTree.getSelectionModel().getSelectedNode());
			for(var j=0; j<subTreeNodes.length; j++){
				var modelinstancenodes = subTreeNodes[j].attributes.modelinstancenodes;
				var modelinstancenodesOfThisSession = subTreeNodes[j].attributes.modelinstancenodesOfThisSession;
				if(!checked){
					for(var i=0; i<modelinstancenodes.length; i++){
						if(modelinstancenodes[i]==node.id){
							modelinstancenodes.splice(i,1);
							break;
						}
					}
					for(var i=0; i<modelinstancenodesOfThisSession.length; i++){
						if(modelinstancenodesOfThisSession[i]==node.id){
							modelinstancenodesOfThisSession.splice(i,1);
							break;
						}
					}
				}else{
					modelinstancenodes.push(node.id);
					modelinstancenodesOfThisSession.push(node.id);
				}
			}
			node.resumeEvents();
		},this);
	}
	
	//discovers the selected ou and update the tree panel of the kpis with the grant of the selected ou 
	,getSelectedOUAndUpdateKpisCheck: function(node){
		var selectedOUNode = this.leftTree.getSelectionModel().getSelectedNode();
		this.updateKpisCheck(selectedOUNode);
	}
	
	//update the tree panel of the kpis with the grant of the selected ou 
	,updateKpisCheck : function(node, event){
		var n;
		var checkedNodes = this.rightTree.getChecked();
	
		//uncheck all the kpis
		for(var i=0; i<checkedNodes.length; i++){
			checkedNodes[i].suspendEvents(false);
			checkedNodes[i].getUI().toggleCheck(false);
			checkedNodes[i].resumeEvents( );
		}	
	
		//disable the kpis not living in the father list.
			//If the node is the root it enables all the nodes
		this.deepDisableCheck(this.rightTree.getRootNode(), node);

		if(node.parentNode!=null){
			//If there is no kpis for the node it copies the kpis selected for the father
			if(node.attributes.modelinstancenodes!=null && node.attributes.modelinstancenodes.length>0){
				//remove the kpis not living in the father list from the node.attributes.modelinstancenodes 
				for(var i=node.attributes.modelinstancenodes.length-1; i>=0; i--){
					n = this.rightTree.getNodeById(node.attributes.modelinstancenodes[i]);
					if(n!=null && n.getUI().checkbox.disabled){
						node.attributes.modelinstancenodes.splice(i,1);
					}
				}
			}
			//If there is no kpis for the node it copies the kpis selected for the father
			if(node.attributes.modelinstancenodesOfThisSession!=null && node.attributes.modelinstancenodesOfThisSession.length>0){
				//remove the kpis not living in the father list from the node.attributes.modelinstancenodes 
				for(var i=node.attributes.modelinstancenodesOfThisSession.length-1; i>=0; i--){
					n = this.rightTree.getNodeById(node.attributes.modelinstancenodesOfThisSession[i]);
					if(n!=null && n.getUI().checkbox.disabled){
						node.attributes.modelinstancenodesOfThisSession.splice(i,1);
					}
				}
			}
		}

		//check the kpis living in the node.attributes.modelinstancenodes
		if(node!=null && node.attributes.modelinstancenodes!=null){
			for(var i=0; i<node.attributes.modelinstancenodes.length; i++){
				n = this.rightTree.getNodeById(node.attributes.modelinstancenodes[i]);
				if(n!=null && n.getUI().checkbox!=null){//enable the checks
					n.suspendEvents(false);
					n.getUI().toggleCheck(true);
					n.resumeEvents( );
				}
			}
		}
	}
	
	//Set disable all the nodes of the subtree rooted in uoNode.. 
	//If the uoNode has no father (id est, it is the root) than it
	//Enables all the check boxes
	,deepDisableCheck: function(kpiNode, uoNode){
		var children = kpiNode.childNodes;
		if(children!=null){
			for(var i=0; i<children.length; i++){
				this.deepDisableCheck(children[i],uoNode);
			}
		}
	
		if(uoNode.parentNode!=null){
			kpiNode.getUI().checkbox.disabled=true;
			var parentKpis = uoNode.parentNode.attributes.modelinstancenodes;
			if(parentKpis!=null){
				for(var i=0; i<parentKpis.length; i++){
					if(parentKpis[i] == kpiNode.id){
						kpiNode.getUI().checkbox.disabled=false;
						break;
					}
				}
			}
		}else{//the root must have all the kpi enabled
			kpiNode.getUI().checkbox.disabled=false;
		}
	}

	,renderTree : function(tree) {
		tree.getLoader().nodeParameter = 'modelInstId';
		tree.getRootNode().expand(false, /*no anim*/false);
	}
	
	,renderTreeOU : function(tree) {
		tree.getLoader().nodeParameter = 'nodeId';
		tree.getRootNode().expand(false, /*no anim*/false);
	}
	
	
	
	,reloadTree : function(tree, rec) {
		var newroot = this.createRootNodeByRec(rec);
		tree.setRootNode(newroot);		
		tree.getSelectionModel().select(newroot);
		tree.doLayout();
	}
	
	,setListeners : function() {
		this.rightTree.addListener('render', this.renderTree, this);
		this.leftTree.addListener('render', this.renderTreeOU, this);
	}
	
	
	//Build the tools menu (the south panel)
	,getToolsMenu: function(){
		var tools = new Array();
	
		var hideOULeafsRadio = new Ext.form.Checkbox({
			boxLabel: LN('sbi.grants.hide.labels'),
			checked: true
		});
	
		hideOULeafsRadio.on('check',function(radio, checked){
			this.hideOULeafs=checked;
			this.reloadTree(this.leftTree, this.nrec);
		},this);
	
		var hideNotActivatedOUSCheckbox = new Ext.form.Checkbox({
			boxLabel: LN('sbi.grants.hide.nogrants.ous'),
			checked: false
		});
	
		hideNotActivatedOUSCheckbox.on('check',function(check, checked){
			this.hideNotActivatedOUS=checked;
			this.reloadTree(this.leftTree, this.nrec);
		},this);
	
		tools.push(hideOULeafsRadio);
		tools.push(hideNotActivatedOUSCheckbox);
	
		return tools;
	},
	
	//save the ou hierarchy with the selected grants
	save : function() {
		if(this.detailsForm.getForm().isValid()){
			var thisPanel = this;
			var grantNodes = Ext.encode(this.getAllNodesWithAbilitation(this.leftTree.getRootNode()));
			var grant =  Ext.encode(this.getGrantFormValues());
			Ext.Ajax.request({
				url: this.configurationObject.saveGrantService,
				params: {'grantnodes': grantNodes, 'grant': grant},
				method: 'GET',
				success: function(response, options) {
					if (response !== undefined) {
						Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.generic.resultMsg'),'');
						thisPanel.fireEvent('saved');
					} else {
						Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.savingItemError'), LN('sbi.generic.serviceError'));
					}
				},
				failure: function() {
					Ext.MessageBox.show({
						title: LN('sbi.generic.error'),
						msg: LN('sbi.generic.savingItemError'),
						width: 150,
						buttons: Ext.MessageBox.OK
					});
				}
				,scope: this
		
			});
		}
	},
	
	//Load the trees from the server
	loadTrees:	function() {
		var thisPanel = this;
		thisPanel.outreeLoader.baseParams.grantId=thisPanel.selectedGrantId;
		var modelInstId = thisPanel.detailFieldKpiHierarchy.getValue();
		var ouHierarchyId = thisPanel.detailFieldOUHierarchy.getValue();
		if(modelInstId!=null && ouHierarchyId!=null && modelInstId!='undefined' && ouHierarchyId!='undefined' && modelInstId!='' && ouHierarchyId!=''){
			//Ajax request used to load the model instance tree
			Ext.Ajax.request({
				url: thisPanel.configurationObject.manageNodeListService,
				params: {'modelInstId': modelInstId},
				method: 'GET',
				success: function(response, options) {
					if (response !== undefined) {		
						if(response.responseText !== undefined) {
							thisPanel.kpiModelInstanceRoot = Ext.util.JSON.decode( response.responseText )
							//Ajax request used to load the organizationa units tree
							Ext.Ajax.request({
								url: thisPanel.configurationObject.manageOURootService,
								params: {'hierarchyId': ouHierarchyId, 'grantId': thisPanel.selectedGrantId},
								method: 'GET',
								success: function(response, options) {
									if (response !== undefined) {		
										if(response.responseText !== undefined) {
											thisPanel.ouHierarchy = Ext.util.JSON.decode( response.responseText )
											thisPanel.fireEvent('changeOU_KPI',thisPanel.kpiModelInstanceRoot, thisPanel.ouHierarchy);
										} else {
											Sbi.exception.ExceptionHandler.showErrorMessage( LN('sbi.generic.serviceError'),  LN('sbi.generic.serviceError'));
										}
									} else {
										Sbi.exception.ExceptionHandler.showErrorMessage( LN('sbi.generic.serviceError'),  LN('sbi.generic.serviceError'));
									}
								},
								failure : function(response) {
									if(response.responseText !== undefined) {
										Sbi.exception.ExceptionHandler.showErrorMessage( LN('sbi.generic.serviceError'), LN('sbi.generic.serviceError'));
									}
								},
								scope: this
							}); 
						} else {
							Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
						}
					} else {
						Sbi.exception.ExceptionHandler.showErrorMessage('Error while saving User', 'Service Error');
					}
				},
				failure : function(response) {
					if(response.responseText !== undefined) {
						alert(LN('sbi.generic.savingItemError'));
					}
				},
				scope: this
			});
		}
	},
	
	//read the details form and build a grant object
	getGrantFormValues: function(){
		var grant = {
				label: this.detailFieldLabel.getValue(), 
				name: this.detailFieldName.getValue(),  
				description: this.detailFieldDescr.getValue(),
				startdate: this.detailFieldFrom.getValue(), 
				enddate: this.detailFieldTo.getValue(), 
				hierarchy: this.detailFieldOUHierarchy.getValue(),
				modelinstance: this.detailFieldKpiHierarchy.getValue(),
				id: this.selectedGrantId
		};
		return grant;
	},
	
	//return an array with couples ouId/grant for the passed ou node
	getAllNodesWithAbilitation: function(node){
		var children = node.childNodes;
		var array = new Array(); 
		if(node.attributes.modelinstancenodes!=null){
			for(var i=0; i<node.attributes.modelinstancenodes.length; i++){
				var c={
						ouPath: node.attributes.path,
						modelinstance: node.attributes.modelinstancenodes[i],
						hierarchyId: this.ouHierarchy.ou.id,
						expanded: node.expanded
				};
				array.push(c);
			}
		}
		if(children!=null){
			for(var i=0; i<children.length; i++){
				array = array.concat(this.getAllNodesWithAbilitation(children[i]));
			}
		}
	
		return array;
	}
	
	//create the root node of the kpi tree
	,createKPIRootNodeByRec: function(rec) {
		this.nrec = rec;
		var iconClass = '';
		var cssClass = '';
		if (rec.kpiInstId !== undefined && rec.kpiInstId != null
				&& rec.kpiInstId != '') {
			iconClass = 'has-kpi';
		}
		if (rec.error !== undefined && rec.error != false) {
			cssClass = 'has-error';
		}
		var attrKpiCode = '';
		if(rec.kpiCode !== undefined){
			attrKpiCode = ' - '+rec.kpiCode;
		}
	
		var tip = rec.modelCode+' - '+rec.name+ attrKpiCode;
	
		var node = new Ext.tree.AsyncTreeNode({
			text		: this.rootNodeRightText,
			expanded	: true,
			leaf		: false,
			modelInstId : this.rootNodeRightId,
			id			: this.rootNodeRightId,
			label		: rec.label,
			description	: rec.description,
			kpiInst		: rec.kpiInstId,
			name		: rec.name,
			modelName   : rec.modelName,
			modelCode   : rec.modelCode,
			modelDescr  : rec.modelDescr,
			modelType   : rec.modelType,
			modelId     : rec.modelId,
			modelTypeDescr: rec.modelTypeDescr,
			kpiName		: rec.kpiName,
			kpiId		: rec.kpiId,
			kpiInstThrId: rec.kpiInstThrId,
			kpiInstThrName: rec.kpiInstThrName,
			kpiInstTarget: rec.kpiInstTarget,
			kpiInstWeight: rec.kpiInstWeight,
			modelUuid	: rec.modelUuid,
			kpiInstChartTypeId: rec.kpiInstChartTypeId,			      
			kpiInstPeriodicity: rec.kpiInstPeriodicity,
			iconCls		: iconClass,
			cls			: cssClass,
			draggable	: false,
			qtip		: tip,
			toSave: true,
			isNewRec :  rec.isNewRec,
			checked: false
		});
		this.addKPINodeListeners(node);
		return node;
	}
	
	//create the root node of the ou tree
	,createRootNodeByRec: function(rec) {
		var iconClass = '';
		var cssClass = '';
		var tip = rec.ou.name;
		var node = new Ext.tree.AsyncTreeNode({
			text		: rec.ou.name,
			nodeId		: rec.ou.id,
			expanded	: true,
			leaf		: false,
			description	: rec.ou.description,
			iconCls		: iconClass,
			cls			: cssClass,
			draggable	: false,
			qtip		: tip,
			toSave: true
		});

		node.id = rec.id;
		node.attributes.id = rec.id;
		node.attributes.path = rec.path;
		node.attributes.modelinstancenodes = rec.modelinstancenodes;
		node.attributes.modelinstancenodesOfThisSession = new Array();
		node.on('click',this.updateKpisCheck,this);
		return node;
	}


});


