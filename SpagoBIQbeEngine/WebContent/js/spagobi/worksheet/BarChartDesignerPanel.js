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

Sbi.worksheet.BarChartDesignerPanel = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.barchartdesignerpanel.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.barChartDesignerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.barChartDesignerPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	this.init();
	
	c = {
		items: [this.form]
	}
	
	Sbi.worksheet.BarChartDesignerPanel.superclass.constructor.call(this, c);
	
	this.on('afterLayout', this.addToolTips, this);

};

Ext.extend(Sbi.worksheet.BarChartDesignerPanel, Ext.Panel, {

	form: null
	, items: null
	, typeRadioGroup: null
	, orientationCombo: null
	, imageTemplate: null
	
	, init: function () {
		
		this.initTemplate();
		
		this.typeRadioGroup = new Ext.form.RadioGroup({
			hideLabel: true,
			columns: 3,
			items: [
		        {name: 'type', height: 80, width: 80, id:'side-by-side-barchart', ctCls:'side-by-side-barchart', inputValue: 'side-by-side-barchart', checked: true},
		        {name: 'type', height: 80, width: 80, id:'stacked-barchart', ctCls:'stacked-barchart', inputValue: 'stacked-barchart'},
		        {name: 'type', height: 80, width: 80, id:'percent-stacked-barchart', ctCls:'percent-stacked-barchart', inputValue: 'percent-stacked-barchart'},
			]
		});
		this.typeRadioGroup.on('change', this.changeBarChartImage, this);
		
		this.orientationCombo = new Ext.form.ComboBox({
			mode:           'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	false,
			fieldLabel:     LN('sbi.worksheet.barchartdesignerpanel.form.orientation.title'),
			name:           'orientation',
			displayField:   'description',
			valueField:     'name',
			value:			'vertical',
			//anchor:			'95%',
			store:          new Ext.data.ArrayStore({
								fields : ['name', 'description']
								, data : [['vertical', LN('sbi.worksheet.barchartdesignerpanel.form.orientation.vertical')]
									, ['horizontal', LN('sbi.worksheet.barchartdesignerpanel.form.orientation.horizontal')]]
							})
		});
		
		
		this.categoryContainerPanel = new Sbi.worksheet.ChartCategoryPanel({
            width: 200
            , height: 50
            , initialData: null
            , ddGroup: this.ddGroup
		});
		
		this.seriesContainerPanel = new Sbi.worksheet.ChartSeriesPanel({
            width: 200
            , initialData: []
            , crosstabConfig: {}
            , ddGroup: this.ddGroup
		});
		
		this.imageContainerPanel = new Ext.Panel({
            width: 200
            , height: 200
            , html: this.imageTemplate.apply(['side-by-side-barchart'])
		});
		
	    this.axisDefinitionPanel = new Ext.Panel({
	        layout: 'table'
	        , baseCls:'x-plain'
	        , padding: '30 30 30 100'
	        , layoutConfig: {columns : 2}
	        // applied to child components
	        , defaults: {height: 100}
	        , items:[
	            this.seriesContainerPanel
	            , this.imageContainerPanel 
	            , {
		        	border: false
		        }
		        , this.categoryContainerPanel
		        
		        
		    ]
	    });
	    
		var items = [this.typeRadioGroup, this.orientationCombo, this.axisDefinitionPanel];
		this.form = new Ext.form.FormPanel({
			items: items
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
			html: LN('sbi.worksheet.barchartdesignerpanel.form.type.tooltip.side-by-side'),
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-stacked-barchart',
			html: LN('sbi.worksheet.barchartdesignerpanel.form.type.tooltip.stacked')
		}, sharedConf));
		new Ext.ToolTip(Ext.apply({
			target: 'x-form-el-percent-stacked-barchart',
			html: LN('sbi.worksheet.barchartdesignerpanel.form.type.tooltip.percent-stacked')
		}, sharedConf));
	}

	, onFieldDrop: function(ddSource) {
		alert('drop');
	}
	
	, initTemplate: function () {
        this.imageTemplate = new Ext.Template('<div class="{0}" style="height: 100%;"></div>');
        this.imageTemplate.compile();
	}
	
	, changeBarChartImage: function(radioGroup, radioChecked) {
		var value = radioChecked.getGroupValue();
		var newHtml = this.imageTemplate.apply([value]);
		this.imageContainerPanel.update(newHtml);
	}

});
