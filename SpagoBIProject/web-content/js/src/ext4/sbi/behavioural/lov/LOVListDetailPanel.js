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
			Sbi.debug('[IN] LOVListDetailPanel - constructor');
			
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
			
			Sbi.debug('[OUT] LOVListDetailPanel - constructor');
		},
		
		initServices: function(baseParams)
		{
			Sbi.debug('[IN] LOVListDetailPanel - initServices()');
			
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
			
			Sbi.debug('[OUT] LOVListDetailPanel - initServices()');
		},
		
		checkCanTest: function(record)
		{
			Sbi.debug('[IN] LOVListDetailPanel - checkCanTest()');
			
			/* We need to check if the LOV form is filled with the necessary data
			 * in both cases: creating a new record or modifying the existing one */
			
			var currentPanel = this;
			var canBeSaved = false;
			
			if (record.LOV_LABEL == "")
			{
				//Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.labelMissing'));
				this.detailPanel.lovLabel.markInvalid(LN('sbi.behavioural.lov.details.labelMissing'));
			}
			else if (record.LOV_NAME == "")
			{
				//Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.nameMissing'));
				this.detailPanel.lovName.markInvalid(LN('sbi.behavioural.lov.details.nameMissing'));				
			}
			else if (record.I_TYPE_CD == "")
			{
				//Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.inputTypeMissing'));
				this.detailPanel.lovInputTypeCombo.markInvalid(LN('sbi.behavioural.lov.details.inputTypeMissing'));					
			}
			else
			{						
				if (record.I_TYPE_CD == "QUERY" && (record.DATASOURCE_ID == "" || record.DATASOURCE_ID == null))
				{
//					Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.dataSourceMissing'));
					this.detailPanel.dataSourceCombo.markInvalid(LN('sbi.behavioural.lov.details.dataSourceMissing'));		
				}
				else if (record.I_TYPE_CD == "SCRIPT" && (record.SCRIPT_TYPE == "" || record.SCRIPT_TYPE == undefined || record.SCRIPT_TYPE == null))
				{
//					Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.scriptTypeMissing'));
					this.detailPanel.scriptTypeCombo.markInvalid(LN('sbi.behavioural.lov.details.scriptTypeMissing'));	
				}
				else
				{					
					var description = null;
						
					if (record.I_TYPE_CD == "QUERY")
						// ?????? Could it be simpler ???
						description = this.detailPanel.getComponent("TAB_PANEL_RESULTS").getActiveTab().getComponent("PANEL2").items.items[1].value;
					else if (record.I_TYPE_CD == "SCRIPT")
						description = this.detailPanel.getComponent("TAB_PANEL_RESULTS").getActiveTab().getComponent("PANEL3").items.items[1].value;
					
					if (description != "" && description != null && description  != undefined)
					{
						canBeSaved = true;
					}
					else
					{						
						if(record.I_TYPE_CD == "QUERY")
						{
//							Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.queryDescriptionMissing'));
							this.detailPanel.dataSourceQuery.markInvalid(LN('sbi.behavioural.lov.details.queryDescriptionMissing'));	
						}
						else if (record.I_TYPE_CD == "SCRIPT")
						{
//							Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.scriptDescriptionMissing'));
							this.detailPanel.scriptQuery.markInvalid(LN('sbi.behavioural.lov.details.scriptDescriptionMissing'));	
						}
					}
					
					if (record.I_TYPE_CD == "FIX_LOV")
					{
						var panel5 = this.detailPanel.getComponent("TAB_PANEL_RESULTS").getActiveTab().getComponent("PANEL5");
						var fixLovGrid = panel5.items.items[1];						
						var numberOfFixLovs = fixLovGrid.getStore().getCount();
						
						if (numberOfFixLovs > 0)
						{
							canBeSaved = true;
						}
						else
						{
							Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.fixLovGridEmpty')); 
						}
					}
				}						
			}
			
			if (canBeSaved == true)
			{					
				this.openTestPage();
			}
			
			Sbi.debug('[OUT] LOVListDetailPanel - checkCanTest()');
		},
		
		openTestPage: function()
		{
			Sbi.debug('[IN] LOVListDetailPanel - openTestPage()');
			
			var lovConfig = {};	
			
			var contextName = 'SpagoBI'; 
			
			var lovId = this.detailPanel.getFormState("testPhase").data.LOV_ID;						
			var lovProvider = this.detailPanel.getFormState("testPhase").data.LOV_PROVIDER;
											
			if (lovId == 0)
			{
				// The new record
				lovConfig.descriptionColumnName =  'null'; 	
				lovConfig.valueColumnName =  'null';			
				lovConfig.visibleColumnNames = '[]';						
				lovConfig.lovType =  'simple';				
				lovConfig.treeColumnNames = 'null';	
			}
			
			else
			{				
				var startDescrColumnName = lovProvider.indexOf("<DESCRIPTION-COLUMN>")+"<DESCRIPTION-COLUMN>".length;
				var endDescrColumnName = lovProvider.indexOf("</DESCRIPTION-COLUMN>");
				lovConfig.descriptionColumnName = lovProvider.substring(startDescrColumnName,endDescrColumnName);
				
				var startValueColumnName = lovProvider.indexOf("<VALUE-COLUMN>")+"<VALUE-COLUMN>".length;
				var endValueColumnName = lovProvider.indexOf("</VALUE-COLUMN>");
				lovConfig.valueColumnName = lovProvider.substring(startValueColumnName,endValueColumnName);
				
				var startVisibleColumnName = lovProvider.indexOf("<VISIBLE-COLUMNS>")+"<VISIBLE-COLUMNS>".length;
				var endVisibleColumnName = lovProvider.indexOf("</VISIBLE-COLUMNS>");
				lovConfig.visibleColumnNames = "[]";
				lovConfig.visibleColumnNames = lovProvider.substring(startVisibleColumnName,endVisibleColumnName);
				
				var startLovType = lovProvider.indexOf("<LOVTYPE>")+"<LOVTYPE>".length;
				var endLovType = lovProvider.indexOf("</LOVTYPE>");
				lovConfig.lovType = lovProvider.substring(startLovType,endLovType);
				
				var startTreeColumnNames = lovProvider.indexOf("<TREE-LEVELS-COLUMNS>")+"<TREE-LEVELS-COLUMNS>".length;
				var endTreeColumnNames  = lovProvider.indexOf("</TREE-LEVELS-COLUMNS>");
				lovConfig.treeColumnNames = lovProvider.substring(startTreeColumnNames,endTreeColumnNames);
			}		
			
			this.detailPanel.updatePanel(contextName, lovConfig, lovProvider);
			
			this.detailPanel.setValues();
			
			// Show Test test page tab and set it as active
			
			// ?????? Simpler ???
			this.detailPanel.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].show();
			this.detailPanel.getComponent("TAB_PANEL_RESULTS").setActiveTab(1);
			
			// Show Save button now
			this.detailPanel.getComponent("TOOLBAR").items.items[3].show();
			
			Sbi.debug('[OUT] LOVListDetailPanel - openTestPage()');
		},
		
		checkCanSave: function(record)
		{
			Sbi.debug('[IN] LOVListDetailPanel - checkCanSave()');
			
			/* Calling chained methods in order to get data from Configuration Panel
			 * that are needed to fill the missing data in LOV provider XML query. */			
			var returnLovValues = this.detailPanel.takeValues();
			
			// Needed for the fix LOVs
			var arrayOfValuesFixLov = [];
			var arrayOfDescripFixLov = [];
			
			if (returnLovValues != null && returnLovValues != undefined)
			{
				var incompleteLovProvider = this.detailPanel.getFormState("savePhase").data.LOV_PROVIDER;				
				
				if (record.I_TYPE_CD == "QUERY")
				{
					var startDataSource = incompleteLovProvider.indexOf("<CONNECTION>")+"<CONNECTION>".length;
					var endDataSource = incompleteLovProvider.indexOf("</CONNECTION>");
					var dataSource = incompleteLovProvider.substring(startDataSource,endDataSource);
					
					var startStatement = incompleteLovProvider.indexOf("<STMT>")+"<STMT>".length;
					var endStatement = incompleteLovProvider.indexOf("</STMT>");
					var statement = incompleteLovProvider.substring(startStatement,endStatement);
				}
				
				else if (record.I_TYPE_CD == "SCRIPT")
				{
					var startScriptType = incompleteLovProvider.indexOf("<LANGUAGE>")+"<LANGUAGE>".length;
	    			var endScriptType = incompleteLovProvider.indexOf("</LANGUAGE>");
	    			var scriptType = incompleteLovProvider.substring(startScriptType,endScriptType);
	    			    			
	    			var startScript = incompleteLovProvider.indexOf("<SCRIPT>")+"<SCRIPT>".length;
	    			var endScript = incompleteLovProvider.indexOf("</SCRIPT>");
	    			var script = incompleteLovProvider.substring(startScript,endScript); 	    			
				}
				
				else if (record.I_TYPE_CD == "FIX_LOV")
				{
					var startFixLov = incompleteLovProvider.indexOf("<ROWS>")+"<ROWS>".length;
        			var endFixLov = incompleteLovProvider.indexOf("</ROWS>");
        			var fixLovRows = incompleteLovProvider.substring(startFixLov,endFixLov);
    				var listRows = fixLovRows.split("<ROW "); 				
    				
    				for (var i=1; i<listRows.length; i++)
					{
    					var valueStart = listRows[i].indexOf("VALUE=")+"VALUE=".length + 1;
        				var valueEnd = listRows[i].indexOf("\" DESCRIPTION");
        				//arrayOfValuesFixLov[i] = listRows[i].substring(valueStart,valueEnd);
        				arrayOfValuesFixLov.push(listRows[i].substring(valueStart,valueEnd));
        				
        				var descriptionStart = listRows[i].indexOf("DESCRIPTION=")+"DESCRIPTION=".length + 1;
        				var descriptionEnd = listRows[i].indexOf("/>")-1;
//        				arrayOfDescripFixLov[i] = listRows[i].substring(descriptionStart,descriptionEnd);      	
        				arrayOfDescripFixLov.push(listRows[i].substring(descriptionStart,descriptionEnd));
					}
    				
				}				
				
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
				// INVISIBLE-COLUMNS ????
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
				
				var completeLovProvider = "";
				
				if (record.I_TYPE_CD == "QUERY")
				{
					completeLovProvider = 
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
				}
				
				else if (record.I_TYPE_CD == "SCRIPT")
				{
					completeLovProvider = 
						"<SCRIPTLOV>" +
		    				"<SCRIPT>" + script + "</SCRIPT>" +	
		    				"<VALUE-COLUMN>" + valueColumn + "</VALUE-COLUMN>" +
		    				"<DESCRIPTION-COLUMN>" + descriptionColumn + "</DESCRIPTION-COLUMN>" +
		    				"<VISIBLE-COLUMNS>" + visibleColumns + "</VISIBLE-COLUMNS>" +
		    				"<INVISIBLE-COLUMNS>" + invisibleColumns + "</INVISIBLE-COLUMNS>" +
		    				"<LANGUAGE>" + scriptType + "</LANGUAGE>" +
		    				"<LOVTYPE>" + lovType + "</LOVTYPE>" +
		    				"<TREE-LEVELS-COLUMNS>" + treeLevelsColumns + "</TREE-LEVELS-COLUMNS>" +
    					"</SCRIPTLOV>";
				}
				
				else if (record.I_TYPE_CD == "FIX_LOV")
				{
					completeLovProvider = "<FIXLISTLOV>";
					completeLovProvider += "<ROWS>";
	    			
	    			var fixLovValue = "";
	    			var fixLovDescription = "";
	    			
	    			
	    			for (var i=0; i<arrayOfValuesFixLov.length; i++)
					{	    				
	    				completeLovProvider += "<ROW" +
						  " VALUE=\"" + arrayOfValuesFixLov[i] + "\"" +
						  " DESCRIPTION=\"" + arrayOfDescripFixLov[i] + "\"" +
						  "/>";
					}
	    			
	    			completeLovProvider += "</ROWS>";
	    			completeLovProvider += "<VALUE-COLUMN>" + valueColumn + "</VALUE-COLUMN>" +
	    					  "<DESCRIPTION-COLUMN>" + descriptionColumn + "</DESCRIPTION-COLUMN>" +
	    					  "<VISIBLE-COLUMNS>" + visibleColumns + "</VISIBLE-COLUMNS>" +
	    					  "<INVISIBLE-COLUMNS>" + invisibleColumns + "</INVISIBLE-COLUMNS>" +
	    					  "<LOVTYPE>"+ lovType + "</LOVTYPE>" +
	    					  "<TREE-LEVELS-COLUMNS>" + treeLevelsColumns + "</TREE-LEVELS-COLUMNS>" +
	    					  "</FIXLISTLOV>";
				}
								
				//this.detailPanel.getFormState().data.LOV_PROVIDER = completeLovProvider;
				this.detailPanel.lovProvider.value = completeLovProvider;
				record.LOV_PROVIDER = completeLovProvider;
				
				this.onFormSave(record);
			}
			
			Sbi.debug('[OUT] LOVListDetailPanel - checkCanSave()');
		},	
		
		onFormSave: function(record)
		{			
			Sbi.debug('[IN] LOVListDetailPanel - onFormSave()');
			
			this.detailPanel.getFormState("savePhase").save
			(
				{
					success: function(object, response, options)
							{									
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
											var selectedRecord = this.grid.getSelectionModel().getSelection();	
											this.grid.store.remove(selectedRecord);
											this.grid.store.commitChanges();
											
											this.detailPanel.hide();
																						
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
											this.grid.store.loadData(selectedRow[0], true);
											
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
			
			Sbi.debug('[OUT] LOVListDetailPanel - onFormSave()');
		},
		
		onDeleteRow: function(record)
		{		
			Sbi.debug('[IN] LOVListDetailPanel - onDeleteRow()');
			
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
								var responseToShow = response;
								
								response = response.response ;
								
								if(response.responseText!=null && response.responseText!=undefined)
								{
									if(response.responseText.indexOf("error.mesage.description")>=0 || 
											response.responseText.indexOf("Error while deleting LOV") >= 0)
									{
										Sbi.exception.ExceptionHandler.handleFailure(responseToShow);
									}
									else
									{
										Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.behavioural.lov.deleted'));
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
			
			Sbi.debug('[OUT] LOVListDetailPanel - onDeleteRow()');
		},
		
		
		onGridSelect: function(selectionrowmodel, record, index, eOpts)
		{
//			Sbi.debug('[IN] LOVListDetailPanel - onGridSelect()');
			
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
			
			this.detailPanel.setFormState(record.data);
			
//			Sbi.debug('[OUT] LOVListDetailPanel - onGridSelect()');
		}
	}
);