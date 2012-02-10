app.views.ParametersView = Ext.extend(Ext.form.FormPanel,

		{
	    dockedItems: [],
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
					items: items
			};
			this.add(fieldset);
		}
	});