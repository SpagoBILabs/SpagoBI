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
Ext.ns("Sbi.tools");

Sbi.tools.ManageDatasets = function(config) {
	 
	var paramsList = {MESSAGE_DET: "DATASETS_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "DATASET_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "DATASET_DELETE"};
	
	this.configurationObject = {};
	
	this.configurationObject.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_DATASETS_ACTION'
		, baseParams: paramsList
	});
	this.configurationObject.saveItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_DATASETS_ACTION'
		, baseParams: paramsSave
	});
	this.configurationObject.deleteItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_DATASETS_ACTION'
		, baseParams: paramsDel
	});
	
	this.initConfigObject();
	config.configurationObject = this.configurationObject;
	
	var c = Ext.apply({}, config || {}, {});

	Sbi.tools.ManageDatasets.superclass.constructor.call(this, c);	 
	
	this.rowselModel.addListener('rowselect',function(sm, row, rec) { 
		this.getForm().loadRecord(rec);  
     }, this);
};

Ext.extend(Sbi.tools.ManageDatasets, Sbi.widgets.ListDetailForm, {
	
	configurationObject: null
	, gridForm:null
	, mainElementsStore:null

	,initConfigObject:function(){
	    this.configurationObject.fields = ['id'
		                     	          , 'name'
		                    	          , 'label'
		                    	          , 'description'   
		                    	          , 'dsTypeCd'
		                    	          , 'catTypeCd'
		                    	          , 'usedByNDocs'
		                    	          , 'fileName'
		                    	          , 'query'
		                    	          , 'dataSource'
		                    	          , 'wsAddress'
		                    	          , 'wsOperation'
		                    	          , 'script'
		                    	          , 'scriptLanguage'
		                    	          , 'jclassName'
		                    	          ];
		
		this.configurationObject.emptyRecToAdd = new Ext.data.Record({
										  id: 0,
										  name:'', 
										  label:'', 
										  description:'',
										  dsTypeCd:'',
										  catTypeCd:'',
										  usedByNDocs: 0,
										  fileName:'',
		                    	          query:'',
		                    	          dataSource:'',
		                    	          wsAddress:'',
		                    	          wsOperation:'',
		                    	          script:'',
		                    	          scriptLanguage:'',
		                    	          jclassName:''
										 });
		
		this.configurationObject.gridColItems = [
		                                         {id:'label',header: LN('sbi.generic.label'), width: 120, sortable: true, locked:false, dataIndex: 'label'},
		                                         {header: LN('sbi.generic.name'), width: 120, sortable: true, dataIndex: 'name'},
		                                         {header: LN('sbi.generic.type'), width: 50, sortable: true, dataIndex: 'dsTypeCd'},
		                                         {header: LN('sbi.ds.numDocs'), width: 60, sortable: true, dataIndex: 'usedByNDocs'}
		                                        ];
		
		this.configurationObject.panelTitle = LN('sbi.ds.panelTitle');
		this.configurationObject.listTitle = LN('sbi.ds.listTitle');
		
		this.initTabItems();
    }

	,initTabItems: function(){

		//Store of the combobox
 	    this.catTypesStore = new Ext.data.SimpleStore({
 	        fields: ['catTypeCd'],
 	        data: config.catTypeCd,
 	        autoLoad: false
 	    });
 	    
 	   //START list of detail fields
 	   var detailFieldId = {
               name: 'id',
               hidden: true
           };
 		   
 	   var detailFieldName = {
          	 maxLength:40,
        	 minLength:1,
        	 regexText : LN('sbi.roles.alfanumericString'),
             fieldLabel: LN('sbi.generic.name'),
             allowBlank: false,
             validationEvent:true,
             name: 'name'
         };
 			  
 	   var detailFieldLabel = {
          	 maxLength:45,
        	 minLength:1,
        	 regexText : LN('sbi.roles.alfanumericString2'),
             fieldLabel:LN('sbi.generic.label'),
             allowBlank: false,
             validationEvent:true,
             name: 'label'
         };	  
 		   
 	   var detailFieldDescr = {
          	 maxLength:400,
          	 xtype: 'textarea',
       	     width : 250,
             height : 80,
        	 regexText : LN('sbi.roles.alfanumericString'),
             fieldLabel: LN('sbi.generic.descr'),
             validationEvent:true,
             name: 'description'
         };
 		   
 	   var detailFieldCatType =  {
        	  name: 'catTypeCd',
              store: this.catTypesStore,
              width : 120,
              fieldLabel: LN('sbi.ds.catType'),
              displayField: 'catTypeCd',   // what the user sees in the popup
              valueField: 'catTypeCd',        // what is passed to the 'change' event
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
 	  //END list of detail fields
 	   
 	  this.detailTab = new Ext.Panel({
	        title: LN('sbi.generic.details')
	        , itemId: 'detail'
	        , width: 430
	        , items: {
		   		 id: 'items-detail',   	
	 		   	 itemId: 'items-detail',   	              
	 		   	 columnWidth: 0.4,
	             xtype: 'fieldset',
	             labelWidth: 90,
	             defaults: {width: 200, border:false},    
	             defaultType: 'textfield',
	             autoHeight: true,
	             autoScroll  : true,
	             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
	             border: false,
	             style: {
	                 "margin-left": "10px", 
	                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
	             },
	             items: [detailFieldId, detailFieldLabel, detailFieldName, 
	                     detailFieldDescr, detailFieldCatType]
	    	}
	    });

 	//DataSource Store types combobox
 	   this.dsTypesStore = new Ext.data.SimpleStore({
	        fields: ['dsTypeCd'],
	        data: config.dsTypes,
	        autoLoad: false
	    });
 	  
	    this.dataSourceStore = new Ext.data.SimpleStore({
	        fields: ['dataSource'],
	        data: config.dataSourceLabels,
	        autoLoad: false
	    });
	    
	    this.scriptLanguagesStore = new Ext.data.SimpleStore({
	        fields: ['scriptLanguage'],
	        data: config.scriptTypes,
	        autoLoad: false
	    });
	    
	  //START list of Advanced fields
	    var detailDsType =  {
	      	    name: 'dsTypeCd',
	            store: this.dsTypesStore,
	            width : 120,
	            fieldLabel: LN('sbi.ds.dsTypeCd'),
	            displayField: 'dsTypeCd',   // what the user sees in the popup
	            valueField: 'dsTypeCd',        // what is passed to the 'change' event
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
	    
	    var detailFileName = {
	          	 maxLength:40,
	        	 minLength:1,
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.ds.fileName'),
	             allowBlank: true,
	             validationEvent:true,
	             name: 'fileName'
	         };
	    
	    var detailDataSource =  {
	      	    name: 'dataSource',
	            store: this.dataSourceStore,
	            width : 120,
	            fieldLabel: LN('sbi.ds.dataSource'),
	            displayField: 'dataSource',   // what the user sees in the popup
	            valueField: 'dataSource',        // what is passed to the 'change' event
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
	    
	    var detailQuery = {
	             maxLength:1000,
	             xtype: 'textarea',
	        	 width : this.textAreaWidth,
	             height : 150,
	          	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.ds.query'),
	             validationEvent:true,
	             name: 'query'
	           };
	    
	    var detailWsAddress = {
	          	 maxLength:40,
	        	 minLength:1,
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.ds.wsAddress'),
	             allowBlank: true,
	             validationEvent:true,
	             name: 'wsAddress'
	         };
	    
	    var detailWsOperation = {
	          	 maxLength:40,
	        	 minLength:1,
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.ds.wsOperation'),
	             allowBlank: true,
	             validationEvent:true,
	             name: 'wsOperation'
	         };
	    
	    var detailScript = {
	    		maxLength:1000,
	             xtype: 'textarea',
	        	 width : this.textAreaWidth,
	             height : 50,
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.ds.script'),
	             allowBlank: true,
	             validationEvent:true,
	             name: 'script'
	         };
	    
	    var detailScriptLanguage =  {
	      	    name: 'scriptLanguage',
	            store: this.scriptLanguagesStore,
	            width : 120,
	            fieldLabel: LN('sbi.ds.scriptLanguage'),
	            displayField: 'scriptLanguage',   // what the user sees in the popup
	            valueField: 'scriptLanguage',        // what is passed to the 'change' event
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
	    
	    var detailJclassName = {
	          	 maxLength:40,
	        	 minLength:1,
	        	 regexText : LN('sbi.roles.alfanumericString'),
	             fieldLabel: LN('sbi.ds.jclassName'),
	             allowBlank: true,
	             validationEvent:true,
	             name: 'jclassName'
	         };
	    
 	   
 	   this.configurationObject.tabItems = [this.detailTab,{
	    	title: LN('sbi.generic.type')
	        , itemId: 'advanced'
	        , width: 350
	        , items: {
		   		 id: 'advanced-detail',   	
	 		   	 itemId: 'advanced-detail',   	              
	 		   	// columnWidth: 0.4,
	             xtype: 'fieldset',
	             scope: this,
	             labelWidth: 90,
	             defaults: {width: 280, border:false},    
	             defaultType: 'textfield',
	             autoHeight: true,
	             autoScroll  : true,
	             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:0px 0px;',
	             border: false,
	             style: {
	                 "margin-left": "10px", 
	                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
	             },
	             items: [detailDsType,
	                     detailFileName, detailDataSource, 
	                     detailQuery, detailWsAddress, 
	                     detailWsOperation, detailScript,
	                     detailScriptLanguage, detailJclassName ]
	    	}			    
	    }];
	}
	
    //OVERRIDING save method
	,save : function() {
		var values = this.getForm().getFieldValues();
		var idRec = values['id'];
		var newRec;
	
		if(idRec ==0 || idRec == null || idRec === ''){
			newRec =new Ext.data.Record({
					name: values['name'],
					code: values['code'],
			        description: values['description'],			        
			        tablename: values['tablename'],	
			        columnname: values['columnname'],
			        typeCd: values['typeCd']
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
			record.set('tablename',values['tablename']);
			record.set('columnname',values['columnname']);
			record.set('typeCd',values['typeCd']);		
		}

        var params = {
        	name :  values['name'],
        	code : values['code'],
        	description : values['description'],
        	tablename : values['tablename'],
        	columnname : values['columnname'],
        	typeCd : values['typeCd']	
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

});
