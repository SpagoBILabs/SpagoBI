/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.editor.widget");

Sbi.cockpit.editor.widget.WidgetEditorGenericConfPanel = function(config) {	
		
	var defaultSettings = {
		name:'WidgetEditorGenericConfPanel',
		emptyMsg: 'Tab vuoto',
		title:'Generic Configuration'
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.editor.widget.WidgetEditorGenericConfPanel', defaultSettings);
	
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);		
	
	this.initEmptyMsgPanel();	
			
	c = {
		height: 400,
		items: [this.emptyMsgPanel]
	};
	
	Sbi.cockpit.editor.widget.WidgetEditorGenericConfPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.cockpit.editor.widget.WidgetEditorGenericConfPanel, Ext.Panel, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	/**
     * @property {Array} services
     * This array contains all the services invoked by this class
     */
	services: null
   
	// =================================================================================================================
	// METHODS
	// =================================================================================================================	
	
	/*	
	 * @method 
	 * 
	 * Initialize the GUI
	 */
	, initEmptyMsgPanel: function() {
		this.emptyMsgPanel = new Ext.Panel({
			html: this.emptyMsg
			, border: false
			, frame: true
		});
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
		
	// -----------------------------------------------------------------------------------------------------------------
    // private methods
	// -----------------------------------------------------------------------------------------------------------------

	// =================================================================================================================
	// EVENTS
	// =================================================================================================================
	
	//this.addEvents(
	/**
     * @event eventone
     * Fired when ...
     * @param {Sbi.xxx.Xxxx} this
     * @param {Ext.Toolbar} ...
     */
	//'eventone'
	/**
     * @event eventtwo
     * Fired before ...
     * @param {Sbi.xxx.Xxxx} this
     * @param {Object} ...
     */
	//'eventtwo'
	//);	
});