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


Ext.define('Ext.chart.theme.ExtJSChartTheme', {
    extend: 'Ext.chart.theme.Base',
    requires: ['Ext.chart.theme.Base','Ext.chart.theme.Theme'],
    
    constructor: function(config) {
    /*	Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
							    					colors: config.colors// ['#94AE0A','#C0C0C0','#a61120']
							    				  , baseColor: config.baseColor
												}, config));
    	*/
    	Ext.chart.theme.Base.prototype.constructor.call(this,  config);
      //  Ext.apply(this, config);
        //this.callParent(arguments);
    	
      
      //  var theme = Ext.chart.createTheme(config);
        
        return this;
    }
   	/*
    constructor: function(config) {
        this.callParent([Ext.apply({
            axis: {
                fill: config.baseColor,
                stroke: config.baseColor
            },
            axisLabelLeft: {
                fill: config.baseColor
            },
            axisLabelBottom: {
                fill: config.baseColor
            },
            axisTitleLeft: {
                fill: config.baseColor
            },
            axisTitleBottom: {
                fill: config.baseColor
            },
            colors: config.colors
        }, config)]);
    }

*/
/*
 *   axis: {
        fill: '#000',
        'stroke-width': 1
    },
    axisLabelTop: {
        fill: '#000',
        font: '11px Arial'
    },
    axisLabelLeft: {
        fill: '#000',
        font: '11px Arial'
    },
    axisLabelRight: {
        fill: '#000',
        font: '11px Arial'
    },
    axisLabelBottom: {
        fill: '#000',
        font: '11px Arial'
    },
    axisTitleTop: {
        fill: '#000',
        font: '11px Arial'
    },
    axisTitleLeft: {
        fill: '#000',
        font: '11px Arial'
    },
    axisTitleRight: {
        fill: '#000',
        font: '11px Arial'
    },
    axisTitleBottom: {
        fill: '#000',
        font: '11px Arial'
    },
    series: {
        'stroke-width': 1
    },
    seriesLabel: {
        font: '12px Arial',
        fill: '#333'
    },
    marker: {
        stroke: '#555',
        fill: '#000',
        radius: 3,
        size: 3
    },
    seriesThemes: [{
        fill: '#C6DBEF'
    }, {
        fill: '#9ECAE1'
    }, {
        fill: '#6BAED6'
    }, {
        fill: '#4292C6'
    }, {
        fill: '#2171B5'
    }, {
        fill: '#084594'
    }],
    markerThemes: [{
        fill: '#084594',
        type: 'circle'
    }, {
        fill: '#2171B5',
        type: 'cross'
    }, {
        fill: '#4292C6',
        type: 'plus'
    }]
 * */

});

