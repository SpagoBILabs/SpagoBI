/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.cockpit.core");

/**
 * @class Sbi.cockpit.core.WidgetRuntime
 * @extends Ext.Panel
 * 
 * It's an abstract class that simplify the implementation of the runtime facet of a new widget extension. 
 * See {@link Sbi.cockpit.core.WidgetExtensionPointManager WidgetExtensionPointManager} to find out more 
 * information on widget's extension point.
 */

/**
 * @cfg {Object} config the widget configuration object
 * @cfg {String} config.storeId The label of the dataset used to feed the widget
 * @cfg {String} config.wtype The type of the widget
 * @cfg {Object} config.wconf The custom configuration of the widget. Its content depends on the widget's #wtype 
 * @cfg {Object} config.wstyle The style configuration of the widget. Its content depends on the widget's #wtype 
 * @cfg {Object} config.wlayout The layout configuration of the widget. Its content depends on the widget's parent {@link #parentContainer container container}
 */
Sbi.cockpit.core.WidgetRuntime = function(config) {	
		
	Sbi.trace("[Widget.constructor]: IN");
	
	// init properties...
	var defaultSettings = {
		border: false
		, bodyBorder: false
		, hideBorders: true
		, frame: false
		, defaultMsg: ' '
	};

	var settings = Sbi.getObjectSettings('Sbi.cockpit.core.WidgetRuntime', defaultSettings);
	
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
		
	if(Sbi.isNotValorized(this.items) || (Ext.isArray(this.items) && this.items.length === 0)) {
		Sbi.trace("[Widget.constructor]: the widget is empty");
		this.msgPanel = new Ext.Panel({
			html: this.defaultMsg
		});
		c = Ext.apply(c, { 
			border: false
			, bodyBorder: false
			, hideBorders: true
			, frame: false
		   	, items: [this.msgPanel]
		});
	} else {
		Sbi.trace("[Widget.constructor]: the widget has been properly initialized by subclass");
	}
	
		
	// constructor
	Sbi.cockpit.core.WidgetRuntime.superclass.constructor.call(this, c);
	
	Sbi.trace("[Widget.constructor]: OUT");
};

