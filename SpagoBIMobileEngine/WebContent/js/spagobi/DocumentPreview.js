app.views.DocumentPreview = Ext.extend(Ext.Panel,

		{
	    dockedItems: [],
	    flex:2,
		initComponent: function ()	{
			this.title = 'Document preview';

			console.log('init document preview');
			
			app.views.DocumentPreview.superclass.initComponent.apply(this, arguments);
		}

	});