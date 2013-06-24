/*
    Author       : Mitchell Simoens
    Site         : http://simoens.org/Sencha-Projects/demos/
    Contact Info : mitchellsimoens@gmail.com
    Purpose      : Needed to have a paging toolbar for Ext.DataView, Ext.List, and Ext.ux.TouchGridPanel

	License      : GPL v3 (http://www.gnu.org/licenses/gpl.html)
    Warranty     : none
    Price        : free
    Version      : 1.0
    Date         : 2/5/2011
*/

Ext.ns("Ext.ux.touch");

Ext.ux.touch.GridSummary = Ext.extend(Ext.util.Observable, {
	type: "dock",

	summaryType: "total",

	decimalPosition: 2,

	constructor: function(config) {
		if (typeof config !== "object") { config = {}; }

		Ext.apply(this, config);
		Ext.ux.touch.GridSummary.superclass.constructor.call(this, config);
	},

	init: function(grid) {
		this.grid = grid;

		this.grid.dataview.on("afterrender", this.dispatcher, this);
	},

	dispatcher: function() {
		var grid = this.grid;
		if (this.type === "dock") {
			grid.store.on("datachanged", this.dataChangedDock, this);
			var dock = this.buildRow();
			grid.addDocked(dock);
		} else if (this.type === "row") {
			var row = this.buildRow();
			var dataviewEl = grid.dataview.getEl();

			var tr = dataviewEl.query("tr");
			var afterThis = tr[tr.length-1];

			var tpl = new Ext.Template(row.html);
			tpl.insertAfter(afterThis);
		}
	},

	dataChangedDock: function() {
		var summaryDock,
			docks = this.grid.getDockedItems();

		for (var i = 0; i < docks.length; i++) {
			var dock = docks[i];
			if (dock.type === "summaryDock") {
				summaryDock = dock;
			}
		}

		var dockCfg = this.buildRow();
		summaryDock.update(dockCfg.html);
	},

	buildRow: function() {
		var grid      = this.grid,
			colModel  = grid.colModel,
			colNum    = grid.getColNum(false),
			cellWidth = 100 / colNum,
			colTpl    = '<table class="x-grid-header">';

		var rowCls = (this.type === "row") ? "x-grid-row" : "";

		colTpl += '    <tr class="' + rowCls + ' x-grid-summary-row">';
		for (var i = 0; i < colModel.length; i++) {
			var col   = colModel[i],
				flex  = col.flex || 1,
				style = (i === colModel.length - 1) ? "padding-right: 10px;" : "",
				cls   = col.cls || "";

			style += col.style || "";
			cls += (this.type === "dock") ? "x-grid-hd-cell " : "";

			var width = flex * cellWidth;

			if (col.hidden) {
				cls += "x-grid-col-hidden";
			}

			var amount = this.getColumnSummary(col.mapping);
			if (!Ext.isNumber(amount)) {
				amount = "";
			}

			colTpl += '<td width="' + width + '%" class="x-grid-cell x-grid-col-' + col.mapping + ' ' + cls + '" style="' + style + '" mapping="' + col.mapping + '">' + amount + '</td>';
		}
		colTpl += '    </tr>';
		colTpl += '</table>';

		return {
			xtype : "component",
			dock  : "bottom",
			type  : "summaryDock",
			html  : colTpl
		};
	},

	getColumnSummary: function(mapping) {
		var amount = 0,
			date   = false,
			grid   = this.grid,
			store  = grid.store;

		store.each(function(rec) {
			var value = rec.get(mapping);
			if (Ext.isNumber(value)) {
				amount += value;
			} else {
				amount += value * 1;
			}
		});

		if (this.summaryType === "average") {
			amount = amount / store.getCount();
		}

		var num = "1";
		for (var i = 0; i < this.decimalPosition; i++) {
			num += "0";
		}
		num *= 1;

		amount = Math.round(amount * num) / num;

		if (amount === NaN) {
			amount = "";
		}

		return amount;
	}
});

Ext.preg("gridsummary", Ext.ux.touch.GridSummary);