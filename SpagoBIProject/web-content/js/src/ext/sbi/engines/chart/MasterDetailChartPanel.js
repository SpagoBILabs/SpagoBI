/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/

/**
 * Object name
 * 
 * [description]
 * 
 * 
 * Public Properties
 * 
 * [list]
 * 
 * 
 * Public Methods
 * 
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Antonella Giachino (antonella.giachino@eng.it)
 */
Ext.ns("Sbi.engines.chart");

Sbi.engines.chart.MasterDetailChartPanel = function(config) {
	
	var defaultSettings = {
	};

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);

	//constructor
	Sbi.engines.chart.MasterDetailChartPanel.superclass.constructor.call(this, c);
	this.init();

};

Ext.extend(Sbi.engines.chart.MasterDetailChartPanel, Sbi.engines.chart.HighchartsPanel, {
	
   	  masterChart: null
    , detailChart: null
    , detailSerieData:  null
	, detailCategoryData: null
	, detailStart: null

	, createChart: function () {
		this.chartConfig.chart.renderTo = this.chartConfig.divId + '__master';
		//disable exporting buttons
		var exp = {};
		exp.enabled = false;
		this.chartConfig.exporting = exp;
		//disable credits information
		var credits = {};
		credits.enabled = false;
		this.chartConfig.credits = credits;
		this.enableDrillEvents(this.chartConfig);
		//gets series values and adds theme to the config
		var seriesNode = [];
		//looks for js function		
		if (this.chartConfig.plotOptions){
			if(this.chartConfig.plotOptions.pie && this.chartConfig.plotOptions.pie.dataLabels){
				var formatter = this.getFormatterCode(this.chartConfig.plotOptions.pie.dataLabels.formatter);
				if (formatter !== undefined && formatter !== null){
					this.chartConfig.plotOptions.pie.dataLabels.formatter = formatter;
				}
			}
			if(this.chartConfig.plotOptions.series){
				var formatter = this.getFormatterCode(this.chartConfig.plotOptions.series.formatter);
				if (formatter != null){
					this.chartConfig.plotOptions.series.formatter = formatter;
				}
			}
		}
		//defines tooltip
		if(this.chartConfig.tooltip){
			var formatter = this.getFormatterCode(this.chartConfig.tooltip.formatter);
			if (formatter != null){
				this.chartConfig.tooltip.formatter = formatter;
			}
		}

		//defines series data
		if (this.chartConfig.series !== undefined ){
			var serieValue = this.chartConfig.series;
			if (Ext.isArray(serieValue)){
				var seriesData =  {};
				var str = "";
				for(var i = 0; i < serieValue.length; i++) {
					seriesData = serieValue[i];					
					seriesData.data = this.getSeries(serieValue[i].alias);//values from dataset
					seriesNode.push(seriesData);
				}
			}
		}else if (this.chartConfig.plotOptions){ 
			seriesData = this.chartConfig.plotOptions.series;//other attributes too
			seriesData.data = this.getSeries();//values from dataset
			seriesNode.push(seriesData);
		}

		this.chartConfig.series = seriesNode;
		//get categories for each axis from dataset
		if(this.chartConfig.xAxis != undefined){
			//if multiple X axis
			if(this.chartConfig.xAxis.length != undefined){
				//gets categories values and adds theme to the config	
				var categoriesX = this.getCategoriesX();
				if(categoriesX == undefined || categoriesX.length == 0){
					delete this.chartConfig.xAxis;
					for(var j =0; j< this.categoryAliasX.length; j++){
						this.chartConfig.xAxis[j].categories = categoriesX[j];
					}
					
				}
				//else keep templates ones

			}else{
				//single axis
				var categoriesX = this.getCategoriesX();
				if(categoriesX != undefined && categoriesX.length != 0){
					this.chartConfig.xAxis.categories = categoriesX[0];
				}
				
			}
		}
		if(this.chartConfig.yAxis != undefined){
			//if multiple Y axis
			if(this.chartConfig.yAxis.length != undefined){
				//gets categories values and adds theme to the config	
				var categoriesY = this.getCategoriesY();
				if(categoriesY == undefined || categoriesY.length == 0){
					delete this.chartConfig.yAxis;
					for(var j =0; j< this.categoryAliasY.length; j++){
						this.chartConfig.yAxis[j].categories = categoriesY[j];
					}
					
				}
				//else keep templates ones
			}else{
				//single axis
				var categoriesY = this.getCategoriesY();
				if(categoriesY != undefined && categoriesY.length != 0){
					this.chartConfig.yAxis.categories = categoriesY[0];
				}
				
			}
		}
		//defines utc values if necessary for the master
		if (this.chartConfig.plotOptions.series.pointStart !== undefined){
			this.chartConfig.plotOptions.series.pointStart = this.getUTCValue(this.chartConfig.plotOptions.series.pointStart);
		}
		if (this.chartConfig.xAxis.plotBands !== undefined){
			var arPlotbands = [];
			var plotBand = this.chartConfig.xAxis.plotBands[0];
			
			plotBand.from = this.getUTCValue(plotBand.from);
			plotBand.to = this.getUTCValue(plotBand.to);
			arPlotbands.push(plotBand);
			delete this.chartConfig.xAxis.plotBands;
			this.chartConfig.xAxis.plotBands = arPlotbands;
			
			if (plotBand.defaultMax !== undefined){
				this.chartConfig.detailChart.plotOptions.series.detailMaxPlotBand = this.getUTCValue(plotBand.defaultMax);
			}
		}
		
		//getDetailData
		this.detailSerieData = this.getDetailSerieData(this.chartConfig.detailChart.plotOptions.series.alias);
		//getDetailCategory
		this.detailCategoryData = this.getDetailCategoryData(this.chartConfig.detailChart.xAxis.alias);
		//getTemplateData
		this.detailTemplate = this.getDetailChartTemplate();
		//defines master events
		this.createMasterEvents(this.chartConfig, this.detailSerieData , this.detailCategoryData, this.detailTemplate);
		
		//alert(this.chartConfig.toSource());
		this.chart = new Highcharts.Chart(this.chartConfig);
		
	}

	, createMasterEvents: function(config, detailSerieData, detailCategoryData, detailTemplate ) {
		if (config.detailChart.plotOptions.series.pointStart !== undefined){
			this.detailStart = this.getUTCValue(config.detailChart.plotOptions.series.pointStart);
			config.detailChart.plotOptions.series.pointStart = this.detailStart;
		}	
		//gets max value for plot bands default
		var events = {
		
			// on load of the master chart, add the detail chart
			load: function() {
				// reverse engineer the last part of the data
				this.detailCategoryData = detailCategoryData;
				
				var tmpDetailSerieData = [];

				for (k in detailCategoryData) {
				    var cats = this.detailCategoryData[k];
				    if (Ext.isArray(cats)){
				    	for(var i =0; i< cats.length; i++){
				    		var cValues = cats[i];
				    		if (cValues >= this.detailStart) {
						    	tmpDetailSerieData.push(cValues);
						    }
    					}
				    }
				}
				this.detailSerieData = tmpDetailSerieData;
				this.detailCategoryData = detailCategoryData;

				
				// create a detail chart referenced by a global variable
				this.detailChart = new Highcharts.Chart(detailTemplate || {});
			}
			// listen to the selection event on the master chart to update the 
			// extremes of the detail chart
		  , selection: function(event) {
				var extremesObject = event.xAxis[0],
					min = extremesObject.min,
					max = extremesObject.max,
					xAxis = this.xAxis[0],
					tmpDetailData = [];
				
				// reverse engineer the last part of the data
				this.detailCategoryData = detailCategoryData;
				this.detailSerieData = detailSerieData;
				
				//TODO : optimize the acquisiition of the values, too for multiple axis
				//for (k in this.detailChart.series[0].data) {			
				/*
				for (k in this.bckChartSerie) {
				    var point = this.bckChartSerie[k];
				    if (point.x > min && point.x < max) {
				    	tmpDetailData.push({
							x: point.x,
							y: point.y
						});
				    }
				}
				*/
				for (k in detailCategoryData) {
				    var cats = this.detailCategoryData[k];
				    //var sers = this.detailSerieData[k];
				    if (Ext.isArray(cats)){
				    	for(var i =0; i< cats.length; i++){
				    		var xValue = cats[i];
				    		//var yValue = sers[i];
				    		var yValue = this.detailSerieData[i];
				    		if (xValue >=  min && xValue < max) {
				    			tmpDetailData.push({
									x: xValue,
									y: yValue
								});
						    }
    					}
				    }
				}
				// move the plot bands to reflect the new detail span
				xAxis.removePlotBand('mask-before');
				xAxis.addPlotBand({
					id: 'mask-before',
					from: config.detailChart.plotOptions.series.pointStart,
					to: min,
					color: 'rgba(0, 0, 0, 0.2)'
				});
				xAxis.removePlotBand('mask-after');
				xAxis.addPlotBand({
					id: 'mask-after',
					from: max,
					to: config.detailChart.plotOptions.series.detailMaxPlotBand,
					color: 'rgba(0, 0, 0, 0.2)'
				});
				this.detailChart.series[0].setData(tmpDetailData);
				
				return false;
			}
		}
		
		config.chart.events = events;
		
	}

	, getDetailChartTemplate: function(){
		var chartTemplate = {};
		
		var chartOptions = {
					borderWidth: 0,
					backgroundColor: null,
					renderTo: this.chartConfig.divId + '__detail', 
					//height: 330,
					margin: [80, 30, 20, 80],
					style: {
						//position: 'absolute'
					}
		};
		chartTemplate.chart = chartOptions;
		
		//disable exporting buttons
		var exp = {};
		exp.enabled = false;
		chartTemplate.exporting = exp;
		
		//disable credits information
		var credits = {};
		credits.enabled = false;
		chartTemplate.credits = credits;
		
		//sets other detail chart properties
		chartTemplate.title = this.chartConfig.detailChart.title || {};
		chartTemplate.subtitle = this.chartConfig.detailChart.subtitle || {};
		chartTemplate.xAxis = this.chartConfig.detailChart.xAxis || {};
		chartTemplate.xAxis.category = this.detailCategoryData || [];
		chartTemplate.yAxis = this.chartConfig.detailChart.yAxis || {};	
		var defaultTooltip = {
			formatter: function() {
				return '<b>'+ (this.point.name || this.series.name) +'</b><br/>'+
					Highcharts.dateFormat('%A %B %e %Y', this.x) + ':<br/>'+
					Highcharts.numberFormat(this.y, 2);
			}
		}; 
		chartTemplate.tooltip = this.chartConfig.detailChart.tooltip || defaultTooltip;
		chartTemplate.legend = this.chartConfig.detailChart.legend || {};
		chartTemplate.plotOptions = this.chartConfig.detailChart.plotOptions || {};
		
		if (chartTemplate.plotOptions.series.pointStart !== undefined){
			chartTemplate.plotOptions.series.pointStart = eval(chartTemplate.plotOptions.series.pointStart);
		}
		
		chartTemplate.series = [{
			name: 'Stores cost',
			data: this.detailSerieData
		}];
		return chartTemplate;
	}
	
	, getDetailCategoryData: function(alias){
		this.setCategoryAliasX(this.chartConfig.detailChart);
		var detailCategoryData = this.getCategoriesX();
		return detailCategoryData;
	} 
	
	, getDetailSerieData: function(alias){
		var detailSerieData = this.getSeries(alias);
		return detailSerieData;
	} 
	
	, getUTCValue: function(elem){
		var elemValue = elem;
		if (typeof elemValue === "string" && elemValue.indexOf("UTC") >= 0){
			elemValue = elemValue.replace(/"/gi, "");
			elemValue = eval(elemValue);
		}
		return elemValue;
	}

});