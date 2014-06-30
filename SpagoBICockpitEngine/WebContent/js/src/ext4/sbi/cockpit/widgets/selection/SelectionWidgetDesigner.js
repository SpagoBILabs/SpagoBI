/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
  

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
 * Authors - Alberto Alagna
 */
Ext.ns("Sbi.cockpit.widgets.selection");

Sbi.cockpit.widgets.selection.SelectionWidgetDesigner = function(config) { 

	var defaultSettings = {
		name: 'selectionWidgetDesigner'		
	};		
		
	if (Sbi.settings && Sbi.settings.cockpit && Sbi.settings.cockpit.widgets && Sbi.settings.cockpit.widgets.selection && Sbi.settings.cockpit.widgets.selection.selectionWidgetDesigner) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.cockpit.widgets.selection.selectionWidgetDesigner);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c); 
	
	this.init();
	
	c = {
		items: [this.selectionPanel]
		,title: LN('sbi.selection.selectiondefinitionpanel.title')
		,border: false
	};	
	
	Sbi.cockpit.widgets.selection.SelectionWidgetDesigner.superclass.constructor.call(this, c);
		
};

Ext.extend(Sbi.cockpit.widgets.selection.SelectionWidgetDesigner, Sbi.cockpit.core.WidgetDesigner, {

	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	selectionPanel: null
	
	//-----------------------------------------------------------------------------------------------------------------
	//public methods
	//-----------------------------------------------------------------------------------------------------------------
	, getDesignerState: function() {
		Sbi.trace("[SelectionWidgetDesigner.getDesignerState]: IN");
		Sbi.trace("[SelectionWidgetDesigner.getDesignerState]: " + Sbi.cockpit.widgets.selection.SelectionWidgetDesigner.superclass.getDesignerState);
		
		var state = Sbi.cockpit.widgets.selection.SelectionWidgetDesigner.superclass.getDesignerState(this);						
						
		Sbi.trace("[SelectionWidgetDesigner.getDesignerState]: OUT");
		
		return state;
	}
	
	, setDesignerState: function(state) {
		Sbi.trace("[SelectionWidgetDesigner.setDesignerState]: IN");
		Sbi.cockpit.widgets.selection.SelectionWidgetDesigner.superclass.setDesignerState(this, state);					
		
		Sbi.trace("[SelectionWidgetDesigner.setDesignerState]: OUT");
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
    // -----------------------------------------------------------------------------------------------------------------		
	, init: function() {
		this.selectionPanel = new Ext.Panel({			
			baseCls:'x-plain'
			, padding: '5 0 0 5'
			, layout:'fit'
			, html: LN('sbi.selection.selectiondefinitionpanel.nodefrequired')
		});
	}

});