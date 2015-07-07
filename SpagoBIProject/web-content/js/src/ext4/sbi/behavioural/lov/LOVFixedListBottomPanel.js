/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Danilo Ristovski (danilo.ristovski@mht.net)
 */

Ext.define
(
	"Sbi.behavioural.lov.LOVFixedListBottomPanel", 
	
	{
		create: function()
		{		
			Sbi.debug("[IN] Creating LOVFixedListBottomPanel");
			
			var fixLOVScope = this;
			
			this.lovFixedListForm = Ext.create
    		(
    			"Ext.form.Panel",
    			
    			{
    				title: LN('sbi.behavioural.lov.details.fixLovForm'),
    				width: "100%",    				
    				padding: '10 10 5 10',	// (top, right, bottom, left)
    				id: "FixLOVForm",
    				
    				defaultType: 'textfield',
    				
    				items: 
					[					 	
					 	{					 		
					 		fieldLabel: LN("sbi.behavioural.lov.details.fixLovItemsLabel"),
					 		name: 'FixLovValue',
					 		id: "FixLovValue",
					 		width: 400,
					 		padding: "10 0 10 0"
				 		},
				 		
				 		{
					 		fieldLabel: LN("sbi.behavioural.lov.details.fixLovItemsDescription"),
					 		name: 'FixLovDescription',
					 		id: "FixLovDescription",
					 		width: 400,
					 		padding: "10 0 10 0"
				 		},
				 		
				 		{
				 			xtype: 'button',
				            text: LN("sbi.behavioural.lov.details.fixLovAddItemsButton"),				           
				            margin: '0 5 15 300',  	// (top, right, bottom, left)
				            width: 100,
				            //scope: this,
				            
				            handler: function() 
					        {			
				            	Sbi.debug('[IN] LOVDetailPanel - initFields() - "Add" button handler');
				            						            	
				            	var backToRoots = this.ownerCt.ownerCt.ownerCt;
				            	var panel1 = backToRoots.items.items[0];
				            	var lovLabel = panel1.items.items[1].value;
				            	var lovName = panel1.items.items[2].value;					            	
				            	
					        	if ( lovLabel != "" && lovName != "" )
					        	{
						            var form = this.up('form').getForm();
						            
						            var fixLovValue = form._fields.items[0].value;
						        	var fixLovDescription = form._fields.items[1].value;	
						        	
						            if (fixLovValue != "" && fixLovValue != undefined && 
						            		fixLovDescription != "" && fixLovDescription != undefined) 
						            {				
						            	fixLOVScope.fixLovStore.add({value: fixLovValue, description: fixLovDescription});		
						            	form.reset();
						            	fixLOVScope.fixLovGrid.getView().focusRow(fixLOVScope.fixLovStore.getCount()-1);
						            	
						            	/* Event "addButtonPressed" will help us in determining whether Add button is
							        	 * pressed or not. Useful when making decision if user could
							        	 * save LOV record before testing it. */
							        	fixLOVScope.lovFixedListForm.fireEvent("addButtonPressed");
						            }
						            else
					            	{	
						            	if (fixLovValue == "" || fixLovValue == undefined)
						        		{
							        		//Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.fixLovValueMissing'));	
							        		form._fields.items[0].markInvalid(LN('sbi.behavioural.lov.details.fixLovValueMissing'));
						        		}
							        	else if (fixLovDescription == "" || fixLovDescription == undefined)
						        		{
							        		//Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.fixLovDescriptionMissing'));
							        		form._fields.items[1].markInvalid(LN('sbi.behavioural.lov.details.fixLovDescriptionMissing'));
						        		}
					            	}
					        	}
					        	else 
				        		{					        		
					        		Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.behavioural.lov.details.lovDetailMissing'));
				        		}						        	
					        	
					        	Sbi.debug('[OUT] LOVDetailPanel - initFields() - "Add" button handler');
					        }
				 		}
					]
    			}
    		);
    		
    		var fixLovModel = Ext.define
    		(
				'FixLovModel', 
				
				{
					extend: 'Ext.data.Model',
					
	    		    fields: 
    		    	[
	    		        {name: 'value',  type: 'string'},
	    		        {name: 'description',   type: 'string'}
    		        ]
    		    }
    		);
    		
    		this.fixLovStore = Ext.create
    		(
    			'Ext.data.Store',
    			
    			{
    				model: "FixLovModel"
    			}
    		); 
    		    		
    		this.infoPanel = Ext.create
    		(
    			'Ext.form.Panel',
    			
    			{
    				title: LN("sbi.behavioural.lov.details.fixLovInfoPanelTitle"),
    				layout: 'fit',
    				width: "100%",
    				// (top, right, bottom, left)
    				padding: '10 10 5 10',
    				icon: '/SpagoBI/themes/sbi_default/img/info22.jpg',
    				
    				items: [{
    			        xtype: 'label',
    			        text: LN('sbi.behavioural.lov.details.infoPanel'),
    			        margin: '10 0 10 10'
    			    }],
    			    
    			    bodyStyle:{"background-color":"#FFFFCC"}
    			}
    		);
    		
    		var globalScope = this;
    		
    		this.fixLovGrid = Ext.create
    		(
    			"Ext.grid.Panel",
    			
    			{
    				title: LN('sbi.behavioural.lov.details.fixLovGridPanel'),
    				store: this.fixLovStore,
    				padding: "10 10 15 10",
    				width: "100%",
    				autoScroll: true,
    				//overflowY: 'auto',
    			    //overflowX: 'auto',
    			    height: 350,
    				
    				columns:
					[
						{ 
							text: LN("sbi.behavioural.lov.details.fixLovGridLabelColumn"), dataIndex: "value", flex: 1, 
							
							editor: 
							{
					            xtype : 'textfield', 
					            allowBlank : false,
					            
					            /* If any item in the first column of the fix LOV grid
			                	 * is changed (modified) anyhow (even if the value for
			                	 * particular item is returned to the original state 
			                	 * (has the value that is existing in DB) after changing
			                	 * it). */
					            listeners : 
					            {
					                change : function(field, e) 
					                {						                	
					                	field.up('grid').fireEvent("changedGridItemsValue");
					                }
					            }
					        } 									
						},

						{ 
							text: LN("sbi.behavioural.lov.details.fixLovGridDescriptionColumn"), dataIndex: "description",  flex: 1, 
							
							editor: 
							{
					            xtype : 'textfield', 
					            allowBlank : false,
					            
					            /* If any item in the first column of the fix LOV grid
			                	 * is changed (modified) anyhow (even if the value for
			                	 * particular item is returned to the original state 
			                	 * (has the value that is existing in DB) after changing
			                	 * it). */
					            listeners : 
					            {
					                change : function(field, e) 
					                {
					                	field.up('grid').fireEvent("changedGridItemsValue");
					                }
					            }
					        } 
								
						},
						
						{
			                xtype: 'actioncolumn',
			                width: 20,
			                
			                items: 
		                	[
			                	 {
				                	iconCls: 'button-remove',
				                    
				                	handler: function(grid, rowIndex, colindex) 
				                	{
				                        var recordToDelete = fixLOVScope.fixLovStore.getAt(rowIndex);
				                        fixLOVScope.fixLovGrid.getStore().remove(recordToDelete);
				                        
				                        fixLOVScope.fixLovGrid.fireEvent("itemRemovedFromTheGrid");
				                    }
			                	 }
		                	 ]
			            }
					],
					
					plugins: 
					[					           
			           Ext.create('Ext.grid.plugin.CellEditing', {})
		            ]
    			}
    		);	    		
    		
    		Sbi.debug("[OUT] Creating LOVFixedListBottomPanel");
		}
		
	}
);