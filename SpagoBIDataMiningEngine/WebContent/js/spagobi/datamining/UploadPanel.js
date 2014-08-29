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
		type: 'vbox'
    },
    config:{
		minWidth: 600
		, width: 800
		, border:0
		, style: 'margin-bottom: 10px;'
	},
	executeScriptBtn: null,
	datasetFiles : [],
	itsParent: null,
	constructor : function(config) {
		this.initConfig(config||{});

		this.itsParent = config.itsParent;
		
		this.callParent(arguments);
	},

	initComponent: function() {
		this.callParent();
		
		this.getUploadButtons();
	
	},
	
	uploadFiles: function(formPanelN, fName, posItem){
        var form = formPanelN.items.items[posItem].getForm();
        //var fName = form.owner.items.items[0].name;

		var service = Ext.create("Sbi.service.RestService",{
			url: "dataset"
			,method: "POST"
			,subPath: "loadDataset"
			,pathParams: [fName]
		});
        
             form.submit({
                 url: service.getRestUrlWithParameters(), // a multipart form cannot contain parameters on its main URL;
                 												   // they must POST parameters
                 waitMsg: 'Uploading your file...',
                 success: function(form, action) {
                	
         			Ext.Msg.show({
      				   title : 'Upload',
      				   msg: 'ok',
      				   buttons: Ext.Msg.OK
      				});
        			
                 },
                 failure : function (form, action) {
         			Ext.Msg.show({
       				   title: 'Error',
       				   msg: action.result.msg,
       				   buttons: Ext.Msg.OK
       				});
                 },
                 scope : this
             });

	},
	
	getUploadButtons: function(){
		
		var thisPanel = this;
		
		var service = Ext.create("Sbi.service.RestService",{
			url: "dataset"
		});
		
		service.callService(this);
		
		var functionSuccess = function(response){
			var thisPanel = this;
			if(response != null && response.responseText !== undefined && response.responseText !== null && response.responseText !== ''){
				var res = Ext.decode(response.responseText);
				
				if(res && Array.isArray(res)){
					
					for (var i=0; i< res.length; i++){
						var dataset = res[i];
						
						var fileFormN = Ext.create('Ext.form.Panel', {
						    fileUpload: true,
						    bodyPadding: 5,
						    width: 500,
						    // Fields will be arranged vertically, stretched to full width
						    layout: 'anchor',
						    defaults: {
						        anchor: '100%'
						    },

						    // The fields
						    defaultType: 'fileuploadfield',
						    items: [Ext.create("Ext.form.field.File",{
						        xtype: 'fileuploadfield',
						        name: dataset.name,
						        fieldLabel: dataset.name,
						        labelWidth: 50,
						        msgTarget: 'side',
						        allowBlank: false,
						        anchor: '100%',
						        buttonText: 'Upload'
						    })],

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
						        handler:  Ext.Function.pass(this.uploadFiles, [this, dataset.name, i]),
						        scope: thisPanel
						    }]
						});
						
						thisPanel.add(fileFormN);

					}
					
				}
			
			}else{			
				//hides the execution button
				thisPanel.itsParent.executeScriptBtn.hide();
				
			}
		};
		
		service.callService(this, functionSuccess);
	}
	
	
});