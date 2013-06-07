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
 * 		Marco Cortella  (marco.cortella@eng.it)
 * 		
 */
Ext.ns("Sbi.tools.dataset");

Sbi.tools.dataset.FileDatasetPanel = function(config) {
	
	
	var defaultSettings =  {
	        labelWidth: 75, 
	        frame:false,
	        defaultType: 'textfield'
	        	
		};






	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);

	var panelItems;
	panelItems = this.initUploadForm(panelItems);
	

	c = {
			items: [
			        panelItems        
			       ]
		};





	Sbi.tools.dataset.FileDatasetPanel.superclass.constructor.call(this, c);	 		
	
};

Ext.extend(Sbi.tools.dataset.FileDatasetPanel, Ext.Panel, {
	
	
	initUploadForm : function(items){
		
	
		this.uploadField = new Ext.form.TextField({
			inputType : 'file',
			fieldLabel : LN('sbi.generic.upload'),
			allowBlank : true,
			id: 'fileNameField'
		});
		
		this.uploadButton = new Ext.Button({
	        text: 'Upload File',
	        id: 'fileUploadButton'	
	    });
		
		this.fileUploadFormPanel = new Ext.Panel({
		  margins: '50 50 50 50',
          labelAlign: 'left',
          bodyStyle:'padding:5px',
          layout: 'form',
		  defaultType: 'textfield',
		  fileUpload: true,
		  id: 'fileUploadPanel',
		  items: [this.uploadField, this.uploadButton]

		});
		
		return this.fileUploadFormPanel;

	}	
	
});