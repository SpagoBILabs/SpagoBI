/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.ns("Sbi.cockpit.widgets.barchart");

Sbi.cockpit.widgets.barchart.SeriesGroupingPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.seriesgroupingpanel.title')
		, frame: true
		, emptyMsg: LN('sbi.worksheet.designer.seriesgroupingpanel.emptymsg')
	};
		
	if (Sbi.settings && Sbi.settings.cockpit && Sbi.settings.cockpit.widgets && Sbi.settings.cockpit.widgets.barchart && Sbi.settings.cockpit.widgets.barchart.seriesGroupingPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.cockpit.widgets.barchart.seriesGroupingPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);

	// constructor	
	Sbi.cockpit.widgets.barchart.SeriesGroupingPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.cockpit.widgets.barchart.SeriesGroupingPanel, Sbi.cockpit.widgets.barchart.ChartCategoryPanel, {
	    
	getSeriesGroupingAttribute : function () {
		return this.getCategory();
	}

	,
	setSeriesGroupingAttribute : function (attribute) {
		return this.setCategory(attribute);
	}
	
});