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

	
	, getParametersForExecutionAction: function(option, refresh){
	
		var id = option.id;
		var label = option.label;
		var roleName = option.roleName;
		var sbiExecutionId = option.sbiExecutionId;
		var typeCode = option.typeCode;
		var engine = option.engine;
		//cross navigation settings
		var isFromCross = option.isFromCross;
		var paramsFromCross = option.params;//filled only from cross navigation
		this.isRefresh=refresh;
		Ext.Ajax.request({
			url: this.services['getParametersForExecutionAction'],
			scope: this,
			method: 'post',
			params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName, SBI_EXECUTION_ID: sbiExecutionId},
			success: function(response, opts) {
				if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
					var responseJson = Ext.decode(response.responseText);
					
					var executionInstance = {
							OBJECT_NAME: option.docName,
							OBJECT_ID: id, 
							OBJECT_LABEL: label, 
							isFromCross: isFromCross, 
							ROLE:roleName, 
							SBI_EXECUTION_ID: sbiExecutionId,
							ENGINE: engine, 
							TYPE_CODE: typeCode
					};
					
					if(responseJson==undefined || responseJson==null || responseJson.length==0  ){
						executionInstance.noParametersPageNeeded=true;
						app.controllers.executionController.executeTemplate({executionInstance: executionInstance},null,refresh);
					}else{
						var parameters = this.onParametersForExecutionLoaded(executionInstance,responseJson);
						var defaultValues = this.fillParametersWithDefault(parameters);
						if(isFromCross){
							//app.controllers.mobileController.destroyExecutionView();
							var paramsToBeFilled = parameters.slice(0);
							var paramsFilled= this.fillParametersFromCross(parameters, paramsFromCross, paramsToBeFilled);
							
							if((paramsToBeFilled.length-defaultValues)==0){
								//execute now!
								executionInstance.PARAMETERS = this.fromArrayToObject(paramsFromCross, defaultValues);
								executionInstance.isFromCross = true;
								executionInstance.noParametersPageNeeded=true;
								app.controllers.executionController.executeTemplate({executionInstance: executionInstance},null,refresh);
							}else{
//								app.views.parameters = Ext.create("app.views.ParametersView");
								this.executionInstance.noParametersPageNeeded=false;
								this.executionInstance.paramsFromCross=paramsFromCross;
								app.views.parameters.refresh(paramsToBeFilled);
//								app.views.viewport.add(app.views.parameters);
								app.views.viewport.goParameters();

							}
						}else{
							if((defaultValues.length)==parameters.length){
								//execute now!
								executionInstance.PARAMETERS = this.fromArrayToObject(null, defaultValues);
								executionInstance.noParametersPageNeeded=true;
								app.controllers.executionController.executeTemplate({executionInstance: executionInstance},null,this.isRefresh);
							}else{
								this.executionInstance.noParametersPageNeeded=false;
								app.views.parameters.refresh(parameters);
								app.views.viewport.goParameters();
							}
						}
					}
				}
			}
			,failure: function(response, options) {
				Sbi.exception.ExceptionHandler.handleFailure(response, options);
			}
		}); 
	}
	
	, fromArrayToObject: function(jsonArray, defaultValues){
		var params={};
		if(jsonArray){
			for(var i=0; i<jsonArray.length; i++){
				var obj = jsonArray[i];
				var name = obj.name;
				var value = obj.value;
				if(!value){
					value = obj.paramValue;
				}
				if(!name){
					name = obj.paramName;
				}
				params[name]=value;
			}
		}
		if(defaultValues){
			for(var i=0; i<defaultValues.length; i++){
				var obj = defaultValues[i];
				var name = obj.name;
				var value = obj.value;
				params[name]=value;
			}
		}

		return params;
	}
	
	, fillParametersWithDefault: function(parametersNeeded){
		var parametersFilled = new Array();
		if(parametersNeeded != null && parametersNeeded != undefined){
		
			for(var i =parametersNeeded.length-1; i>=0; i--){
				var p = parametersNeeded[i];
				var nm = p.getName();
				var value = p.getValue();
				if((!value || value=="") && p.config){
					value = p.config.value;
				}
				if(value && value!=""){
					parametersFilled.push({name:nm, value:value});
				}
			}
		}
		return parametersFilled;
	}
	
	, fillParametersFromCross: function(parametersNeeded, parametersFromCross, paramsToBeFilled){
		var parametersFilled = new Array();
		if(parametersNeeded != null && parametersNeeded != undefined && 
				parametersFromCross != null && parametersFromCross != undefined	){
		
			for(var i =parametersNeeded.length-1; i>=0; i--){
				var p = parametersNeeded[i];
				var nm = p.getName();
				var found =false;
				for(var k =0; k<parametersFromCross.length; k++){
					var pCross = parametersFromCross[k];
					var pCrossValue = pCross.value;
					if(!pCrossValue){
						pCrossValue = pCross.paramValue;
					}
					var pCrossName = pCross.name;
					if(!pCrossName){
						pCrossName = pCross.paramName;
					}
					if(nm == pCrossName && pCrossValue != null && pCrossValue != ''){
						found = true;
						p.setValue(pCrossValue);
						paramsToBeFilled.splice(i,1);
						break;
					}
				}
				if(found){
					parametersFilled.push(p);
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
		var defaultValue= null;
		if(p.defaultValues && p.defaultValues.length != 0){
			defaultValue = p.defaultValues[0].value;
			baseConfig.value = defaultValue;
		}
		if(p.selectionType === 'COMBOBOX' || p.selectionType === 'LIST' || p.selectionType ===  'CHECK_LIST' || p.selectionType === 'LOOKUP') {
	
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
				fields: metadata.fields,
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
				field = Ext.create("Ext.field.DatePicker",baseConfig);
	
			} else if(p.type === 'NUMBER') {
				field =  Ext.create("Ext.field.Number",baseConfig);
			} else {	
				field =  Ext.create("Ext.field.Text",baseConfig);
				
			}			
		}	
		field.setValue(defaultValue);
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
