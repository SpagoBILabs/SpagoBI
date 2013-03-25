/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
Ext.define('app.controllers.ComposedExecutionController',{
	
	extend:'Ext.app.Controller',
	config:{},
	constructor: function(){},
	executeSubDocument : function(executionInstance, parentDocumentPanel){
		var option = {};
		option.executionInstance = executionInstance;
		app.controllers.executionController.executeTemplate(option, parentDocumentPanel);
	}

	,
	refreshSubDocument : function (panel, parentDocumentPanel, newParameters) {
		
		console.log('app.controllers.ComposedExecutionController:refreshSubDocument: IN');
		
		var executionInstance = panel.getExecutionInstance();
		var oldParameters = executionInstance.PARAMETERS;
		if (app.controllers.composedExecutionController.parametersHaveBeenChanged(oldParameters, newParameters)) {
			console.log('app.controllers.ComposedExecutionController:refreshSubDocument: parameters have been changed');
			
			var newDocumentParameters = app.controllers.composedExecutionController.applyNewParameters(newParameters, oldParameters);
			// TODO this is reduntant, or remove 'parameters' on app.controllers.executionController.executeTemplate call
			executionInstance.PARAMETERS = newDocumentParameters;
			
			app.controllers.executionController.executeTemplate({
				executionInstance : executionInstance
				, parameters : newDocumentParameters
			}, parentDocumentPanel);

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