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
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.SheetPanel = function(config) { 

	
	var defaultSettings = {};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.sheetsPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.sheetsPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.initPanels();
	
	c ={
            layout: {
                type:'vbox',
                align:'stretch'
            },
            items:[this.headerPanel, this.filtersPanel, this.contentPanel, this.footerPanel]
	}
	
	c = Ext.apply(config,c);
	Ext.apply(this,c);
	Sbi.worksheet.designer.SheetPanel.superclass.constructor.call(this, c);	 	


};

Ext.extend(Sbi.worksheet.designer.SheetPanel, Ext.Panel, {
	headerPanel: null,
	filtersPanel: null,
	contentPanel: null,
	footerPanel: null,
	layout: null,
	
	initPanels: function(){
		this.layout = 'layout_headerfooter';
		this.headerPanel = new Sbi.worksheet.designer.SheetTitlePanel({});
		this.filtersPanel = new Sbi.worksheet.designer.DesignSheetFiltersPanel({
			style:'padding:0px 15px 0px 15px'
			, ddGroup: 'worksheetDesignerDDGroup'
		});
		this.contentPanel = new Sbi.worksheet.designer.SheetContentPanel({});
		this.footerPanel  = new Sbi.worksheet.designer.SheetTitlePanel({});
		this.contentPanel.flex = 4;
	}

	, updateLayout: function (layout) {
		if(layout!=null){
			 this.layout=layout;
			 if(layout=='layout-header' || layout=='layout-content'){
				 this.footerPanel.hide();
			 }
			 if(layout=='layout-footer' || layout=='layout-content'){
				 this.headerPanel.hide();
			 }
			 if(layout=='layout-footer' || layout=='layout-headerfooter'){
				 this.footerPanel.show();
			 }
			 if(layout=='layout-header' || layout=='layout-headerfooter'){
				 this.headerPanel.show();
			 }
		}
	}
	
	, getSheetState: function(){
		var state = {};
		state.name = this.title;
		if(!this.headerPanel.hidden){
			state.header = this.headerPanel.getTitleState();
		}
		state.filters = this.filtersPanel.getFilters();
		state.content = this.contentPanel.getDesignerState();
		
		if(!this.footerPanel.hidden){
			state.footer = this.footerPanel.getTitleState();
		}
		return state;
	}
	
	, setSheetState: function(sheetState){
		var state = {};
		this.title = sheetState.name;
		if(sheetState.header!=null){
			this.headerPanel.setTitleState(sheetState.header);
		}
		if(sheetState.filters!=null){
			this.filtersPanel.setFilters(sheetState.filters);
		}
		if(sheetState.content!=null){
			this.contentPanel.setDesignerState(sheetState.content);
		}
		if(sheetState.footer!=null){
			this.footerPanel.setTitleState(sheetState.footer);
		}
	}
	
	, isValid: function(){
		var valid = true;
		if(sheetState.header!=null){
			valid = valid && this.headerPanel.isValid();
		}
//		if(sheetState.filters!=null){
//			this.filtersPanel.setFilters(sheetState.filters);
//		}
//		if(sheetState.content!=null){
//			this.contentPanel.setDesignerState(sheetState.content);
//		}
		if(sheetState.footer!=null){
			valid = valid && this.footerPanel.isValid();
		}
		return valid;
	}
	
});