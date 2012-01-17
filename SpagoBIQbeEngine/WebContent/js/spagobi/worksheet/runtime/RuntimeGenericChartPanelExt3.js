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
  * 
  * Public Events
  * 
  * Authors
  * 
  * - Alberto Ghedin (alberto.ghedin@eng.it)
  */

Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3  = function(config) { 
	
	this.legendStyle = 		
	{
			display: 'bottom',
			border:{
				color: "bcbcbc",
				size: 1
			},
			padding: 5,
			font:
			{
				family: 'Tahoma',
				size: 13
			}
	};
	
	this.addEvents();
	
	Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3.superclass.constructor.call(this, config);	 	
	

	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3, Sbi.worksheet.runtime.RuntimeGenericChartPanel, {

	headerClickHandler: function(event, element, object, chart) {	
		
//		var t = document.getElementsByTagName('object');
//		for(var i=0; i<t.length; i++){
//			
//			if(t[i].id.substring(0,11)==('extflashcmp')){
//				alert(t[i].toSource());
//				//t[i].oncontextmenu = function(){alert('ciao');};
//			}
//		}
		
		//document.oncontextmenu = function(){alert('ciao');};
		
		if(!this.clickMenu){
			var clickMenuItems = new Array();
			if(!chart.bkseries){
				chart.bkseries = chart.series;
			}
			
			clickMenuItems.push(new Ext.menu.TextItem({text: LN('sbi.worksheet.runtime.worksheetruntimepanel.chart.visibleseries'), iconCls: 'show'}));
			clickMenuItems.push(new Ext.menu.Separator({}));
			
			for(var i=0; i<chart.series.length; i++){
				var freshCheck = new Ext.menu.CheckItem({
					checked: true,
					text: chart.series[i].displayName,
					serieNumber: i
				});
				freshCheck.on('checkchange', function(checkBox, checked){
					if(!checked){
						chart.hiddenseries.push(checkBox.serieNumber);
					}else{
						chart.hiddenseries.splice((chart.hiddenseries.indexOf(checkBox.serieNumber)),1);
					}
					
					var visibleSeries = new Array();
					//chart.setSeriesStylesByIndex(checkBox.serieNumber,{visibility:checked?"visible":"hidden"});
					for(var y=0; y<chart.bkseries.length; y++){
						if(chart.hiddenseries.indexOf(y)<0){
							visibleSeries.push(chart.bkseries[y]);
						}
					}

					chart.series = visibleSeries;
					chart.refresh();
				}, this);
				clickMenuItems.push(freshCheck);
				
			}

			this.clickMenu = new Ext.menu.Menu({
				items: clickMenuItems
			});
			
			//this.clickMenu.on('show',function(){chart.setSeriesStylesByIndex(0,{visibility: 'hidden'});}, this);


		}
		var x = 20;
		var y = 20;
		if (event!=null){
			x = event.getPageX();
			y = event.getPageY();
		}
		this.clickMenu.showAt([event.getPageX(), event.getPageY()]);
		
		
//		this.clickMenu = new Ext.menu.Menu({
//			items: {
//				text: LN('sbi.crosstab.menu.hideheadertype'),
//				iconCls:'hide',
//				handler:function(){
//					//alert(chart.swf.toSource());
//					chart.setSeriesStylesByIndex(0,{visibility: 'hidden'});
//				},
//				scope: this
//			}
//		});
//		this.clickMenu.showAt([event.getPageX(), event.getPageY()]);

	}





	
	, getJsonStoreExt3: function(percent){
		var storeObject = {};
		
		var series = this.getSeries();
		var categories = this.getCategories();
		
		var data = new Array();
		var fields = new Array();
		var serieNames = new Array();

		
		for(var i=0; i<categories.length; i++){
			var z = {};
			var seriesum = 0;
			for(var j=0; j<series.length; j++){
				z['series'+j] = ((series[j]).data)[i];
				seriesum = seriesum + parseFloat(((series[j]).data)[i]);
			}
			if(percent){
				for(var j=0; j<series.length; j++){
					z['series'+j] = (z['series'+j]/seriesum)*100;;
				}	
			}
			z['seriesum'] = seriesum;
			z['categories'] = categories[i];
			data.push(z);
		}
		
		for(var j=0; j<series.length; j++){
			fields.push('series'+j);
			serieNames.push(series[j].name);
		}
		
		fields.push('seriesum');
		fields.push('categories');

		
	    var store = new Ext.data.JsonStore({
	        fields:fields,
	        data: data
	    });
	    
	    storeObject.store = store;
	    storeObject.serieNames = serieNames;

	    return storeObject;
	}
	
//	, getJsonStore: function(){
//	var series = (this.getSeries())[0].data;
//	var categories = this.getCategories();
//	
//	var data = new Array();
//
//	for(var i=0; i<categories.length; i++){
//		var z = {};
//		z.series = series[i];
//		z.categories = categories[i];
//		data.push(z);
//	}
//	
//    var store = new Ext.data.JsonStore({
//        fields:['series', 'categories'],
//        data: data
//    });
//    
//    return store;
//}
	
	, addChartConfExt3: function(chartConf, showTipMask){
		if((this.chartConfig.showlegend !== undefined) ? this.chartConfig.showlegend : true){
			chartConf.extraStyle.legend = this.legendStyle;
		}
		chartConf.tipRenderer = this.getTooltipFormatter();
    
		 
		
//		if((this.chartConfig.showvalues == undefined) || !this.chartConfig.showvalues){
//			if(!showTipMask){
//				chartConf.extraStyle.dataTip = {
//						border: {
//		                    size:0
//		                },
//		                background: {
//		                    alpha: .0
//		                }
//		            };
//			}
//			chartConf.tipRenderer = function(chart, record, index, series){
//	            return '';
//	        };
//		}
	}
	
	, getTooltipFormatter: function () {
		var showPercentage = this.chartConfig.showpercentage;
		var chartType = this.chartConfig.designer;
		var allSeries = this.chartConfig.series;
		var percentStacked = ((this.chartConfig.type) && ((this.chartConfig.type).indexOf('percent')>=0));
		var getFormattedValueExt3 = this.getFormattedValueExt3;
			
		var toReturn = function (chart, record, index, series) {
			
			var valuePrefix= '';
			var valueSuffix = '';
			
			var value = getFormattedValueExt3(chart, record, series, chartType, allSeries, percentStacked);
			
			if (chartType == 'Pie Chart') {
				//pie
				valuePrefix = record.data.categories+'\n';
			}else{
		        //bar e line
				valuePrefix = series.displayName+'\n'+record.data.categories+'\n';
			}
			
			if(showPercentage){
				valueSuffix = '\n'+ Ext.util.Format.number(100*record.data['series'+chart.serieNumber]/ chart.seriesum, '0.00') + '%';
			}
			
			return valuePrefix+value+valueSuffix;
			
		};
		return toReturn;
	}
	
	//Format the value to display
	, getFormattedValueExt3: function (chart, record, series, chartType, allSeries, percentStacked){
		var theSerieNam;
		var value ;
		var serieDefinition;
		
		if (chartType == 'Pie Chart') {
			 value = record.data['series'+chart.serieNumber];
			 theSerieName = chart.serieName;
		}else{
	        //bar e line
			if(!chart.horizontal){
				value = record.data[series.yField];
			}else{
				value = record.data[series.xField];
			}
			theSerieName = series.displayName;
		}
		// find the serie configuration
		var i = 0;
		for (; i < allSeries.length; i++) {
			if (allSeries[i].seriename === theSerieName) {
				serieDefinition = allSeries[i];
				break;
			}
		}
		
		if(percentStacked){
			value =  Ext.util.Format.number(value, '0.00') + '%';
		}else{
			// format the value according to serie configuration
			value = Sbi.qbe.commons.Format.number(value, {
	    		decimalSeparator: Sbi.locale.formats['float'].decimalSeparator,
	    		decimalPrecision: serieDefinition.precision,
	    		groupingSeparator: (serieDefinition.showcomma) ? Sbi.locale.formats['float'].groupingSeparator : '',
	    		groupingSize: 3,
	    		currencySymbol: '',
	    		nullValue: ''
			});
		}
			
		// add suffix
		if (serieDefinition.suffix !== undefined && serieDefinition.suffix !== null && serieDefinition.suffix !== '') {
			value = value + ' ' + serieDefinition.suffix;
		}
		return value;

	}

});