app.views.LoginForm = Ext.extend(Ext.form.FormPanel,
		{
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
            
        listeners : {
            submit : function(form, result){
                console.log('success', Ext.toArray(arguments));

            },
            exception : function(form, result){
                console.log('failure', result);
                //this.form.close();

            }
        },
		
        dockedItems: [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                items: [
                    {
                        text: 'Login',
                        ui: 'confirm',
                        scope: this,
                        handler: function() {

                              Ext.Ajax.request({
                                  url: formBase.url,
                                  method: 'post',
                                  params: {userID: this.userIDField.getValue(), password : this.pwdField.getValue()},
                                  failure : function(response){
                                        console.log('call Error! ');
                                  }
                                  
                                  ,success: function(response, opts) {
                                	  var content = Ext.util.JSON.decode( response.responseText );
              		      			 
                                      var esito = content.text;
                                      if(esito=='userhome'){
                                    	  alert('login OK!!!!');
                                      }else{
                                    	  alert('Authentication failure!');
                                    	  return;
                                      }
                                  }
                                  
                              });
                              this.submit({
                                  waitMsg : {message:'Submitting', cls : 'loading'}
                              });
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
		
			this.userIDField = new Ext.form.TextField({                                
				xtype: 'textfield',
		        name : 'userID',
		        label: 'Username',
		        useClearIcon: true});
			
			this.pwdField = new Ext.form.TextField({                                
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
        
        