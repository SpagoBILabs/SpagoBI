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
 * - Danilo Ristovski (danilo.ristovski@mht.net)
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
				
				profileAttrib: config.profileAttrib,
				
				storeConfig:
				{ 
					pageSize: 10
				} 
			};
			
			this.title =  LN("sbi.behavioural.lov.lovResultPreviewPanelTitle");
			this.filterConfig = {};
			this.border = false;
			this.region = 'south';
			
			defautlConf = Ext.apply( defautlConf, config || {} );
			
			Ext.apply(this, defautlConf);
			
			defautlConf.serviceUrl = Sbi.config.serviceRegistry.getRestServiceUrl
			(
				{
					serviceName: 'LOV/Test', 
					baseParams: config.baseParams
				}
			);
	    	
	    	this.callParent([defautlConf]);	// Going into "DynamicGridPanel2.js"
	    	
	    	this.store.on
	    	(
    			'load',
    			
    			function()
    			{
    				this.fireEvent('storeLoad')
				},
				
				this
			);
	    	
	    	this.store.on
	    	(
    			"wrongSyntax",
    			
    			function()
    			{
    				this.fireEvent('wrongSyntax1',"wrong");	// Caught in TestLovPanel2.js
    			},
    			
    			this
	    	);
	    	
	    	this.store.on
	    	(
    			"missingProfileAttr",
    			
    			function(missingProfileAttr)
    			{
    				this.fireEvent('missingProfileAttr1',missingProfileAttr);	// Caught in TestLovPanel2.js
    			},
    			
    			this
	    	);
	    	
	    	Sbi.debug('[OUT] TestLovResultPanel2 - constructor');
		}	
	}
);