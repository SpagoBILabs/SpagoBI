/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * Object name 
  * 
  * [description]
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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.PromptablesWindow = function(config) {

	var defaultSettings = Ext.apply({}, config || {}, {
		title: 'Parameters window'
		, width: 500
		, height: 300
		, hasBuddy: false	
		, modal: true
	});

	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.promptablesWindow) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.promptablesWindow);
	}
		
	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
		
	this.initFormPanel();
	
	this.closeButton = new Ext.Button({
		text: LN('sbi.console.promptables.btnClose'),
		handler: function(){
        	this.destroy();
        }
        , scope: this
	});
	
	this.okButton = new Ext.Button({
		text: LN('sbi.console.promptables.btnOK'),
		handler: function(){
        	this.hide();
        	this.fireEvent('click', this, this.getFormState());
        }
        , scope: this
	});
	
	

	c = Ext.apply(c, {  	
		layout: 'fit'
	//,	closeAction:'hide'
	,	plain: true
	,	modal:true
	,	title: this.title
	,	buttonAlign : 'center'
	,	buttons: [this.closeButton, this.okButton]
	,	items: [this.formPanel]
	});

	// constructor
	Sbi.console.PromptablesWindow.superclass.constructor.call(this, c);
	
	this.addEvents('click');
    
};

Ext.extend(Sbi.console.PromptablesWindow, Ext.Window, {

    serviceName: null
    , formPanel: null
    
   , fieldMap: null
   , okButton: null
   , closeButton: null
   , dateFormat: null
    
    // public methods

    
    // private methods

    , initFormPanel: function() {	
		var fields = [];
		this.fieldMap = {};
		
		for(var i = 0, l = this.promptables.length; i < l; i++) { 
			var tmpLabel = null;
			var tmpName = null;
			var param = this.promptables[i]; 
			var tmpField = null;
			//defining label (its variable)
			for(p in param) {    		
        		if (p !== 'values' && p !== 'scope'){
        			tmpLabel = param[p];
        			tmpName = p;
        		}
			}
        		
    		if (param.values === undefined || param.values.type == 'text'){    			
    			//default is a textarea
        		tmpField = new Ext.form.TextField({
        		  fieldLabel: tmpLabel 
    	          , width: 250    	     
    	        });
        		          		
    		}else if (param.values === undefined || param.values.type == 'data'){  
    			//data
    			this.dateFormat = param.values.format || 'd/m/Y';
        		tmpField = new Ext.form.DateField({
        		  fieldLabel: tmpLabel 
    	          , width: 250  
    	          , format: param.values.format || 'd/m/Y'
    	        });
        		
        		
        		          		
    		}  else if (param.values.type == 'combo'){	
    			//combobox 
    			var tmpStore = null; 
    			var tmpValueField = 'column_1';
    			var tmpValueText = 'column_2';
    			
    			tmpStore = this.createStore(param.values);
    			
    			if (param.values.datasetLabel){    				
    				tmpStore.load();
    			}
    			
        		tmpField = new Ext.form.ComboBox({
        		    fieldLabel: tmpLabel,
    	            width: 250,    	    
    	            store: tmpStore,
	    	        valueField: tmpValueField,	
	    	        displayField: tmpValueText,
	    	        mode : 'local',
	    	        typeAhead: true,
	    	        emptyText:'Select ...',
	    	        selectOnFocus:true,
	    	        triggerAction: 'all'
        		 });        		 		 
    		} else if (param.values.type == 'checkList'){
    			//multivalue management
    			var tmpStore = null; 
    			tmpStore = this.createStore(param.values);
    			
    			tmpStore.on('beforeload', function(store, o) {
    				var p = Sbi.commons.JSON.encode(this.getFormState());
    				o.params.PARAMETERS = p;
    				return true;
    			}, this);
    			
    			
    			var baseConfig = {
    				       fieldLabel: tmpLabel
    					   , name : p.id
    					   , width: 250
    					   , allowBlank: !p.mandatory
    					   , valueField: (param.values.valueField)?param.values.valueField:'code'
    					   , descField: (param.values.descField)?param.values.descField:''
    					};
    			
    			tmpField = new Sbi.console.LookupField(Ext.apply(baseConfig, {
   				  	  store: tmpStore
   					//, params: params
   					, params: {}
   					, singleSelect: false
    			}));
    		} else if (param.values.type == 'lookup'){
    			//singlevalue management
    			var tmpStore = null; 
    			tmpStore = this.createStore(param.values);
    			
    			tmpStore.on('beforeload', function(store, o) {
    				var p = Sbi.commons.JSON.encode(this.getFormState());
    				o.params.PARAMETERS = p;
    				return true;
    			}, this);
    			
    			
    			var baseConfig = {
    				       fieldLabel: tmpLabel
    					   , name : p.id
    					   , width: 250
    					   , allowBlank: !p.mandatory
    					   , valueField: (param.values.valueField)?param.values.valueField:'code'
    					   , descField: (param.values.descField)?param.values.descField:''
    					};
  			
	  			tmpField = new Sbi.console.LookupField(Ext.apply(baseConfig, {
	 				  	  store: tmpStore
	 					//, params: params
	 				  	, params: {}
	 					, singleSelect: true
	  			}));
	  		}
    		
    		if (param.values !== undefined && param.values.defaultValue !== undefined){
   			 tmpField.defaultValue = param.values.defaultValue;
   		 	}
    		fields.push(tmpField);
			this.fieldMap[tmpName] = tmpField;
			
    		
        }  			   
	
    	this.formPanel = new  Ext.FormPanel({
    		  //title:  LN('sbi.console.promptables.title'),
    		  margins: '50 50 50 50',
	          labelAlign: 'left',
	          bodyStyle:'padding:5px',
	          width: 850,
	          height: 600,
	          labelWidth: 150,
	          layout: 'form',
	          trackResetOnLoad: true,
	          items: fields
	      }); 
    }

	, getFormState: function() {
    	var state = {};
    	
    	for(f in this.fieldMap) {
    		//sets the default value if it's defined into the template
    		
    		if ((this.fieldMap[f].getValue() === undefined || this.fieldMap[f].getValue() == '') && this.fieldMap[f].defaultValue !== undefined){
    			state[f] = this.fieldMap[f].defaultValue;
    		}
    		else {    			
    			if (this.fieldMap[f].getXTypes().indexOf('/datefield') >= 0){
    				//state[f] = this.fieldMap[f].getValue().toLocaleString();    	
    				state[f] = Sbi.console.commons.Format.date(this.fieldMap[f].getValue() , this.dateFormat);
    				
    			}else{
    				state[f] = this.fieldMap[f].getValue();
    			}    			
    			//state[f] = this.fieldMap[f].getValue();
    		}    	
    	}
    	
    	return state;
    }
    
	, createStore: function(config) {
		var store;
		var params = {};
		
		if (config.datasetLabel){
			//the store is created by the result of a dataset
			params.ds_label = config.datasetLabel;	

			var serviceConfig;
			serviceConfig = {serviceName: 'GET_CONSOLE_DATA_ACTION'};
			serviceConfig.baseParams = params;	
			
			store = new Ext.data.JsonStore({
				url: Sbi.config.serviceRegistry.getServiceUrl( serviceConfig )
			});
			
		} else if (config.data){
			//the store is created by fix values
			store = new Ext.data.SimpleStore({
	              fields: ['column_1','column_2']
	            , data: config.data
	        });
		}else{
			Sbi.Msg.showError('Store not defined for the prompt.', 'Service Error');
		}
		
		store.on('exception', Sbi.exception.ExceptionHandler.onStoreLoadException, this);
		
		return store;
		
	}
});