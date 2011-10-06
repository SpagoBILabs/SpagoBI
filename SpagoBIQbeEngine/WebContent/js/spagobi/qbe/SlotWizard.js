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
		title: 'Slot wizard ...'
		, width: 600
		, height: 300
		, hasBuddy: false	
		
	});

	Ext.apply(this, c);
	
	this.initMainPanel(c);	
	
	if(c.hasBuddy === 'true') {
		this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
	}
	Sbi.qbe.SlotWizard.superclass.constructor.call(this, c);  
	this.add(this.mainPanel);
  
};

Ext.extend(Sbi.qbe.SlotWizard, Ext.Window, {
	
	hasBuddy: null
    , buddy: null
   
    , mainPanel: null
    , firstCalculatedFiledPanel : null
    , secondSlotDefinitionPanel: null
    , buttonsConfig: null
    , startFromFirstPage : true

    
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
            text: 'Back',
            handler: navHandler.createDelegate(this, [-1]),
		});
		
		var btnNext = new Ext.Button({
            id: 'move-next',
            text: 'Next',
            handler: navHandler.createDelegate(this, [1])
		});
		
		var btnFinish = new Ext.Button({
            id: 'finish',
            text: 'Finish',
            disabled: false,
            scope: this,
            handler : save.createDelegate(this)
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
			, title: 'Calulated field definition'

		});
		
		this.secondSlotDefinitionPanel = new Sbi.qbe.SlotEditorPanel({
			id: 'card-1'  ,
			width: 580,
			height: 270,
			title: 'Slot definition'

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
		}
		this.mainPanel = new Ext.Panel({  
			    layout: 'card',  
			    activeItem: 0,  
			    scope: this,
				width: 580,
				height: 270,
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
		this.mainPanel.doLayout();

    }
	, save: function(){

		this.close();
	}

});