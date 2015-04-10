/**
 * 
 */
/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
  

/**
 * Object name 
 * 
 * 
 * Public Properties
 * 
 * [list]
 * 
 * 
 * Public Methods
 * 
 *  [list]
 * 
 * 
 * Public Events
 * 
 *  [list]
 * 
 * Authors
 * 
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.define
(
	'Sbi.behavioural.lov.TestLovPanel2', 
	
	{
    	extend: 'Ext.panel.Panel', 
    	lovTestConfiguration: null,
    	
    	config: {
        	layout: 'border',
        	toolbarHeight: 50,
        	border: false
        },
    	
    	constructor: function(config) 
    	{
    		console.log("[IN] constructor() TestLovPanel2");
    		
    		this.services = {};    		
    		this.initServices();    		
    		
    		this.treeLov = (config.lovConfig.lovType && (config.lovConfig.lovType=='tree'|| config.lovConfig.lovType=='treeinner'));
    		    		
    		var typeStoreValue = "simple";
    		
    		if(config.lovConfig.lovType)
    		{
    			typeStoreValue = config.lovConfig.lovType;
    		}
    		
    	    var typeStore = Ext.create
    	    (
	    		'Ext.data.Store', 
	    		
	    		{
	    	        fields: ['type','description'],
	    	       
	    	        data : 	
    	        	[
    	        	 	{ type:'simple', description:LN('sbi.behavioural.lov.type.simple')}, 
    	                { type:'tree', description:LN('sbi.behavioural.lov.type.tree')},
    	                { type:'treeinner', description:LN('sbi.behavioural.lov.type.treeinner')}
	                ]
	    		}
    		);
    	    
    		this.comboType = Ext.create
    		(
				'Ext.form.ComboBox', 
				
				{
	    	        store: typeStore,
	    	        displayField: 'description',
	    	        valueField: 'type',
	    	        queryMode: 'local',
	    	        value: typeStoreValue,
	    	        triggerAction: 'all',
	    	        emptyText: LN('sbi.behavioural.lov.select.type'),
	    	        selectOnFocus:true,
	    	        width:135
				}
			);
    		
//    		var saveButton = 
//    		{	
//    				handler: this.save,
//    				scope: this
//    		};
//
//    		var backButton = 
//    		{
//    				handler: this.back,
//    				scope: this
//    		};
    		
    		this.dockedItems = 
			[
			 	{
	    	        xtype: 'toolbar',
	    	        dock: 'top',
	    	        items: ['->',this.comboType]
			 	}
		 	];
    		
    		// ???????????????????????????????
    		Ext.QuickTips.init();
    		    		
    		/* The lower part of the result panel - PREVIEW */
    		this.lovTestPreview = Ext.create
    		(
				'Sbi.behavioural.lov.TestLovResultPanel2',
				
				{
					region: 'south',
					height: 315, 
					lovProvider: config.lovProvider,
					treeLov: this.treeLov
				}
			); 
    		
    		/* The upper part of the result panel - COLUMNS OF THE RESULT */
    		this.lovTestConfiguration = Ext.create
    		(
				'Sbi.behavioural.lov.TestLovConfigurationGridPanel2',
				
				{
					lovConfig: config.lovConfig, 
					border: false,
					parentStore: this.lovTestPreview.store, 
					lovType: typeStoreValue, 
					lovProvider: config.lovProvider,
					flex: 1
				}
			);     		
    		
    		this.lovTestPreview.on('storeLoad',this.lovTestConfiguration.onParentStoreLoad,this.lovTestConfiguration);
    		//console.log("{{{}}}} {{{}}}}");
    		var lovConfigurationPanelItems = [this.lovTestConfiguration];
    		
