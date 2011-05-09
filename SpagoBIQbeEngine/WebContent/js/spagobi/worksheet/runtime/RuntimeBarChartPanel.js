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

Sbi.worksheet.runtime.RuntimeBarChartPanel = function(config) {
	
	var defaultSettings = {
	};

	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeBarChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeBarChartPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;">aaaa</div>'
	});
	
	Sbi.worksheet.runtime.RuntimeBarChartPanel.superclass.constructor.call(this, c);
	
	this.init();
	
	this.on('afterlayout', this.onAfterLayout, this);
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeBarChartPanel, Sbi.worksheet.runtime.RuntimeGenericChartPanel, {
	
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
		this.chartConfig = this.chartConfig || {};
		this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}

	, onAfterLayout: function () {
		this.removeListener('afterlayout', this.onAfterLayout, this);
		//this.createChart();
	}
	
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
			xAxis : {
				categories : this.getCategories(),
				title : {
					text : (this.chartConfig.orientation === 'vertical') ?  this.chartConfig.category.alias : ''
				}
			},
			yAxis : {
				title : {
					text : (this.chartConfig.orientation === 'horizontal') ?  this.chartConfig.category.alias : ''
				}
			},
			series : this.getSeries()
		});
	}
	
	, getColors : function () {
		var colors = [];
		if (this.chartConfig !== undefined && this.chartConfig.series !== undefined && this.chartConfig.series.length > 0) {
			for (var i = 0; i < this.chartConfig.series.length; i++) {
				colors.push(this.chartConfig.series[i].colour);
			}
		}
		return colors;
	}
	
	, getPlotOptions : function () {
		var plotOptions = null;
		if (this.chartConfig.orientation === 'horizontal') {
			plotOptions = {
				series: {
					stacking: this.getStacking()
				},
				bar: {
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