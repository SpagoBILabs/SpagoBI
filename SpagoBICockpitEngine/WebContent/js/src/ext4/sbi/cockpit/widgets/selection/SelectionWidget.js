/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.widgets.selection");

Sbi.cockpit.widgets.selection.SelectionWidget = function(config) {	
		
	Sbi.trace("[SelectionWidget.constructor]: IN");
	
	var defaultSettings = {	
		layout: 'fit'			
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.selection.SelectionWidget', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	// constructor
	Sbi.cockpit.widgets.selection.SelectionWidget.superclass.constructor.call(this, c);			
	
	Sbi.trace("[SelectionWidget.constructor]: OUT");
};

Ext.extend(Sbi.cockpit.widgets.selection.SelectionWidget, Sbi.cockpit.core.WidgetRuntime, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	selectionsPanel: null    		
	
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, onRender: function(ct, position) {	
		Sbi.trace("[SelectionWidget.onRender]: IN");
			
		Sbi.cockpit.widgets.selection.SelectionWidget.superclass.onRender.call(this, ct, position);							
		
		var widgetManager = this.getWidgetManager();
		this.selectionsPanel = new Sbi.cockpit.core.SelectionsPanel({
			widgetManager: widgetManager
		});	
		//widgetManager.on('selectionChange',this.onSelectionChange,this);
//		this.selectionsPanel.on('performunselect', this.onPerformUnselect, this);
//		this.selectionsPanel.on('performunselectall', this.onPerformUnselect, this);
		this.items.each( function(item) {
			this.items.remove(item);
	        item.destroy();           
	    }, this);  
		this.add(this.selectionsPanel);
		this.doLayout();
		
		Sbi.trace("[SelectionWidget.onRender]: OUT");
	}	

//	, onPerformUnselect: function(grid, rowIndex, colIndex) {
//		this.widgetManager.clearSingleSelection(grid, rowIndex, colIndex);
//	}
//
//	, onPerformUnselectAll: function(){		
//		this.widgetManager.clearSelections();
//	}
});

Sbi.registerWidget('selection', {
	name: 'Selection'
	, icon: 'js/src/ext/sbi/cockpit/widgets/dummy/dummy_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.selection.SelectionWidget'
	, designerClass: 'Sbi.cockpit.widgets.selection.SelectionWidgetDesigner'	
});