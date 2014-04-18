/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.widgets.barchart");

Sbi.cockpit.widgets.barchart.BarChartWidget = function(config) {	
	Sbi.trace("[BarChartWidget.constructor]: IN");
	var defaultSettings = {
			
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.barchart.BarChartWidget', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);

	//Commented for refactoring
	/*
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>'
		, autoScroll: true
	});
	*/
	
	Sbi.cockpit.widgets.barchart.BarChartWidget.superclass.constructor.call(this, c);
	this.init();

	/*
	this.on("afterRender", function(){
		this.getStore().load();
		//this.refresh();
		Sbi.trace("[BarChartWidget.onRender]: store loaded");
	}, this);
	*/
	
	Sbi.trace("[BarChartWidget.constructor]: OUT");

};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.barchart.BarChartWidget, Sbi.cockpit.widgets.chart.AbstractChartWidget, {
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	  chartDivId : null
	, chart : null
	, chartConfig : null 	
	
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------


    , refresh:  function() {  
		//TODO: must implement this
	}


	, createChart: function () {

		var retriever = new Sbi.cockpit.widgets.chart.DefaultChartDimensionRetrieverStrategy();
		var size = retriever.getChartDimension(this);
		this.update(' <div id="' + this.chartDivId + '" style="width: ' + size.width + '; height: ' + size.height + ';"></div>');
		var percent = ((this.chartConfig.type).indexOf('percent')>=0);
		var storeObject = this.getJsonStoreExt3(percent);
		var colors = this.getColors();
		var extraStyle ={};

		var items = {
				store: storeObject.store,
				extraStyle: extraStyle,
				style: 'height: 85%;',
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

		}else{
			items.xField = 'categories';
			items.series = this.getChartSeriesExt3(storeObject.serieNames, colors);
		}

		this.addChartConfExt3(items);


		items.region = 'center';
		//Ext3 implementation
		//var barChartPanel = this.getChartExt3(this.chartConfig.orientation === 'horizontal', items);
		var barChartPanel = this.getChartExt4(this.chartConfig.orientation === 'horizontal', items, colors, percent);
		
		//Its a workaround because if you change the display name the chart is not able to write the tooltips

		//TODO: Ext3 implementation
		/*
		var exportChartPanel  = new Ext.Panel({
			border: false,
			region: 'north',
			height: 20,
			html: '<div style=\"padding-top: 5px; padding-bottom: 5px; font: 11px tahoma,arial,helvetica,sans-serif;\">'+LN('sbi.worksheet.runtime.worksheetruntimepanel.chart.includeInTheExport')+'</div>'
		});
	
		var chartConf ={
				renderTo : this.chartDivId,
				border: false,
				items: [exportChartPanel, barChartPanel]
		};
	
		
		this.on('contentclick', function(event){
			this.byteArrays=new Array();
			try{
				this.byteArrays.push(barChartPanel.exportPNG());	
			}catch(e){}

			exportChartPanel.update('');
			this.headerClickHandler(event,null,null,barChartPanel, this.reloadJsonStoreExt3, this);
		}, this);
		 */
		//TODO: Ext3 implementation
		//new Ext.Panel(chartConf);

	}
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	
	//----- Ext 4 Implementation related functions ------------------------------------------
	, getChartExt4 : function(horizontal, items, colors, percent){
		
		var chartDataStore = items.store;
		
		var chartType; 
		var isStacked = false;
		
		//Define Ext4 Chart appropriate type
		if(horizontal){
			if(this.chartConfig.type == 'stacked-barchart' || this.chartConfig.type == 'percent-stacked-barchart'){
				chartType = 'bar';
				isStacked = true;
			}else{
				chartType = 'bar';
			}
		} else {
			if(this.chartConfig.type == 'stacked-barchart' || this.chartConfig.type == 'percent-stacked-barchart'){
				chartType = 'column';
				isStacked = true;
			}else{
				chartType = 'column';
			}
		}
		//Create Axes Configuration
		var chartAxes = this.createAxes(horizontal, items, percent);
		//Create Series Configuration
		var chartSeries = this.createSeries(horizontal, items, chartType, isStacked);
		
		//Legend visibility
		var showlegend;
		if (this.chartConfig.showlegend !== undefined){
			showlegend = this.chartConfig.showlegend;
		} else {
			showlegend = true;
		}
		
		//Create theme for using custom defined colors
		Ext.define('Ext.chart.theme.CustomTheme', {
		    extend: 'Ext.chart.theme.Base',

		    constructor: function(config) {
		        this.callParent([Ext.apply({
		            colors: colors
		        }, config)]);
		    }
		});
		
	    var chart = Ext.create("Ext.chart.Chart", {
	        width: '100%',
	    	height: '100%',
	    	theme: 'CustomTheme',
	        hidden: false,
	        title: "My Chart",
	        renderTo: this.chartDivId,
	        layout: "fit",
	        style: "background:#fff",
	        animate: true,
	        store: chartDataStore,
	        shadow: true,
	        legend: showlegend,
	        axes: chartAxes,
	        series: chartSeries

	    });
	    
	    return chart;
	}
	/*
	 * Create the Series object configuration
	 */
	, createSeries : function(horizontal,items, chartType, isStacked){
		var thisPanel = this;
		var axisPosition;
		var series = [];
		
		if (horizontal){
			//bar chart
			axisPosition = 'bottom';
		} else {
			//column chart
			axisPosition = 'left';
		}
		
		var seriesNames = [];
		var displayNames = [];
		

		//Extract technical series names and corresponding name to display
		for (var i=0; i< items.series.length; i++){
			var name;
			if (horizontal){
				name = items.series[i].xField;
			} else {
				name = items.series[i].yField;
			}
			seriesNames.push(name);
			var displayName = items.series[i].displayName;
			displayNames.push(displayName);
		}
		
		//Costruct the series object(s)
		var aSerie = {
                type: chartType,
                highlight: {
                    size: 7,
                    radius: 7
                },
                axis: axisPosition,
                smooth: true,
                stacked: isStacked,
                xField: "categories",
                yField: seriesNames,	                
                title: displayNames,
    	        tips: {
	            	  trackMouse: true,
	            	  minWidth: 140,
	            	  maxWidth: 300,
	            	  width: 'auto',
	            	  minHeight: 28,
	            	  renderer: function(storeItem, item) {
	            		   //this.setTitle(String(item.value[0])+" : "+String(item.value[1]));
	            		   var tooltipContent = thisPanel.getTooltip(storeItem, item);
	            		   this.setTitle(tooltipContent);
	            	  }
    	        }
                
         };
		series.push(aSerie);
		
		return series;
	}
	/*
	 * Create the Axes object configuration
	 */
	, createAxes : function(horizontal,items,percent){
		var axes;	
		var positionNumeric;
		var positionCategory;

		if (horizontal){
			//bar chart
			positionNumeric = 'bottom';
			positionCategory = 'left';
		} else {
			//column chart
			positionNumeric = 'left';
			positionCategory = 'bottom';
		}
		
		var seriesNames = [];
		
		for (var i=0; i< items.series.length; i++){
			var name;
			if (horizontal){
				name = items.series[i].xField;
			} else {
				name = items.series[i].yField;
			}
			seriesNames.push(name);
		}

		axes = [{
			type: "Numeric",
			minimum: 0,
			position: positionNumeric,
			fields: seriesNames,
			title: "Series",
			minorTickSteps: 1,
			grid: true
		}, {
			type: "Category",
			position: positionCategory,
			fields: ["categories"],
			title: "Category"
		}];
		
		//For the percent type chart set the axes scale maximum to 100
		if (percent){
			axes[0].maximum = 100;
		}
		
		return axes;
	}
	
	, getTooltip : function(record, item){
		var chartType = this.chartConfig.designer;
		var allRuntimeSeries = this.getRuntimeSeries();
		var allDesignSeries = this.chartConfig.series;
		var type = this.chartConfig.type;
		var horizontal = this.chartConfig.orientation === 'horizontal';
		var colors = this.getColors();
		var series;
		
		var percent = ((this.chartConfig.type).indexOf('percent')>=0);
		var storeObject = this.getJsonStoreExt3(percent);
		
		var selectedSerieName = item.yField;
		
		var selectedSerie;
		
		if(horizontal){
			series = this.getChartSeriesExt3(storeObject.serieNames, colors, true);
			for (var i =0; i<series.length;i++){
				if (series[i].xField == selectedSerieName){
					selectedSerie = series[i];
					break;
				}
			}

		}else{
			series = this.getChartSeriesExt3(storeObject.serieNames, colors);
			
			for (var i =0; i<series.length;i++){
				if (series[i].yField == selectedSerieName){
					selectedSerie = series[i];
					break;
				}
			}
		}

		
		var valueObj = this.getFormattedValueExt3(null, record, selectedSerie, chartType, allRuntimeSeries, allDesignSeries, type, horizontal);
		
		var tooltip = '';
		
		if (valueObj.measureName !== valueObj.serieName) {
			tooltip = valueObj.serieName + '<br/>' + record.data.categories + '<br/>';
			// in case the serie name is different from the measure name, put also the measure name
			//tooltip += this.formatTextWithMeasureScaleFactor(valueObj.measureName, valueObj.measureName) + ' : ';
		} else {
			tooltip =  record.data.categories + '<br/>' + selectedSerie.displayName + ' : ' ;
		}
		tooltip += valueObj.value;
		
		return tooltip;

	}
	
	
	///---------------------------------------------------------------------
	
	
	, getChartSeriesExt3: function(serieNames, colors, horizontal){
		var seriesForChart = new Array();
		for(var i=0; i<serieNames.length; i++){
			var serie = {	
	                style: {}
			};
			
//			if(this.chartConfig.type == 'percent-stacked-barchart'){
//				serie.displayName =  (serieNames[i]);//if percent doesn't matter the scale 
//			}else{
				//serie.displayName =  this.formatLegendWithScale(serieNames[i]); //Commented by MC
//			}
			serie.displayName =  serieNames[i];
			
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
	/*
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
	*/
	//tooltip formatting
	/*
	, getTooltipFormatter: function () {
		
		var chartType = this.chartConfig.designer;
		var allRuntimeSeries = this.getRuntimeSeries();
		var allDesignSeries = this.chartConfig.series;
		var type = this.chartConfig.type;
		var horizontal = this.chartConfig.orientation === 'horizontal';
		
		var thePanel = this;
		
		var toReturn = function (chart, record, index, series) {
			var tooltip = '';
			
			var valueObj = thePanel.getFormattedValueExt3(chart, record, series, chartType, allRuntimeSeries, allDesignSeries, type, horizontal);
			
			if (valueObj.measureName !== valueObj.serieName) {
				tooltip = valueObj.serieName + '\n' + record.data.categories + '\n';
				// in case the serie name is different from the measure name, put also the measure name
				tooltip += thePanel.formatTextWithMeasureScaleFactor(valueObj.measureName, valueObj.measureName) + ' : ';
			} else {
				tooltip =  record.data.categories + '\n' + series.displayName + ' : ' ;
			}
			tooltip += valueObj.value;
		
			return tooltip;
			
		};
		return toReturn;
	} */
	
	//for tooltip
	
	, getFormattedValueExt3: function (chart, record, series, chartType, allRuntimeSeries, allDesignSeries, type, horizontal){
		var theSerieName  = series.displayName;
		var value ;
		var serieName;  // the serie name without eventual scale factor
		var measureName;  // the measure related to the serie
		var serieDefinition;  // the design-time serie definition (the measure with precision, color, ....)

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
		
		// find the measure's name
		var i = 0;
		for (; i < allRuntimeSeries.length; i++) {
			//substring to remove the scale factor
			if (allRuntimeSeries[i].name === theSerieName.substring(0, allRuntimeSeries[i].name.length)) {
				serieName = allRuntimeSeries[i].name;
				measureName = allRuntimeSeries[i].measure;
				break;
			}
		}
		
		i = 0;
		// find the serie's (design-time) definition
		for (; i < allDesignSeries.length; i++) {
			if (allDesignSeries[i].id === measureName) {
				serieDefinition = allDesignSeries[i];
				break;
			}
		}

		// format the value according to serie configuration
		value = Sbi.commons.Format.number(value, {
    		decimalSeparator: Sbi.locale.formats['float'].decimalSeparator,
    		decimalPrecision: serieDefinition.precision,
    		groupingSeparator: (serieDefinition.showcomma) ? Sbi.locale.formats['float'].groupingSeparator : '',
    		groupingSize: 3,
    		currencySymbol: '',
    		nullValue: ''
		});
			
		// add suffix
		if (serieDefinition.suffix !== undefined && serieDefinition.suffix !== null && serieDefinition.suffix !== '') {
			value = value + ' ' + serieDefinition.suffix;
		}

		var toReturn = {};
		toReturn.value = value;
		toReturn.serieName = serieName;
		toReturn.measureName = measureName;
		return toReturn;
	}
	
	
	//------------------------------------------------------------------------------------------------------------------
	// utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, getByteArraysForExport: function(){
		var byteArrays = new Array();
		for(var i=0; i<this.charts; i++){
			byteArrays.push((this.charts[i]).exportPNG());
		}	
	}
	/*
	, isEmpty : function () {
		var measures = this.dataContainerObject.columns.node_childs;
		return measures === undefined;
	}
	 */
	// -----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------
	, init : function () {
		this.chartConfig = this.wconf;
		
		this.loadChartData({
			'rows':[this.chartConfig.category]
			, 'measures': this.chartConfig.series
			, 'columns': this.chartConfig.groupingVariable ? [this.chartConfig.groupingVariable] : []
		});
	}

});

Sbi.registerWidget('barchart', {
	name: 'Bar Chart'
	, icon: 'js/src/ext4/sbi/cockpit/widgets/barchart/barchart_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.barchart.BarChartWidget'
	, designerClass: 'Sbi.cockpit.widgets.barchart.BarChartWidgetDesigner'
	//, designerClass: 'Ext.Panel'
});
