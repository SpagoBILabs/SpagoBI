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
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.formviewer");

Sbi.formviewer.StaticClosedXORFiltersPanel = function(aStaticClosedXORFiltersGroup, config) {
	
	var defaultSettings = {
		// set default values here
		frame: true
		, autoScroll: true
		//, autoWidth: true
		, autoHeight: true
        //, width: aStaticClosedXORFiltersGroup.width || 300
        //, height: aStaticClosedXORFiltersGroup.height || 150
	};
	if (Sbi.settings && Sbi.settings.formviewer && Sbi.settings.formviewer.staticClosedXORFiltersPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.formviewer.staticClosedXORFiltersPanel);
	}
	var c = Ext.apply(defaultSettings, config || {});
	
	this.init(aStaticClosedXORFiltersGroup);
	
	Ext.apply(c, {
        items: this.items
	});
	
	// constructor
    Sbi.formviewer.StaticClosedXORFiltersPanel.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.formviewer.StaticClosedXORFiltersPanel, Ext.form.FormPanel, {
    
	items: null
	
	// private methods
	
	, init: function(aStaticClosedXORFiltersGroup) {
		
		this.items = {
            xtype: 'fieldset',
            id: aStaticClosedXORFiltersGroup.id,
            title: aStaticClosedXORFiltersGroup.title,
            autoHeight: true,
            autoWidth: true,
            defaultType: 'radio',
            items: []
        }
		
		if (aStaticClosedXORFiltersGroup.allowNoSelection !== null && aStaticClosedXORFiltersGroup.allowNoSelection === true) {
			// create No Selection Item
			this.items.items.push({
				hideLabel: true,
				//fieldLabel: '',
				//labelSeparator: '',
				//labelStyle: 'width: 0px',
				//itemCls: 'no-padding',
				boxLabel: aStaticClosedXORFiltersGroup.noSelectionText,
				name: aStaticClosedXORFiltersGroup.id,
				inputValue: 'noSelection'
			});
		}
		
		for (var i = 0; i < aStaticClosedXORFiltersGroup.options.length; i++) {
			// create items
			var anOption = aStaticClosedXORFiltersGroup.options[i];
			this.items.items.push({
				hideLabel: true,
				//fieldLabel: '',
				//labelSeparator: '',
				//labelStyle: 'width: 0px',
				//itemCls: 'no-padding',
                boxLabel: anOption.text,
                name: aStaticClosedXORFiltersGroup.id,
                inputValue: anOption.id
			});
		}
	}
	
	// public methods
	
	, getFormState: function() {
		var state = this.getForm().getValues();
		return state;
	}

	, setFormState: function(selectedValue) {
		if(this.items!=null && this.items.items[0]!=null && this.items.items[0].items!=null && this.items.items[0].items.items!=null){
			for(var i=0; i<this.items.items[0].items.items.length;i++){
				if(this.items.items[0].items.items[i].inputValue == selectedValue){
					this.items.items[0].items.items[i].setValue(true);
					break;
				}
			}
		}
	}
	
	
	
});