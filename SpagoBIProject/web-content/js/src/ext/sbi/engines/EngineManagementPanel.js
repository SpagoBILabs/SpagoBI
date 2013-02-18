/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

/**
 * Object name
 * 
 * [description]
 *
 * Authors - ...
 */
Ext.ns("Sbi.engines");

Sbi.engines.EngineManagementPanel = function(config) {

	var defaultSettings = {
		singleSelection : true
	};
			 
	if(Sbi.settings && Sbi.settings.engines && Sbi.settings.engines.engineManagementPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.engines.engineManagementPanel);
	}
			 
	var c = Ext.apply(defaultSettings, config || {});
	c.configurationObject = this.initConfigObject();
	
	//Ext.apply(this, c);
			  
	Sbi.engines.EngineManagementPanel.superclass.constructor.call(this, c);

	
	this.rowselModel.addListener('rowselect', function(sm, row, rec) {
		var record = this.rowselModel.getSelected();

		this.setValues(record);
		
	}, this);
	
};

Ext.extend(Sbi.engines.EngineManagementPanel, Sbi.widgets.ListDetailForm, {
	// ---------------------------------------------------------------------------
    // object's members
	// ---------------------------------------------------------------------------
	configurationObject : null
	
	// ---------------------------------------------------------------------------
    // public methods
	// ---------------------------------------------------------------------------
	
	//---------------------------------------------------------------------------
	// private methods
	// ---------------------------------------------------------------------------
	
	, initConfigObject : function() {
	
		this.configurationObject = this.configurationObject || {};
		
		this.initMasterGridConf();
		this.initCrudServicesConf();
		this.initButtonsConf();
		this.initTabsConf();
				
		return this.configurationObject;
	}

	, initCrudServicesConf: function() {
		this.configurationObject = this.configurationObject || {};
		
		this.configurationObject.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName : 'MANAGE_ENGINE_ACTION',
			baseParams : {
				MESSAGE_DET : "ENGINE_LIST"
			}
		});
		
		this.configurationObject.saveItemService = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName : 'MANAGE_ENGINE_ACTION',
			baseParams : {
				LIGHT_NAVIGATOR_DISABLED : 'TRUE',
				MESSAGE_DET : "ENGINE_INSERT"
			}
		});
		
		this.configurationObject.deleteItemService = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName : 'MANAGE_ENGINE_ACTION',
			baseParams : {
				LIGHT_NAVIGATOR_DISABLED : 'TRUE',
				MESSAGE_DET : "ENGINE_DELETE"
			}
		});
	}

    , initMasterGridConf: function() {
    	
    	
    	this.configurationObject = this.configurationObject || {};
    	
    	this.configurationObject.fields = [ 
    	    "id",
    	    "label",
    	    "name",
    	    "description",
    	    "documentType",
    	    "engineType",
    	    "useDataSet",
    	    "useDataSource",
    	    "class",
    	    "url",
    	    "driver",
    	    "secondaryUrl",
    	    "dataSourceId"
    	    ];
  		
    	this.configurationObject.emptyRecToAdd = new Ext.data.Record({	id : null,
    		name : '',
    		label : '',
    		description : '',
    		documentType : '',
    		engineType : '',
    		useDataSet : '',
    		useDataSource : '',
    		class :  '',
    		url : '',
    		driver : '',
    		secondaryUrl : '',
    		dataSourceId : ''
    	});

    	 this.configurationObject.gridColItems = [ {
    	                    			id : 'label',
    	                    			header : LN('sbi.generic.label'),
    	                    			width : 140,
    	                    			sortable : true,
    	                    			locked : false,
    	                    			dataIndex : 'label'
    	                    		}, {
    	                    			header : LN('sbi.generic.name'),
    	                    			width : 150,
    	                    			sortable : true,
    	                    			dataIndex : 'name'
    	                    		}, {
    	                    			header : LN('description'),
    	                    			width : 140,
    	                    			sortable : true,
    	                    			dataIndex : 'description'
    	                    		}

    	                    		];

    	 this.configurationObject.panelTitle = LN('sbi.ds.panelTitle');
    	 this.configurationObject.listTitle = LN('sbi.ds.listTitle');
    	       
    	 this.configurationObject.filter = true;
 		 this.configurationObject.columnName = [['sbiDsConfig.label', LN('sbi.generic.label')],
 		                                       ['sbiDsConfig.name', LN('sbi.generic.name')]
 		                	                   ];
 		 this.configurationObject.setCloneButton = true;
    }
    
    , initButtonsConf: function() {
    	this.configurationObject = this.configurationObject || {};
    	
    	var tbButtonsArray = new Array();
//		this.tbProfAttrsButton = new Ext.Toolbar.Button({
//	            text: LN('sbi.ds.pars'),
//	            iconCls: 'icon-profattr',
//	            handler: this.profileAttrs,
//	            width: 30,
//	            scope: this
//	            });
//		tbButtonsArray.push(this.tbProfAttrsButton);
		
//		this.tbInfoButton = new Ext.Toolbar.Button({
//	            text: LN('sbi.ds.help'),
//	            iconCls: 'icon-info',
//	            handler: this.info,
//	            width: 30,
//	            scope: this
//	            });
//		tbButtonsArray.push(this.tbInfoButton);
		
		this.tbTransfInfoButton = new Ext.Toolbar.Button({
	            text: LN('sbi.ds.help'),
	            iconCls: 'icon-info',
	            handler: this.transfInfo,
	            width: 30,
	            scope: this
	            });
		tbButtonsArray.push(this.tbTransfInfoButton);
		this.configurationObject.tbButtonsArray = tbButtonsArray;
    }
    
    , initTabsConf : function() {	
    	this.configurationObject = this.configurationObject || {};
    	
		this.initDetailTab();
		this.configurationObject.tabItems = [ this.detailTab ];
	}
	
    , initDetailTab : function() {
//		this.profileAttributesStore = new Ext.data.SimpleStore({
//			fields : [ 'profAttrs' ],
//			data : config.attrs,
//			autoLoad : false
//		});
		
		// Store of the combobox
		this.documentTypesStore = new Ext.data.SimpleStore({
			fields : [ 'catTypeVn' ],
			data : config.catTypeVn,
			autoLoad : false
		});

		this.dataSourceStore = new Ext.data.SimpleStore({
			fields : [ 'dataSourceLabels' ],
			data : config.dataSourceLabels,
			autoLoad : false
		});
		

		// START list of detail fields
		var detailFieldName = {
			maxLength : 50,	minLength : 1, width : 350,
			regexText : LN('sbi.roles.alfanumericString'),
			fieldLabel : LN('sbi.generic.name'),
			allowBlank : false,	validationEvent : true,
			name : 'name'
		};

		var detailFieldLabel = {
			maxLength : 50, minLength : 1, width : 350,
			regexText : LN('sbi.roles.alfanumericString2'),
			fieldLabel : LN('sbi.generic.label'),
			allowBlank : false, validationEvent : true,
			name : 'label'
		};

		var detailFieldDescr = {
			xtype : 'textarea',
			width : 350, height : 80, maxLength : 160,
			regexText : LN('sbi.roles.alfanumericString'),
			fieldLabel : LN('sbi.generic.descr'),
			validationEvent : true,
			name : 'description'
		};

		var detailFieldDocumentType = {
			name : 'documentType',
			store : this.documentTypesStore,
			width : 150,
			fieldLabel : 'Document Type',
			displayField : 'documentType', 
			valueField : 'documentType', 
			typeAhead : true, forceSelection : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true, editable : false,
			allowBlank : true, validationEvent : true,
			xtype : 'combo'
		};
		
		// added
		var detailFieldEngineType = {
				name : 'engineType',
				store : this.documentTypesStore,
				width : 150,
				fieldLabel : 'Engine Types',
				displayField : 'engineType', 
				valueField : 'engineType', 
				typeAhead : true, forceSelection : true,
				mode : 'local',
				triggerAction : 'all',
				selectOnFocus : true, editable : false,
				allowBlank : true, validationEvent : true,
				xtype : 'combo'
			};
		
		var detailFieldUseDataSet = {
            xtype: 'checkboxgroup',
            columns: 1,
            boxMinWidth  : 150,
            hideLabel  : false,
            fieldLabel: 'Use Data Set',
            itemId: 'isUseDataSet',
            items: [
                { boxLabel: 'Use Data Set',name: 'useDataSet', checked:'useDataSet',inputValue: 1}
            ]
        };
		
		var detailFieldUseDataSource = {
	            xtype: 'checkboxgroup',
	            columns: 1,
	            boxMinWidth  : 150,
	            hideLabel  : false,
	            fieldLabel: 'Use Data Source',
	            itemId: 'isUseDataSource',
	            items: [
	                {boxLabel: 'Use Data Source', name: 'useDataSource', checked:'useDataSource',inputValue: 1}
	            ]
	        };
		
		var detailFieldDataSource = {
				name : 'dataSourceId',
				store : this.dataSourceStore,
				width : 150,
				fieldLabel : 'Data Source',
				displayField : 'dataSourceId', 
				valueField : 'dataSourceId', 
				typeAhead : true, forceSelection : true,
				mode : 'local',
				triggerAction : 'all',
				selectOnFocus : true, editable : false,
				allowBlank : true, validationEvent : true,
				xtype : 'combo'
			};
		
		var detailFieldUrl = {
				maxLength : 50,	minLength : 1, width : 350,
				regexText : LN('sbi.roles.alfanumericString'),
				fieldLabel : 'Url',
				allowBlank : false,	validationEvent : true,
				name : 'url'
			};
		
		var detailFieldSecondaryUrl = {
				maxLength : 50,	minLength : 1, width : 350,
				regexText : LN('sbi.roles.alfanumericString'),
				fieldLabel : 'Secondary Url',
				allowBlank : false,	validationEvent : true,
				name : 'secondaryUrl'
			};
		
		var detailFieldDriverName = {
				maxLength : 50,	minLength : 1, width : 350,
				regexText : LN('sbi.roles.alfanumericString'),
				fieldLabel : 'Driver Name',
				allowBlank : false,	validationEvent : true,
				name : 'driver'
			};
		// END list of detail fields

		var c = {};


		this.detailTab = new Ext.Panel(
				{
					title : LN('sbi.generic.details'),
					itemId : 'detail',
					width : 430,
					items : {
						id : 'items-detail',
						itemId : 'items-detail',
						columnWidth : 0.4,
						xtype : 'fieldset',
						labelWidth : 90,
						defaultType : 'textfield',
						autoHeight : true, autoScroll : true,
						bodyStyle : Ext.isIE ? 'padding:0 0 8px 10px;'
								: 'padding:8px 10px;',
						border : false,
						
						style : {
							"margin-left" : "8px",
							"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-5px"
									: "-8px")
									: "8"
						},
						items : [ 
						        detailFieldLabel, detailFieldName,
								detailFieldDescr, detailFieldDocumentType, detailFieldEngineType, detailFieldUseDataSet,detailFieldUseDataSource, detailFieldDataSource,
								detailFieldUrl,detailFieldSecondaryUrl,detailFieldDriverName
								// this.manageDsVersionsPanel ,
							// this.detailFieldUserIn,this.detailFieldDateIn,this.detailFieldVersNum,this.detailFieldVersId,this.detailFieldId
								]
					}
				});
	}
	
    
    

	
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
			

				// OVERRIDING METHOD
				, addNewItem : function() {
					this.newRecord = new Ext.data.Record(
							{	name : '',
            					label : '',
            					description : '',
            					documentType : '',
            					engineType : '',
            					useDataSet : 'false',
            					useDataSource : 'false',
            					class :  '',
            					url : '',
            					driver : '',
            					secondaryUrl : '',
            					dataSourceId : ''
							});
					this.setValues(this.newRecord);


					this.tabs.items.each(function(item) {
						item.doLayout();
					});

					if (this.newRecord != null
							&& this.newRecord != undefined) {
						this.mainElementsStore.add(this.newRecord);
						this.rowselModel.selectLastRow(true);
					}				
					this.tabs.setActiveTab(0);					
				}

				,cloneItem: function() {	
					var values = this.getValues();
					var params = this.buildParamsToSendToServer(values);
					var arrayPars = this.manageParsGrid.getParsArray();
					
					this.newRecord = this.buildNewRecordToSave(values);		
					this.newRecord.set('pars',arrayPars);
					this.setValues(this.newRecord);
					
					if (arrayPars) {
						this.manageParsGrid.loadItems(arrayPars);
					}else{
						this.manageParsGrid.loadItems([]);
					}
					this.manageDsVersionsGrid.loadItems([]);
					
					this.tabs.items.each(function(item) {
						item.doLayout();
					});					
					if (this.newRecord != null
							&& this.newRecord != undefined) {
						this.mainElementsStore.add(this.newRecord);
						this.rowselModel.selectLastRow(true);
					}
					this.tabs.setActiveTab(0);
			    }

				
				,buildNewRecordToSave: function(values){
					var newRec = new Ext.data.Record({
						id: null,
						name : values['name'],
			    		label : values['label'],
			    		description : values['description'],
			    		documentType : values['documentType'],
			    		engineType : values['engineType'],
			    		useDataSet : values['useDataSet'],
			    		useDataSource : values['useDataSource'],
			    		class :  values['class'],
			    		url :  values['url'],
			    		driver : values['driver'],
			    		secondaryUrl : values['secondaryUrl'],
			    		dataSourceId : values['dataSourceId']
						});
					return newRec;
				}	
				
				,buildParamsToSendToServer: function(values){
					var params = {
							id: null,
							name : values['name'],
				    		label : values['label'],
				    		description : values['description'],
				    		documentType : values['documentType'],
				    		engineType : values['engineType'],
				    		useDataSet : values['useDataSet'],
				    		useDataSource : values['useDataSource'],
				    		class :  values['class'],
				    		url :  values['url'],
				    		driver : values['driver'],
				    		secondaryUrl : values['secondaryUrl'],
				    		dataSourceId : values['dataSourceId']
						};
					return params;
				}
				
				,updateNewRecord: function(record, values, arrayPars, customString){
					record.set('label',values['label']);
					record.set('name',values['name']);
					record.set('description',values['description']);
					record.set('documentType',values['documentType']);
					record.set('engineType',values['engineType']);
					record.set('useDataSet',values['useDataSet']);
					record.set('class',values['class']);
					record.set('url',values['url']);
					record.set('driver',values['driver']);
					record.set('secondaryUrl',values['secondaryUrl']);
					record.set('dataSourceId',values['dataSourceId']);					

					
					if (arrayPars) {
						record.set('pars',arrayPars);
					}
					
					if (customString) {
						record.set('customData',customString);
					}

					
				}
				
				, updateMainStore: function(idRec){
					var values = this.getValues();
					var record;
					var length = this.mainElementsStore.getCount();
					for(var i=0;i<length;i++){
			   	        var tempRecord = this.mainElementsStore.getAt(i);
			   	        if(tempRecord.data.id==idRec){
			   	        	record = tempRecord;
						}			   
			   	    }	
					var params = this.buildParamsToSendToServer(values);
					var arrayPars = this.manageParsGrid.getParsArray();
					this.updateNewRecord(record,values,arrayPars);
					this.mainElementsStore.commitChanges();
				}
				
				, updateDsVersionsOfMainStore: function(idRec){
					var arrayVersions = this.manageDsVersionsGrid.getCurrentDsVersions();
					if (arrayVersions) {
						var record;
						var length = this.mainElementsStore.getCount();
						for(var i=0;i<length;i++){
				   	        var tempRecord = this.mainElementsStore.getAt(i);
				   	        if(tempRecord.data.id==idRec){
				   	        	record = tempRecord;
							}			   
				   	    }	
						record.set('dsVersions',arrayVersions);
						this.mainElementsStore.commitChanges();			
					}
				}
				
				, getValues: function() {
					var values = this.getForm().getFieldValues();

					
					return values;
					
				}
				
				, setValues: function(record) {
					this.getForm().loadRecord(record);
				
	
				}
				
				// OVERRIDING save method
				, save : function () {
					var values = this.getValues();
					var idRec = values['id'];
					if (idRec == 0 || idRec == null || idRec === '') {
						this.doSave("yes");
					} else {
						Ext.MessageBox.confirm(
							LN('sbi.ds.recalculatemetadataconfirm.title')
							, LN('sbi.ds.recalculatemetadataconfirm.msg')
							, this.doSave
							, this
						);
					}
				}
				
				, doSave : function(recalculateMetadata) {
					var values = this.getValues();
					
					var idRec = values['id'];
					var newRec;
					var newDsVersion;
					var isNewRec = false;
					var params = this.buildParamsToSendToServer(values);
					params.recalculateMetadata = recalculateMetadata;
					var arrayPars = this.manageParsGrid.getParsArray();
					var customString = this.customDataGrid.getDataString();
					
					if (idRec == 0 || idRec == null || idRec === '') {
						this.updateNewRecord(this.newRecord,values,arrayPars, customString);
						isNewRec = true;
					}else{
						var record;
						var oldType;
						var length = this.mainElementsStore.getCount();
						for(var i=0;i<length;i++){
				   	        var tempRecord = this.mainElementsStore.getAt(i);
				   	        if(tempRecord.data.id==idRec){
				   	        	record = tempRecord;
				   	        	oldType = record.get('dsTypeCd');
							}			   
				   	    }	
						this.updateNewRecord(record,values,arrayPars, customString);
						
						newDsVersion = new Ext.data.Record(
								{	dsId: values['id'],
									dateIn : values['dateIn'],
									userIn : values['userIn'],
									versId : values['versId'],
									type : oldType, 
									versNum : values['versNum']
								});
					}
					
					if (arrayPars) {
						params.pars = Ext.util.JSON.encode(arrayPars);
					}
					if (customString) {
						params.customData = Ext.util.JSON.encode(customString);
					}

					
					if (idRec) {
						params.id = idRec;
					}

					Ext.Ajax
							.request({
								url : this.services['saveItemService'],
								params : params,
								method : 'POST',
								success : function(response, options) {
									if (response !== undefined) {
										if (response.responseText !== undefined) {

											var content = Ext.util.JSON
													.decode(response.responseText);
											if (content.responseText !== 'Operation succeded') {
												Ext.MessageBox
														.show({
															title : LN('sbi.generic.error'),
															msg : content,
															width : 150,
															buttons : Ext.MessageBox.OK
														});
											} else {
												var itemId = content.id;
												var dateIn = content.dateIn;
												var userIn = content.userIn;
												var versId = content.versId;
												var versNum = content.versNum;

												if (isNewRec
														&& itemId != null
														&& itemId !== '') {
		
													var record;
													var length = this.mainElementsStore.getCount();
													for(var i=0;i<length;i++){
											   	        var tempRecord = this.mainElementsStore.getAt(i);
											   	        if(tempRecord.data.id==0){
											   	        	tempRecord.set('id',itemId);
											   	        	tempRecord.set('dateIn',dateIn);
											   	        	tempRecord.set('userIn',userIn);
											   	        	tempRecord.set('versId',versId);
											   	        	tempRecord.set('versNum',versNum);
											   	        	tempRecord.commit();
											   	        	this.detailFieldId.setValue(itemId);
											   	        	this.detailFieldUserIn.setValue(userIn);
											   	        	this.detailFieldDateIn.setValue(dateIn);
											   	        	this.detailFieldVersNum.setValue(versNum);
											   	        	this.detailFieldVersId.setValue(versId);
														}			   
											   	    }
												}else{
													if(newDsVersion!= null && newDsVersion != undefined){
														this.manageDsVersionsGrid.getStore().addSorted(newDsVersion);
														this.manageDsVersionsGrid.getStore().commitChanges();
														var values = this.getValues();
														this.updateDsVersionsOfMainStore(values['id']);
													}
												}
												this.mainElementsStore.commitChanges();
												if (isNewRec
														&& itemId != null
														&& itemId !== '') {
													this.rowselModel.selectLastRow(true);
												}

												Ext.MessageBox
														.show({
															title : LN('sbi.generic.result'),
															msg : LN('sbi.generic.resultMsg'),
															width : 200,
															buttons : Ext.MessageBox.OK
														});
											}

										} else {
											Sbi.exception.ExceptionHandler
													.showErrorMessage(
															LN('sbi.generic.serviceResponseEmpty'),
															LN('sbi.generic.serviceError'));
										}
									} else {
										Sbi.exception.ExceptionHandler
												.showErrorMessage(
														LN('sbi.generic.savingItemError'),
														LN('sbi.generic.serviceError'));
									}
								},
								failure : Sbi.exception.ExceptionHandler.handleFailure,
								scope : this
							});
				}

