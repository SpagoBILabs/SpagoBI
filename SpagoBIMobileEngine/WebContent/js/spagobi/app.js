Ext.regApplication({
    name: 'app',
    launch: function() {
        this.launched = true;
        this.mainLaunch();
    },
    mainLaunch: function() {

        console.log('mainLaunch');
    	app.views.viewport = new app.views.Viewport();
    	console.log('viewport created');
    	app.controllers.mobileController = new app.controllers.MobileController();
    	console.log('controller created');

    }
});