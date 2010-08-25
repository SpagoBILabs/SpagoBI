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
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageThresholdValues = function(config) { 
	
	this.severityStore = config.severityStore;
	
	var cField = new Ext.ux.ColorField({ value: '#FFFFFF', msgTarget: 'qtip', fallback: true});
	cField.on('select', function(f,val){
		this.store.getAt(this.currentRowRecordEdited).set('color',val);
		this.getView().refresh();		
		},this);
	
	// Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
	this.userColumns =  [
	    {
	        name: 'idThrVal',
	        hidden: true
	    },{
	    	header: 'Position', 
	    	id:'position',
	    	width: 50, 
	    	sortable: true, 
	    	xtype: 'numbercolumn',
	    	dataIndex: 'position', 
	    	editor: new Ext.form.NumberField({})	
	    },{
	    	header: 'Label', 
	    	width: 60, 
			id:'label',
			sortable: true, 
			dataIndex: 'label',  
			editor: new Ext.form.TextField({})
	    },{
	    	header: 'Min', 
	    	width: 50, 
			id:'min',
			sortable: true, 
			xtype: 'numbercolumn',
			dataIndex: 'min',  
			editor: new Ext.form.NumberField({})				
		},{
			header: 'Include?', 
			width: 50, 
			xtype: 'booleancolumn',
			sortable: true, 
			dataIndex: 'minIncluded',
			editor:new Ext.form.Checkbox({})
		},{
			header: 'Max', 
			width: 50, 
			xtype: 'numbercolumn',
			sortable: true, 
			dataIndex: 'max',
			editor: new Ext.form.NumberField({})	
		},{
			header: 'Include?', 
			width: 50, 
			xtype: 'booleancolumn',
			sortable: true, 
			dataIndex: 'maxIncluded',
			editor:new Ext.form.Checkbox({})
		},{
			header: 'Severity', 
			width: 60, 
			id:'severityCd',
			sortable: true, 
			dataIndex: 'severityCd',  		
			editor: new Ext.form.ComboBox({
	        	  name: 'severityCd',
	              store: this.severityStore,
	              displayField: 'severityCd',   // what the user sees in the popup
	              valueField: 'severityCd',        // what is passed to the 'change' event
	              typeAhead: true,
	              forceSelection: true,
	              mode: 'local',
	              triggerAction: 'all',
	              selectOnFocus: true,
	              editable: false,
	              allowBlank: false,
	              validationEvent:true
	          })
		},{	
			header: 'Color', 
			width: 60, 
			id:'color',
			sortable: true, 
			dataIndex: 'color',  
			editor: cField,
			renderer : function(v, metadata, record){
				if(metadata!=null){
				   metadata.attr = ' style="background:'+v+';"';
				}
			   return v;  
	       }
		},{	
			header: 'Value', 
			width: 40, 
			id:'val',
			sortable: true, 
			dataIndex: 'val',  
			editor: new Ext.form.NumberField({})
		}					
	];
    
	 var cm = new Ext.grid.ColumnModel({
	        // specify any defaults for each column
	        defaults: {
	            sortable: true // columns are not sortable by default           
	        },
	        columns: this.userColumns
	    });
	 
	 this.store = new Ext.data.JsonStore({
		    id : 'idThrVal',
		    fields: ['idThrVal'
     	          , 'label'
      	          , 'position'
      	          , 'min'
      	          , 'minIncluded'
      	          , 'max'
      	          , 'maxIncluded'
      	          , 'val'
      	          , 'color'
      	          , 'severityCd'
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

	    // create the editor grid
	    var grid = {
	    	xtype: 'grid',
	        store: this.store,
	        layout: 'fit',
	        cm: cm,
	        width: 400,
	        height: 250,
	        //autoExpandColumn: 'label', // column with this id will be expanded
	        frame: true,
	        clicksToEdit: 2,
	        tbar: tb
	    };

    var c = Ext.apply( {}, config, grid);

    // constructor
    Sbi.kpi.ManageThresholdValues.superclass.constructor.call(this, c);
    
    this.on('beforeedit', function(e) {
    	var t = Ext.apply({}, e);
		var col = t.column;
		this.currentRowRecordEdited = t.row;	
    	
    }, this);
    
    this.on('afteredit', function(e) {
    	
		var col = e.column;
		var row = e.row;	
    	
    }, this);

}

Ext.extend(Sbi.kpi.ManageThresholdValues, Ext.grid.EditorGridPanel, {
  
	
  	reader:null
  	,currentRowRecordEdited:null
  	,services:null
  	,writer:null
  	,store:null
  	,userColumns:null
  	,editor:null
  	,userGrid:null
  	,severityStore:null
	
  	,loadItems: function(thrValues){
		this.store.loadData(thrValues);
	}

    ,onAdd: function (btn, ev) {
        var emptyRecToAdd = new Ext.data.Record({
			  idThrVal: 0,
			  label: '',
              position: '',
              min: '',
              minIncluded: false,
              max: '',
              maxIncluded: false,
              val: '',
              color: '#FFFFFF',
              severityCd: 'LOW'     
			 });   
        this.store.insert(0,emptyRecToAdd);
    }

    ,onDelete: function() {
        var rec = this.getSelectionModel().getSelected();

        var remove = true;

        this.store.remove(rec);
        this.store.commitChanges();

        if(!remove){
        	//readd record
            this.store.add(rec);
            this.store.commitChanges();
        }
     }


});

