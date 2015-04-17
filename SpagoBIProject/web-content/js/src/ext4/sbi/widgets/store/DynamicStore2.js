/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * MANDATORY PARAMETERS: serviceUrl: the url for the ajax request
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
  * Alberto Ghedin alberto.ghedin@eng.it
  * 
  * - name (mail)
  */

Ext.define('Sbi.widgets.store.DynamicStore2', {
    extend: 'Ext.data.Store'

    ,config: {
    	isDynamicStore:false
    }
      
    /**
     * Creates the store.
     * @param {Object} config (optional) Config object
     */
    , constructor: function(config) {
//    	Sbi.debug('constructor IN'+config);
    	
    	console.log("[IN] constructor() DynamicStore");
    	
    	Ext.apply(this, config);
    	
    	if (this.isDynamicStore){
	    	
	    	var serviceUrl = config.serviceUrl;
	    	
	    	if(!this.model){
	    		Sbi.debug('create new model');	
	    		var d = new Date();
	    		var modelname =  'DynamicStoreModel'+(d.getTime()%10000000);
	            Ext.define(modelname, {
	                extend: 'Ext.data.Model'
	            });
	            
	    		this.model= modelname;	// This is the model of this store (the store field "model")
	    		
	    	}    	
	
			var dataRoot = "root";
			
			if(config.dataRoot){
				dataRoot=config.dataRoot;
			}
						
	    	if ((config.usePost != null) && (config.usePost != undefined ) ){
    		
	    		this.proxy= {
	    				type: 'ajax',
	    				url:  this.serviceUrl,
	    				actionMethods:{ create:'POST', read:'POST', update:'POST', destroy:'POST' },
	    				reader: {
	    					type:"json",
	    					root: dataRoot
	    				}
	    		};
	      	} else {
	      		
	      		this.proxy= 
	      		{
	      				type: 'ajax',
	      				url:  this.serviceUrl,
	      				extraParams: { name: config.lovProvider },
	      		        writer: "json",
	      				reader: "json"
	      		};	      		
      		
//	      		console.log(this.proxy);
	      	}
	
	    	this.callParent([config]); // This will create the store of the defined model (up)
	        
	    	this.fields = this.model.prototype.fields;
	    	
    	}else{
    		this.callParent(arguments);
    	}
    	
//    	Sbi.debug('constructor OUT');
//		this.on('load', this.onStoreLoad, this);
    	
    	console.log("[OUT] constructor() DynamicStore");
    }


	, onStoreLoad: function() {		
		Sbi.debug('onStoreLoad IN');		
		try{			
			var metadata = this.proxy.reader.jsonData.metaData;
			if(metadata.error && metadata.error=='error'){
				Sbi.exception.ExceptionHandler.showErrorMessage(metadata.stacktrace,LN('sbi.behavioural.lov.test.error'));
				return false;
			}else{
				var columns = metadata.fields;
				Sbi.debug('columns length ' + columns.length);
			}
		}catch(e){
			Sbi.exception.ExceptionHandler.showErrorMessage(e,LN('sbi.behavioural.lov.test.error'));
			return false;
		}
		Sbi.debug('onStoreLoad OUT');
	
		}
	
	, getColumns: function(){
		Sbi.debug('store.getColumns');
		
		if(this.proxy.reader.jsonData.metaData != undefined && this.proxy.reader.jsonData.metaData != null)
		{			
			return this.proxy.reader.jsonData.metaData.fields;
		}
		else
		{
			var question = confirm("Entered query has a wrong syntax. Return on the main page");
			
			if (question == true)
			{
				this.fireEvent('wrongSyntax',"wrong");
			}
		}		
	}
	, getValidationErrors : function(){
		//Sbi.debug('store.getValidationErrors');
		if ((this.proxy.reader.jsonData.validationErrors != null) && (this.proxy.reader.jsonData.validationErrors != undefined))
		{ 
			return this.proxy.reader.jsonData.validationErrors;
		}
		return null;	
	}
	
	
});