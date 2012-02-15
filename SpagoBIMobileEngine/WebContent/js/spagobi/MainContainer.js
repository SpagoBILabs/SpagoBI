app.views.MainContainer = Ext.extend(Ext.Panel,

		{

	    fullscreen: true,
	    autoRender: true,

		initComponent: function ()	{
	
			this.title = 'Main container';
			this.cls = 'card4',
			this.layout = {
		        type: 'hbox',
		        align: 'stretch'
		    };
			console.log('init main container');
			
			app.views.MainContainer.superclass.initComponent.apply(this, arguments);

			
		}
		, setItems: function(){
			
			this.fullscreen= true;

		    Ext.apply(app.views, {
		    	browser: 	  new app.views.DocumentBrowser(),
		    	preview:      new app.views.DocumentPreview()

		    });
		    this.add(app.views.browser);
		    this.add(app.views.preview);
		    /*PAY ATTENTION TO INVOKE DO LAYOUT METHOD OF MAIN CONTAINER...otherwise no child item is displayed!!!!*/
		    this.doLayout();  

		}
		

	});