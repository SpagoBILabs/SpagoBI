/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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

Ext.ns("Sbi.console");

Sbi.console.StoreManager = function(config) {

		var defaultSettings = {
			
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.storeManager) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.storeManager);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		Ext.apply(this, c);
		
		this.init(c.datasetsConfig);
		
		// constructor
		Sbi.console.StoreManager.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.console.StoreManager, Ext.util.Observable, {
    
	stores: null
   
	//  -- public methods ---------------------------------------------------------
    
	, addStore: function(s) {
		s.ready = s.ready || false;
		s.storeType = s.storeType || 'ext';
		s.filterPlugin = new Sbi.console.StorePlugin({store: s});
		
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
			}
			Ext.TaskMgr.start(task);
		}
	}

	, getStore: function(storeId) {
		return this.stores.get(storeId);
	}
	
	, stopRefresh: function(value){
		for(var i = 0, l = this.stores.length, s; i < l; i++) {
			var s = this.stores.get(i);
			s.stopped = value;
			//alert('set stopped value ' + value + ' to store: ' + s.toSource());
			//alert('set stopped value ' + value);
		}
		 
	}
	
	, forceRefresh: function(){
		for(var i = 0, l = this.stores.length; i < l; i++) {
			var s = this.getStore(i);
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
	
	
	//  -- private methods ---------------------------------------------------------
    
    , init: function(c) {
		c = c || [];
	
		this.stores = new Ext.util.MixedCollection();
		this.stores.getKey = function(o){
            return o.storeId;
        };
		
		for(var i = 0, l = c.length, s; i < l; i++) {
			s = new Sbi.data.Store({
				storeId: c[i].id
				, datasetLabel: c[i].label
				, autoLoad: false
				, refreshTime: c[i].refreshTime
			}); 
		
			s.ready = c[i].ready || false;
			s.storeType = 'sbi';
			
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
		
	}
    
    
});