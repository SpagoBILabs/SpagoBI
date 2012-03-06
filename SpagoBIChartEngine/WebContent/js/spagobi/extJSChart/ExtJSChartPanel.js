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
Ext.define('Sbi.extjs.chart.ExtJSChartPanel', {
    alias: 'widget.ExtJSChartPanel',
    extend: 'Sbi.extjs.chart.ExtJSGenericChartPanel',
    chart: null,
    
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

  , createChart: function(){
	    var config =  Ext.apply(this.template || {});	    
		config.renderTo = config.divId;
	   	config.store = this.chartStore;
	   	config.animate = (!config.animate)?true:config.animate;	
	   	
	   	//defines dimension
	   	this.width = (!config.width)?500:parseInt(config.width);
	   	this.height =  ((!config.height)?500:parseInt(config.height));
	   	if (config.title !== undefined) 	this.height += 50;
	   	if (config.subtitle !== undefined) 	this.height += 50;
	   	config.width = this.width;
	   	config.height = this.height;
	   	
	   	//updates theme
	   	var localColors = ['#b1da5a', '#4ce0e7', '#e84b67', '#da5abd', '#4d7fe6', '#fec935'];
	   	var localBaseColor = '#6D869F';
	   	
	   	var themeConfig = {
            axis: {
                fill: localBaseColor,
                stroke: localBaseColor
            },
            axisLabelLeft: {
                fill: localBaseColor
            },
            axisLabelBottom: {
                fill: localBaseColor
            },
            axisTitleLeft: {
                fill: localBaseColor
            },
            axisTitleBottom: {
                fill: localBaseColor
            },
            colors: localColors,
    	    baseColor: localBaseColor
        };


	   	var theme = Ext.create('Ext.chart.theme.ExtJSChartTheme', themeConfig);
	   
	   	/*
	   	Ext.define('Ext.chart.theme.ExtJSChartTheme', {
	   	    extend: 'Ext.chart.theme.Base',
	   	    colors : ['#b1da5a', '#4ce0e7', '#e84b67', '#da5abd', '#4d7fe6', '#fec935'],

	   	   	baseColor : '#b1da5a',
	   	        
	   	    constructor: function(config) {
	   	        this.callParent([Ext.apply({
	   	            axis: {
	   	                fill: config.baseColor,
	   	                stroke: baseColor
	   	            },
	   	            axisLabelLeft: {
	   	                fill: baseColor
	   	            },
	   	            axisLabelBottom: {
	   	                fill: baseColor
	   	            },
	   	            axisTitleLeft: {
	   	                fill: baseColor
	   	            },
	   	            axisTitleBottom: {
	   	                fill: baseColor
	   	            },
	   	            colors: colors
	   	        }, config)]);
	   	    }});
*/
	  	config.theme = 'ExtJSChartTheme';
	   	
        if (this.chart){
        	//update the store and redraw the chart
        	this.chart.store = this.chartStore;
        	this.chart.redraw();
        }else{
        	//Creates the new (initial) instance of chart
        	this.chart = Ext.create('Ext.chart.Chart',config);
        	//Adds title and subtitle
        	var configTitle = this.getConfigStyle(config.title);
        	var title = this.createTextObject(configTitle);        	        	
        	title.show(true);
        	var configSubtitle = this.getConfigStyle(config.subtitle, 2);
			var subtitle = this.createTextObject(configSubtitle);        	        	
			subtitle.show(true);
        }
  }

});