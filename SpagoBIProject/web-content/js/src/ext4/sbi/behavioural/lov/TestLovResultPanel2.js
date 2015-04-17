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
		extend: 'Sbi.widgets.grid.DynamicGridPanel2', 
		
		constructor: function(config) 
		{	
			Sbi.debug('[IN] TestLovResultPanel2 - constructor');
			
			var defautlConf = 
			{ 
				pagingConfig: {}, 
				
				lovProvider: config.lovProvider,
				
				storeConfig:
				{ 
					pageSize: 10
				} 
			};
			
			this.title =  "LOV result preview";
			this.filterConfig = {};
			this.border = false;
			this.region = 'south';
			
			defautlConf = Ext.apply( defautlConf, config || {} );
			
			Ext.apply(this, defautlConf);
			
			defautlConf.serviceUrl = "http://localhost:8080/SpagoBI/restful-services/LOV/Test";
	    	
	    	this.callParent([defautlConf]);	// Going into "DynamicGridPanel.js"
	    	
	    	this.store.on
	    	(
    			'load',
    			
    			function()
    			{
    				console.log("[LOAD] in TestLovResultPanel2");
    				this.fireEvent('storeLoad')
				},
				
				this
			);
	    	
	    	this.store.on
	    	(
    			"wrongSyntax",
    			
    			function()
    			{
    				this.fireEvent('wrongSyntax1',"wrong");
    			},
    			
    			this
	    	);
	    	
	    	Sbi.debug('[OUT] TestLovResultPanel2 - constructor');
		}	
	}
);