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
Ext.ns("Sbi.widgets");

Sbi.widgets.ModelsInstanceTree = function(config, ref) { 
	
	this.addEvents();
	this.configurationObject = config;

	this.referencedCmp = ref;
	this.initConfigObject();
	
	var c = Ext.apply({}, config || {}, {});
	Sbi.widgets.ModelsInstanceTree.superclass.constructor.call(this, c);
	this.on('render', function(){this.getRootNode().expand(false, /*no anim*/false);}, this)
};

Ext.extend(Sbi.widgets.ModelsInstanceTree , Sbi.widgets.SimpleTreePanel, {
	
	configurationObject: null
	, root:null
	, referencedCmp : null
	, initConfigObject: function(){

		var thisPanel = this;
		this.treeLoader =new Ext.tree.TreeLoader({
			nodeParameter: 'modelInstId',
			dataUrl: this.configurationObject.manageTreeService,
			baseParams : this.configurationObject.treeLoaderBaseParameters,
	        createNode: function(attr) {
			
	            if (attr.modelInstId) {
	                attr.id = attr.modelInstId;
	            }

	    		if (attr.kpiInstId !== undefined && attr.kpiInstId !== null
	    				&& attr.kpiInstId != '') {
	    			attr.iconCls = 'has-kpi';
	    		}
	    		if (attr.error !== undefined && attr.error !== false) {
	    			attr.cls = 'has-error';
	    		}
	    		var attrKpiCode = '';
	    		if(attr.kpiCode !== undefined){
	    			attrKpiCode = ' - '+attr.kpiCode;
	    		}
	    		if(attr.kpiInstActive !== undefined && attr.kpiInstActive){
	    			attr.disabled = attr.kpiInstActive;
	    			attr.cls = attr.cls+' line-through';
	    		}
				if(thisPanel.configurationObject.checkbox){
					attr.checked= false;
				}
				
	    		attr.qtip = attr.modelCode+' - '+attr.name+ attrKpiCode;
	            return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
	        }
		});
    }



});
