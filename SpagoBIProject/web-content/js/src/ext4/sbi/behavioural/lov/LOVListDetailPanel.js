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
						
			this.columns = 
			[
			 	{ dataIndex:"LOV_NAME", 		header:LN('sbi.generic.label') }, 
			 	{ dataIndex:"LOV_DESCRIPTION", 	header:LN('sbi.generic.descr') }
		 	];
			
			this.fields = ["LOV_ID", "LOV_NAME", "LOV_DESCRIPTION", "INPUT_TYPE_COMBOBOX"];
			
			this.detailPanel.on("save", this.checkCanSave, this);
			this.detailPanel.on("test", this.openTestPage, this);
			
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
		
		openTestPage: function(record)
		{
			console.log("OPEN TEST PAGE...");
			console.log(record);
//			aa.prep(this.detailPanel.lovProvider.value);	
			
			// Iz detailLovTestResult.jsp - kada se klikne na Test treba ovo uraditi
			
			var lovConfig = {};
			
			lovConfig.descriptionColumnName =  'null'; 	// treba da bude null
			lovConfig.valueColumnName =  'null';			// treba da bude null
			lovConfig.visibleColumnNames = '[]';			// treba da bude []
			
			lovConfig.lovType =  'simple';
			
			lovConfig.treeColumnNames = 'null';			

			var contextName = 'SpagoBI'; 
			
			var lovProvider = this.detailPanel.lovProvider.value;
						
//			Ext.data.proxy.Rest.buildRequest(Ext.create("Ext.data.Operation", {action: "create", params: "aaa"}));
			
			this.detailPanel.updatePanel(contextName, lovConfig, lovProvider);
					
//			Ext.Ajax.request
//    		(
//				{
//					url: "http://localhost:8080/SpagoBI/restful-services/LOV/Test",
//	                params:  this.detailPanel.lovProvider.value,
//	                method: "POST",
//	                
//	                success: function(response, options) 
//	                {
//	                	console.log("RESPONSE...");             	
//	                	
//	                	var responseJSON = Ext.JSON.decode(response.responseText);
//	                	console.log(responseJSON);
//	                	console.log(responseJSON.metaData.fields.length);
//	                	var sm = new Array(responseJSON.metaData.fields.length);	                	
//	                	
//	                	console.log(responseJSON);
//	                	console.log(responseJSON.metaData.fields.length);
//	                	
//	                	for (var i=1; i<responseJSON.metaData.fields.length; i++)
//	                	{
//	                		console.log("kkkkkkkkkkkkkk");
//	                		console.log(responseJSON.metaData.fields[i].header);
//	                		sm[i] = responseJSON.metaData.fields[i].header;
//	                		console.log("nnnnnnnnnnnn");
//	                		console.log(sm[i]);
//	                	}                	
//	                	
//	                	//result = responseJSON;
//	                	this.ajaxEnded = true;
//	                	console.log("PRE");
//	                	console.log(responseJSON);
//	                	//console.log(result);
//	                	console.log("W T F");
//	                	console.log(this.detailPanel.getForm().getFields());
//	                	console.log(sm.toString());
//	                	this.detailPanel.getForm().getFields().items[11].value = "aaammm";
//	                	this.detailPanel.getForm().getFields().items[11].rawValue = sm.toString();
//	                	
//	                	console.log("000");
//	                	console.log(this.detailPanel.getForm());
//	                	
//	                	aa.prep("aaammm");
//	                	
//	                	//yy.responseProc(sm);
////	                	console.log(sm);
////	                	yy.textArea1.setValue(sm.toString());
////	                	console.log("U P M");
////	                	console.log(yy.result);
//	                },
//	                
//	                failure: Sbi.exception.ExceptionHandler.handleFailure,
//	                scope: this
//       		 	}
//			);
			
			
		},
		
		checkCanSave: function(record)
		{
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
					canBeSaved = true;
			}
				
			if (canBeSaved == true)
			{
				currentPanel.onFormSave(record);
			}
			
		},	
		
		onFormSave: function(record)
		{			
			this.detailPanel.getFormState().save
			(
				{
					success: function(object, response, options)
							{																
								if(response !== undefined && response.response !== undefined && response.response.responseText !== undefined && response.response.statusText=="OK") 
								{									
									response = response.response ;
									
									if(response.responseText!=null && response.responseText!=undefined)
									{										
										if(response.responseText.indexOf("error.mesage.description")>=0)
										{
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
		},
		
		onDeleteRow: function(record)
		{			
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
		},
		
		
		onGridSelect: function(selectionrowmodel, record, index, eOpts)
		{
			this.detailPanel.show();
			
			if (record.data.LOV_ID == null || record.data.LOV_ID == "")
			{	
				this.detailPanel.panel2.hide();
			}
			
			this.detailPanel.setFormState(record.data);
		}
	}
);