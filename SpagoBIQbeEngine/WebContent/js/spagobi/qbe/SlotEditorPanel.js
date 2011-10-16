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
	
	var params = c.datamart !== undefined ? {'datamartName': c.datamart} : {};
	this.services = this.services || new Array();	
	
	this.services['addCalculatedField'] = this.services['addCalculatedField'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'ADD_CALCULATED_FIELD_ACTION'
		, baseParams: params
		});
	
	this.services['getValuesForQbeFilterLookupService'] = Sbi.config.serviceRegistry.getServiceUrl({
		  serviceName: 'GET_VALUES_FOR_QBE_FILTER_LOOKUP_ACTION'
		, baseParams: params
	});
	
	this.fieldId = c.fieldId;//an be null if click on entity node
	this.firstPage = c.firstPage;
	this.slotWizard = c.slotWizard;
	
	this.initStore(c);
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
    , punctualWindow : null
    , rangeWindow : null
    , rangeToSave : null
    , store: null
    , hasDefault: false
    , fieldId: null
    , firstPage : null
    , expression: null
    , slotWizard: null
    
	, initToolbar: function(c){
	
		this.panelToolbar = new Ext.Toolbar({
			scope: this,
			items: [{
                xtype:'button',
                text: LN('sbi.qbe.bands.addband.btn'),
                iconCls: 'add'
            },{
                xtype:'button',
                text: LN('sbi.qbe.bands.adddefault.btn'),
                iconCls: 'add'
            },{
                xtype:'button',
                text: LN('sbi.qbe.bands.delete.btn'),
                iconCls: 'remove'
            }]
		});
	}
	, initStore: function(c){
	    // create the data store
	    if(c.editStore !== null && c.editStore !== undefined ){
	    	this.store = c.editStore;

	    }else{
		    this.store = new Ext.data.JsonStore({

		        data: {slots:[{ name: "slot 1", valueset: [{type: "range", from: 0, includeFrom: true, to: 100, includeTo: false}, { type: "punctual", values: [100, 101, 201]}]}]},
		        root: 'slots',
		        fields: ['name', 'valueset']
		    });
	    }
	    // looks for default range configuration
	    var defaultRec = null;
	    if(this.store.data != null && this.store.data !== undefined){
			for (var i = 0; i < this.store.data.length; i++) { 
				var record = this.store.getAt(i); 
				var slot = record.data; 
				if(slot.valueset !== null && slot.valueset !== undefined){
					for (var j = 0; j < slot.valueset.length; j++) {
						var val = slot.valueset[j];
						if(val.type == 'default'){
							defaultRec = slot;
							this.hasDefault = true;
							break;
						}
					}
				}
			}
	    }

	}
	, initGrid: function(c) {

        var template = new Ext.XTemplate(
        		'<tpl if="valueset !== null">'+
        		'<tpl for="valueset">'+
        		'<tpl if="type == \'range\'">'+
	        		'<div class="icon-close" id="tpl-slot-val-{[xindex]}">' + 
	        		'<tpl if="includeFrom == true">'+
	        			'&gt;' + 
	        		'</tpl>'+
	        		'<tpl if="includeFrom == false">'+
	        			'&gt;=' + 
	        		'</tpl>'+
	        		'{from} '+ 
	        		'<tpl if="includeTo == true">'+
	        			' &lt;=' + 
	        		'</tpl>'+
	        		'<tpl if="includeTo == false">'+
	        			' &lt;' + 
	        		'</tpl>'+
	        		'{to}'+
	                '</div>'+
                '</tpl>'+
                '<tpl if="type == \'punctual\'">'+
	        		'<div class="icon-close green" id="tpl-slot-val-{[xindex]}">' + 
	                '{values}' + 
      	            '</div>'+
                '</tpl>'+
                '</tpl></tpl>'
             );  
             
        template.compile();
             
        var valuesColumn = new Ext.grid.TemplateColumn({
            header   : LN('sbi.qbe.bands.col.values'), 
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
		        	  if(this.scope.hasDefault == true){
		        		  alert(LN('sbi.qbe.bands.alert.default'));
		        	  }else{
				          var record = this.scope.gridPanel.store.getAt(index);
				          this.scope.openiInsertRangeWindow(record);
		        	  }
		          }
		       }
		       , width: 20
		       , header: LN('sbi.qbe.bands.col.limits')
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
			          if(index !== 0 ){
				          var record = this.scope.gridPanel.store.getAt(index);
				          this.scope.openiInsertPunctualWindow(record);
			          }else{
			        	  if(this.scope.hasDefault == true){
			        		  alert(LN('sbi.qbe.bands.alert.default'));
			        	  }else{
					          var record = this.scope.gridPanel.store.getAt(index);
					          this.scope.openiInsertPunctualWindow(record); 
			        	  }
			        	  
			          }
			       }
			       , width: 20
			       , header:  LN('sbi.qbe.bands.col.vallist')
				   , renderer : function(v, p, record){
			           return '<center><img class="x-mybutton-'+this.id+'" width="21px" height="13px" src="' + this.imgSrc + '"/></center>';
			       }
			       , scope: this
			    })
		    );
	    var nameEditor = new Ext.form.TextField();
	    
		this.gridPanel = new Ext.grid.EditorGridPanel({
			id: 'slot-panel',
			store: this.store,
			columns: [
               {
                   id       :'name',
                   header   :  LN('sbi.qbe.bands.col.name'), 
                   sortable : true, 
                   editor: nameEditor,
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
	            forceFit: true,
	            getRowClass: function(record, index) {
					var slot = record.data; 
					if(slot.valueset !== null && slot.valueset !== undefined){
						for (var j = 0; j < slot.valueset.length; j++) {
							var val = slot.valueset[j];
							if(val.type == 'default'){
								return 'dafault-row';
							}
						}
					}
	            }
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
			        	    			title : LN('sbi.qbe.bands.delete.alert.title'),
			        	    			msg : LN('sbi.qbe.bands.delete.alert.msg'),
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
		        	    	}/*else{
		        	    		var check = id.indexOf('check-tpl-default');
		        	    		var def = slotItem.checked;
		        	    		if(def){
		        	    			this.defaultValueWindow(record);
		        	    		}
		        	    	}*/
	        	    	}
	        	 }
	        }

	    });

		var btnAdd = this.panelToolbar.items.items[0];
		var btnDefault = this.panelToolbar.items.items[1];
		var btnDelete = this.panelToolbar.items.items[2];
		btnAdd.on('click', this.createSlotRowToDisplay, this);
		btnDelete.on('click', this.removeSlot, this);
		btnDefault.on('click', this.createDefault, this);

