/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
	defaultSettings
	var defaultSettings = {
		columnNo: 3
		, columnWidth: 350
		, labelAlign: 'left'
		, fieldWidth: 200	
		, maskOnRender: false
		, fieldLabelWidth: 100
		, moveInMementoUsingCtrlKey: false
	};
	
	
	if (Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.parametersPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.execution.parametersPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
	
//	if(Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.parametersPanel) {
//		defaultSettings = Sbi.settings.execution.parametersPanel;
//	}
	
	// create a new variable and store settings into this new variable
	var temp = {};
	temp = Ext.apply(temp, defaultSettings);
	
	// merge settings and input configuration
	var c = Ext.apply(temp, config || {});
	this.baseConfig = c;
	
	this.parametersPreference = undefined;
	if (c.parameters) {
		this.parametersPreference = c.parameters;
	}
	//if(c.isFromCross) alert('parametersPreference: ' + this.parametersPreference.toSource());
	
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
	this.formWidth = (c.columnWidth * c.columnNo) ;
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
		//to avoid synchronization problem
		
		state = {};
		for(p in this.fields) {
			var field = this.fields[p];
			var value = field.getValue();
			state[field.name] = value;
			var rawValue = field.getRawValue();
			if(value == "" && rawValue != ""){
				state[field.name] = rawValue;
			}
			
			
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
					this.updateDependentFields( this.fields[fieldName] );
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
				this.updateDependentFields( aField );
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
			//if(this.isFromCross) alert('preferenceState: ' + preferenceState.toSource());
		}
	
		
		var nonTransientField = 0;
		for(var i = 0; i < parameters.length; i++) {
			var field = this.createField( executionInstance, parameters[i] );
			 
			
			// check if parameter has dependencies
			var hasDependencies = false;
			if(parameters[i].dependencies && parameters[i].dependencies.length>0){			
				hasDependencies = true;
			}
				
			// if parameter has only one value but has dependencies draw it
			if(parameters[i].valuesCount !== undefined && parameters[i].valuesCount == 1 && hasDependencies == true) {
				field.isTransient = false;
				field.columnNo = (nonTransientField++)%this.columns.length;
				this.columns[field.columnNo].add( field );

			}
			else if(parameters[i].valuesCount !== undefined && parameters[i].valuesCount == 1 && parameters[i].type !== 'DATE') {
				field.isTransient = true;
				field.setValue(parameters[i].value);
				
			} else if (preferenceState !== undefined && preferenceState[parameters[i].id] !== undefined) {
				//field.isTransient = true;
				//if(this.isFromCross) alert(parameters[i].id + ' set equals to ' + preferenceState[parameters[i].id]);
				field.setValue(preferenceState[parameters[i].id]);
			} else if (parameters[i].visible === false) {
				//field.isTransient = true;
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
		
		if(this.isFromCross) {
			//alert('formState[after set]: ' + this.getFormState().toSource());
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
		
		//if(this.isFromCross) alert('thereAreParametersToBeFilled?' + thereAreParametersToBeFilled);
		
		if(thereAreParametersToBeFilled !== true) {
			if (this.rendered) {
				Ext.DomHelper.append(this.body, '<div class="x-grid-empty">' + LN('sbi.execution.parametersselection.noParametersToBeFilled') + '</div>');
			}
		} else {
			// set focus on first field
			// this is a work-around for this problem on IE: very often, the manual input field is not editable;
			// in order to let it be editable, you should click on input label, or above + TAB button
			var firstItem = this.columns[0].items.get(0);
			var itemParameter = firstItem.behindParameter;
			// CONTROLLARE SE VA ANCORA BENE!!!
			if (itemParameter.typeCode == 'MAN_IN') {
				firstItem.on('render', function(theField) {
					theField.focus();
					theField.clearInvalid();
				}, this);
			}
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
						var field = this.fields[ f.dependencies[i].urlName ];
						field.getEl().addClass('x-form-dependent-field');                         
					}		
					//alert(f.getName() + ' get focus');
				}, this
				, {delay:250}
				);
				
				field.on('blur', function(f){
					//alert(f.getName() + ' lose focus');
					for(var i = 0; i < f.dependencies.length; i++) {
						var field = this.fields[ f.dependencies[i].urlName ];
						field.getEl().removeClass('x-form-dependent-field');                         
					}
				}, this);
				
				for(var i = 0; i < field.dependencies.length; i++) {
					var f = this.fields[ field.dependencies[i].urlName ];
					f.dependants = f.dependants || [];
					//f.dependants.push( parameters[j].id ); 
					field.dependencies[i].parameterId = parameters[j].id;
					f.dependants.push( field.dependencies[i] );                      
				}	
			}			
		}
		
		if(this.isFromCross) {
			//alert('formState[before udating dependecies]: ' + this.getFormState().toSource());
		}
		
		for(var p in this.fields) {
			var theField = this.fields[p];
			this.updateDependentFields( theField );
			
			if(this.isFromCross) {
				//alert('formState[after updateDependentFields on ' + p + ']: ' + this.getFormState().toSource());
			}
			
			/*
			 * workaround (work-around):
			 * 'change' event works properly for combo-boxes but not for lookup fields (it is not fired, don't know why...);
			 * 'valid' event works properly for lookup fields but not for combo-boxes (it is fired more times and the first time the getValue() method returns the description column, not the value column);
			 * Therefore we mix them...
			 */
			
			if (theField.behindParameter.selectionType === 'COMBOBOX'
				|| theField.behindParameter.selectionType === 'LIST'
				|| theField.behindParameter.selectionType === 'CHECK_LIST'	) {
				this.fields[p].on('select', this.updateDependentFields , this);
				//this.fields[p].on('change', this.updateDependentFields , this);
			} else if(theField.behindParameter.typeCode == 'MAN_IN') {
				// if input field has an element (it means that the field was displayed)
				if (theField.el !== undefined) {
					
					theField.el.on('keydown', 
						this.updateDependentFields.createDelegate(this, [theField]), this, {buffer: 350});
					
					
					var onKeyDown = function(event, element, options , field){
						if( event.keyCode == 38 || event.keyCode == 40 ) {
							if(!this.moveInMementoUsingCtrlKey || event.ctrlKey == true) {
								var moveDown = (event.keyCode == 40);
								this.setValueFromMemento(field, moveDown);
							}
						} 
					}
					
					theField.el.on( 'keydown', onKeyDown.createDelegate(this, theField, true), this );
				}
			} else {
				alert("Unable to manage dependencies on input field of type [" + theField.behindParameter.selectionType + "]");
			}
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
		
		//if(this.isFromCross) alert('isReadyForExecution? ' + isReadyForExecution);
		
		if(this.isFromCross && isReadyForExecution) {
			//alert('parametersPreference: ' + this.parametersPreference.toSource());
			//alert('formState: ' + this.getFormState().toSource());
		}
		
		this.fireEvent('synchronize', this, isReadyForExecution, this.parametersPreference);
		
	}
	
	, setValueFromMemento: function(f, moveDown) {
		if(!f.memento) return;
		//alert('setValueFromMemento');
		
		if(moveDown) {
			f.memento.readCursor--;
			//alert("MOVE DOWN ->" + f.memento.readCursor);
		} else {
			f.memento.readCursor++;
			//alert("MOVE UP - > " + f.memento.readCursor);
		}
		
		var lastStateIndex = f.memento.size - 1;
		if(f.memento.readCursor < 0) {
			f.memento.readCursor = lastStateIndex; 
			//alert('vai alla fine >' + f.memento.readCursor);
		}
		else if(f.memento.readCursor > lastStateIndex) {
			f.memento.readCursor = 0;
			//alert('torna all inizio > '+ f.memento.readCursor);
		}
		
		
		var state;
				
		state = f.memento.states[f.memento.readCursor];
		
		f.setValue( state.value );
		var fieldDescription = f.name + '_field_visible_description';
		var rawValue = state.description;
		if (state.description !== undefined && state.description != null && f.rendered === true) {
			f.setRawValue( state.description );
			this.updateDependentFields( f );
		}		
	}
	
	, updateDependentFields: function(f) {
		
		//alert(f.getValue());
		
		if (f.dependants !== undefined) {
			for(var i = 0; i < f.dependants.length; i++) {
				if(f.dependants[i].hasDataDependency === true) {
					this.updateDataDependentField(f, f.dependants[i]);
				}
				
				if(f.dependants[i].hasVisualDependency === true) {
					this.updateVisualDependentField(f, f.dependants[i]);
				}
			}
		}
	}
	
	, updateDataDependentField: function(fatherField, dependantConf) {
		//alert("Update data dependency ...");
		var field = this.fields[ dependantConf.parameterId ];
		if(field.behindParameter.selectionType === 'COMBOBOX'){ 
			field.store.load();
		}		
		field.reset();
	}
	
	, updateVisualDependentField: function(fatherField, dependantConf) {
		var dependantField = this.fields[ dependantConf.parameterId ];
		var conditions = dependantConf.visualDependencyConditions;
		
		var fatherFieldValues;
		if(fatherField.behindParameter.selectionType === 'CHECK_LIST') {
			fatherFieldValues = fatherField.getValue();
		} else {
			fatherFieldValues = [fatherField.getValue()];
		}
		var fatherFieldValueSet = {};
		for(var i = 0; i < fatherFieldValues.length; i++) {
			if(fatherFieldValues[i]){
				//var v = fatherFieldValues[i].trim();
				var v = Ext.util.Format.trim(fatherFieldValues[i]);
				fatherFieldValueSet[ v ] = v;
			}
		}
		
		var disableField = conditions.length > 0;
		
		for(var i = 0; i < conditions.length; i++) {
			// check condition
			var condition = conditions[i];
			if( this.isVisualConditionTrue(condition, fatherFieldValueSet) ) {
				this.setFieldLabel(dependantField, condition.label + ':');
				disableField = false;
			}
		}
		
		if(disableField) {
			this.setFieldLabel(dependantField, dependantField.fieldDefaultLabel + ':');
			//dependantField.addClass('x-exec-paramlabel-disabled');
			dependantField.reset();
			dependantField.disable();
			this.hideFieldLabel(dependantField);
			dependantField.setVisible(false);
		} else {
			//dependantField.removeClass('x-exec-paramlabel-disabled');
			dependantField.enable();
			dependantField.setVisible(true);			
		}
	}
	
	, isVisualConditionTrue: function(condition, fatherFieldValueSet) {
		var conditionIsTrue = false;
		var values = condition.value.split(',');
		for(var i = 0; i < values.length; i++) {
		  if(values[i]){
			//var v = values[i].trim();
			var v = Ext.util.Format.trim(values[i]);
			if(fatherFieldValueSet[v]) {
				conditionIsTrue = true;
				break;
			}
		   }
		}
		
		//alert(conditionIsTrue + " ->> " + condition.toSource());
		return (condition.operation == 'contains')? conditionIsTrue: !conditionIsTrue;
	}
	
	, setFieldLabel: function(field, label){   
		// if input field has no element it means that the field wasn't displayed so we have 
		// nothing to do here
		if (field.el === undefined) return;
		
		var el = field.el.dom.parentNode.parentNode;    
		if( el.children[0].tagName.toLowerCase() === 'label' ) {  
			//el.children[0].class = 'x-exec-paramlabel-disabled';
			el.children[0].innerHTML =label;    
		} else if( el.parentNode.children[0].tagName.toLowerCase() === 'label' ){    
			//el.parentNode.children[0].class = 'x-exec-paramlabel-disabled';
			el.parentNode.children[0].innerHTML =label;  
			
		}    
	}
	
	, hideFieldLabel: function(field){    
		// if input field has no element it means that the field wasn't displayed so we have 
		// nothing to do here
		if (field.el === undefined) return;
		
		var el = field.el.dom.parentNode.parentNode;    
		if( el.children[0].tagName.toLowerCase() === 'label' ) {  
			//el.children[0].class = 'x-exec-paramlabel-disabled';
			el.children[0].innerHTML = '';    
		} else if( el.parentNode.children[0].tagName.toLowerCase() === 'label' ){    
			//el.parentNode.children[0].class = 'x-exec-paramlabel-disabled';
			el.parentNode.children[0].innerHTML ='';  
			
		}    
	}

	, createField: function( executionInstance, p, c ) {
		var field;
		
		//alert(p.id + ' - ' + p.selectionType + ' - ' + !p.mandatory);
		var baseConfig = {
	       fieldLabel: p.label
	       , fieldDefaultLabel: p.label
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
		if(p.type === 'DATE' && p.selectionType !== 'MAN_IN') {		
			baseConfig.format = Sbi.config.localizedDateFormat;
			
			field = new Ext.form.DateField(baseConfig);
			
			if(p.value !== undefined && p.value !== null) {	
				var dt = Sbi.commons.Format.date(p.value, Sbi.config.clientServerDateFormat);
				field.setValue(p.value);				
			}
		
		} else if(p.selectionType === 'COMBOBOX') {
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
			
			//on the load event, adds an empty value for the reset at the first position ONLY if the lov doesn't return an 
			//element with empty description
			store.on('load', function(store, records, options) {
				
				var exist = false;
				for (i =0, l= records.length; i<l; i++ ){
					if (store.getAt(i).get('description') === '' ){
						exist = true;
						break;
					}
				}

				if (!exist){
					var emptyData = {
							value: '',
							label: '',
							description:'Empty value'
						};
				
					var emptyId =  store.getTotalCount()+1;
					var r = new store.recordType(emptyData, emptyId); // create new record
					store.insert(0, r);
				}
			}, this);
			
			/*
			 * The following store.load() instruction should not be necessary: the parameter's values are loaded when combobox is expanded
			 */
			//store.load(/*{params: param}*/);
			
			field = new Ext.form.ComboBox(Ext.apply(baseConfig, {
				tpl: '<tpl for="."><div ext:qtip="{label} ({value}): {description}" class="x-combo-list-item">{label}&nbsp;</div></tpl>'
                , editable  : false			    
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
				/*
				 * The following "mode : 'local'" instruction should not be necessary: the parameter's values are loaded from remote server
				 */
			    //, mode : 'local'
			    // used to hack it the first time the panel is expanded in DocumentExecutionPage
			    , xtype : 'combo'
			    , listeners: {
			    	'select': {
			       		fn: function(){	
						}
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
					, readOnly: true
					, singleSelect: (p.selectionType === 'LIST')
			}));
			
			
		} else { 
			if(p.type === 'DATE' || p.type ==='DATE_DEFAULT') {		
				baseConfig.format = Sbi.config.localizedDateFormat;
				field = new Ext.form.DateField(baseConfig);
				if(p.type ==='DATE_DEFAULT') {
					field.setValue(new Date());
					
				}
				
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