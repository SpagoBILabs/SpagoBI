/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

 
/**

  * Authors
  * 
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.define('Sbi.data.editor.relationship.RelationshipEditorList', {
	extend: 'Ext.Panel'
	, layout: 'fit'
	, config:{	
		  services: null
		, grid: null
		, store: null
		, displayRefreshButton: null  // if true, display the refresh button
		, border: true
//		, bodyStyle:'background:green;',
		, style: {marginTop: '3px', marginRight: '5px', marginLeft:'5px'}
		, height: 200	
//	,	bodyStyle:'padding:3px;background:green'
  		
	}

	, constructor : function(config) { 	
		Sbi.trace("[RelationshipEditorList.constructor]: IN");
		this.initConfig(config);
		this.initServices();
		this.init();
		this.callParent(config);
		this.addEvents("addRelation");
		Sbi.trace("[RelationshipEditorList.constructor]: OUT");
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
	   this.store = Ext.create('Ext.data.ArrayStore', {
	        fields: [
	           {name: 'id'},
	           {name: 'rel'}
	        ],
	        data: []
	    });
		
		Sbi.trace("[RelationshipEditorDataset.initStore]: OUT");
	}
	
    , initGrid: function() {
    	var thisPanel = this;
    	var c = this.gridConfig;
    	
    	// The add action
    	var title = new Ext.form.Label({text:'Associations List',  style: 'font-weight:bold;'});
        var action = new Ext.Action({
            text: 'Add',
            handler: function(){
            	thisPanel.fireEvent("addRelation", thisPanel);
            }
        });
		
        this.grid = Ext.create('Ext.grid.Panel', Ext.apply(c || {}, {
	        store: this.store,
	        tbar: new Ext.Toolbar({items:[title, '->', action]}),
	        columns: [
	            { header: 'Id' //LN('sbi.formbuilder.queryfieldspanel.fieldname')
            	, width: 10
            	, sortable: true
            	, dataIndex: 'id'
            	, flex: 1
            	}, {
        		  header: 'Association ' // LN('sbi.formbuilder.queryfieldspanel.fieldtype')
            	, width: 700
            	, sortable: true
            	, dataIndex: 'rel'
            	},{
                    xtype: 'actioncolumn',
                    width: 50,
                    items: [{
//                        icon   : '../shared/icons/fam/delete.gif',  // Use a URL in the icon config
                        iconCls:'icon-delete',
                        tooltip: 'Delete association',
                        handler: function(grid, rowIndex, colIndex) {
                            var rec = this.store.getAt(rowIndex);
                            alert("Delete association " + rec.get('rel'));
                        },
                        scope:this
                    }
                    ]
                }
	        ],	        
	        viewConfig: {
	        	stripeRows: false
	        }
	    }));
    }
    
    , addRelToStore: function(r){
    	var relId = '#'+ ((this.store.data.length !== undefined)?this.store.data.length:0);
    	var myData = [
		              [relId, r.rel]
		             ];
//    	var myData =  this.store.data.items;
//    	myData.push([relId, r.rel]);
	
		this.store.loadData(myData, true);
		this.doLayout();	
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
    
    , addRelationToList: function(rel){
    	this.addRelToStore(rel);
    }
});