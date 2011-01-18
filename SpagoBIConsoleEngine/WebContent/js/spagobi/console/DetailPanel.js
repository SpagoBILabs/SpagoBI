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

Sbi.console.DetailPanel = function(config) {

		var defaultSettings = {
			layout: 'fit'
			, bodyStyle: 'padding: 8px'
			, region: 'center'
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.detailPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.detailPanel);
		}
		
		var c = Ext.apply(defaultSettings, config || {});		
		
		var pagesConfig = c.pages || [];
		pagesConfig.executionContext = c.executionContext;
		pagesConfig.storeManager = c.storeManager;
		delete c.pages;
		
		Ext.apply(this, c);
		
		this.initDetailPages(pagesConfig);
		this.initTabPanel();
		
		c = Ext.apply(c, {  
	      	items: [this.tabPanel]
		});

		// constructor
		Sbi.console.DetailPanel.superclass.constructor.call(this, c);
    
		//this.addEvents();
};

Ext.extend(Sbi.console.DetailPanel, Ext.Panel, {
    
    //services: null
    pages: null
    , activePage: null
    , tabPanel: null
    
   
    //  -- public methods ---------------------------------------------------------
    
    , getActivePage: function() {
		return this.activePage;
	}
    
    //  -- private methods ---------------------------------------------------------
    
    , initDetailPages: function(pagesConfig) {
		this.pages = new Array();
		var detailPage = null;		
		for(var i = 0, l = pagesConfig.length; i < l; i++) {
		  var conf = pagesConfig[i];
		  conf.executionContext = pagesConfig.executionContext; 
		  conf.storeManager = pagesConfig.storeManager;
		  detailPage = new Sbi.console.DetailPage(conf);
		  this.pages.push(detailPage);
		  //actives only the first tab dataset
		  var s = conf.storeManager.getStore(detailPage.getStore().getDsLabel());
		  if (i===0){
			  s.stopped = false;
		  }else{
			  s.stopped = true;
		  }		  
		}
	}

	, initTabPanel: function() {
		this.tabPanel = new Ext.TabPanel({
      		activeTab: 0
      		, items: this.pages
      	});

		this.tabPanel.on('tabchange',function (tabPanel, tab) {
			
			if (tabPanel !== undefined && tab !== undefined && this.activePage != undefined){
				for(var i = 0, l = this.pages.length; i < l; i++) {
					  var tmpPage =  this.pages[i];
					  var s = tmpPage.getStore();
					  //loads only the first tab dataset
					  if (tmpPage.getStore().getDsLabel() === this.activePage.getStore().getDsLabel()){
						  s.stopped = true;						 
					  }else if (tmpPage.getStore().getDsLabel() === tab.getStore().getDsLabel()){
						  s.stopped = false;
						  
						  //force refresh data
						  s.load({
								params: {}, 
								callback: function(){this.ready = true;}, 
								scope: s, 
								add: false
							});
							
					  }						  
					}
				
			}
			this.activePage = tab;
		}, this );
	}
    
    
});