//				,
//				manageQbeQuery : function(theQbeDatasetBuilder, qbeQuery) {
//					var jsonQuery = qbeQuery.data.jsonQuery;
//					this.qbeJSONQuery.setValue(Ext.util.JSON.encode(jsonQuery));
//					var sqlQuery = qbeQuery.data.sqlQuery.sql;
//					this.qbeSQLQuery.setValue(sqlQuery);
//					var datamarts = qbeQuery.data.datamarts;
//					this.qbeDatamarts.setValue(datamarts);
//					var parameters = qbeQuery.data.parameters;
//					this.manageParsGrid.loadItems([]);
//					this.manageParsGrid.loadItems(parameters);
//				}
				
				,
				jsonTriggerFieldHandler : function() {
					var values = this.getValues();
					var datasetId = values['id'];
					var datasourceLabel = this.detailQbeDataSource.getValue();
					if (datasourceLabel == '') {
						Ext.MessageBox.show({
							title : LN('sbi.generic.error'),
							msg : LN('sbi.tools.managedatasets.errors.missingdatasource'),
							width : 150,
							buttons : Ext.MessageBox.OK
						});
						return;
					}
					if (datamart == '') {
						Ext.MessageBox.show({
							title : LN('sbi.generic.error'),
							msg : LN('sbi.tools.managedatasets.errors.missingdatamart'),
							width : 150,
							buttons : Ext.MessageBox.OK
						});
						return;
					}
					var datamart = this.qbeDatamarts.getValue();
					this.initQbeDataSetBuilder(datasourceLabel, datamart, datasetId);
					this.qbeDataSetBuilder.show();
				}
				
