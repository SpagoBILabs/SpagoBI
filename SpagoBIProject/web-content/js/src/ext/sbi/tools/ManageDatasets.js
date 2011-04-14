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
	}, this);
};

Ext.extend(
			Sbi.tools.ManageDatasets,
			Sbi.widgets.ListDetailForm,
			{

				configurationObject : null,
				gridForm : null,
				mainElementsStore : null,
				trasfDetail : null,
				jClassDetail : null,
				scriptDetail : null,
				queryDetail : null,
				WSDetail : null,
				fileDetail : null,
				parsGrid : null,
				datasetTestTab : null,
				//datasetTestGridPanel : null,
				manageParsGrid : null,
				manageDsVersionsGrid : null

				,
				activateTransfForm : function(combo, record, index) {
					var transfSelected = record.get('trasfTypeCd');
					if (transfSelected != null
							&& transfSelected == 'PIVOT_TRANSFOMER') {
						this.trasfDetail.setVisible(true);
					} else {
						this.trasfDetail.setVisible(false);
					}
				}

				,
				activateDsTestTab : function(panel) {
					if (panel) {
						var record = this.rowselModel.getSelected();
						if (record) {
							var dsParsList = this.manageParsGrid
									.getParsArray();
							this.parsGrid.fillParameters(dsParsList);
						}
					}
				}

				,
				test : function(button, event, service) {
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
						trasfTypeCd : values['trasfTypeCd'],
						pivotColName : values['pivotColName'],
						pivotColValue : values['pivotColValue'],
						pivotRowName : values['pivotRowName'],
						pivotIsNumRows : values['pivotIsNumRows'],
						qbeSQLQuery : values['qbeSQLQuery'],
						qbeJSONQuery : values['qbeJSONQuery'],
						qbeDataSource: values['qbeDataSource'],
						qbeDatamarts: values['qbeDatamarts']
					};
					arrayPars = this.parsGrid.getParametersValues();
					if (arrayPars) {
						requestParameters.pars = Ext.util.JSON
								.encode(arrayPars);
					}
					//this.datasetTestGridPanel.execTest(requestParameters);
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

				,
				activateDsVersionsGrid : function(combo, record, index) {
					var dsVersionsList = record.get('dsVersions');
					this.manageDsVersionsGrid.loadItems(dsVersionsList);
				}

				,
				activateDsTypeForm : function(combo, record, index) {

					var dsTypeSelected = record.get('dsTypeCd');
					if (dsTypeSelected != null && dsTypeSelected == 'File') {
						this.fileDetail.setVisible(true);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null
							&& dsTypeSelected == 'Query') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(true);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null
							&& dsTypeSelected == 'Java Class') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(true);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null
							&& dsTypeSelected == 'Web Service') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(true);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null
							&& dsTypeSelected == 'Script') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(true);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(false);
					} else if (dsTypeSelected != null
							&& dsTypeSelected == 'Qbe') {
						this.fileDetail.setVisible(false);
						this.queryDetail.setVisible(false);
						this.jClassDetail.setVisible(false);
						this.scriptDetail.setVisible(false);
						this.WSDetail.setVisible(false);
						this.qbeQueryDetail.setVisible(true);
					}
					var dsParsList = record.get('pars');
					this.manageParsGrid.loadItems(dsParsList);
				}

				,
				initConfigObject : function() {
					this.configurationObject.fields = [ 'id', 'name',
							'label', 'description', 'dsTypeCd',
							'catTypeCd', 'usedByNDocs', 'fileName',
							'query', 'dataSource', 'wsAddress',
							'wsOperation', 'script', 'scriptLanguage',
							'jclassName', 'pars', 'trasfTypeCd',
							'pivotColName', 'pivotColValue',
							'pivotRowName', 'pivotIsNumRows', 'dsVersions',
							'qbeSQLQuery', 'qbeJSONQuery', 'qbeDataSource',
							'qbeDatamarts'];

					this.configurationObject.emptyRecToAdd = new Ext.data.Record(
							{
								id : 0,
								name : '',
								label : '',
								description : '',
								dsTypeCd : '',
								catTypeCd : '',
								usedByNDocs : 0,
								fileName : '',
								query : '',
								dataSource : '',
								wsAddress : '',
								wsOperation : '',
								script : '',
								scriptLanguage : '',
								jclassName : '',
								pars : [],
								trasfTypeCd : '',
								pivotColName : '',
								pivotColValue : '',
								pivotRowName : '',
								pivotIsNumRows : '',
								qbeSQLQuery: '',
								qbeJSONQuery: '',
								qbeDataSource: '',
								qbeDatamarts: '',
								dsVersions : []
							});

					this.configurationObject.gridColItems = [ {
						id : 'label',
						header : LN('sbi.generic.label'),
						width : 120,
						sortable : true,
						locked : false,
						dataIndex : 'label'
					}, {
						header : LN('sbi.generic.name'),
						width : 120,
						sortable : true,
						dataIndex : 'name'
					}, {
						header : LN('sbi.generic.type'),
						width : 55,
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

					this.initTabItems();
				}

				// OVERRIDING METHOD
				,
				addNewItem : function() {
					var emptyRecToAdd = this.emptyRecord;
					this.getForm().loadRecord(emptyRecToAdd);
					this.manageParsGrid.loadItems([]);
					this.manageDsVersionsGrid.loadItems([]);

					this.tabs.items.each(function(item) {
						item.doLayout();
					});
					this.tabs.setActiveTab(0);
				}

				,
				initTabItems : function() {

					// Store of the combobox
					this.catTypesStore = new Ext.data.SimpleStore({
						fields : [ 'catTypeCd' ],
						data : config.catTypeCd,
						autoLoad : false
					});

					// START list of detail fields
					var detailFieldId = {
						name : 'id',
						hidden : true
					};

					var detailFieldName = {
						maxLength : 40,
						minLength : 1,
						width : 200,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.generic.name'),
						allowBlank : false,
						validationEvent : true,
						name : 'name'
					};

					var detailFieldLabel = {
						maxLength : 45,
						minLength : 1,
						width : 200,
						regexText : LN('sbi.roles.alfanumericString2'),
						fieldLabel : LN('sbi.generic.label'),
						allowBlank : false,
						validationEvent : true,
						name : 'label'
					};

					var detailFieldDescr = {
						maxLength : 400,
						xtype : 'textarea',
						width : 250,
						height : 80,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.generic.descr'),
						validationEvent : true,
						name : 'description'
					};

					var detailFieldCatType = {
						name : 'catTypeCd',
						store : this.catTypesStore,
						width : 120,
						fieldLabel : LN('sbi.ds.catType'),
						displayField : 'catTypeCd', // what the user sees in
						// the popup
						valueField : 'catTypeCd', // what is passed to the
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
					};
					// END list of detail fields

					var c = {};
					this.manageDsVersionsGrid = new Sbi.tools.ManageDatasetVersions(
							c);

					this.manageDsVersionsPanel = new Ext.Panel(
							{
								id : 'man-vers',
								title : LN('sbi.ds.versionPanel'),
								layout : 'form',
								autoScroll : false,
								style : {
									"margin-left" : "10px",
									"margin-top" : "10px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-10px"
											: "-13px")
											: "10px"
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
									autoHeight : true,
									autoScroll : true,
									bodyStyle : Ext.isIE ? 'padding:0 0 5px 15px;'
											: 'padding:10px 15px;',
									border : false,
									style : {
										"margin-left" : "10px",
										"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-10px"
												: "-13px")
												: "0"
									},
									items : [ detailFieldId,
											detailFieldLabel,
											detailFieldName,
											detailFieldDescr,
											detailFieldCatType,
											this.manageDsVersionsPanel ]
								}
							});

					// DataSource Store types combobox
					this.dsTypesStore = new Ext.data.SimpleStore({
						fields : [ 'dsTypeCd' ],
						data : config.dsTypes,
						autoLoad : false
					});

					this.dataSourceStore = new Ext.data.SimpleStore({
						fields : [ 'dataSource' ],
						data : config.dataSourceLabels,
						autoLoad : false
					});

					this.scriptLanguagesStore = new Ext.data.SimpleStore({
						fields : [ 'scriptLanguage' ],
						data : config.scriptTypes,
						autoLoad : false
					});

					// START list of Advanced fields
					var detailDsType = new Ext.form.ComboBox({
						name : 'dsTypeCd',
						store : this.dsTypesStore,
						width : 120,
						fieldLabel : LN('sbi.ds.dsTypeCd'),
						displayField : 'dsTypeCd', // what the user sees in
						// the popup
						valueField : 'dsTypeCd', // what is passed to the
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
					detailDsType.addListener('select',
							this.activateDsTypeForm, this);

					this.detailFileName = new Ext.form.TextField({
						maxLength : 40,
						minLength : 1,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.fileName'),
						allowBlank : true,
						validationEvent : true,
						name : 'fileName'
					});

					this.detailDataSource = new Ext.form.ComboBox({
						name : 'dataSource',
						store : this.dataSourceStore,
						width : 120,
						fieldLabel : LN('sbi.ds.dataSource'),
						displayField : 'dataSource', // what the user
						// sees in the
						// popup
						valueField : 'dataSource', // what is passed to the
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

					this.detailQbeDataSource = new Ext.form.ComboBox({
						name : 'qbeDataSource',
						store : this.dataSourceStore,
						width : 120,
						fieldLabel : LN('sbi.ds.dataSource'),
						displayField : 'dataSource', // what the user
						// sees in the popup
						valueField : 'dataSource', // what is passed to the
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

					this.detailQuery = new Ext.form.TextArea({
						maxLength : 5000,
						xtype : 'textarea',
						height : 180,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.query'),
						validationEvent : true,
						name : 'query'
					});

					this.detailWsAddress = new Ext.form.TextField({
						maxLength : 40,
						minLength : 1,
						width : 150,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.wsAddress'),
						allowBlank : true,
						validationEvent : true,
						name : 'wsAddress'
					});

					this.detailWsOperation = new Ext.form.TextField({
						maxLength : 40,
						minLength : 1,
						width : 150,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.wsOperation'),
						allowBlank : true,
						validationEvent : true,
						name : 'wsOperation'
					});

					this.detailScript = new Ext.form.TextArea({
						maxLength : 5000,
						xtype : 'textarea',
						width : this.textAreaWidth,
						height : 175,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.script'),
						allowBlank : true,
						validationEvent : true,
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
			    	});

					this.detailScriptLanguage = new Ext.form.ComboBox({
						name : 'scriptLanguage',
						store : this.scriptLanguagesStore,
						width : 120,
						fieldLabel : LN('sbi.ds.scriptLanguage'),
						displayField : 'scriptLanguage', // what the user
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
						allowBlank : true,
						validationEvent : true,
						xtype : 'combo'
					});

					this.detailJclassName = new Ext.form.TextField({
						maxLength : 40,
						minLength : 1,
						regexText : LN('sbi.roles.alfanumericString'),
						fieldLabel : LN('sbi.ds.jclassName'),
						allowBlank : true,
						validationEvent : true,
						name : 'jclassName'
					});

					this.dsTypeDetail = new Ext.form.FieldSet(
							{
								labelWidth : 90,
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "60px",
									"margin-top" : "3px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-60px"
											: "-63px")
											: "60px"
								},
								items : [ detailDsType ]
							});

					this.queryDetail = new Ext.form.FieldSet(
							{
								labelWidth : 80,
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
									"margin-top" : "3px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailDataSource,
										this.detailQuery ]
							});

					this.qbeQueryDetail = new Ext.form.FieldSet(
							{
								labelWidth : 90,
								defaults : {
									width : 210,
									border : true
								},
								defaultType : 'textfield',
								autoHeight : true,
								autoScroll : true,
								border : true,
								style : {
									"margin-left" : "3px",
									"margin-top" : "3px",
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
								labelWidth : 80,
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
									"margin-top" : "3px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailJclassName ]
							});

					this.fileDetail = new Ext.form.FieldSet(
							{
								labelWidth : 80,
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
									"margin-top" : "3px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailFileName ]
							});

					this.WSDetail = new Ext.form.FieldSet(
							{
								labelWidth : 80,
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
									"margin-top" : "3px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
											: "-5px")
											: "3px"
								},
								items : [ this.detailWsAddress,
										this.detailWsOperation ]
							});

					this.scriptDetail = new Ext.form.FieldSet(
							{
								labelWidth : 80,
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
									"margin-top" : "3px",
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
								layout : 'form',
								autoScroll : false
								// , bodyStyle: Ext.isIE ? 'padding:0 0 5px
								// 15px;' : 'padding:10px
								// 15px;'
								,
								style : {
									"margin-left" : "3px",
									"margin-top" : "3px",
									"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-70px"
											: "-73px")
											: "70px"
								},
								border : true,
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
											this.scriptDetail,
											this.queryDetail,
											this.WSDetail, this.fileDetail,
											this.qbeQueryDetail,
											this.manageParsPanel ]
								}
							});

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
								labelWidth : 90,
								defaults : {
									width : 210,
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
								labelWidth : 90,
								defaults : {
									width : 210,
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

					this.tbTestDSButton = new Ext.Toolbar.Button({
						text : LN('sbi.ds.test'),
						// iconCls: 'icon-save',
						width : 30,
						handler : this.test,
						iconCls : 'icon-filter',
						scope : this
					});

					this.tbTestToolbar = new Ext.Toolbar({
						buttonAlign : 'right',
						height : 25,
						scope : this,
						items : [ this.tbTestDSButton ]
					});

					var c = {
						tbar: this.tbTestToolbar
					};
					this.parsGrid = new Sbi.tools.ParametersFillGrid(c);
					
					/*
					this.datasetTestGridPanel = new Sbi.tools.DataSetTestGrid(
							c);

					this.datasetTestPanel = new Ext.Panel({
						id : 'test',
						autoScroll : true,
						tbar : this.tbTestToolbar,
						layout : 'fit',
						title : 'Test your Data Set',
						border : true,
						frame : true,
						width : 350,
						items : [ this.datasetTestGridPanel ],
						scope : this
					});
					*/

					this.datasetTestTab = new Ext.Panel({
						title : LN('sbi.ds.test'),
						id : 'test-pars',
						layout : 'vbox',
						autoScroll : false,
						bodyStyle : Ext.isIE ? 'padding:0 0 5px 15px;'
								: 'padding:10px 15px;',
						border : true,
						items : [ this.parsGrid/*, this.datasetTestPanel*/],
						scope : this
					});
					this.datasetTestTab.addListener('activate',
							this.activateDsTestTab, this);

					this.configurationObject.tabItems = [ this.detailTab,
							this.typeTab, this.transfTab,
							this.datasetTestTab ];
				}

				// OVERRIDING save method
				,
				save : function() {
					var values = this.getForm().getFieldValues();
					var idRec = values['id'];
					var newRec;

					if (idRec == 0 || idRec == null || idRec === '') {
						newRec = new Ext.data.Record({
							name : values['name'],
							label : values['label'],
							description : values['description'],
							dsTypeCd : values['dsTypeCd'],
							catTypeCd : values['catTypeCd'],
							usedByNDocs : values['usedByNDocs'],
							fileName : values['fileName'],
							query : values['query'],
							dataSource : values['dataSource'],
							wsAddress : values['wsAddress'],
							wsOperation : values['wsOperation'],
							script : values['script'],
							scriptLanguage : values['scriptLanguage'],
							jclassName : values['jclassName'],
							trasfTypeCd : values['trasfTypeCd'],
							pivotColName : values['pivotColName'],
							pivotColValue : values['pivotColValue'],
							pivotRowName : values['pivotRowName'],
							pivotIsNumRows : values['pivotIsNumRows'],
							qbeSQLQuery : values['qbeSQLQuery'],
							qbeJSONQuery : values['qbeJSONQuery'],
							qbeDataSource: values['qbeDataSource'],
							qbeDatamarts: values['qbeDatamarts']
						});

					}

					var params = {
						name : values['name'],
						label : values['label'],
						description : values['description'],
						dsTypeCd : values['dsTypeCd'],
						catTypeCd : values['catTypeCd'],
						usedByNDocs : values['usedByNDocs'],
						fileName : values['fileName'],
						query : values['query'],
						dataSource : values['dataSource'],
						wsAddress : values['wsAddress'],
						wsOperation : values['wsOperation'],
						script : values['script'],
						scriptLanguage : values['scriptLanguage'],
						jclassName : values['jclassName'],
						trasfTypeCd : values['trasfTypeCd'],
						pivotColName : values['pivotColName'],
						pivotColValue : values['pivotColValue'],
						pivotRowName : values['pivotRowName'],
						pivotIsNumRows : values['pivotIsNumRows'],
						qbeSQLQuery : values['qbeSQLQuery'],
						qbeJSONQuery : values['qbeJSONQuery'],
						qbeDataSource: values['qbeDataSource'],
						qbeDatamarts: values['qbeDatamarts']
					};

					var arrayPars = this.manageParsGrid.getParsArray();
					if (arrayPars) {
						params.pars = Ext.util.JSON.encode(arrayPars);
						if (newRec != null && newRec != undefined) {
							newRec.set('pars', arrayPars);
						}
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

												if (newRec != null
														&& newRec != undefined
														&& itemId != null
														&& itemId !== '') {
													newRec
															.set('id',
																	itemId);
													this.mainElementsStore
															.add(newRec);
												}
												this.mainElementsStore
														.commitChanges();
												if (newRec != null
														&& newRec != undefined
														&& itemId != null
														&& itemId !== '') {
													this.rowselModel
															.selectLastRow(true);
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
								failure : function(response) {
									if (response.responseText !== undefined) {
										var content = Ext.util.JSON
												.decode(response.responseText);
										var errMessage = '';
										for ( var count = 0; count < content.errors.length; count++) {
											var anError = content.errors[count];
											if (anError.localizedMessage !== undefined
													&& anError.localizedMessage !== '') {
												errMessage += anError.localizedMessage;
											} else if (anError.message !== undefined
													&& anError.message !== '') {
												errMessage += anError.message;
											}
											if (count < content.errors.length - 1) {
												errMessage += '<br/>';
											}
										}

										Ext.MessageBox
												.show({
													title : LN('sbi.generic.validationError'),
													msg : errMessage,
													width : 400,
													buttons : Ext.MessageBox.OK
												});
									} else {
										Ext.MessageBox
												.show({
													title : LN('sbi.generic.error'),
													msg : LN('sbi.generic.savingItemError'),
													width : 150,
													buttons : Ext.MessageBox.OK
												});
									}
								},
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
					var qbeBaseUrl = Sbi.config.qbeDatasetBuildUrl
							+ '&DATASOURCE_LABEL='
							+ this.detailQbeDataSource
									.getValue()
							+ '&DATAMART_NAME=' 
							+ this.qbeDatamarts.getValue();
					if (this.qbeDataSetBuilder === undefined) {
						this.qbeDataSetBuilder = new Sbi.tools.dataset.QbeDatasetBuilder(
								{
									qbeBaseUrl : qbeBaseUrl,
									jsonQuery : this.qbeJSONQuery.getValue(),
									qbeParameters : this.manageParsGrid.getParsArray(),
									modal : true,
									width : this.getWidth() - 50,
									height : this
											.getHeight() - 50,
									listeners : {
										hide : 
											{
												fn : function(
													theQbeDatasetBuilder) {
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
					this.qbeDataSetBuilder.show();
				}

});
