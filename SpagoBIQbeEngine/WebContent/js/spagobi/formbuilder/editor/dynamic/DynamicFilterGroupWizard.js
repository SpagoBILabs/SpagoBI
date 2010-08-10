/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
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
  * Object name 
  * 
  * [description]
  * 
  * 
  * Public Properties
  * 
  * [list]
  * 
  * 
  * Public Methods
  * 
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - by Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.formbuilder");

Sbi.formbuilder.DynamicFilterGroupWizard = function(config) {
	
	var defaultSettings = {
		// set default values here
		title: LN('sbi.formbuilder.dynamicfiltergroupwizard.title')
		, autoScroll: true
		, width: 400
		, height: 150
	};
	if (Sbi.settings && Sbi.settings.formbuilder && Sbi.settings.formbuilder.dynamicFilterGroupWizard) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formbuilder.dynamicFilterGroupWizard);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = Ext.apply(c, {
		layout: 'fit',
		width: this.width,
		height: this.height,
		closeAction:'hide',
		plain: true,
		modal: true,
		resizable: false,
		title: this.title,
		items: [this.formPanel]
	});
	
	// constructor
    Sbi.formbuilder.DynamicFilterGroupWizard.superclass.constructor.call(this, c);
    
    if(this.hasBuddy === 'true') {
		this.buddy = new Sbi.commons.ComponentBuddy({
    		buddy : this
    	});
	}
	
    this.addEvents('apply');
    
};

Ext.extend(Sbi.formbuilder.DynamicFilterGroupWizard, Ext.Window, {

	formPanel: null
	, filterName: null
	, operatorField: null
	
	
	// --------------------------------------------------------------------------
	// Public
	// --------------------------------------------------------------------------
	
	, getFormState : function () {
		var s = {};
		s.operator = this.operatorField.getValue();
		s.title = this.filterName.getValue();
		return s;
	}

	, setFormState: function(s) {
		if(s.operator) {
			this.operatorField.setValue(s.operator);
		}
		if(s.title) {
			this.filterName.setValue(s.title);
		}
	}
	
	, resetFormState: function() {
		this.filterName.setValue('');
		this.filterName.reset();
		this.operatorField.setValue('');
		this.operatorField.reset();
	}
	
	, setTarget: function(targetFilter) {
		this.targetFilter = targetFilter;
		
		if(this.targetFilter === null) {
			this.resetFormState();
		} else {
			this.setFormState(this.targetFilter.getContents());
		}
	}
	
	, getTarget: function() {
		return this.targetFilter;
	}
	
	
	// --------------------------------------------------------------------------
	// Private
	// --------------------------------------------------------------------------
	
	, init: function() {
		var items = [];
		
		this.filterName = new Ext.form.TextField({
			id: 'title',
			name: 'title',
			allowBlank: true, 
			inputType: 'text',
			maxLength: 100,
			width: 250,
			fieldLabel: LN('sbi.formbuilder.dynamicfiltergroupwizard.fields.filtername.label')
		});
		
		items.push(this.filterName);
		
		var filterOptStore = new Ext.data.SimpleStore({
		    fields: ['funzione', 'nome', 'descrizione'],
		    data : [
		            ['EQUALS TO', LN('sbi.qbe.filtergridpanel.foperators.name.eq'),  LN('sbi.qbe.filtergridpanel.foperators.desc.eq')],
		            ['BETWEEN', LN('sbi.qbe.filtergridpanel.foperators.name.between'),  LN('sbi.qbe.filtergridpanel.foperators.desc.between')]
		    ]
		});
		
	    this.operatorField = new Ext.form.ComboBox({
			//tpl: '<tpl for="."><div ext:qtip="{nome}: {descrizione}" class="x-combo-list-item">{nome}</div></tpl>',	
			store:  filterOptStore, 
			displayField: 'nome',
			valueField: 'funzione',
			maxHeight: 200,
			allowBlank: false,
			editable: true,
			typeAhead: true, // True to populate and autoselect the remainder of the text being typed after a configurable delay
			mode: 'local',
			forceSelection: true, // True to restrict the selected value to one of the values in the list
			triggerAction: 'all',
			emptyText: LN('sbi.qbe.filtergridpanel.foperators.editor.emptymsg'),
			selectOnFocus: true, //True to select any existing text in the field immediately on focus
			fieldLabel: LN('sbi.formbuilder.dynamicfiltergroupwizard.fields.operatorfield.label')
	    });

    	items.push(this.operatorField);
    	
    	this.formPanel = new Ext.form.FormPanel({
    		frame:true,
    		labelWidth: 80,
    		defaults: {
    			width: 225
    		},
    	    bodyStyle:'padding:5px 5px 0',
    	    buttonAlign : 'center',
    	    items: items,
    	    buttons: [{
    			text: LN('sbi.formbuilder.dynamicfiltergroupwizard.buttons.apply'),
    		    handler: function(){
    	    		this.fireEvent('apply', this, this.getTarget(), this.getFormState());
                	this.hide();
            	}
            	, scope: this
    	    },{
    		    text: LN('sbi.formbuilder.dynamicfiltergroupwizard.buttons.cancel'),
    		    handler: function(){
                	this.hide();
            	}
            	, scope: this
    		}]
    	 });
	}
	
});