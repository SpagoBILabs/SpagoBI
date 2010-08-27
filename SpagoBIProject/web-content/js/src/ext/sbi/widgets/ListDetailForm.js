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
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "RESOURCE_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "RESOURCE_DELETE"};
	
	config.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_RESOURCES_ACTION'
		, baseParams: paramsList
	});
	config.saveItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_RESOURCES_ACTION'
		, baseParams: paramsSave
	});
	config.deleteItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_RESOURCES_ACTION'
		, baseParams: paramsDel
	});

 *	config.fields: array of fields that are returned by the manageListService
 *  Example: ['id', 'name', 'code', 'description', 'typeCd']
  
 *	tabItems: Array of tabs that will be put in the Form detail
 *  Example: 
    [{
    title: LN('sbi.generic.details')
    , itemId: 'detail'
    , width: 430
    , items: {
   		id: 'items-detail',   	
	   	itemId: 'items-detail',   	              
	   	columnWidth: 0.4,
         xtype: 'fieldset',
         labelWidth: 90,
         defaults: {width: 140, border:false},    
         defaultType: 'textfield',
         autoHeight: true,
         autoScroll  : true,
         bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
         border: false,
         style: {
             "margin-left": "10px", 
             "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
         },
         items: [{
             name: 'id',
             hidden: true
         },{
        	 maxLength:100,
        	 minLength:1,
        	 regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
        	 regexText : LN('sbi.roles.alfanumericString'),
             fieldLabel: LN('sbi.generic.name'),
             allowBlank: false,
             validationEvent:true,
             name: 'name'
         }, {
        	  name: 'typeCd',
              store: this.typesStore,
              fieldLabel: LN('sbi.generic.type'),
              displayField: 'typeCd',   // what the user sees in the popup
              valueField: 'typeCd',        // what is passed to the 'change' event
              typeAhead: true,
              forceSelection: true,
              mode: 'local',
              triggerAction: 'all',
              selectOnFocus: true,
              editable: false,
              allowBlank: false,
              validationEvent:true,
              xtype: 'combo'
         }]	
	  }
    }]
    
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

Sbi.widgets.ListDetailForm = function(config) {
	
	var conf = config.configurationObject;
	this.services = new Array();
	this.services['manageListService'] = conf.manageListService;
	this.services['saveItemService'] = conf.saveItemService;
	this.services['deleteItemService'] = conf.deleteItemService;
	
	this.emptyRecord = conf.emptyRecToAdd;
	this.tabItems = conf.tabItems;
	this.gridColItems = conf.gridColItems;
	this.panelTitle = conf.panelTitle;
	this.listTitle = conf.listTitle;  	
	this.drawSelectColumn = conf.drawSelectColumn;  
	this.ddGroup = conf.dragndropGroup;

	this.mainElementsStore = new Ext.data.JsonStore({
    	autoLoad: false    	  
    	, id : 'id'		
    	, fields: conf.fields
    	, root: 'rows'
		, url: this.services['manageListService']		
	});

	this.initWidget();
	
	 /*
 	   *    Here is where we create the Form
 	   */
 	  var gridForm = {
 	          frame: true,
 	          autoScroll: true,
 	          labelAlign: 'left',
 	          autoWidth: true,
 	         // title: this.panelTitle,
 	          //bodyStyle:'padding:7px',
 	         // width: 1000,
 	          height: 550,
 	          layout: 'column',
 	          scope:this,
 	          trackResetOnLoad: true,
 	          items: [
 	              
 	            	 this.mainGrid
 	             /* scope:this,
 	              layout: 'fit',
 	              items: this.mainGrid*/
 	              , this.tabs
 	            	  		
 	          ]
 	          
 	      };
 	   
 	  this.mainElementsStore.on('load', 
 				function(){
 			 		this.rowselModel.selectRow(0);
 			 	}, 
 			 	this);  	
 		
 		this.mainElementsStore.load();	
   	
	var c = Ext.apply({}, config, gridForm);
   	
   	Sbi.widgets.ListDetailForm.superclass.constructor.call(this,c);	
   	
   	//to be addedd at the end!
   	this.addEvents('selected');	
};

