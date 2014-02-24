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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.data");

/**
 * @class Sbi.data.StoreManager
 * @extends Ext.util.Observable
 * 
 * ...
 */

/**
 * @cfg {Object} config The configuration object passed to the constructor
 */
Sbi.data.StoreManager = function(config) {
	Sbi.trace("[StoreManager.constructor]: IN");
	
	var defaultSettings = {
			
	};
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.storeManager) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.storeManager);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
		
	this.init(c.datasetsConfig);
		
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
   
	// =================================================================================================================
	// METHODS
	// =================================================================================================================
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Adds a new store to the ones already managed by this manager.
	 * 
	 * @param {Ext.data.Store} store The store to add.
	 * @param {String} store.storeId The store identifier. For datatset related to a SpagoBI's dataset it is equal to the dataset' label
	 * @param {boolean} store.raedy true if the store has been already loaded, false otherwise. The default is false.
	 * @param {String} store.storeType The type of the store. It can be equal to "ext" if the store is a standrad extjs store "sbi" if
	 * the store is an extension provided by SpagoBI. The default is "ext".
	 * @param {Numeric} store.refreshTime The refresh time of the dataset in seconds. The defaut is 0.
	 */
	, addStore: function(s) {
		Sbi.trace("[StoreManager.addStore]: IN");
		
		if (s.storeId !== undefined){
			s.ready = s.ready || false;
			s.storeType = s.storeType || 'ext';
			//s.filterPlugin = new Sbi.data.StorePlugin({store: s});
			
			this.stores.add(s);
					
			
			if(s.refreshTime) {
				var task = {
					run: function(){
						//if the console is hidden doesn't refresh the datastore
						if(s.stopped) return;
						
						// if store is paging...
						if(s.lastParams) {
							// ...force remote reload
							delete s.lastParams;
						}
						s.load({
							params: s.pagingParams || {}, 
							callback: function(){this.ready = true;}, 
							scope: s, 
							add: false
						});
					},
					interval: s.refreshTime * 1000 //1 second
				};
				Ext.TaskMgr.start(task);
			}
		}
		
		Sbi.trace("[StoreManager.addStore]: OUT");
	}

	, getStore: function(storeId) {
		return this.stores.get(storeId);
	}
	
	, containsStore: function(store) {
		Sbi.trace("[StoreManager.containsStore]: typeof store: " + (typeof store));
		if(typeof store === 'String') {
			return this.stores.containsKey(store);
		} else {
			return this.stores.contains(store);
		}
	}
	
	, removeStore: function(store) {
		Sbi.trace("[StoreManager.removeStore]: typeof store: " + (typeof store));
		if(typeof store === 'String') {
			return this.stores.removeKey(store);
		} else {
			return this.stores.remove(store);
		}
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
	// init methods
	// -----------------------------------------------------------------------------------------------------------------
    
    , init: function(c) {
    	Sbi.trace("[StoreManager.init]: IN");
    	
		c = c || [];
	
		this.stores = new Ext.util.MixedCollection();
		this.stores.getKey = function(o){
            return o.storeId;
        };
		
		for(var i = 0, l = c.length, s; i < l; i++) {
			if (c[i].memoryPagination !== undefined &&  c[i].memoryPagination === false){
				//server pagination	
				s = new Sbi.data.Store({
					storeId: c[i].id
					, datasetLabel: c[i].label
					, autoLoad: false
					, refreshTime: c[i].refreshTime
					, limitSS: this.limitSS
					, memoryPagination: c[i].memoryPagination || false 
				});
			}else{
				//local pagination (default)		
				s = new Sbi.data.MemoryStore({
					storeId: c[i].id
					, datasetLabel: c[i].label
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
			
			this.addStore(s);
		}
	
		// for easy debug purpose
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
		
		this.addStore(testStore);
		
		Sbi.trace("[StoreManager.init]: OUT");
		
	}
    
    
});