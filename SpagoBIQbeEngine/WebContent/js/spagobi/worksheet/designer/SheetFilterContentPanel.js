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
 * Contains the content and the filters
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
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it), Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.SheetFilterContentPanel = function(config, filterStore) { 

	var defaultSettings = {
		emptyMsg: LN('sbi.worksheet.designer.sheetcontentpanel.emptymsg')
	};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.sheetFilterContentPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.sheetFilterContentPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);

	this.addEvents('addDesigner');
	this.contentPanel = new Sbi.worksheet.designer.SheetContentPanel({style:'padding: 5px 15px 0px 15px;'});
	this.contentPanel.on('addDesigner', function(sheet, state){this.fireEvent('addDesigner',sheet, state);}, this);
	
	this.filtersPanel = new Sbi.worksheet.designer.DesignSheetFiltersPanel({
		style:'padding:5px 10px 0px 15px; float: left; overflow: auto'
		, store: filterStore
		, ddGroup: 'worksheetDesignerDDGroup'
		, height: 400 
		, width: 150
		, tools:[{
			id: 'toggle',
        	qtip: LN('sbi.worksheet.designer.sheetpanel.tool.up.filter'),
        	handler:this.showTopFilters,
        	scope: this
        }]
	});
		
	c = {
		height: 400,
		border: false,
		items: [this.filtersPanel, this.contentPanel]
	};

	Sbi.worksheet.designer.SheetFilterContentPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.worksheet.designer.SheetFilterContentPanel, Ext.Panel, {
	
	showTopFilters: function(){
		this.filtersPanel.hide();
		this.fireEvent('topFilters');
	},

	showLeftFilter: function(){
		this.filtersPanel.show();
		this.filtersPanel.updateFilters();
	},
	
	getDesignerState: function(){
		return this.contentPanel.getDesignerState();
	},
	
	setDesignerState: function(state){
		this.contentPanel.setDesignerState(state);
	},
	
	addDesigner: function(state){
		this.contentPanel.addDesigner(state);
	}
	
});
