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
	   	
	   	//defines dimensions 
	   	config.width = (!config.width)?500:parseInt(config.width);
	   	config.height = ((!config.height)?500:parseInt(config.height));
	   	
	   	//updates theme
	   	var themeConfig = this.getThemeConfiguration(config);
	   	if (themeConfig !== null){
		   	Ext.define('Ext.chart.theme.ExtJSChartTheme', {
		   	    extend: 'Ext.chart.theme.Base',	       
		   	    constructor: function(config) {
		   	        this.callParent([Ext.apply(themeConfig, config)]);
		   	    }});		
		    config.theme = 'ExtJSChartTheme';
	   	}
	   	
        if (this.chart){
        	//update the store and redraw the chart
        	this.chart.store = this.chartStore;
        	this.chart.redraw();
        }else{
        	//Creates the new (initial) instance of chart
        	this.chart = Ext.create('Ext.chart.Chart',config);
	    	
        	//Adds title and subtitle        	
        	var configTitle = this.getConfigStyle(config.title);
        	configTitle.renderTo = config.divId + '_title';
        	var title = this.createTextObject(configTitle);        	        	
        	var configSubtitle = this.getConfigStyle(config.subtitle, 2);
        	configSubtitle.renderTo = config.divId + '_subtitle';
			var subtitle = this.createTextObject(configSubtitle);                	
        }
  }

});