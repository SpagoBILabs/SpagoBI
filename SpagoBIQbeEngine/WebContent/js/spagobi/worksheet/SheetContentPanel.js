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
Ext.ns("Sbi.worksheet");

Sbi.worksheet.SheetContentPanel = function(config) { 

	var c ={
			html: 'ciao'
	}
	Sbi.worksheet.SheetContentPanel.superclass.constructor.call(this, c);	
	this.on('render', this.initDropTarget, this);

};

Ext.extend(Sbi.worksheet.SheetContentPanel, Ext.Panel, {
	 
	
	initDropTarget: function() {
		// This will make sure we only drop to the view container
		var formPanelDropTargetEl =  this.body.dom;

		var formPanelDropTarget = new Ext.dd.DropTarget(formPanelDropTargetEl, {
			ddGroup     : 'paleteDDGroup',

			notifyDrop  : function(ddSource, e, data){
				alert(data.toSource());
			}
		});
	}

});
