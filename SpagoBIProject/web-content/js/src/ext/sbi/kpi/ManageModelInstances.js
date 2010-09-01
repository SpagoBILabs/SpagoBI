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

Sbi.kpi.ManageModelInstances = function(config, ref) { 
	var hideContextMenu = config.hideContextMenu;
	var paramsList = {MESSAGE_DET: "MODELINSTS_NODES_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_NODES_SAVE"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MODELINSTS_NODE_DELETE"};
	
	this.configurationObject = {};
	
	this.configurationObject.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
		, baseParams: paramsList
	});	
	this.configurationObject.saveTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
		, baseParams: paramsSave
	});
	this.configurationObject.deleteTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODEL_INSTANCES_ACTION'
		, baseParams: paramsDel
	});
	//reference to viewport container
	this.referencedCmp = ref;
	this.initConfigObject();
	config.configurationObject = this.configurationObject;
	
	config.hideContextMenu = hideContextMenu;
	
	var c = Ext.apply({}, config || {}, {});

	Sbi.kpi.ManageModelInstances.superclass.constructor.call(this, c);	 	
	
};

Ext.extend(Sbi.kpi.ManageModelInstances, Sbi.widgets.TreeDetailForm, {
	
	configurationObject: null
	, gridForm:null
	, mainElementsStore:null
	, root:null
	, referencedCmp : null
	, droppedSubtreeToSave: new Array()
	, kpitreeLoader : null

	,initConfigObject: function(){

		this.configurationObject.panelTitle = LN('sbi.modelinstances.panelTitle');
		this.configurationObject.listTitle = LN('sbi.modelinstances.listTitle');

		this.initTabItems();
    }

	,initTabItems: function(){
		this.kpitreeLoader =new Ext.tree.TreeLoader({
			dataUrl: this.configurationObject.manageTreeService,
	        createNode: function(attr) {

	            if (attr.modelInstId) {
	                attr.id = attr.modelInstId;
	            }

	    		if (attr.kpiInstId !== undefined && attr.kpiInstId != null
	    				&& attr.kpiInstId != '') {
	    			attr.iconCls = 'has-kpi';
	    		}
	    		if (attr.error !== undefined && attr.error != false) {
	    			attr.cls = 'has-error';
	    		}
	            return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
	        }

		});
		//Store of the combobox
 	    this.typesStore = new Ext.data.SimpleStore({
 	        fields: ['typeId', 'typeCd', 'typeDs', 'domainCd'],
 	        data: config.nodeTypesCd,
 	        autoLoad: false
 	    });
		/*DETAIL FIELDS*/
		   
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
	             name: 'description'
	         });

	 	   /*END*/
	 	  this.kpiInstItems = new Ext.Panel({
		        title: 'Kpi Instance'

			        , layout: 'fit'
			        , autoScroll: true
			        , items: []
			        , itemId: 'kpiInstItemsTab'
			        , scope: this
			});
	 	  
	 	  this.initSourcePanel();
	 	  this.initKpiPanel();
	 	  
	 	  this.configurationObject.tabItems = [{
		        title: LN('sbi.generic.details')
		        , itemId: 'detail'
		        , width: 430
		        , items: [{

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
		                 //"background-color": "#f1f1f1",
		                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
		             },
		             items: [this.detailFieldLabel, this.detailFieldName,  this.detailFieldDescr]
		    	}]
		    }, {
		        title: 'Kpi Instance'
			        , itemId: 'kpi_model'
			        , width: 430
			        , buttons: [this.kpiPeriodicityButton]
			        , items: [this.kpiInstTypeFieldset ,
			                  this.kpiInstFieldset, 
			                  this.kpiInstFieldset2]
			    },{
			        title: 'Source node'
				        , itemId: 'src_model'
				        , width: 430
				        , items: [{
	
				 		   	 itemId: 'src-detail',   	              
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
				                 //"background-color": "#f1f1f1",
				                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
				             },
				             items: [this.srcModelName,
							         this.srcModelCode,
							         this.srcModelDescr,
							         this.srcModelType,
							         this.srcModelTypeDescr ]
				    	}]
				    } ];
	 	  
	}
	, initSourcePanel: function() {
	 	   this.srcModelName = new Ext.form.TextField({
	             fieldLabel:LN('sbi.generic.name'),
	             readOnly:true,
	             style: '{ color: #ffffff; border: 1px solid white; font-style: italic;}',
	             name: 'srcname'
	         });	  

	 	   this.srcModelCode = new Ext.form.TextField({
	             readOnly: true,
	             fieldLabel: LN('sbi.generic.code'),
	             style: '{  color: #ffffff; border: 1px solid white; font-style: italic;}',
	             name: 'srccode'
	         });

	 	   this.srcModelDescr = new Ext.form.TextArea({
	 		   	 readOnly: true,
	          	 maxLength:400,
	       	     width : 250,
	             height : 80,
	             style: '{  color: #ffffff; border: 1px solid #fff; font-style: italic;}',
	             fieldLabel: LN('sbi.generic.descr'),
	             name: 'srcdescription'
	         });

	 	     this.srcModelType = new Ext.form.TextField({
	 		   	 readOnly: true,
	 		   	style: '{  color: #ffffff; border: 1px solid #fff; font-style: italic;}',
	             fieldLabel: LN('sbi.generic.nodetype'),
	             name: 'srctype'
	         });

		 	 this.srcModelTypeDescr = new Ext.form.TextField({
	             readOnly: true,
	             style: '{  color: #ffffff; border: 1px solid #fff; font-style: italic;}',
	             fieldLabel: LN('sbi.generic.nodedescr'),
	             name: 'srctypeDescr'
	         });

	}
	, launchPeriodicityWindow : function() {
		
		var conf = {};
		
		var managePeriodicities = new Sbi.kpi.ManagePeriodicities(conf);
	
		this.thrWin = new Ext.Window({
			title: 'Lista delle Periodicità' ,   
            layout      : 'fit',
            width       : 400,
            height      : 300,
            closeAction :'close',
            modal		: true,
            plain       : true,
            scope		: this,
            items       : [managePeriodicities]
		});
		
		this.thrWin.show();
	}
	,launchThrWindow : function() {
		
		var conf = {};
		conf.nodeTypesCd = config.thrTypes;
		conf.drawSelectColumn = true;

		
		var manageThresholds = new Sbi.kpi.ManageThresholds(conf);
	
		this.thrWin = new Ext.Window({
			title: LN('sbi.lookup.Select') ,   
            layout      : 'fit',
            width       : 1000,
            height      : 400,
            closeAction :'close',
            plain       : true,
            scope		: this,
            items       : [manageThresholds]
		});
		manageThresholds.on('selectEvent', function(itemId ,index, code){
												this.thrWin.close();
												Ext.getCmp('kpiThresholdF').setValue(code);
											}, this);
		this.thrWin.show();
	}
	, editNodeAttribute: function(field, newVal, oldVal) {
		var node = this.selectedNodeToEdit;
		if (node !== undefined && node !== null) {
			node.attributes.toSave = true;
			var fName = field.name;
			node.attributes[fName] = newVal;
			
		}
	}

	,selectNode : function(field) {
		
		/*utility to store node that has been edited*/
		this.selectedNodeToEdit = this.mainTree.getSelectionModel().getSelectedNode();
		
		if(this.selectedNodeToEdit.attributes.toSave === undefined || this.selectedNodeToEdit.attributes.toSave == false){
			var size = this.nodesToSave.length;
			this.nodesToSave[size] = this.selectedNodeToEdit;
		}//else skip because already taken
		
	}
	, initKpiPanel: function() {

		
		this.kpiModelType = new Ext.form.RadioGroup({
            fieldLabel: LN('sbi.generic.type'),	             
    	    id:'kpiModelType',
    	    xtype: 'radiogroup',
    	    readonly: true,
    	    columns: 2,
    	    items: [
    	        {boxLabel: 'UUID', id:'uuid',name: 'kpiTypeRadio', inputValue: 1},
    	        {boxLabel: 'Kpi Instance', id:'kpiinst',name: 'kpiTypeRadio', inputValue: 2, checked: true}
    	    ]
    	});
		this.kpiModelType.addListener('change', this.changeKpiPanel , this);
		
		this.kpiInstTypeFieldset = new Ext.form.FieldSet({
		   	columnWidth: 1,
            labelWidth: 90,
   
            autoHeight: true,
            autoScroll  : true,
            bodyStyle: Ext.isIE ? 'padding:0 0 5px 5px;' : 'padding: 5px;',
            border: false,
            style: {
            	//"border":"1px solid blue",
            	"background-color": "#f1f1f1",
                "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
            },
            items: [ this.kpiModelType]
		});

 	    this.kpiName = new Ext.form.TextField({
 	    	 id: 'kpinameField',
             fieldLabel:LN('sbi.generic.kpi'),
             readOnly: true,
             style: '{ color: #74B75C; border: 1px solid #74B75C; font-style: italic;}',
             value: 'drop kpi here...',
             name: 'kpiName'
         });	  
 	    this.kpiName.addListener('render', this.configureDD2, this);
 	    this.kpiName.addListener('focus', this.kpiFiledNotify, this);


 	 	 this.kpiThreshold = new Ext.form.TriggerField({
 		     triggerClass: 'x-form-search-trigger',
 		     fieldLabel: 'Threshold',
 		     name: 'kpiInstThrName',
 		     id: 'kpiThresholdF'
 		    });
 	 	 this.kpiThreshold.onTriggerClick = this.launchThrWindow;

		this.kpiWeight = new Ext.form.TextField({
            readOnly: false,
            fieldLabel: 'Weight',
            name: 'kpiInstWeight'
        });
		
		this.kpiTarget = new Ext.form.TextField({
            readOnly: false,
            fieldLabel: 'Target',
            name: 'kpiInstTarget'
        });
		// periodicity----------------
	    this.periodicityStore = new Ext.data.SimpleStore({
	        fields: ['kpiPeriodicityId', 'kpiPeriodicityName'],
	        data: config.kpiPeriodicities,
	        autoLoad: false
	    });
		this.kpiPeriodicity = new Ext.form.ComboBox({
      	    name: 'kpiInstPeriodicity',
            store: this.periodicityStore,
            //width : 120,
            fieldLabel: 'Periodicity',
            displayField: 'kpiPeriodicityName',   // what the user sees in the popup
            valueField: 'kpiPeriodicityId',        // what is passed to the 'change' event
            typeAhead: true,
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            selectOnFocus: true,
            editable: false,
            allowBlank: false,
            validationEvent:true
        }); 

		this.kpiPeriodicityButton = new Ext.Button({
			iconCls :'icon-add',
			text: 'Add Periodicity',
			handler: this.launchPeriodicityWindow	
		});
		
		//---------------chart type------------------------------
 	    //Store of the kpi chart types combobox
	    this.chartTypeStore = new Ext.data.SimpleStore({
	        fields: ['kpiChartTypeId', 'kpiChartTypeCd'],
	        data: config.kpiChartTypes,
	        autoLoad: false
	    });
		this.kpiChartType =  new Ext.form.ComboBox({
	      	    name: 'kpiInstChartTypeId',
	            store: this.chartTypeStore,
	            //width : 120,
	            fieldLabel: 'Chart type',
	            displayField: 'kpiChartTypeCd',   // what the user sees in the popup
	            valueField: 'kpiChartTypeId',        // what is passed to the 'change' event
	            typeAhead: true,
	            forceSelection: true,
	            mode: 'local',
	            triggerAction: 'all',
	            selectOnFocus: true,
	            editable: false,
	            allowBlank: false,
	            validationEvent:true
	        }); 
		//alternate if uuid
		this.kpiLabel = new Ext.form.TextField({
             readOnly: false,
             fieldLabel: LN('sbi.generic.label'),
             name: 'modelUuid'
         });
		

		this.kpiInstFieldset = new Ext.form.FieldSet({
	 		   	 columnWidth: 0.4,
	             labelWidth: 90,
	             defaults: {width: 140, border:false},    
	             autoHeight: true,
	             autoScroll  : true,
	             //bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
	             border: false,
	             style: {
	                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
	             },
	             items: [
	                     this.kpiName,
	                     this.kpiThreshold,
	                     this.kpiWeight,
	                     this.kpiTarget,
	                     this.kpiPeriodicity,
	                     this.kpiChartType
	                     ]
	    	});
		
			this.kpiInstFieldset2 = new Ext.form.FieldSet({
			   	 columnWidth: 0.4,
	            labelWidth: 90,
	            defaults: {width: 140, border:false},    
	            autoHeight: true,
	            autoScroll  : true,
	            //bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
	            border: false,
	            style: {
	                //"background-color": "#D3DAED",
	                "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
	            },
	            items: [
	                    this.kpiLabel]
			});
			this.kpiPeriodicityButton.disable();
			this.kpiInstFieldset.setVisible(false);
			this.kpiInstFieldset2.setVisible(false);

	}
	, kpiFiledNotify : function() {
		this.kpiName.getEl().highlight('#E27119');
		this.kpiName.setValue('');
		var tooltip = new Ext.ToolTip({
	        target: 'kpinameField',
	        anchor: 'right',
	        trackMouse: true,
	        html: 'Drag and drop a kpi from Kpi List here'
	    });

	}
	, configureDD2: function() {
		  var fieldDropTargetEl =  this.kpiName.getEl().dom; 
		  var formPanelDropTarget = new Ext.dd.DropTarget(fieldDropTargetEl, {
			    ddGroup  : 'kpiGrid2kpiForm',
			    overClass: 'over',
			    scope: this,
			    initialConfig: this,
			    notifyEnter : function(ddSource, e, data) {
			  		this.initialConfig.kpiName.getEl().frame("00AE00");

			    },
			    notifyDrop  : function(ddSource, e, data){
			      var selectedRecord = ddSource.dragData.selections[0];
			      this.initialConfig.kpiName.setValue(selectedRecord.get('name')); 
			      var node = this.initialConfig.mainTree.getSelectionModel().getSelectedNode() ;
			      if(node !== undefined && node != null){
			    	  var nodesList = this.initialConfig.nodesToSave;
			    	  var exists = nodesList.indexOf(node);
			    	  if(exists == -1){
						  var size = nodesList.length;
						  this.initialConfig.nodesToSave[size] = node;
						  node.attributes.toSave = true;
			    	  }
			    	  
				      node.attributes.kpiId = selectedRecord.get('id');
			      }
			      Ext.fly(this.getEl()).frame("ff0000");
			      return(true);
			    }
		}, this);
		  

	}
	, fillSourceModelPanel: function(sel, node) {

		this.srcModelName.setValue(node.attributes.modelName);
		this.srcModelCode.setValue(node.attributes.modelCode);
		this.srcModelDescr.setValue(node.attributes.modelDescr);
		this.srcModelType.setValue(node.attributes.modelType);
		this.srcModelTypeDescr.setValue(node.attributes.modelTypeDescr);
	}
	, changeKpiPanel: function(radioGroup, radio){
		if(radio.getItemId() == 'kpiinst'){
			
			this.kpiInstFieldset.setVisible(true);
			this.kpiInstFieldset2.setVisible(false);			

			this.kpiPeriodicityButton.enable();
			this.kpiInstFieldset.doLayout();

		}else if(radio.getItemId() == 'uuid'){

			this.kpiInstFieldset.setVisible(false);
			this.kpiInstFieldset2.setVisible(true);

			this.kpiPeriodicityButton.disable();
			this.kpiInstFieldset2.doLayout();

		}
		this.kpiInstTypeFieldset.setVisible(true);
		this.kpiInstTypeFieldset.doLayout();
		
	}
	, fillKpiPanel: function(sel, node) {

		var hasKpiInst = node.attributes.kpiInst;
		var hasKpiModelUuid = node.attributes.modelUuid;
		var hasKpi = node.attributes.kpiId;
		if(hasKpiInst !== undefined && hasKpiInst != null){

			this.kpiName.setValue(node.attributes.kpiName);
			this.kpiThreshold.setValue(node.attributes.kpiInstThrName);
			this.kpiTarget.setValue(node.attributes.kpiInstTarget);
			this.kpiWeight.setValue(node.attributes.kpiInstWeight);
			this.kpiChartType.setValue(node.attributes.kpiInstChartTypeId);
			this.kpiPeriodicity.setValue(node.attributes.kpiInstPeriodicity);
			
			this.kpiInstFieldset.setVisible(true);
			this.kpiInstFieldset2.setVisible(false);			
			this.kpiModelType.onSetValue( 'kpiinst', true);
			this.kpiPeriodicityButton.enable();
			this.kpiInstFieldset.doLayout();

		}else if(hasKpiModelUuid !== undefined && hasKpiModelUuid != null){
			this.kpiLabel.setValue(node.attributes.modelUuid);
			
			this.kpiInstFieldset.setVisible(false);
			this.kpiInstFieldset2.setVisible(true);
			this.kpiModelType.onSetValue( 'uuid', true);
			this.kpiPeriodicityButton.disable();
			this.kpiInstFieldset2.doLayout();

		}else if(hasKpi !== undefined && hasKpi != null){
			if(node.attributes.kpi){
				//dropped node from model tree
				this.kpiName.setValue(node.attributes.kpi);
			}else{
				//new node
				this.kpiName.setValue(node.attributes.kpiName);
			}
/*			
			this.kpiName.setValue(node.attributes.kpiName);
			this.kpiThreshold.setValue(node.attributes.kpiInstThrName);
			this.kpiTarget.setValue(node.attributes.kpiInstTarget);
			this.kpiWeight.setValue(node.attributes.kpiInstWeight);
			this.kpiChartType.setValue(node.attributes.kpiInstChartTypeId);
			this.kpiPeriodicity.setValue(node.attributes.kpiInstPeriodicity);
			*/
			this.kpiInstFieldset.setVisible(true);
			this.kpiInstFieldset2.setVisible(false);			
			this.kpiModelType.onSetValue( 'kpiinst', true);
			this.kpiPeriodicityButton.enable();
			this.kpiInstFieldset.doLayout();

		}else{
			//alert("nothing associated");
			this.clearKpiInstanceTabForm();
		}
		this.kpiInstTypeFieldset.setVisible(true);
		this.kpiInstTypeFieldset.doLayout();


	}
	, clearKpiInstanceTabForm: function(){
		this.kpiName.setValue(null);
		this.kpiThreshold.setValue(null);
		this.kpiTarget.setValue(null);
		this.kpiWeight.setValue(null);
		this.kpiChartType.setValue(null);
		this.kpiPeriodicity.setValue(null);
		
		this.kpiLabel.setValue(null);
	}
