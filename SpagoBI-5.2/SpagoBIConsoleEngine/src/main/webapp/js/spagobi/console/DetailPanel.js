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
		  var pageTitle = this.getTitlePage(conf);
		  conf.title = pageTitle;
		  detailPage = new Sbi.console.DetailPage(conf);
		  this.pages.push(detailPage);
		  //actives only the first tab dataset
		  //var s = conf.storeManager.getStore(detailPage.getStore().getDsLabel());
		  var s = conf.storeManager.getStore(conf.table.dataset);
		  if (s !== undefined){
			  if (i===0){
				  s.stopped = false;
			  }else{
				  s.stopped = true;
			  }		  
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
    , getTitlePage: function(conf){
    	//internationalizes and substitutes parameter values if its necessary
    	var titlePage =  Sbi.locale.getLNValue(conf.title);
    	if (titlePage.indexOf("$P{") !== -1){
    		titlePage = this.getVarConfiguration(titlePage, conf);
    	}
    	return titlePage;
    	
    }
    , getVarConfiguration: function(titleToCheck, conf){
		var startFieldTitle;
		var lenFieldTitle;
		var nameTitleField;
	
		while (titleToCheck.indexOf("$P{") !== -1){
			startFieldTitle = titleToCheck.indexOf("$P{")+3;
			lenFieldTitle = titleToCheck.indexOf("}")-startFieldTitle;
			nameTitleField =  titleToCheck.substr(startFieldTitle,lenFieldTitle);																
			if (nameTitleField){
					var tmpTitleValue = conf.executionContext[nameTitleField] || " ";
					if (tmpTitleValue == "%") tmpTitleValue = " ";					
					
					if (tmpTitleValue){
						var newTitle = titleToCheck.replace("$P{" + nameTitleField + "}", tmpTitleValue);
						titleToCheck = newTitle;
					}
			}else 
				break;
		}
		
		return titleToCheck;
	}
    
});