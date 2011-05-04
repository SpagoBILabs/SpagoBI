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
 * updateToolsForActiveTab(activeSheet): update the tools: take the configuration of the activeSheet
 * and update the tools
 * 
 * 
 * Public Events
 * 
 * toolschange(change): the value of the tools is changed.. change a map with the change value.
 * for example {layout: layout-header}
 * 
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.DesignToolsPanel = function(config) { 

	var defaultSettings = {};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.designTools) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.designTools);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.initPanels();
	var c = {
        layout: {
            type:'vbox',
            align:'stretch'
        },
        items:[this.designToolsFieldsPanel, this.designToolsPallettePanel, this.designToolsLayoutPanel]
	}
	Sbi.worksheet.designer.DesignToolsPanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.designer.DesignToolsPanel, Ext.Panel, {
	designToolsFieldsPanel: null,
	designToolsPallettePanel: null,
	designToolsLayoutPanel: null,

	initPanels: function() {
		
		this.designToolsFieldsPanel = new Sbi.formbuilder.QueryFieldsPanel({
			border: false,
	        gridConfig: {
				ddGroup: 'worksheetDesignerDDGroup'
	        	, type: 'queryFieldsPanel'
	        }
		});
		
//		this.designToolsFieldsPanel = new Sbi.worksheet.designer.DesignToolsFieldsPanel({
//	        gridConfig: {
//				ddGroup: 'worksheetDesignerDDGroup'
//	        	, type: 'queryFieldsPanel'
//	        }
//		});
		this.designToolsPallettePanel = new Sbi.worksheet.designer.DesignToolsPallettePanel();
		this.designToolsLayoutPanel = new Sbi.worksheet.designer.DesignToolsLayoutPanel();
		this.designToolsFieldsPanel.flex = 1;
		this.designToolsPallettePanel.flex = 1;
		this.designToolsLayoutPanel.flex = 1;
		this.designToolsLayoutPanel.on('layoutchange', function(layout){
			var change = {
				layout : layout
			};
			this.fireEvent('toolschange',change);
		}, this);
	}

	//Update the tools info for the active sheet
	, updateToolsForActiveTab: function(activeSheet){
		if(activeSheet.layout!=null){
			this.designToolsLayoutPanel.setLayoutValue(activeSheet.layout);
		}
	}
});
