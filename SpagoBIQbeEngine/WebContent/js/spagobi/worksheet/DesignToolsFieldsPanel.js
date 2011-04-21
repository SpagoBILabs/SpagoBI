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
Ext.ns("Sbi.worksheet");

Sbi.worksheet.DesignToolsFieldsPanel = function(config) { 

	var c ={
			html: 'ciao'
	}
	Sbi.worksheet.DesignToolsFieldsPanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.DesignToolsFieldsPanel, Ext.Panel, {
	
//    var myData = {
//    		records : [
//    			{ name : "Rec 0"},
//    			{ name : "Rec 1"},
//    			{ name : "Rec 2"},
//    			{ name : "Rec 3"},
//    			{ name : "Rec 4"},
//    			{ name : "Rec 5"},
//    			{ name : "Rec 6"},
//    			{ name : "Rec 7"},
//    			{ name : "Rec 8"},
//    			{ name : "Rec 9"}
//    		]
//    	};
//
//
//    	// Generic fields array to use in both store defs.
//    	var fields = [
//    		{name: 'name', mapping : 'name'}
//    	];
//
//        // create the data store
//        var firstGridStore = new Ext.data.JsonStore({
//            	fields : fields,
//    		data   : myData,
//    		root   : 'records'
//        });
//
//
//    	// Column Model shortcut array
//    	var cols = [
//    		{ id : 'name', header: "Record Name", width: 160, sortable: true, dataIndex: 'name'},
//    		{header: "column1", width: 50, sortable: true, dataIndex: 'column1'},
//    		{header: "column2", width: 50, sortable: true, dataIndex: 'column2'}
//    	];
//
//    	// declare the source Grid
//        var firstGrid = new Ext.grid.GridPanel({
//    	ddGroup          : 'secondGridDDGroup',
//            store            : firstGridStore,
//            columns          : cols,
//    	enableDragDrop   : true,
//            stripeRows       : true,
//            autoExpandColumn : 'name',
//            title            : 'First Grid'
//        });
//
//        var secondGridStore = new Ext.data.JsonStore({
//            fields : fields,
//    		root   : 'records'
//        });
//
//        // create the destination Grid
//        var secondGrid = new Ext.grid.GridPanel({
//    	ddGroup          : 'firstGridDDGroup',
//            store            : secondGridStore,
//            columns          : cols,
//    	enableDragDrop   : true,
//            stripeRows       : true,
//            autoExpandColumn : 'name',
//            title            : 'Second Grid'
//        });
//
//
//    	//Simple 'border layout' panel to house both grids
//    	var displayPanel = new Ext.Panel({
//    		width        : 650,
//    		height       : 300,
//    		layout       : 'hbox',
//    		renderTo     : 'panel',
//    		defaults     : { flex : 1 }, //auto stretch
//    		layoutConfig : { align : 'stretch' },
//    		items        : [
//    			firstGrid,
//    			secondGrid
//    		],
//    		bbar    : [
//    			'->', // Fill
//    			{
//    				text    : 'Reset both grids',
//    				handler : function() {
//    					//refresh source grid
//    					firstGridStore.loadData(myData);
//
//    					//purge destination grid
//    					secondGridStore.removeAll();
//    				}
//    			}
//    		]
//    	});
//
//    	// used to add records to the destination stores
//    	var blankRecord =  Ext.data.Record.create(fields);
//
//            /****
//            * Setup Drop Targets
//            ***/
//            // This will make sure we only drop to the  view scroller element
//            var firstGridDropTargetEl =  firstGrid.getView().scroller.dom;
//            var firstGridDropTarget = new Ext.dd.DropTarget(firstGridDropTargetEl, {
//                    ddGroup    : 'firstGridDDGroup',
//                    notifyDrop : function(ddSource, e, data){
//                            var records =  ddSource.dragData.selections;
//                            Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
//                            firstGrid.store.add(records);
//                            firstGrid.store.sort('name', 'ASC');
//                            return true
//                    }
//            });
//
//
//            // This will make sure we only drop to the view scroller element
//            var secondGridDropTargetEl = secondGrid.getView().scroller.dom;
//            var secondGridDropTarget = new Ext.dd.DropTarget(secondGridDropTargetEl, {
//                    ddGroup    : 'secondGridDDGroup',
//                    notifyDrop : function(ddSource, e, data){
//                            var records =  ddSource.dragData.selections;
//                            Ext.each(records, ddSource.grid.store.remove, ddSource.grid.store);
//                            secondGrid.store.add(records);
//                            secondGrid.store.sort('name', 'ASC');
//                            return true
//                    }
//            });
//	
//	
//	
	
	
});
