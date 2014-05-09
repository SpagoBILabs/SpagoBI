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
		, widgetManager: null
		, showByAssociation: true
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
		
		if(this.showByAssociation === true) {
			var data = this.initStoreDataByAssociation();
			this.store = new Ext.data.ArrayStore({
				fields: ['association', 'values']
				, data: data
			});
		} else {
			var data = this.initStoreDataByWidget();
			this.store = new Ext.data.ArrayStore({
				fields: ['widget', 'field', 'values']
				, groupField: 'widget'
				, data: data
			});
		}
		
			
		Sbi.trace("[SelectionsPanel.initStore]: OUT");
	}
	
	, initStoreDataByAssociation: function() {
		var initialData = [];
		var selections = this.widgetManager.getSelectionsByAssociations();
		for(var association in selections) {
			var el = [association, selections[association].join()];
			initialData.push(el);
		}
		return initialData;
	}
	
	, initStoreDataByWidget: function() {
		var initialData = [];
		
		var selections = this.widgetManager.getSelections() || [];
		
		for (widget in selections){
			var values = [];
			for (field in selections[widget]){						
				if (!Ext.isFunction(selections[widget])){	
					values = this.getFieldValues(selections[widget][field].values);
					var el = [widget,  field, values];
					initialData.push(el);
				}
			}  
		}
		
		return initialData;
	}
		
	, initGrid: function() {
	    	var c = this.gridConfig;
	    	var groupingFeature = Ext.create('Ext.grid.feature.Grouping',{
    	        groupHeaderTpl: 'Widget: {name} ({rows.length} '+ LN('sbi.cockpit.core.selections.list.items')+')'
    	    });
    	    
    	    var features = (this.showByAssociation === true)? undefined: [groupingFeature];
	    	
	    	var columns = [];
	    	
	    	if(this.showByAssociation === true) {
	    		columns.push({ 
	    			header: LN('sbi.cockpit.core.selections.list.columnAssociation')
	            	, width: 10
	            	, sortable: true
	            	, dataIndex: 'association'
	            	, flex: 1
	            });
	    	} else {
	    		columns.push({ 
	    			header: LN('sbi.cockpit.core.selections.list.columnWidget')
	            	, width: 10
	            	, sortable: true
	            	, dataIndex: 'widget'
	            	, flex: 1
	            });
	    		columns.push({ 
	    			 header: LN('sbi.cockpit.core.selections.list.columnField')
		             , width: 70
		             , sortable: true
		             , dataIndex: 'field'
		             , flex: 1
	    		});
	    		
	    	}
	    	
	    	columns.push({ 
	    		header: LN('sbi.cockpit.core.selections.list.columnValues')
            	, width: 70
            	, sortable: true
            	, dataIndex: 'values'
            	, flex: 1
	    	});
	    	
	        this.grid = Ext.create('Ext.grid.Panel', Ext.apply(c || {}, {
		        store: this.store,
		        features: features,
		        columns: columns,	        
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
