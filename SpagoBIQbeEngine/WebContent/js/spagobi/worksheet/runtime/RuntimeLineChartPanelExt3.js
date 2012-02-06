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

Sbi.worksheet.runtime.RuntimeLineChartPanelExt3 = function(config) {
	
	var defaultSettings = {
		
	};

	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeLineChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeLineChartPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	this.addEvents();
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>'
	});
	
	Sbi.worksheet.runtime.RuntimeLineChartPanelExt3.superclass.constructor.call(this, c);
	
	this.init();
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeLineChartPanelExt3, Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3, {
	
	chartDivId : null
	//, chart : null
	, chartConfig : null 
	
	, init : function () {
		this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}

	, createChart: function () {
		var storeObject = this.getJsonStoreLineExt3();
		var colors = this.getColors();
		var extraStyle ={};
		
		var items = {
				//xtype: 'linechart',
				store: storeObject.store,
				xField: 'categories',
				hiddenseries: new Array(),
				series: this.getChartSeriesExt3(storeObject, 'line', colors),
                extraStyle: extraStyle,
                scope: this
			};
		//set the height if ie
    	if(Ext.isIE){
    		items.height = this.ieChartHeight;
    	}

		//set the maximum of the axis
		if(this.getStacking()=='percent'){
			var axis =  new Ext.chart.NumericAxis({
	            minimum: 0,
	            maximum: 100
			});
			items.yAxis = axis;
		}
		
		this.addChartConfExt3(items);
			
		var lineChartPanel = new Ext.chart.LineChart(items);

		var thispanel = this;
		
		this.on('contentclick', function(event){
			this.byteArrays=new Array();
			this.byteArrays.push(lineChartPanel.exportPNG());
			this.headerClickHandler(event,null,null,lineChartPanel, this.reloadJsonStoreExt3, this);
		}, this);
		

		var chartConf = {
				renderTo : this.chartDivId,
				layout: 'fit',
				border: false,
				items: lineChartPanel
			};

		new Ext.Panel(chartConf);
	}
	
	, getChartSeriesExt3: function(store, type, colors){

		var seriesForChart = new Array();

		for(var i=0; i<store.serieNames.length; i++){
			var yField = 'series'+i;
			if(this.getStacking()=='normal' || this.getStacking()=='percent'){
				yField = 'series'+i+'inc';
			}
			var serie = {
					type:type,
	                yField: yField,
	                style: {}
			};
			
//			if( this.getStacking()=='percent'){
//				serie.displayName =(store.serieNames[i]);
//			}else{
				serie.displayName = this.formatLegendWithScale(store.serieNames[i]);
//			}
			
//			serie.displayName =(store.serieNames[i]);
			
			if(colors!=null){
				serie.style.color= colors[i];
			}
			seriesForChart.push(serie);
		}
		return seriesForChart;
	}
	
	,getJsonStoreLineExt3: function(){
		var storeObject = {};
		
		var percent = this.getStacking()=='percent';
		var increment = this.getStacking()=='normal';

		var series = this.getSeries();
		var categories = this.getCategories();
		
		var data = new Array();
		var fields = new Array();
		var serieNames = new Array();
		var serieAlias = new Array();
		
		for(var i=0; i<categories.length; i++){
			var z = {};
			var seriesum = 0;
			for(var j=0; j<series.length; j++){
				seriesum = seriesum + parseFloat(((series[j]).data)[i]);
				z['series'+j] = ((series[j]).data)[i];
				if(percent || increment){
					z['series'+j+'inc'] = seriesum;
				}				
			}
			if(percent){
				for(var j=0; j<series.length; j++){
					z['seriesflatvalue'+j] = z['series'+j];
					z['series'+j] = (z['series'+j]/seriesum)*100;
					z['series'+j+'inc'] = (z['series'+j+'inc']/seriesum)*100;
				}	
			}
			z['seriesum'] = seriesum;
			z['categories'] = categories[i];
			data.push(z);
		}
		
		for(var j=0; j<series.length; j++){
			fields.push('series'+j);
			if(percent || increment){
				fields.push('series'+j+'inc');
			}
			if(percent){
				fields.push('seriesflatvalue'+j);
			}
			serieNames.push(series[j].name);	
			serieAlias.push(series[j].alias);
		}
		
		fields.push('seriesum');
		fields.push('categories');

		
	    var store = new Ext.data.JsonStore({
	        fields:fields,
	        data: data
	    });
	    
	    storeObject.store = store;
	    storeObject.serieNames = serieNames;
	    storeObject.serieAlias = serieAlias;

	    return storeObject;
	}
	
	//reload the store after hide a series
	, reloadJsonStoreExt3 : function(chart,reloadCallbackFunctionScope ){
		var oldDataStore= chart.store;
		var hiddenseries= chart.hiddenseries;
		
		var percent = reloadCallbackFunctionScope.getStacking()=='percent';
		var increment = reloadCallbackFunctionScope.getStacking()=='normal';

		
		var series = reloadCallbackFunctionScope.getSeries();
		var categories = reloadCallbackFunctionScope.getCategories();
		
		var data = new Array();
		var fields = new Array();
		var serieNames = new Array();
		
		for(var i=0; i<categories.length; i++){
			var z = {};
			var seriesum = 0;
			for(var j=0; j<series.length; j++){
				if(hiddenseries.indexOf(j)<0){
					seriesum = seriesum + parseFloat(((series[j]).data)[i]);
				}
				z['series'+j] = ((series[j]).data)[i];
				if(percent || increment){
					z['series'+j+'inc'] = seriesum;
				}			
			}
			if(percent){
				for(var j=0; j<series.length; j++){
					z['seriesflatvalue'+j] = z['series'+j];
					z['series'+j] = (z['series'+j]/seriesum)*100;
					z['series'+j+'inc'] = (z['series'+j+'inc']/seriesum)*100;
				}	
			}
			z['seriesum'] = seriesum;
			z['categories'] = categories[i];
			data.push(z);
		}
		
		oldDataStore.loadData(data);
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
	
	, getTooltipFormatter: function () {

		var chartType = this.chartConfig.designer;
		var allSeries = this.chartConfig.series;
		var stacking  =this.getStacking();
		
		var getFormattedValueExt3 = this.getFormattedValueExt3;
		
		var toReturn = function (chart, record, index, series) {
			var valuePrefix= '';
			
			var value = getFormattedValueExt3(chart, record, series, chartType, allSeries, stacking);
		
			valuePrefix = series.displayName+'\n'+record.data.categories+'\n';

			return valuePrefix+value;
			
		};
		return toReturn;
	}
	
	//Format the value to display
	, getFormattedValueExt3: function (chart, record, series, chartType, allSeries, stacking){
		var theSerieName  = series.displayName;
		var value ;
		var serieDefinition;
		
		
		if(stacking=='normal'){
			value = record.data[series.yField.substring(0,series.yField.length-3)];
		} else if(stacking=='percent'){
//			valuepercent = Ext.util.Format.number(record.data[series.yField], '0.00') + '%';
			value = record.data['seriesflatvalue'+series.yField.substring(series.yField.length-4,series.yField.length-3)];
		}else{
			value = record.data[series.yField];
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

		//if(stacking!='percent'){
			// format the value according to serie configuration
			value = Sbi.qbe.commons.Format.number(value, {
	    		decimalSeparator: Sbi.locale.formats['float'].decimalSeparator,
	    		decimalPrecision: serieDefinition.precision,
	    		groupingSeparator: (serieDefinition.showcomma) ? Sbi.locale.formats['float'].groupingSeparator : '',
	    		groupingSize: 3,
	    		currencySymbol: '',
	    		nullValue: ''
			});
		//}
		// add suffix
		if (serieDefinition.suffix !== undefined && serieDefinition.suffix !== null && serieDefinition.suffix !== '') {
			value = value + ' ' + serieDefinition.suffix;
		}
		return value;
	}

});