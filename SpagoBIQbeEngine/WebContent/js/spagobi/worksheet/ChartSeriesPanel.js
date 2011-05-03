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

Sbi.worksheet.ChartSeriesPanel = function(config) {

	var defaultSettings = {
		title: LN('sbi.worksheet.chartseriespanel.title')
		, frame: true
		, emptyMsg: LN('sbi.worksheet.chartseriespanel.emptymsg')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.chartSeriesPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.chartSeriesPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	this.initialData = c.initialData;		// initial grid's content
	delete c.initialData;
	
	Ext.apply(this, c);
	
	this.init(c);
	
	c = Ext.apply(c, {
		items: [this.emptyMsgPanel, this.grid]
		, layout: 'card'
		, activeItem: 0
		, tools: [
	          {
	        	  id: 'close'
  	        	, handler: this.removeAllMeasures
  	          	, scope: this
  	          	, qtip: LN('sbi.worksheet.chartseriespanel.tools.tt.removeall')
	  	      }
		]
	});
	
	// constructor
    Sbi.worksheet.ChartSeriesPanel.superclass.constructor.call(this, c);
    
    this.on('render', this.initDropTarget, this);
    
};

Ext.extend(Sbi.worksheet.ChartSeriesPanel, Ext.Panel, {
	
	emptyMsg : null
	, emptyMsgPanel : null
	, initialData: undefined
	, targetRow: null
	, detailsWizard: undefined
	, grid: null
	, currentRowEdit : null
	, displayColourColumn : true // to display or not the colour column, default is true
	
	// static members
	, Record: Ext.data.Record.create([
	      {name: 'id', type: 'string'}
	      , {name: 'alias', type: 'string'}
	      , {name: 'funct', type: 'string'}
	      , {name: 'iconCls', type: 'string'}
	      , {name: 'nature', type: 'string'}
	      , {name: 'seriename', type: 'string'}
	      , {name: 'colour', type: 'string'}
	])
	
	, aggregationFunctionsStore:  new Ext.data.ArrayStore({
		 fields: ['funzione', 'nome', 'descrizione'],
	     data : [
	        ['NONE', LN('sbi.qbe.selectgridpanel.aggfunc.name.none'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.none')],
	        ['SUM', LN('sbi.qbe.selectgridpanel.aggfunc.name.sum'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.sum')],
	        ['AVG', LN('sbi.qbe.selectgridpanel.aggfunc.name.avg'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.avg')],
	        ['MAX', LN('sbi.qbe.selectgridpanel.aggfunc.name.max'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.max')],
	        ['MIN', LN('sbi.qbe.selectgridpanel.aggfunc.name.min'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.min')],
	        ['COUNT', LN('sbi.qbe.selectgridpanel.aggfunc.name.count'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.count')],
	        ['COUNT_DISTINCT', LN('sbi.qbe.selectgridpanel.aggfunc.name.countdistinct'), LN('sbi.qbe.selectgridpanel.aggfunc.desc.countdistinct')]
	     ] 
	 })
	
	
	, init: function(c) {
		this.initEmptyMsgPanel();
		this.initStore(c);
		this.initColumnModel(c);
		this.initGrid(c);
	}
	
	, initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
			html: this.emptyMsg
			, height: 40
		});
	}

	, initStore: function(c) {
		this.store =  new Ext.data.SimpleStore({
	        fields: ['id', 'alias', 'funct', 'iconCls', 'nature', 'seriename', 'colour']
		});
		// if there are initialData, load them into the store
		if (this.initialData !== undefined) {
			for (i = 0; i < this.initialData.length; i++) {
				var record = new this.Record(this.initialData[i]);
	  			this.addMeasure(record);
			}
		}
	}
	
	, initColumnModel: function(c) {
		
	    var serieNameColumn = new Ext.grid.Column({
	    	header: LN('sbi.worksheet.chartseriespanel.columns.seriename')
	    	, dataIndex: 'seriename'
	    	, hideable: false
	    	, sortable: false
	    	, editor: new Ext.form.TextField({})
	    });
		
	    var fieldColumn = new Ext.grid.Column({
	    	header: LN('sbi.worksheet.chartseriespanel.columns.queryfield')
	    	, dataIndex: 'alias'
	    	, hideable: false
	    	, sortable: false
	        , scope: this
	    });
	    
	    var aggregatorColumn = new Ext.grid.Column({
	    	 header: LN('sbi.qbe.selectgridpanel.headers.function')
	         , dataIndex: 'funct'
	         , editor: new Ext.form.ComboBox({
		         allowBlank: true,
		         editable: false,
		         store: this.aggregationFunctionsStore,
		         displayField: 'nome',
		         valueField: 'funzione',
		         typeAhead: true,
		         mode: 'local',
		         triggerAction: 'all',
		         autocomplete: 'off',
		         emptyText: LN('sbi.qbe.selectgridpanel.aggfunc.editor.emptymsg'),
		         selectOnFocus: true
	         })
		     , hideable: true
		     , hidden: false
		     , width: 50
		     , sortable: false
	    });
	    
		var colourFieldEditor = new Ext.ux.ColorField({ value: '#FFFFFF', msgTarget: 'qtip', fallback: true});
		colourFieldEditor.on('select', function(f, val) {
			this.store.getAt(this.currentRowRecordEdited).set('colour', val);
		}, this);
		
		var colourColumn = new Ext.grid.Column({
			header: LN('sbi.worksheet.chartseriespanel.columns.colour')
			, width: 60
			, dataIndex: 'colour'
			, editor: colourFieldEditor
			, renderer : function(v, metadata, record) {
				metadata.attr = ' style="background:' + v + ';"';
				return v;  
	       }
		});
	    
		var columns = [serieNameColumn, fieldColumn, aggregatorColumn];
		if (this.displayColourColumn)  {
			columns.push(colourColumn);
		}
	    this.cm = new Ext.grid.ColumnModel(columns);
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
	        	beforeedit: {
	        		fn : function (e) {
	        	    	var t = Ext.apply({}, e);
	        			this.currentRowRecordEdited = t.row;	
	        		}
	        		, scope : this
	        	}
	        	, keydown: {
	        		fn: function(e) {
		        		if (e.keyCode === 46) {
		        			this.removeSelectedMeasures();
		      	      	}      
		      	    }
	        		, scope: this
	        	}
	        	, mouseover: {
	        		fn: function(e, t) {
		        		this.targetRow = t; // for Drag&Drop
			        }
	        		, scope: this
		        }
	        	, mouseout: {
	        		fn: function(e, t) {
	        			this.targetRow = undefined;
			        }
        			, scope: this
	        	}
			}
	        , type: 'measuresContainerPanel'
		});
	}
	
	, initDropTarget: function() {
		this.removeListener('render', this.initDropTarget, this);
		var dropTarget = new Sbi.widgets.GenericDropTarget(this, {
			ddGroup: this.ddGroup || 'crosstabDesignerDDGroup'
			, onFieldDrop: this.onFieldDrop
		});
	}

	, onFieldDrop: function(ddSource) {
		
		if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'queryFieldsPanel') {
			// dragging from QueryFieldsPanel
			this.notifyDropFromQueryFieldsPanel(ddSource);
		} else if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'measuresContainerPanel') {
			// dragging from MeasuresContainerPanel
			this.notifyDropFromMeasuresContainerPanel(ddSource);
		} else if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'attributesContainerPanel') {
			Ext.Msg.show({
				   title: LN('sbi.worksheet.chartseriespanel.cannotdrophere.title'),
				   msg: LN('sbi.worksheet.chartseriespanel.cannotdrophere.attributes'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
		}
		
	}
	
	, notifyDropFromQueryFieldsPanel: function(ddSource) {
		var rows = ddSource.dragData.selections;
		for (var i = 0; i < rows.length; i++) {
			var aRow = rows[i];
			// if the field is an attribute show a warning
			if (aRow.data.nature === 'attribute') {
				Ext.Msg.show({
					   title: LN('sbi.worksheet.chartseriespanel.cannotdrophere.title'),
					   msg: LN('sbi.worksheet.chartseriespanel.cannotdrophere.attributes'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.WARNING
				});
				return;
			}
			// if the field is a postLineCalculated show an error
			if (aRow.data.nature === 'postLineCalculated') {
				Ext.Msg.show({
					   title: LN('sbi.worksheet.chartseriespanel.cannotdrophere.title'),
					   msg: LN('sbi.worksheet.chartseriespanel.cannotdrophere.postlinecalculated'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.ERROR
				});
				return;
			}
			// if the measure is missing the aggregation function, user must select it
			if (aRow.data.funct === null || aRow.data.funct === '' || aRow.data.funct === 'NONE') {
				var aWindow = new Sbi.crosstab.ChooseAggregationFunctionWindow({
					behindMeasure: Ext.apply({}, aRow.data) // creates a clone
        	  	});
        	  	aWindow.show();
        	  	aWindow.on('apply', function(modifiedMeasure, theWindow) {this.addMeasure(new this.Record(modifiedMeasure));}, this);
			} else {
				this.addMeasure(aRow);
			}
			
		}
	}
	
	, notifyDropFromMeasuresContainerPanel: function(ddSource) {
		// DD on the same MeasuresContainerPanel --> re-order the fields
		var rows = ddSource.dragData.selections;
		if (rows.length > 1) {
			Ext.Msg.show({
				   title:'Drop not allowed',
				   msg: 'You can move only one field at a time',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
		} else {
			var row = rows[0];
			var rowIndex; // the row index on which the field has been dropped on
			if(this.targetRow) {
				rowIndex = this.grid.getView().findRowIndex( this.targetRow );
			}
			if (rowIndex == undefined || rowIndex === false) {
				rowIndex = undefined;
			}
	           
         	var rowData = this.store.getById(row.id);
        	this.store.remove(this.store.getById(row.id));
            if (rowIndex != undefined) {
            	this.store.insert(rowIndex, rowData);
            } else {
            	this.store.add(rowData);
            }
	         
	         this.grid.getView().refresh();
		}
	}

	, getContainedMeasures: function () {
		var measures = [];
		for(i = 0; i < this.store.getCount(); i++) {
			var record = this.store.getAt(i);
			measures.push(record.data);
		}
		return measures;
	}
	
	, removeSelectedMeasures: function() {
        var sm = this.grid.getSelectionModel();
        var rows = sm.getSelections();
        this.store.remove( rows );
        if (this.store.getCount() == 0) {
        	this.getLayout().setActiveItem( 0 );
        }
	}
	
	, addMeasure: function(record) {
		var theRecord = null;
		if (record.data.seriename === undefined && record.data.colour === undefined ) {
			var data = Ext.apply({}, record.data); // make a clone
			data = Ext.apply(data, { // add additional properties
				seriename: record.data.alias
				, colour: this.getRandomColour()
			});
			theRecord = new this.Record(data);
		} else {
			theRecord = record;
		}
		this.getLayout().setActiveItem( 1 );
		// if the measure is already present, does not insert it 
		if (this.containsMeasure(theRecord)) {
			Ext.Msg.show({
				   title: LN('sbi.worksheet.chartseriespanel.cannotdrophere.title'),
				   msg: LN('sbi.worksheet.chartseriespanel.cannotdrophere.measurealreadypresent'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
		} else {
			this.store.add(theRecord);
		}
	}
	
	, containsMeasure: function(record) {
		if (this.store.findBy(function(aRecord) {
            	return aRecord.get("alias") === record.get("alias") && aRecord.get("funct") === record.get("funct");
        	}) === -1) {
			return false;
		} else {
			return true;
		}
	}
	
	, removeAllMeasures: function() {
		this.store.removeAll(false);
        this.getLayout().setActiveItem( 0 );
	}
	
	, getRandomColour: function() {
		var chars = "0123456789ABCDEF";
		var string_length = 6;
		var randomstring = '';
		for (var i=0; i<string_length; i++) {
			var rnum = Math.floor(Math.random() * chars.length);
			randomstring += chars.substring(rnum,rnum+1);
		}
		return "#" + randomstring;
	}

});