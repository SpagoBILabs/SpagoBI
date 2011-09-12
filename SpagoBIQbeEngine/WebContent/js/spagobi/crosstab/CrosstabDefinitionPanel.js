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

Ext.ns("Sbi.crosstab");

Sbi.crosstab.CrosstabDefinitionPanel = function(config) {

	var defaultSettings = {
			title: LN('sbi.crosstab.crosstabdefinitionpanel.title')
	};
	if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.crosstabDefinitionPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.crosstabDefinitionPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c); // this operation should overwrite this.crosstabTemplate content, that is the definition of the crosstab

	this.init(c);

	c = Ext.apply(c, {
		items: [this.crosstabDefinitionPanel]
		        , autoScroll: true
		        , tools: this.tools || []
	});

	// constructor
	Sbi.crosstab.CrosstabDefinitionPanel.superclass.constructor.call(this, c);

    this.addEvents("beforeAddAttribute");

	
	this.columnsContainerPanel.on('beforeAddAttribute',
			//this.checkAttNotPresent
			function(crossTabDef, att){
				var boolean = this.fireEvent('beforeAddAttribute', this,  att);
				return boolean;
				}
			, this);
	this.rowsContainerPanel.on('beforeAddAttribute', 
			//this.checkAttNotPresent
			function(crossTabDef, att){
				var boolean = this.fireEvent('beforeAddAttribute', this,  att);
				return boolean;
				}
			, this);
	

	
//	this.addEvents('preview');

};

Ext.extend(Sbi.crosstab.CrosstabDefinitionPanel, Ext.Panel, {

	crosstabTemplate: {}
, crosstabDefinitionPanel: null
, columnsContainerPanel: null
, rowsContainerPanel: null
, measuresContainerPanel: null
, ddGroup: null // must be provided with the constructor input object

, init: function(c) {

	this.columnsContainerPanel = new Sbi.crosstab.AttributesContainerPanel({
		title: LN('sbi.crosstab.crosstabdefinitionpanel.columns')
		, width: 400
		, initialData: this.crosstabTemplate.columns
		, ddGroup: this.ddGroup
	});

	this.rowsContainerPanel = new Sbi.crosstab.AttributesContainerPanel({
		title: LN('sbi.crosstab.crosstabdefinitionpanel.rows')
		, width: 200
		, initialData: this.crosstabTemplate.rows
		, ddGroup: this.ddGroup
	});

	this.measuresContainerPanel = new Sbi.crosstab.MeasuresContainerPanel({
		title: LN('sbi.crosstab.crosstabdefinitionpanel.measures')
		, width: 400
		, initialData: this.crosstabTemplate.measures
		, crosstabConfig: this.crosstabTemplate.config
		, ddGroup: this.ddGroup
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

, getCrosstabDefinition: function() {
	var crosstabDef = {};
	crosstabDef.rows = this.rowsContainerPanel.getContainedAttributes();
	crosstabDef.columns = this.columnsContainerPanel.getContainedAttributes();
	crosstabDef.measures = this.measuresContainerPanel.getContainedMeasures();
	crosstabDef.config = this.measuresContainerPanel.getCrosstabConfig();
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




/* check cross tab validity: rules are
 * - at least one measure
 * - at least one attribute in rows or in columns
 * - if there is segment attribute it must be used
 * - if ther is mandatory measure it must be used
 */

, validate: function(){

	var crossTabDef = this.getCrosstabDefinition();

	// at least one measure
	if(crossTabDef.measures.length<1){
		return LN('sbi.crosstab.crossTabValidation.noMeasure');

	}

	// at least one row or one column
	else if(crossTabDef.columns.length<1 && crossTabDef.rows.length<1){
		return LN('sbi.crosstab.crossTabValidation.noAttribute');
	} 

	// if there is mandatoryField it must have been inserted
	if(this.measuresContainerPanel.hasMandatoryMeasure === true){
		var isMandatory = this.isThereMandatoryMeasure(crossTabDef);
		if(isMandatory === false){
			return LN('sbi.crosstab.crossTabValidation.noMandatoryMeasure');
		}

	}

	// if there is segmentAttribute it must have been inserted in columns or rows
	if(this.rowsContainerPanel.hasSegmentAttribute === true || this.columnsContainerPanel.hasSegmentAttribute === true){
		var isSegment = this.isThereSegmentAttribute(crossTabDef);
		if(isSegment === false){
			return LN('sbi.crosstab.crossTabValidation.noSegmentAttribute');
		}
	}
	return null;	
}

, isThereSegmentAttribute: function(crossTabDef){
	var isThereSegment = false;
	for (var i = 0; i < crossTabDef.rows.length && isThereSegment === false; i++) {
		var row = crossTabDef.rows[i];
		if(row.nature === 'segment_attribute'){
			isThereSegment = true;
		}
	}

	for (var i = 0; i < crossTabDef.columns.length && isThereSegment === false; i++) {
		var row = crossTabDef.columns[i];
		if(row.nature === 'segment_attribute'){
			isThereSegment = true;
		}
	}
	return isThereSegment;
}
, isThereMandatoryMeasure: function(crossTabDef){
	var isThereMandatory = false;
	for (var i = 0; i < crossTabDef.measures.length && isThereMandatory === false; i++) {
		var measure = crossTabDef.measures[i];
		if(measure.nature === 'mandatory_measure'){
			isThereMandatory = true;
		}
	}
	return isThereMandatory;
}
, checkAttNotPresent: function(crossTabDef, att){
	var id = att.data.id;	
	var rows = this.rowsContainerPanel;
	var columns = this.columnsContainerPanel;
	var storeRows = rows.store;
	var storeColumns = columns.store;
	if (storeRows.find('id', id) !== -1) {
			return false;
	}
	else if(storeColumns.find('id', id) !== -1){

			return false;
	}
	
	return true;
	}
});