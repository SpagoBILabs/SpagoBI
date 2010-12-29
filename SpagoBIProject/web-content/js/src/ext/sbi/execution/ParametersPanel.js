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
  * - Andrea Gioia (andrea.gioia@eng.it)
  */


Ext.ns("Sbi.execution");

Sbi.execution.ParametersPanel = function(config) {
	
	var settings = {
		columnNo: 3
		, columnWidth: 350
		, labelAlign: 'left'
		, fieldWidth: 200	
		, maskOnRender: false
		, fieldLabelWidth: 100
	};
	if(Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.parametersPanel) {
		settings = Sbi.settings.execution.parametersPanel;
	}
	
	// create a new variable and store settings into this new variable
	var temp = {};
	temp = Ext.apply(temp, settings);
	
	// merge settings and input configuration
	var c = Ext.apply(temp, config || {});
	this.baseConfig = c;
	
	this.parametersPreference = undefined;
	if (c.parameters) {
		this.parametersPreference = c.parameters;
	}
	
	// always declare exploited services first!
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
	this.services = new Array();
	this.services['getParametersForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETERS_FOR_EXECUTION_ACTION'
		, baseParams: params
	});
	this.services['getParameterValueForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETER_VALUES_FOR_EXECUTION_ACTION'
		, baseParams: params
	});
	
	//var cw = 1/c.columnNo;
	this.formWidth = (c.columnWidth * c.columnNo) + 40;
	var columnsBaseConfig = [];
	for(var i = 0; i < c.columnNo; i++) {		
		columnsBaseConfig[i] = {
			width: c.columnWidth,
            layout: 'form',
            border: false,
            bodyStyle:'padding:5px 5px 5px 5px'
		}
	}

	c = Ext.apply({}, c, {
		labelAlign: c.labelAlign,
        border: false,
        //bodyStyle:'padding:10px 0px 10px 10px',
        autoScroll: true,
        items: [{
            layout:'column',
            width: this.formWidth, 
            border: false,
            items: columnsBaseConfig
        }]
	});
	
	// constructor
    Sbi.execution.ParametersPanel.superclass.constructor.call(this, c);
	
	var columnContainer = this.items.get(0);
	this.columns = [];
	for(var i = 0; i < c.columnNo; i++) {
		this.columns[i] = columnContainer.items.get(i);
	}
	
    this.addEvents('beforesynchronize', 'synchronize');	
};

