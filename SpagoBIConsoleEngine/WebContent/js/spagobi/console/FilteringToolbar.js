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
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.FilteringToolbar = function(config) {

	var defaultSettings = {
	    autoWidth: true
	  , width:'100%'
	  , filters: {}
	};
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.filteringToolbar) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.filteringToolbar);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
	
	// constructor
	Sbi.console.FilteringToolbar.superclass.constructor.call(this, c);
    	
	this.addEvents('beforefilterselect');
};

Ext.extend(Sbi.console.FilteringToolbar, Ext.Toolbar, {
    
	services: null
	, store: null
	, cbStores: null
	, filters: null

	
	// -- public methods -----------------------------------------------------------------
    
    
	// -- private methods ---------------------------------------------------------------
	, onRender : function(ct, position) { 
		Sbi.console.FilteringToolbar.superclass.onRender.call(this, ct, position);		     
	}

	//adds action buttons
	, addActionButtons: function(){
  	    var b;
  	    var conf = {}; 
        conf.executionContext = this.filterBar.executionContext;     
        conf.store = this.store;
		this.addFill();
		if (this.filterBar.actions){
      		for(var i=0; i < this.filterBar.actions.length; i++){
      		   conf.actionConf = this.filterBar.actions[i];
      		  // if (conf.actionConf.name === 'errors' || conf.actionConf.name === 'alarms' || conf.actionConf.name === 'errors')
      		   
    		   b = new Sbi.console.ActionButton(conf);
        	   this.addButton(b);	
        	}	
        }
	}
	
	 //defines fields depending from operator type
	 , createFilterField: function(operator, header, dataIndex){
		   if (operator === 'EQUALS_TO') {
			   this.cbStores = this.cbStores || {}; 
			   var s = new Ext.data.JsonStore({
				   fields:['name', 'value', 'description'],
		           data: []
			   });
			   this.cbStores[dataIndex] = s;
			 
			   //this.store.on('load', this.reloadComboStore.createDelegate(this, [dataIndex]), this);
		     
			   var combDefaultConfig = {
					   width: 130,
				       displayField:'name',
				       valueField:'value',
				       typeAhead: true,
				       triggerAction: 'all',
				       emptyText:'...',
				       selectOnFocus:true,
				       mode: 'local'
			   };
			   
			   
			   var cb = new Ext.form.ComboBox(
			       Ext.apply(combDefaultConfig, {
			    	   store: s,
				       index: dataIndex,
				       listeners: {
						   'select': {
						   		fn: function(combo, record, index) {
									var field = combo.index;
									var exp = record.get(combo.valueField);									
									this.onFilterSelect(field, exp);	  
								},
								scope: this
							}				     					
					   }
			       	})
			   );	

			   this.addText("    " + header + "  ");
			   this.addField(cb);	 
	     } else {
	    	 Sbi.Msg.showWarning('Filter operator type [' + operator + '] not supported');
	     }
	  
	 }
	   
	 , reloadComboStores: function() {
		for(var cs in this.cbStores) {
			this.reloadComboStore(cs);
		}
		this.store.filterPlugin.applyFilters();
	}
	    
	 , reloadComboStore: function(dataIdx) {
		 var distinctValues; 
		 var data;
      
		 var s = this.cbStores[dataIdx];
		
		 if(!s) {
		   Sbi.msg.showError('Impossible to refresh filter associated to column [' + dataIdx + ']');
		   return;
		 }
		 
		 this.store.clearFilter( true );
	   
		 distinctValues = this.store.collect(dataIdx, true, true);
		 data = [];
	   
		 //define the empty (for reset) element
	   	var firstRow = {
	      name: '...'
		  , value: 'emptyEl'
		  , description: ''
	   	};
	   	data.push(firstRow);
      
	   	for(var i = 0, l = distinctValues.length; i < l; i++) {
		   var row = {
			  name: distinctValues[i]
			  , value: distinctValues[i]
			  , description: distinctValues[i]
		   };
		   data.push(row);
	   	}
	   
	   	// replace previous records with the new one
	   	s.loadData(data, false);
	   	
	   	//this.store.filterPlugin.applyFilters();
	}
   
	 //adds the single filter or delete if it's the reset field
	 , onFilterSelect: function(f, exp) { 
		 if(this.fireEvent('beforefilterselect', this, f, exp) !== false){	
			 if (exp === 'emptyEl'){
				   this.store.filterPlugin.removeFilter(f);
			 }else{
			   this.store.filterPlugin.addFilter(f, exp);
			 }
			 this.store.filterPlugin.applyFilters();
		 }
	 }
  
});