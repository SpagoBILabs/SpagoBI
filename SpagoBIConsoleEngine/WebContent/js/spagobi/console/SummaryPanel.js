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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.SummaryPanel = function(config) {
	
		var defaultSettings = {
			layout: 'fit'
			, region: 'north'
			, height: 410
			, split: true
			//, collapseMode: 'mini'
			, collapsible: true
	        , collapseFirst: false
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.summaryPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.summaryPanel);
		}
		var widgetPanelConfig = config.layoutManagerConfig || {};
		
		var c = Ext.apply(defaultSettings, config || {});
		widgetPanelConfig.executionContext = c.executionContext;
		Ext.apply(this, c);
		
		widgetPanelConfig.storeManager = this.storeManager;
		widgetPanelConfig.items = [];
		
		for(var i = 0, l1 = config.charts.length ; i < l1; i++) {
			if(config.charts[i].widgetConfig.type === 'chart.composite') {
				//composite widget
				
				//sets the general style of the table 
				widgetPanelConfig.layoutConfig = config.layoutManagerConfig.layoutConfig || {};
				widgetPanelConfig.layoutConfig.tableAttrs = config.layoutManagerConfig.layoutConfig.tableAttrs || {style: {width: '100%'}};
				
				var compositeWidgetPanelConfig = {};
				//******** DEFAULTS configuration applied to each contained panel **********
				
				//dataset 			
				var defaultDataset = widgetPanelConfig.defaults.dataset;						
				//width 
				var defaultWidth =  (widgetPanelConfig.defaults.columnWidthDefault === undefined) ? .1 : widgetPanelConfig.defaults.columnWidthDefault;				
				//heigth 
				var defaultHeight =  (widgetPanelConfig.defaults.columnHeightDefault === undefined) ? .1: widgetPanelConfig.defaults.columnHeightDefault;

			
				//**************** configuration about the SINGLE contained panel *************
				 
				//title
				if (config.charts[i].widgetConfig.linkableDoc !== undefined){
					compositeWidgetPanelConfig.title = (config.charts[i].widgetConfig.linkableDoc.text === undefined) ? "": config.charts[i].widgetConfig.linkableDoc.text;
				}else {
					compositeWidgetPanelConfig.title = (config.charts[i].widgetConfig.title === undefined) ? "" :  config.charts[i].widgetConfig.title;			
				}			
				//colspan
				compositeWidgetPanelConfig.colspan = (config.charts[i].widgetConfig.colspan === undefined) ? 1 : config.charts[i].widgetConfig.colspan;				
				//rowspan
				compositeWidgetPanelConfig.rowspan = (config.charts[i].widgetConfig.rowspan === undefined) ? 1 : config.charts[i].widgetConfig.rowspan;
				//dataset 
				componentDataset = config.charts[i].widgetConfig.dataset;
				//width 
				var componentWidth = config.charts[i].widgetConfig.width;								
				//height 
				var componentHeight = config.charts[i].widgetConfig.height;	
				
				compositeWidgetPanelConfig.storeManager = this.storeManager;
				compositeWidgetPanelConfig.items = [];
								
				for(var j = 0, l2 = config.charts[i].widgetConfig.subcharts.length ; j < l2; j++) {
					var configSubChart = {};
					configSubChart = config.charts[i].widgetConfig.subcharts[j];									
					
					//sets the DATASET; the order for getting values are: single widget, single panel, table
					if (configSubChart.dataset === undefined){
						if (componentDataset !== undefined){
							configSubChart.dataset = componentDataset;
						}
						else{
							configSubChart.dataset = defaultDataset;
						}
					}
					
					
					//sets the WIDTH of single element; the order for getting values are: single widget, single panel, table
					if (configSubChart.width === undefined){
						if (componentWidth !== undefined){
							configSubChart.width = componentWidth/l2; //divides total space by the number of elements
						}
						else{
							configSubChart.width = defaultWidth/l2; //divides default total space by the number of elements
						}
					}					
					//apply the colspan
					configSubChart.width = (configSubChart.width * compositeWidgetPanelConfig.colspan);				
										
					//percentage dimensions: the single widget occupies the total space; otherwise if the value is not a valid number,
					//deletes the property from configuration (for IE problem) 
					if (configSubChart.width <= 1){						
						configSubChart.width = "100%";								
					}else if (configSubChart.width === undefined || isNaN(configSubChart.width) ){
						delete configSubChart.width;						
					}
					
					//sets the HEIGHT of single element; the order for getting values are: single widget, single panel, table
					if (configSubChart.height === undefined){
						if (componentHeight !== undefined){
							configSubChart.height = componentHeight; 
						}
						else{
							configSubChart.height = defaultHeight;
						}
					}					
					//apply the rowspan
					configSubChart.height = (configSubChart.height * compositeWidgetPanelConfig.rowspan);
				
					//percentage dimensions: the single widget occupies the total space; otherwise if the value is not a valid number,
					//deletes the property from configuration (for IE problem) 
					if (configSubChart.height <= 1){						
						configSubChart.height = "100%";			
					}else if (configSubChart.height === undefined || isNaN(configSubChart.height) ){
						delete configSubChart.height;							
					}
					configSubChart.executionContext = widgetPanelConfig.executionContext;
					
					//sets the dimensions on the parent panel
					compositeWidgetPanelConfig.width = (componentWidth > configSubChart.width)? componentWidth : configSubChart.width;
					compositeWidgetPanelConfig.height = (componentHeight > configSubChart.height)? componentHeight : configSubChart.height;	
					compositeWidgetPanelConfig.linkableDoc = config.charts[i].linkableDoc;
					compositeWidgetPanelConfig.executionContext = widgetPanelConfig.executionContext;
					
					compositeWidgetPanelConfig.items.push(new Sbi.console.ChartWidget(configSubChart));
				}
				var compositeWidgetPanel = new Sbi.console.WidgetPanel(compositeWidgetPanelConfig);
				widgetPanelConfig.items.push(compositeWidgetPanel);
				
			} else {
				//simple widget
				widgetPanelConfig.items.push(new Sbi.console.ChartWidget(config.charts[i]));
			}
		}		
		widgetPanelConfig.autoScroll = {};
		widgetPanelConfig.autoScroll = true;
		var widgetPanel = new Sbi.console.WidgetPanel(widgetPanelConfig);
		
		c = Ext.apply(c, {  	
			items: [widgetPanel]
		});
			
		// constructor
		Sbi.console.SummaryPanel.superclass.constructor.call(this, c);			
};

Ext.extend(Sbi.console.SummaryPanel, Ext.Panel, {
    
    services: null
    
   
    //  -- public methods ---------------------------------------------------------
    
    
    
    //  -- private methods ---------------------------------------------------------
    
    
    
    
});