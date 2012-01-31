app.views.Viewport = Ext.extend(Ext.Panel,
	{
	fullscreen: true,
	layout: 'card',
	cardSwitchAnimation: 'slide',
	initComponent: function() 
	
	  {
	    //put instances of login into app.views namespace
	    Ext.apply(app.views, {
	        loginForm: new app.views.LoginForm(),
	        main:      new app.views.MainContainer()

	    });
	    //put instances of loginForm into viewport
	    Ext.apply(this, {
	        items: [
	            app.views.loginForm,
	            app.views.main
	        ]
	    });
	    
	    app.views.Viewport.superclass.initComponent.apply(this, arguments);
	    
	  }

	});
		