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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it), Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.SheetContentPanel = function(config) { 

	var defaultSettings = {
		emptyMsg: LN('sbi.worksheet.designer.sheetcontentpanel.emptymsg')
	};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.sheetContentPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.sheetContentPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.initEmptyMsgPanel();
	
	c = {
		height: 400,
		style:'padding:5px 15px 2px',
		items: [this.emptyMsgPanel]
	}
	Sbi.worksheet.designer.SheetContentPanel.superclass.constructor.call(this, c);	
	this.on('render', this.initDropTarget, this);

};

Ext.extend(Sbi.worksheet.designer.SheetContentPanel, Ext.Panel, {
	
	emptyMsg: null
	, emptyMsgPanel: null
	, designer: null
	
	, initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
			html: this.emptyMsg
			, border: false
			//, bodyStyle:'height: 100%'
			, frame: true
		});
	}
	
	, initDropTarget: function() {
		this.removeListener('render', this.initDropTarget, this);
		var dropTarget = new Sbi.widgets.GenericDropTarget(this, {
			ddGroup: 'paleteDDGroup'
			, onFieldDrop: this.onFieldDrop
		});
	}

	, onFieldDrop: function(ddSource) {
		if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'palette') {
			// dragging from palette
			this.notifyDropFromPalette(ddSource);
		} else {
			alert('Unknown DD source!!');
		}
	}
	
	, notifyDropFromPalette: function(ddSource) {
		if (this.designer != null) {
			Ext.Msg.show({
				   title:'Drop not allowed',
				   msg: 'You can insert a single widget on a sheet. Create a new sheet',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
			return;
		}
		var rows = ddSource.dragData.selections;
		if (rows.length > 1) {
			Ext.Msg.show({
				   title:'Drop not allowed',
				   msg: 'You can insert a single widget on a sheet',
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.WARNING
			});
			return;
		}
		var row = rows[0];
		var state = {};
		state.designer = row.json.name;
		this.addDesigner(state);
	}

	, insertCrosstabDesigner: function () {
		this.designer = new Sbi.crosstab.CrosstabDefinitionPanel({
			crosstabTemplate: {}
			, ddGroup: 'worksheetDesignerDDGroup'
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		});
		this.insertDesigner();
	}
	
	, insertBarchartDesigner: function () {
		this.designer = new Sbi.worksheet.designer.BarChartDesignerPanel({
			ddGroup: 'worksheetDesignerDDGroup'
			, border: false
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}, {
				id: 'help'
		        	, handler: function() {alert(this.designer.getFormState().toSource());}
		          	, scope: this
		          	, qtip: 'getformstate'
			}/*, {
				id: 'gear'
		        	, handler: function() {this.designer.setFormState({
		        		  type:"stacked-barchart", 
		        		  orientation:"horizontal", 
		        		  showvalues:true, 
		        		  showlegend:false, 
		        		  category:{id:"tre", alias:"COGNOME", funct:"none", iconCls:"field", nature:"attribute"}, 
		        		  series:[
		        		      {id:"5", alias:"MISURA 2", funct:"SUM", iconCls:"field", nature:"measure", seriename:"MISURA 2", color:"#99CC00"}, 
		        		      {id:"quattro", alias:"MISURA", funct:"SUM", iconCls:"field", nature:"measure", seriename:"MISURA", color:"#FF00FF"}
		        		  ]
		        	});}
		          	, scope: this
		          	, qtip: 'setformstate'
			}*/]
		});
		this.insertDesigner();
	}
	
	, insertLinechartDesigner: function () {
		this.designer = new Sbi.worksheet.designer.LineChartDesignerPanel({
			ddGroup: 'worksheetDesignerDDGroup'
			, border: false
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		});
		this.insertDesigner();
	}
	
	, insertPiechartDesigner: function () {
		this.designer = new Sbi.worksheet.designer.PieChartDesignerPanel({
			ddGroup: 'worksheetDesignerDDGroup'
			, border: false
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		});
		this.insertDesigner();
	}
	
	, insertTableDesigner: function () {
		this.designer = new Sbi.worksheet.designer.TableDesignerPanel({
			ddGroup: 'worksheetDesignerDDGroup'
			, border: false
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		});
		this.insertDesigner();
	}
	
	, insertDesigner: function() {
		this.emptyMsgPanel.destroy();
		this.add(this.designer);
		this.doLayout();
	}
	
	, removeDesigner: function (event, tool, panel, tc) {
		this.designer.destroy();
		this.designer = null;
		this.initEmptyMsgPanel();
		this.add(this.emptyMsgPanel);
		this.doLayout();
	}
	
	, getDesignerState: function () {
		if (this.designer !== null) {
			return this.designer.getFormState();
		} else {
			return null;
		}
	}
	
	, setDesignerState: function (state) {
		if (this.designer !== null) {
			this.designer.setFormState(state);
		}
	}
	
	, addDesigner: function (state) {
		switch (state.designer) {
	        case 'Pivot Table':
	        	this.insertCrosstabDesigner();
	            break;
	        case 'Bar Chart':
	        	this.insertBarchartDesigner();
	            break;
	        case 'Line Chart':
	        	this.insertLinechartDesigner();
	            break;
	        case 'Pie Chart':
	        	this.insertPiechartDesigner();
	            break;
	        case 'Table':
	        	this.insertTableDesigner();
	            break;
	        default: 
	        	alert('Unknown widget!');
		}
		this.setDesignerState(state);
	}

});
