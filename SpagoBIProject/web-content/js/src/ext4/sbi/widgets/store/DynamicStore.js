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

Ext.define('Sbi.widgets.store.DynamicStore', {
    extend: 'Ext.data.Store'

    ,config: {

    }
      
    /**
     * Creates the store.
     * @param {Object} config (optional) Config object
     */
    , constructor: function(config) {
    	console.log('constructor IN'+config);
    	Ext.apply(this, config);
    	var d = new Date();
    	var modelname =  'DynamicStoreModel'+(d.getTime()%10000000);
    	
    	var serviceUrl = config.serviceUrl;
        Ext.define(modelname, {
            extend: 'Ext.data.Model'
        });
        
		this.model= modelname;
		this.proxy= {
			type: 'ajax',
			url:  this.serviceUrl,
			reader: {
				type:"json",
				root: "root"
			}
		};
    	
    	this.callParent([config]);
        
    	this.fields = this.model.prototype.fields;
    	console.log('constructor OUT');
		this.on('load', this.onStoreLoad, this);
    }


	, onStoreLoad: function() {
		console.log('onStoreLoad IN');		
		try{
			var metadata = this.proxy.reader.jsonData.metaData;
			if(metadata.error && metadata.error=='error'){
				Sbi.exception.ExceptionHandler.showErrorMessage(metadata.stacktrace,LN('sbi.behavioural.lov.test.error'));
				return false;
			}else{
				var columns = metadata.fields;
				console.log('columns length ' + columns.length);
			}
		}catch(e){
			Sbi.exception.ExceptionHandler.showErrorMessage(e,LN('sbi.behavioural.lov.test.error'));
			return false;
		}
		console.log('onStoreLoad OUT');
	
		}
	
	, getColumns: function(){
		console.log('store.getColumns');
		return this.proxy.reader.jsonData.metaData.fields;
	}
	
	
});