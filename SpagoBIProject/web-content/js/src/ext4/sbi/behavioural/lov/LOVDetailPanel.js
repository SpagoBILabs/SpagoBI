/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define
(
	"Sbi.behavioural.lov.LOVDetailPanel",
	
	{
		extend: "Ext.form.Panel",
				
		config: 
		{
	    	//frame: true,
	    	//bodyPadding: '5 5 0',
	    	
	    	defaults: 
	    	{
	            //width: 400	            
	    		layout: "fit"
	        }, 
	        
	        fieldDefaults: 
	        {
	            labelAlign: 'right',
	            msgTarget: 'side'
	        },
	        
	        border: false,
			
	        services:[]
	    },
	    
	    
	    constructor: function(config)
    	{	    	
	    	Sbi.debug('[IN] LOVDetailPanel - constructor');
	    	
	    	this.initConfig(config);
	    	
	    	this.initFields();
	    	
	    	this.resultPanel = Ext.create
	    	(
    			"Ext.panel.Panel",
    			
    			{
    				layout: "fit",
    				border: false	
    			}
	    	);
	    	
	    	var globalScope = this;
	    	
	    	this.tabPanel = Ext.create
	    	(
    			'Ext.tab.Panel', 
    			
    			{
//		    		width: 720,
		    		//height: screen.innerHeight,
    				border: false,
    				layout: "fit",
		    		id: "TAB_PANEL_RESULTS",
		    		
		    	    items: 
	    	    	[	
	    	    	 	{
	    	    	 		title: "LOV Form",
	    	    	 		items: [ this.panel1, this.panel2 ],
	    	    	 		border: false
	    	    	 	}, 
	    	    	 	
	    	    	 	{
	    	    	 		title: 'LOV Results',
	    	    	 		items: [ this.resultPanel ],
	    	    	 		layout: "fit",
	    	    	 		border: false
	    	    	 	}
		    	 	],
	    	 	
		    	 	defaults: 
		    	 	{
		    	        listeners: 
		    	        {
		    	            activate: function(tab, eOpts) 
		    	            {		    	                
		    	                if (tab.title == "LOV Form")
	    	                	{
		    	                	// When form tab is selected show Test button and hide Save button
		    	                	globalScope.getComponent("TOOLBAR").items.items[2].show();
		    	                	globalScope.getComponent("TOOLBAR").items.items[3].hide();
	    	                	}
		    	                else
	    	                	{
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
	    	
	    	this.tbar = Sbi.widget.toolbar.StaticToolbarBuilder.buildToolbar
	    	(
    			{ 
    				id: "TOOLBAR",
    				
    				items:
    				[ 
    				 	{name:'->'},
    				 	{name:'test'},
    				 	{name:'save'} 
				 	] 
    			}, 
    			
    			this
			);	

	    	this.tbar.on
	    	(
    			"save",
    			
    			function()
    			{
					if(true)
					{	
						this.fireEvent("save", this.getValues());
					}
					else
					{
						Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.datasource.validation.error'),LN('sbi.generic.validationError'));
					}				
    			},
    			
    			this
			);
	    	
	    	this.tbar.on
	    	(
    			"test",
    			
    			function()
    			{    				
    				this.fireEvent("test", this.getValues());
    			},
    			
    			this
			);
	    	
	    	this.callParent(arguments);
	    	
	    	this.on
	    	(
    			"render",
    			
    			function()
    			{
    				this.hide();
				},
				
				this
			);
	    	
	    	Sbi.debug('[OUT] LOVDetailPanel - constructor');
	    	
    	},
    	
    	takeValues: function()
    	{
    		Sbi.debug('[IN&OUT] LOVDetailPanel - takeValues()');
    		return this.resultPanel.getComponent("TEST_LOV_PANEL").takeValues();
    	},
    	
    	setValues: function(data)
    	{
    		Sbi.debug('[IN&OUT] LOVDetailPanel - setValues()');
    		this.resultPanel.getComponent("TEST_LOV_PANEL").setValues(data);
    	},
    	
    	splitProfileAttributes: function(profAttributes)
    	{
    		Sbi.debug('[IN] LOVDetailPanel - splitProfileAttributes()');
    		
    		var profAttr = [];
    		var profAttrValues = "";
    		
    		var array = profAttributes.substring(1,profAttributes.length-1);
    		    	
    		var splitArray = array.split(",");    		
    		
    		var lengthSplitArray = splitArray.length;
    		var nameArray = [];
    		profAttr = [];
    		
    		for (var i=0; i<lengthSplitArray; i++)
			{
    			var oneElemSplit = splitArray[i].split(":");
    			var tempAttrName = oneElemSplit[0].substring(1,oneElemSplit[0].length-1);
    			nameArray[i] = tempAttrName;
    			profAttr[i] = [tempAttrName, oneElemSplit[1].substring(1,oneElemSplit[1].length-1)];
			}
    		    		
    		Sbi.debug('[OUT] LOVDetailPanel - splitProfileAttributes()');
    		
    		return [profAttr,nameArray];
    	},
    	
    	updatePanel: function(contextName, lovConfig, lovProvider, profAttributes)
    	{
    		Sbi.debug('[IN] LOVDetailPanel - updatePanel()');
    		
    		var prof = null;
    		
    		var values = null;
    		var first = null;
    		var firstName = null;
    		var firstValue = null;		   		
 		
    		if (profAttributes != null || profAttributes != undefined)
			{
				prof = this.splitProfileAttributes(profAttributes);
				
				values = prof[0];	
	    		
	    		for (var i=0; i<values.length; i++)
    			{	    			
	    			if (this.profileAttrStoreContainer == undefined || this.profileAttrStoreContainer[values[i][0]] == undefined ||
							this.profileAttrStoreContainer[values[i][0]] == null ||
							this.profileAttrStoreContainer[values[i][0]] == {})
					{						
						Ext.define
			    		(
							'MissingProfileAttributes', 
							
							{
								extend: 'Ext.data.Model',
								
								fields: 
								[
								 	{name: 'firstName',  type: 'string'}
								]
							}
						);    		
			    		
			    		var storeAttributes =  Ext.create
			    		(
		    				'Ext.data.Store', 
		    				{
		    					model: 'MissingProfileAttributes'
	    					}
	    				);
			    		
			    		storeAttributes.add({firstName:values[i][1]}); 
			    		this.profileAttrStoreContainer[values[i][0]] = storeAttributes;				    				    		
					}
					else 
					{			    		
			    		var ourStore = this.profileAttrStoreContainer[values[i][0]];
			    				    		
			    		var storeItems = ourStore.data.items;
			    		var repeating = false;
			    		
			    		for (var j=0; j<storeItems.length; j++)
		    			{			    			
			    			if (storeItems[j].data.firstName != firstValue)
			    				repeating = true;
		    			}
			    		
			    		if (repeating == false)
			    			ourStore.add({firstName:values[i][1]});			    		
			    		
					}	
	    			
	    			this.profileAttrStoreContainer[values[i][0]].lastSelected = values[i][1];
	    			
	    			if (this.lovId.value == 0)
		    		{
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
    		    		
    		var lovTest = Ext.create
			(
				"Sbi.behavioural.lov.TestLovPanel2", 
				
				{
					contextName: contextName, 
					lovConfig: lovConfig,
					profileAttributes: profileAttributes,
					id: "TEST_LOV_PANEL",
					//tabPanelHeight: this.tabPanel.height,
					// Can I define tab height this way?
					tabPanelHeight: window.innerHeight,
					lovProvider: lovProvider
				}
			);
    		
    		lovTest.on
	    	(
    			"wrongSyntax2", 
    			
    			function()
    			{    				
    				/* If user make mistake and type wrong syntax for SQL query
    				 * take him back to the first page (LOV form) and provide him
    				 * the last known (good, valid) SQL query (the query that existed) 
    				 * previously for this LOV. */
    				
    				Sbi.debug('[IN] LOVDetailPanel - updatePanel() - wrong syntax error');
    				
    				var startStatement = -1;
        			var endStatement = -1;
        			var oldStatement = "";
    				
    				// Return old LOV value for new LOV value (error happened, so we are losing nothing)
    				//this.lovProvider.value = this.lovProvider.lastValue;
    				
    				this.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].hide();
    				this.getComponent("TAB_PANEL_RESULTS").setActiveTab(0);
        			    				
    				var lovPanel2 = this.tabPanel.items.items[0].getComponent("PANEL2");    				
    				
    				var lovProviderValue = "";
    				
    				var lovInputTypeModified = "";
    				var endLovInputTypeLastValue = "";
    				
    				/* Find out what is the type input type of the original record and what is the
    				 * input type for the modified record (just tried to be modified, but with 
    				 * unsuccessful epilogue because of the error when testing). */
    				var endLovInputTypeValue = this.lovProvider.value.indexOf(">");
    				
    				if (this.lovProvider.lastValue != null && this.lovProvider.lastValue != undefined && this.lovProvider.lastValue != "")
					{
    					endLovInputTypeLastValue = this.lovProvider.lastValue.indexOf(">");
    					lovInputTypeModified = this.lovProvider.lastValue.substring(1,endLovInputTypeLastValue);   
					}    					
    				   
    				// Variables that contain string values for the two LOV input types
    				var lovInputTypeOriginal = this.lovProvider.value.substring(1,endLovInputTypeValue);    				  				    				
    				
    				var selectedRecord = this.ownerCt.items.items[0].selModel.selected.items[0].data;
    				
    				if (this.lovId.value == 0 || lovInputTypeOriginal != lovInputTypeModified)
					{
    					lovProviderValue = this.lovProvider.value; 
					}
    				else
					{
    					lovProviderValue = this.lovProvider.lastValue;
					}
    				    				        			
    				if (lovProviderValue.indexOf("QUERY") >= 0)
    				{
    					var dataSourceCombo = lovPanel2.items.items[0];
        				var dataSourceQuery = lovPanel2.items.items[1];
    					
    					startStatement = lovProviderValue.indexOf("<STMT>")+"<STMT>".length;
            			endStatement = lovProviderValue.indexOf("</STMT>");
            			oldStatement = lovProviderValue.substring(startStatement,endStatement);
            			
            			dataSourceQuery.setValue(oldStatement);
            			
            			var startIndex = lovProviderValue.indexOf("<CONNECTION>");
            			var endIndex = lovProviderValue.indexOf("</CONNECTION>");
            			
            			var dsType = lovProviderValue.substring(startIndex + "<CONNECTION>".length,endIndex);
            			
            			dataSourceCombo.setValue(dsType);
            			
            			if (this.lovId.value == 0)
        				{            				            			
	            			dataSourceQuery.markInvalid("Wrong syntax for selected data source query type: " + dsType + ". Try again...");
        				}
    				}
    				
    				else if (lovProviderValue.indexOf("SCRIPTLOV") >= 0)
					{
    					var scriptType = lovPanel2.items.items[0];
        				var scriptQuery = lovPanel2.items.items[1];
    					
    					startStatement = lovProviderValue.indexOf("<SCRIPT>")+"<SCRIPT>".length;
    					endStatement = lovProviderValue.indexOf("</SCRIPT>");
    					oldStatement = lovProviderValue.substring(startStatement,endStatement);   
    					
    					var startScriptType = lovProviderValue.indexOf("<LANGUAGE>")+"<LANGUAGE>".length;
            			var endScriptType = lovProviderValue.indexOf("</LANGUAGE>");
            			var scriptTypeString = lovProviderValue.substring(startScriptType,endScriptType);
            			
            			// !!!! Is this allowed ???
            			if (scriptTypeString == "ECMAScript") 	// saw in ScripDetail.java
            				scriptTypeString = "Javascript";
            			else if (scriptTypeString == "groovy") 	// saw in u ScripDetail.java
            				scriptTypeString = "Groovy";
    					
    					if (this.lovId.value == 0)
        				{
    						scriptQuery.markInvalid("Wrong syntax for selected script type: " + scriptTypeString + ". Try again...");
        				}
					}  
    				
    				else if (lovProviderValue.indexOf("JAVACLASSLOV") >= 0)
					{
    					var javaClassName = lovPanel2.items.items[0];
    					
    					startStatement = lovProviderValue.indexOf("<JAVA_CLASS_NAME>")+"<JAVA_CLASS_NAME>".length;
    					endStatement = lovProviderValue.indexOf("</JAVA_CLASS_NAME>");
    					oldStatement = lovProviderValue.substring(startStatement,endStatement);
            			
    					javaClassName.setValue(oldStatement);
    					
    					if (this.lovId.value == 0)
        				{
    						javaClassName.markInvalid("Wrong Java class path and/or wrong Java class name. Try again...");
        				}
					}
    				
    				Sbi.debug('[OUT] LOVDetailPanel - updatePanel() - wrong syntax error');
    			},
    			
    			this
	    	);	
    		
    		lovTest.on
	    	(
    			"missingProfileAttr2", 
    			
    			function(missingProfileAttr)
    			{        				
    				/* We are getting back from server-side with the request to fill the 
    				 * missing Profile attribute(s) for the current user. */ 
					
					var numbOfMissingProfileAttr = missingProfileAttr.length;
					
					var lovProfileAttrPanel = Ext.create("Sbi.behavioural.lov.LOVProfileAttributeFilling",{});
					lovProfileAttrPanel.create();					
					
					if (this.profileAttrStoreContainer == undefined)
					{
						for(var k=0; k<numbOfMissingProfileAttr; k++)
						{							
							this.profileAttrStoreContainer = {};
							
							Ext.define
				    		(
								'MissingProfileAttributes', 
								
								{
									extend: 'Ext.data.Model',
									 fields: [
									          {name: 'firstName',  type: 'string'}
									      ]
								}
							);    		
				    		
				    		var storeAttributes =  Ext.create('Ext.data.Store', {
				    		     model: 'MissingProfileAttributes'
						     });
				    		 
				    		this.profileAttrStoreContainer[missingProfileAttr[k]] = storeAttributes;	
						}
					}	
								
					
					for (var u=0; u<numbOfMissingProfileAttr; u++)
					{
						lovProfileAttrPanel.lovFixedListForm.add
						(
							{
								xtype: 'combobox',
								// (top, right, bottom, left)
								padding: '10 5 0 0',
								valueField: 'firstName',
								fieldLabel: missingProfileAttr[u],
						 		labelAlign: 'right',					 		
						 	    store: this.profileAttrStoreContainer[missingProfileAttr[u]],
						 	    queryMode: 'local',
						 	    displayField: 'firstName'
							}
						);
						
						/* Setting the last chosen value for every single missing profile 
						 * attribute into their belonging combo boxes. */
						var combo = lovProfileAttrPanel.lovFixedListForm.items.items[u];
						var comboStoreData = combo.store.data;
						
						if (comboStoreData.length > 0)
						{							
							combo.setValue(this.profileAttrStoreContainer[missingProfileAttr[u]].lastSelected);
						}
					}
					
					/* Pop-up window that will appear when it is needed to 
					 * define missing profile attributes for LOV. */
					var window1 = Ext.create
					(
						'Ext.window.Window', 
						
						{
						    title: LN('sbi.behavioural.lov.details.missingProfileAttributesWindow'),
						    layout: 'fit',
						    resizable: false,
						    modal: true, // Prevent user from selecting something behind the window 
						    items: [ lovProfileAttrPanel.lovFixedListForm ],
						    
						    listeners:
						    {
				                 'close': function(win)
				                 {			                          
			                          /* Check if window is closed after clicking the Add button
			                           * or after clicking on the X button on the top right corner.
			                           * In the first case close window and show the first tab, hidding
			                           * the second one. In the latter case, show the result page and
			                           * close the window for filling the missing profile attributes. */
			                          if (addClicked == false)
		                        	  {
			                        	  updateScope.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].hide();
				                          updateScope.getComponent("TAB_PANEL_RESULTS").setActiveTab(0);   
		                        	  }			                              
				                 }
						    }
						}
					);
					
					/* If this method (updatePanel) is called for the first time - 
					 * when we should defined the missing profile attribute(s). */
					if (profAttributes == null || profAttributes == undefined || profAttributes == "")
					{						
						/* Add confirmation (Add) button to the form that will lie
						 * inside pop-up window */
						lovProfileAttrPanel.lovFixedListForm.add
						(
							{
					 			xtype: 'button',
					            text: 'Add',				           
					            margin: '10 10 15 290',  	// (top, right, bottom, left)
					            width: 70,
					            //scope: this,
					            
					            handler: function() 
						        {								            	
					            	var form = this.up('form').getForm();	
					            	var arrayOfAttributes = "{";
					            						            	
					            	/* If no profile attribute is missing, we can send the form to
					            	 * the server-side in order to proceed to testing page. Now, we 
					            	 * will form one string with all the data that was missing - 
					            	 * (key,value) pairs that we will parse (split) on the server-side
					            	 * to get those attributes names and their values. */	
				            		
				            		for (var i=0; i<numbOfMissingProfileAttr; i++)
				            		{					            			
				            			if (i<numbOfMissingProfileAttr-1 && numbOfMissingProfileAttr > 1)
				            			{
					            			arrayOfAttributes = arrayOfAttributes + "\"" + missingProfileAttr[i] + "\":\"" + form._fields.items[i].rawValue + "\",";
				            			}
				            			else if (numbOfMissingProfileAttr == 1 || i==numbOfMissingProfileAttr-1)
			            				{
				            				arrayOfAttributes = arrayOfAttributes + "\"" + missingProfileAttr[i] + "\":\"" + form._fields.items[i].rawValue + "\"";
			            				}					            							            		
				            		}	
				            		
				            		arrayOfAttributes = arrayOfAttributes + "}";
				            		
						        	
				            		/* Recursive call - call again THIS function to send data necessary 
				            		 * for testing once again (now, complete ones). */
					            	updateScope.updatePanel(contextName,lovConfig,lovProvider,arrayOfAttributes);
					            	
					            	/* Close the pop-up window for filling the data 
					            	 * about missing data */
					            	addClicked = true;
					            	
					            	window1.close();	
						        }
					 		}
						);			
						
						window1.show();
					}				
					
				}, 
				
				this
	    	);
    		
    		this.resultPanel.add
    		(
    			lovTest
			);
    		
    		this.resultPanel.update(); 
    		    		
    		Sbi.debug('[OUT] LOVDetailPanel - updatePanel()');
    	},    	
    	
    	
    	initFields: function()
    	{
    		Sbi.debug('[IN] LOVDetailPanel - initFields()');
    		
    		var globalScope = this;    
    		
    		this.profileAttrStoreContainer = {};
    		this.profileAttrStoreContainerLovIdZero = [];
    		this.attributeContainer = [];
    		
    		this.lovId = Ext.create
    		(
    			"Ext.form.field.Hidden",
    			
    			{
    				name: "LOV_ID",
    				id: "LovId"
    			}
    		);
 		
    		this.lovLabel = Ext.create
    		(
				"Ext.form.field.Text",
				{
	    			name: "LOV_LABEL",
	    			allowBlank: false,
	    			fieldLabel: LN('sbi.behavioural.lov.details.label'),
	    			width: 400,
	    			padding: '10 0 0 0' 
				}
			);
    		
    		this.lovName = Ext.create
    		(
				"Ext.form.field.Text",
				
				{
	    			name: "LOV_NAME",
	    			allowBlank: false,
	    			fieldLabel: LN('sbi.behavioural.lov.details.name'),
	    			width: 400,
	    			padding: '10 0 0 0' 
				}
			);
    		
    		this.lovDescription = Ext.create
    		(
				"Ext.form.field.Text",
				
				{
	    			name: "LOV_DESCRIPTION",
	    			fieldLabel: LN('sbi.behavioural.lov.details.description'),
	    			width: 400,
	    			padding: '10 0 0 0' 
				}
			);    		
    		
    		this.lovProvider = Ext.create
    		(
    			"Ext.form.field.Hidden",
    			
    			{
    				name: "LOV_PROVIDER",
//    				height: 200,
//    				width: 400,
    				readOnly: true
    			}
    		);    
    		    		
    		this.lovSelectionType = Ext.create
    		(
    			"Ext.form.field.Hidden",
    			
    			{
    				name: "SELECTION_TYPE"
    			}
    		);
    		
    		
    		Ext.define
    		(
				"InputTypeModel", 
				
				{
					extend: 'Ext.data.Model',
					fields: [ "VALUE_NM", "VALUE_DS", "VALUE_ID", "VALUE_CD" ]
				}
			);
        	
    	
    		var inputTypeStore = Ext.create
        	(
    			'Ext.data.Store',
	    		
    			{
	        		model: "InputTypeModel",
	        		autoLoad: true,
	        		
	        		proxy: 
	        		{
	        			type: 'rest',
	        			
	        			extraParams : { DOMAIN_TYPE: "INPUT_TYPE" },
	        			
	        			url: globalScope.services['getDomains'],
	        			
	        			reader: 
	        			{
	        				type:"json"
	        			}
	        		}
	        	}
			);
    		
    		inputTypeStore.on
        	(
    			"load", 
    			
    			function(inputTypeStore)
    			{ 
    				Sbi.debug('[INFO] Input type store loaded');
    			}
			);
    		
    		var lastSelected, lastSelectedId;
    		
    		this.lovInputTypeCombo = Ext.create
    		(
				'Ext.form.ComboBox', 
	    		
				{
	    			fieldLabel: LN('sbi.behavioural.lov.details.inputType'),
	    			name: "I_TYPE_CD",
	    	        store: inputTypeStore,
	    	        id: "INPUT_TYPE_COMBO",
	    	        displayField:'VALUE_NM',
	    	        valueField:'VALUE_CD',
	    	        editable: false,
	    	        allowBlank: false,
	    	        padding: "10 0 10 0",
	    	        
	    	        listeners: 
			        {			            
			            select: function()
			            {			            	
			            	var panel2 = globalScope.tabPanel.items.items[0].getComponent("PANEL2");
			            	
			            	panel2.removeAll();
			            	
			            	var lovProvider = globalScope.lovProvider.rawValue;
			            	
			            	/* Take every detail form LOV provider in order to recreate the existing record
			            	 * (when we, for example, in existing record change the input type, populate the
			            	 * data in the form of the new LOV input type and then we want to return to the
			            	 * previous type of the selected record with the original data). In that situation
			            	 * the initial form for LOV is populated with the right data and LOV provider provide
			            	 * us data necessary for the test page of the re-tested original LOV (record) - VALUE,
			            	 * DESCRIPTION and VISIBLE columns. */
			            	globalScope.lovProvider.value = globalScope.lovProvider.rawValue;
			            				            	
			            	if (this.value == "QUERY")
		            		{
			            		var lovQueryPanel = Ext.create("Sbi.behavioural.lov.LOVQueryBottomPanel",{});
			            		lovQueryPanel.create(globalScope.services["getDataSources"]);	
			            		
			            		if (lovProvider.indexOf("<QUERY>")>-1)
					   			{
			            			if (globalScope.lovId.value == 0)
		            				{
			            				lovQueryPanel.dataSourceCombo.setValue("");
			            				lovQueryPanel.dataSourceQuery.setValue("");
		            				}
			            			else
		            				{
			            				var startIndex = lovProvider.indexOf("<CONNECTION>");
						    			var endIndex = lovProvider.indexOf("</CONNECTION>");
						    			
						    			lovQueryPanel.dataSourceCombo.setValue(lovProvider.substring(startIndex + "<CONNECTION>".length,endIndex));
						    			
						    			var startIndex = lovProvider.indexOf("<STMT>");
						    			var endIndex = lovProvider.indexOf("</STMT>");
						    			
						    			lovQueryPanel.dataSourceQuery.setValue(lovProvider.substring(startIndex + "<STMT>".length,endIndex));
		            				}				            		
					   			}
			            		
				        		panel2.add(lovQueryPanel.dataSourceCombo);
				        		panel2.add(lovQueryPanel.dataSourceQuery);
		            		}
			            	
			            	else if (this.value == "SCRIPT")
		            		{
			            		var lovScriptPanel = Ext.create("Sbi.behavioural.lov.LOVScriptBottomPanel",{});				    			
				    			lovScriptPanel.create(globalScope.services["getDomains"]);	   		
					   			
				    			if (lovProvider.indexOf("<SCRIPT>")>-1)
					   			{		
				    				if (globalScope.lovId.value == 0)
		            				{
				    					lovScriptPanel.scriptTypeCombo.setValue("");
				    					lovScriptPanel.scriptQuery.setValue("");
		            				}
			            			else
		            				{
			            				var startScriptType = lovProvider.indexOf("<LANGUAGE>")+"<LANGUAGE>".length;
					        			var endScriptType = lovProvider.indexOf("</LANGUAGE>");
					        			var scriptType = lovProvider.substring(startScriptType,endScriptType);
					        			
					        			// !!!! Maybe change this solution
					        			if (scriptType == "ECMAScript") 	// saw in ScripDetail.java
					        				scriptType = "Javascript";
					        			else if (scriptType == "groovy") 	// saw in ScripDetail.java
					        				scriptType = "Groovy";
					        			
					        			lovScriptPanel.scriptTypeCombo.setValue(scriptType);
					        			
					        			var startScript = lovProvider.indexOf("<SCRIPT>")+"<SCRIPT>".length;
					        			var endScript = lovProvider.indexOf("</SCRIPT>");
					        			lovScriptPanel.scriptQuery.setValue(lovProvider.substring(startScript,endScript));
		            				}					    			
					   			}
				    			
				        		panel2.add(lovScriptPanel.scriptTypeCombo);
				        		panel2.add(lovScriptPanel.scriptQuery);
		            		}
			            	
			            	else if (this.value == "FIX_LOV")
		            		{
			            		var lovFixedListPanel = Ext.create("Sbi.behavioural.lov.LOVFixedListBottomPanel",{});
				    			lovFixedListPanel.create();
				    			
				    			var fixLovStore = lovFixedListPanel.fixLovStore;    			
				    			
				    			if (lovProvider.indexOf("<FIXLISTLOV>")>-1)
					   			{	
				    				if (globalScope.lovId.value == 0)
		            				{
				    					fixLovStore.removeAll();
		            				}
			            			else
		            				{
			            				var startFixLov = lovProvider.indexOf("<ROWS>")+"<ROWS>".length;
					        			var endFixLov = lovProvider.indexOf("</ROWS>");
					        			var fixLovRows = lovProvider.substring(startFixLov,endFixLov);
					    				var listRows = fixLovRows.split("<ROW ");   				  	
					    				
					    				fixLovStore.removeAll();
			    				
					    				for (var i=1; i<listRows.length; i++)
										{
					    					var valueStart = listRows[i].indexOf("VALUE=")+"VALUE=".length + 1;
					        				var valueEnd = listRows[i].indexOf("\" DESCRIPTION");
					        				var valueFixLov = listRows[i].substring(valueStart,valueEnd);
					        				
					        				var descriptionStart = listRows[i].indexOf("DESCRIPTION=")+"DESCRIPTION=".length + 1;
					        				var descriptionEnd = listRows[i].indexOf("/>")-1;
					        				var descriptionFixLov = listRows[i].substring(descriptionStart,descriptionEnd);
					        				
					        				fixLovStore.insert(i,{value: valueFixLov, description: descriptionFixLov});        				
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
			    				
			    				
				    			panel2.add(lovFixedListForm);
				    			panel2.add(infoPanel);
				    			panel2.add(fixLovGrid);
		            		}
			            	
			            	else if (this.value == "JAVA_CLASS")
		            		{
			            		var lovJavaClassPanel = Ext.create("Sbi.behavioural.lov.LOVJavaClassBottomPanel",{});
			            		lovJavaClassPanel.create();
			            		
			            		if (lovProvider.indexOf("<JAVACLASSLOV>")>-1)
		            			{
			            			if (globalScope.lovId.value == 0)
		            				{
			            				lovJavaClassPanel.javaClassName.setValue("");
		            				}
			            			else
		            				{
			            				var startJavaClassName = lovProvider.indexOf("<JAVA_CLASS_NAME>")+"<JAVA_CLASS_NAME>".length;
										var endJavaClassName = lovProvider.indexOf("</JAVA_CLASS_NAME>");
										var javaClassName = lovProvider.substring(startJavaClassName,endJavaClassName);
						        		
						        		lovJavaClassPanel.javaClassName.setValue(javaClassName);
		            				}	
		            			}
			            		
			            		panel2.add(lovJavaClassPanel.javaClassName);
		            		}
			            	
			            	else if (this.value == "DATASET")
		            		{
			            		var lovDatasetPanel = Ext.create("Sbi.behavioural.lov.LOVDatasetBottomPanel", {});
			            		lovDatasetPanel.create(globalScope.services["datasets"]);
			            					            		
			            		panel2.add(lovDatasetPanel.datasetForm);
		            		}
			        		
			        		panel2.show();	
			            }
			        }
	    	    }
			);
    		
    		var globalThis = this;
    		
    		this.panel1 = Ext.create
    		(
    			"Ext.panel.Panel",
    			
    			{
    				title: LN('sbi.behavioural.lov.details.wizardUpper'),
    				width: "100%",
    				// (top, right, bottom, left)
    				padding: '15 15 10 15',
    				id: "PANEL1",
    				
    				
    				items: 
    					
					[ this.lovId, this.lovLabel, this.lovName, this.lovDescription, 
					  this.lovProvider, this.lovInputTypeCombo, this.lovInputTypeCd,
					  this.lovInputTypeId, this.lovSelectionType ],
	    			    
    			    bodyStyle:{"background-color":"#F9F9F9"}				
    			}
    		);
    		
    		
    		Ext.define
    		(
				"DataSourceModel", 
				
				{
					extend: 'Ext.data.Model',
					fields: [ "DESCRIPTION", "DATASOURCE_LABEL", "JNDI_URL", "DATASOURCE_ID" ] // fields (labels) from JSON that comes from server that we call
				}
			);
    		
    		this.panel2 = Ext.create
    		(
    			"Ext.panel.Panel",
    			
    			{
    				width: "100%",
    				//border: false,
    				// (top, right, bottom, left)
    				padding: '5 15 15 15',
    				id: "PANEL2",
    				bodyStyle:{"background-color":"#F9F9F9"}
    			}
    		); 		
    		
    		Sbi.debug('[OUT] LOVDetailPanel - initFields()');
    	},
    	
    	
//    	rememberOriginalFixLovStore: function()
//    	{
//    		this.fixLovStore.load();
//    		var modifiedFixLovStoreData = this.fixLovStore.data;
//    		
//    		for (var i=0; i<this.fixLovStore.data.length; i++)
//			{
//    			this.copyOfFixLovStore.add(modifiedFixLovStoreData[i]);
//			}
//    		
//    		//return this.modifiedFixLovStoreData;
//    	},
    	
    	getValues: function()
    	{    		
    		Sbi.debug('[IN & OUT] LOVDetailPanel - getValues()');    		
    		var values = this.callParent();
    		return values;
    	},    
    	
    	setFormState: function(values)
    	{    		
    		//Sbi.debug('[IN] LOVDetailPanel - setFormState()');  		
    		
    		// Left part of LOV Details Panel - the form
    		var lovFormTab = this.tabPanel.items.items[0];
    		var lovFormPanel1 = lovFormTab.getComponent("PANEL1");
    		var lovFormPanel2 = lovFormTab.getComponent("PANEL2");
    		
    		//var lovFormPanel3 = lovFormTab.getComponent("PANEL3");
    		
    		lovFormPanel2.removeAll();
    		
    		if (values.LOV_ID != 0)
    		{    		    			
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
	    		    		    		
	    		if (this.lovInputTypeCombo.value == "QUERY")
				{
	    			lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.queryWizard'));
	    			
	    			var lovQueryPanel = Ext.create("Sbi.behavioural.lov.LOVQueryBottomPanel",{});
		    		
	        		lovQueryPanel.create(this.services["getDataSources"]);	
	        		
	        		var startIndex = lovProvider.indexOf("<CONNECTION>");
	    			var endIndex = lovProvider.indexOf("</CONNECTION>");
	    			
	    			lovQueryPanel.dataSourceCombo.setValue(lovProvider.substring(startIndex + "<CONNECTION>".length,endIndex));
	    			
	    			var startIndex = lovProvider.indexOf("<STMT>");
	    			var endIndex = lovProvider.indexOf("</STMT>");
	    			
	    			lovQueryPanel.dataSourceQuery.setValue(lovProvider.substring(startIndex + "<STMT>".length,endIndex));
	   			
	        		lovFormPanel2.add(lovQueryPanel.dataSourceCombo);
	        		lovFormPanel2.add(lovQueryPanel.dataSourceQuery);
				}   
	    		
	    		else if (this.lovInputTypeCombo.value == "SCRIPT")
				{
	    			lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.scriptWizard'));
	    			
	    			var lovScriptPanel = Ext.create("Sbi.behavioural.lov.LOVScriptBottomPanel",{});
	    			
	    			lovScriptPanel.create(this.services["getDomains"]);	
	        		    			
	    			var startScriptType = lovProvider.indexOf("<LANGUAGE>")+"<LANGUAGE>".length;
        			var endScriptType = lovProvider.indexOf("</LANGUAGE>");
        			var scriptType = lovProvider.substring(startScriptType,endScriptType);
        			
        			// !!!! Maybe try with another solution
        			if (scriptType == "ECMAScript") 	// saw in ScripDetail.java
        				scriptType = "Javascript";
        			else if (scriptType == "groovy") 	// saw in ScripDetail.java
        				scriptType = "Groovy";
        			
        			lovScriptPanel.scriptTypeCombo.setValue(scriptType);
        			
        			var startScript = lovProvider.indexOf("<SCRIPT>")+"<SCRIPT>".length;
        			var endScript = lovProvider.indexOf("</SCRIPT>");
        			lovScriptPanel.scriptQuery.setValue(lovProvider.substring(startScript,endScript));
        			
        			lovFormPanel2.add(lovScriptPanel.scriptTypeCombo);
	        		lovFormPanel2.add(lovScriptPanel.scriptQuery);
				}
	    		
	    		else if (this.lovInputTypeCombo.value == "FIX_LOV")
				{   	    	
	    			lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.fixedListWizard'));
	    			
	    			var lovFixedListPanel = Ext.create("Sbi.behavioural.lov.LOVFixedListBottomPanel",{});
	    			lovFixedListPanel.create();
	    			
	    			var fixLovStore = lovFixedListPanel.fixLovStore;	    			
	    			
    				var startFixLov = lovProvider.indexOf("<ROWS>")+"<ROWS>".length;
        			var endFixLov = lovProvider.indexOf("</ROWS>");
        			var fixLovRows = lovProvider.substring(startFixLov,endFixLov);
    				var listRows = fixLovRows.split("<ROW ");   				  	
    				
    				fixLovStore.removeAll();
//    				
    				for (var i=1; i<listRows.length; i++)
					{
    					var valueStart = listRows[i].indexOf("VALUE=")+"VALUE=".length + 1;
        				var valueEnd = listRows[i].indexOf("\" DESCRIPTION");
        				var valueFixLov = listRows[i].substring(valueStart,valueEnd);
        				
        				var descriptionStart = listRows[i].indexOf("DESCRIPTION=")+"DESCRIPTION=".length + 1;
        				var descriptionEnd = listRows[i].indexOf("/>")-1;
        				var descriptionFixLov = listRows[i].substring(descriptionStart,descriptionEnd);
        				
        				fixLovStore.insert(i,{value: valueFixLov, description: descriptionFixLov});        				
					}
    				    				
	    			var lovFixedListForm = lovFixedListPanel.lovFixedListForm;
	    			var fixLovGrid = lovFixedListPanel.fixLovGrid;
 				
    				lovFixedListForm.getComponent("FixLovValue").setValue("");
    				lovFixedListForm.getComponent("FixLovDescription").setValue(""); 
   				
    				fixLovGrid.reconfigure(fixLovStore);
    				fixLovGrid.update();
    				
    				lovFormPanel2.add(lovFixedListForm);
    				lovFormPanel2.add(lovFixedListPanel.infoPanel);
	        		lovFormPanel2.add(fixLovGrid);
				}
	    		
	    		else if (this.lovInputTypeCombo.value == "JAVA_CLASS")
				{
	    			lovFormPanel2.setTitle(LN('sbi.behavioural.lov.details.javaClassWizard'));
	    			
	    			var lovJavaClassPanel = Ext.create("Sbi.behavioural.lov.LOVJavaClassBottomPanel",{});
	        		lovJavaClassPanel.create();	        		
	        		
	        		var startJavaClassName = lovProvider.indexOf("<JAVA_CLASS_NAME>")+"<JAVA_CLASS_NAME>".length;
					var endJavaClassName = lovProvider.indexOf("</JAVA_CLASS_NAME>");
					var javaClassName = lovProvider.substring(startJavaClassName,endJavaClassName);
						        		
	        		lovJavaClassPanel.javaClassName.setValue(javaClassName);
	        		
	        		lovFormPanel2.add(lovJavaClassPanel.javaClassName);
				}    		
	    		
	    		lovFormPanel2.show();
    		}   		
    		
    		else	// LOV ID = 0
			{
    			lovFormPanel2.hide();
    			
    			lovFormPanel1.items.items[0].setValue(0);
    			// LOV LABEL
	    		lovFormPanel1.items.items[1].setValue("");
	    		// LOV NAME
	    		lovFormPanel1.items.items[2].setValue("");
	    		// LOV DESCRIPTION
	    		lovFormPanel1.items.items[3].setValue("");
	    		
	    		
	    		// LOV INPUT TYPE
	    		this.lovInputTypeCombo.setValue("");	    		
	    		this.lovInputTypeCombo.markInvalid(LN('sbi.behavioural.lov.details.inputTypeMissing'));	    		
			}	
    			    		
//    		Sbi.debug('[OUT] LOVDetailPanel - setFormState()');
    	},
    	
    	getFormState: function(phaseOfLov)
    	{     
    		Sbi.debug('[IN] LOVDetailPanel - getFormState()');
    	    		
    		/* Take the values of the textfields in the Panel 1 */
    		var getLovId = this.lovId.value;
    		var getLovName = this.lovName.value;
    		var getLovDescription = this.lovDescription.value;
    		var getLovLabel = this.lovLabel.value; 
    		
    		var lovFormPanel2 = this.tabPanel.items.items[0].getComponent("PANEL2");
    		    		    		    		    		    		    		
    		if (this.lovInputTypeCombo.getValue() == "QUERY")
			{        			
    			var getLovProvider = "";
    			    			
    			var startDataSource = this.lovProvider.value.indexOf("<CONNECTION>")+"<CONNECTION>".length;
    			var endDataSource = this.lovProvider.value.indexOf("</CONNECTION>");
    			var oldDataSource = this.lovProvider.value.substring(startDataSource,endDataSource);
    			
    			var startStatement = this.lovProvider.value.indexOf("<STMT>")+"<STMT>".length;
    			var endStatement = this.lovProvider.value.indexOf("</STMT>");
    			var oldStatement = this.lovProvider.value.substring(startStatement,endStatement);
    			
    			var currentDataSource = lovFormPanel2.items.items[0].value;
    			var currentQuery = lovFormPanel2.items.items[1].value;
    			
    			if (phaseOfLov == "testPhase" && (getLovId == 0 || oldDataSource != currentDataSource || 
    					oldStatement != currentQuery) || this.lovProvider.value.indexOf(this.lovInputTypeCombo.value)<0) // Test phase
//    			if (phaseOfLov == "testPhase" && getLovId == 0 && 
//    					(oldDataSource != this.dataSourceCombo.getValue() || 
//    					oldStatement != this.dataSourceQuery.getValue())) // Test phase
				{				    				
    				getLovProvider = 
    					"<QUERY>" + 
    						"<CONNECTION>" + currentDataSource + "</CONNECTION>" + 
    						"<STMT>" + currentQuery + "</STMT>" + 
    						"<VALUE-COLUMN>" + "</VALUE-COLUMN>" + 
    						"<DESCRIPTION-COLUMN>" + "</DESCRIPTION-COLUMN>" + 
    						"<VISIBLE-COLUMNS>" + "</VISIBLE-COLUMNS>" + 
    						"<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" + 
    						"<LOVTYPE>" + "</LOVTYPE>" + 
    						"<TREE-LEVELS-COLUMNS>" + "</TREE-LEVELS-COLUMNS>" +
						"</QUERY>";
    				    				
    				// Need to attach this XML query to LOV provider for later filling with data    				
    				
    				if (getLovId == 0 || oldDataSource != currentDataSource || oldStatement != currentQuery)
					{
    					this.lovProvider.value = getLovProvider;
					}
    					    				
				}
    			else // Save phase
				{   	    				
    				getLovProvider = this.lovProvider.value; 
				}
    			    			
    			var getInputTypeCd = "QUERY";
    			var getInputTypeId = 1; 
			}    
    		
    		else if (this.lovInputTypeCombo.getValue() == "SCRIPT")
			{
    			var getInputTypeCd = "SCRIPT";
    			var getInputTypeId = 2; 
    			var getLovProvider = "";
    			
    			var startScriptType = this.lovProvider.value.indexOf("<LANGUAGE>")+"<LANGUAGE>".length;
    			var endScriptType = this.lovProvider.value.indexOf("</LANGUAGE>");
    			var oldScriptType = this.lovProvider.value.substring(startScriptType,endScriptType);
    			    			
    			var startScript = this.lovProvider.value.indexOf("<SCRIPT>")+"<SCRIPT>".length;
    			var endScript = this.lovProvider.value.indexOf("</SCRIPT>");
    			var oldScript = this.lovProvider.value.substring(startScript,endScript);
    			
    			var currentScript = lovFormPanel2.items.items[0].value;
    			var currentDescription = lovFormPanel2.items.items[1].value;
    			
    			// !!!! Maybe change...
    			if (oldScriptType == "ECMAScript") 	// saw in ScripDetail.java
    				oldScriptType = "Javascript";
    			else if (oldScriptType == "groovy") 	// saw in ScripDetail.java
    				oldScriptType = "Groovy";
    			
    			
    			if (phaseOfLov == "testPhase" && (getLovId == 0 || 
    					oldScriptType != currentScript || 
    					oldScript != currentDescription) || this.lovProvider.value.indexOf(this.lovInputTypeCombo.value)<0) // Test phase
				{
    				var scriptType = currentScript;
    				
    				// !!!! Maybe change... (different direction then one in setFormState)
        			if (scriptType == "Javascript") 	// saw in ScripDetail.java
        				scriptType = "ECMAScript";
        			else if (scriptType == "Groovy") 	// saw in ScripDetail.java
        				scriptType = "groovy";
    				
    				// ScriptDetail.java
    				getLovProvider = 
    					"<SCRIPTLOV>" +
		    				"<SCRIPT>"+currentDescription+"</SCRIPT>" +	
		    				"<VALUE-COLUMN>"+"</VALUE-COLUMN>" +
		    				"<DESCRIPTION-COLUMN>"+"</DESCRIPTION-COLUMN>" +
		    				"<VISIBLE-COLUMNS>"+"</VISIBLE-COLUMNS>" +
		    				"<INVISIBLE-COLUMNS>"+"</INVISIBLE-COLUMNS>" +
		    				"<LANGUAGE>" + scriptType + "</LANGUAGE>" +
		    				"<LOVTYPE>"+ "</LOVTYPE>" +
		    				"<TREE-LEVELS-COLUMNS>"+"</TREE-LEVELS-COLUMNS>" +
	    				"</SCRIPTLOV>";
    				        			
    				if (getLovId == 0 || oldScriptType != currentScript || oldScript != currentDescription)
    					this.lovProvider.value = getLovProvider;   
				}   
    			else // Save phase
				{   		
    				getLovProvider = this.lovProvider.value; 
				}
			} 
    		
    		else if (this.lovInputTypeCombo.getValue() == "FIX_LOV")
			{
    			var getInputTypeCd = "FIX_LOV";
    			var getInputTypeId = 3; 
    			var getLovProvider = "";   	
    			
    			var oldLovProviderN = this.lovProvider.value; 
    			
    			var fixLovStore = lovFormPanel2.items.items[2].getStore();
    			
    			if (phaseOfLov == "testPhase" && getLovId == 0 || oldLovProviderN.indexOf("FIXLISTLOV") < 0) // Test phase
				{      				
	    			getLovProvider = "<FIXLISTLOV>";
	    			getLovProvider += "<ROWS>";
				
	    			var fixLovCount = fixLovStore.getCount();
	    			
	    			var fixLovValue = "";
	    			var fixLovDescription = "";
	    			
	    			for (var i=0; i<fixLovCount; i++)
					{
	    				fixLovValue = fixLovStore.data.items[i].data.value;
	    				fixLovDescription = fixLovStore.data.items[i].data.description;
	    				
	    				getLovProvider += "<ROW" +
						  " VALUE=\"" + fixLovValue + "\"" +
						  " DESCRIPTION=\"" + fixLovDescription + "\"" +
						  "/>";
					}
	    			
	    			getLovProvider += "</ROWS>";
	    			getLovProvider += "<VALUE-COLUMN>"+"</VALUE-COLUMN>" +
	    					  "<DESCRIPTION-COLUMN>"+"</DESCRIPTION-COLUMN>" +
	    					  "<VISIBLE-COLUMNS>"+"</VISIBLE-COLUMNS>" +
	    					  "<INVISIBLE-COLUMNS>"+"</INVISIBLE-COLUMNS>" +
	    					  "<LOVTYPE>" + "</LOVTYPE>" +
	    					  "<TREE-LEVELS-COLUMNS>"+"</TREE-LEVELS-COLUMNS>" +
	    					  "</FIXLISTLOV>";
	    			
	    			if (getLovId == 0 || oldLovProviderN.indexOf("FIXLISTLOV") < 0)
    					this.lovProvider.value = getLovProvider;   
				}
    			
    			else
				{   	    				
    				if (oldLovProviderN.indexOf("FIXLISTLOV") > -1)
					{
    					var oldLovProvider = this.lovProvider.value;
        				var newFixLovProvider = "";
        				
        				var startFixLovProv = oldLovProvider.indexOf("<FIXLISTLOV>")+"<FIXLISTLOV>".length;
        				var endFixLovProv = oldLovProvider.indexOf("<VALUE-COLUMN>");
        				
        				var fixLovProvider = oldLovProvider.substring(startFixLovProv,endFixLovProv);
        				
        				newFixLovProvider = "<ROWS>";
        				
    	    			var fixLovCount = fixLovStore.getCount();
    	    			
    	    			var fixLovValue = "";
    	    			var fixLovDescription = "";
    	    			
    	    			for (var i=0; i<fixLovCount; i++)
    					{
    	    				fixLovValue = fixLovStore.data.items[i].data.value;
    	    				fixLovDescription = fixLovStore.data.items[i].data.description;
    	    				
    	    				newFixLovProvider += "<ROW" +
    						  " VALUE=\"" + fixLovValue + "\"" +
    						  " DESCRIPTION=\"" + fixLovDescription + "\"" +
    						  "/>";
    					}
    	    			
    	    			newFixLovProvider += "</ROWS>";
        				
        				getLovProvider = oldLovProvider.substring(0,startFixLovProv) + newFixLovProvider + oldLovProvider.substring(endFixLovProv,oldLovProvider.length);
					}  
				}
    			
			} 
    		
    		else if (this.lovInputTypeCombo.getValue() == "JAVA_CLASS")
			{
    			var getInputTypeCd = "JAVA_CLASS";
    			var getInputTypeId = 4; 
    			var getLovProvider = "";
    			
    			var javaClassName = lovFormPanel2.items.items[0].value;  		
		
    			if (phaseOfLov == "testPhase" && getLovId == 0 || this.lovProvider.value.indexOf(this.lovInputTypeCombo.value)<0)
				{
    				getLovProvider = 
    					"<JAVACLASSLOV>" +
    						"<JAVA_CLASS_NAME>" + javaClassName + "</JAVA_CLASS_NAME>" +	
    						"<VISIBLE-COLUMNS>" + "</VISIBLE-COLUMNS>" +
    						"<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" +
    						"<LOVTYPE>" + "</LOVTYPE>" +
    						"<VALUE-COLUMN>" + "</VALUE-COLUMN>" +
    						"<DESCRIPTION-COLUMN>" + "</DESCRIPTION-COLUMN>" +
    					"</JAVACLASSLOV>";
    				
    				this.lovProvider.value = getLovProvider; 
				}					
    			
    			else
				{
    				getLovProvider = this.lovProvider.value; 
				}
			}
    		
    		else if (this.lovInputTypeCombo.getValue() == "DATASET")
			{
    			var getInputTypeCd = "DATASET";
    			var getInputTypeId = 5; 
    			var getLovProvider = "";
			}    			
    		
    		var getSelectionType = "";
    		
    		var modelOfCurrentLov = Ext.create
			(
				"Sbi.behavioural.lov.LOVModel",
				
				{
					 LOV_ID: getLovId,
			         LOV_NAME: getLovName,
			         LOV_DESCRIPTION: getLovDescription,
			         LOV_PROVIDER: getLovProvider,
			         I_TYPE_CD: getInputTypeCd,
			         I_TYPE_ID: getInputTypeId,
			         LOV_LABEL: getLovLabel,
			         SELECTION_TYPE: getSelectionType
				}
			);    		
    		
    		Sbi.debug('[OUT] LOVDetailPanel - getFormState()');
    		
    		return modelOfCurrentLov;
    	}
    	
	}
	
);