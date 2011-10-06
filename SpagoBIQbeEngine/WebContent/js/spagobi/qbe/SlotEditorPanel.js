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
    , valuesItemTemplate: null
    
	, initToolbar: function(c){
	
		this.panelToolbar = new Ext.Toolbar({
			scope: this,
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
		
		//var emptyData = [['eta 1','da 0 a 10']];
	    // create the data store
	    var store = new Ext.data.ArrayStore({
	        fields: ['name', 'values'],
	        data  : [['eta 1','da 0 a 10']]
	    });

	    // manually load local data
	    //store.loadData(emptyData);
	    
        var template = new Ext.XTemplate(
        		'<div style="border: 1px solid silver;border-radius:5px; padding: 2px;">' + 
                '<span width="70px" style="vertical-align: top;">{values}</span>' + 
                '<span align="right"><img onClick="test();" style="vertical-align: top;" src="../img/actions/close_icon-15.png"/>' +
                '</span>'+
                '</div>'
             );  
             
        template.compile();
             
        var valuesColumn = new Ext.grid.TemplateColumn(	{
            header   : 'Values', 
            dataIndex: 'values',
            xtype: 'templatecolumn',
            tpl : template
        });

		this.gridPanel = new Ext.grid.EditorGridPanel({
			store: store,
			columns: [
               {
                   id       :'name',
                   header   : 'Name', 
                   sortable : true, 
                   dataIndex: 'name'
               },
               	   valuesColumn
               ],
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
		var btnAdd = this.panelToolbar.items.items[0];
		btnAdd.on('click', function(){
            // access the Record constructor through the grid's store
            var Slot = this.gridPanel.getStore().recordType;
            var p = new Slot({
                name: 'New Slot 1',
                values: 'New values'
            });
            this.gridPanel.stopEditing();
            this.gridPanel.store.insert(0, p);
            this.gridPanel.startEditing(0, 0);
        }, this);
	}
});
test= function(itemGroupName, expression) {
	alert('funziona!!');
}
