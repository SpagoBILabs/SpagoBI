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
 * Authors - Monica Franceschini (monica.franceschini@eng.it)
 */
Ext.ns("Sbi.udp");

Sbi.udp.ManageUdp = function(config) {

	var paramsList = {MESSAGE_DET: "UDP_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "UDP_DETAIL"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "UDP_DELETE"};
	
	this.types = config.types;
	this.families = config.families;
	
	this.services = new Array();
	this.services['manageUdpList'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_UDP_ACTION'
		, baseParams: paramsList
	});
	this.services['saveUdpService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_UDP_ACTION'
		, baseParams: paramsSave
	});
	this.services['deleteUdpService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_UDP_ACTION'
		, baseParams: paramsDel
	});
	
	this.udpStore = new Ext.data.JsonStore({
    	autoLoad: false    	  
    	,id : 'udpid'		
    	,fields: ['id'
    	          , 'label'
    	          , 'name'
    	          , 'description'
    	          , 'multivalue'
    	          , 'type'
    	          , 'family'
    	          ]
    	, root: 'samples'
		, url: this.services['manageUdpList']
		
	});

	this.udpStore.load();
	this.initManageUdp();
	   
   	Ext.getCmp('udpgrid').store.on('load', function(){
	 var grid = Ext.getCmp('udpgrid');
	 grid.getSelectionModel().selectRow(0);
	 }, this, {
	 single: true
    });
   	
   	Ext.getCmp('udpgrid').on('delete', this.deleteSelectedUdp, this);
}
Ext.extend(Sbi.udp.ManageUdp, Ext.FormPanel, {
	gridForm:null
	, udpStore:null
	, colModel:null
	, types: null
	, families: null
	, buttons: null
	, tabs: null
	
	,initManageUdp: function(){

		this.deleteColumn = new Ext.grid.ButtonColumn({
	       header:  ' ',
	       iconCls: 'icon-remove',
	       clickHandler: function(e, t) {

	          var index = Ext.getCmp("udpgrid").getView().findRowIndex(t);
	          
	          var selectedRecord = Ext.getCmp("udpgrid").store.getAt(index);
	          var udpId = selectedRecord.get('id');

	          Ext.getCmp("udpgrid").fireEvent('delete', udpId, index);
	       }
	       ,width: 25
	       ,renderer : function(v, p, record){
	           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
	       }
	    });
	    this.colModel = new Ext.grid.ColumnModel([	      
	      {header: LN('sbi.udp.label'), width: 50, sortable: true, dataIndex: 'label'},
	      {id:'name',header: LN('sbi.udp.name'), width: 100, sortable: true, locked:false, dataIndex: 'name'},
          {header: LN('sbi.udp.type'), width: 70, sortable: true, dataIndex: 'type'},
          {header: LN('sbi.udp.family'), width: 70, sortable: true, dataIndex: 'family'}
	      , this.deleteColumn
	   ]);

	  //Store of the combobox
	   
 	    this.typesStore = new Ext.data.SimpleStore({
 	        fields: ['type'],
 	        data: this.types,
 	        autoLoad: false
 	    });
 	    
 	   this.familiesStore = new Ext.data.SimpleStore({
	        fields: ['family'],
	        data: this.families,
	        autoLoad: false
	    });
 	  
	    this.tbSave = new Ext.Toolbar({
	    	buttonAlign : 'right', 	    	
	    	items:[new Ext.Toolbar.Button({
	            text: LN('sbi.udp.save'),
	            iconCls: 'icon-save',
	            handler: this.save,
	            width: 30,
	            id: 'save-btn',
	            scope: this
	        })
	    	]
	    });

	   this.tabs = new Ext.TabPanel({
        enableTabScroll : true
        , id: 'tab-panel'
        , activeTab : 0
        , autoScroll : true
        , width: 450
        , height: 500
        , itemId: 'tabs' 
        , tbar: this.tbSave
		   , items: [{
		        title: LN('sbi.udp.detail')
		        , itemId: 'detail'
		        , width: 430
		        , items: {
			   		id: 'udp-detail',   	
		 		   	itemId: 'udp-detail',   	              
		 		   	columnWidth: 0.4,
		             xtype: 'fieldset',
		             labelWidth: 90,
		            // defaults: {width: 140, border:false},    
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
		            	 maxLength:20,
		            	 minLength:1,
		            	 regexText : LN('sbi.udp.validString'),
		                 fieldLabel: LN('sbi.udp.label'),
		                 allowBlank: false,
		                 validationEvent:true,
		                 name: 'label'
		             },{
		            	 maxLength:40,
		            	 minLength:1,
		            	 regexText : LN('sbi.udp.validString'),
		                 fieldLabel: LN('sbi.udp.name'),
		                 allowBlank: false,
		                 validationEvent:true,
		                 name: 'name'
		             },{
		            	 maxLength:1000,
		            	 width : 250,
		                 height : 80,
		            	 regexText : LN('sbi.udp.validString'),
		                 fieldLabel:LN('sbi.udp.description'),
		                 validationEvent:true,
		                 xtype: 'textarea',
		                 name: 'description'
		             }, {
		            	  name: 'multivalue',
		                  fieldLabel: LN('sbi.udp.multivalue'),
		                  displayField: 'multivalue',   // what the user sees in the popup
		                  valueField: 'multivalue',     // what is passed to the 'change' event
		                  typeAhead: true,
		                  forceSelection: true,
		                  mode: 'local',
		                  triggerAction: 'all',
		                  selectOnFocus: true,
		                  allowBlank: true,
		                  validationEvent:true,
		                  xtype: 'checkbox'
		             }, {
		            	  name: 'type',
		                  store: this.typesStore,
		                  fieldLabel: LN('sbi.udp.type'),
		                  displayField: 'type',   // what the user sees in the popup
		                  valueField: 'type',     // what is passed to the 'change' event
		                  typeAhead: true,
		                  forceSelection: true,
		                  mode: 'local',
		                  triggerAction: 'all',
		                  selectOnFocus: true,
		                  editable: false,
		                  allowBlank: false,
		                  validationEvent:true,
		                  xtype: 'combo'
		             }, {
		            	  name: 'family',
		                  store: this.familiesStore,
		                  fieldLabel: LN('sbi.udp.family'),
		                  displayField: 'family',   // what the user sees in the popup
		                  valueField: 'family',     // what is passed to the 'change' event
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
		});

	    this.tb = new Ext.Toolbar({
	    	buttonAlign : 'right',
	    	items:[new Ext.Toolbar.Button({
	            text: LN('sbi.udp.add'),
	            iconCls: 'icon-add',
	            handler: this.addNewUdp,
	            width: 30,
	            scope: this
	        })
	    	]
	    });

	   /*
	   *    Here is where we create the Form
	   */
	   this.gridForm = new Ext.FormPanel({
	          id: 'udp-form',
	          frame: true,
	          labelAlign: 'left',
	          title: LN('sbi.udp.udpManagement'),
	          bodyStyle:'padding:5px',
	          width: 850,
	          height: 600,
	          layout: 'column',
	          trackResetOnLoad: true,
	          renderTo: Ext.getBody(),
	          items: [{
	              columnWidth: 0.90,
	              layout: 'fit',
	              items: {
	        	  	  id: 'udpgrid',
	                  xtype: 'grid',
	                  ds: this.udpStore,   	                  
	                  cm: this.colModel,
	                  plugins: this.deleteColumn,
	                  sm: new Ext.grid.RowSelectionModel({
	                      singleSelect: true,
	                      scope:this,   	                   
	                      listeners: {
	                          rowselect: function(sm, row, rec) { 
	                              Ext.getCmp('udp-form').getForm().loadRecord(rec);      	
	                          }
	                      }
	                  }),
	                  autoExpandColumn: 'name',
	                  height: 500,
	                  width: 400,
	                  layout: 'fit',
	                  title: LN('sbi.udp.udpList'),
	                  tbar: this.tb,

	                  border: true,
	                  listeners: {
	                      viewready: function(g) {
	                          g.getSelectionModel().selectRow(0);
	                      } 
	                  }
	              }
	          }, this.tabs
	          ]
	      });

	}
	, addNewUdp : function(){
	
		var emptyRecToAdd =new Ext.data.Record({
											id: 0,
											label:'',
											name:'', 
											description:'', 
											multivalue:'',
											type:'',
											family:''
											});
	
		Ext.getCmp('udp-form').getForm().loadRecord(emptyRecToAdd);
  
		Ext.getCmp('udp-form').doLayout();
	
	}
	,save : function() {
		var values = this.gridForm.getForm().getValues();
		var idRec = values['id'];
		var newRec;

		if (values['multivalue'] === 'on'){
			values['multivalue'] = true;		
		}else{
			values['multivalue'] = false;
		}
		
		if(idRec ==0 || idRec == null || idRec === ''){
			newRec =new Ext.data.Record({
					label :values['label'],
			        name :values['name'],
			        description :values['description'],
			        multivalue :values['multivalue'],
			        type :values['type'],
			        family :values['family']
			});	  
		}else{
			var newRec;
			var length = this.udpStore.getCount();
			for(var i=0;i<length;i++){
	   	        var tempRecord = this.udpStore.getAt(i);
	   	        if(tempRecord.data.id==idRec){
	   	        	newRec = tempRecord;
				}			   
	   	    }	
			newRec.set('label',values['label']);
			newRec.set('name',values['name']);
			newRec.set('description',values['description']);
			newRec.set('multivalue',values['multivalue']);
			newRec.set('type',values['type']);		
			newRec.set('family',values['family']);
		}

     var params = {
    	label : newRec.data.label,	 
     	name : newRec.data.name,
     	description : newRec.data.description,
     	multivalue : newRec.data.multivalue,
     	type : newRec.data.type,
     	family : newRec.data.family
     };
     if(idRec){
     	params.id = newRec.data.id;
     }
     
     Ext.Ajax.request({
         url: this.services['saveUdpService'],
         params: params,
         method: 'GET',
         success: function(response, options) {
				if (response !== undefined) {			
		      		if(response.responseText !== undefined) {

		      			var content = Ext.util.JSON.decode( response.responseText );
		      			if(content.responseText !== 'Operation succeded') {
			                    Ext.MessageBox.show({
			                        title: LN('sbi.udp.error'),
			                        msg: content,
			                        width: 150,
			                        buttons: Ext.MessageBox.OK
			                   });
			      		}else{
			      			var udpID = content.id;
			      			if(udpID != null && udpID !==''){
			      				newRec.set('id', udpID);
			      				this.udpStore.add(newRec);  
			      			}
			      			this.udpStore.commitChanges();
			      		    if(udpID != null && udpID !==''){
								var grid = Ext.getCmp('udpgrid');
					            grid.getSelectionModel().selectLastRow(true);
				            }
			      			
			      			Ext.MessageBox.show({
			      					title: LN('sbi.generic.result'),
			                        msg: LN('sbi.generic.resultMsg'),
			                        width: 200,
			                        buttons: Ext.MessageBox.OK
			                });    
			      		}      				 

		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.serviceResponseEmpty'), LN('sbi.generic.serviceError'));
		      		}
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.generic.savingItemError'), LN('sbi.generic.serviceError'));
				}
         },
         failure: function(response) {
	      		if(response.responseText !== undefined) {
	      			var content = Ext.util.JSON.decode( response.responseText );
	      			var errMessage ='';
					for (var count = 0; count < content.errors.length; count++) {
						var anError = content.errors[count];
	        			if (anError.localizedMessage !== undefined && anError.localizedMessage !== '') {
	        				errMessage += anError.localizedMessage;
	        			} else if (anError.message !== undefined && anError.message !== '') {
	        				errMessage += anError.message;
	        			}
	        			if (count < content.errors.length - 1) {
	        				errMessage += '<br/>';
	        			}
					}

	                Ext.MessageBox.show({
	                	title: LN('sbi.generic.validationError'),
	                    msg: errMessage,
	                    width: 400,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}else{
	                Ext.MessageBox.show({
	                	title: LN('sbi.generic.error'),
	                    msg: LN('sbi.generic.savingItemError'),
	                    width: 150,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}
         }
         ,scope: this
     });
	}

	, deleteSelectedUdp: function(udpId, index) {
		Ext.MessageBox.confirm(
		LN('sbi.generic.pleaseConfirm'),
		LN('sbi.generic.confirmDelete'), 
         function(btn, text) {
             if (btn=='yes') {
             	if (udpId != null) {	

						Ext.Ajax.request({
				            url: this.services['deleteUdpService'],
				            params: {'id': udpId},
				            method: 'GET',
				            success: function(response, options) {
								if (response !== undefined) {

									var sm = Ext.getCmp('udpgrid').getSelectionModel();
									var deleteRow = sm.getSelected();
									this.udpStore.remove(deleteRow);
									this.udpStore.commitChanges();
									if(this.udpStore.getCount()>0){
										var grid = Ext.getCmp('udpgrid');
										grid.getSelectionModel().selectRow(0);
										grid.fireEvent('rowclick', grid, 0);
									}else{
										this.addNewUdp();
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
});

Ext.reg('manageudp', Sbi.udp.ManageUdp);
