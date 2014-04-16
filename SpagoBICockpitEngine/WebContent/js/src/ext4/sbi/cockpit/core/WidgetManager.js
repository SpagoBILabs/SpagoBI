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
     * The  object monitor contains all active selections
     */
    , selections: null
    
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
    // private methods
	// -----------------------------------------------------------------------------------------------------------------
    
    , onWidgetAdd: function(index, widget, key) {
    	//widget.setParentContainer(this);
    }
    
    , onWidgetRemove: function(widget, key) {
    	//widget.setParentContainer(null);
    }

    , onSelection: function(c){
    	Sbi.trace("[WidgetManager.onSelection]: IN");
    	
    	if (!Sbi.isValorized(this.selections)) this.selections = [];
    	this.removeSelections(c.widgetName);
    	this.addSelections(c);
    	
    	
    	Sbi.trace("[WidgetManager.onSelection]: OUT");
    }
    
    /**
	 * @method
	 * 
	 * Delete the selections list from the object that will be added
	 * 
	 * @param {w} w The widget name
	 * 
	 */
    , removeSelections: function(w){
    	var node = this.getWidgetSelectionNode(w);
    	if (Sbi.isValorized(node) && ! Sbi.isEmptyNode(node)){
			delete this.selections[w];
    	}
    }
    
    /**
	 * @method
	 * 
	 * Adds a selection to the list following the next structure:
	 * selections: [ ext-comp-2014 : {
	 *							STATE: {values:['CA','WA']}
	 *						  , FAMILY:  {values:['Food', 'Drink']}
	 *				 }
	 *		 		 ext-comp-1031 : {
	 *							MEDIA: {values:['TV']}
	 *						  , CUSTOMER:  {values:['79','99']}
	 *				 }
	 *		]
	 * 
	 * @param {c} c The configuration of the widget.
	 * 
	 */
    , addSelections: function(c){
    	for (var i=0; i< c.widgetData.length; i++){
	    	var meta = Sbi.storeManager.getRecordMeta(c.widgetData[i]);
	    	var data = c.widgetData[i].data;
	    	var selection = {};
	    	if (Sbi.isValorized(meta)){
	    		for (d in data){    			    	    	
	    			if (d !== 'id' && d !== 'recNo'){    			
	    				var header = Sbi.storeManager.getFieldHeaderByName(meta, d);
	    				var value = data[d];
	    				var selectionNode =  this.getWidgetSelectionNode(c.widgetName);
	    				var selectionValues = this.getWidgetSelectionValues(selectionNode, header);
	    				if (Sbi.isValorized(header) && Sbi.isValorized(value)){
		    				selectionValues.push(value);	
		    				if (Sbi.isEmptyNode(selectionNode)){
			    				selectionNode.values = selectionValues;
			    				selection[header] = selectionNode;
		    				}
	    				}
	    			}
	    		}
	    	}   
	    	if (!Sbi.isEmptyNode(selection))
	    		this.selections[c.widgetName] = selection;
    	}
    }
        
    /**
	 * @method
	 * 
	 * Returns the object with all the selections linked to the widget in input
	 * 
	 * @param {w} w The widget name
	 * 
	 * @return {Object} object with selections
	 */
    , getWidgetSelectionNode: function(w){
    	for (s in this.selections){
    		if (s === w) {
    			return this.selections[s];
    		}
    	}
    	return {};    	
    }
    
    /**
	 * @method
	 * 
	 * Returns the object with all the selections values 
	 * 
	 * @param {selectionNode} selectionNode The selection Node with all informations
	 * @param {s} s The label for get the specific value
	 * 
	 * @return {Object} object with selections values
	 */
    , getWidgetSelectionValues: function(selectionNode, s){
    	for (n in selectionNode){
    		if (n === s) return selectionNode[n].values;
    	}
    	return [];  
    }
    
     /**
	 * @method
	 * 
	 * Returns the selections list
	 * 
	 */
	, getSelections: function() {
		return this.selections;
	}
   
    // =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
});