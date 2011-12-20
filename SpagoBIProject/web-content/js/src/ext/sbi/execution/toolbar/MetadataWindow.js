/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
 * Authors - Chiara Chiarelli (chiara.chiarelli@eng.it)
 */
Ext.ns("Sbi.execution.toolbar");

Sbi.execution.toolbar.MetadataWindow = function(config) {

    var params = {
        LIGHT_NAVIGATOR_DISABLED : 'TRUE',
        OBJECT_ID: config.OBJECT_ID,
        SUBOBJECT_ID: config.SUBOBJECT_ID
    };
    this.services = new Array();

    this.services['getMetadataService'] = Sbi.config.serviceRegistry
            .getServiceUrl({
                serviceName : 'GET_METADATA_ACTION',
                baseParams : params
            });

    this.services['saveMetadataService'] = Sbi.config.serviceRegistry
            .getServiceUrl({
                serviceName : 'SAVE_METADATA_ACTION',
                baseParams : params
            });
            
 	// store for general metadata
    this.generalMetadataStore = new Ext.data.SimpleStore({
        fields : ['meta_name', 'meta_content' ]
    });

    // store for short text metadata
    this.shortTextMetadataStore = new Ext.data.SimpleStore({
        fields : [ 'meta_id', 'meta_name', 'meta_content' ]
    });
    
    // store for long text metadata
    this.longTextMetadataStore = new Ext.data.SimpleStore({
        fields : [ 'meta_id', 'meta_name', 'meta_content' ]
    });
    
    // store for all metadata
    this.metadataStore = new Ext.data.JsonStore({
        autoLoad: false,
        fields: ['meta_id', 'biobject_id', 'subobject_id', 'meta_name', 'meta_type', 'meta_content',
                   {name:'meta_creation_date', type:'date', dateFormat: Sbi.config.clientServerTimestampFormat}, 
                   {name:'meta_change_date', type:'date', dateFormat: Sbi.config.clientServerTimestampFormat}
                ]
        , url: this.services['getMetadataService']
    });
    this.metadataStore.on('load', this.init, this);
    this.metadataStore.load();
    
    // init  
    this.initGeneralMetadataGridPanel();
    this.initShortTextMetadataGridPanel();
    this.initLongTextMetadataTabPanel();
    
    var buttons = [];
   if (Sbi.user.functionalities.contains('SaveMetadataFunctionality')) {
        buttons.push({
            text : LN('sbi.execution.metadata.savemeta')
            , scope : this
            , handler : this.saveMetadata
        });
    }
    
    var c = Ext.apply( {}, config, {
        id : 'win_metadata',
        items : this.generalMetadataPanel ,
        buttons : buttons,
        width : 650,
        height : 410,
        plain : true,
        autoScroll : true,
        title : LN('sbi.execution.metadata')
    });

    this.buddy = undefined;
    
    // constructor
    Sbi.execution.toolbar.MetadataWindow.superclass.constructor.call(this, c);

    if (this.buddy === undefined) {
        this.buddy = new Sbi.commons.ComponentBuddy( {
            buddy : this
        });
    }

};

