app.views.ChartExecutionPanel = Ext.extend(app.views.WidgetPanel, {
	dockedItems : [],
	scroll : 'vertical',

	initComponent : function() {
		console.log('init chart execution');
		app.views.ChartExecutionPanel.superclass.initComponent.apply(this,
				arguments);
		if(this.IS_FROM_COMPOSED){
			this.on('afterlayout',this.showLoadingMask,this);
			if(app.views.execution.loadingMaskForExec != undefined){
				app.views.execution.loadingMaskForExec.hide();
			}
		}
	},
	setChartWidget : function(resp, fromcomposition, fromCross) {

		var r;
		var config = resp.config;
		config.animate = true;
		config.shadow = true;

		config.listeners = {
			scope: this,
            'itemtap': function(series, item, event) { 
	 			var crossParams = new Array();
				this.setCrossNavigation(resp, item, crossParams);
				var targetDoc;
				if(resp.config != undefined && resp.config.drill != undefined){
					targetDoc = this.setTargetDocument(resp);					
				}
				this.fireEvent('execCrossNavigation', this, crossParams, targetDoc);
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

		
		///---------------------------------------
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
		}else if (fromCross) {
			chartConfig.width = '100%';
			chartConfig.height = '100%';
			chartConfig.bodyMargin = '10% 1px 60% 1px';
			chartConfig.defaultType = 'chart';
			chartConfig.layout = 'fit';
			chartConfig.style = 'z-index:100;';//nedded to render charts border informations (like axis..)
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
			r.doLayout();
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
            gesture: 'longpress',
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

	, setCrossNavigation: function(resp, item, crossParams){
		
		var drill = resp.config.drill;
		if(drill != null && drill != undefined){
			var params = drill.params;
			var series = item.series;
			
			if(params != null && params != undefined){
				for(i=0; i< params.length; i++){
					var param = params[i];
					var name = param.paramName;
					var type = param.paramType;


					if(Ext.isArray(resp.config.series)){
						for (t = 0; t < resp.config.series.length ; t++){
							//chart type
							var charttype = resp.config.series[t].type;
							
							if (charttype == 'area'){
								for(k = 0; k<series.items.length; k++){
								
									var cat = series.items[k].storeField;
									if(item.storeField == cat){
										var ser = item.storeItem.data[cat];
										//RELATIVE AND ABSOLUTE PARAMETERS ARE MANAGED SERVER SIDE 
										if(type == 'SERIE'){
											crossParams.push({name : name, value : ser});
										}else if(type == 'CATEGORY'){
											crossParams.push({name : name, value : cat});
										}else{
											crossParams.push({name : name, value : param.paramValue});
										}
									}
								}
								
							}else{
			                	var storeItem = item.storeItem;
			                	var values = item.value;
								if(values!= undefined){
									//for bar chart multiseries
				                	
				                	//series 
				                	var seriesField = series.label.field;//array
				                	
									var cat;
									var ser;
									
				                	for(var propertyName in storeItem.data){
				                	   if((storeItem.data).hasOwnProperty(propertyName) ){
				                		   var propertyValue = (storeItem.data)[propertyName];
				                		   if(seriesField.indexOf(propertyName) != -1){
				                			   if(values.indexOf(propertyValue) != -1){ 
				                				   ser = propertyValue;
				                			   }
				                		   }else{
				                			   if(propertyName != 'id' && propertyName != 'recNo' ){
				                				   cat = propertyValue;
				                			   }
				                		   }
				                	   }
				                	}
				                	//RELATIVE AND ABSOLUTE PARAMETERS ARE MANAGED SERVER SIDE 
									if(type == 'SERIE'){
										crossParams.push({name : name, value : ser});
									}else if(type == 'CATEGORY'){
										crossParams.push({name : name, value : cat});
									}else{
										crossParams.push({name : name, value : param.paramValue});
									}
								}else{
									//for pie chart
									var serieField = series.field;
									var categoryField = series.label.field;
									
									var cat = item.storeItem.data[categoryField];
									var ser = item.storeItem.data[serieField];
									/*	RELATIVE AND ABSOLUTE PARAMETERS ARE MANAGED SERVER SIDE */
									if(type == 'SERIE'){
										crossParams.push({name : name, value : ser});
									}else if(type == 'CATEGORY'){
										crossParams.push({name : name, value : cat});
									}else{
										crossParams.push({name : name, value : param.paramValue});
									}
								}

							}
						}
					}					
				}
			}				
		}

		return crossParams;
	}
});