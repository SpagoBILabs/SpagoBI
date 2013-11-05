Ext.define('Sbi.tools.dataset.DataSetsWizard', {
	extend: 'Sbi.widgets.wizard.WizardWindow'

	,config: {	
		fieldsStep1: null,
		fieldsStep2: null,
		fieldsStep3: null,
		fieldsStep4: null,
		categoriesStore: null,
		height: 440, //440,
		datasetGenericPropertiesStore: null,
		datasetPropertiesStore: null,
		datasetValuesStore: null,
		scopeStore: null,
		record: {},
		isNew:true, 
		user:'',
		fileUpload:null,
		metaInfo:null,
		isOwner: false,
		isTabbedPanel:false //if false rendering as 'card layout (without tabs)
	}

	, constructor: function(config) {
		thisPanel = this;
		thisPanel.fileUploaded = false; //default value
		this.initConfig(config);
		if (this.record.owner !== undefined && this.record.owner !== this.user) {
			this.isOwner = false;
		}else{
			this.isOwner = true;
		}
		
		this.configureSteps();
		
		config.title =  LN('sbi.ds.wizard'); 	
		config.bodyPadding = 10;   
		config.tabs = this.initSteps();
		config.buttons = this.initWizardBar();

		this.callParent(arguments);
		
		this.addListener('cancel', this.closeWin, this);
		this.addListener('navigate', this.navigate, this);
		this.addListener('confirm', this.save, this);		
		
		this.addEvents('save','delete','getMetaValues','getDataStore');	
		
	}
	
	, configureSteps : function(){
   
		this.fieldsStep1 =  this.getFieldsTab1(); 
			
		this.fieldsStep2 =  this.getFieldsTab2(); 
		
		this.fieldsStep3 =  this.getFieldsTab3();
		
		this.fieldsStep4 = this.getFieldsTab4();

	}
	, initSteps: function(){
		
		var steps = [];
		var item1Label = LN('sbi.ds.wizard.detail');
		var item2Label = item1Label + ' -> ' + LN('sbi.ds.wizard.metadata');
		var item3Label = item2Label + ' -> ' + LN('sbi.ds.wizard.validation');
		var item4Label = item3Label + ' -> ' + LN('sbi.ds.wizard.general');
		steps.push({itemId:'0', title:item1Label, items: this.fieldsStep1});
		steps.push({itemId:'1', title:item2Label, items: this.fieldsStep2});
		steps.push({itemId:'2', title:item3Label, items: this.fieldsStep3});
		steps.push({itemId:'3', title:item4Label, items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep4)});
		
		return steps;
	}
	
	, getFieldsTab4: function(){
		//General tab
		var toReturn = [];
		
		toReturn = [{label:"Id", name:"id",type:"text",hidden:"true", value:this.record.id},
         {label: LN('sbi.ds.dsTypeCd'), name:"type",type:"text",hidden:"true", value:this.record.dsTypeCd || 'File'},
         {label: LN('sbi.ds.label'), name:"label", type:"text",hidden:"true", /*mandatory:true, readOnly:(this.isNew || this.isOwner)?false:true,*/ value:this.record.label}, 
         {label: LN('sbi.ds.name'), name:"name", type:"text", mandatory:true, readOnly:(!this.isOwner), value:this.record.name},
         {label: LN('sbi.ds.description'), name:"description", type:"textarea", readOnly:(!this.isOwner), value:this.record.description}];
		//the category is moved at the first tab because it's necessary for the validation action 
//		toReturn.push({label:LN('sbi.ds.catType'), name:"catTypeVn", type:"combo", valueCol:"VALUE_ID", descCol:"VALUE_DS", readOnly:!this.isOwner, value:this.record.catTypeId, data:this.categoriesStore});
		var valueScope = (this.record.isPublic==true)?'true':'false' ;
		toReturn.push({label:LN('sbi.ds.scope'), name:"isPublicDS", type:"combo", valueCol:"field", descCol:"value", readOnly:!this.isOwner, value:valueScope, data:this.scopeStore});
		
		return toReturn;
	}
	
	, getFieldsTab1: function(){
		
		this.cmbCategory = new Ext.form.ComboBox({
			fieldLabel: LN('sbi.ds.catType'),
			store :this.categoriesStore,
			name : 'catTypeVn',			
			displayField : 'VALUE_DS',
			valueField :  'VALUE_ID',
			width : 300,
//			emptyText:'Select ...',
			typeAhead : true, forceSelection : true,
			mode : 'local',
			triggerAction : 'all',
			selectOnFocus : true, 
			editable : false,
			readOnly:!this.isOwner,
			value: this.record.catTypeId,
			style:'padding:5px',
			listeners: {
			    afterrender: function(combo) {
			    	if (!this.rawValue || this.rawValue == ''){
				        var recordSelected = combo.getStore().getAt(0);                     
				        combo.setValue(recordSelected.get('VALUE_ID'));
			    	}
			    }
			}
		});
		
		//upload details tab
		this.fileUpload = new Sbi.tools.dataset.FileDatasetPanel({fromExt4:true, isOwner: this.isOwner});
		if (this.record !== undefined){
			this.fileUpload.setFormState(this.record);
		}
		var uploadButton = this.fileUpload.getComponent('fileUploadPanel').getComponent('fileUploadButton');			
		uploadButton.setHandler(this.uploadFileButtonHandler,this);
		
		var toReturn = new  Ext.FormPanel({
			  id: 'datasetForm',
			  fileUpload: true, // this is a multipart form!!
			  isUpload: true,
			  method: 'POST',
			  enctype: 'multipart/form-data',
//  		      margins: '50 50 50 50',
	          labelAlign: 'left',
	          bodyStyle:'padding:20px',
//	          autoScroll:true,
	          width: '100%', //650,
	          height: 330,
	          border: false,
	          trackResetOnLoad: true,
	          items: [this.cmbCategory, this.fileUpload]
	      }); 
		
		
		return toReturn;
	}
	
	, getFieldsTab2: function(){
		//metadata tab
		var config = {};
		config.meta = this.record.meta;
		config.datasetGenericPropertiesStore = this.datasetGenericPropertiesStore;
		config.datasetPropertiesStore = this.datasetPropertiesStore;
		config.datasetValuesStore = this.datasetValuesStore;
		config.isOwner = this.isOwner;
		this.metaInfo = new Sbi.tools.dataset.ManageDatasetFieldMetadata(config);
		return this.metaInfo;
	}
	
	, getFieldsTab3: function() {
		//dataset validation tab
		var config = {};
		//create empty panel
		this.validateDatasetInfo = new Sbi.tools.dataset.ValidateDataset(config);
		return this.validateDatasetInfo;
	}

	
	, initWizardBar: function() {
		var bar = this.callParent();
		for (var i=0; i<bar.length; i++){
			var btn = bar[i];
			if (btn.id === 'confirm'){
				if (!this.isOwner) {					
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
		 var newTabId;
		 if (this.isTabbedPanel){
			 newTabId  = parseInt(this.wizardPanel.getActiveTab().itemId);
		 }else{
			newTabId  = parseInt(this.wizardPanel.layout.getActiveItem().itemId);
		 }
		 
		 var oldTabId = newTabId;
		 var numTabs  = (this.wizardPanel.items.length-1);
		 var isTabValid = true;
		 if (direction == 'next'){
			 newTabId += (newTabId < numTabs)?1:0;	
			 if (newTabId == 0){
				 isTabValid = this.validateTab0();					
			 }
			 if (newTabId == 1){
				 isTabValid = this.validateTab1();
				if (isTabValid){						
					var values = Sbi.tools.dataset.DataSetsWizard.superclass.getFormState();
					//added manually category because it's moved on the first tab (it's necessary for correct validation action)
					values[this.cmbCategory.name] = this.cmbCategory.getValue();	
					var fileValues = this.fileUpload.getFormState();
					Ext.apply(values, fileValues);
					if (this.record.meta !== undefined){
						var metaValues = this.metaInfo.getFormState();
						values.meta = metaValues;
					}
					//If true a new file is uploaded
					values.fileUploaded = this.fileUploaded;
					this.fireEvent('getMetaValues', values);
				}
			 }
			 if (newTabId == 2){				 
				 var values = Sbi.tools.dataset.DataSetsWizard.superclass.getFormState();
				 //added manually category because it's moved on the first tab (it's necessary for correct validation action)
				 values[this.cmbCategory.name] = this.cmbCategory.getValue();	
				 var fileValues = this.fileUpload.getFormState();
				 Ext.apply(values, fileValues);
				 //If true a new file is uploaded
				 values.fileUploaded = this.fileUploaded;
				 var datasetMetadata = this.fieldsStep2.getFormState();
				 values.datasetMetadata = Ext.JSON.encode(datasetMetadata) ;

				 this.fieldsStep3.createDynamicGrid(values);
			 }
		 }else{			
			newTabId -= (newTabId <= numTabs)?1:0;					
		 }
		 if (isTabValid){
			 if (this.isTabbedPanel){
				 this.wizardPanel.setActiveTab(newTabId);
			 }else{
				 this.wizardPanel.layout.setActiveItem(newTabId);
			 }
			 Ext.getCmp('move-prev').setDisabled(newTabId==0);
			 Ext.getCmp('move-next').setDisabled(newTabId==numTabs);
		 	 Ext.getCmp('confirm').setVisible(!(parseInt(newTabId)<parseInt(numTabs)));
//			 	Ext.getCmp('confirm').setDisabled(parseInt(newTabId)<parseInt(numTabs));
		 }			 
	}
	
	, closeWin: function(){				
		this.destroy();
	}

	, save : function(){
		if (this.validateTab0() && this.validateTab1()){
			var values = Sbi.tools.dataset.DataSetsWizard.superclass.getFormState();
			if (values['label'] == undefined || values['label'] == ''){
				//defining a new label for the dataset
				var d = new Date();
				values['label'] = 'ds__' + d.getTime()%10000000; 
			}
			//added manually category because it's moved on the first tab (it's necessary for correct validation action)
			values[this.cmbCategory.name] = this.cmbCategory.getValue();	
			var fileValues = this.fileUpload.getFormState();
			Ext.apply(values, fileValues);
			if (this.metaInfo !== undefined){
				var metaValues = this.metaInfo.getFormState();
				values.meta = metaValues;
			}
			//If true a new file is uploaded
			values.fileUploaded = thisPanel.params.fileUploaded;
			this.fireEvent('save', values);
		}
	}
	
	, validateTab0: function(){		
		if (!Sbi.tools.dataset.DataSetsWizard.superclass.validateForm()){
			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.mandatoryFields'), '');
			return false;
		}
		return true;
	}
	
	, validateTab1: function(){
		var categ = this.cmbCategory;	
		if (categ == undefined || categ.getValue() == ""){
			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.mandatoryFields'), '');
			return false;
		}
		var fileName = this.fileUpload.fileNameField;
		if (fileName == undefined || fileName.value == ""){
			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.mandatoryUploadFile'), '');
			return false;
		}
		var fileType =  this.fileUpload.fileType;
		if (fileType == undefined || fileType.value == null || fileType.value == ""){
			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.mandatoryFields'), '');
			return false;
		}
		if (fileType.value == 'CSV'){
			var csvDelimiterCombo = this.fileUpload.csvDelimiterCombo;
			var csvQuoteCombo = this.fileUpload.csvQuoteCombo;
			if (csvDelimiterCombo == undefined || csvDelimiterCombo.value == null|| csvDelimiterCombo.value == "" ||
				csvQuoteCombo == undefined || csvQuoteCombo.value == null || csvQuoteCombo.value == ""){
				Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.mandatoryFields'), '');
				return false;
			}
		}
		
		return true;		
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
		//get only the file name from the path (for IE)
		fileNameUploaded = fileNameUploaded.replace(/^.*[\\\/]/, '');
		
		Ext.MessageBox.wait(LN('sbi.generic.wait'));
		form.submit({
			clientValidation: false,
			url : baseUrl // a multipart form cannot
							// contain parameters on its
							// main URL; they must POST
							// parameters
			,
			params : params,			
			success : function(form, action) {
				Ext.MessageBox.updateProgress(1);
				Ext.MessageBox.hide();
				Ext.getCmp('fileNameField').setValue(fileNameUploaded); //hidden field
				Ext.getCmp('fileDetailText').setText(LN('sbi.ds.wizard.file') +" "+fileNameUploaded + LN('sbi.ds.wizard.successLoad') );
//				Ext.getCmp('fileUploadField').hide();
//				Ext.getCmp('fileUploadButton').hide();
				
				this.fileUpload.activateFileTypePanel(action.result.fileExtension);
				this.fileUploaded = true;
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
	            		if(error.error=='USED'){//the file is used from more than one dataset
	            			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.'+error.error)+error.used+" datasets",LN("sbi.ds.failedToUpload") );
	            		}else{
	            			Sbi.exception.ExceptionHandler.showErrorMessage(LN('sbi.ds.'+error.error),LN("sbi.ds.failedToUpload"));
	            		}
	            		
	            	}else{
	            		Sbi.exception.ExceptionHandler.showErrorMessage(action.result.msg,'Failure');
	            	}
	               
				}
			},
			scope : this
		});		
		
		Sbi.debug("[DatasetWizard.uploadFileButtonHandler]: OUT");
	}

	, goBack: function(n){
		 var newTabId;
		 if (this.isTabbedPanel){
			 newTabId  = parseInt(this.wizardPanel.getActiveTab().itemId)-n;
		 }else{
			 newTabId  = parseInt(this.wizardPanel.layout.getActiveItem().itemId)-n;
		 }
		 var numTabs  = (this.wizardPanel.items.length-1);	
		 if (this.isTabbedPanel){
			 this.wizardPanel.setActiveTab(newTabId);
		 }else{
			 this.wizardPanel.layout.setActiveItem(newTabId);
		 }
		 Ext.getCmp('move-prev').setDisabled(newTabId==0);
		 Ext.getCmp('move-next').setDisabled(newTabId==numTabs);
//		 Ext.getCmp('confirm').setDisabled(newTabId<numTabs);
	}
//	
//	, goNext: function(n){
//		var newTabId;
//		 if (this.isTabbedPanel){
//			 newTabId  = parseInt(this.wizardPanel.getActiveTab().itemId)+n;
//		 }else{
//			 newTabId  = parseInt(this.wizardPanel.layout.getActiveItem().itemId)+n;
//		 }
//		 var numTabs  = (this.wizardPanel.items.length-1);		 
//		 this.wizardPanel.setActiveTab(newTabId);
//		 Ext.getCmp('move-prev').setDisabled(newTabId==0);
//		 Ext.getCmp('move-next').setDisabled(newTabId==numTabs);
//		 Ext.getCmp('confirm').setDisabled(newTabId<numTabs);
//	}
	
	, disableButton: function(btn){
		 Ext.getCmp(btn).setDisabled(true);		
	}	
	
	, enableButton: function(btn){
		 Ext.getCmp(btn).setDisabled(false);
	}
	
});
