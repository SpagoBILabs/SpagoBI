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
 * Authors - Chiara Chiarelli (chiara.chiarelli@eng.it)
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageKpis = function(config) {
	 
	var paramsList = {MESSAGE_DET: "KPIS_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "KPI_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "KPI_DELETE"};

	this.configurationObject = {};
	
	this.configurationObject.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_KPIS_ACTION'
		, baseParams: paramsList
	});
	this.configurationObject.saveItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_KPIS_ACTION'
		, baseParams: paramsSave
	});
	this.configurationObject.deleteItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_KPIS_ACTION'
		, baseParams: paramsDel
	});	
	
	this.services = new Array();
	
	var paramsDocList = {MESSAGE_DET: "BIOBJECTS_LIST"};
	var paramsDatasetList = {MESSAGE_DET: "DATASETS_LIST"};
	
	this.services['manageDocumentsService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_BIOBJECTS_ACTION'
		, baseParams: paramsDocList
	});	
	
	this.services['manageDatasetsService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_DATASETS_ACTION'
		, baseParams: paramsDatasetList
	});
	
	
	var singleSelection = config.singleSelection;
	var tabPanelWidth = config.tabPanelWidth;
	var gridWidth = config.gridWidth;
	this.configurationObject.tabPanelWidth = tabPanelWidth;
	this.configurationObject.gridWidth = gridWidth;
	
	if(config.textAreaWidth){
		this.textAreaWidth = config.textAreaWidth;
    }else{
    	this.textAreaWidth = 250;
    }
	if(config.fieldsDefaultWidth){
		this.fieldsDefaultWidth = config.fieldsDefaultWidth;
	}else{
		this.fieldsDefaultWidth = 200;
	}
	this.gridColumnNumber = config.gridColumnNumber;
	
	this.initConfigObject();
	
	config.configurationObject = this.configurationObject;
	config.singleSelection = singleSelection;

	var c = Ext.apply({}, config || {}, {});

	Sbi.kpi.ManageKpis.superclass.constructor.call(this, c);	
	
	this.rowselModel.addListener('rowselect',function(sm, row, rec) { 
		this.getForm().loadRecord(rec); 
		this.udpValueGrid.fillUdpValues(rec.get('udpValues'));
		
		this.fillKpiLinks(row, rec);
     }, this);
	
	this.tabs.addListener('tabchange', this.modifyToolbar, this);
};

