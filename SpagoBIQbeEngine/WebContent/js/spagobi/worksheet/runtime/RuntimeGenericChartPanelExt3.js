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

	ieChartHeight: 400,
	charts: null, //the list of charts of the panel. Should be 1 for bar and line and can be more than one for pie
	
	exportContent: function() {
		var chartsByteArrays = new Array();
//		if(this.charts!=undefined && this.charts!=null){
//			for(var i =0; i<this.charts.length; i++){
//				chartsByteArrays.push((this.charts[i]).swf.exportPNG());
//			}
//		}
		
		var exportedChart = {CHARTS_ARRAY:this.byteArrays, SHEET_TYPE: 'CHART', CHART_TYPE:'ext3'};
		return exportedChart;
	}
	
	,headerClickHandler: function(event, element, object, chart, reloadCallbackFunction, reloadCallbackFunctionScope) {	
		
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
					if(reloadCallbackFunctionScope){
						reloadCallbackFunction(chart, reloadCallbackFunctionScope);
					}else{
						chart.refresh();
					}

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
					z['seriesflatvalue'+j] = z['series'+j];
					z['series'+j] = (z['series'+j]/seriesum)*100;;
				}	
			}
			z['seriesum'] = seriesum;
			z['categories'] = categories[i];
			data.push(z);
		}
		
		for(var j=0; j<series.length; j++){
			fields.push('series'+j);
			fields.push('seriesflatvalue'+j);
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
	
	
	, addChartConfExt3: function(chartConf, showTipMask){
		if((this.chartConfig.showlegend !== undefined) ? this.chartConfig.showlegend : true){
			chartConf.extraStyle.legend = this.legendStyle;
		}
		chartConf.tipRenderer = this.getTooltipFormatter();
	}


});