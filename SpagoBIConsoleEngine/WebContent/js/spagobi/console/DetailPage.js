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
    
    //  -- private methods ---------------------------------------------------------
    , initNavigationToolbar: function(navigationBarConf) {
    	this.navigationToolbar = new Sbi.console.NavigationToolbar(navigationBarConf);
    }
    
    , initGridPanel: function(conf){       
      this.gridPanel = new Sbi.console.GridPanel(conf);
    }
    
    
});