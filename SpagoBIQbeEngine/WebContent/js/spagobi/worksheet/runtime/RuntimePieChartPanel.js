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
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimePieChartPanel = function(config) {
	
	var defaultSettings = {
	};

	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimePieChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimePieChartPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>'
	});
	
	Sbi.worksheet.runtime.RuntimePieChartPanel.superclass.constructor.call(this, c);
	
	this.init();
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimePieChartPanel, Sbi.worksheet.runtime.RuntimeGenericChartPanel, {
	
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
			exporting : {
				//url : this.services['exportChart']
				buttons : {
					exportButton : {enabled : false}
		  			, printButton : {enabled : false}
				}
			},
			chart : {
				renderTo : this.chartDivId,
				spacingTop : 25,
				spacingRight : 75,
				spacingBottom : 25,
				spacingLeft : 75
			},
			plotOptions: this.getPlotOptions(),
			tooltip: {
				enabled: true,
				formatter: this.getTooltipFormatter()
			},
			title : {
				text : ''
			},
			series : this.getSeries(),
			colors : this.getColors()
		});
	}
	
//	, getTooltipFormatter: function () {
//		var showPercentage = this.chartConfig.showpercentage;
//		var toReturn = function () {
//			var tooltip = '<b>'+ this.point.name +'</b><br/>'+ this.series.name +': '+ this.y;
//			if (showPercentage) {
//				tooltip += ' ( ' + Ext.util.Format.number(this.percentage, '0.00') + ' %)';
//			}
//			return  tooltip;
//		}
//		return toReturn;
//	}
	
	, getColors : function () {
		return this.chartConfig.colors;
	}
	
	, getPlotOptions : function () {
		var plotOptions = null;
		plotOptions = {
			pie: {
				dataLabels: {
					enabled: (this.chartConfig.showvalues !== undefined) ? this.chartConfig.showvalues : true,
					formatter: this.getDataLabelsFormatter()
				},
				showInLegend: (this.chartConfig.showlegend !== undefined) ? this.chartConfig.showlegend : true
			}
		};
		return plotOptions;
	}
	
//	, getDataLabelsFormatter: function () {
//		var showPercentage = this.chartConfig.showpercentage;
//		alert(this.chartConfig.toSource());
//		var toReturn = function () {
//			var tooltip = '<b>'+ this.point.name +'</b>: '+ this.y;
//			if (showPercentage) {
//				tooltip += ' ( ' + Ext.util.Format.number(this.percentage, '0.00') + ' %)';
//			}
//			return  tooltip;
//		}
//		return toReturn;
//	}
	
	, getSeries: function () {
		var superSeries = Sbi.worksheet.runtime.RuntimePieChartPanel.superclass.getSeries.call(this);
		var theSerie = superSeries[0];
		var categories = this.getCategories();
		var series = [];
		var serie = {};
		serie.type = 'pie';
		serie.name = theSerie.name;
		serie.data = [];
		var i = 0;
		for (; i < categories.length; i++) {
			serie.data.push([categories[i], theSerie.data[i]]);
		}
		series.push(serie);
		return series;
		
	}
	
});