/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

Ext.ns("Sbi.cockpit.widgets.table");

Sbi.cockpit.widgets.crosstab.CrossTabWidget = function(config) {	
	Sbi.trace("[CrossTabWidget.constructor]: IN");
	
	var settings = Sbi.getObjectSettings('Sbi.cockpit.widgets.crosstab.CrossTabWidget', defaultSettings);
	var c = Ext.apply(settings, config || {});
	Ext.apply(this, c);
	
	this.initServices();
	this.init();
	
	Sbi.cockpit.widgets.crosstab.CrossTabWidget.superclass.constructor.call(this, c);
	
	this.on("afterRender", function(){
//		this.getStore().load();
		Sbi.storeManager.loadStore(this.storeId);
		//this.refresh();
		Sbi.trace("[CrossTabWidget.onRender]: store loaded");
	}, this);

	this.addEvents('selection');
	
	Sbi.trace("[CrossTabWidget.constructor]: OUT");
};

Ext.extend(Sbi.cockpit.widgets.crosstab.CrossTabWidget, Sbi.cockpit.core.WidgetRuntime, {
    
	// =================================================================================================================
	// PROPERTIES
	// =================================================================================================================
	
	crosstabTemplate: {}
	, isStatic: false
	, crosstabDefinitionPanel: null
	, columnsContainerPanel: null
	, rowsContainerPanel: null
	, measuresContainerPanel: null
	, ddGroup: null // must be provided with the constructor input object
	
	// -----------------------------------------------------------------------------------------------------------------
    // public methods
	// -----------------------------------------------------------------------------------------------------------------
	, getCrosstabDefinition: function() {
		var crosstabDef = {};
		crosstabDef.rows = this.rowsContainerPanel.getContainedAttributes();
		crosstabDef.columns = this.columnsContainerPanel.getContainedAttributes();
		crosstabDef.measures = this.measuresContainerPanel.getContainedMeasures();
		crosstabDef.config = this.measuresContainerPanel.getCrosstabConfig();
		crosstabDef.config.type = 'pivot';
		return crosstabDef;
	}
	
	, getFormState: function() {
		var crosstabDefinition = this.getCrosstabDefinition();
		var state = {
				'designer':'Pivot Table',
				'crosstabDefinition': crosstabDefinition
		};
		return state;
	}

	, setFormState: function(state) {
		if (state !== undefined && state !== null && state.crosstabDefinition !== undefined && state.crosstabDefinition !== null) {
			var crosstabDefinition = state.crosstabDefinition;
			if (crosstabDefinition.rows) this.rowsContainerPanel.setAttributes(crosstabDefinition.rows);
			if (crosstabDefinition.columns) this.columnsContainerPanel.setAttributes(crosstabDefinition.columns);
			if (crosstabDefinition.measures) this.measuresContainerPanel.setMeasures(crosstabDefinition.measures);
			if (crosstabDefinition.config) this.measuresContainerPanel.setCrosstabConfig(crosstabDefinition.config);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------
    // init methods
    // -----------------------------------------------------------------------------------------------------------------
	, init: function(c) {		
		this.columnsContainerPanel = new Sbi.crosstab.AttributesContainerPanel({
			title: LN('sbi.crosstab.crosstabdefinitionpanel.columns')
			, width: 400
			, initialData: this.crosstabTemplate.columns
			, ddGroup: this.ddGroup
		});
		// propagate events
		this.columnsContainerPanel.on(
			'attributeDblClick' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeDblClick", this, attribute); 
			}, 
			this
		);
		this.columnsContainerPanel.on(
			'attributeRemoved' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeRemoved", this, attribute); 
			}, 
			this
		);
	
		this.rowsContainerPanel = new Sbi.crosstab.AttributesContainerPanel({
			title: LN('sbi.crosstab.crosstabdefinitionpanel.rows')
			, width: 200
			, initialData: this.crosstabTemplate.rows
			, ddGroup: this.ddGroup
		});
		// propagate events
		this.rowsContainerPanel.on(
			'attributeDblClick' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeDblClick", this, attribute); 
			}, 
			this
		);
		this.rowsContainerPanel.on(
			'attributeRemoved' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeRemoved", this, attribute); 
			}, 
			this
		);
	
		this.measuresContainerPanel = new Sbi.crosstab.MeasuresContainerPanel({
			title: LN('sbi.crosstab.crosstabdefinitionpanel.measures')
			, width: 400
			, initialData: this.crosstabTemplate.measures
			, crosstabConfig: this.crosstabTemplate.config
			, ddGroup: this.ddGroup
			, isStatic: this.isStatic
		});
	
		this.crosstabDefinitionPanel = new Ext.Panel({
			layout: 'table'
				, baseCls:'x-plain'
					, padding: '30 30 30 100'
						, layoutConfig: {columns:2}
		// applied to child components
		, defaults: {height: 150}
		, items:[
		         {
		        	 border: false
		         }
		         , this.columnsContainerPanel
		         , this.rowsContainerPanel
		         , this.measuresContainerPanel
		         ]
		});
	}
});


Sbi.registerWidget('crosstab', {
	name: 'CrossTab'
	, icon: 'js/src/ext/sbi/cockpit/widgets/crosstab/table_64x64_ico.png'
	, runtimeClass: 'Sbi.cockpit.widgets.crosstab.CrossTablWidget'
	, designerClass: 'Sbi.cockpit.widgets.crosstab.CrossTablWidgetDesigner'
	//, designerClass: 'Ext.Panel'
});