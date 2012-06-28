/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
  
 
/**
  * SaveWindow - short description
  * 
  * Object documentation ...
  * 
  * by Andrea Gioia (andrea.gioia@eng.it)
  */

Sbi.widgets.SaveWindow = function(config) {	
	
	var c = Ext.apply({}, config || {}, {
		hasBuddy: false
	});
		
	this.initFormPanel(c);
	
	
	// constructor
	Sbi.widgets.SaveWindow.superclass.constructor.call(this, {
		layout:'fit',
		width:500,
		height:250,
		closeAction:'hide',
		plain: true,
		title: 'Save as ...',
		items: [this.formPanel]
    });
    
	if(c.hasBuddy === 'true') {
		this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
	}
	
	this.addEvents('save');
    
    
};

Ext.extend(Sbi.widgets.SaveWindow, Ext.Window, {
    
	nameField: null
	, descriptionField: null
	, scopeField: null
	, hasBuddy: null
    , buddy: null
   
   
    // public methods
	,getFormState : function() {      
    	
      	var formState = {};
      	formState.name= this.nameField.getValue();
      	formState.description= this.descriptionField.getValue();
      	formState.scope= this.scopeField.getValue();
      	
      	return formState;
    }

	//private methods
	, initFormPanel: function(config) {
    	this.nameField = new Ext.form.TextField({
    		name:'analysisName',
    		allowBlank:false, 
    		inputType:'text',
    		maxLength:50,
    		width:250,
    		fieldLabel:'Name' 
    	});
    	    
    	this.descriptionField = new Ext.form.TextField({
    		name:'analysisDescription',
    		allowBlank:false, 
    		inputType:'text',
    		maxLength:50,
    		width:250,
    		fieldLabel:'Description' 
    	});
    	    
    	    
    	   
    	var scopeComboBoxData = [
    		['PUBLIC','Public', 'Everybody that can execute this document will see also your saved subobject'],
    		['PRIVATE', 'Private', 'The saved quary will be visible only to you']
    	];
    		
    	var scopeComboBoxStore = new Ext.data.SimpleStore({
    		fields: ['value', 'field', 'description'],
    		data : scopeComboBoxData 
    	});
    		    
    		    
    	this.scopeField = new Ext.form.ComboBox({
    	   	tpl: '<tpl for="."><div ext:qtip="{field}: {description}" class="x-combo-list-item">{field}</div></tpl>',	
    	   	editable  : false,
    	   	fieldLabel : 'Scope',
    	   	forceSelection : true,
    	   	mode : 'local',
    	   	name : 'analysisScope',
    	   	store : scopeComboBoxStore,
    	   	displayField:'field',
    	    valueField:'value',
    	    emptyText:'Select scope...',
    	    typeAhead: true,
    	    triggerAction: 'all',
    	    selectOnFocus:true
    	});
    	
    	this.formPanel = new Ext.form.FormPanel({
    		frame:true,
    	    bodyStyle:'padding:5px 5px 0',
    	    buttonAlign : 'center',
    	    items: [this.nameField,this.descriptionField,this.scopeField],
    	    buttons: [{
    			text: 'Save',
    		    handler: function(){
    	    		this.fireEvent('save', this, this.getFormState());
                	this.hide();
            	}
            	, scope: this
    	    },{
    		    text: 'Cancel',
    		    handler: function(){
                	this.hide();
            	}
            	, scope: this
    		}]
    	 });
    }
});