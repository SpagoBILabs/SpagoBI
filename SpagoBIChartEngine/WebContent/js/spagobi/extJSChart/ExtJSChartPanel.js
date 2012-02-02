
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
 * Authors - Antonella Giachino (antonella.giachino@eng.it)
 */
Ext.define('Sbi.extjs.chart.ExtJSChartPanel', {
    alias: 'widget.ExtJSChartPanel',
    extend: 'Ext.panel.Panel',
    constructor: function(config) {
    	var defaultSettings = {
    	};

    	var c = Ext.apply(defaultSettings, config || {});

    	Ext.apply(this, c);
    	
    	//this.init();
    	this.createExample(c);
    	
        this.callParent(this, c);
    }

  , createExample: function(config){
	   var store = Ext.create('Ext.data.JsonStore', {
		    fields: ['name', 'data1', 'data2', 'data3', 'data4', 'data5'],
		    data: [
		        { 'name': 'metric one',   'data1':10, 'data2':12, 'data3':14, 'data4':8,  'data5':13 },
		        { 'name': 'metric two',   'data1':7,  'data2':8,  'data3':16, 'data4':10, 'data5':3  },
		        { 'name': 'metric three', 'data1':5,  'data2':2,  'data3':14, 'data4':12, 'data5':7  },
		        { 'name': 'metric four',  'data1':2,  'data2':14, 'data3':6,  'data4':1,  'data5':23 },
		        { 'name': 'metric five',  'data1':27, 'data2':38, 'data3':36, 'data4':13, 'data5':33 }
		    ]
		});

		this.chart = Ext.create('Ext.chart.Chart', {
							    renderTo: config.divId ,
							    width: 500,
							    height: 300,
							    animate: true,
							    store: store,
							    axes: [{
							        type: 'Numeric',
							        position: 'bottom',
							        fields: ['data1'],
							        label: {
							            renderer: Ext.util.Format.numberRenderer('0,0')
							        },
							        title: 'Sample Values',
							        grid: true,
							        minimum: 0
							    }, {
							        type: 'Category',
							        position: 'left',
							        fields: ['name'],
							        title: 'Sample Metrics'
							    }],
							    series: [{
							        type: 'bar',
							        axis: 'bottom',
							        highlight: true,
							        tips: {
							          trackMouse: true,
							          width: 140,
							          height: 28,
							          renderer: function(storeItem, item) {
							            this.setTitle(storeItem.get('name') + ': ' + storeItem.get('data1') + ' views');
							          }
							        },
							        label: {
							          display: 'insideEnd',
							            field: 'data1',
							            renderer: Ext.util.Format.numberRenderer('0'),
							            orientation: 'horizontal',
							            color: '#333',
							            'text-anchor': 'middle'
							        },
							        xField: 'name',
							        yField: ['data1']
							    }]
							});

  	} 
});