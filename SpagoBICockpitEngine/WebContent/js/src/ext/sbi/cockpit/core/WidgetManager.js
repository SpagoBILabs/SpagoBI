/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.core");

/**
 * @class Sbi.cockpit.core.WidgetManager
 * @extends Ext.util.Observable
 * 
 *  It handles:
 *  - widgets lifecycle management: register, unregister, lookup
 *  - shared resources: through env
 *  - intra-widgets comunications: sendMessage (asyncronous: point to point or broadcast)
 */

/**  
 * @cfg {Object} config
 * ...
 */
Sbi.cockpit.core.WidgetManager = function(config) {
	
	// init properties...
	var defaultSettings = {
		// set default values here
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.core', defaultSettings);
	
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);

	this.init();
	
	// constructor
	Sbi.cockpit.core.WidgetManager.superclass.constructor.call(this, c);
};


Ext.extend(Sbi.cockpit.core.WidgetManager, Ext.util.Observable, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Ext.util.MixedCollection} widgets
     * The collection of widgets managed by this container
     */
    widgets: null
    
    /**
     * @property {Object} env
     * This container environment
     * WARNINGS: not used at the moment
     */
    , env: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , init: function() {
    	this.widgets = new Ext.util.MixedCollection();
    	this.widgets.on('add', this.onWidgetAdd, this);
    	this.widgets.on('remove', this.onWidgetRemove, this);
	}

    // -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

    /**
     * @method 
     * 
     * Registers a widget to this widget manager.
     * 
     * @param {Sbi.cockpit.core.WidgetRuntime} The widget.
     */    
    , register: function(w) {
    	this.widgets.add(w);
    	Sbi.info("[WidgetManager.register]: widget [" + this.widgets.getKey(w) + "] succesfully registered. Now there are [" + this.widgets.getCount()+ "] registered widget(s)");
	}

    /**
     * @method 
     * 
     * Unregisters a widget from this widget manager.
     * 
     * @param {Sbi.cockpit.core.WidgetRuntime} The widget.
     */  
	, unregister: function(w) {
		this.widgets.remove(w);
		Sbi.info("[WidgetManager.unregister]: widget [" + this.widgets.getKey(w) + "] succesfully unregistered. Now there are [" + this.widgets.getCount()+ "] registered widget(s)");
	}
	
	/**
	 * @method
	 * 
	 * Gets the specified registered widget
	 * 
	 * @param {Sbi.cockpit.core.WidgetRuntime/String} The widget or its id.
	 */
	, getWidget: function(w) {
		if(Ext.isString(w)) {
			return this.widgets.getKey(w);
		} else {
			return this.widgets.get(w);
		}	
	}
	
	/**
	 * @methods 
	 * 
	 * Returns all the registered widgets.
	 * 
	 * @return {Sbi.cockpit.core.WidgetRuntime[]} the list of registered widgets.
	 */
	, getWidgets: function() {
		return this.widgets.getRange();
	}	
	
	, getWidgetCount: function() {
		return this.widgets.getCount();
	}
	
	 /**
	  * @method
	  * 
     * Executes the specified function once for every registered widget, passing the following arguments:
     * <div class="mdetail-params"><ul>
     * <li><b>item</b> : Sbi.cockpit.core.WidgetRuntime<p class="sub-desc">The widget</p></li>
     * <li><b>index</b> : Number<p class="sub-desc">The widget's index</p></li>
     * <li><b>length</b> : Number<p class="sub-desc">The total number of widgets in the collection</p></li>
     * </ul></div>
     * The function should return a boolean value. Returning false from the function will stop the iteration.
     * @param {Function} fn The function to execute for each widget.
     * @param {Object} scope (optional) The scope (<code>this</code> reference) in which the function is executed. Defaults to the current widget in the iteration.
     */
	, forEachWidget: function(fn, scope) {
		this.widgets.each(fn, scope);
	}
	/**
	 * @method
	 * 
	 * Returns a list of widgets that are feed by the specified store.
	 * 
	 * @param {String} storeId The id of the store.
	 * 
	 * @return {Sbi.cockpit.core.WidgetRuntime[]} The list of widgets.
	 */
	, getWidgetsByStore: function(storeId){
		Sbi.trace("[WidgetManager.getWidgetsByStore]: IN");
		
		var toReturn = new Ext.util.MixedCollection();
		if (getStoreId != undefined){
			for(var i=0; i < this.widgets.getCount(); i++){
				var w = this.widgets.item(i);
				if (w.getStoreId() !== undefined && w.getStoreId() == storeId  ){
					toReturn.add(w);
				}
			}
		}
		
		Sbi.trace("[WidgetManager.getWidgetsByStore]: OUT");
		
		return toReturn;
	}
	
	/**
	 * @method
	 * 
	 * Returns true if the store is used at least by one widget managed by this manager,
	 * false otherwise
	 * 
	 * @param {String} storeId The id of the store.
	 * 
	 * @return {boolean} true if the store is used at least by one widget managed by this manager,
	 * false otherwise.
	 */
	, isStoreUsed: function(storeId) {
		var widgets = this.getWidgetsByStore(storeId);
		return Sbi.isValorized(widgets)  && widgets.getCount() > 0;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
    
    , onWidgetAdd: function(index, widget, key) {
    	//widget.setParentContainer(this);
    }
    
    , onWidgetRemove: function(widget, key) {
    	//widget.setParentContainer(null);
    }
    
    // =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
});