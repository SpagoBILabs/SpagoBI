/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.define('Sbi.adhocreporting.MyAnalysisWizard', {
	extend: 'Sbi.widgets.wizard.WizardWindow'

		,config: {	
			fieldsStep1: null,
			fieldsStep2: null,
//			fieldsStep3: null,
//			fieldsStep4: null,
//			categoriesStore: null,
			height: 440, //440,
//			datasetGenericPropertiesStore: null,
//			datasetPropertiesStore: null,
//			datasetValuesStore: null,
//			scopeStore: null,
			record: {},
//			isNew:true, 
			user:'',
//			fileUpload:null,
//			metaInfo:null,
//			isOwner: false,
			isTabbedPanel:false //if false rendering as 'card layout (without tabs)
			
		}

		, constructor: function(config) {
//			thisPanel = this;
//			thisPanel.fileUploaded = false; //default value
			this.initConfig(config);
//			if (this.record.owner !== undefined && this.record.owner !== this.user) {
//				this.isOwner = false;
//			}else{
//				this.isOwner = true;
//			}
		
			this.configureSteps();
		
			config.title =  LN('sbi.myanalysis.wizard.wizardname'); 	
			config.bodyPadding = 10;   
			config.tabs = this.initSteps();
			config.buttons = this.initWizardBar();
		
			this.callParent(arguments);
		
			this.addListener('cancel', this.closeWin, this);
			this.addListener('navigate', this.navigate, this);
			this.addListener('confirm', this.save, this);		
		
//			this.addEvents('save','delete','getMetaValues','getDataStore');	
		
		}
		
		, configureSteps : function(){
			   
			this.fieldsStep1 =  this.getFieldsTab1(); 
				
			this.fieldsStep2 =  this.getFieldsTab2(); 

		}
		
		, getFieldsTab1: function(){
			
			
			//General tab
//			var toReturn = [];
//			
//			toReturn = [
//				// {label:"Id", name:"id",type:"text",hidden:"true", value:this.record.id},
//		        {label: LN('sbi.ds.dsTypeCd'), name:"type",type:"text",hidden:"true", value:this.record.dsTypeCd || 'File'},
//		        {label: LN('sbi.ds.label'), name:"label", type:"text",hidden:"true", /*mandatory:true, readOnly:(this.isNew || this.isOwner)?false:true,*/ value:''}, 
//		        {label: LN('sbi.ds.name'), name:"name", type:"text", mandatory:true, readOnly:false, value:'' /*,value:this.record.name*/},
//		        {label: LN('sbi.ds.description'), name:"description", type:"textarea", readOnly:false, value:'' /*,value:this.record.description*/}
//	         ];
//			
//			return toReturn;
			
			this.worksheetSelectionButton = new Ext.Button({
				  text: ''
		          ,flex:1
		          ,margin: 10
		          ,height: '100px'
		          ,cls:'reportbutton'
		         
			});
			
			this.geoSelectionButton = new Ext.Button({
				  text: ''
		          ,flex:1
		          ,margin: 10
		          ,height: '100px'
		          ,cls:'geobutton'

			});
			
			this.cockpitSelectionButton = new Ext.Button({
				  text: ''
		          ,flex:1
		          ,margin: 10
		          ,height: '100px'
			      ,cls:'cockpitbutton'
			});
			
			this.selectionPanel = new Ext.Panel({
				//width: '100%',
				//layout:'column',
			    layout: 'hbox',
			    //pack: 'start',
			    align: 'stretch',
			    //widthRatio: 0.75,
			    border: 0,
			    padding: 10,
			    style: 'background-color: white;padding: 40px',
				items: [this.worksheetSelectionButton, this.geoSelectionButton, this.cockpitSelectionButton]				
			});
			
			this.parentPanel = new Ext.Panel({
				layout:'fit',
				height: 400,
				style: 'margin:0 auto;margin-top:100px;',
				border: 0,
				items: [this.selectionPanel]
			});
			
			//return this.selectionPanel;
			return this.parentPanel;
			
			
		}
		
		, getFieldsTab2: function(){
			//General tab
			var toReturn = [];
			
			toReturn = [
				        {label: LN('sbi.ds.label'), name:"label", type:"text", mandatory:true, /*readOnly:(this.isNew || this.isOwner)?false:true,*/}, 
				        {label: LN('sbi.ds.name'), name:"name", type:"text", mandatory:true, readOnly:false /*,value:this.record.name*/},
				        {label: LN('sbi.ds.description'), name:"description", type:"textarea", readOnly:false /*,value:this.record.description*/}
			         ];
			
			return toReturn;
		}
		
		, initSteps: function(){
			
			var steps = [];
			var item1Label = LN('sbi.myanalysis.wizard.myanalysisselection');
			var item2Label = item1Label + ' -> ' + LN('sbi.myanalysis.wizard.myanalysisdetail');
			
			steps.push({itemId:'0', title:item1Label, items: this.fieldsStep1});
			steps.push({itemId:'1', title:item2Label, items: Sbi.tools.dataset.DataSetsWizard.superclass.createStepFieldsGUI(this.fieldsStep2)});

			
			return steps;
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
		
		, closeWin: function(){				
			this.destroy();
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

					}
				 }
				 if (newTabId == 2){				 

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
//				 	Ext.getCmp('confirm').setDisabled(parseInt(newTabId)<parseInt(numTabs));
			 }			 
		}
		
		, validateTab0: function(){		
			//TODO: to implement
			return true;
		}
		
		, validateTab1: function(){		
			//TODO: to implement
			return true;
		}
		
		, save : function(){
			if (this.validateTab0() && this.validateTab1()){
				//TODO: to implement
				this.fireEvent('save', values);
			}
		}
});		