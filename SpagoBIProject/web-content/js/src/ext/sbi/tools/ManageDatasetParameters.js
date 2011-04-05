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
 * [description]
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
 * Authors - Chiara Chiarelli
 */
Ext.ns("Sbi.tools");

Sbi.tools.ManageDatasetParameters = function(config) { 
	
	this.severityStore = config.severityStore;

	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "THR_VAL_DELETE"};
	
	this.services = new Array();
	
	this.services['deleteThrValService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_THRESHOLDS_ACTION'
		, baseParams: paramsDel
	});
	
	this.typesStore = new Ext.data.SimpleStore({
        fields: ['type'],
        data: [['String'],['Number'],['Raw'],['Generic']],
        autoLoad: false
    });

	// Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
	//these are grid values for range type threshold value
	this.userColumns =  [
	    {
	    	header: LN('sbi.generic.name'), 
	    	width: 110, 
			id:'name',
			sortable: true, 
			dataIndex: 'name',  
			editor: new Ext.form.TextField({
				 maxLength:20,
				 allowBlank: false,
	             validationEvent:true
			})
	    },{
			header: LN('sbi.generic.type'), 
			width: 90, 
			id:'type',
			sortable: true, 
			dataIndex: 'type',  		
			editor: new Ext.form.ComboBox({
	        	  name: 'type',
	              store: this.typesStore,
	              displayField: 'type',   // what the user sees in the popup
	              valueField: 'type',        // what is passed to the 'change' event
	              typeAhead: true,
	              forceSelection: true,
	              mode: 'local',
	              triggerAction: 'all',
	              selectOnFocus: true,
	              editable: false,
	              allowBlank: false,
	              validationEvent:true
	          })
		}			
	];
    
	 var cm = new Ext.grid.ColumnModel({
	        columns: this.userColumns
	    });
	 
	 this.store = new Ext.data.JsonStore({
		    //id : 'id',
		    fields: ['name'
     	          , 'type'
      	          ],
		    idIndex: 0,
		    data:{}
		});
	 
	 var tb = new Ext.Toolbar({
	    	buttonAlign : 'left',
	    	items:[new Ext.Toolbar.Button({
	            text: LN('sbi.attributes.add'),
	            iconCls: 'icon-add',
	            handler: this.onAdd,
	            width: 30,
	            scope: this
	        }), '-', new Ext.Toolbar.Button({
	            text: LN('sbi.attributes.delete'),
	            iconCls: 'icon-remove',
	            handler: this.onDelete,
	            width: 30,
	            scope: this
	        }), '-'
	    	]
	    });
	 
		 var sm = new Ext.grid.RowSelectionModel({
	         singleSelect: true
	     });

	    // create the editor grid
	    var grid = {
	    	xtype: 'grid',
	        store: this.store,
	        layout: 'fit',
	        cm: cm,
	        sm: sm,
	        width: 240,
	        height: 110,
	        //autoExpandColumn: 'label', // column with this id will be expanded
	        frame: true,
	        clicksToEdit: 2,
	        tbar: tb
	    };

    var c = Ext.apply( {}, config, grid);

    // constructor
    Sbi.tools.ManageDatasetParameters.superclass.constructor.call(this, c);
    
    this.on('beforeedit', function(e) {
    	var t = Ext.apply({}, e);
		var col = t.column;
		this.currentRowRecordEdited = t.row;	 	
    }, this);
    
    this.on('afteredit', function(e) {   	
		var col = e.column;
		var row = e.row;	   	
    }, this);

};

Ext.extend(Sbi.tools.ManageDatasetParameters, Ext.grid.EditorGridPanel, {
  
	
  	reader:null
  	,currentRowRecordEdited:null
  	,services:null
  	,writer:null
  	,store:null
  	,userColumns:null
  	,editor:null
  	,userGrid:null

  	,loadItems: function(pars){
		this.store.loadData(pars);
	}

    ,onAdd: function (btn, ev) {
        var emptyRecToAdd = new Ext.data.Record({
			  id: 0,
              type: 'String'     
			 });   
        this.store.insert(0,emptyRecToAdd);
    }
    
    ,onDelete: function() {   	
        var rec = this.getSelectionModel().getSelected();
        this.store.remove(rec);
        this.store.commitChanges();
     }
    
    ,getParsArray: function(){
	    var arrayPars = new Array();
			var storePars = this.getStore();
			var length = storePars.getCount();
			for(var i = 0;i< length;i++){
				var item = storePars.getAt(i);
				var data = item.data;
				arrayPars.push(data);
			}
		return arrayPars;
	}

});

