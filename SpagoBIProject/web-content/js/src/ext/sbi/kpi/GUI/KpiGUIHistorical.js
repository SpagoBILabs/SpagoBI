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
 * Authors - Antonella Giachino
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGUIHistorical =  function(config) {
		var defaultSettings = {};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.KpiGUIHistorical) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.KpiGUIHistorical);
		}
		
		this.services = new Array();
		this.services['loadHistoricalValues'] = Sbi.config.serviceRegistry.getServiceUrl({
			serviceName: 'GET_HISTORICAL_KPI_VALUES_ACTION'
			, baseParams: {
					LIGHT_NAVIGATOR_DISABLED: 'TRUE'
			}
		});

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
		
		//this.initHistorical(c);
   
		Sbi.kpi.KpiGUIHistorical.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiGUIHistorical , Ext.form.FormPanel, {
	items: null
  , services: null
	
  ,	initHistorical: function(){
		alert("initHistorical - " + this.kpiInstId);
			
	}
	
	, cleanPanel: function(){

	}
	, update:  function(field){
		alert("updateHistorical- kpiInstId: "+field.attributes.kpiInstId);	
		
		Ext.Ajax.request({
	       	url: this.services['loadHistoricalValues'] 			       
	   	, params: this.kpiInstId  			       
		, success: function(response, options) {
			alert("Tutto OK!!");
				/*
			if(!response || !response.responseText) {
				Sbi.Msg.showError('Server response is empty', 'Service Error');
				return;
			}
			var content = Ext.util.JSON.decode( response.responseText );
			action.setBoundColumnValue(r, content.pid);
			this.waitWin.stop('Proecess stopped succesfully');
			action.toggle(r);
			
			*/
			}
			, failure: function(response, options) {
				Sbi.exception.ExceptionHandler.handleFailure(response, options);				
			}
			, scope: this     
		});	
		
		this.doLayout();
        this.render();        
	}
});