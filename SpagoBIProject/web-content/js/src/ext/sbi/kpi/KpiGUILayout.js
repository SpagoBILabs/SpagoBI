/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUILayout =  function(config) {

		
		var defaultSettings = {
			layout:'border'
		};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.kpiGUILayout) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.kpiGUILayout);
		}

		var c = Ext.apply(defaultSettings);

		Ext.apply(this, c);
		this.addEvents();
		this.intPanels(config);
		
		c = {
			items:[this.kpiGridPanel, this.kpiAccordionPanel]
		};
   
		Sbi.kpi.KpiGUILayout.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiGUILayout , Ext.Panel, {
	kpiGridPanel: null,
	kpiAccordionPanel: null,
	
	intPanels : function(config){
		var gridconf= config.grid;
		var accordionconf = config.accordion;
		this.kpiGridPanel = new Sbi.kpi.KpiGridPanel(gridconf);
		
		this.kpiAccordionPanel = new Sbi.kpi.KpiAccordionPanel(accordionconf);
		
		this.kpiGridPanel.on('updateAccordion',function(field){
			this.kpiAccordionPanel.updateAccordion(field);
		},this);
	}

	
});