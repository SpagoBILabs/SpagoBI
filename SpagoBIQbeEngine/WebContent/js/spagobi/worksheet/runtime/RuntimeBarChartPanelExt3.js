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
	
	, getByteArraysForExport: function(){
		var byteArrays = new Array();
		for(var i=0; i<this.charts; i++){
			byteArrays.push((this.charts[i]).exportPNG());
		}	
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
		
		
		items.region = 'center';
		var barChartPanel = this.getChartExt3(this.chartConfig.orientation === 'horizontal', items);
	
		//Its a workaround because if you change the display name the chart is not able to write the tooltips
        
		var exportChartPanel  = new Ext.Panel({
			border: false,
			region: 'north',
			height: 20,
			html: '<div style=\"padding-top: 5px; padding-bottom: 5px; font: 11px tahoma,arial,helvetica,sans-serif;\">'+LN('sbi.worksheet.runtime.worksheetruntimepanel.chart.includeInTheExport')+'</div>'
		});
		
		var chartConf ={
			renderTo : this.chartDivId,
			layout: 'border',
			bodyStyle: 'height: 100%; width: 100%;',
			border: false,
			items: [barChartPanel,exportChartPanel]
		};
		
		this.on('contentclick', function(event){
			this.byteArrays=new Array();
			try{
				this.byteArrays.push(barChartPanel.exportPNG());	
			}catch(e){}

			exportChartPanel.update('');
			this.headerClickHandler(event,null,null,barChartPanel, this.reloadJsonStoreExt3, this);
		}, this);
		
		
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
			
//			if(this.chartConfig.type == 'percent-stacked-barchart'){
//				serie.displayName =  (serieNames[i]);//if percent doesn't matter the scale 
//			}else{
				serie.displayName =  this.formatLegendWithScale(serieNames[i]);
//			}

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
					z['seriesflatvalue'+j] = z['series'+j];
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
	
	, getTooltipFormatter: function () {
	
		var chartType = this.chartConfig.designer;
		var allSeries = this.chartConfig.series;
		var type  = this.chartConfig.type;
		var horizontal = this.chartConfig.orientation === 'horizontal';
		
		var getFormattedValueExt3 = this.getFormattedValueExt3;
		
		var toReturn = function (chart, record, index, series) {
			var valuePrefix= '';
			
			var value = getFormattedValueExt3(chart, record, series, chartType, allSeries, type, horizontal);
		
			valuePrefix = series.displayName+'\n'+record.data.categories+'\n';

			return valuePrefix+value;
			
		};
		return toReturn;
	}
	
	//Format the value to display
	, getFormattedValueExt3: function (chart, record, series, chartType, allSeries, type, horizontal){
		var theSerieName  = series.displayName;
		var value ;
		var serieDefinition;

		if(type != 'percent-stacked-barchart'){
			if(horizontal){
				value =  record.data[series.xField];
			}else{
				value = record.data[series.yField];
			}
		}else{
			//value = Ext.util.Format.number(record.data[series.xField], '0.00');
			if(horizontal){
				value = record.data['seriesflatvalue'+series.xField.substring(series.xField.length-1)];		        
			}else{
				value = record.data['seriesflatvalue'+series.yField.substring(series.yField.length-1)];
			}
			
			
		}
		
		
		// find the serie configuration
		var i = 0;
		for (; i < allSeries.length; i++) {
			//substring to remove the scale factor
			if (allSeries[i].seriename === theSerieName.substring(0, allSeries[i].seriename.length)) {
				serieDefinition = allSeries[i];
				break;
			}
		}

		//if(type != 'percent-stacked-barchart'){
			// format the value according to serie configuration
			value = Sbi.qbe.commons.Format.number(value, {
	    		decimalSeparator: Sbi.locale.formats['float'].decimalSeparator,
	    		decimalPrecision: serieDefinition.precision,
	    		groupingSeparator: (serieDefinition.showcomma) ? Sbi.locale.formats['float'].groupingSeparator : '',
	    		groupingSize: 3,
	    		currencySymbol: '',
	    		nullValue: ''
			});
//		}else{
//			value = value + '%';
//		}
		// add suffix
		if (serieDefinition.suffix !== undefined && serieDefinition.suffix !== null && serieDefinition.suffix !== '') {
			value = value + ' ' + serieDefinition.suffix;
		}

		return value;
	}

});