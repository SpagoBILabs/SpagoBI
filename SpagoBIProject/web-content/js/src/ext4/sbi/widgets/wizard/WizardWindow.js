/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


/**TODO anto: aggiornare documentazione!!!
 * 
 * Data view for a browser style. It define a layout and provides the stubs methods for the icons browser.
 * This methods should be overridden to define the logic. Only the methods onAddNewRow, onGridSelect and onCloneRow are implemented.
 * The configuration dataView must be passed to the object. It should contains the method setFormState (it is used by onGridSelect)
 * 
 * 
 * 		@example
 *	Ext.define('Sbi.tools.datasource.DataSourceListDetailPanel', {
 *   extend: 'Sbi.widgets.compositepannel.ListDetailPanel'
 *	, constructor: function(config) {
 *		//init services
 *		this.initServices();
 *		//init form
 *		this.form =  Ext.create('Sbi.tools.datasource.DataSourceDetailPanel',{services: this.services});
 *		this.columns = [{dataIndex:"DATASOURCE_LABEL", header:"Name"}, {dataIndex:"DESCRIPTION", header:"description"}];
 *		this.fields = ["DATASOURCE_ID","DATASOURCE_LABEL","DESCRIPTION","DRIVER","DIALECT_ID","DIALECT_CLASS","DIALECT_NAME","JNDI_URL","USER","PASSWORD","SCHEMA","MULTISCHEMA","CONNECTION_URL"];
 *		this.form.on("save",this.onFormSave,this);
 *    	this.callParent(arguments);
 *    }
 *	, initServices: function(baseParams){
 *		this.services["getAllValues"]= Sbi.config.serviceRegistry.getRestServiceUrl({
 *			    							serviceName: 'datasources/listall'
 *			    							, baseParams: baseParams
 *			    						});
 *		...
 *		    	
 *	}
 *	, onDeleteRow: function(record){
 *		Ext.Ajax.request({
 *  	        url: this.services["delete"],
 *  	        params: {DATASOURCE_ID: record.get('DATASOURCE_ID')},
 *  	       ...
 *	}
 *	, onFormSave: function(record){
 *		Ext.Ajax.request({
 *  	        url: this.services["save"],
 *  	        params: record,
 *  	   ...
 *	}
 *});
 *			... 
 *
 * 
 * @author
 * Antonella Giachino (antonella.giachino@eng.it)
 */

