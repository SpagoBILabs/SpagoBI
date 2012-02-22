app.controllers.ComposedExecutionController = Ext.extend(Ext.Controller,{
	
	executeSubDocument : function(executionInstance, subDocumentPanel){
		var option = {};
		option.executionInstance = executionInstance;
		app.controllers.executionController.executeTemplate(option, subDocumentPanel);
	}

	,
	refreshSubDocument : function (panel, newParameters) {
		
		console.log('app.controllers.ComposedExecutionController:refreshSubDocument: IN');
		
		var executionInstance = panel.getExecutionInstance();
		var oldParameters = executionInstance.parameters;
		if (app.controllers.composedExecutionController.parametersHaveBeenChanged(oldParameters, newParameters)) {
			console.log('app.controllers.ComposedExecutionController:refreshSubDocument: parameters have been changed');
			app.controllers.executionController.executeTemplate({
				executionInstance : executionInstance
				, parameters : newParameters
			}, panel);
		}
	}
	
	,
	parametersHaveBeenChanged : function (oldParameters, newParameters) {
		
		console.log('app.controllers.ComposedExecutionController:parametersHaveBeenChanged: IN');
		
		for (var aParameterName in oldParameters) {
			var oldParameterValue = oldParameters[aParameterName];
			if (oldParameters.hasOwnProperty(aParameterName) && 
					newParameters[aParameterName] !== undefined && newParameters[aParameterName] !== null) {
				var newParameterValue = newParameters[aParameterName];
				console.log('app.controllers.ComposedExecutionController:refreshSubDocument: comparing new value [' 
						+ newParameterValue + '] with old one [' + oldParameterValue + '] ...');
				if (oldParameterValue != newParameterValue) {
					return true;
				}
			}
		}
		return false;
	}
	
});