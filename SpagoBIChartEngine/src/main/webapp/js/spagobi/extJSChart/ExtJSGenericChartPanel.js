/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

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
	loadMask: null,
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
    	
    	c = Ext.apply(c, {id: 'ExtJSGenericChartPanel'});
    	
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
			//this.on('afterlayout',this.showMask,this);
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
	  var text = "";
	  	
	  if (config !== undefined)  text = config.text;	
	  
	  if (config !== undefined && config.style !== undefined){
		  localSize = parseInt(config.style.fontSize) || 18;
		  localWeight  = config.style.fontWeight || "bold";
		  localFont = config.style.fontFamily || " Arial";
		  localFill = config.style.color || "#6D869F";		 		  
	  }
	  localStyle = 'font-weight:' + localWeight + ';font-size:' + localSize + 'px;font-family:' + localFont +';color:' + localFill + ';';
	 
	  var tagStyle = {text: text || "",
			  		  autoWidth: true,
			  		  autoHeight: true,			    	  
			    	  style: localStyle	    	 
			    	};
	 return tagStyle;
  }
  
 , getThemeConfiguration: function(config) {
       if ( (config.colors === undefined) || (config.colors.color === undefined) ){
    	   return null;
       }
	   	var  localColors = config.colors.color.split(","),
		   	 localBaseColor = config.colors.baseColor,
		   	 localTitleFill = "",
		   	 localTitleFont = "",
		   	 localLabelFill = "",
		   	 localLabelFont = "";
	   	
	   	if (config.axesStyle !== undefined){
		   	localTitleFill = config.axesStyle.color || localBaseColor;
		   	localTitleFont = config.axesStyle.fontWeight + " " + config.axesStyle.fontSize + " " + config.axesStyle.fontFamily; 
	   	}else{
	   		localTitleFill = '#6D869F';
	   		localTitleFont: 'bold 18px Arial';
	   	}
	   	if (config.labelsStyle !== undefined){
	   		localLabelFill = config.labelsStyle.color || localBaseColor;
		   	localLabelFont = config.labelsStyle.fontWeight + " " + config.labelsStyle.fontSize + " " + config.labelsStyle.fontFamily; 
	   	}else{
	   		localLabelFill = '#6D869F';
	   		localLabelFont: 'bold 12px Arial';
	   	}
	   	var themeConfig = {
	        axis: {
	            fill: localTitleFill,
	            stroke: localTitleFill
	        },
	        axisLabelLeft: {
	            fill: localLabelFill
	        },
	        axisLabelBottom: {
	            fill: localLabelFill
	        }, 
	        axisLabelTop: {
                fill: localLabelFill,
                font: localLabelFont
            },
            axisLabelRight: {
                fill: localLabelFill,
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
	 /**
     * Opens the loading mask 
	 */
	 , showMask : function(){
	 	this.un('afterlayout',this.showMask,this);
	 	if (this.loadMask == null) {    		
	 		this.loadMask = new Ext.LoadMask('ExtJSGenericChartPanel', {msg: "Loading.."});
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
 
});