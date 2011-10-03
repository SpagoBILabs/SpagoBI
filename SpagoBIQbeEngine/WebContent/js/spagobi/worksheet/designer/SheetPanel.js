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
	
	this.addEvents("attributeDblClick");
	
	this.initPanels();
	
	var emptyPanel = new Ext.Panel({
		htlm:'&nbsp;',
		height: 0,
		layout: 'fit',
		hidden: true
	});
	
	c = {
			scrollable: true,
            layout: 'fit',
            items:[emptyPanel, this.headerPanel, this.filtersPanel, this.contentPanel, this.footerPanel]
	};
	this.filtersPositionPanel = 'top';
	Ext.apply(this,c);
	
	this.on('resize',this.resizePanels,this);
	
	Sbi.worksheet.designer.SheetPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.worksheet.designer.SheetPanel, Ext.Panel, {
	headerPanel: null,
	filtersPanel: null,
	contentPanel: null,
	footerPanel: null,
	sheetLayout: null,
	filtersPositionPanel: null,
	
	initPanels: function(){
		this.sheetLayout = 'layout_headerfooter';
		this.headerPanel = new Sbi.worksheet.designer.SheetTitlePanel({});
		
		var filtersConf ={
			style:'padding:5px 15px 0px 15px',
			ddGroup: 'worksheetDesignerDDGroup'	
		};
		
		filtersConf.tools=[{
	       	qtip: LN('sbi.worksheet.designer.sheetpanel.tool.left.filter'),
	       	id: 'left',
	       	handler:this.showLeftFilters,
	       	scope: this
	       }];

		
		this.filtersPanel = new Sbi.worksheet.designer.DesignSheetFiltersPanel(filtersConf);
		// propagate event
		this.filtersPanel.on(
			'attributeDblClick' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeDblClick", this, attribute); 
			}, 
			this
		);
		this.contentPanel = new Sbi.worksheet.designer.SheetFilterContentPanel({},this.filtersPanel.store);
		// propagate event
		this.contentPanel.on(
			'attributeDblClick' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeDblClick", this, attribute); 
			}, 
			this
		);
		
		this.contentPanel.on('topFilters', function(){
			this.filtersPanel.show();
			this.filtersPanel.updateFilters();
			this.filtersPositionPanel = 'top';
			var w = this.getWidth()-22;//10 + 10 of left and right paddings
			this.filtersPanel.setWidth(w);
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
			if(this.filtersPositionPanel == null){
				if(this.filtersPanel.hidden){
					state.filters.position='left';
				}else{
					state.filters.position='top';
				}				
			}else{
				state.filters.position=this.filtersPositionPanel;
			}
		}

		state.content = this.contentPanel.getDesignerState();
		
		if(!this.footerPanel.hidden){
			state.footer = this.footerPanel.getTitleState();
		}

		return state;
	}
	
	, setSheetState: function(sheetState){

		this.title = sheetState.name;
		this.sheetLayout = sheetState.sheetLayout;
		this.updateLayout(this.sheetLayout);
		this.setTitle(this.title);
		if(sheetState.header!==null){
			this.headerPanel.setTitleState(sheetState.header);
		}
		if(sheetState.filters !== undefined && sheetState.filters !== null && sheetState.filters.filters !== null  && sheetState.filters.filters.length>0){
			var filters = sheetState.filters.filters;
			this.filtersPanel.setFilters(filters);
			this.filtersPositionPanel = sheetState.filters.position;
			if(sheetState.filters.position=='left'){
				if(this.filtersPanel.rendered){
					this.showLeftFilters();
				}else{
					this.filtersPanel.on('afterrender',this.showLeftFilters, this);	
				}
			}
		}
		if(sheetState.content!==null){
			this.contentPanel.addDesigner(sheetState.content);
		}
		if(sheetState.footer!==null){
			this.footerPanel.setTitleState(sheetState.footer);
		}
	}
	
	, validate: function(){
		var valid;
		//if(this.headerPanel!==null){
		//	valid = valid && this.headerPanel.isValid();
		//}
		if(this.content!==null){
			valid = this.contentPanel.validate();
		}
		//if(this.footerPanel!==null){
		//	valid = valid && this.footerPanel.isValid();
		//}
		return valid;
	}
	
	, showLeftFilters: function(){
		this.filtersPanel.hide();
		this.contentPanel.showLeftFilter();
		this.filtersPositionPanel = 'left';
	}
	
	/**
	 * Resizes the panels in the sheet panel (header, footer, content and filters)
	 */
	, resizePanels: function(a,newWidth,c,d,e){
		var w = newWidth-22;//10 + 10 of left and right paddings
		if(this.headerPanel != undefined && this.headerPanel != null){
			this.headerPanel.setWidth(w);
		}
		if(this.filtersPanel != undefined && this.filtersPanel != null && !this.filtersPanel.hidden){
			this.filtersPanel.setWidth(w);
		}
		if(this.contentPanel != undefined && this.contentPanel != null){
			this.contentPanel.setWidth(w);
		}
		if(this.footerPanel != undefined && this.footerPanel != null){
			this.footerPanel.setWidth(w);
		}
	}
	
	, getName : function () {
		return this.title;
	}
	
});