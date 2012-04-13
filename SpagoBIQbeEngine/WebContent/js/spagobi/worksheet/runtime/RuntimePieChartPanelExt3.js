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

	
	, init : function () {
		this.loadChartData({'rows':[this.chartConfig.category],'measures':this.chartConfig.series});
	}

	, createChart: function () {
		var storeObject = this.getJsonStoreExt3();
		var extraStyle ={};
		var items = new Array();
		this.byteArrays=new Array();
		for(var i=0; i<storeObject.serieNames.length; i++){
			var chartSerieNumber = 'series'+i;
			
			
			var itemChart = {
				store: storeObject.store,
				categoryField: 'categories',
				title: 'Month',
				serieNumber: i,
				serieName: this.formatLegendWithScale(storeObject.serieNames[i]),
	            dataField: chartSerieNumber,
	            style: 'height: 85%;',
	            extraStyle: extraStyle,
	            series:[{
	                style: {
	                    colors: this.chartConfig.colors
	                    }
	                }]
			};
			//set the height if ie
	    	if(Ext.isIE){
	    		itemChart.height = this.ieChartHeight;
	    	}

			//configuration (legend and values)
	    	this.addChartConfExt3(itemChart);
	    	
			//percentage
			if (this.chartConfig.showpercentage) {

				var seriesum=0;
				for(var j=0; j<storeObject.store.totalLength; j++){
					seriesum = seriesum + parseFloat(((storeObject.store.getAt(j)).data)[chartSerieNumber]);
				}
				itemChart.seriesum = seriesum;
			}
			
			var titlePanel = new Ext.Panel({
				border: false,
				html: '<div style=\"padding-top: 5px; color: rgb(255, 102, 0);\" align=\"center\"><font size=\"4\"><b>'+itemChart.serieName+'</b></font></div>'
			});
			
			var chartPanel =  new Ext.chart.PieChart(itemChart);
			
			
			var exportChartPanel  = new Ext.Panel({
				border: false,
				
				
				html: '<div style=\"padding-top: 5px; padding-bottom: 5px; font: 11px tahoma,arial,helvetica,sans-serif;\">'+LN('sbi.worksheet.runtime.worksheetruntimepanel.chart.includeInTheExport')+'</div>'
			});
			
			var chartContainer = new Ext.Panel({
				border: false,
				items: [exportChartPanel, titlePanel,chartPanel]
			});
			
			items.push(chartContainer);
			this.on('contentclick', function(event){
				chartContainer.fireEvent('contentclick');	
			}, this);
			
			chartContainer.on('contentclick', function(event){
				try{
					this.byteArrays.push(chartPanel.exportPNG());	
				}catch(e){}
			
				exportChartPanel.update('');
			}, this);
		}
		
		
		new Ext.Panel({
			renderTo : this.chartDivId,
			//layout: 'fit',
			border: false,
			items: items
		});
		
		//this.on('contentclick', function(event){
		
	}	
	
	, getColors : function () {
		return this.chartConfig.colors;
	}
	
	, getTooltipFormatter: function () {
		var showPercentage = this.chartConfig.showpercentage;
		var allSeries = this.chartConfig.series;
	
		var getFormattedValueExt3 = this.getFormattedValueExt3;

		var toReturn = function (chart, record, index, series) {
		
			var valuePrefix= '';
			var valueSuffix = '';

			var value = getFormattedValueExt3(chart, record, series, allSeries);

			valuePrefix = record.data.categories+'\n';

			if(showPercentage){
				valueSuffix = '\n'+ +Ext.util.Format.number(100*record.data['series'+chart.serieNumber]/ chart.seriesum, '0.00') + '%';
			}

			return valuePrefix+value+valueSuffix;

		};
		return toReturn;
	}

//	Format the value to display
	, getFormattedValueExt3: function (chart, record, series, allSeries){
		var theSerieNam;
		var value ;
		var serieDefinition;

		value = record.data['series'+chart.serieNumber];
		theSerieName = chart.serieName;
		
		// find the serie configuration
		var i = 0;
		for (; i < allSeries.length; i++) {
			//substring to remove the scale factor
			if (allSeries[i].seriename === theSerieName.substring(0, allSeries[i].seriename.length)) {
				serieDefinition = allSeries[i];
				break;
			}
		}


		// format the value according to serie configuration
		value = Sbi.qbe.commons.Format.number(value, {
			decimalSeparator: Sbi.locale.formats['float'].decimalSeparator,
			decimalPrecision: serieDefinition.precision,
			groupingSeparator: (serieDefinition.showcomma) ? Sbi.locale.formats['float'].groupingSeparator : '',
					groupingSize: 3,
					currencySymbol: '',
					nullValue: ''
		});
		

		// add suffix
		if (serieDefinition.suffix !== undefined && serieDefinition.suffix !== null && serieDefinition.suffix !== '') {
			value = value + ' ' + serieDefinition.suffix;
		}
		return value;

	}

});