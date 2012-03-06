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
    	app.controllers.mobileController.init();
    	app.controllers.parametersController = new app.controllers.ParametersController();
    	app.controllers.parametersController.init();
    	app.controllers.executionController = new app.controllers.ExecutionController();
    	app.controllers.executionController.init();
    	app.controllers.composedExecutionController = new app.controllers.ComposedExecutionController();
    	console.log('controller created');
    	

    }
});