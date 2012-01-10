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
			padding: 5,
			font:
			{
				family: 'Tahoma',
				size: 13
			}
	};
	
	Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3.superclass.constructor.call(this, config);	 	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3, Sbi.worksheet.runtime.RuntimeGenericChartPanel, {
	
	getJsonStoreExt3: function(percent){
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
		
		if((this.chartConfig.showvalues == undefined) || !this.chartConfig.showvalues){
			if(!showTipMask){
				chartConf.extraStyle.dataTip = {
						border: {
		                    size:0
		                },
		                background: {
		                    alpha: .0
		                }
		            };
			}
			chartConf.tipRenderer = function(chart, record, index, series){
	            return '';
	        };
		}
	}

});