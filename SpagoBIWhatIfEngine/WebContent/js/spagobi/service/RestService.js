/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Repository of all the services
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.service.RestService', {
	extend: 'Object',

	config: {
		serviceVersion: '1.0',
		url: null,
		subPath: null,
		method: "GET",
		pathParams: null,
		baseParams:[],
		jsonData: null
	},

	constructor : function(config) {
		this.initConfig(config);
		this.callParent();
	},

	getRestUrlWithParameters: function(){
		var url = this.url;

		if( this.serviceVersion){
			url = this.serviceVersion+"/"+url;
		}

		var params = new Array();
		if(this.subPath!=null && this.subPath!=undefined){
			if(!this.subPath instanceof Array){
				params.push(this.subPath);
			}else{
				params = params.concat(this.subPath);
			}
		}

		if(this.pathParams){
			params = params.concat(this.pathParams);
		}
		


		if(params && url){
			for(var i=0; i<params.length; i++){
				var p = params[i];
				if(p!=null && p!=undefined){
					url = url+"/"+p;
				}else{
					url = url+"/null";
				}
			}
		}
		return url;
	},


	callService:	function( url, subpath, method, pathparams, baseParams ){
		var completeUrl = Sbi.service.Service.serviceVersion+"/"+Sbi.service.Service.addRestSubPathAndParameters(url, subpath, pathparams);
		return Sbi.config.serviceRegistry.getRestServiceUrl({
			serviceName:  completeUrl
			, baseParams: baseParams
		});
	},


	callService:function(scope, successCallBack, failureCallBack){

		var mySuccessCallBack= successCallBack;
		var myFailureCallBack= failureCallBack;
		Sbi.olap.eventManager.fireEvent('executeService');
		
		if(!mySuccessCallBack && scope){
			mySuccessCallBack = function(response, options) {
				if(response !== undefined && response.statusText !== undefined && response.responseText!=null && response.responseText!=undefined) {
					Sbi.olap.eventManager.fireEvent('serviceExecuted', response);
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
				}
			};
		};

		if(!myFailureCallBack && scope){
			myFailureCallBack = Sbi.exception.ExceptionHandler.handleFailure;
		};

		var ajaxConf = {
				url: this.getRestUrlWithParameters(),
				method: this.method,
				success : mySuccessCallBack,
				scope: scope,
				failure: myFailureCallBack
		};

		if(this.jsonData){
			ajaxConf.jsonData = this.jsonData;
		}

		Ext.Ajax.request(ajaxConf);
	}


});

