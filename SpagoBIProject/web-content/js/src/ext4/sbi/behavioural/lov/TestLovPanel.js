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

Ext.define('Sbi.behavioural.lov.TestLovPanel', {
    extend: 'Ext.panel.Panel',
    lovTestConfiguration: null

    ,config: {
    	layout: 'border',
    	toolbarHeight: 30
    }

	, constructor: function(config) {
		
		var thisPanel = this;
		this.services = {};
		this.services.saveLovAction = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'SAVE_LOV_ACTION'
		});
		
	    var typeStore = Ext.create('Ext.data.Store', {
	        fields: ['type','description'],
	        data : [{type:'simple', description:LN('sbi.behavioural.lov.type.simple')}, {type:'tree', description:LN('sbi.behavioural.lov.type.tree')}]
	    });
	    
		this.comboType = Ext.create('Ext.form.ComboBox', {
	        store: typeStore,
	        displayField: 'description',
	        valueField: 'type',
	        queryMode: 'local',
	        triggerAction: 'all',
	        emptyText:'Select a type...',
	        selectOnFocus:true,
	        width:135
	    });
	  
	
		
		this.dockedItems = [{
	        xtype: 'toolbar',
	        dock: 'top',
	        items: ['->',this.comboType,{
	            text: 'Save',
	            handler: this.save,
	            scope: this
	        }]
	    }]
		
		Ext.QuickTips.init();
		this.treeLov = (config.lovConfig.lovType && config.lovConfig.lovType=='tree');
		
		this.lovTestPreview = Ext.create('Sbi.behavioural.lov.TestLovResultPanel',{region: 'south',height:315, treeLov: this.treeLov}); //by alias
		//ConfigurationPanel(value, description)
		this.lovTestConfiguration = Ext.create('Sbi.behavioural.lov.TestLovConfigurationGridPanel',{lovConfig:config.lovConfig,  parentStore : this.lovTestPreview.store , treeLov: this.treeLov, flex: 1}); //by alias
		this.lovTestPreview.on('storeLoad',this.lovTestConfiguration.onParentStroreLoad,this.lovTestConfiguration);
		var lovConfigurationPanelItems = [this.lovTestConfiguration];
		
		if(this.treeLov){
			//Tree lov panel
			this.lovTestConfigurationTree = Ext.create('Sbi.behavioural.lov.TestLovTreePanel',{lovConfig:config.lovConfig, flex: 2});
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
    },
    
    save:  function(){
    	
    	var lovConfiguration;
    	if(this.lovTestConfiguration!=null && this.lovTestConfiguration!=undefined && !this.treeLov ){
    		lovConfiguration = this.lovTestConfiguration.getValues();
    	}else{
    		lovConfiguration = this.lovTestConfigurationTree.getValues();
    	}
    	
    	var params ={};
    	params.LOV_CONFIGURATION = Ext.JSON.encode(lovConfiguration);
    	params.MESSAGEDET = this.modality;
    	params.RETURN_FROM_TEST_MSG = 'SAVE';
        Ext.Ajax.request({
            url: this.services.saveLovAction,
            params:  params,
            success: function(response, options) {
            	alert("ok");
            },
            failure: function(response) {
            	alert("ko");
            }
            ,scope: this
   		 });	
    	
    }
    
    , updateType: function(combo, records,eOpt ){
    	var value = records[0].data.type;
    	
    	this.fireEvent('lovTypeChanged',value);
    }




	
});


