/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
 * TODO anto: aggiornare documentazione!!!
 * 
 * Data view for a browser style. It define a layout and provides the stubs
 * methods for the icons browser. This methods should be overridden to define
 * the logic. Only the methods onAddNewRow, onGridSelect and onCloneRow are
 * implemented. The configuration dataView must be passed to the object. It
 * should contains the method setFormState (it is used by onGridSelect)
 * 
 * 
 * @example Ext.define('Sbi.tools.datasource.DataSourceListDetailPanel', {
 *          extend: 'Sbi.widgets.compositepannel.ListDetailPanel' , constructor:
 *          function(config) { //init services this.initServices(); //init form
 *          this.form =
 *          Ext.create('Sbi.tools.datasource.DataSourceDetailPanel',{services:
 *          this.services}); this.columns = [{dataIndex:"DATASOURCE_LABEL",
 *          header:"Name"}, {dataIndex:"DESCRIPTION", header:"description"}];
 *          this.fields =
 *          ["DATASOURCE_ID","DATASOURCE_LABEL","DESCRIPTION","DRIVER","DIALECT_ID","DIALECT_CLASS","DIALECT_NAME","JNDI_URL","USER","PASSWORD","SCHEMA","MULTISCHEMA","CONNECTION_URL"];
 *          this.form.on("save",this.onFormSave,this);
 *          this.callParent(arguments); } , initServices: function(baseParams){
 *          this.services["getAllValues"]=
 *          Sbi.config.serviceRegistry.getRestServiceUrl({ serviceName:
 *          'datasources/listall' , baseParams: baseParams }); ... } ,
 *          onDeleteRow: function(record){ Ext.Ajax.request({ url:
 *          this.services["delete"], params: {DATASOURCE_ID:
 *          record.get('DATASOURCE_ID')}, ... } , onFormSave: function(record){
 *          Ext.Ajax.request({ url: this.services["save"], params: record, ... }
 *          }); ...
 * 
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */

Ext.define('Sbi.tools.dataset.DataSetsView', {
	extend : 'Ext.DataView'

	,
	config : {
		/**
		 * The Ext.data.Store to bind this DataView to.
		 */
		store : null,

		/**
		 * A simple CSS selector that will be used to determine what nodes this
		 * DataView will be working with.
		 */
		itemSelector : null,

		/**
		 * The definition of the columns of the grid.
		 * {@link Sbi.widgets.store.InMemoryFilteredStore#InMemoryFilteredStore}
		 */
		columns : [],
		/**
		 * The list of the properties that should be filtered
		 */
		filteredProperties : new Array(),
		
		autoScroll : true

	}

	/**
	 * In this constructor you must pass configuration
	 */
	,
	constructor : function(config) {
				
		this.initConfig(config);
		this.initTemplate();
		 
		Ext.apply(this, config || {});
		
		this.itemSelector = 'dd';
		this.trackOver = true;
		this.overItemCls = 'over';
		this.frame = true;
		this.emptyText = LN('sbi.ds.noDataset');
		this.inline = {
			wrap : false
		};
		this.scrollable = 'horizontal';

		this.callParent(arguments);

		this.addListener('itemclick', this.onClick, this);
		this.addListener('itemmouseenter', this.onMouseOverX, this);
		this.addListener('itemmouseleave', this.onMouseOutX, this);
		
		this.addEvents('detail');		
	}
	
	,
	initTemplate : function() {
		// BUILD THE TPL
		Sbi.debug('DataViewPanel bulding the tpl...');

		var noItem = LN('sbi.browser.folderdetailpanel.emptytext');
		var title = LN('sbi.ds.listTitle');
		this.tpl = new Ext.XTemplate(
				'<div id="sample-ct">', 	            
	 	           '<div class="group-view">',
	 	            '<ul>',
	 	            	'<tpl if="root.length == 0">',
	 	            		'<div id="empty-group-message">',
	 	            		noItem,
	 	            		'</div>',
	 	            	'</tpl>',        
	 	            	'<tpl for=".">',
		                    '<dd class="group-item">',
				                '<div class="item-control-panel">',	 
			                    	'<tpl for="actions">',   
			                        	'<div class="button"><img class="action-{name}" title="{description}" src="' + Ext.BLANK_IMAGE_URL + '"/></div>',
			                        '</tpl>',
			                    '</div>',			                    								
								'<div class="dataset-view">' +
									'<img src="/SpagoBI/themes/sbi_default/img/dataset/dataset.png" width="110" style="margin-left: 40px; margin-top: 10px;">' ,
									'<p><b>{name}</b></p>',
									'<p>{description}</p><br/>',
								'</div>',
							'</dd>',
		                '</tpl>',	              
	 	            '<div style="clear:left"></div>',
		          '</ul>',
	 	          '</div>',
	 	        '</div>');
		

		Sbi.debug('DataViewPanel tpl built.');

		return this.tpl;
	}
	
	,
	onRender : function(obj, opt) {
		Ext.DataView.superclass.onRender.call(this, opt);
	}

	, 
	onClick : function(obj, record, item, index, e, eOpts) {
		var scope = this;
        // is of type Ext.EventObject		
        var group = e.getTarget('div[class=group-header]', 10, true);
        if(group){
            group.up('div[class*=group]').toggleClass('collapsed');
        }
        
        var actionDetail = e.getTarget('img.action-detail', 10, true);
        var actionDelete = e.getTarget('img.action-delete', 10, true);
        var actionWorksheet = e.getTarget('img.action-worksheet', 10, true);
        
        delete record.data.actions; 
        if (actionDetail != null){
        	Sbi.debug('DataSetView view detail raise event...');        	
        	scope.fireEvent('detail', record.data);   
        }else if (actionDelete != null){
        	Sbi.debug('DataSetView delete dataset raise event...');        	
        	scope.fireEvent('delete', record.data);
        }else{ //if (actionWorksheet != null){
        	Sbi.debug('DataSetView actionWorksheet raise event...'); 
        	if (record.data.pars != undefined && record.data.pars != ''){
        		Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.ds.noWorksheetDesigner'));
        		return true;
        	}
   			scope.fireEvent('executeDocument','WORKSHEET','DATASET',record);

        }
        return true;
    }

	,
	onMouseOverX : function( obj, record, item, index, e, eOpts ) {
		
		var group = e.getTarget('div[class=group-header]', 10, true);
		if (!group) {
			var d = e.getTarget('[class*=group-item]', 5, true);
			if (d) {
				var t = d.first('div[class*=item-control-panel]', false);
				if (t) {
					t.applyStyles('visibility:visible');
				}
			}
		}
		
		return true;
	}
	

	,
	onMouseOutX : function( obj, record, item, index, e, eOpts ) {
		var group = e.getTarget('div[class=group-header]', 10, true);
		if (!group) {
			var d = e.getTarget('[class*=group-item]', 5, true);
			if (d) {
				var t = d.first('div[class*=item-control-panel]', false);
				if (t) {
					t.applyStyles('visibility:hidden');
				}
			}
		}
		
		return true;
	}


});