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
		serviceVersion: 'v1.0',
		url: null,
		subPath: null,
		method: "GET",
		pathParams: null,
		baseParams:[]
	},
	
	constructor : function(config) {
		this.initConfig(config);
		this.callParent();
	},
	
	getRestUrlWithParameters: function(){
		var url = this.url;
		if(this.subPath){
			url = url+"/"+this.subPath;
		}
		if( this.serviceVersion){
			url = this.serviceVersion+"/"+url;
		}
		if(this.pathParams && url){
			for(var i=0; i<this.pathParams.length; i++){
				var p = this.pathParams[i];
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
	
	getUrl:	function(){
		return this.url;
	},
	
	callService:function(scope, successCallBack, failureCallBack){
		
		var mySuccessCallBack= successCallBack;
		var myFailureCallBack= failureCallBack;
		
		if(!mySuccessCallBack && scope){
			mySuccessCallBack = function(response, options) {
				if(response !== undefined && response.statusText !== undefined && response.responseText!=null && response.responseText!=undefined) {
					this.eventManager.updateAfterMDXExecution(response.responseText);
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
				}
			};
		};
		
		if(!myFailureCallBack && scope){
			myFailureCallBack = Sbi.exception.ExceptionHandler.handleFailure;
		};
		
		Ext.Ajax.request({
			url: this.getRestUrlWithParameters(),
			method: this.method,
			success : mySuccessCallBack,
			scope: scope,
			failure: myFailureCallBack
		});
	}
	

});

