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

Ext.define
(
	"Sbi.behavioural.lov.LOVDatasetBottomPanel", 
	
	{			
		create: function(datasetURL)
		{		
			Sbi.debug("[IN] Creating LOVDatasetBottomPanel");
			
			var bottomPanelScope = this;
						
			/**
			 * Columns that dataset grid inside the popup window will contain.
			 */
			var columnsOfTheWindowGrid = 
			[
				{ text: LN("sbi.behavioural.lov.datasetsGridLabelColumn"), dataIndex: "label", flex: 1},
				{ text: LN("sbi.behavioural.lov.datasetsGridNameColumn"), dataIndex: "name", flex: 1},
				{ text: LN("sbi.behavioural.lov.datasetsGridDescriptionColumn"), dataIndex: "description",  flex: 1},
			
				{
	                xtype: 'actioncolumn',
	                width: 20,
	                
	                items: 
                	[
	                	 {
		                	icon: '/SpagoBI/themes/sbi_default/img/button_ok.gif',
		                    
		                	handler: function(grid, rowIndex, colindex, button, mouseEvent, chosenDataset) 
		                	{
//				               	var chosenDataset = datasetStore.getAt(rowIndex);
		                        var datasetFormItems = bottomPanelScope.datasetForm.items.items[0];
		                        datasetFormItems.items.items[0].setValue(chosenDataset.data.label);
		                        datasetFormItems.items.items[1].setValue(chosenDataset.data.id);
		                        bottomPanelScope.datasetWindow.hide();				                        
		                    }
	                	 }
                	 ]
	            }
			];
		
			/**
			 * Configuration of the grid panel that will contain grid with data (available
			 * datasets - existing in the DB), search box on the top of the grid panel and
			 * the pagination toolbar at it's bottom.
			 */
			var fixedGridPanelConf = 
			{
					pagingConfig:{},
					
					storeConfig:
					{ 
						pageSize: 5
					},
					
//					columnWidth: 2/5,
//					buttonToolbarConfig: null,
//					buttonColumnsConfig: null,
//					customComboToolbarConfig: null,
					
					modelName: "Sbi.behavioural.lov.DatasetModel",
					columns: columnsOfTheWindowGrid,
					filterConfig: {},
					filteredProperties: ["name"],
		    		filteredObjects: null

			};
		
			/**
			 * Creating the grid that will be set inside the popup window. Grid offers all
			 * available datasets inside the DB. The grid is created depending on the 
			 * configuration that is set through previously defined variable.
			 */
			var gridWithDatasets = Ext.create('Sbi.widgets.grid.FixedGridPanelInMemoryFiltered', fixedGridPanelConf);			
			
			this.datasetWindow = Ext.create
			(
				'Ext.window.Window', 
				
				{
				    title: LN("sbi.behavioural.lov.datasetsWindowTitle"),
				    closeAction: 'hide',
				    layout: 'fit',
				    width: 600,
				    resizable: true,
				    modal: true, // Prevent user from selecting something behind the window 
				    items: [ gridWithDatasets ]
				}
			);	
			
			datasetStore = Ext.create
        	(
    			'Ext.data.Store',
	    		
    			{
	        		model: Sbi.behavioural.lov.DatasetModel,
	        		autoLoad: true
	        	}
			);
						
			datasetStore.on
        	(
    			'load', 
    			
    			function(datasetStore)
    			{ 
    				//Sbi.debug('[INFO] Dataset store loaded (DATASET)');							
    			}
			);				
			
			this.datasetWindowTrigger = Ext.create
			(
				'Ext.form.field.Trigger', 
				
				{
					triggerCls:'x-form-search-trigger',
					editable: false,
					hideEmptyLabel: true,
					width: 300,
					
					onTriggerClick: function(e) 
					{
						bottomPanelScope.datasetWindow.show(this);
					}					
				}
			);
			
			
			this.datasetForm = Ext.create
			(
				"Ext.form.Panel",
				
				{
					//title: "Dataset Form",
					id: "DATASET_FORM",
					padding: "10 0 5 0",
					border: false,
					
					bodyStyle:{"background-color":"#F9F9F9"},	
					
					//defaultType: 'textfield',
					
				    items: 
			    	[
						{
							xtype: "fieldcontainer",						
							fieldLabel: LN("sbi.behavioural.lov.datasetLovFormLabel"),
				    	 
							layout: "hbox",
							
							items:
							[
								this.datasetWindowTrigger,
							 	
								{
							 		xtype: "textfield",
							        name: 'DATASET_ID',
							        hidden: true
							 	}							 	
							]
						}
				    ]		    				    
				}
			);			
			
    		Sbi.debug("[OUT] Creating LOVDatasetBottomPanel");
		}			
		
	}
);