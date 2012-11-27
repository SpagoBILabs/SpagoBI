/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
 app.views.LoginView = Ext.extend(Ext.Panel, {
	fullscreen : true,
	userIDField : null,
	pwdField : null,
	loginUrl : null,
	style: 'background-color: #747474;',
	initComponent : function() {

		Sbi.config = {};

		var url = {
			host : hostGlobal,
			port : portGlobal

		};

		var executionContext = {};

		Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
			baseUrl : url

		});
		this.loginUrl = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName : 'LOGIN_ACTION'
		});
		console.log(this.loginUrl);

		this.userIDField = new Ext.form.Text({
			xtype : 'textfield',
			name : 'userID',
			//label : 'Username',
			placeHolder : 'Username',
			useClearIcon : true
		});

		this.pwdField = new Ext.form.Password({
			xtype : 'passwordfield',
			name : 'password',
			//label : 'Password',
			placeHolder : 'Password',
			useClearIcon : false
		});

		//creates form panel

		app.views.form = new Ext.form.FormPanel({
			autoRender : true,
			floating : true,
			modal : true,
			centered : true,
			hideOnMaskTap : false,
			height : 280,
			width : 320,
			items : [ {
				xtype : 'fieldset',
				cls: 'login-fieldset-top',
				items : [ this.userIDField, this.pwdField ]
			} ],

			dockedItems : [ {
				xtype : 'toolbar',
				dock : 'bottom',
				scope : this,
				items : [ {xtype: 'spacer'},{
					text : 'Login',
					ui : 'confirm',
					scope : this,
					handler : this.doSubmit
				} ]
			} ]
		});
		app.views.form.on('beforesubmit', function() {
			this.doSubmit();
			return false;
		}, this);
		app.views.form.show();
		//this.add(this.form);
		app.views.LoginView.superclass.initComponent.apply(this, arguments);

	}

	, doSubmit : function() {

		var userid = app.views.loginView.userIDField.getValue();
		var pwd = app.views.loginView.pwdField.getValue();
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
						Ext.dispatch({
							controller : app.controllers.mobileController,
							action : 'login',
							animation : {
								type : 'slide',
								direction : 'right'
							}
						});
					} else {
						Ext.Msg.alert('','<p style="color:#fff; font-weight: bold;">Login</p><br/>Authentication failure!',Ext.emptyFn).doLayout();
						return;
					}
				} else {
					Ext.Msg.alert('','<p style="color:#fff;font-weight: bold;">Login</p><br/>Authentication failure!',Ext.emptyFn).doLayout();
					return;
				}
			}

		});
	}

});
