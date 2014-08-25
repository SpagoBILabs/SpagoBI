/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Container of all the UI of the olap engine.<br>
 * It contains:
 * <ul>
 *		<li>View definition tools</li>
 *		<li>Table/Chart</li>
 *		<li>Options</li>
 *	</ul>
 * 
 *     
 *  @author
 *  Monica Franceschini
 */
 
  
Ext.define('Sbi.datamining.UploadPanel', {
	extend: 'Ext.panel.Panel',
	layout: {
		type: 'fit'
    },

	fileForm: null,
	
	constructor : function(config) {
		this.initConfig(config||{});

		this.fileForm = Ext.create('Ext.form.Panel', {
		    title: 'Dataset loading panel',
		    bodyPadding: 5,
		    width: 500,
		    // Fields will be arranged vertically, stretched to full width
		    layout: 'anchor',
		    defaults: {
		        anchor: '100%'
		    },

		    // The fields
		    defaultType: 'fileuploadfield',
		    items: [],

		    // Reset and Submit buttons
		    buttons: [{
		        text: 'Reset',
		        handler: function() {
		            this.up('form').getForm().reset();
		        }
		    }, {
		        text: 'Carica',
		        formBind: true, //only enabled once the form is valid
		        disabled: true,
		        handler: this.uploadFiles
		    }]
		});
		
		this.callParent(arguments);
	},

	initComponent: function() {
		this.callParent();
		this.add(this.fileForm);
		this.getUploadButtons();
	},
	
	uploadFiles: function(){
		alert('upload');
	},
	
	getUploadButtons: function(){
		
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "dataset"
		});
		
		service.callService(this);
		
		var functionSuccess = function(response){
			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
				var res = Ext.decode(response.responseText);
				
				if(res && Array.isArray(res)){
					for (var i=0; i< res.length; i++){
						var dataset = res[i];
						
						var fileField = Ext.create("Ext.form.field.File",{
					        xtype: 'fileuploadfield',
					        name: dataset.name,
					        fieldLabel: 'Dataset ' +dataset.name,
					        labelWidth: 50,
					        msgTarget: 'side',
					        allowBlank: false,
					        anchor: '100%',
					        buttonText: 'Select DS file '+dataset.readType
					    });
						this.fileForm.add(fileField);
					}
				}
			
			}	
		};
		
		
		
		service.callService(this, functionSuccess);
	}
	
	
});