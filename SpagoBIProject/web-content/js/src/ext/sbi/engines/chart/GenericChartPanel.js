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
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Antonella Giachino(antonella.giachino@eng.it)
  */

Ext.ns("Sbi.engines.chart");

Sbi.engines.chart.GenericChartPanel  = function(config) { 
	alert("generic chart!!");
	var defaultSettings = {
			border: false
	};


	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);

	this.services = this.services || new Array();
	var params = {};
	
	this.services['loadData'] = this.services['loadData'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_DATASETS_ACTION'
		, baseParams: params
	});

	//constructor
	Sbi.engines.chart.GenericChartPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.engines.chart.GenericChartPanel, Ext.Panel, {
	loadMask: null,
	services: null,
	dataContainerObject: null//the object with the data for the panel
	
	/**
	 * Loads the data for the chart.. Call the action which loads the data 
	 * (uses the test method of the manageDataset class) 
	 */
	, loadChartData: function(dataConfig){
		//this.showMask();
		alert("dataConfig: " + dataConfig.toSource());
		var requestParameters = {
				MESSAGE_DET: "DATASET_EXEC"
			  , id: dataConfig.dsId
			  , dsTypeCd: dataConfig.dsTypeCd
			  , pars: dataConfig.dsPars
			  , trasfTypeCd: dataConfig.dsTransformerType
			  , start: -1 	// to get all values
			  , limit: -1	// to get all values
		}
		Ext.Ajax.request({
	        url: this.services['loadData'],//load the data from the server
	        params: requestParameters,
	        success : function(response, opts) {
        		//this.hideMask();
	        	this.dataContainerObject = Ext.util.JSON.decode( response.responseText );
	        	alert(this.dataContainerObject.toSource());
	        	this.createChart();
	        },
	        scope: this,
			failure: function(response, options) {
				//this.hideMask();
				Sbi.exception.ExceptionHandler.handleFailure(response, options);
			}      
		});
	}
	
	/**
	 * Load the categories for the chart
	 */
	, getCategories: function(){
		if(this.dataContainerObject!=null){
			var measures = this.dataContainerObject.columns.node_childs;
			var categories = [];
			for(var i=0; i<measures.length; i++){
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
			for(var i=0; i<measures_metadata.length; i++){
				measures_metadata_map[measures_metadata[i].name] ={'format':measures_metadata[i].format, 'type': measures_metadata[i].type};
			}
			var series = [];
			var serie;
			var map ;
			var serieData, serieDataFormatted;
			for(var i=0; i<seriesNames.length; i++){
			      serie = {};
			      serie.name =   seriesNames[i].node_key;
			      serieData = this.dataContainerObject.data[i];
			      serieDataFormatted = [];
			      for(var j=0; j<serieData.length; j++){
			    	  map = measures_metadata_map[serie.name];
			    	  serieDataFormatted.push(this.format(serieData[j], map.type, map.format ));
			      }
			      serie.data = serieDataFormatted;
			      series.push(serie);
			}	
			return series;
		}
	}
	
	/**
	 * Opens the loading mask 
	 */
    , showMask : function(){
    	if (this.loadMask == null) {
    		this.loadMask = new Ext.LoadMask(this.getId(), {msg: "Loading.."});
    	}
    	this.loadMask.show();
    }
	
	/**
	 * Closes the loading mask
	 */
	, hideMask: function() {
    	if (this.loadMask != null) {
    		this.loadMask.hide();
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
    
});



