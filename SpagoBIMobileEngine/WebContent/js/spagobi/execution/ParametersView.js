app.views.ParametersView = Ext.extend(
		Ext.Panel,
		{
			fullscreen: true,
			dockedItems : [ {
				xtype : 'toolbar',
				dock : 'bottom',
				defaults : {
					ui : 'plain',
					iconMask : true
				},
				scroll : 'horizontal',
				layout : {
					pack : 'center'
				},
				items : [
				         {
				        	 title : 'Home',
				        	 iconCls : 'reply',
				        	 text : 'Home',
				        	 handler : function() {
				        		 Ext
				        		 .dispatch({
				        			 controller : app.controllers.mobileController,
				        			 action : 'backToBrowser'
				        		 });

				        	 }
				         },
				         {
				        	 title : 'Esegui',
				        	 iconCls : 'settings',
				        	 text : 'Esegui',
				        	 handler : function() {
				        		 var executionInstance = app.controllers.parametersController.executionInstance;
				        		 executionInstance.PARAMETERS = app.controllers.parametersController.getFormState();
				        		 Ext.dispatch({
				        			 controller : app.controllers.executionController,
				        			 action : 'executeTemplate',
				        			 executionInstance : executionInstance
				        		 });
				        	 }
				         } ]

			} ],

			initComponent : function() {
				this.html = '  ';
				app.views.ParametersView.superclass.initComponent.apply(this, arguments);

			}

			,refresh : function(items) {

				this.removeAll();
	
				var fieldset = new Ext.form.FieldSet({
						title : 'Document Parameters',
						xtype : 'fieldset',
						items : items
				});
	
				var formPanel = new Ext.form.FormPanel({items: [fieldset]});
				
				this.insert(0,formPanel);

				this.doLayout();


			}
});