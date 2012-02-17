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
  *  [list]
  * 
  * 
  * Public Events
  * 
  *  [list]
  * 
  * Authors
  * 
  * - Antonella Giachino (antonella.giachino@eng.it)
  */


Ext.define('Sbi.extjs.chart.data.Model', {
    extend: 'Ext.data.Model',
    //fields: ["recNo",{"type":"string","header":"name","dataIndex":"column_1","name":"column_1"},{"type":"int","header":"data1","dataIndex":"column_2","name":"column_2"},{"type":"int","header":"data2","dataIndex":"column_3","name":"column_3"},{"type":"int","header":"data3","dataIndex":"column_4","name":"column_4"},{"type":"int","header":"data4","dataIndex":"column_5","name":"column_5"},{"type":"int","header":"data5","dataIndex":"column_6","name":"column_6"}]
    fields: ["recNo",{"type":"string","header":"name","dataIndex":"column_1","name":"column_1"}]

	, constructor: function(config) {
		
		var c = Ext.apply(config || {});
		Ext.apply(this, c);

		this.callParent(arguments);

		return this;
    }
	
});
/*
var proxyUrl = Sbi.config.serviceRegistry.getServiceUrl({serviceName:  'GET_CHART_DATA_ACTION'
	   , baseParams:params
	    });
//The Store contains the AjaxProxy as an inline configuration
var store = Ext.create('Ext.data.Store', {
    model: 'Sbi.extjs.chart.data.Model',
    proxy: {
        type: 'ajax',
        url : proxyUrl
    }
});

store.load();
*/