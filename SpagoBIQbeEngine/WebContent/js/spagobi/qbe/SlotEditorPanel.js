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
    , rangeWindow : null
    , rangeToSave : null
    
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

	    // create the data store
		//data: [{ name: "slot 1", valueset: [{type: "range", from: 0, includeFrom: true, to: 100, includeTo: false}, { type: "punctual", values: [100, 101, 201]}, { type: "default", value: 0}]}]

	    var store = new Ext.data.JsonStore({
	        //url: 'get-images.php',
	        data: {slots:[{ name: "slot 1", valueset: [{type: "range", from: 0, includeFrom: true, to: 100, includeTo: false}, { type: "punctual", values: [100, 101, 201]}, { type: "default", value: 0}]}]},
	        root: 'slots',
	        fields: ['name', 'valueset']
	    });
	    // manually load local data
	    
        var template = new Ext.XTemplate(
        		'<tpl if="valueset !== null">'+
        		'<tpl for="valueset">'+
        		'<tpl if="type == \'range\'">'+
	        		'<div width="150px" style="border: 1px solid red;border-radius:5px; padding: 2px; float: left; margin-right: 1px;" id="tpl-slot-val-{[xindex]}">' + 
	        		'<span style="vertical-align: top;">'+
	        		'<tpl if="includeFrom == true">'+
	        			'[' + 
	        		'</tpl>'+
	        		'<tpl if="includeFrom == false">'+
	        			']' + 
	        		'</tpl>'+
	        		'{from},{to}' + 
	        		'<tpl if="includeTo == true">'+
	        			']' + 
	        		'</tpl>'+
	        		'<tpl if="includeTo == false">'+
	        			'[' + 
	        		'</tpl>'+
	        		'</span>' + 
	                '<span align="right"><img onClick="test({[xindex]})" style="vertical-align: top;" src="../img/actions/close_icon-15.png"/>' +                
	                '</span>'+
	                '</div>'+
                '</tpl>'+
                '<tpl if="type == \'punctual\'">'+
	        		'<div width="150px" style="border: 1px solid green;border-radius:5px; padding: 2px; float: left; margin-right: 1px;" id="tpl-slot-val-{[xindex]}">' + 
	                '<span style="vertical-align: top;">{values}</span>' + 
	                '<span align="right"><img onClick="test({[xindex]})" style="vertical-align: top;" src="../img/actions/close_icon-15.png"/>' +                
	                '</span>'+
	                '</div>'+
                '</tpl>'+
                '<tpl if="type == \'default\'">'+
	        		'<div width="150px" style="border: 1px solid blue;border-radius:5px; padding: 2px; float: left; margin-right: 1px;" id="tpl-slot-val-{[xindex]}">' + 
	                '<span style="vertical-align: top;">{value}</span>' + 
	                '<span align="right"><img onClick="test({[xindex]})" style="vertical-align: top;" src="../img/actions/close_icon-15.png"/>' +                
	                '</span>'+
	                '</div>'+
	            '</tpl>'+
                '</tpl></tpl>'
             );  
             
        template.compile();
             
        var valuesColumn = new Ext.grid.TemplateColumn(	{
            header   : 'Values', 
            dataIndex: 'valueset',
            xtype: 'templatecolumn',
            tpl : template
        });
	    // button-columns
	    var rangeButtonColumn = new Ext.grid.ButtonColumn(
		    Ext.apply({
		       dataIndex: 'range'
		       , imgSrc: '../img/actions/range.gif'
		       , clickHandler:function(e, t){
		          var index = this.scope.gridPanel.getView().findRowIndex(t);
		          var record = this.scope.gridPanel.store.getAt(index);
		          this.scope.openiInsertRangeWindow(record);
		       }
		       , width: 20
		       , header: 'Range'
		       , renderer : function(v, p, record){
		           return '<center><img class="x-mybutton-'+this.id+'" width="29px" height="16px" src="' + this.imgSrc + '"/></center>';
		       }
		       , scope: this
		    })
	    );
	    var punctualButtonColumn = new Ext.grid.ButtonColumn(
			    Ext.apply({
			       dataIndex: 'dots'
			       , imgSrc: '../img/actions/dots.gif'
			       , clickHandler:function(e, t){
			          var index = this.scope.gridPanel.getView().findRowIndex(t);
			          var record = this.scope.gridPanel.store.getAt(index);
			          this.scope.openiInsertPunctualWindow(record);
			       }
			       , width: 20
			       , header: 'Punctual'
				   , renderer : function(v, p, record){
			           return '<center><img class="x-mybutton-'+this.id+'" width="21px" height="13px" src="' + this.imgSrc + '"/></center>';
			       }
			       , scope: this
			    })
		    );
	    
		this.gridPanel = new Ext.grid.EditorGridPanel({
			store: store,
			columns: [
               {
                   id       :'name',
                   header   : 'Name', 
                   sortable : true, 
                   editor: new Ext.form.TextField(),
                   dataIndex: 'name'
               },
               	   valuesColumn
               	,  rangeButtonColumn
               	,  punctualButtonColumn
               ],
	        tbar: this.toolbar,
	        clicksToEdit:2,
	        frame: true,
	        border:true,  
	        style:'padding:0px',
	        iconCls:'icon-grid',
	        collapsible:false,
	        layout: 'fit',
	        viewConfig: {
	            forceFit: true
	        },
	        plugins :[rangeButtonColumn,  punctualButtonColumn],
	        enableDragDrop:false	
	    });
		var btnAdd = this.panelToolbar.items.items[0];

		btnAdd.on('click', this.createSlotRowToDisplay, this);
	}
	, openiInsertRangeWindow: function(rec){
		this.rangeWindow = new Sbi.qbe.RangeDefinitionWindow({slotPanel: this, record: rec});
		
		this.rangeWindow.mainPanel.doLayout();
		this.rangeWindow.show();
	}
	, openiInsertPunctualWindow: function(rec){
		this.punctualWindow = new Sbi.qbe.PunctualDefinitionWindow({slotPanel: this, record: rec});
		
		this.punctualWindow.mainPanel.doLayout();
		this.punctualWindow.show();
	}
	, createSlotRowToDisplay: function(p){
        // access the Record constructor through the grid's store
		var Slot = this.gridPanel.getStore().recordType;
        var p = new Slot({
            name: 'New Slot',
            valueset: null
        });
        this.gridPanel.stopEditing();
        this.gridPanel.store.insert(0, p);
        this.gridPanel.startEditing(0, 0);
	}
	, addRange: function(rowIndex, rec){
		var opFrom = rowIndex.from.operand ;
		var includeFrom = false;
		if(opFrom == 2){
			includeFrom = true;
		}
		var opTo = rowIndex.to.operand;
		var includeTo = false;
		if(opTo == 4){
			includeTo = true;
		}
		var item ={type: 'range', from: rowIndex.from.value, includeFrom: includeFrom, to: rowIndex.to.value, includeTo: includeTo};
		if(rec.data.valueset == null){
			rec.data.valueset = new Array();
		}
		rec.data.valueset.push(item);
		rec.commit();
    }
	, addPunctualVals: function(vals, rec){
		var item ={type: 'punctual', values: vals};
		if(rec.data.valueset == null){
			rec.data.valueset = new Array();
		}
		rec.data.valueset.push(item);
		rec.commit();
    }
	, 
});
var toerase = null;
test= function(idx) {
	//alert(idx);
	toerase = idx;
	var elementToErase = Ext.get('tpl-slot-val-'+idx);
	elementToErase.remove();
	this.fireEvent('onSlotRemove', elementToErase);
}
