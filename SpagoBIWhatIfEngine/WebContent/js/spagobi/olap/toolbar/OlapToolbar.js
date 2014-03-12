/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Toolbar for the execution.. The buttons are sortable and hiddable by the use.. The configuartion is stored in the cookies and restored every time the engine is opened 
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */



Ext.define('Sbi.olap.toolbar.OlapToolbar', {
	extend: 'Ext.toolbar.Toolbar',
	plugins : Ext.create('Ext.ux.BoxReorderer', {}),
	
	config:{
		
	},
	
	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.toolbar && Sbi.settings.olap.toolbar.OlapToolbar) {
			Ext.apply(this, Sbi.settings.olap.toolbar.OlapToolbar);
		}
		this.callParent(arguments);
	},
	
	initComponent: function() {
		
		var buttoB = Ext.create('Ext.Button', {
            text: 'Menu Button',
            iconCls: 'add16',
            menu: [{text: 'Menu Item 1'}],
            reorderable: false
        });
		

		Ext.apply(this, {
			layout: {
                overflowHandler: 'Menu'
            },
			defaults: {
	            reorderable: true
	        },items   : [buttoB
	                     ,
	                     {
	                         xtype:'splitbutton',
	                         text: 'Cut',
	                         iconCls: 'add16',
	                         menu: [{text: 'Cut Menu Item'}]
	                     },
	                     {
	                         text: 'Copy',
	                         iconCls: 'add16'
	                     },
	                     {
	                         text: 'Paste',
	                         iconCls: 'add16',
	                         menu: [{text: 'Paste Menu Item'}]
	                     },
	                     {
	                         text: 'Format',
	                         iconCls: 'add16'
	                     }
	                 ]
		});
		this.callParent();
	},
	
    /**
     * Gets the configuration of the view
     * @return {Object} returns the configuration state: position and visibility of the buttons
     */
	getViewState: function(){
		
	},
	
    /**
     * Sets the state of the view. Updates the toolbar setting the order and the visibility of the buttons.
     * The hidden buttons are grouped in a new menu in the top right 
     * @param {Object} state of the view 
     */
	setViewState: function(state){
		
	}
	
	
});
