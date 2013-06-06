Ext.define('Sbi.tools.dataset.DataSetsWizard', {
	extend: 'Sbi.widgets.wizard.WizardWindow'

	,config: {
		fieldsStep1: null,
		fieldsStep2: null,
		fieldsStep3: null,
		categoriesStore: null
	}

	, constructor: function(config) {
		this.initConfig(config);		
		this.configureSteps();
		
		config.title = 'New Dataset...';	
		config.bodyPadding = 10;   
		config.layout='card';
		config.tabs = this.initSteps();
		config.buttons = this.initWizardBar();

		this.callParent(arguments);
		
		this.addListener('cancel', this.closeWin, this);
		this.addListener('navigate', this.navigate, this);
		this.addListener('confirm', this.save, this);		
		
	}
	
	, configureSteps : function(){
		this.fieldsStep1 = [{label:"Label", name:"label", type:"text", value:""}, 
		                    {label:"Name", name:"name", type:"text", value:""},
		                    {label:"Description", name:"description", type:"textarea", value:""}];
		this.fieldsStep1.push({label:"Category", name:"category", type:"combo", value:"VALUE_ID", description:"VALUE_DS", data:this.categoriesStore});		                    	
		
		this.fieldsStep2 = [{name:"msg", type:"textarea", value:"Work in progress..."}];	
		this.fieldsStep3 = [{name:"msg", type:"textarea", value:"Work in progress..."}];		

	}
	, initSteps: function(){
		
		var steps = [];

		steps.push({itemId:'0', title:'General', items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep1)});
		steps.push({itemId:'1', title:'Detail', items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep2)});
		steps.push({itemId:'2', title:'Metadata', items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep3)});
		
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
//		this.callParent.getFormState();
		var values = Sbi.tools.dataset.DataSetsWizard.superclass.getFormState();
	}
	
	
});
