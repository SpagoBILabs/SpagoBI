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
	
	this.on('afterlayout', this.onAfterLayout, this);
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimePieChartPanel, Sbi.worksheet.runtime.RuntimeGenericChartPanel, {
	
	chartDivId : null
	, chart : null
	, chartConfig : null // mandatory object to be passed as a property of the constructor input object. The template is:
//							template: {
//								type:"stacked-barchart", 
//								orientation:"horizontal", 
//								showvalues:true, 
//								showlegend:true, 
//								category:
//									{id:"it.eng.spagobi.SalesFact1998::customer(customer_id):fullname", alias:"Full Name", funct:"NONE", iconCls:"attribute", nature:"attribute"}, 
//								series:[
//									{id:"it.eng.spagobi.SalesFact1998:unitSales", alias:"Unit Sales", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Unit Sales", colour:"#9220CD"}, 
//									{id:"it.eng.spagobi.SalesFact1998:storeSales", alias:"Store Sales", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Store Sales", colour:"#624D0F"}
//								]
//							}
	
	
	, init : function () {
		//this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}

	, onAfterLayout: function () {
		this.removeListener('afterlayout', this.onAfterLayout, this);
		this.createChart();
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
					return '<b>'+ this.point.name +'</b>: '+ this.y;
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
		return this.chartConfig.colours;
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
		var superSeries = Sbi.worksheet.runtime.RuntimePieChartPanel.superclass.getSeries.call(this);
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