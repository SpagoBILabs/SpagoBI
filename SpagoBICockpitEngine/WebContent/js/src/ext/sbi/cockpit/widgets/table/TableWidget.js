/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.widgets.table");

Sbi.cockpit.widgets.table.TableWidget = function(config) {	
	Sbi.trace("[TableWidget]: IN");
	// constructor
	Sbi.cockpit.widgets.table.TableWidget.superclass.constructor.call(this, config);
	
	Sbi.trace("[TableWidget]: OUT");
};

/**
 * @cfg {Object} config
 * ...
 */
Ext.extend(Sbi.cockpit.widgets.table.TableWidget, Sbi.cockpit.widgets.dummy.DummyWidget, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	// ...
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	onRender: function(ct, position) {	
		Sbi.trace("[TableWidget.onRender]: IN");
		
		this.msg = 'Sono un widget di tipo TABLE';
		
		Sbi.cockpit.widgets.table.TableWidget.superclass.onRender.call(this, ct, position);	
		
	
		Sbi.trace("[TableWidget.onRender]: OUT");
	}

   , getCustomConfiguration: function(){
	    Sbi.trace("[TableWidget.getCustomConfiguration]: IN");
		
	    Sbi.trace("[TableWidget.getCustomConfiguration]: wconf: " + Sbi.toSource(this.wconf, true));
		
		Sbi.trace("[TableWidget.getCustomConfiguration]: OUT");
		
		return Ext.apply({}, this.wconf);
	}
	
	// =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
});

Sbi.registerWidget('table', {
	name: 'Table'
	, icon: 'js/src/ext/sbi/cockpit/widgets/table/table_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.table.TableWidget'
	, designerClass: 'Sbi.cockpit.widgets.table.TableWidgetDesigner'
	//, designerClass: 'Ext.Panel'
});