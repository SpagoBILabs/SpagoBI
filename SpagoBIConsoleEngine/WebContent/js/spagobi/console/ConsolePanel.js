/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
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
	/*
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
	*/
	c = Ext.apply(c, {  	
		items: items
	});
	
	
	// constructor
	Sbi.console.ConsolePanel.superclass.constructor.call(this, c);
	
	//WORKAROUND: on FF is possible to have a problem with the definition of the objects because is faster than other browser,
	//so it gives the error: 'onhostmessage is not defined.' So, now, it forces a pause before to define the callbacks on events
	if (Ext.isIE){
		onhostmessage(this.exportConsole, this, false, 'export');
		onhostmessage(this.onHide, this, false, 'hide');
		onhostmessage(this.onShow, this, false, 'show');
	}else{
		var that = this;
		var setHostMsg = function(){
			that.setHostMessagges();
		}; 
		setTimeout(setHostMsg, 8000); 
	}
	
	//manages the stop refresh on popup windows call
	if (this.detailPanel){
		for(var i = 0; i< this.detailPanel.pages.length; i++){
			this.detailPanel.pages[i].gridPanel.on('lock', this.onHide, this);
			this.detailPanel.pages[i].gridPanel.on('unlock', this.onShow, this);
		}
	}
	
	//this.refreshData();

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
		var dsHeadersLabel = (detailPage.getStoreLabels() !== undefined)?detailPage.getStoreLabels().getDsLabel() : "";
		var params = {
			mimeType: 'application/pdf'
			, responseType: 'attachment'
			, datasetHeadersLabel: dsHeadersLabel	
			,meta: Ext.util.JSON.encode(columnConfigs)
		};
		Sbi.Sync.request({
			url: this.services['export']
		  , params: params
		});
	
	}

	
	//stop all datastore of the hidden console 
	, onHide: function(){
		this.storeManager.stopRefresh(true);
	}
	
	//active all datastore of the active console
	, onShow: function(datasetConfig){
		this.storeManager.stopRefresh(false);
	}
	
	, setHostMessagges: function() {
		onhostmessage(this.exportConsole, this, false, 'export');
		onhostmessage(this.onHide, this, false, 'hide');
		onhostmessage(this.onShow, this, false, 'show'); 
	}
	
	, refreshData: function(){
		this.storeManager.forceRefresh();
	}
	
	/*
	, pause:  function (millis){
		var date = new Date();
		var curDate = null;
	
		do { curDate = new Date(); }
		while(curDate-date < millis);
	}*/

});