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

Sbi.kpi.ManageModelsTree = function(config, ref) { 
	var paramsList = {MESSAGE_DET: "MODEL_NODES_LIST"};
	this.configurationObject = {};
	
	this.configurationObject.manageTreeService = Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'MANAGE_MODELS_ACTION'
		, baseParams: paramsList
	});	

	//reference to viewport container
	this.referencedCmp = ref;
	this.initConfigObject();
	config.configurationObject = this.configurationObject;

	var c = Ext.apply({}, config || {}, {});
	Sbi.kpi.ManageModelsTree.superclass.constructor.call(this, c);	 	
}

Ext.extend(Sbi.kpi.ManageModelsTree, Sbi.widgets.TreeModelPanel, {
	
	configurationObject: null
	, gridForm:null
	, mainElementsStore:null
	, root:null
	, referencedCmp : null

	,initConfigObject: function(){

		this.configurationObject.treeTitle = LN('sbi.models.treeTitle');
		
    }
	,renderTree : function(tree) {
		tree.getLoader().nodeParameter = 'modelId';
		tree.getRootNode().expand(false, /*no anim*/false);
	}

	,setListeners : function() {
		this.modelsTree.addListener('render', this.renderTree, this);
	
	}
	,createRootNodeByRec: function(rec) {
		var iconClass = '';
		var cssClass = '';
		if (rec.get('kpi') !== undefined && rec.get('kpi') != null
				&& rec.get('kpi') != '') {
			iconClass = 'has-kpi';
		}
		if (rec.get('error') !== undefined && rec.get('error') != false) {
			cssClass = 'has-error';
		}
		var node = new Ext.tree.AsyncTreeNode({
	        text		: this.rootNodeText,
	        expanded	: true,
	        leaf		: false,
			modelId 	: this.rootNodeId,
			id			: this.rootNodeId,
			label		: rec.get('label'),
			type		: rec.get('type'),
			typeId		: rec.get('typeId'),
			description	: rec.get('description'),
			typeDescr	: rec.get('typeDescr'),
			kpi			: rec.get('kpi'),
			kpiId		: rec.get('kpiId'),
			code		: rec.get('code'),
			name		: rec.get('name'),
			iconCls		: iconClass,
			cls			: cssClass,
	        draggable	: false
	    });
		return node;
	}
});
