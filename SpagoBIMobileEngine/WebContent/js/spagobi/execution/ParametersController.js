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
	
		Ext.Ajax.request({
			url: this.services['getParametersForExecutionAction'],
			scope: this,
			method: 'post',
			params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName, SBI_EXECUTION_ID: sbiExecutionId},
			success: function(response, opts) {
				if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
					var responseJson = Ext.decode(response.responseText);
					
					if(responseJson==undefined || responseJson==null || responseJson.length==0  ){
						  Ext.dispatch({
							  controller: app.controllers.mobileController,
							  action: 'executeTemplate',
							  id: id,
							  label: label,
							  roleName : roleName, 
							  sbiExecutionId : sbiExecutionId
						  });
					}else{
						var executionInstance = {
								OBJECT_ID: id, 
								OBJECT_LABEL: label, 
								isFromCross:false, 
								ROLE:roleName, 
								SBI_EXECUTION_ID: sbiExecutionId	
						};
						var parameters = this.onParametersForExecutionLoaded(executionInstance,responseJson);
						app.views.parameters.refresh(parameters);
						app.views.viewport.setActiveItem(app.views.parameters);
					}
				}
			}
		}); 
	}
	
	
	, onParametersForExecutionLoaded: function( executionInstance, parameters ) {
	
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
			state[field.name + '_field_visible_description'] = '';
			try{
				var field = this.fields[i];
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

});
