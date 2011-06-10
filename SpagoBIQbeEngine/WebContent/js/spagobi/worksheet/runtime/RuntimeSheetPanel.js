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
	};
	
	c = Ext.apply(config,c);
	this.addEvents('contentloaded');
	Ext.apply(this,c);	
	
	this.on('activate',this.renderContent, this);
	
	Sbi.worksheet.runtime.RuntimeSheetPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetPanel, Ext.Panel, {
	content: null,
	filtersPanel : null,
	contentLoaded: false,
	
	exportContent: function(){
		var exportedContent = this.content.exportContent();
		var header = this.sheetConfig.header;
		var footer = this.sheetConfig.footer;
		var completedExportedContent = {
				HEADER: header,
				FOOTER: footer,
				CONTENT: exportedContent
				}
		return completedExportedContent;
		//Poi aggiungere titolo e footer
	},
	
	initPanels: function(){
		var items = [];
		
		
		//Builds the content
		this.content = new Sbi.worksheet.runtime.RuntimeSheetContentPanel(Ext.apply({style : 'float: left; width: 100%'},{contentConfig: this.sheetConfig.content}));
		//catch the event of the contentloaded from the component and hide the loading mask
		this.content.on('contentloaded',this.hideMask,this);
		
		//show the loading mask
		if(this.rendered){
			this.showMask();
		} else{
			this.on('afterlayout',this.showMask,this);
		}
			
		//Builds the header
		if (this.sheetConfig.header!=undefined && this.sheetConfig.header!=null){
			this.addTitle(this.sheetConfig.header,items, true);
		}

		if (this.sheetConfig.filters != undefined && this.sheetConfig.filters != null && this.sheetConfig.filters.filters.length > 0) {
			var dynamicFilters = [];
			var i = 0;
			for (; i < this.sheetConfig.filters.filters.length; i++ ) {
				var aDynamicFilter = this.getDynamicFilterDefinition(this.sheetConfig.filters.filters[i]);
				dynamicFilters.push(aDynamicFilter);	
			}
			
			var filterConf = {
					title : LN('sbi.worksheet.runtime.runtimesheetpanel.filterspanel.title')
					, layout: 'auto'
					, tools:  [{
						id: 'gear'
				        	, handler: this.applyFilters
				          	, scope: this
				          	, qtip: LN('sbi.worksheet.runtime.runtimesheetpanel.filterspanel.filter.qtip')
					}]
				};
			
			if ( this.sheetConfig.filters.position=='left') {
				filterConf.width= 230;
				filterConf.style = 'float: left; padding: 15px';
			}
			
			this.filtersPanel = new Sbi.formviewer.StaticOpenFiltersPanel(dynamicFilters, filterConf);

			if ( this.sheetConfig.filters.position=='left') {
				var filterContentPanel = new Ext.Panel({
		            border: false,
					items:[this.filtersPanel,this.content]			       
				});
								
				items.push(filterContentPanel);
			}else{
				items.push(this.filtersPanel);
			}			
		}
			
		if (this.sheetConfig.filters==undefined || this.sheetConfig.filters==null || this.sheetConfig.filters.filters.length<=0 || this.sheetConfig.filters.position!='left') {
			items.push(this.content);
		}
		
		
		//Builds the footer
		if (this.sheetConfig.footer!=undefined && this.sheetConfig.footer!=null){
			this.addTitle(this.sheetConfig.footer,items, false);
		}

		return items;
	},
	
	/**
	 * Build the html for the header or the footer
	 * @param: title: the configuration {tilte, img, position}
	 * @param: items: the array of the panel item
	 * @param: header: true if it builds the header, false otherwise
	 * @return: the html for the title
	 */
	addTitle: function(title, items, header){
		
		var loadHeaderImgService = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_IMAGE_CONTENT_ACTION'
			, baseParams: {FILE_NAME: title.img}
		});
