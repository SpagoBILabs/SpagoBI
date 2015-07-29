/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Sbi.sdk.namespace('Sbi.sdk.api');

/* 
 * Note that Sbi.sdk.api definition is defined in both api.js and api_jsonp.js.
 * In api_jsonp.js there are functions that uses jsonp to avoid the same-origin policy.
 * The same functions were also developed with CORS and they are defined in api_cors.js.
 * 
 * jsonp is deprecated, it is highly recommended to use CORS instead of it.
 * 
 * NB: CORS functions are inside Sbi.sdk.cors.api namespace and have same names as jsonp counterpart.
 * */
Sbi.sdk.apply(Sbi.sdk.api, {
	
	elId: 0
	, dataSetList: {}
	
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
			alert('ERROR: at least one beetween documentId and documentLabel attributes must be specified');
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
	
	, getAdHocReportingUrl: function( config ) {
		var url = null;
		
		if(config.datasetLabel === undefined) {
			alert('ERROR: datasetLabel attribute must be specified');
			return null;
		}
		
		var params = {};
		params.dataset_label = config.datasetLabel;
		params.TYPE_DOC = config.type;
		
		if (config.parameters !== undefined){
			for(var parameter in config.parameters)
				params[parameter] = config.parameters[parameter];
		}
		
		return Sbi.sdk.services.getServiceUrl('adHocReporting', params);
	}
	
	, getWorksheetUrl: function( config ) {
		config.type = 'WORKSHEET';
		return this.getAdHocReportingUrl(config);
	}
	
	, getWorksheetHtml: function( config ) {
		
		var serviceUrl = this.getWorksheetUrl( config );
		return this.getIFrameHtml(serviceUrl, config);
	}
	
	, injectWorksheet: function( config ) {
		
		var serviceUrl = this.getWorksheetUrl( config );
		return this.injectIFrame(serviceUrl, config);
	}
	
	, getQbeUrl: function( config ) {
		config.type = 'QBE';
		return this.getAdHocReportingUrl(config);
	}
	
	, getQbeHtml: function( config ) {
		
		var serviceUrl = this.getQbeUrl( config );
		return this.getIFrameHtml(serviceUrl, config);
	}
	
	, injectQbe: function( config ) {
		
		var serviceUrl = this.getQbeUrl( config );
		return this.injectIFrame(serviceUrl, config);
	}
	
});