Ext.extend(Sbi.widgets.ListDetailForm, Ext.FormPanel, {
	
	gridForm:null
	, panelTitle: null
	, listTitle: null
	, mainElementsStore:null
	, colModel:null
	, gridColItems: null
	, drawSelectColumn: null
	, emptyRecord : null
	, tabs: null
	, tabItems: null
	, mainGrid: null
	, gridForm: null
	, rowselModel:null
	, ddGroup : null //for dragndrop
	
	
	,initWidget: function(){

        this.selectColumn = new Ext.grid.ButtonColumn({
		       header:  ' '
		       ,iconCls: 'icon-select'
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
 	       ,clickHandler: function(e, t) {   
        	//this.grid is called since this is the only name that can be used. Look at ButtonColumn.js
 	          var index = this.grid.getView().findRowIndex(t);	
 	          var selectedRecord =  this.grid.store.getAt(index);
 	          var itemId = selectedRecord.get('id');
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

 	    this.tbSave = new Ext.Toolbar({
 	    	buttonAlign : 'right', 	    	
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.generic.update'),
 	            iconCls: 'icon-save',
 	            handler: this.save,
 	            width: 30,
 	            scope: this
 	            })
 	    	]
 	    });

 	   this.tabs = new Ext.TabPanel({
           enableTabScroll : true
           , activeTab : 0
           , autoScroll : true
           , deferredRender: false
           , width: 430          
           , height: 490
           , itemId: 'tabs' 
           , tbar: this.tbSave
           , scope: this
		   , items: this.tabItems
		});

 	    this.tb = new Ext.Toolbar({
 	    	buttonAlign : 'right',
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.generic.add'),
 	            iconCls: 'icon-add',
 	            handler: this.addNewItem,
 	            width: 30,
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
 		  pluginsToAdd = [this.deleteColumn, this.selectColumn]; 
       }else{
    	  pluginsToAdd = this.deleteColumn; 
       }
 	   
 	  this.rowselModel = new Ext.grid.RowSelectionModel({
           singleSelect: true
       });
 	   
 	   this.mainGrid = {
	                  xtype: 'grid',
	                  ds: this.mainElementsStore,   	                  
	                  colModel: this.colModel,
	                  plugins: pluginsToAdd ,
	                  selModel: this.rowselModel,
	                  //autoExpandColumn: 'name',
	                  height: 490,
	                  width: 410,
	                  scope: this,
	                  title: this.listTitle,
		              bbar: pagingBar,
	                  tbar: this.tb,
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
	                      			viewready: function(g) {g.getSelectionModel().selectRow(0); } 
	                             }
	                  };
	}

	,sendSelectedItem: function(itemId, index){
		this.fireEvent('selected',itemId,index);
	}	

	, addNewItem : function(){
	
		var emptyRecToAdd = this.emptyRecord;
		this.getForm().loadRecord(emptyRecToAdd);
		//this.mainElementsStore.add(emptyRecToAdd); 
	
	    this.tabs.items.each(function(item)
		    {		
		    	item.doLayout();
		    });   
	}
	
	, deleteSelectedItem: function(itemId, index) {
		Ext.MessageBox.confirm(
			LN('sbi.generic.pleaseConfirm'),
			LN('sbi.generic.confirmDelete'),            
            function(btn, text) {
                if (btn=='yes') {
                	if (itemId != null) {	

						Ext.Ajax.request({
				            url: this.services['deleteItemService'],
				            params: {'id': itemId},
				            method: 'GET',
				            success: function(response, options) {
								if (response !== undefined) {
									var deleteRow = this.rowselModel.getSelected();
									this.mainElementsStore.remove(deleteRow);
									this.mainElementsStore.commitChanges();
									if(this.mainElementsStore.getCount()>0){
										this.rowselModel.selectRow(0);
									}else{
										this.addNewItem();
									}
								} else {
									Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.deletingItemError'), LN('sbi.generic.serviceError'));
								}
				            },
				            failure: function() {
				                Ext.MessageBox.show({
				                    title: LN('sbi.generic.error'),
				                    msg: LN('sbi.generic.deletingItemError'),
				                    width: 150,
				                    buttons: Ext.MessageBox.OK
				               });
				            }
				            ,scope: this
			
						});
					} else {
						Sbi.exception.ExceptionHandler.showWarningMessage(LN('sbi.generic.error.msg'),LN('sbi.generic.warning'));
					}
                }
            },
            this
		);
	}
	
	//METHOD TO BE OVERRIDDEN IN EXTENDED ELEMENT!!!!!
	,save : function() {		
		alert('Abstract Method: it needs to be overridden');
    }

});
