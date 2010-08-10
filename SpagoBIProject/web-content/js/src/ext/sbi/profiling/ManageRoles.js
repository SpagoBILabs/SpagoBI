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
Ext.ns("Sbi.profiling");

Sbi.profiling.ManageRoles = function(config) { 

	var paramsList = {MESSAGE_DET: "ROLES_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "ROLE_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "ROLE_DELETE"};
	
	this.services = new Array();
	this.services['manageRolesList'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ROLES_ACTION'
		, baseParams: paramsList
	});
	this.services['saveRoleService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ROLES_ACTION'
		, baseParams: paramsSave
	});
	
	this.services['deleteRoleService'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ROLES_ACTION'
		, baseParams: paramsDel
	});

	this.rolesStore = new Ext.data.JsonStore({
    	autoLoad: false    	  
    	,id : 'id'		
    	,fields: ['id'
    	          , 'name'
    	          , 'description'
    	          , 'code'
    	          , 'typeCd'
    	          , 'savePersonalFolder'
    	          , 'saveMeta'
    	          , 'saveRemember'
    	          , 'saveSubobj'
    	          , 'seeMeta'
    	          , 'seeNotes'
    	          , 'seeSnapshot'
    	          , 'seeSubobj'
    	          , 'seeViewpoints'
    	          , 'sendMail'
    	          , 'buildQbe'
    	          ]
    	, root: 'samples'
		, url: this.services['manageRolesList']
		//, writer: this.writer
		
	});

	this.rolesStore.load();

	this.initManageRoles();
	   
   	Ext.getCmp('rolegrid').store.on('load', function(){
	 var grid = Ext.getCmp('rolegrid');
	 grid.getSelectionModel().selectRow(0);
	 //grid.fireEvent('rowclick', grid, 0);
	 }, this, {
	 single: true
   });
   	
   	
   	Ext.getCmp('rolegrid').on('delete', this.deleteSelectedRole, this);

}

