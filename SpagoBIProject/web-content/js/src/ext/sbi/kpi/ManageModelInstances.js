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
Ext.ns("Sbi.profiling");

Sbi.profiling.ManageUsers = function(config) { 
	var paramsList = { MESSAGE_DET: "USERS_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "USER_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE', MESSAGE_DET: "USER_DELETE"};
	
	this.services = new Array();
	this.services['manageUsersList'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_USER_ACTION'
		, baseParams: paramsList
	});
	this.services['saveUserService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_USER_ACTION'
		, baseParams: paramsSave
	});
	
	this.services['deleteUserService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_USER_ACTION'
		, baseParams: paramsDel
	});	
	
	this.initStores(config);
	
	this.initManageUsers();
	
	Ext.getCmp('usergrid').store.on('load', function(){
	 var grid = Ext.getCmp('usergrid');
	 
	 if(this.usersStore.getTotalCount()>0){
		 grid.fireEvent('rowclick', grid, 0);
		 grid.getSelectionModel().selectRow(0);
	 }
	 }, this, {
	 single: true
   });
	
	this.usersStore.load();
 
    Ext.getCmp('usergrid').on('delete', this.deleteSelectedUser, this);
	
}

Ext.extend(Sbi.profiling.ManageUsers, Ext.FormPanel, {
	gridForm:null
	, usersStore:null
	, colModel:null
	, typeData: null
	, buttons: null
	, tabs: null
	, attributesGridPanel: null
	, attributesStore: null
	, smRoles: null
	, rolesGrid: null
	, rolesStore: null
	, rolesEmptyStore: null
	, attributesEmptyStore: null

	, initStores: function (config) {
	
		this.usersStore = new Ext.data.JsonStore({
	    	autoLoad: false  
	    	,fields: ['userId'
	    			  , 'id'
	    	          , 'fullName'
	    	          , 'pwd'
	    	          , 'confirmpwd'
	    	          , 'userRoles'
	    	          , 'userAttributes'
	    	          ]
	    	, root: 'samples'
			, url: this.services['manageUsersList']			
		});	
		
		this.attributesStore = new Ext.data.SimpleStore({
	        fields : [ 'id', 'name', 'value' ]
	    });
	    
	    this.rolesStore = new Ext.data.SimpleStore({
	    	id: 'id',
	        fields : [ 'id', 'name', 'description', 'checked' ]
	    });

	    
	    this.attributesEmptyStore = config.attributesEmpyList;
	    this.rolesEmptyStore = config.rolesEmptyList;
	    
	    this.initAttributesGridPanel();
	    this.initRolesGridPanel();
    
    }
	
	,initManageUsers: function(){
		
	    this.deleteColumn = new Ext.grid.ButtonColumn({
	       header:  ' ',
	       dataIndex: 'id',
	       iconCls: 'icon-remove',
	       clickHandler: function(e, t) {
	          var index = Ext.getCmp("usergrid").getView().findRowIndex(t);
	          
	          var selectedRecord = Ext.getCmp("usergrid").store.getAt(index);
	          var userId = selectedRecord.get('id');
	          Ext.getCmp("usergrid").fireEvent('delete', userId, index);
	       }
	       ,width: 25
	       ,renderer : function(v, p, record){
	           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
	       }
        });
       
        this.colModel = new Ext.grid.ColumnModel([
         {id:'userId', header: LN('sbi.users.userId'), width: 150, sortable: true, dataIndex: 'userId'},
         {header: LN('sbi.users.fullName'), width: 150, sortable: true, dataIndex: 'fullName'},
         this.deleteColumn
        ]);
     	   

	    this.tbSave = new Ext.Toolbar({
 	    	buttonAlign : 'right', 	    	
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.attributes.update'),
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
           , renderTo: Ext.getBody()
           , activeTab : 0
           , autoScroll : true
           //NB: Important trick: to render all content tabs on page load
           , deferredRender: false
           , width: 450
           , height: 450
           , itemId: 'tabs'
           , tbar: this.tbSave            
		   , items: [{
		        title: LN('sbi.roles.details')
		        , itemId: 'detail'		
		        , layout: 'fit'		        	
		        , items: {
		 		   	 itemId: 'user-detail',  
		 		     id : 'detail',		 		    
		 		   	 columnWidth: 0.4,
		             xtype: 'fieldset',
		             labelWidth: 90,
		             defaults: {width: 140, border:false},    
		             defaultType: 'textfield',
		             autoHeight: true,
		             autoScroll  : true,
		             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
		             border: false, 
		             buttons: [{
		                 text: 'Change Password',
		                 id: 'changePwd',
		                 iconCls: 'icon-refresh',		                 
		                 style: Ext.isIE ? {} : {	
			            	 position: 'absolute'
		            	 	,top: '95px'
			            	,left: '320px'
			            	,zIndex: '100'
				         },
		                 handler: function(){
	                         Ext.getCmp("detail").items.each(function(item){
	                              if(item.getItemId() == 'pwdId' || item.getItemId() == 'confirmpwdId'){
		   	                   		  item.enable();
	   	                   		  }	
	                         });
		                 }
				         ,scope: this
		             }],

		             style: {
		                 "margin-left": "10px", 
		                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-10px" : "-13px") : "0"  
		             },

		             items: [{
		                 name: 'id',
		                 hidden: true
		             },{
		                 fieldLabel: LN('sbi.users.userId'),
		                 name: 'userId',
		                 allowBlank: false,
		                 validationEvent:true,
		            	 maxLength:100,
		            	 minLength:1,
		            	 regex : new RegExp("^([a-zA-Z1-9_\x2D])+$", "g"),
		            	 regexText : LN('sbi.users.wrongFormat')
		             },{
		                 fieldLabel:  LN('sbi.users.fullName'),
		                 name: 'fullName',
		                 allowBlank: false,
		                 validationEvent:true,
		            	 maxLength:255,
		            	 minLength:1,
		            	 regex : new RegExp("^([a-zA-Z0-9_\x2D\s\x2F])+$", "g"),
		            	 regexText : LN('sbi.users.wrongFormat')
		             },{
		                 fieldLabel: LN('sbi.users.pwd'),
		                 name: 'pwd',
		                 itemId: 'pwdId',
		                 inputType: 'password',
		                 //allowBlank: false,
		                 validationEvent:true,
		            	 maxLength:160,
		            	 minLength:1
		             },{
		                 fieldLabel:  LN('sbi.users.confPwd'),		                 
		                 name: 'confirmpwd',
		                 itemId: 'confirmpwdId',
		                 inputType: 'password',
		                 //allowBlank: false,
		                 validationEvent:true,
		            	 maxLength:160,
		            	 minLength:1
		             }]
		             
		    	}
		    },{
		        title: LN('sbi.users.roles')
		        , id : 'rolesList'
		        , layout: 'fit'
		        , autoScroll: true
		        //, renderTo: Ext.getBody()
		        , items: [this.rolesGrid]
		        , itemId: 'roles'
		        , scope: this
		    },{
		        title: LN('sbi.users.attributes')
		        , id : 'attrList'
		        , autoScroll: true
	            , items : [ this.attributesGridPanel ]
		        , itemId: 'attributes'
		        , layout: 'fit'
		    }]

		});

	    this.tb = new Ext.Toolbar({
 	    	buttonAlign : 'right',
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.attributes.add'),
 	            iconCls: 'icon-add',
 	            handler: this.addNewUser,
 	            width: 30,
 	            scope: this
 	        })
 	    	]
 	    });
   	   /*
   	   *    Here is where we create the Form
   	   */
   	   this.gridForm = new Ext.FormPanel({
   	          id: 'user-form',
   	          frame: true,
   	          labelAlign: 'left',
   	          title: LN('sbi.users.manageUsers'),
   	          bodyStyle:'padding:5px',
   	          width: 850,
   	          layout: 'column',

   	          items: [{
   	              columnWidth: 0.90,
   	              layout: 'fit',
   	              items: {
   	        	  	  id: 'usergrid',
   	                  xtype: 'grid',
   	                  ds: this.usersStore,   	                  
   	                  cm: this.colModel,
   	                  plugins: this.deleteColumn,
   	                  sm: new Ext.grid.RowSelectionModel({   	                  	  
   	                      singleSelect: true,
   	                      scope:this,   	                   
	   	                  fillRoles : function(row, rec) {	 
							Ext.getCmp("roles-form").store.removeAll();
	   	                   	var tempArr = rec.data.userRoles;
	   	                  	var length = rec.data.userRoles.length;
	   	                  	for(var i=0;i<length;i++){
	   	                  		var tempRecord = new Ext.data.Record({"description":tempArr[i].description, "name":tempArr[i].name, "id":tempArr[i].id });
							    Ext.getCmp("roles-form").store.addSorted(tempRecord);
							    Ext.getCmp("roles-form").store.commitChanges();
							    Ext.getCmp("roles-form").selModel.unlock();
							    if(tempArr[i].checked){
							    	var roleId = tempRecord.get('id');				    	
							    	Ext.getCmp("roles-form").fireEvent('recToSelect', roleId, i);
							    }

	   	                  	}	
	   	                  	
	   	                  },
	   	                  fillAttributes : function(row, rec) {	 
	   	                    Ext.getCmp("attributes-form").store.removeAll();
	   	                  	var tempArr = rec.data.userAttributes;
	   	                  	var length = rec.data.userAttributes.length;
	   	                  	for(var i=0;i<length;i++){
	   	                  		var tempRecord = new Ext.data.Record({"value":tempArr[i].value,"name":tempArr[i].name,"id":tempArr[i].id });
							    Ext.getCmp("attributes-form").store.add(tempRecord);	
	   	                  	}			        
 
	   	                  },
   	                      listeners: {
   	                          rowselect: function(sm, row, rec) {   
   	                          	  Ext.getCmp('save-btn').enable();
   	                          	  rec.set('confirmpwd', '');
   	                              Ext.getCmp("user-form").getForm().loadRecord(rec);  
   	                              Ext.getCmp("detail").items.each(function(item){
	   	                              if(item.getItemId() == 'pwdId' || item.getItemId() == 'confirmpwdId'){
	  		   	                   		  item.disable();
	  	   	                   		  }	
   	                              });
   	                              Ext.getCmp('changePwd').show();
   	                	  		  this.fillAttributes(row, rec);
   	                	  		  this.fillRoles(row, rec);   	                                  	                              
   	                          }
   	                      }
   	                  }),
   	                  height: 450,
   	                  width: 400,
   	                  layout: 'fit',
   	                  title:LN('sbi.users.usersList'),
   	                  tbar: this.tb,
   	                  border: true
  	                 ,listeners: {
   	                      viewready: function(g) {
   	                      	  g.getView().refresh();
	   	                      g.fireEvent('rowclick', g, 0);
							  g.getSelectionModel().selectRow(0); 	                      
   	                      }  
   	                  }
   	              }
   	          }, this.tabs
   	          ],
   	          renderTo: Ext.getBody()
   	      });

	}
	
	, initAttributesGridPanel : function() {
        
        this.attributesGridPanel = new Ext.grid.EditorGridPanel({
            id: 'attributes-form',
            store : this.attributesStore,
            autoHeight : true,
            columns : [ {          	
                header : LN('sbi.roles.headerName'),
                width : 70,
                sortable : true,
                dataIndex : 'name'
            }, {           	
                header : LN('sbi.users.headerValue'),
                width : 70,
                sortable : true,
                dataIndex : 'value',
                editor : new Ext.form.TextField({}) 
            } ],
            viewConfig : {
                forceFit : true,
                scrollOffset : 2
            // the grid will never have scrollbars
            },
            singleSelect : true,
            clicksToEdit : 2
        });

    }
    
    , initRolesGridPanel : function() {
    	
    	this.smRoles = new Ext.grid.CheckboxSelectionModel( {header: ' ',singleSelect: false, scope:this, dataIndex: 'id'} );
		
        this.cmRoles = new Ext.grid.ColumnModel([
	         //{id:'id',header: "id", dataIndex: 'id'},
	         {header: LN('sbi.roles.headerName'), width: 45, sortable: true, dataIndex: 'name'},
	         {header: LN('sbi.roles.headerDescr'), width: 65, sortable: true, dataIndex: 'description'}
	         ,this.smRoles 
	    ]);

		this.rolesGrid = new Ext.grid.GridPanel({
			  store: this.rolesStore
			, id: 'roles-form'
			//NB: Important trick!!!to render the grid with activeTab=0	
			//, renderTo: Ext.get('ext-gen97')
   	     	, cm: this.cmRoles
   	     	, sm: this.smRoles
   	     	, frame: false
   	     	, border:false  
   	     	, collapsible:false
   	     	, loadMask: true
   	     	, viewConfig: {
   	        	forceFit:true
   	        	, enableRowBody:true
   	        	, showPreview:true
   	     	}
			, scope: this
		});
		this.rolesGrid.superclass.constructor.call(this);
		
		Ext.getCmp("roles-form").on('recToSelect', function(roleId, index){		
			Ext.getCmp("roles-form").selModel.selectRow(index,true);
		});
	}

	
	,save : function() {
		   
	   var values = this.gridForm.getForm().getValues();
	   
       if(!Sbi.config.passwordAbilitated || (values['pwd']===values['confirmpwd'])){

	      	var newRec = null;
	      	var idRec = values['id'];
	      	
			var params = {
	        	userId : values['userId'],
	        	fullName : values['fullName']  
	        }
	        params.id = values['id'];
	        
			if(values['pwd'] != undefined){
				params.pwd = values['pwd'] ;
			}
	        
	        var rolesSelected = Ext.getCmp("roles-form").selModel.getSelections();
	        var lengthR = rolesSelected.length;
            var roles =new Array();
            for(var i=0;i<lengthR;i++){
             	var role ={'name':rolesSelected[i].get("name"),'id':rolesSelected[i].get("id"),'description':rolesSelected[i].get("description"),'checked':true};
 				roles.push(role);
           }
	       params.userRoles =  Ext.util.JSON.encode(roles);
  
       	   var userRoles =new Array();
	       var tempArr = Ext.getCmp("roles-form").store;
           var length = Ext.getCmp("roles-form").store.data.length;

           for(var i=0;i<length;i++){
           		var selected = false;
           		for(var j=0;j<lengthR;j++){
           			if(rolesSelected[j].get("id")===tempArr.getAt(i).get("id")){
           				selected = true;
           				var role ={'name':tempArr.getAt(i).get("name"),'id':tempArr.getAt(i).get("id"),'description':tempArr.getAt(i).get("description"),'checked':true};
							userRoles.push(role);
           				break;
           			}
	            }
	            if(!selected){
	          		var role ={'name':tempArr.getAt(i).get("name"),'id':tempArr.getAt(i).get("id"),'description':tempArr.getAt(i).get("description"),'checked':false};
	 				userRoles.push(role);
 				}
		    }	
      
	        var modifAttributes = this.attributesStore.getModifiedRecords();
            var lengthA = modifAttributes.length;
            var attrs =new Array();
            for(var i=0;i<lengthA;i++){
             	var attr ={'name':modifAttributes[i].get("name"),'id':modifAttributes[i].get("id"),'value':modifAttributes[i].get("value")};
 				attrs.push(attr);
            }
	        params.userAttributes =  Ext.util.JSON.encode(attrs);      
	        
	        
       	    var userAttributes = new Array();
	        var tempArr = Ext.getCmp("attributes-form").store;
            var length = Ext.getCmp("attributes-form").store.data.length;

            for(var i=0;i<length;i++){
          		var attr ={'name':tempArr.getAt(i).get("name"),'id':tempArr.getAt(i).get("id"),'value':tempArr.getAt(i).get("value")};
 				userAttributes.push(attr);
		    }	

 			if(idRec ==0 || idRec == null || idRec === ''){
	          newRec =new Ext.data.Record({'userId': values['userId'],'fullName': values['fullName'],'pwd':values['pwd']});	  
	          newRec.set('userRoles', userRoles);
			  newRec.set('userAttributes', userAttributes);        
	        }else{
				var record;
				var length = this.usersStore.getCount();
				for(var i=0;i<length;i++){
		   	        var tempRecord = this.usersStore.getAt(i);
		   	        if(tempRecord.data.id==idRec ){
		   	        	record = tempRecord;
					}			   
		   	    }	
				record.set('userId',values['userId']);
				record.set('fullName',values['fullName']);
				if(values['pwd'] != undefined){
					record.set('pwd',values['pwd']);
				}
				
				record.set('userRoles',userRoles);
				record.set('userAttributes',userAttributes);				      
			}
    
	        
	        Ext.Ajax.request({
	            url: this.services['saveUserService'],
	            params: params,
	            method: 'GET',
	            success: function(response, options) {
					if (response !== undefined) {		
			      		if(response.responseText !== undefined) {
			      			var content = Ext.util.JSON.decode( response.responseText );
			      			if(content.responseText !== 'Operation succeded') {
			                    Ext.MessageBox.show({
			                        title: LN('sbi.roles.error'),
			                        msg: content,
			                        width: 150,
			                        buttons: Ext.MessageBox.OK
			                   });
			      			}else{
			      			    
								var idTemp = content.id;
								if(newRec!==null){
									newRec.set('id', idTemp);
									this.usersStore.add(newRec);
								}
								this.attributesStore.commitChanges();
								this.rolesStore.commitChanges();
								this.usersStore.commitChanges();
								if(newRec!==null){
									var grid = Ext.getCmp('usergrid');
						            grid.getSelectionModel().selectLastRow(true);
					            }
								Ext.MessageBox.show({
			                        title: LN('sbi.attributes.result'),
			                        msg: LN('sbi.attributes.resultMsg'),
			                        width: 200,
			                        buttons: Ext.MessageBox.OK
			                });		
							
			      			}
			      		} else {
			      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
			      		}
					} else {
						Sbi.exception.ExceptionHandler.showErrorMessage('Error while saving User', 'Service Error');
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
		                    title: LN('sbi.attributes.validationError'),
		                    msg: errMessage,
		                    width: 400,
		                    buttons: Ext.MessageBox.OK
		               });
		      		}else{
		                Ext.MessageBox.show({
		                    title:LN('sbi.roles.error'),
		                    msg: 'Error in Saving User',
		                    width: 150,
		                    buttons: Ext.MessageBox.OK
		               });
		      		}
	            }
	            ,scope: this
       		 });
			
		}else{
			alert(LN('sbi.users.pwdNotMatching'))			
		}
	
    }
	, addNewUser : function(){
		Ext.getCmp('save-btn').enable();
		var emptyRecToAdd =new Ext.data.Record({userId:'', 
											fullName:'', 
											pwd:'',
											userRoles:'',
											userAttributes:'',
											id: 0
											});
	
		Ext.getCmp('user-form').getForm().loadRecord(emptyRecToAdd);
		
		Ext.getCmp("attributes-form").store.removeAll();
	   	var tempAttrArr = this.attributesEmptyStore;
	   	var length = this.attributesEmptyStore.length;
        for(var i=0;i<length;i++){
        	var tempRecord = new Ext.data.Record({"value":tempAttrArr[i].value,"name":tempAttrArr[i].name,"id":tempAttrArr[i].id });
    		Ext.getCmp("attributes-form").store.add(tempRecord);	
        }		
        
        Ext.getCmp("roles-form").store.removeAll();
        var tempRolesArr = this.rolesEmptyStore;
        var length2 = this.rolesEmptyStore.length;
        for(var i=0;i<length2;i++){
          	var tempRecord = new Ext.data.Record({"description":tempRolesArr[i].description,"name":tempRolesArr[i].name,"id":tempRolesArr[i].id });
			Ext.getCmp("roles-form").store.add(tempRecord);								   
        }	
        Ext.getCmp("detail").items.each(function(item){
            if(item.getItemId() == 'pwdId' || item.getItemId() == 'confirmpwdId'){
        		  item.enable();
    		}	
        });
        Ext.getCmp('changePwd').hide();
		Ext.getCmp('user-form').doLayout();
	}
	
	, deleteSelectedUser: function(userId, index) {
		Ext.MessageBox.confirm(
		     LN('sbi.users.confirm'),
            LN('sbi.users.confirmDelete'),            
            function(btn, text) {
                if (btn=='yes') {
                	if (userId != null) {	

						Ext.Ajax.request({
				            url: this.services['deleteUserService'],
				            params: {'ID': userId},
				            method: 'GET',
				            success: function(response, options) {
								if (response !== undefined) {
									//this.rolesStore.load();
									var sm = Ext.getCmp('usergrid').getSelectionModel();
									var deleteRow = sm.getSelected();
									this.usersStore.remove(deleteRow);
									this.usersStore.commitChanges();
									var grid = Ext.getCmp('usergrid');
									if(this.usersStore.getCount()>0){
										grid.getSelectionModel().selectRow(0);
										grid.fireEvent('rowclick', grid, 0);
									}else{
										this.addNewUser();
									}
								} else {
									Sbi.exception.ExceptionHandler.showErrorMessage('Error while deleting User', 'Service Error');
								}
				            },
				            failure: function() {
				                Ext.MessageBox.show({
				                    title: LN('sbi.roles.error'),
				                    msg: 'Error in deleting User',
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

Ext.reg('manageusers', Sbi.profiling.ManageUsers);
