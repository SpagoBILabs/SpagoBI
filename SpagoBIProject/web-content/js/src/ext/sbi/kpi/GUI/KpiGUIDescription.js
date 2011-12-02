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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUIDescription =  function(config) {
		
		var defaultSettings = {};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.KpiGUIDescription) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.KpiGUIDescription);
		}

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
		
		this.initDescription(c);
   
		Sbi.kpi.KpiGUIDescription.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiGUIDescription , Ext.form.FormPanel, {
	items: null,
	descrFields: null,
	descrName: null,
	
	initDescription: function(){
		this.descrName = new Ext.form.DisplayField({fieldLabel: 'Nome', 
			style: 'padding-left:5px; font-style: italic;'});

		this.descrFields = new Ext.form.FieldSet({
	        xtype:'fieldset',
	        border: false,
	        defaultType: 'displayfield',
	        items: [this.descrName]
	    });

		
		this.items =[this.descrFields];
	}
	
	, cleanPanel: function(){

	}
	, update:  function(field){	
		this.descrName.setValue(field.attributes.kpiName);
		this.descrName.show();
		this.doLayout();
        this.render();
	}
});