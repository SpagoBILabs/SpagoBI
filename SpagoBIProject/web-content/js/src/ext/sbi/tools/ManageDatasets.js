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

	var paramsList = {
		MESSAGE_DET : "DATASETS_LIST"
	};
	var paramsSave = {
		LIGHT_NAVIGATOR_DISABLED : 'TRUE',
		MESSAGE_DET : "DATASET_INSERT"
	};
	var paramsDel = {
		LIGHT_NAVIGATOR_DISABLED : 'TRUE',
		MESSAGE_DET : "DATASET_DELETE"
	};

	this.configurationObject = {};

	this.configurationObject.manageListService = Sbi.config.serviceRegistry
			.getServiceUrl({
				serviceName : 'MANAGE_DATASETS_ACTION',
				baseParams : paramsList
			});
	this.configurationObject.saveItemService = Sbi.config.serviceRegistry
			.getServiceUrl({
				serviceName : 'MANAGE_DATASETS_ACTION',
				baseParams : paramsSave
			});
	this.configurationObject.deleteItemService = Sbi.config.serviceRegistry
			.getServiceUrl({
				serviceName : 'MANAGE_DATASETS_ACTION',
				baseParams : paramsDel
			});
	this.configurationObject.getDatamartsService = Sbi.config.qbeGetDatamartsUrl;

	this.initConfigObject();
	this.configurationObject.filter = true;
	this.configurationObject.columnName = [['sbiDsConfig.label', LN('sbi.generic.label')],
	                                       ['sbiDsConfig.name', LN('sbi.generic.name')],
	                                       ['category.valueNm', LN('sbi.ds.catType')]
	                	                   ];
	this.configurationObject.setCloneButton = true;
	config.configurationObject = this.configurationObject;
	config.singleSelection = true;

	var c = Ext.apply({}, config || {}, {});

	Sbi.tools.ManageDatasets.superclass.constructor.call(this, c);

	this.rowselModel.addListener('rowselect', function(sm, row, rec) {
		this.activateDsTypeForm(null, rec, row);
		this.activateTransfForm(null, rec, row);
		this.activateDsVersionsGrid(null, rec, row);
		this.activateDsTestTab(this.datasetTestTab);
		this.getForm().loadRecord(rec);
		// destroy the qbe query builder, if existing
		if (this.qbeDataSetBuilder != null) {
			this.qbeDataSetBuilder.destroy();
			this.qbeDataSetBuilder = null;
		}
	}, this);
	
	this.tabs.addListener('tabchange', this.modifyToolbar, this);
};

