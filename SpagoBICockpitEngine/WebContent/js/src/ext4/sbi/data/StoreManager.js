/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.data");

/**
 * @class Sbi.data.StoreManager
 * @extends Ext.util.Observable
 * 
 * This class manages a group of stores shared by different component. It can be instantiated
 * at any level of the application classes hierarchy depending of the components that need to use it.
 * For example it can be instantiated within a panel whose child need to share different stores. 
 */

/**
 * @cfg {Object} config The configuration object passed to the constructor
 */
Sbi.data.StoreManager = function(config) {
	Sbi.trace("[StoreManager.constructor]: IN");
	
	// init properties...
	var defaultSettings = {
		autoDestroy: true
	};
	
	var settings = Sbi.getObjectSettings('Sbi.data.StoreManager', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
		
	this.setConfiguration(c);
		
	// constructor
	Sbi.data.StoreManager.superclass.constructor.call(this, c);
	
	Sbi.trace("[StoreManager.constructor]: OUT");
};

Ext.extend(Sbi.data.StoreManager, Ext.util.Observable, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================	
	/**
     * @property {Ext.util.MixedCollection()} stores
     * The list of registered stores managed by this manager
     */
	stores: null
	
	/**
     * @property {Ext.util.MixedCollection()} associations
     * The list of registered associations between datasets managed by this manager
     */
	, associations: null
	
	/**
     * @property {Ext.util.MixedCollection()} filters
     * The list of registered filters managed by this manager
     */
	, filters: null
	
   
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // configuration methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method
	 * Sets the configuration of this manage
	 * 
	 * @param {Object} conf The configuration object
	 */
	, setConfiguration: function(conf) {
		Sbi.trace("[StoreManager.init]: IN");
		
		conf = conf || {};
		
		var stores = conf.stores || [];
		this.setStoreConfigurations(stores);
		
		var associations = conf.associations || [];
		this.setAssociationConfigurations(associations);

		var filters = conf.filters || [];
		this.setFilterConfigurations(filters);

		Sbi.trace("[StoreManager.init]: OUT");
	}

	/**
	 * Removes all stores registered to this manager.  
	 * 
	 * @param {Boolean} autoDestroy (optional) True to automatically also destry the each store after removal.
	 * Defaults to the value of this Manager's {@link #autoDestroy} config.
	 */
	, resetConfiguration: function(autoDestroy) {
		Sbi.trace("[StoreManager.resetConfiguration]: IN");
				
		this.resetStoreConfigurations(autoDestroy);
		this.resetAssociationConfigurations(autoDestroy);
	    
	    Sbi.trace("[StoreManager.resetConfiguration]: OUT");
	}
	
	/**
	 * @method
	 * Gets the store configuration object. This object can be passed to #setConfiguration method
	 * at any time to roll back to the current configuration. It can also be passed to the constructor 
	 * of this class to create a clone of this instance of store manager.
	 * 
	 * <b>WARNING: </b> what stated above is true only for store whose storeType is equal to "sbi". In other words
	 * it s true only for stores created using the method #createStore. Other stores managed by this manager are not included 
	 * in the configuration object and so will be lost. This is due to the fact that the method #getStoreConfiguration is not able 
	 * to extract configuration for a general Ext.data.Store object.
	 * 
	 * @return {Object} The configuration object
	 */
	, getConfiguration: function() {
		Sbi.trace("[StoreManager.getConfiguration]: IN");
		var config = {};
		config.stores = this.getStoreConfigurations();
		config.associations = this.getAssociationConfigurations();
		config.filters = this.getFilterConfigurations();
		Sbi.trace("[StoreManager.getConfiguration]: OUT");
		return config;
	}

	//STORE CONFIGS
	
	/**
	 * @method
	 * Sets stores' configuration
	 * 
	 * @param {Object[]} conf The configuration object
	 */
	, setStoreConfigurations: function(conf){
		Sbi.trace("[StoreManager.setStoreConfigurations]: IN");
		this.resetStoreConfigurations();
		Sbi.trace("[StoreManager.setStoreConfigurations]: Input parameter [conf] is equal to [" + Sbi.toSource(conf) + "]");
		conf = conf || [];
		for(var i = 0; i < conf.length; i++) {
			var store = this.createStore(conf[i]);
			this.addStore(store);
		}
		
		// for easy debug purpose
		//	var testStore = this.createTestStore();
		//	Sbi.trace("[StoreManager.init]: adding test store whose type is equal to [" + testStore.storeType + "]");
		//	this.addStore(testStore);
		Sbi.trace("[StoreManager.setStoreConfigurations]: OUT");
	}
	
	, resetStoreConfigurations: function(autoDestroy) {
		if(Sbi.isValorized(this.stores)) {
			Sbi.trace("[StoreManager.resetConfiguration]: There are [" + this.stores.getCount() + "] store(s) to remove");
			autoDestroy = autoDestroy || this.autoDestroy;
			this.stores.each(function(store, index, length) {
				this.removeStore(store, autoDestroy);
			}, this);
		}
		
		this.stores = new Ext.util.MixedCollection();
		this.stores.getKey = function(o){
	        return o.storeId;
	    };
	}
	
	/**
	 * @method
	 * Gets the configuration of all stores whose type is equal to "sbi" managed by this store manager.
	 * 
	 * @return {Object[]} The stores' configuration 
	 */
	, getStoreConfigurations: function() {
		var confs = [];
		this.stores.each(function(store, index, length) {
			var c = this.getStoreConfiguration(store.storeId);
			if(Sbi.isValorized(c)) {
				confs.push(c);
			}
		}, this);
		return confs;
	}
	
	/**
	 * @method
	 * Gets the configuration of the store whose id is equal to #storeId if it is managed by this
	 * manager and its type is equal to "sbu", null otherwise.
	 * 
	 * @param {String} storeId the store id
	 * 
	 * @return {Object[]} The store's configuration 
	 */
	, getStoreConfiguration: function(storeId) {
		
		Sbi.trace("[StoreManager.getStoreConfiguration]: IN");
		
		var store = this.getStore(storeId);
		var storeConf = null;
		
		if(Sbi.isValorized(store)) {
			if(store.storeType === "sbi") {
				Sbi.trace("[StoreManager.getStoreConfiguration]: conf of store [" + storeId + "] of type [" + store.storeType + "] " +
						"is equal to [" + Sbi.toSource(store.storeConf, true)+ "]");
				
				storeConf = Ext.apply({}, store.storeConf);
			} else {
				Sbi.warn("[StoreManager.getStoreConfiguration]: impossible to extract configuration from store of type different from [sbi]");
			}
		} else {
			Sbi.warn("[StoreManager.getStoreConfiguration]: impossible to find store [" + storeId + "]");
		}
		
		Sbi.trace("[StoreManager.getStoreConfiguration]: OUT");
		
		return storeConf;
	}
	
	// ASSOCIATION CONFIGS
	
	/**
	 * @method
	 * Sets associations configuration
	 * 
	 * @param {Object[]} conf The configuration object
	 */
	, setAssociationConfigurations: function(conf) {
		Sbi.trace("[StoreManager.setAssociationConfigurations]: IN");
		this.resetAssociationConfigurations();
		Sbi.debug("[StoreManager.setAssociationConfigurations]: parameter [conf] is equal to [" + Sbi.toSource(conf)+ "]");
		conf = conf || [];
		for(var i = 0; i < conf.length; i++) {
			this.addAssociation(conf[i]);
		}
		this.refreshAssociationGroups();
		Sbi.trace("[StoreManager.setAssociationConfigurations]: OUT");
	}
	
	, resetAssociationConfigurations: function(autoDestroy) {
		if(Sbi.isValorized(this.associations)) {
			Sbi.trace("[StoreManager.resetAssociationConfigurations]: There are [" + this.associations.getCount() + "] association(s) to remove");
			autoDestroy = autoDestroy || this.autoDestroy;
			this.associations.each(function(association, index, length) {
				this.removeAssociation(association, autoDestroy);
			}, this);
		}
		
		this.associations = new Ext.util.MixedCollection();
		this.associations.getKey = function(o){
	        return o.id;
	    };
	}
	
	/**
	 * @method
	 * Gets the configuration of all associations defined in this store manager.
	 * 
	 * @return {Object[]} The associations' configuration 
	 */
	, getAssociationConfigurations: function() {
		Sbi.trace("[StoreManager.getAssociationConfigurations]: IN");
		var confs = [];
		this.associations.each(function(association, index, length) {
			var c = this.getAssociationConfiguration(association.id);
			if(Sbi.isValorized(c)) {
				confs.push(c);
			}
		}, this);
		Sbi.trace("[StoreManager.getAssociationConfigurations]: OUT");
		return confs;
	}
	
	/**
	 * @method
	 * Gets the configuration of the association whose id is equal to #associationId if it is defined in this
	 * manager, null otherwise.
	 * 
	 * @param {String} associationId the association id
	 * 
	 * @return {Object[]} The association's configuration 
	 */
	, getAssociationConfiguration: function(associationId) {
		Sbi.trace("[StoreManager.getAssociationConfiguration]: IN");
		
		var association = this.getAssociation(associationId);
		var associationConf = null;
		
		if(Sbi.isValorized(association)) {
			associationConf = Ext.apply({}, association);
			Sbi.trace("[StoreManager.getAssociationConfiguration]: conf of store [" + associationId + "] is equal to [" + Sbi.toSource(associationConf, true)+ "]");
		} else {
			Sbi.warn("[StoreManager.getAssociationConfiguration]: impossible to find association [" + associationId + "]");
		}
		
		Sbi.trace("[StoreManager.getAssociationConfiguration]: OUT");
		
		return associationConf;	
	}
	, refreshAssociationGroups: function() {
		// mask container (non direttamente ma tramite lancio di evento onRefresh group)
		// call refresh service on server
		// implement a callback that 
		//	- save the response of the server 
		//	- unmask container (non direttamente ma tramite lancio di evento afterRefresh group)
		
		Ext.Ajax.request({
		    //url: 'https://localhost:1447/SpagoBICockpitEngine/api/1.0/associations/json',
			url: Sbi.config.serviceReg.getServiceUrl('setAssociations'),
		    method: 'POST',
		    params: {
		        requestParam: 'notInRequestBody'
		    },
		    jsonData: Ext.JSON.encode(this.associations),
		    success: function() {
		        alert('success');
		    },
		    failure: function() {
		    	alert('woops');
		    }
		});
	}

	// FILTERS CONFIGS
	
	/**
	 * @method
	 * Sets filters configuration
	 * 
	 * @param {Object[]} conf The configuration object
	 */
	, setFilterConfigurations: function(conf) {
		Sbi.trace("[StoreManager.setFilterConfigurations]: IN");
		this.resetFilterConfigurations();
		Sbi.debug("[StoreManager.setFilterConfigurations]: parameter [conf] is equal to [" + Sbi.toSource(conf)+ "]");
		conf = conf || [];
		for(var i = 0; i < conf.length; i++) {
			this.addFilter(conf[i]);
		}
		Sbi.trace("[StoreManager.setFilterConfigurations]: OUT");
	}
	
	, resetFilterConfigurations: function(autoDestroy) {
		if(Sbi.isValorized(this.filters)) {
			Sbi.trace("[StoreManager.resetFilterConfigurations]: There are [" + this.filters.getCount() + "] filter(s) to remove");
			autoDestroy = autoDestroy || this.autoDestroy;
			this.filters.each(function(filter, index, length) {
				this.removeFilter(filter, autoDestroy);
			}, this);
		}
		
		this.filters = new Ext.util.MixedCollection();
		this.filters.getKey = function(o){
	        return o.id;
	    };
	}
	
	/**
	 * @method
	 * Gets the configuration of all filters defined in this store manager.
	 * 
	 * @return {Object[]} The filters' configuration 
	 */
	, getFilterConfigurations: function() {
		Sbi.trace("[StoreManager.getFilterConfigurations]: IN");
		var confs = [];
		this.filters.each(function(filter, index, length) {
			var c = this.getFilterConfiguration(filter.id);
			if(Sbi.isValorized(c)) {
				confs.push(c);
			}
		}, this);
		Sbi.trace("[StoreManager.getFilterConfigurations]: OUT");
		return confs;
	}
	
	/**
	 * @method
	 * Gets the configuration of the filter whose id is equal to #associationId if it is defined in this
	 * manager, null otherwise.
	 * 
	 * @param {String} filterId the filter id
	 * 
	 * @return {Object[]} The filter's configuration 
	 */
	, getFilterConfiguration: function(filterId) {
		Sbi.trace("[StoreManager.getFilterConfiguration]: IN");
		
		var filter = this.getFilter(filterId);
		var filterConf = null;
		
		if(Sbi.isValorized(filter)) {
			filterConf = Ext.apply({}, filter);
			Sbi.trace("[StoreManager.getFilterConfiguration]: conf of filter [" + filterId + "] is equal to [" + Sbi.toSource(filterConf, true)+ "]");
		} else {
			Sbi.warn("[StoreManager.getFilterConfiguration]: impossible to find filter [" + filterId + "]");
		}
		
		Sbi.trace("[StoreManager.getFilterConfiguration]: OUT");
		
		return filterConf;	
	}

	
	// -----------------------------------------------------------------------------------------------------------------
    // store methods
	// -----------------------------------------------------------------------------------------------------------------
	/**
	 * @method 
	 * 
	 * Adds a new store to the ones already managed by this manager.
	 * 
	 * @param {Ext.data.Store} store The store to add.
	 * @param {String} store.storeId The store identifier. For store related to a SpagoBI's dataset it is equal to the dataset' label
	 * @param {boolean} store.raedy true if the store has been already loaded, false otherwise. The default is false.
	 * @param {String} store.storeType The type of the store. It can be equal to "ext" if the store is a standrad extjs store "sbi" if
	 * the store is an extension provided by SpagoBI. The default is "ext".
	 * @param {Numeric} store.refreshTime The refresh time of the store in seconds. The default is 0.
	 */
	, addStore: function(store) {
		Sbi.trace("[StoreManager.addStore]: IN");
		
		if(Sbi.isNotValorized(store)) {
			Sbi.warn("[StoreManager.addStore]: Input parameter [s] is not defined");
			Sbi.trace("[StoreManager.addStore]: OUT");
		}
		
		if(Ext.isArray(store)) {
			Sbi.trace("[StoreManager.addStore]: Input parameter [s] is of type [Array]");
			for(var i = 0; i < store.length; i++) {
				this.addStore(store[i]);
			}
		} else if(Ext.isString(store)) {
			Sbi.trace("[StoreManager.addStore]: Input parameter [s] is of type [String]");	
			this.addStore({storeId: store});
		} else if(Sbi.isNotExtObject(store)) {
			Sbi.trace("[StoreManager.addStore]: Input parameter [s] is of type [Object]");	
			store = this.createStore(store);
		} else if((store instanceof Ext.data.Store) === true) {
			Sbi.trace("[StoreManager.addStore]: Input parameter [s] is of type [Store]");
			// nothing to do here
		} else {
			Sbi.error("[StoreManager.addStore]: Input parameter [s] of type [" + (typeof store) + "] is not valid");	
		}
		
		
		if (store.storeId !== undefined){ //TODO this is valid only for store of type sbi. Generalize!
			
			Sbi.trace("[StoreManager.addStore]: Adding store [" + store.storeId + "] of type [" + store.storeType + "] to manager");
			store.ready = store.ready || false;
			store.storeType = store.storeType || 'ext';
			//s.filterPlugin = new Sbi.data.StorePlugin({store: s});
			
			this.stores.add(store);
					
			if(store.refreshTime) {
				var task = {
					run: function(){
						//if the console is hidden doesn't refresh the datastore
						if(store.stopped) return;
						
						// if store is paging...
						if(store.lastParams) {
							// ...force remote reload
							delete store.lastParams;
						}
						store.load({
							params: store.pagingParams || {}, 
							callback: function(){this.ready = true;}, 
							scope: store, 
							add: false
						});
					},
					interval: store.refreshTime * 1000 //1 second
				};
				Ext.TaskMgr.start(task);
			}
		}
		
		Sbi.trace("[StoreManager.addStore]: OUT");
	}
	
	/**
	 * @methods
	 * 
	 * Returns all the stores managed by this store manager
	 * 
	 *  @return {Ext.data.Store[]} The stores list
	 */
	, getStores: function() {
		return this.stores.getRange();
	}
	
	, getStoresCount: function() {
		return this.stores.getCount();
	}
	
	, getStore: function(storeId) {
		return this.stores.get(storeId);
	}
	
	, containsStore: function(store) {
		if(Ext.isString(store)) {
			return this.stores.containsKey(store);
		} else {
			return this.stores.contains(store);
		}
	}
	
	/**
	 * @method
	 * 
	 * @param {Ext.data.Store/String} store The store to rmove or its id.
	 * @param {Boolean} autoDestroy (optional) True to automatically also destroy the store after removal.
	 * Defaults to the value of this Manager's {@link #autoDestroy} config.
	 * 
	 * @return {Ext.data.Store} the store removed. False if it was impossible to remove the store. null if the store after removal
	 * has been destroyed (see autoDestroy parameter).
	 */
	, removeStore: function(store, autoDestroy) {
		
		Sbi.trace("[StoreManager.removeStore]: IN");
		
		if(Sbi.isNotValorized(store)) {
			Sbi.trace("[StoreManager.removeStore]: Parameter [store] is not valorized");
			Sbi.trace("[StoreManager.removeStore]: OUT");
			return false;
		}
		
		var storeId = null;
		
		Sbi.trace("[StoreManager.removeStore]: typeof store: " + (typeof store));
		
		if(Ext.isString(store)) {
			storeId = store;
			store = this.stores.removeAtKey(store);
		} else {
			storeId = store.id;
			store = this.stores.remove(store);
		}
		
		if(store === false) {
			Sbi.trace("[StoreManager.removeStore]: Impossible to remove store [" + storeId  + "]");
		}
		
		autoDestroy = autoDestroy || this.autoDestroy;
		if(autoDestroy) {
			store.destroy();
			store = null;
		}
		
		Sbi.trace("[StoreManager.removeStore]: OUT");
		
		return store;
	}
	
	,loadStore: function(store, params){
		store.load({params:params});
	}
	
	, getStoreIds: function() {
		var ids = [];
		this.stores.each(function(store, index, length) {
			ids.push(store.storeId);
		}, this);
		return ids;
	}
	
	/*
	 * storeId is optional: in case it is not specified, all stores are stopped
	 */
	, stopRefresh: function(value, storeId){
		if (storeId) { // if a storeId is defined, stopRefresh only on it
			var s = this.stores.get(storeId);
			s.stopped = value;
		} else { // if a storeId is NOT defined, stopRefresh on ALL stores
			for(var i = 0, l = this.stores.length, s; i < l; i++) {
				var s = this.stores.get(i);		
				if (s.dsLabel !== undefined){
					s.stopped = value;					
				}
			}
		}
	}
	
	//refresh All stores of the store manager managed
	, forceRefresh: function(){		
		for(var i = 0, l = this.stores.length; i < l; i++) {
			var s = this.getStore(i);			
			//s.stopped = false; 
			if (s !== undefined && s.dsLabel !== undefined && s.dsLabel !== 'testStore' && !s.stopped){					
				s.load({
					params: s.pagingParams || {},
					callback: function(){this.ready = true;}, 
					scope: s, 
					add: false
				});
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // association methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, addAssociation: function(association){
		Sbi.trace("[StoreManager.addAssociation]: IN");
		
		if(Sbi.isNotValorized(association)) {
			Sbi.warn("[StoreManager.addAssociation]: Input parameter [association] is not defined");
			Sbi.trace("[StoreManager.addAssociation]: OUT");
		}
		
		if(Ext.isArray(association)) {
			Sbi.trace("[StoreManager.addAssociation]: Input parameter [association] is of type [Array]");
			for(var i = 0; i < store.length; i++) {
				this.addAssociation(association[i]);
			}
		} else if(Sbi.isNotExtObject(association)) {
			Sbi.trace("[StoreManager.addAssociation]: Input parameter [association] is of type [Object]");	
			this.associations.add(association);
			alert(Sbi.toSource(association));
			Sbi.debug("[StoreManager.addAssociation]: Association [" + Sbi.toSource(association) + "] succesfully added");
		} else {
			Sbi.error("[StoreManager.addStore]: Input parameter [association] of type [" + (typeof store) + "] is not valid");	
		}
		
		Sbi.trace("[StoreManager.addAssociation]: OUT");
	}
	
	/**
	 * @methods
	 * 
	 * Returns all the associations defined in this store manager
	 * 
	 *  @return {Object[]} The associations list
	 */
	, getAssociations: function() {
		return this.associations.getRange();
	}
	
	, getAssociation: function(associationId) {
		return this.associations.get(associationId);
	}
	
	, containsAssociation: function(association) {
		if(Ext.isString(association)) {
			return this.associations.containsKey(association);
		} else {
			return this.associations.contains(association);
		}
	}	
	
	
	, getStoresByAssociation: function(a){
		for(var i=0; i<this.getAssociations().length; i++){
			var obj = this.getAssociations()[i];
			if (obj.description == a)	
				return obj.stores;
		}
		
		return null;
	}
	
	
	, getAssociationsByStore: function(s){
		var assList = [];
		
		for(var i=0; i<this.getAssociations().length; i++){
			var obj = this.getAssociations()[i];
			if (obj.stores !== null && obj.stores !== undefined ){
				for(var i=0; i<obj.stores.length; i++){
					if (obj.stores[i] == s)
						assList.push(obj);
				}
			}				
		}
		
		return assList;
	}
	
	, removeAssociation: function(association, autoDestroy) {
		
		Sbi.trace("[StoreManager.removeAssociation]: IN");
		
		if(Sbi.isNotValorized(association)) {
			Sbi.trace("[StoreManager.removeAssociation]: Parameter [association] is not valorized");
			Sbi.trace("[StoreManager.removeAssociation]: OUT");
			return false;
		}
		
		association = this.stores.removeKey(association);
		
		if(association === false) {
			Sbi.trace("[StoreManager.removeAssociation]: Impossible to remove association [" + Sbi.toSource(association)  + "]");
		}
		
		Sbi.trace("[StoreManager.removeAssociation]: OUT");
		
		return association;
	}

	// -----------------------------------------------------------------------------------------------------------------
    // filter methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, addFilter: function(filter){
		Sbi.trace("[StoreManager.addFilter]: IN");
		
		if(Sbi.isNotValorized(filter)) {
			Sbi.warn("[StoreManager.addFilter]: Input parameter [filter] is not defined");
			Sbi.trace("[StoreManager.addFilter]: OUT");
		}
		
		if(Ext.isArray(filter)) {
			Sbi.trace("[StoreManager.addFilter]: Input parameter [filter] is of type [Array]");
			for(var i = 0; i < store.length; i++) {
				this.addFilter(filter[i]);
			}
		} else if(Sbi.isNotExtObject(filter)) {
			Sbi.trace("[StoreManager.addFilter]: Input parameter [filter] is of type [Object]");	
			this.filters.add(filter);
			Sbi.debug("[StoreManager.addFilter]: Association [" + Sbi.toSource(filter) + "] succesfully added");
		} else {
			Sbi.error("[StoreManager.addStore]: Input parameter [filter] of type [" + (typeof filter) + "] is not valid");	
		}
		
		Sbi.trace("[StoreManager.addFilter]: OUT");
	}
	
	/**
	 * @methods
	 * 
	 * Returns all the filters defined in this store manager
	 * 
	 *  @return {Object[]} The filters list
	 */
	, getFilters: function() {
		return this.filters.getRange();
	}
	
	, getFilter: function(filterId) {
		return this.filters.get(filterId);
	}
	
	, containsFilters: function(filter) {
		if(Ext.isString(filter)) {
			return this.filters.containsKey(filter);
		} else {
			return this.filters.contains(filter);
		}
	}	
	
	
	, removeFilter: function(filter, autoDestroy) {
		
		Sbi.trace("[StoreManager.removeFilter]: IN");
		
		if(Sbi.isNotValorized(filter)) {
			Sbi.trace("[StoreManager.removeFilter]: Parameter [filter] is not valorized");
			Sbi.trace("[StoreManager.removeFilter]: OUT");
			return false;
		}
		
		filter = this.filters.removeKey(filter);
		
		if(filter === false) {
			Sbi.trace("[StoreManager.removeFilter]: Impossible to remove filter [" + Sbi.toSource(filter)  + "]");
		}
		
		Sbi.trace("[StoreManager.removeFilter]: OUT");
		
		return filter;
	}

	// -----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------

    
    , createStore: function(storeConf) {
    	
    	Sbi.trace("[StoreManager.createStore]: store [" + storeConf.storeId + "] conf is equal to [" + Sbi.toSource(storeConf, true)+ "]");
    	
    	var proxy = new Ext.data.HttpProxy({
			url: Sbi.config.serviceReg.getServiceUrl('loadDataSetStore', {
				pathParams: {datasetLabel: storeConf.storeId}
			})
			, method: 'GET'
	    	//, timeout : this.timeout
	    	, failure: this.onStoreLoadException
	    });
		
		Ext.define(storeConf.storeId, {
		     extend: 'Ext.data.Model',
		     fields: [
		         {name: 'data', type: 'string'}
		     ]
		});
		
		var store = new Ext.data.Store({
			storeId: storeConf.storeId,
			storeType: 'sbi',
			storeConf: storeConf,
			model: storeConf.storeId,
	        proxy: proxy,
	        reader: new Ext.data.JsonReader(),
	        remoteSort: true
	    });
		
		return store;
	}
    
    , onStoreLoadException: function(response, options) {
    	Sbi.trace("[TableWidget.onStoreLoadException]: IN");	
		Sbi.exception.ExceptionHandler.handleFailure(response, options);
		Sbi.trace("[TableWidget.onStoreLoadException]: OUT");	
	}
    
    , createStoreOld: function(c) {
    	var s = null;
    	if (c[i].memoryPagination !== undefined &&  c[i].memoryPagination === false){
			//server pagination	
			s = new Sbi.data.Store({
				storeId: c[i].storeId
				, autoLoad: false
				, refreshTime: c[i].refreshTime
				, limitSS: this.limitSS
				, memoryPagination: c[i].memoryPagination || false 
			});
		} else {
			//local pagination (default)		
			s = new Sbi.data.MemoryStore({
				storeId: c[i].storeId
				, autoLoad: false
				, refreshTime: c[i].refreshTime
				, rowsLimit:  c[i].rowsLimit || this.rowsLimit
				, memoryPagination: c[i].memoryPagination || true	//default pagination type is client side
			});
		}
		
    	s.ready = c[i].ready || false;
		s.storeType = 'sbi';
		
		//to optimize the execution time, the store is created with the stopped property to false, so it's loaded
		//when the component (widget or grid) is viewed. 
		s.stopped = true;
		
		return s;
    }
    
    , createTestStore: function(c) {
    	var testStore = new Ext.data.JsonStore({
			id: 'testStore'
			, fields:['name', 'visits', 'views']
	        , data: [
	            {name:'Jul 07', visits: 245000, views: 3000000},
	            {name:'Aug 07', visits: 240000, views: 3500000},
	            {name:'Sep 07', visits: 355000, views: 4000000},
	            {name:'Oct 07', visits: 375000, views: 4200000},
	            {name:'Nov 07', visits: 490000, views: 4500000},
	            {name:'Dec 07', visits: 495000, views: 5800000},
	            {name:'Jan 08', visits: 520000, views: 6000000},
	            {name:'Feb 08', visits: 620000, views: 7500000}
	        ]
	    });
		
		testStore.ready = true;
		testStore.storeType = 'ext';
		return testStore;
    }
});
	
	
	