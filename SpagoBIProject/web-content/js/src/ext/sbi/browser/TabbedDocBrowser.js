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
	// if browser is IE, re-inject parent.execCrossNavigation function in order to solve parent variable conflict that occurs when 
	// more iframes are built and the same function in injected: it is a workaround that let cross navigation work properly
	if (Ext.isIE) {
		this.brTab.on(
				'tabchange',
				function  ( thisTabPanel, anActiveTab ) {
					var act = thisTabPanel.getActiveTab();
					try {
						if (act !== undefined && act.getActiveDocument()) {
							
								var documentPage = act.getActiveDocument().getDocumentExecutionPage();
								if (documentPage.isVisible()) {
									documentPage.injectCrossNavigationFunction();
								}
	
						}
					} catch (e) {}
				}
				, this
		);
	}
	//send messages about enable or disable datastore refresh action (for console engine) 
	this.brTab.on(
	   'beforetabchange',
	   function (tabPanel, newTab, currentTab ) {
		   if(currentTab && currentTab.tabType === 'document' && currentTab.getActiveDocument() && currentTab.getActiveDocument().getDocumentExecutionPage()) {
			   currentTab.getActiveDocument().getDocumentExecutionPage().getDocumentPage().sendMessage('Disable datastore', 'hide');
		   }
		   if(newTab.tabType === 'document' && newTab.getActiveDocument() && newTab.getActiveDocument().getDocumentExecutionPage()){
			   newTab.getActiveDocument().getDocumentExecutionPage().getDocumentPage().sendMessage('Enable datastore', 'show');
		   }
	   }
	   , this
	);
}


Ext.extend(Sbi.browser.TabbedDocBrowser, Ext.Panel, {
	brTab: null
});