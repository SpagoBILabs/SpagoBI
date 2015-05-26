Ext.define
(
	"Sbi.behavioural.lov.LOVDatasetBottomPanel", 
	
	{			
		create: function(datasetURL)
		{		
			Sbi.debug("[IN] Creating LOVDatasetBottomPanel");
			
			console.log("USAOOOOOOO...");
			
			Ext.define
    		(
				"DatasetModel", 
				
				{
					extend: 'Ext.data.Model',
					fields: [ "id", "name", "description", "label" ] // fields (labels) from JSON that comes from server that we call
				}
			);	
			
			var datasetStore = Ext.create
        	(
    			'Ext.data.Store',
	    		
    			{
	        		model: "DatasetModel",
	        		autoLoad: true,
	        		
	        		proxy: 
	        		{
	        			type: 'rest',	        			
	        			
	        			url:  datasetURL,	
	        			
	        			reader: 
	        			{
	        				type:"json",
	        				root: "root"
	        			}
	        		}
	        	}
			);
			
			datasetStore.on
        	(
    			'load', 
    			
    			function(datasetStore)
    			{ 
    				Sbi.debug('[INFO] Dataset store loaded (DATASET)');
    				console.log(datasetStore);
    			}
			);
			
			this.datasetForm = Ext.create
			(
				"Ext.form.Panel",
				
				{
					title: "Dataset Form",
					width: "100%",
					
					defaultType: 'textfield',
					
				    items: 
			    	[
				    	 {
					        fieldLabel: 'Dataset',
					        name: 'first',
					        allowBlank: false
				    	 }
				    ],
				    
				    buttons:
			    	[
						{
						    text: 'Choose...',
						    
						    handler: function() 
						    {
						    	console.log("########################");
						    	
						    	var datasetWindow = Ext.create
			        			(
			        				'Ext.window.Window', 
			        				
			        				{
			        				    title: "AAA",
			        				    layout: 'fit',
			        				    resizable: false,
			        				    modal: true, // Prevent user from selecting something behind the window 
			        				   // items: [ lovDatasetPanel.datasetForm ],
			        				    
			        				    listeners:
			        				    {
//			        		                 'close': function(win)
//			        		                 {			                          
//			        	                          /* Check if window is closed after clicking the Add button
//			        	                           * or after clicking on the X button on the top right corner.
//			        	                           * In the first case close window and show the first tab, hidding
//			        	                           * the second one. In the latter case, show the result page and
//			        	                           * close the window for filling the missing profile attributes. */
//			        	                          if (addClicked == false)
//			                                	  {
//			        	                        	  updateScope.getComponent("TAB_PANEL_RESULTS").tabBar.items.items[1].hide();
//			        		                          updateScope.getComponent("TAB_PANEL_RESULTS").setActiveTab(0);   
//			                                	  }			                              
//			        		                 }
			        				    }
			        				}
			        			);
						    }
						}
			    	]
				}
			);
			
			
			
			   		
    		Sbi.debug("[OUT] Creating LOVDatasetBottomPanel");
		}			
		
	}
);