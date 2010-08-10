
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
  * - Chiara Chiarelli (chiara.chiarelli@eng.it)
  */

Ext.ns("Sbi.widgets");

Sbi.widgets.FilterLookupField = function(config) {
	
	Ext.apply(this, config);
	
	this.store = config.store;
	this.store.on('metachange', function( store, meta ) {
		this.updateMeta( meta );
	}, this);
	this.store.on('load', function( store, records, options  ) {
		this.applySelection();		
	}, this);
	
	this.addEvents('selectionmade');	

	this.store.baseParams  = config.params;
	this.params = config.params;
	this.initWin();
	
	
	var c = Ext.apply({}, config, {
		triggerClass: 'x-form-search-trigger'
		, enableKeyEvents: true
		,  width: 150
	});   
	
	// constructor
	Sbi.widgets.FilterLookupField.superclass.constructor.call(this, c);
	
	this.on("render", function(field) {
		field.trigger.on("click", function(e) {
			this.onLookUp(); 
		}, this);
	}, this);
	
	this.on("render", function(field) {
		field.el.on("keyup", function(e) {
			this.xdirty = true;
		}, this);
	}, this);
	
	/*
	this.grid.on('render', function(g) {
		alert('DONE');
		g.getView().on( 'beforerefresh', function(){ alert('beforerefresh'); } );
		g.getView().on( 'refresh', function(){ alert('refresh'); } );
	});
	*/
	
	
	
};

