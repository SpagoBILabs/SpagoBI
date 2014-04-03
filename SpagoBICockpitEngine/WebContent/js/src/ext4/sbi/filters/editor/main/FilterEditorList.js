/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

 
/**

  * Authors
  * 
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.define('Sbi.filters.editor.main.FilterEditorList', {
	extend: 'Ext.Panel'
	, layout: 'fit'
	, config:{	
		  services: null
		, grid: null
		, store: null
		, storesList: null
		, displayRefreshButton: null  // if true, display the refresh button
		, border: false
		, style: {marginTop: '3px', marginRight: '5px', marginLeft:'5px'}
		, height: 200	
		, autoScroll: true
//	,	bodyStyle:'padding:3px;background:green'
  		
	}
	/**
	 * @property currentAss
	 * The current Association selected in the list
	 */
	, currentAss : null
	
	, constructor : function(config) { 	
		Sbi.trace("[FilterEditorList.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(config);
//		this.addEvents("addAssociation","removeAssociation","selectAssociation","updateIdentifier");
		Sbi.trace("[FilterEditorList.constructor]: OUT");
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
	
    // -----------------------------------------------------------------------------------------------------------------
    // init methods
	// -----------------------------------------------------------------------------------------------------------------
	/**
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, init: function() {
		this.initStore();
//		this.initGrid();
	}
    
	, initStore: function() {
		Sbi.trace("[FilterEditorList.initStore]: IN");
		var pippo = Ext.JSON.encode( this.storesList);
		var pluto = Ext.JSON.decode( this.storesList);
		var storeConfig = {
//				   model: 'Sbi.data.DatasetsFieldsModel' 
				   proxy:{
				    	type : 'rest',
				    	url : Sbi.config.serviceRegistry.getRestServiceUrl({
				    		serviceName : 'datasets/' + this.storesList+ '/parameters'
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
		Sbi.trace("[FilterEditorList.initStore]: OUT");
	}
	
    , initGrid: function() {
//    	var thisPanel = this;
//    	var c = this.gridConfig;
//    	
//    	// The add action
//    	var title = new Ext.form.Label({text:LN('sbi.cockpit.association.editor.wizard.list.title'),  style: 'font-weight:bold;'});
//        var actionAdd = new Ext.Action({
////            text: LN('sbi.cockpit.association.editor.wizard.list.add'),
//            tooltip: LN('sbi.cockpit.association.editor.wizard.list.add.tooltip'),
//            iconCls:'icon-add',
//            handler: function(){
//            	thisPanel.fireEvent("addAssociation",null);
//            }
//        });
//        
//        var actionModify = new Ext.Action({
////        	text: LN('sbi.cockpit.association.editor.wizard.list.modify'),
//            tooltip: LN('sbi.cockpit.association.editor.wizard.list.modify.tooltip'),
//            iconCls:'icon-edit',
//            scope: this,
//            handler: function(){
//            	thisPanel.fireEvent("modifyAssociation", thisPanel);
//            }
//        });
//        
//        var actionAutoDetect = new Ext.Action({
//        	text: LN('sbi.cockpit.association.editor.wizard.list.autodetect'),
//            tooltip: LN('sbi.cockpit.association.editor.wizard.list.autodetect.tooltip'),
//            handler: function(){
//            	alert('Functionality not available!');
////            	thisPanel.fireEvent("autodetect", thisPanel);
//            },
//            disabled: true
//        });
//		
//
//        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
//            clicksToEdit: 1
//        });
//        
//        this.grid = Ext.create('Ext.grid.Panel', Ext.apply(c || {}, {
//	        store: this.store,
//	        tbar: new Ext.Toolbar({items:[title, '->', actionAdd, actionModify, actionAutoDetect]}),
//	        selModel: {selType: 'rowmodel', mode: 'SINGLE', allowDeselect: true},
//	        columns: [
//	            { header: LN('sbi.cockpit.association.editor.wizard.list.columnId')
//            	, width: 10
//            	, sortable: true
//            	, dataIndex: 'id'
//            	, editor: {
//                        allowBlank: false
//                  }
//            	, flex: 1
//            	}, {
//        		  header: LN('sbi.cockpit.association.editor.wizard.list.columnAssociation')
//            	, width: 700
//            	, sortable: true
//            	, dataIndex: 'ass'
//            	},{
//                    xtype: 'actioncolumn',
//                    width: 50,
//                    items: [{
//                        iconCls:'icon-delete',
//                        tooltip: LN('sbi.cockpit.association.editor.wizard.list.delete.tooltip'),
//                        handler: this.deleteAssociation ,
//                        scope:this
//                    }
//                    ]
//                }
//	        ],	        
//	        viewConfig: {
//	        	stripeRows: false
//	        },
//	        plugins: [cellEditing]
//	    }));
//        this.grid.on('itemclick', this.onCellClick, this);
//        this.grid.on('edit', function(editor, e) {
//            // commit the changes right after editing finished
//            e.record.commit();
//            this.fireEvent('updateIdentifier', e);
//        }, this);
    }    

    
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------	

   
    
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------	    
 

});