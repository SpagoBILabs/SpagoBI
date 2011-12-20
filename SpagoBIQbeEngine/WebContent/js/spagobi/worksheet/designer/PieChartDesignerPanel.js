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
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.PieChartDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.piechartdesignerpanel.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.pieChartDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.pieChartDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.addEvents("attributeDblClick", "attributeRemoved");
	
	this.init();
	
	c = {
		items: [this.form]
	};
	
	Sbi.worksheet.designer.PieChartDesignerPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.worksheet.designer.PieChartDesignerPanel, Ext.Panel, {

	form: null
	, items: null
	, showValuesCheck: null
	, categoryContainerPanel: null
	, seriesContainerPanel: null
	, axisDefinitionPanel: null
	, showLegendCheck: null
	, showPercentageCheck: null
	, seriesPalette: null
	
	, init: function () {
		
		this.showValuesCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.designer.piechartdesignerpanel.form.showvalues.title')
		});
		
		this.showLegendCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.designer.piechartdesignerpanel.form.showlegend.title')
		});
		
		this.showPercentageCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.designer.piechartdesignerpanel.form.showpercentage.title')
		});
		
		this.categoryContainerPanel = new Sbi.worksheet.designer.ChartCategoryPanel({
            width: 200
            , height: 70
            , initialData: null
            , ddGroup: this.ddGroup
            , tools: [{
            	id: 'list'
  	        	, handler: function() {
					this.seriesPalette.show();
				}
  	          	, scope: this
  	          	, qtip: LN('sbi.worksheet.designer.piechartdesignerpanel.categorypalette.title')
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
		
		this.seriesContainerPanel = new Sbi.worksheet.designer.ChartSeriesPanel({
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
	    
		this.seriesPalette = new Sbi.widgets.SeriesPalette({
			title: LN('sbi.worksheet.designer.piechartdesignerpanel.categorypalette.title')
			, height: 300
			, width: 150
			, closeAction: 'hide'
		});
	    
		this.form = new Ext.Panel({
			border: false
			, layout: 'form'
			, items: [
				{
					xtype: 'form'
					, style: 'padding: 10px 0px 0px 15px;'
				//	, title: LN('sbi.worksheet.designer.barchartdesignerpanel.form.fieldsets.options')
					, border: false
					, items: [this.showValuesCheck, this.showLegendCheck, this.showPercentageCheck]
				}
				, 
				this.axisDefinitionPanel
			]
		});
	}

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
	, validate: function(){
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