Ext.extend(Sbi.execution.ParametersPanel, Ext.FormPanel, {
    
    services: null
    , executionInstance: null
    , parametersPreference: null
    
    , fields: null
    , columns: null
    , baseConfig: null
    
    
   
    // ----------------------------------------------------------------------------------------
    // public methods
    // ----------------------------------------------------------------------------------------
    
    
    , synchronize: function( executionInstance ) {
		var sync = this.fireEvent('beforesynchronize', this, executionInstance, this.executionInstance);
		this.executionInstance = executionInstance;
		this.loadParametersForExecution( this.executionInstance );
	}
	
	, getFormState: function() {
		var state;
		
		state = {};
		for(p in this.fields) {
	
			var field = this.fields[p];
			var value = field.getValue();
			field.focus();
			state[field.name] = value;

			var rawValue = field.getRawValue();
			if (rawValue !== undefined) {
				// TODO to improve: the value of the field should be an object with actual value and its description
				// Conflicts with other parameters are avoided since the parameter url name max lenght is 20
				state[field.name + '_field_visible_description'] = rawValue;
			}
		}
		return state;
	}
	
	, setFormState: function( state ) {
		var state;	
		for(p in state) {
			var fieldName = p;
			var fieldValue = state[p];
			if(this.fields[fieldName]) {
				this.fields[fieldName].setValue( fieldValue );
				var fieldDescription = fieldName + '_field_visible_description';
				var rawValue = state[fieldDescription];
				if (rawValue !== undefined && rawValue != null && this.fields[fieldName].rendered === true) {
					this.fields[fieldName].setRawValue( rawValue );
				}
			}
		}
	}
	
	, applyViewPoint: function(v) {
		for(var p in v) {
			var str = '' + v[p];
			if(str.split(';').length > 1) {
				v[p] = str.split(';');
			}
		}
		this.setFormState(v);
	}
	
	
	, clear: function() {
		for(p in this.fields) {
			var aField = this.fields[p];
			if (!aField.isTransient) {
				aField.reset();
			}
		}
	}
	
	// ----------------------------------------------------------------------------------------
	// private methods
	// ----------------------------------------------------------------------------------------
	
	, loadParametersForExecution: function( executionInstance ) {
		Ext.Ajax.request({
	          url: this.services['getParametersForExecutionService'],
	          
	          params: executionInstance,
	          
	          callback : function(options, success, response){
	    	  	if(success && response !== undefined) {   
		      		if(response.responseText !== undefined) {
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if(content !== undefined) {
		      				this.onParametersForExecutionLoaded(executionInstance, content);
		      			} 
		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
	    	  	}
	          },
	          scope: this,
	  		  failure: Sbi.exception.ExceptionHandler.handleFailure      
	     });
	}

	, onParametersForExecutionLoaded: function( executionInstance, parameters ) {
		
		// clears the form
		for(p in this.fields) {
			// if input field has an element (it means that the field was displayed)
			if (this.fields[p].el !== undefined) {
				// retrieves the element containing label plus input field and removes it
				var el = this.fields[p].el.up('.x-form-item');
				this.columns[this.fields[p].columnNo].remove( this.fields[p], true );
				el.remove();
			}
		}
		
		this.fields = {};
		var preferenceState = undefined;
		if (this.parametersPreference) {
			preferenceState = Ext.urlDecode(this.parametersPreference);
		}
		
		var nonTransientField = 0;
		for(var i = 0; i < parameters.length; i++) {
			var field = this.createField( executionInstance, parameters[i] );
			if(parameters[i].valuesCount !== undefined && parameters[i].valuesCount == 1) {
				field.isTransient = true;
				field.setValue(parameters[i].value);
			} else if (preferenceState !== undefined && preferenceState[parameters[i].id] !== undefined) {
				field.isTransient = true;
				field.setValue(preferenceState[parameters[i].id]);
			} else if (parameters[i].visible === false) {
				field.isTransient = true;
				if (preferenceState !== undefined && preferenceState[parameters[i].id] !== undefined) {
					field.setValue(preferenceState[parameters[i].id]);
				}
			} else {
				field.isTransient = false;
				field.columnNo = (nonTransientField++)%this.columns.length;
				this.columns[field.columnNo].add( field );
			}
			this.fields[parameters[i].id] = field;
		}
		
		var thereAreParametersToBeFilled = false;
		if(parameters.length > 0) {
			var o = this.getFormState();
			for(p in o) {
				// must check is this.fields[p] is undefined because form state contains also parameters' descriptions
				if(this.fields[p] != undefined && this.fields[p].isTransient === false) {
					thereAreParametersToBeFilled = true;
					break;
				}
			}
		}
		
		if(thereAreParametersToBeFilled !== true) {
			if (this.rendered) {
				Ext.DomHelper.append(this.body, '<div class="x-grid-empty">' + LN('sbi.execution.parametersselection.noParametersToBeFilled') + '</div>');
			}
		} else {
			// set focus on first field
			// this is a work-around for this problem on IE: very often, the manual input field is not editable;
			// in order to let it be editable, you should click on input label, or above + TAB button
			this.columns[0].items.get(0).on('render', function(theField) {
				theField.focus();
				theField.clearInvalid();
			}, this);
		}
		
		// Help message on Parameters Panel.
		// work-around: since the panel toolbar may be to short, the message is injected with Ext.DomHelper.insertFirst on the body of
		// the panel, but a function for width calculation is necessary (this function does not work on page 3 when executing in
		// document browser with tree structure initially opened, since containerWidth is 0).
		// TODO: try to remove the on resize method and the width calculation
		if (this.messageElement == undefined && this.rendered) {
			var containerWidth = this.getInnerWidth();
			this.widthDiscrepancy = Ext.isIE ? 1 : 5;
			var initialWidth = containerWidth > this.formWidth ? containerWidth - this.widthDiscrepancy: this.formWidth;
			var message = this.getHelpMessage(executionInstance, thereAreParametersToBeFilled);
			this.messageElement = Ext.DomHelper.insertFirst(this.body, 
					'<div style="font-size: 12px; font-family: tahoma,verdana,helvetica; margin-bottom: 14px; color: rgb(24, 18, 241);' 
					+ (containerWidth === 0 ? '' : 'width: ' + initialWidth + 'px;') + '"'  
					+ ' class="x-panel-tbar x-panel-tbar-noheader x-toolbar x-panel-tbar-noborder x-btn-text x-item-disabled">'
					+ message
					+ '</div>');
			this.on('resize', function() {
				var containerWidth = this.getInnerWidth();
				this.messageElement.style.width = containerWidth > this.formWidth ? containerWidth - this.widthDiscrepancy: this.formWidth;
			}, this);
		}
		
		this.doLayout();
		
		for(var j = 0; j < parameters.length; j++) {
			
			if(parameters[j].dependencies.length > 0) {
				var field = this.fields[parameters[j].id];
				var p = parameters[j];
				
				field.on('focus', function(f){
					for(var i = 0; i < f.dependencies.length; i++) {
						var field = this.fields[ f.dependencies[i] ];
						field.getEl().addClass('x-form-dependent-field');                         
					}		
					//alert(f.getName() + ' get focus');
				}, this, {delay:250});
				
				field.on('blur', function(f){
					//alert(f.getName() + ' lose focus');
					for(var i = 0; i < f.dependencies.length; i++) {
						var field = this.fields[ f.dependencies[i] ];
						field.getEl().removeClass('x-form-dependent-field');                         
					}
				}, this);
				
				for(var i = 0; i < field.dependencies.length; i++) {
					var f = this.fields[ field.dependencies[i] ];
					f.dependants = f.dependants || [];
					f.dependants.push( parameters[j].id );                      
				}	
			}			
		}
		

		for(var p in this.fields) {
		//patch: event changed from 'change' to 'valid' in order to make work the parameters correlation
			this.fields[p].on('valid', function(f) {
				if(f.dependants !== undefined) {
				
					for(var i = 0; i < f.dependants.length; i++) {

						var field = this.fields[ f.dependants[i] ];

						if(field.behindParameter.selectionType === 'COMBOBOX'){ 
							field.store.load();
						}
					}
				}
			}, this); 
			
		}
		
		var isReadyForExecution = true;
		if(parameters.length == 0) {
			isReadyForExecution = true;
		} else 	{
			var o = this.getFormState();
			for(p in o) {
				// must check is this.fields[p] is undefined because form state contains also parameters' descriptions
				if(this.fields[p] != undefined && this.fields[p].isTransient === false) {
					isReadyForExecution = false;
					break;
				}
			}
		}
		
		this.fireEvent('synchronize', this, isReadyForExecution, this.parametersPreference);
		
	}
	
	, createField: function( executionInstance, p, c ) {
		var field;
		
		//alert(p.id + ' - ' + p.selectionType + ' - ' + !p.mandatory);
		var baseConfig = {
	       fieldLabel: p.label
		   , name : p.id
		   , width: this.baseConfig.fieldWidth
		   , allowBlank: !p.mandatory
		};
		
		var labelStyle = '';
		labelStyle += (p.mandatory === true)?'font-weight:bold;': '';
		labelStyle += (p.dependencies.length > 0)?'font-style: italic;': '';
		labelStyle += 'width: '+this.baseConfig.fieldLabelWidth+'px;';
		baseConfig.labelStyle = labelStyle;
		
		//if(p.dependencies.length > 0) baseConfig.fieldClass = 'background-color:yellow;';
		
		if(p.selectionType === 'COMBOBOX') {
			var baseParams = {};
			Ext.apply(baseParams, executionInstance);
			Ext.apply(baseParams, {
				PARAMETER_ID: p.id
				, MODE: 'simple'
			});
			delete baseParams.PARAMETERS;
			
			var store = this.createStore();
			store.baseParams  = baseParams;
			store.on('beforeload', function(store, o) {
				var p = Sbi.commons.JSON.encode(this.getFormState());
				o.params = o.params || {};
				o.params.PARAMETERS = p;
				return true;
			}, this);
			
			store.load(/*{params: param}*/);
			
			field = new Ext.form.ComboBox(Ext.apply(baseConfig, {
				tpl: '<tpl for="."><div ext:qtip="{label} ({value}): {description}" class="x-combo-list-item">{label}</div></tpl>'
                , editable  : true			    
			    , forceSelection : false
			    , store :  store
			    , displayField:'label'
			    , valueField:'value'
			    , emptyText: ''
			    , typeAhead: false
			    //, typeAheadDelay: 1000
			    , triggerAction: 'all'
			    , selectOnFocus:true
			    , autoLoad: false
			    , mode : 'local'
			    , listeners: {
			    	'select': {
			       		fn: function(){}
			       		, scope: this
			    	}
			    }
			}));
			
		} else if(p.selectionType === 'LIST' || p.selectionType ===  'CHECK_LIST') {
			
			var params = Ext.apply({}, {
				PARAMETER_ID: p.id
				, MODE: 'complete'
			}, executionInstance);
			delete params.PARAMETERS;
			
			var store = this.createStore();
			store.on('beforeload', function(store, o) {
				var p = Sbi.commons.JSON.encode(this.getFormState());
				o.params.PARAMETERS = p;
				return true;
			}, this);
			
			field = new Sbi.widgets.LookupField(Ext.apply(baseConfig, {
				  store: store
					, params: params
					, singleSelect: (p.selectionType === 'LIST')
			}));
			
			
		} else { 
			if(p.type === 'DATE') {		
				baseConfig.format = Sbi.config.localizedDateFormat;
				field = new Ext.form.DateField(baseConfig);
				
				
				
			} else if(p.type === 'NUMBER') {
				field = new Ext.form.NumberField(baseConfig);
			} else {
				field = new Ext.form.TextField(baseConfig);
			}			
		}
		
		field.behindParameter = p;
		field.dependencies = p.dependencies;
		
		return field;
	}
	
	, createStore: function() {
		var store;
		
		store = new Ext.data.JsonStore({
			url: this.services['getParameterValueForExecutionService']
		});
		
		store.on('loadexception', function(store, options, response, e) {
			Sbi.exception.ExceptionHandler.handleFailure(response, options);
		});
		
		return store;
		
	}
	
	, getHelpMessage: function(executionInstance, thereAreParametersToBeFilled) {
		if (this.baseConfig.pageNumber === 2) {
			return this.getHelpMessageForPage2(executionInstance, thereAreParametersToBeFilled);
		} else {
			return this.getHelpMessageForPage3(executionInstance, thereAreParametersToBeFilled);
		}
	}
	
	, getHelpMessageForPage2: function(executionInstance, thereAreParametersToBeFilled) {
		var toReturn = null;
		var doc = executionInstance.document;
		if (doc.typeCode == 'DATAMART' && this.baseConfig.subobject == undefined) {
			if (Sbi.user.functionalities.contains('BuildQbeQueriesFunctionality')) {
				if (!thereAreParametersToBeFilled) {
					toReturn = LN('sbi.execution.parametersselection.message.page2.qbe.powerUserMessageWithoutParameters');
				} else {
					toReturn = LN('sbi.execution.parametersselection.message.page2.qbe.powerUserMessageWithParameters');
				}
			} else {
				if (!thereAreParametersToBeFilled) {
					toReturn = LN('sbi.execution.parametersselection.message.page2.qbe.readOnlyUserMessageWithoutParameters');
				} else {
					toReturn = LN('sbi.execution.parametersselection.message.page2.qbe.readOnlyUserMessageWithParameters');
				}
			}
		} else {
			if (!thereAreParametersToBeFilled) {
				toReturn = LN('sbi.execution.parametersselection.message.page2.execute');
			} else {
				toReturn = LN('sbi.execution.parametersselection.message.page2.fillFormAndExecute');
			}
		}
		return toReturn;
	}
	
	, getHelpMessageForPage3: function(executionInstance, thereAreParametersToBeFilled) {
		var toReturn = null;
		if (!thereAreParametersToBeFilled) {
			toReturn = LN('sbi.execution.parametersselection.message.page3.refresh');
		} else {
			toReturn = LN('sbi.execution.parametersselection.message.page3.fillFormAndRefresh');
		}
		return toReturn;
	}
	
});