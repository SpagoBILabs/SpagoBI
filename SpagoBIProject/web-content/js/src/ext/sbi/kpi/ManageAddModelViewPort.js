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

Sbi.kpi.ManageAddModelViewPort = function(config) { 
	
	var conf = config;

	//DRAW west element
    this.modelsGrid = new Sbi.kpi.ManageModelsGrid(conf, this);
	//DRAW center element
	this.manageModelsTree = new Sbi.kpi.ManageModelsTree(conf, this.modelsGrid);
	
	var viewport = {
		layout: 'border'
		, id: 'model-viewport'
		, height:560
		, autoScroll: true
		, items: [
	         {
	           region: 'west',
	           width: 275,
	           height:560,
	           collapseMode:'mini',
	           autoScroll: true,
	           split: true,
	           layout: 'fit',
	           items:[this.modelsGrid]
	          },
		    {
		       region: 'center',
		       width: 300,
		       height:560,
		       split: true,
		       collapseMode:'mini',
		       autoScroll: true,
		       layout: 'fit',
		       items: [this.manageModelsTree]
		    }
		]	

	};
	
	
	var c = Ext.apply({}, config || {}, viewport);
	
	this.initPanels();

	Sbi.kpi.ManageaddModelViewPort.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.kpi.ManageaddModelViewPort, Ext.Viewport, {
	modelsGrid: null,
	manageModels: null,
	lastRecSelected: null
	
	,initPanels : function() {

		this.modelsGrid.addListener('rowclick', this.sendSelectedItem, this);

	}
	, displayTree: function(rec){
		this.manageModelsTree.rootNodeText = rec.get('code')+ " - "+rec.get('name');
		this.manageModelsTree.rootNodeId = rec.get('modelId');
		var newroot = this.manageModelsTree.createRootNodeByRec(rec);
		this.manageModelsTree.mainTree.setRootNode(newroot);
		
		this.manageModelsTree.mainTree.getSelectionModel().select(newroot);
		this.manageModelsTree.mainTree.doLayout();
	}

	,sendSelectedItem: function(grid, rowIndex, e){
		var rec = grid.getSelectionModel().getSelected();
		this.displayTree(rec);
	}

});
