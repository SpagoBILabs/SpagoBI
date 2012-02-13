app.views.ComposedExecutionPanel = Ext.extend(Ext.Panel,

		{
	    scroll: 'vertical',
	    fullscreen: true
		, initComponent: function (options)	{

			console.log('init composed execution');
		    
			app.views.ComposedExecutionPanel.superclass.initComponent.apply(this, arguments);
			
		},
		setComposedWidget: function(resp){
			

		}

		
});