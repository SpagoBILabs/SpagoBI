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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.chart.Chart.CHART_URL = '/SpagoBIConsoleEngine/swf/yuichart/charts.swf';

Ext.ns("Sbi.console");

Sbi.console.ChartWidget = function(config) {
	
		var defaultSettings = {
			height: 170,
			dataset: 'testStore'
	        , widgetConfig: {
	           	type: 'chart.ext.line'
	        	, xField: 'name'
	            , yField: 'visits'
	        }
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.chartWidget) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.chartWidget);
		}
		
		var c = Ext.apply(defaultSettings, config || {});		
		if(c.dataset) {
			if((typeof c.dataset) === 'string' ) {
				c.storeName = c.dataset;
			} else {
				c.storeConfig = c.dataset;
			}
			delete c.dataset;
		}		
		Ext.apply(this, c);

		// constructor
		Sbi.console.ChartWidget.superclass.constructor.call(this, c);
		
};

Ext.extend(Sbi.console.ChartWidget, Sbi.console.Widget, {
    
	
	store: null
	, storeName: null
	, storeConfig: null
	
	, widgetConfig: null
	, chart: null
	
	, YUI_CHART_LINE: 'chart.ext.line'
	, YUI_CHART_BAR: 'chart.ext.bar'
	, YUI_CHART_PIE: 'chart.ext.pie'
	, SBI_CHART_SPEEDOMETER: 'chart.sbi.speedometer'
	, SBI_CHART_LIVELINES: 'chart.sbi.livelines'
	, SBI_CHART_MULTILEDS: 'chart.sbi.multileds'
	, SBI_CHART_SEMAPHORE: 'chart.sbi.semaphore'
	, OFC_CHART_BAR: 'chart.of.bar'
	, FCF_CHART_BAR: 'chart.fcf.bar'
		
	
    //  -- public methods ---------------------------------------------------------
    
   
    
    //  -- private methods ---------------------------------------------------------
	
	

	, onRender: function(ct, position) {
		Sbi.console.ChartWidget.superclass.onRender.call(this, ct, position);
		
		if(!this.store) {
			if(this.storeName) {
				this.store = this.getStore(this.storeName);
			} else if(this.storeConfig) {
				alert('ChartWidget: sorry unable to create a private dataset from config');
			} else {
				return;
			}
		}

		if(this.store.proxy) {
			this.store.load({
				params: {}, 
				callback: function(){this.store.ready = true;}, 
				scope: this, 
				add: false
			});
		}
		
		this.chart = null;
		if(this.store.ready === true) {
			this.initChart();
		} else {
			this.store.on('load', this.initChart, this);
		}
			
		
	}

	, initChart: function() {
		if(this.chart == null) {
			this.store.un('load', this.initChart, this);
		}
		
		this.chart = this.createChart(this.widgetConfig);
		
		this.items.each( function(item) {
			this.items.remove(item);
	        item.destroy();           
	    }, this);   
		
		if(this.chart !== null) {
			this.add(this.chart);
			this.doLayout();
		}
	}
    
	, createChart: function(chartConfig) {
		var chart = null;
		
		chartConfig = chartConfig || {};
		
		var chartType = chartConfig.type;
			
	
		if(chartType === this.YUI_CHART_LINE) {
			chart = this.createLineChart(chartConfig);
		} else if(chartType === this.YUI_CHART_BAR) {
			chart = this.createBarChart(chartConfig);
		} else if(chartType === this.YUI_CHART_PIE){
			chart = this.createPieChart(chartConfig);
		} else if(chartType === this.SBI_CHART_SPEEDOMETER 
				|| chartType === this.SBI_CHART_LIVELINES
				|| chartType === this.SBI_CHART_MULTILEDS
				|| chartType === this.SBI_CHART_SEMAPHORE){
			
			chart = this.createSpagoBIChart(chartConfig);
			
		} else if(chartType === this.OFC_CHART_BAR){
			chart = this.createOFBarChart(chartConfig);
		} else if(chartType === this.FCF_CHART_BAR){
			chart = this.createFCFBarChart(chartConfig);
		} else {
			Sbi.Msg.showError('Chart type [' + chartType + '] not supported by [ChartWidget]');
		}
		
		return chart;
	}
	
	, createSpagoBIChart: function(chartConfig) {
		
		chartConfig.store = this.store;
		chartConfig.xtype = chartConfig.type;
		delete chartConfig.type;
		
		return new Ext.Panel({
			layout:'fit'
		    , height: this.height	
		    , items: [chartConfig]
		});		
	}
	
	, createLineChart: function(chartConfig) {
		
		// type attribute is reseved 
		delete chartConfig.type;
		var c = Ext.apply({}, chartConfig, {
			xtype: 'linechart'
			, xField: 'category'
            , yField: 'value'
	        , store: this.store
			, listeners: {
				itemclick: function(o){
					//var rec = this.store.getAt(o.index);
					//alert('Item Selected', 'You chose ' + rec.get('name'));
				}
			}
		});
		
		c.xField = this.getFieldNameByAlias(c.xField);
		c.yField = this.getFieldNameByAlias(c.yField);
		
		return new Ext.Panel({
	        layout:'fit'
	        , height: this.height
	        , items: c
	    });
	}
	
	
	, createBarChart: function(chartConfig) {
		// type attribute is reseved 
		delete chartConfig.type;
		var c = Ext.apply({}, chartConfig, {
			xtype: 'columnchart'
			, xField: 'category'
	        , yField: 'value'
			, store: this.store
			, listeners: {
				itemclick: function(o){
					var rec = this.store.getAt(o.index);
					alert('Item Selected', 'You chose ' + rec.get('column-2'));
				}
			}
		});
		
		c.xField = this.getFieldNameByAlias(c.xField);
		c.yField = this.getFieldNameByAlias(c.yField);
		
		
		
		return new Ext.Panel({
			layout:'fit'
		    , height: this.height	
		    , items: c
		});		
	}
	
	, createPieChart: function(chartConfig) {
		// type attribute is reseved 
		delete chartConfig.type;
		
		var c = Ext.apply({}, chartConfig, {
			xtype: 'piechart'
			, dataField: 'value'
	        , categoryField: 'category'
			, store: this.store
	        , extraStyle:
	         {
	         	legend:
	            {
	            	display: 'bottom',
	                padding: 5,
	                font:
	                {
	                	family: 'Tahoma',
	                    size: 13
	                }
	            }
	         }
		});
		
		c.categoryField = this.getFieldNameByAlias(c.categoryField);
		c.dataField = this.getFieldNameByAlias(c.dataField);
		
		
		return new Ext.Panel({
			layout:'fit'	
			, height: this.height	
			, items: c
		});
	}
	
	, createOFBarChart: function(chartConfig) {
		
		return new Ext.Panel({
			layout:'fit'
		    , height: this.height	
		    , items: [new Sbi.chart.OpenFlashChart()]
		});		
	}
	
	, createFCFBarChart: function(chartConfig) {
		
		return new Ext.Panel({
			layout:'fit'
		    , height: this.height	
		    , items: [new Sbi.chart.FusionFreeChart()]
		});		
	}
	
	
	
	, getFieldNameByAlias: function(alias) {
		var fname;
		
		fname = alias;
		if(this.store.getFieldNameByAlias) {
			fname = this.store.getFieldNameByAlias(alias);
			if(!fname) {
				Sbi.Msg.showError(
					'Dataset [' + this.storeId + '] does not contain a field whose alias is  [' + alias + ']', 
					'Error in chart configuration'
				);
				fname = alias;
			}
		}
		
		return fname;
	}
    
});