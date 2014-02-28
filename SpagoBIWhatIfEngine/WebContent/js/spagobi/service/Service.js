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


Ext.define('Sbi.service.Service', {
	statics:{
		addRestPathParameters: function(url, params){
			if(params && url){
				for(var i=0; i<params.length; i++){
					var p = params[i];
					if(p){
						url = url+"/"+params[i];
					}else{
						url = url+"/null";
					}
					
				}
			}
			return url;
		},
		
		addRestSubPathAndParameters: function(url, subpath, pathparams){
			if(subpath){
				url = url+"/"+subpath;
			}
			return Sbi.service.Service.addRestPathParameters(url, pathparams);
		},
		
		callService:	function( url, subpath, pathparams, baseParams ){
			return Sbi.config.serviceRegistry.getRestServiceUrl({
				serviceName: Sbi.service.Service.addRestSubPathAndParameters(url, subpath, pathparams)
				, baseParams: baseParams
			});
		}
	}
});
