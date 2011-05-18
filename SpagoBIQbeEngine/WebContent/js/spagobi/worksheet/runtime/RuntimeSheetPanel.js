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
	
	c ={
			border: false,
			title: this.sheetConfig.name,
            items: [new Ext.Panel({})]
	}
	
	c = Ext.apply(config,c);
	this.addEvents();
	Ext.apply(this,c);	
	
	this.on('activate',this.renderContent, this)
	
	Sbi.worksheet.runtime.RuntimeSheetPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetPanel, Ext.Panel, {
	content: null,
	filtersPanel : null,
	
	initPanels: function(){
		var items = [];
		
		
		//show the loading mask
		if(this.rendered){
			this.showMask();
		} else{
			this.on('afterlayout',this.showMask,this);
		}
		
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

		if (this.sheetConfig.filters != undefined && this.sheetConfig.filters != null && this.sheetConfig.filters.length > 0) {
			var dynamicFilters = [];
			for ( var i = 0; i < this.sheetConfig.filters.length; i++ ) {
				var aDynamicFilter = this.getDynamicFilterDefinition(this.sheetConfig.filters[i]);
				dynamicFilters.push(aDynamicFilter);	
			}
			this.filtersPanel = new Sbi.formviewer.StaticOpenFiltersPanel(dynamicFilters, {
				title : LN('sbi.worksheet.runtime.runtimesheetpanel.filterspanel.title')
			});
			items.push(this.filtersPanel);
		}
		
		//Builds the content
		this.content = new Sbi.worksheet.runtime.RuntimeSheetContentPanel(Ext.apply({},{contentConfig: this.sheetConfig.content}));
		//catch the event of the contentloaded from the component and hide the loading mask
		this.content.on('contentloaded',this.hideMask,this);
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
		var titleId = Ext.id();
		var textId = Ext.id();
		var imgId = null;
		var html = '<div id="'+titleId+'">';
		if(title.position==null || title.position==undefined){
			title.position='center';
		}
		if(title.img!=undefined && title.img!=null && title.position!='center'){
			html = '<div id="'+textId+'" style="float: left">'+title.title+'</div>';
		}else{
			html = '<div>'+title.title+'</div>';
		}
		if(title.img!=undefined && title.img!=null){
			var loadHeaderImgService = Sbi.config.serviceRegistry.getServiceUrl({
				serviceName: 'GET_IMAGE_CONTENT_ACTION'
				, baseParams: {FILE_NAME: title.img}
			});
			switch (title.position) {
	        case 'left':
	        	imgId= Ext.id();
	           	html = '<div id="'+imgId+'" style="float: left; margin-right:10px;"><img src="'+loadHeaderImgService+'"></img></div>'+html;
	            break;
	        case 'right':
	        	imgId= Ext.id();
	        	html = html+'<div id="'+imgId+'" style="float: right; margin-left:10px;"><img src="'+loadHeaderImgService+'"></img></div>';
	            break;
	        default: 
	        	if(header){
	        		html = '<div style="text-align:center"><img src="'+loadHeaderImgService+'"></img></div>'+html;
	        	}else{
	        		html = html+'<div style="text-align:center"><img src="'+loadHeaderImgService+'"></img></div>';
	        	}
	            break;
			}
		}
		
		if(imgId!=null){
			//align the image and the text:
			//get the width of the image and set as width of the text in this way:
			//width text = total width-img width-2
           	this.on("afterlayout",function(){
           		var textDiv = Ext.get(textId);
           		var imgDiv = Ext.get(imgId);
           		var titleDiv = Ext.get(titleId);
           		textDiv.setWidth(titleDiv.getWidth()-imgDiv.getWidth()-2);
           	},this);
		}
		return html+'</div>';
	}
	
	, getDynamicFilterDefinition: function (aField) {
		return {
            "text": aField.alias,
            "field": aField.id,
            "operator": "IN",
            "singleSelection": false
		};
	}
	
	//render the content after the sheet has been activated
	, renderContent: function(){
		this.un('activate', this.renderContent, this);
		this.removeAll();
		this.add(this.initPanels());
	}
	
	/**
	 * Opens the loading mask 
	 */
    , showMask : function(){
    	this.un('afterlayout',this.showMask,this);
    	if (this.loadMask == null) {
    		this.loadMask = new Ext.LoadMask(this.getId(), {msg: "Loading.."});
    	}
    	this.loadMask.show();
    }
	
	/**
	 * Closes the loading mask
	 */
	, hideMask: function() {
    	if (this.loadMask != null) {
    		this.loadMask.hide();
    	}
	}
});