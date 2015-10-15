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