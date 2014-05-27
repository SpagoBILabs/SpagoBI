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
    
    /**
     * @property {Object} selections
     * The object containing current selection state. Selected values are indexed first by widget (widgetSelections) then by 
     * field (fieldSelections) as shown in the following example:
     * 
     *	{
     * 		ext-comp-2014 : {
	 *			STATE: {values:['CA','WA']}
	 *			, FAMILY:  {values:['Food', 'Drink']}
	 *		}
	 *		ext-comp-1031 : {
	 *			MEDIA: {values:['TV']}
	 *		  	, CUSTOMER:  {values:['79','99']}
	 *		}
	 *	}
     */
    , selections: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================

    // -----------------------------------------------------------------------------------------------------------------
    // widgets management methods
	// -----------------------------------------------------------------------------------------------------------------

    /**
     * @method 
     * 
     * Registers a widget to this widget manager.
     * 
     * @param {Sbi.cockpit.core.WidgetRuntime} The widget.
     */    
    , register: function(w) {
    	w.on('selection', this.onSelection, this);
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
		Sbi.trace("[WidgetManager.unregister]: IN");
		if(this.widgets.contains(w)) {
			var storeId = w.getStoreId();
			w.unboundStore();
			this.widgets.remove(w);
			Sbi.info("[WidgetManager.unregister]: widget [" + this.widgets.getKey(w) + "] succesfully unregistered. " +
					"Now there are [" + this.widgets.getCount()+ "] registered widget(s)");
			if( this.isStoreUsed(storeId) == false) {
				Sbi.storeManager.removeStore(storeId, true );
				Sbi.info("[WidgetManager.unregister]: store [" + storeId + "] succesfully removed");
			} else {
				Sbi.info("[WidgetManager.unregister]: store [" + storeId + "] not removed because there are other widgets using it");;
			}
		} else {
			Sbi.warn("[WidgetManager.unregister]: widget [" + this.widgets.getKey(w) + "] is not registered in this manager.");
		}
		Sbi.trace("[WidgetManager.unregister]: OUT");
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
		if (Sbi.isValorized(storeId)){
			for(var i=0; i < this.widgets.getCount(); i++){
				var w = this.widgets.item(i);
				if (Sbi.isValorized(w.getStoreId()) && w.getStoreId() == storeId  ){
					toReturn.add(w);
				}
			}
		}
		
		Sbi.trace("[WidgetManager.getWidgetsByStore]: store [" + storeId + "] is used " +
				"by [" + toReturn.getCount()  + "] widget(s)");
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
    // selection management methods
	// -----------------------------------------------------------------------------------------------------------------
	
	// -- selection ----
	 /**
	 * @method
	 * 
	 * @returns current #selections
	 * 
	 */
	, getSelections: function() {
		return this.selections;
	}
	
	/**
	 * @method
	 * 
	 * set the current #selections
	 * 
	 */
	, setSelections: function(selections) {
		this.selections = selections;
	}
	
	/**
	 * @method
	 * 
	 * clear current #selections
	 * 
	 */
	, clearSelections: function() {
		this.selections = {};
	}
	
	/**
	 * @method 
	 * 
	 * add the passed in selections to the current #selections
	 */
    , addSelections: function(selections){
    	Sbi.trace("[WidgetManager.addSelections]: IN");
    	
    	for (var widgetId in selections){
    		this.addWidgetSelections(widgetId, selections[widgetId]);
    	}
    	Sbi.trace("[WidgetManager.addSelections]: OUT");
    }
	
    // -- widget selections ----
    
    /**
	 * @method
	 * 
	 * Returns the field selections over the specified widget
	 * 
	 * @param {String} widgetId The widget id
	 * 
	 * @return {Object} the field selections encoded using an object as the one show in the following
	 *  example:
	 *  	{
	 *			STATE: {values:['CA','WA']}
	 *			, FAMILY:  {values:['Food', 'Drink']}
	 *		}
	 *  If no selections are specified over the input widget an empty object is returned.
	 */
    , getWidgetSelections: function(widgetId) {
    	return this.selections[widgetId] || {};
    }
    
	/**
	 * @method
	 * 
	 * Set the field selections over the specified widget
	 * 
	 *  @param {String} widgetId The id of the widget
	 *  @param {Object} selections the widget selections encoded using an object as the one show in the following
	 *  example:
	 *  	{
	 *			STATE: {values:['CA','WA']}
	 *			, FAMILY:  {values:['Food', 'Drink']}
	 *		}
	 *  
	 */
    , setWidgetSelections: function(widgetId, selections) {
    	this.selections[widgetId] = selections;
    }
    
    /**
	 * @method
	 * 
	 * Clear selection of the specified widget
	 * 
	 * @param {String} widgetId The widget id
	 * 
	 */
    , clearWidgetSelections: function(widgetId){
    	Sbi.trace("[WidgetManager.clearWidgetSelections]: IN");
    	var widgetSelections = this.getWidgetSelections(widgetId);
    	if (Sbi.isValorized(widgetSelections) && Sbi.isNotEmptyObject(widgetSelections)){
			delete this.selections[widgetId];
			Sbi.debug("[WidgetManager.clearWidgetSelections]: selections specified over widget [" + widgetId + "] have been succesfully cleared");
    	} else {
    		Sbi.debug("[WidgetManager.clearWidgetSelections]: no selections specified over widget [" + widgetId + "]");
    	}
    	Sbi.trace("[WidgetManager.clearWidgetSelections]: IN");
    }

    , addWidgetSelections: function(widgetId, selections){
    	Sbi.trace("[WidgetManager.addWidgetSelections]: IN");
    	for (var fieldHeader in selections){
    		this.addFieldSelections(widgetId, fieldHeader, selections[fieldHeader].values);
    	}
    	Sbi.trace("[WidgetManager.addWidgetSelections]: OUT");
    }
 
    // -- field selections ----
    
    /**
     * @method
     * 
     * @return {Object} the field selections encoded using an object as the one show in the following
	 *  example:
	 *  	FAMILY:  {values:['Food', 'Drink']}
	 *  If no selections are specified over the input filed an empty object is returned. 
     */
    , getFieldSelections: function(widgetId, fieldHeader) {
    	return this.getWidgetSelections(widgetId)[fieldHeader] || {};
    }
    
    /**
     * @method
     */
    , setFieldSelections: function(widgetId, fieldHeader, selections) {
    	this.selections[widgetId] = this.selections[widgetId] || {};
    	this.selections[widgetId][fieldHeader] = selections;
    }
    
    , clearFieldSelections: function(widgetId, fieldHeader) {
    	this.selections[widgetId] = this.selections[widgetId] || {};
    	this.selections[widgetId][fieldHeader] = {values: []};
    }
    
    , addFieldSelections: function(widgetId, fieldHeader, valuesToAdd) {
		var currentSelectedValues = this.getFieldSelectedValues(widgetId, fieldHeader);
		var values = Ext.Array.union(currentSelectedValues, valuesToAdd);
    	this.setFieldSelectedValues(widgetId, fieldHeader, values);
	}
    
    // -- value selections ----
    /**
     * @method
     * 
     * @return {Array} the selected values over the specified field of the specified widget. An empty array
     * if no values are selected.
     */
    , getFieldSelectedValues: function(widgetId, fieldHeader) {
    	return this.getFieldSelections(widgetId, fieldHeader).values || [];
    }
    
    /**
     * @method
     */
    , setFieldSelectedValues: function(widgetId, fieldHeader, values) {
    	this.setFieldSelections(widgetId, fieldHeader, {values: values});
    }
    
    // -- store field selections ----
    
    // a store filed selections contain all the values selected for the specific field in all the widgets that use the 
    // store that contains the field itself
    
    /** 
	 * @method
	 */
	, getStoreFieldSelectedValues: function(store, fieldHeader) {
		Sbi.trace("[WidgetManager.getSelectionsOnField]: IN");
		
		var selectedValues = {};
		var widgets = this.getWidgetsByStore(store);
		
		for(var i = 0; i < widgets.getCount(); i++) {
			var widget = widgets.get(i);
			var values = this.getFieldSelectedValues(widget.getId(), fieldHeader);
			for(var j = 0; j < values.length; j++) {
				selectedValues[values[j]] = values[j];
				Sbi.trace("[SelectionsPanel.getSelectionsOnField]: Added value [" + values[j] + "] to selection on field [" + fieldHeader + "]");
			} 
		}
		
		Sbi.trace("[SelectionsPanel.getSelectionsOnField]: OUT");
		
		return selectedValues;
	}
	
	// -- selections by associations ----
	
	/**
	 * @returns the selections grouped by associations like in the following example:
	 * 
	 * 	{
	 * 		cityAssociation: ['Milan', 'Turin']
	 * 		, customerAssociation: ['Andrea', 'Sofia', 'Lucio']
	 * 	}
	 */
	, getSelectionsByAssociations: function() {
		var selectionsByAssociations = {};
		
		var selections = this.getSelections();
		
		var associations = Sbi.storeManager.getAssociationConfigurations();
		for(var i = 0; i <  associations.length; i++){
			var selectedValues = {};
			var fields = associations[i].fields;
			for(var j = 0; j <  fields.length; j++){
				var field = fields[j];
				var values = this.getStoreFieldSelectedValues(field.store, field.column);
				Ext.apply(selectedValues, values);
			}
			var results = [];
			for(var value in selectedValues) { results.push(value); }
	
			selectionsByAssociations[associations[i].id] = results;
		}
		return selectionsByAssociations;
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

    , onSelection: function(widget, selections){
    	Sbi.trace("[WidgetManager.onSelection]: IN");
    
    	//alert("onSelection [" + widget.getId() + "]: " + Sbi.toSource(selections));
    	this.setWidgetSelections(widget.getId(), selections);
    	
    	var associationGroup = Sbi.storeManager.getAssociationGroupByStore( widget.getStore() );
    	//alert("onSelection: " + Sbi.toSource(associationGroup));
    	
    	if(Sbi.isValorized(associationGroup)) {
    		var selections = this.getSelectionsByAssociations();
        	Sbi.storeManager.loadStores( associationGroup,  selections);
    	}
    	
  
    	Sbi.trace("[WidgetManager.onSelection]: OUT");
    }
    
    
   
    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
    , init: function() {
    	this.widgets = new Ext.util.MixedCollection();
    	this.selections = {};
    	this.widgets.on('add', this.onWidgetAdd, this);
    	this.widgets.on('remove', this.onWidgetRemove, this);
	}
    
    // =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
});