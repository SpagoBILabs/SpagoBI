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
  * - Andrea Gioia (andrea.gioia@eng.it)
  */

Ext.ns("Sbi.georeport");

Sbi.georeport.ControlPanel = function(config) {
	
	var defaultSettings = {
		title       : LN('sbi.georeport.controlpanel.title'),
		region      : 'west',
		split       : true,
		width       : 300,
		collapsible : true,
		margins     : '3 0 3 3',
		cmargins    : '3 3 3 3',
		autoScroll	 : true
	};
	
	if(Sbi.settings && Sbi.settings.georeport && Sbi.settings.georeport.controlPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.georeport.controlPanel);
	}
		
	var c = Ext.apply(defaultSettings, config || {});
		
	Ext.apply(this, c);
	
	this.initControls();
	
	c = Ext.apply(c, {
	     items: this.controlPanelItemsConfig 
	});
		
	// constructor
	Sbi.georeport.ControlPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.georeport.ControlPanel, Ext.Panel, {
    
	controlPanelItemsConfig: null
	
	, earthPanelEnabled: false
	, layerPanelEnabled: false
	, analysisPanelEnabled: false
	, measurePanelEnabled: false
	, logoPanelEnabled: false
	, legendPanelEnabled: false
	, debugPanelEnabled: false
   
   
    // public methods
    
    // private methods
    
    , initControls: function() {
		
		this.controlPanelItemsConfig = [];
	
		if(this.earthPanelEnabled === true) {
			this.controlPanelItemsConfig.push({
				title: LN('sbi.georeport.earthpanel.title'),
				html: '<center id="map3dContainer"></center>',
				split: true,
				height: 300,
				minSize: 300,
				maxSize: 500,
				collapsible: false                
			});
		}
	
		if(this.layerPanelEnabled === true) {
		
			this.controlPanelItemsConfig.push({
	        	title: LN('sbi.georeport.layerpanel.title'),
	            collapsible: true,
	            autoHeight: true,
	            xtype: 'layertree',
	            map: this.map
	        });
		
			// -- modifica Fabio
			
			/*
			this.model = [{ 
				text: "Layer",
				leaf: false,
	            expanded: true,
	            //children: this.layers
	            children: [{
	                  layerName: "Google Mappe",
	                  text: "Google Mappe",
	                  leaf: true,
	                  checked: true
	            }, {
	                  layerName: "Google Satellite",
	                  text: "Google Satellite",
	                  leaf: true,
	                  checked: true
	            }, {
	                  layerName: this.targetLayerConf.text,
	                  text: this.targetLayerConf.text,
	                  leaf: true,
	                  checked: true
	            }]
	        }];
			
			controlPanelItems.push({
	            title: LN('sbi.georeport.layerpanel.title'),
	            collapsible: true,
	            autoHeight: true,
	              
	            xtype: 'layertree',
	            model: this.model,
	            id:'laytr',
	            map: this.map
	        });
			*/
			// -- modifica Fabio
		}
	
		if(this.analysisPanelEnabled === true) {
			this.controlPanelItemsConfig.push({
	        	title: LN('sbi.georeport.analysispanel.title'),
	            collapsible: true,
	            items: [this.geostatistic]
	        });
		}
	
		if(this.measurePanelEnabled === true) {
			this.controlPanelItemsConfig.push({
	             title: 'Misurazione',
	             collapsible: true,
	             height: 85,
	             html: '<center></center>',
	             id: 'mapOutput'
			});
		}
	
	
		if(this.legendPanelEnabled === true) {
			this.controlPanelItemsConfig.push({
		           title: LN('sbi.georeport.legendpanel.title'),
		           collapsible: true,
		           height: 150,
		           html: '<center id="myChoroplethLegendDiv"></center>'
		     });
		}
		
		if(this.logoPanelEnabled === true) {
			this.controlPanelItemsConfig.push({
		           title: 'Logo',
		           collapsible: true,
		           height: 85,
		           html: '<center><img src="/SpagoBIGeoReportEngine/img/georeport.jpg" alt="GeoReport"/></center>'
		    });
		}
	
		if(this.debugPanelEnabled === true) {
			this.controlPanelItemsConfig.push({
		           title: 'Debug',
		           collapsible: true,
		           height: 85,
		           items: [new Ext.Button({
				    	text: 'Debug',
				        width: 30,
				        handler: function() {
		        	   		this.init3D();
		        	   		/*
		        	   		var size = this.controlPanel.getSize();
		        	   		size.width += 1;
		        	   		this.controlPanel.refreshSize(size);
		        	   		*/
		           		},
		           		scope: this
				    })]
		    });
		}
	}
});