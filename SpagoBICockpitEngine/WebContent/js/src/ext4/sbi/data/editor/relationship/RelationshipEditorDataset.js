/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

 
/**

  * Authors
  * 
  * - Antonella Giachino (antonella.giachino@eng.it)
  */


Ext.define('Sbi.data.editor.relationship.RelationshipEditorDataset', {
	  extend: 'Ext.Panel'
	, layout: 'fit'
		
	, config:{	
		border: true
		, dataset: null
		, gridConfig: null
//		, bodyStyle:'background:green;',
	    , style: {marginTop: '3px', marginRight: '5px', marginLeft:'5px'}
//	,	bodyStyle:'padding:3px;background:green'        
		, services: null
	    , grid: null	    
	    , height : 225
		, width : 180		
	    , displayRefreshButton: true  // if true, display the refresh button
	}

	/**
	 * @property {Ext.data.Store} store
	 *  The store for the grid
	 */
	, store: null
	, constructor : function(config) {
		Sbi.trace("[RelationshipEditorDataset.constructor]: IN");
		this.initConfig(config);
		this.initServices();
		this.init();
		this.callParent(config);
		this.addEvents('addFieldToRelation');	
		Sbi.trace("[RelationshipEditorDataset.constructor]: OUT");
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
    	Sbi.trace("[RelationshipEditorDataset.refreshFieldsList]: IN");
    	
    	Sbi.trace("[RelationshipEditorDataset.refreshFieldsList]: input parameter datasetLabel is equal to [" + datasetLabel + "]");
    	
		if (datasetLabel) {	
			this.dataset = datasetLabel;
			this.store.proxy.setUrl(Sbi.config.serviceRegistry.getRestServiceUrl({
				serviceName : 'dataset/' + this.dataset + '/fields'
			}), true);
			Sbi.trace("[RelationshipEditorDataset.refreshFieldsList]: url: " + this.store.url);
		} 
		this.store.load();
		
		Sbi.trace("[RelationshipEditorDataset.refreshFieldsList]: OUT");
	}

	

    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	
	/**
	 * @method 
	 * 
	 * Initialize the following services exploited by this component:
	 * 
	 *    - getQueryFields
	 */
    , initServices: function(){
    	var baseParams = {};
    	if (this.dataset) baseParams.dataset = this.dataset;
    	this.services = this.services || new Array();	
    	this.services["getQueryFields"] = Sbi.config.serviceRegistry.getRestServiceUrl({
    		serviceName : 'dataset/{label}/fields'
    	  , baseParams:	baseParams
    	});	
    
    }
	
	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.initStore();
		this.initGrid();
	}
    
	, initStore: function() {
		Sbi.trace("[RelationshipEditorDataset.initStore]: IN");

		
//		var storeConfig = {
//				   model: 'Sbi.data.DatasetsFieldsModel',
//				   proxy:{
//				    	type : 'rest',
//				    	url : Sbi.config.serviceRegistry.getRestServiceUrl({
//				    		serviceName : 'dataset/' + this.dataset + '/fields'
//				    	}),
//				    	reader : {
//				    		type : 'json',
//				    		root : 'results'
//				    	}
//				   	},
//				   	autoLoad: true,
//				   	autoSync: true
//		};
//		this.store = Ext.create('Ext.data.Store', storeConfig);
		
		var myData = [
		              ['Col. 1', 'String'],
		              ['Col. 2', 'Double'],
		              ['Col. 3', 'String']
		              ];
		
		this.store = Ext.create('Ext.data.ArrayStore', {
	        fields: [
	           {name: 'alias'},
	           {name: 'colType'}
	        ],
	        data: myData
	    });

//		this.store.on('load', function(){
//			Sbi.trace("[RelationshipEditorDataset.onLoad]: XXX");
//			alert('loading store...');
////			this.fireEvent("validateInvalidFieldsAfterLoad", this); 	 //evento per autodetect?	
//		}, this);
		
		Sbi.trace("[RelationshipEditorDataset.initStore]: OUT");
	}
	
    , initGrid: function() {
    	var c = this.gridConfig;
		
		this.grid = Ext.create('Ext.grid.Panel', Ext.apply(c || {}, {
	        store: this.store,
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
	    }));
//		this.grid.on('cellClick', this.onCellClick, this);
    }
    
    // private methods
    
    , onCellClick: function(grid, rowIndex, columnIndex, e){
    	var record = grid.getStore().getAt(rowIndex);  // Get the Record
        var fieldName = grid.getColumnModel().getDataIndex(0); // Get field name
        var fieldType = grid.getColumnModel().getDataIndex(1); // Get field type
        var dataName = record.get(fieldName);
        var dataType = record.get(fieldType);
        var f = {ds: this.dataset, fName: dataName, fType: dataType};
    }
    
    
    
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