/** 
 * @license
 * SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Sbi.sdk.namespace('Sbi.sdk.api');

Sbi.sdk.apply(Sbi.sdk.api, {
	authenticate:  function ( config ) {	    
		var serviceUrl = Sbi.sdk.services.getServiceUrl('authenticate', config.params);
		Sbi.sdk.jsonp.asyncRequest(serviceUrl, config.callback.fn, config.callback.scope, config.callback.args);
	}

	/**
	 * This callback is called a response is returned by the server.
	 * @callback ResponseCallback
	 * @param {String} json - json of response
	 * @param {Array} args
	 * @param {Boolean} success - true if it's a success response otherwise false 
	 */

	/**
	 * It returns the list of datasets
	 * @example
	 * execTest6 = function() {
	 *	    Sbi.sdk.api.getDataSetList({
	 *	    	callback: function( json, args, success ) {
	 *	    		if (success){
	 *	    			var str = "";
	 *	    			
	 *	    			for (var key in json){
	 *		    			str += "<tr><td>" + json[key].label + "</td><td>" + json[key].name + "</td><td>" + json[key].description + "</td></tr>";
	 *	    			}
	 *	    			
	 *	    			document.getElementById('datasets').innerHTML = str;
	 *	    		}
	 *			}});
	 *	};
	 * @method Sbi.sdk.api.getDataSetList
	 * @param {Object} config - the configuration
	 * @param {ResponseCallback} config.callback - function to be called after the response is returned by the server
	 */
	, getDataSetList: function( config ) {
		
		Sbi.sdk.jsonp.timeout = 10000;
		
		var baseUrl = Sbi.sdk.services.baseUrl;
		var serviceUrl = baseUrl.protocol + '://' + baseUrl.host + ":" + baseUrl.port + '/' + baseUrl.contextPath + '/restful-services/2.0/datasets';
		
		Sbi.sdk.jsonp.asyncRequest(serviceUrl, config.callback, this);
	}
	
	/**
	 * It executes a dataset
	 * @example
	 * execTest7 = function() {
	 *    Sbi.sdk.api.executeDataSet({
	 *    	datasetLabel: 'DS_DEMO_EXTCHART'
	 *    	, parameters: {
	 *    		par_year: 2011,
	 *    		par_family: 'Food'
	 *    	}
	 *    	, callback: function( json, args, success ) {
	 *    		if (success){
	 *    			var str = "<th>Id</th>";
	 *    			
	 *    			var fields = json.metaData.fields;
	 *    			for(var fieldIndex in fields) {
	 *    				if (fields[fieldIndex].hasOwnProperty('header'))
	 *    					str += '<th>' + fields[fieldIndex]['header'] + '</th>';
	 *    			}
	 *    			
	 *    			str += '<tbody>';
	 *    			
	 *    			var rows = json.rows;
	 *    			for (var rowIndex in rows){
	 *    				str += '<tr>';
	 *    				for (var colIndex in rows[rowIndex]) {
	 *    					str += '<td>' + rows[rowIndex][colIndex] + '</td>';
	 *    				}
	 *    				str += '</tr>';
	 *    			}
	 *    			
	 *    			str += '</tbody>';
	 *    			
	 *    			document.getElementById('results').innerHTML = str;
	 *    		}
	 *		}});
	 * };
	 * @method Sbi.sdk.api.executeDataSet
	 * @param {Object} config - the configuration
	 * @param {String} config.documentLabel - the document label
	 * @param {Object} [config.parameters] - the values of dataset parameters
	 * @param {ResponseCallback} config.callback - function to be called after the response is returned by the server
	 */
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