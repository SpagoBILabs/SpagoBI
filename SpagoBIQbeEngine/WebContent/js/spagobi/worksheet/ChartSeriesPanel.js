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
	, Record: Ext.data.Record.create([
	      {name: 'id', type: 'string'}
	      , {name: 'alias', type: 'string'}
	      , {name: 'funct', type: 'string'}
	      , {name: 'iconCls', type: 'string'}
	      , {name: 'nature', type: 'string'}
	])

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
	        fields: ['id', 'alias', 'funct', 'iconCls', 'nature']
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
        this.template = new Ext.Template( // see Ext.Button.buttonTemplate and Button's onRender method
        		// margin auto in order to have button center alignment
                '<table style="margin-left: auto; margin-right: auto;" id="{4}" cellspacing="0" class="x-btn {3}"><tbody class="{1}">',
                '<tr><td class="x-btn-tl"><i>&#160;</i></td><td class="x-btn-tc"></td><td class="x-btn-tr"><i>&#160;</i></td></tr>',
                '<tr><td class="x-btn-ml"><i>&#160;</i></td><td class="x-btn-mc"><button type="{0}" class=" x-btn-text {5}"></button>{6} ({7})</td><td class="x-btn-mr"><i>&#160;</i></td></tr>',
                '<tr><td class="x-btn-bl"><i>&#160;</i></td><td class="x-btn-bc"></td><td class="x-btn-br"><i>&#160;</i></td></tr>',
                '</tbody></table>');
        
        this.template.compile();
		
	    var fieldColumn = new Ext.grid.Column({
	    	header:  ''
	    	, dataIndex: 'alias'
	    	, hideable: false
	    	, hidden: false	
	    	, sortable: false
	   	    , renderer : function(value, metaData, record, rowIndex, colIndex, store){
	        	return this.template.apply(
	        			['button', 'x-btn-small x-btn-icon-small-left', '', 'x-btn-text-icon', Ext.id(), record.data.iconCls, record.data.alias, record.data.funct]		
	        	);
	    	}
	        , scope: this
	    });
	    this.cm = new Ext.grid.ColumnModel([fieldColumn]);
	}
	
	, initGrid: function (c) {
		this.grid = new Ext.grid.GridPanel({
	        store: this.store
	        , border: false
	        , cm: this.cm
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
	        	, rowdblclick: {
	        		fn: function(theGrid, rowIndex, e) {
		        		var theRow = this.store.getAt(rowIndex);
						var aWindow = new Sbi.crosstab.ChooseAggregationFunctionWindow({
							behindMeasure: Ext.apply({}, theRow.data) // creates a clone
		        	  	});
		        	  	aWindow.show();
		        	  	aWindow.on('apply', function(modifiedMeasure, theWindow) {this.modifyMeasure(theRow, new this.Record(modifiedMeasure));}, this);
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
		this.getLayout().setActiveItem( 1 );
		// if the measure is already present, does not insert it 
		if (this.containsMeasure(record)) {
			Ext.Msg.show({
				   title: LN('sbi.worksheet.chartseriespanel.cannotdrophere.title'),
				   msg: LN('sbi.worksheet.chartseriespanel.cannotdrophere.measurealreadypresent'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
		} else {
			this.store.add(record);
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
	
	, modifyMeasure: function(recordToBeModified, newRecordsValues) {
		recordToBeModified.set("funct", newRecordsValues.get("funct")); // only the aggregation function must be modified
	}

});