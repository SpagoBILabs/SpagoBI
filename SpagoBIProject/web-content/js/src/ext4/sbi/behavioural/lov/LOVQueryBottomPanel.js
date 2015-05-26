Ext.define
(
		"Sbi.behavioural.lov.LOVQueryBottomPanel", 
		
		{			
			create: function(dataSourceURL)
			{		
				Sbi.debug("[IN] Creating LOVQueryBottomPanel");
				
				Ext.define
	    		(
					"DataSourceModel", 
					
					{
						extend: 'Ext.data.Model',
						fields: [ "DESCRIPTION", "DATASOURCE_LABEL", "JNDI_URL", "DATASOURCE_ID" ] // fields (labels) from JSON that comes from server that we call
					}
				);
				
				var dataSourceStore = Ext.create
	        	(
	    			'Ext.data.Store',
		    		
	    			{
		        		model: "DataSourceModel",
		        		autoLoad: true,
		        		
		        		proxy: 
		        		{
		        			type: 'rest',	        			
		        			//extraParams : { DOMAIN_TYPE: "INPUT_TYPE" },
		        			
		        			url:  dataSourceURL,	
		        			
		        			reader: 
		        			{
		        				type:"json",
		        				root: "root"
		        			}
		        		}
		        	}
				);
	        	
	    		dataSourceStore.on
	        	(
	    			'load', 
	    			
	    			function(dataSourceStore)
	    			{ 
	    				Sbi.debug('[INFO] Data source store loaded (QUERY)');
	    			}
				);
	    		
	    		this.dataSourceCombo = new Ext.create
	    		(
					'Ext.form.ComboBox', 
		    		
					{
		    			fieldLabel: LN('sbi.behavioural.lov.details.dataSourceLabel'),
		    	        store: dataSourceStore,
		    	        name: "DATASOURCE_ID",
		    	        id: "DATA_SOURCE_COMBO",
		    	        displayField:'DATASOURCE_LABEL',
		    	        valueField:'DATASOURCE_LABEL',
		    	        // (top, right, bottom, left)
		    	        padding: "10 0 10 0",
		    	        editable: false,
		    	        allowBlank: false
		    	    }
				); 
	    		
	    		this.dataSourceQuery = Ext.create
	    		(
					"Ext.form.field.TextArea",
					
					{					
						fieldLabel: LN('sbi.behavioural.lov.details.queryDescription'), 
						id: "DATA_SOURCE_QUERY",
						height: 100,
						width: 500,
						padding: '10 0 10 0',
						allowBlank: false
					}
	    		);
	    		
	    		Sbi.debug("[OUT] Creating LOVQueryBottomPanel");
			}			
			
		}
);