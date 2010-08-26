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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Davide Zerbetto (davide.zerbetto@eng.it)
  */

Ext.ns("Sbi.crosstab");

Sbi.crosstab.CrosstabPreviewPanel = function(config) {
	
	var defaultSettings = {
		title: LN('sbi.crosstab.crosstabpreviewpanel.title')
  	};
	if(Sbi.settings && Sbi.settings.qbe && Sbi.settings.qbe.crosstabPreviewPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.qbe.crosstabPreviewPanel);
	}
	
	this.services = new Array();
	var params = {};
	this.services['loadCrosstab'] = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'LOAD_CROSSTAB_ACTION'
		, baseParams: params
	});
	

	var c = Ext.apply(defaultSettings, config || {});
	
	c = Ext.apply(c, {
      		layout:'fit',
      		border: false     		
    	});
	
	// constructor
    Sbi.crosstab.CrosstabPreviewPanel.superclass.constructor.call(this, c);
	
};

Ext.extend(Sbi.crosstab.CrosstabPreviewPanel, Ext.Panel, {
	
	services: null
	, crossTabJSON: null
	
	
	, load: function(crosstabDefinition) {
			this.addLoadingToPage();
			Ext.Ajax.request({
		        url: this.services['loadCrosstab'],
		        params: {
						crosstabDefinition: Ext.util.JSON.encode(crosstabDefinition)
				},
		        success : function(response, opts) {
//	  	  			try {
	  	  				
	  	  				//this.crossTabJSON = Ext.util.JSON.decode( response.responseText );
	  	  				this.refreshCrossTab(Ext.util.JSON.decode( response.responseText ));
//	  	  			} catch (err) {
//	  	  				alert(err);
//	  	  				alert(err.description);
//	  	  			}
		        },
		        scope: this,
				failure: Sbi.exception.ExceptionHandler.handleFailure      
			});
	}

	, refreshCrossTab: function(crosstabDefinition){
		this.removeAll(true);
	
		var rows = this.fromNodeToArray(crosstabDefinition.rows);
		var columns = this.fromNodeToArray(crosstabDefinition.columns);
		var data = crosstabDefinition.data;
		var config = crosstabDefinition.config;
		var ct =  new CrossTab( rows,columns, data, config.calculatetotalsonrows=="on", config.calculatetotalsoncolumns=="on");
		ct.reloadHeadersAndTable();
		
		this.add(ct);
		this.doLayout();
	}

	, fromNodeToArray: function(node){
		var childs = node.node_childs;
		var array = new Array();
		array.push(node.node_key);
		if(childs!=null && childs.length>0){
			var childsArray = new Array();
			for(var i=0; i<childs.length; i++){
				childsArray.push(this.fromNodeToArray(childs[i]));
			}
			array.push(childsArray);
		}
		return array;
	}
	
    , addLoadingToPage : function(){
		var dh = Ext.DomHelper; 
	
		
		var bodyElement = document.getElementsByTagName('body');
		
		var loadingmask = {
				id: 'loading-mask',
				tag: 'div',
				html: '&nbsp;'
			};
		
		var loading = {
				id: 'loading',
				tag: 'div',
				html: '&nbsp;'
			};
		
		var loadingindicator = {
				id: 'loading-indicator',
				tag: 'div',
				cls: 'loading-indicator',
				html: 'Loading...'
			};

		var loadingmaskDOM = dh.append(bodyElement[0].id, loadingmask);
		var loadingDOM = dh.append(loadingmaskDOM, loading);
		dh.append(loadingDOM, loadingindicator);
		
    }

});