/*	, completeDroppedNode: function(node, id, parent){
		alert("id:"+id+" parent:"+parent);
		var counter = 0;
		var resultNode = node;
		if(id == null){
			resultNode.attributes.id = 'generatedId_'+counter;
		}else{
			resultNode.attributes.id = id;
		}	
		if(parent != null){
			resultNode.attributes.parentId = parent;
		}	
		//Add child nodes if any
		var children = node.childNodes;
		var clen = children.length;
		if(clen != 0){
		    for(var i = 0; i < clen; i++){
		        this.completeDroppedNode(children[i], counter, id);
		        counter++;
		    }

		}
		return resultNode;
	}*/
    //OVERRIDING save method
	,save : function() {

    	var jsonStr = '[';

		Ext.each(this.nodesToSave, function(node, index) {
			if(node instanceof Ext.tree.TreeNode){
				jsonStr += Ext.util.JSON.encode(node.attributes);
				jsonStr +=',';
			}
		});

		jsonStr += ']';
		
		var jsonDroppedStr = '[';
		//extracts dropped nodes
		var JsonSer = new Sbi.widgets.JsonTreeSerializer(this.mainTree);

		Ext.each(this.droppedSubtreeToSave, function(node, index) {
			if(node instanceof Ext.tree.TreeNode){
				alert(JsonSer.nodeToString(node));
				jsonDroppedStr += JsonSer.nodeToString(node);
				jsonDroppedStr +=',';
			}
		}, this);
		jsonDroppedStr += ']';
		
		var params = {
			nodes : jsonStr,
			droppedNodes : jsonDroppedStr
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
		      				  var nodeSel = this.mainTree.getNodeById(key);
		      				  //response returns key = guiid, value = 'KO' if operation fails, or modelInstId if operation succeded
		      				  if(value  == 'KO'){
		      					  hasErrors= true;
		 		      			  ///contains error gui ids      						  
	      						  nodeSel.attributes.error = true;
	      						  Ext.fly(nodeSel.getUI().getEl()).applyStyles('{ border: 1px solid red; font-weight: bold; font-style: italic; color: #cd2020; text-decoration: underline; }');
		      				  }else{
		      					  nodeSel.attributes.error = false; 
		      					  nodeSel.attributes.modelInstId = value; 
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
		      				this.referencedCmp.modelInstancesGrid.mainElementsStore.load();
	      				}
	      			}else{
	      				alert(LN('sbi.generic.savingItemError'));
	      			}
				}else{
      				this.cleanAllUnsavedNodes();
      				alert(LN('sbi.generic.resultMsg'));
      				this.referencedCmp.modelInstancesGrid.mainElementsStore.load();
				}
      			this.mainTree.doLayout();
      			this.referencedCmp.modelInstancesGrid.getView().refresh();
				this.referencedCmp.modelInstancesGrid.doLayout();
				
				
				
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

