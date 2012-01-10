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
 * [list]
 * 
 * 
 * Public Events
 * 
 * [list]
 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimePieChartPanelExt3 = function(config) {
	
	var defaultSettings = {
	};

	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimePieChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimePieChartPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	this.chartDivId = Ext.id();
	
	c = Ext.apply(c, {
		html : '<div id="' + this.chartDivId + '""></div>'
	});
	
	Sbi.worksheet.runtime.RuntimePieChartPanelExt3.superclass.constructor.call(this, c);
	
	this.init();
	
};

Ext.extend(Sbi.worksheet.runtime.RuntimePieChartPanelExt3, Sbi.worksheet.runtime.RuntimeGenericChartPanelExt3, {
	
	chartDivId : null
	, chart : null
	, chartConfig : null // mandatory object to be passed as a property of the constructor input object. The template is:
//							template: {
//								showvalues: true, 
//								showlegend: true,
//								category: {id:"it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productFamily", alias:"Product Family", funct:"NONE", iconCls:"attribute", nature:"attribute"},
//								series: [
//								    {id:"it.eng.spagobi.SalesFact1998:storeCost", alias:"Store Cost", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Store Cost", color:"#FFFFCC"}, 
//								    {id:"it.eng.spagobi.SalesFact1998:storeSales", alias:"Store Sales", funct:"SUM", iconCls:"measure", nature:"measure", seriename:"Store Sales", color:"#FFBBAA"}
//								],
//							    colors: ['#4572A7', '#DB843D', '#56AFC7', '#80699B', '#89A54E', '#AA4643', '#50B432'
//									    , '#1EA6E0', '#DDDF00', '#ED561B', '#64E572', '#9C9C9C', '#4EC0B1', "#C3198E"
//										, "#6B976B", "#B0AF3D", "#E7913A", "#82AEE9", "#7C3454", "#A08C1F", "#84D3D1", "#586B8A", "#B999CC"]
//							}
	
	
	, init : function () {
		this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}

	, createChart: function () {
		var storeObject = this.getJsonStoreExt3();
		var extraStyle ={};
		var items = new Array();
		
		

		for(var i=0; i<storeObject.serieNames.length; i++){
			var chartSerieNumber = 'series'+i;
			
			var itemChart = {
				xtype: 'piechart',
				store: storeObject.store,
				categoryField: 'categories',
				title: 'Month',
				serieNumber: i,
				//displayName: storeObject.serieNames[i],
	            dataField: chartSerieNumber,
	            extraStyle: extraStyle
			};
			//alert(storeObject.serieNames[i]);
			
			//percentage
			if (this.chartConfig.showpercentage==undefined || (!this.chartConfig.showpercentage)) {
				itemChart.tipRenderer = function(chart, record, index, series){
					return record.data.categories+'\n'+ record.data['series'+chart.serieNumber];
		        };
		        //configuration (legend and values)
		        //Do not put this line outside the if block because
		        //it can override the tipRenderer
				this.addChartConfExt3(itemChart);
			}else{
				//configuration (legend and values)
				this.addChartConfExt3(itemChart, true);
				var seriesum=0;
				for(var j=0; j<storeObject.serieNames.length; j++){
					seriesum = seriesum + parseFloat(((storeObject.store.getAt(j)).data)[chartSerieNumber]);
				}
				itemChart.seriesum = seriesum;
				if((this.chartConfig.showvalues == undefined) || !this.chartConfig.showvalues){
					itemChart.tipRenderer = function(chart, record, index, series){
						return  Ext.util.Format.number(100*record.data['series'+chart.serieNumber]/ chart.seriesum, '0.00') + '%';
			        };
				}
			}
			
			var titlePanel = new Ext.Panel({
				border: false,
				html: '<div style=\"padding-top: 5px; color: rgb(255, 102, 0);\" align=\"center\"><font size=\"4\"><b>'+storeObject.serieNames[i]+'</b></font></div>'
			});
			
			items.push(new Ext.Panel({
				border: false,
				items: [titlePanel, itemChart]
			}));
		}
		
		
		new Ext.Panel({
			renderTo : this.chartDivId,
			//layout: 'fit',
			border: false,
			items: items
		});
		
		
	}	
	
	, getColors : function () {
		return this.chartConfig.colors;
	}
	
	

});