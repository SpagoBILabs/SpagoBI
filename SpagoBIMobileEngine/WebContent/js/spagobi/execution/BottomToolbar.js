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
		            handler: function () {
		        		Ext.dispatch({
		                    controller: app.controllers.mobileController,
		                    action: 'backToBrowser'
		        		});
		
		            }});
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
			this.configureItems(this.parameters);
			app.views.BottomToolbar.superclass.initComponent.apply(this, arguments);
			

		}
		, configureItems: function(par){

			if(par == undefined || par == null){
				this.docParams.hide();
			}else{
				app.views.parameters.refresh(par);
				app.views.viewport.setActiveItem(app.views.parameters);
			}

		}

});