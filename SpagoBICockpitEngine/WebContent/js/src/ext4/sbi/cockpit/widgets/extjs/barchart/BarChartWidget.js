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
	
	Sbi.cockpit.widgets.extjs.barchart.BarChartWidget.superclass.constructor.call(this, c);
	
	Sbi.trace("[BarChartWidget.constructor]: OUT");

};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.extjs.barchart.BarChartWidget, Sbi.cockpit.widgets.extjs.abstractchart.AbstractCartesianChartWidgetRuntime, {
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	
	
	getChartType: function() {
		return this.isHorizontallyOriented()? 'bar': 'column';
	}
	
	, isStacked: function() {
		return (this.wconf.type == 'stacked-barchart' || this.wconf.type == 'percent-stacked-barchart');
	}
	
	, isPercentStacked: function() {
		return this.wconf.type == 'percent-stacked-barchart';
	}
  
    , refresh:  function() {  
    	Sbi.trace("[BarChartWidget.refresh]: IN");
    	
    	Sbi.cockpit.widgets.extjs.barchart.BarChartWidget.superclass.refresh.call(this);	
    	
    	this.redraw();
		
    	Sbi.trace("[BarChartWidget.refresh]: OUT");
	}
    
	, redraw: function () {
		Sbi.trace("[BarChartWidget.redraw]: IN");
		
		Sbi.cockpit.widgets.extjs.barchart.BarChartWidget.superclass.redraw.call(this);	

		var seriresConfig = this.getSeriesConfig();
		var categoriesConfig =  this.getCategoriesConfig();
		
		var axes = this.getAxes( categoriesConfig, seriresConfig );
		var series = this.getSeries( categoriesConfig, seriresConfig );
		
		var store = this.getStore();
			
		
		if(this.isPercentStacked()) {
			var data = [];
			if(categoriesConfig.fields.length == 1) {
				var fields = [];
				for(var h in store.fieldsMeta) {
					fields.push(store.fieldsMeta[h].name);
				}
				var newStore =  new Ext.data.JsonStore({
			        fields:fields,
			        data: store.data
			    });
				//store = newStore;
				alert("Impossible to create a percet stacked bar chart");
			} else {
				alert("Impossible to create a percet stacked bar chart with more then on category");
			}
		}
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
        
		Sbi.trace("[BarChartWidget.redraw]: OUT");
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
		
		Sbi.trace("[BarChartWidget.getSeries]: IN");
		
		var series = [{
			type: this.getChartType(), 
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
            xField: categoriesConfig.fields,
            yField: seriesConfig.fields,
            listeners: {
    	    	itemmousedown: this.onItemMouseDown,
    	    	scope: this
    	    }
        }];
		
		Sbi.trace("[BarChartWidget.getSeries]: OUT");
		
		return series;
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
           		var tooltipContent = thisPanel.getTooltip(storeItem, item);
           		this.setTitle(tooltipContent);
            }
        };
		
		return tips;
	}
	
	, getItemMeta: function(item) {
		var itemMeta = {};
		
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: IN " + Sbi.toSource(item, true));
		
		// selected categories: names, headers & values
		itemMeta.categoryFieldNames = item.series.xField;
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected categories names are equal to [" + itemMeta.categoryFieldNames +"]");
		
		itemMeta.categoryFieldHeaders = [];
		for(var i = 0; i < itemMeta.categoryFieldNames.length; i++) {
			itemMeta.categoryFieldHeaders[i] = this.getFieldHeaderByName( itemMeta.categoryFieldNames[i] );
		}
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected categories headers are equal to [" + itemMeta.categoryFieldHeaders +"]");
		
		itemMeta.categoryValues = [];
		for(var i = 0; i < itemMeta.categoryFieldNames.length; i++) {
			itemMeta.categoryValues.push( item.storeItem.data[itemMeta.categoryFieldNames[i]] );	
		}
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected categories values are equal to [" + itemMeta.categoryValues +"]");
	
		// selected series: name, header & value
		itemMeta.seriesFieldName = item.yField;
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected series name is equal to [" + itemMeta.seriesFieldName +"]");
		
		itemMeta.seriesFieldHeader = this.getFieldHeaderByName(itemMeta.seriesFieldName);
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected series header is equal to [" + itemMeta.seriesFieldHeader +"]");
		
		itemMeta.seriesFieldValue = item.storeItem.data[itemMeta.seriesFieldName];	 
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected series value is equal to [" + itemMeta.seriesFieldValue +"]");
 
    	
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: OUT");
		
		return itemMeta;
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
});

Sbi.registerWidget('barchart-ext', {
	name: 'NEW Bar Chart'
	, icon: 'js/src/ext4/sbi/cockpit/widgets/extjs/barchart/barchart_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.extjs.barchart.BarChartWidget'
	, designerClass: 'Sbi.cockpit.widgets.barchart.BarChartWidgetDesigner'
	//, designerClass: 'Ext.Panel'
});
