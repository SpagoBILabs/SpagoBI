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
  *  loadChartData(dataConfig): load the data for the chart
  *  getCategories(): Load the categories for the chart
  *  getSeries(): Load the series for the chart
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Alberto Ghedin (alberto.ghedin@eng.it)
  */

Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeGenericChartPanel  = function(config) { 
	
	var defaultSettings = {
			id: 'RuntimeGenericChartPanel'
	};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeGenericChartPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeGenericChartPanel);
	}
	
	this.services = new Array();
	var params = {};
	this.services['loadData'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'LOAD_CROSSTAB_ACTION'
		, baseParams: params
	});
	var c = Ext.apply(defaultSettings, config || {});
	Ext.apply(this, c);
	
	

	Sbi.worksheet.runtime.RuntimeGenericChartPanel.superclass.constructor.call(this, c);	 	
};

Ext.extend(Sbi.worksheet.runtime.RuntimeGenericChartPanel, Ext.Panel, {
	loadMask: null,
	dataContainerObject: null//the object with the data for the panel
	
	/**
	 * Loads the data for the chart.. Call the action which loads the crosstab 
	 * (the crosstab is the object that contains the data for the chart)
	 * @param dataConfig the field for the chart..
	 * The syntax is {rows, measures}.. For example {'rows':[{'id':'it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productFamily','nature':'attribute','alias':'Product Family','iconCls':'attribute'}],'measures':[{'id':'it.eng.spagobi.SalesFact1998:storeCost','nature':'measure','alias':'Store Cost','funct':'SUM','iconCls':'measure'},{'id':'it.eng.spagobi.SalesFact1998:unitSales','nature':'measure','alias':'Unit Sales','funct':'SUM','iconCls':'measure'}]}
	 */
	, loadChartData: function(dataConfig){
		this.showMask();
		crosstabDefinitionEncoded = Ext.util.JSON.encode(dataConfig);
		var requestParameters = {
				crosstabDefinition: {
					'rows': [dataConfig.rows],
					'columns':[],
					'measures': [dataConfig.measures],
					'config': {'measureson':'columns'}
				}
		}
		Ext.Ajax.request({
	        url: this.services['loadData'],//load the crosstab from the server
	        params: requestParameters,
	        success : function(response, opts) {
        		this.hideMask();
	        	this.dataContainerObject = Ext.util.JSON.decode( response.responseText );
	        	alert(this.getCategories().toSource());
	        	alert(this.getSeries().toSource());
	        },
	        scope: this,
			failure: function(response, options) {
				this.hideMask();
				Sbi.exception.ExceptionHandler.handleFailure(response, options);
			}      
		});
	}
	
	/**
	 * Load the categories for the chart
	 */
	, getCategories: function(){
		if(this.dataContainerObject!=null){
			var measures = this.dataContainerObject.columns.node_childs;
			var categories = [];
			for(var i=0; i<measures.length; i++){
				categories.push(measures[i].node_key);
			}	
			return  categories;
		}
	}
	
	/**
	 * Loads the series for the chart
	 */
	, getSeries: function(){
		if(this.dataContainerObject!=null){
			var seriesNames = this.dataContainerObject.rows.node_childs;
			var data = this.dataContainerObject.data;
			var series = [];
			var serie;
			for(var i=0; i<seriesNames.length; i++){
			      serie = {};
			      serie.name =   seriesNames[i].node_key;
			      serie.data =   this.dataContainerObject.data[i];
			      series.push(serie);
			}	
			return series;
		}
	}
	
	/**
	 * Opens the loading mask 
	 */
    , showMask : function(){
    	
    	if (this.loadMask == null) {
    		this.loadMask = new Ext.LoadMask('RuntimeGenericChartPanel', {msg: "Loading.."});
    	}
    	this.loadMask.show();
    }
	
	/**
	 * Closes the loading mask
	 */
	, hideMask: function() {
    	if (this.loadMask != null) {
    		this.loadMask.hide();
    	}
	}

});