//		var titlePanel = new Ext.Panel({html:''});
//		var titleHTML = '<div>'+title.title+'</div>';
//		var html = titleHTML;
//		
//		if(title.img!=undefined && title.img!=null){
//			alert(title.width);
//			var imgWidth = '20';
//			if(title.width!=undefined && title.width!=null && title.width!=''){
//				imgWidth = title.width;
//			}
//			
//			imgWidth = parseInt(imgWidth)/100;
//			var textWidth =  1-imgWidth;
//			
//			var imgHTML = '<img width="100%" src="'+loadHeaderImgService+'"></img>';
//			
//			var tableItems;
			
//			if(title.position=='right') {
//				tableItems = [{html: titleHTML, border: false, columnWidth: textWidth},{html: imgHTML, border: false, columnWidth: imgWidth}];	
//			} else  if(title.position=='left') {
//				tableItems = [{html: imgHTML, border: false, columnWidth: imgWidth}, {html: titleHTML, border: false, columnWidth: textWidth}];
//			} else {
//				imgHTML = '<div style="text-align: center; padding: 10px;"><img width="'+ imgWidth*100 +'%" src="'+loadHeaderImgService+'"></img></div>';
//				if(header){ //position is center
//					tableItems = [{html: imgHTML, border: false, columnWidth: 1},{html: titleHTML, border: false, columnWidth: 1}];
//				}else {
//					tableItems = [{html: titleHTML, border: false, columnWidth: 1},{html: imgHTML, border: false, columnWidth: 1}];
//				}
//			}
			
			
			

//			logger.debug("IN");
//			
//			titlePanel = new Ext.Panel({
//				layout: 'column',
//				border: false,
//				style: 'padding: 10px',
//				autoHeight: true,
//				items: tableItems
//			});
//		
//		}
			
			
		var titleHTML = '<div style="width: 100%; padding: 4px">'+title.title+'</div>';
		var html = titleHTML;
		
		if(title.img!=undefined && title.img!=null){

			var imgWidth = '20';//default width 
			if(title.width!=undefined && title.width!=null && title.width!=''){
				imgWidth = title.width;
			}

			var imgHTML = '<img width="'+imgWidth+'px" src="'+loadHeaderImgService+'"></img>';
	
			if(title.position=='right') {
				html = '<table style="border-style: none; width:100%;"><tbody><tr><td>'+titleHTML+'</td><td width="'+ imgWidth+'px">'+imgHTML+'</td></tr></tbody></table>';	
			} else  if(title.position=='left') {
				html = '<table style="border-style: none; width:100%;"><tbody><tr><td width="'+imgWidth +'px">'+imgHTML+'</td><td>'+titleHTML+'</td></tr></tbody></table>';
			} else if(header){ //position is center
				html = '<div style="text-align:center; width: 100%;"><img src="'+loadHeaderImgService+'"></img></div>'+titleHTML;
			}else {
				html = titleHTML+'<div style="text-align:center; width: 100%;"><img src="'+loadHeaderImgService+'"></img></div>';
			}
		}
	
		var titlePanel = new Ext.Panel({
			style: "padding: 10px;",
			border: false,
			autoHeight: true,
			html : html
		});
		
	   	items.push(titlePanel);
	}	
	
	, getDynamicFilterDefinition: function (aField) {
		return {
            "text": aField.alias,
            "field": aField.id,
            "id": aField.id,
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
	 * Get the values of the filters..
	 * Return an array of properties
	 */
	, applyFilters: function(){
		var filtersValue = [];
		if(this.filtersPanel!=null){
			filtersValue = this.filtersPanel.getFormState();
		}
		this.content.applyFilters(filtersValue);
		return filtersValue;
	}
	
	/**
	 * Opens the loading mask 
	 */
    , showMask : function(){
    	this.un('afterlayout',this.showMask,this);
    	if (this.loadMask == null) {//'runtimeworksheet'
    		this.loadMask = new Ext.LoadMask('runtimeworksheet', {msg: "Loading.."});
    		//this.loadMask = new Ext.LoadMask(this.getId(), {msg: "Loading.."});
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
    	this.contentLoaded = true;
    	this.fireEvent('contentloaded');
	}

});