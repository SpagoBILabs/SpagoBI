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
	this.addEvents();
	Ext.apply(this,c);	
	
	this.on('activate',this.renderContent, this);
	
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
			
		//Builds the header
		if (this.sheetConfig.header!=undefined && this.sheetConfig.header!=null){
			if(Ext.isIE){
				this.addTitleIE(this.sheetConfig.header,items, true);
			} else{
				this.addTitle(this.sheetConfig.header,items, true);
			}
		}
		
		//Builds the content
		this.content = new Sbi.worksheet.runtime.RuntimeSheetContentPanel(Ext.apply({style : 'float: left; width: 100%'},{contentConfig: this.sheetConfig.content}));
		//catch the event of the contentloaded from the component and hide the loading mask
		this.content.on('contentloaded',this.hideMask,this);

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
			if(Ext.isIE){
				this.addTitleIE(this.sheetConfig.footer,items, false);
			} else{
				this.addTitle(this.sheetConfig.footer,items, false);
			}
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
		var textPanel = new Ext.Panel({
			border: false,
			html: '<div>'+title.title+'</div>'
		});
		
		
		var titlePanelItems = [];
		var layout = 'fit';
		if(title.img!=undefined && title.img!=null){
			
			var imgPanel = new Ext.Panel({
				border: false,
				autoWidth: true,
				autoHeight: true,
				html: '<img src="'+loadHeaderImgService+'"></img>'
			});
			
			switch (title.position) {
	        case 'left':
	        	titlePanelItems.push(imgPanel);
	        	titlePanelItems.push(textPanel);
	        	layout = 'hbox';
	            break;
	        case 'right':
	        	titlePanelItems.push(textPanel);
	        	titlePanelItems.push(imgPanel);
	        	layout = 'hbox';
	        	break;
	        default: 
	        	imgPanel.style='width: 100%; text-align: center;';
	        	textPanel.style='width: 100%';
	        	if(header){
		        	titlePanelItems.push(imgPanel);
		        	titlePanelItems.push(textPanel);
	        	}else{
		        	titlePanelItems.push(textPanel);
		        	titlePanelItems.push(imgPanel);	
	        	}
	            break;
			}
		}else{
        	textPanel.style='width: 100%';
        	titlePanelItems.push(textPanel);
		}
		
		var titlePanel = new Ext.Panel({
			border: false,
	        layout: layout,
	        items: titlePanelItems
		});

		//Set the width of the text as the width of the panel - the width of the img
		if(title.position=='left' || title.position=='right'){
		   	this.on("afterlayout",function(){
		   		textPanel.setWidth(titlePanel.getWidth() - imgPanel.getWidth());
			},this);
		}
	   	items.push(titlePanel);
	},
	
	addTitleIE: function(title, items, header){

		var loadHeaderImgService = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_IMAGE_CONTENT_ACTION'
			, baseParams: {FILE_NAME: title.img}
		});
		
		//build the text panel
		var textPanel = new Ext.Panel({
			border: false,
			autoHeight: true,
			padding: '15',
			html: '<div>'+title.title+'</div>'
		});
		
		
		var titlePanelItems = [];
		var layout = 'fit';
		
		//The image is defined
		if(title.img!=undefined && title.img!=null){
			
			//build the image panel
			var imgPanel = new Ext.Panel({
				border: false,
				autoWidth: true,
				autoHeight: true,
				padding: '15',
				html: '<img src="'+loadHeaderImgService+'"></img>'
			});
			
			if (title.position=='left' || title.position=='right') {

	        	titlePanelItems.push(textPanel);
	        	titlePanelItems.push(imgPanel);
	        	layout = 'hbox';
			}else{
	        	imgPanel.style='width: 100%; text-align: center;';
	        	textPanel.style='width: 100%';
	        	if(header){
		        	titlePanelItems.push(imgPanel);
		        	titlePanelItems.push(textPanel);
	        	}else{
		        	titlePanelItems.push(textPanel);
		        	titlePanelItems.push(imgPanel);	
	        	}
			}
		}else{
        	textPanel.style='width: 100%';
        	titlePanelItems.push(textPanel);
		}
		
		
		var titlePanel = new Ext.Panel({
			border: false,
	        layout: layout,
	        autoHeight: true,
	        items: titlePanelItems
		});
		var titlePanel2 = new Ext.Panel({
			border: false,
	        layout: 'fit',
	        autoHeight: true,
	        items: [titlePanel]
		});

		//this is a trick.. The solution of a problem with IE 
		if(title.position=='left' ){
		   	this.on("afterlayout",function(){
		   		var imgW =   imgPanel.getWidth();
		   		var imgH =   imgPanel.getHeight();
		   		var titleW = titlePanel.getWidth();
		   		titlePanel2.remove(titlePanel);
	   			
				var textPanel2 = new Ext.Panel({
					border: false,
					width: titleW-40-imgW,
					padding: '15',
					html: '<div>'+title.title+'</div>'
				});
				
				var imgPanel2 = new Ext.Panel({
					border: false,
					width: imgW,
					height: imgH,
					padding: '15',
					html: '<img src="'+loadHeaderImgService+'"></img>'
				});

				var titlePanel3 = new Ext.Panel({
					border: false,
			        layout: layout,
			        height: imgH,
			        items: [imgPanel2, textPanel2]
				});
				titlePanel2.add(titlePanel3);
			},this);
		}
		
		//Set the width of the title text panel
		if( title.position=='right'){
		   	this.on("afterlayout",function(){
		   		textPanel.setWidth(titlePanel.getWidth()-imgPanel.getWidth());
			},this);
		}
	   	items.push(titlePanel2);
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
	}
});