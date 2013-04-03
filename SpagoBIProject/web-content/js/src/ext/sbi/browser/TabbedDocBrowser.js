/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  

/**
 * 
 * @author MOnica Franceschini (monica.franceschini@eng.it)
 */

Ext.ns("Sbi.browser");

Sbi.browser.TabbedDocBrowser = function(config) {    

	
	config.baseLayout = config.baseLayout || {}; 	
	config.parentTab = this;
    var browser = new Sbi.browser.DocumentsBrowser(config);
    this.brTab = new Ext.TabPanel({
    	activeTab: 0,
	    items: [browser]
    });
    
	var c = ({
			title: 'Browser'
			,layout: 'fit'
	        ,items: [this.brTab]

	});        
    Sbi.browser.TabbedDocBrowser.superclass.constructor.call(this, c);

}


Ext.extend(Sbi.browser.TabbedDocBrowser, Ext.Panel, {
	brTab: null
});