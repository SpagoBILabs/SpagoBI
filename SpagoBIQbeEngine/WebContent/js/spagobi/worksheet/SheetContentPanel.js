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
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it), Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet");

Sbi.worksheet.SheetContentPanel = function(config) { 

	var defaultSettings = {
		emptyMsg: LN('sbi.worksheet.sheetcontentpanel.emptymsg')
	};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.sheetContentPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.sheetContentPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.initEmptyMsgPanel();
	
	c = {
		items: [this.emptyMsgPanel]
	}
	Sbi.worksheet.SheetContentPanel.superclass.constructor.call(this, c);	
	this.on('render', this.initDropTarget, this);

};

Ext.extend(Sbi.worksheet.SheetContentPanel, Ext.Panel, {
	
	emptyMsg: null
	, emptyMsgPanel: null
	
	, initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
			html: this.emptyMsg
			, height: 40
			, frame: true
		});
	}
	
	, initDropTarget: function() {
		this.removeListener('render', this.initDropTarget, this);
		var dropTarget = new Sbi.widgets.GenericDropTarget(this, {
			ddGroup: 'paleteDDGroup'
			, onFieldDrop: this.onFieldDrop
		});
	}

	, onFieldDrop: function(ddSource) {
		if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'palette') {
			// dragging from palette
			this.notifyDropFromPalette(ddSource);
		} else {
			alert('Unknown DD source!!');
		}
	}
	
	, notifyDropFromPalette: function(ddSource) {
		var rows = ddSource.dragData.selections;
		if (rows.length > 1) {
			Ext.Msg.show({
				   title:'Drop not allowed',
				   msg: 'You can insert a single widget on a sheet',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
			return;
		}
		var row = rows[0];
		if (row.json.name === 'Pivot Table') {
			this.insertCrosstabDesigner();
		}
	}

	, insertCrosstabDesigner: function () {
		this.emptyMsgPanel.destroy();
		var crosstabDesigner = new Sbi.crosstab.CrosstabDefinitionPanel({
			crosstabTemplate: {}
			, ddGroup: 'worksheetDesignerDDGroup'
		});
		this.add(crosstabDesigner);
		this.doLayout();
	}
	
});
