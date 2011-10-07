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
  * by Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.qbe");

Sbi.qbe.CalculatedFieldWizard = function(config) {	
	
	var c = Ext.apply({}, config || {}, {
		title: 'Expression wizard ...'
		, width: 600
		, height: 300
		, hasBuddy: false		
	});

	Ext.apply(this, c);
	
	this.initMainPanel(c);	
	this.initButtonsConfig(c);
	
	// constructor
	Sbi.widgets.SaveWindow.superclass.constructor.call(this, {
		layout: 'fit',
		width: this.width,
		height: this.height,
		closeAction:'hide',
		plain: true,
		title: this.title,
		buttonAlign : 'center',
	    buttons: this.buttonsConfig,
		items: [this.mainPanel]
    });
	
	if(c.hasBuddy === 'true') {
		this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
	}
		
	this.addEvents('apply');    
};

Ext.extend(Sbi.qbe.CalculatedFieldWizard, Ext.Window, {
	
	hasBuddy: null
    , buddy: null
   
    , mainPanel: null 
    , buttonsConfig: null

    
    , setExpItems: function(itemGroupName, items) {
    	this.mainPanel.setExpItems(itemGroupName, items);
    }

	, setTargetRecord: function(record) {
		this.mainPanel.setTargetRecord(record);
	}

	, setTargetNode: function(node) {
		this.mainPanel.setTargetNode(node);
	}
    
	, initMainPanel: function(c) {
		this.mainPanel = new Sbi.qbe.CalculatedFieldEditorPanel({
			expItemGroups: c.expItemGroups
			, fields: c.fields
			, functions: c.functions
			, aggregationFunctions: c.aggregationFunctions
			, dateFunctions: c.dateFunctions
			, expertMode: c.expertMode
			, scopeComboBoxData: c.scopeComboBoxData   		
			, validationService: c.validationService
		});
    }

	, initButtonsConfig: function(c) {
		var okButtonConfig = {
			text: LN('sbi.qbe.calculatedFields.buttons.text.ok'),
		    handler: function(){
		   	    var emptyAlias = (this.mainPanel.inputFields.alias.getValue()==null) || (this.mainPanel.inputFields.alias.getValue()=="");
		   	    var emptyType = (this.mainPanel.inputFields.type.getValue()==null) || (this.mainPanel.inputFields.type.getValue()=="");

		   	    if(emptyAlias){
		    	   	this.mainPanel.inputFields.alias.focus();
		    	} else if(emptyType){
		    	  	this.mainPanel.inputFields.type.focus();
		    	} else {
			    	this.fireEvent('apply', this, this.mainPanel.getFormState(), this.mainPanel.target);
		           	this.hide();
		    	}
		    }
	       	, scope: this
		};
		
		var koButtonConfig = {
		    text: LN('sbi.qbe.calculatedFields.buttons.text.cancel'),
		    handler: function(){
	           	this.hide();
	      	}
	       	, scope: this
		}
		
		this.buttonsConfig = [okButtonConfig, koButtonConfig];
	}

});

//--------------------------------------------------------------------------------------------
// static methods
// --------------------------------------------------------------------------------------------
Sbi.qbe.CalculatedFieldWizard.getUsedItemSeeds = function(itemGroupName, expression) {
	 var pattern = new RegExp(/dmFields\['[^\']+'\]/g);
     var patternSeed = new RegExp(/'.*'/);
     var token = null;
     var seeds = new Array();
     while( (token = pattern.exec(expression)) !== null) {
         token = patternSeed.exec(token); 
         token = new String(token);
         token = token.substring(1, token.length - 1);
         seeds.push(token);
     } 
     return seeds;
}



