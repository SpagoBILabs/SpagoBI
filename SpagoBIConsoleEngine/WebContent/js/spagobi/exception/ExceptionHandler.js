/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
/**
  * Object name 
  * 
  * Singleton object that handle all errors generated on the client side
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */


Ext.ns("Sbi.exception.ExceptionHandler");

Sbi.exception.ExceptionHandler = function(){
	// do NOT access DOM from here; elements don't exist yet
 
    // private variables
 
    // public space
	return {
	
		init : function() {
			//alert("init");
		}
		
		
		, onServiceRequestFailure : function(response, options) {
        	var errMessage;
        	
        	
        	
        	if(response !== undefined) {        		
        		if(response.responseText !== undefined) {
        			var content = Ext.util.JSON.decode( response.responseText );
        			if(content.errors !== undefined && content.errors.length > 0) {
        				for(var i = 0; i < content.errors.length; i++) {
        					if(!content.errors[i].message) continue;
        					if(!errMessage) errMessage = '';
        					errMessage += content.errors[i].message + '<br>';
        				}
        			}
        			
        			if(content.message) {
        				errMessage = errMessage || content.message;
        			}
        			
        		} 
        		if(!errMessage)	errMessage = 'An unspecified error occurred on the server side';
        	} else {
        		errMessage = 'Request has been aborted due to a timeout trigger';
        	}
        		
        	errMessage = errMessage || 'An error occurred while processing the server error response';
        	
        	if (errMessage.indexOf('Warning') >= 0) {
        		Sbi.Msg.showWarning(errMessage, 'Service Warning');
        	}
        	else{
        		Sbi.Msg.showError(errMessage, 'Service Error');
        	}
       	
        }
		
        , onStoreLoadException : function(proxy, type, action, options, response, arg) {
			
			var errMessage = 'Generic error';
        
			if(type === 'response') {
				errMessage = 'An error occurred while parsing server response: ' + arg;
			} else if(type === 'remote') {
				errMessage = 'An error occurred at the server side';
			}
			Sbi.Msg.showError(errMessage, 'Store loading error');
			
			// to do ...
			// dump some more contextual infos (dataset name, options)
			// test timeout exception
			// when type = remote show more info on the error
        }
        


	};
}();