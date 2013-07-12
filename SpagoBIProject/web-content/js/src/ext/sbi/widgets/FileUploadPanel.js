/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Ceneselli" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


/**
 * Object name
 * 
 * 
 * 
 * Public Properties
 * 
 * [list]
 * 
 * 
 * Public Methods
 * 
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors
 * 		Antonella Giachino  (antonella.giachino@eng.it)
 * 		
 */
Ext.ns("Sbi.widgets");

Sbi.widgets.FileUploadPanel = function(config) {
	
	var defaultSettings =  {
	        labelWidth: 75, 
	        frame:false,
	        defaultType: 'textfield',
	        fromExt4: false,
	        isEnabled: true,
	        border: false
		};


	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);

	var panelItems;
	panelItems = this.initUploadForm(panelItems,config);
	

	c = {
			items: [
			        panelItems        
			       ]
		};

	Sbi.widgets.FileUploadPanel.superclass.constructor.call(this, c);	 		
	
};

Ext.extend(Sbi.widgets.FileUploadPanel, Ext.Panel, {
	
	initUploadForm : function(items,config){
		this.fileNameField = new Ext.form.DisplayField({
			fieldLabel : LN('sbi.ds.fileName'),
//			width:  300,
			allowBlank : false,
			id: 'fileNameField',
			name: 'fileName',
			readOnly:true,
			hidden: !this.isEnabled || false
		});
		
		this.uploadField = new Ext.form.TextField({
			inputType : 'file',
			fieldLabel : LN('sbi.generic.upload'),
			allowBlank : false,
//			width: (this.fromExt4)? '60%' : '50%',
			id: 'fileUploadField',
			name: 'fileUpload',
			hidden: !this.isEnabled || false
		});
		
		this.uploadButton = new Ext.Button({
//	        text: LN('sbi.ds.file.upload.button'),
//	        id: 'fileUploadButton',
//	        hidden: !this.isEnabled || false
			id: 			'fileUploadButton',			
			xtype:          'button',
        	handler:		this.uploadImgButtonHandler,
//        	columnWidth:	0.1,
        	scope: 			this,
        	tooltip: 		LN('sbi.worksheet.designer.sheettitlepanel.uploadimage'),
//        	style:			'padding-left: 5px',
        	iconCls:		'uploadImgIcon',
        	hidden: 		!this.isEnabled || false
	    });
		
		
		//Main Panel
		this.fileUploadFormPanel = new Ext.Panel({
		  layout:'column',
		  margins: '5 5 5 5',
          labelAlign: 'left',
          bodyStyle:'padding:1px',
		  defaultType: 'textfield',
		  fileUpload: true,
		  id: 'fileUploadPanel',
		  items: [this.fileNameField, this.uploadField,this.uploadButton]

		});
		if (!this.fromExt4) {
			this.fileUploadFormPanel.layout = 'form';
		}
		return this.fileUploadFormPanel;

	}	
	
	//Public Methods
	, setFormState: function(formState) {
		this.fileNameField.setValue(formState.fileName);		
	}
	
	, getFormState: function() {
		var formState = {};
		
		formState.fileName = this.fileNameField.getValue();
		return formState;
	}

	
});