Ext.define('Sbi.tools.dataset.DataSetsWizard', {
	extend: 'Sbi.widgets.wizard.WizardWindow'

	,config: {
		fieldsStep1: null,
		fieldsStep2: null,
		fieldsStep3: null,
		categoriesStore: null,
		scopeStore: null,
		record: {},
		isNew:true, 
		user:'',
		fileUpload:null
	}

	, constructor: function(config) {
		this.initConfig(config);		
		this.configureSteps();
		
		config.title =  LN('sbi.ds.wizard'); 	
		config.bodyPadding = 10;   
		config.layout='card';
		config.tabs = this.initSteps();
		config.buttons = this.initWizardBar();

		this.callParent(arguments);
		
		this.addListener('cancel', this.closeWin, this);
		this.addListener('navigate', this.navigate, this);
		this.addListener('confirm', this.save, this);		
		
		this.addEvents('save','delete');	
		
	}
	
	, configureSteps : function(){
   
		this.fieldsStep1 = this.getFieldsTab1();
			
		this.fieldsStep2 =  this.getFieldsTab2(); 
			
		this.fieldsStep3 = [{name:"msg", type:"textarea", value:"Work in progress..."}];		

	}
	, initSteps: function(){
		
		var steps = [];

		steps.push({itemId:'0', title:LN('sbi.ds.wizard.general'), items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep1)});
		steps.push({itemId:'1', title:LN('sbi.ds.wizard.detail'), items: this.fieldsStep2});
		steps.push({itemId:'2', title:LN('sbi.ds.wizard.metadata'), items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep3)});
		
		return steps;
	}
	
	, getFieldsTab1: function(){
		//General tab
		var toReturn = [];
		
		toReturn = [{label:"Id", name:"id",type:"text",hidden:"true", value:this.record.id},
         {label: LN('sbi.ds.dsTypeCd'), name:"type",type:"text",hidden:"true", value:this.record.dsTypeCd || 'File'},
         {label: LN('sbi.ds.label')+' (*)', name:"label", type:"text", mandatory:true, readOnly:(this.isNew)?false:true, value:this.record.label}, 
         {label: LN('sbi.ds.name')+' (*)', name:"name", type:"text", mandatory:true, value:this.record.name},
         {label: LN('sbi.ds.description'), name:"description", type:"textarea", value:this.record.description}];
		
		toReturn.push({label:LN('sbi.ds.catType'), name:"catTypeVn", type:"combo", valueCol:"VALUE_ID", descCol:"VALUE_DS", value:this.record.catTypeVn, data:this.categoriesStore});
		var valueScope = (this.record.isPublic==true)?'true':'false' ;
		toReturn.push({label:LN('sbi.ds.scope'), name:"isPublic", type:"combo", valueCol:"field", descCol:"value", value:valueScope, data:this.scopeStore});
		
		return toReturn;
	}
	
	, getFieldsTab2: function(){
		//upload details tab
		var toReturn = [];
		this.fileUpload = new Sbi.tools.dataset.FileDatasetPanel({fromWizard:true});
		if (this.record !== undefined){
			this.fileUpload.setFormState(this.record);
		}
		var uploadButton = this.fileUpload.getComponent('fileUploadPanel').getComponent('fileUploadButton');		
		uploadButton.setHandler(this.uploadFileButtonHandler);
		toReturn = new  Ext.FormPanel({
			  id: 'datasetForm',
			  fileUpload: true, // this is a multipart form!!
			  isUpload: true,
			  method: 'POST',
			  enctype: 'multipart/form-data',
  		      margins: '10 10 10 10',
	          labelAlign: 'left',
	          bodyStyle:'padding:5px',
	          autoScroll:true,
	          width: 650,
	          height: 600,
//	          labelWidth: 130,
//	          layout: 'form',
	          trackResetOnLoad: true,
	          items: this.fileUpload
	      }); 
		
		
		return toReturn;
	}
	
	, getFieldsTab3: function(){
		//metadata tab
		var toReturn = [];
		
		toReturn = new Sbi.tools.dataset.FileDatasetPanel({});
		
		return toReturn;
	}
	, initWizardBar: function() {
		var bar = this.callParent();
		for (var i=0; i<bar.length; i++){
			var btn = bar[i];
			if (btn.id === 'confirm'){
				if (this.record.owner !== undefined && this.record.owner !== this.user) {
					btn.disabled = true;
				}
			}				
		}
		return bar;
	}
	
	, navigate: function(panel, direction){		
        // This routine could contain business logic required to manage the navigation steps.
        // It would call setActiveItem as needed, manage navigation button state, handle any
         // branching logic that might be required, handle alternate actions like cancellation
         // or finalization, etc.  A complete wizard implementation could get pretty
         // sophisticated depending on the complexity required, and should probably be
         // done as a subclass of CardLayout in a real-world implementation.
		 var layout = panel.getLayout();		 
		 var newTabId  = parseInt(this.wizardPanel.getActiveTab().itemId);
		 var oldTabId = newTabId;
		 var numTabs  = (this.wizardPanel.items.length-1);
		 
		 if (direction == 'next'){
			 newTabId += (newTabId < numTabs)?1:0;				
		 }else{			
			newTabId -= (newTabId <= numTabs)?1:0;					
		 }
		 this.wizardPanel.setActiveTab(newTabId);
		 Ext.getCmp('move-prev').setDisabled(newTabId==0);
		 Ext.getCmp('move-next').setDisabled(newTabId==numTabs);
	}
	
	, closeWin: function(){				
		this.destroy();
	}

	, save : function(){
		var fileName = this.fileUpload.fileNameField;
		if (fileName == undefined || fileName.value == ""){
			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.mandatoryUploadFile'), '');
			return;
		}
		if (Sbi.tools.dataset.DataSetsWizard.superclass.validateForm()){
			var values = Sbi.tools.dataset.DataSetsWizard.superclass.getFormState();
			var fileValues = this.fileUpload.getFormState();
			Ext.apply(values, fileValues);
			this.fireEvent('save', values);
		}else{
			//alert(LN('sbi.ds.mandatoryFields'));
			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.mandatoryFields'), '');
		}
	}
	
	//handler for the upload file button
	,uploadFileButtonHandler: function(btn, e) {
		
		Sbi.debug("[DatasetWizard.uploadFileButtonHandler]: IN");
		
        var form = Ext.getCmp('datasetForm').getForm();
        
        Sbi.debug("[DatasetWizard.uploadFileButtonHandler]: form is equal to [" + form + "]");
		
        var completeUrl =  Sbi.config.serviceRegistry.getServiceUrl({
					    		serviceName : 'UPLOAD_DATASET_FILE_ACTION',
					    		baseParams : {LIGHT_NAVIGATOR_DISABLED: 'TRUE'}
					    	});
		var baseUrl = completeUrl.substr(0, completeUrl
				.indexOf("?"));
		
		Sbi.debug("[DatasetWizard.uploadFileButtonHandler]: base url is equal to [" + baseUrl + "]");
		
		var queryStr = completeUrl.substr(completeUrl.indexOf("?") + 1);
		var params = Ext.urlDecode(queryStr);

		Sbi.debug("[DatasetWizard.uploadFileButtonHandler]: form is valid [" + form.isValid() + "]");		
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
	               Ext.Msg.alert('Failure', action.result.msg);
				}
			},
			scope : this
		});		
		
		Sbi.debug("[DatasetWizard.uploadFileButtonHandler]: OUT");
	}
	
});
