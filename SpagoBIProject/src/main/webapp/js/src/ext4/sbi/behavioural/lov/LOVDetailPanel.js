/***************************************************************************************************************************************************************
 * SpagoBI, the Open Source Business Intelligence suite
 * 
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 **************************************************************************************************************************************************************/

/**
 * 
 *  @author
 *  Danilo Ristovski (danilo.ristovski@mht.net)
 */

/**
 * LOV Detail Panel is positioned on the right side of the initial LOV page and it is composed
 * of two tabs: 
 * 
 * 		1) 	the first one is the LOV GUI form that contains GUI elements that collect and 
 * 			display the LOV data characteristic for specific LOV input types and every single LOV 
 * 			record;
 * 		2) 	the second tab displays the results of the testing of the data that are specified for 
 * 			the current LOV record.
 */

Ext.define("Sbi.behavioural.lov.LOVDetailPanel",

{
	extend : "Ext.form.Panel",

	config : 
	{
		// frame: true,
		// bodyPadding: '5 5 0',

		defaults : 
		{
			// width: 400
			layout : "fit"
		},

		fieldDefaults : 
		{
			labelAlign : 'right',
			msgTarget : 'side'
		},

		border : false,

		services : []
	},

	constructor : function(config) 
	{
		Sbi.debug('[IN] LOVDetailPanel - constructor');

		this.initConfig(config);	
		
		this.initFields();

		/**
		 * The Result panel is the second tab on the right part of the LOV page. It displays 
		 * the result of the testing of the current LOV data (LOV record). We can test already
		 * existing LOV records (the one existing in the DB) or the new one (the one we want to
		 * create).
		 */
		this.resultPanel = Ext.create
		(
			"Ext.panel.Panel",

			{
				layout : "fit",
				border : false
			}
		);

		/**
		 * Variable that keeps us in touch with the global scope (the scope of the LOV detail panel)
		 * through the code.
		 * */
		var globalScope = this;

		// TODO: 
		/**
		 * The complete right side of the LOV page is in the form of the tab panel that contains two
		 * tags (
		 */
		this.tabPanel = Ext.create
		(
			'Ext.tab.Panel',

			{
				// width: 720,
				// height: screen.innerHeight,
				border : false,
				layout : "fit",
				id : "TAB_PANEL_RESULTS",
	
				items : [ {
					title : "LOV Form",
					items : [ this.panel1, this.panel2 ],
					border : false
				},
	
				{
					title : 'LOV Results',
					items : [ this.resultPanel ],
					layout : "fit",
					border : false
				} ],
	
				defaults : {
					listeners : {
						activate : function(tab, eOpts) {
							if (tab.title == "LOV Form") {
								// When form tab is selected show Test button and hide Save button
								globalScope.getComponent("TOOLBAR").items.items[2].show();
								globalScope.getComponent("TOOLBAR").items.items[3].show();
							} else {
								// When result tab is selected show Save button and hide Test button
								globalScope.getComponent("TOOLBAR").items.items[2].hide();
								globalScope.getComponent("TOOLBAR").items.items[3].show();
							}
						}
					}
				}
			}
		);

		this.items = [ this.tabPanel ];

		this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar({
			id : "TOOLBAR",

			items : [ {
				name : '->'
			}, {
				name : 'test'
			}, {
				name : 'save'
			} ]
		},

		this);

		this.tbar.on("save",

		function() {
			if (true) {
				this.fireEvent("save", this.getValues());
			} else {
				Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.datasource.validation.error'), LN('sbi.generic.validationError'));
			}
		},

		this);

		this.tbar.on("test",

		function() {
			this.fireEvent("test", this.getValues());
		},

		this);

		this.callParent(arguments);

		this.on("render",

		function() {
			this.hide();
		},

		this);
		
		

		Sbi.debug('[OUT] LOVDetailPanel - constructor');

	},

	takeValues : function() {
		Sbi.debug('[IN&OUT] LOVDetailPanel - takeValues()');
		return this.resultPanel.getComponent("TEST_LOV_PANEL").takeValues();
	},

	setValues : function(data) {
		Sbi.debug('[IN&OUT] LOVDetailPanel - setValues()');
		this.resultPanel.getComponent("TEST_LOV_PANEL").setValues(data);
	},

	splitProfileAttributes : function(profAttributes) {
		Sbi.debug('[IN] LOVDetailPanel - splitProfileAttributes()');

		var profAttr = [];
		var profAttrValues = "";

		var array = profAttributes.substring(1, profAttributes.length - 1);

		var splitArray = array.split(",");

		var lengthSplitArray = splitArray.length;
		var nameArray = [];
		profAttr = [];

		for (var i = 0; i < lengthSplitArray; i++) {
			var oneElemSplit = splitArray[i].split(":");
			var tempAttrName = oneElemSplit[0].substring(1, oneElemSplit[0].length - 1);
			nameArray[i] = tempAttrName;
			profAttr[i] = [ tempAttrName, oneElemSplit[1].substring(1, oneElemSplit[1].length - 1) ];
		}

		Sbi.debug('[OUT] LOVDetailPanel - splitProfileAttributes()');

		return [ profAttr, nameArray ];
	},

	updatePanel : function(contextName, lovConfig, lovProvider, profAttributes) {
		Sbi.debug('[IN] LOVDetailPanel - updatePanel()');

		var prof = null;

		var values = null;
		var first = null;
		var firstName = null;
		var firstValue = null;

		if (profAttributes != null || profAttributes != undefined) {
			prof = this.splitProfileAttributes(profAttributes);

			values = prof[0];

			for (var i = 0; i < values.length; i++) {
				if (this.profileAttrStoreContainer == undefined || this.profileAttrStoreContainer[values[i][0]] == undefined
						|| this.profileAttrStoreContainer[values[i][0]] == null || this.profileAttrStoreContainer[values[i][0]] == {}) {
					Ext.define('MissingProfileAttributes',

					{
						extend : 'Ext.data.Model',

						fields : [ {
							name : 'firstName',
							type : 'string'
						} ]
					});

					var storeAttributes = Ext.create('Ext.data.Store', {
						model : 'MissingProfileAttributes'
					});

					storeAttributes.add({
						firstName : values[i][1]
					});
					this.profileAttrStoreContainer[values[i][0]] = storeAttributes;
				} else {
					var ourStore = this.profileAttrStoreContainer[values[i][0]];

					var storeItems = ourStore.data.items;
					var repeating = false;

					for (var j = 0; j < storeItems.length; j++) {
						if (storeItems[j].data.firstName != firstValue)
							repeating = true;
					}

					if (repeating == false)
						ourStore.add({
							firstName : values[i][1]
						});

				}

				this.profileAttrStoreContainer[values[i][0]].lastSelected = values[i][1];

				if (this.lovId.value == 0) {
					this.profileAttrStoreContainerLovIdZero[i] = this.profileAttrStoreContainer[values[i][0]];
					this.attributeContainer[i] = values[i][0];
				}
			}
		}

		var updateScope = this;

		// Indicator of whether Add button on pop-up window is clicked.
		var addClicked = false;

		this.resultPanel.removeAll();

		var profileAttributes = profAttributes;

		var lovTest = Ext.create("Sbi.behavioural.lov.TestLovPanel2",

		{
			contextName : contextName,
			lovConfig : lovConfig,
			profileAttributes : profileAttributes,
			id : "TEST_LOV_PANEL",
			// tabPanelHeight: this.tabPanel.height,
			// Can I define tab height this way?
			tabPanelHeight : window.innerHeight,
			lovProvider : lovProvider
		});

		lovTest.on("wrongSyntax2",

		function()
		{
			/*
			 * If user make mistake and type wrong syntax for SQL query take 
			 * him back to the first page (LOV form) and provide him the last known (good, valid)
			 * SQL query (the query that existed) previously for this LOV.
			 */

			Sbi.debug('[IN] LOVDetailPanel - updatePanel() - wrong syntax error');

			var startStatement = -1;
			var endStatement = -1;
			var oldStatement = "";

			// Return old LOV value for new LOV value (error happened, so we are losing nothing)
			// this.lovProvider.value = this.lovProvider.lastValue;

			this.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].hide();			
			this.getComponent("TAB_PANEL_RESULTS").setActiveTab(0);
			
			// Hide the Save button
			this.getComponent("TOOLBAR").items.items[3].hide();

			Sbi.debug('[OUT] LOVDetailPanel - updatePanel() - wrong syntax error');
		},

		this);

		lovTest.on("missingProfileAttr2",

		function(missingProfileAttr) {
			/*
			 * We are getting back from server-side with the request to fill the missing Profile attribute(s) for the current user.
			 */

			var numbOfMissingProfileAttr = missingProfileAttr.length;

			var lovProfileAttrPanel = Ext.create("Sbi.behavioural.lov.LOVProfileAttributeFilling", {});
			lovProfileAttrPanel.create();

			if (this.profileAttrStoreContainer == undefined) {
				for (var k = 0; k < numbOfMissingProfileAttr; k++) {
					this.profileAttrStoreContainer = {};

					Ext.define('MissingProfileAttributes',

					{
						extend : 'Ext.data.Model',
						fields : [ {
							name : 'firstName',
							type : 'string'
						} ]
					});

					var storeAttributes = Ext.create('Ext.data.Store', {
						model : 'MissingProfileAttributes'
					});

					this.profileAttrStoreContainer[missingProfileAttr[k]] = storeAttributes;
				}
			}

			for (var u = 0; u < numbOfMissingProfileAttr; u++) {
				lovProfileAttrPanel.lovFixedListForm.add({
					xtype : 'combobox',
					// (top, right, bottom, left)
					padding : '10 5 0 0',
					valueField : 'firstName',
					fieldLabel : missingProfileAttr[u],
					labelAlign : 'right',
					store : this.profileAttrStoreContainer[missingProfileAttr[u]],
					queryMode : 'local',
					displayField : 'firstName'
				});

				/*
				 * Setting the last chosen value for every single missing profile attribute into their belonging combo boxes.
				 */
				var combo = lovProfileAttrPanel.lovFixedListForm.items.items[u];
				var comboStoreData = combo.store.data;

				if (comboStoreData.length > 0) {
					combo.setValue(this.profileAttrStoreContainer[missingProfileAttr[u]].lastSelected);
				}
			}

			/*
			 * Pop-up window that will appear when it is needed to define missing profile attributes for LOV.
			 */
			var window1 = Ext.create('Ext.window.Window',

			{
				title : LN('sbi.behavioural.lov.details.missingProfileAttributesWindow'),
				layout : 'fit',
				resizable : false,
				modal : true, // Prevent user from selecting something behind the window
				items : [ lovProfileAttrPanel.lovFixedListForm ],

				listeners : {
					'close' : function(win) {
						/*
						 * Check if window is closed after clicking the Add button or after clicking on the X button on the top right corner. In the first case
						 * close window and show the first tab, hidding the second one. In the latter case, show the result page and close the window for
						 * filling the missing profile attributes.
						 */
						if (addClicked == false) {
							updateScope.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].hide();
							updateScope.getComponent("TAB_PANEL_RESULTS").setActiveTab(0);
						}
					}
				}
			});

			/*
			 * If this method (updatePanel) is called for the first time - when we should defined the missing profile attribute(s).
			 */
			if (profAttributes == null || profAttributes == undefined || profAttributes == "") {
				/*
				 * Add confirmation (Add) button to the form that will lie inside pop-up window
				 */
				lovProfileAttrPanel.lovFixedListForm.add({
					xtype : "button",
					text : LN("sbi.behavioural.lov.details.fixLovAddItemsButton"),
					margin : '10 10 15 290', // (top, right, bottom, left)
					width : 70,
					// scope: this,

					handler : function() {
						var form = this.up('form').getForm();
						var arrayOfAttributes = "{";

						/*
						 * If no profile attribute is missing, we can send the form to the server-side in order to proceed to testing page. Now, we will form
						 * one string with all the data that was missing - (key,value) pairs that we will parse (split) on the server-side to get those
						 * attributes names and their values.
						 */

						for (var i = 0; i < numbOfMissingProfileAttr; i++) {
							if (i < numbOfMissingProfileAttr - 1 && numbOfMissingProfileAttr > 1) {
								arrayOfAttributes = arrayOfAttributes + "\"" + missingProfileAttr[i] + "\":\"" + form._fields.items[i].rawValue + "\",";
							} else if (numbOfMissingProfileAttr == 1 || i == numbOfMissingProfileAttr - 1) {
								arrayOfAttributes = arrayOfAttributes + "\"" + missingProfileAttr[i] + "\":\"" + form._fields.items[i].rawValue + "\"";
							}
						}

						arrayOfAttributes = arrayOfAttributes + "}";

						/*
						 * Recursive call - call again THIS function to send data necessary for testing once again (now, complete ones).
						 */
						updateScope.updatePanel(contextName, lovConfig, lovProvider, arrayOfAttributes);

						/*
						 * Close the pop-up window for filling the data about missing data
						 */
						addClicked = true;

						window1.close();
					}
				});

				window1.show();
			}

		},

		this);

		this.resultPanel.add(lovTest);

		this.resultPanel.update();

		Sbi.debug('[OUT] LOVDetailPanel - updatePanel()');
	},

	/** 
	 * This method initialize all the fields that are characteristic for different
	 * LOV input types. Fields are GUI representation of the LOV data lying behind 
	 * them - reflecting the data that are connected to them.
	 */
	initFields : function() 
	{
		Sbi.debug('[IN] LOVDetailPanel - initFields()');

		var globalScope = this;

		this.profileAttrStoreContainer = {};
		this.profileAttrStoreContainerLovIdZero = [];
		this.attributeContainer = [];

		this.lovId = Ext.create("Ext.form.field.Hidden",

		{
			name : "LOV_ID",
			id : "LovId"
		});

		this.lovLabel = Ext.create("Ext.form.field.Text", {
			name : "LOV_LABEL",
			allowBlank : false,
			fieldLabel : LN('sbi.behavioural.lov.details.label'),
			width : 400,
			padding : '10 0 0 0'
		});

		this.lovName = Ext.create("Ext.form.field.Text",

		{
			name : "LOV_NAME",
			allowBlank : false,
			fieldLabel : LN('sbi.behavioural.lov.details.name'),
			width : 400,
			padding : '10 0 0 0'
		});

		this.lovDescription = Ext.create("Ext.form.field.Text",

		{
			name : "LOV_DESCRIPTION",
			fieldLabel : LN('sbi.behavioural.lov.details.description'),
			width : 400,
			padding : '10 0 0 0'
		});

		this.lovProvider = Ext.create("Ext.form.field.Hidden",

		{
			name : "LOV_PROVIDER",
			// height: 200,
			// width: 400,
			readOnly : true
		});

		this.lovSelectionType = Ext.create("Ext.form.field.Hidden",

		{
			name : "SELECTION_TYPE"
		});

		Ext.define("InputTypeModel",

		{
			extend : 'Ext.data.Model',
			fields : [ "VALUE_NM", "VALUE_DS", "VALUE_ID", "VALUE_CD" ]
		});

		var inputTypeStore = Ext.create('Ext.data.Store',

		{
			model : "InputTypeModel",
			autoLoad : true,

			proxy : {
				type : 'rest',

				extraParams : {
					DOMAIN_TYPE : "INPUT_TYPE"
				},

				url : globalScope.services['getDomains'],

				reader : {
					type : "json"
				}
			}
		});

		inputTypeStore.on("load",

		function(inputTypeStore) {
			Sbi.debug('[INFO] Input type store loaded');
		});

		this.lastSelectedLov = null;

		this.lovInputTypeCombo = Ext.create('Ext.form.ComboBox',

		{
			fieldLabel : LN('sbi.behavioural.lov.details.inputType'),
			name : "I_TYPE_CD",
			store : inputTypeStore,
			id : "INPUT_TYPE_COMBO",
			displayField : 'VALUE_NM',
			valueField : 'VALUE_CD',
			editable : false,
			allowBlank : false,
			padding : "10 0 10 0",

			listeners : 
			{
				select : function() 
				{
					var panel2 = globalScope.tabPanel.items.items[0].getComponent("PANEL2");

					panel2.removeAll();				
					
					/*
					 * Take every detail form LOV provider in order to recreate the existing record (when we, for example, in existing record change the input
					 * type, populate the data in the form of the new LOV input type and then we want to return to the previous type of the selected record with
					 * the original data). In that situation the initial form for LOV is populated with the right data and LOV provider provide us data
					 * necessary for the test page of the re-tested original LOV (record) - VALUE, DESCRIPTION and VISIBLE columns.
					 */

					var lovProvider = globalScope.panel1.items.items[4].value;
					
					if (this.value == "QUERY") 
					{
						var lovQueryPanel = Ext.create("Sbi.behavioural.lov.LOVQueryBottomPanel", {});
						lovQueryPanel.create(globalScope.services["getDataSources"]);

						if (lovProvider.indexOf("<QUERY>") > -1)
						{							
							if (globalScope.lovId.value == 0) 
							{
								lovQueryPanel.dataSourceCombo.setValue("");
								lovQueryPanel.dataSourceQuery.setValue("");
							} 
							else 
							{
								// NOTE: This was within the previous 'else' scope
							   	var startIndex = lovProvider.indexOf("<CONNECTION>");
								var endIndex = lovProvider.indexOf("</CONNECTION>");

								lovQueryPanel.dataSourceCombo.setValue(lovProvider.substring(startIndex + "<CONNECTION>".length, endIndex));

								var startIndex = lovProvider.indexOf("<STMT>");
								var endIndex = lovProvider.indexOf("</STMT>");

								lovQueryPanel.dataSourceQuery.setValue(lovProvider.substring(startIndex + "<STMT>".length, endIndex));								
							}
						}
						
						/* Disable SAVE button if query is 
						 * changed (different from the original). */
						lovQueryPanel.dataSourceQuery.on
						(
							"change", 
							
							function()
							{										
								/* Value for query of the saved LOV record 
								 * (the one that exists in DB). */						
								var startIndex2 = globalScope.lovProvider.value.indexOf("<STMT>");
								var endIndex2 = globalScope.lovProvider.value.indexOf("</STMT>");
								var query2 = globalScope.lovProvider.value.substring(startIndex2 + "<STMT>".length, endIndex2);
								
								/* Value of the query that is written in its textarea 
								 * right now (at this very moment). */
								var currentFormQuery = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[1].value;
								
								/* Value for datasource of the saved LOV record 
								 * (the one that exists in DB). */						
								var startDataSourceIndex2 = globalScope.lovProvider.value.indexOf("<CONNECTION>");
								var endDataSourceIndex2 = globalScope.lovProvider.value.indexOf("</CONNECTION>");		
								var dataSource2 = globalScope.lovProvider.value.substring(startDataSourceIndex2 + "<CONNECTION>".length, endDataSourceIndex2);
								
								/* Value of the datasource that is chosen in combobox
								 * right now (at this very moment). */
								var currentFormDataSource = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[0].value;
								
								/* In case that current query in LOV form is different
								 * form the one saved in DB (existing LOV record) hide Save
								 * button, as well as Test result page. */
								if (currentFormQuery != query2 || currentFormDataSource != dataSource2)
								{
									globalScope.getComponent("TOOLBAR").items.items[3].hide();
									globalScope.tabPanel.tabBar.items.items[1].hide();
								}	
								else
								{									
									globalScope.getComponent("TOOLBAR").items.items[3].show();
								}								
							}
						);
						
						/* Disable SAVE button if datasource is 
						 * changed (different from the original). */
						lovQueryPanel.dataSourceCombo.on
						(
							"change", 
							
							function()
							{		
								/* Value for query of the saved LOV record 
								 * (the one that exists in DB). */						
								var startQueryIndex2 = globalScope.lovProvider.value.indexOf("<STMT>");
								var endQueryIndex2 = globalScope.lovProvider.value.indexOf("</STMT>");
								var query2 = globalScope.lovProvider.value.substring(startQueryIndex2 + "<STMT>".length, endQueryIndex2);
								
								/* Value of the query that is written in its textarea 
								 * right now (at this very moment). */
								var currentFormQuery = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[1].value;
								
								
									/* Value for datasource of the saved LOV record 
									 * (the one that exists in DB). */						
									var startIndex2 = globalScope.lovProvider.value.indexOf("<CONNECTION>");
									var endIndex2 = globalScope.lovProvider.value.indexOf("</CONNECTION>");		
									var dataSource2 = globalScope.lovProvider.value.substring(startIndex2 + "<CONNECTION>".length, endIndex2);
									
									/* Value of the datasource that is chosen in combobox
									 * right now (at this very moment). */
									var currentFormDataSource = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[0].value;
																		
									/* In case that datasource code in LOV form is different
									 * form the one saved in DB (existing LOV record) hide Save
									 * button, as well as Test result page. */
									if (currentFormDataSource != dataSource2 || currentFormQuery != query2)
									{
										globalScope.getComponent("TOOLBAR").items.items[3].hide();
										globalScope.tabPanel.tabBar.items.items[1].hide();
									}	
									else
									{
										globalScope.getComponent("TOOLBAR").items.items[3].show();
									}
								
							}
						);

						panel2.setTitle(LN('sbi.behavioural.lov.details.queryWizard'));
						panel2.add(lovQueryPanel.dataSourceCombo);
						panel2.add(lovQueryPanel.dataSourceQuery);
					}

					else if (this.value == "SCRIPT") 
					{
						var lovScriptPanel = Ext.create("Sbi.behavioural.lov.LOVScriptBottomPanel", {});
						lovScriptPanel.create(globalScope.services["getDomains"]);

						if (lovProvider.indexOf("<SCRIPT>") > -1) 
						{
							if (globalScope.lovId.value == 0) 
							{
								lovScriptPanel.scriptTypeCombo.setValue("");
								lovScriptPanel.scriptQuery.setValue("");
							} else 
							{
								var startScriptType = lovProvider.indexOf("<LANGUAGE>") + "<LANGUAGE>".length;
								var endScriptType = lovProvider.indexOf("</LANGUAGE>");
								var scriptType = lovProvider.substring(startScriptType, endScriptType);

								// !!!! Maybe change this solution
								if (scriptType == "ECMAScript") // saw in ScripDetail.java
									scriptType = "Javascript";
								else if (scriptType == "groovy") // saw in ScripDetail.java
									scriptType = "Groovy";

								lovScriptPanel.scriptTypeCombo.setValue(scriptType);

								var startScript = lovProvider.indexOf("<SCRIPT>") + "<SCRIPT>".length;
								var endScript = lovProvider.indexOf("</SCRIPT>");
								lovScriptPanel.scriptQuery.setValue(lovProvider.substring(startScript, endScript));
							}
						}

						/* Disable SAVE button if script code is 
						 * changed (different from the original). */
						lovScriptPanel.scriptQuery.on
						(
							"change", 
							
							function()
							{		
								/* Value for script code of the saved LOV record 
								 * (the one that exists in DB). */						
								var startScriptCode2 = globalScope.lovProvider.value.indexOf("<SCRIPT>") + "<SCRIPT>".length;
								var endScriptCode2 = globalScope.lovProvider.value.indexOf("</SCRIPT>");
								var scriptCode2 = globalScope.lovProvider.value.substring(startScriptCode2, endScriptCode2);
								
								/* Value of the script code that is written in its textarea 
								 * right now (at this very moment). */
								var currentFormScriptCode = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[1].value;
								
								/* Value for script type of the saved LOV record 
								 * (the one that exists in DB). */
								var startScriptType2 = globalScope.lovProvider.value.indexOf("<LANGUAGE>") + "<LANGUAGE>".length;
								var endScriptType2 = globalScope.lovProvider.value.indexOf("</LANGUAGE>");
								var scriptType2 = globalScope.lovProvider.value.substring(startScriptType2, endScriptType2);
								
								if (scriptType2 == "ECMAScript") // saw in ScripDetail.java
								{
									scriptType2 = "Javascript";
								}					
								else if (scriptType2 == "groovy") // saw in ScripDetail.java
								{
									scriptType2 = "Groovy";	
								}	
								
								/* Value of the script type that is chosen in its combobox 
								 * right now (at this very moment). */
								var currentFormScriptType = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[0].value;
								
								/* In case that current script code in LOV form is different
								 * form the one saved in DB (existing LOV record) hide Save
								 * button, as well as Test result page. */
								if (currentFormScriptCode != scriptCode2 || currentFormScriptType != scriptType2)
								{
									globalScope.getComponent("TOOLBAR").items.items[3].hide();
									globalScope.tabPanel.tabBar.items.items[1].hide();
								}	
								else
								{
									globalScope.getComponent("TOOLBAR").items.items[3].show();
								}								
							}
						);
						
						/* Disable SAVE button if script type is changed (different from the original). */
						lovScriptPanel.scriptTypeCombo.on
						(
							"change", 
							
							function()
							{
								/* Value for script type of the saved LOV record 
								 * (the one that exists in DB). */
								var startScriptType2 = globalScope.lovProvider.value.indexOf("<LANGUAGE>") + "<LANGUAGE>".length;
								var endScriptType2 = globalScope.lovProvider.value.indexOf("</LANGUAGE>");
								var scriptType2 = globalScope.lovProvider.value.substring(startScriptType2, endScriptType2);
								
								if (scriptType2 == "ECMAScript") // saw in ScripDetail.java
								{
									scriptType2 = "Javascript";
								}					
								else if (scriptType2 == "groovy") // saw in ScripDetail.java
								{
									scriptType2 = "Groovy";	
								}	
								
								/* Value of the script type that is chosen in its combobox 
								 * right now (at this very moment). */
								var currentFormScriptType = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[0].value;
								
								/* Value for script code of the saved LOV record 
								 * (the one that exists in DB). */						
								var startScriptCode2 = globalScope.lovProvider.value.indexOf("<SCRIPT>") + "<SCRIPT>".length;
								var endScriptCode2 = globalScope.lovProvider.value.indexOf("</SCRIPT>");
								var scriptCode2 = globalScope.lovProvider.value.substring(startScriptCode2, endScriptCode2);
								
								/* Value of the script code that is written in its textarea 
								 * right now (at this very moment). */
								var currentFormScriptCode = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[1].value;								
								
								/* In case that current script type in LOV form is different
								 * form the one saved in DB (existing LOV record) hide Save
								 * button, as well as Test result page. */
								if (currentFormScriptType != scriptType2 || currentFormScriptCode != scriptCode2)
								{
									globalScope.getComponent("TOOLBAR").items.items[3].hide();
									globalScope.tabPanel.tabBar.items.items[1].hide();
								}	
								else
								{
									globalScope.getComponent("TOOLBAR").items.items[3].show();
								}	
							}
						);
						
						panel2.setTitle(LN('sbi.behavioural.lov.details.scriptWizard'));
						panel2.add(lovScriptPanel.scriptTypeCombo);
						panel2.add(lovScriptPanel.scriptQuery);
					}

					else if (this.value == "FIX_LOV") 
					{
						var lovFixedListPanel = Ext.create("Sbi.behavioural.lov.LOVFixedListBottomPanel", {});
						lovFixedListPanel.create();

						var fixLovStore = lovFixedListPanel.fixLovStore;

						if (lovProvider.indexOf("<FIXLISTLOV>") > -1) 
						{
							if (globalScope.lovId.value == 0) 
							{
								fixLovStore.removeAll();
							} 
							else 
							{
								var startFixLov = lovProvider.indexOf("<ROWS>") + "<ROWS>".length;
								var endFixLov = lovProvider.indexOf("</ROWS>");
								var fixLovRows = lovProvider.substring(startFixLov, endFixLov);
								var listRows = fixLovRows.split("<ROW ");

								fixLovStore.removeAll();

								for (var i = 1; i < listRows.length; i++) {
									var valueStart = listRows[i].indexOf("VALUE=") + "VALUE=".length + 1;
									var valueEnd = listRows[i].indexOf("\" DESCRIPTION");
									var valueFixLov = listRows[i].substring(valueStart, valueEnd);

									var descriptionStart = listRows[i].indexOf("DESCRIPTION=") + "DESCRIPTION=".length + 1;
									var descriptionEnd = listRows[i].indexOf("/>") - 1;
									var descriptionFixLov = listRows[i].substring(descriptionStart, descriptionEnd);

									fixLovStore.insert
									(
										i, 
										
										{
											value : valueFixLov,
											description : descriptionFixLov
										}
									);
								}
							}

						}

						var lovFixedListForm = lovFixedListPanel.lovFixedListForm;
						var fixLovGrid = lovFixedListPanel.fixLovGrid;
						var infoPanel = lovFixedListPanel.infoPanel;

						lovFixedListForm.getComponent("FixLovValue").setValue("");
						lovFixedListForm.getComponent("FixLovDescription").setValue("");

						fixLovGrid.reconfigure(fixLovStore);
						fixLovGrid.update();
						
						/* Disable SAVE button if any item in fixed list grid is changed - modified. 
						 * Not enabled to retrieve Save button if grid is returned into original
						 * state (with original (starting) values for all items present in grid).
						 * Any item from any column in fix LOV grid will fire this event.*/
						lovFixedListPanel.fixLovGrid.on
						(
							"changedGridItemsValue", 
							
							function()
							{
								globalScope.getComponent("TOOLBAR").items.items[3].hide();
								globalScope.tabPanel.tabBar.items.items[1].hide();
							}
						);
						
						/* Also, disable SAVE button if Add button is successfully executed
						 * (parameters of the LOV fix form are well defined) and new row is 
						 * added to the grid. */
						lovFixedListPanel.lovFixedListForm.on
						(
							"addButtonPressed", 
							
							function() 
							{
								globalScope.getComponent("TOOLBAR").items.items[3].hide();
								globalScope.tabPanel.tabBar.items.items[1].hide();
							}
						);
						
						/* Disable SAVE button if Delete (Remove) button is pressed and
						 * record is removed from the fixed list grid. */
						lovFixedListPanel.fixLovGrid.on
						(
							"itemRemovedFromTheGrid", 
							
							function() 
							{
								globalScope.getComponent("TOOLBAR").items.items[3].hide();
								globalScope.tabPanel.tabBar.items.items[1].hide();
							}
						);		

						panel2.setTitle(LN('sbi.behavioural.lov.details.fixedListWizard'));
						panel2.add(lovFixedListForm);
						panel2.add(infoPanel);
						panel2.add(fixLovGrid);
					}

					else if (this.value == "JAVA_CLASS") 
					{
						var lovJavaClassPanel = Ext.create("Sbi.behavioural.lov.LOVJavaClassBottomPanel", {});
						lovJavaClassPanel.create();

						if (lovProvider.indexOf("<JAVACLASSLOV>") > -1) 
						{
							if (globalScope.lovId.value == 0) 
							{
								lovJavaClassPanel.javaClassName.setValue("");
							} 
							else 
							{
								var startJavaClassName = lovProvider.indexOf("<JAVA_CLASS_NAME>") + "<JAVA_CLASS_NAME>".length;
								var endJavaClassName = lovProvider.indexOf("</JAVA_CLASS_NAME>");
								var javaClassName = lovProvider.substring(startJavaClassName, endJavaClassName);

								lovJavaClassPanel.javaClassName.setValue(javaClassName);
							}
						}
						
						lovJavaClassPanel.javaClassName.on
						(
							"change",
							
							function()
							{
								/* Value for script code of the saved LOV record 
								 * (the one that exists in DB). */						
								var startJavaClassName2 = globalScope.lovProvider.value.indexOf("<JAVA_CLASS_NAME>") + "<JAVA_CLASS_NAME>".length;
								var endJavaClassName2 = globalScope.lovProvider.value.indexOf("</JAVA_CLASS_NAME>");
								var javaClassName2 = globalScope.lovProvider.value.substring(startJavaClassName2, endJavaClassName2);
								
								/* Value of the script code that is written in its textarea 
								 * right now (at this very moment). */
								var currentJavaClassName = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[0].value;
								
								/* In case that current script code in LOV form is different
								 * form the one saved in DB (existing LOV record) hide Save
								 * button, as well as Test result page. */
								if (currentJavaClassName != javaClassName2)
								{
									globalScope.getComponent("TOOLBAR").items.items[3].hide();
									globalScope.tabPanel.tabBar.items.items[1].hide();
								}	
								else
								{
									globalScope.getComponent("TOOLBAR").items.items[3].show();
								}	
							}
						);

						panel2.setTitle(LN('sbi.behavioural.lov.details.javaClassWizard'));
						panel2.add(lovJavaClassPanel.javaClassName);
					}

					else if (this.value == "DATASET") 
					{
						var lovDatasetPanel = Ext.create("Sbi.behavioural.lov.LOVDatasetBottomPanel", {});
						lovDatasetPanel.create(globalScope.services["datasets"]);
						
						if (lovProvider.indexOf("<DATASET>") > -1) 
						{
							if (globalScope.lovId.value == 0) 
							{
								lovDatasetPanel.datasetForm.items.items[0].items.items[0].setValue("");
							} 
							else 
							{
								var startDatasetLabel = lovProvider.indexOf("<LABEL>") + "<LABEL>".length;
								var endDatasetLabel = lovProvider.indexOf("</LABEL>");
								var datasetLabel = lovProvider.substring(startDatasetLabel, endDatasetLabel);
								
								lovDatasetPanel.datasetForm.items.items[0].items.items[0].setValue(datasetLabel);
							}
						}
						
						lovDatasetPanel.datasetWindow.on
						(
							"hide",
							
							function()
							{
								/* Value for script code of the saved LOV record 
								 * (the one that exists in DB). */						
								var startDatasetLabel2 = globalScope.lovProvider.value.indexOf("<LABEL>") + "<LABEL>".length;
								var endDatasetLabel2 = globalScope.lovProvider.value.indexOf("</LABEL>");
								var datasetLabel2 = globalScope.lovProvider.value.substring(startDatasetLabel2, endDatasetLabel2);
														
								/* Value of the script code that is written in its textarea 
								 * right now (at this very moment). */
								
								var currentDatasetLabel = globalScope.tabPanel.items.items[0].getComponent("PANEL2").items.items[0].value;								
								var panel2 = globalScope.tabPanel.items.items[0].getComponent("PANEL2");
								var currentDatasetLabel = panel2.items.items[0].items.items[0].items.items[0].value;
								
								/* In case that current script code in LOV form is different
								 * form the one saved in DB (existing LOV record) hide Save
								 * button, as well as Test result page. */
								if (currentDatasetLabel != datasetLabel2)
								{
									globalScope.getComponent("TOOLBAR").items.items[3].hide();
									globalScope.tabPanel.tabBar.items.items[1].hide();
								}	
								else
								{
									globalScope.getComponent("TOOLBAR").items.items[3].show();
								}												
							}
						);

						panel2.setTitle(LN('sbi.behavioural.lov.details.datasetWizard'));
						panel2.add(lovDatasetPanel.datasetForm);
					}

					panel2.show();
				},
				
				change: function()
				{		
					Sbi.debug("[IN] Change listener");
					var lovListDetails = globalScope.ownerCt.ownerCt;
					var grid = lovListDetails.items.items[0].items.items[0];
					var store = grid.store;
					
					var indexOfSelected = grid.selModel.selected.items[0].index;
					
					/* Exists only when we already have record in DB, i.e. store that 
					 * contains LOV and that grid is synchronized with. When we are 
					 * creating new record, 'change' event will be fired, but this
					 * variable will not be defined, because it does not exist in the
					 * store (LOV ID=0). */					
					var selectedLov = store.getAt(indexOfSelected);	
					
					/* Therefore, we need to check if this event is fired after changing 
					 * the item in input type combobox or when new record is going to be 
					 * defined. In latter case, 'selectedLov' variable will be undefined. */
					if (selectedLov && globalScope.lovInputTypeCombo.value == selectedLov.data.I_TYPE_CD)
					{							
						globalScope.getComponent("TOOLBAR").items.items[3].show();						
					}
					else
					{
						globalScope.getComponent("TOOLBAR").items.items[3].hide();
						globalScope.tabPanel.tabBar.items.items[1].hide();
					}
					
					Sbi.debug("[OUT] Change listener");
				}
			}
		});

		

		this.panel1 = Ext.create("Ext.panel.Panel",

		{
			title : LN('sbi.behavioural.lov.details.wizardUpper'),
			width : "100%",
			// (top, right, bottom, left)
			padding : '15 15 10 15',
			id : "PANEL1",

			items :

			[ this.lovId, this.lovLabel, this.lovName, this.lovDescription, this.lovProvider, this.lovInputTypeCombo, this.lovInputTypeCd, this.lovInputTypeId,
					this.lovSelectionType ],

			bodyStyle : {
				"background-color" : "#F9F9F9"
			}
		});

		Ext.define("DataSourceModel",

		{
			extend : 'Ext.data.Model',
			fields : [ "DESCRIPTION", "DATASOURCE_LABEL", "JNDI_URL", "DATASOURCE_ID" ]
		// fields (labels) from JSON that comes from server that we call
		});
		
//		this.infoPanelClearForm = Ext.create
//		(
//			'Ext.form.Panel',
//			
//			{
//				title: LN("sbi.behavioural.lov.details.fixLovInfoPanelTitle"),
//				layout: 'fit',
//				width: "100%",
//				// (top, right, bottom, left)
//				padding: '10 10 5 10',
//				icon: '/SpagoBI/themes/sbi_default/img/info22.jpg',
//				
//				items: [{
//			        xtype: 'label',
//			        text: LN('sbi.behavioural.lov.details.infoPanel'),
//			        margin: '10 0 10 10'
//			    }],
//			    
//			    bodyStyle:{"background-color":"#FFFFCC"}
//			}
//		);

		this.panel2 = Ext.create("Ext.panel.Panel",

		{
			width : "100%",	
			
			// (top, right, bottom, left)
			padding : '5 15 15 15',
			
			id : "PANEL2",
			
			bodyStyle : 
			{
				"background-color" : "#F9F9F9"
			},			
			
			tools: 
			[
			 	{ 
			        id: 'refresh',
			        qtip: "Clear the form",
			        
			        handler: function(e, toolEl, panel, tc)
			        {
			        	var panel2 = globalScope.panel2;
			        	var lovInputType = globalScope.lovInputTypeCombo.value;	
			        	
			        	globalScope.getComponent("TOOLBAR").items.items[3].hide();
						globalScope.tabPanel.tabBar.items.items[1].hide();
			        	
			        	Ext.MessageBox.confirm
			        	(
			        		LN("sbi.behavioural.lov.celarSecondPanelConfirmTitle"),
			        		
			        		LN("sbi.behavioural.lov.celarSecondPanelConfirmQuestion"),
		        			
		        			function(btn)
		        			{
			        		   if(btn === 'yes')
			        		   {			        			   
			        			   	if (lovInputType == "FIX_LOV")
		        			   		{			        			   		
			        			   		panel2.items.items[0].items.items[0].setValue("");
			        			   		panel2.items.items[0].items.items[1].setValue("");
			        			   		
			        			   		panel2.items.items[2].store.removeAll();
		        			   		}
			        			   	
			        			   	else if (lovInputType == "DATASET")
		        			   		{
			        			   		panel2.items.items[0].items.items[0].items.items[0].setValue("");        			   		
		        			   		}
			        			   	
			        			   	else
						        	{
			        			   		panel2.items.items[0].setValue("");
			        			   		
			        			   		if (lovInputType != "JAVA_CLASS")
		        			   			{
			        			   			panel2.items.items[1].setValue("");
		        			   			}						        		
						        	}
			        		   }
			        		 }
	        			);
			        	
			        	
			        }
			 	}
		 	]			
			
		});

		Sbi.debug('[OUT] LOVDetailPanel - initFields()');
	},
	
	

	getValues : function() 
	{
		Sbi.debug('[IN & OUT] LOVDetailPanel - getValues()');
		var values = this.callParent();
		return values;
	},
	
	setFormState : function(values) 
	{
		Sbi.debug('[IN] LOVDetailPanel - setFormState()');	
		
		var globalScope = this;	
		
		// Left part of LOV Details Panel - the form
		var lovFormTab = this.tabPanel.items.items[0];
		var lovFormPanel1 = lovFormTab.getComponent("PANEL1");
		var lovFormPanel2 = lovFormTab.getComponent("PANEL2");
		
		/* Show the Test button */
		globalScope.getComponent("TOOLBAR").items.items[2].show();		

		lovFormPanel2.removeAll();
		
		if (values.LOV_ID != 0) 
		{
			/* Show the Save button */
			globalScope.getComponent("TOOLBAR").items.items[3].show();
			
			// LOV ID
			lovFormPanel1.items.items[0].setValue(values.LOV_ID);
			// LOV LABEL
			lovFormPanel1.items.items[1].setValue(values.LOV_LABEL);
			// LOV NAME
			lovFormPanel1.items.items[2].setValue(values.LOV_NAME);
			// LOV DESCRIPTION
			lovFormPanel1.items.items[3].setValue(values.LOV_DESCRIPTION);

			// LOV INPUT TYPE COMBO
			this.lovInputTypeCombo.setValue(values.I_TYPE_CD);

			// LOV PROVIDER
			var lovProvider = values.LOV_PROVIDER;

			lovFormPanel1.items.items[4].setValue(lovProvider);

			if (this.lovInputTypeCombo.value == "QUERY") {
				lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.queryWizard'));				

				var startIndex = lovProvider.indexOf("<CONNECTION>");
				var endIndex = lovProvider.indexOf("</CONNECTION>");		
				var dataSource = lovProvider.substring(startIndex + "<CONNECTION>".length, endIndex);

				var startIndex = lovProvider.indexOf("<STMT>");
				var endIndex = lovProvider.indexOf("</STMT>");
				var query = lovProvider.substring(startIndex + "<STMT>".length, endIndex);

				var lovQueryPanel = Ext.create("Sbi.behavioural.lov.LOVQueryBottomPanel", {});
				lovQueryPanel.create(this.services["getDataSources"]);
				
				lovQueryPanel.dataSourceCombo.setValue(dataSource);
				lovQueryPanel.dataSourceQuery.setValue(query);

				/* Disable SAVE button if datasource is 
				 * changed (different from the original). */
				lovQueryPanel.dataSourceCombo.on
				(
					"change", 
					
					function()
					{		
						/* Value for datasource of the saved LOV record 
						 * (the one that exists in DB). */						
						var startIndex2 = globalScope.lovProvider.value.indexOf("<CONNECTION>");
						var endIndex2 = globalScope.lovProvider.value.indexOf("</CONNECTION>");		
						var dataSource2 = globalScope.lovProvider.value.substring(startIndex2 + "<CONNECTION>".length, endIndex2);
						
						/* Value of the datasource that is chosen in combobox
						 * right now (at this very moment). */
						var currentFormDataSource = lovFormPanel2.items.items[0].value;
						
						/* Value for query of the saved LOV record 
						 * (the one that exists in DB). */						
						var startQueryIndex2 = globalScope.lovProvider.value.indexOf("<STMT>");
						var endQueryIndex2 = globalScope.lovProvider.value.indexOf("</STMT>");
						var query2 = globalScope.lovProvider.value.substring(startQueryIndex2 + "<STMT>".length, endQueryIndex2);
						
						/* Value of the query that is written in its textarea 
						 * right now (at this very moment). */
						var currentFormQuery= lovFormPanel2.items.items[1].value;
												
						/* In case that datasource code in LOV form is different
						 * form the one saved in DB (existing LOV record) hide Save
						 * button, as well as Test result page. */
						if (currentFormDataSource != dataSource2 || currentFormQuery != query2)
						{
							globalScope.getComponent("TOOLBAR").items.items[3].hide();
							globalScope.tabPanel.tabBar.items.items[1].hide();
						}	
						else
						{
							globalScope.getComponent("TOOLBAR").items.items[3].show();
						}								
					}
				);
				
				/* Disable SAVE button if query is 
				 * changed (different from the original). */
				lovQueryPanel.dataSourceQuery.on
				(
					"change", 
					
					function()
					{								
						/* Value for query of the saved LOV record 
						 * (the one that exists in DB). */						
						var startIndex2 = globalScope.lovProvider.value.indexOf("<STMT>");
						var endIndex2 = globalScope.lovProvider.value.indexOf("</STMT>");
						var query2 = globalScope.lovProvider.value.substring(startIndex2 + "<STMT>".length, endIndex2);
						
						/* Value of the query that is written in its textarea 
						 * right now (at this very moment). */
						var currentFormQuery= lovFormPanel2.items.items[1].value;
						
						/* Value for datasource of the saved LOV record 
						 * (the one that exists in DB). */						
						var startDataSourceIndex2 = globalScope.lovProvider.value.indexOf("<CONNECTION>");
						var endDataSourceIndex2 = globalScope.lovProvider.value.indexOf("</CONNECTION>");		
						var dataSource2 = globalScope.lovProvider.value.substring(startDataSourceIndex2 + "<CONNECTION>".length, endDataSourceIndex2);
						
						/* Value of the datasource that is chosen in combobox
						 * right now (at this very moment). */
						var currentFormDataSource = lovFormPanel2.items.items[0].value;
						
						/* In case that current query in LOV form is different
						 * form the one saved in DB (existing LOV record) hide Save
						 * button, as well as Test result page. */
						if (currentFormQuery != query2 || currentFormDataSource != dataSource2)
						{
							globalScope.getComponent("TOOLBAR").items.items[3].hide();
							globalScope.tabPanel.tabBar.items.items[1].hide();
						}	
						else
						{
							globalScope.getComponent("TOOLBAR").items.items[3].show();
						}								
					}
				);
				
				lovFormPanel2.add(lovQueryPanel.dataSourceCombo);
				lovFormPanel2.add(lovQueryPanel.dataSourceQuery);
			}

			else if (this.lovInputTypeCombo.value == "SCRIPT") 
			{
				lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.scriptWizard'));				

				var startScriptType = lovProvider.indexOf("<LANGUAGE>") + "<LANGUAGE>".length;
				var endScriptType = lovProvider.indexOf("</LANGUAGE>");
				var scriptType = lovProvider.substring(startScriptType, endScriptType);
				
				var startScriptCode = lovProvider.indexOf("<SCRIPT>") + "<SCRIPT>".length;
				var endScriptCode = lovProvider.indexOf("</SCRIPT>");
				var scriptCode = lovProvider.substring(startScriptCode, endScriptCode);

				if (scriptType == "ECMAScript") // saw in ScripDetail.java
				{
					scriptType = "Javascript";
				}					
				else if (scriptType == "groovy") // saw in ScripDetail.java
				{
					scriptType = "Groovy";	
				}											
				
				var lovScriptPanel = Ext.create("Sbi.behavioural.lov.LOVScriptBottomPanel", {});
				lovScriptPanel.create(this.services["getDomains"]);
				
				lovScriptPanel.scriptQuery.setValue(scriptCode);
				lovScriptPanel.scriptTypeCombo.setValue(scriptType);				
				
				/* Disable SAVE button if script code is 
				 * changed (different from the original). */
				lovScriptPanel.scriptQuery.on
				(
					"change", 
					
					function()
					{		
						/* Value for script code of the saved LOV record 
						 * (the one that exists in DB). */						
						var startScriptCode2 = globalScope.lovProvider.value.indexOf("<SCRIPT>") + "<SCRIPT>".length;
						var endScriptCode2 = globalScope.lovProvider.value.indexOf("</SCRIPT>");
						var scriptCode2 = globalScope.lovProvider.value.substring(startScriptCode2, endScriptCode2);
						
						/* Value of the script code that is written in its textarea 
						 * right now (at this very moment). */
						var currentFormScriptCode = lovFormPanel2.items.items[1].value;
						
						/* Value for script type of the saved LOV record 
						 * (the one that exists in DB). */
						var startScriptType2 = globalScope.lovProvider.value.indexOf("<LANGUAGE>") + "<LANGUAGE>".length;
						var endScriptType2 = globalScope.lovProvider.value.indexOf("</LANGUAGE>");
						var scriptType2 = globalScope.lovProvider.value.substring(startScriptType2, endScriptType2);
												
						if (scriptType2 == "ECMAScript") // saw in ScripDetail.java
						{
							scriptType2 = "Javascript";
						}					
						else if (scriptType2 == "groovy") // saw in ScripDetail.java
						{
							scriptType2 = "Groovy";	
						}	
						
						/* Value of the script type that is chosen in its combobox 
						 * right now (at this very moment). */
						var currentFormScriptType = lovFormPanel2.items.items[0].value;
						
						/* In case that current script code in LOV form is different
						 * form the one saved in DB (existing LOV record) hide Save
						 * button, as well as Test result page. */
						if (currentFormScriptCode != scriptCode2 || currentFormScriptType != scriptType2)
						{
							globalScope.getComponent("TOOLBAR").items.items[3].hide();
							globalScope.tabPanel.tabBar.items.items[1].hide();
						}	
						else
						{
							globalScope.getComponent("TOOLBAR").items.items[3].show();
						}								
					}
				);
				
				/* Disable SAVE button if script type is changed (different from the original). */
				lovScriptPanel.scriptTypeCombo.on
				(
					"change", 
					
					function()
					{
						/* Value for script type of the saved LOV record 
						 * (the one that exists in DB). */
						var startScriptType2 = globalScope.lovProvider.value.indexOf("<LANGUAGE>") + "<LANGUAGE>".length;
						var endScriptType2 = globalScope.lovProvider.value.indexOf("</LANGUAGE>");
						var scriptType2 = globalScope.lovProvider.value.substring(startScriptType2, endScriptType2);
						
						/* Value for script code of the saved LOV record 
						 * (the one that exists in DB). */						
						var startScriptCode2 = globalScope.lovProvider.value.indexOf("<SCRIPT>") + "<SCRIPT>".length;
						var endScriptCode2 = globalScope.lovProvider.value.indexOf("</SCRIPT>");
						var scriptCode2 = globalScope.lovProvider.value.substring(startScriptCode2, endScriptCode2);
						
						/* Value of the script code that is written in its textarea 
						 * right now (at this very moment). */
						var currentFormScriptCode = lovFormPanel2.items.items[1].value;
						
						if (scriptType2 == "ECMAScript") // saw in ScripDetail.java
						{
							scriptType2 = "Javascript";
						}					
						else if (scriptType2 == "groovy") // saw in ScripDetail.java
						{
							scriptType2 = "Groovy";	
						}	
						
						/* Value of the script type that is chosen in its combobox 
						 * right now (at this very moment). */
						var currentFormScriptType = lovFormPanel2.items.items[0].value;
						
						/* In case that current script type in LOV form is different
						 * form the one saved in DB (existing LOV record) hide Save
						 * button, as well as Test result page. */
						if (currentFormScriptType != scriptType2 || currentFormScriptCode != scriptCode2)
						{
							globalScope.getComponent("TOOLBAR").items.items[3].hide();
							globalScope.tabPanel.tabBar.items.items[1].hide();
						}	
						else
						{
							globalScope.getComponent("TOOLBAR").items.items[3].show();
						}	
					}
				);
				
				lovFormPanel2.add(lovScriptPanel.scriptTypeCombo);
				lovFormPanel2.add(lovScriptPanel.scriptQuery);
			}

			else if (this.lovInputTypeCombo.value == "FIX_LOV") 
			{
				lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.fixedListWizard'));

				var lovFixedListPanel = Ext.create("Sbi.behavioural.lov.LOVFixedListBottomPanel", {});
				lovFixedListPanel.create();

				var fixLovStore = lovFixedListPanel.fixLovStore;

				var startFixLov = lovProvider.indexOf("<ROWS>") + "<ROWS>".length;
				var endFixLov = lovProvider.indexOf("</ROWS>");
				var fixLovRows = lovProvider.substring(startFixLov, endFixLov);
				var listRows = fixLovRows.split("<ROW ");

				fixLovStore.removeAll();
				    				
				for (var i = 1; i < listRows.length; i++) 
				{
					var valueStart = listRows[i].indexOf("VALUE=") + "VALUE=".length + 1;
					var valueEnd = listRows[i].indexOf("\" DESCRIPTION");
					var valueFixLov = listRows[i].substring(valueStart, valueEnd);

					var descriptionStart = listRows[i].indexOf("DESCRIPTION=") + "DESCRIPTION=".length + 1;
					var descriptionEnd = listRows[i].indexOf("/>") - 1;
					var descriptionFixLov = listRows[i].substring(descriptionStart, descriptionEnd);

					fixLovStore.insert(i, {
						value: valueFixLov,
						description : descriptionFixLov
					});
				}

				var lovFixedListForm = lovFixedListPanel.lovFixedListForm;
				var fixLovGrid = lovFixedListPanel.fixLovGrid;

				lovFixedListForm.getComponent("FixLovValue").setValue("");
				lovFixedListForm.getComponent("FixLovDescription").setValue("");

				fixLovGrid.reconfigure(fixLovStore);
				fixLovGrid.update();
				
				/* Disable SAVE button if any item in fixed list grid is changed - modified. 
				 * Not enabled to retrieve Save button if grid is returned into original
				 * state (with original (starting) values for all items present in grid).
				 * Any item from any column in fix LOV grid will fire this event.*/
				lovFixedListPanel.fixLovGrid.on
				(
					"changedGridItemsValue", 
					
					function()
					{
						globalScope.getComponent("TOOLBAR").items.items[3].hide();
						globalScope.tabPanel.tabBar.items.items[1].hide();
					}
				);
				
				/* Also, disable SAVE button if Add button is successfully executed
				 * (parameters of the LOV fix form are well defined) and new row is 
				 * added to the grid. */
				lovFixedListPanel.lovFixedListForm.on
				(
					"addButtonPressed", 
					
					function() 
					{
						globalScope.getComponent("TOOLBAR").items.items[3].hide();
						globalScope.tabPanel.tabBar.items.items[1].hide();
					}
				);
				
				/* Disable SAVE button if Delete (Remove) button is pressed and
				 * record is removed from the fixed list grid. */
				lovFixedListPanel.fixLovGrid.on
				(
					"itemRemovedFromTheGrid", 
					
					function() 
					{
						globalScope.getComponent("TOOLBAR").items.items[3].hide();
						globalScope.tabPanel.tabBar.items.items[1].hide();
					}
				);				
				
				lovFormPanel2.add(lovFixedListForm);
				lovFormPanel2.add(lovFixedListPanel.infoPanel);
				lovFormPanel2.add(fixLovGrid);
			}

			else if (this.lovInputTypeCombo.value == "JAVA_CLASS") 
			{
				lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.javaClassWizard'));

				var lovJavaClassPanel = Ext.create("Sbi.behavioural.lov.LOVJavaClassBottomPanel", {});
				lovJavaClassPanel.create();

				var startJavaClassName = lovProvider.indexOf("<JAVA_CLASS_NAME>") + "<JAVA_CLASS_NAME>".length;
				var endJavaClassName = lovProvider.indexOf("</JAVA_CLASS_NAME>");
				var javaClassName = lovProvider.substring(startJavaClassName, endJavaClassName);

				lovJavaClassPanel.javaClassName.setValue(javaClassName);
				
				lovJavaClassPanel.javaClassName.on
				(
					"change",
					
					function()
					{
						/* Value for script code of the saved LOV record 
						 * (the one that exists in DB). */						
						var startJavaClassName2 = globalScope.lovProvider.value.indexOf("<JAVA_CLASS_NAME>") + "<JAVA_CLASS_NAME>".length;
						var endJavaClassName2 = globalScope.lovProvider.value.indexOf("</JAVA_CLASS_NAME>");
						var javaClassName2 = globalScope.lovProvider.value.substring(startJavaClassName2, endJavaClassName2);
						
						/* Value of the script code that is written in its textarea 
						 * right now (at this very moment). */
						var currentJavaClassName = lovFormPanel2.items.items[0].value;
						
						/* In case that current script code in LOV form is different
						 * form the one saved in DB (existing LOV record) hide Save
						 * button, as well as Test result page. */
						if (currentJavaClassName != javaClassName2)
						{
							globalScope.getComponent("TOOLBAR").items.items[3].hide();
							globalScope.tabPanel.tabBar.items.items[1].hide();
						}	
						else
						{
							globalScope.getComponent("TOOLBAR").items.items[3].show();
						}	
					}
				);

				lovFormPanel2.add(lovJavaClassPanel.javaClassName);
			}

			else if (this.lovInputTypeCombo.value == "DATASET") 
			{
				lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.datasetWizard'));

				var lovDatasetPanel = Ext.create("Sbi.behavioural.lov.LOVDatasetBottomPanel", {});
				lovDatasetPanel.create(globalScope.services["datasets"]);

				var startDatasetId = lovProvider.indexOf("<ID>") + "<ID>".length;
				var endDatasetId = lovProvider.indexOf("</ID>");
				var datasetId = lovProvider.substring(startDatasetId, endDatasetId);

				var startDatasetLabel = lovProvider.indexOf("<LABEL>") + "<LABEL>".length;
				var endDatasetLabel = lovProvider.indexOf("</LABEL>");
				var datasetLabel = lovProvider.substring(startDatasetLabel, endDatasetLabel);
								
				lovDatasetPanel.datasetWindow.on
				(
					"hide",
					
					function()
					{
						/* Value for script code of the saved LOV record 
						 * (the one that exists in DB). */						
						var startDatasetLabel2 = globalScope.lovProvider.value.indexOf("<LABEL>") + "<LABEL>".length;
						var endDatasetLabel2 = globalScope.lovProvider.value.indexOf("</LABEL>");
						var datasetLabel2 = globalScope.lovProvider.value.substring(startDatasetLabel2, endDatasetLabel2);
												
						/* Value of the script code that is written in its textarea 
						 * right now (at this very moment). */
						
						var currentDatasetLabel = lovDatasetPanel.datasetForm.items.items[0].items.items[0].value;
//						
//						console.log(datasetLabel2);
//						console.log(currentDatasetLabel);
						
						/* In case that current script code in LOV form is different
						 * form the one saved in DB (existing LOV record) hide Save
						 * button, as well as Test result page. */
						if (currentDatasetLabel != datasetLabel2)
						{
							globalScope.getComponent("TOOLBAR").items.items[3].hide();
							globalScope.tabPanel.tabBar.items.items[1].hide();
						}	
						else
						{
							globalScope.getComponent("TOOLBAR").items.items[3].show();
						}												
					}
				);

				Ext.define("DatasetModel",

				{
					extend : 'Ext.data.Model',
					fields : [ "id", "name", "description", "label" ]
				// fields (labels) from JSON that comes from server that we call
				});

				this.datasetStore = Ext.create('Ext.data.Store',

				{
					model : "DatasetModel",
					autoLoad : true,

					proxy : {
						type : 'rest',

						url : globalScope.services["datasets"],

						reader : {
							type : "json",
							root : "root"
						}
					}
				});

				this.datasetStore.on('load',

				function(datasetStore) {
					//Sbi.debug('[INFO] Dataset store loaded (DATASET)');
					
					var datasetByIdFromDB = datasetStore.getById(Number(datasetId));
					
					if (datasetByIdFromDB == null || datasetByIdFromDB == undefined)
					{
						Sbi.exception.ExceptionHandler.
							showWarningMessage("Dataset specified for this LOV record does not exist");
					}
					else
					{
						var datasetLabelInDB = datasetByIdFromDB.data.label; // label value for Dataset of this ID

						lovDatasetPanel.datasetForm.items.items[0].items.items[0].setValue(datasetLabelInDB);
						lovDatasetPanel.datasetForm.items.items[0].items.items[1].setValue(datasetId);

						lovFormPanel2.add(lovDatasetPanel.datasetForm);
					}					
				});
			}

			lovFormPanel2.show();
		}

		else // LOV ID = 0
		{
			lovFormPanel2.hide();
			
			/* Hide the Save button */
			globalScope.getComponent("TOOLBAR").items.items[3].hide();

			lovFormPanel1.items.items[0].setValue(0);
			// LOV LABEL
			lovFormPanel1.items.items[1].setValue("");
			// LOV NAME
			lovFormPanel1.items.items[2].setValue("");
			// LOV DESCRIPTION
			lovFormPanel1.items.items[3].setValue("");
			// LOV PROVIDER
			lovFormPanel1.items.items[4].setValue("");
			

			// LOV INPUT TYPE
			this.lovInputTypeCombo.setValue("");
			this.lovInputTypeCombo.markInvalid(LN('sbi.behavioural.lov.details.inputTypeMissing'));
		}

		// Sbi.debug('[OUT] LOVDetailPanel - setFormState()');
	},
	
	getFormState : function(phaseOfLov) 
	{
		Sbi.debug('[IN] LOVDetailPanel - getFormState()');

		/* Take the values of the textfields in the Panel 1 */
		var lovId = this.lovId.value;
		var lovName = this.lovName.value;
		var lovDescription = this.lovDescription.value;
		var lovLabel = this.lovLabel.value;		
		var oldLovProvider = this.lovProvider.value;

		var lovFormPanel2 = this.tabPanel.items.items[0].getComponent("PANEL2");
		
		var finalLovProvider = "";
		var newlovProvider = "";
		
		var lovRecordCanBeSaved = true;

		if (this.lovInputTypeCombo.getValue() == "QUERY") 
		{
			var inputTypeCd = "QUERY";
			var inputTypeId = 1;
			
			var startDataSource = oldLovProvider.indexOf("<CONNECTION>") + "<CONNECTION>".length;
			var endDataSource = oldLovProvider.indexOf("</CONNECTION>");
			var oldDataSource = oldLovProvider.substring(startDataSource, endDataSource);

			var startStatement = oldLovProvider.indexOf("<STMT>") + "<STMT>".length;
			var endStatement = oldLovProvider.indexOf("</STMT>");
			var oldStatement = oldLovProvider.substring(startStatement, endStatement);

			var currentDataSource = lovFormPanel2.items.items[0].value;
			var currentQuery = lovFormPanel2.items.items[1].value;

			if (phaseOfLov == "testPhase" || phaseOfLov == "savePhase") 
			{						
				if (phaseOfLov == "savePhase" || lovId == 0 || oldDataSource != currentDataSource || oldStatement != currentQuery
						|| this.lovProvider.value.indexOf(this.lovInputTypeCombo.value) < 0)
				{
					finalLovProvider = "<QUERY>" + "<CONNECTION>" + currentDataSource + "</CONNECTION>" + "<STMT>" + currentQuery + "</STMT>" + "<VALUE-COLUMN>"
					+ "</VALUE-COLUMN>" + "<DESCRIPTION-COLUMN>" + "</DESCRIPTION-COLUMN>" + "<VISIBLE-COLUMNS>" + "</VISIBLE-COLUMNS>"
					+ "<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" + "<LOVTYPE>" + "</LOVTYPE>" + "<TREE-LEVELS-COLUMNS>" + "</TREE-LEVELS-COLUMNS>"
					+ "</QUERY>";
				}
				else
				{
					finalLovProvider = oldLovProvider;	
				}				
			}
			
			else if (phaseOfLov == "finalSave")
			{
				finalLovProvider = this.lovProvider.value;
			}		
		}

		else if (this.lovInputTypeCombo.getValue() == "SCRIPT") 
		{
			var inputTypeCd = "SCRIPT";
			var inputTypeId = 2;

			var startScriptType = oldLovProvider.indexOf("<LANGUAGE>") + "<LANGUAGE>".length;
			var endScriptType = oldLovProvider.indexOf("</LANGUAGE>");
			var oldScriptType = oldLovProvider.substring(startScriptType, endScriptType);

			var startScriptCode = oldLovProvider.indexOf("<SCRIPT>") + "<SCRIPT>".length;
			var endScriptCode = oldLovProvider.indexOf("</SCRIPT>");
			var oldScriptCode = oldLovProvider.substring(startScriptCode, endScriptCode);
			
			/* Script type (JavaScript or Groovy) from the combobox (current value) 
			 * and script code from the textarea (current value). */
			var currentScriptType = lovFormPanel2.items.items[0].value;
			var currentScriptCode = lovFormPanel2.items.items[1].value;
									
			if (currentScriptType == "Javascript")
			{
				currentScriptType = "ECMAScript";
			}				
			else if (currentScriptType == "Groovy")
			{
				currentScriptType = "groovy";
			}				
			
			if (phaseOfLov == "testPhase" || phaseOfLov == "savePhase")
			{									
				if (phaseOfLov == "savePhase" || lovId == 0 || oldScriptType!=currentScriptType || oldScriptCode!=currentScriptCode
						|| oldLovProvider.indexOf(this.lovInputTypeCombo.value) < 0)
				{						
					finalLovProvider = "<SCRIPTLOV>" + "<SCRIPT>" + currentScriptCode + "</SCRIPT>" + "<VALUE-COLUMN>" + "</VALUE-COLUMN>" + "<DESCRIPTION-COLUMN>"
					+ "</DESCRIPTION-COLUMN>" + "<VISIBLE-COLUMNS>" + "</VISIBLE-COLUMNS>" + "<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" + "<LANGUAGE>"
					+ currentScriptType + "</LANGUAGE>" + "<LOVTYPE>" + "</LOVTYPE>" + "<TREE-LEVELS-COLUMNS>" + "</TREE-LEVELS-COLUMNS>" + "</SCRIPTLOV>";
				}
				else 
				{						
					finalLovProvider = oldLovProvider;					
				}
			}	
			
			else if (phaseOfLov == "finalSave")
			{
				finalLovProvider = this.lovProvider.value;
			}
		}

		// TODO: Fix this part! Simplify everything that can be simplified.
		else if (this.lovInputTypeCombo.getValue() == "FIX_LOV") 
		{
			var inputTypeCd = "FIX_LOV";
			var inputTypeId = 3;

			var oldLovProviderN = this.lovProvider.value;

			var fixLovStore = lovFormPanel2.items.items[2].getStore();

			if (phaseOfLov == "testPhase" && lovId == 0 || oldLovProviderN.indexOf("FIXLISTLOV") < 0) // Test phase
			{
				finalLovProvider = "<FIXLISTLOV>";
				finalLovProvider += "<ROWS>";

				var fixLovCount = fixLovStore.getCount();

				var fixLovValue = "";
				var fixLovDescription = "";

				for (var i = 0; i < fixLovCount; i++) {
					fixLovValue = fixLovStore.data.items[i].data.value;
					fixLovDescription = fixLovStore.data.items[i].data.description;

					finalLovProvider += "<ROW" + " VALUE=\"" + fixLovValue + "\"" + " DESCRIPTION=\"" + fixLovDescription + "\"" + "/>";
				}

				finalLovProvider += "</ROWS>";
				finalLovProvider += "<VALUE-COLUMN>" + "</VALUE-COLUMN>" + "<DESCRIPTION-COLUMN>" + "</DESCRIPTION-COLUMN>" + "<VISIBLE-COLUMNS>"
						+ "</VISIBLE-COLUMNS>" + "<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" + "<LOVTYPE>" + "</LOVTYPE>" + "<TREE-LEVELS-COLUMNS>"
						+ "</TREE-LEVELS-COLUMNS>" + "</FIXLISTLOV>";

//				if (lovId == 0 || oldLovProviderN.indexOf("FIXLISTLOV") < 0)
//					this.lovProvider.value = finalLovProvider;
			}

			else 
			{
				if (oldLovProviderN.indexOf("FIXLISTLOV") > -1) 
				{
					var oldLovProvider = this.lovProvider.value;
					var newFixLovProvider = "";

					var startFixLovProv = oldLovProvider.indexOf("<FIXLISTLOV>") + "<FIXLISTLOV>".length;
					var endFixLovProv = oldLovProvider.indexOf("<VALUE-COLUMN>");

					var fixLovProvider = oldLovProvider.substring(startFixLovProv, endFixLovProv);

					newFixLovProvider = "<ROWS>";

					var fixLovCount = fixLovStore.getCount();

					var fixLovValue = "";
					var fixLovDescription = "";

					for (var i = 0; i < fixLovCount; i++) {
						fixLovValue = fixLovStore.data.items[i].data.value;
						fixLovDescription = fixLovStore.data.items[i].data.description;

						newFixLovProvider += "<ROW" + " VALUE=\"" + fixLovValue + "\"" + " DESCRIPTION=\"" + fixLovDescription + "\"" + "/>";
					}

					newFixLovProvider += "</ROWS>";

					finalLovProvider = oldLovProvider.substring(0, startFixLovProv) + newFixLovProvider
							+ oldLovProvider.substring(endFixLovProv, oldLovProvider.length);
				}
			}

		}

		else if (this.lovInputTypeCombo.getValue() == "JAVA_CLASS") 
		{
			var inputTypeCd = "JAVA_CLASS";
			var inputTypeId = 4;

			/* Take the string value for path to the targeted Java class from 
			 * the Java class path text box. */
			var newJavaClassName = lovFormPanel2.items.items[0].value;			
			
			var startJavaClassName = oldLovProvider.indexOf("<JAVA_CLASS_NAME>") + "<JAVA_CLASS_NAME>".length;
			var endJavaClassName = oldLovProvider.indexOf("</JAVA_CLASS_NAME>");
			var oldJavaClassName = oldLovProvider.substring(startJavaClassName, endJavaClassName);

			if (phaseOfLov == "testPhase" || phaseOfLov == "savePhase") 
			{				
				if (phaseOfLov == "savePhase" || lovId == 0 || oldJavaClassName != newJavaClassName || this.lovProvider.value.indexOf(this.lovInputTypeCombo.value) < 0)
				{
					finalLovProvider = "<JAVACLASSLOV>" + "<JAVA_CLASS_NAME>" + newJavaClassName + "</JAVA_CLASS_NAME>" + "<VISIBLE-COLUMNS>" + "</VISIBLE-COLUMNS>"
					+ "<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" + "<LOVTYPE>" + "</LOVTYPE>" + "<VALUE-COLUMN>" + "</VALUE-COLUMN>"
					+ "<DESCRIPTION-COLUMN>" + "</DESCRIPTION-COLUMN>" + "</JAVACLASSLOV>";
				}	
				else
				{
					finalLovProvider = oldLovProvider;		
				}
			}			

			else if (phaseOfLov == "finalSave")
			{
				finalLovProvider = this.lovProvider.value;
			}
		}

		else if (this.lovInputTypeCombo.getValue() == "DATASET") 
		{
			var inputTypeCd = "DATASET";
			var inputTypeId = 5;

			var datasetForm = lovFormPanel2.getComponent("DATASET_FORM");
			
			var datasetFormItems = datasetForm.items.items[0];			

			var datasetLabel = datasetFormItems.items.items[0].value;
			var datasetId = datasetFormItems.items.items[1].value;

			if (phaseOfLov == "testPhase" && lovId == 0 || this.lovProvider.value.indexOf(this.lovInputTypeCombo.value) < 0) {
				finalLovProvider = "<DATASET>" + "<ID>" + datasetId + "</ID>" + "<LABEL>" + datasetLabel + "</LABEL>" + "<VISIBLE-COLUMNS>" + "</VISIBLE-COLUMNS>"
						+ "<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" + "<LOVTYPE>" + "</LOVTYPE>" + "<VALUE-COLUMN>" + "</VALUE-COLUMN>"
						+ "<DESCRIPTION-COLUMN>" + "</DESCRIPTION-COLUMN>" + "</DATASET>";

				this.lovProvider.value = finalLovProvider;
			}

			else {
				finalLovProvider = this.lovProvider.value;
			}			
		}

		var selectionType = "";

		var modelOfCurrentLov = Ext.create("Sbi.behavioural.lov.LOVModel",

		{
			LOV_ID : lovId,
			LOV_NAME : lovName,
			LOV_DESCRIPTION : lovDescription,
			LOV_PROVIDER : finalLovProvider,
			I_TYPE_CD : inputTypeCd,
			I_TYPE_ID : inputTypeId,
			LOV_LABEL : lovLabel,
			SELECTION_TYPE : selectionType
		});
		
		Sbi.debug('[OUT] LOVDetailPanel - getFormState()');
		
		return modelOfCurrentLov;
	}

}

);