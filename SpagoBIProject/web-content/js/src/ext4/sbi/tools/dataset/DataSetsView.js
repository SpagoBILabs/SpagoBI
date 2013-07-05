/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/

/**
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
		
		sorters : new Array(),
		
		autoScroll : true,
		
		fromMyDataCtx : true

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
		var changed = LN('sbi.ds.changedon');
		var title = LN('sbi.ds.listTitle');
		/*
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
									'<img src="/SpagoBI/themes/sbi_default/img/dataset/csv-xls.png" width="110" style="margin-left: 40px; margin-top: 10px;">' ,
									'<p><b>{name}</b></p>',
									'<p>{description}</p>',
									'<p>{dateIn}</p><br/>',
								'</div>',
							'</dd>',
		                '</tpl>',	              
	 	            '<div style="clear:left"></div>',
		          '</ul>',
	 	          '</div>',
	 	        '</div>');
		*/
		var img = "csv-xls-smaller.png";
//		if (!this.fromMyDataCtx ){
//			img = "csv-xls-small.png";
//		}
		this.tpl = new Ext.XTemplate(
				'<div id="dataset-view">', 	            
	 	           '<div class="dataset-group-view">',
	 	            '<ul>',
	 	            	'<tpl if="root.length == 0">',
	 	            		'<div id="empty-group-message">',
	 	            		noItem,
	 	            		'</div>',
	 	            	'</tpl>', 
	 	            	'<tpl for=".">',
							'<dd class="box">',
								'<a href="#" class="box-link">',
									'<div class="box-map">',
										'<img  align="center" src="/SpagoBI/themes/sbi_default/img/dataset/'+img+'" alt=" " />',
										'<span class="shadow"></span>',
									'</div>',
									'<div class="box-text">',
										'<h2>{name}</h2>',
										'<p>{[Ext.String.ellipsis(values.description, 100, false)]}</p>',
										'<p class="modified">'+changed+' {dateIn}</p>',
									'</div>',
								'</a>',
								'<div class="fav-container" style="width:{actions.length*45}px">',
									'<tpl for="actions">',   
										'<div class="fav">',
											'<span class="icon-{name}" title="{description}"></span>',
										'</div>',
									'</tpl>',
								'</div>',
							'</dd>',
						 '</tpl>',	 
						 '<div style="clear:left"></div>',
					'</ul>',
				'</div>',
			'</div>'
		);
     
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

        var actionDetail = e.getTarget('span.icon-detail', 10, true);
        var actionDelete = e.getTarget('span.icon-delete', 10, true);
        var actionWorksheet = e.getTarget('span.icon-worksheet', 10, true);
        var actionGeoreport = e.getTarget('span.icon-georeport', 10, true);
        
        if (!this.fromMyDataCtx) actionWorksheet = true;
        
        delete record.data.actions; 
        if (actionDetail != null){
        	Sbi.debug('DataSetView view detail raise event...');        	
        	scope.fireEvent('detail', record.data);   
        } else if (actionDelete != null){
        	Sbi.debug('DataSetView delete dataset raise event...');        	
        	scope.fireEvent('delete', record.data);
        } else if (actionWorksheet != null){
        	Sbi.debug('DataSetView actionWorksheet raise event...'); 
        	if (record.data.pars != undefined && record.data.pars != ''){
        		Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.ds.noWorksheetDesigner'));
        		return true;
        	}
   			scope.fireEvent('executeDocument','WORKSHEET','DATASET',record);
        } else if (actionGeoreport != null){
        	Sbi.debug('DataSetView actionGeoreport raise event...'); 
        	if (record.data.pars != undefined && record.data.pars != ''){
        		Sbi.exception.ExceptionHandler.showInfoMessage(LN('sbi.ds.noGeoreportDesigner'));
        		return true;
        	}
   			scope.fireEvent('executeDocument','GEOREPORT','DATASET',record);
        } else {
        	scope.fireEvent('detail', record.data);          	
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