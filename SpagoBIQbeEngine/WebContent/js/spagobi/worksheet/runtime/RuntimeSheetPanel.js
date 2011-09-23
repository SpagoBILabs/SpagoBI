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
	
	c = {
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
	
	exportContent: function(mimeType){
		var exportedContent = this.content.exportContent();
		var header = this.sheetConfig.header;	
		if(header!=null && header!=undefined){
			var headerTitleHTML = this.sheetConfig.header.title;
			if (mimeType == 'application/vnd.ms-excel') {
				var headerTitle = Ext.util.Format.stripTags( headerTitleHTML );
				header.title = headerTitle;
			} else {
				header.title = headerTitleHTML;
			}
		}
		var footer = this.sheetConfig.footer;
		if(footer!=null && footer!=undefined){
			var footerTitleHTML = this.sheetConfig.footer.title;
			if (mimeType == 'application/vnd.ms-excel') {
				var footerTitle = Ext.util.Format.stripTags( footerTitleHTML );
				footer.title = footerTitle;
			} else {
				footer.title = footerTitleHTML;
			}
		}
		var filters =null;
		if(this.filtersPanel!=null && this.filtersPanel!=undefined){
			filters = this.filtersPanel.getFormState();
		}
		var completedExportedContent = {
				HEADER: header,
				FOOTER: footer,
				CONTENT: exportedContent,
				FILTERS: filters
				}
		return completedExportedContent;
	},
	
	initPanels: function(){
		var items = [];
		var dynamicFilters = [];
		var hiddenContent = false;
		
		
		//prepare the filters.. we need to do this before build the content 
		//because we need to know if there is same mandatory filter
		if (this.sheetConfig.filters != undefined && this.sheetConfig.filters != null &&  this.sheetConfig.filters.filters != undefined &&  this.sheetConfig.filters.filters != null && this.sheetConfig.filters.filters.length > 0) {
			var i = 0;
			for (; i < this.sheetConfig.filters.filters.length; i++ ) {
				var aDynamicFilter = this.getDynamicFilterDefinition(this.sheetConfig.filters.filters[i]);
				if(!hiddenContent && aDynamicFilter.allowBlank!=undefined && aDynamicFilter.allowBlank!=null && !aDynamicFilter.allowBlank){
					hiddenContent=true;
				}
				dynamicFilters.push(aDynamicFilter);	
			}
		}
		
		//Builds the content
		this.content = new Sbi.worksheet.runtime.RuntimeSheetContentPanel(
				Ext.apply({
					style : 'float: left; width: 100%',
					hiddenContent : hiddenContent
				}, {
					contentConfig : this.sheetConfig.content
					, sheetName : this.sheetConfig.name
				})
		);
		//catch the event of the contentloaded from the component and hide the loading mask
		this.content.on('contentloaded',this.hideMask,this);
		this.content.on('contentloading',this.showMask,this);
		
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
			var filterConf = {
					title : LN('sbi.worksheet.runtime.runtimesheetpanel.filterspanel.title')
					, layout: 'auto'
					, autoScroll :true
					, style:'padding: 5px 15px 10px 15px;'	
					, position : 'top'
//					, tools:  [{
//						id: 'gear'
//				        	, handler: this.applyFilters
//				          	, scope: this
//				          	, qtip: LN('sbi.worksheet.runtime.runtimesheetpanel.filterspanel.filter.qtip')
//					}]
				};
			
			if ( this.sheetConfig.filters.position=='left') {
				filterConf.width= 250;
				filterConf.autoWidth = false;
				filterConf.style = 'float: left; padding: 15px 0px 10px 15px';
				filterConf.position = 'left';
			}
			/* this was an attempt to make the filters panel collapsible, but it has a side-effect: combo-boxes are narrow
			 * when displayed on top of a chart
			 else {  
				filterConf.collapsible = true;
				filterConf.collapsed = true;
			}
			*/
			
			this.filtersPanel = new Sbi.worksheet.RuntimeSheetFiltersPanel(dynamicFilters, filterConf);
			this.filtersPanel.on('apply', this.applyFilters, this);

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
		if(title.title!='' || title.img!=null){

			var titleHTML = '<div style="width: 100%; padding: 4px">'+title.title+'</div>';
			var html = titleHTML;
			
			if(title.img!=undefined && title.img!=null){

				var loadHeaderImgService = Sbi.config.serviceRegistry.getServiceUrl({
					serviceName: 'GET_IMAGE_CONTENT_ACTION'
					, baseParams: {FILE_NAME: title.img}
				});
				
				var imgWidth = '20';//default width 
				if(title.width!=undefined && title.width!=null && title.width!=''){
					imgWidth = title.width;
				}

				var imgHTML = '<img width="'+imgWidth+'px" src="'+loadHeaderImgService+'"></img>';
		
				if(title.position=='right') {
					html = '<table style="border-style: none; width:100%;"><tbody><tr><td>'+titleHTML+'</td><td width="'+ imgWidth+'px">'+imgHTML+'</td></tr></tbody></table>';	
				} else  if(title.position=='left') {
					html = '<table style="border-style: none; width:100%;"><tbody><tr><td width="'+imgWidth +'px">'+imgHTML+'</td><td>'+titleHTML+'</td></tr></tbody></table>';
				} else {
					if(title.title==undefined || title.title==null || title.title==''){
						titleHTML='';
					}
					if(header){ //position is center
						html = '<div style="text-align:center; width: 100%;"><img src="'+loadHeaderImgService+'"></img></div>'+titleHTML;
					}else {
						html = titleHTML+'<div style="text-align:center; width: 100%;"><img src="'+loadHeaderImgService+'"></img></div>';
					}
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
	}	
	
	, getDynamicFilterDefinition: function (aField) {
		var dynamicaFilter = {
	            "text": aField.alias,
	            "field": aField.id,
	            "id": aField.id,
	            "operator": "IN",
	            "values": aField.values
			};
		if(aField.mandatory=='yes'){
			dynamicaFilter.allowBlank=false;
		}
		if(aField.selection=='singlevalue'){
			dynamicaFilter.maxSelectedNumber=1;
		}
		return dynamicaFilter;
	}
	
	//render the content after the sheet has been activated
	, renderContent: function(){
		this.un('activate', this.renderContent, this);
		this.removeAll();
		this.add(this.initPanels());
	}
	
	, applyFilters: function(filterPanel, formState){
		this.content.applyFilters(formState);
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