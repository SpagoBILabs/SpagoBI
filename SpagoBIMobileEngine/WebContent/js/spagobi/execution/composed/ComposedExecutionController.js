app.controllers.ComposedExecutionController = Ext.extend(Ext.Controller,{
	
	executeSubDocument : function(executionInstance, subDocumentPanel){
		var option = {};
		option.executionInstance = executionInstance;
		option.parameters = executionInstance.parameters;	
		app.controllers.executionController.executeTemplate(option,subDocumentPanel);
	}
});