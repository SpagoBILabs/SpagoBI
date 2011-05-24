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

Sbi.engines.chart.PieChartPanel = function(config) {
	
	var defaultSettings = {
	};

	/*
	if (Sbi.settings && Sbi.settings.engines.chart.PieChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.engines.chart.PieChartPanel);
	}
*/
	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>'
	});
	
	Sbi.engines.chart.PieChartPanel.superclass.constructor.call(this, c);
	
	//test anto
	//this.init();
	//fine anto
	
};

Ext.extend(Sbi.engines.chart.PieChartPanel, Sbi.engines.chart.GenericChartPanel, {
	
	chartDivId : null
	, chart : null
	, chartConfig : null // mandatory object to be passed as a property of the constructor input object. The template is:
//							template: {
//								showvalues: true, 
//								showlegend: true,
//								category: {id:"it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productFamily", alias:"Product Family", funct:"NONE", iconCls:"attribute", nature:"attribute"},
//								series: [
//								    {id:"it.eng.spagobi.SalesFact1998:storeCost", alias:"Store Cost", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Store Cost", color:"#FFFFCC"}, 
//								    {id:"it.eng.spagobi.SalesFact1998:storeSales", alias:"Store Sales", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Store Sales", color:"#FFBBAA"}
//								],
//							    colors: ['#4572A7', '#DB843D', '#56AFC7', '#80699B', '#89A54E', '#AA4643', '#50B432'
//									    , '#1EA6E0', '#DDDF00', '#ED561B', '#64E572', '#9C9C9C', '#4EC0B1', "#C3198E"
//										, "#6B976B", "#B0AF3D", "#E7913A", "#82AEE9", "#7C3454", "#A08C1F", "#84D3D1", "#586B8A", "#B999CC"]
//							}
	
	
	, init : function () {
		this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}

	
	, createChart: function () {
		  this.chart = new Highcharts.Chart({
			chart : {
				renderTo : this.chartDivId
			},
			plotOptions: this.getPlotOptions(),
			tooltip: {
				enabled: true,
				formatter: function() {
	                return '<b>'+ this.point.name +'</b><br/>'+ this.series.name +': '+ this.y;
					//return '<b>'+ this.point.name +'</b>: '+ this.y;
				}
			},
			colors: this.getColors(),
			title : {
				text : ''
			},
			series : this.getSeries(),
			colors : this.getColors()
		});
	}
	
	, getColors : function () {
		return this.chartConfig.colors;
	}
	
	, getPlotOptions : function () {
		var plotOptions = null;
		plotOptions = {
			pie: {
				dataLabels: {
					enabled: (this.chartConfig.showvalues !== undefined) ? this.chartConfig.showvalues : true,
					formatter: function() {
						return '<b>'+ this.point.name +'</b>: '+ this.y;
					}
				},
				showInLegend: (this.chartConfig.showlegend !== undefined) ? this.chartConfig.showlegend : true
			}
		};
		return plotOptions;
	}
	
	, getSeries: function () {
		var superSeries = Sbi.engines.chart.PieChartPanel.superclass.getSeries.call(this);
		var theSerie = superSeries[0];
		var categories = this.getCategories();
		var series = [];
		var serie = {};
		serie.type = 'pie';
		serie.name = theSerie.name;
		serie.data = [];
		for (var i = 0; i < categories.length; i++) {
			serie.data.push([categories[i], theSerie.data[i]]);
		}
		series.push(serie);
		return series;
		
	}
	
});