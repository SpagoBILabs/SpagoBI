/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

 
/**

  * Authors
  * 
  * - Antonella Giachino (antonella.giachino@eng.it)
  */


Ext.define('Sbi.data.editor.association.AssociationEditorDataset', {
	  extend: 'Ext.Panel'
	, layout: 'fit'
		
	, config:{	
		border: true
		, dataset: null
		, gridConfig: null
	    , style: {marginTop: '3px', marginRight: '5px', marginLeft:'5px'}
//		, bodyStyle:'padding:3px;background:green'    
		, services: null
	    , grid: null	    
	    , height : 225
		, width : 180		
	    , displayRefreshButton: true  // if true, display the refresh button
	}

	, constructor : function(config) {
		Sbi.trace("[AssociationEditorDataset.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(config);
		this.addEvents('addFieldToAssociation');	
		
//		this.on("afterRender", function(){
//			this.grid.store.load();
//			Sbi.trace("[AssociationEditorDataset.onRender]: store loaded");
//		}, this);
		Sbi.trace("[AssociationEditorDataset.constructor]: OUT");
	}
	
	, initComponent: function() {
	        Ext.apply(this, {
	            items: [this.grid]
	        });
	        this.callParent();
	}	   

    
    // =================================================================================================================
	// METHODS
	// =================================================================================================================
	    
    , refreshFieldsList: function(datasetLabel) {
    	Sbi.trace("[AssociationEditorDataset.refreshFieldsList]: IN");
    	
    	Sbi.trace("[AssociationEditorDataset.refreshFieldsList]: input parameter datasetLabel is equal to [" + datasetLabel + "]");
    	
		if (datasetLabel) {	
			this.dataset = datasetLabel;
			this.store.proxy.setUrl(Sbi.config.serviceRegistry.getRestServiceUrl({
				serviceName : 'dataset/' + this.dataset + '/fields'
			}), true);
			Sbi.trace("[AssociationEditorDataset.refreshFieldsList]: url: " + this.store.url);
		} 
		this.store.load();
		
		Sbi.trace("[AssociationEditorDataset.refreshFieldsList]: OUT");
	}

	

    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.initGrid();
	}
    
	, initStore: function() {
		Sbi.trace("[AssociationEditorDataset.initStore]: IN");
		var storeConfig = {
//				   model: 'Sbi.data.DatasetsFieldsModel' //with model doesn'work !! But WHY???
				   proxy:{
				    	type : 'rest',
				    	url : Sbi.config.serviceRegistry.getRestServiceUrl({
				    		serviceName : 'dataset/' + this.dataset + '/fields'
				    	}),
				    	reader : {
				    		type : 'json',
				    		root : 'results'
				    	}
				   	},
				   	autoLoad: true,
				   	fields: [
					         'id', 
					         'alias', 
					         'colType', 
					         'funct', 
					         'iconCls', 
					         'nature', 
					         'values', 
					         'precision', 
					         'options'
					         ]
		};
		var store = Ext.create('Ext.data.Store', storeConfig);
		return store;
		Sbi.trace("[AssociationEditorDataset.initStore]: OUT");
	}
	
    , initGrid: function() {
    	var gridStore = this.initStore();
    	
    	this.grid = Ext.create('Ext.grid.Panel',  {
    		title: this.dataset,
        	margins: '5 5 5 0',
        	selModel: {selType: 'rowmodel', mode: 'SINGLE', allowDeselect: true},
	        store: gridStore,
	        columns: [
	            { header: 'Column' //LN('sbi.formbuilder.queryfieldspanel.fieldname')
            	, width: 100
            	, sortable: true
            	, dataIndex: 'alias'
            	, flex: 1
            	}, {
        		  header: 'Type' // LN('sbi.formbuilder.queryfieldspanel.fieldtype')
            	, width: 75
            	, sortable: true
            	, dataIndex: 'colType'
            	}
	        ],
	        viewConfig: {
	        	stripeRows: false	        	
	        }	        
	    });

    }
    
    // private methods
    
    
    
    // public methods 
    , getFields : function () {
    	var fields = [];
    	var count = this.store.getCount();
    	for (var i = 0; i < count; i++) {
    		fields.push(this.store.getAt(i).data);
    	}
    	return fields;
    }

});