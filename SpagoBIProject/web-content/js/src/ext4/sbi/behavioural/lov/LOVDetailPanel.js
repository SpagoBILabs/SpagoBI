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
	    	    	 		items: [ this.panel1, this.panel2, this.panel3 ],
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
	    	
    	},
    	
    	takeValues: function()
    	{
    		return this.resultPanel.getComponent("TEST_LOV_PANEL").takeValues();
    	},
    	
    	setValues: function(data)
    	{
    		this.resultPanel.getComponent("TEST_LOV_PANEL").setValues(data);
    	},
    	
    	updatePanel: function(contextName, lovConfig, lovProvider)
    	{
    		console.log("[IN] updatePanel() LOVDetailPanel");
    		
    		//console.log(lovProvider);
    		
    		this.resultPanel.removeAll();
    		
    		var lovTest = Ext.create
			(
				"Sbi.behavioural.lov.TestLovPanel2", 
				
				{
					contextName: contextName, 
					lovConfig: lovConfig,
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
    				
    				var startStatement = -1;
        			var endStatement = -1;
        			var oldStatement = "";
    				
    				// Return old LOV value for new LOV value (error happened, so we are losing nothing)
    				//this.lovProvider.value = this.lovProvider.lastValue;
    				
    				this.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].hide();
    				this.getComponent("TAB_PANEL_RESULTS").setActiveTab(0);    	
        			
    				console.log("#@#@#@#@#@#@#@#");    				
    				console.log(this.lovProvider.value);
    				console.log(this.lovProvider.lastValue);
    				
    				var lovProviderValue = "";
    				
    				if (this.lovId.value == 0)
    					lovProviderValue = this.lovProvider.value;   
    				else
    					lovProviderValue = this.lovProvider.lastValue;          			     		
        			
    				if (lovProviderValue.indexOf("QUERY") >= 0)
    				{
    					startStatement = lovProviderValue.indexOf("<STMT>")+"<STMT>".length;
            			endStatement = lovProviderValue.indexOf("</STMT>");
            			oldStatement = lovProviderValue.substring(startStatement,endStatement);
            			
            			this.dataSourceQuery.setValue(oldStatement);
            			
            			var startIndex = lovProviderValue.indexOf("<CONNECTION>");
            			var endIndex = lovProviderValue.indexOf("</CONNECTION>");
            			
            			var dsType = lovProviderValue.substring(startIndex + "<CONNECTION>".length,endIndex);
            			
            			this.dataSourceCombo.setValue(dsType);
            			
            			if (this.lovId.value == 0)
        				{            				            			
	            			this.dataSourceQuery.markInvalid("Wrong syntax for selected data source query type: " + dsType + ". Try again...");
        				}
    				}
    				
    				else if (lovProviderValue.indexOf("SCRIPTLOV") >= 0)
					{
    					startStatement = lovProviderValue.indexOf("<SCRIPT>")+"<SCRIPT>".length;
    					endStatement = lovProviderValue.indexOf("</SCRIPT>");
    					oldStatement = lovProviderValue.substring(startStatement,endStatement);   
    					
    					this.scriptQuery.setValue(oldStatement);
    					
    					var startScriptType = lovProviderValue.indexOf("<LANGUAGE>")+"<LANGUAGE>".length;
            			var endScriptType = lovProviderValue.indexOf("</LANGUAGE>");
            			var scriptType = lovProviderValue.substring(startScriptType,endScriptType);
            			
            			// !!!! Ovim resenjem uopste nisam odusevljen
            			if (scriptType == "ECMAScript") 	// vidjeno u ScripDetail.java
            				scriptType = "Javascript";
            			else if (scriptType == "groovy") 	// vidjeno u ScripDetail.java
            				scriptType = "Groovy";
            			
            			this.scriptTypeCombo.setValue(scriptType);
    					
    					if (this.lovId.value == 0)
        				{
    						this.scriptQuery.markInvalid("Wrong syntax for selected script type: " + scriptType + ". Try again...");
        				}
					}   
    				
    			},
    			
    			this
	    	);
    		
    		lovTest.on('lovTypeChanged',function(type){
    			console.log("[START] lovTypeChanged EVENT");
				this.resultPanel.remove(lovTest,'true');
				lovConfig.lovType=type;
				lovTest = Ext.create('Sbi.behavioural.lov.TestLovPanel2',{contextName: contextName,lovConfig:lovConfig, lovProvider: lovProvider, tabPanelHeight: window.innerHeight}); //by alias
				//addLovTestEvents(lovTest, lovConfig, contextName);
				this.resultPanel.add(lovTest);
				console.log("LLLL");
			},this);
    		
    		this.resultPanel.add
    		(
    			lovTest
			);
    		
    		this.resultPanel.update(); 
    		
