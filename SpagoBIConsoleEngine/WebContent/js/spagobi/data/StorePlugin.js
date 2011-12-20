/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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

       /*
        * Apply the filters.
        * The filters object is a multidimensional array (ie: filters=[<col1>:[<val1>, <val2>],
        *							   					              [<col2>:[<val3>, <val4>]])
        * In the application of filters each filter column works in AND condition with filters of others columns, 
        * while works in OR condition with all its values. 
        * (In the example the filter of col1 is in AND with the filter on col2; BUT to satisfy the col1 is necessary 
        *  at least one between val1 and val2.)
        * So, this method uses three flags to manage this situation:
        *  - isVisible: defines if the record matches the current condition
        *  - isVisibilePrec: defines the visibility of the previous condition
        *  - isVisibileRet: defines the real value returned
        */
      
	   this.store.filterBy(function(record,id){		
		   var isVisible = false; //flag for single condition (multivalue)
		   var isVisiblePrec = true;
		   var isVisibleRet = true;
		   for(var f in this.filters){ 	// cycles on all filters
			   var tmpValues = this.filters[f];
			   for(var val in tmpValues){  		//cycles on the single value for each condition (logical OR case)
				   if (tmpValues[val] !== undefined){
					   if(record.data[f] === tmpValues[val]) {						
						   //return true;  						   
						   isVisible = true;
					   }	
				   }
			   }
			   
			   isVisibleRet = (isVisible && isVisiblePrec);
			   isVisiblePrec =  isVisible;
			   isVisible = false; //reset value
		   }
		   //return false;				   
		   return isVisibleRet;
	   }, this);
	   
	   this.fireEvent('filterschange', this.store, this.filters);
	       	    
   }
   
	// just for test: remove asap
	, getFilters: function(fieldName) {
		return this.filters;
	}
    
    // private methods
    
});