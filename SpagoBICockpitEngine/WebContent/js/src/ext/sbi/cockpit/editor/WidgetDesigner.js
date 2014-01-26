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
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.cockpit.editor");

Sbi.cockpit.editor.WidgetDesigner = function(config) { 

	var defaultSettings = {
		title: LN('Sbi.cockpit.editor.WidgetDesigner.title')
		, layout: 'fit'
	};
	var settings = Sbi.getObjectSettings('Sbi.cockpit.editor.WidgetDesigner', defaultSettings);
		
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	if(!c.items) {
		c.html = "Sono un widget designer";
	}
	
	Sbi.cockpit.editor.WidgetDesigner.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.cockpit.editor.WidgetDesigner, Ext.Panel, {
	
	getFormState: function() {
		var state = {};
		return state;
	}
	
	, setFormState: function(state) {
		
	}

	, validate: function(validFields){
		return;
	}
	
	, containsAttribute: function (attributeId) {
		return false;
	}
	
	
	
});