Ext.extend(Sbi.execution.toolbar.MetadataWindow, Ext.Window, {
    services : null
    , metadataStore : null
    , shortTextMetadataStore : null
    , longTextMetadataStore : null
    , generalMetadataStore: null
    , shortTextMetadataPanel : null
    , generalMetadataPanel : null

    , init: function () {
        
        for (var i = 0; i < this.metadataStore.getCount(); i++) {
            var aRecord = this.metadataStore.getAt(i);
            if (aRecord.data.meta_type == 'SHORT_TEXT') {
            	
                this.shortTextMetadataStore.add(aRecord);
                
            }else if (aRecord.data.meta_type == 'GENERAL_META') {
            	
                this.generalMetadataStore.add(aRecord);
            } else {
            	
                this.longTextMetadataStore.add(aRecord);
                var html = aRecord.data.meta_content !== '' ? aRecord.data.meta_content : '<br/>';
                
                var editablePanel = new Sbi.widgets.EditablePanel({
                    "title" : aRecord.data.meta_name
                    , "html" : html
                    , "height" : 246
                    , "editable" : Sbi.user.functionalities.contains('SaveMetadataFunctionality')
                    , "fieldName" : aRecord.data.meta_id
                });
                
                editablePanel.on('activate', function(panel) {
                    panel.doLayout();
                }, this);
                
                editablePanel.on('change', function(panel, newValue) {
                    var index = this.longTextMetadataStore.find('meta_id', panel.fieldName);
                    var record = this.longTextMetadataStore.getAt(index);
                    record.set('meta_content', newValue);
                }, this);
                
                this.longTextMetadataTabPanel.add(editablePanel);
            }
        }
        
        
        
        if(this.shortTextMetadataStore.getCount() !== 0 ){
	    	this.add(this.shortTextMetadataPanel);
	    }
     
        if(this.longTextMetadataStore.getCount() !== 0 ){
    		 this.add(this.longTextMetadataPanel);
    	} 
	      
	    this.doLayout();
        
    }
    
    , initGeneralMetadataGridPanel : function() {
                     
        var generalMetadataGridPanel = new Ext.grid.GridPanel({
            store : this.generalMetadataStore,
            autoHeight : true,
            columns : [ {
                header : LN('sbi.execution.metadata.metaname'),
                width : 100,
                sortable : true,
                dataIndex : 'meta_name'
            }, {
                header : LN('sbi.execution.metadata.metavalue'),
                width : 540,
                sortable : true,
                dataIndex : 'meta_content',
                renderer: Ext.util.Format.htmlEncode
            } ],
            viewConfig : {
                forceFit : true,
                scrollOffset : 2
            // the grid will never have scrollbars
            }
        });

        this.generalMetadataPanel = new Ext.Panel({
            title : LN('sbi.execution.metadata.generalmetadata'),
            layout : 'fit',
            collapsible : true,
            collapsed : false,
            items : [ generalMetadataGridPanel ],
            autoWidth : true,
            autoHeight : true
        });
    }

    , initShortTextMetadataGridPanel : function() {
        
        var shortTextMetadataGridPanel = new Ext.grid.EditorGridPanel({
            store : this.shortTextMetadataStore,
            autoHeight : true,
            columns : [ {
                header : LN('sbi.execution.metadata.metaname'),
                width : 100,
                sortable : true,
                dataIndex : 'meta_name'
            }, {
                header : LN('sbi.execution.metadata.metavalue'),
                width : 540,
                sortable : true,
                dataIndex : 'meta_content',
                editor : Sbi.user.functionalities.contains('SaveMetadataFunctionality') ? new Ext.form.TextField({}) : undefined
            } ],
            viewConfig : {
                forceFit : true,
                scrollOffset : 2
            // the grid will never have scrollbars
            },
            singleSelect : true,
            clicksToEdit : 2
        });

        this.shortTextMetadataPanel = new Ext.Panel({
            title : LN('sbi.execution.metadata.shorttextmetadata'),
            layout : 'fit',
            collapsible : true,
            collapsed : false,
            items : [ shortTextMetadataGridPanel ],
            autoWidth : true,
            autoHeight : true
        });
    }
    
    , initLongTextMetadataTabPanel : function() {

        this.longTextMetadataTabPanel = new Ext.TabPanel({
            enableTabScroll : true
            , activeTab : 0
            , autoScroll : true
            //, hideMode : 'offsets'
        });

        this.longTextMetadataPanel = new Ext.Panel( {
            title : LN('sbi.execution.metadata.longtextmetadata'),
            collapsible : true,
            collapsed : false,
            items : [ this.longTextMetadataTabPanel ],
            height : 300
        });
        
    }

    , saveMetadata : function() {
        var modifiedRecords = new Array();
        modifiedRecords = modifiedRecords.concat(this.shortTextMetadataStore.getModifiedRecords());
        modifiedRecords = modifiedRecords.concat(this.longTextMetadataStore.getModifiedRecords());
        var modifiedMetadata = new Array();
        for (var i = 0; i < modifiedRecords.length; i++) {
            modifiedMetadata.push(modifiedRecords[i].data);
        }
        var params = {
            METADATA: Ext.util.JSON.encode(modifiedMetadata)
        };
        
        Ext.MessageBox.wait(LN('sbi.execution.metadata.waitmessage'), LN('sbi.execution.metadata.waittitle'));
        Ext.Ajax.request({
            url: this.services['saveMetadataService'],
            success: function(response, options) {
				if (response !== undefined) {
					Ext.MessageBox.hide();
					//this.shortTextMetadataStore.commitChanges();
					//var editablePanels = this.longTextMetadataTabPanel.items;
					//editablePanels.each(function() {this.commitChanges();});
					//for (var i = 0; i < editablePanels.getCount(); i++) {
					//	editablePanels.get(i).commitChanges();
					//}
					this.close();
				} else {
					Sbi.exception.ExceptionHandler.showErrorMessage('Error while saving Metadata', 'Service Error');
				}
            },
            failure: Sbi.exception.ExceptionHandler.handleFailure,    
            scope: this,
            params: params
        });
    }

});