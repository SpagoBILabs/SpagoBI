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
 * Authors - Davide Zerbetto (davide.zerbetto@eng.it)
 */

Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.ChooseImageWindow = function(config) { 

	var defaultSettings = {
		title: LN('sbi.worksheet.designer.chooseimagewindow.title')
	};
		
	if (Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.chooseImageWindow) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.chooseImageWindow);
	}
	
	var c = Ext.apply(defaultSettings, config || {});
	
	Ext.apply(this, c);
	
	// services
	this.services = this.services || new Array();
	
	var params = {LIGHT_NAVIGATOR_DISABLED: 'TRUE'};
	
	this.services['getImagesList'] = this.services['getImagesList'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_WORKSHEET_IMAGES_LIST_ACTION'
		, baseParams: params
	});
	this.services['getImageContent'] = this.services['getImageContent'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_IMAGE_CONTENT_ACTION'
		, baseParams: params
	});
	
	this.init();
	
	var dataviewHeight = this.height-this.loadImageFileBrows.height-30;
	if(dataviewHeight==null || dataviewHeight<=0 || isNaN(dataviewHeight)){
		c.autoScroll= true;	
		dataviewHeight='auto';
	}
	
	c = Ext.apply(c, {
		id : 'choose-image-window'
        , items : [this.loadImageFileBrows,new Ext.Panel({height: dataviewHeight, autoScroll: true, layout: 'fit', items: [new Ext.DataView({
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
        })]})]
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
		this.initImageUpload();
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
	            '			<img class="image-preview" src="' + this.services['getImageContent'] + '&FILE_NAME={image}" title="{image}">',
	            //'			<img src="/SpagoBIQbeEngine/temp/{image}" title="{image}" max-width="100px" max-height="100px">',
	            //'			<img class="image-preview" src="/SpagoBIQbeEngine/GetImageContentServlet?FILE_NAME={image}" title="{image}">',
	            '		</div>',
	            '		<span class="x-editable">{image:ellipsis(20)}</span>',
	            '	</div>',
	            '</div>',
	        '</tpl>',
	        '<div class="x-clear"></div>'
		);
	},
	
	initImageUpload: function(){
		//text field for load the image in the server from the file system
		this.imgFile = new Ext.form.TextField({
			inputType:	'file',
			fieldLabel: LN('sbi.worksheet.designer.image'),
			//anchor:			'95%',
			allowBlank: true
		});
		
		
		this.imgFileFormPanel = new Ext.FormPanel({
			border: false,
			columnWidth: 0.8,
			fileUpload: true,
			items: [this.imgFile]
		});
		
		//Panel with the load file field
		this.loadImageFileBrows = new Ext.Panel({
			height: 45,
			layout:'column',
			frame: true,
			header: false,
			border: false,
			padding: '5 5 5 5',
			items: [
			        this.imgFileFormPanel ,
			        {
			        	xtype:          'button',
			        	border: 		false,
			        	handler:		this.uploadImgButtonHandler,
			        	columnWidth:	0.1,
			        	scope: 			this,
			        	tooltip: 		LN('sbi.worksheet.designer.sheettitlepanel.uploadimage'),
			        	style:			'padding-left: 5px',
			        	iconCls:		'uploadImgIcon'
			        }
			        ]
		});
	},
	
	//handler for the upload image button
	//This button hides the file input field 
	//and shows the load file combo box
	uploadImgButtonHandler: function(btn, e) {
		
        var form = this.imgFileFormPanel.getForm();
       // if(form.isValid()){
            form.submit({
                url: Sbi.config.serviceRegistry.getBaseUrlStr({}), // a multipart form cannot contain parameters on its main URL;
                												   // they must POST parameters
                params: {
                    ACTION_NAME: 'UPLOAD_WORKSHEET_IMAGE_ACTION'
                    , SBI_EXECUTION_ID: Sbi.config.serviceRegistry.getExecutionId()
                },
                waitMsg: 'Uploading your image...',
                success: function(form, action) {
        			Ext.Msg.show({
     				   title: LN('sbi.worksheet.designer.sheettitlepanel.uploadfile.confirm.title'),
     				   msg: LN('sbi.worksheet.designer.sheettitlepanel.uploadfile.confirm.msg'),
     				   buttons: Ext.Msg.OK,
     				   icon: Ext.MessageBox.INFO
     				});
       				this.store.load();
                },
                failure : function (form, action) {
        			Ext.Msg.show({
      				   title: 'Error',
      				   msg: action.result.msg,
      				   buttons: Ext.Msg.OK,
      				   icon: Ext.MessageBox.ERROR
      				});
                },
                scope : this
            });
        //}
	}


});
