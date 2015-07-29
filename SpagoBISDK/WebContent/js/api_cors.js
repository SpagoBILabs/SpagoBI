/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Sbi.sdk.namespace('Sbi.sdk.cors.api');

Sbi.sdk.apply(Sbi.sdk.cors.api, {
	
	elId: 0
	, dataSetList: {}
	
	, authenticate: function ( config ) {
		var serviceUrl = Sbi.sdk.services.getServiceUrl('authenticate', config.params);
		Sbi.sdk.cors.asyncRequest({
			method: 'POST',
			url: serviceUrl,
			headers: config.headers,
			callbackOk: config.callbackOk,
			callbackError: config.callbackError,
			body: config.credentials
		});
	}
	
	, getDataSetList: function ( config ) {
		var baseUrl = Sbi.sdk.services.baseUrl;
		var serviceUrl = baseUrl.protocol + '://' + baseUrl.host + ":" + baseUrl.port + '/' + baseUrl.contextPath + '/restful-services/2.0/datasets';
		
		var headers = [];
		if (config.basicAuthentication !== undefined) {
			var ba = config.basicAuthentication;
			var encoded = btoa(ba.userName + ':' + ba.password);
			
			headers[0] = {
				name: 'Authorization',
				value: 'Basic ' + encoded
			} 
		}
		
		Sbi.sdk.cors.asyncRequest({
			method: 'GET',
			url: serviceUrl,
			headers: headers,
			callbackOk: config.callbackOk,
			callbackError: config.callbackError
		});
	}
	
	, executeDataSet: function ( config ) {
		var baseUrl = Sbi.sdk.services.baseUrl;
		var serviceUrl = baseUrl.protocol + '://' + baseUrl.host + ":" + baseUrl.port + '/' + baseUrl.contextPath + '/restful-services/2.0/datasets/';
		serviceUrl += config.datasetLabel + '/content';
		
		if (config.parameters !== undefined) {
			var first = true;
			
			for(var parameter in config.parameters) {
				if (first) {
					serviceUrl += '?';
					first = false;
				}
				else serviceUrl += '&';
				
				serviceUrl += parameter + '=' + config.parameters[parameter];
			}
		}
		
		var headers = [];
		if (config.basicAuthentication !== undefined) {
			var ba = config.basicAuthentication;
			var encoded = btoa(ba.userName + ':' + ba.password);
			
			headers[0] = {
				name: 'Authorization',
				value: 'Basic ' + encoded
			} 
		}
		
		Sbi.sdk.cors.asyncRequest({
			method: 'GET',
			url: serviceUrl,
			headers: headers,
			callbackOk: config.callbackOk,
			callbackError: config.callbackError
		});
	}
});