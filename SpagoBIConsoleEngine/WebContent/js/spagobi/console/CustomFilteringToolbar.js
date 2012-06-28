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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.CustomFilteringToolbar = function(config) {

		var defaultSettings = {
		    showIfEmpty: true
		    //, emptyMsg: 'No filters'
		    , tbInizialzed: false
		};
		
		
		
		if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.customFilteringToolbar) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.customFilteringToolbar);
		}
		
		var c = Ext.apply(defaultSettings, config || {});
		Ext.apply(this, c);
		
		if(this.showIfEmpty === true) {
			c = Ext.apply(c, {
				items: [{
					xtype: 'tbtext',
		            text: this.emptyMsg
				}]
			});
		}
		
		// constructor
		Sbi.console.CustomFilteringToolbar.superclass.constructor.call(this, c);
		
		//adds events		
		if (this.store !== undefined){
			this.store.on('metachange', this.onMetaChange, this);
		}
};

Ext.extend(Sbi.console.CustomFilteringToolbar, Sbi.console.FilteringToolbar, {  
    
	services: null
   // , customFilterBar: null
    , tbInizialzed: null
    
    // automatic: all dataset fields are added as filter
    , AUTOMATIC_FILTERBAR: 'automatic'
    	//custom: only configurated fields are added as filter  
    , CUSTOM_FILTERBAR: 'custom'
    
    // -- public methods ---------------------------------------------------------------
      
    
    
    // -- private methods ---------------------------------------------------------------
   
    , onRender : function(ct, position) {
		Sbi.console.CustomFilteringToolbar.superclass.onRender.call(this, ct, position);
    }
    
    , onMetaChange: function( store, meta ) {
    	var i;
    	if(this.tbInizialzed === false) {
	    	if (this.filterBar.type ===  this.AUTOMATIC_FILTERBAR){
	    	   for(i = 0; i < meta.fields.length; i++) { 		  
	    		   if (meta.fields[i].header && meta.fields[i].header !== ''){   
	    			   this.createFilterField(this.filterBar.defaults.operator,  meta.fields[i].header, store.getFieldNameByAlias(meta.fields[i].header));
	    		   }
	    	   } 
	    		this.store.on('load', this.reloadFilterStores, this);
	      	} else if(this.filterBar.type === this.CUSTOM_FILTERBAR){
	        	for(i = 0; i < meta.fields.length; i++) { 		           
	        		 if (meta.fields[i].header &&  meta.fields[i].header !== '' && this.isConfiguratedFilter(meta.fields[i].header)){         		     	
	                  this.createFilterField(this.getFilterOperator(meta.fields[i].header), this.getColumnText(meta.fields[i].header),  store.getFieldNameByAlias(meta.fields[i].header));  	                  
	            	}        		  
	        	} 
	        	this.store.on('load', this.reloadFilterStores, this);
	      	} else {
	      		Sbi.Msg.showError('Toolbar type [' + this.filterBar.type + '] is not supported');
	      	}	

			this.addActionButtons();    					
			
	       	this.doLayout();
       	
	       	this.tbInizialzed = true;
    	}
    }
	
    //returns true if the input field is a filter defined into template, false otherwise.
    , isConfiguratedFilter: function (field){   
          if (this.filterBar.filters){    
            for(var i=0, l=this.filterBar.filters.length; i<l; i++) {              
              if (field === this.filterBar.filters[i].column)
                return true;
        		}
        	}
          return false;
    }
    
    , getColumnText: function (columnName){  
        if (this.filterBar.filters){       
          for(var i=0, l=this.filterBar.filters.length; i<l; i++) {              
            if (columnName === this.filterBar.filters[i].column)
              return this.filterBar.filters[i].text;
      		}
      	}
        return columnName;
    }
   
    , getFilterOperator: function (columnName){         
      	for(var i=0, l=this.filterBar.filters.length; i<l; i++) {              
            if (columnName === this.filterBar.filters[i].column)
              return this.filterBar.filters[i].operator;
  		}
      	return null;
    }
    
});