/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */


Ext.define('Sbi.extjs.chart.data.StoreManager', {
    extend: 'Ext.util.Observable',
    stores: null
    
    , constructor: function(config) {
    	var defaultSettings = {
    			
		};
		
		if(Sbi.settings && Sbi.settings.chart && Sbi.extjs.chart.data && Sbi.extjs.chart.data.storeManager) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.extjs.chart.data.storeManager);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		Ext.apply(this, c);
		
		this.init(c.datasetsConfig);
		
		this.callParent(arguments);
    }

	, addStore: function(s) {
		s.ready = s.ready || false;
		s.storeType = s.storeType || 'ext';
		
		this.stores.add(s);				
		
		if(s.refreshTime) {
			var task = {
				run: function(){
					//if the chart is hidden doesn't refresh the datastore
					if(s.stopped) {return;}
					
					// if store is paging...
					if(s.lastParams) {
						// ...force remote reload
						delete s.lastParams;
					}
					
					s.load({
						params: s.pagingParams || {}, 
						callback: function(){this.ready = true;}, 
						scope: s 						
						//, add: false
					});
				},
				interval: s.refreshTime * 1000 //1 second
			};
			Ext.TaskManager.start(task);
		}
	}
	
	, getStore: function(storeId) {
		return this.stores.get(storeId);
	}
	
	, stopRefresh: function(value){
		for(var i = 0, l = this.stores.length, s; i < l; i++) {
			var s = this.stores.get(i);
			s.stopped = value;
		}
		 
	}
	
	//refresh All stores of the store manager managed
	, forceRefresh: function(){
		for(var i = 0, l = this.stores.length; i < l; i++) {
			var s = this.getStore(i);
			//s.stopped = false; 
			if (s !== undefined && s.dsLabel !== undefined  && !s.stopped){				
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
	        //return o.storeId;
	        return o.dsLabel;
	    };
	  
		for(var i = 0, l = c.length, s; i < l; i++) {
			var loading = false;
			if (!c[i].refreshTime) loading = true;
			s = Ext.create('Sbi.extjs.chart.data.Store',{
				storeId: c[i].id
				, datasetLabel: c[i].label
				, dsTypeCd: c[i].dsTypeCd
				, dsPars: c[i].pars
				, refreshTime: c[i].refreshTime 
				, autoLoad: loading
			}); 
		
			s.ready = c[i].ready || false;
			s.storeType = 'sbi';
		
			this.addStore(s);
		}
	}

});
