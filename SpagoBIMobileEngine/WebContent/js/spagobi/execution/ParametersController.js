app.controllers.ParametersController = Ext.extend(Ext.Controller,{

	init: function()  {
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
						  Ext.dispatch({
							  controller: app.controllers.executionController,
							  action: 'executeTemplate',
							  executionInstance: executionInstance
						  });
					}else{
						if(isFromCross){

							var parameters = this.onParametersForExecutionLoaded(executionInstance,responseJson);
							app.controllers.mobileController.destroyExecutionView();
							var paramsToBeFilled = parameters.slice(0);
							var paramsFromCrossFilled= this.fillParametersFromCross(parameters, paramsFromCross, paramsToBeFilled);
							if(paramsToBeFilled.length == 0){
								//execute now!
								executionInstance.PARAMETERS = paramsFromCross;
								Ext.dispatch({
									  controller: app.controllers.executionController,
									  action: 'executeTemplate',
									  executionInstance: executionInstance
								});
							}else{
								app.views.parameters.refresh(paramsFromCrossFilled);
								app.views.crossExecView.setActiveItem(app.views.parameters);
							}
						}else{
							var parameters = this.onParametersForExecutionLoaded(executionInstance,responseJson);
							app.views.parameters.refresh(parameters);
							app.views.viewport.setActiveItem(app.views.parameters);
						}
					}
				}
			}
		}); 
	}
	, fillParametersFromCross: function(parametersNeeded, parametersFromCross, paramsToBeFilled){
		var parametersFilled = {};
		if(parametersNeeded != null && parametersNeeded != undefined && 
				parametersFromCross != null && parametersFromCross != undefined	){
		
			for(i =0; i<parametersNeeded.length; i++){
				var p = parametersNeeded[i];
				var label = p.label;
				for(k =0; k<parametersFromCross.length; k++){
					var pCross = parametersFromCross[k];
					if(label == pCross.name && pCross.value != null && pCross.value != ''){
						parametersFilled[label] = pCross.value;
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
	
			var store = new Ext.data.Store({
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
				return true;
			}, this);
	
			field = new Ext.form.Select(Ext.apply({
				valueField : metadata.valueField,
				displayField : metadata.displayField,
				store : store
			},baseConfig));
	
	
		} else { 
			if(p.type === 'DATE' || p.type ==='DATE_DEFAULT') {		
				field = new Ext.form.DatePicker(baseConfig);
	
			} else if(p.type === 'NUMBER') {
				field = new Ext.form.Number(baseConfig);
			} else {	
				field = new Ext.form.Text(baseConfig);
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
				state[field.name + '_field_visible_description'] = '';
				var value = field.getValue();
				if(value==undefined || value==null){
					state[field.name] = '';
				}else{
					state[field.name] = value;
				}
			}catch (e){
				state[field.name] = '';
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
