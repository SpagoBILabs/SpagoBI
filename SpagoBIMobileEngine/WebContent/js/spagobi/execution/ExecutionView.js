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


		initComponent: function ()	{
			this.title = 'Execution view';
			console.log('init Execution view');
	        this.bottomTools = new app.views.BottomToolbar({parameters: this.parameters});
	        this.dockedItems= [this.bottomTools];
			//var toolbar = new app.views.BottomToolbar(this.parameters);
			//this.dockedItems = [bottomTools];
			
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