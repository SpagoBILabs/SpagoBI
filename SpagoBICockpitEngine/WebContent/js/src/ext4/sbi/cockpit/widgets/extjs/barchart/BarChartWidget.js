/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.widgets.extjs.barchart");

Sbi.cockpit.widgets.extjs.barchart.BarChartWidget = function(config) {	
	Sbi.trace("[BarChartWidget.constructor]: IN");
	
	var defaultSettings = {
			
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.extjs.barchart.BarChartWidget', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	this.aggregations = {
		measures: this.wconf.series,
		categories: this.wconf.categories
	};
	
	Sbi.cockpit.widgets.extjs.barchart.BarChartWidget.superclass.constructor.call(this, c);
	
	this.boundStore();
	
	
	var categories = [];
	categories.push(this.wconf.category);
	if(this.wconf.groupingVariable) categories.push(this.wconf.groupingVariable);
	
	this.reload();
//	this.reload({
//		measures: Ext.JSON.encode(this.wconf.series),
//		categories: Ext.JSON.encode(categories)
//	});
	
//	this.loadChartData({
//		'rows':[this.wconf.category]
//		, 'measures': this.wconf.series
//		, 'columns': this.wconf.groupingVariable ? [this.wconf.groupingVariable] : []
//	});

	this.addEvents('selection');
	
	Sbi.trace("[BarChartWidget.constructor]: OUT");

};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.extjs.barchart.BarChartWidget, Sbi.cockpit.widgets.extjs.abstractchart.AbstractChartWidget, {
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

	, getOrientation: function() {
		return this.wconf? this.wconf.orientation: null;
	}

	, isVerticallyOriented: function() {
		return this.getOrientation() === 'vertical';
	}
	
	, isHorizontallyOriented: function() {
		return this.getOrientation() === 'horizontal';
	}
	
	, getChartType: function() {
		return this.isHorizontallyOriented()? 'bar': 'column';
	}
	
	, isStacked: function() {
		return (this.wconf.type == 'stacked-barchart' || this.wconf.type == 'percent-stacked-barchart');
	}
		
    , refresh:  function() {  
    	Sbi.trace("[BarChartWidget.refresh]: IN");
    	
		this.loadChartData({
			'rows':[this.wconf.category]
			, 'measures': this.wconf.series
			, 'columns': this.wconf.groupingVariable ? [this.wconf.groupingVariable] : []
		});
		
    	this.redraw();
		
    	Sbi.trace("[BarChartWidget.refresh]: OUT");
	}


	, redraw: function () {
		Sbi.trace("[BarChartWidget.redraw]: IN");
		Sbi.cockpit.widgets.extjs.barchart.BarChartWidget.superclass.redraw.call(this);	
		
		s = this.getStore();
		
		
		var seriesFields = [];
		for(var i = 0; i < this.wconf.series.length; i++) {
			var id = this.wconf.series[i].id;
			seriesFields.push(s.fieldsMeta[id].name);
		}
		
		var categories = [];
		categories.push(this.wconf.category);
		if(this.wconf.groupingVariable) categories.push(this.wconf.groupingVariable);
		
		var categoriesFields = [];
		for(var i = 0; i < categories.length; i++) {
			var id = categories[i].id;
			categoriesFields.push(s.fieldsMeta[id].name);
		}
		
		this.series = {
			fields: seriesFields,
			title: seriesFields[0],
			position: this.isHorizontallyOriented()? 'bottom' : 'left'
		};

		this.category = {
			fields: categoriesFields,
			title: categoriesFields[0], 
			position: this.isHorizontallyOriented()? 'left': 'bottom'
		};
		
		var axes = this.getAxes();
		var series = this.getSeries();
		
		
		s.sort(categoriesFields[0], 'ASC');
		
		this.chartPanel =  Ext.create('Ext.chart.Chart', {
			animate: true,
            shadow: true,
            theme: 'CustomBlue',
            store: s,
            axes: axes,
            background: this.getBackground(),
            series: series
        });
		
		this.setContentPanel(this.chartPanel);
        
		Sbi.trace("[BarChartWidget.redraw]: OUT");
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
	
	, getAxes: function() {
		
//		axes = [{
//			type: "Numeric",
//			minimum: 0,
//			position: positionNumeric,
//			fields: seriesNames,
//			minorTickSteps: 1,
//			grid: true
//		}, {
//			type: "Category",
//			position: positionCategory,
//			fields: ["categories"]
//		}];
		
		 
		
		var seriesAxis = {
		    type: 'Numeric'
		    , position: this.series.position
		    , fields: this.series.fields
		    , minorTickSteps: 1 // The number of small ticks between two major ticks. Default is zero.
		    , label: {
		    	renderer: Ext.util.Format.numberRenderer('0,0')
		    }
			, title: this.series.title
		   	, grid: true
		    , minimum: 0
		};
		
		var categoryAxis = {
		    type: 'Category'
		    , position: this.category.position
		    , fields: this.category.fields
		    , title: this.category.title       
	    };

		var axes = [seriesAxis, categoryAxis];
		
		return axes;
	}
	
	, getSeries: function() {

//		var aSerie = {
//			type: this.getChartType(),
//            highlight: {
//            	size: 7,
//                radius: 7
//            },
//            axis: this.series.position,
//            smooth: true,
//            stacked: this.isStacked(),
//            xField: "categories",
//            yField: seriesNames,	                
//            title: displayNames,
//    	    tips: this.getSeriesTips(),
//    	    listeners: {
//    	    	itemmousedown: this.onItemMouseDown
//    	    }
//		};
		
		var series = [{
			type: this.getChartType(), // 'bar',
			stacked: this.isStacked(),
            highlight: {
            	size: 7,
                radius: 7
            },
            axis: this.series.position,  //'bottom',
            smooth: true,
            tips: this.getSeriesTips(),
            label: {
               display: 'insideEnd',
               field: this.series.fields[0],
               renderer: Ext.util.Format.numberRenderer('0'),
               orientation: 'horizontal',
               color: '#333',
               'text-anchor': 'middle'
            },
            xField: this.category.fields,
            yField: this.series.fields
        }];
		
		return series;
	}
	
	, getSeriesTips: function() {
		var thisPanel = this;
		
		var tips =  {
			trackMouse: true,
            width: 140,
            height: 28,
            renderer: function(storeItem, item) {
          	  this.setTitle(storeItem.get('name') + ': ' + storeItem.get('data1') + ' views');
            }
        };
		
		return tips;
	}
	
	, onItemMouseDown: function(obj) {
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
	
	
	
	
	, redrawOld: function () {
		
		Sbi.trace("[BarChartWidget.redraw]: IN");
		Sbi.cockpit.widgets.extjs.barchart.BarChartWidget.superclass.redraw.call(this);	
		
		var retriever = new Sbi.cockpit.widgets.chart.DefaultChartDimensionRetrieverStrategy();
		var size = retriever.getChartDimension(this);
		this.update(' <div id="' + this.chartDivId + '" style="width: ' + size.width + '; height: ' + size.height + ';"></div>');
		var percent = ((this.wconf.type).indexOf('percent')>=0);
		var storeObject = this.getJsonStore(percent);
		var colors = this.getColors();
		var extraStyle ={};

		var items = {
				store: storeObject.store,
				extraStyle: extraStyle,
				style: 'height: 85%;',
				hiddenseries: new Array(),
				horizontal: this.isHorizontallyOriented()
		};

		//set the height if ie
		if(Ext.isIE){
			items.height = this.ieChartHeight;
		}

		if(this.isHorizontallyOriented()){
			items.yField = 'categories';
			items.series = this.getChartSeries(storeObject.serieNames, colors, true);

		}else{
			items.xField = 'categories';
			items.series = this.getChartSeries(storeObject.serieNames, colors);
		}

		this.addChartConf(items);

		items.region = 'center';

		var barChartPanel = this.getChart(items, colors, percent);
		
		Sbi.trace("[BarChartWidget.redraw]: IN");
	}
	
	
	
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	
	//----- Ext 4 Implementation related functions ------------------------------------------
	, getChart : function(items, colors, percent){
		
		var chartDataStore = items.store;
	
		//Create Axes Configuration
		var chartAxes = this.createAxes(items, percent);
		//Create Series Configuration
		var chartSeries = this.createSeries(items);
		
		//Legend visibility
		var showlegend;
		if (this.wconf.showlegend !== undefined){
			showlegend = this.wconf.showlegend;
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
//	    chart.on('selection', this.pippo, this);
	    return chart;
	}
	
	, createAxes : function(items,percent){
		var axes;	
		var positionNumeric;
		var positionCategory;

		if (this.isHorizontallyOriented()){
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
			if (this.isHorizontallyOriented()){
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

	/*
	 * Create the Series object configuration
	 */
	, createSeries : function(items){
		var thisPanel = this;
		
		var series = [];
				
		var seriesNames = [];
		var displayNames = [];
	

		//Extract technical series names and corresponding name to display
		for (var i=0; i< items.series.length; i++){
			var name;
			if (this.isHorizontallyOriented()){
				name = items.series[i].xField;
			} else {
				name = items.series[i].yField;
			}
			seriesNames.push(name);
			var displayName = items.series[i].displayName;
			displayNames.push(displayName);
		}
		
		var aSerie = {
                type: this.getChartType(),
                highlight: {
                    size: 7,
                    radius: 7
                },
                axis: this.series.position,
                smooth: true,
                stacked: this.isStacked(),
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
	            		   var tooltipContent = thisPanel.getTooltip(storeItem, item);
	            		   this.setTitle(tooltipContent);
	            	  }
    	        },
    	        listeners: {
    	        	itemmousedown: this.onItemMouseDown
    	        }
                
         };
		series.push(aSerie);
		
		return series;
	}
	/*
	 * Create the Axes object configuration
	 */
	
	
	, getTooltip : function(record, item){
		var chartType = this.wconf.designer;
		var allRuntimeSeries = this.getRuntimeSeries();
		var allDesignSeries = this.wconf.series;
		var type = this.wconf.type;
		var colors = this.getColors();
		var series;
		
		var percent = ((this.wconf.type).indexOf('percent')>=0);
		var storeObject = this.getJsonStore(percent);
		
		var selectedSerieName = item.yField;
		
		var selectedSerie;
		
		if(this.isHorizontallyOriented()){
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

		
		var valueObj = this.getFormattedValue(null, record, selectedSerie, chartType, allRuntimeSeries, allDesignSeries, type, this.isHorizontallyOriented());
		
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
	
	
	, getChartSeries: function(serieNames, colors){
		var seriesForChart = new Array();
		for(var i=0; i<serieNames.length; i++){
			var serie = {	
	                style: {}
			};
			
			serie.displayName =  serieNames[i];
			
			if(this.isHorizontallyOriented()){
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
	, getFormattedValue: function (chart, record, series, chartType, allRuntimeSeries, allDesignSeries, type){
		var theSerieName  = series.displayName;
		var value ;
		var serieName;  // the serie name without eventual scale factor
		var measureName;  // the measure related to the serie
		var serieDefinition;  // the design-time serie definition (the measure with precision, color, ....)

		if(type != 'percent-stacked-barchart'){
			if(this.isHorizontallyOriented()){
				value =  record.data[series.xField];
			}else{
				value = record.data[series.yField];
			}
		}else{
			//value = Ext.util.Format.number(record.data[series.xField], '0.00');
			if(this.isHorizontallyOriented()){
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
	, onRender: function(ct, position) {	
		Sbi.trace("[BarChartWidget.onRender]: IN");
		
		this.msg = 'Sono un widget di tipo BarChart';
		
		Sbi.cockpit.widgets.extjs.barchart.BarChartWidget.superclass.onRender.call(this, ct, position);	
		
		Sbi.trace("[BarChartWidget.onRender]: OUT");
	}
	
	, getByteArraysForExport: function(){
		var byteArrays = new Array();
		for(var i=0; i<this.charts; i++){
			byteArrays.push((this.charts[i]).exportPNG());
		}	
	}

	// -----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------
//	, init : function () {
//		
//		this.loadChartData({
//			'rows':[this.wconf.category]
//			, 'measures': this.wconf.series
//			, 'columns': this.wconf.groupingVariable ? [this.wconf.groupingVariable] : []
//		});
//	}

});

Sbi.registerWidget('barchart-ext', {
	name: 'NEW Bar Chart'
	, icon: 'js/src/ext4/sbi/cockpit/widgets/extjs/barchart/barchart_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.extjs.barchart.BarChartWidget'
	, designerClass: 'Sbi.cockpit.widgets.barchart.BarChartWidgetDesigner'
	//, designerClass: 'Ext.Panel'
});
