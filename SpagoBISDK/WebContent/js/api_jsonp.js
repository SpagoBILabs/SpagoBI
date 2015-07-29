/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Sbi.sdk.namespace('Sbi.sdk.api');

/*
 * New api that gives the same functionalities of these functions were developed using CORS instead of jsonp.
 * There are three main advantages on using CORS over jsonp:
 *  1. all the methods are available while in jsonp only GET request can be done;
 *  2. if an error occurs it is possible to manage it with CORS, while in jsonp it is only possible to set a timeout;
 *  3. jsonp has security problems (see later for an example).
 * 
 * However, jsonp is supported by all browser while CORS doesn't work properly in Internet Explorer 8 and 9
 * (in IE 7 and earlier versions is not supported at all).
 * 
 * If you use the version with jsonp please take note about this security problem:
 * the authentication is made with a GET request, so users credentials are sent as query parameters.
 * It would be better to not use "authenticate" function (in that way, the user should already logged in
 * in order to use the api).
 * */
Sbi.sdk.apply(Sbi.sdk.api, {
	authenticate:  function ( config ) {	    
		var serviceUrl = Sbi.sdk.services.getServiceUrl('authenticate', config.params);
		Sbi.sdk.jsonp.asyncRequest(serviceUrl, config.callback.fn, config.callback.scope, config.callback.args);
	}

	, getDataSetList: function( config ) {
		
		Sbi.sdk.jsonp.timeout = 10000;
		
		var baseUrl = Sbi.sdk.services.baseUrl;
		var serviceUrl = baseUrl.protocol + '://' + baseUrl.host + ":" + baseUrl.port + '/' + baseUrl.contextPath + '/restful-services/2.0/datasets';
		
		Sbi.sdk.jsonp.asyncRequest(serviceUrl, config.callback, this);
	}
	
	, executeDataSet: function( config ) {
		
		Sbi.sdk.jsonp.timeout = 20000;
		
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
		
		Sbi.sdk.jsonp.asyncRequest(serviceUrl, config.callback, this);
	}
});