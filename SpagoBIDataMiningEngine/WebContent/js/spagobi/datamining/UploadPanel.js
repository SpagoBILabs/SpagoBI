/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
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

		var service = Ext.create("Sbi.service.RestService",{
			url: "dataset"
			,method: "POST"
			,subPath: "loadDataset"
			,pathParams: [fName]
		});
        
             form.submit({
                 url: service.getRestUrlWithParameters(), // a multipart form cannot contain parameters on its main URL;
                 												   // they must POST parameters
                 waitMsg: LN('sbi.dm.execution.load.dataset.wait'),
                 success: function(form, action) {
         			Ext.Msg.show({
      				   title : LN('sbi.dm.execution.msg'),
      				   msg: LN('sbi.dm.execution.load.dataset.ok'),
      				   buttons: Ext.Msg.OK
      				});
        			
                 },
                 failure : function (form, action) {
         			Ext.Msg.show({
         				title : LN('sbi.dm.execution.msg'),
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
						
						//file datasets
						if(dataset.type == Sbi.settings.datamining.execution.fileDataset){
							var fieldLbl = dataset.name;
							if(dataset.fileName !== undefined && dataset.fileName != null){
								fieldLbl = dataset.name +' ('+dataset.fileName+')';
							}
							var fileField= Ext.create("Ext.form.field.File",{
						        xtype: 'fileuploadfield',
						        value: 'default',
						        name: dataset.name,
						        fieldLabel: fieldLbl,
						        labelWidth: 150,
						        msgTarget: 'side',
						        allowBlank: false,
						        anchor: '100%',
						        buttonText: LN('sbi.dm.execution.upload.btn')
						    });

							
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
							    items: [fileField],

							    // Reset and Submit buttons
							    buttons: [{
							        text: LN('sbi.dm.execution.reset.btn'),
							        handler: function() {
							            this.up('form').getForm().reset();
							        }
							    }, {
							        text: LN('sbi.dm.execution.load.btn'),
							        formBind: true, //only enabled once the form is valid
							        disabled: true,	
							        scale: 'small',
							        iconCls:'upload',
							        handler:  Ext.Function.pass(this.uploadFiles, [this, dataset.name, i]),
							        scope: thisPanel
							    }]
							});
							
							thisPanel.add(fileFormN);
						}else if(dataset.type == Sbi.settings.datamining.execution.spagoBIDsDataset){
							
							var datasetField =Ext.create("Ext.form.field.Display", {
						        xtype: 'displayfield',
						        fieldLabel: 'SpagoBI Dataset label',
						        name: dataset.spagobiLabel,
						        value: dataset.spagobiLabel
						    });
							thisPanel.add(datasetField);
						}
						

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