Ext.extend(Sbi.kpi.ManageKpis, Sbi.widgets.ListDetailForm, {
	
	configurationObject: null
	, gridForm:null
	, mainElementsStore:null
	, thrWin:null
	, dsWin:null
	, detailFieldThreshold: null
	, fieldsDefaultWidth: null
	, textAreaWidth: null
	, gridColumnNumber: null

	,initConfigObject:function(){

	this.configurationObject.fields = ['id'
		                     	          , 'name'
		                    	          , 'code'
		                    	          , 'description'   
		                    	          , 'weight' 
		                    	          , 'dataset'
		                    	          , 'threshold'
		                    	          , 'documents'
		                    	          , 'interpretation'
		                    	          , 'algdesc'
		                    	          , 'inputAttr'
		                    	          , 'modelReference'
		                    	          , 'targetAudience'
		                    	          , 'kpiTypeCd'
		                    	          , 'metricScaleCd'
		                    	          , 'measureTypeCd'
		                    	          , 'udpValues'
		                    	          ];
		
		this.configurationObject.emptyRecToAdd = new Ext.data.Record({
										  id: 0
										  , name:'' 
										  , code:'' 
										  , description:''
										  , weight:''
		                    	          , dataset:''
		                    	          , threshold:''
		                    	          , documents:''
		                    	          , interpretation:''
		                    	          , algdesc:''
		                    	          , inputAttr:''
		                    	          , modelReference:''
		                    	          , targetAudience:''
		                    	          , kpiTypeCd:''
		                    	          , metricScaleCd:''
		                    	          , measureTypeCd:''
		                    	          , udpValues:''
										 });
		
		if(this.gridColumnNumber == 1){
			this.configurationObject.gridColItems = [
			                                         {id:'name',header: LN('sbi.generic.name'), width: 125, sortable: true, locked:false, dataIndex: 'name'}
			                                        ];
		}else if(this.gridColumnNumber == 2){
			this.configurationObject.gridColItems = [
			                                         {id:'name',header: LN('sbi.generic.name'), width: 110, sortable: true, locked:false, dataIndex: 'name'},
			                                         {header: LN('sbi.generic.code'), width: 110, sortable: true, dataIndex: 'code'}
			                                        ];
		}else{
			this.configurationObject.gridColItems = [
			                                         {id:'name',header: LN('sbi.generic.name'), width: 125, sortable: true, locked:false, dataIndex: 'name'},
			                                         {header: LN('sbi.generic.code'), width: 125, sortable: true, dataIndex: 'code'},
			                                         {header: LN('sbi.kpis.threshold'), width: 110, sortable: true, dataIndex: 'threshold'}
			                                        ];
		}
		
		this.configurationObject.panelTitle = LN('sbi.kpis.panelTitle');
		this.configurationObject.listTitle = LN('sbi.kpis.listTitle');
		this.configurationObject.dragndropGroup ='grid2treeAndDetail';
		this.initTabItems();			
    }
	, modifyToolbar : function(tabpanel, panel){
		var itemId = panel.getItemId();
		if(itemId !== undefined && itemId !== null && itemId === 'kpiLinks'){
			this.tbSaveButton.hide();
		}else{
			this.tbSaveButton.show();
		}
	}
	,initTabItems: function(){
		
 	   //START list of detail fields
 	   var detailFieldId = {
               name: 'id',
               hidden: true
           };
 		   
 	   var detailFieldName = {
          	 maxLength:400,
        	 minLength:1,
        	// regex : new RegExp("^([a-zA-Z0-9_\x2F])+$", "g"),
        	 regexText : LN('sbi.roles.alfanumericString'),
             fieldLabel: LN('sbi.generic.name'),
             allowBlank: false,
             validationEvent:true,
             name: 'name'
         };
 			  
 	   var detailFieldCode = {
          	 maxLength:40,
        	// regex : new RegExp("^([A-Za-z0-9_])+$", "g"),
        	 regexText : LN('sbi.roles.alfanumericString2'),
             fieldLabel:LN('sbi.generic.code'),
             validationEvent:true,
             name: 'code'
         };	  
 		   
 	   var detailFieldDescr = {
          	 maxLength:1000,
          	 xtype: 'textarea',
       	     width : this.textAreaWidth,
             height : 80,
        	 regexText : LN('sbi.roles.alfanumericString'),
             fieldLabel: LN('sbi.generic.descr'),
             validationEvent:true,
             name: 'description'
         };	 	
 	   
 	  var detailFieldWeight = {
         	 width : 120,
         	// regex : new RegExp("^([0-9/])+$", "g"),
         	 regexText : LN('sbi.roles.alfanumericString'),
             fieldLabel: LN('sbi.kpis.weight'),
             validationEvent:true,
             name: 'weight'
          };	
 	  
 	 
  	 var baseConfig = {drawFilterToolbar:false};
     
      var datasets = new Ext.data.JsonStore({
 		root: 'rows'
 		, fields: ['label','name','description']
 		, url: this.services['manageDatasetsService']
 	  });
 	  
 	  var detailFieldDataset = new Sbi.widgets.LookupField(Ext.apply(baseConfig, {
			  store: datasets
			  , name: 'dataset'
			  ,	fieldLabel: LN('sbi.kpis.dataset')
			  , singleSelect: true
			  ,	valueField: 'label'
			  , displayField: 'label'
			  , descriptionField: 'label'
			  , cm: new Ext.grid.ColumnModel([
            		new Ext.grid.RowNumberer(),
            		{   header: LN('sbi.generic.label'),
            		    dataIndex: 'label',
            		    width: 75
            		},{   header: LN('sbi.generic.name'),
            		    dataIndex: 'name',
            		    width: 75
            		},{   header: LN('sbi.generic.descr'),
            		    dataIndex: 'description',
            		    width: 75
            		}
        		])
		 }));  
 	 //detailFieldDataset.addListener('change', this.refillKpiLinks, this ); 
 	  
 	 this.detailFieldThreshold = new Ext.form.TriggerField({
 		     triggerClass: 'x-form-search-trigger',
 		     fieldLabel: LN('sbi.kpis.threshold'),
 		     name: 'threshold',
 		     id: 'detailFieldThreshold',
 		     scope: this
 		    	 
 		    });
 
 	this.detailFieldThreshold.onTriggerClick = this.launchThrWindow;
 	
 	var docs = new Ext.data.JsonStore({
		root: 'rows'
		, fields: ['label','name','engine','stateCode']
		, url: this.services['manageDocumentsService']
	});
 	
 	 var detailFieldDocuments = new Sbi.widgets.LookupField(Ext.apply(baseConfig, {
		  store: docs
		  , name: 'documents'
		  ,	fieldLabel: LN('sbi.kpis.documents')
		  , singleSelect: false
		  ,	valueField: 'label'
		  , displayField: 'label'
		  , descriptionField: 'label'
		  , cm: new Ext.grid.ColumnModel([
        		new Ext.grid.RowNumberer(),
        		{   header: LN('sbi.generic.label'),
        		    dataIndex: 'label',
        		    width: 75
        		},{   header: LN('sbi.generic.name'),
        		    dataIndex: 'name',
        		    width: 75
        		},{   header: LN('sbi.generic.engine'),
        		    dataIndex: 'engine',
        		    width: 75
        		},{   header: LN('sbi.generic.state'),
        		    dataIndex: 'stateCode',
        		    width: 75
        		}
    		])
 	 }));  	 
 	    
 	   //END list of detail fields
 	   
 	    //Store of the kpi types combobox
	    this.kpisStore = new Ext.data.SimpleStore({
	        fields: ['kpiTypeCd'],
	        data: config.kpiTypesCd,
	        autoLoad: false
	    });
	    
	    //Store of the measure types combobox
	    this.measuresStore = new Ext.data.SimpleStore({
	        fields: ['metricScaleCd'],
	        data: config.measureTypesCd,
	        autoLoad: false
	    });
	    
	    //Store of the metric scale combobox
	    this.metricScalesStore = new Ext.data.SimpleStore({
	        fields: ['measureTypeCd'],
	        data: config.metricScaleTypesCd,
	        autoLoad: false
	    });
	    
 	   //START list of Advanced fields
	    var detailFieldInterpretation = {
	             maxLength:1000,
	             xtype: 'textarea',
	        	 width : this.textAreaWidth,
	             height : 80,
	          	// regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	          	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.kpis.interpretation'),
	             validationEvent:true,
	             name: 'interpretation'
	           };
	    
	    var detailFieldAlgDesc = {
	             maxLength:1000,
	             xtype: 'textarea',
	        	 width : this.textAreaWidth,
	             height : 80,
	          	// regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	          	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.kpis.algDescr'),
	             validationEvent:true,
	             name: 'algdesc'
	           };
	    
	    var detailFieldInputAttr = {
	             maxLength:1000,
	          	 //regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	          	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.kpis.inputAttr'),
	             validationEvent:true,
	             name: 'inputAttr'
	           };
	    
	    var detailFieldModelReference = {
	             maxLength:255,
	          	 //regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	          	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.kpis.modelRef'),
	             validationEvent:true,
	             name: 'modelReference'
	           };
	    
	    var detailFieldTargetAud = {
	             maxLength:1000,
	             xtype: 'textarea',
	        	 width : this.textAreaWidth,
	             height : 80,
	          	// regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	          	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.kpis.targAud'),
	             validationEvent:true,
	             name: 'targetAudience'
	           };
	    
 	    var detailFieldKpiType =  {
      	    name: 'kpiTypeCd',
            store: this.kpisStore,
            width : 120,
            fieldLabel: LN('sbi.kpis.kpiType'),
            displayField: 'kpiTypeCd',   // what the user sees in the popup
            valueField: 'kpiTypeCd',        // what is passed to the 'change' event
            typeAhead: true,
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            selectOnFocus: true,
            editable: false,
            allowBlank: true,
            validationEvent:true,
            xtype: 'combo'
        }; 
 	   
 	     var detailFieldMeasureType =  {
 	      	    name: 'metricScaleCd',
 	            store: this.measuresStore,
 	            width : 120,
 	            fieldLabel: LN('sbi.kpis.metricScale'),
 	            displayField: 'metricScaleCd',   // what the user sees in the popup
 	            valueField: 'metricScaleCd',        // what is passed to the 'change' event
 	            typeAhead: true,
 	            forceSelection: true,
 	            mode: 'local',
 	            triggerAction: 'all',
 	            selectOnFocus: true,
 	            editable: false,
 	            allowBlank: true,
 	            validationEvent:true,
 	            xtype: 'combo'
 	        };
 	   
 	      var detailFieldMetricScaleType =  {
 	      	    name: 'measureTypeCd',
 	            store: this.metricScalesStore,
 	            width : 120,
 	            fieldLabel: LN('sbi.kpis.measType'),
 	            displayField: 'measureTypeCd',   // what the user sees in the popup
 	            valueField: 'measureTypeCd',        // what is passed to the 'change' event
 	            typeAhead: true,
 	            forceSelection: true,
 	            mode: 'local',
 	            triggerAction: 'all',
 	            selectOnFocus: true,
 	            editable: false,
 	            allowBlank: true,
 	            validationEvent:true,
 	            xtype: 'combo'
 	        };
 	   //END list of Advanced fields
 	   var conf = {};
 	   conf.readonlyStrict = true;
 	   
 	   this.initKpiLinksTab();  
 	   
 	   this.udpValueGrid = new Sbi.kpi.ManageUdpValues(config);
 	   this.udpValueGrid.setSource(config.udpKpiEmptyList); 	     

 	   this.configurationObject.tabItems = [{
		        title: LN('sbi.generic.details')
		        , itemId: 'detail'
		        , width: 350
		        , scope: this
		        , items: {
 		   			 scope: this,
			   		 id: 'items-detail',   	
		 		   	 itemId: 'items-detail',   	              
		 		   	 //columnWidth: 0.4,
		             xtype: 'fieldset',
		             labelWidth: 90,
		             defaults: {width: this.fieldsDefaultWidth, border:false},    
		             defaultType: 'textfield',
		             autoHeight: true,
		             autoScroll  : true,
		             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:0px 0px;',
		             border: false,
		             style: {
		                 "margin-left": "10px", 
		                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
		             },
		             items: [detailFieldId, detailFieldName, detailFieldCode, 
		                     detailFieldDescr,  detailFieldDataset,
		                    this.detailFieldThreshold, detailFieldDocuments, detailFieldWeight]
		    	}
		    },{
		    	title: LN('sbi.generic.advanced')
		        , itemId: 'advanced'
		        , width: 350
		        , items: {
			   		 id: 'advanced-detail',   	
		 		   	 itemId: 'advanced-detail',   	              
		 		   	// columnWidth: 0.4,
		             xtype: 'fieldset',
		             scope: this,
		             labelWidth: 90,
		             defaults: {width: this.fieldsDefaultWidth, border:false},    
		             defaultType: 'textfield',
		             autoHeight: true,
		             autoScroll  : true,
		             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:0px 0px;',
		             border: false,
		             style: {
		                 "margin-left": "10px", 
		                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
		             },
		             items: [detailFieldInterpretation, detailFieldAlgDesc, detailFieldTargetAud, detailFieldInputAttr, 
		                     detailFieldModelReference, detailFieldKpiType,
		                     detailFieldMeasureType, detailFieldMetricScaleType ]
		    	}		
		    
		    },{
		    	title: LN('sbi.generic.udpValues')
		        , itemId: 'upd-values'
		        , width: 350
		        , items: this.udpValueGrid  
		        , layout: 'fit'
		        , autoScroll  : true   	
		    },this.kpiLinksTab];
 	   
 	   
 	   	
	}
	
	,launchThrWindow : function() {
		
		/*var r = Ext.getCmp('maingrid').getSelectionModel().getSelected();
		var tSelected = r.get('threshold');*/
		var conf = {};
		conf.nodeTypesCd = config.thrTypes;
		conf.thrSeverityTypesCd = config.thrSeverityTypesCd;
		conf.drawSelectColumn = true;
		//conf.toBeSelected = tSelected;
		
		var manageThresholds = new Sbi.kpi.ManageThresholds(conf);
	
		this.thrWin = new Ext.Window({
			title: LN('sbi.lookup.Select') ,   
            layout      : 'fit',
            width       : 955,
            height      : 350,
            y			: 15,
            closeAction :'close',
            plain       : true,
            scope		: this,
            modal		: true,
            autoScroll  : true,
            items       : [manageThresholds]
		});
		manageThresholds.on('selectEvent', function(itemId,index,code){this.thrWin.close();Ext.getCmp('detailFieldThreshold').setValue(code);}, this);
		this.thrWin.show();
	}
	
    //OVERRIDING save method
	,save : function() {
		var values = this.getForm().getFieldValues();
		var idRec = values['id'];
		var newRec;
	
		var storeUdps = this.udpValueGrid.getStore();
		var arrayUdps = this.udpValueGrid.saveUdpValues('KPI');		
		
		if(idRec == 0 || idRec == null || idRec === ''){
			newRec = new Ext.data.Record({
					name: values['name'],
					code: values['code'],
			        description: values['description'],		
			        weight: values['weight'],	
			        dataset: values['dataset'],	
			        threshold: values['threshold'],
			        documents: values['documents'],
			        interpretation: values['interpretation'],			        
			        algdesc: values['algdesc'],	
			        inputAttr: values['inputAttr'],	
			        modelReference: values['modelReference'],
			        targetAudience: values['targetAudience'],		        
			        kpiTypeCd: values['kpiTypeCd'],	
			        metricScaleCd: values['metricScaleCd'],
			        measureTypeCd: values['measureTypeCd'],
			        udpValues: arrayUdps
			});	  
			
		}else{
			var record;
			var length = this.mainElementsStore.getCount();
			for(var i=0;i<length;i++){
	   	        var tempRecord = this.mainElementsStore.getAt(i);
	   	        if(tempRecord.data.id==idRec){
	   	        	record = tempRecord;
				}			   
	   	    }	
			record.set('name',values['name']);
			record.set('code',values['code']);
			record.set('description',values['description']);
			record.set('weight',values['weight']);
			record.set('dataset',values['dataset']);
			record.set('threshold',values['threshold']);
			record.set('documents',values['documents']);
			record.set('interpretation',values['interpretation']);
			record.set('algdesc',values['algdesc']);
			record.set('inputAttr',values['inputAttr']);
			record.set('modelReference',values['modelReference']);
			record.set('targetAudience',values['targetAudience']);	
			record.set('kpiTypeCd',values['kpiTypeCd']);
			record.set('metricScaleCd',values['metricScaleCd']);
			record.set('measureTypeCd',values['measureTypeCd']);
			record.set('udpValues',arrayUdps);
			
		}

        var params = {
        	name :  values['name'],
        	code : values['code'],
        	description : values['description'],
        	weight : values['weight'],
        	dataset : values['dataset'],
        	threshold : values['threshold'],
        	documents : values['documents'],
        	interpretation : values['interpretation'],
        	algdesc : values['algdesc'],
        	inputAttr : values['inputAttr'],
        	modelReference : values['modelReference'],
        	targetAudience : values['targetAudience'],
        	kpiTypeCd : values['kpiTypeCd'],
        	metricScaleCd : values['metricScaleCd'],
        	measureTypeCd : values['measureTypeCd'],
        	udpValuesAtt : arrayUdps.toSource()
        };

        
        if(idRec){
        	params.id = idRec;
        }
        
        Ext.Ajax.request({
            url: this.services['saveItemService'],
            params: params,
            method: 'GET',
            success: function(response, options) {
				if (response !== undefined) {			
		      		if(response.responseText !== undefined) {

		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if(content.responseText !== 'Operation succeded') {
			                    Ext.MessageBox.show({
			                        title: LN('sbi.generic.error'),
			                        msg: content,
			                        width: 150,
			                        buttons: Ext.MessageBox.OK
			                   });
			      		}else{
			      			var itemId = content.id;			      			
			      			
			      			if(newRec != null && newRec != undefined && itemId != null && itemId !==''){
			      				newRec.set('id', itemId);
			      				this.mainElementsStore.add(newRec);  
			      			}

			      			this.mainElementsStore.commitChanges();
			      			
			      			if(newRec != null && newRec != undefined && itemId != null && itemId !==''){
								this.rowselModel.selectLastRow(true);
				            }
			      			record.commit();
			      			// commit changes on store udpValue
			      			if(storeUdps){
			      				storeUdps.commitChanges();
			      			}
			      			
			      			Ext.MessageBox.show({
			                        title: LN('sbi.generic.result'),
			                        msg: LN('sbi.generic.resultMsg'),
			                        width: 200,
			                        buttons: Ext.MessageBox.OK
			                });
			      		}      				 

		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.serviceResponseEmpty'), LN('sbi.generic.serviceError'));
		      		}
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.savingItemError'), LN('sbi.generic.serviceError'));
				}
            },
            failure: function(response) {
	      		if(response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			var errMessage ='';
					for (var count = 0; count < content.errors.length; count++) {
						var anError = content.errors[count];
	        			if (anError.localizedMessage !== undefined && anError.localizedMessage !== '') {
	        				errMessage += anError.localizedMessage;
	        			} else if (anError.message !== undefined && anError.message !== '') {
	        				errMessage += anError.message;
	        			}
	        			if (count < content.errors.length - 1) {
	        				errMessage += '<br/>';
	        			}
					}

	                Ext.MessageBox.show({
	                    title: LN('sbi.generic.validationError'),
	                    msg: errMessage,
	                    width: 400,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}else{
	                Ext.MessageBox.show({
	                    title: LN('sbi.generic.error'),
	                    msg: LN('sbi.generic.savingItemError'),
	                    width: 150,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}
            }
            ,scope: this
        });
    }
	,initKpiLinksTab: function() {
		
		this.lookupColumn = new Ext.grid.ButtonColumn({
		       header:  ' '
		       ,iconCls: 'icon-inspect'
		       ,scope: this
		       ,clickHandler: function(e, t) {
		          var index = this.grid.getView().findRowIndex(t);
		          var selectedRecord = this.grid.store.getAt(index);
		          var param = selectedRecord.get('id');
		          this.grid.fireEvent('select', param);
		       }
		       ,width: 25
		       ,renderer : function(v, p, record){
		           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
		       }
		       , resizable: true
	        }, this); 
  
	    this.deleteLinkColumn = new Ext.grid.ButtonColumn({
		       header:  ' '
		       ,iconCls: 'icon-remove'
		       ,scope: this
		       ,clickHandler: function(e, t) {   
		          var index = this.grid.getView().findRowIndex(t);
		          var selectedRecord = this.grid.store.getAt(index);
		          var relId = selectedRecord.get('relId');
		          this.grid.fireEvent('delete', relId);
		       }
		       ,width: 25
		       ,renderer : function(v, p, record){
		           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
		       }
		       , resizable: true
	     });
		var linkColItems = [
			{id:'id',header: LN('sbi.kpis.parameter'), autoWidth: true, sortable: true, locked:false, dataIndex: 'parameterName', resizable: true},
			{header:  LN('sbi.generic.kpi'), autoWidth: true, sortable: true, resizable: true}
		];

    	this.parameterStore = new Ext.data.JsonStore({	
			fields : [ {name: 'parameterName'},{name: 'kpi'},{name: 'relId'},{}]
	    });
    	
		linkColItems.push(this.deleteLinkColumn);  
		linkColItems.push(this.lookupColumn);  
		
		var linkColModel = new Ext.grid.ColumnModel(linkColItems);
		
 	    var linkspluginsToAdd = [this.deleteLinkColumn, this.lookupColumn]; 

	 	this.rowlinkselModel = new Ext.grid.RowSelectionModel({
	           singleSelect: this.singleSelection
	    });
	 	
		this.kpiLinksGrid = new Ext.grid.GridPanel ({
			id: 'kpilinks-grid',
			store: this.parameterStore,
			autoHeight : true,
			viewConfig : {
	            forceFit: true,
	            scrollOffset: 2 // the grid will never have scrollbars
	        },
			cm: linkColModel,
			sm: this.rowlinkselModel,
			plugins: linkspluginsToAdd ,
			//autoExpandColumn : 'parameterName',
			autoWidth: true,
			autoScroll : true,
            layout:'fit',
            //width: 300,
			//frame: true,
			forceFit: true,
	        singleSelect : true,
	        scope:this
	    }); 

		
    	this.kpiLinksTab = {
		        title: LN('sbi.kpis.linksTitle')
		        , layout: 'fit'
		        , autoHeight : true
		        , autoScroll: true
		        , itemId: 'kpiLinks'
		        , scope: this
			    , border: false
		        , items: this.kpiLinksGrid
		       // , width: 300
		        
		    };
    	
    	this.kpiLinksGrid.on('select', this.launchKpisWindow, this);
    	this.kpiLinksGrid.on('delete', this.deleteKpiLink, this);
 
	}

	, fillKpiLinks : function(row, rec) {
		
		var kpiSelected = rec.data.id;
		//alert(kpiSelected);
		if(kpiSelected!=null){

			var paramsList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "KPI_LINKS"};	
			var loadParams = Sbi.config.serviceRegistry.getServiceUrl({
				serviceName: 'MANAGE_KPIS_ACTION'
				, baseParams: paramsList
			 });	
			
			Ext.Ajax.request({
		          url: loadParams,
		          params: {id: kpiSelected},
		          method: 'GET',
		          success: function(response, options) {   	
					if (response !== undefined) {		
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			//alert(content.rows);
		      			if(content !== undefined) {	  
		      				var record = content.rows;
		      				this.kpiLinksGrid.store.loadData(record);
		      				this.kpiLinksGrid.store.commitChanges();
		      			}
					 } 	
		          }
		          ,scope: this
		    });
	    }else{
	    	this.kpiLinksGrid.store.removeAll();
	    	this.kpiLinksGrid.store.commitChanges();
	    	this.kpiLinksGrid.clearValue();
	    }
		this.kpiLinksGrid.getView().refresh();
	}
	
	,launchKpisWindow : function() {
		
		var conf = {};
		var kpiParent = this.rowselModel.getSelected();
		var kpiParentId = kpiParent.data.id;
		conf.kpiParentId = kpiParentId;
		
		this.manageKpiLinksWin = new Sbi.kpi.ManageKpiWindow(conf);
	
		this.linksWin = new Ext.Window({
			title: LN('sbi.lookup.Select') ,   
            layout      : 'fit',
            width       : 450,
            height      : 250,
            closeAction :'close',
            plain       : false,
            modal		: true,
            scope		: this,
            y			: 20,
            items       : [this.manageKpiLinksWin]
		});
		this.manageKpiLinksWin.on('selected', function(selectedRecord, code){
												var record = this.rowlinkselModel.getSelected();
												record.set('kpi',selectedRecord.data.name);
												record.commit() ;
												this.linksWin.close();
												this.saveKpiLink(selectedRecord, record.data.parameterName);
											}, this);
		this.linksWin.show();
	}
	
	, saveKpiLink : function(kpisel, parameter){
		
		var kpiParent = this.rowselModel.getSelected();
		var kpiParentId = kpiParent.data.id;
		
		var kpiLinked = kpisel.data.id;
		
		var recordRel = this.rowlinkselModel.getSelected();
		var relIdPrv = recordRel.data.relId;
		var paramsList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "KPI_LINK_SAVE"};	
		var loadParams = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MANAGE_KPIS_ACTION'
			, baseParams: paramsList
		 });	
		
		Ext.Ajax.request({
	          url: loadParams,
	          params: {kpiParentId: kpiParentId, kpiLinked : kpiLinked, parameter: parameter, relId: relIdPrv},
	          method: 'GET',
	          success: function(response, options) {   	
				if (response !== undefined) {		
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if(content !== undefined) {	  
	      				//alert(content.id);
	      				var record = this.rowlinkselModel.getSelected();
	      				record.set('relId', content.id);
	      				record.commit();
		      			Ext.MessageBox.show({
	                        title: LN('sbi.generic.result'),
	                        msg: LN('sbi.generic.resultMsg'),
	                        width: 200,
	                        buttons: Ext.MessageBox.OK
		      			});
	      			}
				 } 	
	          }
	          ,failure: function(response) {
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
	
	, deleteKpiLink : function(relId){

		var paramsList = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "KPI_LINK_DELETE"};	
		var loadParams = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'MANAGE_KPIS_ACTION'
			, baseParams: paramsList
		 });	
		
		Ext.Ajax.request({
	          url: loadParams,
	          params: {relId: relId},
	          method: 'GET',
	          success: function(response, options) {   	
				if (response !== undefined) {		
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			if(content !== undefined) {	  
						var deleteRow = this.rowlinkselModel.getSelected();
						deleteRow.set('kpi', '');
						deleteRow.set('relId', '');
						deleteRow.commit();

		      			Ext.MessageBox.show({
	                        title: LN('sbi.generic.result'),
	                        msg: LN('sbi.generic.resultMsg'),
	                        width: 200,
	                        buttons: Ext.MessageBox.OK
		      			});
	      			}
				 } 	
	          }
	          ,failure: function(response) {
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
});