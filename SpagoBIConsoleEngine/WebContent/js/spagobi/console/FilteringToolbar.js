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
	, filterStores: null
	//, filters: null
	, cbFilters: null


	
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
			   //single value
			   this.filterStores = this.filterStores || {}; 
			   this.cbFilters = this.cbFilters || {};
			   var s = new Ext.data.JsonStore({
				   fields:['name', 'value', 'description'],
		           data: []
			   });
			   this.filterStores[dataIndex] = s;
			 
			   //this.store.on('load', this.reloadFilterStore.createDelegate(this, [dataIndex]), this);
		     
			   var combDefaultConfig = {
					   width: 130,
				       displayField:'name',
				       valueField:'value',
				       typeAhead: true,
				       triggerAction: 'all',
				       emptyText:'...',
				       //selectOnFocus:true,
				       selectOnFocus:false,
				       validateOnBlur: false,
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
			   //adds the combo field to a temporary array to manage the workaround on the opening on each refresh
			   this.cbFilters[dataIndex] = cb;
			   
	     }else if (operator === 'IN') {
	    	 //multivalue
	    	 this.filterStores = this.filterStores || {};	    	 
	    	 var smLookup = new Ext.grid.CheckboxSelectionModel( {singleSelect: false } );
	    	 var cmLookup =  new Ext.grid.ColumnModel([
		    	                                          new Ext.grid.RowNumberer(),		    	                                          
						                    		      {header: "Data", dataIndex: 'value', width: 75},
						                    		      smLookup
						                    		    ]);
	    	 var baseConfig = {
	    			     width: 130
				       , name : dataIndex
				       , emptyText:'...'
					   , allowBlank: true
					   , cm: cmLookup
					   , sm: smLookup
					};
	    	
	    	 var s = new Ext.ux.data.PagingJsonStore({	  
	    	//var s = new Ext.ux.data.PagingStore({	   
				   fields:['name', 'value', 'description'],
		           data: [],
		           lastOptions: {params: {start: 0, limit: 20}}
			   });
	    	
			 this.filterStores[dataIndex] = s;
			
	    	 var lk = new Sbi.console.LookupField(Ext.apply(baseConfig, {
				  	  store: s
					, params: {}
					, singleSelect: false
					, displayField: 'value'
					, valueField: 'value'
					, listeners: {
							   'select': {
							   		fn: function(values) {							   			
							   			var exp =  new Array();
										var field = dataIndex;
										
										for(var val in values){ 
											if (val !== '...'){
												exp.push(val);
											}else{
												exp =  new Array();
											}
										}										
										this.onFilterSelect(field, exp);	  
									},
									scope: this
								}				     					
						   }
			}));
	    	 
	    	this.addText("    " + header + "  ");
			this.addField(lk);	
			
	     }else {
	    	 Sbi.Msg.showWarning('Filter operator type [' + operator + '] not supported');
	     }
	  
	 }
	   
	 , reloadFilterStores: function() {
		for(var fs in this.filterStores) {
			this.reloadFilterStore(fs);
		}
		this.store.filterPlugin.applyFilters();
	}
	    
	 , reloadFilterStore: function(dataIdx) {
		 var distinctValues; 
		 var data;
      
		 var s = this.filterStores[dataIdx];
		
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
		  , value: '...'
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
	   	 
	   	 //WORKAROUND: when the user selects an item from the combo and stay on it, this combo is opened on each refresh.
	   	 //This workaround force the closure of the combo.
	   	 var cb = this.cbFilters[dataIdx];
	   	 if (cb){
	   		 cb.collapse();
	   	 }
	}
   
	 //adds the single filter or delete if it's the reset field
	 , onFilterSelect: function(f, exp) { 
		 if(this.fireEvent('beforefilterselect', this, f, exp) !== false){	
			 if (exp === '...' || exp.length == 0){
				   this.store.filterPlugin.removeFilter(f);
			 }else{
			   if (!Ext.isArray(exp)){
				   var arExp =  new Array();
				   arExp.push(exp);
				   exp = arExp;
			   }
			   this.store.filterPlugin.addFilter(f, exp );
			 }
			 this.store.filterPlugin.applyFilters();
		 }
	 }
  
});