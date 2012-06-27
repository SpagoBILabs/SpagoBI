/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
/**
  * ServiceSequence 
  * 
  * Concat multiple ajax requests. This class have a very simple implementation. 
  * Lot of improvements are needed :-(
  * 
  * 
  * Public Properties
  * 
  * - serviceSequence: an array containing ajax request configuration objects
  * 				   (see Ext.Ajax.request doc). This property is optional.
  * 					If not specified an empy sequence will be created.
  *
  *
  * Public Methods
  * 
  * - add: append a new ajax request configuration object (see Ext.Ajax.request doc) 
  * 	   to the sequence list.
  * 
  * - run: execute the service sequnce with a FIFO policy.
  * 
  * 
  * Public Events
  * 
  *  none 
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Sbi.commons.ServiceSequence = function(config) {
	
	this.serviceSequence = [];
	this.serviceStack = [];
	this.onSequenceExecuted = function(responce){allert(responce.toSource())};
	this.onSequenceExecutedScope = undefined;
	
	Ext.apply(this, config);
	
	this.addEvents();	
	
	// constructor
    Sbi.commons.ServiceSequence.superclass.constructor.call(this);
};

Ext.extend(Sbi.commons.ServiceSequence, Ext.util.Observable, {
    
    // static contens and methods definitions
   
   
    // public methods
    add : function(serviceConfig) {
    	this.serviceSequence.push( serviceConfig );
    }
    
    , run : function() {
    	this.serviceStack = [];
    	for(i = 0; i < this.serviceSequence.length; i++) {
    		this.serviceStack.push( this.serviceSequence[i] );
    	}
    	
    	this.serviceStack.reverse();
    	this.runNext();
    }
    
    , runNext : function(serviceResponse, serviceConfig) {
    	
    	//if(serviceResponse !== undefined) alert('serviceResponse: ' + serviceResponse.toSource());
    	//if(serviceConfig !== undefined) alert('serviceConfig: ' + serviceConfig.toSource());
    	
    	if( this.serviceStack && this.serviceStack.length > 0) {    	
	    	var nextServiceConfig = this.serviceStack.pop();
	    	
	    	if(typeof nextServiceConfig.params == "function"){
                nextServiceConfig.params = nextServiceConfig.params.call(nextServiceConfig.scope||window, nextServiceConfig);
            }
	    	
	    	nextServiceConfig.scope = this;
	    	nextServiceConfig.success = this.runNext;	    	
	    	Ext.Ajax.request( nextServiceConfig );  
	    	
    	} else {   
    		Sbi.commons.log('sequence ended');
    		this.onSequenceExecuted.call(this.onSequenceExecutedScope||window, serviceResponse);
    	}
    }
});