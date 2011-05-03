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
 * config.img = image form line
 * config.title = title form line
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
 * 		Alberto Ghedin (alberto.ghedin@eng.it)
 * 		Davide Zerbetto (davide.zerbetto@eng.it)
 */
Ext.ns("Sbi.worksheet");

Sbi.worksheet.SheetTitlePanel = function(config) { 

	this.config = config;
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
	if(config.title){
		formRows++;
		formItems = this.addTitle(formItems);
	}
	
	//row of the image
	if(config.img){
		formRows++;
		formItems = this.addImage(formItems);
	}
	
	var c = {
		border: false,
		frame:true,
		style:'padding:5px 15px 5px',  
		fileUpload: true,
		height: 15+formRows*30,
		defaults: {
			anchor: '95%',
		},
        items: [{
            layout:'column',
            items: formItems
        }]
	};
	
	Sbi.worksheet.SheetTitlePanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.worksheet.SheetTitlePanel, Ext.FormPanel, {
	loadImageCombo: null,
	loadImageFileBrows: null,
	titlePanel: null,
	titleField: null,
	sizeField: null,
	imgCombo: null,
	imgFile: null,
	imgPosition: null,
	config: null,
	imagesStore: null,
	
	
	//add the title row in the items of the panel
	addTitle:function(items){
		
		this.titleField = new Ext.form.TextField({
			fieldLabel: LN('sbi.worksheet.title'),
			allowBlank: false,
			name: 		'first',
			anchor:		'95%'
		});
		
		this.sizeField = new Ext.form.ComboBox({
			mode:           'local',
			triggerAction:  'all',
			forceSelection: true,
			editable:       false,
			allowBlank: 	false,
			fieldLabel:     LN('sbi.worksheet.size'),
			name:           'size',
			displayField:   'size',
			valueField:     'size',
			anchor:			'95%',
			store:          new Ext.data.JsonStore({
				fields : ['size'],
				data   : this.getAvailableSizes(10,50,2)
			})
		});
		
		this.titlePanel = new Ext.Panel({
            layout:'column',
            columnWidth:1,
            items: [		{
    			columnWidth:.7,
    			layout: 'form',
    			items: [this.titleField]
    		},
    		{
    			columnWidth:.3,
    			layout: 'form',
    			items: [this.sizeField]
    		}]
		});
		
		items.push(this.titlePanel);
		
//		items.push({
//			columnWidth:.7,
//			layout: 'form',
//			items: [this.titleField]
//		});
//		items.push({
//	        items: [{
//	            layout:'column',
//	            items: formItems
//	        }]
//		});
		
		
		

		
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
			allowBlank: 	false,
			editable:       false,
			fieldLabel:     LN('sbi.worksheet.image'),
			name:           'image',
			displayField:   'image',
			valueField:     'image',
			anchor:			'95%',
			store:          this.imagesStore
		});
		
		//text field for load the image in the server from the file system
		this.imgFile = new Ext.form.TextField({
			inputType:	'file',
			fieldLabel: LN('sbi.worksheet.image'),
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
			allowBlank: 	false,
			fieldLabel:     LN('sbi.worksheet.position'),
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
			columnWidth:.5,
			layout: 'form',
			items: [this.loadImageCombo,this.loadImageFileBrows]
		});
		
		items.push({
			columnWidth:.5,
			layout: 'form',
			items: [this.imgPosition]
		});
		return items;
	},
	
	//returns the array with the available positions for the image 
	getAvailablePositions:function(){
		var array = [];
		array.push({position: 'left'});
		array.push({position: 'top'});
		array.push({position: 'right'});
		array.push({position: 'bottom'});	
		return array;
	},
	
	//returns the array with the available size for the text form
	getAvailableSizes:function(min,max,step){
		var array = [];
		var i=min;
		while(i<=max){
			array.push({size: ''+i});
			i=i+step;
		}
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
     				   title: LN('sbi.worksheet.sheettitlepanel.uploadfile.confirm.title'),
     				   msg: LN('sbi.worksheet.sheettitlepanel.uploadfile.confirm.msg'),
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
	
	isValidForm: function(){
		var valid= true;
		if(this.config.title){
			valid = valid && this.titleField.isValid(false) && this.sizeField.isValid(false);
		}
		if(this.config.img){
			valid = valid && this.imgCombo.isValid(false) && this.imgPosition.isValid(false);
		}
		return valid;
	},
	
	getFormValues: function(messageBox){
		if(this.isValidForm()){	
			var values={};
			if(this.config.title){
				values.title =  this.titleField.getValue();
				values.size =   this.sizeField.getValue();
			}
			if(this.config.img){
				values.img =  this.imgCombo.getValue()
				values.position =   this.imgPosition.getValue();
			}
			return values;
		}
		if(messageBox){
			Ext.Msg.show({
				   title: LN('sbi.worksheet.msg.invalidinput.title'),
				   msg: LN('sbi.worksheet.msg.invalidinput.msg'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
				});
		}
		
	},
	
	setFormValues: function(values){
		if(this.config.title){
			 this.titleField.setValue(values.title)
			 this.sizeField.setValue(values.size);
		}
		if(this.config.img){
			 this.imgCombo.setValue(values.img)
			 this.imgPosition.setValue(values.position);
		}
	},
	
	hideTitle: function(){
		this.titlePanel.hide();
		this.setHeight(45);
	},
	
	showTitle: function(){
		this.titlePanel.show();
		this.setHeight(75);
	}
	
});