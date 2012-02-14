app.views.ParametersView = Ext.extend(Ext.form.FormPanel,{
		
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        defaults: {
            ui: 'plain',
            iconMask: true
        },
        scroll: 'horizontal',
        layout: {
            pack: 'center'
        },
        items: [{
		    title: 'Home',    		    
		    iconCls: 'reply',			    
		    text: 'Home',
            handler: function () {
        		Ext.dispatch({
                    controller: app.controllers.mobileController,
                    action: 'backToBrowser'
        		});

            }},
            {
		    title: 'Esegui',    		    
		    iconCls: 'settings',
		    text: 'Esegui',
            handler: function () {
        		Ext.dispatch({
                    controller: app.controllers.executionController,
                    action: 'prepareDocumentForExecution',
                    parameters:  app.controllers.parametersController.getFormState(),
                    executionInstance: app.controllers.parametersController.executionInstance
        		});

            }},
            {
		    title: 'Info',    		    
		    iconCls: 'info',
		    text: 'Info'

        }]

    }],

	    
	    
	    previewItem: null,

		initComponent: function ()	{
			this.html = '  ';		
			app.views.ParametersView.superclass.initComponent.apply(this, arguments);
			
		}
		
		, refresh: function(items){
			this.removeAll();
			var fieldset = {
					title: 'Document Parameters',
					xtype: 'fieldset',
					scope: this,
					items: items
			};

			this.add(fieldset);
		}
	});