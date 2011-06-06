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
	
	var emptyPanel = new Ext.Panel({
		htlm:'&nbsp;',
		height: 0,
		layout: 'fit',
		hidden: true
	});
	
	c ={
			scrollable: true,
            layout: 'fit',
            items:[emptyPanel, this.headerPanel, this.filtersPanel, this.contentPanel, this.footerPanel]
	};

	c = Ext.apply(config,c);
	Ext.apply(this,c);
	Sbi.worksheet.designer.SheetPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.worksheet.designer.SheetPanel, Ext.Panel, {
	headerPanel: null,
	filtersPanel: null,
	contentPanel: null,
	footerPanel: null,
	sheetLayout: null,
	
	initPanels: function(){
		this.sheetLayout = 'layout_headerfooter';
		this.headerPanel = new Sbi.worksheet.designer.SheetTitlePanel({});
		this.filtersPanel = new Sbi.worksheet.designer.DesignSheetFiltersPanel({
			hidden: true,
			tools:[{
	        	qtip: LN('sbi.worksheet.designer.sheetpanel.tool.left.filter'),
	        	id: 'left',
	        	handler:this.showLeftFilters,
	        	scope: this
	        }],
			style:'padding:5px 15px 0px 15px',
			ddGroup: 'worksheetDesignerDDGroup'
		});
		
		this.contentPanel = new Sbi.worksheet.designer.SheetFilterContentPanel({},this.filtersPanel.store);
		
		this.contentPanel.on('topFilters', function(){
			this.filtersPanel.show();
			this.filtersPanel.updateFilters();
		},this)
		
		this.footerPanel  = new Sbi.worksheet.designer.SheetTitlePanel({});
	}

	, updateLayout: function (sheetLayout) {
		if(sheetLayout!==null){
			 this.sheetLayout=sheetLayout;
			 if(sheetLayout==='layout-header' || sheetLayout==='layout-content'){
				 this.footerPanel.hide();
			 }
			 if(sheetLayout==='layout-footer' || sheetLayout==='layout-content'){
				 this.headerPanel.hide();
			 }
			 if(sheetLayout==='layout-footer' || sheetLayout==='layout-headerfooter'){
				 this.footerPanel.show();
			 }
			 if(sheetLayout==='layout-header' || sheetLayout==='layout-headerfooter'){
				 this.headerPanel.show();
			 }
		}
	}
	
	, getSheetState: function(){
		var state = {};
		state.name = this.title;
		state.sheetLayout = this.sheetLayout;
		if(!this.headerPanel.hidden){
			state.header = this.headerPanel.getTitleState();
		}
		state.filters ={};
		var filters = this.filtersPanel.getFilters();
		if(filters!==null){
			state.filters.filters = filters;
			if(this.filtersPanel.hidden){
				state.filters.position='left';
			}else{
				state.filters.position='top';
			}
		}

		state.content = this.contentPanel.getDesignerState();
		
		if(!this.footerPanel.hidden){
			state.footer = this.footerPanel.getTitleState();
		}
		return state;
	}
	
	, setSheetState: function(sheetState){
		var state = {};
		this.title = sheetState.name;
		this.sheetLayout = state.sheetLayout;
		if(sheetState.header!==null){
			this.headerPanel.setTitleState(sheetState.header);
		}
		if(sheetState.filters !== undefined && sheetState.filters !== null){
			var filters = sheetState.filters.filters;
			this.filtersPanel.setFilters(filters);
//			if(sheetState.filters.position){
//				sheetState.filters.position='left';
//			}else{
//				sheetState.filters.position='top';
//			}
			
		}
		if(sheetState.content!==null){
			this.contentPanel.addDesigner(sheetState.content);
		}
		if(sheetState.footer!==null){
			this.footerPanel.setTitleState(sheetState.footer);
		}
	}
	
	, isValid: function(){
		var valid = true;
		if(this.headerPanel!==null){
			valid = valid && this.headerPanel.isValid();
		}
//		if(sheetState.filters!==null){
//			this.filtersPanel.setFilters(sheetState.filters);
//		}
		if(this.content!==null){
			valid = valid && this.contentPanel.isValid();
		}
		if(this.footerPanel!==null){
			valid = valid && this.footerPanel.isValid();
		}
		return valid;
	}
	
	, showLeftFilters: function(){
		this.filtersPanel.hide();
		this.contentPanel.showLeftFilter();
	}
	
});