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
	var defaultSettings = {
			border: false
	};

	var c = Ext.apply(defaultSettings, config || {});
	c.storeId = c.dsLabel;
	
	Ext.apply(this, c);

	//constructor
	Sbi.engines.chart.GenericChartPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.engines.chart.GenericChartPanel, Ext.Panel, {
	loadMask: null,
	services: null,
	storeManager: null,				//the store manager
	store: null						//the store
	
	/**
	 * Loads the data for the chart.. Call the action which loads the data 
	 * (uses the test method of the manageDataset class) 
	 */
	, loadChartData: function(dataConfig){
		//this.showMask();

		var requestParameters = {
			    id: dataConfig.dsId
			  , label: dataConfig.dsLabel
			  , refreshTime: dataConfig.refreshTime || 0
			  , dsTypeCd: dataConfig.dsTypeCd
			  , pars: dataConfig.dsPars
			  , trasfTypeCd: dataConfig.dsTransformerType
		}
		var datasets = [];
		datasets.push(requestParameters);	
		this.initStore(datasets, dataConfig.dsId);
		this.createChart();
	}
	
	/**
	 * Load the categories for the chart
	 */
	, getCategories: function(){
		if(this.store!=null){
			var categories = [];
			var rec = this.store.getAt(0);
			//alert("rec: " + rec);
			if(rec) {
				//alert("rec: " + rec.toSource());
			
				var fields = this.storeMeta.fields;
				for(var i = 0, l = fields.length, f; i < l; i++) {
					f = fields[i];
					//alert("f: " + f.toSource());
					if( (typeof f) === 'string') {
						f = {name: f};
					}
					var alias = f.header || f.name;
					if(alias === 'recNo') continue;
					
					categories[alias] = rec.get(f.name);
					//alert(categories.toSource());
					/*
					var tmpDescValue = this.getDescriptionColumn(alias);
					if (tmpDescValue!== undefined && tmpDescValue != ''){											
						data[alias] = rec.get(f.name) + '|' + rec.get(this.store.getFieldNameByAlias(tmpDescValue));
					}else{
						data[alias] = rec.get(f.name);
					}
					*/
				}
			}
			/*
			alert(this.store.toSource());
			var measures = this.store.columns.node_childs;
			var categories = [];
			for(var i=0; i<measures.length; i++){
				categories.push(measures[i].node_key);
			}
			*/
			return  categories;
		}
	}
	
	/**
	 * Loads the series for the chart
	 */
	, getSeries: function(){
		if(this.store!=null){
			var seriesNames = this.store.rows.node_childs;
			var data = this.store.data;
			var measures_metadata = this.store.measures_metadata;
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
			      serieData = this.store.data[i];
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
    
    , initStore: function(config, dsId) {
    	this.storeManager = new Sbi.engines.chart.data.StoreManager({datasetsConfig: config});	
		this.store = this.storeManager.getStore(dsId);
		this.store.loadStore();
		if (this.store === undefined) {
			Sbi.Msg.showError('Dataset with identifier [' + this.storeId + '] is not correctly configurated');			
		}else{
			//this.store.remoteSort = false;  //local type		
			this.store.on('load', this.onLoad, this);
			this.store.on('exception', Sbi.exception.ExceptionHandler.onStoreLoadException, this);
			this.store.on('metachange', this.onMetaChange, this);
		}
	}
    
    , onMetaChange: function( store, meta ) {
		var i;
	    var fieldsMap = {};

		var tmpMeta =  Ext.apply({}, meta); // meta;
		var fields = tmpMeta.fields;
		tmpMeta.fields = new Array(fields.length);
		
		for(i = 0; i < fields.length; i++) {
			if( (typeof fields[i]) === 'string') {
				fields[i] = {name: fields[i]};
			}
			
			if (this.columnId !== undefined && this.columnId === fields[i].header ){
				fields[i].hidden = true;
			}
			tmpMeta.fields[i] = Ext.apply({}, fields[i]);
			fieldsMap[fields[i].name] = i;
		}
	   
		//adds numeration column    
		tmpMeta.fields[0] = new Ext.grid.RowNumberer();

		var categories = this.getCategories();
		//alert("this.store: " + this.store.toSource()	);

	}
    
    , onLoad: function(){
    	//alert("onload");
    	var cat =  this.store.find(this.store.getFieldNameByAlias("mese"),"Jun")
    	//alert("cat: " +cat);
/*
		for(var j = 0, len = this.inlineCharts.length; j < len; j++) {
			var idx = this.getColumnModel().findColumnIndex(this.store.getFieldNameByAlias(this.inlineCharts[j].column));
			this.getColumnModel().setRenderer(idx, this.createInlineChartRenderer(this.inlineCharts[j]) );			
		}
		*/
	}
	

    
});



