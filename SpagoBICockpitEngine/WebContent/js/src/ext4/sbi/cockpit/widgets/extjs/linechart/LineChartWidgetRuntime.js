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

	Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.constructor.call(this, c);

	Sbi.trace("[LineChartWidgetRuntime.constructor]: OUT");

};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime, Sbi.cockpit.widgets.extjs.abstractchart.AbstractCartesianChartWidgetRuntime, {
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================

	// no props for the moment


    // =================================================================================================================
	// METHODS
	// =================================================================================================================

    // =================================================================================================================
	// METHODS
	// =================================================================================================================

    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	//return category axis name (if specified)
	getCategoryAxis: function(){
		var categoryAxis = '';
		if (this.wconf.categoryAxis){
			categoryAxis = this.wconf.categoryAxis;
		}
		return categoryAxis;
	}

	//return series axis name (if specified)
	, getSeriesAxis: function(){
		var seriesAxis = '';
		if (this.wconf.seriesAxis){
			seriesAxis = this.wconf.seriesAxis;
		}
		return seriesAxis;
	}

	, getChartType: function() {
		return 'line';
	}

	, isAreaFilled: function() {
		return this.wconf.colorarea;
	}

	, isStacked: function() {
		return (this.wconf.type == 'stacked-linechart');
		//return false;
	}

	, isPercentStacked: function() {
		return (this.wconf.type == 'percent-stacked-linechart');
		//return((this.wconf.type).indexOf('percent')>=0);
	}

	, refresh:  function() {
		Sbi.trace("[LineChartWidgetRuntime.refresh]: IN");
		Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.refresh.call(this);
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

		for(var i = 0; i < store.data.items.length; i++){
			var seriesum = 0;

			if(this.isPercentStacked()){
				for(var j = 0; j < seriresConfig.fields.length; j++){
					for (var h in store.data.items[i].data){
						if (h == seriresConfig.fields[j]){
							seriesum = seriesum + parseFloat(store.data.items[i].data[h]);
						}
					}
				}

				for(var j = 0; j < seriresConfig.fields.length; j++){
					for (var h in store.data.items[i].data){
						if (h == seriresConfig.fields[j]){
							if (seriesum != 0){
								store.data.items[i].data[h] = parseFloat((store.data.items[i].data[h]/seriesum)*100);
							}
						}
					}
				}
			}

			//truncate long category labels, and riassign value inside store
			var categoryField = categoriesConfig.fields[0];
			var categoryLabel = store.data.items[i].data[categoryField];
			store.data.items[i].data[categoryField] = Ext.util.Format.ellipsis(categoryLabel,50);
		}

		var sortMeasure = this.getMeasureToSort();
		if (sortMeasure.length > 0){
			store.sort(sortMeasure[0], 'ASC');
		} else {
			store.sort(categoriesConfig.fields[0], 'ASC');
		}

		if (!this.areIncomingEventsEnabled()) {
	     	var clone = Sbi.storeManager.cloneStore(this.getStore());
	     	store = clone;
	     	this.unboundStore();
		}

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
		var seriesTitle;
		if (this.getSeriesAxis().length > 0){
			seriesTitle = this.getSeriesAxis();
		} else {
			seriesTitle = seriesConfig.titles.length == 1? seriesConfig.titles[0]: undefined;
		}
		var seriesAxis = {
		    type: 'Numeric'
		    , position: seriesConfig.position
		    , fields: seriesConfig.fields
		    , minorTickSteps: 1 // The number of small ticks between two major ticks. Default is zero.
//		    , label: {
//		    	display: 'inside',
//		    	renderer: Ext.util.Format.numberRenderer('0,0')
//		    }
			, title: seriesTitle
		   	, grid: true
		    , minimum: 0
		};

		//For the percent type chart set the axes scale maximum to 100
		if(this.isPercentStacked()) {
			seriesAxis.maximum = 100;
		}

		var categoryTitle;
		if (this.getCategoryAxis().length > 0){
			categoryTitle = this.getCategoryAxis();
		} else {
			categoryTitle = categoriesConfig.titles.length == 1? categoriesConfig.titles[0]: undefined;
		}
		var categoryAxis = {
		    type: 'Category'
		    , position: categoriesConfig.position
		    , fields: categoriesConfig.fields
		    , title: categoryTitle
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
				title: seriesConfig.titles[i],
	            highlight: {
	            	size: 7,
	                radius: 7
	            },
	            axis: seriesConfig.position,
	            smooth: true,
	            tips: this.getSeriesTips(seriesConfig),
	            xField: categoriesConfig.fields[0],
	            yField: seriesConfig.fields[i],
	            listeners: {
	    	    	itemmousedown: this.onItemMouseDown,
	    	    	scope: this
	    	    },
	    	    label:
	            {
	               display: 'over',
	               field: seriesConfig.fields[i],
	               renderer: function(val) {
	                   return val;
	              }
	            }
	        });
		}


		Sbi.trace("[LineChartWidgetRuntime.getSeries]: OUT");

		return series;
	}

	, getItemMeta: function(item) {
		var itemMeta = {};

		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: IN " + Sbi.toSource(item, true));
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: IN " + Sbi.toSource(item.series, true));
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: IN yField: " + item.series.yField);
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: IN xField: " + item.series.xField);

		// selected categories: names, headers & values
		var categoriesConfig = this.getCategoriesConfig();
		itemMeta.categoryFieldNames = [categoriesConfig.fields[0]];
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
		itemMeta.seriesFieldName = item.series.yField;
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected series name is equal to [" + itemMeta.seriesFieldName +"]");

		itemMeta.seriesFieldHeader = this.getFieldHeaderByName(itemMeta.seriesFieldName);
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected series header is equal to [" + itemMeta.seriesFieldHeader +"]");

		itemMeta.seriesFieldValue = item.storeItem.data[itemMeta.seriesFieldName];
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: selected series value is equal to [" + itemMeta.seriesFieldValue +"]");


		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getItemMeta]: OUT");

		return itemMeta;
	}

	//------------------------------------------------------------------------------------------------------------------
	// utility methods
	// -----------------------------------------------------------------------------------------------------------------
	, onRender: function(ct, position) {
		Sbi.trace("[LineChartWidgetRuntime.onRender]: IN");

		this.msg = 'Sono un widget di tipo LineChart';

		Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime.superclass.onRender.call(this, ct, position);

		Sbi.trace("[LineChartWidgetRuntime.onRender]: OUT");
	}
});



Sbi.registerWidget('linechart-ext', {
	name: 'Line Chart'
	, icon: 'js/src/ext4/sbi/cockpit/widgets/extjs/linechart/img/linechart_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.extjs.linechart.LineChartWidgetRuntime'
	, designerClass: 'Sbi.cockpit.widgets.linechart.LineChartWidgetDesigner'
});