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
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.DetailPage = function(config) {
		var defaultSettings = {
			title: LN('sbi.console.detailpage.title')
			, layout: 'fit'
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.detailPage) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.detailPage);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		var navigationBarConfig = c.navigationBar || {};
		navigationBarConfig.executionContext = c.executionContext;
		delete c.navigationBar;
		var tableConfig = c.table || {};
		tableConfig.executionContext = c.executionContext;
		tableConfig.storeManager = c.storeManager;
		tableConfig.exportName = c.exportName;
		delete c.table;
		Ext.apply(this, c);
		

		
		this.initNavigationToolbar(navigationBarConfig);
		this.initGridPanel(tableConfig);
		
		c = Ext.apply(c, {  	
			//html: this.msg
			tbar: this.navigationToolbar
	      	, items: [this.gridPanel]	      	
		});

		// constructor
		Sbi.console.DetailPage.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.console.DetailPage, Ext.Panel, {
    
    services: null
    , navigationToolbar: null
    , gridPanel: null
   
    //  -- public methods ----------------------------------------------------------
    
    , getStore: function() {
		return this.gridPanel.store;
	}

	, getStoreLabels: function() {
		return this.gridPanel.storeLabels;
	}
    
    //  -- private methods ---------------------------------------------------------
    , initNavigationToolbar: function(navigationBarConf) {
    	this.navigationToolbar = new Sbi.console.NavigationToolbar(navigationBarConf);
    }
    
    , initGridPanel: function(conf){       
      this.gridPanel = new Sbi.console.GridPanel(conf);
    }
    
    
});