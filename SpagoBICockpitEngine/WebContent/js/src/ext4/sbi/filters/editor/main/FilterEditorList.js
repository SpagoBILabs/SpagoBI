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
//		, style: {marginTop: '3px', marginRight: '5px', marginLeft:'5px'}
		, height: 180	
		, autoScroll: false
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
		this.initGrid();
	}
    
	, initStore: function() {		
		Sbi.trace("[FilterEditorList.initStore]: IN");
		
		 var paramStore = [];
			
		  if (this.storesList !== null ){			 
			   for (var i=0; i< this.storesList.length; i++){
				   var storeConfig = {
					   proxy:{
					    	type : 'rest',
					    	url: Sbi.config.serviceReg.getServiceUrl("loadDataSetParams", {
								pathParams: {datasetLabel: this.storesList[i]}
							}),
					    	reader : {
					    		type : 'json',
					    		root : 'results'
					    	}
					   	},
					   	autoLoad: true,
					   	fields: [
						         'nameObj', 
						         'typeObj', 
						         'namePar', 
						         'typePar',
						         'initialValue',
						         'typeDriver'
						         ]
						};
				   
					var localStore = Ext.create('Ext.data.Store', storeConfig);
					if (Sbi.isValorized( localStore.data ))
						paramStore.push(localStore.data.items);
				}
		   }
		  
		  //ONLY FOR TEST
		  paramStore = [];
		  var el = ['DS_CHART_SALES_COSTS', 'dataset', 'par_country', 'String'];
		  paramStore.push(el);
		  el = ['DS_CHART_SALES_COSTS', 'dataset', 'par_region', 'String'];
		  paramStore.push(el);
		  el = ['DS_CHART_SALES_COSTS', 'dataset', 'par_state', 'String'];
		  paramStore.push(el);
		  el = ['[ext-comp-1020]', 'widget', 'family', 'String'];
		  paramStore.push(el);
		  //FINE TEST
		  
//		  this.store = Ext.create('Ext.data.ArrayStore', {
		  this.store = Ext.create('Ext.data.SimpleStore', {
		        fields: [
		          'nameObj',
		          'typeObj',
		          'namePar',
		          'typePar',
 		          'initialValue',
		          'typeDriver'
		        ],
		        data: paramStore
//		        data: [{"nameObj":"Family","typeObj":"dataset","namePar":"family","typePar":"String"}]
		});
		this.store.load();
		Sbi.trace("[FilterEditorList.initStore]: OUT");
	}
	
    , initGrid: function() {
    	var thisPanel = this;
    	var c = this.gridConfig;
    	
        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        });
        
        var comboScope = new Ext.form.ComboBox({
			name : 'comboScope',
			store: new Ext.data.ArrayStore({
		        fields: [
		            'comboScopeType',
		            'comboScopeValue'
		        ],
		        data: [ ['Static', 'Static'], ['Relative', 'Relative']]
		    }),
			//width : 150,
			displayField : 'comboScopeType', 
			valueField : 'comboScopeType', 
			typeAhead : true, forceSelection : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true, 
			editable : false,
			allowBlank : false, 
			validationEvent : false,	
			queryMode: 'local'
			});	
        
        var textFieldInitialValue = new Ext.form.TextField({});
        
        this.grid = Ext.create('Ext.grid.Panel', Ext.apply(c || {}, {
	        store: this.store,
//	        tbar: new Ext.Toolbar({items:[title, '->', actionAdd, actionModify, actionAutoDetect]}),
	        selModel: {selType: 'rowmodel', mode: 'SINGLE', allowDeselect: true},
	        columns: [
	            { header: LN('sbi.cockpit.filter.editor.wizard.list.nameObj')
            	, width: "15%"
            	, sortable: true
            	, dataIndex: 'nameObj'            	
            	, flex: 1
//            	, style: 'backgroundcolor:#ff0000; text-decoration:line-through;'
            	}, {
        		  header: LN('sbi.cockpit.filter.editor.wizard.list.typeObj')
            	, width: "15%"
            	, sortable: true
            	, dataIndex: 'typeObj'
//            	, style: 'backgroundcolor:#ff0000; text-decoration:line-through;'
            	},{
          		  header: LN('sbi.cockpit.filter.editor.wizard.list.namePar')
              	, width: "15%"
              	, sortable: true
              	, dataIndex: 'namePar'
//              	, style: 'backgroundcolor:#ff0000; text-decoration:line-through;'
              	},{
          		  header: LN('sbi.cockpit.filter.editor.wizard.list.typePar')
              	, width: "15%"
              	, sortable: true
              	, dataIndex: 'typePar'
//              	, style: 'backgroundcolor:#ff0000; text-decoration:line-through;'
              	},{
          		  header: LN('sbi.cockpit.filter.editor.wizard.list.typeDriver')
              	, width: "15%"
              	, sortable: true
              	, dataIndex: 'typeDriver'
              	, editor: comboScope              	
                },{
        		  header: LN('sbi.cockpit.filter.editor.wizard.list.initialValue')
            	, width: "15%"
            	, sortable: true
            	, dataIndex: 'initialValue'
            	, editor: textFieldInitialValue
            	}
//              	,{
//                    xtype: 'actioncolumn',
//                    width: 50,
//                    items: [{
//                        iconCls:'icon-delete',
//                        tooltip: LN('sbi.cockpit.association.editor.wizard.list.delete.tooltip'),
//                        handler: this.deleteAssociation ,
//                        scope:this
//                    }
                    ],
//                }
//	        ],	        
	        viewConfig: {
	        	stripeRows: true
//	          , getRowClass: function(record, rowIndex, rowParams, store){
//	                return record.get('cls');
//	            }
//	        , getRowClass: function(record, index) {
//	            var c = record.get('change');
//	            if (c < 0) {
//	                return 'price-fall';
//	            } else if (c > 0) {
//	                return 'price-rise';
//	            }
//	        }
	        },
	        plugins: [cellEditing]
	    }));
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