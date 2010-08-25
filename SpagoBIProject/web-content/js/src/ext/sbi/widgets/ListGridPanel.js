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
 * Authors - Chiara Chiarelli (chiara.chiarelli@eng.it)
 */

/*
 * Elements expected in the config parameter:
  
 *  config.manageListService: Service that returns the list of items
 *	config.saveItemService: Service that saves an item
 *	config.deleteItemService: Service that deletes an item
 *  Services Example:
    var paramsList = {MESSAGE_DET: "RESOURCES_LIST"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "RESOURCE_DELETE"};
	
	config.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_RESOURCES_ACTION'
		, baseParams: paramsList
	});
	config.deleteItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_RESOURCES_ACTION'
		, baseParams: paramsDel
	});

 *	config.fields: array of fields that are returned by the manageListService
 *  Example: ['id', 'name', 'code', 'description', 'typeCd']
    
 *	config.emptyRecToAdd: Empty record to add
 *  Example:
    new Ext.data.Record({id: 0,
						 name:'', 
						 code:'', 
						 description:'',
						 typeCd: '' });:
						 
 *	config.gridColItems: Columns to be put in the grid
 *  Example:
    [{id:'name',header: LN('sbi.generic.name'), width: 50, sortable: true, locked:false, dataIndex: 'name'},
     {header: LN('sbi.generic.code'), width: 150, sortable: true, dataIndex: 'code'}]	
     
 *  config.panelTitle: Title of the whole list-detail form
 *  config.listTitle: Title of the list
 *  Example:
    config.panelTitle = LN('sbi.generic.panelTitle');
    config.listTitle =  LN('sbi.generic.listTitle');  				 
 * 
 */
Ext.ns("Sbi.widgets");

Sbi.widgets.ListGridPanel = function(config) {
	
	var conf = config.configurationObject;
	this.services = new Array();
	this.services['manageListService'] = conf.manageListService;
	this.services['deleteItemService'] = conf.deleteItemService;
	
	this.gridColItems = conf.gridColItems;
	this.panelTitle = conf.panelTitle;
	this.listTitle = conf.listTitle;  	  
	this.idKeyForGrid = conf.idKey;
	this.ddGroup = conf.dragndropGroup;
	this.reference = conf.referencedCmp;
	this.drawSelectColumn = conf.drawSelectColumn;  

	this.mainElementsStore = new Ext.data.JsonStore({
    	autoLoad: false    	  
    	//, id : 'id'		
    	, fields: conf.fields
    	, root: 'rows'
		, url: this.services['manageListService']		
	});
	this.initWidget();
	   
	this.mainElementsStore.load();	
   	
	var c = Ext.apply({}, config, this.mainGrid);
   	
   	Sbi.widgets.ListDetailForm.superclass.constructor.call(this,c);	
};

Ext.extend(Sbi.widgets.ListGridPanel, Ext.grid.GridPanel, {
	
	 panelTitle: null
	, listTitle: null
	, mainElementsStore:null
	, colModel:null
	, gridColItems: null
	, mainGrid: null
	, rowselModel:null
	, idKeyForGrid : 'id'
	, ddGroup : null
	, reference : null
	, drawSelectColumn: null
	
	,initWidget: function(){
	
	    this.selectColumn = new Ext.grid.ButtonColumn({
		       header:  ' '
		       ,iconCls: 'icon-execute'
		       ,scope: this
		       ,clickHandler: function(e, t) {
		          var index = this.grid.getView().findRowIndex(t);	          
		          var selectedRecord = this.grid.store.getAt(index);
		          var itemId = selectedRecord.get('id');
		          this.grid.fireEvent('select', itemId, index);
		       }
		       ,width: 25
		       ,renderer : function(v, p, record){
		           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
		       }
	     }); 
        
        this.deleteColumn = new Ext.grid.ButtonColumn({
 	       header:  ' '
 	       ,iconCls: 'icon-remove'
 	       ,scope: this
 	       ,initialConfig: this.idKeyForGrid
 	       ,clickHandler: function(e, t) {   
        	//this.grid is called since this is the only name that can be used. Look at ButtonColumn.js
 	          var index = this.grid.getView().findRowIndex(t);	
        	  var selectedRecord =	this.grid.getSelectionModel().getSelected();
 	          var itemId = selectedRecord.get(this.initialConfig);
 	          this.grid.fireEvent('delete', itemId, index);
 	       }
 	       ,width: 25
 	       ,renderer : function(v, p, record){
 	           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
 	       }
         });
       
        this.gridColItems.push(this.deleteColumn);  
        
        if(this.drawSelectColumn){
        	this.gridColItems.push(this.selectColumn); 
        }
        this.colModel = new Ext.grid.ColumnModel(this.gridColItems);

 	    this.tb = new Ext.Toolbar({
 	    	buttonAlign : 'right',
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.generic.add'),
 	            iconCls: 'icon-add',
 	            handler: this.addNewItem,
 	            width: 30,
 	            ref : this.renference,
 	            scope: this
 	            })
 	    	]
 	    });
 	    
 	  // var filteringToolbar = new Sbi.widgets.FilteringToolbar({store: this.store});
 	   var pagingBar = new Ext.PagingToolbar({
	        pageSize: 16,
	        store: this.mainElementsStore,
	        displayInfo: true,
	        displayMsg: '', 
	        scope: this,
	        emptyMsg: "No topics to display"	        
	    }); 	   
 	  
 	   var pluginsToAdd;
 	   if(this.drawSelectColumn){
  		  //pluginsToAdd = [this.deleteColumn, this.selectColumn]; 
 		   pluginsToAdd = [this.selectColumn];
        }else{
     	  pluginsToAdd = this.deleteColumn; 
        }
 	   
 	  this.rowselModel = new Ext.grid.RowSelectionModel({
           singleSelect: true
       });
 	   
 	   this.mainGrid = new Ext.grid.GridPanel({
	                  ds: this.mainElementsStore,   	                  
	                  colModel: this.colModel,
	                  plugins: pluginsToAdd ,
	                  selModel: this.rowselModel,
	                  layout: 'fit',
	                 // autoExpandColumn: 'name',
	                  height: 550,
	                  width: 270,
	                  scope: this,
	                  title: this.listTitle,
		              bbar: pagingBar,
	                  tbar: this.tb,
	                  stripeRows: false,
	                  enableDragDrop: true,
	                  ddGroup: this.ddGroup,
	                  listeners: {
   							'delete': {
					     		fn: this.deleteSelectedItem,
					      		scope: this
					    	} ,
					    	'select': {
					     		fn: this.sendSelectedItem,
					      		scope: this
					    	} ,
                  			viewready: function(g) {//g.getSelectionModel().selectRow(0); 
					    		
					    	} 
                         }
	                  });

	}

	, addNewItem : function(){
		alert('Add method Needs to be written');
	}
	
	, deleteSelectedItem: function(itemId, index) {
		alert('Delete method Needs to be written');
	}
	
	//METHOD TO BE OVERRIDDEN IN EXTENDED ELEMENT!!!!!
	,save : function() {		
		alert('Abstract Method: it needs to be overridden');
    }

});
