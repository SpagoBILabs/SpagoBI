/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Andrea Gioia (andrea.gioia@eng.it)
  * - Antonella Giachino (antonella.giachino@eng.it)
  */

Ext.ns("Sbi.console");

Sbi.console.FilteringToolbar = function(config) {

	var defaultSettings = {
	    autoWidth: true
	  , width:'100%'
	  , filters: {}
	};
		
	if(Sbi.settings && Sbi.settings.console && Sbi.settings.console.filteringToolbar) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.console.filteringToolbar);
	}

	
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
	
	this.services = this.services || new Array();	
	this.services['createexportfile'] = this.services['createexportfile'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'CREATE_EXPORT_FILE_ACTION'
		, baseParams: new Object()
	});
	this.services['getexportfile'] = this.services['getexportfile'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_TEMPORARY_FILE_ACTION'
		, baseParams: new Object()
	});
	// constructor
	Sbi.console.FilteringToolbar.superclass.constructor.call(this, c);
	
	this.addEvents('beforefilterselect');

};

Ext.extend(Sbi.console.FilteringToolbar, Ext.Toolbar, {
    
	services: null
	, store: null
	, filterStores: null	
	, cbFilters: null
	, buttons: []


	
	// -- public methods -----------------------------------------------------------------
    
    
	// -- private methods ---------------------------------------------------------------
	, onRender : function(ct, position) { 
		Sbi.console.FilteringToolbar.superclass.onRender.call(this, ct, position);		     
	}

	//adds action buttons
	, addActionButtons: function(){
  	    var b;
  	    var conf = {}; 
        conf.executionContext = this.filterBar.executionContext;
        var exportName = this.filterBar.exportName;
        conf.store = this.store;
        conf.refreshDataAfterAction = this.filterBar.refreshDataAfterAction;
		this.addFill();
		if (this.filterBar.actions){
      		for(var i=0; i < this.filterBar.actions.length; i++){
      		   if (this.filterBar.actions[i].type == undefined) this.filterBar.actions[i].type = this.filterBar.actions[i].name;
      		   conf.actionConf = this.filterBar.actions[i];
      		   conf.actionConf.tooltip = Sbi.locale.getLNValue(conf.actionConf.tooltip);
      		   conf.actionConf.tooltipInactive = Sbi.locale.getLNValue(conf.actionConf.tooltipInactive);
      		   conf.actionConf.tooltipActive = Sbi.locale.getLNValue(conf.actionConf.tooltipActive);
      		   conf.actionConf.msgConfirm = Sbi.locale.getLNValue(conf.actionConf.msgConfirm);
    		   b = new Sbi.console.ActionButton(conf);
    		   b.on('toggleIcons', this.onToggleIcons, this);
        	   this.addButton(b);	
        	   this.buttons.push(b);        	  
        	}	
        }
		//adds export button
		var menuItems = new Array();
		var types = new Array();		
		types.push('XLS');
		types.push('CSV');
		//types.push('PDF'); //to be implemented
		var menuBtn = null;
		if(types.length != 1){
			for(k=0; k< types.length; k++){
				var type = types[k];							
				var iconname = 'icon-'+type.toLowerCase();
				
				var itemExp = new Ext.menu.Item({
		            text: type
		            , group: 'group_2'
		            , iconCls: iconname 
					, width: 15
					, scope:this
					, docType : type
					, href: ''
					, exportName: exportName
		        });
				itemExp.addListener('click', this.exportConsole, this, type);
				menuItems.push(itemExp); 
		 
			}
			var menu0 = new Ext.menu.Menu({
				id: 'basicMenu_0',
				items: menuItems    
				});	
			
			menuBtn = new Ext.Toolbar.Button({
				tooltip: 'Exporters'
				, path: 'Exporters'	
				, iconCls: 'icon-export' 	
	            , menu: menu0
	            , width: 15
	            , cls: 'x-btn-menubutton x-btn-text-icon bmenu '
	        });

		}else{
			var type = types[0];	
			var iconname = 'icon-'+type.toLowerCase();
			menuBtn = new Ext.Toolbar.Button({
                text: type
                , group: 'group_2'
                , iconCls: iconname 
		     	, scope: this
				, width: 15
				, href: ''
				, docType : type
			});			
			menuBtn.addListener('click', this.exportConsole, this, type);
		}
 	    this.addButton(menuBtn);
	}
	, exportConsole: function(item) {
		var format = item.docType;
		var exportName= item.exportName;
	
		var gridConsole = this.ownerCt;
		var columnConfigs = gridConsole.columnConfig;
		
		//gets colModel to retrieve order
		var colModArray = gridConsole.colModel.fields;
		
		//gets store reader metadata to retrieve dataIndex
		var storeMetaArray = gridConsole.store.reader.meta.fields;

		//different dataset to export
		var dsExport;
		var metaCols = new Array();
		if (gridConsole.datasetExport != null){
			dsExport = gridConsole.datasetExport.datasetExp || "";
			var exportColumnConfig = gridConsole.datasetExport.columnConfig;
			
			for(p in exportColumnConfig) {
				var column = {};
				column[p] = exportColumnConfig[p];
				metaCols.push(column);    
			}
		}
		
		var dsHeadersLabel = (gridConsole.storeLabels !== undefined && gridConsole.storeLabels !== null)? gridConsole.storeLabels.dsLabel : "";
		
		//var meta = this.orderMetaColumns(colModArray, storeMetaArray , columnConfigs);
		var meta = new Array();
		for(p in columnConfigs) {
			var column = {};
			column[p] = columnConfigs[p];
			meta.push(column);    
		}
		
		
		if (gridConsole.datasetExport != null){
			dsExport = gridConsole.datasetExport.datasetExp || "";
			var exportColumnConfig = gridConsole.datasetExport.columnConfig;
			
			for(p in exportColumnConfig) {
				var column = {};
				column[p] = exportColumnConfig[p];
				metaCols.push(column);    
			}
		}
		var output = 'application/vnd.ms-excel';
		if(format == 'PDF'){
			output = 'application/pdf';
		}
		if(format == 'CSV'){
			output = 'text/csv';
		}
		if(exportName == undefined){
			exportName ='';
		}
		//check if specific datasource for export is definied
		var params = null;
		if (gridConsole.datasetExport != null){
			params = {
					mimeType: output
					//, responseType: 'attachment'
					, datasetLabel: dsExport
					, datasetHeadersLabel: dsHeadersLabel
					, meta: Ext.util.JSON.encode(metaCols)
					, exportName: exportName
				};
		}
		else {
			params = {
					mimeType: output
					//, responseType: 'attachment'
					, datasetLabel: gridConsole.store.dsLabel
					, datasetHeadersLabel: dsHeadersLabel
					, meta: Ext.util.JSON.encode(meta)
					, exportName: exportName
				};
		}
		
		this.ownerCt.stopLoading();
		this.ownerCt.showMask();
		
  		Ext.Ajax.request({
	       	url: this.services['createexportfile']			       
	       	, params: params 			       
	    	, success: this.onExportFileSuccess
	    	, failure: this.onExportFileFailure
	    	, scope: this     
	    });
		
	}
	
	,
	onExportFileSuccess : function (response, options) {
		this.ownerCt.hideMask();
		this.ownerCt.restartLoading();
		if (response !== undefined && response.responseText !== undefined) {
			var responseJson = Ext.util.JSON.decode( response.responseText );
			Sbi.Sync.request({
				url : this.services['getexportfile']
				, params : {
					name : responseJson.name
					, extension : responseJson.extension
				}
			});
		} else {
			Sbi.Msg.showError('Server response is empty', 'Service Error');
		}
	}
	
	,
	onExportFileFailure : function (response, options) {
		this.ownerCt.hideMask();
		this.ownerCt.restartLoading();
		Sbi.exception.ExceptionHandler.onServiceRequestFailure(response, options);
	}
	
	
	, orderMetaColumns : function(colModArray, storeMetaArray, columnConfig){
		var result = new Array();
		if(colModArray != null && colModArray !== undefined){
			for(i=0; i<colModArray.length; i++){
				var colItem = colModArray[i];
				if(colItem !== undefined && colItem.dataIndex !== ''){
					var header = colItem.header;
					for(j=0; j<storeMetaArray.length; j++){
						var storeItem = storeMetaArray[j];
						if(storeItem !== undefined && (colItem.dataIndex == storeItem.dataIndex)){
							var colName= storeItem.header;
							var metaObj = {};
							metaObj[colName] = columnConfig[colName];

							result.push(metaObj);
							break;
						}
					}
				}
			}
		}
		return result;
	}
	 //defines fields depending from operator type
	 , createFilterField: function(operator, header, dataIndex){
		   if (operator === 'EQUALS_TO') {
			   //single value
			   this.filterStores = this.filterStores || {}; 
			   this.cbFilters = this.cbFilters || {};
			   var s = new Ext.data.JsonStore({
				   fields:['name', 'value', 'description'],
		           data: []
			   });
			   this.filterStores[dataIndex] = s;
			 
			   //this.store.on('load', this.reloadFilterStore.createDelegate(this, [dataIndex]), this);
		     
			   var combDefaultConfig = {
					   width: 130,
				       displayField:'name',
				       valueField:'value',
				       typeAhead: true,
				       triggerAction: 'all',
				       emptyText:'...',
				       //selectOnFocus:true,
				       selectOnFocus:false,
				       validateOnBlur: false,
				       mode: 'local'
			   };
			   
			   
			   var cb = new Ext.form.ComboBox(
			       Ext.apply(combDefaultConfig, {
			    	   store: s,
				       index: dataIndex,
				       listeners: {
						   'select': {
						   		fn: function(combo, record, index) {
									var field = combo.index;
									var exp = record.get(combo.valueField);									
									this.onFilterSelect(field, exp);	
								},
								scope: this
							}
					   }
			       	})
			   );	

			   this.addText("    " + header + "  ");
			   this.addField(cb);	
			   //adds the combo field to a temporary array to manage the workaround on the opening on each refresh
			   this.cbFilters[dataIndex] = cb;
			   
	     }else if (operator === 'IN') {
	    	 //multivalue
	    	 this.filterStores = this.filterStores || {};	    	 
	    	 var smLookup = new Ext.grid.CheckboxSelectionModel( {singleSelect: false } );
	    	 var cmLookup =  new Ext.grid.ColumnModel([
		    	                                          new Ext.grid.RowNumberer(),		    	                                          
						                    		      {header: "Data", dataIndex: 'value', width: 75},
						                    		      smLookup
						                    		    ]);
	    	 var baseConfig = {
	    			     width: 130
				       , name : dataIndex
				       , emptyText:'...'
					   , allowBlank: true
					   , cm: cmLookup
					   , sm: smLookup
					};
	    	
	    	 var s = new Ext.ux.data.PagingJsonStore({	  
	    	//var s = new Ext.ux.data.PagingStore({	   
				   fields:['name', 'value', 'description'],
		           data: [],
		           lastOptions: {params: {start: 0, limit: 20}}
			   });
	    	
			 this.filterStores[dataIndex] = s;
			
	    	 var lk = new Sbi.console.LookupField(Ext.apply(baseConfig, {
				  	  store: s
					, params: {}
					, singleSelect: false
					, displayField: 'value'
					, valueField: 'value'
					, listeners: {
							   'select': {
							   		fn: function(values) {							   			
							   			var exp =  new Array();
										var field = dataIndex;
										
										for(var val in values){ 
											if (val !== '...'){
												exp.push(val);
											}else{
												exp =  new Array();
											}
										}										
										this.onFilterSelect(field, exp);	  
									},
									scope: this
								}				     					
						   }
			}));
	    	 
	    	this.addText("    " + header + "  ");
			this.addField(lk);	
			
	     }else {
	    	 Sbi.Msg.showWarning('Filter operator type [' + operator + '] not supported');
	     }
	  
	 }
	   
	 , reloadFilterStores: function() {
		for(var fs in this.filterStores) {
			this.reloadFilterStore(fs);
		}
		this.store.filterPlugin.applyFilters();
	}
	    
	 , reloadFilterStore: function(dataIdx) {
		 var distinctValues; 
		 var data;
      
		 var s = this.filterStores[dataIdx];
		
		 if(!s) {
		   Sbi.msg.showError('Impossible to refresh filter associated to column [' + dataIdx + ']');
		   return;
		 }
		 
		 this.store.clearFilter( true );
	   
		 distinctValues = this.store.collect(dataIdx, true, true);
		 data = [];
	   
		 //define the empty (for reset) element
	   	 var firstRow = {
	        name: '...'
		  , value: '...'
		  , description: ''
	   	 };
	    	data.push(firstRow);
      
	     for(var i = 0, l = distinctValues.length; i < l; i++) {
		   var row = {
			  name: distinctValues[i]
		    , value: distinctValues[i]
		    , description: distinctValues[i]
		   };
		   data.push(row);
	   	 }

	   	 // replace previous records with the new one
	   	 s.loadData(data, false);	   	 
	   	 
	   	 //WORKAROUND: when the user selects an item from the combo and stay on it, this combo is opened on each refresh.
	   	 //This workaround force the closure of the combo.
	   	 if (this.cbFilters !== null && this.cbFilters !== undefined){
		   	 var cb = this.cbFilters[dataIdx];
		   	 if (cb){
		   		 cb.collapse();
		   	 }
	   	 }
	}
   
	 //adds the single filter or delete if it's the reset field
	 , onFilterSelect: function(f, exp) { 
		 if(this.fireEvent('beforefilterselect', this, f, exp) !== false){	
			 if (exp === '...' || exp.length == 0){
				   this.store.filterPlugin.removeFilter(f);
			 }else{
			   if (!Ext.isArray(exp)){
				   var arExp =  new Array();
				   arExp.push(exp);
				   exp = arExp;
			   }
			   this.store.filterPlugin.addFilter(f, exp );
			 }
			 this.store.filterPlugin.applyFilters();
		 }
	 }
	 
	 , onToggleIcons: function(action, flgCheck){
		 var gridConsole = this.ownerCt;
		 var s = gridConsole.store;
		 var isDirty = false;			 
		 if (gridConsole.selectedRowsId == null) gridConsole.selectedRowsId = [];	
		 
		 if (action.actionConf.type == 'selectRow' || action.actionConf.type == 'unselectRow' || 
			 action.actionConf.type == 'invertSelectionRow' ){			 		 			
			 for(var i=0; i< s.getCount(); i++){
				 var record = s.getAt(i);
				 var value = record.get(s.getFieldNameByAlias(action.actionConf.columnID));
				 var isVisible = record.get(s.getFieldNameByAlias(action.actionConf.flagColumn));
				 var posValue = this.getPositionEl(value, gridConsole.selectedRowsId);
				 if (action.actionConf.type == 'selectRow'  && (isVisible == 1 || isVisible == 'true')){
					 if (posValue == -1){
						 isDirty = true;
						 gridConsole.selectedRowsId.push(value);
					 }
				 }else if (action.actionConf.type == 'unselectRow'){
					 if (posValue != -1){
						 isDirty = true;
						 delete gridConsole.selectedRowsId[posValue];
					 }
				 }else if (action.actionConf.type == 'invertSelectionRow' && (isVisible == 1 || isVisible == 'true')){
					 isDirty = true;
					 if ( posValue == -1){						 
						 gridConsole.selectedRowsId.push(value);						
					 }else{
						 delete gridConsole.selectedRowsId[posValue];
					 }		
				 }
				 
				 if (isDirty){
					 //update the check (fisical field to force a refresh of rendering)
					 gridConsole.isDirty = isDirty;
					 var newValue = (isVisible === 1 || isVisible === '1' ) ? 'true' : '1';
					 record.set (s.getFieldNameByAlias(action.actionConf.flagColumn), newValue );
					 isDirty = false;
				 }				
			 }			 
			 return;
		 }
		 
		//toggles all icons of the same family
		 for (var i=0, l= this.buttons.length; i<l; i++ ){
			var cleanCheckBox = false;
			var btn = this.buttons[i];
			if (btn.actionConf.type == action.actionConf.type && btn.actionConf.name !== action.actionConf.name){
				btn.setCheckValue(btn.actionConf.checkColumn, flgCheck, false);   
			}			
		}
		
    }
	 
   , getPositionEl: function(value, lst) {	    	
			//check if the row is in the listRowsSelected (pagination management)
	    	//returns the position element into the array 
	    	var toReturn = -1;    	
	    	
	    	if (lst == null) return toReturn;
	        
	    	for(var i=0; i<lst.length; i++) {
	    		if (lst[i] == value ){
	    			toReturn = i;
	    			break;
	    		}   		
	    	}
	    	return toReturn;	
	}
});