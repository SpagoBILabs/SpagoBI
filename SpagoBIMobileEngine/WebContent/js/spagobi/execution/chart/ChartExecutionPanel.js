app.views.ChartExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical',
	    fullscreen: true
		, initComponent: function (options)	{

			console.log('init chart execution');
		    
			app.views.ChartExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setChartWidget: function(resp){
			

		}

		
});