/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * ServiceRegistry - short description
  * 
  * Object documentation ...
  * 
  * by Andrea Gioia (andrea.gioia@eng.it)
  */

Sbi.commons.ServiceRegistry = function(config) {
	
	this.baseUrl = {
		protocol: 'http',       
		host: 'localhost',
        port: '8080',
        contextPath: 'SpagoBI',
        controllerPath: 'servlet/AdapterHTTP',
        execId: -1 
    };
	
	Ext.apply(this, config);
	
	//this.addEvents();	
	
	// constructor
    Sbi.commons.ServiceRegistry.superclass.constructor.call(this);
};

Ext.extend(Sbi.commons.ServiceRegistry, Ext.util.Observable, {
    
    // static contens and methods definitions
   
   
    // public methods
    
    setBaseUrl : function(url) {
       Ext.apply(this.baseUrl, url); 
    }
        
    , getServiceUrl : function(actionName, absolute){
    	var baseUrlStr;
        var serviceUrl;
        	
        if(absolute === undefined || absolute === false) {
        	baseUrlStr = 'AdapterHTTP';
        } else {
        	baseUrlStr = this.baseUrl.protocol + "://" + this.baseUrl.host + ":" + this.baseUrl.port + "/" + this.baseUrl.contextPath + "/" + this.baseUrl.controllerPath;
        }
        
        serviceUrl = baseUrlStr + "?ACTION_NAME=" + actionName + "&SBI_EXECUTION_ID=" + this.baseUrl.execId;
        
        return serviceUrl;
    }     
});