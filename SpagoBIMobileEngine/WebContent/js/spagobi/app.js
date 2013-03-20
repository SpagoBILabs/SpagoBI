/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
 
 Ext.application({
    name: 'app',
    launch: function() {
        this.launched = true;
        app.views =[];
        app.views.viewport = Ext.create('app.views.Viewport');
        Ext.Viewport.add( app.views.viewport);
       
        app.controllers.mobileController = Ext.create('app.controllers.MobileController');
    },
    mainLaunch: function() {

    	
    	
        console.log('mainLaunch');
        app.views.viewport = Ext.create('app.views.LoginView');
    	//app.views.viewport = Ext.create('app.views.DocumentBrowser');
//    	Ext.Viewport.add(app.views.viewport);
//    }
//    	console.log('viewport created');
//    	app.controllers.mobileController = Ext.create('app.controllers.MobileController');
//    	
//    	app.controllers.parametersController = Ext.create('app.controllers.ParametersController');
//
//    	app.controllers.executionController = Ext.create('app.controllers.ExecutionController');
//    	
//    	app.controllers.composedExecutionController = Ext.create('app.controllers.ComposedExecutionController');
//    	console.log('controller created');
//    	
//    	// Retrieve the object from storage
//    	var appViewsLaunched = localStorage.getItem('app.views.launched');
//    	var loadedBrowser = localStorage.getItem('app.views.browser');
//    	
//    	if(appViewsLaunched !== undefined &&
//    			appViewsLaunched != null &&
//    			appViewsLaunched == 'true' &&
//    			loadedBrowser != undefined &&
//    			loadedBrowser != null &&
//    			loadedBrowser == 'true'
//    			){
//    		
//			Ext.dispatch({
//				controller : app.controllers.mobileController,
//				action : 'login',
//				animation : {
//					type : 'slide',
//					direction : 'right'
//				}
//			});
//
//			if(app.views.form != undefined && app.views.form != null){
//				app.views.form.hide();
//			}
//			// refresh page
//
//    	}
//
////    	Ext.util.Observable.observeClass(Ext.data.Connection);
////    	// connection handler, if server sends callback of expired session, logout!
////    	Ext.data.Connection.on('requestexception', function (conn, response, options) {
////    		//console.log('----------'+response);
////    		var r = response;
////    		var content = null;
////    		try{
////    			content = Ext.util.JSON.decode( response.responseText );
////    		}catch(err){
////    			console.log('logging out');
////    			return;
////    		}
////    		
////    		//console.log('**********'+response.responseText);
////			if (content.errors !== undefined  && content.errors.length > 0) {
////				if (content.errors[0].message === 'session-expired') {
////					
////					localStorage.removeItem('app.views.launched');
////					localStorage.removeItem('app.views.browser');
////					window.location.href = Sbi.env.contextPath;
////				}
////    	    }
////    	});
//    }
    }
});