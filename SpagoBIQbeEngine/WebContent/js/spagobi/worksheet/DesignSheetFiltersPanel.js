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
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet");

Sbi.worksheet.DesignSheetFiltersPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designsheetfilterspanel.title')
		, frame: true
		, emptyMsg: LN('sbi.worksheet.designsheetfilterspanel.emptymsg')
	};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designSheetFiltersPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designSheetFiltersPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		title: this.title
        , layout: {
            type:'column'
        }
		, items: [this.emptyMsgPanel]
	});

	// constructor	
	Sbi.worksheet.DesignSheetFiltersPanel.superclass.constructor.call(this, c);
	
	this.on('render', this.initDropTarget, this);

};

Ext.extend(Sbi.worksheet.DesignSheetFiltersPanel, Ext.Panel, {
	
	store: null
	, emptyMsgPanel: null
    , filters: null
	, empty: null
	, contents: null
	, Record: Ext.data.Record.create([
	      {name: 'id', type: 'string'}
	      , {name: 'alias', type: 'string'}
	      , {name: 'funct', type: 'string'}
	      , {name: 'iconCls', type: 'string'}
	      , {name: 'nature', type: 'string'}
	])
	
	, init: function() {
		this.initStore();
		this.initEmptyMsgPanel();
		this.contents = [this.emptyMsgPanel];
		this.empty = true;
	}
	
	, initStore: function() {
		this.store =  new Ext.data.ArrayStore({
	        fields: ['id', 'alias', 'funct', 'iconCls', 'nature']
		});
		// if there are initialData, load them into the store
		if (this.initialData !== undefined) {
			for (i = 0; i < this.initialData.length; i++) {
				var record = new this.Record(this.initialData[i]);
	  			this.store.add(record);
			}
		}
	}
	
	, initDropTarget: function() {
		this.removeListener('render', this.initDropTarget, this);
		var dropTarget = new Sbi.widgets.GenericDropTarget(this, {
			ddGroup: this.ddGroup
			, onFieldDrop: this.onFieldDrop
		});
	}

	, onFieldDrop: function(ddSource) {

		if (ddSource.grid && ddSource.grid.type && ddSource.grid.type === 'queryFieldsPanel') {
			this.notifyDropFromQueryFieldsPanel(ddSource);
		} else {
			Ext.Msg.show({
			   title: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.title'),
			   msg: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.unknownsource'),
			   buttons: Ext.Msg.OK,
			   icon: Ext.MessageBox.WARNING
			});
		}
	}
	
	, notifyDropFromQueryFieldsPanel: function(ddSource) {
		var rows = ddSource.dragData.selections;
		for (var i = 0; i < rows.length; i++) {
			var aRow = rows[i];
			// if the attribute is already present show a warning
			if (this.store.find('id', aRow.data.id) !== -1) {
				Ext.Msg.show({
					   title: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.title'),
					   msg: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.attributealreadypresent'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.WARNING
				});
				return;
			}
			// if the field is a measure show a warning
			if (aRow.data.nature === 'measure') {
				Ext.Msg.show({
					   title: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.title'),
					   msg: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.measures'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.WARNING
				});
				return;
			}
			// if the field is a postLineCalculated show an error
			if (aRow.data.nature === 'postLineCalculated') {
				Ext.Msg.show({
					   title: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.title'),
					   msg: LN('sbi.worksheet.designsheetfilterspanel.cannotdrophere.postlinecalculated'),
					   buttons: Ext.Msg.OK,
					   icon: Ext.MessageBox.ERROR
				});
				return;
			}
			
			this.addFilter(aRow);

		}
	}
	
	
	, getFilters: function () {
		var filters = [];
		for(i = 0; i < this.store.getCount(); i++) {
			var record = this.store.getAt(i);
			filters.push(record.data);
		}
		return filters;
	}
	
	, removeSelectedFilters: function() {
        var sm = this.getSelectionModel();
        var rows = sm.getSelections();
        this.store.remove(rows);
	}
	
	, removeAllFilters: function() {
		this.store.removeAll(false);
	}
	
	, initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
			html: this.emptyMsg
			, height: 20
		});
	}

	, addFilter: function(aRow) {
		if (this.empty === true) {
			this.reset();
			this.empty = false;	
		}
		
		this.store.add([aRow]);
		var item = this.createFilterPanel(aRow);

		this.contents.push(item);
		this.add(item);
		this.doLayout();
	}
	
	, createFilterPanel: function(aRow) {
		var item = new Ext.Panel({
			id: 'designsheetfilterspanel_' + aRow.data.alias
            , layout: {
                type:'column'
            }
			, style:'padding:0px 5px 5px 5px'
       		, items: [{
       			html: aRow.data.alias
       		}, new Ext.Button({
       		    template: new Ext.Template(
       		         '<div class="smallBtn">',
       		             '<div class="delete-icon"></div>',
       		             '<div class="btnText"></div>',
       		         '</div>')
       		     , buttonSelector: '.delete-icon'
       		  	 , iconCls: 'delete-icon'
       		     , text: '&nbsp;&nbsp;&nbsp;&nbsp;'
       		     , handler: this.closeHandler.createDelegate(this, [aRow], true)
       		     , scope: this
       		})]
		});
		return item;
	}
	
	, closeHandler: function (button, event, aRow) {
		this.removeFilter(aRow);
	}
	
	, removeFilter: function(aRow) {
		var rowId = aRow.data.id;
		var recordIndex = this.store.find('id', rowId);
		this.store.removeAt(recordIndex);
		var item = null;
		for (var i = this.contents.length-1; i >= 0; i--) {
			var temp = this.contents[i];
			if (temp.getId() == 'designsheetfilterspanel_' + aRow.data.alias) {
				item = temp;
				break;
			}
		}
		this.contents.remove(item);
		item.destroy();

		if (this.contents.length == 0) {
			this.initEmptyMsgPanel();
			this.contents.push(this.emptyMsgPanel);
			this.add(this.emptyMsgPanel);
			this.empty = true;
		}
		this.doLayout();
	}
	
	, reset: function() {
		if (this.contents && this.contents.length) {
			for (var i = this.contents.length - 1; i >= 0; i--) {
				this.contents[i].destroy();
			}
		}
		this.contents = new Array();
	}
    
});