//				, initQbeDataSetBuilder: function(datasourceLabel, datamart, datasetId) {
//					if (this.qbeDataSetBuilder === null) {
//						this.initNewQbeDataSetBuilder(datasourceLabel, datamart, datasetId);
//						return;
//					}
//					if (this.mustRefreshQbeView(datasourceLabel, datamart, datasetId)) {
//						this.qbeDataSetBuilder.destroy();
//						this.initNewQbeDataSetBuilder(datasourceLabel, datamart, datasetId);
//						return;
//					}
//				}
				
//				, initNewQbeDataSetBuilder: function(datasourceLabel, datamart, datasetId) {
//					this.qbeDataSetBuilder = new Sbi.tools.dataset.QbeDatasetBuilder({
//						datasourceLabel : datasourceLabel,
//						datamart : datamart,
//						datasetId : datasetId,
//						jsonQuery : this.qbeJSONQuery.getValue(),
//						qbeParameters : this.manageParsGrid.getParsArray(),
//						modal : true,
//						width : this.getWidth() - 50,
//						height : this.getHeight() - 50,
//						listeners : {
//							hide : 
//								{
//									fn : function(theQbeDatasetBuilder) {
//											theQbeDatasetBuilder.getQbeQuery(); // asynchronous
//									}
//									, scope: this
//								},
//							gotqbequery : {
//									fn: this.manageQbeQuery
//									, scope: this
//							}
//						}
//					});
//				}
				