/*		nameEditor.on('change', function(f, newv, oldv){
			var slot = this.gridPanel.selModel.selection.record;
			slot.set('name', newv);
			slot.commit();
			this.gridPanel.stopEditing();

		}, this);*/
	}
	, openiInsertRangeWindow: function(rec){
		this.expression = this.slotWizard.expression;
		this.rangeWindow = new Sbi.qbe.RangeDefinitionWindow({slotPanel: this, record: rec, id: this.fieldId, expression: this.expression});
		
		this.rangeWindow.mainPanel.doLayout();
		this.rangeWindow.show();
	}
	, openiInsertPunctualWindow: function(rec){
		this.expression = this.slotWizard.expression;
		var lookupStore = this.createLookupStore();		
		lookupStore.load();
		var baseConfig = {
	       store: lookupStore
	     , singleSelect: false
	     , valuesSeparator: Sbi.settings.qbe.filterGridPanel.lookupValuesSeparator
		};
		this.punctualWindow = new Sbi.widgets.FilterLookupPopupWindow(baseConfig);
		this.punctualWindow.on('selectionmade', function(xselection) {
			this.addPunctualVals(xselection.xselection.Values, rec);	
			this.punctualWindow.close();
		}, this);
		this.punctualWindow.show();
	}
	, createSlotRowToDisplay: function(p){
        // access the Record constructor through the grid's store
		var Slot = this.gridPanel.getStore().recordType;
        var p = new Slot({
            name: LN('sbi.qbe.bands.new.name'),
            valueset: null
        });
        this.gridPanel.stopEditing();
        this.gridPanel.store.add(p);
        var idx = this.gridPanel.store.indexOf(p);
        this.gridPanel.startEditing(idx, 0);
	}
	, createDefault: function(){
        // access the Record constructor through the grid's store
        //{type: 'default", name: 'ecco il default''}
        if(this.hasDefault == false){
			var Slot = this.gridPanel.getStore().recordType;
	        var p = new Slot({
	            name: LN('sbi.qbe.bands.default.name'),
	            valueset: [{type: "default", value: ''}]
	        });
	        this.gridPanel.stopEditing();
	        this.store.insert(0, p);
	        this.gridPanel.startEditing(0, 0);
	        this.hasDefault = true;
	        this.gridPanel.getView().refresh();
        }else{
        	alert(LN('sbi.qbe.bands.default.alert'));
        }

	}
	, removeSlot: function(){
        // access the Record constructor through the grid's store
		var slot = this.gridPanel.selModel.selection.record;
        if(slot !== null && slot !== undefined){
        	var idx = this.gridPanel.store.indexOf(slot);
        	if(idx == 0){
        		this.hasDefault = false;
        	}
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
	        text: LN('sbi.qbe.bands.save.btn'),
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
/*        
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
        win.show();*/
	}
	
	, createLookupStore: function() {
		var createStoreUrl = this.services['getValuesForQbeFilterLookupService'];
		
		if (this.fieldId !== null) createStoreUrl += '&ENTITY_ID=' + this.fieldId;
		if (this.expression !== null) {
			createStoreUrl += '&' + Ext.urlEncode({EXPRESSION: this.expression});
			alert(createStoreUrl);
		}
		var store = new Ext.data.JsonStore({
			url: createStoreUrl
		});
		
		store.on('loadexception', function(store, options, response, e) {
			var msg = '';
			var content = Ext.util.JSON.decode( response.responseText );
  			if(content !== undefined) {
  				msg += content.serviceName + ' : ' + content.message;
  			} else {
  				msg += 'Server response is empty';
  			}
	
			Sbi.exception.ExceptionHandler.showErrorMessage(msg, response.statusText);
		});
		return store;	
	}
});

