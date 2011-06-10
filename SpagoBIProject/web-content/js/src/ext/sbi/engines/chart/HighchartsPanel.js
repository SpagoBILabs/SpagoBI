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
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.engines.chart");

Sbi.engines.chart.HighchartsPanel = function(config) {
	//alert("$$$ config: " + config.toSource());
	var defaultSettings = {
	};

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	// constructor
	Sbi.engines.chart.HighchartsPanel.superclass.constructor.call(this, c);

	this.init();
};

Ext.extend(Sbi.engines.chart.HighchartsPanel, Sbi.engines.chart.GenericChartPanel, {

	 chart : null
   , chartConfig : null // mandatory object to be passed as a property of the constructor input object.						


	, init : function () {
		/*
		//sets categoryX and categoryY
		var totalCharts = []; //for reset
		totalCharts = this.chartConfig.charts || [];
		if (totalCharts.length === 0 && this.chartConfig !== undefined){
			//mono chart, retrocompatibility
			totalCharts.push(this.chartConfig);
		}
		for (c in totalCharts){
			this.setCategoryAliasX(totalCharts[c]);
			this.setCategoryAliasY(totalCharts[c]);
			this.setSerieAlias(totalCharts[c]);
		}
		*/
		//gets dataset values (at the moment one dataset for all charts in a document)
		var dataConfig = this.chartConfig;
		if (this.chartConfig.charts){			
			dataConfig =  Ext.apply( this.chartConfig.charts[0], dataConfig);
			delete dataConfig.charts;
		}
		this.loadChartData(dataConfig);
	}

	, createChart: function () {
		var totalCharts = []; //for reset
		totalCharts = this.chartConfig.charts || [];
		if (totalCharts.length === 0 && this.chartConfig !== undefined){
			//mono chart, retrocompatibility
			totalCharts.push(this.chartConfig);
		}
		for (c in totalCharts){
			var singleChartConfig = this.chartConfig;
			singleChartConfig =  Ext.apply( totalCharts[c], singleChartConfig);
			delete singleChartConfig.charts;
			singleChartConfig.chart.renderTo = singleChartConfig.divId + '__' + c + '';
			//disable exporting buttons
			var exp = {};
			exp.enabled = false;
			singleChartConfig.exporting = exp;
			//disable credits information
			var credits = {};
			credits.enabled = false;
			singleChartConfig.credits = credits;
			this.enableDrillEvents(singleChartConfig);
			//gets series values and adds theme to the config
			var seriesNode = [];
			//looks for js function		
			if (singleChartConfig.plotOptions){
				if(singleChartConfig.plotOptions.pie && singleChartConfig.plotOptions.pie.dataLabels){
					var formatter = this.getFormatterCode(singleChartConfig.plotOptions.pie.dataLabels.formatter);
					if (formatter !== undefined && formatter !== null){
						singleChartConfig.plotOptions.pie.dataLabels.formatter = formatter;
					}
				}
				if(singleChartConfig.plotOptions.series){
					var formatter = this.getFormatterCode(singleChartConfig.plotOptions.series.formatter);
					if (formatter != null){
						singleChartConfig.plotOptions.series.formatter = formatter;
					}
				}
			}
			//defines tooltip
			if(singleChartConfig.tooltip){
				var formatter = this.getFormatterCode(singleChartConfig.tooltip.formatter);
				if (formatter != null){
					singleChartConfig.tooltip.formatter = formatter;
				}
			}
	
			//defines series data
			if (singleChartConfig.series !== undefined ){
				var serieValue = singleChartConfig.series;
				if (Ext.isArray(serieValue)){
					var seriesData =  {};
					var str = "";
					for(var i = 0; i < serieValue.length; i++) {
						seriesData = serieValue[i];					
						seriesData.data = this.getSeries(serieValue[i].alias);//values from dataset
						seriesNode.push(seriesData);
					}
				}
			}else if (singleChartConfig.plotOptions){ 
				seriesData = singleChartConfig.plotOptions.series;//other attributes too
				seriesData.data = this.getSeries();//values from dataset
				seriesNode.push(seriesData);
			}
	
			singleChartConfig.series = seriesNode;
			//get categories for each axis from dataset
			if(singleChartConfig.xAxis != undefined){
				//if multiple X axis
				if(singleChartConfig.xAxis.length != undefined){
					//gets categories values and adds theme to the config	
					var categoriesX = this.getCategoriesX();
					if(categoriesX == undefined || categoriesX.length == 0){
						delete this.chartConfig.xAxis;
						for(var j =0; j< this.categoryAliasX.length; j++){
							singleChartConfig.xAxis[j].categories = categoriesX[j];
						}
						
					}
					//else keep templates ones
	
				}else{
					//single axis
					var categoriesX = this.getCategoriesX();
					if(categoriesX != undefined && categoriesX.length != 0){
						singleChartConfig.xAxis.categories = categoriesX[0];
					}
					
				}
			}
			if(singleChartConfig.yAxis != undefined){
				//if multiple Y axis
				if(singleChartConfig.yAxis.length != undefined){
					//gets categories values and adds theme to the config	
					var categoriesY = this.getCategoriesY();
					if(categoriesY == undefined || categoriesY.length == 0){
						delete this.chartConfig.yAxis;
						for(var j =0; j< this.categoryAliasY.length; j++){
							singleChartConfig.yAxis[j].categories = categoriesY[j];
						}
						
					}
					//else keep templates ones
				}else{
					//single axis
					var categoriesY = this.getCategoriesY();
					if(categoriesY != undefined && categoriesY.length != 0){
						singleChartConfig.yAxis.categories = categoriesY[0];
					}
					
				}
			}
			this.chart = new Highcharts.Chart(singleChartConfig);
			
		}
	}

	, getColors : function () {
		var colors = [];
		if (this.chartConfig !== undefined && this.chartConfig.series !== undefined && this.chartConfig.series.length > 0) {
			for (var i = 0; i < this.chartConfig.series.length; i++) {
				colors.push(this.chartConfig.series[i].color);
			}
		}
		return colors;
	}
	
	, getPlotOptions : function () {
		var plotOptions = null;
		if (this.chartConfig.orientation === 'horizontal') {
			plotOptions = {
				bar: {
					stacking: this.getStacking(),
					dataLabels: {
						enabled: (this.chartConfig.showvalues !== undefined) ? this.chartConfig.showvalues : true
					}
				}
			};
		} else {
			plotOptions = {
				column: {
					stacking: this.getStacking(),
					dataLabels: {
						enabled: (this.chartConfig.showvalues !== undefined) ? this.chartConfig.showvalues : true
					}
				}
			};
		}
		return plotOptions;
	}
	
	, getStacking : function () {
		switch (this.chartConfig.type) {
	        case 'side-by-side-barchart':
	        	return null;
	        case 'stacked-barchart':
	        	return 'normal';
	        case 'percent-stacked-barchart':
	        	return 'percent';
	        default: 
	        	alert('Unknown chart type!');
	        return null;
		}
	}

	, getFormatterCode: function (formatter){
		if (formatter === undefined || formatter === null) return null;
		
		var result;
		var formatterCode = "";
		//alert("formatter: " + formatter);
		switch (formatter) {
	        case 'name_percentage':
	        	formatterCode = "function (){return \'\<b\>\'+ this.series.name +\'\</b\>\<br/\>\'+ this.point.name +\': \'+ this.y +\' %\';}";	        	
	        	break;
	        case 'name':
	        	formatterCode = "function (){return \'\<b\>\'+ this.series.name +\'\</b\>\<br/\>\'+ this.point.name ;}";
	        	break;
	        case 'percentage':
	        	formatterCode = "function (){return \'\<b\>\'+ this.series.name +\'\</b\>\<br/\>\'+ this.y +\' %';}";
	        	break;
	        case 'x_y':
	        	//TODO : internazionalizzare messaggi
	        	formatterCode = "function (){return \'The value for \<b\>\'+ this.x +\'\</b\> is \<b\>\'+ this.y +\'\</b\>\';}";
	        	break;
	        default: 
	        	formatterCode = "function (){return " + formatter.replace(/"/gi, "\'") + ";}";
	        	break	       
		}
		
		//formatterCode = 'function (){return '+formatterCode+';}';
		result = eval(formatterCode);
		//result = eval("{ return this.y;}");
		//alert("result: " + result);
		return result;
	}
});