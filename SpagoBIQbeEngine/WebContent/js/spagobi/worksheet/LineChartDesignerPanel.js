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

Sbi.worksheet.LineChartDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.linechartdesignerpanel.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.lineChartDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.lineChartDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = {
		items: [this.form]
	}
	
	Sbi.worksheet.LineChartDesignerPanel.superclass.constructor.call(this, c);
	
	this.on('afterLayout', this.addToolTips, this);

};

Ext.extend(Sbi.worksheet.LineChartDesignerPanel, Ext.Panel, {

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
		        {name: 'type', height: 80, width: 80, id:'side-by-side-linechart', ctCls:'side-by-side-linechart-vertical', inputValue: 'side-by-side-linechart', checked: true},
		        {name: 'type', height: 80, width: 80, id:'stacked-linechart', ctCls:'stacked-linechart-vertical', inputValue: 'stacked-linechart'},
		        {name: 'type', height: 80, width: 80, id:'percent-stacked-linechart', ctCls:'percent-stacked-linechart-vertical', inputValue: 'percent-stacked-linechart'},
			]
		});
		this.typeRadioGroup.on('change', this.changeBarChartImage, this);
		
		this.orientationCombo = new Ext.form.ComboBox({
			mode:           'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	false,
			fieldLabel:     LN('sbi.worksheet.linechartdesignerpanel.form.orientation.title'),
			name:           'orientation',
			displayField:   'description',
			valueField:     'name',
			value:			'vertical',
			//anchor:			'95%',
			store:          new Ext.data.ArrayStore({
								fields : ['name', 'description']
								, data : [['vertical', LN('sbi.worksheet.linechartdesignerpanel.form.orientation.vertical')]
									, ['horizontal', LN('sbi.worksheet.linechartdesignerpanel.form.orientation.horizontal')]]
							})
		});
		this.orientationCombo.on('change', this.changeBarChartImage, this);
		
		this.showValuesCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.linechartdesignerpanel.form.showvalues.title')
		});
		
		this.showLegendCheck = new Ext.form.Checkbox({
			checked: false
			, fieldLabel: LN('sbi.worksheet.linechartdesignerpanel.form.showlegend.title')
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
		});
		
		this.imageContainerPanel = new Ext.Panel({
            width: 200
            , height: 120
            , html: this.imageTemplate.apply(['side-by-side-linechart', 'vertical'])
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
//							, title: LN('sbi.worksheet.linechartdesignerpanel.form.fieldsets.type')
							, columnWidth : .7
							, border: false
							, items: [this.typeRadioGroup]
						}
						, {
							xtype: 'fieldset'
//							, title: LN('sbi.worksheet.linechartdesignerpanel.form.fieldsets.options')
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
			target: 'x-form-el-side-by-side-linechart',
			html: LN('sbi.worksheet.linechartdesignerpanel.form.type.tooltip.side-by-side'),
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-stacked-linechart',
			html: LN('sbi.worksheet.linechartdesignerpanel.form.type.tooltip.stacked')
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-percent-stacked-linechart',
			html: LN('sbi.worksheet.linechartdesignerpanel.form.type.tooltip.percent-stacked')
		}, sharedConf));
	}
	
	, initTemplate: function () {
        this.imageTemplate = new Ext.Template('<div class="{0}-{1}" style="height: 100%;"></div>');
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
		state.designer = 'Line Chart';
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
