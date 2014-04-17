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
	, showPercentageCheck: null
	, seriesPalette: null
	, chartLib: null

	, constructor : function(config) {
		Sbi.trace("[PieChartWidgetDesigner.constructor]: IN");
		this.initConfig(config);
		this.initEvents();
		this.init(config);
		this.callParent(arguments);
		this.addEvents("attributeDblClick", "attributeRemoved");
		Sbi.trace("[PieChartWidgetDesigner.constructor]: OUT");
	}
	
	, initComponent: function() {
  
        Ext.apply(this, {
            items: [this.form]
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
			, fieldLabel: LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.showlegend.title')
		});
		
		this.showPercentageCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.cockpit.widgets.piechartwidgetdesigner.form.showpercentage.title')
		});
		
//		this.categoryContainerPanel = new Sbi.worksheet.designer.ChartCategoryPanel({
//            width: 200
//            , height: 70
//            , initialData: null
//            , ddGroup: this.ddGroup
//            , tools: [{
//            	id: 'list'
//  	        	, handler: function() {
//					this.seriesPalette.show();
//				}
//  	          	, scope: this
//  	          	, qtip: LN('sbi.worksheet.designer.piechartdesignerpanel.categorypalette.title')
//            }]
//		});
		this.categoryContainerPanel = new Sbi.cockpit.widgets.chart.ChartCategoryPanel({
            width: 200
            , height: 70
            , initialData: null
            , ddGroup: this.ddGroup
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
		
//		this.seriesContainerPanel = new Sbi.worksheet.designer.ChartSeriesPanel({
//            width: 430
//            , height: 120
//            , initialData: []
//            , crosstabConfig: {}
//            , ddGroup: this.ddGroup
//            , displayColorColumn: false
//		});
		this.seriesContainerPanel = new Sbi.cockpit.widgets.chart.ChartSeriesPanel({
            width: 430
            , height: 120
            , initialData: []
            , crosstabConfig: {}
            , ddGroup: this.ddGroup
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
	    
		this.seriesPalette = new Sbi.cockpit.widgets.chart.SeriesPalette({
			title: LN('sbi.cockpit.widgets.piechartwidgetdesigner.categorypalette.title')
			, height: 300
			, width: 150
			, closeAction: 'hide'
		});
	    
		var controlsItems = new Array();
		
		switch (this.chartLib) {
	        case 'ext3':
	        	break;
	        default: 
	        	controlsItems.push(this.showValuesCheck);
		} 
		
    	controlsItems.push(this.showLegendCheck);
    	controlsItems.push(this.showPercentageCheck);
		
		this.form = new Ext.Panel({
			border: false
			, layout: 'form'
			, items: [
				{
					xtype: 'form'
					, style: 'padding: 10px 0px 0px 15px;'
				//	, title: LN('sbi.worksheet.designer.barchartdesignerpanel.form.fieldsets.options')
					, border: false
					, items: controlsItems
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
	, getFormState: function() {
		var state = {};
		state.designer = 'Pie Chart';
		state.showvalues = this.showValuesCheck.getValue();
		state.showlegend = this.showLegendCheck.getValue();
		state.showpercentage = this.showPercentageCheck.getValue();
		state.category = this.categoryContainerPanel.getCategory();
		state.series = this.seriesContainerPanel.getContainedMeasures();
		state.colors = this.seriesPalette.getColors();
		return state;
	}
	
	, setFormState: function(state) {
		if (state.showvalues) this.showValuesCheck.setValue(state.showvalues);
		if (state.showlegend) this.showLegendCheck.setValue(state.showlegend);
		if (state.showpercentage) this.showPercentageCheck.setValue(state.showpercentage);
		if (state.category) this.categoryContainerPanel.setCategory(state.category);
		if (state.series) this.seriesContainerPanel.setMeasures(state.series);
		if (state.colors) this.seriesPalette.setColors(state.colors);
	}
	, validate: function(validFields){
		var valErr='';	
		valErr+=''+this.categoryContainerPanel.validate(validFields);
		valErr+=''+this.seriesContainerPanel.validate(validFields);
		
		if(valErr!= ''){
			valErr = valErr.substring(0, valErr.length - 1)
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