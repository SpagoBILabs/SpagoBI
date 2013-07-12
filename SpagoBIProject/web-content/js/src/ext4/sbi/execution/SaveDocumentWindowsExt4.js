/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
Ext.define('Sbi.execution.SaveDocumentWindowExt4', {
	extend: 'Ext.Window'
		


	
	, inputForm: null
	,saveDocumentForm: null
	,SBI_EXECUTION_ID: null
	,OBJECT_ID: null
	,OBJECT_TYPE: null
	,OBJECT_ENGINE: null
	,OBJECT_TEMPLATE: null
	,OBJECT_DATA_SOURCE: null
	,OBJECT_PARS: null
	,OBJECT_PREVIEW_FILE: null

	
	,constructor: function(config) {

		this.services = new Array();
		
		var saveDocParams= {
			LIGHT_NAVIGATOR_DISABLED: 'TRUE'
		};
		
		// case coming from createWorksheetObject.jsp
		if(config.MESSAGE_DET != undefined && config.MESSAGE_DET != null ){
			saveDocParams.MESSAGE_DET = config.MESSAGE_DET;
		
			if(config.dataset_label != undefined && config.dataset_label != null ){
				saveDocParams.dataset_label = config.dataset_label;
			}
			
			if(config.business_metadata != undefined && config.business_metadata != null ){
				saveDocParams.business_metadata = Ext.JSON.encode(config.business_metadata);
			}
			
			if(config.model_name != undefined && config.model_name != null ){
				saveDocParams.model_name = config.model_name;
			}
			
			
		} else{
			saveDocParams.MESSAGE_DET = 'DOC_SAVE';		
		}
		
	
		this.services['saveDocumentService'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'SAVE_DOCUMENT_ACTION'
			, baseParams: saveDocParams
		});
		
		this.SBI_EXECUTION_ID = config.SBI_EXECUTION_ID;
		this.OBJECT_ID = config.OBJECT_ID;
		this.OBJECT_TYPE = config.OBJECT_TYPE;
		this.OBJECT_ENGINE = config.OBJECT_ENGINE;
		this.OBJECT_TEMPLATE = config.OBJECT_TEMPLATE;
		this.OBJECT_DATA_SOURCE = config.OBJECT_DATA_SOURCE;
		this.OBJECT_WK_DEFINITION = config.OBJECT_WK_DEFINITION;
		this.OBJECT_QUERY = config.OBJECT_QUERY;
		this.OBJECT_FORM_VALUES = config.OBJECT_FORM_VALUES;
		this.OBJECT_PREVIEW_FILE = config.OBJECT_PREVIEW_FILE;
		
		this.initFormPanel();
		
		var c = Ext.apply({}, config, {
			id:'popup_docSave',
			layout:'fit',
			width:640,
			height:350,
			closeAction: 'destroy',
			buttons:[{ 
				  iconCls: 'icon-save' 	
				, handler: this.saveDocument
				, scope: this
				, text: LN('sbi.generic.update')
	           }],
			title: LN('sbi.execution.saveDocument'),
			items: this.saveDocumentForm
		});   
		
		Ext.apply(this,c);
		
	    this.callParent(arguments);
	    
	}

	
	,initFormPanel: function (){
		
		this.docName = Ext.create("Ext.form.TextField", {
			id: 'docName',
			name: 'docName',
			allowBlank: false, 
			inputType: 'text',
			maxLength: 200,
			enforceMaxLength: true,
			anchor: '95%',
			fieldLabel: LN('sbi.generic.name') 
		});
		
		this.docLabel =  Ext.create("Ext.form.TextField",{
	        id:'docLabel',
	        name: 'docLabel',
	        allowBlank: false, 
	        inputType: 'text',
	        maxLength: 20,
	        enforceMaxLength: true,
	        anchor: '95%',
			fieldLabel: LN('sbi.generic.label')  
	    });
		
		this.docDescr =  Ext.create("Ext.form.TextArea",{
	        id:'docDescr',
	        name: 'docDescr',
	        inputType: 'text',
	        allowBlank: true, 
	        maxLength: 400,
	        anchor:	 '95%',
	        height: 80,
			fieldLabel: LN('sbi.generic.descr')  
	    });
		
		this.docVisibility = Ext.create("Ext.form.field.Checkbox",{
			fieldLabel:'Document Visibility',
			id:'docVisibility',
	        boxLabel  : 'Visible',
            name      : 'docVisibility',
            inputValue: 1,
            checked   : true
           });
		
//		this.fileUpload = this.initFileUpload();
	    
	    this.inputForm =  Ext.create("Ext.Panel",{
	         itemId: 'detail'
	        , columnWidth: 0.6
	        , border: false
	        , items: {
	 		   	 columnWidth: 0.4,
	             xtype: 'fieldset',
	             labelWidth: 80,
	             defaults: {border:false},    
	             defaultType: 'textfield',
	             autoScroll  : true,
	             border: false,
	             style: {
	                 "margin-left": "4px",
	                 "margin-top": "25px"
	             },
	             items: [this.docLabel,this.docName,this.docDescr,this.docVisibility,this.fileUpload]
	    	}
	    });
	    
	    
	    this.treePanel =  Ext.create("Sbi.browser.DocumentsTree",{
	    	  columnWidth: 0.4,
	          border: false,

	          drawUncheckedChecks: true
	    });
	    
	    this.saveDocumentForm =  Ext.create("Ext.form.FormPanel",{
		          autoScroll: true,
		          labelAlign: 'left',
		          autoWidth: true,
		          height: 350,
		          layout: 'column',
		          columnWidth:	0.1,
		          scope:this,
		          forceLayout: true,
		          trackResetOnLoad: true,
		          layoutConfig : {
		 				animate : true,
		 				activeOnTop : false

		 			},
		          items: [
		              this.inputForm
		              , this.treePanel           	  		
		          ]
		          
		      });
	}
	
	,saveDocument: function () {
		 
		var docName = this.docName.getValue();
		var docLabel = this.docLabel.getValue();
		var docDescr = this.docDescr.getValue();
		var docVisibility = this.docVisibility.getValue();
		var functs = this.treePanel.returnCheckedIdNodesArray();
		var query = this.OBJECT_QUERY;
		var formValues = this.OBJECT_FORM_VALUES;// the values of the form for the smart filter
		var wk_definition = this.OBJECT_WK_DEFINITION;
		var fileUpload = this.OBJECT_PREVIEW_FILE
		
		if(formValues!=undefined && formValues!=null){
			formValues=Ext.encode(formValues);
		}
		if(query!=undefined && query!=null){
			query = Ext.JSON.encode(query);
		}
		if(wk_definition!=undefined && wk_definition!=null){
			wk_definition = Ext.JSON.encode(wk_definition);
		}
		
		if(fileUpload!=undefined && fileUpload!=null){
			fileUpload = Ext.JSON.encode(fileUpload);
		}
		
		if(docName == null || docName == undefined || docName == '' ||
		   docLabel == null || docLabel == undefined || docLabel == '' ||
		   functs == null || functs == undefined  
		   //|| fileUpload == null || fileUpload == '' 
		   || functs.length == 0){
				Ext.MessageBox.show({
	                title: LN('sbi.generic.warning'),
	                msg:  LN('sbi.document.saveWarning'),
	                width: 180,
	                buttons: Ext.MessageBox.OK
	           });
		}else{	
			functs = Ext.JSON.encode(functs);
			var params = {
		        	name :  docName,
		        	label : docLabel,
		        	description : docDescr,
		        	visibility: docVisibility,
		        	obj_id: this.OBJECT_ID,
					typeid: this.OBJECT_TYPE,
					wk_definition: wk_definition,
					fileUpload: fileUpload,
					query: query,
					formValues: formValues,
					template: this.OBJECT_TEMPLATE,
					datasourceid: this.OBJECT_DATA_SOURCE,
					SBI_EXECUTION_ID: this.SBI_EXECUTION_ID,
					functs: functs
		        };
			
			Ext.Ajax.request({
		        url: this.services['saveDocumentService'],
		        params: params,
		        success : function(response , options) {
			      		if(response !== undefined && response.responseText !== undefined) {
			      			var content = Ext.JSON.decode( response.responseText );
			      			if(content.responseText !== 'Operation succeded') {
			                    Ext.MessageBox.show({
			                        title: LN('sbi.generic.error'),
			                        msg: content,
			                        width: 150,
			                        buttons: Ext.MessageBox.OK
			                   });              
				      		}else{			      			
				      			Ext.MessageBox.show({
				                        title: LN('sbi.generic.result'),
				                        msg: LN('sbi.generic.resultMsg'),
				                        width: 200,
				                        buttons: Ext.MessageBox.OK
				                });
				      			 this.destroy();
				      		}  
			      		} else {
			      			Sbi.exception.ExceptionHandler.showErrorMessage('Server response is empty', 'Service Error');
			      		}  	  		
		        },
		        scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure      
			});
		}
	}
	, initFileUpload: function(){
		//upload preview file
		var fileUpload = new Sbi.widgets.FileUploadPanel({fromExt4:true, isEnabled: true});
//		if (this.record !== undefined){
//			this.fileUpload.setFormState(this.record);
//		}
		var uploadButton = fileUpload.getComponent('fileUploadPanel').getComponent('fileUploadButton');		
		uploadButton.setHandler(this.uploadFileButtonHandler,this);
		var toReturn = new  Ext.FormPanel({
			  id: 'previewFileForm',
			  fileUpload: true, // this is a multipart form!!
			  isUpload: true,
			  border:false,
			  method: 'POST',
			  enctype: 'multipart/form-data',
//  		      margins: '10 10 10 10',
	          labelAlign: 'left',
	          bodyStyle:'padding:5px',
	          autoScroll:true,
//	          width: 650,
//	          height: 600,
	          trackResetOnLoad: true,
	          items: fileUpload
	      });
		
		return toReturn;
	}
	
	//handler for the upload file button
	,uploadFileButtonHandler: function(btn, e) {
		
		Sbi.debug("[PreviewFileWizard.uploadFileButtonHandler]: IN");
		
        var form = Ext.getCmp('previewFileForm').getForm();
        
        Sbi.debug("[PreviewFileWizard.uploadFileButtonHandler]: form is equal to [" + form + "]");
		
        var completeUrl =  Sbi.config.serviceRegistry.getServiceUrl({
					    		serviceName : 'UPLOAD_PREVIEW_FILE_ACTION',
					    		baseParams : {LIGHT_NAVIGATOR_DISABLED: 'TRUE'}
					    	});
		var baseUrl = completeUrl.substr(0, completeUrl
				.indexOf("?"));
		
		Sbi.debug("[PreviewFileWizard.uploadFileButtonHandler]: base url is equal to [" + baseUrl + "]");
		
		var queryStr = completeUrl.substr(completeUrl.indexOf("?") + 1);
		var params = Ext.urlDecode(queryStr);

		Sbi.debug("[PreviewFileWizard.uploadFileButtonHandler]: form is valid [" + form.isValid() + "]");		
		var fileNameUploaded = Ext.getCmp('fileUploadField').getValue();
		fileNameUploaded = fileNameUploaded.replace("C:\\fakepath\\", "");
		
		form.submit({
			clientValidation: false,
			url : baseUrl // a multipart form cannot
							// contain parameters on its
							// main URL; they must POST
							// parameters
			,
			params : params,
			waitMsg : LN('sbi.generic.wait'),
			success : function(form, action) {
				Ext.MessageBox.alert('Success!','File Uploaded to the Server');
				Ext.getCmp('fileNameField').setValue(fileNameUploaded);
			},
			failure : function(form, action) {
				switch (action.failureType) {
	            case Ext.form.Action.CLIENT_INVALID:
	                Ext.Msg.alert('Failure', 'Form fields may not be submitted with invalid values');
	                break;
	            case Ext.form.Action.CONNECT_FAILURE:
	                Ext.Msg.alert('Failure', 'Ajax communication failed');
	                break;
	            case Ext.form.Action.SERVER_INVALID:
	            	if(action.result.msg && action.result.msg.indexOf("NonBlockingError:")>=0){
	            		var error = Ext.JSON.decode(action.result.msg);
//	            		if(error.error=='USED'){//the file is used from more than one dataset
//	            			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.'+error.error)+error.used+" datasets",LN("sbi.ds.failedToUpload") );
//	            		}else{
	            			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.'+error.error),LN("sbi.ds.failedToUpload"));
//	            		}
	            		
	            	}else{
	            		Sbi.exception.ExceptionHandler.showErrorMessage(action.result.msg,'Failure');
	            	}
	               
				}
			},
			scope : this
		});		
		
		Sbi.debug("[PreviewFileWizard.uploadFileButtonHandler]: OUT");
	}

});