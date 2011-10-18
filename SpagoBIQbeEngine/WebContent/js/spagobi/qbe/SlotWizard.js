/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.ZONE
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
 
/**
  * CalculatedFieldWizard - short description
  * 
  * Object documentation ...
  * 
  * by Monica Franceschini
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.SlotWizard = function(config) {	
	
	var c = Ext.apply({}, config || {}, {
		title: LN('sbi.qbe.bands.title')
		, width: 800
		, height: 450
		, resizable: true
		, hasBuddy: false	
		
	});

	Ext.apply(this, c);
	if(c.fieldForSlot !== undefined){
		this.fieldForSlot = c.fieldForSlot;
	}

	
	this.initMainPanel(c);	
	
	if(c.hasBuddy === 'true') {
		this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
	}
	Sbi.qbe.SlotWizard.superclass.constructor.call(this, c);  
	this.add(this.mainPanel);
  
	this.addEvents('apply'); 
	
	

};

Ext.extend(Sbi.qbe.SlotWizard, Ext.Window, {
	
	hasBuddy: null
    , buddy: null
   
    , mainPanel: null
    , firstCalculatedFiledPanel : null
    , secondSlotDefinitionPanel: null
    , buttonsConfig: null
    , startFromFirstPage : true
    , fieldForSlot: null
    , modality: 'add'
    , fieldId : null
    , expression : null

    , getExpression: function() {
    	var expression;
    	var fs = this.firstCalculatedFiledPanel.getFormState();
    	expression = fs.expression || this.firstCalculatedFiledPanel.target.attributes.attributes.formState.expression;
    	//alert(this.firstCalculatedFiledPanel.target.attributes.attributes.formState.expression + ' vediamo se così va: ' + expression );
		return expression;
    }

    , setExpItems: function(itemGroupName, items) {
    	this.firstCalculatedFiledPanel.setExpItems(itemGroupName, items);
    }

	, setTargetRecord: function(record) {
		this.firstCalculatedFiledPanel.setTargetRecord(record);
	}

	, setTargetNode: function(node) {
		this.firstCalculatedFiledPanel.setTargetNode(node);
	}
    
    , getCalculatedFiledPanel : function(){		
		return this.firstCalculatedFiledPanel;
	}
	, initMainPanel: function(c) {
		if(c.startFromFirstPage !== undefined){
			this.startFromFirstPage = c.startFromFirstPage;
		}
		var editStore = new Ext.data.JsonStore({
	        root: 'slots',
	        data: {slots:[]},
	        fields: ['name', 'valueset']
	    });
		this.modality = c.modality;//add (not passed) or edit
		if(this.modality !== undefined && this.modality !== null && this.modality =='edit'){
			this.startFromFirstPage = false;//this function is to edit the slot only
			var field = this.fieldForSlot;
			try{
				var storedata = {slots: field.attributes.attributes.formState.slots}
				editStore.loadData(storedata);
				editStore.commitChanges();
			}catch(err){
				alert(LN('sbi.qbe.bands.noteditable'));
				return;
			}
		}
		var save = function(){
			this.save();
		};
		
		
		var navHandler = function(page){
			if(this.mainPanel !== null){
				var curr = this.mainPanel.layout.activeItem;
				if(page == 1){
					this.mainPanel.layout.setActiveItem(1);
					btnNext.disabled = true;
					btnPrev.disabled = false;
					btnFinish.disabled = false;
					btnNext.disable();
					btnPrev.enable();
					btnFinish.enable();
					///gets field id
					var fs = this.firstCalculatedFiledPanel.getFormState();
					this.expression = fs.expression;

				}else{
					//back
					this.mainPanel.layout.setActiveItem(0);
					btnPrev.disabled = true;
					btnNext.disabled = false;
					btnFinish.disabled = true;
					
					btnPrev.disable();
					btnNext.enable();
					btnFinish.disable();
				}
			}
		};
		var btnPrev = new Ext.Button({
            id: 'move-prev',
            text: LN('sbi.qbe.bands.back.btn'),
            handler: navHandler.createDelegate(this, [-1])
		});
		
		var btnNext = new Ext.Button({
            id: 'move-next',
            text: LN('sbi.qbe.bands.next.btn'),
            handler: navHandler.createDelegate(this, [1])
		});
		
		var btnFinish = new Ext.Button({
            id: 'finish',
            text: LN('sbi.qbe.bands.finish.btn'),
            disabled: false,
            scope: this,
            handler: function(){
			    var formState = null;
				var target = this.firstCalculatedFiledPanel.target;
				//add band mode
				if(this.modality === undefined || this.modality == null || this.modality !='edit'){

					if(this.startFromFirstPage == undefined || this.startFromFirstPage == null || this.startFromFirstPage == false){
						formState = {alias: LN('sbi.qbe.bands.prefix') +this.fieldForSlot.text, type: 'STRING', expression: this.fieldForSlot.text };
						target = this.fieldForSlot.parentNode;
					}else{
						formState = this.firstCalculatedFiledPanel.getFormState();
					}
					this.addSlotToFormState(formState);
				}else{
					//edit band
					formState = this.fieldForSlot.attributes.attributes.formState;
					this.addSlotToFormState(formState);
					target = this.fieldForSlot;
				}
		    	this.fireEvent('apply', this, formState, target);
		        this.close();

		    }
		});

		this.firstCalculatedFiledPanel = new Sbi.qbe.CalculatedFieldEditorPanel({
			expItemGroups: c.expItemGroups
			, fields: c.fields
			, functions: c.functions
			, aggregationFunctions: c.aggregationFunctions
			, dateFunctions: c.dateFunctions
			, expertMode: c.expertMode
			, scopeComboBoxData: c.scopeComboBoxData   		
			, validationService: c.validationService
			, expertDisable: true

		});
		var firstPage = null;
		if(this.startFromFirstPage){
			firstPage = this.firstCalculatedFiledPanel;
			
		}
		var fieldID = null;
		if(this.fieldForSlot !== null){
			fieldID= this.fieldForSlot.attributes.id;
			if(fieldID !== undefined && fieldID !== null && fieldID.indexOf('xnode-') !== -1){
				fieldID = null;
				this.expression = this.fieldForSlot.attributes.attributes.formState.expression;
			}
		}
		this.secondSlotDefinitionPanel = new Sbi.qbe.SlotEditorPanel({
			height: 420,
			autoWidth: true,
			fieldId: fieldID,
			firstPage: firstPage,
			slotWizard: this,
			editStore: editStore
	    });
		var wizardPages = [];
		
		if(this.startFromFirstPage){
			wizardPages = [this.firstCalculatedFiledPanel, this.secondSlotDefinitionPanel] ; 
			btnPrev.disable();
			btnNext.enable();
			btnFinish.disable();
			

		}else{
			wizardPages = [this.secondSlotDefinitionPanel] ; 
			btnPrev.disable();
			btnNext.disable();
			btnFinish.enable();
			if(this.modality =='edit'){
				wizardPages = [this.firstCalculatedFiledPanel, this.secondSlotDefinitionPanel] ; 
			}
		}
		this.mainPanel = new Ext.Panel({  
			    layout: 'card',  
			    activeItem: 0,  
			    scope: this,
				height: 420,
				autoWidth: true,
				resizable: true,
			    defaults: {border:false},  
			    bbar: [
			           btnPrev,
			           '->', // greedy spacer so that the buttons are aligned to each side
			           btnNext,
			           btnFinish
			    ], 
			    items: wizardPages
		});  
		
		this.firstCalculatedFiledPanel.doLayout();
		this.secondSlotDefinitionPanel.doLayout();
		if(this.modality =='edit'){
			this.mainPanel.activeItem = 1;
			btnPrev.enable();
		}
		this.mainPanel.doLayout();

    }
	, addSlotToFormState: function(formState){
		
		var slotStore = this.secondSlotDefinitionPanel.gridPanel.store;
		var slots = [];
		if(slotStore !== null){
			for (var i = 0; i < slotStore.data.length; i++) { 
				var record = slotStore.getAt(i); 
				slots[i] = record.data; 
			}
		}
		formState.slots = slots;
		
	}


});