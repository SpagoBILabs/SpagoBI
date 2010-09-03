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

Sbi.widgets.TreeModelPanel = function(config) {

	var conf = config.configurationObject;
	this.services = new Array();
	this.services['listModelService'] = conf.manageTreeService;
	
	this.tabItems = conf.tabItems;
	this.notDraggable =config.notDraggable;
	//alert(this.notDraggable);
	this.treeTitle = conf.treeTitle;

	this.initWidget();	

	var c = Ext.apply( {}, config, this.modelPanel);

	Sbi.widgets.TreeModelPanel.superclass.constructor.call(this, c);
};

Ext.extend(Sbi.widgets.TreeModelPanel, Ext.FormPanel, {

	gridForm : null,
	tabs : null,
	tabItems : null,
	treeLoader : null,
	rootNode : null,
	rootNodeId : null,
	preloadTree : true,
	rootNodeText : null,
	treeTitle : null,
	importCheck: null,
	treeLoader: null,
	

	initWidget : function() {

		this.modelsTree = new Ext.tree.TreePanel( {
			title : this.treeTitle,
			width : 250,
			height : 300,
			layout: 'fit',
			userArrows : true,
			animate : true,
			autoScroll : true,		
            style: {
                //"background-color": "#f1f1f1",
                "border":"none"
            },
			loader: this.treeLoader,

			preloadTree : this.preloadTree,
			enableDD : true,
            enableDrop: false,
            enableDrag: true,
            ddAppendOnly: false ,
            ddGroup  : 'tree2tree',
			scope : this,
			shadow : true,
			root : {
				nodeType : 'async',
				text : this.rootNodeText,
				modelId : this.rootNodeId,
				id:  this.rootNodeId
			}
		  // ,listeners:{  }
		});
		var label = LN('sbi.modelinstances.importCheck');
		if(this.notDraggable !== undefined && this.notDraggable !== null && this.notDraggable == true){
			label ='';
		}
		this.importCheck = new Ext.form.Checkbox({
             fieldLabel: label,
             allowBlank: false,
         	 inputValue  :'true',
             name: 'importChildrenFlag'
         });
		this.modelPanel = new Ext.form.FormPanel( {
			frame : true,
			labelWidth: 150,  
			autoScroll : true,
			labelAlign : 'left',
			width : 390,
			height : 510,
			layoutConfig : {
				animate : true,
				activeOnTop : false
			},
			trackResetOnLoad : true,
			//, this.importCheck
			items: [this.modelsTree, this.importCheck]
		});
		
		if(this.notDraggable !== undefined && this.notDraggable !== null && this.notDraggable == true){
			this.importCheck.hide();
			this.modelsTree.enableDD = false;
		}
		this.setListeners();
	}

});

