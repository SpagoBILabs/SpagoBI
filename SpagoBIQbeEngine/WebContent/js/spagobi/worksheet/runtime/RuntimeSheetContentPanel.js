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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeSheetContentPanel = function(config) { 

	var defaultSettings = {};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.runtime.runtimeSheetContentPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeSheetContentPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.content = this.initContent(c);
	//this.content = new Ext.Panel({height: 500});
	c = {
		border: false,
		style:'padding:5px 15px 5px',
		items: this.content,
		autoHeight: true
	}
	Sbi.worksheet.runtime.RuntimeSheetContentPanel.superclass.constructor.call(this, c);	

};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetContentPanel, Ext.Panel, {
	content: null,

	
	initContent: function (c) {
		var items = [];
		switch (this.contentConfig.designer) {
	        case 'Pivot Table':
	        	return new Sbi.crosstab.CrosstabPreviewPanel(Ext.apply(c|| {},{
	        			crosstabConfig: {autoHeight: true}, 
	        			title: false})
	        	);
	        case 'Bar Chart':
	        	return new Sbi.worksheet.runtime.RuntimeBarChartPanel(this.contentConfig);
	        case 'Line Chart':
	        	this.insertLinechartDesigner();
	            break;
	        case 'Pie Chart':
	        	this.insertPiechartDesigner();
	            break;
	        case 'Table':
	        	return new Sbi.formviewer.DataStorePanel({
	        		split: true,
	        		collapsible: false,
	        		padding: '0 20 0 0',
	        		autoScroll: false,
	        		frame: false, 
	        		border: false,
	        		displayInfo: false,
	        		pageSize: 50,
	        		autoHeight: true,
	        		sortable: false,
	        		gridConfig: {
	        			autoHeight: true
	        		},
	        		services: {
	        			loadDataStore: Sbi.config.serviceRegistry.getServiceUrl({
	        				serviceName: 'EXECUTE_WORKSHEET_QUERY_ACTION'
	        				, baseParams: new Object()
	        			})
	        		}
	        	});
	        default: 
	        	alert('Unknown widget!');
		}
		this.doLayout();
		this.updateContent();
	},
	
	
//	initTable(): function{
//		
//	},
//	
//	initTable(): function{
//		
//	},
//	
//	initTable(): function{
//		
//	},
	
	
	
	updateContent: function () {
		var items = [];
		switch (this.contentConfig.designer) {
	        case 'Pivot Table':
	        	this.content.load(this.contentConfig.crosstabDefinition);
	            break;
	        case 'Bar Chart':
	        	this.remove(this.content);
	        	this.content= new Sbi.worksheet.runtime.RuntimeBarChartPanel(this.contentConfig);
	        	this.add(this.content);
	            break;
	        case 'Line Chart':
	        	this.insertLinechartDesigner();
	            break;
	        case 'Pie Chart':
	        	this.insertPiechartDesigner();
	            break;
	        case 'Table':
	        	this.content.execQuery({});
	            break;
	        default: 
	        	alert('Unknown widget!');
		}
		
		this.doLayout();
	}
	
	
	
	

});
