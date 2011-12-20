/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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