Ext.define('Sbi.widgets.wizard.WizardWindow', {
    extend: 'Ext.Window'
//    , requires: ['Ext.Toolbar', 'Ext.tab.Panel']
    ,config: {    	  	     	
    	width: 700,
		height: 500,
		hasBuddy: false,	
		modal: true,
		closeAction:'close',
		constrain: true,
		plain: true,
		modal:true,
		title: '',
		buttonAlign : 'center',
		wizardPanel: null
    } 
   
	/**
	 * In this constructor you must pass configuration
	 */
	, constructor: function(config) {
		this.initTabPanel(config);		
		
		this.addEvents('cancel',		           
		  	 		   'navigate');

		this.callParent(arguments);
	}
	
	, initTabPanel: function(c){
		var localConf = {};		
		localConf.items = c.tabs;		
		localConf.activeTab = 0;
		this.wizardPanel = Ext.create('Ext.TabPanel', localConf);	
		this.wizardPanel.addListener('cancel',	this.navigate, this);
		this.wizardPanel.addListener('navigate',this.navigate, this);
		this.items = [this.wizardPanel];
	}
	
	, createWizardToolbar: function() {
		Sbi.debug('WizardWindow bulding the wizard toolbar...');
		var buttons = [];
		if(this.buttonsBar){
			for(var i=0; i<this.buttonsBar.length;i++){
				buttons.push(this.buttonsBar[i]);
			}
		}
		
		Sbi.debug('WizardWindow wizard toolbar created...');
		return buttons;
	}
	
	, navigate: function(panel, direction){
		this.fireEvent('navigate',panel, direction);

        // This routine could contain business logic required to manage the navigation steps.
        // It would call setActiveItem as needed, manage navigation button state, handle any
        // branching logic that might be required, handle alternate actions like cancellation
        // or finalization, etc.  A complete wizard implementation could get pretty
        // sophisticated depending on the complexity required, and should probably be
        // done as a subclass of CardLayout in a real-world implementation.
		 
	}
	
	, createStepFieldsGUI: function(fieldsConf) {
		var fields = [];		
		
		if (!fieldsConf) return;
		
		for(var i=0; i<fieldsConf.length;i++){
			var tmpField = this.createWizardField(fieldsConf[i]);
			fields.push(tmpField);
		}
		
		return fields;
	}
	
	, initWizardBar: function() {
		var buttonsBar = [];
		buttonsBar.push('->');
		buttonsBar.push({ id: 'move-prev',
            text: '< Back',
            handler: function(btn) {
                this.navigate(btn.up("panel"), "prev");
            }, 
            scope: this,
            disabled: true
        });
		
		buttonsBar.push({id: 'move-next',
            text: 'Next >',
            handler: function(btn) {
                this.navigate(btn.up("panel"), "next");
            }, scope: this
        	});
		
		buttonsBar.push({id: 'cancel',
            text: 'Cancel',
            handler: function(){
            	this.fireEvent('cancel', this);   
//            	this.hide();
            }, scope: this
        	});
		return buttonsBar;
	}
	
//	, closePanel: function(){
//    	if (this.fireEvent('cancel', this) !== false) {
//            this.close();
//        }
//
//	}
	, createWizardField: function (obj){
		var tmpField = null;
		if (obj.values === undefined ||obj.type == 'text' || obj.type == 'textarea'){        			
			//default is a textarea
			tmpField = this.createTextField(obj);        		          		
		}else if (obj.type == 'data'){  
			//data
			tmpField = this.createDataField(obj);   
		}  else if (param.values.type == 'combo'){	
			//combobox     			
			tmpField = this.createComboField(obj);    			    					 
		} else if (param.values.type == 'checkList'){
			//multivalue management
			tmpField = this.createCheckListField(obj);    	
			
		} else if (param.values.type == 'lookup'){
			//singlevalue management
			tmpField = this.createListField(obj);        			
  		} else if (param.values.type == 'group'){
			//group radio management
  			tmpField = this.createRadioGroupField(obj);        			
  		} else if (param.values.type == 'html'){
			//group radio management
  			tmpField = this.createHtml(obj);        			
  		}
		
		return tmpField;
	}
	, createHtml: function(f){
		var field = new Ext.form.TextField({	  		  	  		  	  
		           width: 500 
		          , height: 30
		          , margin: '0 0 0 10'
		          , labelStyle:'font-weight:bold;' //usare itemCls : <tagstyle>
		          , value: f.value || ""
		        });
		return field;
	}
	
	, createTextField: function(f){
		var field = new Ext.form.TextField({
  		  fieldLabel: f.label 
  		  	  , name: f.name
	          , width: 500 
	          , height: (f.type == 'textarea')?80:30
			  , xtype : 'textarea'
	          , margin: '0 0 0 10'
	          , labelStyle:'font-weight:bold;' //usare itemCls : <tagstyle>
	          , value: f.value || f.defaultValue || ""
	        });
		return field;
	}
	
	, createDataField: function(f){
		this.dateFormat = f.values.format || 'd/m/Y';
		
		var field = new Ext.form.DateField({
		  fieldLabel: f.label 
		  , name: f.name
          , width: 250  
          , format: f.values.format || 'd/m/Y'
          , defaultValue: f.defaultValue 
        });
		
		return field;
	}
	/*
	, createComboField: function(f){
		
		var tmpStore = null; 
		var tmpValueField = 'column_1';
		var tmpValueText = 'column_2';
		
		tmpStore = this.createStore(f.values);
		
		if (f.values.datasetLabel){    				
			tmpStore.load();
		}
		var field = new Ext.form.ComboBox({
		    fieldLabel: f.label ,
		    name: f.name,
            width: 250,    	    
            store: tmpStore,
	        valueField: tmpValueField,	
	        displayField: tmpValueText,
	        mode : 'local',
	        typeAhead: true,
	        emptyText:'Select ...',
	        selectOnFocus:true,
	        triggerAction: 'all',
	        defaultValue: f.defaultValue 
		 });        		 
		
		return field;
	}
	
	, createCheckListField: function(f){
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
*/
});