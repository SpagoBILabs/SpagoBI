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
	 * @property grid
	 * The grid with the list of filters
	 */
	, grid: null
	
	/**
	 * @property currentFilter
	 * The current filter selected in the list
	 */
	, currentFilter : null
	
	, constructor : function(config) { 	
		Sbi.trace("[FilterEditorList.constructor]: IN");
		this.initConfig(config);
		this.init();
		this.callParent(config);
//		this.addEvents("addAssociation","removeAssociation","selectAssociation","updateIdentifier");
//		this.addEvents('updateInitialValues');
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
		
		this.store = new Ext.data.JsonStore({
				fields: [
				         'nameObj', 
				         'typeObj', 
				         'namePar', 
				         'typePar',
				         'initialValue',
				         'typeDriver',
				         'scope',
				         'initialValue'
				         ]
		  });
			
		  if (this.storesList !== null ){			 
			   for (var i=0; i< this.storesList.length; i++){
				   Ext.Ajax.request({
						url: Sbi.config.serviceReg.getServiceUrl("loadDataSetParams", {
							pathParams: {datasetLabel: this.storesList[i]}
						}),
						success : function(response, options) {							
							if(response !== undefined && response.responseText !== undefined && response.statusText=="OK") {
								if(response.responseText!=null && response.responseText!=undefined){
									if(response.responseText.indexOf("error.mesage.description")>=0){
										Sbi.exception.ExceptionHandler.handleFailure(response);
									} else {
										var r = Ext.util.JSON.decode(response.responseText);
																		
										if (Sbi.isValorized(r.results))
											this.store.loadData(r.results,true);
									}
								}
							} else {
								Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
							}
						},
						scope: this,
						failure: Sbi.exception.ExceptionHandler.handleFailure
					});				   
				}
		   }
		  
		  //ONLY FOR TEST
//		  if (this.store.data && this.store.data.items && this.store.data.items.length == 0){
//			  paramStore = [];
//			  var el = ['DS_CHART_SALES_COSTS', 'dataset', 'par_country', 'String'];
//			  paramStore.push(el);
//			  el = ['DS_CHART_SALES_COSTS', 'dataset', 'par_region', 'String'];
//			  paramStore.push(el);
//			  el = ['DS_CHART_SALES_COSTS', 'dataset', 'par_state', 'String'];
//			  paramStore.push(el);
//			  el = ['[ext-comp-1020]', 'widget', 'family', 'String'];
//			  paramStore.push(el);
//			  this.store.loadData(paramStore,false);
//		  }
		  //FINE TEST
		  
		Sbi.trace("[FilterEditorList.initStore]: OUT");
	}
	
    , initGrid: function() {
    	var thisPanel = this;
    	var c = this.gridConfig;
    	
        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        });
        
        var storeComboInitialValues = new Ext.data.ArrayStore({
	        fields: [
			            'comboFieldInitialType',
			            'comboFieldInitialValue'
			        ],
			        data : [ ['url1' , 'Param. 1'] , ['url2', 'Param. 2'] ]
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
		    storeComboInitialValues: storeComboInitialValues,
			displayField : 'comboScopeType', 
			valueField : 'comboScopeValue', 
			typeAhead : true, forceSelection : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true, 
			editable : false,
			allowBlank : false, 
			validationEvent : false,	
			queryMode: 'local'
			});
        comboScope.on('change', this.onScopeChange, this);
        
