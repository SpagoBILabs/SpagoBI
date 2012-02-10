app.controllers.ExecutionController = Ext.extend(Ext.Controller,{
	
	init: function()  {
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		
		this.services = new Array();
		this.services['executeMobileTableAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_TABLE_ACTION'
			, baseParams: params
		});
	}

	
	, executeTemplate: function(option){
		var id = option.id;
		var label = option.label;
		var roleName = option.roleName;
		var sbiExecutionId = option.sbiExecutionId;
		
		Ext.Ajax.request({
	        url: this.services['executeMobileTableAction'],
	        scope: this,
	        method: 'post',
	        params: {OBJECT_ID: id, OBJECT_LABEL: label, isFromCross:false, ROLE:roleName, SBI_EXECUTION_ID: sbiExecutionId},
	        success: function(response, opts) {
	        	if(response!=undefined && response!=null && response.responseText!=undefined && response.responseText!=null){
	        		var resp = Ext.decode(response.responseText);
	        		this.createTableExecution(resp);
	        	}
	        }
	    }); 
	}
	
	, createTableExecution: function(resp){
		
		//these are settings for table object
		app.views.execView = new app.views.ExecutionView(resp);
	    //adds execution view directly to viewport
	    var viewport = app.views.viewport;
	    viewport.add(app.views.execView);
	    app.views.execView.setWidget(resp, 'table');
	    viewport.setActiveItem(app.views.execView, { type: 'slide', direction: 'left' });
	}


});