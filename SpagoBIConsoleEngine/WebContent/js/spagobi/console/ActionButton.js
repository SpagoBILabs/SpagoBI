/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
  * Object name 
  * 
  * [description]
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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.ActionButton = function(config) {

		var defaultSettings = {
			iconCls: config.actionConf.name
			,tooltip: (config.actionConf.tooltip === undefined)?config.actionConf.name : config.actionConf.tooltip 
			,hidden: config.actionConf.hidden
			,scope:this
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.actionButton) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.actionButton);
		}
	
		var c = Ext.apply(defaultSettings, config || {});
	
		Ext.apply(this, c);

		//this.addEvents('customEvents');
	    this.initServices();
	  //  this.initButton();
	    
        c = Ext.apply(c, this);
      
      
	    // constructor
	    Sbi.console.ActionButton.superclass.constructor.call(this, c);
	    this.on('click', this.execAction, this);
	    this.store.on('load', this.initButton, this);
       //this.addEvents();
}; 

Ext.extend(Sbi.console.ActionButton, Ext.Button, {
    
    services: null
    , ACTIVE_VALUE: 1
	, INACTIVE_VALUE: 0
	, USER_ID: 'userId'
	, ERRORS: 'errors'
	, ALARMS: 'alarms'
	, MONITOR: 'monitor'
	, MONITOR_INACTIVE: 'monitor_inactive'
		
	, FILTERBAR_ACTIONS: {		
		  monitor: {serviceName: 'UPDATE_ACTION', images: 'monitor'}
		, monitor_inactive: {serviceName: 'UPDATE_ACTION', images: 'monitor_inactive'}
		, errors: {serviceName: 'UPDATE_ACTION', images: {active: 'errors', inactive: 'errors_inactive'}} 
		, alarms: {serviceName: 'UPDATE_ACTION', images: {active: 'alarms', inactive: 'alarms_inactive'}}
		, views: {serviceName: 'UPDATE_ACTION', images: {active: 'views', inactive: 'views_inactive'}}
		, refresh: {serviceName: 'REFRESH_ACTION', images: 'refresh'}
	}
   
    // public methods

	//This method search the dynamic parameter value before in the request, if it isn't found it search into filter.
	//If it isn't found again it shows a message error.
	, resolveParameters: function(parameters, context) {
		var results = {};  

		results = Ext.apply(results, parameters.staticParams);
		
		var dynamicParams = parameters.dynamicParams;
	    if(dynamicParams) {        	
	    	var msgErr = ""; 
	      	for (var i = 0, l = dynamicParams.length; i < l; i++) {      		     
	      		var param = dynamicParams[i]; 
	        	for(p in param) { 
	        		if(p === 'scope') continue;
	        			//Searchs into request
	        			if (param.scope === 'env'){ 
			            	if (p !== this.USER_ID && context[p] === undefined ) {
			            		//search into filters
			            		var paramValue = this.store.filterPlugin.getFilter(this.store.getFieldNameByAlias(p)); 
			            		if (paramValue !== undefined){
			            			//results[param[p]] = paramValue;
			            			results[p] = paramValue;
			            		}
			            		else{
			            		//	msgErr += 'Parameter "' + p + '" undefined into request or filter. <p>';
			            		}
		                    } else {          	 	 		           	 	 		  
		                    	//results[param[p]] = context[p];
		                    	results[p] = context[p];
		                    } 	 		 
	                }          	 	 		   
	          		    
	        	 }          			   
	      	} 
	      	
	      	var metaParams = parameters.metaParams;
		    if(metaParams) {  
		    	results['metaParams'] = Ext.util.JSON.encode(metaParams);
		    }
	      	
	        if  (msgErr != ""){
	        	Sbi.Msg.showError(msgErr, 'Service Error');
	        }		  
	    }
	    
	    return results;
	}
	

    , execAction: function(){
    	
    	var flgCheck = null;
    	var checkCol = null;
    	
    	checkCol = this.actionConf.checkColumn;
    	
    	if (this.actionConf.name === 'monitor' || this.actionConf.name === 'monitor_inactive'){     		
    		this.store.filterPlugin.removeFilter(this.store.getFieldNameByAlias(this.actionConf.checkColumn));
    		var newFilter = new Array();
    		newFilter.push((this.actionConf.name === 'monitor') ? this.ACTIVE_VALUE : this.INACTIVE_VALUE);    	
    		this.store.filterPlugin.addFilter(this.store.getFieldNameByAlias(this.actionConf.checkColumn), newFilter);    		
    		this.store.filterPlugin.applyFilters();	   
    		return;
    	}else if (this.actionConf.name === 'refresh'){    	
    		if(this.store.pagingParams && this.store.pagingParams.paginator) {
    			if(this.store.lastParams) {
    				delete this.store.lastParams;
    			}
    			var paginator = this.store.pagingParams.paginator;
    			paginator.doLoad(paginator.cursor); 
    		} else {
    			this.store.loadStore();    
    		}
    				
    		return;
    	} else if (this.actionConf.name === 'errors' || this.actionConf.name === 'errors_inactive'){  
    		flgCheck = (this.iconCls === 'errors')? this.ACTIVE_VALUE: this.INACTIVE_VALUE;    		    	
    	} else if (this.actionConf.name === 'alarms' || this.actionConf.name === 'alarms_inactive'){      		
    		flgCheck = (this.iconCls === 'alarms')? this.ACTIVE_VALUE: this.INACTIVE_VALUE;    		
    	} else if (this.actionConf.name === 'views' || this.actionConf.name === 'views_inactive'){      		
    		flgCheck = (this.iconCls === 'views')? this.ACTIVE_VALUE: this.INACTIVE_VALUE;    		
    	}
    	
    	//if in configuration is set that the action is usable only once, it doesn't change the check if it's yet checked
        if(flgCheck != null  && flgCheck === this.INACTIVE_VALUE &&
        		this.actionConf.singleExecution !== undefined && this.actionConf.singleExecution == true) return;            	
    	
    	this.executionContext[checkCol] = flgCheck;
		var params = this.resolveParameters(this.actionConf.config, this.executionContext);
		params = Ext.apply(params, {
				message: this.actionConf.name, 
				userId: Sbi.user.userId 
			}); 
				
		Ext.Ajax.request({
		url: this.services[this.actionConf.name]	       
       	, params: params 			       
    	, success: function(response, options) {
    		if(response !== undefined && response.responseText !== undefined) {
					var content = Ext.util.JSON.decode( response.responseText );
					if (content !== undefined) {				      			  
					//	alert(content.toSource());
					}				      		
			} else {
				Sbi.Msg.showError('Server response is empty', 'Service Error');
			}
    	}
    	, failure: Sbi.exception.ExceptionHandler.onServiceRequestFailure
    	, scope: this     
	    });  
			
		//updates the row's icons
		this.setCheckValue(this.actionConf.checkColumn, flgCheck);        

	}
 

    // private methods
    , initServices: function() {
    	this.services = this.services || new Array();	
		this.images = this.images || new Array();	
				
		for(var actionName in this.FILTERBAR_ACTIONS) {
			var actionConfig = this.FILTERBAR_ACTIONS[actionName];
			this.services[actionName] = this.services[actionName] || Sbi.config.serviceRegistry.getServiceUrl({
				serviceName: actionConfig.serviceName
				, baseParams: new Object()
			});
		}
    }
    
    , initButton: function(){    	
    	//icons about monitoring are ever enabled
    	if (this.actionConf.name === this.MONITOR || this.actionConf.name === this.MONITOR_INACTIVE){
    		return;
    	}
    	
    	//checks if the button is visible (when the action is errors or alarms)
    	if (this.actionConf.name === this.ERROR || this.actionConf.name === this.ALARMS){
    		var flagCol = this.store.getFieldNameByAlias(this.actionConf.flagColumn);    
        	if (flagCol === undefined ){
        		return;
        	}else{
        		var flagValue = this.store.findExact(flagCol, this.ACTIVE_VALUE);
        		if (flagValue === -1) {
        			return;
        		}
        	}    		
    	}

    	var isCheck = this.store.getFieldNameByAlias(this.actionConf.checkColumn);
    	if (isCheck !== undefined ){
    		//checkValue: -1 if all rows are ACTIVE, greater then -1 when ther's almost one active 
    		var checkValue = this.store.findExact(isCheck,this.INACTIVE_VALUE);
    		if (checkValue > -1){  //there's any inactive --> enable active actions
    			this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.name ].images[ "active"]); 
    			this.setTooltip(this.actionConf.tooltipInactive);
    		}else{      		    		
    			this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.name ].images["inactive"]); 
	    		this.setTooltip(this.actionConf.tooltipActive);    			
    	    }
    	}	
    }
  
    //updates checkColumn value in each store's row
    , setCheckValue: function(columnAlias, value){
    	for (var i=0, l= this.store.getCount(); i < l; i++){
            var record = this.store.getAt(i);  
            record.set (this.store.getFieldNameByAlias(columnAlias), value );
        } 
    	if (value === this.ACTIVE_VALUE){
    		this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.name ].images[ "inactive"]); 
    	}else{
    		this.setIconClass(this.FILTERBAR_ACTIONS[ this.actionConf.name ].images[ "active"]); 
    	}
    }
    
});
    
