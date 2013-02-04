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
 * - Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.define('Sbi.behavioural.lov.TestLovResultPanel', {
    extend: 'Sbi.widgets.grid.DynamicGridPanel'

	, constructor: function(config) {
		
		var defautlConf = { pagingConfig:{}, storeConfig:{ pageSize: 10}	};
		this.title =  "LOV result preview";
		this.border = false;
		this.region = 'south';
		defautlConf = Ext.apply( defautlConf,config ||{} );
		Ext.apply(this,defautlConf);
		
		console.log('TestLovPanel costructor IN');
		
		defautlConf.serviceUrl=   Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'LIST_TEST_LOV_ACTION'
    	});
    	
    	this.callParent([defautlConf]);
    	this.store.on('load',function(){this.fireEvent('storeLoad')},this);
    	console.log('TestLovPanel costructor OUT');
    }
    





	
});


