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
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUIDetail =  function(config) {
	
		this.customChartName= config.customChartName;
		var defaultSettings = {};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.kpiGUIDetail) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.kpiGUIDetail);
		}

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
		
		this.initDetail(c);
   
		Sbi.kpi.KpiGUIDetail.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiGUIDetail , Ext.form.FormPanel, {
	items: null,
	threshFields: null,
	valueItem: null,
	weightItem: null,
	targetItem: null,
	chartPanel : null,
	chartid: null,
	//chart
	dial: null,
	maxChartValue: 0,
	ranges: new Array(),
	customChartName: null,
	//custom chart
	selectedThr: null,
	
	initDetail: function(){	
		this.chartid = Ext.id();
		
		this.defaultType= 'label';
		this.style= 'padding-left:10px;';
		this.border=false;
		
		
		this.chartPanel = new Ext.Panel({
			id: this.chartid,
			border: false,
			html: '&nbsp;',
			height: 5
		});

		this.threshFields = new Ext.form.FieldSet({
	        // Fieldset thresholds
	        xtype:'fieldset',
	        border: false,
	        width:350,
	        defaultType: 'fieldset',
	        items: []
	    });
		if(Ext.isIE && this.customChartName === undefined || this.customChartName == null || this.customChartName === 'null'){
			this.threshFields.style = 'margin-top: 150px;';

		}
		this.items =[this.chartPanel, this.threshFields];
		
	}
	, calculateMax: function(threshold){
		if(threshold.max > this.maxChartValue){
			this.maxChartValue = threshold.max;
		}
	}
	, calculateRange: function(thr){
		var range = {from: thr.min, to: thr.max, color: thr.color};
		this.ranges.push(range);
	}
	, drawChart: function(value){
		if(this.dial == null){
			// Build the dial
			this.dial = drawDial({
			    renderTo: this.chartid,
			    value: value,
			    centerX: 150,
			    centerY: 130,
			    min: 0,
			    max: this.maxChartValue,
			    
			    minAngle: -Math.PI,
			    maxAngle: 0,
			    tickInterval: 20,
			    ranges: this.ranges
			    , pivotLength: 70 //arrow length
			    , backgroundRadius: 120
			    , arcMinRadius : 70
			    , arcMaxRadius : 100
			    , textRadius : 105
			    , renderX : 300 //width of the area of the chart
			    , renderY : 145 //height of the area of the chart
			});
		}else{
			this.dial.setMax(this.maxChartValue);			
			this.dial.setRanges(this.ranges);
			this.dial.setTicks(this.maxChartValue);
			this.dial.setValue(value);
			this.dial.setCircle();
		}
		
	}
	, cleanPanel: function(){
		if(this.chartPanel != null){
			this.chartPanel.removeAll(true);
			
		}
		if(this.threshFields != null){
			this.threshFields.removeAll();
		}
		if(this.valueItem != null){
			this.remove(this.valueItem);
		}
		if(this.weightItem != null){
			this.remove(this.weightItem);
		}
		if(this.targetItem != null){
			this.remove(this.targetItem);
		}
		this.maxChartValue =0;
		this.ranges = new Array();
	}
	, update:  function(field){

		//chart
		var val = field.attributes.actual;
		if(this.customChartName === undefined || this.customChartName == null || this.customChartName === 'null'){
			this.on('afterlayout',function(){
				this.drawChart(val);
			},this);
		}else{
			var x = this.calculateInnerThrChart(field);
			this.chartPanel.setHeight(180);
			var html = '<div style="float: left; margin-left:20px; text-align:center; background-color:'+field.attributes.status+'; height=180px; width: 200px;"><img src="../themes/other_theme/img/'+this.customChartName+'_'+x+'.png"></img></div>'
			+ '<div style="margin-top: 30px; float: left; background-color:white; width: 100px; padding-left:5px; font-style: italic; font-weight: bold;"> Valore: '+val+'</div>';
			this.chartPanel.update(html);
			this.doLayout();
		}
		
		this.cleanPanel();
		//value
		this.valueItem = new Ext.form.DisplayField({fieldLabel: 'Valore', 
													value: val, 
													style: 'padding-left:5px; font-style: italic; font-weight: bold;'});
		this.add(this.valueItem );
		
		//thresholds

		var thrArray = field.attributes.thresholds;

		if(thrArray != undefined && thrArray !== null){

			for(i=0; i<thrArray.length; i++){
				var bItems = [];
				var thrLine = new Ext.form.FieldSet({
			        xtype:'fieldset',			        
			        style: 'padding:0px; margin: 1px;',
			        border:false,
			        layout: 'column',
			        autoHeight:true,
			        minHeight:20,
			        defaults: {
			            anchor: '0'
			        },
			        defaultType: 'displayfield',
			        items : bItems
			    });
				
				
				var thr = thrArray[i];	
				var colorBox = new Ext.form.DisplayField({value: thr.color, 
															columnWidth:0.3,
															style: 'background-color:'+thr.color});
				
				thrLine.add([ {value: thr.label, columnWidth:0.3, style:'font-weight: bold;'}, 
				              {columnWidth:0.2, value: thr.min }, 
				              {value: thr.max, columnWidth:0.2}, 
				              colorBox]);
				
				this.threshFields.add(thrLine);
				//calculate chart options
				this.calculateMax(thr);
				this.calculateRange(thr);

			}
			this.threshFields.doLayout();
		}

		//weight
		var weight = field.attributes.weight;
		this.weightItem = new Ext.form.DisplayField({fieldLabel: 'Peso',
													style: 'margin-left:5px;',
													value: weight});
		this.add(this.weightItem );
		//target
		var target = field.attributes.target;
		this.targetItem = new Ext.form.DisplayField({fieldLabel: 'Target', 
													style: 'padding-left:5px;',
													value: target});
		this.add(this.targetItem );
		this.doLayout();
        this.render();
	}
	, calculateInnerThrChart: function(field){
		var val = parseFloat(field.attributes.actual) ;
		var thrArray = field.attributes.thresholds;

		if(thrArray != undefined && thrArray !== null){
			var valColor = field.attributes.status;
			for(i=0; i<thrArray.length; i++){
				var thr = thrArray[i];	
				if(valColor !== undefined && valColor !== null){
					if(thr.color === valColor){
						//selected threshold
						this.selectedThr = thr;
					}
				}
			}
		}
		var total = parseFloat(this.selectedThr.max) - parseFloat(this.selectedThr.min);
		//total/9 = val/x;
		var sbtrct = val - parseFloat(this.selectedThr.min)
		var x = Math.round(9*sbtrct/total);
		return x;
	}
});