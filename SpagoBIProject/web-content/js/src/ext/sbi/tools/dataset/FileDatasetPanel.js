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
	
	var defaultSettings = {		
			height: 300,
			width: 300,
			layout: 'form'
//			frame: true,
//			header: false,
//			border: false,
//			padding: '5 5 5 5'
			//html: '<p><b> Prova File Dataset Panel </b></p>'
		};
	







	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);

	var panelItems;
	panelItems = this.initUploadForm(panelItems);
	

	c = {
			items: [
			        panelItems,
			        {
			        	xtype:          'button',
			        	border: 		false,
			        	//handler:		this.uploadFileButtonHandler,
			        	columnWidth:	0.1,
			        	scope: 			this,
			        	style:			'padding-left: 5px',
			        	text: 			'upload'
			        }
		        ]
		};


//	var c ;
//	this.layout = 'fit';
//	this.height = 100;
//		
//	this.html = "<p><b> Prova File Dataset Panel </b></p>";


	Sbi.tools.dataset.FileDatasetPanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.tools.dataset.FileDatasetPanel, Ext.Panel, {
	
//	initDetailItems : function(items) {				
//		this.initUploadForm();		
//	}
	
	initUploadForm : function(items){
		
		this.uploadField = new Ext.form.TextField({
			inputType : 'file',
			fieldLabel : LN('sbi.generic.upload'),
			allowBlank : true,
		});
		
		this.fileUploadFields = new Ext.form.FieldSet({
			labelWidth : 100,
			defaults : {
				border : true
			},
			defaultType : 'textfield',
			autoHeight : true,
			autoScroll : true,
			border : true,
			style : {
				"margin-left" : "3px",
				"margin-top" : "0px",
				"margin-right" : Ext.isIE6 ? (Ext.isStrict ? "-3px"
						: "-5px")
						: "3px"
			},
			items : [ 
			    this.uploadField
			]
		});
		
		this.fileUploadFormPanel = new Ext.FormPanel({
		    title: 'Simple Form with FieldSets',
		    labelWidth: 75, // label settings here cascade unless overridden
		    frame:true,
		    bodyStyle:'padding:5px 5px 0',
		    width: 700,
		    layout:'column', // arrange items in columns
		    defaults: {      // defaults applied to items
		        layout: 'form',
		        border: false,
		        bodyStyle: 'padding:4px'
		    }
          	,items: [{
		                fieldLabel: 'First Name',
		                name: 'first',
		                allowBlank:false
		                , width: 50
		            },{
		                fieldLabel: 'Last Name',
		                name: 'last'
		                , width: 50
		            },{
		                fieldLabel: 'Company',
		                name: 'company'
		                , width: 50
		            }, {
		                fieldLabel: 'Email',
		                name: 'email',
		                vtype:'email'
		               , width: 50
		            }, new Ext.form.TimeField({
		                fieldLabel: 'Time',
		                name: 'time',
		                minValue: '8:00am',
		                maxValue: '6:00pm'
		                , width: 50
		            })
		        ]    
		});
		

		
		//items.push(this.fileUploadFormPanel);
		return this.fileUploadFormPanel;

	}	
	
});

