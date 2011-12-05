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

Sbi.kpi.KpiGUIComments =  function(config) {
		
		var defaultSettings = {};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.KpiGUIDescription) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.KpiGUIComments);
		}

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
		
		this.initComments(c);
   
		Sbi.kpi.KpiGUIComments.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiGUIComments , Ext.form.FormPanel, {
	comments: new Array(),
	commentText: null,
	
	initComments: function(c){
	
		this.commentText = new Ext.form.TextArea({
			width: 350,
			boxMaxHeight: 150,
			boxMaxWidth: 450,
			style: 'margin: 5px;'
		});

		
		this.items =[this.commentText];
	}
	
	, cleanPanel: function(){

	}
	, update:  function(field){	

/*		this.descrName.setValue(field.attributes.kpiName);
		this.descrDescription.setValue(field.attributes.kpiDescr);
		this.descrCode.setValue(field.attributes.kpiCode);
		this.descrDsLbl.setValue(field.attributes.kpiDsLbl);
		this.descrTypeCd.setValue(field.attributes.kpiTypeCd);
		this.measureTypeCd.setValue(field.attributes.measureTypeCd);
		this.targetAudience.setValue(field.attributes.targetAudience);
		this.scaleName.setValue(field.attributes.scaleName);
		
		this.descrName.show();*/
		
		this.doLayout();
        this.render();
	}
});