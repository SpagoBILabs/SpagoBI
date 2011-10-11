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
	    
	   // {name: 'Default Slot Value', valueset: [{type: "default", value: 0}]},
	    var store = new Ext.data.JsonStore({
	        //url: 'get-images.php',
	        data: {slots:[{ name: "slot 1", valueset: [{type: "range", from: 0, includeFrom: true, to: 100, includeTo: false}, { type: "punctual", values: [100, 101, 201]}]}]},
	        root: 'slots',
	        fields: ['name', 'valueset']
	    });
	    // manually load local data
	   	store.insert(0,new store.recordType({name: 'Default Slot Value', valueset: [{type: "default", value: ''}]}));
	  	store.commitChanges();
	  
        var template = new Ext.XTemplate(
        		'<tpl if="valueset !== null">'+
        		'<tpl for="valueset">'+
        		'<tpl if="type == \'range\'">'+
	        		'<div class="icon-close" id="tpl-slot-val-{[xindex]}">' + 
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
	                '</div>'+
                '</tpl>'+
                '<tpl if="type == \'punctual\'">'+
	        		'<div class="icon-close green" id="tpl-slot-val-{[xindex]}">' + 
	                '{values}' + 
      	            '</div>'+
                '</tpl>'+
               '<tpl if="type == \'default\'">'+
	        		'<div id="tpl-default-val">' + 
	        		'<input type="checkbox" id="check-tpl-default" value="" '+
	        		'<tpl if="value != \'\'">'+
	        		' checked="true" ' +
	        		'</tpl>'+	        		
	        		'/> '+
	        		'<tpl if="value != \'\'">'+
	        		'&nbsp; VALUE: {value}' +
	        		'</tpl>'+
	                '</div>'+
	            '</tpl>'+
                '</tpl></tpl>'
             );  
             
        template.compile();
             
        var valuesColumn = new Ext.grid.TemplateColumn({
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
		          if(index !== 0){
			          var record = this.scope.gridPanel.store.getAt(index);
			          this.scope.openiInsertRangeWindow(record);
			       }else{
		        	  alert('Operation denied for default slot');
		          }
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
			          if(index !== 0){
				          var record = this.scope.gridPanel.store.getAt(index);
				          this.scope.openiInsertPunctualWindow(record);
			          }else{
			        	  alert('Operation denied for default slot');
			          }
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
			id: 'slot-panel',
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
	        enableDragDrop:false,
	        scope: this,
	        listeners:{
	        	 scope: this,
	        	 cellclick: function(grid, rowIndex, columnIndex, e) {
	        			// Get the Record for the row
	        	        var record = grid.getStore().getAt(rowIndex);
	        	        // Get field name for the column
	        	        var fieldName = grid.getColumnModel().getDataIndex(columnIndex);
	        	    	var slotItem = e.getTarget();
	        	    	var id = slotItem.id;
	        	    	if(id !== undefined && id != null && id !== ''){
		        	    	var startIndex = id.indexOf('tpl-slot-val-');
		        	    	if(startIndex !== -1){
			        	    	var itemIdx = id.substring(startIndex + ('tpl-slot-val-'.length));
			        	    	var valuesSets = record.data.valueset;
			        	    	try{
	
			        	    		Ext.MessageBox.show({
			        	    			title : 'Slot item deletion',
			        	    			msg : 'Confirm item delete?',
			        	    		   	buttons: Ext.Msg.YESNO,
			        	    		   	fn: function(btn) {
			        	    				if(btn === 'yes') {
			    			        	    	var idx = parseInt(itemIdx) ;
							        	    	var toremove = record.data.valueset[idx-1];
							        	    	record.data.valueset.remove(toremove);
							        	    	record.commit();
			        	    				}
			        	    			},
			        	    			scope: this
			        	    		});
			        	    	}catch(err){
			        	    		
			        	    	}
		        	    	}else{
		        	    		var check = id.indexOf('check-tpl-default');
		        	    		var def = slotItem.checked;
		        	    		if(def){
		        	    			this.defaultValueWindow(record);
		        	    		}
		        	    	}
	        	    	}
	        	 }
	        }

	    });

		var btnAdd = this.panelToolbar.items.items[0];
		var btnDelete = this.panelToolbar.items.items[1];
		btnAdd.on('click', this.createSlotRowToDisplay, this);
		btnDelete.on('click', this.removeSlot, this);

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
        this.gridPanel.store.insert(1, p);
        this.gridPanel.startEditing(1, 0);
	}
	, removeSlot: function(){
        // access the Record constructor through the grid's store
		var slot = this.gridPanel.selModel.selection.record;
        if(slot !== null && slot !== undefined){

            this.gridPanel.store.remove(slot);
            this.gridPanel.store.commitChanges();


        }

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
	, defaultValueWindow: function(record){
		
		if(record.data.valueset == null){
			record.data.valueset = new Array();
		}
		var defVal = new Ext.form.TextField();
		var item = record.data.valueset[0];
		
		defVal.on('change', function(field,newValue,oldValue){
			item.value = newValue;
		}, this);
		
		var btnFinish = new Ext.Button({
	        text: 'Save',
	        disabled: false,
	        scope: this,
	        handler : function(){
				record.data.valueset[0]=item;
				record.commit();
				win.close();
			}

		});
		
        var defPanel = new Ext.form.FormPanel({
            layout: 'hbox',
            width: 120,
            height: 70,
		    bbar: ['->',
		        btnFinish
		    ],
            items: [defVal]
        });
        
        var win = new Ext.Window({
            layout: 'fit',
            title: 'Type default value',
            width: 150,
            height: 90,
            closable: false,
            resizable: false,
            draggable: false,
            plain: true,
            items: [defPanel]
        });
        win.show();
	}

});

