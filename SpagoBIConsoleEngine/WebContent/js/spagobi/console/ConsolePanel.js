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

Sbi.console.ConsolePanel = function(config) {

	var defaultSettings = {
		title: LN('sbi.console.consolepanel.title'),
		layout: 'border'
	};
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.consolePanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.consolePanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
	
	var datasetsConfig = c.datasets || [];
	delete c.datasets;
	
	var summaryPanelConfig = c.summaryPanel;
	if(summaryPanelConfig) summaryPanelConfig.executionContext = c.executionContext; 
	delete c.summaryPanel;
	
	var detailPanelConfig = c.detailPanel;
	if(detailPanelConfig) detailPanelConfig.executionContext = c.executionContext; 
	delete c.detailPanel;
		
	Ext.apply(this, c);
		
	this.services = this.services || new Array();	
	this.services['export'] = this.services['export'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'EXPORT_ACTION'
		, baseParams: new Object()
	});
	
	
	
	this.initStoreManager(datasetsConfig);
	
	var items = new Array();	
	
	
	if (summaryPanelConfig !== undefined){
		summaryPanelConfig.storeManager = this.storeManager;
		this.initSummaryPanel(summaryPanelConfig);
		items.push(this.summaryPanel);
	}
	
	if (detailPanelConfig !== undefined){
		detailPanelConfig.storeManager = this.storeManager;
		this.initDetailPanel(detailPanelConfig);
		items.push(this.detailPanel);
	} 
	
	if(this.detailPanel === null) {
		if(!this.summaryPanel === null) {
			items.push({region: 'center', html: 'The console is empty. Please check the template.'});
		} else {
			this.summaryPanel.region = 'center';
		}
	}
	
	// just for test export function
	items.push({
		title: 'Export panel'
		, hidden: true
		, region: 'south'
		, tools: [{
			id:'gear',
			qtip: LN('export as text'),
				hidden: false,
				handler: this.exportConsole,
				scope: this
			}]
		, html: 'Click on the button up here to export console document'
	});
	
	c = Ext.apply(c, {  	
		items: items
	});
	

	// constructor
	Sbi.console.ConsolePanel.superclass.constructor.call(this, c);
	
	onhostmessage(this.exportConsole, this, false, 'export');
};

Ext.extend(Sbi.console.ConsolePanel, Ext.Panel, {
    
    services: null
    , storeManager: null
    , summaryPanel: null
    , detailPanel: null
   
   
    //  -- public methods ---------------------------------------------------------
    
    
    
    //  -- private methods ---------------------------------------------------------
    
    , initStoreManager: function(datasetsConfig) {
	
		this.storeManager = new Sbi.console.StoreManager({datasetsConfig: datasetsConfig});
		
	}
    
    , initSummaryPanel: function(conf) {
		this.summaryPanel = new Sbi.console.SummaryPanel(conf);
	}

	, initDetailPanel: function(conf) {
		this.detailPanel = new Sbi.console.DetailPanel(conf);
	}
	
	, exportConsole: function(format) {
		
		var detailPage = this.detailPanel.getActivePage();
		var columnConfigs = detailPage.gridPanel.getColumnConfigs();
		
		var params = {
			mimeType: 'application/pdf'
			, responseType: 'attachment'
			, datasetLabel: detailPage.getStore().getDsLabel()
			, meta: Ext.util.JSON.encode(columnConfigs)
		};
		
		Sbi.Sync.request({
			url: this.services['export']
			, params: params
		});
	}


    
    
});