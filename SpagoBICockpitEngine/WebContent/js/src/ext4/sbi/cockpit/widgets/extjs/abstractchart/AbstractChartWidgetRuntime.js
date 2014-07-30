/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


/*
 * NOTE: This class is meant to be extended and not directly istantiated
 */
Ext.ns("Sbi.cockpit.widgets.extjs.abstractchart");

Sbi.cockpit.widgets.extjs.abstractchart.AbstractChartWidgetRuntime = function(config) {	
	Sbi.trace("[AbstractChartWidgetRuntime.constructor]: IN");
	
	var defaultSettings = {
		layout: 'fit', 	
		fieldsSelectionEnabled: true
	};

	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.extjs.abstractchart.AbstractChartWidgetRuntime', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);

	var categories = [];
	categories.push(this.wconf.category);
	if(this.wconf.groupingVariable) categories.push(this.wconf.groupingVariable);
	
	this.aggregations = {
		measures: this.wconf.series,
		categories: categories
	};
	Sbi.trace("[AbstractChartWidgetRuntime.constructor]: aggregations properties set to [" + Sbi.toSource(this.aggregations)+ "]");
	
	this.init();
	this.items = this.chart || this.msgPanel;
	
	Sbi.cockpit.widgets.extjs.abstractchart.AbstractChartWidgetRuntime.superclass.constructor.call(this, c);
	
	var bounded = this.boundStore();
	if(bounded) {
		Sbi.trace("[AbstractChartWidgetRuntime.constructor]: store [" + this.getStoreId() + "] succesfully bounded to widget [" + this.getWidgetName() + "]");
	} else {
		Sbi.error("[AbstractChartWidgetRuntime.constructor]: store [" + this.getStoreId() + "] not bounded to widget [" + this.getWidgetName() + "]");
	}

	
	this.reload();
	Sbi.trace("[AbstractChartWidgetRuntime.constructor]: Reloading store [" + this.getStoreId() + "] ...");
	
	this.addEvents('selection');

	Sbi.trace("[AbstractChartWidgetRuntime.constructor]: OUT");
};

