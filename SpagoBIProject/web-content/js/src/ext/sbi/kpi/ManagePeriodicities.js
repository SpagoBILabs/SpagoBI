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

Sbi.kpi.ManagePeriodicities = function(config) { 
	
	var paramsList = {MESSAGE_DET: "PERIODICTIES_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "PERIODICITY_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "PERIODICITY_DELETE"};
	
	this.services = new Array();
	
	this.services['managePrListService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_PERIODICITIES_ACTION'
		, baseParams: paramsList
	});
	
	this.services['savePrService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_PERIODICITIES_ACTION'
		, baseParams: paramsSave
	});
	
	this.services['deletePrService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_PERIODICITIES_ACTION'
		, baseParams: paramsDel
	});
	
	this.store = new Ext.data.JsonStore({
    	autoLoad: false    	  
    	, id : 'id'		
        , fields: ['idPr'
         	          , 'name'
          	          , 'months'
          	          , 'days'
          	          , 'hours'
          	          , 'mins'
          	          ]
    	, root: 'rows'
		, url: this.services['managePrListService']		
	});
	
	var monthsStore = new Ext.data.SimpleStore({
	    fields: ['months'],
  	    autoLoad: false,
	    data:[[0],[1],[2],[3],[4],[5],[6],[7],[8],[9],[10],[11],[12]]
	});
	
	var daysStore = new Ext.data.SimpleStore({
	    fields: ['days'],
  	    autoLoad: false,
  	    data:[[0],[1],[2],[3],[4],[5],[6],[7],[8],[9],[10],
  	          [11],[12],[13],[14],[15],[16],[17],[18],[19],[20],
  	          [21],[22],[23],[24],[25],[26],[27],[28],[29],[30],[31]]
	});
	
	var hoursStore = new Ext.data.SimpleStore({
	    fields: ['hours'],
  	    autoLoad: false,
  	    data:[[0],[1],[2],[3],[4],[5],[6],[7],[8],[9],[10],
  	          [11],[12],[13],[14],[15],[16],[17],[18],[19],[20],
  	          [21],[22],[23],[24]]
	});
	
	var minsStore = new Ext.data.SimpleStore({
	    fields: ['mins'],
  	    autoLoad: false,
  	    data:[[0],[1],[2],[3],[4],[5],[6],[7],[8],[9],[10],
  	          [11],[12],[13],[14],[15],[16],[17],[18],[19],[20],
  	          [21],[22],[23],[24],[25],[26],[27],[28],[29],[30],
  	          [31],[32],[33],[34],[35],[36],[37],[38],[39],[40],
  	          [41],[42],[43],[44],[45],[46],[47],[48],[49],[50],
  	          [51],[52],[53],[54],[55],[56],[57],[58],[59],[60]]
	});
	
	// Let's pretend we rendered our grid-columns with meta-data from our ORM framework.
	this.userColumns =  [
	    {
	        name: 'idPr',
	        hidden: true
	    },{
	    	header: 'Name', 
	    	id:'name',
	    	width: 80, 
	    	sortable: true, 
	    	dataIndex: 'name', 
	    	editor: new Ext.form.TextField({})	
	    },{
	    	header: 'Months', 
	    	width: 60, 
			id:'months',
			sortable: true, 
			dataIndex: 'months',  
			editor: new Ext.form.ComboBox({
	        	  name: 'months',
	              store: monthsStore,
	              displayField: 'months',   // what the user sees in the popup
	              valueField: 'months',        // what is passed to the 'change' event
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
	    	header: 'Days', 
	    	width: 60, 
			id:'days',
			sortable: true, 
			dataIndex: 'days',  
			editor: new Ext.form.ComboBox({
	        	  name: 'days',
	              store: daysStore,
	              displayField: 'days',   // what the user sees in the popup
	              valueField: 'days',        // what is passed to the 'change' event
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
			header: 'Hours', 
			width: 60, 
			sortable: true, 
			dataIndex: 'hours',
			editor: new Ext.form.ComboBox({
	        	  name: 'hours',
	              store: hoursStore,
	              displayField: 'hours',   // what the user sees in the popup
	              valueField: 'hours',        // what is passed to the 'change' event
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
			header: 'Minutes', 
			width: 60, 
			id:'mins',
			sortable: true, 
			dataIndex: 'mins',  		
			editor: new Ext.form.ComboBox({
	        	  name: 'mins',
	              store: minsStore,
	              displayField: 'mins',   // what the user sees in the popup
	              valueField: 'mins',        // what is passed to the 'change' event
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
	        // specify any defaults for each column
	        defaults: {
	            sortable: true // columns are not sortable by default           
	        },
	        columns: this.userColumns
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
	        }), '-', new Ext.Toolbar.Button({
	            text: 'Save',
	            iconCls: 'icon-save',
	            handler: this.onSave,
	            width: 30,
	            scope: this
	        })
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
	        width: 400,
	        height: 250,
	        //autoExpandColumn: 'label', // column with this id will be expanded
	        frame: true,
	        clicksToEdit: 2,
	        tbar: tb
	    };

    var c = Ext.apply( {}, config, grid);

    // constructor
    Sbi.kpi.ManagePeriodicities.superclass.constructor.call(this, c);
	
    this.store.load();

}

Ext.extend(Sbi.kpi.ManagePeriodicities, Ext.grid.EditorGridPanel, {
  
	
  	reader:null
  	,currentRowRecordEdited:null
  	,services:null
  	,writer:null
  	,store:null
  	,userColumns:null
  	,editor:null
  	,userGrid:null
  	,severityStore:null

    ,onAdd: function (btn, ev) {

        var emptyRecToAdd = new Ext.data.Record({
        	  idPr: 0,
        	  name: '',
        	  months: 0,
        	  days: 0,
        	  hours: 0,
        	  mins: 0 
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

    ,onSave: function() {
        alert('Save');
     }
    
});

