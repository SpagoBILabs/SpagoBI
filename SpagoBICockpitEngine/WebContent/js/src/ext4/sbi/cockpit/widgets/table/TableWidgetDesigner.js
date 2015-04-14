/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/







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
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */

Ext.ns("Sbi.cockpit.widgets.table");

Sbi.cockpit.widgets.table.TableWidgetDesigner = function(config) {

	var defaultSettings = {
		name: 'tableWidgetDesigner',
		title: LN('sbi.cockpit.widgets.table.tableWidgetDesigner.title')
	};

	if (Sbi.settings && Sbi.settings.cockpit && Sbi.settings.cockpit.widgets && Sbi.settings.cockpit.widgets.table && Sbi.settings.cockpit.widgets.table.tableWidgetDesigner) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.cockpit.widgets.table.tableWidgetDesigner);
	}
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);

	this.addEvents("attributeDblClick", "attributeRemoved");

	this.tableDesigner = new Sbi.cockpit.widgets.table.QueryFieldsCardPanel({
		ddGroup: this.ddGroup,
		title: 'Table Designer'
	});

	// propagate events
	this.tableDesigner.on(
		'attributeDblClick' ,
		function (thePanel, attribute) {
			//this.fireEvent("attributeDblClick", this, attribute);
			this.attributeDblClickHandler(attribute, thePanel);
		},
		this
	);
	this.tableDesigner.on(
		'attributeRemoved' ,
		function (thePanel, attribute) {
			this.fireEvent("attributeRemoved", this, attribute);
		},
		this
	);

	this.tableDesigner.on(
		'beforerender' ,
		function (thePanel, attribute) {
			if(Sbi.isValorized(this.visibleselectfields)) {
				this.setDesignerState({
						visibleselectfields: this.visibleselectfields,
						fontType: this.fontType,
						fontSize: this.fontSize,
						headerFontSize: this.headerFontSize,
						headerFontColor: this.headerFontColor,
						headerFontWeight: this.headerFontWeight,
						headerFontDecoration: this.headerFontDecoration,
						rowsFontSize: this.rowsFontSize,
						rowsFontColor: this.rowsFontColor,
						rowsFontWeight: this.rowsFontWeight,
						rowsFontDecoration: this.rowsFontDecoration
				});
			}
		},
		this
	);
	
	/* font array definition: TODO create stores in a separate way */
	
	var fontSizeStore =  Ext.create('Ext.data.ArrayStore', {
		fields : ['name', 'description']
		, data : [[6,"6"],[8,"8"],[10,"10"],[12,"12"],[14,"14"],[16,"16"],[18,"18"],[22,"22"],[24,"24"],[28,"28"],[32,"32"],[36,"36"],[40,"40"]]
	});
	
	var fontFamilyStore = Ext.create('Ext.data.ArrayStore', {
		fields: ['name','description'],
		data:   [['Arial','Arial'], ['"Courier New"','Courier New'], ['Tahoma','Tahoma'], ['"Times New Roman"','Times New Roman'],['Verdana','Verdana'],]
	});
	
	var fontWeightStore = Ext.create('Ext.data.ArrayStore', {
		fields: ['name','description'],
		data:   [['normal',LN('sbi.cockpit.designer.fontConf.normalFontWeight')],['bold',LN('sbi.cockpit.designer.fontConf.boldFontWeight')]]
	});
	
	var fontDecorationStore = Ext.create('Ext.data.ArrayStore', {
		fields: ['name','description'],
		data:   [['none',LN('sbi.cockpit.designer.fontConf.noneFontDecoration')], 
		         ['overline',LN('sbi.cockpit.designer.fontConf.overlineFontDecoration')],
		         ['line-through',LN('sbi.cockpit.designer.fontConf.linethroughFontDecoration')],
		         ['underline',LN('sbi.cockpit.designer.fontConf.underlineFontDecoration')]]
	});
	
	var hexColorReg = new RegExp("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
	
	/* table font general options */
	
	this.fontTypeCombo = Ext.create('Ext.form.ComboBox',{
		fieldLabel: 	LN('sbi.cockpit.designer.fontConf.widgetFontType'),
		queryMode:      'local',
		triggerAction:  'all',
		forceSelection: true,
		editable:       false,
		allowBlank: 	true,
		typeAhead: 		true,
		lazyRender:		true,
		store: 			fontFamilyStore, 
		valueField: 	'name',
		displayField: 	'description',
		name:			'fontType',
		labelWidth:		110,
		width:			245

	});
	
	this.fontSizeCombo = Ext.create('Ext.form.ComboBox',{
		fieldLabel: 	LN('sbi.cockpit.designer.fontConf.widgetFontSize'),
		queryMode:      'local',
		triggerAction:  'all',
		forceSelection: true,
		editable:       false,
		allowBlank: 	true,
		typeAhead: 		true,
		lazyRender:		true,
		store: 			fontSizeStore, 
		valueField: 	'name',
		displayField: 	'description',
		name:			'fontSize',
		labelWidth:		120,
		width:			170

	});
	
	var tableGeneralFontOptions = 
	{
		xtype: 				'fieldset'
		, fieldDefaults: 	{ margin: 5}
		, layout: 			{type: 'table', columns: 2}
        , collapsible: 		true
        , collapsed: 		true
        , title: 			LN('sbi.cockpit.designer.fontConf.tableFontGeneralOpts')
        , margin: 			10
    	, items: 			[this.fontTypeCombo, this.fontSizeCombo]	
		, width:			600
	};
	
	
	/* table font header options */
	
	 this.headerFontSizeCombo = Ext.create('Ext.form.ComboBox',{
			fieldLabel: 	LN('sbi.cockpit.designer.fontConf.widgetFontSize'),
			queryMode:      'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			typeAhead: 		true,
			lazyRender:		true,
			store: 			fontSizeStore,    
			valueField: 	'name',
			displayField: 	'description',
			name:			'headerFontSize',
			labelWidth:		130,
			width:			180,
		});
	 
	
	 this.headerFontColorText = Ext.create('Ext.form.field.Text',{
			 fieldLabel: 		LN('sbi.cockpit.designer.fontConf.fontColor'),
			 name: 				'headerFontColor',
	         allowBlank: 		true,
	         regex: 			hexColorReg,
	         regextText: 		'Not a valid HEX color',
	    	 enforceMaxLength: 	true,
	 		 maxLength: 		7,
	 		 msgTarget: 		'side',
 			labelWidth:			140,
			width:				250,
			afterLabelTextTpl : '<span class="help" data-qtip="'
				+ LN('sbi.cockpit.designer.fontConf.fontColorTip')
            	+ '">&nbsp;&nbsp;&nbsp;&nbsp;</span>',
	 });
	 
	 this.headerFontWeightCombo = Ext.create('Ext.form.ComboBox',{
			fieldLabel: 	LN('sbi.cockpit.designer.fontConf.fontWeight'),
			queryMode:      'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			typeAhead: 		true,
			lazyRender:		true,
			store: 			fontWeightStore, 
			valueField: 	'name',
			displayField: 	'description',
			name:			'headerFontWeight',
			labelWidth:		130,
			width:			245

		});
	 
	 this.headerFontDecorationCombo = Ext.create('Ext.form.ComboBox',{
			fieldLabel: 	LN('sbi.cockpit.designer.fontConf.fontDecoration'),
			queryMode:      'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			typeAhead: 		true,
			lazyRender:		true,
			store: 			fontDecorationStore, 
			valueField: 	'name',
			displayField: 	'description',
			name:			'headerFontDecoration',
			labelWidth:		140,
			width:			255

		});
	
	 
	var tableHeaderFontOptions = 
	{
		xtype: 				'fieldset'
		, fieldDefaults: 	{ margin: 5}
		, layout: 			{type: 'table', columns: 2}
        , collapsible: 		true
        , collapsed: 		true
        , title: 			LN('sbi.cockpit.designer.fontConf.tableHeaderFontOptions')
    	, margin: 			10
    	, items: 			[this.headerFontSizeCombo, this.headerFontColorText, this.headerFontWeightCombo, this.headerFontDecorationCombo]	
		, width:			600
	};
	
	 
	 /* table font rows options */
	 
	 this.rowsFontSizeCombo = Ext.create('Ext.form.ComboBox',{
			fieldLabel: 	LN('sbi.cockpit.designer.fontConf.widgetFontSize'),
			typeAhead: 		true,
			triggerAction: 'all',
			lazyRender:		true,
			queryMode:      'local',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			store: 			fontSizeStore,    
			valueField: 	'name',
			displayField: 	'description',
			name:			'rowsFontSize',
			labelWidth:		130,
			width:			180
		});
	 
	 this.rowsFontColorText = Ext.create('Ext.form.field.Text',{
		 fieldLabel: 		LN('sbi.cockpit.designer.fontConf.fontColor'),
		 name: 				'rowsFontColor',
         allowBlank: 		true,
         regex: 			hexColorReg,
         regextText: 		'Not a valid HEX color',
    	 enforceMaxLength: 	true,
 		 maxLength: 		7,
 		 msgTarget: 		'side',
		 labelWidth:		140,
		 width:				250,
		 afterLabelTextTpl : '<span class="help" data-qtip="'
         	+ LN('sbi.cockpit.designer.fontConf.fontColorTip')
         	+ '">&nbsp;&nbsp;&nbsp;&nbsp;</span>',
	 });
	 
	 this.rowsFontWeightCombo = Ext.create('Ext.form.ComboBox',{
			fieldLabel: 	LN('sbi.cockpit.designer.fontConf.fontWeight'),
			queryMode:      'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			typeAhead: 		true,
			lazyRender:		true,
			store: 			fontWeightStore, 
			valueField: 	'name',
			displayField: 	'description',
			name:			'rowsFontWeight',
			labelWidth:		130,
			width:			245

		});
	 
	 this.rowsFontDecorationCombo = Ext.create('Ext.form.ComboBox',{
			fieldLabel: 	LN('sbi.cockpit.designer.fontConf.fontDecoration'),
			queryMode:      'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			typeAhead: 		true,
			lazyRender:		true,
			store: 			fontDecorationStore, 
			valueField: 	'name',
			displayField: 	'description',
			name:			'rowsFontDecoration',
			labelWidth:		140,
			width:			255

		});
	 
	 var tableRowsFontOptions = 
	{
		xtype: 				'fieldset'
		, fieldDefaults: 	{ margin: 5}
		, layout: 			{type: 'table', columns: 2}
        , collapsible: 		true
        , collapsed: 		true
        , title: 			LN('sbi.cockpit.designer.fontConf.tableRowsFontOptions')
    	, margin: 			10
    	, items: 			[this.rowsFontSizeCombo, this.rowsFontColorText, this.rowsFontWeightCombo, this.rowsFontDecorationCombo]	
		, width:			600
	};
	 
	this.fontConfigurationPanel = 
	{
		xtype: 				'panel'
		, layout: {
		    type: 'table',
		    columns: 1
		}

        , title: 			LN('sbi.cockpit.designer.fontConf.fontOptions')
    	, items: 			[tableGeneralFontOptions, tableHeaderFontOptions, tableRowsFontOptions]	
	};
	
	var tabPanel = Ext.create('Ext.tab.Panel', {
		        	tabPosition: 'right'
		        	, border: false
		        	, margin: 0
		        	, padding: 0
		        	, bodyStyle: 'width: 100%; height: 100%'
		        	, items:[this.tableDesigner, this.fontConfigurationPanel]
		        	//, html: "tableDesigner"
		        });

	c = {
		layout: 'fit',
		height: 400,
		items: [ tabPanel ]
	};

	Sbi.cockpit.widgets.table.TableWidgetDesigner.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.cockpit.widgets.table.TableWidgetDesigner, Sbi.cockpit.core.WidgetDesigner, {
	tableDesigner: null
	
	//field to select widget font type
	, fontTypeCombo: null
	//field to select widget font size
	, fontSizeCombo: null
	//field to select header font size
	, headerFontSizeCombo: null
	//field to select header font color
	, headerFontColorText: null
	//field to select header font weight
	, headerFontWeightCombo: null
	//field to select header font decoration
	, headerFontDecorationCombo: null
	//field to select rows font size
	, rowsFontSizeCombo: null
	//field to select rows font color
	, rowsFontColorText: null
	//field to select rows font weight
	, rowsFontWeightCombo: null
	//field to select rows font decoration
	, rowsFontDecorationCombo: null
	//panel to show font size options
	, fontConfigurationPanel: null

	, getDesignerState: function(running) {
		Sbi.trace("[TableWidgetDesigner.getDesignerState]: IN");

		var state = Sbi.cockpit.widgets.table.TableWidgetDesigner.superclass.getDesignerState(this);
		state.wtype = 'table';
		if(this.tableDesigner.rendered === true) {
			state.visibleselectfields = this.tableDesigner.tableDesigner.getContainedValues();
		} else {
			state.visibleselectfields =  this.visibleselectfields;
		}
		//blank values are permitted, so we need to check the objects before call .getValue()
		if(this.fontTypeCombo !== null)
		{	
			state.fontType = this.fontTypeCombo.getValue();
		}
		
		if(this.fontSizeCombo !== null)
		{	
			state.fontSize = this.fontSizeCombo.getValue();
		}
		
		//header font
		if(this.headerFontSizeCombo !== null)
		{	
			state.headerFontSize = this.headerFontSizeCombo.getValue();
		}
		if(this.headerFontColorText !== null)
		{	
			state.headerFontColor = this.headerFontColorText.getValue();
		}		
		if(this.headerFontWeightCombo !== null)
		{	
			state.headerFontWeight = this.headerFontWeightCombo.getValue();
		}
		if(this.headerFontDecorationCombo !== null)
		{	
			state.headerFontDecoration = this.headerFontDecorationCombo.getValue();
		}
		
		//rows font
		if(this.rowsFontSizeCombo !== null)
		{
			state.rowsFontSize = this.rowsFontSizeCombo.getValue();
		}
		if(this.rowsFontColorText !== null)
		{	
			state.rowsFontColor = this.rowsFontColorText.getValue();
		}	
		if(this.rowsFontWeightCombo !== null)
		{	
			state.rowsFontWeight = this.rowsFontWeightCombo.getValue();
		}
		if(this.rowsFontDecorationCombo !== null)
		{	
			state.rowsFontDecoration = this.rowsFontDecorationCombo.getValue();
		}
		

		// if all measures are aggregate set category and series: category are attributes, seriesare measure with aggregation function
		var atLeastOneAggregate = false;
		var areAllMeasureAggregate = true;
		var measureNumber = 0;

		for (var i = 0; i < state.visibleselectfields.length; i++) {
			var  field = state.visibleselectfields[i];
			if(field.nature == 'measure'){
				measureNumber++;
				if(field.funct != null && field.funct != 'NaN' && field.funct != '' ){
					atLeastOneAggregate = true;
				}
				if(field.funct == null || field.funct == 'NaN' || field.funct == ''){
					areAllMeasureAggregate = false;
				}
			}
		}

		if(running != undefined && running === true){
			if(atLeastOneAggregate == true && areAllMeasureAggregate==false){
				Sbi.exception.ExceptionHandler.showWarningMessage(LN("sbi.cockpit.TableWidgetDesigner.notAllMeasureAggregated"), "Warning");
				throw new Error(LN("sbi.cockpit.TableWidgetDesigner.notAllMeasureAggregated"));
			}
		}

		var toAggregate = false;
		if(measureNumber > 0 && areAllMeasureAggregate == true){
			toAggregate = true;
			state.category = new Array();
			state.series = new Array();

			// calculate category and series
			for (var i = 0; i < state.visibleselectfields.length; i++) {
				var  field = state.visibleselectfields[i];
				if(field.nature == 'attribute' || field.nature == 'segment_attribute'){
					state.category.push(field);
				}
				else if(field.nature == 'measure'){
					state.series.push(field);
				}
			}
		}


		Sbi.trace("[TableWidgetDesigner.getDesignerState]: OUT");
		return state;
	}

	, setDesignerState: function(state) {
		Sbi.trace("[TableWidgetDesigner.setDesignerState]: IN");
		Sbi.cockpit.widgets.table.TableWidgetDesigner.superclass.setDesignerState(this, state);
		if (state.fontType) this.fontTypeCombo.setValue(state.fontType);
		if (state.fontSize) this.fontSizeCombo.setValue(state.fontSize);
		//header font
		if (state.headerFontSize) this.headerFontSizeCombo.setValue(state.headerFontSize);
		if (state.headerFontColor) this.headerFontColorText.setValue(state.headerFontColor);
		if (state.headerFontWeight) this.headerFontWeightCombo.setValue(state.headerFontWeight);
		if (state.headerFontDecoration) this.headerFontDecorationCombo.setValue(state.headerFontDecoration);
		//rows font		
		if (state.rowsFontSize) this.rowsFontSizeCombo.setValue(state.rowsFontSize);
		if (state.rowsFontColor) this.rowsFontColorText.setValue(state.rowsFontColor);
		if (state.rowsFontWeight) this.rowsFontWeightCombo.setValue(state.rowsFontWeight);
		if (state.rowsFontDecoration) this.rowsFontDecorationCombo.setValue(state.rowsFontDecoration);
		
		if(state.visibleselectfields!=undefined && state.visibleselectfields!=null){
			Sbi.trace("[TableWidgetDesigner.setDesignerState]: there are [" + state.visibleselectfields.length + "] fields slected");
			this.tableDesigner.tableDesigner.setValues(state.visibleselectfields);
		} else {
			Sbi.trace("[TableWidgetDesigner.setDesignerState]: no fields selected");
		}
		
		Sbi.trace("[TableWidgetDesigner.setDesignerState]: OUT");
	}

	/* tab validity: rules are
	 * - at least one measure or attribute is in
	 */
	, validate: function(validFields){

		var valErr = Sbi.cockpit.widgets.table.TableWidgetDesigner.superclass.validate(this, validFields);
		if(valErr!= ''){
			return varErr;
		}

		valErr = ''+this.tableDesigner.validate(validFields);

		if(valErr!= ''){
			valErr = valErr.substring(0, valErr.length - 1);
			return LN("sbi.cockpit.widgets.table.validation.invalidFields")+valErr;
		}

		var vals = this.tableDesigner.tableDesigner.getContainedValues();
		if (vals && vals.length> 0) {return;} // OK
		else {
				return LN("sbi.designertable.tableValidation.noElement");
		}
	}

	, containsAttribute: function (attributeId) {
		return this.tableDesigner.containsAttribute(attributeId);
	}

	, attributeDblClickHandler : function (thePanel, attribute, theSheet) {

	}


});
