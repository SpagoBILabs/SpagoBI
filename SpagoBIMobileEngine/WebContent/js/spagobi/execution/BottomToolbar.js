app.views.BottomToolbar = Ext.extend(Ext.Toolbar,
        {xtype: 'toolbar',
        dock: 'bottom',
        defaults: {
            ui: 'plain',
            iconMask: true
        },
        scroll: 'horizontal',
        layout: {
            pack: 'center'
        },

		initComponent: function ()	{

			console.log('init bottom toolbar view');

			var par = this.parameters;
			
			this.docParams = new Ext.Button({
				    title: 'Parametri',    		    
				    iconCls: 'compose',
				    text: 'Parametri',
				    scope: this,
					listeners:{
						scope: this,
						tap: function(){

							Ext.dispatch({
			                    controller: app.controllers.mobileController,
			                    action: 'backToParametersView',
			                    params : this.parameters
			        		});
						}
					}
				});
			this.docHome = {
				    title: 'Home',    		    
				    iconCls: 'reply',			    
				    text: 'Home',
		            handler: function () {
		        		Ext.dispatch({
		                    controller: app.controllers.mobileController,
		                    action: 'backToBrowser'
		        		});
		
		            }};
			this.docInfo = {
				    title: 'Info',    		    
				    iconCls: 'info',
				    text: 'Info'

		        };
			this.items =[this.docHome, this.docParams, this.docInfo];
			
			app.views.BottomToolbar.superclass.initComponent.apply(this, arguments);
			this.configureItems(this.parameters);

		}
		, configureItems: function(par){

			if(par == undefined || par == null){
				this.docParams.hide();
			}

		}
});