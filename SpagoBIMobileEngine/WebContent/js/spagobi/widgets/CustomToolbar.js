/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  

Ext.define('app.views.CustomToolbar', {
	extend: 'Ext.Toolbar',
	config: {
        docked: 'top',
        padding: '5 5 5 5',
        height: 30,
        items: [{
            iconCls: 'arrow_down',
            iconMask: true,
            ui: 'normal',
            //left: true,
            text: 'Menu',
            action: 'openmenu'
        },{
            xtype: 'spacer'
        },{
            xtype: 'button',
            iconCls: 'arrow_down',
            iconMask: true,
            ui: 'normal',
            align: 'right',
            text: 'Logout',
            action: 'logout'
        }]
    },


	initialize : function() {
		this.callParent(arguments);
		
		console.log('custom toolbar');
		this.hideToolbar();
	}
    ,hideToolbar: function(){
    	this.hide();
    }
    ,showToolbar: function(){
    	this.show();
    }

});