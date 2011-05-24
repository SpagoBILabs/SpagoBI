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

Sbi.engines.chart.BarChartPanel = function(config) {

	var defaultSettings = {
	};

	/*riattivare...
	if (Sbi.settings  && Sbi.engines.chart.BarChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.engines.chart.BarChartPanel);
	}
*/
	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>'
	});
	
	// constructor
	Sbi.engines.chart.BarChartPanel.superclass.constructor.call(this, c);

	this.init();
};

Ext.extend(Sbi.engines.chart.BarChartPanel, Sbi.engines.chart.GenericChartPanel, {
	
	chartDivId : null
	, chart : null
	, chartConfig : null // mandatory object to be passed as a property of the constructor input object.						
	

	, init : function () {
		this.loadChartData(this.chartConfig);
	}
	/* orig
	, createChart: function () {
		  this.chart = new Highcharts.Chart({
			chart : {
				renderTo : this.chartDivId,
				defaultSeriesType : (this.chartConfig.orientation === 'horizontal') ?  'bar' : 'column'
			},
			plotOptions: this.getPlotOptions(),
			legend: {
				enabled: (this.chartConfig.showlegend !== undefined) ? this.chartConfig.showlegend : true
			},
			tooltip: {
				enabled: true
			},
			colors: this.getColors(),
			title : {
				text : ''
			},
			yAxis : {
				title : {
					text : ''
				}
			},
			xAxis : {
				categories : this.getCategories(),
				title : {
					text : this.chartConfig.category.alias
				}
			},
			series : this.getSeries()
		});
	}
	*/
	//TODO: come sarà: da gestire acquisizione dati e completamento config con dati

	, createChart: function () {
		this.chartConfig.chart.renderTo = "pippo";
		//gets series values and adds theme to the config
		var seriesNode = [];
		var seriesData = {};
		seriesData.data = this.getSeries();
		seriesNode.push(seriesData);
		this.chartConfig.series = seriesNode;

		//gets categories values and adds theme to the config
		delete this.chartConfig.xaxis;
		this.chartConfig.xAxis = {};
		this.chartConfig.xAxis.categories = this.getCategories();
		//alert("this.chartConfig : " + this.chartConfig.toSource());
		this.chart = new Highcharts.Chart(this.chartConfig);
		/*this.chart = new Highcharts.Chart({
				 legend:{verticalalign:"top", floating:true, layout:"vertical", enabled:true, align:"right", backgroundcolor:"#FFFFFF", y:60, x:-60}, 
				 yaxis:{}, 
				 xAxis:{categories:["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]}, 
				 title:{verticalalign:"top", floating:false, text:"My custom title", align:"left", style:{color:"#FF00FF", fontsize:"16px", fontweight:"bold"}, y:10, margin:50, x:70}, 
				 chart:{type:"bar", renderTo:"pippo"}, 
				 subtitle:{verticalalign:"bottom", floating:false, text:"My custom subtitle", align:"left", style:{color:"green", fontsize:"16px", fontweight:"bold"}, y:50, x:70}, 
				 dsId:76, dsLabel:"testHighcharts", dsTypeCd:"Query", dsPars:[], dsTransformerType:"", 
				 series:[{data:[29.9, 71.5, 106.4, 129.2, 144, 176, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4]}]
				});
		*/
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

});