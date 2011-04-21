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
 * Authors - Alberto Ghedin
 */
Ext.ns("Sbi.adhoc");

Sbi.adhoc.SheetTitlePanel = function(config) { 

	this.config = config;
	var formItems = [];
	var formRows = 0;	
	
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
	
	var c ={
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
	}
	Sbi.adhoc.SheetTitlePanel.superclass.constructor.call(this, c);	 		

};

Ext.extend(Sbi.adhoc.SheetTitlePanel, Ext.FormPanel, {
	loadImageCombo: null,
	loadImageFileBrows: null,
	titleField: null,
	sizeField: null,
	imgCombo: null,
	imgFile: null,
	imgPosition: null,
	config: null,
	
	
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
		
		items.push({
			columnWidth:.7,
			layout: 'form',
			items: [this.titleField]
		});
		items.push({
			columnWidth:.3,
			layout: 'form',
			items: [this.sizeField]
		});
		return items;
	},
	
	
	//add the image row in the items of the panel
	addImage: function(items){
		
		//combo box with the image.. the store get the image from the server
		this.imgCombo = new Ext.form.ComboBox({
			xtype:          'combo',
			mode:           'local',
			triggerAction:  'all',
			forceSelection: true,
			allowBlank: 	false,
			editable:       false,
			fieldLabel:     LN('sbi.worksheet.image'),
			name:           'image',
			displayField:   'image',
			valueField:     'image',
			anchor:			'95%',
			store:          new Ext.data.JsonStore({
				fields : ['image'],
				data   : this.getAvailableImages()
			})}
		);
		
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
		
		//Panel with the load file field
		this.loadImageFileBrows = new Ext.Panel({
            layout:'column',
            hidden: true,
            items: [{
    			layout: 'form',
    			items: [this.imgFile]
			},{
				xtype:          'button',
               	width: 			40,
				handler:		this.uploadImgButtonHandler,
				scope: 			this,
				style:			'padding-left: 5px',
				iconCls:		'uploadImgIcon'
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
	
	//returns the array with the available images
	getAvailableImages:function(min,max,step){
		this.imageArray = [];
		this.imageArray.push({image: 'img1'});
		this.imageArray.push({image: 'img2'});
		this.imageArray.push({image: 'img3'});
		this.imageArray.push({image: 'img4'});	
		return this.imageArray;
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
	uploadImgButtonHandler: function(btn, e){
		if(this.imgFile.isValid(false)){
			this.imgCombo.setValue("img1");
			this.loadImageFileBrows.hide();
			this.loadImageCombo.show();
		}
		this.getFormValues(true);
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
	}
	
});