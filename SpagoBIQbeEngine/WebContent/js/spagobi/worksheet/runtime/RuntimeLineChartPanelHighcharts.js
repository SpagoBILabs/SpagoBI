/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

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

Sbi.worksheet.runtime.RuntimeLineChartPanelHighcharts = function(config) {
	
	var defaultSettings = {
	};

	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeLineChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeLineChartPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>'
	});
	
	Sbi.worksheet.runtime.RuntimeLineChartPanelHighcharts.superclass.constructor.call(this, c);
	
	this.init();
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeLineChartPanelHighcharts, Sbi.worksheet.runtime.RuntimeGenericChartPanel, {
	
	chartDivId : null
	, chart : null
	, chartConfig : null // mandatory object to be passed as a property of the constructor input object. The template is:
//							template: {
//								type:"stacked-linechart", 
//								colorarea:true, 
//								showvalues:true, 
//								showlegend:true, 
//								category:
//									{id:"it.eng.spagobi.SalesFact1998::customer(customer_id):fullname", alias:"Full Name", funct:"NONE", iconCls:"attribute", nature:"attribute"}, 
//								series:[
//									{id:"it.eng.spagobi.SalesFact1998:unitSales", alias:"Unit Sales", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Unit Sales", color:"#9220CD"}, 
//									{id:"it.eng.spagobi.SalesFact1998:storeSales", alias:"Store Sales", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Store Sales", color:"#624D0F"}
//								]
//							}
	
	
	, init : function () {
		this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}

	, createChart: function () {
		  var thisPanel = this;
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
				defaultSeriesType : (this.chartConfig.colorarea === true) ?  'area' : 'line',
				spacingTop : 25,
				spacingRight : 75,
				spacingBottom : 25,
				spacingLeft : 75
			},
			plotOptions: this.getPlotOptions(),
			legend: {
				enabled: (this.chartConfig.showlegend !== undefined) ? this.chartConfig.showlegend : true,
				labelFormatter: function() {
					return thisPanel.formatLegendWithScale(this.name)
				}
				
			},
			tooltip: {
				enabled: true,
				formatter: this.getTooltipFormatter()
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
			series : this.getSeries(),
			credits : {
				enabled : false
			}
		});
	}
	
	, getColors : function () {
		var colors = [];
		if (this.chartConfig !== undefined && this.chartConfig.series !== undefined && this.chartConfig.series.length > 0) {
			var i = 0;
			for (; i < this.chartConfig.series.length; i++) {
				colors.push(this.chartConfig.series[i].color);
			}
		}
		return colors;
	}
	
	, getPlotOptions : function () {
		var plotOptions = null;

		if (this.chartConfig.colorarea === true) {
			plotOptions = {
				area: {
					stacking: this.getStacking(),
					dataLabels: {
						enabled: (this.chartConfig.showvalues !== undefined) ? this.chartConfig.showvalues : true,
						formatter: this.getDataLabelsFormatter()
					}
				}
			};
		} else {
			plotOptions = {
				line: {
					stacking: this.getStacking(),
					dataLabels: {
						enabled: (this.chartConfig.showvalues !== undefined) ? this.chartConfig.showvalues : true,
						formatter: this.getDataLabelsFormatter()
					}
				}
			};
		}
		return plotOptions;
	}
	
	, getStacking : function () {
		switch (this.chartConfig.type) {
	        case 'side-by-side-linechart':
	        	return null;
	        case 'stacked-linechart':
	        	return 'normal';
	        case 'percent-stacked-linechart':
	        	return 'percent';
	        default: 
	        	alert('Unknown chart type!');
	        return null;
		}
	}

});