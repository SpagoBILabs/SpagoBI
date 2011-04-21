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
 * ManageConfig
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
 * Authors
 * 
 * Monia Spinelli (monia.spinelli@eng.it)
 */

Ext.ns("Sbi.config");

Sbi.config.ManageConfig = function(config) {
	
	var c = Ext.apply( {
		title : 'Config',
		layout : 'fit'
	}, config || {});

	var paramsList = {
		MESSAGE_DET : "CONFIG_LIST"
	};
	var paramsSave = {
		LIGHT_NAVIGATOR_DISABLED : 'TRUE',
		MESSAGE_DET : "CONFIG_SAVE"
	};
	var paramsDel = {
		LIGHT_NAVIGATOR_DISABLED : 'TRUE',
		MESSAGE_DET : "CONFIG_DELETE"
	};

	this.crudServices = {};

	this.crudServices['manageListService'] = Sbi.config.serviceRegistry
			.getServiceUrl( {
				serviceName : 'CONFIG_ACTION',
				baseParams : paramsList
			});
	this.crudServices['saveItemService'] = Sbi.config.serviceRegistry
			.getServiceUrl( {
				serviceName : 'CONFIG_ACTION',
				baseParams : paramsSave
			});
	this.crudServices['deleteItemService'] = Sbi.config.serviceRegistry
			.getServiceUrl( {
				serviceName : 'CONFIG_ACTION',
				baseParams : paramsDel
			});

	this.initGrid();

	c.items = [ this.grid ];

	// constructor
	Sbi.config.ManageConfig.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.config.ManageConfig, Ext.Panel, {

	grid : null,
	columnModel : null,
	store : null,
	gridToolbar : null,
	Record : null,
	editor : null

	// public methods
	,
	initGrid : function() {

		this.editor = new Ext.ux.grid.RowEditor( {
			saveText : 'Update',
			listeners : {
				afteredit : {
					fn : this.saveConfig,
					scope : this
				}
			}

		});
		
		  var pagingToolbar = new Ext.PagingToolbar ({ 
				  store: this.store, 
				  pageSize:20,
				  displayInfo :true 
		   });
		 

		this.initStore();
		this.store.load({});
		this.initColumnModel();
		this.initToolbar();
		this.grid = new Ext.grid.GridPanel( {
			store : this.store,
			cm : this.columnModel,
			tbar : this.gridToolbar,
			bbar: pagingToolbar,
			sm : new Ext.grid.RowSelectionModel( {
				singleSelect : true
			}),
			plugins : [ this.editor ],
			// width: 600,
			height : 300,
			margins : '0 5 5 5',
			viewConfig : {
				forceFit : true
			}
		});
	}

	,
	initToolbar : function() {
		this.gridToolbar = new Ext.Toolbar( [ {
			iconCls : 'icon-domain-add',
			text : 'Add',
			handler : function() {
				var record = new this.Record();
				this.editor.stopEditing();
				this.store.insert(0, record);
				this.grid.getView().refresh();
				this.grid.getSelectionModel().selectRow(0);
				this.editor.startEditing(0);
			},
			scope : this
		}, {
			// ref: '../removeBtn',
			iconCls : 'icon-domain-delete',
			text : 'Delete',
			// disabled: true,
			handler : function() {
				this.editor.stopEditing();
				var s = this.grid.getSelectionModel().getSelections();
				for ( var i = 0, r; r = s[i]; i++) {
					var params = {
							VALUE_ID: r.get('ID')
							};

					Ext.Ajax.request( {
						url : this.crudServices['deleteItemService'],
						params : params,
						// method: 'GET',
						success : function(response, options) {
						 	response = Ext.util.JSON.decode( response.responseText );
							alert(response.VALUE_ID); 
						    var index = this.store.find( "ID", response.ID );
						    var record =  this.store.getAt(  index ) ;
						    if(record) this.store.remove(record);						    
						},
						failure : Sbi.exception.ExceptionHandler.handleFailure,
						scope : this
					});
				}
			},
			scope : this
		} ])
	}

	,
	initColumnModel : function() {
		this.columnModel = new Ext.grid.ColumnModel( [ {
			header : LN('sbi.config.manageconfig.fields.label'),
			dataIndex : 'LABEL',
			// width: 220,
			sortable : true,
			editor : {
				xtype : 'textfield',
				 allowBlank : false,
				 maxLength : 100,
				 maxLengthText :
				 LN('sbi.config.manageconfig.validation.maxlengthtext')
			}
		}, {
			header : LN('sbi.config.manageconfig.fields.name'),
			dataIndex : 'NAME',
			// width: 150,
			sortable : true,
			editor : {
				xtype : 'textfield',
				allowBlank : false,
				maxLength : 100,
				maxLengthText : LN('sbi.config.manageconfig.validation.maxlengthtext')
			}
		}, {
			header : LN('sbi.config.manageconfig.fields.description'),
			dataIndex : 'DESCRIPTION',
			// width: 150,
			sortable : true,
			editor : {
				xtype : 'textfield',
				allowBlank : false,
				maxLength : 500,
				maxLengthText : LN('sbi.config.manageconfig.validation.maxlengthtext')
			}
		}, {
			header : LN('sbi.config.manageconfig.fields.isactive'),
			dataIndex : 'IS_ACTIVE',
			// width: 150,
			sortable : true,
			editor : {
				xtype : 'field',
				allowBlank : false,
				maxLength : 1,
				maxLengthText : LN('sbi.config.manageconfig.validation.maxlengthtext')
			}
		}, {
			header : LN('sbi.config.manageconfig.fields.valuecheck'),
			dataIndex : 'VALUE_CHECK',
			// width: 150,
			sortable : true,
			editor : {
				xtype : 'textfield',
				allowBlank : false,
				maxLength : 1000,
				maxLengthText : LN('sbi.config.manageconfig.validation.maxlengthtext')
			}
		},{
			header : LN('sbi.config.manageconfig.fields.valuetype'),
			dataIndex : 'VALUE_TYPE',
			// width: 150,
			sortable : true,
			editor : {
				xtype : 'numberfield',
				allowBlank : false,
				maxLength : 11,
				maxLengthText : LN('sbi.config.manageconfig.validation.maxlengthtext')
			}
		}]);
	}

	,
	initStore : function() {

		var fields = [ {
			name : 'ID'
		}, {
			name : 'LABEL'
		}, {
			name : 'NAME'
		}, {
			name : 'DESCRIPTION'
		}, {
			name : 'IS_ACTIVE'
		}, {
			name : 'VALUE_CHECK'
		}, {
			name : 'VALUE_TYPE'
		} ];

		this.store = new Ext.data.JsonStore( {

			root : 'response',
			idProperty : 'ID',
			fields : fields,
			url : this.crudServices['manageListService']

		});
		this.Record = Ext.data.Record.create(fields);
	}

	,
	saveConfig : function(rowEditor, obj, record, rowIndex) {

		/*var p = {};
		if (record.get('ID') != undefined && record.get('ID') != null && record.get('ID') !== '') {
			p.VALUE_ID = record.get('ID');
		}
		
		p.LABEL = record.get('LABEL');
		p.NAME = record.get('NAME');
		p.DESCRIPTION = record.get('DESCRIPTION');
		p.IS_ACTIVE = record.get('IS_ACTIVE');
		p.VALUE_CHECK = record.get('VALUE_CHECK');
		p.VALUE_TYPE = record.get('VALUE_TYPE');*/
		var p = Ext.apply({},record.data);
		if (record.get('ID') == undefined || record.get('ID') == null || record.get('ID') == '') {
			delete p.ID;
		}

		Ext.Ajax.request( {
			url : this.crudServices['saveItemService'],
			params : p,
			method : 'POST',
			success : function(response, options) {
				alert('Salvataggio ok: ' + response.responseText);
				var jsonResponse = Ext.util.JSON.decode(response.responseText);
				record.set('ID', jsonResponse.ID);
				record.commit();
			},
			failure : Sbi.exception.ExceptionHandler.handleFailure,
			scope : this
		});
	}
});