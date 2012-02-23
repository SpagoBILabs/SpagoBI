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
		var oldParameters = executionInstance.PARAMETERS;
		if (app.controllers.composedExecutionController.parametersHaveBeenChanged(oldParameters, newParameters)) {
			console.log('app.controllers.ComposedExecutionController:refreshSubDocument: parameters have been changed');
			
			var newDocumentParameters = app.controllers.composedExecutionController.applyNewParameters(newParameters, oldParameters);
			
			app.controllers.executionController.executeTemplate({
				executionInstance : executionInstance
				, parameters : newDocumentParameters
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
						+ newParameterValue + '] with old one [' + oldParameterValue + '] for parameter [' + aParameterName + '] ...');
				if (oldParameterValue != newParameterValue) {
					return true;
				}
			}
		}
		return false;
	}
	
	,
	applyNewParameters : function (newParameters, oldParameters) {
		
		console.log('app.controllers.ComposedExecutionController:applyNewParameters: IN');
		
		var toReturn = {};
		
		for (var aParameterName in oldParameters) {
			var oldParameterValue = oldParameters[aParameterName];
			if (oldParameters.hasOwnProperty(aParameterName)) {
				if (newParameters[aParameterName] !== undefined && newParameters[aParameterName] !== null) {
					var newParameterValue = newParameters[aParameterName];
					console.log('app.controllers.ComposedExecutionController:applyNewParameters: replacing new value [' 
							+ newParameterValue + '] with old one [' + oldParameterValue + '] for parameter [' + aParameterName + ']');
					toReturn[aParameterName] = newParameterValue;
				} else {
					console.log('app.controllers.ComposedExecutionController:applyNewParameters: keeping old value [' 
							+ oldParameterValue + '] for parameter [' + aParameterName + ']');
					toReturn[aParameterName] = oldParameterValue;
				}
			}
		}
		
		return toReturn;
	}
	
});