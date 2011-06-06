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
		emptyMsg: LN('sbi.worksheet.designer.sheetcontentpanel.emptymsg'),
		style:'padding:5px 15px 2px'
	};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.sheetContentPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.sheetContentPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.initEmptyMsgPanel();
	
	c = {
		height: 400,
		items: [this.emptyMsgPanel]
	};
	Sbi.worksheet.designer.SheetContentPanel.superclass.constructor.call(this, c);
	
	this.addEvents('addDesigner');
	
	this.on('render', this.initDropTarget, this);

};

Ext.extend(Sbi.worksheet.designer.SheetContentPanel, Ext.Panel, {
	
	emptyMsg: null
	, emptyMsgPanel: null
	, designer: null
	, designerState: null //State of the designer.. (see setDesignerState & getDesignerState)
	
	, initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
			html: this.emptyMsg
			, border: false
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
		if (this.designer !== null) {
			this.fireEvent('addDesigner', this, state);
			return;
		}
		this.addDesigner(state);
	}

	, insertCrosstabDesigner: function (sheredConf) {
		this.designer = new Sbi.crosstab.CrosstabDefinitionPanel(Ext.apply({
			crosstabTemplate: {}
			, ddGroup: 'worksheetDesignerDDGroup'
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		},sheredConf));
		this.insertDesigner();
	}
	
	, insertBarchartDesigner: function (sheredConf) {
		this.designer = new Sbi.worksheet.designer.BarChartDesignerPanel(Ext.apply({
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
			}]
		},sheredConf));
		this.insertDesigner();
	}
	
	, insertLinechartDesigner: function (sheredConf) {
		this.designer = new Sbi.worksheet.designer.LineChartDesignerPanel(Ext.apply({
			ddGroup: 'worksheetDesignerDDGroup'
			, border: false
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		},sheredConf));
		this.insertDesigner();
	}
	
	, insertPiechartDesigner: function (sheredConf) {
		this.designer = new Sbi.worksheet.designer.PieChartDesignerPanel(Ext.apply({
			ddGroup: 'worksheetDesignerDDGroup'
			, border: false
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		},sheredConf));
		this.insertDesigner();
	}
	
	, insertTableDesigner: function (sheredConf) {
		this.designer = new Sbi.worksheet.designer.TableDesignerPanel(Ext.apply({
			ddGroup: 'worksheetDesignerDDGroup'
			, border: false
			, tools:  [{
				id: 'close'
	        	, handler: this.removeDesigner
	          	, scope: this
	          	, qtip: LN('sbi.worksheet.designer.sheetcontentpanel.tools.tt.remove')
			}]
		},sheredConf));
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
	
	/**
	 * Gets the state of the designer.. 
	 * If the global variable designerState is null the sheet has been 
	 * rendered and so the state of the designer is taken from  
	 * this.designer by the method designer.getFormState()...
	 * If the variable is != null the designer has not been rendered and
	 * the method returns this.designerState
	 */
	, getDesignerState: function () {
		
		if(this.designerState==null){
			if (this.designer !== null) {
				return this.designer.getFormState();
			} else {
				return null;
			}
		}else{
			return this.designerState;
		}
	}
	
	, setDesignerState: function (state) {
		if (this.designer !== null) {
			this.designer.setFormState(state);
			this.designerState = null;
		}
	}
	
	, addDesigner: function (state) {
		var sheredConf = {padding: Ext.isIE ? '10 0 0 35' : '0'};
		switch (state.designer) {
	        case 'Pivot Table':
	        	this.insertCrosstabDesigner(sheredConf);
	            break;
	        case 'Bar Chart':
	        	this.insertBarchartDesigner(sheredConf);
	            break;
	        case 'Line Chart':
	        	this.insertLinechartDesigner(sheredConf);
	            break;
	        case 'Pie Chart':
	        	this.insertPiechartDesigner(sheredConf);
	            break;
	        case 'Table':
	        	this.insertTableDesigner(sheredConf);
	            break;
	        default: 
	        	alert('Unknown widget!');
		}
		this.designerState = state;
		if (this.rendered) {
			this.setDesignerState(state);
		} else {
			this.designer.on('render', function() {
				this.setDesignerState(state);
			}, this);
		}
	}

});
