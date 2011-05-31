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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.WorkSheetsRuntimePanel = function(template, config) { 
	
	var defaultSettings = {
		title: LN('sbi.worksheet.runtime.worksheetruntimepanel.title')
	};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.workSheetsRuntimePanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.workSheetsRuntimePanel);
	}

	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.initPanels(template);

	c = Ext.apply(c, {
		id: 'runtimeworksheet',
		border: false,
		layout: 'fit',
		autoScroll: true,
		items: [this.sheetsContainerPanel]
	}); 
		
	Sbi.worksheet.runtime.WorkSheetsRuntimePanel.superclass.constructor.call(this, c);	
};

Ext.extend(Sbi.worksheet.runtime.WorkSheetsRuntimePanel, Ext.Panel, {
	sheetsContainerPanel: null,

	initPanels: function(template){
		this.sheetsContainerPanel = new Sbi.worksheet.runtime.RuntimeSheetsContainerPanel({},template);		
	},

	exportContent: function(){
		var exportedContent = this.sheetsContainerPanel.exportContent();
		var encodedExportedContent = Ext.util.JSON.encode(exportedContent);
		return encodedExportedContent;
	}
});
