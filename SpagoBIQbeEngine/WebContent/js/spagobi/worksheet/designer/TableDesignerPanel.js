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

Sbi.worksheet.designer.TableDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.tabledesignerpanel.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.tableDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.tableDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.addEvents("attributeDblClick", "attributeRemoved");
	
	this.tableDesigner = new Sbi.worksheet.designer.QueryFieldsCardPanel({ddGroup: this.ddGroup});
	// propagate events
	this.tableDesigner.on(
		'attributeDblClick' , 
		function (thePanel, attribute) { 
			this.fireEvent("attributeDblClick", this, attribute); 
		}, 
		this
	);
	this.tableDesigner.on(
		'attributeRemoved' , 
		function (thePanel, attribute) { 
			this.fireEvent("attributeRemoved", this, attribute); 
		}, 
		this
	);
	
	c = {
		layout: 'fit',
		height: 350,
		items: [new Ext.Panel({items:[this.tableDesigner], border: false, bodyStyle: 'width: 100%; height: 100%'})]
	};
	
	Sbi.worksheet.designer.TableDesignerPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.worksheet.designer.TableDesignerPanel, Ext.Panel, {
	tableDesigner: null,
	
	getFormState: function() {
		var state = {};
		state.designer = 'Table';
		state.visibleselectfields = this.tableDesigner.tableDesigner.getContainedValues();
		return state;
	}
	
	, setFormState: function(state) {
		if(state.visibleselectfields!=undefined && state.visibleselectfields!=null){
			this.tableDesigner.tableDesigner.setValues(state.visibleselectfields);
		}
	}
	/* tab validity: rules are
	 * - at least one measure or attribute is in
	 */

	, validate: function(){
		var vals = this.tableDesigner.tableDesigner.getContainedValues();
		if (vals && vals.length> 0) {return;} // OK
		else {return LN("sbi.designertable.tableValidation.noElement");} // ERROR MESSAGE
	}
	
	, containsAttribute: function (attributeId) {
		return this.tableDesigner.containsAttribute(attributeId);
	}
	
});
