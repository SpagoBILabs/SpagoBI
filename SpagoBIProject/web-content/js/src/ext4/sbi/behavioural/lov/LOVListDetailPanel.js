/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */
 
   
Ext.define
(
	"Sbi.behavioural.lov.LOVListDetailPanel", 
	
	{
		extend: 'Sbi.widgets.compositepannel.ListDetailPanel',

		config: 
		{
			stripeRows: true,
			modelName: "Sbi.behavioural.lov.LOVModel"
		},

		constructor: function(config) 
		{		
			var isSuperadmin = config.isSuperadmin;
			
			this.services =[];
			this.initServices();
						
			this.detailPanel =  Ext.create
			(
				"Sbi.behavioural.lov.LOVDetailPanel",
				
				{
					services: this.services, 
					isSuperadmin: isSuperadmin 
				}
			);
					
			// Hide Save button
			this.detailPanel.getComponent("TOOLBAR").items.items[3].hide();	
			
			this.columns = 
			[
			 	{ dataIndex:"LOV_NAME", 		header:LN('sbi.generic.label') }, 
			 	{ dataIndex:"LOV_DESCRIPTION", 	header:LN('sbi.generic.descr') }
		 	];
			
			this.fields = ["LOV_ID", "LOV_NAME", "LOV_DESCRIPTION", "INPUT_TYPE_COMBOBOX"];
			
			this.detailPanel.on("save", this.checkCanSave, this);
			this.detailPanel.on("test", this.checkCanTest, this);
			
			this.filteredProperties = ["LOV_NAME"];
			
			this.buttonToolbarConfig = 
			{
				newButton: true
			};
			
			this.buttonColumnsConfig =
			{
				deletebutton: true
			};
	
			this.callParent(arguments);
		},
		
		initServices: function(baseParams)
		{
			this.services["test"]= Sbi.config.serviceRegistry.getRestServiceUrl({
				serviceName: 'LOV/Test'
					, baseParams: baseParams
			});
			
			this.services["getDomains"]= Sbi.config.serviceRegistry.getRestServiceUrl
			(
				{
					serviceName: 'domains/listValueDescriptionByType', 
					baseParams: baseParams
				}
			);
			
			this.services["getDataSources"] = Sbi.config.serviceRegistry.getRestServiceUrl
			(
				{
					serviceName: 'datasources', 
					baseParams: baseParams
				}
			);	
		
			this.services["LOV"] = Sbi.config.serviceRegistry.getRestServiceUrl
			(
				{
					serviceName: 'LOV', 
					baseParams: baseParams
				}
			);	
		},
		
		checkCanTest: function(record)
		{
			console.log("[IN] checkCanTest() LOVListDetailPanel");
			
			/* We need to check if the LOV form is filled with the necessary data
			 * in both cases: creating a new record or modifying the existing one */
			
			var currentPanel = this;
			var canBeSaved = false;
			
			if (record.LOV_LABEL == "")
			{
				Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.labelMissing'));
			}
			else if (record.LOV_NAME == "")
			{
				Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.nameMissing'));
			}
			else if (record.I_TYPE_CD == "")
			{
				Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.inputTypeMissing'));
			}
			else
			{				
				if (record.I_TYPE_CD == "QUERY" && record.DATASOURCE_ID == "" || record.DATASOURCE_ID == null)
					Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.dataSourceMissing'));
				else if (record.I_TYPE_CD == "SCRIPT" && record.SCRIPT_TYPE == "")
					Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.scriptTypeMissing'));
				else
				{
					// ?????? MOZE LI JEDNOSTAVNIJE ???????
					if (this.detailPanel.getComponent("TAB_PANEL_RESULTS").getActiveTab().getComponent("PANEL2").items.items[1].value != "")
					{
						canBeSaved = true;
					}
					else
					{
						Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.queryDescriptionMissing'));
					}
				}						
			}
			
			if (canBeSaved == true)
			{					
				console.log("[checkCanTest - canBeSaved]");
				this.openTestPage();
			}
			
			console.log("[OUT] checkCanTest() LOVListDetailPanel");
		},
		
		openTestPage: function()
		{
			console.log("[IN] openTestPage() LOVListDetailPanel");
			
			var lovConfig = {};	
			
			var contextName = 'SpagoBI'; 
			
			var lovId = this.detailPanel.getFormState("testPhase").data.LOV_ID;						
			var lovProvider = this.detailPanel.getFormState("testPhase").data.LOV_PROVIDER;
			
			if (lovId == 0)
			{
				// The new record
				lovConfig.descriptionColumnName =  'null'; 		// treba da bude null
				lovConfig.valueColumnName =  'null';			// treba da bude null
				lovConfig.visibleColumnNames = '[]';			// treba da bude []				
				lovConfig.lovType =  'simple';				
				lovConfig.treeColumnNames = 'null';	
			}
			else
			{				
				var startDescrColumnName = lovProvider.indexOf("<DESCRIPTION-COLUMN>")+"<DESCRIPTION-COLUMN>".length;
				var endDescrColumnName = lovProvider.indexOf("</DESCRIPTION-COLUMN>");
				lovConfig.descriptionColumnName = lovProvider.substring(startDescrColumnName,endDescrColumnName);
				//console.log(lovConfig.descriptionColumnName);
				
				var startValueColumnName = lovProvider.indexOf("<VALUE-COLUMN>")+"<VALUE-COLUMN>".length;
				var endValueColumnName = lovProvider.indexOf("</VALUE-COLUMN>");
				lovConfig.valueColumnName = lovProvider.substring(startValueColumnName,endValueColumnName);
				//console.log(lovConfig.valueColumnName);
				
				var startVisibleColumnName = lovProvider.indexOf("<VISIBLE-COLUMNS>")+"<VISIBLE-COLUMNS>".length;
				var endVisibleColumnName = lovProvider.indexOf("</VISIBLE-COLUMNS>");
				lovConfig.visibleColumnNames = "[]";
				lovConfig.visibleColumnNames = lovProvider.substring(startVisibleColumnName,endVisibleColumnName);
				//console.log("-*-*-*-*-*-*-*");
				//console.log(lovConfig.visibleColumnNames);
				
				var startLovType = lovProvider.indexOf("<LOVTYPE>")+"<LOVTYPE>".length;
				var endLovType = lovProvider.indexOf("</LOVTYPE>");
				lovConfig.lovType = lovProvider.substring(startLovType,endLovType);
				//console.log(lovConfig.lovType);
				
				var startTreeColumnNames = lovProvider.indexOf("<TREE-LEVELS-COLUMNS>")+"<TREE-LEVELS-COLUMNS>".length;
				var endTreeColumnNames  = lovProvider.indexOf("</TREE-LEVELS-COLUMNS>");
				lovConfig.treeColumnNames = lovProvider.substring(startTreeColumnNames,endTreeColumnNames);
				//console.log(lovConfig.treeColumnNames);
			}		
																		
			this.detailPanel.updatePanel(contextName, lovConfig, lovProvider);
			
			this.detailPanel.setValues();
			
			// Show Test test page tab and set it as active
			
			// ?????? MOZE LI JEDNOSTAVNIJE ???????
			this.detailPanel.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].show();
			this.detailPanel.getComponent("TAB_PANEL_RESULTS").setActiveTab(1);
			
			// Show Save button now
			this.detailPanel.getComponent("TOOLBAR").items.items[3].show();
			
			console.log("[OUT] openTestPage() LOVListDetailPanel");
		},
		
		checkCanSave: function(record)
		{
			console.log("[IN] checkCanSave() LOVListDetailPanel");
			
			/* Calling chained methods in order to get data from Configuration Panel
			 * that are needed to fill the missing data in LOV provider XML query. */			
			var returnLovValues = this.detailPanel.takeValues();
			
			if (returnLovValues != null && returnLovValues != undefined)
			{
				var incompleteLovProvider = this.detailPanel.getFormState("savePhase").data.LOV_PROVIDER;
				
				var startDataSource = incompleteLovProvider.indexOf("<CONNECTION>")+"<CONNECTION>".length;
				var endDataSource = incompleteLovProvider.indexOf("</CONNECTION>");
				var dataSource = incompleteLovProvider.substring(startDataSource,endDataSource);
				
				var startStatement = incompleteLovProvider.indexOf("<STMT>")+"<STMT>".length;
				var endStatement = incompleteLovProvider.indexOf("</STMT>");
				var statement = incompleteLovProvider.substring(startStatement,endStatement);
				
				var valueColumn = returnLovValues.valueColumnName.valueOf();
				var descriptionColumn = returnLovValues.descriptionColumnName.valueOf();
	
				var visibleColumns = "";
				var visibleColumnNames = returnLovValues.visibleColumnNames.valueOf();
	
				for (var i=0; i<visibleColumnNames.length; i++)
				{
					if (i==0)
					{
						visibleColumns = visibleColumnNames[i];
					}
					else
					{
						visibleColumns += "," + visibleColumnNames[i];
					}
				}
				
				// ?????????????????????????????????????????????
				// I dont't know what is INVISIBLE-COLUMNS ????
				// ?????????????????????????????????????????????
				var allColumns = returnLovValues.column.valueOf();
				var invisibleColumns = allColumns[0];
				
				var lovType = returnLovValues.lovType.valueOf();
				
				var treeLevelsColumns = "";
				
				for (var i=1; i<allColumns.length; i++)
				{
					if (i==1)
					{
						treeLevelsColumns = allColumns[i];
					}
					else
					{
						treeLevelsColumns += "," + allColumns[i];
					}
				}
				
				var completeLovProvider = 
					"<QUERY>" + 
						"<CONNECTION>" + dataSource + "</CONNECTION>" + 
						"<STMT>" + statement + "</STMT>" + 
						"<VALUE-COLUMN>" + valueColumn + "</VALUE-COLUMN>" + 
						"<DESCRIPTION-COLUMN>" + descriptionColumn + "</DESCRIPTION-COLUMN>" +
						"<VISIBLE-COLUMNS>" + visibleColumns + "</VISIBLE-COLUMNS>" + 
						"<INVISIBLE-COLUMNS>" + invisibleColumns + "</INVISIBLE-COLUMNS>" + 
						"<LOVTYPE>" + lovType + "</LOVTYPE>" + 
						"<TREE-LEVELS-COLUMNS>" + treeLevelsColumns + "</TREE-LEVELS-COLUMNS>"
					+ "</QUERY>";
				
				//console.log(completeLovProvider);
				
				//this.detailPanel.getFormState().data.LOV_PROVIDER = completeLovProvider;
				this.detailPanel.lovProvider.value = completeLovProvider;
				record.LOV_PROVIDER = completeLovProvider;
				
				this.onFormSave(record);
			}
			
			console.log("[OUT] checkCanSave() LOVListDetailPanel");
		},	
		
		onFormSave: function(record)
		{			
			console.log("[IN] onFormSave() LOVListDetailPanel");
			
			this.detailPanel.getFormState("savePhase").save
			(
				{
					success: function(object, response, options)
							{			
								console.log("SAVE PROVERA GRESKE");
								console.log(response);
						
								if(response !== undefined && response.response !== undefined && response.response.responseText !== undefined && response.response.statusText=="OK") 
								{									
									response = response.response;
									
									if(response.responseText!=null && response.responseText!=undefined)
									{										
										if(response.responseText.indexOf("error.mesage.description")>=0)
										{
											Sbi.exception.ExceptionHandler.handleFailure(response);
										}
										else if (response.responseText.toLowerCase().indexOf("error") >= 0)
										{
											console.log("GRESKA SAVE");
											
											var selectedRecord = this.grid.getSelectionModel().getSelection();	
											this.grid.store.remove(selectedRecord);
											this.grid.store.commitChanges();
											
											/* UMESTO hide() BI BILO BOLJE DA SE VRATIMO NA FORMU 
											 * SA IZBRISANIM PODACIMA IZ POLJA - OVO CE SE DESITI
											 * KADA POKUSAMO DA DUPLIRAMO LOV - PO LABELI ISTI NAZIVI. */
											this.detailPanel.hide();
											
//											this.grid.store.remove(record);
//											console.log(record);
//											this.grid.store.sync();
//											//this.grid.store.commitChanges();
//											
//											this.grid.getView().refresh();
											
											Sbi.exception.ExceptionHandler.handleFailure(response);											
										}
										else
										{
											// When there is no error in message - successfully saved record
											Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.behavioural.lov.saved'));
											var selectedRow = this.grid.getSelectionModel().getSelection();
											
											
											
											// Ext.apply() - Copies all the properties of config to the specified object
											
											selectedRow[0].data = Ext.apply(selectedRow[0].data, record);	
											selectedRow[0].raw = Ext.apply(selectedRow[0].raw, record);											
											
											selectedRow[0].data.LOV_ID = response.responseText;
											
											selectedRow[0].commit();

											this.grid.store.sync();
											this.grid.store.commitChanges() ;
											this.grid.store.loadData(selectedRow[0], true) ;
											
											this.grid.getView().refresh();
											
											this.detailPanel.lovId.setValue(response.responseText);
										}
									}
								}
								else
								{
									Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
								}
							},
					
					failure: Sbi.exception.ExceptionHandler.handleFailure,
							
					scope: this					
				}
			);	
			
			console.log("[OUT] onFormSave() LOVListDetailPanel");
		},
		
		onDeleteRow: function(record)
		{		
			console.log("[IN] onDeleteRow() LOVListDetailPanel");
			
			var selectedRecord = this.grid.getSelectionModel().getSelection();	
			
			var recordToDelete = Ext.create("Sbi.behavioural.lov.LOVModel",record.data);
			
			if (selectedRecord[0] != undefined && recordToDelete.data.LOV_ID == selectedRecord[0].data.LOV_ID)
			{
				this.detailPanel.hide();
			}
			
			if (record.data.LOV_ID == 0)
			{
				this.grid.store.remove(record);
				this.grid.store.commitChanges(); 
			}
			else	
			{			
				recordToDelete.destroy
				(
					{
						appendId:false,
						
						success : function(object, response, options) 
						{
							if(response !== undefined && response.response !== undefined && response.response.responseText !== undefined && response.response.statusText=="OK") 
							{
								var aaa = response;
								
								response = response.response ;
								
								if(response.responseText!=null && response.responseText!=undefined)
								{
									if(response.responseText.indexOf("error.mesage.description")>=0 || 
											response.responseText.indexOf("Error while deleting LOV") >= 0)
									{
										Sbi.exception.ExceptionHandler.handleFailure(aaa);
									}
									else
									{
										Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.datasource.deleted'));
										this.grid.store.remove(record);
										this.grid.store.commitChanges(); 
									}
								}
							} 
							else 
							{
								Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
							}
						},
						
						scope: this,
						
						failure: Sbi.exception.ExceptionHandler.handleFailure      
					}
				);
			}
			
			console.log("[OUT] onDeleteRow() LOVListDetailPanel");
		},
		
		
		onGridSelect: function(selectionrowmodel, record, index, eOpts)
		{
			//console.log("[IN] onGridSelect() LOVListDetailPanel");
			
			this.detailPanel.show();
			
			/* Whenever we click on some record on the left, on the right we should 
			 * show the first tab on the right part of the page */
			this.detailPanel.getComponent("TAB_PANEL_RESULTS").setActiveTab(0);
			
			// A way too hide TEST tab for those records that didn't start the Test button
			this.detailPanel.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].hide();
			// Hide Save button
			this.detailPanel.getComponent("TOOLBAR").items.items[3].hide();			
			
			if (record.data.LOV_ID == null || record.data.LOV_ID == "")
			{	
				this.detailPanel.panel2.hide();
			}
			
			//console.log("*********");
			//console.log(record.data);
			
			this.detailPanel.setFormState(record.data);
			
			//console.log("[OUT] onGridSelect() LOVListDetailPanel");
		}
	}
);