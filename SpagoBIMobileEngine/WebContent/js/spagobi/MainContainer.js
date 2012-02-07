app.views.MainContainer = Ext.extend(Ext.Panel,

		{
		browser: null,
		preview: null,
	    dockedItems: [],
	   

		initComponent: function ()	{
			this.title = 'Main container';
			this.cls = 'card4',
			this.layout = {
		        type: 'hbox',
		        align: 'stretch'
		    };
			console.log('init main container');
			

		    //put instances of login into app.views namespace
		    Ext.apply(app.views, {
		    	browser: 	  new app.views.DocumentBrowser(),
		    	preview:      new app.views.DocumentPreview()

		    });
		    //put instances of loginForm into viewport
		    Ext.apply(this, {
		        items: [
		            app.views.browser,
		            app.views.preview
		        ]
		    });
			
			app.views.MainContainer.superclass.initComponent.apply(this, arguments);
			
			
		}

		

	});