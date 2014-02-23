/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.core");

/**
 * @class Sbi.cockpit.core.WidgetExtensionPoint
 * <p>This object provides a registry of available Widget's extensions
 * indexed by a mnemonic code known as the Widget's {@link Sbi.cockpit.core.Widget#wtype wtype}. 
 * The <code>{@link Sbi.cockpit.core.Widget#wtype wtype}</code> provides a way to dynamically instantiate a widget's runtime or designer 
 * component without hard coding their constructors into the code. In this way new widgets can easily plugged into the platform</p>. 
 * To implement a new widget extension it is necessary to implement the following two classes:
 * 
 * <ul>
 * <li>{@link Sbi.cockpit.core.Sbi.cockpit.core.Widget Sbi.cockpit.core.Widget}</li> that manage the runtime facet of the widget 
 * <li>{@link Sbi.cockpit.core.Sbi.cockpit.core.Widget Sbi.cockpit.core.WidgetDesigner}</li> that manage teh edit facet of the widget
 * </ul>  
 * 
 * Onece the two calsses has been implemented the new extension can be registered as shown in the following example:
 * 
 * <pre><code>
Sbi.registerWidget('table', {
	name: 'Table'
	, icon: 'js/src/ext/sbi/cockpit/widgets/table/table_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.table.TableWidget'
	, designerClass: 'Sbi.cockpit.widgets.table.TableWidgetDesigner'
});
</code></pre>
 *
 * <code>Sbi.registerWidget</code> is a shortcut to {@link Sbi.cockpit.core.WidgetExtensionPoint#registerWidget registerWidget}. The list 
 * of all available <b>shortcusts</b> is:
 * 
 * <ul>
 * <li><code>Sbi.registerWidget<code> for {@link Sbi.cockpit.core.WidgetExtensionPoint#registerWidget registerWidget}</li>
 * <li><code>Sbi.unregisterWidget<code> for {@link Sbi.cockpit.core.WidgetExtensionPoint#unregisterWidget unregisterWidget}</li>
 * </ul> 
 * @singleton
 */
