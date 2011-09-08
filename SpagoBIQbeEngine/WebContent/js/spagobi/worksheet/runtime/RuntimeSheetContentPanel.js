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
 *  contentloaded: fired after the data has been loaded
 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeSheetContentPanel = function(config) { 

	var defaultSettings = {};
		
	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.runtime.runtimeSheetContentPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeSheetContentPanel);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	if(config.hiddenContent){
		if(config.contentConfig==undefined || config.contentConfig==null){
			config.contentConfig = {};
		}
		config.contentConfig.hiddenContent=true;
	}
	
	Ext.apply(this, c);

    this.addEvents('contentloaded');
    this.addEvents('contentloading');
    
	this.content = this.initContent(c);
	
	//catch the event of the contentloaded and throws it to the parent
	this.content.on('contentloaded',function(empty){this.fireEvent('contentloaded', empty);},this);
		
	c = {
		border: false,
		style:'padding:5px 15px 5px',
		items: this.content,
		autoHeight: true
	};
	if(config.hiddenContent){
		c.hidden = true;
	}
	
	Sbi.worksheet.runtime.RuntimeSheetContentPanel.superclass.constructor.call(this, c);	

};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetContentPanel, Ext.Panel, {
	content : null
	, sheetName : null

	, exportContent: function(filtersValue){
		if(this.contentConfig.designer == 'Table') {
			var visibleselectfields = (this.contentConfig.visibleselectfields);
    		var params ={'visibleselectfields': visibleselectfields};

    		return this.content.exportContent(params);
		}else{
			return this.content.exportContent();
		}
	},
	
	initContent: function (c) {
		var items = [];
	
		switch (this.contentConfig.designer) {
	        case 'Pivot Table':
	        	return this.initCrossTab(c);
	        case 'Bar Chart':
	        	return new Sbi.worksheet.runtime.RuntimeBarChartPanel({'chartConfig':this.contentConfig, sheetName : this.sheetName});
	        case 'Line Chart':
	        	return new Sbi.worksheet.runtime.RuntimeLineChartPanel({'chartConfig':this.contentConfig, sheetName : this.sheetName});
	        case 'Pie Chart':
	        	return new Sbi.worksheet.runtime.RuntimePieChartPanel({'chartConfig':this.contentConfig, sheetName : this.sheetName});
	        case 'Table':
	        	return this.initTable(c);
	        default: 
	        	alert('Unknown widget!');
		}
	},
	
	
	initTable: function(c){
		
		var ieFixVar = {};
		
		if(Ext.isIE){
			ieFixVar ={
				padding: '0 5 0 0',	
				style:'width: 95%'
			}
		}
		
    	var table =  new Sbi.formviewer.DataStorePanel(Ext.apply({
    		split: true,
    		collapsible: false,
    		padding: '0 20 0 0',
    		autoScroll: true,
    		frame: false, 
    		border: false,
    		displayInfo: false,
    		pageSize: 50,
    		//autoHeight: true,
    		sortable: false,
    		gridConfig: {
    			height: Sbi.settings.worksheet.runtime.table.height || 400
    			//autoHeight: true // setting autoHeight to true, scrollbars do not appear (ExtJS sets overflow : visible to the element style)
    		},
    		services: {
    			loadDataStore: Sbi.config.serviceRegistry.getServiceUrl({
    				serviceName: 'EXECUTE_WORKSHEET_QUERY_ACTION'
    				, baseParams: {sheetName : this.sheetName}//baseParams: {'visibleselectfields': Ext.encode(this.contentConfig.visibleselectfields)}
    			})
    		}
    	},ieFixVar));
    	if(!c.hiddenContent){
    		table.execQuery({'visibleselectfields': Ext.encode(this.contentConfig.visibleselectfields)});
    	}else{
    		this.on('afterrender', function(){this.fireEvent('contentloaded')}, this);
    	}
		return table;
	},
	
	initCrossTab: function(c){
		var crossTab = new Sbi.crosstab.CrosstabPreviewPanel(Ext.apply(c|| {},{
			hideLoadingMask: true,
			sheetName : this.sheetName,
			crosstabConfig: {autoHeight: true}, 
			title: false}));
		if(!c.hiddenContent){
			this.on('afterlayout',this.loadCrosstab,this);
		}else{
			this.on('afterrender', function(){this.fireEvent('contentloaded')}, this);
		}
		
		return crossTab;
	},
	
	loadCrosstab: function(){
		this.content.load(this.contentConfig.crosstabDefinition);
		this.un('afterlayout',this.loadCrosstab,this);
	},
	
	applyFilters: function(filtersValue){
		this.fireEvent('contentloading');
		switch (this.contentConfig.designer) {
	        case 'Pivot Table':
	        	this.content.load(this.contentConfig.crosstabDefinition, filtersValue);
	        	break;
	        case 'Table':
        		var params ={'visibleselectfields': Ext.encode(this.contentConfig.visibleselectfields)};
        		if(filtersValue!=undefined && filtersValue!=null){
        			params.optionalfilters = Ext.encode(filtersValue);
        		}
        		this.content.execQuery(params);
	        	break;
	        case 'Bar Chart':
	        	this.content.loadChartData({'rows':[this.contentConfig.category],'measures':this.contentConfig.series},filtersValue);
	        	break;
	        case 'Line Chart':
	        	this.content.loadChartData({'rows':[this.contentConfig.category],'measures':this.contentConfig.series},filtersValue);
	        	break;
	        case 'Pie Chart':
	        	this.content.loadChartData({'rows':[this.contentConfig.category],'measures':this.contentConfig.series},filtersValue);
	        	break;
		}

		this.contentConfig.hiddenContent=false;
		this.show();
	}

	

});
