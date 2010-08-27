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
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "MMODELINSTS_NODE_DELETE"};
	
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


	,initConfigObject: function(){

		this.configurationObject.panelTitle = LN('sbi.modelinstances.panelTitle');
		this.configurationObject.listTitle = LN('sbi.modelinstances.listTitle');

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
	             name: 'name'
	         });	  

	 	   this.srcModelCode = new Ext.form.TextField({
	             readOnly: true,
	             fieldLabel: LN('sbi.generic.code'),
	             style: '{  color: #ffffff; border: 1px solid white; font-style: italic;}',
	             name: 'code'
	         });

	 	   this.srcModelDescr = new Ext.form.TextArea({
	 		   	 readOnly: true,
	          	 maxLength:400,
	       	     width : 250,
	             height : 80,
	             style: '{  color: #ffffff; border: 1px solid #fff; font-style: italic;}',
	             fieldLabel: LN('sbi.generic.descr'),
	             name: 'description'
	         });

	 	     this.srcModelType = new Ext.form.TextField({
	 		   	 readOnly: true,
	 		   	style: '{  color: #ffffff; border: 1px solid #fff; font-style: italic;}',
	             fieldLabel: LN('sbi.generic.nodetype'),
	             name: 'type'
	         });

		 	 this.srcModelTypeDescr = new Ext.form.TextField({
	             readOnly: true,
	             style: '{  color: #ffffff; border: 1px solid #fff; font-style: italic;}',
	             fieldLabel: LN('sbi.generic.nodedescr'),
	             name: 'typeDescr'
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
		manageThresholds.on('selectEvent', function(itemId,index, code){this.thrWin.close();Ext.getCmp('kpiThresholdF').setValue(code);}, this);
		this.thrWin.show();
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
    	        {boxLabel: 'Kpi Instance', id:'kpiinst',name: 'kpiTypeRadio', inputValue: 2}
    	    ]
    	});
		
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
             name: 'kpiname'
         });	  
 	    this.kpiName.addListener('render', this.configureDD2, this);
 	    this.kpiName.addListener('focus', this.kpiFiledNotify, this);


 	 	 this.kpiThreshold = new Ext.form.TriggerField({
 		     triggerClass: 'x-form-search-trigger',
 		     fieldLabel: 'Threshold',
 		     name: 'threshold',
 		     id: 'kpiThresholdF'
 		    });
 	 	 this.kpiThreshold.onTriggerClick = this.launchThrWindow;

		this.kpiWeight = new Ext.form.TextField({
            readOnly: false,
            fieldLabel: 'Weight',
            name: 'kpiWeight'
        });
		
		this.kpiTarget = new Ext.form.TextField({
            readOnly: false,
            fieldLabel: 'Target',
            name: 'kpiTarget'
        });
		// periodicity----------------
	    this.periodicityStore = new Ext.data.SimpleStore({
	        fields: ['kpiPeriodicityId', 'kpiPeriodicityName'],
	        data: config.kpiPeriodicities,
	        autoLoad: false
	    });
		this.kpiPeriodicity = new Ext.form.ComboBox({
      	    name: 'kpiPeriodicity',
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
	      	    name: 'kpiChartType',
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
             name: 'kpiLabel'
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
	                 //"background-color": "#f1f1f1",
	                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
	             },
	             items: [
	                     this.kpiName,
	                     this.kpiThreshold,
	                     this.kpiWeight,
	                     this.kpiTarget,
	                     this.kpiPeriodicity,
	                     //this.kpiPeriodicityButton,
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
			    	  
				      node.attributes.kpiInstId = selectedRecord.get('id');
			      }
			      Ext.fly(this.getEl()).frame("ff0000");
			      return(true);
			    }
		}, this);
		  

	}
	, fillKpiPanel: function(sel, node) {
		
		var hasKpiInst = node.attributes.kpiInst;
		if(hasKpiInst !== undefined && hasKpiInst != null){

			this.kpiInstFieldset.setVisible(true);
			this.kpiInstFieldset2.setVisible(false);			
			this.kpiModelType.onSetValue( 'kpiinst', true);
			this.kpiPeriodicityButton.enable();
			this.kpiInstFieldset.doLayout();

		}else{

			this.kpiInstFieldset.setVisible(false);
			this.kpiInstFieldset2.setVisible(true);
			this.kpiModelType.onSetValue( 'uuid', true);
			this.kpiPeriodicityButton.disable();
			this.kpiInstFieldset2.doLayout();

		}
		this.kpiInstTypeFieldset.setVisible(true);
		this.kpiInstTypeFieldset.doLayout();
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
		      				  var nodeSel = this.mainTree.getNodeById(key);
		      				  //response returns key = guiid, value = 'KO' if operation fails, or modelId if operation succeded
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
		      				  var nodeSel = this.mainTree.getNodeById(key);
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
			
			this.mainTree.getSelectionModel().addListener('selectionchange',
					this.fillKpiPanel, this);
			
			this.mainTree.addListener('render', this.renderTree, this);

			/* form fields editing */
			this.detailFieldName.addListener('focus', this.selectNode, this);
			this.detailFieldName.addListener('change', this.editNode, this);

			this.detailFieldDescr.addListener('focus', this.selectNode, this);
			this.detailFieldDescr.addListener('change', this.editNodeAttribute, this);

			this.detailFieldLabel.addListener('focus', this.selectNode, this);
			this.detailFieldLabel.addListener('change', this.editNodeAttribute, this);


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
			   parentNode.appendChild(copiedNode);

		   }

	   // if we get here the drop is automatically cancelled by Ext
	   }
});
