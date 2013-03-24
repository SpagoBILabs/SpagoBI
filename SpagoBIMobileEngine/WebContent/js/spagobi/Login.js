/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
Ext.define('app.views.LoginView',{
	extend: 'Ext.Panel',
	config:{
		fullscreen : true,
		userIDField : null,
		pwdField : null,
		loginUrl : null,
		layout:"fit",
		style: 'background-color: #747474;'		

	},
	
	initialize : function() {

		this.loginUrl = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName : 'LOGIN_ACTION'
		});
		console.log("Log in URL: "+ this.loginUrl);
		
		app.views.form = Ext.create('Ext.form.FormPanel',{
				floating : true,
	            modal: true,
	            centered: true,
	            width: 320,
	            height: 270,
	            submitOnAction: true,
	            ui: 'dark',
	            
                listeners:{
                	submit: function( f, result, e, eOpts ) {
                		alert('bu');
                	}
                },
	            items:[
	                   {
	                       xtype : 'fieldset',
	                       cls: 'login-fieldset-top',
	                       items:[{
		               			xtype : 'textfield',
		            			name : 'userID',
		            			//label : 'Username',
		            			placeHolder : 'Username',
		            			useClearIcon : true
		
		                    },{
		            			xtype : 'passwordfield',
		            			name : 'password',
		            			//label : 'Password',
		            			placeHolder : 'Password',
		            			useClearIcon : false
		                    }]
	                   		,dockedItems:[]      
	                    	   
	                   }
	                   ,
	                    {
	                        docked: 'bottom',
	                        xtype: 'toolbar',
	                        height:30,
	                        style:'padding : 7px;',
	                        ui: 'light',
	                        items:[
	                            {
	                            	xtype: 'spacer'
	                            },
	                        	{
	                                text: 'Login',
	                                ui: 'confirm',
	                                handler: function(){
	                                	app.views.loginView.doSubmit(app.views.form.getValues());
	                                }
	                            }
	                        ]
	                    }
	            ],
	            scrollable: true
	        });
		this.add(app.views.form);

		app.views.form.on('submit', function() {
			this.doSubmit();
			return false;
		}, this);
		
		this.callParent(this, arguments);

	}

	, doSubmit : function(user) {

		var userid =  user.userID;
		var pwd = user.password;
		Ext.Ajax.request({
			url : app.views.loginView.loginUrl,
			scope : this,
			method : 'post',
			params : {
				userID : userid,
				password : pwd,
				roleGlobalLbl : roleGlobal
			},
			failure : function(response) {
				console.log('call Error! ');
				Sbi.exception.ExceptionHandler.handleFailure(response);
			}

			,
			success : function(response, opts) {
				if (response.responseText.indexOf('<') == -1) {
					var content = Ext.util.JSON.decode(response.responseText);
					var esito = content.text;
					if (esito == 'userhome') {
						app.views.form.hide();
						localStorage.setItem('app.views.launched', 'true');
						app.controllers.mobileController.login();
					
						
					} else {
						Ext.Msg.alert('','<p style="color:#fff; font-weight: bold;">Login</p><br/>Authentication failure!',Ext.emptyFn);
						return;
					}
				} else {
					Ext.Msg.alert('','<p style="color:#fff;font-weight: bold;">Login</p><br/>Authentication failure!',Ext.emptyFn);
					return;
				}
			}

		});
	}

});
