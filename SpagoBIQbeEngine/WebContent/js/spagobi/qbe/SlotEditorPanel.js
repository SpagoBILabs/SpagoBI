/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.ZONE
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
  * SlotEditorPanel - short description
  * 
  * Object documentation ...
  * 
  * by Monica Franceschini
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.SlotEditorPanel = function(config) {	
	
	var c = Ext.apply({}, config || {}, {
		layout: 'fit'
	});

	Ext.apply(this, c);
	
	this.initToolbar(c);
	this.initGrid(c);
	
	Ext.apply(c, {
		tbar: this.panelToolbar,
		items:  [this.gridPanel]
	});	
	
	// constructor
	Sbi.qbe.SlotEditorPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.qbe.SlotEditorPanel, Ext.Panel, {
   
    gridPanel: null 
    , panelToolbar: null
    
	, initToolbar: function(c){
	
		this.panelToolbar = new Ext.Toolbar({
			items: [{
                xtype:'button',
                text: 'Add',
                iconCls: 'add'
            },{
                xtype:'button',
                text: 'Delete',
                iconCls: 'remove'
            }]
		});
	}
	, initGrid: function(c) {
		
		var emptyData = [{name: '', values: []}];
	    // create the data store
	    var store = new Ext.data.ArrayStore({
	        fields: [
	           {name: 'name'},
	           {name: 'values'}
	        ]
	    });

	    // manually load local data
	    store.loadData(emptyData);
	    
		this.gridPanel = new Ext.grid.EditorGridPanel({
			store: store,
			columns: [
               {
                   id       :'name',
                   header   : 'Name', 
                   sortable : true, 
                   dataIndex: 'name'
               },
               {
                   header   : 'Values', 
                   sortable : true, 
                   dataIndex: 'values'
               }],
	        tbar: this.toolbar,
	        clicksToEdit:1,
	        frame: true,
	        border:true,  
	        style:'padding:0px',
	        iconCls:'icon-grid',
	        collapsible:false,
	        layout: 'fit',
	        viewConfig: {
	            forceFit: true
	        },		

	        enableDragDrop:false	
	    });

	}

});
