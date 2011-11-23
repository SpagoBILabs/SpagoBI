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
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.KpiGridPanel =  function(config) {
		
		var defaultSettings = {
		        title: 'Core Team Projects',
		        region:'center',
		        enableDD: true,
		        columns:[{
		            header: 'Task',
		            dataIndex: 'task',
		            width: 230
		        },{
		            header: 'Duration',
		            width: 100,
		            dataIndex: 'duration',
		            align: 'center',
		            sortType: 'asFloat',
		            tpl: new Ext.XTemplate('{duration:this.formatHours}', {
		                formatHours: function(v) {
		                    if(v < 1) {
		                        return Math.round(v * 60) + ' mins';
		                    } else if (Math.floor(v) !== v) {
		                        var min = v - Math.floor(v);
		                        return Math.floor(v) + 'h ' + Math.round(min * 60) + 'm';
		                    } else {
		                        return v + ' hour' + (v === 1 ? '' : 's');
		                    }
		                }
		            })
		        },{
		            header: 'Assigned To',
		            width: 150,
		            dataIndex: 'user'
		        }],

		        dataUrl: 'treegrid-data.json'
				
		};

		if (Sbi.settings && Sbi.settings.kpi && Sbi.settings.kpi.kpiGridPanel) {
			defaultSettings = Ext.apply(defaultSettings, Sbi.settings.kpi.kpiGridPanel);
		}

		var c = Ext.apply(defaultSettings, config || {});

		Ext.apply(this, c);
		
		Sbi.kpi.KpiGridPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.kpi.KpiGridPanel ,Ext.ux.tree.TreeGrid, {});