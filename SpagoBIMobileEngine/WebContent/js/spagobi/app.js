/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
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
    	
    	Ext.util.Observable.observeClass(Ext.data.Connection);
    	// connection handler, if server sends callback of expired session, logout!
    	Ext.data.Connection.on('requestexception', function (conn, response, options) {
    		//console.log('----------'+response);
    		var r = response;
    		var content = null;
    		try{
    			content = Ext.util.JSON.decode( response.responseText );
    		}catch(err){
    			console.log('logging out');
    			return;
    		}
    		
    		//console.log('**********'+response.responseText);
			if (content.errors !== undefined  && content.errors.length > 0) {
				if (content.errors[0].message === 'session-expired') {
					window.location.href = Sbi.env.contextPath;

				}
    	    }
    	});
    }
});