Sbi.cockpit.core.WidgetExtensionPoint = {
	registry: {}

	/**
	 * @method
	 * Registers a widget.
	 * 
	 * @param {String} wtype The mnemonic code of the registered widget
	 * @param {Object} wdescriptor The object that describe the registered widget extension
	 * @param {String} wdescriptor.name The descriptive name of the widget extension
	 * @param {String} wdescriptor.icon The icon associate to the widget extension
	 * @param {String} wdescriptor.runtimeClass The name of the class used as widget's runtime. It must extend Sbi.cockpit.core.Widget
	 * @param {String} wdescriptor.designerClass The name of the class used as widget's designer. It must extend Sbi.cockpit.core.WidgetDesigner
	 */
	, registerWidget: function(wtype, wdescriptor) {
		Sbi.trace("[WidgetExtensionPoint.registerWidget]: IN");
		Sbi.debug("[WidgetExtensionPoint.registerWidget]: registered widget extension type [" + wtype + "]");
		Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype] = wdescriptor;
		Sbi.trace("[WidgetExtensionPoint.registerWidget]: OUT");
	}
	
	/**
	 * @method
	 * Unregisters the widget extension whose wtype is equal to the one passed in as argument
	 * 
	 * @param {String} wtype The wtype of the widget extension to unregister
	 * 
	 * @return {Object} The descriptor of the unregistered widget extension (see #registerWidget to have more info about the structure of the descriptor object)
	 */
	, unregisterWidget: function(wtype) {
		Sbi.debug("[WidgetExtensionPoint.registerWidget]: unregistered widget extension type [" + wtype + "]");
		var wdescriptor = Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
		delete Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
		return wdescriptor;
	}
	
	/**
	 * @methd
	 * 
	 * Returns true if a widget of the passed in type is registered, false otherwise
	 * 
	 * @param {String} the widget type
	 * 
	 * @return {boolean} true if a widget of the specified type is registered
	 */
	, isWidgetRegistered: function(wtype) {
		var widgetDescriptor = this.getWidgetDescriptor(wtype);
		return  Sbi.isValorized(widgetDescriptor);
	}
	
	/**
	 * @method
	 * 
	 * Returns the list of types of registered widgets
	 * 
	 * @return {String[]} the list of types
	 */
	, getWidgetTypes: function() {
		var types = new Array();
		var registry = Sbi.cockpit.core.WidgetExtensionPoint.registry;
		for(wtype in registry) {
			types.push(wtype);
		}
		return types;
	}
	
	/**
	 * @method
	 * 
	 * Returns the list of all descriptors associated to the registered widgets. To see the inner structure of a descriptor object 
	 * see #registerWidget method. The returned descriptors are anyway just a  copy of the ones internally used by <code>WidgetExtensionPoint</code>
	 * so any modification made to them have no impact on the related extensions. To modify a descriptor associated to an extension type is necessary 
	 * to unregister it and the register the modified version.
	 * 
	 * @return {Object[]} the list of registered widgets descriptors
	 */
	, getWidgetDescriptors: function() {
		var descriptors = new Array();
		var registry = Sbi.cockpit.core.WidgetExtensionPoint.registry;
		for(wtype in registry) {
			descriptors.push( Ext.apply({}, registry[wtype]) );
		}
		return descriptors;
	}
	
	/**
	 * @method
	 */
	, getWidgetDescriptor: function(wtype) {
		return  Sbi.cockpit.core.WidgetExtensionPoint.registry[wtype];
	}
	
	/**
	 * @method
	 * 
     * Executes the specified function once for every registered widget, passing the following arguments:
     * <div class="mdetail-params"><ul>
     * <li><b>wtype</b> : String<p class="sub-desc">The widget extension type</p></li>
     * <li><b>index</b> : Object<p class="sub-desc">The widget extension descriptor. It's not the origunal one, just a copy. 
     * Any modification applied to it so have no impact on on the related extension</p></li>
     * <li><b>length</b> : Number<p class="sub-desc">The total number of items in the collection</p></li>
     * </ul></div>
     * The function should return a boolean value. Returning false from the function will stop the iteration.
     * @param {Function} fn The function to execute for each widget extension.
     * @param {Object} scope (optional) The scope (<code>this</code> reference) in which the function is executed. Defaults to the current item in the iteration.
     */
	, forEachWidget : function(fn, scope){
		
		var registry = Sbi.cockpit.core.WidgetExtensionPoint.registry;
	    for(var wtype in registry){
	    	if(fn.call(scope || window, wtype, Ext.apply({}, registry[wtype])) === false){
	                break;
	        }
	    }
	}
	
	/**
	 * @method
	 * 
	 * Returns a widget runtime If widget parameter is already a valid widget return it. Otherwise try to create it using 
	 * the #widget parameter as a configuration object passed to method #createWidget as shown in the following example
	 * 
 * <pre><code>
Sbi.cockpit.core.WidgetExtensionPoint.getWidget(widget);
</code></pre>
	 */
	, getWidget: function(widget) {		
		var w = null;
		
		Sbi.trace("[WidgetExtensionPoint.getWidget]: IN");
		if(Sbi.isNotValorized(widget)) {
    		Sbi.warn("[WidgetExtensionPoint.getWidget]: Input parameter [widget] is not valorized.");	
    	} else if( (widget instanceof Sbi.cockpit.core.Widget) === true) {
    		var wtype = widget.getWType();
    		Sbi.warn("[WidgetExtensionPoint.getWidget]: Input parameter [widget] is a widget object of type [" + wtype + "]");
    		if( this.isWidgetRegistered(wtype) ) {
    			w = widget;
    		} else {
    			Sbi.warn("[WidgetExtensionPoint.getWidget]: Input parameter [widget] is of an unregistered type");	
    		}
    	} else {
    		if(typeof widget === 'object' && (widget instanceof Ext.util.Observable) === false) {
    			Sbi.trace("[WidgetExtensionPoint.getWidget]: Input parameter [widget] is a widget configuration object equlas to [" + Sbi.toSource(widget, true) + "]");	
    			w = Sbi.cockpit.core.WidgetExtensionPoint.createWidget(widget);
    		} else {
    			Sbi.error("[WidgetExtensionPoint.addWidget]: Input parameter [widget] of type [" + (typeof widget) + "] is not valid");	
    		}	
    	}
		Sbi.trace("[WidgetExtensionPoint.getWidget]: OUT");
		
		return w;
	}
	
	/**
	 * @method
	 * 
	 * Returns a brandnew widget of type #conf.wtype. The parameter #conf is passed to the constructor of the new widget.
	 * 
	 * @param {Object} conf The object to pass to the constructor of the new widget
	 * @param {String} conf.wtype The type of the new widget
	 * @param {String} conf.storeId (optional) The label of the dataset that feed the new widget
	 * @param {Object} conf.wconf (optional) The custom configuration of the new widget
	 * @param {Object} conf.wlayout (optional) The layout configuration of the new widget
	 * @param {Object} conf.wstyle (optional) The style configuration of the new widget
	 * 
	 * @return {Sbi.cockpit.core.Widget} The new widget
	 */
	, createWidget: function(conf) {
		Sbi.trace("[WidgetExtensionPoint.createWidget]: IN");
		
		var wdescriptor = Sbi.cockpit.core.WidgetExtensionPoint.registry[conf.wtype];
		
		if(wdescriptor !== undefined) {
			Sbi.trace("[WidgetExtensionPoint.createWidget]: runtime class for widget of type [" + conf.wtype + "] is equal to [" + wdescriptor.runtimeClass + "]");
			var widget = Sbi.createObjectByClassName(wdescriptor.runtimeClass, conf);
			return widget;
		} else {
			alert("Widget of type [" + conf.wtype +"] not supprted. Supported types are [" + Sbi.cockpit.core.WidgetExtensionPoint.getWidgetTypes().join() + "]");
		}
		Sbi.trace("[WidgetExtensionPoint.createWidget]: OUT");
	}
	
	/**
	 * @method
	 * 
	 * Returns a widget designer. If designer parameter is already a valid widget designer return it. Otherwise try to create it using 
	 * the #designer parameter as a configuration object passed to method #createWidgetDesigner as shown in the following example
	 * 
 * <pre><code>
Sbi.cockpit.core.WidgetExtensionPoint.createWidgetDesigner(designer);
</code></pre>
	 */
	, getWidgetDesigner: function(designer) {
		var d = null;
		
		Sbi.trace("[WidgetExtensionPoint.getWidgetDesigner]: IN");
		if(Sbi.isNotValorized(designer)) {
    		Sbi.warn("[WidgetExtensionPoint.getWidgetDesigner]: Input parameter [designer] is not valorized.");	
    	} else if( (designer instanceof Sbi.cockpit.core.WidgetDesigner) === true) {
    		var wtype = designer.getWType();
    		Sbi.warn("[WidgetExtensionPoint.getWidgetDesigner]: Input parameter [designer] is a widget designer object of type [" + wtype + "]");
    		if( this.isWidgetRegistered(wtype) ) {
    			d = designer;
    		} else {
    			Sbi.warn("[WidgetExtensionPoint.getWidgetDesigner]: Input parameter [designer] is of an unregistered type");	
    		}
    	} else {
    		if(typeof designer === 'object' && (designer instanceof Ext.util.Observable) === false) {
    			Sbi.trace("[WidgetExtensionPoint.getWidgetDesigner]: Input parameter [designer] is a widget designer configuration object equlas to [" + Sbi.toSource(designer, true) + "]");	
    			d = Sbi.cockpit.core.WidgetExtensionPoint.createWidgetDesigner(designer);
    		} else {
    			Sbi.error("[WidgetExtensionPoint.getWidgetDesigner]: Input parameter [designer] of type [" + (typeof designer) + "] is not valid");	
    		}	
    	}
		Sbi.trace("[WidgetExtensionPoint.getWidgetDesigner]: OUT");
		
		return d;
	}
	
	/**
	 * @method
	 * 
	 * Returns a brandnew widget designer of type #conf.wtype. The parameter #conf is passed to the constructor of the new widget designer.
	 * 
	 * @param {Object} The widget designer configuration oject
	 *  
	 * @return {Sbi.cockpit.core.Widget} The new widget designer
	 */
	, createWidgetDesigner: function(conf) {
		Sbi.trace("[WidgetExtensionPoint.createWidgetDesigner]: IN");
		
		var wdescriptor = Sbi.cockpit.core.WidgetExtensionPoint.registry[conf.wtype];
		
		if(wdescriptor !== undefined) {
			var widgetDesigner = Sbi.createObjectByClassName(wdescriptor.designerClass, conf);
			if(Sbi.isNotValorized( widgetDesigner.getDesignerType() ) ) { // TODO remove this
				widgetDesigner.wtype = wtype;
			}
			return widgetDesigner;
		} else {
			alert("Widget of type [" + wtype +"] not supported. Supported types are [" + Sbi.cockpit.core.WidgetExtensionPoint.getWidgetTypes().join() + "]");
		}
		Sbi.trace("[WidgetExtensionPoint.createWidgetDesigner]: OUT");
	}
};

// shortcuts
Sbi.registerWidget = Sbi.cockpit.core.WidgetExtensionPoint.registerWidget;
Sbi.unregisterWidget = Sbi.cockpit.core.WidgetExtensionPoint.unregisterWidget;


