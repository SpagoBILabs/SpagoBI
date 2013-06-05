Ext.define('Sbi.tools.dataset.DataSetsWizard', {
	extend: 'Sbi.widgets.wizard.WizardWindow'

	,config: {
		fieldsStep1: null,
		fieldsStep2: null,
		fieldsStep3: null
	}

	, constructor: function(config) {
		this.initConfig(config);
		this.fieldsStep1 = [{label:"Label", name:"label", type:"text", value:""}, 
		                    {label:"Name", name:"name", type:"text", value:""},
		                    {label:"Description", name:"description", type:"textarea", value:""}];	
		this.fieldsStep2 = [{type:"html", value:"Work in progress..."}];	
		this.fieldsStep3 = [{type:"html", value:"Work in progress..."}];
	
		config.title = 'New Dataset...';	
		config.bodyPadding = 10;   
		config.layout='fit';
		config.tabs = this.initSteps();
		config.buttons = this.initWizardBar();
		this.callParent(arguments);

		this.addListener('cancel', this.closeWin, this);
		this.addListener('navigate', this.navigate, this);
	}
	
	, initSteps: function(){
		var steps = [];

		steps.push({title:'General', items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep1)});
		steps.push({title:'Detail', items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep2)});
		steps.push({title:'Metadata', items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep3)});
		
		return steps;
	}
	
	, initWizardBar: function() {
		return Sbi.tools.dataset.DataSetsWizard.superclass.initWizardBar();
	}
	
	, navigate: function(panel, direction){		
		alert("navigate! " + panel + " - " + direction);
		
        // This routine could contain business logic required to manage the navigation steps.
        // It would call setActiveItem as needed, manage navigation button state, handle any
         // branching logic that might be required, handle alternate actions like cancellation
         // or finalization, etc.  A complete wizard implementation could get pretty
         // sophisticated depending on the complexity required, and should probably be
         // done as a subclass of CardLayout in a real-world implementation.
		 var layout = panel.getLayout();
		 layout[direction]();
		 Ext.getCmp('move-prev').setDisabled(!layout.getPrev());
		 Ext.getCmp('move-next').setDisabled(!layout.getNext());
	}
	
	, closeWin: function(){		
		alert("closeWin! ");
		this.hide();
	}
	
});
