/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Danilo Ristovski (danilo.ristovski@mht.net)
 */

Ext.define
(
		"Sbi.behavioural.lov.LOVScriptBottomPanel", 
		
		{
			create: function(dataSourceURL) 
			{
				Sbi.debug("[IN] Creating LOVScriptBottomPanel");	
				
				Ext.define
	    		(
					"ScriptTypeModel", 
					
					{
						extend: 'Ext.data.Model',
						fields: [ "VALUE_NM", "VALUE_DS", "VALUE_ID", "VALUE_CD" ]
					}
				);
	        	    		
	        	var scriptTypeStore = Ext.create
	        	(
	    			'Ext.data.Store',
		    		
	    			{
		        		model: "ScriptTypeModel",
		        		autoLoad: true,
		        		
		        		proxy: 
		        		{
		        			type: 'rest',   			
		        			extraParams : { DOMAIN_TYPE: "SCRIPT_TYPE" },
		        			
		        			url:  dataSourceURL,	
		        			
		        			reader: 
		        			{
		        				type:"json"
		        			}
		        		}
		        	}
				);
	        	
	        	scriptTypeStore.on
	        	(
	    			"load", 
	    			
	    			function(scriptTypeStore)
	    			{ 
	    				Sbi.debug('[INFO] Script type  store loaded (SCRIPT)');
	    			}
				);
	        	
	    		this.scriptTypeCombo = new Ext.create
	    		(
					'Ext.form.ComboBox', 
		    		
					{
		    			fieldLabel: LN('sbi.behavioural.lov.details.scriptType'),
		    	        store: scriptTypeStore,	    	        
		    	        name: "SCRIPT_TYPE",
		    	        id: "SCRIPT_TYPE_COMBO",
		    	        displayField:'VALUE_NM',
		    	        valueField:'VALUE_NM',
		    	        editable: false,
		    	        allowBlank: false,
		    	        padding: "10 0 10 0"
		    	    }
				);
	    		
	    		this.scriptQuery = Ext.create
	    		(
					"Ext.form.field.TextArea",
					
					{
						id: "SCRIPT_QUERY",
						layout: "fit",
						fieldLabel: LN('sbi.behavioural.lov.details.scriptDescription'),
						height: 300,
						width: 800,
						padding: '10 0 10 0',
						allowBlank: false
					}
	    		);	
	    		
	    		Sbi.debug("[OUT] Creating LOVScriptBottomPanel");
			}
		}
);