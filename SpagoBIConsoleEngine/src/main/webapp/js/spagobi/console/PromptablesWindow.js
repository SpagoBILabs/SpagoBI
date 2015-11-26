/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
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
        	//this.hide();			
        	this.fireEvent('click', this, this.getFormState());
        	this.close();
        }
        , scope: this
	});
	
	

	c = Ext.apply(c, {  	
		layout: 'fit'
	//,	closeAction:'hide'
	,	closeAction:'close'
	,	constrain: true
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
			var tmpParamConfig = null;
			var tmpField = null;
			//defining label (it's variable)
			for(p in param) {    		
        		if (p !== 'values' && p !== 'scope' && p !== 'defaultValue'){
        			tmpLabel = param[p];
        			tmpName = p;
        		}
			}						
			
			tmpParamConfig = param;
			
			tmpField = this.createParameterField(tmpLabel, tmpName, tmpParamConfig);
    		fields.push(tmpField);
			this.fieldMap[tmpName] = tmpField;			
        } //for
	
    	this.formPanel = new  Ext.FormPanel({
    		  //title:  LN('sbi.console.promptables.title'),
    		  margins: '50 50 50 50',
	          labelAlign: 'left',
	          bodyStyle:'padding:5px',
	          autoScroll:true,
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
		var index = null;
    	for(f in this.fieldMap) {
    		//sets the default value if it's defined into the template 
    		index = f;
    		if (f.indexOf('__') >= 0){
    			index = f.substr(0, f.indexOf('__'));
    		}
    		//when the field is visible and the value is yet empty checks the default value
    		//if (this.fieldMap[f].isVisible() && state[index] === undefined || state[index] === null || state[index] === '' ){
    		if (this.fieldMap[f].isVisible() && state[index] === undefined || state[index] === null ){
    			
    			var tmpField = this.fieldMap[f];
    			var tmpFieldValue = tmpField.getValue();
    			var tmpDefaultValue = tmpField.defaultValue;    	
    			//if (tmpDefaultValue === undefined || tmpDefaultValue === null || tmpDefaultValue == '' ){
    			if (tmpDefaultValue === undefined || tmpDefaultValue === null  ){
					tmpDefaultValue = (tmpFieldValue !== null)?tmpFieldValue.defaultValue:'' ;
				} 
    			if ((tmpFieldValue === undefined || tmpFieldValue === null || tmpFieldValue == '') && 
    				 tmpDefaultValue !== undefined){
   	    			state[index] = tmpDefaultValue.trim();
   	    		}else {    		
	    			//the field is correctly valorized:
	    			if (tmpField.getXTypes().indexOf('/datefield') >= 0){
	    				//if it's a date: sets its format
	    				state[index] = Sbi.console.commons.Format.date(tmpFieldValue , this.dateFormat);
	    			}else{	
	    				//sets the current value
	    				state[index] = tmpFieldValue;
	    			}
	    		}
    		}    		
    	}
    	//alert('state: ' + state.toSource());
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
	
	, createTextField: function(label, name, param){
		var field = new Ext.form.TextField({
  		  fieldLabel: label 
  		  	  , name: name
	          , width: 250 
	          , defaultValue: param.defaultValue 
	        });
		return field;
	}
	
	, createDataField: function(label, name, param){
		this.dateFormat = param.values.format || 'd/m/Y';
		
		var field = new Ext.form.DateField({
		  fieldLabel: label || param.title
		  , name: name
          , width: 250  
          , format: param.values.format || 'd/m/Y'
          , defaultValue: param.defaultValue 
        });
		
		return field;
	}
	
	, createComboField: function(label, name, param){
		
		var tmpStore = null; 
		var tmpValueField = 'column_1';
		var tmpValueText = 'column_2';
		
		tmpStore = this.createStore(param.values);
		
		if (param.values.datasetLabel){    				
			tmpStore.load();
		}
		var field = new Ext.form.ComboBox({
		    fieldLabel: label || param.title,
		    name: name,
            width: 250,    	    
            store: tmpStore,
	        valueField: tmpValueField,	
	        displayField: tmpValueText,
	        mode : 'local',
	        typeAhead: true,
	        emptyText:'Select ...',
	        selectOnFocus:true,
	        triggerAction: 'all',
	        defaultValue: param.defaultValue 
		 });        		 
		
		return field;
	}
	
	, createCheckListField: function(label, name, param){
		var smLookup = new Ext.grid.CheckboxSelectionModel( {singleSelect: false } );
		var cmLookup =  new Ext.grid.ColumnModel([
    	                                          new Ext.grid.RowNumberer(),		    	                                          
				                    		      {header: "Data", dataIndex: 'value', width: 75},
				                    		      smLookup
				                    		    ]);
		var tmpStore = null; 
		tmpStore = this.createStore(param.values);
		
		
		tmpStore.on('beforeload', function(store, o) {
			var p = Sbi.commons.JSON.encode(this.getFormState());
			o.params.PARAMETERS = p;
			return true;
		}, this);
		
		
		var baseConfig = {
			       fieldLabel: label || param.title
				  // , name : p.id
				   , name : name
				   , width: 250
				   , sm: smLookup
				   , cm: cmLookup
				   , allowBlank: !p.mandatory
				   , valueField: (param.values.valueField)?param.values.valueField:'code'
				   , descField: (param.values.descField)?param.values.descField:''		
				   //, displayField: (param.values.descField)?param.values.valueField:'value'
				   , defaultValue: param.defaultValue
				};
		var field = new Sbi.console.LookupField(Ext.apply(baseConfig, {
			  	  store: tmpStore
				//, params: params
				, params: {}
				, singleSelect: false
				
		}));	 
		
		return field;
	}
	
	, createListField: function(label, name, param){
		var tmpStore = null; 
		tmpStore = this.createStore(param.values);
		
		tmpStore.on('beforeload', function(store, o) {
			var p = Sbi.commons.JSON.encode(this.getFormState());
			o.params.PARAMETERS = p;
			return true;
		}, this);
		
		
		var baseConfig = {
			       fieldLabel: label || param.title
				  // , name : p.id
				   , name : name
				   , width: 250
				   , allowBlank: !p.mandatory
				   , valueField: (param.values.valueField)?param.values.valueField:'code'
				   , descField: (param.values.descField)?param.values.descField:''
				   , defaultValue: param.defaultValue
				};
		
		var field = new Sbi.console.LookupField(Ext.apply(baseConfig, {
			  	  store: tmpStore
				//, params: params
			  	, params: {}
				, singleSelect: true
		})); 
		
		return field;
	}
	
	, createRadioGroupField: function(label, name, param){
		var options = [];
		var tmpParamConfig = null;
		var tmpField = null;
		
		
		for(var j = 0, l2 = param.values.options.length; j < l2; j++) {			
			tmpParamConfig = param.values.options[j];
			var idRadio =  name + '__' + j;
			
			//adds the radio as the first field			
			tmpField =  {
						 id: idRadio,
						 name: name,
		                 labelSeparator: '',
		                 boxLabel: tmpParamConfig.values.title || '',		                 
		                 inputValue: idRadio
			            };
			options.push(tmpField);
			//adds all sub parameters		
			if (tmpParamConfig.defaultValue === undefined || tmpParamConfig.defaultValue === null){
				tmpParamConfig.defaultValue =  param.defaultValue;
			}
			var tmpParam = this.createParameterField(label, idRadio, tmpParamConfig);
			tmpParam.setVisible(false) ;			
			options.push(tmpParam);			
			this.fieldMap[idRadio] = tmpParam;
			var radioField = new Ext.form.RadioGroup({			
				id: name,
				name: name,
	            fieldLabel: label,	  	            
	            width: 250, 
	            autoHeight: true,
	            autoScroll: true,
	    	    xtype: 'fieldset',
	    	    border: false,
	    	    defaultType: 'radio', // each item will be a radio button
	    	    columns: 2,
	    	    items: options,
	    	    defaultValue: param.defaultValue
	    	});

			radioField.addListener('change', this.changeRadioField , this);
			
		}

		return radioField;
	}
	, createParameterField: function (label, name, param){
		var tmpField = null;
		if (param.values === undefined || param.values.type == 'text'){        			
			//default is a textarea
			tmpField = this.createTextField(label, name, param);        		          		
		}else if (param.values.type == 'data'){  
			//data
			tmpField = this.createDataField(label, name, param);   
		}  else if (param.values.type == 'combo'){	
			//combobox     			
			tmpField = this.createComboField(label, name, param);    			    					 
		} else if (param.values.type == 'checkList'){
			//multivalue management
			tmpField = this.createCheckListField(label, name, param);    	
			
		} else if (param.values.type == 'lookup'){
			//singlevalue management
			tmpField = this.createListField(label, name, param);        			
  		} else if (param.values.type == 'group'){
			//group radio management
  			tmpField = this.createRadioGroupField(label, name, param);        			
  		}
		
		if (param.values !== undefined && param.values.defaultValue !== undefined){
			tmpField.defaultValue = param.values.defaultValue;
		}
		
		return tmpField;
	}
	
	, changeRadioField: function(radioGroup, radio){		
		
		var currentRadioId = radio.getItemId().substr(0, radio.getItemId().indexOf('__'));	
		for(f in this.fieldMap) {			
			var tmpField = this.fieldMap[f];
			var tmpFieldId =  tmpField.name || tmpField.id;
			if (tmpFieldId == radio.getItemId()){				
				tmpField.setVisible(true);
				//tmpField.setValue(''); //resets the field value to '' (system default)
		//		tmpField.doLayout();
			}else if (tmpFieldId.substr(0, tmpFieldId.indexOf('__')) === currentRadioId ){
				//disables others fields of the group
				tmpField.setVisible(false);				
			}
    		 	
    	}
    	
	}
});