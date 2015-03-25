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
        	toolbarHeight: 30
        },
    	
    	constructor: function(config) 
    	{
    		console.log("USAO U TEST LOV 2");
    		console.log(config.lovConfig);
    		
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
    		
    		var saveButton = 
    		{	
    				handler: this.save,
    				scope: this
    		};

    		var backButton = 
    		{
    				handler: this.back,
    				scope: this
    		};
    		
    		this.dockedItems = 
			[
			 	{
	    	        xtype: 'toolbar',
	    	        dock: 'top',
	    	        items: ['->',this.comboType,saveButton,backButton]
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
					treeLov: this.treeLov
				}
			); 
    		
    		console.log("1111111111111");
    		console.log(this.lovTestPreview.store);
    		
    		/* The upper part of the result panel - COLUMNS OF THE RESULT */
    		this.lovTestConfiguration = Ext.create
    		(
				'Sbi.behavioural.lov.TestLovConfigurationGridPanel2',
				
				{
					lovConfig: config.lovConfig,  
					parentStore: this.lovTestPreview.store, 
					lovType: typeStoreValue, 
					flex: 1
				}
			);     		
    		
    		this.lovTestPreview.on('storeLoad',this.lovTestConfiguration.onParentStroreLoad,this.lovTestConfiguration);
    		var lovConfigurationPanelItems = [this.lovTestConfiguration];
    		
    		if(this.treeLov){
    			//Tree lov panel
    			this.lovTestConfigurationTree = Ext.create('Sbi.behavioural.lov.TestLovTreePanel',{lovConfig:config.lovConfig, flex: 2, parentStore : this.lovTestPreview.store ,lovType: typeStoreValue});
    			this.lovTestPreview.on('storeLoad',this.lovTestConfigurationTree.onParentStroreLoad,this.lovTestConfigurationTree);
    			lovConfigurationPanelItems.push(this.lovTestConfigurationTree);
    		}

    		var lovConfigurationPanel = Ext.create('Ext.Panel', {
    		      	layout: 'hbox',
    		      	region: 'center',
    		     	width: "100%",
    		      	items: lovConfigurationPanelItems
    		    });
    		
    		this.listeners = {
          		"render" : function(){
          			
        			var thisH = this.getHeight();
        			var previewH;
        			if(this.lovTestPreview.getEl()){
        				previewH = this.lovTestPreview.getHeight();
        			}else{
        				previewH = this.lovTestPreview.height;
        			}
        			this.lovTestConfiguration.setHeight(thisH-previewH-this.toolbarHeight);
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
        			this.lovTestConfiguration.setHeight(thisH-previewH-this.toolbarHeight);
        			if(this.treeLov){
        				this.lovTestConfigurationTree.setHeight(thisH-previewH-this.toolbarHeight);
        			}
        		}
          	};
    		
    		Ext.apply(this,config||{});
    		this.items = [lovConfigurationPanel,this.lovTestPreview];
        	this.callParent(arguments);
        	this.comboType.on('select',this.updateType,this);
    		
    		console.log("kraj konstruktora");
    	},
    	
    	initServices: function(baseParams)
    	{
    		this.services["getSmth"] = Sbi.config.serviceRegistry.getServiceUrl({
    			serviceName: 'LOV/Test',
    			baseParams: baseParams
    		});    		
    		
    		console.log("init services");
    		console.log(this.services["getSmth"]);
    	},
    	
    	prep: function(data) 
    	{    	
    		var result = 0; 
    		var yy = this;
    		//var ajaxEnded = false;
    		
    		/* opis problema: ne znam kako da vratim string iz ove funckije u konstruktor - vraca undefined */
    		
//    		Ext.Ajax.request
//    		(
//				{
//					url: "http://localhost:8080/SpagoBI/restful-services/LOV/Test",
//	                params:  data,
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
//	                	result = responseJSON;
//	                	this.ajaxEnded = true;
//	                	console.log("PRE");
//	                	console.log(result);
//	                	console.log("W T F");
//	                	yy.responseProc(sm);
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
    		
    		
    		
    		console.log("PRE AJAX COMPLETE-a");
    		console.log(data);
    		//console.log(this.textArea1.setValue("aaaa"));
    		this.textArea1.value = "yyyy";
    		this.doLayout();
//    		while(ajaxEnded==false)
//    		{}
    		
    		console.log("POSLE AJAX COMPLETE-a");
//    		Ext.Ajax.on('requestcomplete', function() {console.log("888888888");
//    		console.log(result);});
    		
//    		return result;
    	},
    	
    	responseProc: function(response)
    	{
    		console.log("QQQ");
    		console.log(response.toString());
    		console.log(response.length);
    		console.log("{}{}{}");
    		console.log(this.textArea1);
    		this.textArea1.setValue("aaa");
    	}
	}
);