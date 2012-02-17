app.views.Viewport = Ext.extend(Ext.Panel,
	{
	fullscreen: true,
	layout: 'card',
	cardSwitchAnimation: 'slide',
	initComponent: function() 
	
	  {
	    //put instances of login into app.views namespace
	    Ext.apply(app.views, {
	        loginView: new app.views.LoginView(),
	        main:      new app.views.MainContainer(),
	        parameters:new app.views.ParametersView(),
	        execution: new app.views.ExecutionView ()

	    });
	    //put instances of loginView into viewport
	    Ext.apply(this, {
	        items: [
	            app.views.loginView,
	            app.views.main,
	            app.views.parameters,
	            app.views.execution
	        ]
	    });
	    
	    app.views.Viewport.superclass.initComponent.apply(this, arguments);
	    
	  }

	});
		