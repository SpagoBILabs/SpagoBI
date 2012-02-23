app.views.ChartExecutionPanel = Ext.extend(app.views.WidgetPanel, {
	dockedItems : [],
	scroll : 'vertical',

	initComponent : function() {
		console.log('init chart execution');
		app.views.ChartExecutionPanel.superclass.initComponent.apply(this,
				arguments);
		if(this.IS_FROM_COMPOSED){
			this.on('afterlayout',this.showLoadingMask,this);
		}
	},
	setChartWidget : function(resp, fromcomposition) {
	
		var r;
		var config = resp.config;
		config.animate = true;

		config.listeners = {
            'itemtap': function(series, item, event) {  }
        };
		var chartConfig = {
			items : [ config ]
		};

		if (fromcomposition) {
			chartConfig.width = '100%';
			chartConfig.height = '100%';
			chartConfig.defaultType = 'chart';
			chartConfig.layout = 'fit';
			r = new Ext.Panel(chartConfig);
			this.insert(0, r);
			this.doLayout();
		} else {
			chartConfig.bodyMargin = '10% 1px 60% 1px';
			chartConfig.fullscreen = true;
			app.views.chart = new Ext.chart.Panel(chartConfig);
		}
		if(this.IS_FROM_COMPOSED){
			this.loadingMask.hide();
		}
	}

});