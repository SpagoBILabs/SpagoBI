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

Sbi.worksheet.runtime.RuntimeSheetPanel = function(config) { 

	
	var defaultSettings = {};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeSheetPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeSheetPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	var items = this.initPanels();
	
	c ={
            items: items
	}
	
	c = Ext.apply(config,c);
	this.addEvents();
	Ext.apply(this,c);
	Sbi.worksheet.runtime.RuntimeSheetPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetPanel, Ext.Panel, {
	
	initPanels: function(){
		
		var items = [];
		
		var sharedConf = {				
			border: false,
			style:'padding:5px 15px 5px'
		}
		
		if (this.sheetConf.title!=null && this.sheetConf.title!=undefined){
			
			//this.sheetConf.title = '<div style="float: left">'+this.sheetConf.title+'</div><div style="float: left">asd456456456sad</div>';
			this.sheetConf.title = '<div>'+this.sheetConf.title+'</div><div>234234234</div>';
			var header = new Ext.Panel(Ext.apply({
				html: this.sheetConf.title
			},sharedConf));

			
			
			items.push(header);
		}
		
		var content = new Ext.Panel(Ext.apply({
			html: 'sadasdasd'
		},sharedConf));
		items.push(content);
		
		
		if (this.sheetConf.footer!=null && this.sheetConf.footer!=undefined){
			var footer = new Ext.Panel(Ext.apply({
				html: this.sheetConf.title
			},sharedConf));
			items.push(footer);
		}

		return items;
	}
	
});