Ext.extend(Sbi.cockpit.widgets.extjs.abstractchart.AbstractChartWidgetRuntime, Sbi.cockpit.core.WidgetRuntime, {
	
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
	 * It's the content panel used when the chart is not yet available. By default it's empty. If the config property
	 * msg is passed in then show the msg text. As soon as the chart became available it is removed and destroyed.
	 */
	msgPanel: null
	
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, setContentPanel: function(panel) {
		this.items.each( function(item) {
			this.items.remove(item);
	        item.destroy();           
	    }, this);  
		this.msgContent = null;
        this.add(panel);
        this.doLayout();
	}
	
	, isLegendVisible: function() {
		var showlegend;
		if (this.wconf.showlegend !== undefined){
			showlegend = this.wconf.showlegend;
		} else {
			showlegend = true;
		}
		return showlegend;
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
	
	, getLegendConfiguration: function() {
		var legendConf = {};
		legendConf.position = (Sbi.isValorized(this.wconf.legendPosition))? this.wconf.legendPosition:'bottom';
		return legendConf;
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

	//------------------------------------------------------------------------------------------------------------------
	// utility methods
	// -----------------------------------------------------------------------------------------------------------------
	, init: function() {
		Sbi.trace("[AbstractChartWidgetRuntime.init]: IN");
		this.initMsgPanel();
		this.initChartThemes();
		Sbi.trace("[AbstractChartWidgetRuntime.init]: OUT");
	}
	
	, initMsgPanel: function() {
		this.msgPanel = new Ext.Panel({
			border: false
			, bodyBorder: false
			, hideBorders: true
			, frame: false
			, html: this.msg || ''
		});
	}
	
	, initChartThemes: function() {
		Ext.define('Ext.chart.theme.CustomBlue', {
	        extend: 'Ext.chart.theme.Base',
	        
	        constructor: function(config) {
	            var titleLabel = {
	                font: 'bold 18px Arial'
	            }, axisLabel = {
	                fill: 'rgb(8,69,148)',
	                font: '12px Arial',
	                spacing: 2,
	                padding: 5
	            };
	            
	            this.callParent([Ext.apply({
	               axis: {
	                   stroke: '#084594'
	               },
	               axisLabelLeft: axisLabel,
	               axisLabelBottom: axisLabel,
	               axisTitleLeft: titleLabel,
	               axisTitleBottom: titleLabel
	           }, config)]);
	        }
	    });
	}
	
	//------------------------------------------------------------------------------------------------------------------
	// utility methods
	// -----------------------------------------------------------------------------------------------------------------
	, getColors : function () {
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.init]: IN");
		var colors = [];
		if (this.wconf !== undefined && this.wconf.groupingVariable != null) {
			colors = Sbi.widgets.Colors.defaultColors;
		} else {
			if (this.wconf !== undefined && this.wconf.series !== undefined && this.wconf.series.length > 0) {
				var i = 0;
				for (; i < this.wconf.series.length; i++) {
					colors.push(this.wconf.series[i].color);
				}
			}
		}
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.init]: IN");
		return colors;
	}
	
	, getMeasureScaleFactor: function (theMeasureName){
		var i=0;
		var scaleFactor={value:1, text:''};
		var optionDefinition = null;
		if ( this.fieldsOptions != null) {
			for (; i < this.fieldsOptions.length; i++) {
				if (this.fieldsOptions[i].alias === theMeasureName) {
					optionDefinition = this.fieldsOptions[i];
					break;
				}
			}
			if(optionDefinition!=null){
				legendSuffix = optionDefinition.options.measureScaleFactor;
				if(legendSuffix != undefined && legendSuffix != null && legendSuffix!='NONE'){
					scaleFactor.text = LN('sbi.worksheet.runtime.options.scalefactor.'+legendSuffix);
					switch (legendSuffix)
					{
					case 'K':
						scaleFactor.value=1000;
						break;
					case 'M':
						scaleFactor.value=1000000;
						break;
					case 'G':
						scaleFactor.value=1000000000;
						break;
					default:
						scaleFactor.value=1;
					}
				}
			}
		}
		return scaleFactor;
	}
	
	, formatTextWithMeasureScaleFactor : function(text, measureName) {
		var legendSuffix;
		legendSuffix = (this.getMeasureScaleFactor(measureName)).text;
		
		if (legendSuffix != '' ) {
			return text + ' ' + legendSuffix;
		}
		return text;
	}
	
	, formatLegendWithScale : function(theSerieName) {
		var serie = this.getRuntimeSerie(theSerieName);
		var toReturn = this.formatTextWithMeasureScaleFactor(serie.name, serie.measure);
		return toReturn;
	}
	
    , format: function(value, type, format, scaleFactor) {
    	if(value==null){
    		return value;
    	}
		try {
			var valueObj = value;
			if (type == 'int') {
				valueObj = (parseInt(value))/scaleFactor;
			} else if (type == 'float') {
				valueObj = (parseFloat(value))/scaleFactor;
			} else if (type == 'date') {
				valueObj = Date.parseDate(value, format);
			} else if (type == 'timestamp') {
				valueObj = Date.parseDate(value, format);
			}
			return valueObj;
		} catch (err) {
			return value;
		}
	}	
	
	
	, isEmpty : function () {		
		var measures = undefined;
		
		if (Sbi.isValorized(this.dataContainerObject.columns))
			measures = this.dataContainerObject.columns.node_childs;
		
		return measures === undefined;
	}
	
	, onStoreLoad: function() {
		Sbi.trace("[AbstractChartWidgetRuntime.onStoreLoad][" + this.getId() + "]: IN");
		
		Sbi.cockpit.widgets.extjs.abstractchart.AbstractChartWidgetRuntime.superclass.onStoreLoad.call(this, this.getStore());
		
		
		if(this.getStore().status === "error") {
			return;
		}
		
		if(this.rendered){
    		this.redraw();
    	} else {
    		this.on('afterrender', function(){this.redraw();}, this);
    	}
		Sbi.trace("[AbstractChartWidgetRuntime.onStoreLoad][" + this.getId() + "]: OUT");
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
	, getFieldMetaByName: function(fieldName) {
		var store = this.getStore();
		var fieldsMeta = store.fieldsMeta;
    	for(var h in fieldsMeta) {
    		var fieldMeta = fieldsMeta[h];
    		if(fieldMeta.name == fieldName) {
    			return fieldMeta;
    		}
    	}
    	return null;
	}
	
	, getFieldHeaderByName: function(fieldName) {
		var fieldMeta = this.getFieldMetaByName(fieldName);
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.getFieldHeaderByName]: " + Sbi.toSource(fieldMeta));
		return fieldMeta!=null?fieldMeta.header: null;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, onItemMouseDown: function(item) {
		Sbi.trace("[AbstractCartesianChartWidgetRuntime.onItemMouseDown]: IN");
		var itemMeta = this.getItemMeta(item);
	    var selections = {};
		selections[itemMeta.categoryFieldHeaders[0]] = {values: []};
	    Ext.Array.include(selections[itemMeta.categoryFieldHeaders].values, itemMeta.categoryValues[0]);
	    this.fireEvent('selection', this, selections);
	    Sbi.trace("[AbstractCartesianChartWidgetRuntime.onItemMouseDown]: OUT");
	}
	
	//------------------------------------------------------------------------------------------------------------------
	// test methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, getSampleStore: function() {
		var store = Ext.create('Ext.data.JsonStore', {
	        fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5', 'data6', 'data7', 'data9', 'data9'],
	        data: this.generateData()
	    });
		return store;
	}
	
	, generateData: function(n, floor){
        var data = [],
            p = (Math.random() *  11) + 1,
            i;
            
        floor = (!floor && floor !== 0)? 20 : floor;
        
        for (i = 0; i < (n || 12); i++) {
            data.push({
                name: Ext.Date.monthNames[i % 12],
                data1: Math.floor(Math.max((Math.random() * 100), floor)),
                data2: Math.floor(Math.max((Math.random() * 100), floor)),
                data3: Math.floor(Math.max((Math.random() * 100), floor)),
                data4: Math.floor(Math.max((Math.random() * 100), floor)),
                data5: Math.floor(Math.max((Math.random() * 100), floor)),
                data6: Math.floor(Math.max((Math.random() * 100), floor)),
                data7: Math.floor(Math.max((Math.random() * 100), floor)),
                data8: Math.floor(Math.max((Math.random() * 100), floor)),
                data9: Math.floor(Math.max((Math.random() * 100), floor))
            });
        }
        
        //alert('data: ' + Sbi.toSource(data));
        
        return data;
    }
    
    , generateDataNegative: function(n, floor){
        var data = [],
            p = (Math.random() *  11) + 1,
            i;
            
        floor = (!floor && floor !== 0)? 20 : floor;
            
        for (i = 0; i < (n || 12); i++) {
            data.push({
                name: Ext.Date.monthNames[i % 12],
                data1: Math.floor(((Math.random() - 0.5) * 100), floor),
                data2: Math.floor(((Math.random() - 0.5) * 100), floor),
                data3: Math.floor(((Math.random() - 0.5) * 100), floor),
                data4: Math.floor(((Math.random() - 0.5) * 100), floor),
                data5: Math.floor(((Math.random() - 0.5) * 100), floor),
                data6: Math.floor(((Math.random() - 0.5) * 100), floor),
                data7: Math.floor(((Math.random() - 0.5) * 100), floor),
                data8: Math.floor(((Math.random() - 0.5) * 100), floor),
                data9: Math.floor(((Math.random() - 0.5) * 100), floor)
            });
        }
        return data;
    }

});