//    		console.log(this.resultPanel);  		
//    		console.log("----*");    	
    		
    		console.log("[OUT] updatePanel() LOVDetailPanel");
    	},    	
    	
    	
    	initFields: function()
    	{
    		var globalScope = this;    		    		
    		
    		//this.testLov = Ext.create("Sbi.behavioural.lov.TestLovPanel2",{});
    		
    		this.lovId = Ext.create
    		(
    			"Ext.form.field.Hidden",
    			
    			{
    				name: "LOV_ID",
    				id: "LovId",
    				readOnly: true
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
    			}
			);
    		
    		this.lovInputTypeCombo = new Ext.create
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
			                globalScope.selectedInputType(this.getValue());
			            }
			        }
	    	    }
			);
    		
    		this.panel1 = Ext.create
    		(
    			"Ext.panel.Panel",
    			
    			{
    				width: "100%",
    				// (top, right, bottom, left)
    				padding: '10 10 5 10',
    				
    				items: 
    					
					[ this.lovId, this.lovLabel, this.lovName, this.lovDescription, 
					  this.lovProvider, this.lovInputTypeCombo, this.lovInputTypeCd,
					  this.lovInputTypeId, this.lovSelectionType ]    				
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
        	
    	
    		var dataSourceStore = Ext.create
        	(
    			'Ext.data.Store',
	    		
    			{
	        		model: "DataSourceModel",
	        		autoLoad: true,
	        		
	        		proxy: 
	        		{
	        			type: 'rest',	        			
	        			//extraParams : { DOMAIN_TYPE: "INPUT_TYPE" },
	        			
	        			url:  this.services["getDataSources"],	
	        			
	        			reader: 
	        			{
	        				type:"json",
	        				root: "root"
	        			}
	        		}
	        	}
			);
        	
    		dataSourceStore.on
        	(
    			'load', 
    			
    			function(dataSourceStore)
    			{ 
    			}
			);
    		
    		this.dataSourceCombo = new Ext.create
    		(
				'Ext.form.ComboBox', 
	    		
				{
	    			fieldLabel: LN('sbi.behavioural.lov.details.dataSourceLabel'),
	    	        store: dataSourceStore,
	    	        name: "DATASOURCE_ID",
	    	        id: "DATA_SOURCE_COMBO",
	    	        displayField:'DATASOURCE_LABEL',
	    	        valueField:'DATASOURCE_LABEL',
	    	        // (top, right, bottom, left)
	    	        padding: "10 0 10 0",
	    	        editable: false,
	    	        allowBlank: false
	    	    }
			); 
    		
    		this.dataSourceQuery = Ext.create
    		(
				"Ext.form.field.TextArea",
				
				{					
					fieldLabel: LN('sbi.behavioural.lov.details.queryDescription'), 
					id: "DATA_SOURCE_QUERY",
					height: 100,
					width: 500,
					padding: '10 0 10 0'
				}
    		);
    		
    		    		
    		Ext.define
    		(
				"ScriptTypeModel", 
				
				{
					extend: 'Ext.data.Model',
					fields: [ "VALUE_NM", "VALUE_DS", "VALUE_ID", "VALUE_CD" ]
				}
			);
        	    		
        	var scriptTypeStore = Ext.create
        	(
    			'Ext.data.Store',
	    		
    			{
	        		model: "ScriptTypeModel",
	        		autoLoad: true,
	        		
	        		proxy: 
	        		{
	        			type: 'rest',   			
	        			extraParams : { DOMAIN_TYPE: "SCRIPT_TYPE" },
	        			
	        			url:  this.services['getDomains'],	
	        			
	        			reader: 
	        			{
	        				type:"json"
	        			}
	        		}
	        	}
			);
        	
        	scriptTypeStore.on
        	(
    			"load", 
    			
    			function(scriptTypeStore)
    			{ 
    				//console.log(scriptTypeStore);
    			}
			);
        	
    		this.scriptTypeCombo = new Ext.create
    		(
				'Ext.form.ComboBox', 
	    		
				{
	    			fieldLabel: LN('sbi.behavioural.lov.details.scriptType'),
	    	        store: scriptTypeStore,	    	        
	    	        name: "SCRIPT_TYPE",
	    	        id: "SCRIPT_TYPE_COMBO",
	    	        displayField:'VALUE_NM',
	    	        valueField:'VALUE_NM',
	    	        editable: false,
	    	        allowBlank: false,
	    	        padding: "10 0 10 0",
	    	        
	    	        listeners: 
			        {
			            change: function() 
			            {
			            	console.log("AAAA");
			            }
			        }
	    	    }
			);
    		
    		this.scriptQuery = Ext.create
    		(
				"Ext.form.field.TextArea",
				
				{
					id: "SCRIPT_QUERY",
					fieldLabel: LN('sbi.behavioural.lov.details.scriptDescription'),
					height: 100,
					width: 500,
					padding: '10 0 10 0'
				}
    		);
    		
    		this.panel2 = Ext.create
    		(
    			"Ext.panel.Panel",
    			
    			{
    				width: "100%",
    				//border: false,
    				// (top, right, bottom, left)
    				padding: '5 10 5 10',
    				id: "PANEL2",
    				
    				items: 
					[ this.dataSourceCombo, this.dataSourceQuery ]    				
    			}
    		);
    		
    		this.panel3 = Ext.create
    		(
    			"Ext.panel.Panel",
    			
    			{
    				width: "100%",
    				// (top, right, bottom, left)
    				padding: '5 10 5 10',
    				id: "PANEL3",
    				
    				items: 
					[ this.scriptTypeCombo, this.scriptQuery  ]    				
    			}
    		);
    		
    		
    	},
    	
    	
    	selectedInputType: function(inputTypeCD)
		{    			
			var type = -1;
			//this.panel2.show();
			
			this.panel2.hide();
			this.panel3.hide();
			
			if (inputTypeCD == "QUERY")
			{
				this.panel2.show();
			}
			else if (inputTypeCD == "SCRIPT")
			{
				this.panel3.show();
			}
			
		},
    	
    	getValues: function()
    	{
    		var values = this.callParent();
    		return values;
    	},    
    	
    	setFormState: function(values)
    	{    		
    		this.selectedInputType(values.I_TYPE_CD);
    		
//    		console.log("PROVERA ID-eva");
//			console.log(values.LOV_ID);
//			console.log(values.I_TYPE_ID);
    		/* *** 	Problem solved: Keeping panel 2 details (combo and text area) from 
    		 * 		the old record in the new one. */
    		
    		// Needed for differentiating between old (existing) record and the new one
    		if (values.LOV_ID != 0)
    		{           			
    			this.lovInputTypeCombo.setValue(values.I_TYPE_CD);
    			    			
    			var query = values.LOV_PROVIDER;
    			
    			if (values.I_TYPE_CD == "QUERY") 
    			{
        			var startIndex = query.indexOf("<CONNECTION>");
        			var endIndex = query.indexOf("</CONNECTION>");
        			
        			this.dataSourceCombo.setValue(query.substring(startIndex + "<CONNECTION>".length,endIndex));
        			
        			var startIndex = query.indexOf("<STMT>");
        			var endIndex = query.indexOf("</STMT>");
        			
        			this.dataSourceQuery.setValue(query.substring(startIndex + "<STMT>".length,endIndex));
    			}  
        		
    			else if (values.I_TYPE_CD == "SCRIPT") 
    			{
    				var startScriptType = query.indexOf("<LANGUAGE>")+"<LANGUAGE>".length;
        			var endScriptType = query.indexOf("</LANGUAGE>");
        			var scriptType = query.substring(startScriptType,endScriptType);
        			
        			// !!!! Ovim resenjem uopste nisam odusevljen
        			if (scriptType == "ECMAScript") 	// vidjeno u ScripDetail.java
        				scriptType = "Javascript";
        			else if (scriptType == "groovy") 	// vidjeno u ScripDetail.java
        				scriptType = "Groovy";
        			
        			this.scriptTypeCombo.setValue(scriptType);
        			
        			var startScript = query.indexOf("<SCRIPT>")+"<SCRIPT>".length;
        			var endScript = query.indexOf("</SCRIPT>");
        			this.scriptQuery.setValue(query.substring(startScript,endScript));
    			}        		
        		
        		//this.selectedInputType(values.I_TYPE_CD);				
    		}
    		else
			{
    			this.lovInputTypeCombo.setValue(values.I_TYPE_CD);
    			
    			// For query (data source)
    			this.dataSourceCombo.setValue("");
    			this.dataSourceQuery.setValue("");
    			
    			// For script
    			this.scriptTypeCombo.setValue("");
    			this.scriptQuery.setValue("");
			}
    		
    		this.getForm().setValues(values);    		
    	},
    	
    	getFormState: function(phaseOfLov)
    	{    		
    		var getLovId = this.lovId.value;
    		var getLovName = this.lovName.value;
    		var getLovDescription = this.lovDescription.value;
    		var getLovLabel = this.lovLabel.value;
    		
    		console.log(" +++++ getFormState +++++ ");
    		
    		if (this.lovInputTypeCombo.getValue() == "QUERY")
			{    
    			
    			var getLovProvider = "";
    			
    			var startDataSource = this.lovProvider.value.indexOf("<CONNECTION>")+"<CONNECTION>".length;
    			var endDataSource = this.lovProvider.value.indexOf("</CONNECTION>");
    			var oldDataSource = this.lovProvider.value.substring(startDataSource,endDataSource);
    			
    			var startStatement = this.lovProvider.value.indexOf("<STMT>")+"<STMT>".length;
    			var endStatement = this.lovProvider.value.indexOf("</STMT>");
    			var oldStatement = this.lovProvider.value.substring(startStatement,endStatement);
    			
    			// Prethodna varijanta: zakomentarisano 9.4. u 1:06 sati 
    			if (phaseOfLov == "testPhase" && (getLovId == 0 || oldDataSource != this.dataSourceCombo.getValue() || 
    					oldStatement != this.dataSourceQuery.getValue())) // Test phase
//    			if (phaseOfLov == "testPhase" && getLovId == 0 && 
//    					(oldDataSource != this.dataSourceCombo.getValue() || 
//    					oldStatement != this.dataSourceQuery.getValue())) // Test phase
				{
//    				var dataSourceComboValue = "";
//    				var dataSourceQueryValue = "";
//    				
//    				if (getLovId == 0)
//    				{
//    					dataSourceComboValue = this.dataSourceCombo.getValue();
//    					dataSourceQueryValue = this.dataSourceQuery.getValue();
//    				}
//    				else 
//    				{
//    					if (oldDataSource != this.dataSourceCombo.getValue())
//    						dataSourceComboValue = this.dataSourceCombo.getValue();
//    					else
//    						dataSourceComboValue = oldDataSource;
//    					
//    					if (oldStatement != this.dataSourceQuery.getValue())
//    						dataSourceQueryValue = this.dataSourceQuery.getValue();
//    					else
//    						dataSourceQueryValue = oldStatement;
//    				}
    				
    				// Za svaki tip LOV-a podaci o XML formi za upit se mogu naci u Java klasama na ovoj putanji: 
    				// C:\eclipse-jee-luna-SR1a-win32-x86_64\MyWorkspace\SpagoBIDAO\src\it\eng\spagobi\behaviouralmodel\lov\bo
    				getLovProvider = 
    					"<QUERY>" + 
    						"<CONNECTION>" + this.dataSourceCombo.getValue() + "</CONNECTION>" + 
    						"<STMT>" + this.dataSourceQuery.getValue() + "</STMT>" + 
    						"<VALUE-COLUMN>" + "</VALUE-COLUMN>" + 
    						"<DESCRIPTION-COLUMN>" + "</DESCRIPTION-COLUMN>" + 
    						"<VISIBLE-COLUMNS>" + "</VISIBLE-COLUMNS>" + 
    						"<INVISIBLE-COLUMNS>" + "</INVISIBLE-COLUMNS>" + 
    						"<LOVTYPE>" + "</LOVTYPE>" + 
    						"<TREE-LEVELS-COLUMNS>" + "</TREE-LEVELS-COLUMNS>" +
						"</QUERY>";
    				
    				// Need to attach this XML query to LOV provider for later filling with data
    				
    				
    				if (getLovId == 0 || oldDataSource != this.dataSourceCombo.getValue())
    					this.lovProvider.value = getLovProvider;    				
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
    			
    			// !!!! Ovim resenjem uopste nisam odusevljen
    			if (oldScriptType == "ECMAScript") 	// vidjeno u ScripDetail.java
    				oldScriptType = "Javascript";
    			else if (oldScriptType == "groovy") 	// vidjeno u ScripDetail.java
    				oldScriptType = "Groovy";
    			
	    		console.log("-+-+-+-+-");
	    		console.log(getLovId);
	    		console.log(oldScriptType);
	    		console.log(this.scriptTypeCombo.getValue());
	    		console.log(oldScript);
	    		console.log(this.scriptQuery.getValue());
    			
    			if (phaseOfLov == "testPhase" && (getLovId == 0 || 
    					oldScriptType != this.scriptTypeCombo.getValue() || 
    					oldScript != this.scriptQuery.getValue())) // Test phase
				{
    				var scriptType = this.scriptTypeCombo.getValue();
    				
    				// !!!! Ovim resenjem uopste nisam odusevljen (obrnut smer od setFormState)
        			if (scriptType == "Javascript") 	// vidjeno u ScripDetail.java
        				scriptType = "ECMAScript";
        			else if (scriptType == "Groovy") 	// vidjeno u ScripDetail.java
        				scriptType = "groovy";
    				
    				// ScriptDetail.java
    				getLovProvider = 
    					"<SCRIPTLOV>" +
		    				"<SCRIPT>"+this.scriptQuery.getValue()+"</SCRIPT>" +	
		    				"<VALUE-COLUMN>"+"</VALUE-COLUMN>" +
		    				"<DESCRIPTION-COLUMN>"+"</DESCRIPTION-COLUMN>" +
		    				"<VISIBLE-COLUMNS>"+"</VISIBLE-COLUMNS>" +
		    				"<INVISIBLE-COLUMNS>"+"</INVISIBLE-COLUMNS>" +
		    				"<LANGUAGE>" + scriptType + "</LANGUAGE>" +
		    				"<LOVTYPE>"+ "</LOVTYPE>" +
		    				"<TREE-LEVELS-COLUMNS>"+"</TREE-LEVELS-COLUMNS>" +
	    				"</SCRIPTLOV>";
    				        			
    				if (getLovId == 0 || oldScriptType != this.scriptTypeCombo.getValue())
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
			}    			
    		else if (this.lovInputTypeCombo.getValue() == "JAVA_CLASS")
			{
    			var getInputTypeCd = "JAVA_CLASS";
    			var getInputTypeId = 4; 
    			var getLovProvider = "";
			}    			
    		else if (this.lovInputTypeCombo.getValue() == "DATASET")
			{
    			var getInputTypeCd = "DATASET";
    			var getInputTypeId = 5; 
    			var getLovProvider = "";
			}    			
    		
    		var getSelectionType = "";
    		
    		var model1 = Ext.create
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
    		
    		return model1;
    	}
    	
	}
	
);