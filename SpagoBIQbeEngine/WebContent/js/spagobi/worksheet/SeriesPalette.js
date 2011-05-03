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

Ext.ns("Sbi.worksheet");

Sbi.worksheet.SeriesPalette = function(config) {

	var defaultSettings = {
		title: LN('sbi.worksheet.seriespalette.title')
		, frame: true
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.seriesPalette) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.seriesPalette);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init(c);
	
	c = Ext.apply(c, {
		xtype: 'panel'
		, frame: true
		, border: false
		, autoScroll: true
		, layout:'fit'
        , items:[this.grid]
	});
	
	// constructor
    Sbi.worksheet.SeriesPalette.superclass.constructor.call(this, c);
    
};

Ext.extend(Sbi.worksheet.SeriesPalette, Ext.Window, {

	grid : null
	, colourFieldEditor : null
	, colourColumn : null
	, colours : [['#690000'], ['#FFA1D3'], ['#33FE32'], ['#0079F7'], ['#FFFF00'], ['#A17653'], ['#FD0100']
			, ['#0000A2'], ['#67FFFE'], ['#E2D2C3'], ['#F69495'], ['#E2E2E2'], ['#FFFF9B'], ['#FFFFFF']
			, ['#FFFFFF'], ['#FFFFFF'], ['#FFFFFF'], ['#FFFFFF'], ['#FFFFFF'], ['#FFFFFF'], ['#FFFFFF'], ['#FFFFFF']]

	, init: function(c) {
		this.initStore(c);
		this.initColumnModel(c);
		this.initGrid(c);
	}

	, initStore: function(c) {
		this.store =  new Ext.data.ArrayStore({
	        fields: ['colour']
			, data: this.colours
		});
	}
	
	, initColumnModel: function(c) {
		
		this.colourColumn = new Ext.grid.Column({
			header: ''
			, width: 60
			, dataIndex: 'colour'
			, editor: new Ext.form.TextField({}) // only in order to make the column editable: the editor is built 
												 // on the grid's beforeedit event 
			, renderer : function(v, metadata, record) {
				metadata.attr = ' style="background:' + v + ';"';
				return '';  
	       }
		});
	    this.cm = new Ext.grid.ColumnModel([this.colourColumn]);
	}
	
	, initGrid: function (c) {
		this.grid = new Ext.grid.EditorGridPanel({
	        store: this.store
	        , border: false
	        , cm: this.cm
	        , sm: new Ext.grid.RowSelectionModel()
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
	        			var colour = this.store.getAt(this.currentRowRecordEdited).data.colour;
	        			var colourFieldEditor = new Ext.ux.ColorField({ value: colour, msgTarget: 'qtip', fallback: true});
	        			colourFieldEditor.on('select', function(f, val) {
	        				this.store.getAt(this.currentRowRecordEdited).set('colour', val);
	        			}, this);
	        			this.colourColumn.setEditor(colourFieldEditor);
	        		}
	        		, scope : this
	        	}
			}
		});
	}

});