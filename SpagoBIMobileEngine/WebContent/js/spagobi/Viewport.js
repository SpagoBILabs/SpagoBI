app.views.Viewport = Ext.extend(Ext.Panel,
		{
	fullscreen: true,
	layout: 'card',
	cardSwitchAnimation: 'slide',
	initComponent: function() 
	
	  {
		app.views.loginForm = new app.views.LoginForm();
		
	    Ext.apply(this, 
	    {
	      items: 
	      [
	         app.views.loginForm
	        
	      ]
	    });

	    app.views.Viewport.superclass.initComponent.apply(this, arguments);

	  }

	});
		