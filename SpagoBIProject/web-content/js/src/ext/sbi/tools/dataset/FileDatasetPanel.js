/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
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

	var myItems = [];
	

	this.initForm(myItems);
	
	c = {
			height : 200,
		    frame:false,
          	items: myItems 
		};


	Sbi.tools.dataset.FileDatasetPanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.tools.dataset.FileDatasetPanel, Ext.Panel, {
	
	initForm: function(myItems){
		this.uploadField = new Ext.form.TextField({
			inputType : 'file',
			width: '300',
			fieldLabel : LN('sbi.generic.upload'),
			allowBlank : true,
		});
		
		this.optField = new Ext.form.TextField({
			width: '300',
			name: "opt1",
			allowBlank : true,
		});
		
		myItems.push(this.uploadField);
		myItems.push(this.optField);
	}

	
});

