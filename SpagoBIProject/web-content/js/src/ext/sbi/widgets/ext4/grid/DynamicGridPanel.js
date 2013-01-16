/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
  

/**
 * Object name 
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
 * Authors
 * 
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.define('Sbi.widgets.grid.DynamicGridPanel', {
    extend: 'Ext.grid.Panel'

	, constructor: function(config) {

		console.log('DynamicGridPanel costructor IN');
		var defaultConfig = {};
		defaultConfig = Ext.apply( defaultConfig,config ||{} );
		Ext.apply(this,defaultConfig);
		
    	console.log('DynamicGridPanel build store');
		
    	var store = Ext.create('Sbi.widgets.store.DynamicStore', config ||{});
      	this.store = store;
      	
      	this.columns = [];

      	console.log('DynamicGridPanel load store');
      	this.store.on('load', this.updateGrid, this);
      	
    	this.store.load();
    	this.callParent([defaultConfig]);
    	console.log('DynamicGridPanel costructor OUT');
    },
    
    updateGrid: function(){
    	console.log('DynamicGridPanel updategrid IN');
    	var columns = this.store.getColumns();
    	this.reconfigure(this.store, columns);
    }




	
});


