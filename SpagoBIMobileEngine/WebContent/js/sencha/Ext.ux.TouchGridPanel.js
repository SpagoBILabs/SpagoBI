/*
 * Because of limitation of the current WebKit implementation of CSS3 column layout,
 * I have decided to revert back to using table.
 */

Ext.define("Ext.ux.TouchGridPanel",{
	extend: "Ext.Panel",
	xtype: "touchgridpanel",
	layout        : "fit",
	multiSelect   : false,
	scroll        : "vertical",
	conditions    : null,
	columnToStyle : new Array(),
	

	initComponent : function() {
		var me = this;
		
		this.conditions = me.conditions ;
		for(i=0; i<this.conditions.length; i++){
			if(this.columnToStyle.indexOf(this.conditions[i].column) == -1){
				this.columnToStyle.push(this.conditions[i].column);
			}
			
		}
		
		me.items = me.dataview = me.buildDataView();

		if (!Ext.isArray(me.dockedItems)) {
			me.dockedItems = [];
		}

		me.header = new Ext.Component(me.buildHeader());
		me.dockedItems.push(me.header);

		Ext.ux.TouchGridPanel.superclass.initComponent.call(me);

		var store = me.store;

		store.on("update", me.dispatchDataChanged, me);
	},

	dispatchDataChanged: function(store, rec, operation) {
		var me = this;

		me.fireEvent("storeupdate", store, rec, operation);
	},
	
	buildHeader   : function() {
		

	var me        = this,
			colModel  = me.colModel,
			colNum    = me.getColNum(false),
			cellWidth = 100/colNum,
			colTpl    = '<table class="x-grid-header">';

		colTpl += '    <tr>';
		for (var i = 0; i < colModel.length; i++) {
			var col  = colModel[i],
				flex = col.flex || 1,
				cls  = "";

			var width = (flex * cellWidth)+'%';
			if(col.width != null && col.width !== undefined){
				//percetage column or oixel are specified in template
				width = col.width;		
			}
			if (col.hidden) {
				cls += "x-grid-col-hidden";
			}

			colTpl += '<td width="' + width + '" class="x-grid-cell x-grid-hd-cell x-grid-col-' + col.mapping + ' ' + cls + '" mapping="' + col.mapping + '">' + col.header + '</td>';
		}
		colTpl += '    </tr>';
		colTpl += '</table>';

		return {
			dock      : "top",
			html      : colTpl,
			listeners : {
				scope       : me,
				afterrender : me.initHeaderEvents
			}
		};
	},

	initHeaderEvents: function(cmp) {
		var me = this,
			el = cmp.getEl();

		el.on("click", me.handleHeaderClick, me);
	},

	handleHeaderClick: function(e, t) {
		e.stopEvent();

		var me      = this,
			el      = Ext.get(t),
			mapping = el.getAttribute("mapping");

		if (typeof mapping === "string") {
			me.store.sort(mapping);
			el.set({
				sort : me.store.sortToggle[mapping]
			});
		}
	},

	buildDataView : function() {
		var me        = this,
			colModel  = me.colModel,
			colNum    = me.getColNum(false),
			colTpl    = '<tr class="x-grid-row {isDirty:this.isRowDirty(parent)}">',
			cellWidth = 100/colNum;
		var generatedCellId;
		
		for (var i = 0; i < colModel.length; i++) {
			var col   = colModel[i],
				flex  = col.flex || 1,
				width = (flex * cellWidth)+'%',
				style = (i === colModel.length - 1) ? "padding-right: 10px;" : "",
				cls   = col.cls || "";
			
			if(col.width != null && col.width !== undefined){
				//percetage column or oixel are specified in template
				width = col.width;		
			}
			generatedCellId = "spagobi-"+i+"-";
				
			style += col.style || "";

			if (col.hidden) {
				cls += "x-grid-col-hidden";
			}

			if(me.columnToStyle.indexOf(col.mapping) != -1){
				colTpl += '<td id="'+generatedCellId+'{rowIndex}" width="' + width + '" class="x-grid-cell x-grid-col-' + col.mapping + ' ' + cls + ' {isDirty:this.isCellDirty(parent)}" style="' + style + ' {[values.styleTD['+i+']]}" mapping="' + col.mapping + '" rowIndex="{rowIndex}">{' + col.mapping + '}</td>';
			}else{
				colTpl += '<td id="'+generatedCellId+'{rowIndex}" width="' + width + '" class="x-grid-cell x-grid-col-' + col.mapping + ' ' + cls + ' {isDirty:this.isCellDirty(parent)}" style="' + style + '" mapping="' + col.mapping + '" rowIndex="{rowIndex}">{' + col.mapping + '}</td>';
			}	
			
		}
		colTpl += '</tr>';

		return new Ext.DataView({
			store        : me.store,
			itemSelector : "tr.x-grid-row",
			simpleSelect : me.multiSelect,
			//scroll       : me.scroll,
			scope        : this,
			tpl          : new Ext.XTemplate(
				'<table style="width: 100%;">',
					'<tpl for=".">',
						colTpl,
					'</tpl>',
				'</table>',
				{
					isRowDirty: function(dirty, data) {
						return dirty ? "x-grid-row-dirty" : "";
					},
					isCellDirty: function(dirty, data) {
						return dirty ? "x-grid-cell-dirty" : "";
					},
					hasStyle: function(st, index){
						if(st[i] !== undefined && st[i] != null){
							console.log(st[i]);
							return true;
						}else{
							return false;
							
						}
						return st[index];
					}
				}
			),
			prepareData  : function(data, index, record) {
				var column,
					i  = 0,
					ln = colModel.length;
				var prepare_data = {};
				prepare_data.dirtyFields = {};
				prepare_data.styleTD = {};
				for (; i < ln; i++) {

					column = colModel[i];
					var styleTD = {};
					
					if (typeof column.renderer === "function") {
						prepare_data[column.mapping] = column.renderer.apply(me, [data[column.mapping],column, record, index]);
					} else {
						prepare_data[column.mapping] = data[column.mapping];
						
					}
					
					//column.mapping is the column name
					var condList = me.getAlarmConditionForColumn(column.mapping);

					if(condList != null && condList.length !== 0){
						var result = false;

						for(k =0; k < condList.length; k++){
							result = me.evaluateCondition(condList[k], column, record);
							if(result == true){
								
								prepare_data.styleTD[i] = condList[k].style;
								break;
							}
						}

					}
				}

				prepare_data.isDirty = record.dirty;
				prepare_data.rowIndex = index;
				
				return prepare_data;
			},

			bubbleEvents : [
				"beforeselect",
				"containertap",
				"itemdoubletap",
				"itemswipe",
				"itemtap",
				"selectionchange"
			]
		});

	},
	
	getFirst: function(a){
		return a[0];
	}
	,
	getAlarmConditionForColumn: function(column){
		var condList = new Array();
		for (c = 0; c <this.conditions.length; c++){			
			var cond = this.conditions[c];
			var col = cond["column"];
			if(col == column){
				condList.push(cond);
			}
		}
		return condList;
	},
	
	evaluateCondition: function(condition, column, record){
		var alarmName = column.alarm;
		var alarmValue = record.data[alarmName];
		
		var cond = condition.condition;
		if(cond.indexOf('=') !== -1 && cond.indexOf('==') == -1){
			//add one =
			cond = '='+cond;
		}
		var ret =false;
		eval('if('+alarmValue + cond+'){ret = true;}');
		return ret;
	},
	// hidden = true to count all columns
	getColNum     : function(hidden) {
		var me       = this,
			colModel = me.colModel,
			colNum   = 0;

		for (var i = 0; i < colModel.length; i++) {
			var col = colModel[i];
			if (!hidden && typeof col.header !== "string") { continue; }
			if (!col.hidden) {
				colNum += col.flex || 1;
			}
		}

		return colNum;
	},

	getMappings: function() {
		var me       = this,
			mappings = {},
			colModel = me.colModel;

		for (var i = 0; i < colModel.length; i++) {
			mappings[colModel[i].mapping] = i
		}

		return mappings;
	},

	toggleColumn: function(index) {
		var me = this;

		if (typeof index === "string") {
			var mappings = me.getMappings();
			index = mappings[index];
		}
		var el      = me.getEl(),
			mapping = me.colModel[index].mapping,
			cells   = el.query("td.x-grid-col-"+mapping);

		for (var c = 0; c < cells.length; c++) {
			var cellEl = Ext.get(cells[c]);
			if (cellEl.hasCls("x-grid-col-hidden")) {
				cellEl.removeCls("x-grid-col-hidden");
				this.colModel[index].hidden = false;
			} else {
				cellEl.addCls("x-grid-col-hidden");
				this.colModel[index].hidden = true;
			}
		}

		me.updateWidths();
	},

	updateWidths: function() {
		var me          = this,
			el          = me.getEl(),
			headerWidth = me.header.getEl().getWidth(),
			colModel    = me.colModel,
			cells       = el.query("td.x-grid-cell"),
			colNum      = me.getColNum(false),
			cellWidth   = 100 / colNum,
			mappings    = me.getMappings();

		for (var c = 0; c < cells.length; c++) {
			var cellEl  = Ext.get(cells[c]),
				mapping = cellEl.getAttribute("mapping"),
				col     = colModel[mappings[mapping]],
				flex    = col.flex || 1,
				width   = flex * cellWidth / 100 * headerWidth;

			cellEl.setWidth(width);
		}
	},

	scrollToRow: function(index) {
		var me       = this,
			el       = me.getEl(),
			rows     = el.query("tr.x-grid-row"),
			rowEl    = Ext.get(rows[index]),
			scroller = me.dataview.scroller;

		var pos = {
			x: 0,
			y: rowEl.dom.offsetTop
		};

		scroller.scrollTo(pos, true);
	},

	getView: function() {
		var me = this;

		return me.dataview;
	},

	bindStore: function(store) {
		var me   = this,
			view = me.getView();

		view.bindStore(store);
	},

	getStore: function() {
		var me   = this,
			view = me.getView();

		return view.getStore();
	},

	getRow: function(index) {
		var me = this;
		if (typeof index === "object") {
			var store = me.getStore(),
				index = store.indexOf(index);
		}

		var el   = me.getEl(),
			rows = el.query("tr");

		return rows[index+1];
	}
});
