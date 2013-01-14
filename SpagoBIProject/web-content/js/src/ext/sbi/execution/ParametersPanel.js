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
	
	var defaultSettings = {
		columnNo: 3
		, columnWidth: 350
		, labelAlign: 'left'
		, fieldWidth: 200	
		, maskOnRender: false
		, fieldLabelWidth: 100
		, addEmptyValueToCombo: false
		, moveInMementoUsingCtrlKey: false
	};
	
	
	if (Sbi.settings && Sbi.settings.execution && Sbi.settings.execution.parametersPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.execution.parametersPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});	
	Ext.apply(this, c);
	
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
	if (this.parametersPreference) {
		this.preferenceState = Ext.urlDecode(this.parametersPreference);
	}
	
	// always declare exploited services first!
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null, CONTEST: this.contest};
	
	this.services = this.services || new Array();
	
	this.services['getParametersForExecutionService'] = this.services['getParametersForExecutionService'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETERS_FOR_EXECUTION_ACTION'
		, baseParams: params
	});
	this.services['getParameterValueForExecutionService'] = this.services['getParameterValueForExecutionService'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_PARAMETER_VALUES_FOR_EXECUTION_ACTION'
		, baseParams: params
	});
	
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
	
    this.addEvents('beforesynchronize', 'synchronize', 'parametersForExecutionLoaded');	
};