//    		console.log("#########1");
//    		console.log(lovConfigurationPanelItems);
    		
    		if(this.treeLov){
    			console.log("######### Tree LOV [TestLovPanel2] #########");
    			//Tree lov panel
    			this.lovTestConfigurationTree = Ext.create('Sbi.behavioural.lov.TestLovTreePanel2',{lovConfig:config.lovConfig, flex: 2, parentStore: this.lovTestPreview.store,lovType: typeStoreValue});
    			this.lovTestPreview.on('storeLoad',this.lovTestConfigurationTree.onParentStoreLoad,this.lovTestConfigurationTree);
    			lovConfigurationPanelItems.push(this.lovTestConfigurationTree);
    		}
    		
    		var lovConfigurationPanel = Ext.create('Ext.Panel', {
    		      	layout: 'hbox',
    		      	region: 'center',
    		     	width: "100%",
    		      	items: lovConfigurationPanelItems
    		    });
    		
    		this.listeners = 
    		{
          		"render" : function()
          		{
          			
        			var thisH = this.getHeight();
        			var previewH;
        			
        			if(this.lovTestPreview.getEl())
        			{
        				previewH = this.lovTestPreview.getHeight();
        			}
        			else
        			{
        				previewH = this.lovTestPreview.height;
        			}
        			
//        			console.log("##########");
//        			console.log(config.tabPanelHeight);
//        			console.log(thisH);
//        			console.log(previewH);
//        			console.log(this.toolbarHeight);
        			
        			// The old code:
        			//this.lovTestConfiguration.setHeight(thisH-previewH-this.toolbarHeight);
        			
        			// Changed code (adapting to the height of the tab panel on the right)        			
        			this.lovTestConfiguration.setHeight(config.tabPanelHeight-previewH-this.toolbarHeight-34);
        			
        			if(this.treeLov){
        				this.lovTestConfigurationTree.setHeight(thisH-previewH-this.toolbarHeight);
        			}
        		},
        		"resize" : function(){
        			var thisH = this.getHeight();
        			var previewH;
        			if(this.lovTestPreview.getEl()){
        				previewH = this.lovTestPreview.getHeight();
        			}else{
        				previewH = this.lovTestPreview.height;
        			}
        			//this.lovTestConfiguration.setHeight(thisH-previewH-this.toolbarHeight);
        			if(this.treeLov){
        				this.lovTestConfigurationTree.setHeight(thisH-previewH-this.toolbarHeight);
        			}
        		},
        		
       		
//        		"lovTypeChanged": function(type)
//        		{
//        			this.remove();
//        			console.log(type);
//        			config.lovConfig.lovType=type;
//        			lovTest = Ext.create('Sbi.behavioural.lov.TestLovPanel',{contextName: config.contextName,lovConfig:config.lovConfig}); //by alias
//        			addLovTestEvents(lovTest,lovPanel, lovConfig, modality, contextName);
//        			lovPanel.add(lovTest);
//        		}
          	};
    		
//    		this.on('lovTypeChanged',function(type){
//    			lovPanel.remove(lovTest,'true');
//    			lovConfig.lovType=type;
//    			lovTest = Ext.create('Sbi.behavioural.lov.TestLovPanel',{contextName: contextName,lovConfig:lovConfig, modality:modality}); //by alias
//    			addLovTestEvents(lovTest,lovPanel, lovConfig, modality, contextName);
//    			lovPanel.add(lovTest);
//    		},this);
    		
    		this.lovTestPreview.on
	    	(
    			"wrongSyntax1",
    			
    			function()
    			{
    				//console.log("GREEEEEEEEEEESKSA");
    				this.fireEvent('wrongSyntax2',"wrong");
    			},
    			
    			this
	    	);
    		
    		Ext.apply(this,config||{});
    		this.items = [lovConfigurationPanel,this.lovTestPreview];
        	this.callParent(arguments);
        	this.comboType.on('select',this.updateType,this);
        	
        	console.log("[OUT] constructor() TestLovPanel2");
    	},
    	
    	takeValues: function()
    	{
    		return this.lovTestConfiguration.getValues();
    	},
    	
    	setValues: function(data)
    	{
    		this.lovTestConfiguration.setValues(data);
    	},
    	
    	initServices: function(baseParams)
    	{
    		console.log("[IN] initServices() TestLovPanel2");
    		
    		this.services["getSmth"] = Sbi.config.serviceRegistry.getServiceUrl({
    			serviceName: 'LOV/Test',
    			baseParams: baseParams
    		});    		
    		
    		console.log("[OUT] initServices() TestLovPanel2");
    	},   	
    	
    	updateType: function(combo, records,eOpt )
    	{
    		console.log("--- UPDATE TYPE ---");
        	var value = records[0].data.type;  
        	console.log(value);
        	this.fireEvent('lovTypeChanged',value);
        }
	}
);