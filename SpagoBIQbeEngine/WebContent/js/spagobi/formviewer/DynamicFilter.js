/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
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
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.formviewer");

Sbi.formviewer.DynamicFilter = function(dynamicFilter, config) {
	
	var defaultSettings = {
		// set default values here
		id: dynamicFilter.id
		, autoScroll: true
		, autoWidth: true
        , layout: 'column'
    	, layoutConfig: {
	        columns: dynamicFilter.operator.toUpperCase() === 'BETWEEN' ? 4 : 3
	    }
	};
	if (Sbi.settings && Sbi.settings.formviewer && Sbi.settings.formviewer.dynamicFilter) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formviewer.dynamicFilter);
	}
	var c = Ext.apply(defaultSettings, config || {});

	this.baseConfig = c;
	
	this.init(dynamicFilter);
	
	Ext.apply(c, {
  		items: this.fields
	});
	
	// constructor
    Sbi.formviewer.DynamicFilter.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.formviewer.DynamicFilter, Ext.form.FormPanel, {
    
	services: null
	, combo: null
	, valuesInputs: null
	, fields: null
	   
	// private methods
	   
	, init: function(dynamicFilter) {
		this.fields = [];
		this.combo = this.createFieldCombo( dynamicFilter );
		var aPanel = new Ext.Panel({
			items: [this.combo]
			, layout: 'form' // form layout required: input field labels are displayed only with this layout
			, width: 300
		});
		this.fields.push(aPanel);
		
		this.valuesInputs = this.createFieldValuesInput( dynamicFilter );
		if (this.valuesInputs.length == 1) {
			this.fields.push(new Ext.Panel({
				items: [this.valuesInputs[0]]
				, layout: 'form' // form layout required: input field labels are displayed only with this layout
				, width: 470
			}));
		} else {
			this.fields.push(new Ext.Panel({
				items: [this.valuesInputs[0]]
				, layout: 'form' // form layout required: input field labels are displayed only with this layout
				, width: 250
			}));
			this.fields.push(new Ext.Panel({
				items: [this.valuesInputs[1]]
				, layout: 'form' // form layout required: input field labels are displayed only with this layout
				, width: 220
			}));
		}
		var clearButtonPanel = new Ext.Panel({
			items: [new Ext.Button({
				iconCls: 'icon-clear'
				, tooltip: LN('sbi.formviewer.dynamicfilterspanel.clear.tt')
				, scope: this
				, handler: this.clear
			})]
			, width: 30
		});
		this.fields.push(clearButtonPanel);
	}

	, createFieldCombo: function(dynamicFilter) {
		
		var store = new Ext.data.JsonStore({
			data: [{'field': '', 'text': ''}].concat(dynamicFilter.admissibleFields),
		    fields: ['field', 'text']
		});
		
		var fieldLabel = (dynamicFilter.title !== undefined && dynamicFilter.title !== '') ? 
				dynamicFilter.title :
					LN('sbi.formviewer.dynamicfilterspanel.variable');
		
		var combo = new Ext.form.ComboBox({
			name: 'field'
            , editable: false
            , fieldLabel: fieldLabel
		    , forceSelection: false
		    , store: store
		    , mode : 'local'
		    , triggerAction: 'all'
		    , displayField: 'text'
		    , valueField: 'field'
		    , emptyText: ''
		});

		return combo;
	}
	
	, createFieldValuesInput: function(dynamicFilter) {
		var valuesInput = [];
		if (dynamicFilter.operator.toUpperCase() === 'BETWEEN') {
			valuesInput[0] = new Ext.form.TextField({
				fieldLabel: LN('sbi.formviewer.dynamicfilterspanel.fromvalue')
			   , name : 'fromvalue'
			   , allowBlank: true
			   , width: 100
			});
			valuesInput[1] = new Ext.form.TextField({
				fieldLabel: LN('sbi.formviewer.dynamicfilterspanel.tovalue')
			   , name : 'tovalue'
			   , allowBlank: true
			   , width: 100
			});
		} else {
			valuesInput[0] = new Ext.form.TextField({
				fieldLabel: LN('sbi.formviewer.dynamicfilterspanel.value')
			   , name : 'value'
			   , allowBlank: true
			   , width: 290
			});
		}
		return valuesInput;
	}

	   
	// public methods
	
	, clear: function() {
		this.combo.setValue('');
		for (var i = 0; i < this.valuesInputs.length; i++) {
			var aValueInput = this.valuesInputs[i];
			aValueInput.setValue('');
		}
	}
	
	, getFormState: function() {
		var state = {field: this.combo.getValue()};
		for (var i = 0; i < this.valuesInputs.length; i++) {
			var aValueInput = this.valuesInputs[i];
			state[aValueInput.name] = aValueInput.getValue();
		}
		return state;
	}
	
	, setFormState: function(value) {	
		var field = this.combo.setValue(value.field);
		this.getForm().setValues(value);
	}
	
    , isValid: function(){
        if(this.combo.getValue() === '') {
        	return true;
        }
		for (var i = 0; i < this.valuesInputs.length; i++) {
			var aValueInput = this.valuesInputs[i];
			if (aValueInput.getValue() !== null && aValueInput.getValue().trim() === '') {
				return false;
			}
		}
        return true;
    }
    
    , getValidationErrors: function() {
    	var errors = new Array();
    	if (this.isValid()) {
    		return errors;
    	} else {
    		var error = String.format(LN('sbi.formviewer.dynamicfilter.validation.missingvalue'), this.combo.getRawValue());
    		errors.push(error);
    	}
    	return errors;
    }
  	
});