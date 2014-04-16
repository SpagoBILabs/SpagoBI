/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define('Sbi.cockpit.core.SelectionsPanel', {
	extend: 'Ext.Panel'
	, layout:'fit'

	, config:{
		  closable: false
		, modal: true
		, grid: null
		, store: null
		, selections :null
	}

	, constructor : function(config) {
		Sbi.trace("[SelectionsPanel.constructor]: IN");
		this.initConfig(config);
		this.init(config);
		this.initEvents();
		this.callParent(arguments);
		Sbi.trace("[SelectionsPanel.constructor]: OUT");
	}
	
	, initComponent: function() {
  
        Ext.apply(this, {
            items: [this.grid]
        });
        
        this.callParent();
    }
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------

	
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------

	, init: function(c){
		Sbi.trace("[SelectionsPanel.init]: IN");		
		this.initStore();
		this.initGrid();		
		Sbi.trace("[SelectionsPanel.init]: OUT");
	}
	
	, initEvents: function() {
		this.addEvents(
			/**
			* @event indicatorsChanged
			* Fires when data inserted in the wizard is canceled by the user
			* @param {AssociationEditorWizard} this
			*/
			'cancel'
		);
	}
	
	, initStore: function() {
		   Sbi.trace("[SelectionsPanel.initStore]: IN");
		   var initialData = [];
			
		   if (this.selections !== null ){
			   for (s in this.selections){
				   	var widget = s;
				   	var field = "";
				   	var values = [];
					for (f in this.selections[s]){						
						if (!Ext.isFunction(this.selections[s])){	
							var obj = this.selections[s];
							field = f;
							values = this.getFieldValues(obj[f].values);
							var el = [widget,  field, values];
							initialData.push(el);
						}
					}  
				}
		   }

		   this.store = new Ext.data.ArrayStore({
				fields: [
				         'widget',
				         'field',
				         'values'
				         ]
		   		, groupField: 'widget'
				, data: initialData
		  });
			
			Sbi.trace("[SelectionsPanel.initStore]: OUT");
		}
		
	    , initGrid: function() {
	    	var c = this.gridConfig;
	    	var groupingFeature = Ext.create('Ext.grid.feature.Grouping',{
    	        groupHeaderTpl: 'Widget: {name} ({rows.length} Item{[values.rows.length > 1 ? "s" : ""]})'
    	    });
	        this.grid = Ext.create('Ext.grid.Panel', Ext.apply(c || {}, {
		        store: this.store,
		        features: [groupingFeature],
		        columns: [
		            { header: LN('sbi.cockpit.core.selections.list.columnWidget')
	            	, width: 10
	            	, sortable: true
	            	, dataIndex: 'widget'
	            	, flex: 1
//	            	, hidden: true
	            	}, {
	        		  header: LN('sbi.cockpit.core.selections.list.columnField')
	            	, width: 70
	            	, sortable: true
	            	, dataIndex: 'field'
	            	, flex: 1
	            	}, {
	        		  header: LN('sbi.cockpit.core.selections.list.columnValues')
	            	, width: 70
	            	, sortable: true
	            	, dataIndex: 'values'
	            	, flex: 1
		            }
		        ],	        
		        viewConfig: {
		        	stripeRows: true
		        }
		    }));
	    }    
	
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, onCancel: function(){
		this.fireEvent("cancel", this);
	}
	
	, getFieldValues: function(values){
		var toReturn = "";
		var comma = "";
		for (var i=0; i< values.length; i++){
			toReturn += comma + values[i];
			if (comma == "") comma = ", ";
		}
		return toReturn;
	}
	

});
