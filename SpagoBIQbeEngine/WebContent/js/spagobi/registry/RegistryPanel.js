/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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