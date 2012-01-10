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
				extraStyle: extraStyle
		};

		
		if(this.chartConfig.orientation === 'horizontal'){
			items.yField = 'categories';
			items.series = this.getChartSeriesExt3(storeObject.serieNames, colors, true);
			items.xtype = this.getTypeExt3('barchart');
			//if percent stacked set the max of the axis
			if(percent){
				this.setPercentageStyleExt3(items, true);
			}
		}else{
			items.xField = 'categories';
			items.series = this.getChartSeriesExt3(storeObject.serieNames, colors);
			items.xtype =  this.getTypeExt3('columnchart');
			//if percent stacked set the max of the axis
			if(percent){
				this.setPercentageStyleExt3(items, false);
			}
		}
		
		this.addChartConfExt3(items);
		
		var chartConf ={
			renderTo : this.chartDivId,
			layout: 'fit',
			border: false,
			items: items
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
		
	, getTypeExt3 : function (type) {
		switch (this.chartConfig.type) {
	        case 'stacked-barchart':
	        	return 'stacked'+type;
	        case 'percent-stacked-barchart':
	        	return 'stacked'+type;
	        default: 
	        	return type;
	        return null;
		}
	}

	
	, getChartSeriesExt3: function(serieNames, colors, horizontal){
		var seriesForChart = new Array();
		for(var i=0; i<serieNames.length; i++){
			var serie = {	
	                displayName: serieNames[i],
	                style: {}
			};

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

});