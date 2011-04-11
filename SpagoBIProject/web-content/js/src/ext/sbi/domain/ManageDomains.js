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
 * ManageDomains
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

Ext.ns("Sbi.domain");

Sbi.domain.ManageDomains = function(config) {

	var c = Ext.apply( {
		title : 'Domains',
		layout : 'fit'
	}, config || {});

	var paramsList = {
		MESSAGE_DET : "DOMAIN_LIST"
	};
	var paramsSave = {
		LIGHT_NAVIGATOR_DISABLED : 'TRUE',
		MESSAGE_DET : "DOMAIN_SAVE"
	};
	var paramsDel = {
		LIGHT_NAVIGATOR_DISABLED : 'TRUE',
		MESSAGE_DET : "DOMAIN_DELETE"
	};

	this.crudServices = {};

	this.crudServices['manageListService'] = Sbi.config.serviceRegistry
			.getServiceUrl( {
				serviceName : 'DOMAIN_ACTION',
				baseParams : paramsList
			});
	this.crudServices['saveItemService'] = Sbi.config.serviceRegistry
			.getServiceUrl( {
				serviceName : 'DOMAIN_ACTION',
				baseParams : paramsSave
			});
	this.crudServices['deleteItemService'] = Sbi.config.serviceRegistry
			.getServiceUrl( {
				serviceName : 'DOMAIN_ACTION',
				baseParams : paramsDel
			});

	this.initGrid();

	c.items = [ this.grid ];

	// constructor
	Sbi.domain.ManageDomains.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.domain.ManageDomains, Ext.Panel,
		{

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
							fn : this.saveDomain,
							scope : this
						}
					}

				});

				this.initStore();
				this.initColumnModel();
				this.initToolbar();
				this.grid = new Ext.grid.GridPanel( {
					store : this.store,
					cm : this.columnModel,
					tbar : this.gridToolbar,
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

						var params = {};

						Ext.Ajax.request( {
							url : this.crudServices['saveItemService'],
							params : params,
							// method: 'GET',
							success : function(response, options) {
								alert('success');
							},
							failure : function(response) {
								alert('failure');
							}
						});
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
							this.store.remove(r);
						}
					},
					scope : this
				} ])
			}

			,
			initColumnModel : function() {
				this.columnModel = new Ext.grid.ColumnModel( [ {
					header : 'VALUE ID',
					dataIndex : 'VALUE_ID',
					// width: 220,
					sortable : false,
					hidden : true
				}, {
					header : 'VALUE CODE',
					dataIndex : 'VALUE_CD',
					// width: 220,
					sortable : true,
					editor : {
						xtype : 'textfield',
						allowBlank : false
					}
				}, {
					header : 'VALUE NAME',
					dataIndex : 'VALUE_NM',
					// width: 150,
					sortable : true,
					editor : {
						xtype : 'textfield',
						allowBlank : false
					}
				}, {
					header : 'DOMAIN CODE',
					dataIndex : 'DOMAIN_CD',
					// width: 150,
					sortable : true,
					editor : {
						xtype : 'textfield',
						allowBlank : false
					}
				}, {
					header : 'DOMAIN NAME',
					dataIndex : 'DOMAIN_NM',
					// width: 150,
					sortable : true,
					editor : {
						xtype : 'textfield',
						allowBlank : false
					}
				}, {
					header : 'VALUE DESCRIPTION',
					dataIndex : 'VALUE_DS',
					// width: 150,
					sortable : true,
					editor : {
						xtype : 'textfield',
						allowBlank : false
					}
				} ]);
			}

			,
			initStore : function() {

				var myData = [
						[ 105, 'Monia', 'monia@eng.it', 'prova', 'test',
								'provatest' ],
						[ 106, 'Andrea', 'andrea@eng.it', 'prova', 'test',
								'provatest' ] ];
				var fields = [ {
					name : 'VALUE_ID'
				}, {
					name : 'VALUE_CD'
				}, {
					name : 'VALUE_NM'
				}, {
					name : 'DOMAIN_CD'
				}, {
					name : 'DOMAIN_NM'
				}, {
					name : 'VALUE_DS'
				} ];
				this.store = new Ext.data.ArrayStore( {
					fields : fields
				});

				this.store.loadData(myData);
				this.Record = Ext.data.Record.create(fields);
			}

			,
			saveDomain : function(rowEditor, obj, data, rowIndex) {
				Ext.Ajax.request( {
					url : this.crudServices['saveItemService'],
					params : Ext.apply(obj, {
						id : data.get('VALUE_ID')
					}),
					method : 'POST',
					success : function(response, options) {
						alert('Salvataggio ok');
						data.commit();
					},
					failure : Sbi.exception.ExceptionHandler.handleFailure,
					scope : this
				});
			}

		});