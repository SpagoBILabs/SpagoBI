app.controllers.MobileController = Ext.extend(Ext.Controller,
	{

	login: function(options) 
	  {
		console.log('MobileController: Received event of login successfull');
		var viewport = app.views.viewport;
		viewport.setActiveItem(app.views.main, { type: 'slide', direction: 'left' });
		app.views.main.showDocumentBrowser();
	  }

});