//        var textFieldInitialValue = new Ext.form.TriggerField({
        var comboInitialValue = new Ext.form.ComboBox({
//        	triggerCls: 'x-form-search-trigger' //lente
            onTriggerClick : this.onSelectInitialValue
//          , currentFilter: this.currentFilter
          , store: storeComboInitialValues
          , originalStore: storeComboInitialValues
          , displayField : 'comboFieldInitialType' 
		  , valueField : 'comboFieldInitialValue'
        });
        
        comboInitialValue.on('focus', this.onFocusInitialValue, this);

        var fixedCellRender = function (value, metaData, record, rowIndex, colIndex, store, view) {
           if (colIndex <5 ){
                metaData.attr = 'style="background-color:#f3f3f3 !important;"';
           }
            return value
        };
        
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
            	, renderer: fixedCellRender
            	, style: 'font-weight:bold;'
            	}, {
        		  header: LN('sbi.cockpit.filter.editor.wizard.list.typeObj')
            	, width: "15%"
            	, sortable: true
            	, dataIndex: 'typeObj'
            	, renderer: fixedCellRender
            	, style: 'font-weight:bold;'
            	},{
          		  header: LN('sbi.cockpit.filter.editor.wizard.list.namePar')
              	, width: "15%"
              	, sortable: true
              	, dataIndex: 'namePar'
              	, renderer: fixedCellRender
              	, style: 'font-weight:bold;'
              	},{
          		  header: LN('sbi.cockpit.filter.editor.wizard.list.typePar')
              	, width: "15%"
              	, sortable: true
              	, dataIndex: 'typePar'
              	, renderer: fixedCellRender
              	, style: 'font-weight:bold;'
              	},{
          		  header: LN('sbi.cockpit.filter.editor.wizard.list.typeDriver')
              	, width: "15%"
              	, sortable: true
              	, dataIndex: 'typeDriver'
              	, editor: comboScope        
              	, style: 'font-weight:bold;'
                },{
        		  header: LN('sbi.cockpit.filter.editor.wizard.list.initialValue')
            	, width: "15%"
            	, sortable: true
            	, dataIndex: 'initialValue'
            	, editor: comboInitialValue
            	, style: 'font-weight:bold;'
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
	        },
	        plugins: [cellEditing]
	    }));
        this.grid.on('itemclick', this.onCellClick, this);
        this.grid.on('edit', function(editor, e) {
            // commit the changes right after editing finished
            e.record.commit();
        }, this);
    }    

    
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------	

   
    
	// -----------------------------------------------------------------------------------------------------------------
    // utility methods
	// -----------------------------------------------------------------------------------------------------------------	        
    , onSelectInitialValue: function(){
//    	if (Sbi.isValorized(this.currentFilter)) {
//    		if (this.currentFilter.typeDriver === 'Relative' && this.store.getCount() == 0){
//    			alert('Il documento che si sta gestendo non ha driver analitici associati. \n'+
//    		      'Associare Driver Analitici al documento oppure definire filtri solo di tipo STATICO ! ');
//    		}else if (this.currentFilter.typeDriver !== 'Relative' ){
//    			this.store.loadData([],false); //this is the combo/trigger
//    		}else{
//    			this.store.loadData(storeComboFieldInitialValue.data,false);
//    		}
//    	}
    	
    	if (Sbi.isValorized(this.currentFilter)) {
    		if (this.currentFilter.typeDriver === 'Relative'){
    			alert('Il documento che si sta gestendo non ha driver analitici associati. \n'+
    		      'Associare Driver Analitici al documento oppure definire filtri solo di tipo STATICO ! ');
    		}
    	}
    }
    
    /**
	 * @method (listener)
	 * 
	 */    
    , onCellClick: function(grid, record, item, index, e, opt){
    	this.currentFilter = record.data;
    }

    /**
	 * @method (listener)
	 * Update the currentFilter informations for the trigger object
	 * 
	 */ 
    , onFocusInitialValue: function(trigger,event, obj){
    	trigger.currentFilter = this.currentFilter;
    }
    
//    , onScopeChange: function(combo, newValue, oldValue, opts){
//    	if (newValue == 'Static'){
//    		//reset initial values combo    
//    		this.fireEvent('updateInitialValues', []);
//    	}else{
//    		//reload initial values combo for relative case
//    		this.fireEvent('updateInitialValues', combo.storeComboInitialValues)
//    	}
//    	
//    }
});