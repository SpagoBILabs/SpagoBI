/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
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
  *  [list]
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.widgets");

Sbi.widgets.SimpleValuesChooserWindow = function(config) {
	
	var defaultSettings = {
		singleSelect : false
	};
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		title		: LN('sbi.lookup.Select'),   
        layout      : 'fit',
        width       : 500,
        height      : 300,
        plain       : true,
        items       : [this.grid]
 	});
	
	// constructor
	Sbi.widgets.SimpleValuesChooserWindow.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.widgets.SimpleValuesChooserWindow, Ext.Window, {

	sm 		: null
	, cm 	: null
	, store : null // this must be in the constructor input object and it must contain a column named 'Values'
	
	,
	init : function () {
 		this.sm = new Ext.grid.CheckboxSelectionModel( {singleSelect: this.singleSelect } );
 		
     	this.cm = new Ext.grid.ColumnModel([
		   new Ext.grid.RowNumberer(),
	       {
	       	  header: "Values",
	          dataIndex: 'Values',
	          width: 75
	       },
	       this.sm
	    ]);
 		
 		this.grid = new Ext.grid.GridPanel({
 			store 			: this.store
	     	, cm 			: this.cm
	     	, sm 			: this.sm
	     	, frame 		: false
	     	, border 		: false  
	     	, collapsible 	: false
	     	, loadMask		: true
	     	, viewConfig: {
	        	forceFit : true
	        	, enableRowBody : true
	        	, showPreview : true
	     	}
 		});
	}

	,
	getSelectedValues : function () {
		var selectedRecords = this.sm.getSelections();
		var toReturn = [];
		for (var i = 0; i < selectedRecords.length; i++) {
			var aRecord = selectedRecords[i];
			toReturn.push(aRecord.data.Values);
		}
		return toReturn;
	}
	
	,
	select : function (values) {
		var records = [];
		this.store.each(function (aRecord) {
			if (values.indexOf(aRecord.data.Values) != -1) {
				records.push(aRecord);
			}
		}, this);
		this.sm.selectRecords(records, false);
	}
	
});