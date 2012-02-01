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
			
			this.browser = new app.views.DocumentBrowser();
			this.preview = new app.views.DocumentPreview();
			
			this.items =[this.browser , this.preview ];
			app.views.MainContainer.superclass.initComponent.apply(this, arguments);
		}
		, showDocumentBrowser: function(){
			this.browser.populateList();
		}

	});