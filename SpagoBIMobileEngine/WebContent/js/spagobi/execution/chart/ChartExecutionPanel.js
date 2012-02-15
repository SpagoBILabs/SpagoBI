app.views.ChartExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical',
	    fullscreen: true
		, initComponent: function ()	{

			console.log('init chart execution');
		    
			app.views.ChartExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setChartWidget : function(resp) {

			var config = resp.config;

			var r =	   new Ext.chart.Panel({
	            fullscreen: true,
	            bodyMargin: '50px 50px 100px 50px',
	            title: 'Area Chart',
	            items: [config]});

			
			app.views.chart =  r;
	
			this.add(app.views.chart);

	}

		
});