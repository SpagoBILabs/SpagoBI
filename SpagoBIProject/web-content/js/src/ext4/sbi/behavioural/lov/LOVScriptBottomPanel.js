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
						fieldLabel: LN('sbi.behavioural.lov.details.scriptDescription'),
						height: 100,
						width: 500,
						padding: '10 0 10 0',
						allowBlank: false
					}
	    		);	
	    		
	    		Sbi.debug("[OUT] Creating LOVScriptBottomPanel");
			}
		}
);