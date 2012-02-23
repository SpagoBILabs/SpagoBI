app.views.LoginView = Ext.extend(Ext.Panel, {
	fullscreen : true,
	userIDField : null,
	pwdField : null,
	loginUrl : null,

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
			label : 'Username',
			placeHolder : 'Username',
			useClearIcon : true
		});

		this.pwdField = new Ext.form.Password({
			xtype : 'passwordfield',
			name : 'password',
			label : 'Password',
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
			height : 350,
			width : 450,
			items : [ {
				xtype : 'fieldset',
				title : 'SpagoBI Mobile Login',
				instructions : 'Please login.',

				defaults : {
					required : true,
					labelAlign : 'left',
					labelWidth : '45%'
				},
				items : [ this.userIDField, this.pwdField ]
			} ],
			dockedItems : [ {
				xtype : 'toolbar',
				dock : 'bottom',
				scope : this,
				items : [ {
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
				password : pwd
			},
			failure : function(response) {
				console.log('call Error! ');
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
