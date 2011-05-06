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
 * Authors
 * 		Alberto Ghedin  (alberto.ghedin@eng.it)
 * 		Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet.designer");

Sbi.worksheet.designer.SheetTitlePanel = function(config) { 
	
	var defaultSettings = {		
		border: false,
		frame:true,
		style:'padding:5px 15px 5px',  
		fileUpload: true,
		height: 90
	};

	if(Sbi.settings && Sbi.settings.worksheet && Sbi.settings.worksheet.designer && Sbi.settings.worksheet.designer.sheetTitlePanel) {
		defaultSettings = Ext.apply(defaultSettings, Sbi.settings.worksheet.designer.sheetTitlePanel);
	}

	var c = Ext.apply(defaultSettings, config || {});

	Ext.apply(this, c);
	
	Ext.QuickTips.init();  // enable tool tips

	var formItems = [];
	var formRows = 0;	
	
	// services
	this.services = this.services || new Array();	
	this.services['getImagesList'] = this.services['getImagesList'] || Sbi.config.serviceRegistry.getServiceUrl({
		serviceName: 'GET_WORKSHEET_IMAGES_LIST_ACTION'
		, baseParams: new Object()
	});
	// there also the upload service, but it cannot be built with Sbi.config.serviceRegistry.getServiceUrl,
	// since parameters (ACTION_NAME, ...) cannot be put on the service url, but they must be POST parameters 
	
	//row of the title

	formItems = this.addTitle(formItems);
	formItems = this.addImage(formItems);
	
	c = {
        items: [{
            layout:'column',
            items: formItems
        }]
	};
	
	Sbi.worksheet.designer.SheetTitlePanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.designer.SheetTitlePanel, Ext.FormPanel, {
	loadImageCombo: null,
	loadImageFileBrows: null,
	titlePanel: null,
	imgCombo: null,
	imgFile: null,
	imgPosition: null,
	imagesStore: null,
	
	
	//add the title row in the items of the panel
	addTitle:function(items){
		
		this.titlePanel =
			new Ext.form.HtmlEditor({
            	columnWidth:		.6,
            	anchor:				'95%',
                height: 			65,
        	    enableLinks :  		false,
        	    enableLists :		false,
        	    enableSourceEdit : 	false
            });
		
		
		items.push(this.titlePanel);
		return items;
	},
	
	
	//add the image row in the items of the panel
	addImage: function(items){
		
		this.imagesStore = new Ext.data.ArrayStore({
			fields : ['image'],
			url   : this.services['getImagesList']
		});
		
		//combo box with the image.. the store get the image from the server
		this.imgCombo = new Ext.form.ComboBox({
			xtype:          'combo',
			mode:           'remote',
			triggerAction:  'all',
			forceSelection: true,
			allowBlank: 	true,
			editable:       false,
			fieldLabel:     LN('sbi.worksheet.designer.image'),
			name:           'image',
			displayField:   'image',
			valueField:     'image',
			anchor:			'95%',
			store:          this.imagesStore
		});
		
		//text field for load the image in the server from the file system
		this.imgFile = new Ext.form.TextField({
			inputType:	'file',
			fieldLabel: LN('sbi.worksheet.designer.image'),
			anchor:			'95%',
			allowBlank: false
		});
		
		
		//Panel with the load image combo box
		this.loadImageCombo = new Ext.Panel({
            layout:'column',
            items: [{
            	columnWidth:.7,
    			layout: 'form',
    			items: [this.imgCombo]
			},{
				xtype:          'button',
               	width: 			30,
				handler:		this.imgButtonHandler,
				scope: 			this,
				iconCls:		'browsImgIcon'
			}]
		});
		
		this.imgFileFormPanel = new Ext.form.FormPanel({
			fileUpload: true
			, items: [this.imgFile]
		});
		
		//Panel with the load file field
		this.loadImageFileBrows = new Ext.Panel({
            layout:'column',
            hidden: true,
            items: [
                    this.imgFileFormPanel,
              {
				xtype:          'button',
               	width: 			40,
				handler:		this.uploadImgButtonHandler,
				scope: 			this,
				style:			'padding-left: 5px',
				iconCls:		'uploadImgIcon'
			}, {
				xtype:          'button',
               	width: 			40,
				handler:		this.closeUploader,
				scope: 			this,
				style:			'padding-left: 5px',
				iconCls:		'closeUploadImgIcon'
			}]
		});
		
		
		//Combo box with positions
		this.imgPosition = new Ext.form.ComboBox({
			xtype:          'combo',
			mode:           'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	true,
			fieldLabel:     LN('sbi.worksheet.designer.position'),
			name:           'position',
			displayField:   'position',
			valueField:     'position',
			anchor:			'95%',
			store:          new Ext.data.JsonStore({
				fields : ['position'],
				data   : this.getAvailablePositions()
			})
		});

		items.push({
			columnWidth:.4,
			style:'padding-left: 10px; padding-top: 7px;',
			layout: 'form',
			items: [this.loadImageCombo,this.loadImageFileBrows,this.imgPosition]
		});
		
		return items;
	},
	
	//returns the array with the available positions for the image 
	getAvailablePositions:function(){
		var array = [];
		array.push({position: 'left'});
		array.push({position: 'center'});
		array.push({position: 'right'});
		return array;
	},
		
	//handler for the load image button
	//This button hides the select image combo box 
	//and shows the file input field
	imgButtonHandler: function(btn, e){
		this.loadImageCombo.hide();
		this.loadImageFileBrows.show();
	},
	
	//handler for the upload image button
	//This button hides the file input field 
	//and shows the load file combo box
	uploadImgButtonHandler: function(btn, e) {
		
        var form = this.imgFileFormPanel.getForm();
        if(form.isValid()){
            form.submit({
                url: Sbi.config.serviceRegistry.getBaseUrlStr({}), // a multipart form cannot contain parameters on its main URL;
                												   // they must POST parameters
                params: {
                    ACTION_NAME: 'UPLOAD_WORKSHEET_IMAGE_ACTION'
                },
                waitMsg: 'Uploading your image...',
                success: function(form, action) {
        			Ext.Msg.show({
     				   title: LN('sbi.worksheet.designer.sheettitlepanel.uploadfile.confirm.title'),
     				   msg: LN('sbi.worksheet.designer.sheettitlepanel.uploadfile.confirm.msg'),
     				   buttons: Ext.Msg.OK,
     				   icon: Ext.MessageBox.INFO
     				});
        			this.imagesStore.load();
        			this.closeUploader();
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
        }
	},
	
	closeUploader: function (btn, e) {
		this.loadImageFileBrows.hide();
		this.loadImageCombo.show();
		this.imgCombo.clearInvalid();
	},
	
	isValid: function(){
		var valid= true;
		var title = this.titlePanel.getValue();
		valid = valid && title!=null && title!='' ;
		valid = valid && this.imgCombo.isValid(false) && this.imgPosition.isValid(false);
		return valid;
	},
	
	getTitleState: function(messageBox){
		var values={};
		values.title =  this.titlePanel.getValue();
		values.img =  this.imgCombo.getValue();
		if(values.img==''){
			values.img =null;
		}
		values.position =   this.imgPosition.getValue();
		if(values.position==''){
			values.img =null;
		}
		return values;
	},
	
	setTitleState: function(values){
		 this.titlePanel.setValue(values.title)
		 this.imgCombo.setValue(values.img)
		 this.imgPosition.setValue(values.position);
	}
	
});