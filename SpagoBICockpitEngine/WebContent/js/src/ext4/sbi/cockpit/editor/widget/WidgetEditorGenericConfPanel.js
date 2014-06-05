/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
Ext.ns("Sbi.cockpit.editor.widget");

Sbi.cockpit.editor.widget.WidgetEditorGenericConfPanel = function(config) {	
		
	this.initFields();
	
	var defaultSettings = {
		xtype: 'form',
		name:'WidgetEditorGenericConfPanel',		
		title:'Generic Configuration',
		layout: 'form',
		bodyPadding: '5 5 0',		
		items: this.fields		
	};
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.editor.widget.WidgetEditorGenericConfPanel', defaultSettings);
	
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);			
			
	c = {
		height: 400		
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

	, initFields: function() {
		
		this.fields = [];
		
		 var title = new Ext.form.field.Text({
			fieldLabel: 'Title',
			name: 'title',
            allowBlank: false,
            tooltip: 'Enter the widget title'  
		});
		
		var description = new Ext.form.field.TextArea({
			fieldLabel: 'Description',
			name: 'description',
            allowBlank: true            
		});
		
		this.fields.push(title);
		this.fields.push(description);
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	
	, getFormState: function() {		
		
		var formState = Ext.apply({}, {
			title: this.fields[0].getValue(),
			description: this.fields[1].getValue()			
		});
		
		return formState;
	}
	
	, setFormState: function(widgetConf) {
		Sbi.trace("[WidgetEditorGenericConfPanel.setFormState]: IN");
				
		this.fields[0].setValue(widgetConf.title);
		this.fields[1].setValue(widgetConf.description);
		
		Sbi.trace("[WidgetEditorGenericConfPanel.setFormState]: OUT");			
	}
	
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