Ext.extend(Sbi.profiling.ManageRoles, Ext.FormPanel, {
	
	gridForm:null
	, rolesStore:null
	, colModel:null
	,typeData: null
	,buttons: null
	, tabs: null

	
	,initManageRoles: function(){


       this.deleteColumn = new Ext.grid.ButtonColumn({
	       header:  ' ',
	       iconCls: 'icon-remove',
	       clickHandler: function(e, t) {

	          var index = Ext.getCmp("rolegrid").getView().findRowIndex(t);
	          
	          var selectedRecord = Ext.getCmp("rolegrid").store.getAt(index);
	          var roleId = selectedRecord.get('id');

	          Ext.getCmp("rolegrid").fireEvent('delete', roleId, index);
	       }
	       ,width: 25
	       ,renderer : function(v, p, record){
	           return '<center><img class="x-mybutton-'+this.id+' grid-button ' +this.iconCls+'" width="16px" height="16px" src="'+Ext.BLANK_IMAGE_URL+'"/></center>';
	       }
       });
       this.colModel = new Ext.grid.ColumnModel([
         {id:'name',header: LN('sbi.attributes.headerName'), width: 50, sortable: true, locked:false, dataIndex: 'name'},
         {header:  LN('sbi.attributes.headerDescr'), width: 150, sortable: true, dataIndex: 'description'},
         this.deleteColumn
      ]);


 	   
 	    /*====================================================================
 	     * CheckGroup Is able to
 	     *====================================================================*/

 	    this.checkGroup = {
           xtype:'fieldset'
           ,id: 'checks-form'
           ,columnWidth: 0.8
           ,autoHeight:true
           ,autoWidth: true
           , defaults: {
               anchor: '-20' // leave room for error icon
           }
           ,items :[
				{
		            xtype: 'checkboxgroup',
		            itemId: 'isAbleToSave',
		            columns: 1,
		            boxMinWidth  : 150,
		            boxMinHeight  : 100,
		            hideLabel  : false,
		            fieldLabel: LN('sbi.roles.save')
		            ,items: [
		                {boxLabel: LN('sbi.roles.savePersonalFolder'), name: 'savePersonalFolder', checked:'savePersonalFolder',inputValue: 1},
		                {boxLabel: LN('sbi.roles.saveMeta'), name: 'saveMeta', checked:'saveMeta',inputValue: 1},
		                {boxLabel: LN('sbi.roles.saveRemember'), name: 'saveRemember', checked:'saveRemember',inputValue: 1},
		                {boxLabel: LN('sbi.roles.saveSubobj'), name: 'saveSubobj', checked:'saveSubobj',inputValue: 1}
		            ]
		        },
		        {
		            xtype: 'checkboxgroup',
		            itemId: 'isAbleToSee',
		            columns: 1,
		            boxMinWidth  : 150,
		            boxMinHeight  : 100,
		            hideLabel  : false,
		            fieldLabel: LN('sbi.roles.see'),
		            items: [
		                {boxLabel: LN('sbi.roles.seeMeta'), name: 'seeMeta', checked: 'seeMeta', inputValue: 1},
		                {boxLabel: LN('sbi.roles.seeNotes'), name: 'seeNotes', checked:'seeNotes',inputValue: 1},
		                {boxLabel: LN('sbi.roles.seeSnapshot'), name: 'seeSnapshot', checked:'seeSnapshot',inputValue: 1},
		                {boxLabel: LN('sbi.roles.seeSubobj'), name: 'seeSubobj', checked:'seeSubobj',inputValue: 1},
		                {boxLabel: LN('sbi.roles.seeViewpoints'), name: 'seeViewpoints', checked:'seeViewpoints',inputValue: 1}
		            ]
		        },
		        {
		            xtype: 'checkboxgroup',
		            columns: 1,
		            boxMinWidth  : 150,
		            hideLabel  : false,
		            fieldLabel: LN('sbi.roles.send'),
		            itemId: 'isAbleToSend',
		            //height:200,
		            items: [
		                {boxLabel: LN('sbi.roles.sendMail'), name: 'sendMail', checked:'sendMail',inputValue: 1}
		            ]
		        },
		        {
		            xtype: 'checkboxgroup',
		            columns: 1,
		            boxMinWidth  : 150,
		            hideLabel  : false,
		            fieldLabel: LN('sbi.roles.build'),
		            itemId: 'isAbleToBuild',
		            items: [
		                {boxLabel: LN('sbi.roles.buildQbe'), name: 'buildQbe', checked:'buildQbe',inputValue: 1}
		            ]
		        }
           ]
 	    };
 	    this.typesStore = new Ext.data.SimpleStore({
 	        fields: ['typeCd'],
 	        data: config,
 	        autoLoad: false
 	    });
 	    
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
           , id: 'tab-panel'
           , activeTab : 0
           , autoScroll : true
           , width: 450
           , height: 500
           , itemId: 'tabs' 
           , tbar: this.tbSave
		   , items: [{
		        title: LN('sbi.roles.details')
		        , itemId: 'detail'
		        , width: 430
		        , items: {
			   		id: 'role-detail',   	
		 		   	itemId: 'role-detail',   	              
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
		                 fieldLabel: LN('sbi.roles.headerName'),
		                 allowBlank: false,
		                 validationEvent:true,
		                 //preventMark: true,
		                 name: 'name'
		             },{
		            	 maxLength:160,
		            	 minLength:1,
		            	 regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
		            	 regexText : LN('sbi.roles.alfanumericString'),
		                 fieldLabel: LN('sbi.roles.headerDescr'),
		                 validationEvent:true,
		                 name: 'description'
		             },{
		            	 maxLength:20,
		            	 minLength:0,
		            	 regex : new RegExp("^([A-Za-z0-9_])+$", "g"),
		            	 regexText : LN('sbi.roles.alfanumericString2'),
		                 fieldLabel:LN('sbi.roles.headerCode'),
		                 validationEvent:true,
		                 name: 'code'
		             }, {
		            	  name: 'typeCd',
		                  store: this.typesStore,
		                  fieldLabel: LN('sbi.roles.headerRoleType'),
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
		    },{
		        title: LN('sbi.roles.authorizations')
		        , width: 430
		        , items: this.checkGroup
		        , itemId: 'checks'
		    }]
		});

 	    this.tb = new Ext.Toolbar({
 	    	buttonAlign : 'right',
 	    	items:[new Ext.Toolbar.Button({
 	            text: LN('sbi.attributes.add'),
 	            iconCls: 'icon-add',
 	            handler: this.addNewRole,
 	            width: 30,
 	            scope: this
 	        })
 	    	]
 	    });

   	   /*
   	   *    Here is where we create the Form
   	   */
   	   this.gridForm = new Ext.FormPanel({
   	          id: 'role-form',
   	          frame: true,
   	          labelAlign: 'left',
   	          title: LN('sbi.roles.rolesManagement'),
   	          bodyStyle:'padding:5px',
   	          width: 850,
   	          height: 600,
   	          layout: 'column',
   	          trackResetOnLoad: true,
	          //buttons: this.buttons,
	          //buttonAlign: 'right',
	          renderTo: Ext.getBody(),
   	          items: [{
   	              columnWidth: 0.90,
   	              layout: 'fit',
   	              items: {
   	        	  	  id: 'rolegrid',
   	                  xtype: 'grid',
   	                  ds: this.rolesStore,   	                  
   	                  cm: this.colModel,
   	                  plugins: this.deleteColumn,
   	                  sm: new Ext.grid.RowSelectionModel({
   	                      singleSelect: true,
   	                      scope:this,   	                   
	   	                  fillChecks : function(row, rec) {	  
	   	                   	  Ext.getCmp("checks-form").items.each(function(item){	   	                   		  
	   	                   		  if(item.getItemId() == 'isAbleToSave'){
		   	                   		  item.setValue('saveMeta', rec.get('saveMeta'));
		   	                   		  item.setValue('saveRemember', rec.get('saveRemember'));
		   	                   		  item.setValue('saveSubobj', rec.get('saveSubobj'));	   	              
		   	                   		  item.setValue('savePersonalFolder', rec.get('savePersonalFolder'));
	   	                   		  }else if(item.getItemId() == 'isAbleToSee'){
		   	                   		  item.setValue('seeMeta', rec.get('seeMeta'));
		   	                   		  item.setValue('seeNotes', rec.get('seeNotes'));
		   	                   		  item.setValue('seeSnapshot', rec.get('seeSnapshot'));	   	              
		   	                   		  item.setValue('seeSubobj', rec.get('seeSubobj'));
		   	                   		  item.setValue('seeViewpoints', rec.get('seeViewpoints'));
	   	                   		  }else if(item.getItemId() == 'isAbleToSend'){
		   	                   		  item.setValue('sendMail', rec.get('sendMail'));
	   	                   		  }else if(item.getItemId() == 'isAbleToBuild'){
		   	                   		  item.setValue('buildQbe', rec.get('buildQbe'));
	   	                   		  }

			   	        	  });
	   	                   	 
	   	                  },
   	                      listeners: {
   	                          rowselect: function(sm, row, rec) { 
   	                	  		  this.fillChecks(row, rec);
   	                              Ext.getCmp('role-form').getForm().loadRecord(rec);      	
   	                          }
   	                      }
   	                  }),
   	                  autoExpandColumn: 'name',
   	                  height: 500,
   	                  width: 400,
   	                  layout: 'fit',
   	                  title: LN('sbi.roles.rolesList'),
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
	, addNewRole : function(){
	
		var emptyRecToAdd =new Ext.data.Record({
											id: 0,
											name:'', 
											label:'', 
											description:'',
											typeCd:'',
											code:'',
											saveSubobj: true,
											seeSubobj:true,
											seeViewpoints:true,
											seeSnapshot:true,
											seeNotes:true,
											sendMail:true,
											savePersonalFolder:true,
											saveRemember:true,
											seeMeta:true,
											saveMeta:true,
											buildQbe:true
											});
	
		Ext.getCmp('role-form').getForm().loadRecord(emptyRecToAdd);
	
	    this.tabs.items.each(function(item)
	    {		
	    	if(item.getItemId() == 'checks'){
	
	    		item.items.each(function(itemTab){
	
	    			itemTab.items.each(function(item1){
	
	    				item1.setValue({
							'saveSubobj': true,
							'seeSubobj':true,
							'seeViewpoints':true,
							'seeSnapshot':true,
							'seeNotes':true,
							'sendMail':true,
							'savePersonalFolder':true,
							'saveRemember':true,
							'seeMeta':true,
							'saveMeta':true,
							'buildQbe':true
	    				});
	
	    			});
	    		});
	    		
	    	}
	    	item.doLayout();
	    });   
		Ext.getCmp('role-form').doLayout();
	
	}
	,save : function() {
		var values = this.gridForm.getForm().getValues();
		var idRec = values['id'];
		var newRec;
	
		if(idRec ==0 || idRec == null || idRec === ''){
			newRec =new Ext.data.Record({
					name :values['name'],
			        description :values['description'],
			        typeCd :values['typeCd'],
			        code :values['code']
			});	  

			newRec = this.fillRecord(newRec);
			
		}else{
			var record;
			var length = this.rolesStore.getCount();
			for(var i=0;i<length;i++){
	   	        var tempRecord = this.rolesStore.getAt(i);
	   	        if(tempRecord.data.id==idRec){
	   	        	record = tempRecord;
				}			   
	   	    }	
			record.set('name',values['name']);
			record.set('description',values['description']);
			record.set('typeCd',values['typeCd']);
			record.set('code',values['code']);
			
			newRec = this.fillRecord(record);
			
		}


        var params = {
        	name : newRec.data.name,
        	description : newRec.data.description,
        	typeCd : newRec.data.typeCd,
        	code : newRec.data.code,
			saveSubobj: newRec.data.saveSubobj,
			seeSubobj:newRec.data.seeSubobj,
			seeViewpoints:newRec.data.seeViewpoints,
			seeSnapshot:newRec.data.seeSnapshot,
			seeNotes:newRec.data.seeNotes,
			sendMail:newRec.data.sendMail,
			savePersonalFolder:newRec.data.savePersonalFolder,
			saveRemember:newRec.data.saveRemember,
			seeMeta:newRec.data.seeMeta,
			saveMeta:newRec.data.saveMeta,
			buildQbe:newRec.data.buildQbe
        };
        if(idRec){
        	params.id = newRec.data.id;
        }
        
        Ext.Ajax.request({
            url: this.services['saveRoleService'],
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
			      			var roleID = content.id;
			      			if(roleID != null && roleID !==''){
			      				newRec.set('id', roleID);
			      				this.rolesStore.add(newRec);  
			      			}
			      			this.rolesStore.commitChanges();
			      			if(roleID != null && roleID !==''){
								var grid = Ext.getCmp('rolegrid');
					            grid.getSelectionModel().selectLastRow(true);
				            }
			      			
			      			Ext.MessageBox.show({
			                        title: LN('sbi.attributes.result'),
			                        msg: LN('sbi.roles.resultMsg'),
			                        width: 200,
			                        buttons: Ext.MessageBox.OK
			                });

			      		}      				 

		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Error while saving Role', 'Service Error');
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
	                    title: LN('sbi.roles.error'),
	                    msg: 'Error while Saving Role',
	                    width: 150,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}
            }
            ,scope: this
        });
    }

	, fillRecord : function(record){
		var values = this.gridForm.getForm().getValues();
		
		//record.set('id'values['id']),
		
 
        var savePf =values['savePersonalFolder'];
        var saveSo =values['saveSubobj'];
        var seeSo =values['seeSubobj'];
        var seeV =values['seeViewpoints'];
        var seeSn =values['seeSnapshot'];
        var seeN =values['seeNotes'];
        var sendM =values['sendMail'];
        var saveRe =values['saveRemember'];
        var seeMe =values['seeMeta'];
        var saveMe =values['saveMeta'];
        var builQ =values['buildQbe'];      
        

		if(savePf == 1){
        	record.set('savePersonalFolder', true);
        }else{
        	record.set('savePersonalFolder', false);
        }
        if(saveSo == 1){
        	record.set('saveSubobj', true);
        }else{
        	record.set('saveSubobj', false);
        }
        if(seeSo == 1){
        	record.set('seeSubobj', true);
        }else{
        	record.set('seeSubobj', false);
        }
        if(seeV == 1){
        	record.set('seeViewpoints', true);
        }else{
        	record.set('seeViewpoints', false);
        }
        if(seeSn == 1){
        	record.set('seeSnapshot', true);
        }else{
        	record.set('seeSnapshot', false);
        }
        if(seeN == 1){
        	record.set('seeNotes', true);
        }else{
        	record.set('seeNotes', false);
        }
        if(sendM == 1){
        	record.set('sendMail', true);
        }else{
        	record.set('sendMail', false);
        }
        if(saveRe == 1){
        	record.set('saveRemember', true);
        }else{
        	record.set('saveRemember', false);
        }
        if(seeMe == 1){
        	record.set('seeMeta', true);
        }else{
        	record.set('seeMeta', false);
        }
        if(saveMe == 1){
        	record.set('saveMeta', true);
        }else{
        	record.set('saveMeta', false);
        }
        if(builQ == 1){
        	record.set('buildQbe', true);
        }else{
        	record.set('buildQbe', false);
        }

		return record;
		
	}
	, deleteSelectedRole: function(roleId, index) {
		Ext.MessageBox.confirm(
            'Please confirm',
            'Confirm role delete?',            
            function(btn, text) {
                if (btn=='yes') {
                	if (roleId != null) {	

						Ext.Ajax.request({
				            url: this.services['deleteRoleService'],
				            params: {'id': roleId},
				            method: 'GET',
				            success: function(response, options) {
								if (response !== undefined) {
									//this.rolesStore.load();
									var sm = Ext.getCmp('rolegrid').getSelectionModel();
									var deleteRow = sm.getSelected();
									this.rolesStore.remove(deleteRow);
									this.rolesStore.commitChanges();
									if(this.rolesStore.getCount()>0){
										var grid = Ext.getCmp('rolegrid');
										grid.getSelectionModel().selectRow(0);
										grid.fireEvent('rowclick', grid, 0);
									}else{
										this.addNewRole();
									}
								} else {
									Sbi.exception.ExceptionHandler.showErrorMessage('Error while deleting Role', 'Service Error');
								}
				            },
				            failure: function() {
				                Ext.MessageBox.show({
				                    title: LN('sbi.roles.error'),
				                    msg: 'Error while deleting Role',
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

Ext.reg('manageroles', Sbi.profiling.ManageRoles);