/*	,saveParentNode : function(parent, child) {
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
		      				  var nodeSel = this.mainTree.getNodeById(key);
		      				  //response returns key = guiid, value = 'KO' if operation fails, or modelInstId if operation succeded
		      				  if(value  == 'KO'){
		      					  hasErrors= true;
		 		      			  ///contains error gui ids      						  
	      						  nodeSel.attributes.error = true;
	      						  Ext.fly(nodeSel.getUI().getEl()).applyStyles('{ border: 1px solid red; font-weight: bold; font-style: italic; color: #cd2020; text-decoration: underline; }');
		      				  }else{
		      					  nodeSel.attributes.error = false; 
		      					  nodeSel.attributes.modelInstId = value; 
		      					  Ext.fly(nodeSel.getUI().getEl()).applyStyles('{ border: 0; font-weight: normal; font-style: normal; text-decoration: none; }');
		      					  
		      					  //completes child node instanciation
			  	        		  this.selectedNodeToEdit = child;
				        		  //this.mainTree.getSelectionModel().select(child);

			  	        		  child.attributes.parentId = parent.attributes.modelInstId;
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
		
    }*/

	,fillDetail : function(sel, node) {
		if(node !== undefined && node != null){
			var val = node.text;//name value
			if (val != null && val !== undefined) {
				var name = node.attributes.name;	
				this.detailFieldDescr.setValue(node.attributes.description);			
				this.detailFieldLabel.setValue(node.attributes.label);
				this.detailFieldName.setValue(name);
			}
		}
	}
	,renderTree : function(tree) {
		tree.getLoader().nodeParameter = 'modelInstId';
		tree.getRootNode().expand(false, /*no anim*/false);
	}

	,setListeners : function() {
		this.mainTree.getSelectionModel().addListener('selectionchange',
				this.fillDetail, this);		
		this.mainTree.getSelectionModel().addListener('selectionchange',
				this.fillKpiPanel, this);
		this.mainTree.getSelectionModel().addListener('selectionchange',
				this.fillSourceModelPanel, this);
		
		this.mainTree.addListener('render', this.renderTree, this);

		/* form fields editing */
		this.detailFieldName.addListener('focus', this.selectNode, this);
		this.detailFieldName.addListener('change', this.editNodeAttribute, this);

		this.detailFieldDescr.addListener('focus', this.selectNode, this);
		this.detailFieldDescr.addListener('change', this.editNodeAttribute, this);

		this.detailFieldLabel.addListener('focus', this.selectNode, this);
		this.detailFieldLabel.addListener('change', this.editNodeAttribute, this);
		
		this.kpiThreshold.addListener('focus', this.selectNode, this);
		this.kpiThreshold.addListener('change', this.editNodeAttribute, this);
		
		this.kpiTarget.addListener('focus', this.selectNode, this);
		this.kpiTarget.addListener('change', this.editNodeAttribute, this);
		
		this.kpiWeight.addListener('focus', this.selectNode, this);
		this.kpiWeight.addListener('change', this.editNodeAttribute, this);
		
		this.kpiChartType.addListener('focus', this.selectNode, this);
		this.kpiChartType.addListener('change', this.editNodeAttribute, this);
		
		this.kpiPeriodicity.addListener('focus', this.selectNode, this);
		this.kpiPeriodicity.addListener('change', this.editNodeAttribute, this);
		
		this.kpiName.addListener('focus', this.selectNode, this);
		this.kpiName.addListener('change', this.editNodeAttribute, this);
		
		this.kpiLabel.addListener('focus', this.selectNode, this);
		this.kpiLabel.addListener('change', this.editNodeAttribute, this);

	}
	,createRootNodeByRec: function(rec) {
			var iconClass = '';
			var cssClass = '';
			if (rec.get('kpiInstId') !== undefined && rec.get('kpiInstId') != null
					&& rec.get('kpiInstId') != '') {
				iconClass = 'has-kpi';
			}
			if (rec.get('error') !== undefined && rec.get('error') != false) {
				cssClass = 'has-error';
			}
			var node = new Ext.tree.AsyncTreeNode({
		        text		: this.rootNodeText,
		        expanded	: true,
		        leaf		: false,
				modelInstId : this.rootNodeId,
				id			: this.rootNodeId,
				label		: rec.get('label'),
				description	: rec.get('description'),
				kpiInst		: rec.get('kpiInstId'),
				name		: rec.get('name'),
				modelName   : rec.get('modelName'),
				modelCode   : rec.get('modelCode'),
				modelDescr  : rec.get('modelDescr'),
				modelType   : rec.get('modelType'),
				modelId     : rec.get('modelId'),
				modelTypeDescr: rec.get('modelTypeDescr'),
				kpiName		: rec.get( 'kpiName'),
				kpiId		: rec.get( 'kpiId'),
				kpiInstThrId: rec.get( 'kpiInstThrId'),
				kpiInstThrName: rec.get( 'kpiInstThrName'),
				kpiInstTarget: rec.get( 'kpiInstTarget'),
				kpiInstWeight: rec.get( 'kpiInstWeight'),
				modelUuid	: rec.get( 'modelUuid'),
				kpiInstChartTypeId: rec.get( 'kpiInstChartTypeId'),			      
				kpiInstPeriodicity: rec.get( 'kpiInstPeriodicity'),
				iconCls		: iconClass,
				cls			: cssClass,
		        draggable	: false,
		        toSave: true
		    });

			return node;
	}
	, cleanAllUnsavedNodes: function() {
		
		Ext.each(this.nodesToSave, function(node, index) {
			node.attributes.toSave = false;  
					
		});
		this.nodesToSave = new Array();
	}    
	,dropNodeBehavoiur: function(e) {
  
			/*
			* tree - The TreePanel
		    * target - The node being targeted for the drop
		    * data - The drag data from the drag source
		    * point - The point of the drop - append, above or below
		    * source - The drag source
		    * rawEvent - Raw mouse event
		    * dropNode - Drop node(s) provided by the source OR you can supply node(s) to be inserted by setting them on this object.
		    * cancel - Set this to true to cancel the drop.
		    */
	 
		   // e.data.selections is the array of selected records
		if(!Ext.isArray(e.data.selections)) {	
 
			   var srcNodeDepth = e.dropNode.parentNode.getDepth();

			   var targetNodeDepth = e.target.getDepth();
				//alert("targer parent depth:"+targetNodeDepth +" srr parent depth:"+srcNodeDepth);

			   //simulates drag&drop but copies the node

			   var importSub = this.referencedCmp.manageModelsTree.importCheck;

			   var copiedNode ;
			   var parentNode = e.target;
			   
			   if(this.referencedCmp.manageModelsTree.importCheck.checked){
				   importSub = true;
				   //imports children
				   copiedNode = new Ext.tree.AsyncTreeNode(e.dropNode.attributes); 

			   }else{
				   importSub = false;
				   copiedNode = new Ext.tree.TreeNode(e.dropNode.attributes); 
			   }
			   copiedNode.setText(e.dropNode.attributes.name)

			   e.cancel = true;
			   //if parents have same depth --> enable kind of drop else forbid
			   if(srcNodeDepth == targetNodeDepth){
				   
				   copiedNode.attributes.toSave = true;
				   copiedNode.attributes.parentId = parentNode.attributes.modelInstId;
				   
				   if(importSub){
					   copiedNode.expand(true);
					   copiedNode.attributes.saveChildren = true;
				   }else{
					   copiedNode.attributes.saveChildren = false;
				   }
				   parentNode.appendChild(copiedNode);	
			       
			       var ddLength = this.droppedSubtreeToSave.length;

			       this.droppedSubtreeToSave[ddLength] = copiedNode;
			   }else{
				   alert("Nodes hierarchy must be respected!");
			   }		   
		   }


	   // if we get here the drop is automatically cancelled by Ext
	   }
		, initContextMenu : function() {

			this.menu = new Ext.menu.Menu( {
				items : [{
							text : 'Remove Model Node',
							iconCls : 'icon-remove',
							handler : function() {
								this.deleteItem(this.ctxNode);
							},
							scope : this
						} ]
			});

		}
		, deleteItem : function(node) {
			
			if (node === undefined || node == null) {
				alert("Select node to delete");
				return;
			}
			//if model instance already exists
			if(node.attributes.modelInstId){
				Ext.MessageBox.confirm(
						LN('sbi.generic.pleaseConfirm'),
						LN('sbi.generic.confirmDelete'),            
			            function(btn, text) {
			                if (btn=='yes') {
			                	if (node != null) {	
									Ext.Ajax.request({
							            url: this.services['deleteTreeService'],
							            params: {'modelInstId': node.attributes.modelInstId},
							            method: 'GET',
							            success: function(response, options) {
											if (response !== undefined) {
												this.mainTree.getSelectionModel().clearSelections(false);
												node.remove();
												this.mainTree.doLayout();
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
			}else{
				this.mainTree.getSelectionModel().clearSelections(false);
				node.remove();
				this.mainTree.doLayout();
			}
			
		}
});
