/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 

Ext.define('Sbi.cockpit.widgets.chart.SeriesPalette', {
	extend: 'Ext.Window'
	, layout:'fit'

	, config:{
		title: 'Palette'
	  , frame: true
	  , xtype: 'panel'
	  , border: false
	  , autoScroll: true
	}

	, grid : null
	, colorFieldEditor : null
	, colorColumn : null
	, defaultColors : Sbi.widgets.Colors.defaultColors	

	, constructor : function(config) {
		Sbi.trace("[SeriesPalette.constructor]: IN");
		this.initConfig(config);
		this.initStore();
		this.init(config);
		this.callParent(arguments);
		Sbi.trace("[SeriesPalette.constructor]: OUT");
	}
	
	, initComponent: function() {
  
		 Ext.apply(this, {
	            items: [this.grid]
	     });
        
        this.callParent();
    }
		
	, init: function(c) {
		this.initStore(c);
		this.initColumnModel(c);
		this.initGrid(c);
	}

	, initStore: function(c) {
		this.store =  new Ext.data.ArrayStore({
	        fields: ['color']
		});
		this.setColors(this.defaultColors);
	}
	
	, initColumnModel: function(c) {
		
		this.colorColumn = new Ext.grid.Column({
			header: ''
			, width: 60
			, dataIndex: 'color'
			, editor: new Ext.form.TextField({}) // only in order to make the column editable: the editor is built 
												 // on the grid's beforeedit event 
			, renderer : function(v, metadata, record) {
				metadata.attr = ' style="background:' + v + ';"';
				return '';  
	       }
		});
//	    this.cm = new Ext.grid.ColumnModel([this.colorColumn]);
	}
	
	, initGrid: function (c) {
		this.grid = new Ext.grid.EditorGridPanel({
	        store: this.store
	        , border: false
	        , columns: [{
	               header: ''
				, width: 60
				, dataIndex: 'color'
				, editor: new Ext.form.TextField({}) // only in order to make the column editable: the editor is built 
													 // on the grid's beforeedit event 
				, renderer : function(v, metadata, record) {
					metadata.attr = ' style="background:' + v + ';"';
					return '';  
		       }
	        }]
			, selModel: new Ext.selection.RowModel({})
//	        , cm: this.cm
//	        , sm: new Ext.grid.RowSelectionModel()
	        , enableDragDrop: true
	        , ddGroup: this.ddGroup || 'crosstabDesignerDDGroup'
		    , layout: 'fit'
		    , viewConfig: {
		    	forceFit: true
		    }
	        , listeners: {
				render: function(grid) { // hide the grid header
					grid.getView().el.select('.x-grid3-header').setStyle('display', 'none');
	    		}
	        	, beforeedit: {
	        		fn : function (e) {
	        	    	var t = Ext.apply({}, e);
	        			this.currentRowRecordEdited = t.row;
	        			var color = this.store.getAt(this.currentRowRecordEdited).data.color;
	        			var colorFieldEditor = new Ext.ux.ColorField({ value: color, msgTarget: 'qtip', fallback: true});
	        			colorFieldEditor.on('select', function(f, val) {
	        				this.store.getAt(this.currentRowRecordEdited).set('color', val);
	        			}, this);
	        			this.colorColumn.setEditor(colorFieldEditor);
	        		}
	        		, scope : this
	        	}
			}
		});
	}
	
	, getColors: function() {
		var colors = [];
		for(i = 0; i < this.store.getCount(); i++) {
			var record = this.store.getAt(i);
			var color = record.data.color;
			colors.push(color);
		}
		return colors;
	}
	
	, setColors: function(colors) {
		var array = [];
		for (var i = 0; i < colors.length; i++) {
			array.push([colors[i]]);
		}
		this.store.loadData(array);
	}

});