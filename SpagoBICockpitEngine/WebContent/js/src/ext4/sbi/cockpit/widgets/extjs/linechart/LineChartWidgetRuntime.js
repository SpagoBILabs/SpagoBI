/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.widgets.extjs.linechart");

Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime = function(config) {	
	Sbi.trace("[LineChartWidgetRuntime.constructor]: IN");
	var defaultSettings = {
			
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	var categories = [];
	categories.push(this.wconf.category);
	if(this.wconf.groupingVariable) categories.push(this.wconf.groupingVariable);
	
	this.aggregations = {
		measures: this.wconf.series,
		categories: categories
	};
	
	Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.constructor.call(this, c);
	
	this.boundStore();
	this.reload();
	this.addEvents('selection');
	
	Sbi.trace("[LineChartWidgetRuntime.constructor]: OUT");

};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime, Sbi.cockpit.widgets.extjs.abstractchart.AbstractChartWidget, {
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================

	// no props for the moment

	
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
	// cartesian chart shared methods
	// -----------------------------------------------------------------------------------------------------------------  
	getSeriesConfig: function() {
	    	
		var store = this.getStore();
	    	
	    var seriesFields = [];
		var seriesTitles = [];
		for(var i = 0; i < this.wconf.series.length; i++) {
			var id = this.wconf.series[i].id;
			seriesFields.push(store.fieldsMeta[id].name);
			seriesTitles.push(id);
		}
			
		var series = {
			fields: seriesFields,
			titles: seriesTitles,
			position: this.isHorizontallyOriented()? 'bottom' : 'left'
		};
			
		return series;
	}
	    
	, getCategoriesConfig: function() {
	    	
	    	var store = this.getStore();
	    	
	    	var categories = [];
			categories.push(this.wconf.category);
			if(this.wconf.groupingVariable) categories.push(this.wconf.groupingVariable);
			
			var categoriesFields = [];
			var categoriesTitles = [];
			for(var i = 0; i < categories.length; i++) {
				var id = categories[i].id;
				categoriesFields.push(store.fieldsMeta[id].name);
				categoriesTitles.push(id);
			}
			
			var categories = {
				fields: categoriesFields,
				titles: categoriesTitles, 
				position: this.isHorizontallyOriented()? 'left': 'bottom'
			};
			
			return categories;
	}
	
	, getOrientation: function() {
		return this.wconf? this.wconf.orientation: null;
	}

	, isVerticallyOriented: function() {
		return this.getOrientation() === 'vertical';
	}
	
	, isHorizontallyOriented: function() {
		return this.getOrientation() === 'horizontal';
	}
		
	, getSeriesLabel: function(seriesConfig) {
		var label = {
            display: 'insideEnd',
            field: seriesConfig.titles.length == 1? seriesConfig.titles[0]: undefined,
            renderer: Ext.util.Format.numberRenderer('0'),
            orientation: 'horizontal',
            color: '#333',
            'text-anchor': 'middle'
		};
		return label;
	}
	
	, getSeriesTips: function(series) {
		var thisPanel = this;
		
		var tips =  {
			trackMouse: true,
           	minWidth: 140,
           	maxWidth: 300,
           	width: 'auto',
           	minHeight: 28,
           	renderer: function(storeItem, item) {
           		//var tooltipContent = thisPanel.getTooltip(storeItem, item);
           		//this.setTitle(tooltipContent);
           		this.setTitle("Tooltip");
            }
        };
		
		return tips;
	}
	
	, getBackground: function() {
		var background = {
		    gradient: {
			    id: 'backgroundGradient',
			    angle: 45,
			    stops: {
				    0: {color: '#ffffff'},
				    100: {color: '#eaf1f8'}
				}
			}
		};
		return background;
	}
	
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, getChartType: function() {
		return 'line'; 
	}
	
	, isAreaFilled: function() {
		return this.wconf.colorarea;
	}

	, isStacked: function() {
		//return (this.wconf.type == 'stacked-barchart' || this.wconf.type == 'percent-stacked-barchart');
		return false;
	}
	
	, isPercentStacked: function() {
		//return this.wconf.type == 'percent-stacked-barchart';
		return((this.wconf.type).indexOf('percent')>=0);
	}
	
	, refresh:  function() {  
		Sbi.trace("[LineChartWidgetRuntime.refresh]: IN");
		Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.refresh.call(this);			
		this.redraw();
		Sbi.trace("[LineChartWidgetRuntime.refresh]: OUT");
	}

	, redraw: function() {
		Sbi.trace("[LineChartWidgetRuntime.redraw]: IN");
		
		Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.redraw.call(this);
		
		var seriresConfig = this.getSeriesConfig();
		var categoriesConfig =  this.getCategoriesConfig();
		
		var axes = this.getAxes( categoriesConfig, seriresConfig );
		var series = this.getSeries( categoriesConfig, seriresConfig );
		
		var store = this.getStore();
		store.sort(categoriesConfig.fields[0], 'ASC');
		
		this.chartPanel =  Ext.create('Ext.chart.Chart', {
            store: store,
            axes: axes,
            series: series,
            shadow: true,
            animate: true,
            theme: 'CustomBlue',
            background: this.getBackground(),
	        legend: this.isLegendVisible()
        });
		
		this.setContentPanel(this.chartPanel);
		
		Sbi.trace("[LineChartWidgetRuntime.redraw]: OUT");
	}
	
	, getAxes: function( categoriesConfig, seriesConfig ) {
		
		var seriesAxis = {
		    type: 'Numeric'
		    , position: seriesConfig.position
		    , fields: seriesConfig.fields
		    , minorTickSteps: 1 // The number of small ticks between two major ticks. Default is zero.
		    , label: {
		    	renderer: Ext.util.Format.numberRenderer('0,0')
		    }
			, title: seriesConfig.titles.length == 1? seriesConfig.titles[0]: undefined
		   	, grid: true
		    , minimum: 0
		};
		
		//For the percent type chart set the axes scale maximum to 100
		if(this.isPercentStacked()) {
			seriesAxis.maximum = 100;
		}
		
		var categoryAxis = {
		    type: 'Category'
		    , position: categoriesConfig.position
		    , fields: categoriesConfig.fields
		    , title: categoriesConfig.titles.length == 1? categoriesConfig.titles[0]: undefined       
	    };

		var axes = [seriesAxis, categoryAxis];
		
		return axes;
	}

	, getSeries: function( categoriesConfig, seriesConfig ) {
		
		Sbi.trace("[LineChartWidgetRuntime.getSeries]: IN");
		var series = [];
		
		for(var i = 0; i < seriesConfig.fields.length; i++) {
			series.push({
				type: this.getChartType(), 
				fill: this.isAreaFilled(),
				stacked: this.isStacked(),
				title: seriesConfig.titles,
	            highlight: {
	            	size: 7,
	                radius: 7
	            },
	            axis: seriesConfig.position,  
	            smooth: true,
	            tips: this.getSeriesTips(seriesConfig),
	            label: this.getSeriesLabel(seriesConfig),
	            xField: categoriesConfig.fields[0],
	            yField: seriesConfig.fields[i],
	            listeners: {
	    	    	itemmousedown: this.onItemMouseDown,
	    	    	scope: this
	    	    }
	        });
		}
		/*
		var series = [{
			type: this.getChartType(), 
//			fill: this.isAreaFilled(),
//			stacked: this.isStacked(),
			title: seriesConfig.titles,
            highlight: {
            	size: 7,
                radius: 7
            },
            axis: seriesConfig.position,  
            smooth: true,
//            tips: this.getSeriesTips(seriesConfig),
//            label: this.getSeriesLabel(seriesConfig),
            xField: categoriesConfig.fields[0],
            yField: seriesConfig.fields[0],
            listeners: {
    	    	itemmousedown: this.onItemMouseDown,
    	    	scope: this
    	    }
        }];
		*/
		
		Sbi.trace("[LineChartWidgetRuntime.getSeries]: OUT");
		
		return series;
	}
	
	, onItemMouseDown: function(item) {
		Sbi.trace("[LineChartWidgetRuntime.onItemMouseDown]: IN");
		alert("[LineChartWidgetRuntime.onItemMouseDown]: CLICK");
	    Sbi.trace("[LineChartWidgetRuntime.onItemMouseDown]: OUT");
	}


	
	
	
	, redrawOld: function() {
		Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.redraw.call(this);
		
		var retriever = new Sbi.cockpit.widgets.chart.DefaultChartDimensionRetrieverStrategy();
		var size = retriever.getChartDimension(this);
		this.update(' <div id="' + this.chartDivId + '" style="width: ' + size.width + '; height: ' + size.height + ';"></div>');
		var percent = ((this.chartConfig.type).indexOf('percent')>=0);
		var storeObject = this.getJsonStore(percent);
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
			items.series = this.getChartSeries(storeObject.serieNames, colors, true);

		}else{
			items.xField = 'categories';
			items.series = this.getChartSeries(storeObject.serieNames, colors);
		}

		this.addChartConf(items);


		items.region = 'center';

		var lineChartPanel = this.getChart(this.chartConfig.orientation === 'horizontal', items, colors, percent);
	}
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	
	//----- Ext 4 Implementation related functions ------------------------------------------
	, getChart : function(horizontal, items, colors, percent){
		
		var chartDataStore = items.store;
		
		var chartType = 'line'; 
		var isStacked = false;

		//Chart Type is 'side-by-side-linechart'		
		
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
			//seriesNames.push(name);
			var displayName = items.series[i].displayName;
			//displayNames.push(displayName);
		
		
			var areaFill = this.chartConfig.colorarea;
			
			//Costruct the series object(s)
			var aSerie = {			
					fill: areaFill,
	                type: chartType,
	                highlight: {
	                    size: 7,
	                    radius: 7
	                },
	                axis: axisPosition,
	                smooth: true,
	                stacked: isStacked,
	                xField: "categories",
	                yField: name,	                
	                title: displayName,
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
	    	        },
	    	        listeners: {
			  			itemmousedown:function(obj) {
			  				var categoryField ;
			  				var valueField ;
			  				categoryField = obj.storeItem.data[obj.series.xField];
			  				valueField = obj.storeItem.data[obj.series.xField];	  				
		  		    		var selections = {};
			  				var values =  [];
			  				selections[displayNames] = {};
		  		    		selections[displayNames].values = values; //manage multi-selection!
		  		    		Ext.Array.include(selections[displayNames].values, valueField);
		  		    		thisPanel.fireEvent('selection', thisPanel, selections);
			  			}
				}
	                
	         };
			
			series.push(aSerie);
		}
		
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
//			title: "Series",
			minorTickSteps: 1,
			grid: true
		}, {
			type: "Category",
			position: positionCategory,
			fields: ["categories"]
//			title: "Category"
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
		var series = item.series;
		
		var percent = ((this.chartConfig.type).indexOf('percent')>=0);
		alert("getTooltip");
		var storeObject = this.getJsonStore(percent);
		
		var selectedSerieName = series.yField;
		
		var selectedSerie;
		
		if(horizontal){
			series = this.getChartSeries(storeObject.serieNames, colors, true);
			for (var i =0; i<series.length;i++){
				if (series[i].xField == selectedSerieName){
					selectedSerie = series[i];
					break;
				}
			}

		}else{
			series = this.getChartSeries(storeObject.serieNames, colors);			
			for (var i =0; i<series.length;i++){
				if (series[i].yField == selectedSerieName){
					selectedSerie = series[i];					
					break;
				}
			}
		}

		
		var valueObj = this.getFormattedValue(null, record, selectedSerie, chartType, allRuntimeSeries, allDesignSeries, type, horizontal);
		
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
	
	
	, getChartSeries: function(serieNames, colors, horizontal){
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

	//used for tooltip	
	, getFormattedValue: function (chart, record, series, chartType, allRuntimeSeries, allDesignSeries, type, horizontal){
		var theSerieName  = series.displayName;
		var value ;
		var serieName;  // the serie name without eventual scale factor
		var measureName;  // the measure related to the serie
		var serieDefinition;  // the design-time serie definition (the measure with precision, color, ....)
		/*
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
		*/
		
		value = record.data[series.yField];
		
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
	, onRender: function(ct, position) {	
		Sbi.trace("[LineChartWidgetRuntime.onRender]: IN");
		
		this.msg = 'Sono un widget di tipo BarChart';
		
		Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.onRender.call(this, ct, position);	
		
		Sbi.trace("[LineChartWidgetRuntime.onRender]: OUT");
	}
	
	, getByteArraysForExport: function(){
		var byteArrays = new Array();
		for(var i=0; i<this.charts; i++){
			byteArrays.push((this.charts[i]).exportPNG());
		}	
	}

});



Sbi.registerWidget('linechart-ext', {
	name: 'Line Chart (NEW)'
	, icon: 'js/src/ext4/sbi/cockpit/widgets/extjs/linechart/img/linechart_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime'
	, designerClass: 'Sbi.cockpit.widgets.linechart.LineChartWidgetDesigner'
});