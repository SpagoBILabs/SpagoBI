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
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.LineChartDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.linechartdesignerpanel.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.lineChartDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.lineChartDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = {
		items: [this.form]
	}
	
	Sbi.worksheet.designer.LineChartDesignerPanel.superclass.constructor.call(this, c);
	
	this.on('afterLayout', this.addToolTips, this);

};

Ext.extend(Sbi.worksheet.designer.LineChartDesignerPanel, Ext.Panel, {

	form: null
	, items: null
	, typeRadioGroup: null
	, colorAreaCheck: null
	, showValuesCheck: null
	, imageTemplate: null
	, categoryContainerPanel: null
	, seriesContainerPanel: null
	, axisDefinitionPanel: null
	, showLegendCheck: null
	
	, init: function () {
		
		this.initTemplate();
		
		this.typeRadioGroup = new Ext.form.RadioGroup({
			hideLabel: true,
			columns: 3,
			items: [
		        {name: 'type', height: 80, width: 80, id:'side-by-side-linechart', ctCls:'side-by-side-linechart-line', inputValue: 'side-by-side-linechart', checked: true},
		        {name: 'type', height: 80, width: 80, id:'stacked-linechart', ctCls:'stacked-linechart-line', inputValue: 'stacked-linechart'},
		        {name: 'type', height: 80, width: 80, id:'percent-stacked-linechart', ctCls:'percent-stacked-linechart-line', inputValue: 'percent-stacked-linechart'},
			]
		});
		this.typeRadioGroup.on('change', this.changeLineChartImage, this);
		
		this.colorAreaCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.designer.linechartdesignerpanel.form.colorarea.title')
		});
		this.colorAreaCheck.on('check', this.changeLineChartImage, this);
		
		this.showValuesCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.designer.linechartdesignerpanel.form.showvalues.title')
		});
		
		this.showLegendCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.designer.linechartdesignerpanel.form.showlegend.title')
		});
		
		
		this.categoryContainerPanel = new Sbi.worksheet.designer.ChartCategoryPanel({
            width: 200
            , height: 70
            , initialData: null
            , ddGroup: this.ddGroup
		});
		
		this.seriesContainerPanel = new Sbi.worksheet.designer.ChartSeriesPanel({
            width: 400
            , height: 120
            , initialData: []
            , crosstabConfig: {}
            , ddGroup: this.ddGroup
		});
		
		this.imageContainerPanel = new Ext.Panel({
            width: 200
            , height: 120
            , html: this.imageTemplate.apply(['side-by-side-linechart', 'line'])
		});
		
	    this.axisDefinitionPanel = new Ext.Panel({
	        layout: 'table'
	        , baseCls:'x-plain'
	        , cls: 'centered-panel' //for center the panel
	        , width: this.seriesContainerPanel.width+this.imageContainerPanel.width+20 //for center the panel
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
	    

		this.form = new Ext.form.FormPanel({
			border: false
			, items: [
			    {
			    	layout: 'column'
			    	, padding: '0 10 10 10'
			    	, border: false
			    	, items: [
		  			    {
							xtype: 'fieldset'
//							, title: LN('sbi.worksheet.designer.linechartdesignerpanel.form.fieldsets.type')
							, columnWidth : .7
							, border: false
							, items: [this.typeRadioGroup]
						}
						, {
							xtype: 'fieldset'
//							, title: LN('sbi.worksheet.designer.linechartdesignerpanel.form.fieldsets.options')
							, columnWidth : .3
							, border: false
							, items: [this.colorAreaCheck, this.showValuesCheck, this.showLegendCheck]
						}
			    	]
			    }
				, this.axisDefinitionPanel
			]
		});
	}
	
	, addToolTips: function(){
		this.removeListener('afterLayout', this.addToolTips, this);
		
		var sharedConf = {
			anchor : 'top'
			, width : 200
			, trackMouse : true
		};
	
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-side-by-side-linechart',
			html: LN('sbi.worksheet.designer.linechartdesignerpanel.form.type.tooltip.side-by-side'),
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-stacked-linechart',
			html: LN('sbi.worksheet.designer.linechartdesignerpanel.form.type.tooltip.stacked')
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-percent-stacked-linechart',
			html: LN('sbi.worksheet.designer.linechartdesignerpanel.form.type.tooltip.percent-stacked')
		}, sharedConf));
	}
	
	, initTemplate: function () {
        this.imageTemplate = new Ext.Template('<div class="{0}-{1}-preview" style="height: 100%;"></div>');
        this.imageTemplate.compile();
	}
	
	, changeLineChartImage: function() {
		var type = this.typeRadioGroup.getValue().getGroupValue();
		var lineOrArea = this.colorAreaCheck.getValue() ? 'area' : 'line';
		var newHtml = this.imageTemplate.apply([type, lineOrArea]);
		this.imageContainerPanel.update(newHtml);
	}
	
	, getFormState: function() {
		var state = {};
		state.designer = 'Line Chart';
		state.type = this.typeRadioGroup.getValue().getGroupValue();
		state.colorarea = this.colorAreaCheck.getValue();
		state.showvalues = this.showValuesCheck.getValue();
		state.showlegend = this.showLegendCheck.getValue();
		state.category = this.categoryContainerPanel.getCategory();
		state.series = this.seriesContainerPanel.getContainedMeasures();
		return state;
	}
	
	, setFormState: function(state) {
		if (state.type) this.typeRadioGroup.setValue(state.type);
		if (state.colorarea) this.colorAreaCheck.setValue(state.colorarea);
		if (state.showvalues) this.showValuesCheck.setValue(state.showvalues);
		if (state.showlegend) this.showLegendCheck.setValue(state.showlegend);
		if (state.category) this.categoryContainerPanel.setCategory(state.category);
		if (state.series) this.seriesContainerPanel.setMeasures(state.series);
	}

});
