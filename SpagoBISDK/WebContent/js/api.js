/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 Sbi.sdk.namespace('Sbi.sdk.api');

Sbi.sdk.apply(Sbi.sdk.api, {
	
	elId: 0
	, dataSetList: {}
	
	/*	
	config = { 
		params: {
			user: 'biuser'
			, password: 'biuser'
		}
		
		, callback: {
			fn: doThis
			, scope: this
			, args: {arg1: 'A', arg2: 'B', ...}
		}
	}
	*/
	
	, authenticate:  function ( config ) {	    
		var serviceUrl = Sbi.sdk.services.getServiceUrl('authenticate', config.params);
		Sbi.sdk.jsonp.asyncRequest(serviceUrl, config.callback.fn, config.callback.scope, config.callback.args);
    }
	
	, getIFrameHtml: function( serviceUrl, config ) {
		
		var html;
		config.iframe = config.iframe || {};
		
		if(config.iframe.id === undefined) {
			config.iframe.id = 'sbi-docexec-iframe-' + this.elId;
			this.elId = this.elId +1;
		}
		
		html = '';
		html += '<iframe';
		html += ' id = "' + config.iframe.id + '" ';
		html += ' src = "' + serviceUrl + '" ';
		if(config.iframe.style !== undefined) html += ' style = "' + config.iframe.style + '" ';
		if(config.iframe.width !== undefined) html += ' width = "' + config.iframe.width + '" ';
		if(config.iframe.height !== undefined) html += ' height = "' + config.iframe.height + '" ';
		html += '></iframe>';
		
		return html;
	}
	
	, injectIFrame: function( serviceUrl, config ) {
		
		var targetEl = config.target || document.body;
		
		if(typeof targetEl === 'string') {
			var elId = targetEl;
			targetEl = document.getElementById(targetEl);
			
			if(targetEl === null) {
				targetEl = document.createElement('div');
				targetEl.setAttribute('id', elId);
				if(config.width !== undefined) {
					targetEl.setAttribute('width', config.width);
				}				
				if(config.height !== undefined) {
					targetEl.setAttribute('height', config.height);
				}
				document.body.appendChild( targetEl );
			} 
		}
		
		config.iframe = config.iframe || {};
		config.iframe.width = targetEl.getAttribute('width');
		config.iframe.height = targetEl.getAttribute('height');
		
		
		targetEl.innerHTML = this.getIFrameHtml(serviceUrl, config);
	}

	, getDocumentUrl: function( config ) {
		var documentUrl = null;
		
		if(config.documentId === undefined && config.documentLabel === undefined) {
			alert('ERRORE: at least one beetween documentId and documentLabel attributes must be specifyed');
			return null;
		}
		
		var params = Sbi.sdk.apply({}, config.parameters || {});
		
		if(config.documentId !== undefined) params.OBJECT_ID = config.documentId;
		if(config.documentLabel !== undefined) params.OBJECT_LABEL = config.documentLabel;
		
		if (config.executionRole !== undefined) params.ROLE = config.executionRole;
		if (config.displayToolbar !== undefined) params.TOOLBAR_VISIBLE = config.displayToolbar;
		if (config.theme !== undefined)	params.theme = config.theme;
		
		documentUrl = Sbi.sdk.services.getServiceUrl('execute', params);
		
		return documentUrl;
	}

	, getDocumentHtml: function( config ) {
		
		var serviceUrl = this.getDocumentUrl( config );
		return this.getIFrameHtml(serviceUrl, config);
	}
	
	, injectDocument: function( config ) {
		
		var serviceUrl = this.getDocumentUrl( config );
		return this.injectIFrame(serviceUrl, config);
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