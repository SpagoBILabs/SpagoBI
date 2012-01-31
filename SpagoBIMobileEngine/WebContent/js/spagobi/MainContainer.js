app.views.MainContainer = Ext.extend(Ext.Panel,

		{
		browser: null,
		execution: null,
	    dockedItems: [],

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

	});