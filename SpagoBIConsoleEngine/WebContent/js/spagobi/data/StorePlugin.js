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
  * - name (mail)
  */

Ext.ns("Sbi.console");

Sbi.console.StorePlugin = function(config) {
	
		var defaultSettings = {
			filters: {}
		};
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.storePlugin) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.storePlugin);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		
		Ext.apply(this, c);
		
	
		// constructor
		Sbi.console.StorePlugin.superclass.constructor.call(this, c);
    
		this.store.filterPlugin = this;
		this.store.addEvents('filterschange');
		
};

Ext.extend(Sbi.console.StorePlugin, Ext.util.Observable, {
    
    store: null
    , filters: null
   
    // public methods
    
    , removeFilter: function(fieldName) {
		delete this.filters[fieldName];
	}

    , addFilter: function(fieldName, filter) {
		this.filters[fieldName] = filter;
	}

	, getFilter: function(fieldName) {
		return this.filters[fieldName];
	}

	, resetFilters: function() {
		filters = {};
	}
	
   //filters functions
   , applyFilters: function() {
	   //apply the ordering if it's presents
       if (this.store.getSortState() !== undefined){
      		this.store.sort(this.store.getSortState().field, this.store.getSortState().direction);
       }
      	
	   //apply the filters
	   this.store.filterBy(function(record,id){		
		   for(var f in this.filters){ 
			   if(record.data[f] != this.filters[f]) return false;              
	       }
	       return true;
	   }, this);
	   
	   this.fireEvent('filterschange', this.store, this.filtrs);
	       	    
   }
   
	// just for test: remove asap
	, getFilters: function(fieldName) {
		return this.filters;
	}
    
    // private methods
    
});