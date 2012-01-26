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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors
 *  - Davide Zerbetto (davide.zerbetto@eng.it)
 */

Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeSheetFiltersInfoPanel = function(config) {
	
	var defaultSettings = {
		title : LN('sbi.worksheet.runtime.runtimesheetfiltersinfopanel.title')
		, valuesSeparator : ", "
		, style : 'padding: 15px'
		, frame : true
	};
	
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime && Sbi.settings.worksheet.runtime.runtimeSheetFiltersInfoPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeSheetFiltersInfoPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.baseConfig = c;

	this.init();
	
	c = Ext.apply(c, {
		store : this.store
		, cm : this.cm
		, sm : this.sm
	    , width : 600
	    , height : 200
		, viewConfig: {
			forceFit : true
        	, emptyText : LN('sbi.worksheet.runtime.runtimesheetfiltersinfopanel.empty')
        	, deferEmptyText : false
		}
	}); 
	
	// constructor
    Sbi.worksheet.runtime.RuntimeSheetFiltersInfoPanel.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetFiltersInfoPanel, Ext.grid.GridPanel, {
    
	store : null
	, filtersInfo : null // must be in the constructor input object
	, Record: Ext.data.Record.create([
	      {name: 'id', type: 'string'}
	      , {name: 'values', type: 'string'}
	])
	   
	// private methods
	   
	,
	init: function() {
		this.store = new Ext.data.SimpleStore({
			 fields : ['name', 'values']
		     , data : this.filtersInfo
		});
		this.cm = new Ext.grid.ColumnModel([
			 {
				 header: LN('sbi.worksheet.runtime.runtimesheetfiltersinfopanel.columns.attribute') 
				 , dataIndex: 'name'
				 , width: 50
				 , renderer: this.columnRenderer
			 },
			 {
				 header: LN('sbi.worksheet.runtime.runtimesheetfiltersinfopanel.columns.values') 
				 , dataIndex: 'values'
				 , renderer: this.columnRenderer
			 }
 	    ]);
		this.sm = new Ext.grid.RowSelectionModel({singleSelect:true})
	}

	,
	columnRenderer: function (value, metadata, record) {
	 	var tooltipString = value;
	 	if (tooltipString !== undefined && tooltipString != null) {
	 		metadata.attr = ' ext:qtip="'  + tooltipString + '"';
	 	}
	 	return value;
	}

	
	// public methods

	,
	getOriginalValue : function (fieldName) {
		for (var i = 0 ; i < this.filtersInfo.length ; i++) {
			var data = this.filtersInfo[i];
			if (data[0] == fieldName) {
				return data[1];
			}
		}
		return null;
	}
	
	,
	update : function (filtersInfo) {
		for (var c in filtersInfo) {
			var newValuesArray = filtersInfo[c];
			var newValuesJoined = null;
			if (Sbi.qbe.commons.Utils.isEmpty(newValuesArray)) {
				newValuesJoined = this.getOriginalValue(c);
			} else {
				newValuesJoined = newValuesArray.join(this.valuesSeparator);
			}
			var index = this.store.findExact('name', c);
			if (index != -1) {
				// existing record
				if (Sbi.qbe.commons.Utils.isEmpty(newValuesJoined)) {
					// record is present but must be removed
					this.store.removeAt(index);
				} else {
					// modify record
					var record = this.store.getAt(index);
					record.set('values', newValuesJoined);
				}
			} else {
				// non existing record
				if (Sbi.qbe.commons.Utils.isEmpty(newValuesJoined)) {
					continue;
				} else {
					// add new record
					var newRecord = new this.Record({
						name : c
						, values : newValuesJoined
					});
					this.store.add(newRecord);
				}
			}
			this.store.commitChanges();
		}
	}
	
});