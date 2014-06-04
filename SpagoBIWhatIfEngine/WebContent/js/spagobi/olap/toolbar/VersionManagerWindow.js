/** SpagoBI, the Open Source Business Intelligence suite
 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * 
 * Window that allows the management of versions 
 * 
 *     
 *  @author
 *  Alberto Ghedin (alberto.ghedin@eng.it)
 */



Ext.define('Sbi.olap.toolbar.VersionManagerWindow', {
	extend: 'Ext.window.Window',

	config:{
		height: 200,
		width: 400,
		actualVersion: null,
		title: LN("sbi.olap.toolbar.versionmanagerwindow.version.title")
	},

	/**
     * @property {Ext.grid.Panel} grid
     *  The grid panel with the versions
     */
	grid: null,
	
	constructor : function(config) {
		this.initConfig(config);
		if(Sbi.settings && Sbi.settings.olap && Sbi.settings.olap.toolbar && Sbi.settings.olap.toolbar.VersionManagerWindow) {
			Ext.apply(this, Sbi.settings.olap.toolbar.VersionManagerWindow);
		}

		var service = Ext.create("Sbi.service.RestService",{
			url: "version"
		});

		var store = Ext.create('Ext.data.Store', {
			model: 'Sbi.olap.VersionModel',
			proxy: {
				type: 'rest',
				url: service.getRestUrlWithParameters(),
				extraParams: service.getRequestParams()
			},
			autoLoad: true
		});

		var selectionModel = Ext.create('Ext.selection.CheckboxModel');
		this.grid = Ext.create('Ext.grid.Panel', {
			store: store,
			selModel: selectionModel,
			selection: "checkboxmodel",
			columns: [
			          { text: 'Id',  dataIndex: 'id' },
			          { text: 'Description', dataIndex: 'description', flex: 1 }
			          ]
		});



		var thisPanel = this;
		this.items= [this.grid];
		this.bbar = [
		             '->',    {
		            	 text: LN('sbi.common.cancel'),
		            	 handler: function(){
		            		 thisPanel.destroy();
		            	 }
		             },    {
		            	 text: LN('sbi.common.select'),
		            	 handler: function(){
		            		 var selected = thisPanel.grid.getSelectionModel( ).getSelection();
		            		 var itemsToDelete = "";
		            		 if(selected && selected.length>0){
		            			 
		            			 if(selected.length == thisPanel.grid.getStore().getCount( )){
		            				 Sbi.exception.ExceptionHandler.showWarningMessage(LN("sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.all"));
		            				 return;
		            			 }
		            			 
		            			 for(var i=0; i<selected.length; i++){
		            				 var id = selected[i].get("id");
		            				 if(id==this.actualVersion){
		            					 Sbi.exception.ExceptionHandler.showWarningMessage(LN("sbi.olap.toolbar.versionmanagerwindow.version.no.cancel.current"));
		            					 return;
		            				 }
		            				 itemsToDelete = itemsToDelete+ ","+ id;
		            			 }
		            		 }

		            		 if(itemsToDelete.length>1){
		            			 itemsToDelete = itemsToDelete.substring(1);
		            			 Sbi.olap.eventManager.deleteVersions(itemsToDelete);
		            		 }
		            		 
		            		 
		            		 return itemsToDelete;
		            	 }
		             }];

		this.callParent(arguments);
	}




});
