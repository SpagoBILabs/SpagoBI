app.views.LoginForm = Ext.extend(Ext.form.FormPanel,
		{
		name: 'login-form',	
        autoRender: true,
        floating: true,
        modal: true,
        centered: true,
        hideOnMaskTap: false,
        height: 385,
        width: 480,
        scroll : 'vertical',
        url    : this.loginUrl,
        standardSubmit : false,
        userIDField: null, 
        pwdField: null,
        loginUrl: null,
        fullscreen : false,
	
        dockedItems: [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                scope:this,
                items: [
                    {
                        text: 'Login',
                        ui: 'confirm',
                        scope: this,
                        handler: function() {
                    		  var form = app.views.loginForm;
                    		  var userid = form.userIDField.getValue();
                    		  var pwd = form.pwdField.getValue();
                              Ext.Ajax.request({
                                  url: form.loginUrl,
                                  scope: this,
                                  method: 'post',
                                  params: {userID: userid, password : pwd},
                                  failure : function(response){
                                        console.log('call Error! ');
                                  }
                                  
                                  ,success: function(response, opts) {
                                	  if(response.responseText.indexOf('<') == -1){
	                                	  var content = Ext.util.JSON.decode( response.responseText );	              		      			 
	                                      var esito = content.text;
	                                      if(esito=='userhome'){
	                                    	  //alert('login OK!!!!');
	                                    	  Ext.dispatch({
	                                    		  controller: app.controllers.mobileController,
	                                    		  action: 'login',
	                                    		  animation: {
	                                    		  type: 'slide',
	                                    		  direction: 'right'
	                                    		  }
	                                    	  });
	                                      }else{
	                                    	  alert('Authentication failure!');
	                                    	  return;
	                                      }
                                	  }else{
                                		  alert('Authentication failure!');
                                    	  return;
                                	  }
                                  }
                                  
                              });
/*                              form.submit({
                                  waitMsg : {message:'Submitting', cls : 'loading'},
                
                              });*/
                        }
                    }
                ]
            }
        ],
        initComponent: function() 	{

		    Sbi.config = {};
			
			var url = {
		    	host: 'localhost'
		    	, port: '8080'
		
		    };
		
		    var executionContext = {};
		    
		    Sbi.config.serviceRegistry = new Sbi.service.ServiceRegistry({
		    	baseUrl: url
		
		    });
			this.loginUrl = Sbi.config.serviceRegistry.getServiceUrl({
				serviceName: 'LOGIN_ACTION'
			});	
			console.log(this.loginUrl);
		
			this.userIDField = new Ext.form.Text({                                
				xtype: 'textfield',
		        name : 'userID',
		        label: 'Username',
		        useClearIcon: true});
			
			this.pwdField = new Ext.form.Text({                                
		        xtype: 'passwordfield',
		        name : 'password',
		        label: 'Password',
		        useClearIcon: false});
			
			this.items =[
			             {
			                 xtype: 'fieldset',
			                 title: 'SpagoBI Mobile Login',
			                 instructions: 'Please login.',
			                 defaults: {
			                     required: true,
			                     labelAlign: 'left',
			                     labelWidth: '45%'
			                 },
			                 items: [
			                 	this.userIDField, 
			                 	this.pwdField]
			             }];
			
			app.views.LoginForm.superclass.initComponent.apply(this, arguments);

		}

		});
        
        