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

	, config:{	
		  services: null
	    , grid: null
	    , store: null
	    , displayRefreshButton: null  // if true, display the refresh button
	}

	, constructor : function(config) { 	

		var defaultSettings = {
			border: true,
			bodyStyle:'background:green;',
			style: {marginTop: '3px', marginRight: '5px', marginLeft:'5px'}
	//	,	bodyStyle:'padding:3px;background:green'
	      ,	layout: 'fit'
		};
		var settings = Sbi.getObjectSettings('Sbi.data.editor.relationship.RelationshipEditorDataset', defaultSettings);
	
		var c = Ext.apply(settings, config || {});
		Ext.apply(this, c);
			
		this.initServices();
		this.init();
		
		this.items = [this.grid];
		this.callParent(c);
		this.addEvents('addFieldToRelation');	
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
    	
    	baseParams.user_id = 'paperino';
    	
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
		this.initGrid();
	}
    
	, initStore: function() {
		Sbi.trace("[RelationshipEditorDataset.initStore]: IN");
		//ONLY FOR TEST
		baseParams ={};
		baseParams.user_id = 'paperino';
		//FINE TEST
		this.store = new Ext.data.JsonStore({
			autoLoad : (this.dataset)?true:false
			, idProperty : 'alias'
			, root: 'results'
			, fields: ['id', 'alias', 'colType', 'funct', 'iconCls', 'nature', 'values', 'precision', 'options']
			, url: Sbi.config.serviceRegistry.getRestServiceUrl({
				serviceName : 'dataset/' + this.dataset + '/fields'
				,baseParams : baseParams //ONLY FOR TEST
			})
		}); 
    	
		this.store.on('loadexception', function(store, options, response, e){
			Sbi.exception.ExceptionHandler.handleFailure(response, options);
		}, this);
		
		this.store.on('load', function(){
			Sbi.trace("[RelationshipEditorDataset.onLoad]: XXX");
//			this.fireEvent("validateInvalidFieldsAfterLoad", this); 	 //evento per autodetect?	
		}, this);
		
		Sbi.trace("[RelationshipEditorDataset.initStore]: OUT");
	}
	
    , initGrid: function() {
    	var c = this.gridConfig;
		
    	this.initStore();
//		this.grid = new Ext.Panel({title:'sono un pannello ma dovrei essere una griglia'});
		this.grid = new Ext.grid.Panel(Ext.apply(c || {}, {
	        store: this.store,
	        hideHeaders: false,
	        columns: [
	            { header: 'Column' //LN('sbi.formbuilder.queryfieldspanel.fieldname')
            	, width: 160
            	, sortable: true
            	, dataIndex: 'alias'
	            , scope: this
            	}, {
        		  header: 'Type' // LN('sbi.formbuilder.queryfieldspanel.fieldtype')
            	, width: 75
            	, sortable: true
            	, dataIndex: 'colType'
	            , scope: this
            	}
	        ],
	        viewConfig: {stripeRows: false,
			        	 autoExpandColumn: 'alias',
			        	 enableDragDrop: true
	        }
//	        stripeRows: false,
//	        autoExpandColumn: 'alias',
//	        enableDragDrop: true
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