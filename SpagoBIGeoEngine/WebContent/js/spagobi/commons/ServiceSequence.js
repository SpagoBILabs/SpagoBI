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