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
  * - name (mail)
  */

Ext.ns("Sbi.console");

Sbi.console.Xxxx = function(config) {
	
		var defaultSettings = {
			//title: LN('sbi.qbe.queryeditor.title')
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.queryBuilderPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.queryBuilderPanel);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		
		Ext.apply(this, c);
		
		
		this.services = this.services || new Array();	
		this.services['doThat'] = this.services['doThat'] || Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'DO_THAT_ACTION'
			, baseParams: new Object()
		});
		
		this.addEvents('customEvents');
		
		
		this.initThis(c.westConfig || {});
		this.initThat(c.westConfig || {});
	
		c = Ext.apply(c, {  	
	      	items: [this.thisPanel, this.thatPanel]
		});

		// constructor
		Sbi.console.Xxxx.superclass.constructor.call(this, c);
    
		this.addEvents();
};

Ext.extend(Sbi.console.Xxxx, Ext.util.Observable, {
    
    services: null
    
   
    // public methods
    
   
    
    
    // private methods
    
    
    
    
});