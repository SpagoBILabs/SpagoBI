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
Ext.ns("Sbi.profiling");

Sbi.profiling.ManageRoles = function(config) { 

	var paramsList = {MESSAGE_DET: "ROLES_LIST"};
	var paramsSave = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "ROLE_INSERT"};
	var paramsDel = {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "ROLE_DELETE"};
	
	this.configurationObject = {};
	
	this.configurationObject.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ROLES_ACTION'
		, baseParams: paramsList
	});
	this.configurationObject.saveItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ROLES_ACTION'
		, baseParams: paramsSave
	});
	this.configurationObject.deleteItemService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_ROLES_ACTION'
		, baseParams: paramsDel
	});

	var configSecurity = {};
	configSecurity.isInternalSecurity = config.isInternalSecurity;
	this.initConfigObject(configSecurity);
	config.configurationObject = this.configurationObject;
	
	var c = Ext.apply({}, config || {}, {});

	Sbi.profiling.ManageRoles.superclass.constructor.call(this, c);	 
	
	this.rowselModel.addListener('rowselect',function(sm, row, rec) { 
		this.getForm().loadRecord(rec);  
		this.fillChecks(row, rec);
     }, this);

};

Ext.extend(Sbi.profiling.ManageRoles, Sbi.widgets.ListDetailForm, {
	
	configurationObject: null
	, gridForm:null
	, mainElementsStore:null
	, detailTab:null
	, authorizationTab:null
	, checkGroup: null

	,initConfigObject:function(configSecurity){
	    this.configurationObject.fields = ['id'
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
	                        	        ];
		
		this.configurationObject.emptyRecToAdd = new Ext.data.Record({
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
		
		this.configurationObject.gridColItems = [
					{id:'name',header: LN('sbi.attributes.headerName'), width: 200, sortable: true, locked:false, dataIndex: 'name'},
					{header:  LN('sbi.attributes.headerDescr'), width: 220, sortable: true, dataIndex: 'description'}
				];
		
		this.configurationObject.panelTitle = LN('sbi.roles.rolesManagement');
		this.configurationObject.listTitle = LN('sbi.roles.rolesList');
		
		/*create buttons toolbar's list (Add and Synchronize buttons)*/
		if (configSecurity.isInternalSecurity !== undefined && configSecurity.isInternalSecurity == false) {
			var tbButtonsArray = new Array();
			tbButtonsArray.push(new Ext.Toolbar.Button({
		            text: LN('sbi.roles.rolesSynchronization'),
		            iconCls: 'icon-refresh',
		            handler: this.synchronize,
		            width: 30,
		            scope: this	            
		            }));
			this.configurationObject.tbListButtonsArray = tbButtonsArray;
		}
		this.initTabItems();
    }

	,initTabItems: function(){
		
		this.initDetailtab();
		this.initChecksTab();
		
		this.configurationObject.tabItems = [ this.detailTab, this.authorizationTab];
	}

	,initDetailtab: function() {

		this.typesStore = new Ext.data.JsonStore({
 	        fields: ['typeCd', 'valueNm'],
 	        data: config,
 	        listeners: {
	                'load': {
                        fn: function( store, records, options) {
                             for (i=0; i< records.length; i++){ 
                            	 var a = LN(records[i].data.valueNm);                            	 
                            	 var b = records[i].data.typeCd;                            	 
                            	 
                            	 records[i].set('typeCd1', b);
                            	 records[i].set('valueNm1', a);
                            	 records[i].commit();
                             }
                             
                        }
	                }
	        },
 	        autoLoad: false
 	    });
		
		//START list of detail fields
	 	   var detailFieldId = {
	                 name: 'id',
	                 hidden: true
	       };
	 		   
	 	   var detailFieldName = {
	            	 maxLength:100,
	            	 minLength:1,
	            	 //regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	            	 regexText : LN('sbi.roles.alfanumericString'),
	                 fieldLabel: LN('sbi.roles.headerName'),
	                 allowBlank: false,
	                 validationEvent:true,
	                 //preventMark: true,
	                 name: 'name'
	             };
	 			  
	 	   var detailFieldCode = {
	            	 maxLength:20,
	            	 minLength:0,
	            	 //regex : new RegExp("^([A-Za-z0-9_])+$", "g"),
	            	 regexText : LN('sbi.roles.alfanumericString2'),
	                 fieldLabel:LN('sbi.roles.headerCode'),
	                 validationEvent:true,
	                 name: 'code'
	             };	  
	 		   
	 	   var detailFieldDescr = {
	            	 maxLength:160,
	            	 minLength:1,
	            	 //regex : new RegExp("^([a-zA-Z1-9_\x2F])+$", "g"),
	            	 regexText : LN('sbi.roles.alfanumericString'),
	                 fieldLabel: LN('sbi.roles.headerDescr'),
	                 validationEvent:true,
	                 name: 'description'
	             };


	 	   var detailFieldNodeType =  new Ext.form.ComboBox({
	            	  name: 'typeCd',
	            	  hiddenName: 'typeCd',
	                  store: this.typesStore,
	                  fieldLabel: LN('sbi.roles.headerRoleType'),
	                  displayField: 'valueNm1',   // what the user sees in the popup
	                  valueField: 'typeCd1',      // what is passed to the 'change' event
	                  typeAhead: true,
	                  forceSelection: true,
	                  mode: 'local',
	                  triggerAction: 'all',
	                  selectOnFocus: false,
	                  editable: false,
	                  allowBlank: false,
	                  validationEvent:true,
	                  tpl: '<tpl for="."><div ext:qtip="{typeCd1}" class="x-combo-list-item">{valueNm1}</div></tpl>'
	             });  

	 	  //END list of detail fields
	 	   
	 	  this.detailTab = new Ext.Panel({
		        title: LN('sbi.roles.details')
		        , id: 'detail'
		        , layout: 'fit'
		        , items: {
		 		   	     id: 'role-detail',   	              
		 		   	     columnWidth: 0.4,
			             xtype: 'fieldset',
			             labelWidth: 110,
			             defaults: {width: 220, border:false},    
			             defaultType: 'textfield',
			             autoHeight: true,
			             autoScroll  : true,
			             bodyStyle: Ext.isIE ? 'padding:0 0 5px 15px;' : 'padding:10px 15px;',
			             border: false,
			             style: {
			                 "margin-left": "20px", 
			                 "margin-right": Ext.isIE6 ? (Ext.isStrict ? "-20px" : "-23px") : "20"  
			             },
			             items: [ detailFieldId, detailFieldName, detailFieldCode, 
			                      detailFieldDescr, detailFieldNodeType]
		    	}
		    });

	}
	
	,initChecksTab: function(){
		
		 /*====================================================================
 	     * CheckGroup Is able to
 	     *====================================================================*/

 	    this.checkGroup = {
           xtype:'fieldset'
           ,id: 'checks-form'
           ,columnWidth: 0.8
           , width: 250
	        , height: 350
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
 	    
 	    this.authorizationTab = new Ext.Panel({
	        title: LN('sbi.roles.authorizations')
	        , width: 430
	        , items: this.checkGroup
	        , itemId: 'checks'
	        , layout: 'fit'
	    });
	}
	
	,fillChecks : function(row, rec) {
		Ext.getCmp('checks-form').items.each(function(item){	   	                   		  
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
        	 
       }
	
	
	///****


	//OVERRIDING ADD METHOD
	, addNewItem : function(){

		var emptyRecToAdd = new Ext.data.Record({
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
		
		this.getForm().loadRecord(emptyRecToAdd); 
		this.fillChecks(0, emptyRecToAdd);

		this.tabs.setActiveTab(0);
	}
	

, fillRecord : function(record){
		
		var values = this.getForm().getValues();	
 
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
	
	,save : function() {
		var values = this.getForm().getValues();
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
			var length = this.mainElementsStore.getCount();
			for(var i=0;i<length;i++){
	   	        var tempRecord = this.mainElementsStore.getAt(i);
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
            url: this.services['saveItemService'],
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
			      				this.mainElementsStore.add(newRec);  
			      			}
			      			this.mainElementsStore.commitChanges();
			      			if(roleID != null && roleID !==''){
								this.rowselModel.selectLastRow(true);
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
	
	,synchronize : function() {
		var syncUrl = Sbi.config.serviceRegistry.getServiceUrl({
					  serviceName: 'MANAGE_ROLES_ACTION'
					, baseParams: {LIGHT_NAVIGATOR_DISABLED: 'TRUE',MESSAGE_DET: "ROLES_SYNCHRONIZATION"}
			});
		
        Ext.Ajax.request({
            url: syncUrl,
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
			      			this.mainElementsStore.load();
			      			Ext.MessageBox.show({
			                        title: LN('sbi.roles.result'),
			                        msg: LN('sbi.roles.resultMsg'),
			                        width: 200,
			                        buttons: Ext.MessageBox.OK
			                });
			      		}      				 

		      		} else {
		      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
		      		}
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Error while synchronize Roles', 'Service Error');
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
	                    msg: 'Error while synchronize Roles',
	                    width: 150,
	                    buttons: Ext.MessageBox.OK
	               });
	      		}
            }
            ,scope: this
        });
    }
	

});
