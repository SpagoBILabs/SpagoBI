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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * Monia Spinelli (monia.spinelli@eng.it)
  */

Ext.ns("Sbi.domain");

Sbi.domain.ManageDomains = function(config) {
	
	var c = Ext.apply({
		title: 'Domains',
		layout: 'fit'
	}, config || {});
	
	this.initGrid();
	
	c.items = [this.grid];
	
	// constructor
	Sbi.domain.ManageDomains.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.domain.ManageDomains, Ext.Panel, {
    
	grid: null
	, columnModel: null
	, store: null
	, gridToolbar: null
	, Record: null
	, editor: null
	
    // public methods
	, initGrid: function() {
		
		this.editor = new Ext.ux.grid.RowEditor({
			saveText: 'Update'
		});
		 
		this.initStore();
		this.initColumnModel();
		this.initToolbar();
		this.grid = new Ext.grid.GridPanel({
	        store: this.store,
	        cm: this.columnModel,
	        tbar: this.gridToolbar,
	        sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
	        plugins: [this.editor],
	        //width: 600,
	        height: 300,
	        margins: '0 5 5 5',
	        viewConfig: {
	            forceFit: true
	        }
		});
	}

	, initToolbar: function(){
		this.gridToolbar = new Ext.Toolbar([{
            //iconCls: 'icon-user-add',
            text: 'Add',
            handler: function(){
                var dom = new this.Record;
                this.editor.stopEditing();
                this.store.insert(0, dom);
                this.grid.getView().refresh();
                this.grid.getSelectionModel().selectRow(0);
                this.editor.startEditing(0);
            },scope:this
        },{
            //ref: '../removeBtn',
            //iconCls: 'icon-user-delete',
            text: 'Delete',
            //disabled: true,
            handler: function(){
                editor.stopEditing();
                var s = grid.getSelectionModel().getSelections();
                for(var i = 0, r; r = s[i]; i++){
                    store.remove(r);
                }
            }
        }])
	}	

	, initColumnModel: function() {
		this.columnModel =   new Ext.grid.ColumnModel([
		   {
			   header: 'First Name',
		       dataIndex: 'name',
		       width: 220,
		       sortable: true,
		       editor: {
	                xtype: 'textfield',
	                allowBlank: false
	            }
		    },{
		        header: 'Email',
		        dataIndex: 'email',
		        width: 150,
		        sortable: true,
		        editor: {
	                xtype: 'textfield',
	                allowBlank: false
	            }
		    }
		]);
	}
	
	, initStore: function() {
		
		var myData = [
		       ['Monia', 'monia@eng.it'],
		       ['Andrea', 'andrea@eng.it']
		];
		var fields = [
			           {name: 'name'},
			           {name: 'email'}
			         ];
		this.store =  new Ext.data.ArrayStore({
	        fields: fields 
	     });
		
		 this.store.loadData(myData);
		 this.Record = Ext.data.Record.create(fields);
	}
	
});