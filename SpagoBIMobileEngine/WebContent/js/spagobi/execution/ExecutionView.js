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


			app.views.tableExecutionPanel = new app.views.TableExecutionPanel();
			app.views.chartExecutionPanel = new app.views.ChartExecutionPanel();
			app.views.composedExecutionPanel = new app.views.ComposedExecutionPanel();
		    Ext.apply(this, {
		        items: [
		            app.views.tableExecutionPanel,
		            app.views.chartExecutionPanel,
		            app.views.composedExecutionPanel
		        ]
		    });
			app.views.ExecutionView.superclass.initComponent.apply(this, arguments);

		}
		, setWidget: function(resp, type){
			if(type == 'table'){
				app.views.tableExecutionPanel.setTableWidget(resp);
			}

		}

});