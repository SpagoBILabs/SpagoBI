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

Ext.define
(
	'Sbi.behavioural.lov.TestLovResultPanel2', 
	
	{
		extend: 'Sbi.widgets.grid.DynamicGridPanel', 
		
		constructor: function(config) 
		{		
			console.log("USAO U TestLovResultPanel2.js");
			
			var defautlConf = 
			{ 
				pagingConfig: {}, 
				
				storeConfig:
				{ 
					pageSize: 10
				} 
			};
			
			console.log("TEST LOV RESULT PANEL (2) - 1");
						
			this.title =  "LOV result preview";
			this.filterConfig = {};
			this.border = false;
			this.region = 'south';
			
			defautlConf = Ext.apply( defautlConf, config || {} );
			
			Ext.apply(this, defautlConf);
			
			Sbi.debug('TestLovPanel costructor IN');
			
			defautlConf.serviceUrl = "http://localhost:8080/SpagoBI/restful-services/LOV/Test";
	    	
	    	this.callParent([defautlConf]);
	    	
	    	this.store.on
	    	(
    			'load',
    			
    			function()
    			{
    				console.log("555555555555555");
    				this.fireEvent('storeLoad')
				},
				
				this
			);
	    	
	    	Sbi.debug('TestLovPanel costructor OUT');
		}	
	}
);