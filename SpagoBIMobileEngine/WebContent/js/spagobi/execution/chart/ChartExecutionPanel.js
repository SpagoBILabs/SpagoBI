/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define('app.views.ChartExecutionPanel',{
	extend: 'app.views.WidgetPanel',
	config:{
		scroll : 'vertical',
		style:{
			position: "relative"
		},
		items:[],
		//colors: ['red','blue','yellow','green','orange','brown','white','grey'],
		chartTypes:{
			polar: "Ext.chart.PolarChart",
			cartesian: "Ext.chart.CartesianChart",
			spacefilling: "Ext.chart.SpaceFillingChart"
		}
	},


	initialize : function() {
		
		var c = this.setChartWidget(this.resp, this.fromcomposition,this.fromCross );

		if(c.chartType){
			this.add(Ext.create(this.getChartTypes()[c.chartType],c));
		}else{
			this.add(new Ext.chart.Chart(c));
		}

		console.log('init chart execution');
		this.callParent();
	},

	setChartWidget : function(resp, fromcomposition, fromCross) {
		var config = resp.config;
		config.animate = true;
		config.shadow = true;

		if(config.enableuserfunction){
			this.resolveUserFunctions(config);	
		}

		//manage the god direct to the document in any place for the chart I click
		if(resp.config.series && resp.config.series[0] && resp.config.series[0].type=='gauge'){
			config.listeners = {
					scope: this,
					'tap': function(series, item, event) { 
						var crossParams = new Array();
						var targetDoc;
						if(resp.config && resp.config.drill){
							if(resp.config.drill.params){
								crossParams = resp.config.drill.params;
							}
							targetDoc = this.setTargetDocument(resp);	
							if(targetDoc){
								this.fireEvent('execCrossNavigation', this, crossParams, targetDoc);
							}
						}
					}
			};
		}else{
			config.listeners = {
					scope: this,
					'itemtap': function(series, item, event) { 

						if(resp.config && resp.config.drill){
							var crossParams = new Array();
							if(resp.config.drill.params){
								crossParams = resp.config.drill.params;
							}
							this.setCrossNavigation(resp, item, crossParams);
							var targetDoc;
							targetDoc = this.setTargetDocument(resp);	
							if(targetDoc){
								this.fireEvent('execCrossNavigation', this, crossParams, targetDoc);
							}
						}
					}
			};
		}
		

		


		if(config.interactions==undefined || config.interactions==null){
			config.interactions = new Array();
		}

		if(config.options !== undefined && config.options !== null && config.options.showValueTip){
			this.addValueTip(config);
		}
		
		var chartConfig = Ext.apply({},config);
		
		this.manageColors(chartConfig);

//		if (fromcomposition) {

		chartConfig.width = '100%';
		chartConfig.height = '100%';
//		} else {
//			this.fullscreen = true;
//			chartConfig.fullscreen = true;
//

		//chartConfig.bodyMargin = '10% 1px 60% 1px';


		if(config.title){
			chartConfig.title = config.title.value;
		}
		
		
		return chartConfig;
	}
	
	//search in the template, the user function tag,
	//and resolve it with the function definition in user_functions.js
	, resolveUserFunctions: function(template){
	
		if(!(template instanceof Object)){
			return this.evalFunction(template);
		}
		for(p in template){
			if(p=="userFunction"){
				var functionDef = template[p];
				var functionName = functionDef.functionName;
				var functionArgs = functionDef.functionArgs;
				try{
					return Sbi.chart.userFunctions[functionName].call(this,functionArgs);
				}catch (e){
					Sbi.exception.ExceptionHandler.showErrorMessage("Error executing the user function '"+functionName+"':"+e );
				}
			}else{
				template[p] = this.resolveUserFunctions(template[p]);
			}
		}
		return template;
	}
	
	, evalFunction: function(template){

		if((typeof template) == "string"){
			if(template.indexOf("functionToEval")>=0){
				template = template.replace("functionToEval(","");
				template = template.replace(")functionToEval","");
				template = template.replace("userFunctions.","Sbi.chart.userFunctions");
				template = eval(template);
			}
		}
		return template;
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
			}else if(config.colors.functionName=="getGradientColorsByBrightness"){
				config.colors = Ext.ux.ColorPatterns.getGradientColorsHSL.call(
						this,
						Ext.ux.ColorPatterns.getBaseColors(config.colors.basecolor),
						config.colors.from,
						config.colors.to,
						config.colors.number);
			}else if(config.colors.functionName=="getGradientColors"){
				config.colors = Ext.ux.ColorPatterns.getGradientColorsHSL.call(
						this,
						config.colors.from,
						config.colors.to,
						config.colors.number);
			}else if(config.colors.functionName=="getAlteredBaseColorsHSL"){
				config.colors = Ext.ux.ColorPatterns.getGradientColorsHSL.call(
						this,
						config.colors.deltaHSL);
			}else if(config.colors.functionName=="getBaseColors"){
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
						var categoryField;
						if(resp.config.axes){
							for(var ax = 0; ax<resp.config.axes.length; ax++){
								var axe = resp.config.axes[ax];
								if(axe.type && axe.type=="category"){
									if(axe.fields instanceof Array){
										categoryField=axe.fields[0];
									}else{
										categoryField=axe.fields;
									}
								}
							}
						}
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
									if (charttype == 'pie' || charttype == 'pie3d'){
										crossParams.push({name : name, value : storeItem.data[serieField]});
									}else{
										if(categoryField){
											var cat = (storeItem.data)[categoryField];
											crossParams.push({name : name, value : cat});
										}
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