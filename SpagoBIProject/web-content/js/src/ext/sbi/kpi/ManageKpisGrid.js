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
 * Authors - Monica Franceschini
 */
Ext.ns("Sbi.kpi");

Sbi.kpi.ManageKpisGrid = function(config, ref) {
	
	var readonlyStrict = config.readonlyStrict; 
	var paramsList = {MESSAGE_DET: "KPIS_LIST"};

	this.configurationObject = {};
	
	this.configurationObject.manageListService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_KPIS_ACTION'
		, baseParams: paramsList
	});

	
	this.referencedCmp = ref;
	this.initConfigObject();
	config.configurationObject = this.configurationObject;

	config.readonlyStrict = readonlyStrict;
	var c = Ext.apply({}, config || {}, {});

	Sbi.kpi.ManageKpisGrid.superclass.constructor.call(this, c);	
	
	this.addEvents('selected');
};

Ext.extend(Sbi.kpi.ManageKpisGrid, Sbi.widgets.ListGridPanel, {
	
	configurationObject: null
	, treeConfigObject: null
	, gridForm:null
	, mainElementsStore:null
	, referencedCmp : null
	, emptyRecord: null

	,initConfigObject:function(){
	
		this.configurationObject.idKey = 'id';
		this.configurationObject.referencedCmp = this.referencedCmp;
		
	    this.configurationObject.fields = ['id'
		                     	          , 'name'
		                    	          , 'code'
		                    	          , 'description'   
		                    	          , 'weight' 
		                    	          , 'dataset'
		                    	          , 'threshold'
		                    	          , 'documents'
		                    	          , 'interpretation'
		                    	          , 'algdesc'
		                    	          , 'inputAttr'
		                    	          , 'modelReference'
		                    	          , 'targetAudience'
		                    	          , 'kpiTypeCd'
		                    	          , 'metricScaleCd'
		                    	          , 'measureTypeCd'
		                    	          ];

		this.configurationObject.gridColItems = [
		                                         {id:'name',header: LN('sbi.generic.name'), width: 125, sortable: true, locked:false, dataIndex: 'name'},
		                                         {header: LN('sbi.generic.code'), width: 125, sortable: true, dataIndex: 'code'}
		                                        ];
		
		this.configurationObject.panelTitle = LN('sbi.kpis.panelTitle');
		this.configurationObject.listTitle = LN('sbi.kpis.listTitle');
		this.configurationObject.dragndropGroup ='grid2treeAndDetail';

    }

	
	
});