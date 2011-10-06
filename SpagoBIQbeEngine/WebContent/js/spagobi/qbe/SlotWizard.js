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
		
		var navHandler = function(direction){
			if(this.mainPanel !== null){
				var curr = this.mainPanel.layout.activeItem;
				if(direction == 1){
					this.mainPanel.layout.setActiveItem(1);
					//this.mainPanel.toolbars.items[2].disabled = false;
				}else{
					//back
					this.mainPanel.layout.setActiveItem(0);
					//this.mainPanel.toolbars.items[0].disabled = true;
				}
			}
		};
		
		var save = function(){
			this.save();
		};
		
		this.firstCalculatedFiledPanel = new Sbi.qbe.CalculatedFieldEditorPanel({
			expItemGroups: c.expItemGroups
			, fields: c.fields
			, functions: c.functions
			, aggregationFunctions: c.aggregationFunctions
			, dateFunctions: c.dateFunctions
			, expertMode: c.expertMode
			, scopeComboBoxData: c.scopeComboBoxData   		
			, validationService: c.validationService
		});
		
		this.secondSlotDefinitionPanel = new Ext.Panel({
			id: 'card-1',  
	        layout: 'fit',
	        html: '<p>Step 2 of 2: complete!!!</p><p>Almost there.  Please click the "Next" button to continue...</p>'  
	    });
		
		this.mainPanel = new Ext.Panel({  
			    title: 'Card Layout (Wizard)',  
			    layout: 'card',  
			    activeItem: 0,  
			    bodyStyle: 'padding:15px',
			    scope: this,
			    defaults: {border:false},  
			    bbar: [
			           {
			               id: 'move-prev',
			               text: 'Back',
			               handler: navHandler.createDelegate(this, [-1]),
			               disabled: true
			           },
			           '->', // greedy spacer so that the buttons are aligned to each side
			           {
			               id: 'move-next',
			               text: 'Next',
			               handler: navHandler.createDelegate(this, [1])
			           },
			           {
			               id: 'finish',
			               text: 'Finish',
			               disabled: false,
			               scope: this,
			               handler : save.createDelegate(this) 
			               
			           }
			    ], 
			    items: [this.firstCalculatedFiledPanel,this.secondSlotDefinitionPanel]  
		});    
		this.mainPanel.doLayout();
    }
	, save: function(){
		alert('save');
	}
});