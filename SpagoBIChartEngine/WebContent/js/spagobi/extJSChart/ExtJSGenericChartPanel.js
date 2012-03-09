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
 * Authors - Antonella Giachino (antonella.giachino@eng.it)
 */
Ext.define('Sbi.extjs.chart.ExtJSGenericChartPanel', {
    alias: 'widget.ExtJSChartPanel',
    extend: 'Ext.panel.Panel',
   // chart: null,
    template: null, 
    store: null,
    storeManager: null,
    chartStore: null,
    width: null,
    height: null,
    
    constructor: function(config) {
    	var defaultSettings = {
    	};

    	var c = Ext.apply(defaultSettings, config || {});
    	
    	c = Ext.apply(c, {id: 'ExtJSChartPanel'});
    	
    	c.storeId = c.dsLabel;
    	
    	Ext.apply(this, c);
    	
    	this.template = c.template || {};
    	this.template.divId = c.divId || {};
    	delete c.template;
    	this.init(c);
        this.callParent(arguments);
    }

	, init : function (dataConfig) {
		//gets dataset values
		this.loadChartData(dataConfig);
		
		//show the loading mask
		if(this.rendered){
			this.showMask();
		} else{
		//	this.on('afterlayout',this.showMask,this);
		}
	}
	
	, loadChartData: function(dataConfig){
		var requestParameters = {
			    id: dataConfig.dsLabel
			  , label: dataConfig.dsLabel
			  , refreshTime: this.template.refreshtime || 0
			  , dsTypeCd: dataConfig.dsTypeCd
			  , pars: dataConfig.dsPars || []
		};
		var datasets = [];
		datasets.push(requestParameters);	
		this.initStore(datasets, dataConfig.dsLabel);
	}
	
    , initStore: function(config, dsLabel) {
    	this.storeManager = Ext.create('Sbi.extjs.chart.data.StoreManager',{datasetsConfig: config});
		this.store = this.storeManager.getStore(dsLabel);
		if (this.store === undefined) {
			Sbi.exception.ExceptionHandler.showErrorMessage('Dataset with identifier [' + this.storeId + '] is not correctly configurated');			
		}else{		
			this.store.on('load', this.onLoad, this);
			this.store.on('exception', Sbi.exception.ExceptionHandler.onStoreLoadException, this);
		}		
	}

    , adaptStoreForChart: function () {
    	if(this.store!=null){
        	var meta = [],
    	    fields = [],
    	    storeRec = {},
    	    store = {};
        	
        	//defines new fields for the store chart
        	for (var m in this.store.alias2FieldMetaMap){
        		var tmpMeta = this.store.alias2FieldMetaMap[m];
        		var tmpField = tmpMeta[0];
        		meta.push(tmpField.header);        		
        	}
        	// defines new data for the store chart
        	var records = this.store.getRange();
        	for (var i = 0; i < records.length; i++) {
        		storeRec = {};
	    		var rec = records[i];
				if(rec ) {
					for (var j in meta){
						var fieldName =	this.store.getFieldNameByAlias(meta[j]); 
						var fieldValue = rec.get(fieldName);
						if (fieldValue !== undefined) storeRec[meta[j]] = fieldValue;
					}	
					fields.push(storeRec);
				}
	        }

        	this.chartStore  = Ext.create('Ext.data.JsonStore', {
     		    fields: meta,
     		    data: fields
     		});        	

    	}
    }
    
  , onLoad: function(){
	  this.adaptStoreForChart();
      this.createChart();
  }
 
  , createTextObject: function (config){
	  return txtObject = Ext.create('Ext.form.Label', config); 
  }
  
  , getConfigStyle: function (config) {
	  var localStyle = "";
	  var localFont = "Arial";
	  var localFill = "#6D869F"; 	  
	  var localWeight = "bold";
	  var localSize = 16;
	  var width = 100;
	  var height = 50;
	  var text = "";
	  	
	  if (config !== undefined){
		  width =  parseInt(config.width) || 100;
		  height = parseInt(config.height) || 50;
		  text = config.text;	
	  }
	  
	  if (config !== undefined && config.style !== undefined){
		  localSize = parseInt(config.style.fontSize) || 18;
		  localWeight  = config.style.fontWeight || "bold";
		  localFont = config.style.fontFamily || " Arial";
		  localFill = config.style.color || "#6D869F";		 		  
	  }
	  localStyle = 'font-weight:' + localWeight + ';font-size:' + localSize + 'px;font-family:' + localFont +';color:' + localFill + ';';
	 
	  var tagStyle = {text: text || "",
			    	  width: width,
			    	  height: height,
			    	  style: localStyle	    	 
			    	};
	 return tagStyle;
  }
  
 , getThemeConfiguration: function(config) {
       if (config.colors === undefined){
    	   return null;
       }
	   	var  localColors = config.colors.color.split(","),
		   	 localBaseColor = config.colors.baseColor,
		   	 localTitleFill = "",
		   	 localTitleFont = "",
		   	 localLableFill = "",
		   	 localLableFont = "";
	   	
	   	if (config.axesStyle !== undefined){
		   	localTitleFill = config.axesStyle.color || localBaseColor;
		   	localTitleFont = config.axesStyle.fontWeight + " " + config.axesStyle.fontSize + " " + config.axesStyle.fontFamily; 
	   	}else{
	   		localTitleFill = '#6D869F';
	   		localTitleFont: 'bold 18px Arial';
	   	}
	   	if (config.labelsStyle !== undefined){
	   		localLableFill = config.labelsStyle.color || localBaseColor;
		   	localLabelFont = config.labelsStyle.fontWeight + " " + config.labelsStyle.fontSize + " " + config.labelsStyle.fontFamily; 
	   	}else{
	   		localLableFill = '#6D869F';
	   		localLabelFont: 'bold 12px Arial';
	   	}
	   	var themeConfig = {
	        axis: {
	            fill: localTitleFill,
	            stroke: localTitleFill
	        },
	        axisLabelLeft: {
	            fill: localLableFill
	        },
	        axisLabelBottom: {
	            fill: localLableFill
	        }, 
	        axisLabelTop: {
                fill: localLableFill,
                font: localLabelFont
            },
            axisLabelRight: {
                fill: localLableFill,
                font: localLabelFont
            },
	        axisTitleLeft: {
	        	font: localTitleFont,
	            fill: localTitleFill
	        },
	        axisTitleBottom: {
	        	font: localTitleFont,
	            fill: localTitleFill
	        },
	        axisTitleTop: {
	        	font: localTitleFont,
	            fill: localTitleFill
            },
            axisTitleRight: {
            	font: localTitleFont,
	            fill: localTitleFill             
            },
	        colors: localColors
	    };
	   	return themeConfig;
 }
 
});