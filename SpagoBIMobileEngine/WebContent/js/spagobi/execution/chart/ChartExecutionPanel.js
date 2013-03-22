/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
Ext.define('app.views.ChartExecutionPanel',{
	extend: 'app.views.WidgetPanel',
	config:{
		//dockedItems : [],
		scroll : 'vertical',
		// fullscreen: true,
		 items:[]
	},


	constructor : function(config) {
		Ext.apply(this,config);
		this.callParent(arguments);

	},
	
	initialize : function() {
		var c = this.setChartWidget(this.resp, this.fromcomposition,this.fromCross );
		this.add(new Ext.chart.Chart(c));
		//this.add({html:"asdasdddas444",height:200});
		console.log('init chart execution');
//		if(this.IS_FROM_COMPOSED){
//		this.on('afterlayout',this.showLoadingMask,this);
//		if(app.views.execution.loadingMaskForExec != undefined){
//			app.views.execution.loadingMaskForExec.hide();
//		}
//	}.callParent(arguments);

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

	//	if(config.options !== undefined && config.options !== null && config.options.showValueTip){
			this.addValueTip(config);
		//}


		var chartConfig = {

			items : [ config ]
		};

		if (fromcomposition) {

			chartConfig.width = '100%';
			chartConfig.height = '100%';
		} else {
			this.fullscreen = true;
			chartConfig.fullscreen = true;
		}

			chartConfig.bodyMargin = '10% 1px 60% 1px';
			
			
			if(config.title){
				chartConfig.title = config.title.value;
			}
			//app.views.chart = Ext.create("Ext.chart.CartesianChart",config );
			config.xtype = "chart";
			return config;
//		}
//		if(this.IS_FROM_COMPOSED){
//			this.loadingMask.hide();
//		}
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
				                	
									if(type == 'SERIE_NAME'){
										crossParams.push({name : name, value : seriesField});
									}else{
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