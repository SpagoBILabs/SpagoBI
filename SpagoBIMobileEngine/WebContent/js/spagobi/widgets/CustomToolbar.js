/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  

Ext.define('app.views.CustomToolbar', {
	extend: 'Ext.Toolbar',
	config: {
        docked: 'top',
        padding: 5,
    	defaults : {
    		ui : 'plain',
    		iconMask : true
    	},
    	scroll : 'horizontal',
    	layout : {
    		pack : 'center'
    	},
        height: 30
    },


	initialize : function() {
		this.callParent(arguments);
		
		console.log('initialize hidden custom toolbar');
		this.setButtons();
		this.hideToolbar();
	}
    ,hideToolbar: function(){
    	this.hide();
    }
    ,showToolbar: function(){
    	this.show();
    }
    ,setButtons: function(){
    	var buttons = Sbi.settings.top.toolbar.buttons;
    	for(i =0; i< buttons.length; i++){
    		var btnKey = buttons[i];
	    	if(btnKey === 'home'){
				this.homeButton = {
						title : 'Home',
						iconCls : 'home',
						text : 'Home',
						ui: 'plain',
						iconMask: true,
						autoEvent: 'home'
						};
				this.add(this.homeButton);	
	    	}else if(btnKey === 'prec'){
	    		this.precButton = new Ext.Button( {
	    			text : 'Previous',
	    			iconCls : 'reply',
	    			ui: 'plain',
					autoEvent: 'back'
	    		});
	    		this.add(this.precButton);
	    	}else if(btnKey === 'refresh'){
	    		this.refreshButton = new Ext.Button( {
	    			text : 'Refresh',
	    			iconCls : 'refresh',
	    			ui: 'plain',
	    			autoEvent: 'refresh'
		    		});
	    		this.add(this.refreshButton);
	    	}else if(btnKey === 'params'){
	    		this.paramsButton = {
						title : 'Parameters',
						iconCls : 'compose',
						text : 'Parameters',
						ui: 'plain',
						autoEvent: 'params'
						
						};
	    		this.add(this.paramsButton);
	    	}else if(btnKey === 'html'){
	    		this.paramsButton = {
						title : 'Html',
						ui: 'plain',
						html: '<div style="color: violet; border: 1px solid red; background-color: #fff;">Questo &egrave; un html di esempio</div>',
						autoEvent: 'html'
						
						};
	    		this.add(this.paramsButton);
	    	}else if(btnKey === 'spacer'){
	    		this.spacer ={
	    			xtype: 'spacer'
	    		};
	    		this.add(this.spacer);
	    	}else if(btnKey === 'logout'){
	    		this.logoutButton = new Ext.Button( {
	    			iconCls : 'logout',
	    			text : 'Logout',
	    			ui: 'round',
	    			autoEvent: 'logout'
	    		});
	    		this.add(this.logoutButton);
	    	}
    	}
    }
    ,
	logoutHandler : function () {
		Ext.Msg.confirm(null, 'Are you sure you want to logout?', function(answer) {
	        if (answer === "yes") {
	        	Ext.Ajax.request({
                     url : Sbi.env.invalidateSessionURL
                     , method : 'POST'
                     , success : function(response, opts) {
                    	 // refresh page
                    	 localStorage.removeItem('app.views.launched');
                    	 localStorage.removeItem('app.views.browser');
                    	 window.location.href = Sbi.env.contextPath;
                     }
                     , failure : Sbi.exception.ExceptionHandler.handleFailure
                     , scope : this
                });
	        }
		});
	}
});