Ext.extend(Sbi.execution.ParametersPanel, Ext.FormPanel, {
    
    services: null
    , executionInstance: null
    
    /**
     * parameters configuration as returned from getParametersForExecutionService. 
     * @see function loadParametersForExecution()
     */
    , parameters: null
   
    /**
     * url encoded parameters whose value must be set during initialization (ex. foodFamily=Drink)
     */
    , parametersPreference: null
    /**
     * url decoded parameters whose value must be set during initialization (ex. {foodFamily:'Drink'})
     */
    , preferenceState: null
    
    /**
     * An array of all the fields contained in the form 
     * Injected properties:
     *  - isTransient: true if ???
     *  - columnNo: the number of the column containing the field
     *  - dependecies: an array of all the fields that depends from this one
     *  - dependants: an array of all fields on which the field depends on
     */
    , fields: null
    
    /**
     * The columns (Ext.FormPanel) that compose the main column layout
     */
    , columns: null
    , baseConfig: null
    , modality : null
    , drawHelpMessage : true
    , mandatoryFieldAdditionalString: null
    
    , manageDataDependencies: true // not used so far but reserved for future use
    , manageVisualDependencies: true
    , manageVisualDependenciesOnVisibility: true
    , manageVisualDependenciesOnLabel: true
    
   
    // ----------------------------------------------------------------------------------------
    // public methods
    // ----------------------------------------------------------------------------------------
    
    
    , synchronize: function( executionInstance ) {
		var sync = this.fireEvent('beforesynchronize', this, executionInstance, this.executionInstance);
		this.executionInstance = executionInstance;
		this.loadParametersForExecution( );
	}

	, getFieldValue: function(field) {
		var value;
		
		if(field.behindParameter.multivalue === true) {
			value = field.getValues();
		} else {
			value = field.getValue();
		}
		
		return value;
	}
	
	, getFormState: function() {
		var state;
		
		//to avoid synchronization problem
		state = {};
		for(p in this.fields) {
			var field = this.fields[p];
			var value = this.getFieldValue(field);;
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
			
			// add objParsId information if present (massive export case)
			if(field.objParameterIds && this.contest=='massiveExport'){
				state[field.name + '_objParameterIds']=field.objParameterIds;
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
	  
	/**
	 * reset all the fields in the form and recalculate all the dependencies
	 */
	, reset: function() {
		//change for menu calls		
		for(p in this.fields) {
			if (!this.isInPreferences(p)){
				var aField = this.fields[p];
				if (!aField.isTransient) {
					aField.reset();
					this.updateDependentFields( aField );
				}
			}
		}
		
		for(p in this.fields) {
			if (!this.isInPreferences(p)){
				var aField = this.fields[p];
				aField.clearInvalid();
			}
		}
		
	}
	
	, isInPreferences: function(p){
		if (this.parametersPreference == null || this.parametersPreference == undefined || this.parametersPreference == "") return false;
		
		var values = this.parametersPreference.split("&");
		for (var i=0, l=values.length; i<l; i++){
			var parName = values[i].substring(0,  values[i].indexOf("="));
			if (parName == p) return true;
		}
		
		return false;
	}
	
	/**
	 * @deprecated use this.reset() instead
	 */
	, clear: function() {
		//alert('function clear() is deprecated');
		this.reset();
	}
	
	, getParentPageNumber: function() {
		return this.baseConfig.pageNumber;
	}
	
	, isInParametersPage: function() {
		return this.getParentPageNumber() === 2;
	}
	
	, isInExecutionPage: function() {
		return this.getParentPageNumber() === 3;
	}
	
	, isReadyForExecution: function() {
		var isReadyForExecution = true;
		if(this.parameters.length == 0) {
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
		return isReadyForExecution;
	}
	
	// ----------------------------------------------------------------------------------------
	// private methods
	// ----------------------------------------------------------------------------------------
	
	
	// =====================================================================================
	// PARAMETERS functions
	// =====================================================================================
	
	, setParameters: function(parameters) {
		this.parameters = parameters;
	}
	
	, parameterHasDependencies: function(parameter) {
		return parameter.dependencies && parameter.dependencies.length > 0;
	}
	
	, parameterHasOnlyOneValue: function(parameter) {
		return parameter.valuesCount !== undefined && parameter.valuesCount == 1;
	}
	
	, parameterValueIsInPreferences: function(parameter) {
		return this.preferenceState !== null && this.preferenceState[parameter.id] !== undefined;
	}
	
	, thereAreParametersToBeFilled: function() {
		var thereAreParametersToBeFilled = false;
		if(this.parameters.length > 0) {
			var o = this.getFormState();
			for(p in o) {
				// must check this.fields[p] is undefined because form state contains also parameters' descriptions
				if(this.fields[p] != undefined && this.fields[p].isTransient === false) {
					thereAreParametersToBeFilled = true;
					break;
				}
			}
		}
		return thereAreParametersToBeFilled;
	}
	
	, loadParametersForExecution: function( ) {
		
		if( !this.executionInstance ) {
			alert("Impossible to load parameters because executionInstance is not properly initialized");
		}
		
		Ext.Ajax.request({
	          url: this.services['getParametersForExecutionService'],
	          
	          params: this.executionInstance,
	          
	          callback : function(options, success, response){
	    	  	if(success && response !== undefined) {   
		      		if(response.responseText !== undefined) {
		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if(content !== undefined) {
		      				this.initializeParametersPanel(content);
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
	
	, initializeParametersPanel: function( parameters ) {
		
//		if(this.isInExecutionPage()) {
//			alert('initializeParametersPanel IN');
//		}
		
		this.setParameters(parameters);
		
		this.removeAllFields();
		
			
		var nonTransientField = 0;
		for(var i = 0; i < parameters.length; i++) {
			var field = this.createField( parameters[i] );
			 
		
			if( this.parameterHasOnlyOneValue( parameters[i] ) ) {
				if( this.parameterHasDependencies( parameters[i] ) || parameters[i].type === 'DATE') {
					this.addField(field, nonTransientField++);
				} else {
					field.isTransient = true;
					field.setValue(parameters[i].value);
				}
			} else {				
				if ( this.parameterValueIsInPreferences(parameters[i]) ) {
					field.setValue(this.preferenceState[parameters[i].id]);
				} else {
					if (parameters[i].visible === true && parameters[i].vizible !== false) {
						this.addField(field, nonTransientField++);
					}
				}
			}
			
			 this.fields[parameters[i].id] = field;
		}
		
		if(this.thereAreParametersToBeFilled() !== true) {
			if (this.rendered) {
				Ext.DomHelper.append(this.body, '<div class="x-grid-empty">' + LN('sbi.execution.parametersselection.noParametersToBeFilled') + '</div>');
			}
		} else {
			// set focus on first field
			// this is a work-around for this problem on IE: very often, the manual input field is not editable;
			// in order to let it be editable, you should click on input label, or above + TAB button
			var firstItem = this.columns[0].items.get(0);
			var itemParameter = firstItem.behindParameter;
			if (itemParameter.typeCode == 'MAN_IN') {
				firstItem.on('render', function(theField) {
					theField.focus();
					theField.clearInvalid();
				}, this);
			}
		}
		
		this.insertHelpMessage();
		
		this.doLayout();
		
		this.initializeFieldDependencies();
		
		if(this.isInParametersPage() && this.isFromCross === false) {
			this.reset();
		} 
		
		this.fireEvent('synchronize', this, this.isReadyForExecution(), this.parametersPreference);
		
//		if(this.isInExecutionPage()) {
//			alert('initializeParametersPanel OUT');
//		}
	}
	
	
	// =====================================================================================
	// DEPENDENCIES management functions
	// =====================================================================================
	
	, initializeFieldDependencies: function() {
		for(var j = 0; j < this.parameters.length; j++) {
			
			if( this.parameters[j].dependencies.length > 0) {
				var field = this.fields[this.parameters[j].id];
				var p =  this.parameters[j];
				
				field.on('focus', function(f){
					for(var i = 0; i < f.dependencies.length; i++) {
						var field = this.fields[ f.dependencies[i].urlName ];
						field.getEl().addClass('x-form-dependent-field');                         
					}		
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
					field.dependencies[i].parameterId = this.parameters[j].id;
					f.dependants.push( field.dependencies[i] );                      
				}	
			}			
		}
		
		for(var p in this.fields) {
			var theField = this.fields[p];
			
			/*
			 * workaround (work-around):
			 * 'change' event works properly for combo-boxes but not for lookup fields (it is not fired, don't know why...);
			 * 'valid' event works properly for lookup fields but not for combo-boxes (it is fired more times and the first time the getValue() method returns the description column, not the value column);
			 * Therefore we mix them...
			 */
			
			if (theField.behindParameter.selectionType === 'COMBOBOX'
				|| theField.behindParameter.selectionType === 'LOOKUP') {
			
				this.fields[p].on('select', function(field, record, index) {
					this.updateDependentFields( field );
				} , this);
				
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
	}
	
	, updateDependentFields: function(f) {
		var hasDataDependency = false;
		var hasVisualDependency = false;
		
		if (f.dependants !== undefined) {
			for(var i = 0; i < f.dependants.length; i++) {
				if(f.dependants[i].hasDataDependency === true) {
					this.updateDataDependentField(f, f.dependants[i]);
					hasDataDependency = true;
				}
				
				if(this.manageVisualDependencies === true && f.dependants[i].hasVisualDependency === true) {
					this.updateVisualDependentField(f, f.dependants[i]);
					hasVisualDependency = true;
				}
			}
		}
		
		if(this.manageVisualDependenciesOnVisibility == true 
			&& hasVisualDependency === true 
			&& Sbi.settings.invisibleParameters.remove === true) {
			
			//alert('doing the dirty tricks: ' + f.name);
		
			this.manageVisualDependenciesOnVisibility = false;
			
			var state = this.getFormState();
			
			this.removeAllFields();
			
			//alert('removed all: ' + f.name);
		
			this.initializeParametersPanel(this.parameters);
						
			//alert('added all: ' + f.name);
			
			this.setFormState(state, true);
			
			this.manageVisualDependenciesOnVisibility = true;
		
		}
	}
	
	, updateDataDependentField: function(fatherField, dependantConf) {
		var field = this.fields[ dependantConf.parameterId ];
		if(field.behindParameter.selectionType === 'COMBOBOX'){ 
			field.store.load();
		}		
		field.reset();
	}
	
	, updateVisualDependentField: function(fatherField, dependantConf) {
		//alert('updateVisualDependentField');
		var dependantField = this.fields[ dependantConf.parameterId ];
		var conditions = dependantConf.visualDependencyConditions;
		
		var fatherFieldValues;
		fatherFieldValues = this.getFieldValue(fatherField);
		if(fatherField.behindParameter.multivalue === false) {
			fatherFieldValues = [fatherFieldValues];
		}
		
		var fatherFieldValueSet = {};
		for(var i = 0; i < fatherFieldValues.length; i++) {
			if(fatherFieldValues[i]){
				var v = Ext.util.Format.trim(fatherFieldValues[i]);
				fatherFieldValueSet[ v ] = v;
			}
		}
		
		var disableField = conditions.length > 0;
		
		for(var i = 0; i < conditions.length; i++) {
			// check condition
			var condition = conditions[i];
			if( this.isVisualConditionTrue(condition, fatherFieldValueSet) ) {
				if(this.manageVisualDependenciesOnLabel === true) {
					this.setFieldLabel(dependantField, condition.label + ':');
				}
				disableField = false;
			}
		}
		
		if(this.manageVisualDependenciesOnVisibility === true) {
			if(disableField) {
				this.setFieldLabel(dependantField, dependantField.fieldDefaultLabel + ':');
				dependantField.reset();
				dependantField.disable();
				
				this.hideFieldLabel(dependantField);
				dependantField.setVisible(false);
				dependantField.parameter.vizible = false;
			} else {
				dependantField.enable();
				dependantField.setVisible(true);	
				dependantField.parameter.vizible = true;
			}
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
	
	
	// =====================================================================================
	// FIELDS functions
	// =====================================================================================
	
	, addField: function(field, index) {
		field.isTransient = false;
		field.columnNo = (index)%this.columns.length;
		this.columns[field.columnNo].add( field );
	}
	
	/**
	 * Remove and destroy all fields contained in the form. This is a private function
	 * and should not be called from an external comeponent. To just reset fields content
	 * use reset().
	 */
	, removeAllFields : function() {
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

	, createField: function( p, c ) {
		var field;
		
		var baseConfig = {
	       fieldLabel: p.label
	       , fieldDefaultLabel: p.label
		   , name : p.id
		   , width: this.baseConfig.fieldWidth
		   , allowBlank: !p.mandatory
		   , parameter: p
		};
		
		var labelStyle = '';
		labelStyle += (p.mandatory === true)?'font-weight:bold;': '';
		labelStyle += (p.dependencies.length > 0)?'font-style: italic;': '';
		labelStyle += 'width: '+this.baseConfig.fieldLabelWidth+'px;';
		baseConfig.labelStyle = labelStyle;
		
		if((this.mandatoryFieldAdditionalString!=null && this.mandatoryFieldAdditionalString!=undefined) && p.mandatory === true ){
			if(baseConfig.fieldDefaultLabel!=undefined && baseConfig.fieldDefaultLabel!=null){
				baseConfig.fieldDefaultLabel =  baseConfig.fieldDefaultLabel+' *';
			}
			if(baseConfig.fieldLabel!=undefined && baseConfig.fieldLabel!=null){
				baseConfig.fieldLabel =  baseConfig.fieldLabel+' *';
			}
		}
		
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
			Ext.apply(baseParams, this.executionInstance);
			Ext.apply(baseParams, {
				PARAMETER_ID: p.id
				, MODE: 'simple'
				, OBJ_PARAMETER_IDS: p.objParameterIds  // ONly in massive export case
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
			
			//on the load event, adds an empty value for the reset at the first 
			// position ONLY if the lov doesn't return an element with empty description
			if(this.addEmptyValueToCombo) {
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
			}
			
			/*
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
			*/
			field = new Ext.ux.Andrie.Select(Ext.apply(baseConfig, {
				multiSelect: p.multivalue
				//, minLength:2
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
				, xtype : 'combo'
				, listeners: {
				    'select': {
				       	fn: function(){	}
				       	, scope: this
				    }			    
				}
			}));

			
			
		} else if(p.selectionType === 'LOOKUP') {
			
			var params = Ext.apply({}, {
				PARAMETER_ID: p.id
				, MODE: 'complete'
				, OBJ_PARAMETER_IDS: p.objParameterIds  // ONly in massive export case
			}, this.executionInstance);
			delete params.PARAMETERS;
			
			var store = this.createStore();
			store.on('beforeload', function(store, o) {
				var p = Sbi.commons.JSON.encode(this.getFormState());
				o.params.PARAMETERS = p;
				return true;
			}, this);
			
			
			//field = new Sbi.execution.LookupFieldWithTableWindow(Ext.apply(baseConfig, {
			field = new Sbi.widgets.LookupField(Ext.apply(baseConfig, {
				
				  store: store
					, params: params
					, readOnly: true
					, singleSelect: (p.multivalue === false)
			}));
			
			
		} else { 
			if(p.type === 'DATE' || p.type ==='DATE_DEFAULT') {		
				baseConfig.format = Sbi.config.localizedDateFormat;
				field = new Ext.form.DateField(baseConfig);
				if(p.type ==='DATE_DEFAULT') {
					field.setValue(new Date());
					
				}
				
			} else {
				if (p.enableMaximizer) {
					field = new Sbi.execution.LookupFieldWithMaximize(baseConfig);
				} else {
					field = new Ext.form.TextField(baseConfig);
				}
				
			}			
		}
		
		field.behindParameter = p;
		field.dependencies = p.dependencies;
		
		// add information: objParameterIds if present (massive export case)
		if(p.objParameterIds){
			field.objParameterIds = p.objParameterIds;	
		}
		
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
	
	// =====================================================================================
	// HELP messages functions
	// =====================================================================================
	
	// Help message on Parameters Panel.
	// work-around: since the panel toolbar may be to short, the message is injected with Ext.DomHelper.insertFirst on the body of
	// the panel, but a function for width calculation is necessary (this function does not work on page 3 when executing in
	// document browser with tree structure initially opened, since containerWidth is 0).
	// TODO: try to remove the on resize method and the width calculation
	, insertHelpMessage: function() {
		
		if (this.messageElement == undefined && this.rendered && (this.drawHelpMessage == true)) {
			var containerWidth = this.getInnerWidth();
			this.widthDiscrepancy = Ext.isIE ? 1 : 5;
			var initialWidth = containerWidth > this.formWidth ? containerWidth - this.widthDiscrepancy: this.formWidth;
			
			var message = this.getHelpMessage(this.executionInstance, this.thereAreParametersToBeFilled());
			
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
	
	// =====================================================================================
	// MEMENTO functions
	// =====================================================================================
	
	, setValueFromMemento: function(f, moveDown) {
		if(!f.memento) return;
		
		if(moveDown) {
			f.memento.readCursor--;
		} else {
			f.memento.readCursor++;
		}
		
		var lastStateIndex = f.memento.size - 1;
		if(f.memento.readCursor < 0) {
			f.memento.readCursor = lastStateIndex; 
		}
		else if(f.memento.readCursor > lastStateIndex) {
			f.memento.readCursor = 0;
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
	
});