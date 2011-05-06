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
	
	
	
	var items = this.initPanels(config);
	
	c ={
			title: this.sheetConfig.title,
            items: items
	}
	
	c = Ext.apply(config,c);
	this.addEvents();
	Ext.apply(this,c);	
	
	this.on('activate', function(){this.content.updateContent();}, this)
	
	Sbi.worksheet.runtime.RuntimeSheetPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetPanel, Ext.Panel, {
	content: null,
	filtersPanel : null,
	
	initPanels: function(config){
		var items = [];
		
		var sharedConf = {				
			border: false,
			style:'padding:5px 15px 5px'
		}
		
		//Builds the header
		if (this.sheetConfig.header!=undefined && this.sheetConfig.header!=null){
			
			var header = new Ext.Panel(Ext.apply({
				html: this.buildTitleHtml(this.sheetConfig.header, true)
			},sharedConf));

			items.push(header);
		}

		if (this.sheetConf.filters != undefined && this.sheetConf.filters != null && this.sheetConf.filters.length > 0) {
			var dynamicFilters = [];
			for ( var i = 0; i < this.sheetConf.filters.length; i++ ) {
				var aDynamicFilter = this.getDynamicFilterDefinition(this.sheetConf.filters[i]);
				dynamicFilters.push(aDynamicFilter);	
			}
			this.filtersPanel = new Sbi.formviewer.StaticOpenFiltersPanel(dynamicFilters, {
				title : LN('sbi.worksheet.runtime.runtimesheetpanel.filterspanel.title')
			});
			items.push(this.filtersPanel);
		}
		
		//Builds the content
		this.content = new Sbi.worksheet.runtime.RuntimeSheetContentPanel(Ext.apply(config||{},{contentConfig: this.sheetConfig.content}));
		items.push(this.content);
		
		//Builds the footer
		if (this.sheetConfig.footer!=undefined && this.sheetConfig.footer!=null){
			var footer = new Ext.Panel(Ext.apply({
				html: this.buildTitleHtml(this.sheetConfig.footer, false)
			},sharedConf));

			items.push(footer);
		}

		return items;
	},
	
	/**
	 * Build the html for the header or the footer
	 * @param: title: the configuration {tilte, img, position}
	 * @param: header: true if it builds the header, false otherwise
	 * @return: the hatml for the title
	 */
	buildTitleHtml: function(title, header){
		if(title.position==null || title.position==undefined){
			title.position='center';
		}
		if(title.img!=undefined && title.img!=null && title.position!='center'){
			html = '<div style="float: left">'+title.title+'</div>';
		}else{
			html = '<div>'+title.title+'</div>';
		}
		if(title.img!=undefined && title.img!=null){
			switch (title.position) {
	        case 'left':
	        	html = '<div style="float: left"><img src="'+title.img+'"></img></div>'+html;
	            break;
	        case 'right':
	        	html = html+'<div style="float: right"><img src="'+title.img+'"></img></div>';
	            break;
	        default: 
	        	if(header){
	        		html = '<div style="text-align:center"><img src="'+title.img+'"></img></div>'+html;
	        	}else{
	        		html = html+'<div style="text-align:center"><img src="'+title.img+'"></img></div>';
	        	}
	            break;
			}
		}
		return html;
	}
	
	, getDynamicFilterDefinition: function (aField) {
		return {
            "text": aField.alias,
            "field": aField.id,
            "operator": "IN",
            "singleSelection": false
		};
	}
});