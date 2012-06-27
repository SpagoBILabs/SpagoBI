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
  */

Ext.ns("Sbi.registry");

Sbi.registry.RegistryPanel = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.registry.registrypanel.title')
	};
		
	if(Sbi.settings && Sbi.settings.registry && Sbi.settings.registry.registryPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.registry.registryPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		layout: 'fit',
		autoScroll: true,
		items: [this.registryGridPanel]
	});
	
	// constructor
    Sbi.registry.RegistryPanel.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.registry.RegistryPanel, Ext.Panel, {
	
	registryConfiguration : null
	
	, init: function () {
		this.registryGridPanel = new Sbi.registry.RegistryEditorGridPanel({
			registryConfiguration : this.registryConfiguration || {}
		});
		this.registryGridPanel.on('afterrender', function () {
			this.registryGridPanel.load();
		}, this);
	}

});