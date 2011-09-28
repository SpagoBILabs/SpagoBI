/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
  *  loadChartData(dataConfig): load the data for the chart
  *  getCategories(): Load the categories for the chart
  *  getSeries(): Load the series for the chart
  * 
  * 
  * Public Events
  * 
  *  contentloaded: fired after the data has been loaded
  * 
  * Authors
  * 
  * - Alberto Ghedin (alberto.ghedin@eng.it)
  */

Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeGenericChartPanel  = function(config) { 
	
	var defaultSettings = {
			border: false
	};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeGenericChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeGenericChartPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);

	this.style='width: 80%';
	
	this.services = this.services || new Array();
	var params = {};
	this.services['loadData'] = this.services['loadData'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'LOAD_CROSSTAB_ACTION'
		, baseParams: params
	});
//	this.services['exportChart'] = this.services['exportChart'] || Sbi.config.serviceRegistry.getServiceUrl({
//		serviceName: 'EXPORT_CHART_ACTION'
//		, baseParams: params
//	});

	this.addEvents('contentloaded');
	Sbi.worksheet.runtime.RuntimeGenericChartPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeGenericChartPanel, Ext.Panel, {
	loadMask: null,
	services: null,
	sheetName: null,
	dataContainerObject: null//the object with the data for the panel
	
	/**
	 * Loads the data for the chart.. Call the action which loads the crosstab 
	 * (the crosstab is the object that contains the data for the chart)
	 * @param dataConfig the field for the chart..
	 * The syntax is {rows, measures}.. For example {'rows':[{'id':'it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productFamily','nature':'attribute','alias':'Product Family','iconCls':'attribute'}],'measures':[{'id':'it.eng.spagobi.SalesFact1998:storeCost','nature':'measure','alias':'Store Cost','funct':'SUM','iconCls':'measure'},{'id':'it.eng.spagobi.SalesFact1998:unitSales','nature':'measure','alias':'Unit Sales','funct':'SUM','iconCls':'measure'}]}
	 */
	, loadChartData: function(dataConfig, filters){
		
		if ( !this.chartConfig.hiddenContent ){
			var requestParameters = {
					'crosstabDefinition': Ext.util.JSON.encode({
						'rows': [],
						'columns': dataConfig.rows,
						'measures': dataConfig.measures,
						'config': {'measureson':'rows'},
					})
					, 'sheetName' : this.sheetName
			};
			if ( filters != null ) {
				requestParameters.optionalfilters = Ext.encode(filters);
			}
			Ext.Ajax.request({
		        url: this.services['loadData'],//load the crosstab from the server
		        params: requestParameters,
		        success : function(response, opts) {
		        	
		        	this.dataContainerObject = Ext.util.JSON.decode( response.responseText );
		        	if (this.isEmpty()) {
		        		this.update(' <div id="' + this.chartDivId + '" style="width: 100%; height: 100%;"></div>');
		    			Ext.Msg.show({
		 				   title: LN('sbi.qbe.messagewin.info.title'),
		 				   msg: LN('sbi.qbe.datastorepanel.grid.emptywarningmsg'),
		 				   buttons: Ext.Msg.OK,
		 				   icon: Ext.MessageBox.INFO
		    			});
		    			this.fireEvent('contentloaded');
		        	} else {
			        	if(this.rendered){
			        		this.createChart();
			        		this.fireEvent('contentloaded');
			        	}else{
			        		this.on('afterrender',function(){this.createChart();this.fireEvent('contentloaded');}, this);
			        	}
			        	
		        	}
		        	
		        },
		        scope: this,
				failure: function(response, options) {
					this.fireEvent('contentloaded');
					Sbi.exception.ExceptionHandler.handleFailure(response, options);
				}      
			});
		}else{
        	if(this.rendered){
        		this.fireEvent('contentloaded');
        	}else{
        		this.on('afterrender',function(){this.fireEvent('contentloaded');}, this);
        	}
		}
	}

	, isEmpty : function () {
		var measures = this.dataContainerObject.columns.node_childs;
		return measures === undefined;
	}
	
	/**
	 * Load the categories for the chart
	 */
	, getCategories: function(){
		if(this.dataContainerObject!=null){
			var measures = this.dataContainerObject.columns.node_childs;
			var categories = [];
			var i=0;
			for(; i<measures.length; i++){
				categories.push(measures[i].node_key);
			}
			return  categories;
		}
	}
	
	/**
	 * Loads the series for the chart
	 */
	, getSeries: function(){
		if(this.dataContainerObject!=null){
			var seriesNames = this.dataContainerObject.rows.node_childs;
			var data = this.dataContainerObject.data;
			var measures_metadata = this.dataContainerObject.measures_metadata;
			var measures_metadata_map = {};
			//load the metadata of the measures (we need the type)
			var i=0;
			for(; i<measures_metadata.length; i++){
				measures_metadata_map[measures_metadata[i].name] ={'format':measures_metadata[i].format, 'type': measures_metadata[i].type};
			}
			var series = [];
			var serie;
			var map ;
			var serieData, serieDataFormatted;
			i=0;
			for(; i<seriesNames.length; i++){
			      serie = {};
			      serie.name =   seriesNames[i].node_key;
			      serieData = this.dataContainerObject.data[i];
			      serieDataFormatted = [];
			      var j=0;
			      for(; j<serieData.length; j++){
			    	  map = measures_metadata_map[serie.name];
			    	  serieDataFormatted.push(this.format(serieData[j], map.type, map.format ));
			      }
			      serie.data = serieDataFormatted;
			      series.push(serie);
			}	
			return series;
		}
	}
	
    , format: function(value, type, format) {
    	if(value==null){
    		return value;
    	}
		try {
			var valueObj = value;
			if (type == 'int') {
				valueObj = parseInt(value);
			} else if (type == 'float') {
				valueObj = parseFloat(value);
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
    
	, getDataLabelsFormatter: function () {
		var showPercentage = this.chartConfig.showpercentage;
		var chartType = this.chartConfig.designer;
		var allSeries = this.chartConfig.series;
		
		var toReturn = function () {
			var theSerieName = this.series.name;
			var serieDefinition = null;
			
			// find the serie configuration
			var i = 0;
			for (; i < allSeries.length; i++) {
				if (allSeries[i].seriename === theSerieName) {
					serieDefinition = allSeries[i];
					break;
				}
			}
			
			// format the value according to serie configuration
			var value = Sbi.qbe.commons.Format.number(this.y, {
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
			
			var dataLabel = null;
			if (chartType == 'Pie Chart') {
				dataLabel = '<b>'+ this.point.name +'</b>: ' + value;
			} else {
				dataLabel = value;
			}
			
			// display percentage if needed
			if (showPercentage) {
				dataLabel += ' ( ' + Ext.util.Format.number(this.percentage, '0.00') + ' %)';
			}
			
			return dataLabel;
			
		};
		return toReturn;
	}
	
	, getTooltipFormatter: function () {
		var showPercentage = this.chartConfig.showpercentage;
		var chartType = this.chartConfig.designer;
		var allSeries = this.chartConfig.series;
		
		var toReturn = function () {
			
			var theSerieName = this.series.name;
			var serieDefinition = null;
			
			// find the serie configuration
			var i = 0;
			for (; i < allSeries.length; i++) {
				if (allSeries[i].seriename === theSerieName) {
					serieDefinition = allSeries[i];
					break;
				}
			}
			
			// format the value according to serie configuration
			var value = Sbi.qbe.commons.Format.number(this.y, {
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
			
			var tooltip = null;
			if (chartType == 'Pie Chart') {
				tooltip = '<b>' + this.point.name + '</b><br/>' + this.series.name + ': ' + value;
			} else {
				tooltip = '<b>' + this.x + '</b><br/> ' + this.series.name + ': ' + value;
			}
			
			// display percentage if needed
			if (showPercentage) {
				tooltip += ' ( ' + Ext.util.Format.number(this.percentage, '0.00') + ' %)';
			}
			
			return  tooltip;
			
		};
		
		return toReturn;
	}
	
	, exportContent: function() {
		var svg = this.chart.getSVG();
		var exportedChart = {SVG: svg, SHEET_TYPE: 'CHART'};
		return exportedChart;
	}

});