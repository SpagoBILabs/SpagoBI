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
            'itemtap': function(series, item, event) { 
	 			var crossParams = new Array();
				var target = event.target;
				//this.setCrossNavigation(resp, target, crossParams);
				this.fireEvent('execCrossNavigation', this, crossParams);
			}
        };
		
		if(config.dockedItems==undefined || config.dockedItems==null){
			config.dockedItems = new Array();
		}
		
		if(config.interactions==undefined || config.interactions==null){
			config.interactions = new Array();
		}

		if(config.options !== undefined && config.options !== null && config.options.showValueTip){
			this.addValueTip(config);
		}

		var chartConfig = {

			items : [ config ]
		};

		if (fromcomposition) {
			chartConfig.width = '100%';
			chartConfig.height = '100%';
			chartConfig.defaultType = 'chart';
			chartConfig.layout = 'fit';
			if(config.title){
				chartConfig.dockedItems = [{
	                dock: 'top',
	                xtype: 'toolbar',
	                ui: 'light',
	                title: config.title.value
	            }];
			}
			r = new Ext.Panel(chartConfig);
			this.insert(0, r);
			this.doLayout();
		} else {
			chartConfig.bodyMargin = '10% 1px 60% 1px';
			chartConfig.fullscreen = true;
			if(config.title){
				chartConfig.title = config.title.value;
			}
			app.views.chart = new Ext.chart.Panel(chartConfig);
		}
		if(this.IS_FROM_COMPOSED){
			this.loadingMask.hide();
		}
	}
	
	, addValueTip: function(config){
		config.interactions.push({
            type: 'iteminfo',
            listeners: {
                show: function(interaction, item, panel) {
                	panel.setWidth(400);
                	var str = "";
                	var storeItem = item.storeItem;
                	var values = item.value;
                	for(var propertyName in storeItem.data){
                	   if((storeItem.data).hasOwnProperty(propertyName) ){
                		   var propertyValue = (storeItem.data)[propertyName];
                		   if(values.indexOf(propertyValue)>=0){
                			   str = str +"<li><b><span>"+propertyName+"</b>: "+propertyValue+"</span></li>";
                		   }
                	   }
                	}
                	if(str.length>0){
                		str = "<ul>"+str+"</ul>";
                		 panel.update(str);
                	}
                }
            }
        });
	}

});