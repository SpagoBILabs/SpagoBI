/** SpagoBI, the Open Source Business Intelligence suite

 * © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  

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