Ext.extend(
			Sbi.tools.ManageDatasets,
			Sbi.widgets.ListDetailForm,
			{

				configurationObject : null,
				tbInfoButton: null,
				tbProfAttrsButton : null,
				tbTransfInfoButton: null,
				gridForm : null,
				mainElementsStore : null,
				profileAttributesStore: null,
				trasfDetail : null,
				jClassDetail : null,
				customDataDetail : null,		
				scriptDetail : null,
				queryDetail : null,
				WSDetail : null,
				fileDetail : null,
				parsGrid : null,
				datasetTestTab : null,
				manageParsGrid : null,
				manageDsVersionsGrid : null,
				newRecord: null,
				detailFieldId: null,
				detailFieldUserIn: null,
				detailFieldDateIn: null,
				detailFieldVersNum: null,
				detailFieldVersId: null,
				qbeDataSetBuilder: null

				, modifyToolbar : function(tabpanel, panel){
					var itemId = panel.getItemId();
					if(itemId !== undefined && itemId !== null && itemId === 'advanced'){
						this.tbInfoButton.show();
						this.tbProfAttrsButton.show();
						this.tbTransfInfoButton.hide();
					}else if(itemId !== undefined && itemId !== null && itemId === 'transf'){
						this.tbTransfInfoButton.show();
						this.tbInfoButton.hide();
						this.tbProfAttrsButton.hide();
					}else{
						this.tbInfoButton.hide();
						this.tbProfAttrsButton.hide();
						this.tbTransfInfoButton.hide();
					}	
				}
			
				,activateTransfForm : function(combo, record, index) {
					var transfSelected = record.get('trasfTypeCd');
					if (transfSelected != null
							&& transfSelected == 'PIVOT_TRANSFOMER') {
						this.trasfDetail.setVisible(true);
					} else {
						this.trasfDetail.setVisible(false);
					}
				}

				,activateDsTestTab : function(panel) {
					if (panel) {
						var record = this.rowselModel.getSelected();
						if (record) {
							var dsParsList = this.manageParsGrid
									.getParsArray();
							this.parsGrid.fillParameters(dsParsList);
						}
					}
				}
				,activateDsVersionsGrid : function(combo, record, index) {
					var dsVersionsList = record.get('dsVersions');
					this.manageDsVersionsGrid.loadItems(dsVersionsList);
				}

				,activateDsTypeForm : function(combo, record, index) {

					var dsTypeSelected = record.get('dsTypeCd');
					if (dsTypeSelected != null && dsTypeSelected == 'File') {
						this.fileDetail.setVisible(true);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.customDataDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null && dsTypeSelected == 'Query') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(true);
						this.jClassDetail.setVisible(false);
						this.customDataDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null && dsTypeSelected == 'Java Class') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(true);
						this.customDataDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null && dsTypeSelected == 'Web Service') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.customDataDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(true);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null && dsTypeSelected == 'Script') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.customDataDetail.setVisible(false);
						this.scriptDetail.setVisible(true);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null && dsTypeSelected == 'Qbe') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.customDataDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(true);
					} else if (dsTypeSelected != null && dsTypeSelected == 'Custom') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
						this.customDataDetail.setVisible(true);
					} else if (dsTypeSelected != null || dsTypeSelected == '') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
						this.customDataDetail.setVisible(false);
					}
					
					var dsParsList = record.get('pars');
					if(dsParsList!=null && dsParsList!= undefined){
						this.manageParsGrid.loadItems(dsParsList);
					}else{
						this.manageParsGrid.loadItems([]);
					}

					if(record && record.json){
					var dsCustomList = record.json.customs;
					if(dsCustomList!=null && dsCustomList!= undefined){
						this.customDataGrid.loadItems(dsCustomList);
					}else{
						this.customDataGrid.loadItems([]);
					}
					}

					
				}

				,test : function(button, event, service) {
					var values = this.getForm().getFieldValues();

					var requestParameters = {
						start : 0,
						limit : 25,
						dsTypeCd : values['dsTypeCd'],
						fileName : values['fileName'],
						query : values['query'],
						dataSource : values['dataSource'],
						wsAddress : values['wsAddress'],
						wsOperation : values['wsOperation'],
						script : values['script'],
						scriptLanguage : values['scriptLanguage'],
						jclassName : values['jclassName'],
						customData : values['customData'],
						trasfTypeCd : values['trasfTypeCd'],
						pivotColName : values['pivotColName'],
						pivotColValue : values['pivotColValue'],
						pivotRowName : values['pivotRowName'],
						pivotIsNumRows : values['pivotIsNumRows'],
						qbeSQLQuery : values['qbeSQLQuery'],
						qbeJSONQuery : values['qbeJSONQuery'],
						qbeDataSource: values['qbeDataSource'],
						qbeDatamarts: values['qbeDatamarts'],
						userIn: values['userIn'],
						dateIn: values['dateIn'],
						versNum: values['versNum'],
						versId: values['versId']
					};
					arrayPars = this.parsGrid.getParametersValues();
					if (arrayPars) {
						requestParameters.pars = Ext.util.JSON
								.encode(arrayPars);
					}

					customArray = this.customDataGrid.getDataArray();
					
					if (customArray) {
						requestParameters.customData = Ext.util.JSON.encode(customArray);
					}
					
					if (this.previewWindow === undefined) {
						this.previewWindow = new Sbi.tools.dataset.PreviewWindow({
							modal : true
							, width: this.getWidth() - 50
							, height: this.getHeight() - 50
						});
					}
					this.previewWindow.show();
					this.previewWindow.load(requestParameters);
				}

				,initConfigObject : function() {
					this.configurationObject.fields = [ 'id', 'name',
							'label', 'description', 'dsTypeCd',
							'catTypeVn', 'usedByNDocs', 'fileName',
							'query', 'dataSource', 'wsAddress',
							'wsOperation', 'script', 'scriptLanguage',
							'jclassName', 'customData', 'pars', 'trasfTypeCd',
							'pivotColName', 'pivotColValue',
							'pivotRowName', 'pivotIsNumRows', 'dsVersions',
							'qbeSQLQuery', 'qbeJSONQuery', 'qbeDataSource',
							'qbeDatamarts',	'userIn','dateIn','versNum','versId'];

					this.configurationObject.emptyRecToAdd = new Ext.data.Record(
							{	id : 0,
								name : '', label : '', description : '',
								dsTypeCd : '', catTypeVn : '', usedByNDocs : 0,
								fileName : '', query : '', dataSource : '',
								wsAddress : '', wsOperation : '', script : '',
								scriptLanguage : '', jclassName : '', customData: '', pars : [],
								trasfTypeCd : '', pivotColName : '', pivotColValue : '',
								pivotRowName : '', pivotIsNumRows : '', qbeSQLQuery: '',
								qbeJSONQuery: '', qbeDataSource: '', qbeDatamarts: '',
								dsVersions : [], userIn:'',dateIn:'',versNum:'',versId:''
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
						header : LN('sbi.generic.type'),
						width : 70,
						sortable : true,
						dataIndex : 'dsTypeCd'
					}, {
						header : LN('sbi.ds.numDocs'),
						width : 60,
						sortable : true,
						dataIndex : 'usedByNDocs'
					} ];

					this.configurationObject.panelTitle = LN('sbi.ds.panelTitle');
					this.configurationObject.listTitle = LN('sbi.ds.listTitle');
					
					var tbButtonsArray = new Array();
					this.tbProfAttrsButton = new Ext.Toolbar.Button({
		 	            text: LN('sbi.ds.pars'),
		 	            iconCls: 'icon-profattr',
		 	            handler: this.profileAttrs,
		 	            width: 30,
		 	            scope: this
		 	            });
					tbButtonsArray.push(this.tbProfAttrsButton);
					
					this.tbInfoButton = new Ext.Toolbar.Button({
		 	            text: LN('sbi.ds.help'),
		 	            iconCls: 'icon-info',
		 	            handler: this.info,
		 	            width: 30,
		 	            scope: this
		 	            });
					tbButtonsArray.push(this.tbInfoButton);
					
					this.tbTransfInfoButton = new Ext.Toolbar.Button({
		 	            text: LN('sbi.ds.help'),
		 	            iconCls: 'icon-info',
		 	            handler: this.transfInfo,
		 	            width: 30,
		 	            scope: this
		 	            });
					tbButtonsArray.push(this.tbTransfInfoButton);
					this.configurationObject.tbButtonsArray = tbButtonsArray;

					this.initTabItems();
				}
				
				,initTabItems : function() {				
					this.initDetailTab();
					this.initTypeTab();
					this.initTrasfTab();
					this.initTestTab();				
				}
				
				,initDetailTab : function() {
					this.profileAttributesStore = new Ext.data.SimpleStore({
						fields : [ 'profAttrs' ],
						data : config.attrs,
						autoLoad : false
					});
					
					// Store of the combobox
					this.catTypesStore = new Ext.data.SimpleStore({
						fields : [ 'catTypeVn' ],
						data : config.catTypeVn,
						autoLoad : false
					});

					// START list of detail fields
					this.detailFieldId = new Ext.form.TextField({
						name : 'id',
						hidden : true
					});
					
					this.detailFieldUserIn = new Ext.form.TextField({
							name : 'userIn',
							hidden : true
						});
					
					this.detailFieldDateIn = new Ext.form.TextField({
							name : 'dateIn',
							hidden : true
					});
					
					this.detailFieldVersNum = new Ext.form.TextField({
							name : 'versNum',
							hidden : true
					});
					
					this.detailFieldVersId = new Ext.form.TextField({
							name : 'versId',
							hidden : true
					});

					var detailFieldName = {
						maxLength : 50,	minLength : 1, width : 250,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.generic.name'),
						allowBlank : false,	validationEvent : true,
						name : 'name'
					};

					var detailFieldLabel = {
						maxLength : 50, minLength : 1, width : 250,
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

					var detailFieldCatType = {
						name : 'catTypeVn',
						store : this.catTypesStore,
						width : 150,
						fieldLabel : LN('sbi.ds.catType'),
						displayField : 'catTypeVn', 
						valueField : 'catTypeVn', 
						typeAhead : true, forceSelection : true,
						mode : 'local',
						triggerAction : 'all',
						selectOnFocus : true, editable : false,
						allowBlank : true, validationEvent : true,
						xtype : 'combo'
					};
					// END list of detail fields

					var c = {};
					this.manageDsVersionsGrid = new Sbi.tools.ManageDatasetVersions(c);
					this.manageDsVersionsGrid.addListener('verionrestored', function(version) {
						
						var values = this.getForm().getFieldValues();
						var newDsVersion = new Ext.data.Record(
								{	dsId: values['id'],
									dateIn : values['dateIn'],
									userIn : values['userIn'],
									versId : values['versId'],
									type : values['dsTypeCd'], 
									versNum : values['versNum']
								});
						this.manageDsVersionsGrid.getStore().addSorted(newDsVersion);
						this.manageDsVersionsGrid.getStore().commitChanges();	
						var rec = this.buildNewRecordDsVersion(version);
						this.activateDsTypeForm(null, rec, null);
						this.activateTransfForm(null, rec, null);
						this.activateDsTestTab(this.datasetTestTab);
						this.getForm().loadRecord(rec);
						this.updateMainStore(values['id']);
					}, this);
					
					this.manageDsVersionsGrid.addListener('verionsdeleted', function() {
						var values = this.getForm().getFieldValues();
						this.updateDsVersionsOfMainStore(values['id']);
					}, this);

					this.manageDsVersionsPanel = new Ext.Panel(
							{	id : 'man-vers',
								title : LN('sbi.ds.versionPanel'),
								layout : 'fit',
								autoScroll : true,
								style : {
									"margin-top" : "20px"
								},
								border : true,
								items : [ this.manageDsVersionsGrid ],
								scope : this
							});

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
											detailFieldDescr, detailFieldCatType, this.manageDsVersionsPanel ,
											this.detailFieldUserIn,this.detailFieldDateIn,this.detailFieldVersNum,this.detailFieldVersId,this.detailFieldId
											]
								}
							});
				}
				
				,initTypeTab : function() {
					// DataSource Store types combobox
					this.dsTypesStore = new Ext.data.SimpleStore({
						fields : [ 'dsTypeCd' ],
						data : config.dsTypes,
						autoLoad : false
					});

					this.dataSourceStore = new Ext.data.SimpleStore({
						fields : [ 'dataSource','name' ],
						data : config.dataSourceLabels,
						autoLoad : false
					});

					this.scriptLanguagesStore = new Ext.data.SimpleStore({
						fields : [ 'scriptLanguage' ,'name'],
						data : config.scriptTypes,
						autoLoad : false
					});

					// START list of Advanced fields
					var detailDsType = new Ext.form.ComboBox({
						name : 'dsTypeCd',
						store : this.dsTypesStore,
						width : 160,
						fieldLabel : LN('sbi.ds.dsTypeCd'),
						displayField : 'dsTypeCd', // what the user sees in
						// the popup
						valueField : 'dsTypeCd', // what is passed to the
						// 'change' event
						typeAhead : true, forceSelection : true,
						mode : 'local',
						triggerAction : 'all',
						selectOnFocus : true, editable : false,
						allowBlank : false, validationEvent : true
					});
					detailDsType.addListener('select',this.activateDsTypeForm, this);
					
					this.fileNamesStore = new Ext.data.SimpleStore({
						fields : ['fileName'],
						data : config.fileTypes,
						autoLoad : false
					});
					
					this.detailFileName = new Ext.form.ComboBox({
						name : 'fileName',
						store : this.fileNamesStore,
						width : 350,
						fieldLabel : LN('sbi.ds.fileName'),
						displayField : 'fileName', 
						valueField : 'fileName', 
						typeAhead : true, forceSelection : true,
						mode : 'local',
						triggerAction : 'all',
						selectOnFocus : true, editable : false,
						allowBlank : false, validationEvent : true
					});

					this.detailDataSource = new Ext.form.ComboBox({
						name : 'dataSource',
						store : this.dataSourceStore,
						width : 180,
						fieldLabel : LN('sbi.ds.dataSource'),
						displayField : 'name', // what the user
						// sees in the
						// popup
						valueField : 'dataSource', // what is passed to the
						// 'change' event
						typeAhead : true, forceSelection : true,
						mode : 'local',
						triggerAction : 'all',
						selectOnFocus : true, editable : false,
						allowBlank : false, validationEvent : true
					});

					this.detailQbeDataSource = new Ext.form.ComboBox({
						name : 'qbeDataSource',
						store : this.dataSourceStore,
						width : 350,
						fieldLabel : LN('sbi.ds.dataSource'),
						displayField : 'dataSource', // what the user
						// sees in the popup
						valueField : 'dataSource', // what is passed to the
						// 'change' event
						typeAhead : true, forceSelection : true,
						mode : 'local',
						triggerAction : 'all',
						selectOnFocus : true, editable : false,
						allowBlank : false, validationEvent : true
					});

					this.detailQuery = new Ext.form.TextArea({
						maxLength : 30000,
						xtype : 'textarea',
						width : 350,
						height : 195,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.query'),
						validationEvent : true,
						allowBlank : false,
						name : 'query'
					});

					this.detailWsAddress = new Ext.form.TextField({
						maxLength : 250, minLength : 1,
						width : 350,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.wsAddress'),
						allowBlank : false, validationEvent : true,
						name : 'wsAddress'
					});

					this.detailWsOperation = new Ext.form.TextField({
						maxLength : 50, minLength : 1, width : 350,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.wsOperation'),
						allowBlank : true, validationEvent : true,
						name : 'wsOperation'
					});

					this.detailScript = new Ext.form.TextArea({
						maxLength : 30000,
						xtype : 'textarea',
						width : this.textAreaWidth,
						height : 195,
						width : 350,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.script'),
						allowBlank : false, validationEvent : true,
						name : 'script'
					});

					var openQbeWizardButton = new Ext.Button(
							{
								text : LN('sbi.ds.openQbeQizard'),
								handler : this.jsonTriggerFieldHandler,
								scope : this,
								icon : 'null' // workaround: without this,
							// the button shows other
							// icons (such as Bold, Italic, ...) in
							// background
							});
					
					this.qbeSQLQuery = new Ext.form.Hidden({
						name : 'qbeSQLQuery'
					});
					
					this.qbeJSONQuery = new Ext.form.TriggerField({
						name : 'qbeJSONQuery'
						, valueField : 'qbeJSONQuery'
						, fieldLabel : 'Qbe Query'
						, triggerClass: 'x-form-search-trigger'
						, editable: false
					});
					this.qbeJSONQuery.on("render", function(field) {
						field.trigger.on("click", function(e) {
							this.jsonTriggerFieldHandler(); 
						}, this);
					}, this);
					//this.qbeJSONQuery.on('click', this.jsonTriggerFieldHandler, this);
					
					var datamartsStore = new Ext.data.Store({
				        proxy: new Ext.data.ScriptTagProxy({
					        url: this.configurationObject.getDatamartsService,
					        method: 'GET'
					    }),
					    reader: new Ext.data.JsonReader({id: 'datamart'}, [
			                 {name:'datamart'}
			     	    ])
					});
					
					this.qbeDatamarts = new Ext.form.ComboBox({
						name : 'qbeDatamarts'
			    	   	, fieldLabel: LN('sbi.tools.managedatasets.datamartcombo.label')
			    	   	, forceSelection: true
			    	   	, editable: false
			    	   	, store: datamartsStore
			    	   	, displayField: 'datamart'
			    	    , valueField: 'datamart'
			    	    , typeAhead: true
			    	    , triggerAction: 'all'
			    	    , selectOnFocus: true
			    	    , allowBlank: false
			    	});

					this.detailScriptLanguage = new Ext.form.ComboBox({
						name : 'scriptLanguage',
						store : this.scriptLanguagesStore,
						width : 160,
						fieldLabel : LN('sbi.ds.scriptLanguage'),
						displayField : 'name', // what the user
						// sees in the
						// popup
						valueField : 'scriptLanguage', // what is passed to
						// the
						// 'change' event
						typeAhead : true,
						forceSelection : true,
						mode : 'local',
						triggerAction : 'all',
						selectOnFocus : true,
						editable : false,
						allowBlank : false,
						validationEvent : true,
						xtype : 'combo'
					});

					this.detailJclassName = new Ext.form.TextField({
						maxLength : 100,
						minLength : 1,
						width : 350,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.jclassName'),
						allowBlank : false,
						validationEvent : true,
						name : 'jclassName'
					});
					
//					this.customData = new Ext.form.TextField({
//						maxLength : 100,
//						minLength : 1,
//						width : 350,
//						regexText : LN('sbi.roles.alfanumericString'),
//						fieldLabel : LN('sbi.ds.customData'),
//						allowBlank : false,
//						validationEvent : true,
//						name : 'customData'
//					});

					
					function Config () {
					    this.pars = 'ciao';
					}

					var c = new Config();
					
					
					this.customDataGrid = new Sbi.tools.dataset.CustomDataGrid(c);
					

					this.dsTypeDetail = new Ext.form.FieldSet(
							{
								labelWidth : 100,
								defaultType : 'textfield',
								//autoHeight : true,
								autoScroll : true,
								border : false,
								style : {
									"margin-left" : "5px",
									"margin-bottom" : "0px",
									"margin-top" : "3px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "0px"
											: "-3px")
											: "0px"
								},
								items : [ detailDsType ]
							});

					this.queryDetail = new Ext.form.FieldSet(
							{
								labelWidth : 100,
								defaults : {
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "0px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailDataSource,
										this.detailQuery ]
							});

					this.qbeQueryDetail = new Ext.form.FieldSet(
							{
								labelWidth : 120,
								defaults : {
									width : 280,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "0px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [
								        this.detailQbeDataSource,
								        this.qbeDatamarts,
										this.qbeSQLQuery,
										this.qbeJSONQuery
										]
							});

					this.jClassDetail = new Ext.form.FieldSet(
							{
								labelWidth : 100,
								defaults : {
									//width : 280,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "0px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailJclassName ]
							});
					
					
					this.customDataDetail = new Ext.form.FieldSet(
							{
								labelWidth : 100,
								defaults : {
									//width : 280,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "0px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailJclassName, this.customDataGrid ]
							});

					this.fileDetail = new Ext.form.FieldSet(
							{
								labelWidth : 80,
								defaults : {
									//width : 280,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "0px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailFileName ]
							});

					this.WSDetail = new Ext.form.FieldSet(
							{
								labelWidth : 100,
								defaults : {
									//width : 280,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "0px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailWsAddress,
										this.detailWsOperation ]
							});

					this.scriptDetail = new Ext.form.FieldSet(
							{
								labelWidth : 100,
								defaults : {
								//	width : 280,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "0px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailScriptLanguage,
										this.detailScript ]
							});

					var c = {};
					this.manageParsGrid = new Sbi.tools.ManageDatasetParameters(
							c);

					this.manageParsPanel = new Ext.Panel(
							{
								id : 'man-pars',
								layout : 'fit',
								autoScroll : false
								 , bodyStyle: Ext.isIE ? 'padding:0 0 3px 3px;' : 'padding:3px 3px;',
								border : true,
								height: 110,
								items : [ this.manageParsGrid ],
								scope : this
							});

					this.typeTab = new Ext.Panel(
							{
								title : LN('sbi.generic.type'),
								itemId : 'advanced',
								width : 350,
								items : {
									id : 'advanced-detail',
									itemId : 'advanced-detail',
									// columnWidth: 0.4,
									xtype : 'fieldset',
									scope : this,
									labelWidth : 80,
									defaultType : 'textfield',
									autoHeight : true,
									autoScroll : true,
									border : false,
									style : {
										"margin-left" : "5px",
										"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-5px"
												: "-7px")
												: "0"
									},
									items : [ this.dsTypeDetail,
											this.jClassDetail,
											this.customDataDetail,
											this.scriptDetail,
											this.queryDetail,
											this.WSDetail, this.fileDetail,
											this.qbeQueryDetail,
											this.manageParsPanel ]
								}
							});
				}
				
				,initTrasfTab : function() {
					this.transfTypesStore = new Ext.data.SimpleStore({
						fields : [ 'trasfTypeCd' ],
						data : config.trasfTypes,
						autoLoad : false
					});

					var detailTransfType = new Ext.form.ComboBox({
						name : 'trasfTypeCd',
						store : this.transfTypesStore,
						width : 120,
						fieldLabel : LN('sbi.ds.trasfTypeCd'),
						displayField : 'trasfTypeCd', // what the user
						// sees in the
						// popup
						valueField : 'trasfTypeCd', // what is passed to the
						// 'change' event
						typeAhead : true,
						forceSelection : true,
						mode : 'local',
						triggerAction : 'all',
						selectOnFocus : true,
						editable : false,
						allowBlank : true,
						validationEvent : true,
						xtype : 'combo'
					});
					detailTransfType.addListener('select',
							this.activateTransfForm, this);

					this.trasfTypeDetail = new Ext.form.FieldSet(
							{
								labelWidth : 120,
								defaults : {
									width : 260,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								bodyStyle : Ext.isIE ? 'padding:0 0 5px 15px;'
										: 'padding:10px 15px;',
								border : true,
								style : {
									"margin-left" : "10px",
									"margin-top" : "10px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-10px"
											: "-13px")
											: "10px"
								},
								items : [ detailTransfType ]
							});

					var detailPivotCName = {
						maxLength : 40,
						minLength : 1,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.pivotColName'),
						allowBlank : true,
						validationEvent : true,
						name : 'pivotColName'
					};

					var detailPivotCValue = {
						maxLength : 40,
						minLength : 1,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.pivotColValue'),
						allowBlank : true,
						validationEvent : true,
						name : 'pivotColValue'
					};

					var detailPivotRName = {
						maxLength : 40,
						minLength : 1,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.pivotRowName'),
						allowBlank : true,
						validationEvent : true,
						name : 'pivotRowName'
					};

					var detailIsNumRow = new Ext.form.Checkbox({
						xtype : 'checkbox',
						itemId : 'pivotIsNumRows',
						name : 'pivotIsNumRows',
						fieldLabel : LN('sbi.ds.pivotIsNumRows')
					});

					this.trasfDetail = new Ext.form.FieldSet(
							{
								labelWidth : 150,
								defaults : {
									width : 260,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								bodyStyle : Ext.isIE ? 'padding:0 0 5px 15px;'
										: 'padding:10px 15px;',
								border : true,
								style : {
									"margin-left" : "10px",
									"margin-top" : "10px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-10px"
											: "-13px")
											: "10px"
								},
								items : [ detailPivotCName,
										detailPivotCValue,
										detailPivotRName, detailIsNumRow ]
							});

					this.transfTab = new Ext.Panel(
							{
								title : LN('sbi.ds.transfType'),
								itemId : 'transf',
								width : 350,
								items : {
									id : 'transf-detail',
									itemId : 'transf-detail',
									xtype : 'fieldset',
									scope : this,
									labelWidth : 90,
									defaultType : 'textfield',
									autoHeight : true,
									autoScroll : true,
									bodyStyle : Ext.isIE ? 'padding:0 0 5px 15px;'
											: 'padding:0px 0px;',
									border : false,
									style : {
										"margin-left" : "10px",
										"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-10px"
												: "-13px")
												: "0"
									},
									items : [ this.trasfTypeDetail,
											this.trasfDetail ]
								}
							});
				}
				
				,initTestTab : function() {
					this.tbTestDSButton = new Ext.Toolbar.Button({
						text : LN('sbi.ds.test'),
						width : 30,
						handler : this.test,
						iconCls : 'icon-execute',
						scope : this
					});

					this.tbTestToolbar = new Ext.Toolbar({
						buttonAlign : 'right',
						height : 25,
						scope : this,
						items : [ this.tbTestDSButton ]
					});

					var c = {tbar: this.tbTestToolbar};
					this.parsGrid = new Sbi.tools.ParametersFillGrid(c);

					this.datasetTestTab = new Ext.Panel({
						title : LN('sbi.ds.test'),
						id : 'test-pars',
						layout : 'fit',
						autoScroll : true,
						bodyStyle : Ext.isIE ? 'padding:0 0 10px 10px;'
								: 'padding:10px 10px;',
						border : true,
						items : [ this.parsGrid],
						scope : this
					});
					this.datasetTestTab.addListener('activate',
							this.activateDsTestTab, this);

					this.configurationObject.tabItems = [ this.detailTab,
							this.typeTab, this.transfTab,
							this.datasetTestTab ];
				}

				// OVERRIDING METHOD
				,
				addNewItem : function() {
					this.newRecord = new Ext.data.Record(
							{	id : 0,
								name : '', label : '', description : '',
								dsTypeCd : '', catTypeVn : '', usedByNDocs : 0,
								fileName : '', query : '', dataSource : '',
								wsAddress : '', wsOperation : '', script : '',
								scriptLanguage : '', jclassName : '', customData : '', pars : [],
								trasfTypeCd : '', pivotColName : '', pivotColValue : '',
								pivotRowName : '', pivotIsNumRows : '', qbeSQLQuery: '',
								qbeJSONQuery: '', qbeDataSource: '', qbeDatamarts: '',
								dsVersions : [],
								userIn: '',
								dateIn: '',
								versNum: 2,
								versId: 0
							});
					this.getForm().loadRecord(this.newRecord);
					this.manageParsGrid.loadItems([]);
					this.manageDsVersionsGrid.loadItems([]);

					this.tabs.items.each(function(item) {
						item.doLayout();
					});
					this.trasfDetail.setVisible(false);
					if (this.newRecord != null
							&& this.newRecord != undefined) {
						this.mainElementsStore.add(this.newRecord);
						this.rowselModel.selectLastRow(true);
					}				
					this.tabs.setActiveTab(0);					
				}

				,cloneItem: function() {	
					var values = this.getForm().getFieldValues();
					var params = this.buildParamsToSendToServer(values);
					var arrayPars = this.manageParsGrid.getParsArray();
					
					this.newRecord = this.buildNewRecordToSave(values);		
					this.newRecord.set('pars',arrayPars);
					this.getForm().loadRecord(this.newRecord);
					
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
				,buildNewRecordDsVersion: function(values){
					var actualValues = this.getForm().getFieldValues();
					var newRec = new Ext.data.Record({
						id: actualValues['id'],
						name : actualValues['name'],
						label : actualValues['label'],
						usedByNDocs: actualValues['usedByNDocs'],
						dsVersions: [],
						pars: values['pars'],
						description : actualValues['description'],
						dsTypeCd : values['dsTypeCd'],
						catTypeVn : values['catTypeVn'],
						usedByNDocs : values['usedByNDocs'],
						fileName : values['fileName'],
						query : values['query'],
						dataSource : values['dataSource'],
						wsAddress : values['wsAddress'],
						wsOperation : values['wsOperation'],
						script : values['script'],
						scriptLanguage : values['scriptLanguage'],
						jclassName : values['jclassName'],
						customData : values['customData'],
						trasfTypeCd : values['trasfTypeCd'],
						pivotColName : values['pivotColName'],
						pivotColValue : values['pivotColValue'],
						pivotRowName : values['pivotRowName'],
						pivotIsNumRows : values['pivotIsNumRows'],
						qbeSQLQuery : values['qbeSQLQuery'],
						qbeJSONQuery : values['qbeJSONQuery'],
						qbeDataSource: values['qbeDataSource'],
						qbeDatamarts: values['qbeDatamarts'],
						userIn: values['userIn'],
						dateIn: values['dateIn'],
						versNum: values['versNum'],
						versId: values['versId']
					});
					return newRec;
				}
				
				,buildNewRecordToSave: function(values){
					var newRec = new Ext.data.Record({
						id: 0,
						name : values['name'],
						label : '...',
						usedByNDocs: 0,
						dsVersions: [],
						description : values['description'],
						dsTypeCd : values['dsTypeCd'],
						catTypeVn : values['catTypeVn'],
						usedByNDocs : values['usedByNDocs'],
						fileName : values['fileName'],
						query : values['query'],
						dataSource : values['dataSource'],
						wsAddress : values['wsAddress'],
						wsOperation : values['wsOperation'],
						script : values['script'],
						scriptLanguage : values['scriptLanguage'],
						customData : values['customData'],
						jclassName : values['jclassName'],
						trasfTypeCd : values['trasfTypeCd'],
						pivotColName : values['pivotColName'],
						pivotColValue : values['pivotColValue'],
						pivotRowName : values['pivotRowName'],
						pivotIsNumRows : values['pivotIsNumRows'],
						qbeSQLQuery : values['qbeSQLQuery'],
						qbeJSONQuery : values['qbeJSONQuery'],
						qbeDataSource: values['qbeDataSource'],
						qbeDatamarts: values['qbeDatamarts'],
						userIn: values['userIn'],
						dateIn: values['dateIn'],
						versNum: values['versNum'],
						versId: values['versId']
					});
					return newRec;
				}	
				
				,buildParamsToSendToServer: function(values){
					var params = {
							name : values['name'],
							label : values['label'],
							description : values['description'],
							dsTypeCd : values['dsTypeCd'],
							catTypeVn : values['catTypeVn'],
							usedByNDocs : values['usedByNDocs'],
							fileName : values['fileName'],
							query : values['query'],
							dataSource : values['dataSource'],
							wsAddress : values['wsAddress'],
							wsOperation : values['wsOperation'],
							script : values['script'],
							scriptLanguage : values['scriptLanguage'],
							jclassName : values['jclassName'],
							customData : values['customData'],
							trasfTypeCd : values['trasfTypeCd'],
							pivotColName : values['pivotColName'],
							pivotColValue : values['pivotColValue'],
							pivotRowName : values['pivotRowName'],
							pivotIsNumRows : values['pivotIsNumRows'],
							qbeSQLQuery : values['qbeSQLQuery'],
							qbeJSONQuery : values['qbeJSONQuery'],
							qbeDataSource: values['qbeDataSource'],
							qbeDatamarts: values['qbeDatamarts'],
							userIn: values['userIn'],
							dateIn: values['dateIn'],
							versNum: values['versNum'],
							versId: values['versId']
						};
					return params;
				}
				
				,updateNewRecord: function(record, values, arrayPars, customArray){
					record.set('label',values['label']);
					record.set('name',values['name']);
					record.set('description',values['description']);
					record.set('usedByNDocs',0);
					record.set('dsTypeCd',values['dsTypeCd']);
					record.set('catTypeVn',values['catTypeVn']);
					record.set('fileName',values['fileName']);
					record.set('query',values['query']);
					record.set('dataSource',values['dataSource']);
					record.set('wsAddress',values['wsAddress']);
					record.set('wsOperation',values['wsOperation']);
					record.set('script',values['script']);
					record.set('scriptLanguage',values['scriptLanguage']);
					record.set('jclassName',values['jclassName']);
					record.set('customData',values['customData']);					
					record.set('trasfTypeCd',values['trasfTypeCd']);
					record.set('pivotColName',values['pivotColName']);
					record.set('pivotColValue',values['pivotColValue']);
					record.set('pivotRowName',values['pivotRowName']);
					record.set('pivotIsNumRows',values['pivotIsNumRows']);
					record.set('qbeSQLQuery',values['qbeSQLQuery']);
					record.set('qbeJSONQuery',values['qbeJSONQuery']);
					record.set('qbeDataSource',values['qbeDataSource']);
					record.set('qbeDatamarts',values['qbeDatamarts']);
					record.set('userIn',values['userIn']);
					record.set('dateIn',values['dateIn']);
					record.set('versNum',values['versNum']);
					record.set('versId',values['versId']);
					
					if (arrayPars) {
						record.set('pars',arrayPars);
					}
					
					if (customArray) {
						record.set('customData',customArray);
					}

					
				}
				
				, updateMainStore: function(idRec){
					var values = this.getForm().getFieldValues();
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
				
				// OVERRIDING save method
				,
				save : function() {
					var values = this.getForm().getFieldValues();
					var idRec = values['id'];
					var newRec;
					var newDsVersion;
					var isNewRec = false;
					var params = this.buildParamsToSendToServer(values);
					var arrayPars = this.manageParsGrid.getParsArray();
					var customArray = this.customDataGrid.getDataArray();
					
					if (idRec == 0 || idRec == null || idRec === '') {
						this.updateNewRecord(this.newRecord,values,arrayPars, customArray);
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
						this.updateNewRecord(record,values,arrayPars, customArray);
						
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
					if (customArray) {
						params.customData = Ext.util.JSON.encode(customArray);
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
														var values = this.getForm().getFieldValues();
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

				,
				manageQbeQuery : function(theQbeDatasetBuilder, qbeQuery) {
					var jsonQuery = qbeQuery.data.jsonQuery;
					this.qbeJSONQuery.setValue(Ext.util.JSON.encode(jsonQuery));
					var sqlQuery = qbeQuery.data.sqlQuery.sql;
					this.qbeSQLQuery.setValue(sqlQuery);
					var datamarts = qbeQuery.data.datamarts;
					this.qbeDatamarts.setValue(datamarts);
					var parameters = qbeQuery.data.parameters;
					this.manageParsGrid.loadItems([]);
					this.manageParsGrid.loadItems(parameters);
				}
				
				,
				jsonTriggerFieldHandler : function() {
					var values = this.getForm().getFieldValues();
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
				
				, initQbeDataSetBuilder: function(datasourceLabel, datamart, datasetId) {
					if (this.qbeDataSetBuilder === null) {
						this.initNewQbeDataSetBuilder(datasourceLabel, datamart, datasetId);
						return;
					}
					if (this.mustRefreshQbeView(datasourceLabel, datamart, datasetId)) {
						this.qbeDataSetBuilder.destroy();
						this.initNewQbeDataSetBuilder(datasourceLabel, datamart, datasetId);
						return;
					}
				}
				
				, initNewQbeDataSetBuilder: function(datasourceLabel, datamart, datasetId) {
					this.qbeDataSetBuilder = new Sbi.tools.dataset.QbeDatasetBuilder({
						datasourceLabel : datasourceLabel,
						datamart : datamart,
						datasetId : datasetId,
						jsonQuery : this.qbeJSONQuery.getValue(),
						qbeParameters : this.manageParsGrid.getParsArray(),
						modal : true,
						width : this.getWidth() - 50,
						height : this.getHeight() - 50,
						listeners : {
							hide : 
								{
									fn : function(theQbeDatasetBuilder) {
											theQbeDatasetBuilder.getQbeQuery(); // asynchronous
									}
									, scope: this
								},
							gotqbequery : {
									fn: this.manageQbeQuery
									, scope: this
							}
						}
					});
				}
				
				, mustRefreshQbeView: function(datasourceLabel, datamart, datasetId) {
					if (datasourceLabel == this.qbeDataSetBuilder.getDatasourceLabel()
							&& datamart == this.qbeDataSetBuilder.getDatamart()
							&& datasetId == this.qbeDataSetBuilder.getDatasetId()) {
						return false;
					}
					return true;
				}
				
				//METHOD TO BE OVERRIDDEN IN EXTENDED ELEMENT!!!!!
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
				
				,transfInfo: function() {		
					var win_info_4;
					if(!win_info_4){
						win_info_4= new Ext.Window({
							id:'win_info_4',
							autoLoad: {url: Sbi.config.contextName+'/themes/'+Sbi.config.currTheme+'/html/dsTrasformationHelp.html'},             				
							layout:'fit',
							width:760,
							height:420,
							autoScroll: true,
							closeAction:'close',
							buttonAlign : 'left',
							plain: true,
							title: LN('sbi.ds.help')
						});
					};
					win_info_4.show();
			    }
				
				,profileAttrs: function() {		
					var win_info_3;
					if(!win_info_3){
						win_info_3= new Ext.Window({
							id:'win_info_3',          				
							layout:'fit',
							width:220,
							height:350,
							closeAction:'close',
							buttonAlign : 'left',
							autoScroll: true,
							plain: true,
							items: {  
						        xtype: 'grid',
						        border: false,
						        columns: [{header: LN('sbi.ds.pars'),width : 170}],                
						        store: this.profileAttributesStore
						    }
						});
					};
					win_info_3.show();
			    }

});
