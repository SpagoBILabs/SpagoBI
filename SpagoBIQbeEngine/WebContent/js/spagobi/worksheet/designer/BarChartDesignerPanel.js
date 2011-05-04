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

Sbi.worksheet.designer.BarChartDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.barchartdesignerpanel.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.barChartDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.barChartDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = {
		items: [this.form]
	}
	
	Sbi.worksheet.designer.BarChartDesignerPanel.superclass.constructor.call(this, c);
	
	this.on('afterLayout', this.addToolTips, this);

};

Ext.extend(Sbi.worksheet.designer.BarChartDesignerPanel, Ext.Panel, {

	form: null
	, items: null
	, typeRadioGroup: null
	, orientationCombo: null
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
		        {name: 'type', height: 80, width: 80, id:'side-by-side-barchart', ctCls:'side-by-side-barchart-vertical', inputValue: 'side-by-side-barchart', checked: true},
		        {name: 'type', height: 80, width: 80, id:'stacked-barchart', ctCls:'stacked-barchart-vertical', inputValue: 'stacked-barchart'},
		        {name: 'type', height: 80, width: 80, id:'percent-stacked-barchart', ctCls:'percent-stacked-barchart-vertical', inputValue: 'percent-stacked-barchart'},
			]
		});
		this.typeRadioGroup.on('change', this.changeBarChartImage, this);
		
		this.orientationCombo = new Ext.form.ComboBox({
			mode:           'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	false,
			fieldLabel:     LN('sbi.worksheet.designer.barchartdesignerpanel.form.orientation.title'),
			name:           'orientation',
			displayField:   'description',
			valueField:     'name',
			value:			'vertical',
			//anchor:			'95%',
			store:          new Ext.data.ArrayStore({
								fields : ['name', 'description']
								, data : [['vertical', LN('sbi.worksheet.designer.barchartdesignerpanel.form.orientation.vertical')]
									, ['horizontal', LN('sbi.worksheet.designer.barchartdesignerpanel.form.orientation.horizontal')]]
							})
		});
		this.orientationCombo.on('change', this.changeBarChartImage, this);
		
		this.showValuesCheck = new Ext.form.Checkbox({
			name: 'showvalues'
			, checked: false
			, fieldLabel: LN('sbi.worksheet.designer.barchartdesignerpanel.form.showvalues.title')
		});
		
		this.showLegendCheck = new Ext.form.Checkbox({
			name: 'showlegend'
			, checked: false
			, fieldLabel: LN('sbi.worksheet.designer.barchartdesignerpanel.form.showlegend.title')
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
            , html: this.imageTemplate.apply(['side-by-side-barchart', 'vertical'])
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
	    
	    
		this.form = new Ext.form.FormPanel({
			border: false
			, items: [
			    {
			    	layout: 'column'
			    	, padding: '10 10 10 10'
			    	, border: false
			    	, items: [
		  			    {
							xtype: 'fieldset'
//							, title: LN('sbi.worksheet.designer.barchartdesignerpanel.form.fieldsets.type')
							, columnWidth : .7
							, border: false
							, items: [this.typeRadioGroup]
						}
						, {
							xtype: 'fieldset'
//							, title: LN('sbi.worksheet.designer.barchartdesignerpanel.form.fieldsets.options')
							, columnWidth : .3
							, border: false
							, items: [this.orientationCombo, this.showValuesCheck, this.showLegendCheck]
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
			target: 'x-form-el-side-by-side-barchart',
			html: LN('sbi.worksheet.designer.barchartdesignerpanel.form.type.tooltip.side-by-side'),
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-stacked-barchart',
			html: LN('sbi.worksheet.designer.barchartdesignerpanel.form.type.tooltip.stacked')
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-percent-stacked-barchart',
			html: LN('sbi.worksheet.designer.barchartdesignerpanel.form.type.tooltip.percent-stacked')
		}, sharedConf));
	}
	
	, initTemplate: function () {
        this.imageTemplate = new Ext.Template('<div class="{0}-{1}-preview" style="height: 100%;"></div>');
        this.imageTemplate.compile();
	}
	
	, changeBarChartImage: function() {
		var type = this.typeRadioGroup.getValue().getGroupValue();
		var orientation = this.orientationCombo.getValue();
		var newHtml = this.imageTemplate.apply([type, orientation]);
		this.imageContainerPanel.update(newHtml);
	}
	
	, getFormState: function() {
		var state = {};
		state.designer = 'Bar Chart';
		state.type = this.typeRadioGroup.getValue().getGroupValue();
		state.orientation = this.orientationCombo.getValue();
		state.showvalues = this.showValuesCheck.getValue();
		state.showlegend = this.showLegendCheck.getValue();
		state.category = this.categoryContainerPanel.getCategory();
		state.series = this.seriesContainerPanel.getContainedMeasures();
		return state;
	}
	
	, setFormState: function(state) {
		if (state.type) this.typeRadioGroup.setValue(state.type);
		if (state.orientation) this.orientationCombo.setValue(state.orientation);
		if (state.showvalues) this.showValuesCheck.setValue(state.showvalues);
		if (state.showlegend) this.showLegendCheck.setValue(state.showlegend);
		if (state.category) this.categoryContainerPanel.setCategory(state.category);
		if (state.series) this.seriesContainerPanel.setMeasures(state.series);
	}

});
