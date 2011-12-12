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
	descrDescription: null,
	
	initDescription: function(){
		this.border = false;
		this.descrName = new Ext.form.DisplayField({fieldLabel: 'Nome', 
			style: 'padding-left:5px; font-style: italic;'});
		this.descrDescription = new Ext.form.DisplayField({fieldLabel: 'Descrizione', 
			style: 'padding-left:5px; font-style: italic;'});
		this.descrCode = new Ext.form.DisplayField({fieldLabel: 'Codice', 
			style: 'padding-left:5px; font-style: italic;'});
		
		this.descrDsLbl = new Ext.form.DisplayField({fieldLabel: 'Label Dataset', 
			style: 'padding-left:5px; font-style: italic;'});
		this.descrTypeCd = new Ext.form.DisplayField({fieldLabel: 'Codice Tipo', 
			style: 'padding-left:5px; font-style: italic;'});
		this.measureTypeCd = new Ext.form.DisplayField({fieldLabel: 'Misura', 
			style: 'padding-left:5px; font-style: italic;'});
		this.scaleName = new Ext.form.DisplayField({fieldLabel: 'Scala', 
			style: 'padding-left:5px; font-style: italic;'});
		this.targetAudience = new Ext.form.DisplayField({fieldLabel: 'Target Audience', 
			style: 'padding-left:5px; font-style: italic;'});
		this.descrFields = new Ext.form.FieldSet({
	        xtype:'fieldset',
	        border: false,
	        defaultType: 'displayfield',
	        items: [this.descrName, 
	                 this.descrCode,
		             this.descrDescription,		             
		             this.descrDsLbl,
		             this.descrTypeCd,
		             this.measureTypeCd,
		             this.scaleName,
		             this.targetAudience]
	    });

		
		this.items =[this.descrFields];
	}
	
	, cleanPanel: function(){

	}
	, update:  function(field){	
		this.descrName.setValue(field.attributes.kpiName);
		this.descrDescription.setValue(field.attributes.kpiDescr);
		this.descrCode.setValue(field.attributes.kpiCode);
		this.descrDsLbl.setValue(field.attributes.kpiDsLbl);
		this.descrTypeCd.setValue(field.attributes.kpiTypeCd);
		this.measureTypeCd.setValue(field.attributes.measureTypeCd);
		this.targetAudience.setValue(field.attributes.targetAudience);
		this.scaleName.setValue(field.attributes.scaleName);
		
		this.descrName.show();
		this.doLayout();
        this.render();
	}
});