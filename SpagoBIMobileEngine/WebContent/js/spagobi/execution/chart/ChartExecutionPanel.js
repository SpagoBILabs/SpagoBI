/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define('app.views.ChartExecutionPanel',{
	extend: 'app.views.WidgetPanel',
	config:{
		scroll : 'vertical',
		executionInstance: null,
		items:[],
		//colors: ['red','blue','yellow','green','orange','brown','white','grey'],
		chartTypes:{
			polar: "Ext.chart.PolarChart",
			cartesian: "Ext.chart.CartesianChart",
			spacefilling: "Ext.chart.SpaceFillingChart"
		}
	},


	constructor : function(config) {
		Ext.apply(this,config);
		this.callParent(arguments);

	},

	initialize : function() {
		var c = this.setChartWidget(this.resp, this.fromcomposition,this.fromCross );

		if(c.chartType){
			this.add(Ext.create(this.getChartTypes()[c.chartType],c));
		}else{
			this.add(new Ext.chart.Chart(c));
		}

		console.log('init chart execution');
//		if(this.IS_FROM_COMPOSED){
//		this.on('afterlayout',this.showLoadingMask,this);
//		if(app.views.execution.loadingMaskForExec != undefined){
//		app.views.execution.loadingMaskForExec.hide();
//		}
//		}.callParent(arguments);

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
		
		var chartConfig = Ext.apply({},config);
		
		this.manageColors(chartConfig);

		if (fromcomposition) {

			chartConfig.width = '100%';
			chartConfig.height = '100%';
		} else {
			this.fullscreen = true;
			chartConfig.fullscreen = true;
		}

		//chartConfig.bodyMargin = '10% 1px 60% 1px';


		if(config.title){
			chartConfig.title = config.title.value;
		}

		return chartConfig;

	}
	
	, manageColors: function(config){
		if(config.colors){
			if(config.colors.functionName=="getGradientColorsHSL"){
				config.colors = Ext.ux.ColorPatterns.getGradientColorsHSL.call(
						this,
						Ext.ux.ColorPatterns.getBaseColors(config.colors.basecolor),
						config.colors.from,
						config.colors.to,
						config.colors.number);
			}if(config.colors.functionName=="getGradientColorsHSL"){
				config.colors = Ext.ux.ColorPatterns.getBaseColors.call(
						this,
						config.colors.index);
			}
		}else{
			config.colors = Ext.ux.ColorPatterns.getBaseColors();
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
					var storeItem = item.record.raw;
					//var values = item.value;
					for(var propertyName in storeItem){
						if((storeItem).hasOwnProperty(propertyName) ){
							var propertyValue = (storeItem)[propertyName];
							if(propertyValue){
								//  if(values.indexOf(propertyValue)>=0){
								str = str +"<li><b><span>"+propertyName+"</b>: "+propertyValue+"</span></li>";
								// } 
							}
						}
					}
					if(str.length>0){
						str = "<ul>"+str+"</ul>";
						panel.setHtml(str);
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
				for(var i=0; i< params.length; i++){
					var param = params[i];
					var name = param.paramName;
					var type = param.paramType;


					if(Ext.isArray(resp.config.series)){
						for (var t = 0; t < resp.config.series.length ; t++){
							//chart type
							var charttype = resp.config.series[t].type;




							var storeItem = item.record;
							//var values = item.value;
							if(storeItem!= undefined){
								//for bar chart multiseries

								//series 
								var serieField = item.field;//selected serie


								if(type == 'SERIE_NAME'){
									crossParams.push({name : name, value : serieField});
								}else if(type == 'SERIE'){
									crossParams.push({name : name, value : storeItem.data[serieField]});
								}else if(type == 'CATEGORY'){
									if (charttype == 'pie'){
										crossParams.push({name : name, value : storeItem.data[serieField]});
									}else{
										var cat;
										var seriesField = [];//all series name
										for(var s = 0; s<series.sprites.length; s++){
											seriesField.push(series.sprites[s].getField());
										}
										for(var propertyName in storeItem.data){
											if(seriesField.indexOf(propertyName) < 0 && propertyName != 'id' && propertyName != 'recNo' ){
												cat = (storeItem.data)[propertyName];;
											}
										}
										crossParams.push({name : name, value : cat});
									}

								}else{
									crossParams.push({name : name, value : param.paramValue});
								}					                	
								//RELATIVE AND ABSOLUTE PARAMETERS ARE MANAGED SERVER SIDE 

							}
						}
					}					
				}
			}				
		}

		return crossParams;
	}
});