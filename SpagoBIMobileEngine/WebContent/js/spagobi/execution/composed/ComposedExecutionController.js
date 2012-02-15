app.controllers.ComposedExecutionController = Ext.extend(Ext.Controller,{
	
	init: function()  {
		var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', SBI_EXECUTION_ID: null};
		
		this.services = new Array();
		this.services['executeMobileTableAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_TABLE_ACTION'
			, baseParams: params
		});
		
		this.services['executeMobileChartAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_CHART_ACTION'
			, baseParams: params
		});
		
		this.services['executeMobileComposedAction'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'EXECUTE_MOBILE_COMPOSED_ACTION'
			, baseParams: params
		});
		
		this.services['prepareDocumentForExecution'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'PREPARE_DOCUMENT_FOR_EXECUTION_ACTION'
			, baseParams: params
		});
	}

	, executeSubDocument : function(executionInstance, subDocumentPanel){
		var option = {};
		option.executionInstance = executionInstance;
		option.parameters = executionInstance;	
		app.controllers.ExecutionController.executeTemplate(option);
	}
});