/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeBarChartPanelExt3 = function(config) {
	var defaultSettings = {
	};

	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeBarChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeBarChartPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>'
	});
	Sbi.worksheet.runtime.RuntimeBarChartPanelExt3.superclass.constructor.call(this, c);
	this.init();
};

Ext.extend(Sbi.worksheet.runtime.RuntimeBarChartPanelExt3, Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3, {


	chartDivId : null
	, chart : null
	, chartConfig : null 	
	
	, init : function () {
		this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}
	
	
	, createChart: function () {
		var percent = ((this.chartConfig.type).indexOf('percent')>=0);
		var storeObject = this.getJsonStoreExt3(percent);
		var colors = this.getColors();
		var extraStyle ={};
		
		var items = {
				store: storeObject.store,
				extraStyle: extraStyle,
				hiddenseries: new Array(),
				horizontal: this.chartConfig.orientation === 'horizontal'
		};
		
		//set the height if ie
    	if(Ext.isIE){
    		items.height = this.ieChartHeight;
    	}

		
		if(this.chartConfig.orientation === 'horizontal'){
			items.yField = 'categories';
			items.series = this.getChartSeriesExt3(storeObject.serieNames, colors, true);
			
			//if percent stacked set the max of the axis
			if(percent){
				this.setPercentageStyleExt3(items, true);
			}
		}else{
			items.xField = 'categories';
			items.series = this.getChartSeriesExt3(storeObject.serieNames, colors);
			
			//if percent stacked set the max of the axis
			if(percent){
				this.setPercentageStyleExt3(items, false);
			}
		}
		
		this.addChartConfExt3(items);
		
		var barChartPanel = this.getChartExt3(this.chartConfig.orientation === 'horizontal', items);
		this.on('contentclick', function(event){
			this.headerClickHandler(event,null,null,barChartPanel, this.reloadJsonStoreExt3, this);
		}, this);
		
	
		//Its a workaround because if you change the display name the chart is not able to write the tooltips
		if(this.chartConfig.type != 'percent-stacked-barchart'){
			if(this.chartConfig.orientation === 'horizontal'){
				barChartPanel.tipRenderer = function(chart, record, index, series){
		            return series.displayName+'\n'+record.data.categories+'\n'+ record.data[series.xField];
		        };
			}else{
				barChartPanel.tipRenderer = function(chart, record, index, series){
		            return series.displayName+'\n'+record.data.categories+'\n'+ record.data[series.yField];
		        };
			}
		}
        
		var chartConf ={
			renderTo : this.chartDivId,
			layout: 'fit',
			border: false,
			items: barChartPanel
		};
		
		new Ext.Panel(chartConf);

	}
	
	
	, setPercentageStyleExt3 : function(chart, horizontal){
		var axis =  new Ext.chart.NumericAxis({
			stackingEnabled: true,
            minimum: 0,
            maximum: 100
		});
		
		if(horizontal){
			chart.xAxis = axis;
		}else{
			chart.yAxis = axis;
		}
		
		chart.tipRenderer = function(chart, record, index, series){
            return series.displayName+'\n'+record.data.categories+'\n'+ Ext.util.Format.number(record.data[series.xField], '0.00') + '%';
        };
	}
		
	, getChartExt3 : function (horizontal, config) {
		if(horizontal){
			if(this.chartConfig.type == 'stacked-barchart' || this.chartConfig.type == 'percent-stacked-barchart'){
				return new Ext.chart.StackedBarChart(config);
			}else{
				return new Ext.chart.BarChart(config);
			}
		} else {
			if(this.chartConfig.type == 'stacked-barchart' || this.chartConfig.type == 'percent-stacked-barchart'){
				return new Ext.chart.StackedColumnChart(config);
			}else{
				return new Ext.chart.ColumnChart(config);
			}
		}
	}

	
	, getChartSeriesExt3: function(serieNames, colors, horizontal){
		var seriesForChart = new Array();
		for(var i=0; i<serieNames.length; i++){
			var serie = {	
	                style: {}
			};
			
			if(this.chartConfig.type == 'percent-stacked-barchart'){
				serie.displayName =  (serieNames[i]);//if percent doesn't matter the scale 
			}else{
				serie.displayName =  this.formatLegendWithScale(serieNames[i]);
			}

			if(horizontal){
				serie.xField = 'series'+i;
			}else{
				serie.yField = 'series'+i;
			}
			
			if(colors!=null){
				serie.style.color= colors[i];
			}
			
			seriesForChart.push(serie);
		}
		return seriesForChart;
	}
	
	//reload the store after hide a series
	, reloadJsonStoreExt3: function(chart,reloadCallbackFunctionScope ){
		var oldDataStore= chart.store;
		var hiddenseries= chart.hiddenseries;
		var percent = ((reloadCallbackFunctionScope.chartConfig.type).indexOf('percent')>=0);
		
		if(percent){
			var series = reloadCallbackFunctionScope.getSeries();
			var categories = reloadCallbackFunctionScope.getCategories();
			
			var data = new Array();
			var fields = new Array();
			var serieNames = new Array();

			for(var i=0; i<categories.length; i++){
				var z = {};
				var seriesum = 0;
				for(var j=0; j<series.length; j++){
					z['series'+j] = ((series[j]).data)[i];
					if(hiddenseries.indexOf(j)<0){
						seriesum = seriesum + parseFloat(((series[j]).data)[i]);
					}
				}
				for(var j=0; j<series.length; j++){
					z['series'+j] = (z['series'+j]/seriesum)*100;;
				}
				z['seriesum'] = seriesum;
				z['categories'] = categories[i];
				data.push(z);
			}
			oldDataStore.loadData(data);
		}else{
			chart.refresh();
		}

	}

});