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
	titleItem: null,
	chartid: null,
	//chart
	dial: null,
	maxChartValue: 0,
	ranges: new Array(),
	customChartName: null,
	//custom chart
	selectedThr: null,
	val: null,
	
	initDetail: function(){	
		this.chartid = Ext.id();
		
		this.defaultType= 'label';
		this.style= 'padding-left:10px; padding-top:10px;';
		this.border = false;

		//title
		this.titleItem = new Ext.form.DisplayField({
			style: 'text-align: left; font-weight: bold; width: 100%; '});
		
		this.chartPanel = new Ext.Panel({
			id: this.chartid,
			border: false,
			html: '&nbsp;',
			width: 300,
			style: 'float:left; margin-bottom: 10px; margin-top: 5px;',
			height: 5
			, items: []
		});
		if(Ext.isIE && (this.customChartName !== undefined && this.customChartName != null && this.customChartName !== 'null')){
			this.chartPanel.style = 'float:left; margin-bottom: 10px; margin-top: 15px;';
		}
		this.detailFields = new Ext.form.FieldSet({
	        xtype:'fieldset',
	        border: false,
	        width:300,
	        style: 'padding: 5px; margin-top: 20px;float:left; margin-left:7px;',
	        defaultType: 'displayfield',
	        labelWidth: 30,
	        //layout: 'hbox',
	        items: []
	    });
		this.threshFields = new Ext.form.FieldSet({
	        // Fieldset thresholds
	        xtype:'fieldset',
	        border: false,
	        width:300,
	        
	        defaultType: 'fieldset',
	        style: 'float:left;',
	        items: []
	    });

		if(Ext.isIE && (this.customChartName === undefined || this.customChartName == null || this.customChartName === 'null')){
			this.threshFields.style = 'margin-top: 50px; padding: 5px; float:left; margin-left: 10px;';
		}else{
			this.threshFields.style = 'margin-top: 18px; padding: 5px; float:left; margin-left: 10px;';
		}

		this.items =[this.titleItem, this.chartPanel, this.detailFields, this.threshFields];
		
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
		var y = 130;
		if(Ext.isIE && (this.customChartName === undefined || this.customChartName == null || this.customChartName === 'null')){
			y = 155;
		}
		if(this.dial == null){
			// Build the dial
			this.dial = drawDial({
			    renderTo: this.chartid,
			    value: value,
			    centerX: 135,
			    centerY: y,
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
			this.detailFields.remove(this.valueItem);
		}
		if(this.weightItem != null){
			this.detailFields.remove(this.weightItem);
		}
		if(this.targetItem != null){
			this.detailFields.remove(this.targetItem);
		}
		this.maxChartValue =0;
		this.ranges = new Array();
	}
	, updateEmpy: function(){
		this.hide();
	}
	, update:  function(field){
		this.show();
		//title
		this.titleItem.setValue(field.attributes.kpiName);
		
		//chart
		this.val = field.attributes.actual;

		if(this.val !== null && this.val !== undefined){
			this.threshFields.addClass( 'rounded-box' ) ;
			this.detailFields.addClass( 'rounded-box' ) ;
			if(this.customChartName === undefined || this.customChartName == null || this.customChartName === 'null'){
				this.on('afterlayout',function(){
					this.drawChart(this.val);
				},this);
			}else{
				var x = this.calculateInnerThrChart(field);
				var chartName = this.customChartName+'_'+x+'.png';
				if(x >= 9 &&  field.attributes.status == ''){
					chartName = this.customChartName+'_0.png';
				}
				
				this.chartPanel.setHeight(180);
				var html = '<div style="float: left; margin-left:20px; text-align:center; background-color:'+field.attributes.status+'; height=180px; width: 200px;"><img src="../themes/other_theme/img/'+chartName+'"></img></div>'
				+ '<div style="margin-top: 30px; float: left; background-color:white; width: 100px; padding-left:5px; font-style: italic; font-weight: bold;"> Valore: '+this.val+'</div>';
				this.chartPanel.update(html);
				this.doLayout();
			}
		}else{

			if(this.customChartName !== undefined && this.customChartName != null && this.customChartName !== 'null'){
				this.chartPanel.setHeight(180);
				var html = '<div style="float: left; margin-left:20px; text-align:center; background-color: white; height=180px; width: 200px;"><img src="../themes/other_theme/img/'+this.customChartName+'_'+0+'.png"></img></div>';
				this.chartPanel.update(html);
				this.doLayout();
			}else{
				this.chartPanel.removeAll();
			}
		}
		
		this.cleanPanel();
		
		//thresholds

		var thrArray = field.attributes.thresholds;

		if(thrArray != undefined && thrArray !== null){

			for(i=0; i<thrArray.length; i++){
				var bItems = [];
				var thrLine = new Ext.form.FieldSet({
			        xtype:'fieldset',			        
			        border:false,
			        layout: 'column',
			        autoHeight:true,
			        style: 'border-bottom: 1px solid black; padding: 2px; margin:2px;',
			        defaults: {
			            anchor: '0'
			        },
			        defaultType: 'displayfield',
			        items : bItems
			    });
				
				
				var thr = thrArray[i];	
				var colorBox = new Ext.form.DisplayField({value: '&nbsp;', 
															columnWidth:0.10,
															style: 'background-color:'+thr.color});
				
				thrLine.add([ {value: thr.label, columnWidth:0.6, style:'font-weight: bold;'}, 
				              {columnWidth:0.15, value: thr.min }, 
				              {value: thr.max, columnWidth:0.15}, 
				              colorBox]);
				
				this.threshFields.add(thrLine);
				//calculate chart options
				this.calculateMax(thr);
				this.calculateRange(thr);

			}
			this.threshFields.doLayout();
		}
		//value
		this.valueItem = new Ext.form.DisplayField({fieldLabel: 'Valore', 
													value: this.val, 
													width: 0.49,
													style: 'margin-left:5px; padding-left:5px; font-style: italic; font-weight: bold;'
														});
		this.detailFields.add(this.valueItem );

		//target
		var target = field.attributes.target;
		this.targetItem = new Ext.form.DisplayField({fieldLabel: 'Target', 
													style: 'padding-left:5px;',
													width: 145,
													labelWidth:45,
													value: target});
		this.detailFields.add(this.targetItem );
		
		//weight
		var weight = field.attributes.weight;
		
		this.weightItem = new Ext.form.DisplayField({fieldLabel: 'Peso',
													style: 'margin-left:5px;',
													width: 145,
													labelWidth:45,
													value: weight});
		
		if(weight !== undefined && weight != null && weight  != ''){
			this.detailFields.add(this.weightItem );
		}
		
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
		//determines if the value to achieve is set and eventually reverse the chart
		x = this.recalculateByAchievement(x);
		return x;
	}
	, recalculateByAchievement: function( x){
		var newX = x;
		if(this.selectedThr.achieve !== undefined){			
			if(this.selectedThr.min == this.selectedThr.achieve){
				//then reverse
				newX = 9 - x;
			}
		}
		return newX;
	}
});