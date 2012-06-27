/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
/**
  * WidgetPanel
  * 
  * handle layout of widgets (maybe also d&d)
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
  */

Ext.ns("Sbi.console");

Sbi.console.WidgetPanel = function(config) {
	
		var defaultSettings = {
			layout:'table'
		  , layoutConfig: {
			   tableAttrs: {
                    style: {width: '100%', height:'100%'}
              }
            }
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.widgetPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.widgetPanel);
		}	
		var c = Ext.apply(defaultSettings, config || {});
	
		//for retrocompatibility with 'column' type layout. 
		if (c.layout !== undefined && c.layout === 'column'){
			delete c.layout;
			c.layout = {}; 
			c.layoutConfig = {};
			c.layout = 'table';
			c.layoutConfig.columns = c.columnNumber;
			c.layoutConfig.tableAttrs = {};
			c.layoutConfig.tableAttrs.style = {};
			c.layoutConfig.tableAttrs.style['float'] = 'left';
		//	c.layoutConfig.tableAttrs.style.width = '100%';

			delete c.columnNumber;
			delete c.columnWidths;		
		}	
		this.widgetContainer = new Sbi.console.WidgetContainer({storeManager: c.storeManager});
		if(c.storeManager) {
			delete c.storeManager;
		}

		if(c.items !== undefined) {
			this.widgetContainer.register(c.items);
			var x = c.items[0];
			delete c.items;
		}	
		Ext.apply(this, c);
		// constructor
		Sbi.console.WidgetPanel.superclass.constructor.call(this, c);	
    
};

Ext.extend(Sbi.console.WidgetPanel, Sbi.console.Widget, {
    
	 widgetContainer: null
    
    //  -- public methods ---------------------------------------------------------
   
    , addWidget: function(widget) {	
		this.widgetContainer.register(widget);	
	}
    
    //  -- private methods ---------------------------------------------------------
    
    , onRender: function(ct, position) {	
    	
		Sbi.console.WidgetPanel.superclass.onRender.call(this, ct, position);
	
	    this.items.each( function(item) {
			this.items.remove(item);
	        item.destroy();           
	    }, this); 
	    
		var widgets = this.widgetContainer.getWidgets();
	
		widgets.each(function(widget, index, length) {
			this.add(widget);
		}, this);	
	}

}); 