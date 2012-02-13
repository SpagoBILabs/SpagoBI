app.views.ComposedExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical',
	    fullscreen: true
		, initComponent: function (options)	{

			console.log('init chart execution');
		    
			app.views.ComposedExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setChartWidget: function(resp){
			

		}

		
});