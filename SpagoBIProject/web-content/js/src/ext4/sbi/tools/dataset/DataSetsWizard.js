Ext.define('Sbi.tools.dataset.DataSetsWizard', {
	extend: 'Sbi.widgets.wizard.WizardWindow'

	,config: {
		fieldsStep1: null,
		fieldsStep2: null,
		fieldsStep3: null,
		categoriesStore: null,
		record: {},
		isNew:true
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
		this.fieldsStep1 = [{label:"Id", name:"id",type:"text",hidden:"true", value:this.record.id},
		                    {label: LN('sbi.ds.dsTypeCd'), name:"type",type:"text",hidden:"true", value:this.record.dsTypeCd || 'SelfService'},
		                    {label: LN('sbi.ds.label')+' (*)', name:"label", type:"text", mandatory:true, readOnly:(this.isNew)?false:true, value:this.record.label}, 
		                    {label: LN('sbi.ds.name')+' (*)', name:"name", type:"text", mandatory:true, value:this.record.name},
		                    {label: LN('sbi.ds.description'), name:"description", type:"textarea", value:this.record.description}];
		this.fieldsStep1.push({label:LN('sbi.ds.catType'), name:"catTypeVn", type:"combo", valueCol:"VALUE_ID", descCol:"VALUE_DS", value:this.record.catTypeVn, data:this.categoriesStore});		                    	
		
		this.fieldsStep2 = [{name:"msg", type:"textarea", value:"Work in progress..."}];	
		this.fieldsStep3 = [{name:"msg", type:"textarea", value:"Work in progress..."}];		

	}
	, initSteps: function(){
		
		var steps = [];

		steps.push({itemId:'0', title:LN('sbi.ds.wizard.general'), items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep1)});
		steps.push({itemId:'1', title:LN('sbi.ds.wizard.detail'), items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep2)});
		steps.push({itemId:'2', title:LN('sbi.ds.wizard.metadata'), items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep3)});
		
		return steps;
	}
	
//	, initWizardBar: function() {
//		return this.callParent();
//	}
	
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
		if (Sbi.tools.dataset.DataSetsWizard.superclass.validateForm()){
			var values = Sbi.tools.dataset.DataSetsWizard.superclass.getFormState();			
			this.fireEvent('save', values);
		}else{
			alert(LN('sbi.ds.mandatoryFields'));
		}
	}
	
});
