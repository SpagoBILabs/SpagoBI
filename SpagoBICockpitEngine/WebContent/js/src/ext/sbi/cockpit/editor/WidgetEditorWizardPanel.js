/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetEditorWizardPanel = function(config) { 

	var defaultSettings = {		
		border: false,
		layout : 'card',
		activeItem : 0, //set to 1 if the dataset was already selected
		autoScroll : true
	};
		
	if(Sbi.settings && Sbi.cockpit && Sbi.cockpit.editor && Sbi.cockpit.editor.widgetEditorWizardPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.cockpit.editor.widgetEditorWizardPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	
	c = Ext.apply(c, {
					items: this.initContent(),
					buttons: this.initButtons()
	}); 
	
	Sbi.cockpit.editor.WidgetEditorWizardPanel.superclass.constructor.call(this, c);
	
	this.addEvents('cancel', 'navigate', 'confirm');

};

Ext.extend(Sbi.cockpit.editor.WidgetEditorWizardPanel, Ext.Panel, {
	
	initButtons: function() {
		var thisPanel = this;
		var buttonsBar = [];
		
		buttonsBar.push('->');
		buttonsBar.push({ id: 'move-prev',
	        text: LN('sbi.ds.wizard.back'),
	        handler: function(btn) {
	        	thisPanel.navigate(btn.findParentByType("panel"), "prev");
	        }, 
	        scope: this,
	        disabled: true
	    });
		
		buttonsBar.push({id: 'move-next',
	        text:  LN('sbi.ds.wizard.next'),
	        handler: function(btn) {
	        	thisPanel.navigate(btn.findParentByType("panel"), "next");
	        }, scope: this
	    	});
		
		buttonsBar.push({id: 'confirm',
			hidden: true,
	        text:  LN('sbi.ds.wizard.confirm'),
	        handler: function(){
	        	thisPanel.fireEvent('confirm', this);   
	        }, scope: this            
	    	});
		
		buttonsBar.push({id: 'cancel',
	        text:  LN('sbi.ds.wizard.cancel'),
	        handler: function(){
	        	alert("fire event cancel");
	        	thisPanel.hide();  
	        	thisPanel.fireEvent('cancel', this);  
	        }, scope: this
	    	});
		
		return buttonsBar;
	}


	, initContent: function(){
		
		var datasetsBrowser = new Sbi.cockpit.editor.WidgetEditorDatasetsBrowser({widgetManager: this.widgetManager, widget: this.widget}); 
		datasetsBrowser.itemId = 0;
		datasetsBrowser.addListener('click', this.onClick, this);
	
		var editor = new Sbi.cockpit.editor.WidgetEditor({});
		editor.itemId  = 1;

		var cards = [];
		cards.push(datasetsBrowser);
		cards.push(editor);
		
		return cards;
	}
	
	, navigate: function(panel, direction){		
        // This routine could contain business logic required to manage the navigation steps.
        // It would call setActiveItem as needed, manage navigation button state, handle any
         // branching logic that might be required, handle alternate actions like cancellation
         // or finalization, etc.  A complete wizard implementation could get pretty
         // sophisticated depending on the complexity required, and should probably be
         // done as a subclass of CardLayout in a real-world implementation.
		 var newTabId =  this.layout.activeItem.itemId;
		 var numTabs  = (this.items.length-1);
		 var isTabValid = true;
		 
		 if (direction == 'next'){
			 newTabId += (newTabId < numTabs)?1:0;	
			 if (newTabId == 1){
				 isTabValid = this.validateTab0();		
			 }
		 }else{			
			newTabId -= (newTabId <= numTabs)?1:0;					
		 }
		 if (isTabValid){
			
			 this.layout.setActiveItem(newTabId);
			 
			 Ext.getCmp('move-prev').setDisabled(newTabId==0);
			 Ext.getCmp('move-next').setDisabled(newTabId==numTabs);
		 	 Ext.getCmp('confirm').setVisible(!(parseInt(newTabId)<parseInt(numTabs)));
		 }			 
	}
		

//	, onClick : function(obj, i, node, e) {
//		alert('clickedd!!!');
//		var actionSelect = e.getTarget('div[class=select]',10,true);
//		
//		var s = obj.getStore();
//		var r = s.getAt(s.findExact('label',node.id));
//		if (r){
//			r = r.data;
//		}
//		
//		if (actionSelect != null){
//			 Sbi.debug('WidgetEditorWizardPanel clicked on dataset...[' + r.name + ']');
//		     var storeConfig = {};
//		     storeConfig.dsLabel = r.label;		     
//		     this.widgetManager.addStore(storeConfig);
//		     this.widget.dataset =  r.label; //this.widgetManager.getStore(storeConfig.dsLabel);    			
//		}	
//    	return true;
//	}

	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, validateTab0: function() {
		if (this.widget.dataset == undefined || this.widget.dataset == null){
			alert('Per procedere e\' necessario selezionare un dataset!');
			return false;
		}
		
		return true;
	}
	
//	, getDatasetStore : function() {
//		var scope = this;
//
//		var baseParams ={};
//		baseParams.isTech = false;
//		baseParams.showOnlyOwner = false;
//		baseParams.allMyDataDs = true;
//
//		var services = new Array();
//		services["list"] = Sbi.config.serviceRegistry.getRestServiceUrl({
//			serviceName : 'certificateddatasets',
//			baseParams : baseParams,
//			baseUrl:{contextPath: 'SpagoBI', controllerPath: 'servlet/AdapterHTTP'}
//		});
//		
//		
//		Sbi.debug('WidgetEditorDatasetsView bulding the store...');
//		var store = new Ext.data.JsonStore({
//			 url: services['list']
//			 , root: 'root'
////			 , autoLoad: true
//			 , fields: ["id",
//			    	 	"label",
//			    	 	"name",
//			    	 	"description",
//			    	 	"typeCode",
//			    	 	"typeId",
//			    	 	"encrypt",
//			    	 	"visible",
//			    	 	"engine",
//			    	 	"engineId",
//			    	 	"dataset",
//			    	 	"stateCode",
//			    	 	"stateId",
//			    	 	"functionalities",
//			    	 	"creationDate",
//			    	 	"creationUser",
//			    	 	"refreshSeconds",
//			    	 	"isPublic",
//			    	 	"actions",
//			    	 	"exporters",
//			    	 	"decorators",
//			    	 	"previewFile",
//			    	 	"isUsed" /*local property*/]
//		});	
//	
//		store.on('load', this.checkUsage, this);
//		
////		store.load({callback : function(records, options, success) {
////				            if (success) {
////				            	alert('store.load!');
////				            	var sm = scope.widgetManager.getStoreManager();			
////				            	
////				            	for (var i=0; i< records.length; i++){
////				            		if ( sm.get(records[i].data.label) != undefined &&   sm.get(records[i].data.label) != null){
////				            			records[i].data.isUsed = 'true';	
////				            			alert('isUsed is TRUE!!');
////				            		}else{
////				            			records[i].data.isUsed = 'false';
////				            		}
////				            	}				           
////				            }}
////        },scope);
//		store.load();
//
//		
//		Sbi.debug('WidgetEditorDatasetsView store loaded.');
//		
//		return store;
//	}	
////	(records, options, success) {
//	, checkUsage: function  (s, records, success, operation, eOpts){
//		alert('checkUsage');
//            if (success) {
//            	alert('store.load!');
//            	var sm = this.widgetManager.getStoreManager();			
//            	
//            	for (var i=0; i< records.length; i++){
//            		if ( sm.get(records[i].data.label) != undefined &&   sm.get(records[i].data.label) != null){
//            			records[i].data.isUsed = 'true';	
//            			alert('isUsed is TRUE!!');
//            		}else{
//            			records[i].data.isUsed = 'false';
//            		}
//            	}				           
//            }
//            
//            return true;
//	}
});
