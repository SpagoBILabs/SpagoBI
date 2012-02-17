app.views.ChartExecutionPanel = Ext.extend(Ext.Panel,	{
		dockedItems: [],
	    scroll: 'vertical',

		initComponent: function ()	{
			console.log('init chart execution');  
			app.views.ChartExecutionPanel.superclass.initComponent.apply(this, arguments);
		},
		setChartWidget : function(resp, fromcomposition) {

			var config = resp.config;

			var chartConfig ={
	            items: [config]
			};

			if(fromcomposition){
				chartConfig.width='100%';
				chartConfig.height='100%';
			}else{
				chartConfig.bodyMargin='50px 50px 100px 50px';
				chartConfig.fullscreen=true;
			}
			var r =	new Ext.chart.Panel(chartConfig);
			this.insert(0,r);
			this.doLayout();

	}

		
});