//				, mustRefreshQbeView: function(datasourceLabel, datamart, datasetId) {
//					if (datasourceLabel == this.qbeDataSetBuilder.getDatasourceLabel()
//							&& datamart == this.qbeDataSetBuilder.getDatamart()
//							&& datasetId == this.qbeDataSetBuilder.getDatasetId()) {
//						return false;
//					}
//					return true;
//				}
				
				// METHOD TO BE OVERRIDDEN IN EXTENDED ELEMENT!!!!!
				,info : function() {		
					var win_info_2;
					if(!win_info_2){
						win_info_2= new Ext.Window({
							id:'win_info_2',
							autoLoad: {url: Sbi.config.contextName+'/themes/'+Sbi.config.currTheme+'/html/dsrules.html'},             				
							layout:'fit',
							width:620,
							height:410,
							autoScroll: true,
							closeAction:'close',
							buttonAlign : 'left',
							plain: true,
							title: LN('sbi.ds.help')
						});
					};
					win_info_2.show();
			    }
				
//				,transfInfo: function() {		
//					var win_info_4;
//					if(!win_info_4){
//						win_info_4= new Ext.Window({
//							id:'win_info_4',
//							autoLoad: {url: Sbi.config.contextName+'/themes/'+Sbi.config.currTheme+'/html/dsTrasformationHelp.html'},             				
//							layout:'fit',
//							width:760,
//							height:420,
//							autoScroll: true,
//							closeAction:'close',
//							buttonAlign : 'left',
//							plain: true,
//							title: LN('sbi.ds.help')
//						});
//					};
//					win_info_4.show();
//			    }
				
//				,profileAttrs: function() {		
//					var win_info_3;
//					if(!win_info_3){
//						win_info_3= new Ext.Window({
//							id:'win_info_3',          				
//							layout:'fit',
//							width:220,
//							height:350,
//							closeAction:'close',
//							buttonAlign : 'left',
//							autoScroll: true,
//							plain: true,
//							items: {  
//						        xtype: 'grid',
//						        border: false,
//						        columns: [{header: LN('sbi.ds.pars'),width : 170}],                
//						        store: this.profileAttributesStore
//						    }
//						});
//					};
//					win_info_3.show();
//			    }

});
