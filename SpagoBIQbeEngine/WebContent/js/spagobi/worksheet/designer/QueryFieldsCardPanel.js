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
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.QueryFieldsCardPanel = function(config) { 

	
	var defaultSettings = {
		emptyMsg: LN('sbi.worksheet.designer.tabledesignerpanel.fields.emptymsg')
	};
	
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.queryFieldsCardPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.queryFieldsCardPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.addEvents("attributeDblClick");
	
	this.initEmptyMsgPanel();
	
	this.tableDesigner = new Sbi.worksheet.designer.QueryFieldsContainerPanel( {
		ddGroup: this.ddGroup
	});
	// propagate event
	this.tableDesigner.on(
		'attributeDblClick' , 
		function (thePanel, attribute) { 
			this.fireEvent("attributeDblClick", this, attribute); 
		}, 
		this
	);
		
	c = {
			items: [this.emptyMsgPanel, this.tableDesigner]
		    , enableDragDrop: true
		    , border: false
		    , ddGroup: this.ddGroup || 'crosstabDesignerDDGroup'
			, layout: 'card'
			, activeItem: 0
			, height: 280
			, style: 'margin-top: 10px; margin-left: auto; margin-right: auto;'
			, width: 250
	};
	
	this.on('render', this.initDropTarget, this);
	this.on('afterLayout', this.setActiveItem, this);
	
	Sbi.worksheet.designer.QueryFieldsCardPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.worksheet.designer.QueryFieldsCardPanel, Ext.Panel, {
	tableDesigner: null,
	emptyMsgPanel : null,
	emptyMsg : null,
	

	initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
		    frame: true,
			title: LN('sbi.worksheet.designer.tabledesignerpanel.fields'),
			html: this.emptyMsg
		});
	}
	
	, initDropTarget: function() {
		this.removeListener('render', this.initDropTarget, this);
		var dropTarget = new Sbi.widgets.GenericDropTarget(this, {
			ddGroup: this.ddGroup
			, onFieldDrop: this.onFieldDrop
		});
	}

	, onFieldDrop: function(ddSource) {
		if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'queryFieldsPanel') {
			this.tableDesigner.notifyDropFromQueryFieldsPanel(ddSource);
		} else if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'queryFieldsContainerPanel') {
			// do nothing (TODO: manage fields order)
		} else {
			alert('Unknown drag source');
		}
	}
	
	, setActiveItem: function() {
		this.un('afterLayout', this.setActiveItem, this);
    	if (this.tableDesigner.getContainedValues().length > 0) {
    		this.getLayout().setActiveItem( 1 );
    	} else {
    		this.getLayout().setActiveItem( 0 );
    	}
    	
    	//if the table has no data we show the empty message 
    	this.tableDesigner.on('storeChanged', this.setActiveItem, this);
	}
	
});
