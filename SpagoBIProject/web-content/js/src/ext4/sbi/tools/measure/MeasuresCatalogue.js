/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */


Ext.define('Sbi.tools.measure.MeasuresCatalogue', {
	extend: 'Sbi.widgets.grid.GroupedGrid'

		, constructor: function(config) {

			var columns = this.buildColumns();
			thisPanel = this;
			var joinMeasuresButton = Ext.create('Ext.Button', {
				text    : 'Join',
				tooltip :'Join',
				hidden	:true,
				handler : function() {
					this.hide();
					selectMeasuresButton.show();
					thisPanel.columns[thisPanel.columns.length-1].hide();
				}
			});

			var selectMeasuresButton = Ext.create('Ext.Button', {
				text    : 'Select',
				tooltip :'Select',
				handler : function() {
					this.hide();
					joinMeasuresButton.show();
					thisPanel.columns[thisPanel.columns.length-1].show();
				}
			});

			var myconfig = {
					store: this.buildDataStore(), 
					columns: columns,
					dockedItems : [{
						xtype: 'toolbar',
						items: [selectMeasuresButton, joinMeasuresButton]
					}],
					selModel: Ext.create('Ext.selection.CheckboxModel', {
						injectCheckbox: columns.length+1,
						
					    getHeaderConfig: function() {
					        var me = this,
					            showCheck = me.showHeaderCheckbox !== false;

					        return {
					            isCheckerHd: showCheck,
					            text : '&#160;',
					            width: me.headerWidth,
					            sortable: false,
					            draggable: false,
					            resizable: false,
					            hideable: false,
					            menuDisabled: true,
					            hidden: true,//TO HIDE THE COLUMN AT THE BIGINNING
					            dataIndex: '',
					            cls: showCheck ? Ext.baseCSSPrefix + 'column-header-checkbox ' : '',
					            renderer: Ext.Function.bind(me.renderer, me),
					            editRenderer: me.editRenderer || me.renderEmpty,
					            locked: me.hasLockedHeader()
					        };
					    }
					}),
					plugins: [{
						ptype: 'rowexpander',
						rowBodyTpl : [
						              'ssssssssssss'
						              ]
					}]
			};


			this.callParent([myconfig]);

//			this.on("render",function(){
//				this.columns[this.columns.length-1].hide();
//			},this);
		},

		buildColumns: function(){
			var columns = [{
				text: 'Alias',
				flex: 1,
				dataIndex: 'alias'
			},
			{
				text: 'DS Name',
				flex: 1,
				dataIndex: 'dsName'
			},
			{
				text: 'DS Label',
				flex: 1,
				dataIndex: 'dsLabel'
			},
			{
				text: 'DS Category',
				flex: 1,
				dataIndex: 'dsCategory'
			},
			{
				text: 'DS Type',
				flex: 1,
				dataIndex: 'dsType'
			}];

			return columns;
		},

		buildDataStore: function(){
			var store = Ext.create('Ext.data.Store', {
			    model: 'Sbi.tools.measure.MeasureModel',
			    autoLoad: true
			});
			store.on("load",function(a,b,c,d,e){
				var t=2;
				},this);
			return store;

		}

});
