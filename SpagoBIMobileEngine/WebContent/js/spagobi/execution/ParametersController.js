/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.define('app.controllers.ParametersController',{
	extend: 'Ext.app.Controller',
	config:{
	},
	constructor: function()  {
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};

		this.services = new Array();

		this.services['getParametersForExecutionAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_PARAMETERS_FOR_EXECUTION_ACTION'
				, baseParams: params
		});

		this.services['getParameterValueForExecutionService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_PARAMETER_VALUES_FOR_EXECUTION_ACTION'
				, baseParams: params
		});
	}

	
	, getParametersForExecutionAction: function(option){
	
		var id = option.id;
		var label = option.label;
		var roleName = option.roleName;
		var sbiExecutionId = option.sbiExecutionId;
		var typeCode = option.typeCode;
		var engine = option.engine;
		//cross navigation settings
		var isFromCross = option.isFromCross;
		var paramsFromCross = option.params;//filled only from cross navigation
		
		Ext.Ajax.request({
			url: this.services['getParametersForExecutionAction'],
			scope: this,
			method: 'post',
			params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName, SBI_EXECUTION_ID: sbiExecutionId},
			success: function(response, opts) {
				if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
					var responseJson = Ext.decode(response.responseText);
					
					var executionInstance = {
							OBJECT_ID: id, 
							OBJECT_LABEL: label, 
							isFromCross: isFromCross, 
							ROLE:roleName, 
							SBI_EXECUTION_ID: sbiExecutionId,
							ENGINE: engine, 
							TYPE_CODE: typeCode
					};
					
					if(responseJson==undefined || responseJson==null || responseJson.length==0  ){
						  app.controllers.executionController.executeTemplate({executionInstance: executionInstance});
					}else{
						if(isFromCross){

							var parameters = this.onParametersForExecutionLoaded(executionInstance,responseJson);
							app.controllers.mobileController.destroyExecutionView();
							var paramsToBeFilled = parameters.slice(0);
							var paramsFromCrossFilled= this.fillParametersFromCross(parameters, paramsFromCross, paramsToBeFilled);
							if(paramsToBeFilled.length == 0){
								//execute now!
								executionInstance.PARAMETERS = this.fromArrayToObject(paramsFromCross);
								executionInstance.isFromCross = true;
								controller: app.controllers.executionController.executeTemplate({executionInstance: executionInstance});
							}else{
//								app.views.parameters = Ext.create("app.views.ParametersView");
								app.views.parameters.refresh(paramsToBeFilled);
								app.views.viewport.add(app.views.parameters);
								app.views.viewport.setActiveItem(app.views.parameters);

							}
						}else{
							var parameters = this.onParametersForExecutionLoaded(executionInstance,responseJson);
							app.views.parameters.refresh(parameters);
							app.views.viewport.setActiveItem(app.views.parameters);
						}
					}
				}
			}
			,failure: function(response, options) {
				Sbi.exception.ExceptionHandler.handleFailure(response, options);
			}
		}); 
	}
	
	, fromArrayToObject: function(jsonArray){
		var params={};
		for(var i=0; i<jsonArray.length; i++){
			var obj = jsonArray[i];
			var name = obj.name;
			var value = obj.value;
			params[name]=value;
		}
		return params;
	}
	
	, fillParametersFromCross: function(parametersNeeded, parametersFromCross, paramsToBeFilled){
		var parametersFilled = {};
		if(parametersNeeded != null && parametersNeeded != undefined && 
				parametersFromCross != null && parametersFromCross != undefined	){
		
			for(i =0; i<parametersNeeded.length; i++){
				var p = parametersNeeded[i];
				var nm = p.name;
				for(k =0; k<parametersFromCross.length; k++){
					var pCross = parametersFromCross[k];
					if(nm == pCross.name && pCross.value != null && pCross.value != ''){
						parametersFilled[nm] = pCross.value;
						paramsToBeFilled.remove(p);
						p.value = pCross.value;
						break;
					}
				}
			}
		}

		return parametersFilled;
	}
	
	, onParametersForExecutionLoaded: function( executionInstance, parameters ) {
		executionInstance.PARAMETERS = parameters;
		this.executionInstance = executionInstance;
		this.fields = new Array();
	
		for(var i = 0; i < parameters.length; i++) {
			var field = this.createField( executionInstance, parameters[i] );
			this.fields.push(field);
		}
	
		return this.fields;
	
	}
	
	, createField: function( executionInstance, p, c ) {
		var field;
	
	
		var baseConfig = {
				label: p.label
				, name : p.id
				// , allowBlank: !p.mandatory
		};
	
		if(p.selectionType === 'COMBOBOX' || p.selectionType === 'LIST' || p.selectionType ===  'CHECK_LIST') {
	
			//get the metadata of the parameter 
			var metadata = p.metaData.metaData;
	
			var params = Ext.apply({}, {
				PARAMETER_ID: p.id
				, MODE: 'complete'
			}, executionInstance);
	
			var store = Ext.create("Ext.data.Store",{
				proxy: {
					type: 'ajax',
					url: this.services['getParameterValueForExecutionService'],
					extraParams: params,
					reader: {
						type: 'json',
						root: metadata.root
					}
				},
				fields: metadata.root,
				autoLoad : true,
				autoDestroy : true
			});
	
			store.on('beforeload', function(store, o) {
				var p = Ext.encode(this.getFormState());
				store.proxy.extraParams.PARAMETERS = p;
				//return true;
			}, this);
	
			var mandatory= false;
			if(p.mandatory && p.mandatory == true){
				mandatory = true;
			}
			field = Ext.create("Ext.field.Select",(Ext.apply({
				valueField : metadata.valueField,
				displayField : metadata.displayField,
				placeHolder: 'Selezionare un valore...',
		        useClearIcon: true,
		        required: mandatory,
				store : store
			},baseConfig)));

			field.on('focus', function(f) {
				f.setValue(' ');
			}, this);
		} else { 
			if(p.type === 'DATE' || p.type ==='DATE_DEFAULT') {		
				field = Ext.create("Ext.form.DatePicker",baseConfig);
	
			} else if(p.type === 'NUMBER') {
				field =  Ext.create("Ext.form.Number",baseConfig);
			} else {	
				field =  Ext.create("Ext.form.Text",baseConfig);
			}			
		}		
		return field;
	}
	
	, getFormState: function() {
		var state;
		state = {};
		for(var i=0; i<this.fields.length; i++) {
			
			try{
				var field = this.fields[i];
				state[field.getName() + '_field_visible_description'] = '';
				var value = field.getValue();
				if(value==undefined || value==null){
					state[field.getName()] = '';
				}else{
					state[field.getName()] = value;
				}
			}catch (e){
				state[field.getName()] = '';
			}
	
		}
		return state;
	}
	
	, setFormState: function(state) {
		var state;
		state = {};
		if(this.fields!=undefined && this.fields!=null){
			for(var i=0; i<this.fields.length; i++) {
				
				var field = this.fields[i];
				var newValue = state.fieldName;
				if(newValue!=undefined && newValue!=null){
					field.setValue(newValue);
				}
			}
		}
	}

});
