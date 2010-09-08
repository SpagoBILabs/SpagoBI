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
Ext.ns("Sbi.alarm");

Sbi.alarm.ManageContacts = function(config) { 
	var paramsList = {MESSAGE_DET: "CONTACTS_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "CONTACT_DETAIL"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "CONTACT_DELETE"};
	
	this.services = new Array();
	this.services['manageContactsList'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_CONTACTS_ACTION'
		, baseParams: paramsList
	});
	this.services['saveContactsService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_CONTACTS_ACTION'
		, baseParams: paramsSave
	});
	this.services['deleteContactsService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_CONTACTS_ACTION'
		, baseParams: paramsDel
	});
	
	this.contactsStore = new Ext.data.JsonStore({
    	autoLoad: false    	  
    	,id : 'id'		
    	,fields: ['id'
    	          , 'name'
    	          , 'email'
    	          , 'resources'
    	          , 'mobile'
    	          ]
    	, root: 'samples'
		, url: this.services['manageContactsList']
		
	});

	this.contactsStore.load();
	this.initManageContacts();
	   
   	Ext.getCmp('contactsgrid').store.on('load', function(){
	 var grid = Ext.getCmp('contactsgrid');
	 grid.getSelectionModel().selectRow(0);
	 }, this, {
	 single: true
    });
   	
   	Ext.getCmp('contactsgrid').on('delete', this.deleteSelectedContact, this);
}
Ext.extend(Sbi.alarm.ManageContacts, Ext.FormPanel, {
	gridForm:null
	, contactsStore:null
	, colModel:null
	, typeData: null
	, buttons: null
	, tabs: null
	
	,initManageContacts: function(){


    this.deleteColumn = new Ext.grid.ButtonColumn({
	       header:  ' ',
	       iconCls: 'icon-remove',
	       clickHandler: function(e, t) {

	          var index = Ext.getCmp("contactsgrid").getView().findRowIndex(t);
	          
	          var selectedRecord = Ext.getCmp("contactsgrid").store.getAt(index);
	          var contactId = selectedRecord.get('id');

	          Ext.getCmp("contactsgrid").fireEvent('delete', contactId, index);
	       }
	       ,width: 25
	       ,renderer : function(v, p, record){
	           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
	       }
	    });
	    this.colModel = new Ext.grid.ColumnModel([
	      {id:'name',header: LN('sbi.alarmcontact.name'), width: 50, sortable: true, locked:false, dataIndex: 'name'}
	      , this.deleteColumn
	   ]);

	    this.typesStore = new Ext.data.SimpleStore({
	        fields: ['resources'],
	        data: config,
	        autoLoad: false
	    });
	    
	    this.tbSave = new Ext.Toolbar({
	    	buttonAlign : 'right', 	    	
	    	items:[new Ext.Toolbar.Button({
	            text: LN('sbi.alarmcontact.save'),
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
		        title: LN('sbi.alarmcontact.detail')
		        , itemId: 'detail'
		        , width: 430
		        , items: {
			   		id: 'contact-detail',   	
		 		   	itemId: 'contact-detail',   	              
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
		            	 //regex : new RegExp("^([a-zA-Z1-9_\x2F\s])+$", "g"),
		            	 regexText : LN('sbi.alarmcontact.validString'),
		                 fieldLabel: LN('sbi.alarmcontact.name'),
		                 allowBlank: false,
		                 validationEvent:true,
		                 name: 'name'
		             },{
		            	 maxLength:100,
		            	 minLength:1,
		            	 //regex : new RegExp("/^([\w]+)(.[\w]+)*@([\w-]+\.){1,5}([A-Za-z]){2,4}$/", "g"),
		            	 regexText : LN('sbi.alarmcontact.validEmailString'),
		                 fieldLabel: LN('sbi.alarmcontact.email'),
		                 validationEvent:true,
		                 name: 'email'
		             },{
		            	 maxLength:50,
		            	 minLength:0,
		            	 //regex : new RegExp("^([0-9/])+$", "g"),
		            	 regexText : LN('sbi.alarmcontact.validMobileString'),
		                 fieldLabel:LN('sbi.alarmcontact.mobile'),
		                 validationEvent:true,
		                 name: 'mobile'
		             }, {
		            	  name: 'resources',
		                  store: this.typesStore,
		                  fieldLabel: LN('sbi.alarmcontact.resources'),
		                  displayField: 'resources',   // what the user sees in the popup
		                  valueField: 'resources',        // what is passed to the 'change' event
		                  typeAhead: true,
		                  forceSelection: true,
		                  mode: 'local',
		                  triggerAction: 'all',
		                  selectOnFocus: true,
		                  emptyText: '-',
		                  editable: false,
		                  allowBlank: true,
		                  validationEvent:true,
		                  xtype: 'combo'
		             }]
		    	
		    	}
		    }]
		});

	    this.tb = new Ext.Toolbar({
	    	buttonAlign : 'right',
	    	items:[new Ext.Toolbar.Button({
	            text: LN('sbi.alarmcontact.add'),
	            iconCls: 'icon-add',
	            handler: this.addNewContact,
	            width: 30,
	            scope: this
	        })
	    	]
	    });

	   /*
	   *    Here is where we create the Form
	   */
	   this.gridForm = new Ext.FormPanel({
	          id: 'contact-form',
	          frame: true,
	          labelAlign: 'left',
	          title: LN('sbi.alarmcontact.contactsManagement'),
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
	        	  	  id: 'contactsgrid',
	                  xtype: 'grid',
	                  ds: this.contactsStore,   	                  
	                  cm: this.colModel,
	                  plugins: this.deleteColumn,
	                  sm: new Ext.grid.RowSelectionModel({
	                      singleSelect: true,
	                      scope:this,   	                   
	                      listeners: {
	                          rowselect: function(sm, row, rec) { 
	                              Ext.getCmp('contact-form').getForm().loadRecord(rec);      	
	                          }
	                      }
	                  }),
	                  autoExpandColumn: 'name',
	                  height: 500,
	                  width: 400,
	                  layout: 'fit',
	                  title: LN('sbi.alarmcontact.contactsList'),
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
	, addNewContact : function(){
	
		var emptyRecToAdd =new Ext.data.Record({
											id: 0,
											name:'', 
											mobile:'', 
											email:'',
											resources:''
											});
	
		Ext.getCmp('contact-form').getForm().loadRecord(emptyRecToAdd);
  
		Ext.getCmp('contact-form').doLayout();
	
	}
	,save : function() {
		var values = this.gridForm.getForm().getValues();
		var idRec = values['id'];
		var newRec;
	
		if(idRec ==0 || idRec == null || idRec === ''){
			newRec =new Ext.data.Record({
					name :values['name'],
			        email :values['email'],
			        resources :values['resources'],
			        mobile :values['mobile']
			});	  

			
		}else{
			var newRec;
			var length = this.contactsStore.getCount();
			for(var i=0;i<length;i++){
	   	        var tempRecord = this.contactsStore.getAt(i);
	   	        if(tempRecord.data.id==idRec){
	   	        	newRec = tempRecord;
				}			   
	   	    }	
			newRec.set('name',values['name']);
			newRec.set('email',values['email']);
			newRec.set('resources',values['resources']);
			newRec.set('mobile',values['mobile']);		
		}

     var params = {
     	name : newRec.data.name,
     	resources : newRec.data.resources,
     	email : newRec.data.email,
     	mobile : newRec.data.mobile
     };
     if(idRec){
     	params.id = newRec.data.id;
     }
     
     Ext.Ajax.request({
         url: this.services['saveContactsService'],
         params: params,
         method: 'GET',
         success: function(response, options) {
				if (response !== undefined) {			
		      		if(response.responseText !== undefined) {

		      			var content = Ext.util.JSON.decode( response.responseText );

		      			if(content.responseText !== 'Operation succeded') {
			                    Ext.MessageBox.show({
			                        title: LN('sbi.alarmcontact.error'),
			                        msg: content,
			                        width: 150,
			                        buttons: Ext.MessageBox.OK
			                   });
			      		}else{
			      			var contactID = content.id;
			      			if(contactID != null && contactID !==''){
			      				newRec.set('id', contactID);
			      				this.contactsStore.add(newRec);  
			      			}
			      			this.contactsStore.commitChanges();
			      		    if(contactID != null && contactID !==''){
								var grid = Ext.getCmp('contactsgrid');
					            grid.getSelectionModel().selectLastRow(true);
				            }
			      			
			      			Ext.MessageBox.show({
			                        title: LN('sbi.alarmcontact.result'),
			                        msg: 'Operation succeded',
			                        width: 200,
			                        buttons: Ext.MessageBox.OK
			                });    
			      		}      				 

		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Error while saving Contact', 'Service Error');
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
	                    title: LN('sbi.alarmcontact.validationError'),
	                    msg: errMessage,
	                    width: 400,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}else{
	                Ext.MessageBox.show({
	                    title: LN('sbi.alarmcontact.error'),
	                    msg: 'Error while Saving Contact',
	                    width: 150,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}
         }
         ,scope: this
     });
	}

	, deleteSelectedContact: function(contactId, index) {
		Ext.MessageBox.confirm(
         'Please confirm',
         'Confirm contact delete?',            
         function(btn, text) {
             if (btn=='yes') {
             	if (contactId != null) {	

						Ext.Ajax.request({
				            url: this.services['deleteContactsService'],
				            params: {'id': contactId},
				            method: 'GET',
				            success: function(response, options) {
								if (response !== undefined) {

									var sm = Ext.getCmp('contactsgrid').getSelectionModel();
									var deleteRow = sm.getSelected();
									this.contactsStore.remove(deleteRow);
									this.contactsStore.commitChanges();
									if(this.contactsStore.getCount()>0){
										var grid = Ext.getCmp('contactsgrid');
										grid.getSelectionModel().selectRow(0);
										grid.fireEvent('rowclick', grid, 0);
									}else{
										this.addNewContact();
									}
								} else {
									Sbi.exception.ExceptionHandler.showErrorMessage('Error while deleting Contact', 'Service Error');
								}
				            },
				            failure: function() {
				                Ext.MessageBox.show({
				                    title: LN('sbi.alarmcontact.error'),
				                    msg: 'Error while deleting Contact',
				                    width: 150,
				                    buttons: Ext.MessageBox.OK
				               });
				            }
				            ,scope: this
			
						});
					} else {
						Sbi.exception.ExceptionHandler.showWarningMessage('Operation failed', 'Warning');
					}
             }
         },
         this
		);
	}
});

Ext.reg('managecontacts', Sbi.alarm.ManageContacts);
