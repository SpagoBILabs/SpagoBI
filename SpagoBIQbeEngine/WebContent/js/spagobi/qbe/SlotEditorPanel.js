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
		
		//var emptyData = [['eta 1',' 0 a 10']];
	    // create the data store
	    var store = new Ext.data.ArrayStore({
	        fields: ['name', 'values'],
	        data  : [['eta 1',['[ 0 , 10 ]']]]
	    });
//[{name:'slot name 1', values:[{from: {operand:'>', value:'0'}, to:{operand:'<', value:'10'}, descr: 'da 0 a 10'}]]
	    // manually load local data
	    //store.loadData(emptyData);
	    
        var template = new Ext.XTemplate(
        		'<tpl if="values !== null">'+
        		'<tpl for="values">'+
        		'<div width="150px" style="border: 1px solid silver;border-radius:5px; padding: 2px; float: left;" id="tpl-range-{[xindex]}">' + 
                '<span style="vertical-align: top;">{[values]}</span>' + 
                //'<span align="right"><img onClick="this.eraseRange(\"{[xindex]}\",\"{.}\");" style="vertical-align: top;" src="../img/actions/close_icon-15.png"/>' +
                '<span align="right"><img onClick="test({[xindex]})" style="vertical-align: top;" src="../img/actions/close_icon-15.png"/>' +                
                '</span>'+
                '</div>'+
                '</tpl></tpl>',
                { eraseRange : function (i) {
                	alert('"'+i+'"');
                }
                }
             );  
             
        template.compile();
             
        var valuesColumn = new Ext.grid.TemplateColumn(	{
            header   : 'Values', 
            dataIndex: 'values',
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
            values: null
        });
        this.gridPanel.stopEditing();
        this.gridPanel.store.insert(0, p);
        this.gridPanel.startEditing(0, 0);
	}
	, addRange: function(rowIndex, rec){
		var opFrom = rowIndex.from.operand ;
		var dsOpFrom = ']';
		if(opFrom == 2){
			dsOpFrom = '[';
		}
		var valFrom = rowIndex.from.value;
		var opTo = rowIndex.to.operand;
		var dsOpTo = '[';
		if(opTo == 4){
			dsOpTo = ']';
		}
		var valTo = rowIndex.to.value;
		var newVal = dsOpFrom+' '+valFrom+ ','+valTo+' '+dsOpTo;
		if(rec.data.values == null){
			rec.data.values = new Array();
		}
		rec.data.values.push(newVal);
		rec.commit();
    }
	
});
var toerase = null;
test= function(idx) {
	//alert(idx);
	toerase = idx;
	var elementToErase = Ext.get('tpl-range-'+idx);
	elementToErase.remove();
}
