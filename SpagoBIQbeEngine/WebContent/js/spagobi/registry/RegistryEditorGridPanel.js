/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
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
  * [list]
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.registry");

Sbi.registry.RegistryEditorGridPanel = function(config) {
	
	var defaultSettings = {
	};
			
	if(Sbi.settings && Sbi.settings.registry && Sbi.settings.registry.registryEditorGridPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.registry.registryEditorGridPanel);
	}
			
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.services = this.services || new Array();	
	this.services['load'] = this.services['load'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'LOAD_REGISTRY_ACTION'
		, baseParams: new Object()
	});
	this.services['getFieldDistinctValues'] = this.services['getFieldDistinctValues'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_FILTER_VALUES_ACTION'
		, baseParams: new Object()
	});
	this.services['update'] = this.services['update'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'UPDATE_RECORDS_ACTION'
		, baseParams: new Object()
	});
	
	this.init();
	
	var initialColumnModel = new Ext.grid.ColumnModel({
		columns:[
		new Ext.grid.RowNumberer(), 
		{
			header: "Data",
			dataIndex: 'data',
			width: 75
		}
		]

	});
	
	c = Ext.apply(c, {
		//height : 500
		autoScroll : true
    	, store : this.store
    	, tbar : this.gridToolbar
        , cm : initialColumnModel
        , clicksToEdit : 1
        , style : 'padding:10px'
        , frame : true
        , border : true
        , collapsible : false
        , loadMask : true
        , enableHdMenu : false ///set true to eneable drob down menu on the header (which causes the loss of mouse control for resize column....)
        , viewConfig : {
            forceFit : false
            , autoFill : true
            , enableRowBody : true
        }
		,listeners: {headerdblclick : function( grid, columnIndex, e ){
			this.showExpandPointer(grid, columnIndex);
		}
		} 
	});
	
	// constructor
	Sbi.formviewer.DataStorePanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.registry.RegistryEditorGridPanel, Ext.grid.EditorGridPanel, {
    
    services: null
	, store: null
	, registryConfiguration : null
	, gridToolbar : null
	, filters : null
	, columnName2columnHeader : null
	, columnHeader2columnName : null
	, keyUpTimeoutId : null
	, mandatory: null
	, visibleColumns: []
	, columnsMaxSize: 150
    
	// ---------------------------------------------------------------------------------------------------
    // public methods
	// ---------------------------------------------------------------------------------------------------
	,renderTooltip:function(val, cell, record) {
		 
		// get data
		var data = record.data;
		 
		// return markup
		return '<div qtip="' + val +'">' + val + '</div>';
		}
	,
	showExpandPointer: function(grid, columnIndex){
		grid.getColumnModel().setColumnWidth( columnIndex, this.columnsMaxSize, false ) 
	}
	,
	load:  function() {
		this.store.load({});
	}
  	
	// ---------------------------------------------------------------------------------------------------
	// private methods
	// ---------------------------------------------------------------------------------------------------
	
	,
	init : function () {
		this.initStore();
		this.initToolbar();
		Ext.QuickTips.init();

	}

	,
	initStore: function() {
		
		var proxy = new Ext.data.HttpProxy({
	           url: this.services['load']
	           , timeout : 300000
	   		   , failure: this.onDataStoreLoadException
	    });
		
		this.store = new Ext.data.Store({
	        proxy: proxy,
	        reader: new Ext.data.JsonReader(),
	        remoteSort: false
	    });
		
		this.store.on('load', function(store, records, options ){
			var numRec = this.store.getCount();
			
			//redefines the columns labels if they are dynamics
			var tmpMeta = this.getColumnModel();
			var fields = tmpMeta.config;
			var metaIsChanged = false;
			var fieldsMap = {};
			tmpMeta.fields = new Array(fields.length);

			for(var i = 0, len = fields.length; i < len; i++) {				
				
				for(k=0; k<numRec; k++){
					var tmpRec = this.store.getAt(k);
					if (tmpRec !== undefined) {
						
						var valorig =  tmpRec.json[fields[i].header];

						
						if(fields[i].type === 'int'){	
						
					    	if (valorig !== undefined){	
					    		if(valorig === ''){
					    			tmpRec.data[fields[i].header] = valorig;
					    			tmpRec.commit();
					    		}
					    		if(valorig === '0'){
					    			tmpRec.data[fields[i].header] = '0';
					    			tmpRec.commit();
					    		}
					    		
					    	}
						}
					}
			    }
			}
		}, this);
		this.store.on('metachange', function( store, meta ) {
			
			this.columnName2columnHeader = {};
			this.columnHeader2columnName = {};
			var columnVisible = [];
			if(meta.maxSize != null && meta.maxSize !== undefined){
				this.columnsMaxSize = parseInt(meta.maxSize);
			}
			
			for(var i = 0; i < meta.fields.length; i++) {
				
				this.columnName2columnHeader[meta.fields[i].name] = meta.fields[i].header;
				this.columnHeader2columnName[meta.fields[i].header] = meta.fields[i].name;
				
				var col = meta.fields[i].name;
				for(var j = 0; j < meta.columnsInfos.length; j++) {
					if(meta.columnsInfos[j].sizeColumn !== undefined && meta.columnsInfos[j].sizeColumn == meta.fields[i].name){
						meta.fields[i].width = meta.columnsInfos[j].size;
					}
					if(meta.columnsInfos[j].unsigned !== undefined && meta.columnsInfos[j].sizeColumn == meta.fields[i].name){
						meta.fields[i].unsigned = meta.columnsInfos[j].unsigned;
					}
				}
			    if(meta.fields[i].type) {
				   var t = meta.fields[i].type;
				   if (t ==='float') { // format is applied only to numbers
					   var format = Sbi.qbe.commons.Format.getFormatFromJavaPattern(meta.fields[i].format);
					   var f = Ext.apply( Sbi.locale.formats[t], format);
					   meta.fields[i].renderer = Sbi.qbe.commons.Format.floatRenderer(f);
				
				   }else{
					   if(t ==='int'){
						   meta.fields[i].renderer = Sbi.locale.formatters['string']; 
					   }else{
						   //meta.fields[i].renderer = Sbi.locale.formatters[t];
						   meta.fields[i].renderer = this.renderTooltip.createDelegate(this);
					   }
				   }   
			   }
			   
			   if(meta.fields[i].subtype && meta.fields[i].subtype === 'html') {
				   meta.fields[i].renderer  =  Sbi.locale.formatters['html'];
			   }
			   if(meta.fields[i].subtype && meta.fields[i].subtype === 'timestamp') {
				   meta.fields[i].renderer  =  Sbi.locale.formatters['timestamp'];
			   }
			   
			   if(this.sortable === false) {
				   meta.fields[i].sortable = false;
			   } else {
				   if(meta.fields[i].sortable === undefined) { // keep server value if defined
					   meta.fields[i].sortable = true;
				   }
			   }

			   var editor = this.getEditor(meta.fields[i].header, meta.fields[i].type);
			   if (editor != null) {

				   meta.fields[i].editor = editor;
			   }
			   
			   var config = this.getColumnEditorConfig(meta.fields[i].header);
			   if(config.visible == false){
					continue;
			   }else{		
				   this.visibleColumns.push(meta.fields[i]);
			   }
			   
			   meta.fields[i].allowBlank = true;

		   }

		   this.visibleColumns[0] = new Ext.grid.RowNumberer();
		   this.getColumnModel().setConfig(this.visibleColumns);
		   
		   this.mandatory = meta.mandatory;


		   this.on('beforeedit', function(e){			   
			   
			   /*
			    grid - This grid
			    record - The record being edited
			    field - The field name being edited
			    value - The value for the field being edited.
			    row - The grid row index
			    column - The grid column index
			    cancel - Set this to true to cancel the edit or return false from your handler.
				*/
			    var val = e.value;
			    var valorig = e.record.json[e.field];
			    var t = this.visibleColumns[e.column].type;
			    var st = this.visibleColumns[e.column].subtype;
			    if(Ext.isDate(val) ){
			    	if(st != null && st !== undefined && st === 'timestamp'){
			    		e.record.data[e.field] = Sbi.qbe.commons.Format.date(val, Sbi.locale.formats['timestamp']);
			    	}else{
			    		e.record.data[e.field] = Sbi.qbe.commons.Format.date(val, Sbi.locale.formats['date']);
			    	}
			    }
			    else if(Ext.isNumber(val)){
			    	if(t === 'float'){
			    		e.record.data[e.field] = Sbi.qbe.commons.Format.number(val, Sbi.locale.formats['float']);
			    	}
			    	if(t === 'int' && valorig == ''){
			    		e.record.data[e.field] = valorig;
			    	}
			    }
			    return true;
		   }, this);
		   this.on('afteredit', function(e) {
			   
			      /*grid - This grid
				    record - The record being edited
				    field - The field name being edited
				    value - The value being set
				    originalValue - The original value for the field, before the edit.
				    row - The grid row index
				    column - The grid column index*/
				
			   var t = this.visibleColumns[e.column].type;
			   var st = this.visibleColumns[e.column].subtype;
			   if (t === 'date') {
				   var dt = new Date(Date.parse(e.value));
				   e.record.data[e.field] = dt;
			   }
			   if (t === 'float') {
				   //replace , wirth .
				   var dottedVal = e.value.replace(',', '.');
				   var f = parseFloat(dottedVal);
				   e.record.data[e.field] = f;

			   }
			 }, this);
		   
		   this.on('validateedit', function(e) {
	    	   var t = this.visibleColumns[e.column].type;
			   var st = this.visibleColumns[e.column].subtype;
			   
			   var unsigned = this.visibleColumns[e.column].unsigned;

			   if(t === 'float'){
					   var dottedVal = e.value.replace(',', '.');
					   var isfloat = isFloat(dottedVal);

					   if(!isfloat){
						   if(e.value == ''){
							   //removed
							   e.value = NaN;
							   return;
						   }
						   e.cancel = true;
						   Ext.MessageBox.show({
								title : LN('sbi.registry.registryeditorgridpanel.saveconfirm.title'),
								msg : LN('sbi.registry.registryeditorgridpanel.validation'),
								buttons : Ext.MessageBox.OK,
								width : 300,
								icon : Ext.MessageBox.INFO
							}); 
					   }
			   }

			   if(t === 'int'){
				   var isInt = isInteger(e.value);
				   
				   if(unsigned){
					   //only positive numbers
					   isInt = isUnsignedInteger(e.value);
					   
					   if(!isInt){
						   if(e.value == ''){
							   //removed
							   e.value = NaN;
							   return;
						   }
						   e.cancel = true;
						   Ext.MessageBox.show({
								title : LN('sbi.registry.registryeditorgridpanel.saveconfirm.title'),
								msg : LN('sbi.registry.registryeditorgridpanel.validation.unsigned'),
								buttons : Ext.MessageBox.OK,
								width : 300,
								icon : Ext.MessageBox.INFO
							});
						   return;
					   }
					   
				   }

				   if(!isInt){
					   if(e.value == ''){
						   //removed
						   e.value = NaN;
						   return;
					   }
					   e.cancel = true;
					   Ext.MessageBox.show({
							title : LN('sbi.registry.registryeditorgridpanel.saveconfirm.title'),
							msg : LN('sbi.registry.registryeditorgridpanel.validation'),
							buttons : Ext.MessageBox.OK,
							width : 300,
							icon : Ext.MessageBox.INFO
						}); 
				   }
			   }

		 }, this);

		}, this);

	}

	,
	onDataStoreLoadException: function(response, options) {
		Sbi.exception.ExceptionHandler.handleFailure(response, options);
	}
	
	,
	getEditor : function (field, type) {
		var toReturn = null;
		var editorConfig = this.getColumnEditorConfig(field);

		if (editorConfig.editable == true) {
			if (editorConfig.editor == "COMBO") {
				
				if(type === 'boolean'){
					toReturn = this.createFieldBoolean(field);
				}else{
					toReturn = this.createFieldCombo(field);
				}
			} else {
				toReturn = new Ext.form.TextField();
			}
		}
		return toReturn;
	}

	,
	getColumnEditorConfig : function (field) {

		var columnsConf = this.getColumnsConfiguration();
		var toReturn = {  // default values
				editable : true
				, visible : true
		};
		for (var i = 0; i < columnsConf.length; i++) {
			if (columnsConf[i].field == field) {
				toReturn = Ext.apply(toReturn, columnsConf[i]);
				break;
			}
		}
		return toReturn;
	}
	
	,
	initToolbar : function () {
		var items = this.initFiltersToolbarItems();
		this.gridToolbar = new Ext.Toolbar(items);
	}
	
	,
	initFiltersToolbarItems : function () {
		var items = [];
		items.push({
			iconCls : 'icon-save',
			handler : this.save,
			scope : this
		});
		items.push({
			iconCls : 'icon-refresh',
			handler : this.refresh,
			scope : this
		});
		items.push({xtype: 'tbspacer', width: 30});
		items.push({
			iconCls : 'icon-clear',
			handler : this.clearFilterForm,
			scope : this
		});
		this.filters = [];
		var filtersConf = this.getFiltersConfiguration();
		if (filtersConf.length > 0) {
			for (var i = 0 ; i < filtersConf.length ; i++) {
				var aFilter = filtersConf[i];
				items.push({xtype: 'tbtext', text: aFilter.title, style: {'padding-left': 20}});
				var filterField = this.createFilterField(aFilter);
				items.push(filterField);
				this.filters.push(filterField); // save filters into local variable this.filters
			}
		}

		return items;
	}
	
	,
	getFiltersConfiguration : function () {
		var toReturn = [];
		if (this.registryConfiguration != undefined && this.registryConfiguration != null) {
			toReturn = this.registryConfiguration.filters;
		}
		return toReturn;
	}
	
	,
	getColumnsConfiguration : function () {
		var toReturn = [];
		if (this.registryConfiguration != undefined && this.registryConfiguration != null) {
			toReturn = this.registryConfiguration.columns;
		}
		return toReturn;
	}
	
	,
	getColumnConfiguration : function (field) {
		var columns = this.getColumnsConfiguration();
		for (var i = 0; i < columns.length; i++) {
			if (columns[i].field == field) {
				return columns[i];
			}
		}
		return null;
	}
	
	,
	createFilterField : function (aFilter) {
		var filterField = null;
		if (aFilter.presentation != undefined && aFilter.presentation == "COMBO") {
			filterField = this.createFieldCombo(aFilter.field);
			filterField.type = "COMBO";
			filterField.on('change', this.filterMainStore, this);
		} else {
			filterField = new Ext.form.TextField({
				name: aFilter.field
				, enableKeyEvents : true
				, listeners : {
					keyup : this.setKeyUpTimeout
					, scope: this
				}
			});
			filterField.type = "TEXT";
		}
		return filterField;
	}
	,
	createFieldBoolean: function(field) {

		var combo = new Ext.form.ComboBox({
			name: field
            , editable : false
            , store: new Ext.data.SimpleStore({
            	fields: ['column_1'],
                data: [['true'], ['false']]
            })
	        , displayField: 'column_1'
	        , valueField: 'column_1'
	        , mode:'local'
	        , triggerAction: 'all'
        });
		
		return combo;
	}
	,
	createFieldCombo: function(field) {
		var store = new Ext.data.JsonStore({
			url: this.services['getFieldDistinctValues']
		});
		var temp = this.registryConfiguration.entity;
		var index = this.registryConfiguration.entity.indexOf('::');
		if (index != -1) {
			temp = this.registryConfiguration.entity.substring(0 , index);
		}
		var entityId = null;
		var column = this.getColumnConfiguration(field);
		if (column.subEntity) {
			entityId = temp + "::" + column.subEntity + "(" + column.foreignKey + ")" + ":" + field;
		} else {
			entityId = temp + ':' + field;
		}

		var baseParams = {
			'QUERY_TYPE': 'standard', 
			'ENTITY_ID': entityId, 
			'ORDER_ENTITY': entityId, 
			'ORDER_TYPE': 'asc', 
			'QUERY_ROOT_ENTITY': true
		};
		store.baseParams = baseParams;
		store.on('loadexception', function(store, options, response, e) {
			Sbi.exception.ExceptionHandler.handleFailure(response, options);
		});
		
		var combo = new Ext.form.ComboBox({
			name: field
            , editable : false
            , store: store
	        , displayField: 'column_1'
	        , valueField: 'column_1'
	        , triggerAction: 'all'
        });
		
		return combo;
	}	
	
	,
	setKeyUpTimeout : function () {
        clearTimeout(this.keyUpTimeoutId);
        this.keyUpTimeoutId = (function() {
	          this.keyUpTimeoutId = null;
	          this.filterMainStore();
	    }).defer(500, this);
	}
	
	,
	filterMainStore : function () {
		var filtersValuesObject = this.getFiltersValues();
		var filterFunction = this.createFilterFunction(filtersValuesObject);
		this.store.filterBy(filterFunction);
	}
	
	,
	getFiltersValues : function () {
		var filtersValuesObject = {};
		for (var i = 0 ; i < this.filters.length ; i++) {
			var aFilter = this.filters[i];
			filtersValuesObject[aFilter.getName()] = {
				value : aFilter.getValue()
				, type : aFilter.type
			};
		}
		return filtersValuesObject;
	}
	
	,
	createFilterFunction : function (filtersValuesObject) {
		var columnHeader2columnName = this.columnHeader2columnName;
		var filterFunction = function (record, recordId) {
			for (var aFilterName in filtersValuesObject) {
				var filterObject = filtersValuesObject[aFilterName];
				// filter name corresponds to the column header, so we retrieve the relevant column name
				var columnName = columnHeader2columnName[aFilterName];
				var fieldValue = record.get(columnName).toString();
				var filterType = filterObject.type;
				var filterValue = filterObject.value;
				var fieldCompareValue = null;
				if (filterType == 'COMBO') {
					fieldCompareValue = fieldValue;
					if (filterValue == '') {
						continue;
					}
				} else {
					filterValue = filterValue.toUpperCase();
					fieldCompareValue = fieldValue.substring(0, filterValue.length).toUpperCase();
				}
				if (filterValue != fieldCompareValue) {
					return false;
				}
			}
			return true;
		};
		return filterFunction;
	}
	
	,
	clearFilterForm: function () {
		for (var i = 0 ; i < this.filters.length ; i++) {
			var aFilter = this.filters[i];
			if (aFilter.type == "COMBO") {
				aFilter.clearValue();
			} else {
				aFilter.setValue('');
			}
		}
		this.store.clearFilter(false);
	}
	, hasMandatoryColumnRespected: function(aRecordData){
		var ok = '';
		for(i =0; i<this.mandatory.length; i++){
			var columnToCheck = this.mandatory[i].column;
			var col = aRecordData[columnToCheck];
			if(col === undefined || col == null || col === ''){
				var columnRef = this.mandatory[i].mandatoryColumn;
				var valueRef = this.mandatory[i].mandatoryValue;
				var value = aRecordData[columnRef];
				if(value !== undefined && value !== null && value === valueRef){
					return columnToCheck;					
				}
			}
		}
		return ok;
	}
	,
	saveSingleRecord : function (index, modifiedRecords) {
		var recordsData = [];

		if(index<modifiedRecords.length){

			var aRecordData = Ext.apply({}, modifiedRecords[index].data);
			delete aRecordData.recNo; // record number is not something to be persisted
			recordsData.push(aRecordData);
			var colMandatory= this.hasMandatoryColumnRespected(aRecordData);
			if(colMandatory !== ''){
				Ext.MessageBox.show({
					title : LN('sbi.registry.registryeditorgridpanel.saveconfirm.title'),
					msg : colMandatory +" "+LN('sbi.registry.registryeditorgridpanel.mandatory'),
					buttons : Ext.MessageBox.OK,
					width : 300,
					icon : Ext.MessageBox.INFO
				});

				return;
			}
			Ext.Ajax.request({
				url: this.services['update'],
				method: 'post',
				params: {"records" : Sbi.commons.JSON.encode(recordsData)},
				success : this.saveSingleRecord.createDelegate(this, [index + 1, modifiedRecords], false),
				failure: function(msg, title){
					for(var j=0; j<index;j++){
						modifiedRecords[0].commit();
					}
					Ext.MessageBox.show({
						title : LN('sbi.registry.registryeditorgridpanel.saveconfirm.title'),
						msg : LN('sbi.registry.registryeditorgridpanel.saveconfirm.message.ko'),
						buttons : Ext.MessageBox.OK,
						width : 300,
						icon : Ext.MessageBox.INFO
					});
				},
				scope: this
			});
		}else{
			this.updateSuccessHandler();
		}
	}
	
	,
	save: function () {
		var modifiedRecords = this.store.getModifiedRecords();
		this.saveSingleRecord(0,modifiedRecords);
	}
	,
	refresh: function () {
		this.view.refresh();

	}
	,
	updateSuccessHandler : function () {
		Ext.MessageBox.show({
			title : LN('sbi.registry.registryeditorgridpanel.saveconfirm.title'),
			msg : LN('sbi.registry.registryeditorgridpanel.saveconfirm.message'),
			buttons : Ext.MessageBox.OK,
			width : 300,
			icon : Ext.MessageBox.INFO
		});
		this.store.commitChanges();
	}
	
});
function isInteger(s) {
	  return (s.search(/^-?[0-9]+$/) == 0);
}
function isUnsignedInteger(s) {
	  return (s.search(/^[0-9]+$/) == 0);
}
function isFloat(s){
	return (s.search(/^[0-9]*[.][0-9]+$/) == 0);
}