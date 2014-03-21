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
		, border: false
		, style: {marginTop: '3px', marginRight: '5px', marginLeft:'5px'}
		, height: 200	
//	,	bodyStyle:'padding:3px;background:green'
  		
	}
	/**
	 * @property currentRel
	 * The current relation selected in the list
	 */
	, currentRel : null
	
	, constructor : function(config) { 	
		Sbi.trace("[RelationshipEditorList.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(config);
		this.addEvents("addRelation","removeRelation","selectRelation","updateIdentifier");
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
    	var title = new Ext.form.Label({text:LN('sbi.cockpit.relationship.editor.wizard.list.title'),  style: 'font-weight:bold;'});
        var actionAdd = new Ext.Action({
//            text: LN('sbi.cockpit.relationship.editor.wizard.list.add'),
            tooltip: LN('sbi.cockpit.relationship.editor.wizard.list.add.tooltip'),
            iconCls:'icon-add',
            handler: function(){
            	thisPanel.fireEvent("addRelation",null);
            }
        });
        
        var actionModify = new Ext.Action({
//        	text: LN('sbi.cockpit.relationship.editor.wizard.list.modify'),
            tooltip: LN('sbi.cockpit.relationship.editor.wizard.list.modify.tooltip'),
            iconCls:'icon-edit',
            scope: this,
            handler: function(){
            	thisPanel.fireEvent("modifyRelation", thisPanel);
            }
        });
        
        var actionAutoDetect = new Ext.Action({
        	text: LN('sbi.cockpit.relationship.editor.wizard.list.autodetect'),
            tooltip: LN('sbi.cockpit.relationship.editor.wizard.list.autodetect.tooltip'),
            handler: function(){
            	alert('Functionality not available!');
//            	thisPanel.fireEvent("autodetect", thisPanel);
            },
            disabled: true
        });
		

        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        });
        
        this.grid = Ext.create('Ext.grid.Panel', Ext.apply(c || {}, {
	        store: this.store,
	        tbar: new Ext.Toolbar({items:[title, '->', actionAdd, actionModify, actionAutoDetect]}),
	        columns: [
	            { header: LN('sbi.cockpit.relationship.editor.wizard.list.columnId')
            	, width: 10
            	, sortable: true
            	, dataIndex: 'id'
            	, editor: {
                        allowBlank: false
                  }
            	, flex: 1
            	}, {
        		  header: LN('sbi.cockpit.relationship.editor.wizard.list.columnAssociation')
            	, width: 700
            	, sortable: true
            	, dataIndex: 'rel'
            	},{
                    xtype: 'actioncolumn',
                    width: 50,
                    items: [{
                        iconCls:'icon-delete',
                        tooltip: LN('sbi.cockpit.relationship.editor.wizard.list.delete.tooltip'),
                        handler: this.deleteRelation ,
                        scope:this
                    }
                    ]
                }
	        ],	        
	        viewConfig: {
	        	stripeRows: false
	        },
	        plugins: [cellEditing]
	    }));
        this.grid.on('itemclick', this.onCellClick, this);
        this.grid.on('edit', function(editor, e) {
            // commit the changes right after editing finished
            e.record.commit();
            this.fireEvent('updateIdentifier', e);
        }, this);
    }    

    
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------	
    /**
	 * @method 
	 * Returns the fields of the store
	 * 
	 */
    , getFields : function () {
    	var fields = [];
    	var count = this.store.getCount();
    	for (var i = 0; i < count; i++) {
    		fields.push(this.store.getAt(i).data);
    	}
    	return fields;
    }
    
    /**
	 * @method 
	 * Returns the current relation object
	 * 
	 */
    , getCurrentRel: function(){
    	return this.currentRel;
    }
    
    /**
	 * @method 
	 * Add the relation to the grid's store 
	 * 
	 * @param {Object} rel The relation 
	 */
    , addRelationToList: function(rel){
    	this.addRelToStore(rel);
    }
    
    /**
	 * @method 
	 * Remove the relation from the list and fire the event 'removeRelation' for remove it 
	 * from the relationsList too.
	 * 
	 * @param {Object} r The relation 
	 */
    , deleteRelation: function(grid, rowIndex, colIndex) {
    	var rec = this.store.getAt(rowIndex);  
    	var rel = rec.get('rel') ;
    	Ext.MessageBox.confirm(
    			LN('sbi.generic.pleaseConfirm')
    			, LN('sbi.cockpit.relationship.editor.msg.confirmDelete') +rel + ' ?'
                , function(btn, text) {
                    if ( btn == 'yes' ) {
                    	Sbi.trace("[RelationshipEditorList.deleteRelation]: Removed association  [ " +  rel + '] from Associations List');                                                
                    	this.removeRelationFromGrid(rec);
                        this.fireEvent('removeRelation', rel);
                    }
    			}
    			, this
    		);      
    }
	
    /**
	 * @method 
	 * Remove fisically the relation from the grid's store  
	 * 
	 * @param {Object} r The record to delete 
	 */
    , removeRelationFromGrid: function(r){
    	 this.grid.store.remove(r);
    }
    
    /**
	 * @method 
	 * Returns the record of the store.
	 * 
	 * @param {Object} id The relation identifier
	 */
    , getRelationById: function(id){
    	var recIdx = this.store.find('id', id); 
    	return this.store.getAt(recIdx);
    }
    
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------	    
    /**
	 * @method 
	 * Add the relation (getted by the cells selected) to the grid's store 
	 * 
	 * @param {Object} r The relation 
	 */
    , addRelToStore: function(r){
    	if (r.id  == null || r.id == undefined)
    		r.id = '#'+ ((this.store.data.length !== undefined)?this.store.data.length:0);
    	
    	var myData = [
		              [r.id, r.rel]
		             ];
	
		this.store.loadData(myData, true);
		this.doLayout();	
    }
    /**
	 * @method (listener)
	 * Fire the selectRelation event to select only the cells of the relation selected in the grid
	 * 
	 */    
    , onCellClick: function(grid, record, item, index, e, opt){
    	var ass = {};
//        ass.id = grid.getSelectionModel().getSelection()[0].get('id'); // Get association id
//        ass.rel= grid.getSelectionModel().getSelection()[0].get('rel'); // Get association content      
    	ass.id = record.get('id');
    	ass.rel = record.get('rel');
    		
        this.currentRel = ass;
        this.fireEvent('selectRelation',ass);
    }

});