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
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet");

Sbi.worksheet.PieChartDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.piechartdesignerpanel.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.pieChartDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.pieChartDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = {
		items: [this.form]
	}
	
	Sbi.worksheet.PieChartDesignerPanel.superclass.constructor.call(this, c);

};

Ext.extend(Sbi.worksheet.PieChartDesignerPanel, Ext.Panel, {

	form: null
	, items: null
	, showValuesCheck: null
	, categoryContainerPanel: null
	, seriesContainerPanel: null
	, axisDefinitionPanel: null
	, showLegendCheck: null
	, seriesPalette: null
	
	, init: function () {
		
		this.showValuesCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.piechartdesignerpanel.form.showvalues.title')
		});
		
		this.showLegendCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.piechartdesignerpanel.form.showlegend.title')
		});
		
		this.categoryContainerPanel = new Sbi.worksheet.ChartCategoryPanel({
            width: 200
            , height: 70
            , initialData: null
            , ddGroup: this.ddGroup
		});
		
		this.seriesContainerPanel = new Sbi.worksheet.ChartSeriesPanel({
            width: 400
            , height: 120
            , initialData: []
            , crosstabConfig: {}
            , ddGroup: this.ddGroup
            , displayColourColumn: false
		});
		
		this.imageContainerPanel = new Ext.Panel({
            width: 200
            , height: 120
            , html: '<div class="piechart" style="height: 100%;"></div>'
		});
		
	    this.axisDefinitionPanel = new Ext.Panel({
	        layout: 'table'
	        , baseCls:'x-plain'
	        , padding: '10 10 10 10'
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
	    
		this.seriesPalette = new Sbi.worksheet.SeriesPalette({
			height: 300
			, width: 150
			, closeAction: 'hide'
		});
	    
		this.form = new Ext.form.FormPanel({
			border: false
			, items: [
				{
					xtype: 'fieldset'
				//	, title: LN('sbi.worksheet.barchartdesignerpanel.form.fieldsets.options')
					, border: false
					, items: [this.showValuesCheck, this.showLegendCheck]
				}
				, this.axisDefinitionPanel
				, {
					padding: '10 10 10 10'
					, border: false
					, items: new Ext.Button({
						text: LN('sbi.worksheet.seriespalette.title')
				        , handler: function() {
							this.seriesPalette.show();
						} 
						, scope: this
					})
				}
			]
		});
	}

	, getFormState: function() {
		var state = {};
		state.designer = 'Pie Chart';
		state.showvalues = this.showValuesCheck.getValue();
		state.showlegend = this.showLegendCheck.getValue();
		state.category = this.categoryContainerPanel.getCategory();
		state.series = this.seriesContainerPanel.getContainedMeasures();
		return state;
	}
	
	, setFormState: function(state) {
		if (state.showvalues) this.showValuesCheck.setValue(state.showvalues);
		if (state.showlegend) this.showLegendCheck.setValue(state.showlegend);
		if (state.category) this.categoryContainerPanel.setCategory(state.category);
		if (state.series) this.seriesContainerPanel.setMeasures(state.series);
	}

});
