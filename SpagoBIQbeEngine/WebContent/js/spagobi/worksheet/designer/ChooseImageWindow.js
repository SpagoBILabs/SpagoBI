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
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */

Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.ChooseImageWindow = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.chooseimagewindow.title')
		, autoScroll: true
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.chooseImageWindow) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.chooseImageWindow);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	// services
	this.services = this.services || new Array();	
	this.services['getImagesList'] = this.services['getImagesList'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_WORKSHEET_IMAGES_LIST_ACTION'
		, baseParams: new Object()
	});
	this.services['getImageContent'] = this.services['getImageContent'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_IMAGE_CONTENT_ACTION'
		, baseParams: new Object()
	});
	
	this.init();
	
	c = Ext.apply(c, {
		id : 'choose-image-window'
        , items : new Ext.DataView({
            store : this.store,
            tpl : this.tpl,
            autoHeight : true,
            multiSelect : true,
            overClass : 'x-item-over',
            itemSelector : 'div.thumb-wrap',
            emptyText : 'No images to display',
            listeners : {
                'click' : {
                	fn: function( dv, index, node, e ) {
	                	var record = this.store.getAt(index);
	                	var fileName = record.data.image;
	                	this.fireEvent('select', this, fileName);
                	}, 
                	scope : this
                }
            }
        })
	});

	Sbi.worksheet.designer.ChooseImageWindow.superclass.constructor.call(this, c);
	
	this.addEvents('select');
	
};

Ext.extend(Sbi.worksheet.designer.ChooseImageWindow, Ext.Window, {

	store : null
	, services : null
	, tpl : null
	
	, init : function () {
		this.initStore();
		this.initTemplate();
	}

	, initStore : function () {
		this.store = new Ext.data.ArrayStore({
		    fields: ['image', 'url']
			, url   : this.services['getImagesList']
			, autoLoad : true
		});
	}
	
	, initTemplate : function () {
		this.tpl = new Ext.XTemplate(
		    '<tpl for=".">',
	            '<div class="thumb-wrap" id="{image}" style="float: left; margin: 10px; padding: 5px; width: 100px; height: 110px">',
	            '	<div class="thumb">',
	            '		<div style="width:100px;height:100px">',
	            '			<img src="' + this.services['getImageContent'] + '&FILE_NAME={image}" title="{image}" max-width="100px" max-height="100px">',
	            //'			<img src="/SpagoBIQbeEngine/temp/{image}" title="{image}" max-width="100px" max-height="100px">',
	            '		</div>',
	            '		<span class="x-editable">{image:ellipsis(20)}</span>',
	            '	</div>',
	            '</div>',
	        '</tpl>',
	        '<div class="x-clear"></div>'
		);
	}

});
