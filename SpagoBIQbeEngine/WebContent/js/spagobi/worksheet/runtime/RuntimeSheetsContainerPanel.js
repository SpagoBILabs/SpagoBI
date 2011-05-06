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
 * 
 * 
 * Public Events
 * 

 * 
 * Authors - Alberto Ghedin (alberto.ghedin@eng.it)
 */
Ext.ns("Sbi.worksheet.runtime");

Sbi.worksheet.runtime.RuntimeSheetsContainerPanel = function(config) { 
	
	var defaultSettings = {};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.runtime.runtimeSheetsContainerPanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.runtime.runtimeSheetsContainerPanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	var items =this.buildSheets(config,
		[{//FAKE
					title: 'Sheet n',
					header :{title: 'header', img:'img/delete.gif', position:'left'},
					content:{designer: 'Pivot Table', crosstabDefinition: 	{'rows':[{'id':'it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productFamily','nature':'attribute','alias':'Product Family','iconCls':'attribute'},{'id':'it.eng.spagobi.SalesFact1998:product(product_id):productClass(product_class_id):productDepartment','nature':'attribute','alias':'Product Department','iconCls':'attribute'}],'columns':[{'id':'it.eng.spagobi.SalesFact1998::store(store_id):storeCountry','nature':'attribute','alias':'Store Country','iconCls':'attribute'},{'id':'it.eng.spagobi.SalesFact1998::store(store_id):storeState','nature':'attribute','alias':'Store State','iconCls':'attribute'}],'measures':[{'id':'it.eng.spagobi.SalesFact1998:storeCost','nature':'measure','alias':'Store Cost','funct':'SUM','iconCls':'measure'},{'id':'it.eng.spagobi.SalesFact1998:unitSales','nature':'measure','alias':'Unit Sales','funct':'SUM','iconCls':'measure'}],'config':{'measureson':'columns'}}},
					footer :{title: 'footer', img:'img/delete.gif', position:'right'}
					
			}]); 
	c ={
			tabPosition: 'bottom',        
	        enableTabScroll:true,
	        defaults: {autoScroll:true},
	        items: items
	};
	this.addEvents();
	Sbi.worksheet.runtime.RuntimeSheetsContainerPanel.superclass.constructor.call(this, c);	 
	
	//active the first tab after render
	this.on('render',function(){
		if(this.items.length>0){
			this.setActiveTab(0);
		}	
	}, this);
};

Ext.extend(Sbi.worksheet.runtime.RuntimeSheetsContainerPanel, Ext.TabPanel, {
	//build the sheets
	buildSheets: function(config, sheetsConfig){
		var items = [];
		if(sheetsConfig!=undefined && sheetsConfig!=null){
			for(var i=0; i<sheetsConfig.length; i++){
				items.push(new Sbi.worksheet.runtime.RuntimeSheetPanel(Ext.apply(config||{},{sheetConfig: sheetsConfig[i]})));
			}
		}
		return items;
	}
	
	
});