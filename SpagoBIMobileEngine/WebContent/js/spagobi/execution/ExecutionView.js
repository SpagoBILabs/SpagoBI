app.views.ExecutionView = Ext.extend(Ext.TabPanel,

		{
	    fullscreen: true,
	    //type: 'light',
	    tabBar: {
            dock: 'top',
            height: 0,
            layout: {
                pack: 'center'
            }
        },

        dockedItems: [{
            xtype: 'toolbar',
            dock: 'bottom',
            defaults: {
                ui: 'plain',
                iconMask: true
            },
            scroll: 'horizontal',
            layout: {
                pack: 'center'
            },
	        items: [{
			    title: 'Home',    		    
			    iconCls: 'reply',			    
			    text: 'Home',
	            handler: function () {
	        		Ext.dispatch({
	                    controller: app.controllers.mobileController,
	                    action: 'backToBrowser'
	        		});
	
	            }},
	            {
			    title: 'Parametri',    		    
			    iconCls: 'compose',
			    text: 'Parametri',
	            handler: function () {
	        		Ext.dispatch({
	                    controller: app.controllers.mobileController,
	                    action: 'backToBrowser'
	        		});
	
	            }},
	            {
			    title: 'Info',    		    
			    iconCls: 'info',
			    text: 'Info'

	        }]
  
        }],

		initComponent: function ()	{
			this.title = 'Execution view';
			console.log('init Execution view');

			app.views.tableExecution = new app.views.TableExecution();
		    Ext.apply(this, {
		        items: [
		            app.views.tableExecution
		        ]
		    });
			app.views.ExecutionView.superclass.initComponent.apply(this, arguments);

		}
		, setWidget: function(resp, type){
			app.views.tableExecution.setTableWidget(resp);

		}

});