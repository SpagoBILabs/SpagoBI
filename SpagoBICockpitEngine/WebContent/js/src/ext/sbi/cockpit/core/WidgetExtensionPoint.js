/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.core");

Sbi.cockpit.core.WidgetExtensionPoint = {
	registry: {}

	, registerWidget: function(wtype, wdescriptor) {
		Sbi.debug("[WidgetExtensionPoint.registerWidget]: registered widget extension type [" + wtype + "]");
		Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype] = wdescriptor;
	}
	
	, unregisterWidget: function(wtype, wdescriptor) {
		Sbi.debug("[WidgetExtensionPoint.registerWidget]: unregistered widget extension type [" + wtype + "]");
		var wdescriptor = Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
		delete Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
		return wdescriptor;
	}
	
	, forEachWidget : function(fn, scope){
		
		var registry = Sbi.cockpit.core.WidgetExtensionPoint.registry;
	    for(var wtype in registry){
	    	if(fn.call(scope || window, wtype, registry[wtype]) === false){
	                break;
	        }
	    }
	}
	
	, getWidgetTypes: function() {
		var types = new Array();
		var registry = Sbi.cockpit.core.WidgetExtensionPoint.registry;
		for(wtype in registry) {
			types.push(wtype);
		}
		return types;
	}
	
	, getWidgetDescriptors: function() {
		var descriptors = new Array();
		var registry = Sbi.cockpit.core.WidgetExtensionPoint.registry;
		for(wtype in registry) {
			descriptors.push(registry[wtype]);
		}
		return descriptors;
	}
	
	, getWidgetDescriptor: function(wtype) {
		return  Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
	}
	
	, getWidget: function(wtype, wconf) {
		Sbi.trace("[WidgetExtensionPoint.getWidget]: IN");
		
		var wdescriptor = Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
		
		if(wdescriptor !== undefined) {
			Sbi.trace("[WidgetExtensionPoint.getWidget]: runtime class for widget of type [" + wtype + "] is equal to [" + wdescriptor.runtimeClass + "]");
			var widget = Sbi.createObjectByClassName(wdescriptor.runtimeClass, wconf);
			return widget;
		} else {
			alert("Widget of type [" + wtype +"] not supprted. Supported types are [" + Sbi.cockpit.core.WidgetExtensionPoint.getWidgetTypes().join() + "]");
		}
		Sbi.trace("[WidgetExtensionPoint.getWidget]: OUT");
	}
	
	, getWidgetDesigner: function(wtype, wconf) {
		Sbi.trace("[WidgetExtensionPoint.getWidgetDesigner]: IN");
		var wdescriptor = Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
		
		wconf.wtype = wtype;
		if(wdescriptor !== undefined) {
			var widgetDesigner = Sbi.createObjectByClassName(wdescriptor.designerClass, wconf);
			if(Sbi.isNotValorized( widgetDesigner.getDesignerType() ) ) {
				widgetDesigner.wtype = wtype;
			}
			return widgetDesigner;
		} else {
			alert("Widget of type [" + wtype +"] not supported. Supported types are [" + Sbi.cockpit.core.WidgetExtensionPoint.getWidgetTypes().join() + "]");
		}
		Sbi.trace("[WidgetExtensionPoint.getWidgetDesigner]: OUT");
	}
};

// shortcuts
Sbi.registerWidget = Sbi.cockpit.core.WidgetExtensionPoint.registerWidget;

