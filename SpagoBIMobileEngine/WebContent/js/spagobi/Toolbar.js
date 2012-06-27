/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/

app.views.Toolbar = Ext.extend(Ext.Toolbar, {
	
	xtype : 'toolbar',
	dock : 'bottom',
	defaults : {
		ui : 'plain',
		iconMask : true
	},
	scroll : 'horizontal',
	layout : {
		pack : 'center'
	},

	initComponent : function() {

		this.logoutButton = new Ext.Button( {
			iconCls : 'logout',
			text : 'Logout',
			handler : this.logoutHandler
		});

		this.items = [ this.logoutButton ];

		app.views.BottomToolbar.superclass.initComponent.apply(this, arguments);

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
                    	 window.location.href = Sbi.env.contextPath;
                     }
                     , failure : Sbi.exception.ExceptionHandler.handleFailure
                     , scope : this
                });
	        }
		});
	}

});