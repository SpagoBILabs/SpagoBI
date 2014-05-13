/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/


Ext.define('Sbi.cockpit.widgets.piechart.PieChartWidgetDesigner', {
	extend: 'Sbi.cockpit.core.WidgetDesigner'
//	, layout:'fit'

	, config:{
		  title: LN('sbi.cockpit.widgets.piechartwidgetdesigner.title')
		, border: false
		, ddGroup: null
	}

	, form: null
	, items: null
	, showValuesCheck: null
	, categoryContainerPanel: null
	, seriesContainerPanel: null
	, axisDefinitionPanel: null
	, showLegendCheck: null
	, legendPositionCombo: null
	, showPercentageCheck: null
	, seriesPalette: null
	, chartLib: null

	, constructor : function(config) {
		Sbi.trace("[PieChartWidgetDesigner.constructor]: IN");
		this.initConfig(config);	
		this.init(config);
		this.callParent(arguments);
		this.initEvents();					
		Sbi.trace("[PieChartWidgetDesigner.constructor]: OUT");
	}
	
	, initComponent: function() {
  
        Ext.apply(this, {
            items: [this.form]
          , title: LN('sbi.cockpit.widgets.piechartwidgetdesigner.title')
		  , border: false
        });
        
        this.callParent();
    }

	
	
	// =================================================================================================================
	// METHODS
	// =================================================================================================================

	// -----------------------------------------------------------------------------------------------------------------
	// init methods
	// -----------------------------------------------------------------------------------------------------------------
	, initEvents: function(){
		this.on(
				'beforerender' , 
				function (thePanel, attribute) {					
					var state = {};
					state.showvalues = thePanel.showvalues;
					state.showlegend = thePanel.showlegend;
					state.legendPosition = thePanel.legendPosition;
					state.showpercentage = thePanel.showpercentage;
					state.category = thePanel.category;
					state.series = thePanel.series;
					state.colors = thePanel.colors;
					state.wtype = 'piechart';
					this.setDesignerState(state);
				}, 
				this
			);
		this.addEvents("attributeDblClick", "attributeRemoved");
	}

	, init: function () {
		this.chartLib = 'ext3'; //default
		
		if (Sbi.settings && Sbi.settings.cockpit && Sbi.settings.cockpit.chartlib) {
			this.chartLib = Sbi.settings.cockpit.chartlib;
		}
		this.chartLib = this.chartLib.toLowerCase();
		
		this.initTemplate();
		
		this.showValuesCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.showvalues.title')
		});
		
		this.showLegendCheck = new Ext.form.Checkbox({
			checked: false
			, width: 200	
			, fieldLabel: LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.showlegend.title')
		});
		
		this.legendPositionStore = new Ext.data.ArrayStore({
			fields : ['name', 'description']
			, data : [['bottom', LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.legend.position.bottom')]
					, ['top', LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.legend.position.top')]
					, ['left', LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.legend.position.left')]
					, ['right', LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.legend.position.right')]]
		});
		this.legendPositionCombo = new Ext.form.ComboBox({
			width:			200,
			queryMode:      'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	false,
			fieldLabel:      LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.legend.position.title'),
			name:           'position',
			displayField:   'description',
			valueField:     'name',
			value:			'bottom',
			store:          this.legendPositionStore
		});
		
		this.showPercentageCheck = new Ext.form.Checkbox({
			checked: false
			, width: 200	
			, fieldLabel: LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.showpercentage.title')
		});
		
		this.seriesPalette = new Sbi.cockpit.widgets.chart.SeriesPalette({
			title: LN('sbi.cockpit.widgets.piechartwidgetdesigner.categorypalette.title')
			, height: 300
			, width: 150
			, closeAction: 'hide'
		});
		
		this.categoryContainerPanel = new Sbi.cockpit.widgets.chart.ChartCategoryPanel({
            width: 200
            , height: 70
            , initialData: null
            , ddGroup: this.ddGroup
            , tools: [{
            	  id: 'list'
                , type: 'collapse'
  	        	, handler: function() {
					this.seriesPalette.show();
				}
  	          	, scope: this
  	          	, qtip: LN('sbi.cockpit.widgets.piechartwidgetdesigner.categorypalette.title')
            }]
		});
		
		// propagate events
		this.categoryContainerPanel.on(
			'attributeDblClick' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeDblClick", this, attribute); 
			}, 
			this
		);
		this.categoryContainerPanel.on(
			'attributeRemoved' , 
			function (thePanel, attribute) { 
				this.fireEvent("attributeRemoved", this, attribute); 
			}, 
			this
		);
		
		this.seriesContainerPanel = new Sbi.cockpit.widgets.chart.ChartSeriesPanel({
            width: 430
            , height: 120
            , initialData: []
            , crosstabConfig: {}
            , ddGroup: this.ddGroup
            , displayColorColumn: false
		});
		
		this.imageContainerPanel = new Ext.Panel({
            width: 200
            , height: 120
            , html: '<div class="piechart" style="height: 100%;"></div>'
		});
		
	    this.axisDefinitionPanel = new Ext.Panel({
	        layout: 'table'
	        , baseCls:'x-plain'
		    , cls: 'centered-panel' //for center the panel
			, width: this.seriesContainerPanel.width+this.imageContainerPanel.width+20 //for center the panel
	        , padding: '0 10 10 10'
	        , layoutConfig: {columns : 2}
	        // applied to child components
	        //, defaults: {height: 100}
	        , items:[
	            this.seriesContainerPanel
	            , this.imageContainerPanel 
	            , {
		        	border: false
		        }
		        , this.categoryContainerPanel
		    ]
	    });
	    
	
	    
		var controlsItems = new Array();
		
		switch (this.chartLib) {
	        case 'ext3':
	        	break;
	        default: 
	        	controlsItems.push(this.showValuesCheck);
		} 
		
    	controlsItems.push(this.showLegendCheck);   	
    	controlsItems.push(this.legendPositionCombo);
//    	controlsItems.push(this.showPercentageCheck);
		
		this.form = new Ext.Panel({
			border: false
			, layout: 'form'
		    , padding: '10 10 10 10'
			, items: [
				{
					xtype: 'fieldset'
					, bodyStyle:'padding:5px!important;'
					, layout: 'column'
					, columnWidth : .4
					, style: 'padding: 10px 0px 0px 15px;'
					, border: false
					, items: controlsItems
				}
				,{
					xtype: 'fieldset'
					, layout: 'column'
					, columnWidth : .9
					, style: 'padding: 10px 0px 0px 15px;'
					, border: false
					, items: [this.showPercentageCheck]
				}			
				, 
				this.axisDefinitionPanel
			]
		});
	}

	, initTemplate: function () {
	    this.imageTemplate = new Ext.Template('<div class="{0}-{1}-preview" style="height: 100%;"></div>');
	    this.imageTemplate.compile();
	}


	//-----------------------------------------------------------------------------------------------------------------
	//public methods
	//-----------------------------------------------------------------------------------------------------------------
	, getDesignerState: function() {		
		Sbi.trace("[PieChartWidgetDesigner.getDesignerState]: IN");
		Sbi.trace("[PieChartWidgetDesigner.getDesignerState]: " + Sbi.cockpit.widgets.piechart.PieChartWidgetDesigner.superclass.getDesignerState);
		var state = Sbi.cockpit.widgets.piechart.PieChartWidgetDesigner.superclass.getDesignerState(this);
		state.designer = 'Pie Chart';
		state.wtype = 'piechart';
		state.showvalues = this.showValuesCheck.getValue();
		state.showlegend = this.showLegendCheck.getValue();
		state.legendPosition = this.legendPositionCombo.getValue();
		state.showpercentage = this.showPercentageCheck.getValue();
		state.category = this.categoryContainerPanel.getCategory();
		state.series = this.seriesContainerPanel.getContainedMeasures();
		state.colors = this.seriesPalette.getColors();
		Sbi.trace("[PieChartWidgetDesigner.getDesignerState]: OUT");
		return state;
	}
	
	, setDesignerState: function(state) {
		Sbi.trace("[PieChartWidgetDesigner.setDesignerState]: IN");
		Sbi.cockpit.widgets.piechart.PieChartWidgetDesigner.superclass.setDesignerState(this, state);
		if (state.showvalues) this.showValuesCheck.setValue(state.showvalues);
		if (state.showlegend) this.showLegendCheck.setValue(state.showlegend);
		if (state.legendPosition) this.legendPositionCombo.setValue(state.legendPosition);
		if (state.showpercentage) this.showPercentageCheck.setValue(state.showpercentage);
		if (state.category) this.categoryContainerPanel.setCategory(state.category);
		if (state.series) this.seriesContainerPanel.setMeasures(state.series);
		if (state.colors) this.seriesPalette.setColors(state.colors);
		Sbi.trace("[PieChartWidgetDesigner.setDesignerState]: OUT");
	}
	
	, validate: function(validFields){
		var valErr='';	
		valErr+=''+this.categoryContainerPanel.validate(validFields);
		valErr+=''+this.seriesContainerPanel.validate(validFields);
		
		if(valErr!= ''){
			valErr = valErr.substring(0, valErr.length - 1);
			return LN("sbi.cockpit.designer.validation.invalidFields")+valErr;
		}
		
		if (this.categoryContainerPanel.category== null){
			return LN("sbi.designerchart.chartValidation.noCategory");
		}
		var store = this.seriesContainerPanel.store;
		var seriesCount = store.getCount();
		if(seriesCount == 0 ){
			return LN("sbi.designerchart.chartValidation.noSeries");
		}
		

		
		return; 

	}
	
	, containsAttribute: function (attributeId) {
		if (this.categoryContainerPanel.category == null) {
			return false;
		} else {
			return this.categoryContainerPanel.category.id == attributeId;
		}
	}
	
});