Ext.extend(Sbi.cockpit.core.WidgetRuntime, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Sbi.cockpit.core.WidgetPanel} parentContainer
     * The WidgetPanel object that contains the widget
     */
    parentContainer: null
    
    /**
     * @property {String} storeId
     * The label of the dataset used to feed the widget
     */
    , storeId: null
    
    /**
     * @property {String} wtype
     * The wtype of the widget extension as registered in {@link Sbi.cockpit.core.WidgetExtensionPointManager}
     * to which this runtime class is associated. 
     */
    , wtype: null
    
    /**
     * @property {String} wconf
     * The custom configuration of the widget. Its content depends on the widget type
     */
    , wconf: null
    
    /**
     * @property {String} wlayout
     * The custom layout configuration of the widget. Its content depends on the widget container used
     */
    , wlayout: null
    
    /**
     * @property {String} wstyle
     * The custom style of the widget.
     */
    , wstyle: null
    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	
    // -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
    
    /**
     * Refresh the widget's content. This method is abstract and so must be implemented properly by subclass.
     */
    , refresh:  function() {  
		Sbi.trace("[Widget.refresh]: IN");
		Sbi.trace("[Widget.refresh]: OUT");
	}

	/**
	 * @method
	 * 
	 * Sets the configuration of the widget. All part of the configuration are optional. If a part of the configuratio is not specified
	 * it is simply not applied.
	 * 
	 * @param {Object} config The widget configuration object.
	 * @param {String} config.storeId The label of the dataset used to feed the widget.
	 * @param {String} config.wtype The wtype of the widget.
	 * @param {Object} config.wconf The custom configuration of the widget. Its content depends on the widget's #wtype.
	 * @param {Object} config.wstyle The style configuration of the widget. Its content depends on the widget's #wtype.
	 * @param {Object} config.wlayout The layout configuration of the widget. Its content depends on the widget's #parentContainer
	 * @param {boolean} refresh true to force the refresh of the widget after after the the configuration is set, false otherwise. The default is true.
	 */
	, setConfiguration: function(config, refresh) {
		Sbi.trace("[Widget.setConfiguration]: IN");
		
		this.setStoreId(config.storeId, false);
		this.setWType(config.wtype, false);
		this.setCustomConfiguration(config.wconf, false);
    	this.setStyleConfiguration(config.wstyle, false);
    	
    	this.setLayoutConfiguration(config.wlayout);
    	
    	if(refresh !== false) {
			this.refresh();
		} else {
			Sbi.trace("[Widget.setConfiguration]: Input parameter [refresh] is equal [" + refresh + "] to so widget won't be refreshed");
		}
		
		Sbi.trace("[Widget.setConfiguration]: OUT");
	}
	
	/**
	 * @method
	 * 
	 * Returns the widget configuration object.
	 * 
	 * @return {Object} The widget configuration object.
	 * @return {String} return.storeId The label of the dataset used to feed the widget.
	 * @return {String} return.wtype The wtype of the widget.
	 * @return {Object} return.wconf The custom configuration of the widget. Its content depends on the widget's #wtype.
	 * @return {Object} return.wstyle The style configuration of the widget. Its content depends on the widget's #wtype.
	 * @return {Object} return.wlayout The layout configuration of the widget. Its content depends on the widget's #parentContainer
	 */
    , getConfiguration: function() {
    	Sbi.trace("[Widget.getConfiguration]: IN");
    	
    	var config = {};
		
    	if(Sbi.isValorized(this.getStoreId())) config.storeId = this.getStoreId();
		if(Sbi.isValorized(this.getWType())) config.wtype = this.getWType();
    	config.wconf = this.getCustomConfiguration() || {};
    	config.wstyle = this.getStyleConfiguration() || {};
    	
    	config.wlayout = this.getLayoutConfiguration() || {};

    	Sbi.trace("[Widget.getConfiguration]: OUT");
    	
    	return config;
	}
    
	/**
	 * @method
	 * Sets the label of the dataset used to feed the widget
	 * 
	 * @param {String} storeId The dataset's label
	 * @param {boolean} refresh true to force the refresh after the setting of the property, false otherwise. The default is true.
	 */
	, setStoreId: function(storeId, refresh) {
		if(Sbi.isValorized(storeId)) {
			this.storeId = storeId;
			Sbi.trace("[Widget.setStoreId]: Store id set to [" + storeId + "]");
			if(refresh !== false) {
				this.refresh();
			} else {
				Sbi.trace("[Widget.setStoreId]: Input parameter [refresh] is equal [" + refresh + "] to so widget won't be refreshed");
			}
		} else {
			Sbi.trace("[Widget.setStoreId]: Input parameter [storeId] is not valorized so the property [storeId] will be left unchanged");
		}
		
	}
	
    /**
     * @method
     * 
     * Returns the label of the dataset used to feed the widget
     * 
     * @return {String} the dataset's label
     */
    , getStoreId: function() {
    	return this.storeId;
    	
    }
	
	/**
	 * @method
	 * 
	 * Returns the store that feed this widget
	 * 
	 * @param {boolean} forceCreation true to create and add a new store to <code>Sbi.storeManager</code> if there is not
	 * yet a store with #storeId already managed by global store manager, false otherwise. The default value is false.
	 *   
	 * @return {Ext.data.Store} The store
	 */
	, getStore: function(forceCreation) {
		var store;
		
		if(Sbi.isNotValorized(this.getStoreId())) {
			Sbi.warn("[Widget.getStore]: The widget have no store associated (i.e storeId in not valorized)");
			return null;
		}
		
		if(Sbi.storeManager.containsStore(this.getStoreId()) === false && forceCreation !== false) {
			Sbi.warn("[Widget.getStore]: store [" + this.getStoreId() + "] will be added to store manager");
			Sbi.storeManager.addStore({storeId: this.getStoreId()});
		}
		store = Sbi.storeManager.getStore( this.getStoreId() );
		
		return store;
	}
	
	
	/**
	 * @method
	 * @private
	 * 
     * Sets the wtype for this widget
     * 
     * @param {String} The wtype
     * @param {boolean} refresh true to force the refresh after the setting of the property, false otherwise. The default is true.
     */
	, setWType: function(wtype, refresh) {
		if(Sbi.isValorized(wtype)) {
			this.wtype = wtype;
			Sbi.trace("[Widget.setWType]: wtype set to [" + wtype + "]");
			if(refresh !== false) {
				this.refresh();
			} else {
				Sbi.trace("[Widget.setWType]: Input parameter [refresh] is equal [" + refresh + "] to so widget won't be refreshed");
			}
		} else {
			Sbi.trace("[Widget.setWType]: Input parameter [wtype] is not valorized so the property [wtype] will be left unchanged");
		}
		
	}
	
	/**
	 * @method
	 * 
     * Gets the wtype for this widget as registered in {@link Sbi.cockpit.core.WidgetExtensionPointManager}
     * 
     * @return {String} The wtype
     */
	, getWType: function() {
		return this.wtype;
	}
		
	/**
	 * @method
	 * 
	 * Sets the custom configuration of the widget. Its content depends on the widget's #wtype.
	 * 
	 * @param {Object} wconf The custom configuration of the widget.
	 * @param {boolean} refresh true to force the refresh after the setting of the property, false otherwise. The default is true.
	 */
	, setCustomConfiguration: function(wconf, refresh) {
		if(Sbi.isValorized(wconf)) {
			this.wconf = wconf;
			Sbi.trace("[Widget.setCustomConfiguration]: wconf set to [" + Sbi.toSource(wconf) + "]");
			if(refresh !== false) {
				this.refresh();
			} else {
				Sbi.trace("[Widget.setCustomConfiguration]: Input parameter [refresh] is equal [" + refresh + "] to so widget won't be refreshed");
			}
		} else {
			Sbi.trace("[Widget.setCustomConfiguration]: Input parameter [wconf] is not valorized so the property [wconf] will be left unchanged");
		}
	}
	
    /**
	 * @method
	 * 
	 * Gets the custom configuration of the widget. Its content depends on the widget's #wtype.
	 * 
	 * @return The custom configuration of the widget.
	 */
	, getCustomConfiguration: function() {
		Sbi.trace("[Widget.getCustomConfiguration]: IN");
		var config = Ext.apply({}, this.wconf || {});
		Sbi.trace("[Widget.getCustomConfiguration]: OUT");
		return config;
	}
	
	/**
	 * @method
	 * 
	 * Sets the style configuration of the widget. Its content depends on the widget's #wtype.
	 * 
	 * @param {Object} wconf The custom configuration of the widget.
	 * @param {boolean} refresh true to force the refresh after the setting of the property, false otherwise. The default is true.
	 */
	, setStyleConfiguration: function(wstyle) {
		if(Sbi.isValorized(wstyle)) {
			this.wstyle = wstyle;
			Sbi.trace("[Widget.setStyleConfiguration]: wstyle set to [" + Sbi.toSource(wstyle) + "]");
		} else {
			Sbi.trace("[Widget.setStyleConfiguration]: Input parameter [wstyle] is not valorized so the property [wstyle] will be left unchanged");
		}	
	}
	
	
	 /**
	 * @method
	 * 
	 * Gets the style configuration of the widget. Its content depends on the widget's #wtype.
	 * 
	 * @return The style configuration of the widget.
	 */
	, getStyleConfiguration: function() {
		return this.wstyle;
	}

	
	/**
	 * @method
	 * 
	 * Sets the layout configuration of the widget. Its content depends on the widget's #parentContainer.
	 * 
	 * @param {Object} wlayout The layout configuration of the widget.
	 * @param {boolean} toContainer true to force the set of the new layout also in the #parentContainer. The default is false.
	 */
	, setLayoutConfiguration: function(wlayout, toContainer) {
		if(Sbi.isValorized(wlayout)) {
			this.wlayout = wlayout;
			if(toContainer === true) {
				Sbi.trace("[Widget.setLayoutConfiguration]: update layout configuration also in parent container");
				//TODO update the layoutcof of this widet also in its container
			}
			Sbi.trace("[Widget.setLayoutConfiguration]: wlayout set to [" + Sbi.toSource(wlayout) + "]");
		} else {
			Sbi.trace("[Widget.setLayoutConfiguration]: Input parameter [wlayout] is not valorized so the property [wlayout] will be left unchanged");
		}
	}
	
	 /**
	 * @method
	 * 
	 * Gets the layout configuration of the widget. Its content depends on the widget's #parentContainer.
	 * 
	 * @param {boolean} fromContainer true to get the layoutConf directly form #parentConatiner, false to get it from the widget cached copy.
	 * The widget's cached copy may be different from the one that come from the #parentContainer that is the most updated. Usually the 
	 * widget's cached copy is synchronized with the #parentContainer one when the widget configuration is saved.
	 * 
	 * @return The layout configuration of the widget.
	 */
	, getLayoutConfiguration: function(fromContainer) {
		var wl =  this.wlayout;
		if(fromContainer === false) {
			return wl;
		}
		
		if(this.isBoundToAContainer() === true) {
			wl = wl || {};
			wl.region = this.getWidgetRegion(true) || wl.region;
		}
		return wl;
	}
	
	/**
	 * @method
	 * @deprecated
	 * 
	 * Returns the region used by this widget in the #parentContainer to which it is bound. Its content depends on the particular implementation 
	 * of the container used.
	 * 
	 * @param {boolean} relative true to return all measures expressed in relative units (i.e. % of the #parentContainer dimensions).
	 * 
	 * @returns {Object} The region used by the widget
	 */
    , getWidgetRegion: function(relative) {
    	Sbi.trace("[Widget.getWidgetRegion]: IN");
    	
    	var r = null;
    	
    	if(this.isBoundToAContainer() === true) {
    		Sbi.trace("[Widget.getWidgetRegion]: widget [" + this.getId()+ "] is bound to a container");
    		var parentContainer = this.getParentContainer(); 
    		var parentComponent = this.getParentComponent();
    		r = parentContainer.getComponentRegion( parentComponent, relative );
    		if(r === null) {
    			Sbi.warn("[Widget.getWidgetManager]: Widget [" + this.toString() + "] is bound to a widget container but it is not possible to retrive the region it occupies");
    		}    	
    	} else {
    		Sbi.trace("[Widget.getWidgetRegion]: widget [" + this.getId()+ "] is not bound to a container");
    		if(this.wlayout && this.wlayout.region) {
    			r = this.wlayout.region;
    		}
    	}

    	Sbi.trace("[Widget.getWidgetRegion]: OUT");
    	
    	return r;
    }
	
	/**
	 * @method
	 * 
	 * Returns the #parentContainer that embed the widget. The parent component implementation depends on the container in
	 * which the widget is deployed
	 * 
	 * @return {Sbi.cockpit.core.WidgetContainerComponent} The parent component
	 */
    , getParentComponent: function() {	
		return this.parentComponent;	
	}

    /**
	 * @method
	 * 
	 * Sets the parent component that embed the widget. The parent component implementation depends on the container in
	 * which the widget is deployed
	 * 
	 * @param {Sbi.cockpit.core.WidgetContainerComponent} component The parent component
	 * 
	 */
    , setParentComponent: function(component) {
		this.parentComponent = component;	
		Sbi.trace("[Widget.setParentComponent]: Parent container of widget [" + this.id +  "] is [" + (component?component.id:"null") + "]");
	}
    
    /**
	 * @method
	 * 
	 * Returns the container in which the widget is bounded, null if the widget is not bounded to any container
	 * 
	 * @return {Sbi.cockpit.core.WidgetContainer} The container to which teh widget is bounded
	 */
    , getParentContainer: function() {	
    	Sbi.trace("[Widget.getParentContainer]: IN");
		
    	var container = null;
		
		var component = this.getParentComponent();
			
		if(Sbi.isValorized(component)) {
			//.trace("[Widget.getParentContainer]: widget [" + this.id +  "] is bound to component [" + component.id + "]");
			container = component.getParentContainer();
		} else {
			//Sbi.warn("[Widget.getParentContainer]: widget [" + this.id + "] is not bound to any component");
		}
		
		Sbi.trace("[Widget.getParentContainer]: OUT");
    	
		return container;	
	}
    
    /**
	 * @method
	 * 
	 * Returns true if the widget is bounded to a container, false otherwise.
	 * 
	 * @return {boolean} true if the widget is bounded to a container, false otherwise.
	 */
    , isBoundToAContainer: function() {
    	Sbi.trace("[Widget.isBoundToAContainer]: IN");
    	var isBound = false;
    	var container = this.getParentContainer();
    	if(container != null) {
    		isBound = true;
    		//Sbi.trace("[Widget.getParentContainer]: widget [" + this.id +  "] is bound to container [" + container.id + "]");
    	}
    	Sbi.trace("[Widget.isBoundToAContainer]: OUT");
    	return isBound;
    }
    
    /**
	 * @method
	 * 
	 * Returns the widget manager to which the widget is registered. The widget manager manage the lifecycle
	 * of all widgets bounded to a particular widget container. 
	 * 
	 * @return {Sbi.cockpit.core.WidgetManager} the widget manager to which the widget is registered.
	 */
    , getWidgetManager: function() {
    	Sbi.trace("[Widget.getWidgetManager]: IN");
    	
    	var widgetManager = null;
    	
    	if(this.isBoundToAContainer() === true) {
    		widgetManager = this.getParentContainer().getWidgetManager();
    		if(widgetManager === null) {
    			Sbi.error("[Widget.getWidgetManager]: Widget [" + this.toString() + "] is bound to a widget container but it is not possible to retrive from it a valid widget manager");
    		}
    	} else {
    		Sbi.warn("[Widget.getWidgetManager]: It's not possble to retrieve widget manager of widget [" + this.toString() + "] because it is not bound to any widget container");
    	}
    	
    	Sbi.trace("[Widget.getWidgetManager]: OUT");
    	
    	return widgetManager;
    }
	
	, toString: function() {
		return this.id;
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	, onRender: function(ct, position) {	
		Sbi.cockpit.core.WidgetRuntime.superclass.onRender.call(this, ct, position);	
	}
	
	
	
	// =================================================================================================================
	// EVENTS
	// =================================================================================================================

	// ...
    
	
});