Ext.extend(Sbi.widgets.FilterLookupField, Ext.form.TriggerField, {
    
	// ----------------------------------------------------------------------------------------
	// members
	// ----------------------------------------------------------------------------------------
    
	// STATE MEMBERS
	  valueField: null
    , displayField: null
    , descriptionField: null
    
    // oggetto (value: description, *)
    , xvalue: null
    // oggetto (value: description, *)
    , xselection: null
    , xdirty: false
    
    , singleSelect: true
    
    , paging: true
    , start: 0 
    , limit: 20
    
	// SUB-COMPONENTS MEMBERS
	, store: null
	, sm: null
    , grid: null
    , win: null
    
    // default separator for lookup values
    , valuesSeparator: ';'
       
   
    // ----------------------------------------------------------------------------------------
    // public methods
	// ----------------------------------------------------------------------------------------
    
    
    , getValue : function(){
    
		this.clean();
		var v = [];
		this.xvalue = this.xvalue || {};
		for(p in this.xvalue) {
			v.push(p);
		}
			
		if(this.singleSelect === true) {
			v = (v.length > 0)? v[0] : '';
		}
		
		return v;
	}

	/**
	 * v: 
	 *  - object -> multivalue with values/descriptions
	 *  - array -> multivalue with only values
	 *  - string -> single value
	 */
	, setValue : function(v){	
		if(typeof v === 'object') {
			this.xvalue = {};
			
			if(v instanceof Array) {
				var t = {};
				for(var i = 0; i < v.length; i++) {
					t[ v[i] ] = v[i];
				}
				v = t;
			}
			
			Ext.apply(this.xvalue, v);
			var displayText = '';
			for(p in this.xvalue) {
				displayText += this.xvalue[p] + ';';
			}	
			if(this.singleSelect === true) {
				displayText = displayText.substr(0, displayText.length-1);
			}
			Sbi.widgets.FilterLookupField.superclass.setValue.call(this, displayText);
		} else {
			this.xvalue = {};
			this.xvalue[v] = v;
			Sbi.widgets.FilterLookupField.superclass.setValue.call(this, v);
		}
	}
	
	
    
    // private methods
    , initWin: function() {
     
     var cm = new Ext.grid.ColumnModel([
		   new Ext.grid.RowNumberer(),
	       {
	       	  header: "Values",
	          dataIndex: 'Values',
	          width: 75
	          //renderer: Ext.util.Format.dateRenderer('d/m/Y H:i:s')
	       }
	    ]);
		
		
		var pagingBar = new Sbi.widgets.PagingToolbar({
	        pageSize: this.limit,
	        store: this.store,
	        displayInfo: true,
	        displayMsg: '', //'Displaying topics {0} - {1} of {2}',
	        emptyMsg: "No topics to display",
	        
	        items:[
	               '->'
	               , {
	            	   text: LN('sbi.lookup.Annulla')
	            	   , listeners: {
		           			'click': {
		                  		fn: this.onCancel,
		                  		scope: this
		                	} 
	               		}
	               } , {
	            	   text: LN('sbi.lookup.Confirm')
	            	   , listeners: {
		           			'click': {
		                  		fn: this.onOk,
		                  		scope: this
		                	} 
	               		}
	               }
	        ]
	    });
		
		var filteringToolbar = new Sbi.widgets.FilteringToolbar({store: this.store});
		
		this.sm = new Ext.grid.CheckboxSelectionModel( {singleSelect: this.singleSelect } );
		this.sm.on('rowselect', this.onSelect, this);
		this.sm.on('rowdeselect', this.onDeselect, this);
		
		this.grid = new Ext.grid.GridPanel({
			store: this.store
   	     	, cm: cm
   	     	, sm: this.sm
   	     	, frame: false
   	     	, border:false  
   	     	, collapsible:false
   	     	, loadMask: true
   	     	, viewConfig: {
   	        	forceFit:true
   	        	, enableRowBody:true
   	        	, showPreview:true
   	     	}
			
			, tbar: filteringToolbar
	        , bbar: pagingBar
		});
		
		this.win = new Ext.Window({
			title: LN('sbi.lookup.Select') ,   
            layout      : 'fit',
            width       : 580,
            height      : 300,
            closeAction :'hide',
            plain       : true,
            items       : [this.grid]
		});
	}
    
    , updateMeta: function(meta) {
    	if(this.grid){		
  
			this.valueField = meta.valueField;
			this.displayField = meta.displayField;
			this.descriptionField = meta.descriptionField;
			
			meta.fields[0] = new Ext.grid.RowNumberer();
			meta.fields[ meta.fields.length - 1 ] = this.sm;

			if(meta.fields[1].type == 'date'){
				meta.fields[1].renderer = Ext.util.Format.dateRenderer('d/m/Y H:i:s');
			}
	
			this.grid.getColumnModel().setConfig(meta.fields);		
			
		} else {
		   alert('ERROR: store meta changed before grid instatiation')
		}		
	}
    
    , resetSelection: function(valuesToLoad) {
    	this.xselection = Ext.apply({}, this.xvalue);   
    	if (valuesToLoad && valuesToLoad !== undefined){
    		this.xselection['Values'] = valuesToLoad;    
    	}
   	}
    
    , onSelect: function(sm, rowIndex, record) {
    	
    	if(this.singleSelect === true){
    		this.xselection = {}
    	}
    	
    	// initializing the values' array, if not already initialized
    	if (this.xselection['Values'] === undefined) {
    		this.xselection['Values'] = [];
    	}

    	var valueToAdd = record.data[this.valueField];
    	if(this.grid.getColumnModel().getColumnById('1').type == 'date'){
    		valueToAdd = valueToAdd.format('d/m/Y H:i:s');
    	}
    	
    	// it the new value is not contained into the values' array, it is added
    	if (this.xselection['Values'].indexOf(valueToAdd) === -1) {
    		this.xselection['Values'].push(valueToAdd);
    	}
    }
    
    , onDeselect: function(sm, rowIndex, record) {
    	if (this.xselection['Values'] && this.xselection['Values'].length > 0) {
    		var valueToRemove = record.data[this.valueField];
    		if (this.grid.getColumnModel().getColumnById('1').type == 'date') {
    			valueToRemove = valueToRemove.format('d/m/Y H:i:s');
    		}
    		this.xselection['Values'].remove(valueToRemove);
    	}    	
    }
    
    , applySelection: function() {
    	if (this.grid && this.xselection['Values']) {
			var selectedRecs = [];
			this.grid.getStore().each(function(rec) {
				var valueToLookFor = rec.data[this.valueField];
	    		if (this.grid.getColumnModel().getColumnById('1').type == 'date') {
	    			valueToLookFor = valueToLookFor.format('d/m/Y H:i:s');
	    		}
				if (this.xselection['Values'].indexOf(valueToLookFor) !== -1){
		        	selectedRecs.push(rec);	        	
		        }
		    }, this);
			this.sm.selectRecords(selectedRecs);		    
		 }		
    }
	
    , clean: function() {
    	if (this.xdirty) {
	    	var text = Sbi.widgets.FilterLookupField.superclass.getValue.call(this);
	    	var values = text.split(this.valuesSeparator);
	    	this.xvalue = {};
	    	if(text.trim() === '') return;
	    	var ub = (this.singleSelect === true)? 1 : values.length;
	    	for (var i = 0; i < ub; i++) {
	    		this.xvalue[ '' + values[i] ] = values[i];
	    	}
	    	this.xdirty = false;
    	}
    }
    
	, onLookUp: function(valuesToload) {

		this.clean();
		this.resetSelection(valuesToload);
		
		this.win.show(this);
		var p = Ext.apply({}, this.params, {
			start: this.start
			, limit: this.limit
		});
		this.store.load({params: p});

	}
	
	, onOk: function() {
		this.setValue(this.xselection);	
		this.win.hide();	
		this.fireEvent('selectionmade', this.xselection);	
	}
	
	, onCancel: function() {
		this.win.hide();
	}
});